<?php

namespace Evolutio\Http\Controllers;

use Carbon\Carbon;
use Datatables;
use DB;
use Evolutio\Cliente;
use Evolutio\DetalleVenta;
use Evolutio\Empresa;
use Evolutio\Http\Requests\VentaRequest;
use Evolutio\Inventario;
use Evolutio\User;
use Evolutio\Venta;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class VentaController extends Controller {

	/**
	 * Display a listing of the resource.
	 *
	 * @return \Illuminate\Http\Response
	 */
	public function index() {
		//
	}

	/**
	 * Show the form for creating a new resource.
	 *
	 * @return \Illuminate\Http\Response
	 */
	public function create() {
		//
	}

	/**
	 * Store a newly created resource in storage.
	 *
	 * @param  \Illuminate\Http\Request  $request
	 * @return \Illuminate\Http\Response
	 */
	public function store(VentaRequest $request) {
		try {
			// dd($request);
			DB::beginTransaction();

			$fechaHoy = Carbon::now();

			$saldoVenta = 0;

			//Se inicializa en 1, pero será -1 en caso de Notas de crédito para realizar una devolución (sumar al inventario)
			$operador = 1;

			if ($request->formVenta['tipo_documento'] == "CRE" || $request->formVenta['tipo_documento'] == "NOT") {
				$saldoVenta = $request->formVenta['total'];
			}

			$venta = new Venta([
				// 'codigo_factura'    => $request->formVenta['codigo_factura'],
				'codigo_orden'    => $request->formVenta['codigo_orden'],
				'tipo_documento'  => $request->formVenta['tipo_documento'],
				'fecha_hora'      => $fechaHoy,
				'exonerada'       => $request->formVenta['exonerada'],
				'subtotal'        => $request->formVenta['subtotal'],
				// 'itbms'          => $request->formVenta['itbms'],
				'exento'          => $request->formVenta['exento'],
				'contra_entrega'  => $request->formVenta['contra_entrega'],
				'total'           => $request->formVenta['total'],
				'impuesto'        => $request->formVenta['impuesto'],
				'comentario'      => $request->formVenta['comentario'],
				'cliente_id'      => $request->formVenta['cliente_id'],
				'vendedor_id'     => $request->formVenta['vendedor_id'],
				'comisionista_id' => $request->formVenta['comisionista_id'],
				'estado'          => $request->formVenta['tipo_documento'] == "CRE" ? 'F':$request->formVenta['estado'],
				'saldo'           => $saldoVenta,
				'fecha_hora_pago'      => $fechaHoy,
				'nota_credito'    => $request->formVenta['tipo_documento'] == "NOT" ? $request->formVenta['id_ventaNC'] : null,
				'empresa_id'      => $request->formVenta['empresa_id'],
				'deposito_id'     => $request->formVenta['deposito_id'],
			]);
			$venta->save();

			//Actualizando el código de la factura
			$venta["codigo_factura"] = $venta->id;
			$venta->update();

			//Variable que lleva el detalle de la venta al pdf
			$detalleVentaPDF = [];

			$saldo      = null;
			$inventario = null;

			//Se hace un ciclo por cada detalle de la factura para irla guardando
			foreach ($request->formVenta['detalleVenta'] as $row => $detalle) {
				if ($detalle['cantidad'] > 0) {

					if ($request->formVenta['tipo_documento'] != "COT") {

						//Se consulta la cantidad disponible del producto en cuestión
						$inventario = Inventario::select('inventarios.disponible', 'inventarios.cif_local', 'inventarios.separado')
							->where('inventarios.empresa_id', $request->formVenta['empresa_id'])
							->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
							->where('inventarios.producto_id', $detalle['id'])
							->first();

						//Valído que exista el inventario en ese depósito, de no existir, debo crearlo...
		                if(is_null($inventario)){
		                    $inventario = new Inventario();
		                    $inventario["producto_id"] = $detalle['id'];
		                    $inventario["empresa_id"] = $request->formVenta['empresa_id'];
		                    $inventario["deposito_id"] = $request->formVenta['deposito_id'];
		                    $inventario->save();
		                }
							
						$disponible = 0;
						if (!(is_null($inventario)) && !(is_null($inventario->disponible))) {
							$disponible = $inventario->disponible;
						}

						//Si es una nota de crédito, debo hacer una devolución (aumentar inventario)
						if ($request->formVenta['tipo_documento'] == "NOT") {
							$operador = -1;

							//Y al ser una nota de crédito debo buscar la venta a la que aplicarán la devolución, para restar dicho saldo, solo aplica si dicha venta era a crédito.
							$restarSaldo = Venta::select('ventas.saldo', 'ventas.tipo_documento')
								->where('ventas.empresa_id', $request->formVenta['empresa_id'])
								->where('ventas.deposito_id', $request->formVenta['deposito_id'])
								->where('ventas.id', $request->formVenta['id_ventaNC'])
								->first();

							//En el caso de que la venta a la que se le aplicó la NC es a crédito, se actualiza el saldo
							if ($restarSaldo->tipo_documento == "CRE") {
								Venta::where('ventas.empresa_id', $request->formVenta['empresa_id'])
									->where('ventas.deposito_id', $request->formVenta['deposito_id'])
									->where('ventas.id', $request->formVenta['id_ventaNC'])
									->update(['saldo' => $restarSaldo->saldo - $saldoVenta]);
							}
						}

						//El saldo es la cantidad de productos luego de la venta, era lo disponible menos la cantidad vendida
						$saldo = $disponible - $detalle['cantidad'] * $operador;

						//Actualizo el campo disponible del inventario para ese producto
						Inventario::where('inventarios.empresa_id', $request->formVenta['empresa_id'])
							->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
							->where('inventarios.producto_id', $detalle['id'])
							->update(['disponible' => $saldo]);

						//Es una proforma, por ende se aumenta tambien el campo separado del producto
						if ($request->formVenta['tipo_documento'] == "PRO") {
							$separado = 0;
							if (!(is_null($inventario)) && !(is_null($inventario->separado))) {
								$separado = $inventario->separado;
							}

							Inventario::where('inventarios.empresa_id', $request->formVenta['empresa_id'])
								->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
								->where('inventarios.producto_id', $detalle['id'])
								->update(['separado' => $separado + $detalle['cantidad']]);

						}
					}

					$detalleVenta = new DetalleVenta([
						'venta_id'       => $venta->id,
						'producto_id'    => $detalle['id'],
						'descripcion'    => $detalle['descripcion'],
						'cantidad'       => $detalle['cantidad'],
						'saldo'          => $saldo,
						'cif_local'      => $inventario["cif_local"],
						'precio'         => $detalle['precio'],
						'subtotal'       => $detalle['total'],
						'porc_descuento' => $detalle['porc_descuento'],
						'descuento'      => $detalle['monto_descuento'],
						'porc_impuesto'  => $detalle['itbms'],
						'impuesto'       => $detalle['impuesto'],
						// 'comentario'     => "nada",
						// 'autorizado_id'  => 1,
						'empresa_id'     => $venta->empresa_id,
						'deposito_id'    => $venta->deposito_id,
					]);
					$detalleVenta->save();

					//Agregando al array de detalle de la venta para el reporte.
					$detalleVentaPDF[] = array('codigo' => $detalle['codigo'], 'descripcion' => $detalle['descripcion'], 'cantidad' => $detalle['cantidad'], 'precio' => $detalle['precio'], 'subtotal' => $detalle['subtotal']);
				}
			}

			//Genero el PDF.
			//Nombre de la empresa y depósito
			$datosEmpresa = Empresa::nombreEmpresaYDeposito($request->formVenta['empresa_id'], $request->formVenta['deposito_id']);

			//Buscando los datos del vendedor
			$vendedor = User::find($request->formVenta['vendedor_id']);

			//Buscando los datos del cliente
			$cliente = Cliente::find($request->formVenta['cliente_id']);

			$titulo = "Venta";

			switch ($request->formVenta['tipo_documento']) {
			case 'COT':
				$titulo = "Cotización";
				break;
			case 'NOT':
				$titulo = "Nota de Crédito";
				break;
			case 'PRO':
				$titulo = "Proforma";
				break;
			}

			//Variable a enviar para el reporte
			$ventaPDF = [
				'titulo'         => $titulo,
				'empresa'        => $datosEmpresa[0]["nombre_empresa"],
				'deposito'       => $datosEmpresa[0]["nombre_deposito"],
				'fecha'          => Carbon::parse($fechaHoy)->format('d/m/Y'),
				'documento'      => $venta->codigo_factura,
				'cliente'        => $cliente->primer_nombre,
				'cliente_cedula' => $cliente->cedula,
				'vendedor'       => $vendedor->name,
				'detalle'        => $detalleVentaPDF,
				'exento'         => $request->formVenta['exento'],
				'subtotal'       => $request->formVenta['subtotal'],
				'impuesto'       => $request->formVenta['impuesto'],
				'total'          => $request->formVenta['total'],
			];

			//Llamo al método para la creacarion del reporte.
			$this->generarReporte($ventaPDF);

			DB::commit();
			return response()->json(["message" => "Registro almacenado exitosamente.",
				"pdf"                              => "/pdfVenta/".$titulo."". $venta->codigo_factura . ".pdf"], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de guardar la venta: {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de guardar la venta", 500);
		}
	}

	/**
	 * Display the specified resource.
	 *
	 * @param  int  $id
	 * @return \Illuminate\Http\Response
	 */
	public function show($id) {
		//
	}

	/**
	 * Show the form for editing the specified resource.
	 *
	 * @param  int  $id
	 * @return \Illuminate\Http\Response
	 */
	public function edit($id) {
		//
	}

	/**
	 * Update the specified resource in storage.
	 *
	 * @param  \Illuminate\Http\Request  $request
	 * @param  int  $id
	 * @return \Illuminate\Http\Response
	 */
	public function update(Request $request, $id) {

		try {

			DB::beginTransaction();

			$fechaHoy = Carbon::now();

			//Actualizando el maestro de la Venta
			Venta::where("id", $id)
				->update([
					'codigo_orden'    => $request->formVenta['codigo_orden'],
					'fecha_hora'      => $fechaHoy,
					'exonerada'       => $request->formVenta['exonerada'],
					'subtotal'        => $request->formVenta['subtotal'],
					'exento'          => $request->formVenta['exento'],
					'contra_entrega'  => $request->formVenta['contra_entrega'],
					'total'           => $request->formVenta['total'],
					'impuesto'        => $request->formVenta['impuesto'],
					'comentario'      => $request->formVenta['comentario'],
					'fecha_hora_pago' => $fechaHoy,
					// 'cliente_id'      => $request->formVenta['cliente_id'],
					'vendedor_id'     => $request->formVenta['vendedor_id'],
					'comisionista_id' => $request->formVenta['comisionista_id'],
					'empresa_id'      => $request->formVenta['empresa_id'],
					'deposito_id'     => $request->formVenta['deposito_id'],
				]);

			//Consulto el detalle de la venta
			$detalleViejoVenta = DetalleVenta::where('venta_id', $id)->get();

			if ($request->formVenta['tipo_documento'] === "PRO") {
				//Si es una proforma, debo devolver inventario disponible del detalle que eliminaré, así como restar el inventario separado

				//Hacer un ciclo por el detalle, y todas las cantidades de cada detalle sumarlas al inventario y restarlas del separado.
				foreach ($detalleViejoVenta as $row => $detalle) {
					//Se consulta la cantidad disponible del producto en cuestión
					$inventario = Inventario::select('inventarios.disponible', 'inventarios.cif_local', 'inventarios.separado')
						->where('inventarios.empresa_id', $request->formVenta['empresa_id'])
						->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
						->where('inventarios.producto_id', $detalle['producto_id'])
						->first();

					$disponible = 0;
					if (!(is_null($inventario)) && !(is_null($inventario->disponible))) {
						$disponible = $inventario->disponible;
					}

					//Actualizo el campo disponible del inventario para ese producto, sumandole la cantidad que voy a eliminar (Devolviendo al inventario)
					Inventario::where('inventarios.empresa_id', $request->formVenta['empresa_id'])
						->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
						->where('inventarios.producto_id', $detalle['producto_id'])
						->update(['disponible' => $disponible + $detalle['cantidad']]);

					//Ahora hay que restar el inventario separado.
					$separado = 0;
					if (!(is_null($inventario)) && !(is_null($inventario->separado))) {
						$separado = $inventario->separado;
					}

					//Actualizo el inventario separado
					Inventario::where('inventarios.empresa_id', $request->formVenta['empresa_id'])
						->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
						->where('inventarios.producto_id', $detalle['producto_id'])
						->update(['separado' => $separado - $detalle['cantidad']]);
				}

			}

			/**
			 * Una vez realizado el ajuste de lo disponible y lo separado, puedo realizar la eliminación del detalle de la venta.
			 */
			DetalleVenta::where('detalle_ventas.empresa_id', $request->formVenta['empresa_id'])
				->where('detalle_ventas.deposito_id', $request->formVenta['deposito_id'])
				->where('detalle_ventas.venta_id', $id)
				->delete();

			//Variable que lleva el detalle de la venta al pdf
			$detalleVentaPDF = [];

			$saldo      = null;
			$inventario = null;
			/**
			 * Ahora voy a insertar el nuevo detalle de venta, si es una proforma en este caso haciendo lo contrario, restando de inventario disponible y sumando separados (solo en caso de proforma, la cotización no mueve inventarios.)
			 */
			//Se hace un ciclo por cada detalle de la factura para irla guardando
			foreach ($request->formVenta['detalleVenta'] as $row => $detalle) {

				if ($request->formVenta['tipo_documento'] === "PRO") {
					//Se consulta la cantidad disponible del producto en cuestión
					$inventario = Inventario::select('inventarios.disponible', 'inventarios.cif_local', 'inventarios.separado')
						->where('inventarios.empresa_id', $request->formVenta['empresa_id'])
						->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
						->where('inventarios.producto_id', $detalle['id'])
						->first();

					$disponible = 0;
					if (!(is_null($inventario)) && !(is_null($inventario->disponible))) {
						$disponible = $inventario->disponible;
					}

					//El saldo es la cantidad de productos luego de la venta, era lo disponible menos la cantidad vendida
					$saldo = $disponible - $detalle['cantidad'];

					//Actualizo el campo disponible del inventario para ese producto
					Inventario::where('inventarios.empresa_id', $request->formVenta['empresa_id'])
						->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
						->where('inventarios.producto_id', $detalle['id'])
						->update(['disponible' => $saldo]);

					//Es una proforma, por ende se aumenta tambien el campo separado del producto
					$separado = 0;
					if (!(is_null($inventario)) && !(is_null($inventario->separado))) {
						$separado = $inventario->separado;
					}

					Inventario::where('inventarios.empresa_id', $request->formVenta['empresa_id'])
						->where('inventarios.deposito_id', $request->formVenta['deposito_id'])
						->where('inventarios.producto_id', $detalle['id'])
						->update(['separado' => $separado + $detalle['cantidad']]);
				}

				//Se guarda el detalle de la venta.
				$detalleVenta = new DetalleVenta([
					'venta_id'       => $id,
					'producto_id'    => $detalle['id'],
					'descripcion'    => $detalle['descripcion'],
					'cantidad'       => $detalle['cantidad'],
					'saldo'          => $saldo,
					'cif_local'      => $inventario["cif_local"],
					'precio'         => $detalle['precio'],
					'subtotal'       => $detalle['total'],
					'porc_descuento' => $detalle['porc_descuento'],
					'descuento'      => $detalle['monto_descuento'],
					'porc_impuesto'  => $detalle['itbms'],
					'impuesto'       => $detalle['impuesto'],
					'empresa_id'     => $request->formVenta['empresa_id'],
					'deposito_id'    => $request->formVenta['deposito_id'],
				]);
				$detalleVenta->save();

				//Agregando al array de detalle de la venta para el reporte.
				$detalleVentaPDF[] = array('codigo' => $detalle['codigo'], 'descripcion' => $detalle['descripcion'], 'cantidad' => $detalle['cantidad'], 'precio' => $detalle['precio'], 'subtotal' => $detalle['subtotal']);
			}

			//Genero el PDF.
			//Nombre de la empresa y depósito
			$datosEmpresa = Empresa::nombreEmpresaYDeposito($request->formVenta['empresa_id'], $request->formVenta['deposito_id']);

			//Buscando los datos del vendedor
			$vendedor = User::find($request->formVenta['vendedor_id']);

			//Buscando los datos del cliente
			$cliente = Cliente::find($request->formVenta['cliente_id']);

			$titulo = "Venta";

			switch ($request->formVenta['tipo_documento']) {
			case 'COT':
				$titulo = "Cotización";
				break;
			case 'NOT':
				$titulo = "Nota de Crédito";
				break;
			case 'PRO':
				$titulo = "Proforma";
				break;
			}

			//Variable a enviar para el reporte
			$ventaPDF = [
				'titulo'         => $titulo,
				'empresa'        => $datosEmpresa[0]["nombre_empresa"],
				'deposito'       => $datosEmpresa[0]["nombre_deposito"],
				'fecha'          => Carbon::parse($fechaHoy)->format('d/m/Y'),
				'documento'      => $id,
				'cliente'        => $cliente->primer_nombre,
				'cliente_cedula' => $cliente->cedula,
				'vendedor'       => $vendedor->name,
				'detalle'        => $detalleVentaPDF,
				'exento'         => $request->formVenta['exento'],
				'subtotal'       => $request->formVenta['subtotal'],
				'impuesto'       => $request->formVenta['impuesto'],
				'total'          => $request->formVenta['total'],
			];

			//Llamo al método para la creacarion del reporte.
			$this->generarReporte($ventaPDF);

			DB::commit();
			return response()->json(["message" => "Documento actualizado correctamente", "pdf" => "/pdfVenta/".$titulo."". $id . ".pdf"], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de Actualizar el documento (update) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Actualizar el documento (update)", 500);
		}
	}

	/**
	 * Remove the specified resource from storage.
	 *
	 * @param  int  $id
	 * @return \Illuminate\Http\Response
	 */
	public function destroy($id) {
		//
	}

/**
 * Método para realizar la consulta de las transacciones de venta de un producto
 * @param  [type] $empresa_id  Id de la empresa
 * @param  [type] $deposito_id Id del depósito
 * @param  [type] $producto_id Id del producto
 * @return [type]              [description]
 */
	public function transaccionesVenta($empresa_id, $deposito_id, $producto_id) {
		try {

			$transVenta = Venta::select('ventas.fecha_hora as fecha', 'ventas.codigo_factura as documento', DB::raw("'-' as op"), 'detalle_ventas.cantidad as cantidad', 'detalle_ventas.saldo as saldo', 'detalle_ventas.precio as fob', 'detalle_ventas.cif_local as cif',
				'users.name as vendedor',
				DB::raw("CONCAT(clientes.cedula, ' - ' , clientes.primer_nombre, ' ', clientes.primer_apellido) as cliente")
			)
				->leftJoin('detalle_ventas', 'ventas.id', '=', 'detalle_ventas.venta_id')
				->leftJoin('users', 'ventas.vendedor_id', '=', 'users.id')
				->leftJoin('clientes', 'ventas.cliente_id', '=', 'clientes.id')
				->whereColumn("detalle_ventas.empresa_id", "ventas.empresa_id")
				->whereColumn('detalle_ventas.deposito_id', "ventas.deposito_id")
				->where("detalle_ventas.producto_id", $producto_id)
				->where("ventas.empresa_id", $empresa_id)
				->where("ventas.deposito_id", $deposito_id)
				->get();

			return response()->json(["transVenta" => $transVenta], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar las transacciones de venta del producto (transaccionesVenta) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar las transacciones de venta del producto (transaccionesVenta)", 500);
		}
	}

	/**
	 * Método para consultar el último precio en el que fue vendido un producto.
	 *
	 * @param  [type] $empresa_id      [Id de la empresa]
	 * @param  [type] $deposito_id     [Id del depósito]
	 * @param  [type] $producto_codigo [Código del producto]
	 * @return [type]                  [Último precio en que se vendío el producto]
	 */
	public function ultimoPrecio($empresa_id, $deposito_id, $producto_codigo) {

		try {

			$UltimoPrecio = Venta::select('detalle_ventas.precio')
				->join('detalle_ventas', 'detalle_ventas.venta_id', 'ventas.id')
				->whereColumn('detalle_ventas.empresa_id', 'ventas.empresa_id')
				->whereColumn('detalle_ventas.deposito_id', 'ventas.deposito_id')
				->join('productos', 'productos.id', 'detalle_ventas.producto_id')
				->whereColumn('productos.empresa_id', 'ventas.empresa_id')
				->where('ventas.empresa_id', $empresa_id)
				->where('ventas.deposito_id', $deposito_id)
				->where('ventas.estado', 'F')
				->where('productos.codigo', $producto_codigo)
				->orderBy('ventas.fecha_hora', 'desc')
				->first();

			return $UltimoPrecio;

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar el último precio del producto (ultimoPrecio) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar el último precio del producto (ultimoPrecio)", 500);
		}
	}

	/**
	 * Método para consultar las ventas con estatus "P" (Prefacturadas) y retornar un DataTable, posteriormente por requerimiento
	 * se agregó para que consultara también las notes de crédito.
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @return [type] DataTable    [description]
	 */
	public function ventasFacturadas($empresa_id, $deposito_id) {

		try {
			// Using Eloquent
			return Datatables::eloquent(
				Venta::select('ventas.tipo_documento', 'ventas.codigo_factura', 'ventas.fecha_hora', 'users.name as vendedor_id', 'clientes.primer_nombre as cliente_id', 'ventas.total', 'ventas.id as venta_id')
					->leftjoin('clientes', 'clientes.id', 'ventas.cliente_id')
					->whereColumn('clientes.empresa_id', 'ventas.empresa_id')
					->leftjoin('users', 'users.id', 'ventas.vendedor_id')
					->whereColumn('users.empresa_id', 'ventas.empresa_id')
					->whereColumn('users.deposito_id', 'ventas.deposito_id')
					->where('ventas.empresa_id', $empresa_id)
					->where('ventas.deposito_id', $deposito_id)
					->where('ventas.estado', "P")
					->where(function ($query) {
						$query->where('ventas.tipo_documento', '=', 'CRE')->orWhere('ventas.tipo_documento', '=', 'CON')->orWhere('ventas.tipo_documento', '=', 'NOT');
					})
					->orderBy('ventas.fecha_hora', 'desc')
			)
				->editColumn('tipo_documento', function ($row) {
					$retorno = "";

					switch ($row->tipo_documento) {
					case 'CON':
						$retorno = "Contado";
						break;
					case 'CRE':
						$retorno = "Crédito";
						break;
					case 'COT':
						$retorno = "Cotización";
						break;
					case 'PRO':
						$retorno = "Proforma";
						break;
					case 'NOT':
						$retorno = "Nota C.";
						break;
					}

					return $retorno;
				})
				->editColumn('fecha_hora', function ($row) {
					return Carbon::parse($row->fecha_hora)->format('d/m/Y');
				})
				->make(true);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar las ventas Facturadas (ventasFacturadas) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de de Consultar las ventas Facturadas (ventasFacturadas)", 500);
		}
	}

	/**
	 * Método para consultar el detalle de la venta, según su nro de documento
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @param  [type] $nro_documento [Nro de documento]
	 * @return [type] DataTable    [description]
	 */
	public function consultarDetalle($empresa_id, $deposito_id, $venta_id) {

		try {
			// Using Eloquent
			return Datatables::eloquent(
				DetalleVenta::select('productos.codigo as codigo', 'detalle_ventas.descripcion', 'detalle_ventas.cantidad', 'detalle_ventas.precio', 'detalle_ventas.subtotal', 'detalle_ventas.id as id_detalle')
					->leftjoin('productos', 'productos.id', 'detalle_ventas.producto_id')
					->whereColumn('productos.empresa_id', 'detalle_ventas.empresa_id')
					->where('detalle_ventas.empresa_id', $empresa_id)
					->where('detalle_ventas.deposito_id', $deposito_id)
					->where('detalle_ventas.venta_id', $venta_id)
			)
				->make(true);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar el detalle de la venta (consultarDetalle) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar el detalle de la venta (consultarDetalle)", 500);
		}
	}

	/**
	 * Método para anular una venta (documento)
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @param  [type] $venta_id [Venta Id]
	 */
	public function anularDocumento($empresa_id, $deposito_id, $venta_id) {

		try {

			DB::beginTransaction();

			//Consulta la venta que se quiere anular para saber su tipo y otros datos necesarios.
			$venta = Venta::find($venta_id);

			//Se inicializa en 1, pero será -1 en caso de Notas de crédito para realizar una devolución
			$operador = 1;

			//Saldo del cliente, se inicializa en 0 y tendrá valor solo si el documento es a Crédito o es una nota de crédito
			$saldoVenta = 0;

			if ($venta->tipo_documento == "CRE" || $venta->tipo_documento == "NOT") {
				$saldoVenta = $venta->total;
			}

			//Consulto el detalle de la venta
			$detalleVenta = DetalleVenta::where('venta_id', $venta_id)->get();

			/**
			 * Si es de tipo crédito, o contado, o proforma se deben ejecutar las siguientes acciones: CRE y CON sumar al inventario disponible lo anulado, y
			 * si es PRO además también se debe disminuir el inventario separado.
			 */
			if ($venta->tipo_documento != "COT") {

				//Hacer un ciclo por el detalle, y todas las cantidades de cada detalle sumarlas al inventario y restarlas del separado.
				foreach ($detalleVenta as $row => $detalle) {

					//Si es una nota de crédito, debo deshacer una devolución (restar inventario)
					if ($venta->tipo_documento == "NOT") {
						$operador = -1;

						//Y al ser una nota de crédito debo buscar la venta a la que aplicarán la devolución, para sumar de nuevo dicho saldo, solo para cuando se trata de venta a crédito
						$restarSaldo = Venta::select('ventas.saldo', 'ventas.tipo_documento')
							->where('ventas.empresa_id', $empresa_id)
							->where('ventas.deposito_id', $deposito_id)
							->where('ventas.id', $venta->nota_credito)
							->first();

						if ($restarSaldo->tipo_documento == "CRE") {
							Venta::where('ventas.empresa_id', $empresa_id)
								->where('ventas.deposito_id', $deposito_id)
								->where('ventas.id', $venta->nota_credito)
								->update(['saldo' => $restarSaldo->saldo + $saldoVenta]);
						}
					}

					//Se consulta la cantidad disponible del producto en cuestión
					$inventario = Inventario::select('inventarios.disponible', 'inventarios.cif_local', 'inventarios.separado')
						->where('inventarios.empresa_id', $venta->empresa_id)
						->where('inventarios.deposito_id', $venta->deposito_id)
						->where('inventarios.producto_id', $detalle['producto_id'])
						->first();

					$disponible = 0;
					if (!(is_null($inventario)) && !(is_null($inventario->disponible))) {
						$disponible = $inventario->disponible;
					}

					//El saldo es la cantidad de productos que quedan luego de anular el documento (devuelven productos al inventario)
					$saldo = $disponible + $detalle['cantidad'] * $operador;

					//Actualizo el campo disponible del inventario para ese producto, sumandole la cantidad que voy a eliminar (Devolviendo al inventario)
					Inventario::where('inventarios.empresa_id', $venta->empresa_id)
						->where('inventarios.deposito_id', $venta->deposito_id)
						->where('inventarios.producto_id', $detalle['producto_id'])
						->update(['disponible' => $saldo]);

					if ($venta->tipo_documento === "PRO") {
						//Ahora hay que restar el inventario separado.
						$separado = 0;
						if (!(is_null($inventario)) && !(is_null($inventario->separado))) {
							$separado = $inventario->separado;
						}

						//Actualizo el inventario separado
						Inventario::where('inventarios.empresa_id', $venta->empresa_id)
							->where('inventarios.deposito_id', $venta->deposito_id)
							->where('inventarios.producto_id', $detalle['producto_id'])
							->update(['separado' => $separado - $detalle['cantidad']]);
					}
				}
			}

			//Actualizando el estado del documento, se anula
			Venta::where("id", $venta_id)
				->where("empresa_id", $empresa_id)
				->where("deposito_id", $deposito_id)
				->update(['estado' => "A"]);

			DB::commit();

			return response()->json(["message" => "Documento anulado correctamente"], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de Anular el documento (anularDocumento) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Anular el documento (anularDocumento)", 500);
		}
	}

	/**
	 * Método para consultar todos los datos de un documento, que son necesarios para cargar en la vista principal de ventas.
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @param  [type] $venta_id [Venta Id]
	 */
	public function consultarDocumento($empresa_id, $deposito_id, $venta_id, $anular) {

		try {

			DB::beginTransaction();

			//Realizo la consulta de los datos básicos de la venta, del documento en general, vendedores, comisionistas, etc.
			$venta = Venta::select(DB::raw("CONCAT('ID: ',clientes.id,' Ced: ', clientes.cedula, ' Nombre: ' , clientes.primer_nombre) as cliente"), 'clientes.movil', 'clientes.puntos', 'clientes.direccion', 'clientes.tipo_cliente_id', 'ventas.tipo_documento', 'ventas.contra_entrega', 'ventas.exonerada', 'clientes.id as cliente_id', 'clientes.codigo_precio',
				DB::raw("CONCAT('ID: ',u1.id,' Nombre: ', u1.name) as vendedor"),
				DB::raw("CONCAT('ID: ',u2.id,' Nombre: ', u2.name) as comisionista"))
				->join('clientes', 'clientes.id', 'ventas.cliente_id')
				->leftJoin('users as u2', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('ventas.empresa_id', '=', 'u2.empresa_id')
						->on('ventas.comisionista_id', '=', 'u2.id')
						->on('u2.deposito_id', '=', DB::raw($deposito_id));
				})
				->leftjoin('users as u1', 'u1.id', 'ventas.vendedor_id')
				->whereColumn('ventas.empresa_id', 'u1.empresa_id')
				->whereColumn('ventas.deposito_id', 'u1.deposito_id')
				->whereColumn('ventas.empresa_id', 'clientes.empresa_id')
				->where("ventas.id", $venta_id)
				->where("ventas.empresa_id", $empresa_id)
				->where("ventas.deposito_id", $deposito_id)
				->first();

			//Se realiza la consulta del detalle de la factura
			//id_producto,codigo_producto,descripción_producto,cantidad,precio,itbms_producto, impuesto (monto), subtotal, total, exento.
			$detalle = DetalleVenta::select('detalle_ventas.producto_id', 'productos.codigo', 'detalle_ventas.descripcion', 'detalle_ventas.cantidad', 'detalle_ventas.precio', 'detalle_ventas.porc_impuesto', 'detalle_ventas.impuesto', 'detalle_ventas.subtotal as total', 'detalle_ventas.porc_descuento', 'detalle_ventas.descuento')
				->leftjoin('productos', 'productos.id', 'detalle_ventas.producto_id')
				->whereColumn('productos.empresa_id', 'detalle_ventas.empresa_id')
				->where('detalle_ventas.empresa_id', $empresa_id)
				->where('detalle_ventas.deposito_id', $deposito_id)
				->where('detalle_ventas.venta_id', $venta_id)
				->get();

			//Por cada detalle de factura encontrado, se calcula su exento y su subtotal, puesto que estos no son guardados en BD directamente, pero es necesario mostrarlos en la vista.
			foreach ($detalle as $detalle_venta) {
				$detalle_venta['subtotal'] = $detalle_venta['cantidad'] * $detalle_venta['precio'];

				if ($detalle_venta['porc_impuesto'] == 0) {
					$detalle_venta['exento'] = $detalle_venta['total'];
				} else {
					$detalle_venta['exento'] = 0;
				}
			}

			$message = "Nuevo documento cargado correctamente";

			if ($anular == "true") {
				//Actualizando el estado del documento
				Venta::where("id", $venta_id)
					->where("empresa_id", $empresa_id)
					->where("deposito_id", $deposito_id)
					->update(['estado' => "A"]);

				$message = "Documento anterior anulado, se ha cargado uno nuevo para su modificación";
			}

			DB::commit();

			return response()->json(["message" => $message, "venta" => $venta, "detalle" => $detalle], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos del documento (consultarDocumento) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos del documento (consultarDocumento)", 500);
		}
	}

	/**
	 * Método para generar el reporte de la venta.
	 * @param  [type] $ventaPDF [Obj con los datos que necesita el reporte para ser generado.]
	 * @return [type]           [description]
	 */
	public function generarReporte($ventaPDF) {
		//Gestion y carga del reporte
		$view = \View::make('pdf.ventasPDF', compact('ventaPDF'))->render();
		$pdf  = \App::make('dompdf.wrapper');
		$pdf->loadHTML($view);
		//Guardado del reporte en el servidor.
		file_put_contents("documentos/ventas/".$ventaPDF["titulo"]."". $ventaPDF["documento"] . ".pdf", $pdf->output());
	}

	/**
	 * Método para reimprimir la venta.
	 * @param  [type] $empresa_id  [Id de la empresa]
	 * @param  [type] $deposito_id [Id del depósito]
	 * @param  [type] $venta_id    [Id de la venta]
	 * @return [type]              [description]
	 */
	public function reimprimirDocumento($empresa_id, $deposito_id, $venta_id) {
		try {

			//Consultando el maestro de la venta
			$venta = Venta::select('fecha_hora', 'codigo_factura', 'cliente_id', 'vendedor_id', 'exento', 'subtotal', 'impuesto', 'total', 'tipo_documento')
				->where('empresa_id', $empresa_id)
				->where('deposito_id', $deposito_id)
				->where('id', $venta_id)
				->first();

			//Consultando el detalle de la venta
			$detalleVenta = DetalleVenta::select('productos.codigo', 'detalle_ventas.descripcion', 'detalle_ventas.cantidad', 'detalle_ventas.precio', 'detalle_ventas.subtotal', 'detalle_ventas.impuesto')
				->join('productos', 'productos.id', 'detalle_ventas.producto_id')
				->whereColumn('productos.empresa_id', 'detalle_ventas.empresa_id')
				->where('detalle_ventas.venta_id', $venta_id)
				->where('detalle_ventas.empresa_id', $empresa_id)
				->where('detalle_ventas.deposito_id', $deposito_id)
				->get();

			//Variable que lleva el detalle de la venta al pdf
			$detalleVentaPDF = [];

			//Se hace un ciclo por cada detalle de la factura para irla guardando
			foreach ($detalleVenta as $row => $detalle) {
				//Agregando al array de detalle de la venta para el reporte.
				$detalleVentaPDF[] = array('codigo' => $detalle['codigo'], 'descripcion' => $detalle['descripcion'], 'cantidad' => $detalle['cantidad'], 'precio' => $detalle['precio'], 'subtotal' => $detalle['subtotal'] - $detalle['impuesto']);
			}

			//Genero el PDF.
			//Nombre de la empresa y depósito
			$datosEmpresa = Empresa::nombreEmpresaYDeposito($empresa_id, $deposito_id);

			//Buscando los datos del vendedor
			$vendedor = User::find($venta['vendedor_id']);

			//Buscando los datos del cliente
			$cliente = Cliente::find($venta['cliente_id']);

			$titulo = "Venta";

			switch ($venta['tipo_documento']) {
			case 'COT':
				$titulo = "Cotización";
				break;
			case 'NOT':
				$titulo = "Nota de Crédito";
				break;
			case 'PRO':
				$titulo = "Proforma";
				break;
			}

			//Variable a enviar para el reporte
			$ventaPDF = [
				'titulo'         => $titulo,
				'empresa'        => $datosEmpresa[0]["nombre_empresa"],
				'deposito'       => $datosEmpresa[0]["nombre_deposito"],
				'fecha'          => Carbon::parse($venta["fecha_hora"])->format('d/m/Y'),
				'documento'      => $venta["codigo_factura"],
				'cliente'        => $cliente->primer_nombre,
				'cliente_cedula' => $cliente->cedula,
				'vendedor'       => $vendedor->name,
				'detalle'        => $detalleVentaPDF,
				'exento'         => $venta['exento'],
				'subtotal'       => $venta['subtotal'],
				'impuesto'       => $venta['impuesto'],
				'total'          => $venta['total'],
			];

			//Llamo al método para la creacarion del reporte.
			$this->generarReporte($ventaPDF);

			DB::commit();
			return response()->json(["message" => "Reimpresión del documento realizada correctamente",
				"pdf"                              => "/pdfVenta/".$titulo."". $venta->codigo_factura . ".pdf"], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de Reimprimir el documento (reimprimirDocumento) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Reimprimir el documento (reimprimirDocumento)", 500);
		}
	}

	/**
	 * Método para consultar todos los datos de un documento que aplica para realizarle una nota de crédito (Ventas Facturadas), que son necesarios para cargar en la vista principal de ventas.
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @param  [type] $venta_id [Venta Id]
	 */
	public function consultarDocumentoNC($empresa_id, $deposito_id, $venta_id) {

		try {

			//Realizo la consulta de los datos básicos de la venta, del documento en general, vendedores, comisionistas, etc.
			$venta = Venta::select(DB::raw("CONCAT('ID: ',clientes.id,' Ced: ', clientes.cedula, ' Nombre: ' , clientes.primer_nombre) as cliente"), 'clientes.movil', 'clientes.puntos', 'clientes.direccion', 'clientes.tipo_cliente_id', 'ventas.tipo_documento', 'ventas.contra_entrega', 'ventas.exonerada', 'ventas.codigo_orden', 'ventas.id as id_venta',
				DB::raw("CONCAT('ID: ',u1.id,' Nombre: ', u1.name) as vendedor"),
				DB::raw("CONCAT('ID: ',u2.id,' Nombre: ', u2.name) as comisionista"))
				->join('clientes', 'clientes.id', 'ventas.cliente_id')
				->leftJoin('users as u2', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('ventas.empresa_id', '=', 'u2.empresa_id')
						->on('ventas.comisionista_id', '=', 'u2.id')
						->on('u2.deposito_id', '=', DB::raw($deposito_id));
				})
				->leftjoin('users as u1', 'u1.id', 'ventas.vendedor_id')
				->whereColumn('ventas.empresa_id', 'u1.empresa_id')
				->whereColumn('ventas.deposito_id', 'u1.deposito_id')
				->whereColumn('ventas.empresa_id', 'clientes.empresa_id')
				->where("ventas.id", $venta_id)
				->where("ventas.empresa_id", $empresa_id)
				->where("ventas.deposito_id", $deposito_id)
				->where("ventas.estado", "F")
				->where(function ($query) {
					$query->where('ventas.tipo_documento', '=', 'CRE')->orWhere('ventas.tipo_documento', '=', 'CON');
				})
				->first();

			$detalle = null;

			//Si se consigue una venta con dichas características, se procede a buscar su detalle.
			if ($venta != null) {
				//Se realiza la consulta del detalle de la factura
				$detalle = DetalleVenta::select('detalle_ventas.producto_id', 'productos.codigo', 'detalle_ventas.descripcion', 'detalle_ventas.cantidad', 'detalle_ventas.precio', 'detalle_ventas.porc_impuesto', 'detalle_ventas.impuesto', 'detalle_ventas.subtotal as total', 'detalle_ventas.porc_descuento', 'detalle_ventas.descuento')
					->leftjoin('productos', 'productos.id', 'detalle_ventas.producto_id')
					->whereColumn('productos.empresa_id', 'detalle_ventas.empresa_id')
					->where('detalle_ventas.empresa_id', $empresa_id)
					->where('detalle_ventas.deposito_id', $deposito_id)
					->where('detalle_ventas.venta_id', $venta_id)
					->get();

				//Por cada detalle de factura encontrado, se calcula su exento y su subtotal, puesto que estos no son guardados en BD directamente, pero es necesario mostrarlos en la vista.
				foreach ($detalle as $detalle_venta) {
					$detalle_venta['subtotal'] = $detalle_venta['cantidad'] * $detalle_venta['precio'];

					if ($detalle_venta['porc_impuesto'] == 0) {
						$detalle_venta['exento'] = $detalle_venta['total'];
					} else {
						$detalle_venta['exento'] = 0;
					}
				}

				$message = "Factura cargada correctamente, proceda a realizar la devolución";
			} else {

				$message = "El #Documento ingresado no pertenece a una factura válida para realizar una devolución";
			}

			return response()->json(["message" => $message, "venta" => $venta, "detalle" => $detalle], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos del documento (consultarDocumentoNC) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos del documento (consultarDocumentoNC)", 500);
		}
	}

	/**
	 * Método para consultar las cotizaciones y retornar un DataTable
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @return [type] DataTable    [description]
	 */
	public function ventasCotizacion($empresa_id, $deposito_id) {

		try {
			// Using Eloquent
			return Datatables::eloquent(
				Venta::select('ventas.tipo_documento', 'ventas.codigo_factura', 'ventas.fecha_hora', 'users.name as vendedor_id', 'clientes.primer_nombre as cliente_id', 'ventas.total', 'ventas.id as venta_id')
					->leftjoin('clientes', 'clientes.id', 'ventas.cliente_id')
					->whereColumn('clientes.empresa_id', 'ventas.empresa_id')
					->leftjoin('users', 'users.id', 'ventas.vendedor_id')
					->whereColumn('users.empresa_id', 'ventas.empresa_id')
					->whereColumn('users.deposito_id', 'ventas.deposito_id')
					->where('ventas.empresa_id', $empresa_id)
					->where('ventas.deposito_id', $deposito_id)
					->where('ventas.tipo_documento', "COT")
					->where('ventas.estado', "!=", "A")
					->orderBy('ventas.fecha_hora', 'desc')
			)
				->editColumn('tipo_documento', function ($row) {
					return "Cotización";
				})
				->editColumn('fecha_hora', function ($row) {
					return Carbon::parse($row->fecha_hora)->format('d/m/Y');
				})
				->make(true);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar las Cotizaciones (ventasCotizacion) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de de Consultar las Cotizaciones (ventasCotizacion)", 500);
		}
	}

	/**
	 * Método para consultar las proformas y retornar un DataTable
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @return [type] DataTable    [description]
	 */
	public function ventasProforma($empresa_id, $deposito_id) {

		try {
			// Using Eloquent
			return Datatables::eloquent(
				Venta::select('ventas.tipo_documento', 'ventas.codigo_factura', 'ventas.fecha_hora', 'users.name as vendedor_id', 'clientes.primer_nombre as cliente_id', 'ventas.total', 'ventas.id as venta_id')
					->leftjoin('clientes', 'clientes.id', 'ventas.cliente_id')
					->whereColumn('clientes.empresa_id', 'ventas.empresa_id')
					->leftjoin('users', 'users.id', 'ventas.vendedor_id')
					->whereColumn('users.empresa_id', 'ventas.empresa_id')
					->whereColumn('users.deposito_id', 'ventas.deposito_id')
					->where('ventas.empresa_id', $empresa_id)
					->where('ventas.deposito_id', $deposito_id)
					->where('ventas.tipo_documento', "PRO")
					->where('ventas.estado', "!=", "A")
					->orderBy('ventas.fecha_hora', 'desc')
			)
				->editColumn('tipo_documento', function ($row) {
					return "Proforma";
				})
				->editColumn('fecha_hora', function ($row) {
					return Carbon::parse($row->fecha_hora)->format('d/m/Y');
				})
				->make(true);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar las Proformas (ventasProforma) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de de Consultar las Proformas (ventasProforma)", 500);
		}
	}

	/**
	 * Método para consultar el tipo de cliente
	 *
	 * @param  [type] $empresa_id      [Id de la empresa]
	 * @param  [type] $deposito_id     [Id del depósito]
	 * @param  [type] $venta_id        [Id de la venta]
	 * @return [type]
	 */
	public function tipoCliente($empresa_id, $deposito_id, $venta_id) {

		try {

			$tipoCliente = Venta::select('clientes.tipo_cliente_id')
				->leftjoin('clientes', 'clientes.id', 'ventas.cliente_id')
				->whereColumn('clientes.empresa_id', 'ventas.empresa_id')
				->where('ventas.empresa_id', $empresa_id)
				->where('ventas.deposito_id', $deposito_id)
				->where('ventas.id', $venta_id)
				->first();

			return response()->json(["tipoCliente" => $tipoCliente], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar el tipo del cliente dada la venta (tipoCliente) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar el tipo del cliente dada la venta (tipoCliente)", 500);
		}
	}

	/**
	 * Método para hacer un update a una factura (documento) y pasarlo de ser proforma o cotización, a ser una factura de contado
	 *
	 * @param  [type] $empresa_id      [Id de la empresa]
	 * @param  [type] $deposito_id     [Id del depósito]
	 * @param  [type] $producto_codigo [Código del producto]
	 * @return [type]                  [Último precio en que se vendío el producto]
	 */
	public function convertirFactura($empresa_id, $deposito_id, $venta_id, $contado) {

		try {
			DB::beginTransaction();

			$venta = Venta::where('ventas.empresa_id', $empresa_id)
				->where('ventas.deposito_id', $deposito_id)
				->where('ventas.id', $venta_id)
				->first();

			//Si es a contado solo se actualiza el tipo de documento
			if ($contado === true) {
				$venta->tipo_documento = 'CON';
				$venta->save();
			} else {
				//Si es a crédito, debo actualizar el saldo, y el tipo de documento.
				$venta->tipo_documento = 'CRE';
				$venta->saldo          = $venta->total;
				$venta->save();
			}
			DB::commit();

			return response()->json(["message" => "Documento convertido a factura correctamente"], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de Convertir a factura (convertirFactura) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Convertir a factura (convertirFactura)", 500);
		}
	}

	/**
	 * Método para consultar los datos del producto que serán mostrados en la vista de cotizaciones o proformas
	 * @param  [type] $empresa_id  Código de la empresa
	 * @param  [type] $deposito_id Código del depósito
	 * @param  [type] $venta_id Id de la venta.
	 * @param  [type] $detalle_id Id del detalle de la venta a eliminar
	 * @return [type]              [description]
	 */
	public function eliminarReglon($empresa_id, $deposito_id, $venta_id, $detalle_id) {

		try {

			DB::beginTransaction();

			//Hago una consulta sobre la venta en cuestión
			$venta = Venta::where('ventas.empresa_id', $empresa_id)
				->where('ventas.deposito_id', $deposito_id)
				->where('ventas.id', $venta_id)
				->first();

			$detalleVenta = DetalleVenta::where('detalle_ventas.empresa_id', $empresa_id)
				->where('detalle_ventas.deposito_id', $deposito_id)
				->where('detalle_ventas.venta_id', $venta_id)
				->where('detalle_ventas.id', $detalle_id)
				->first();

			//Sea una cotización o una proforma, se debe actualizar el maestro de la venta, debido a que el detalle cambió, por ende los totales y demás tambien lo hacen
			$venta->subtotal = $venta->subtotal - ($detalleVenta->cantidad * $detalleVenta->precio);
			$venta->total    = $venta->total - $detalleVenta->subtotal;
			$venta->impuesto = $venta->impuesto - $detalleVenta->impuesto;
			$venta->save();
			//subtotal (subtotal de venta - (detalle[cantidad] * detalle[precio])), total (restar al total de la factura el detalle[subtotal]), impuesto (impuesto total de venta, si el reglón eliminado tiene impuesto, restarlo de aquí. detalle['impuesto'])

			//Sólo en caso de que se trate de una proforma, se debe hacer movimiento de inventario.
			if ($venta->tipo_documento === "PRO") {
				//Se consulta la cantidad disponible del producto en cuestión
				$inventario = Inventario::select('inventarios.disponible', 'inventarios.cif_local', 'inventarios.separado')
					->where('inventarios.empresa_id', $empresa_id)
					->where('inventarios.deposito_id', $deposito_id)
					->where('inventarios.producto_id', $detalleVenta->producto_id)
					->first();

				$disponible = 0;
				if (!(is_null($inventario)) && !(is_null($inventario->disponible))) {
					$disponible = $inventario->disponible;
				}

				//Actualizo el campo disponible del inventario para ese producto, sumandole la cantidad que voy a eliminar (Devolviendo al inventario)
				Inventario::where('inventarios.empresa_id', $empresa_id)
					->where('inventarios.deposito_id', $deposito_id)
					->where('inventarios.producto_id', $detalleVenta->producto_id)
					->update(['disponible' => $disponible + $detalleVenta->cantidad]);

				//Ahora hay que restar el inventario separado.
				$separado = 0;
				if (!(is_null($inventario)) && !(is_null($inventario->separado))) {
					$separado = $inventario->separado;
				}

				//Actualizo el inventario separado
				Inventario::where('inventarios.empresa_id', $empresa_id)
					->where('inventarios.deposito_id', $deposito_id)
					->where('inventarios.producto_id', $detalleVenta->producto_id)
					->update(['separado' => $separado - $detalleVenta->cantidad]);
			}

			//Buscando el detalle de la venta involucrado y eliminando el registro de la BD
			DetalleVenta::where('detalle_ventas.empresa_id', $empresa_id)
				->where('detalle_ventas.deposito_id', $deposito_id)
				->where('detalle_ventas.venta_id', $venta_id)
				->where('detalle_ventas.id', $detalle_id)
				->delete();

			DB::commit();
			return response()->json(["message" => "Reglón eliminado correctamente"], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de eliminar el reglón de la cotización o proforma (eliminarReglon) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de eliminar el reglón de la cotización o proforma (eliminarReglon)", 500);
		}
	}

	/**
	 * Método para consultar los últimos 2 precios en los que fue vendido un producto
	 *
	 * @param  [type] $empresa_id      [Id de la empresa]
	 * @param  [type] $deposito_id     [Id del depósito]
	 * @param  [type] $producto_codigo [Código del producto]
	 * @return [type]                  [Último precio en que se vendío el producto]
	 */
	public function ultimos2Precios($empresa_id, $deposito_id, $producto_codigo) {

		try {

			$UltimosPrecios = Venta::select('detalle_ventas.precio')
				->join('detalle_ventas', 'detalle_ventas.venta_id', 'ventas.id')
				->whereColumn('detalle_ventas.empresa_id', 'ventas.empresa_id')
				->whereColumn('detalle_ventas.deposito_id', 'ventas.deposito_id')
				->join('productos', 'productos.id', 'detalle_ventas.producto_id')
				->whereColumn('productos.empresa_id', 'ventas.empresa_id')
				->where('ventas.empresa_id', $empresa_id)
				->where('ventas.deposito_id', $deposito_id)
				->where('ventas.estado', 'F')
				->where('productos.codigo', $producto_codigo)
				->orderBy('ventas.fecha_hora', 'desc')
				->take(2)
				->get();

			return $UltimosPrecios;

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los últimos 2 precios a los que fue vendido un producto (UltimosPrecios) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los últimos 2 precios a los que fue vendido un producto (UltimosPrecios)", 500);
		}
	}
}