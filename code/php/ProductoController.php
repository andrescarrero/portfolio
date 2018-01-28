<?php

namespace Evolutio\Http\Controllers;

use Datatables;
use DB;
use Evolutio\Http\Controllers\NumeroSerieController;
use Evolutio\Http\Requests\ProductoRequest;
use Evolutio\Kit;
use Evolutio\NumeroSerie;
use Evolutio\Producto;
use Evolutio\ProductoCompatible;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Maatwebsite\Excel\Facades\Excel;

class ProductoController extends Controller {

	public function getIcon($valor) {

		$columna = "";

		switch ($valor) {
		case 1: //Producto con inventario disponible
			$columna = '<i class="material-icons" style="font-size:24px;color:green">fiber_manual_record</i>';
			break;
		case 2: //Producto sin inventario disponible
			$columna = '<i class="material-icons" style="font-size:24px;color:red">fiber_manual_record</i>';
			break;
		case 3: //Inventario se esta agotando
			$columna = '<i class="material-icons" style="font-size:24px;color:orange">fiber_manual_record</i>';
			break;
		case 4: //Inventario en negativo
			$columna = '<i class="material-icons" style="font-size:24px;color:red">pan_tool</i>';
			break;
		case 5: //Producto inactivo
			$columna = '<i class="material-icons" style="font-size:24px;">clear</i>';
			break;
		case 6: //Falta información importante
			$columna = '<i class="material-icons" style="font-size:24px;color:orange">bug_report</i>';
			break;
		case 7: //Producto con N dias sin ventas
			$columna = '<i class="material-icons" style="font-size:24px;color:orange">warning</i>';
			break;
		case 8: //Item de producción
			$columna = '<i class="material-icons" style="font-size:24px;">build</i>';
			break;
		case 9: //Producto variable
			$columna = '<i class="material-icons" style="font-size:24px;">border_color</i>';
			break;
		case 10: //Servicio
			$columna = '<i class="material-icons" style="font-size:24px;">home</i>';
			break;
		case 11: //kit de productos
			$columna = '<i class="material-icons" style="font-size:24px;">settings_input_composite</i>';
			break;
		case 12: //Producto con números de serie
			$columna = '<i class="material-icons" style="font-size:24px;">list</i>';
			break;
		case 13: //Facturación permitida temporalmente
			$columna = '<i class="material-icons" style="font-size:24px;color:green">swap_vertical_circle</i>';
			break;
		case 14: //Pedido en transito
			$columna = '<i class="material-icons" style="font-size:24px;color:red">sync</i>';
			break;
		case 15: //Pedido efectuado sin registrar
			$columna = '<i class="material-icons" style="font-size:24px;color:blue">sync</i>';
			break;
		case 16: //Marcado para pedir
			$columna = '<i class="material-icons" style="font-size:24px;">check_box</i>';
			break;
		case 17: //Marcado para pedir URGENTE
			$columna = '<i class="material-icons" style="font-size:24px;color:red">check_box</i>';
			break;
		case 18: //Marcado para pedir URGENTE
			$columna = '<i class="material-icons" style="font-size:24px;">check_box_outline_blank</i>';
			break;
		case 19: //El item tiene código compatibles
			$columna = '<i class="material-icons" style="font-size:24px;">attach_file</i>';
			break;
		case 20: //foto relacionada al item
			$columna = '<i class="material-icons" style="font-size:24px;">photo_camera</i>';
			break;
		case 21: //video relacionado al item
			$columna = '<i class="material-icons" style="font-size:24px;">videocam</i>';
			break;
		case 22: //foto y video disponible
			$columna = '<i class="material-icons” style="font-size:24px;">add_a_photo</i>';
			break;
		default:
			$columna = '';
			break;
		}
		return $columna;
	}

	/**
	 * Para obtener un json de todoslos productos
	 * @return [type] [description]
	 */
	public function getDatatable($valores) {
		try {
			$listValores = explode(":", $valores);
			$empresa_id  = $listValores[0];
			$deposito_id = $listValores[1];

			$productsTable = DB::table("producto_table")
				->select(
					"producto_table.codigo_producto",
					"producto_table.nombre_producto",
					"producto_table.columna_a",
					"producto_table.columna_b",
					"producto_table.columna_c",
					"producto_table.columna_d",
					"producto_table.columna_e",
					"inventarios.disponible",
					"producto_table.grupo_id",
					"producto_table.grupo_nombre",
					"producto_table.ubicacion",
					"producto_table.codigo2",
					"producto_table.comprames3",
					"producto_table.comprames2",
					"producto_table.comprames1",
					"producto_table.comprames",
					"producto_table.ventames3",
					"producto_table.ventames2",
					"producto_table.ventames1",
					"producto_table.ventames",
					/*"deposito_id",*/
					"producto_table.empresa_id",
					"producto_table.itotal")
				->leftjoin('inventarios', function ($leftjoin) use ($deposito_id) {
					$leftjoin->on('producto_table.empresa_id', '=', 'inventarios.empresa_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id))
						->on('producto_table.producto_id', '=', 'inventarios.producto_id');

				})
				->where('producto_table.empresa_id', '=', $empresa_id)
				->orderBy("producto_table.producto_id", "desc");

			return Datatables::of($productsTable)
				->editColumn('columna_a', function ($row) {
					return $this->getIcon($row->columna_a);
				})
				->editColumn('columna_b', function ($row) {
					return $this->getIcon($row->columna_b);
				})
				->editColumn('columna_c', function ($row) {
					return $this->getIcon($row->columna_c);
				})
				->editColumn('columna_d', function ($row) {
					return $this->getIcon($row->columna_d);
				})
				->editColumn('columna_e', function ($row) {
					return $this->getIcon($row->columna_e);
				})
				->rawColumns(['columna_a', 'columna_b', 'columna_c', 'columna_d', 'columna_e'])
				->make(true);
		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de obtener datatable productos: {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de obtener datatable productos", 500);
		}
	}

	public function getProductByCodigo($empresa_id, $codigo) {
		try {

			$producto = DB::table("productos")
				->where("empresa_id", $empresa_id)
				->where("codigo", $codigo)
				->get();
			return response()->json(["producto" => $producto], 200);
		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de obtener el producto mediante product_id: {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de obtener el producto mediante product_id", 500);
		}
	}

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
	 * Método para proceder con el guardado de un producto proveniente de un formulario.
	 *
	 * @param  \Illuminate\Http\Request  $request
	 * @return \Illuminate\Http\Response
	 */
	public function store(ProductoRequest $request) {

		try {

			DB::beginTransaction();

			$producto = new Producto([
				//Por defecto
				'empresa_id'               => $request->formProducto['empresa_id'],
				'trash'                    => $request->formProducto['trash'],
				//Ficha A
				'codigo'                   => $request->formProducto['codigo'],
				'estatus'                  => $request->formProducto['estatus'],
				'grupo_id'                 => $request->formProducto['grupo_id'],
				'padre_grupo'              => $request->formProducto['padre_grupo'] == "true" ? 'S' : 'N',
				'nombre'                   => $request->formProducto['nombre'],
				'max_dias_venta'           => $request->formProducto['max_dias_venta'],
				'porc_imp_venta'           => $request->formProducto['porc_imp_venta'],
				'codigo2'                  => $request->formProducto['codigo2'],
				'barcode'                  => $request->formProducto['barcode'],
				'nombre2'                  => $request->formProducto['nombre2'],
				'categoria_id'             => $request->formProducto['categoria_id'],
				'sub_grupo_id'             => $request->formProducto['sub_grupo_id'],
				'marca_id'                 => $request->formProducto['marca_id'],
				'pais_id'                  => $request->formProducto['pais_id'],
				'fabrica_id'               => $request->formProducto['fabrica_id'],
				'garantia'                 => $request->formProducto['garantia'],
				'proveedor_id'             => $request->formProducto['proveedor_id'],
				'tipo_producto'            => $request->formProducto['tipo_producto'],
				//Ficha B
				'ancho'                    => $request->formProducto['ancho'],
				'largo'                    => $request->formProducto['largo'],
				'alto'                     => $request->formProducto['alto'],
				'factura_decimal'          => $request->formProducto['factura_decimal'] == "true" ? 'S' : 'N',
				'comision_vendedor'        => $request->formProducto['comision_vendedor'] == "true" ? 'S' : 'N',
				'comision_comisionista'    => $request->formProducto['comision_comisionista'] == "true" ? 'S' : 'N',
				'peso_bruto'               => $request->formProducto['peso_bruto'],
				'activar_num_serie'        => $request->formProducto['activar_num_serie'] == "true" ? 'S' : 'N',
				'peso_neto'                => $request->formProducto['peso_neto'],
				'unidad_medida_id'         => $request->formProducto['unidad_medida_id'],
				'cantidad_por_empaque'     => $request->formProducto['cantidad_por_empaque'],
				'acepta_ajuste_precio'     => $request->formProducto['acepta_ajuste_precio'] == "true" ? 'S' : 'N',
				'art_lento_mov'            => $request->formProducto['art_lento_mov'] == "true" ? 'S' : 'N',
				'marcar_inventario_minimo' => $request->formProducto['marcar_inventario_minimo'] == "true" ? 'S' : 'N',
				'cantidad_sugerida'        => $request->formProducto['cantidad_sugerida'] == "true" ? 'S' : 'N',
				'factura_tiempo'           => $request->formProducto['factura_tiempo'] == "true" ? 'S' : 'N',
				'porcentaje_comision'      => $request->formProducto['porcentaje_comision'],
				'porcentaje_descuento'     => $request->formProducto['porcentaje_descuento'],
				'suplir_codigo2'           => $request->formProducto['suplir_codigo2'] == "true" ? 'S' : 'N',
				'dimesion_precio_fact'     => $request->formProducto['dimesion_precio_fact'] == "true" ? 'S' : 'N',
				'arancel_id'               => $request->formProducto['arancel_id'],
				'lento_movimiento'         => $request->formProducto['lento_movimiento'],
				'venta_promo'              => $request->formProducto['venta_promo'],
				'precio'                   => $request->formProducto['precio'],
				'puntos_venta'             => $request->formProducto['puntos_venta'],
			]);
			$producto->save();

			//Almacenando la foto
			if ($request->formProducto['photo'] != null && $request->formProducto['photo'] != "SinFoto") {

				$data              = $request->formProducto['photo'];
				list($type, $data) = explode(';', $data);
				list(, $data)      = explode(',', $data);
				$data              = base64_decode($data);
				$imageName         = $producto->id . '.png';
				// $imageName = $producto->id."-".time().'.png';

				file_put_contents('images/productos/' . $imageName, $data);
				$producto["foto"] = "images/productos/" . $imageName;
				$producto->save();
			}

			//Solo si hay productos compatibles definidos
			if (array_key_exists('compatibilidad', $request->formProducto)) {
				foreach ($request->formProducto['compatibilidad'] as $row => $innerArray) {
					$compatible = new ProductoCompatible([
						'producto_id'            => $producto->id,
						'producto_compatible_id' => $innerArray['id'],
						'empresa_id'             => $producto->empresa_id,
					]);
					$compatible->save();
				}
			}

			//Solo si hay Kit Admin definidos
			if (array_key_exists('kitAdmin', $request->formProducto)) {
				foreach ($request->formProducto['kitAdmin'] as $row => $innerArray) {
					$kit = new Kit([
						'producto_id'     => $producto->id,
						'producto_kid_id' => $innerArray['id'],
						'cantidad'        => $innerArray['cantidad'],
						'empresa_id'      => $producto->empresa_id,
					]);
					$kit->save();
				}
			}

			//Solo si hay Series Definidas
			if (array_key_exists('series', $request->formProducto)) {
				foreach ($request->formProducto['series'] as $row => $innerArray) {
					$serie = new NumeroSerie([
						'producto_id'    => $producto->id,
						'empresa_id'     => $producto->empresa_id,
						'numero_serie'   => $innerArray['serie'],
						'factura_compra' => $innerArray['fact_c'],
						'factura_venta'  => $innerArray['fact_v'],
						'nota_credito'   => $innerArray['doc'],
						'deposito'       => $innerArray['depo'],
					]);
					$serie->save();
				}
			}

			//creación de la datos de inventario
			$inventarioController = new InventarioController();
			$result               = $inventarioController->storeInventario($request->formInventario, $producto->id);

			if (strlen(trim($result)) > 0) {
				DB::rollBack();
				return response()->json(["message" => $result], 500);
			}

			DB::commit();
			return response()->json(["message" => "Producto Creado Exitosamente."], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de guardar el producto: {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de guardar el producto", 500);
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
	public function update(ProductoRequest $request, $id) {
		try {

			DB::beginTransaction();

			Producto::where("empresa_id", $request->formProducto['empresa_id'])
				->where("id", $id)
				->update([
					'empresa_id'               => $request->formProducto['empresa_id'],
					'trash'                    => $request->formProducto['trash'],
					'codigo'                   => $request->formProducto['codigo'],
					'estatus'                  => $request->formProducto['estatus'],
					'grupo_id'                 => $request->formProducto['grupo_id'],
					'padre_grupo'              => $request->formProducto['padre_grupo'] == "true" ? 'S' : 'N',
					'nombre'                   => $request->formProducto['nombre'],
					'max_dias_venta'           => $request->formProducto['max_dias_venta'],
					'porc_imp_venta'           => $request->formProducto['porc_imp_venta'],
					'codigo2'                  => $request->formProducto['codigo2'],
					'barcode'                  => $request->formProducto['barcode'],
					'nombre2'                  => $request->formProducto['nombre2'],
					'categoria_id'             => $request->formProducto['categoria_id'],
					'sub_grupo_id'             => $request->formProducto['sub_grupo_id'],
					'marca_id'                 => $request->formProducto['marca_id'],
					'pais_id'                  => $request->formProducto['pais_id'],
					'fabrica_id'               => $request->formProducto['fabrica_id'],
					'garantia'                 => $request->formProducto['garantia'],
					'proveedor_id'             => $request->formProducto['proveedor_id'],
					'tipo_producto'            => $request->formProducto['tipo_producto'],
					'ancho'                    => $request->formProducto['ancho'],
					'largo'                    => $request->formProducto['largo'],
					'alto'                     => $request->formProducto['alto'],
					'factura_decimal'          => $request->formProducto['factura_decimal'] == "true" ? 'S' : 'N',
					'comision_vendedor'        => $request->formProducto['comision_vendedor'] == "true" ? 'S' : 'N',
					'comision_comisionista'    => $request->formProducto['comision_comisionista'] == "true" ? 'S' : 'N',
					'peso_bruto'               => $request->formProducto['peso_bruto'],
					'activar_num_serie'        => $request->formProducto['activar_num_serie'] == "true" ? 'S' : 'N',
					'peso_neto'                => $request->formProducto['peso_neto'],
					'unidad_medida_id'         => $request->formProducto['unidad_medida_id'],
					'cantidad_por_empaque'     => $request->formProducto['cantidad_por_empaque'],
					'acepta_ajuste_precio'     => $request->formProducto['acepta_ajuste_precio'] == "true" ? 'S' : 'N',
					'art_lento_mov'            => $request->formProducto['art_lento_mov'] == "true" ? 'S' : 'N',
					'marcar_inventario_minimo' => $request->formProducto['marcar_inventario_minimo'] == "true" ? 'S' : 'N',
					'cantidad_sugerida'        => $request->formProducto['cantidad_sugerida'] == "true" ? 'S' : 'N',
					'factura_tiempo'           => $request->formProducto['factura_tiempo'] == "true" ? 'S' : 'N',
					'porcentaje_comision'      => $request->formProducto['porcentaje_comision'],
					'porcentaje_descuento'     => $request->formProducto['porcentaje_descuento'],
					'suplir_codigo2'           => $request->formProducto['suplir_codigo2'] == "true" ? 'S' : 'N',
					'dimesion_precio_fact'     => $request->formProducto['dimesion_precio_fact'] == "true" ? 'S' : 'N',
					'arancel_id'               => $request->formProducto['arancel_id'],
					'lento_movimiento'         => $request->formProducto['lento_movimiento'],
					'venta_promo'              => $request->formProducto['venta_promo'],
					'precio'                   => $request->formProducto['precio'],
					'puntos_venta'             => $request->formProducto['puntos_venta'],
				]);

			$producto = Producto::where("empresa_id", $request->formProducto['empresa_id'])
				->where("id", $id)
				->first();

			//Almacenando la foto
			if ($request->formProducto['photo'] != null && $request->formProducto['photo'] != "Foto") {

				if ($request->formProducto['photo'] == "SinFoto") {
					$producto["foto"] = null;
					$producto->save();
				} else {
					$data              = $request->formProducto['photo'];
					list($type, $data) = explode(';', $data);
					list(, $data)      = explode(',', $data);
					$data              = base64_decode($data);
					$imageName         = $producto->id . '.png';
					// $imageName = $producto->id."-".time().'.png';

					file_put_contents('images/productos/' . $imageName, $data);
					$producto["foto"] = "images/productos/" . $imageName;
					$producto->save();
				}
			}

			//Solo si hay productos compatibles definidos, se debe evaluar si ya están en BD (no pasa nada), si no están (se agregan) o si dejaron de estar (se borran)

			//Se hace una consulta de lo que está en BD
			$productoCompatible = ProductoCompatible::where("producto_id", $id)
				->where("empresa_id", $request->formProducto['empresa_id'])
				->pluck('producto_compatible_id')->toArray();

			//Si existe la compatibilidad en el request.
			if (array_key_exists('compatibilidad', $request->formProducto)) {

				//Se busca primero que hay de nuevo que no esté en BD (Agregar)
				foreach ($request->formProducto['compatibilidad'] as $row => $compatible_update) {
					if (!in_array($compatible_update['id'], $productoCompatible)) {
						//Si no está, se crea el registro en BD.
						$compatible = new ProductoCompatible([
							'producto_id'            => $id,
							'producto_compatible_id' => $compatible_update['id'],
							'empresa_id'             => $request->formProducto['empresa_id'],
						]);
						$compatible->save();
					}
				}

				//Se busca lo que esté en BD que no esté en lo nuevo (Eliminar)
				foreach ($productoCompatible as $compatible_DB) {
					$encontrado = false;

					foreach ($request->formProducto['compatibilidad'] as $row => $compatible_update) {
						if ($compatible_DB == $compatible_update['id']) {
							$encontrado = true;
						}
					}

					if ($encontrado == false) {
						//Se borra lo que no llegó por request (fue eliminado y debe ser eliminado de BD)
						ProductoCompatible::where("producto_id", $id)
							->where("empresa_id", $request->formProducto['empresa_id'])
							->where("producto_compatible_id", $compatible_DB)
							->delete();
					}
				}

			} else if (count($productoCompatible) > 0) {
				//En el caso de que no venga compatibilidad por el Request pero si haya información en BD se debe borrar todo lo de compatibilidad
				ProductoCompatible::where("producto_id", $id)
					->where("empresa_id", $request->formProducto['empresa_id'])
					->delete();
			}

			//En el caso de ser un Kit.
			//Se hace la consulta de la tabla correspondiente al Kit, se debe evaluar si ya está en BD y es igual(no pasa nada), si está en BD pero la cantidad es diferente (se hace update), si no está (se agregan) o si dejaron de estar (se borran)

			//Se hace una consulta de lo que está en BD
			$productoKit = Kit::select('producto_kid_id', 'cantidad')
				->where("producto_id", $id)
				->where("empresa_id", $request->formProducto['empresa_id'])
				->get();

			//Si existe el kit en el request.
			if (array_key_exists('kitAdmin', $request->formProducto)) {

				//Se busca primero que hay de nuevo que no esté en BD (Agregar)
				foreach ($request->formProducto['kitAdmin'] as $row => $kit_update) {
					$encontrado = false;
					$update     = false;
					$cantidad   = 0;
					foreach ($productoKit as $kit_DB) {
						if ($kit_update['id'] == $kit_DB['producto_kid_id']) {
							$encontrado = true;
							if ($kit_update['cantidad'] != $kit_DB['cantidad']) {
								$update   = true;
								$cantidad = $kit_update['cantidad'];
							}
						}
					}
					if ($encontrado == true && $update == true) {
						//Se encontró el elemento en BD pero con cantidad diferente, es necesario hacer update
						Kit::where("producto_id", $id)
							->where("empresa_id", $request->formProducto['empresa_id'])
							->where("producto_kid_id", $kit_update['id'])
							->update(['cantidad' => $kit_update['cantidad']]);
					} else if ($encontrado == false) {
						//No se encontró el elemento en la BD, se debe agregar
						$kit = new kit([
							'producto_id'     => $id,
							'producto_kid_id' => $kit_update['id'],
							'cantidad'        => $kit_update['cantidad'],
							'empresa_id'      => $request->formProducto['empresa_id'],
						]);
						$kit->save();
					}
				}

				//Se busca lo que esté en BD que no esté en lo nuevo (Eliminar)
				foreach ($productoKit as $kit_DB) {
					$encontrado = false;

					foreach ($request->formProducto['kitAdmin'] as $row => $kit_update) {
						if ($kit_DB['producto_kid_id'] == $kit_update['id']) {
							$encontrado = true;
						}
					}

					if ($encontrado == false) {
						//Se borra lo que no llegó por request (fue eliminado y debe ser eliminado de BD)
						Kit::where("producto_id", $id)
							->where("empresa_id", $request->formProducto['empresa_id'])
							->where("producto_kid_id", $kit_DB['producto_kid_id'])
							->delete();
					}
				}

			} else if (count($productoKit) > 0) {
				//En el caso de que no venga Kit por el Request pero si haya información en BD se debe borrar todo lo perteneciente al Kit.
				Kit::where("producto_id", $id)
					->where("empresa_id", $request->formProducto['empresa_id'])
					->delete();
			}

			//Solo si hay Series definidas, se debe evaluar si ya están en BD (no pasa nada), si no están (se agregan) o si dejaron de estar (se borran)
			//Se hace una consulta de lo que está en BD
			$series = NumeroSerie::select('numero_serie', 'factura_compra', 'factura_venta', 'nota_credito', 'deposito')
				->where("producto_id", $id)
				->where("empresa_id", $request->formProducto['empresa_id'])
				->get();

			//Si existe una serie en el request
			if (array_key_exists('series', $request->formProducto)) {

				//Se busca primero que hay de nuevo que no esté en BD (Agregar)
				foreach ($request->formProducto['series'] as $row => $serie_update) {
					$encontrado = false;
					$update     = false;
					foreach ($series as $series_DB) {
						//El registro ya existe
						if ($serie_update['serie'] == $series_DB['numero_serie']) {
							$encontrado = true;

							//Ya existe pero se modificó algún valor de la BD
							if ($serie_update['fact_c'] != $series_DB['factura_compra'] || $serie_update['fact_v'] != $series_DB['factura_venta']
								|| $serie_update['doc'] != $series_DB['nota_credito'] || $serie_update['depo'] != $series_DB['deposito']) {
								$update = true;
							}
						}
					}
					if ($encontrado == true && $update == true) {

						//Se encontró el elemento en BD pero con alguno de los valores diferentes, es necesario hacer update
						NumeroSerie::where("producto_id", $id)
							->where("empresa_id", $request->formProducto['empresa_id'])
							->where("numero_serie", $serie_update['serie'])
							->update(['factura_compra' => $serie_update['fact_c'],
								'factura_venta'            => $serie_update['fact_v'],
								'nota_credito'             => $serie_update['doc'],
								'deposito'                 => $serie_update['depo']]);
					} else if ($encontrado == false) {
						//No se encontró el elemento en la BD, se debe agregar
						$serie = new NumeroSerie([
							'producto_id'    => $id,
							'empresa_id'     => $request->formProducto['empresa_id'],
							'numero_serie'   => $serie_update['serie'],
							'factura_compra' => $serie_update['fact_c'],
							'factura_venta'  => $serie_update['fact_v'],
							'nota_credito'   => $serie_update['doc'],
							'deposito'       => $serie_update['depo'],
						]);
						$serie->save();
					}
				}

				//Se busca lo que esté en BD que no esté en lo nuevo (Eliminar)
				foreach ($series as $series_DB) {
					$encontrado = false;

					foreach ($request->formProducto['series'] as $row => $serie_update) {
						if ($series_DB['numero_serie'] == $serie_update['serie']) {
							$encontrado = true;
						}
					}

					if ($encontrado == false) {
						//Se borra lo que no llegó por request (fue eliminado y debe ser eliminado de BD)
						NumeroSerie::where("producto_id", $id)
							->where("empresa_id", $request->formProducto['empresa_id'])
							->where("numero_serie", $series_DB['numero_serie'])
							->delete();
					}
				}

			} else if (count($series) > 0) {
				//En el caso de que no vengan series por el Request pero si haya información en BD se deben borrar todas las series
				NumeroSerie::where("producto_id", $id)
					->where("empresa_id", $request->formProducto['empresa_id'])
					->delete();
			}

			//creación de la datos de inventario
			$inventarioController = new InventarioController();
			$result               = $inventarioController->updateInventario($request->formInventario, $id);

			if (strlen(trim($result)) > 0) {
				DB::rollBack();
				return response()->json(["message" => $result], 500);
			}

			DB::commit();

			return response()
				->json(["message" => "Producto actualizado correctamente."], 200);

		} catch (\Exception $e) {
			DB::rollBack();
			Log::critical("Ha ocurrido un problema al tratar de actualizar el producto: (update) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de actualizar el producto: (update)", 500);
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
	 * Método para determinar si deteminado grupo posee un padre.
	 * @param  grupo_id del que se quiere consultar si posee un producto que sea padre.
	 * @return response json.
	 */
	public function isPadre($empresa_id, $grupo_id) {
		try {
			$productoPadre = DB::table("productos")
				->select("productos.id")
				->where("productos.grupo_id", $grupo_id)
				->where("productos.padre_grupo", 'S')
				->where("productos.empresa_id", $empresa_id)
				->get();

			return response()->json(["productoPadre" => $productoPadre], 200);
		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de consultar si un producto es Padre de un grupo (isPadre): {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de consultar si un producto es Padre de un grupo (isPadre)", 500);
		}
	}

/**
 * Método que consulta todos los productos que se encuentren visibles (trash = '0') y con estatus activo (estatus = 'A')
 * @param  $empresa_id
 * @return Respuesta json con los productos encontrados.
 */
	public function getProductosActivos($empresa_id) {
		try {
			$productos = DB::table("productos")
				->select("codigo", "nombre", "codigo2", "barcode", "id")
				->where("trash", '0')
				->where("estatus", 'A')
				->where("empresa_id", $empresa_id)
				->get();

			return response()->json(["productos" => $productos], 200);
		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de consultar los productos activos (getProductosActivos): {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de consultar los productos activos (getProductosActivos)", 500);
		}
	}

/**
 * Método para la eliminación lógica de un producto, se cambia a trash = '1' (Eliminado)
 * @param  $empresa_id  Código de la empresa
 * @param  $producto_id Código del producto a eliminar
 * @return Respuesta Json
 */
	public function eliminacionLogica($empresa_id, $producto_id) {

		try {
			Producto::where("empresa_id", $empresa_id)
				->where("id", $producto_id)
				->update(["trash" => '1']);

			return response()->json(["message" => "Producto eliminado correctamente."], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de eliminar de manera lógica el producto: (eliminacionLogica) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de eliminar de manera lógica el producto: (eliminacionLogica)", 500);
		}
	}

/**
 * Método para consultar los datos de compatibilidad que serán mostrados en la vista.
 * @param  [type] $empresa_id  Código de la empresa
 * @param  [type] $producto_id Código del producto
 * @return [type]              [description]
 */
	public function consultarDatosCompatibilidad($empresa_id, $deposito_id, $producto_codigo) {

		try {

			$producto = Producto::select('productos.codigo', 'marcas.nombre as marca', 'productos.nombre', DB::raw('IFNULL(inventarios.disponible,0) disponible'))
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->leftJoin('marcas', 'productos.marca_id', '=', 'marcas.id')
				->where("productos.empresa_id", $empresa_id)
				->where("productos.codigo", $producto_codigo)
				->get();

			return response()->json(["producto" => $producto], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos de compatibilidad (consultarDatosCompatibilidad) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos de compatibilidad (consultarDatosCompatibilidad)", 500);
		}
	}

	/**
	 * Método para consultar si un producto posee productos compatibles asociados, y conocer su id, codigo, marca, nombre (descripcion) y disponibilidad
	 *
	 * @param  [type] $empresa_id  Código de la empresa
	 * @param  [type] $deposito_id Código del depósito
	 * @param  [type] $producto_id Id del producto
	 * @return [type]              [description]
	 */
	public function consultarProductosCompatibles($empresa_id, $deposito_id, $producto_id) {
		try {

			$productoCompatible = ProductoCompatible::select('productos.id', 'productos.codigo', 'marcas.nombre as marca', 'productos.nombre', DB::raw('IFNULL(inventarios.disponible,0) disponible'))
				->leftJoin('productos', 'producto_compatibles.producto_compatible_id', '=', 'productos.id')
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->leftJoin('marcas', 'productos.marca_id', '=', 'marcas.id')
				->whereColumn("productos.empresa_id", "producto_compatibles.empresa_id")
				->where('producto_compatibles.producto_id', $producto_id)
				->where('producto_compatibles.empresa_id', $empresa_id)
				->get();

			return response()->json(["productoCompatible" => $productoCompatible], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los Productos Compatibles (consultarProductosCompatibles) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los Productos Compatibles (consultarProductosCompatibles)", 500);
		}
	}

	/**
	 * Método que realiza la consulta de los campos que se muestran en la vista de Datos Del Kit (Codigo, Descripcion, Cantidad y Precio).
	 * @param  [type] $empresa_id      Código de la empresa
	 * @param  [type] $deposito_id     Código del depósito
	 * @param  [type] $producto_codigo Código del producto
	 * @return [type]                  [description]
	 */
	public function consultarDatosKit($empresa_id, $deposito_id, $producto_codigo) {

		try {

			$producto = Producto::select('productos.codigo', 'productos.nombre', DB::raw('IFNULL(inventarios.precio_a,0) precio_a'))
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->where("productos.empresa_id", $empresa_id)
				->where("productos.codigo", $producto_codigo)
				->get();

			return response()->json(["producto" => $producto], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos a ubicar en el Kit (consultarDatosKit) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos a ubicar en el Kit (consultarDatosKit)", 500);
		}
	}

	/**
	 * Método para consultar los productos que posee un Kit y conocer su id, codigo, descripción, cantidad y precio
	 *
	 * @param  [type] $empresa_id  Código de la empresa
	 * @param  [type] $deposito_id Código del depósito
	 * @param  [type] $producto_id Id del producto
	 * @return [type]              [description]
	 */
	public function consultarProductosKit($empresa_id, $deposito_id, $producto_id) {
		try {

			$productoKit = Kit::select('productos.id', 'productos.codigo', 'productos.nombre', 'kits.cantidad', DB::raw('IFNULL(inventarios.precio_a,0) precio_a'))
				->leftJoin('productos', 'kits.producto_kid_id', '=', 'productos.id')
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->whereColumn("kits.empresa_id", "productos.empresa_id")
				->where('kits.producto_id', $producto_id)
				->where('kits.empresa_id', $empresa_id)
				->get();

			return response()->json(["productoKit" => $productoKit], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los productos del Kit (consultarProductosKit) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los productos del Kit (consultarProductosKit) ", 500);
		}
	}

	/**
	 * Método para consultar los datos de Ajustes que serán mostrados en la vista.
	 * @param  [type] $empresa_id  Código de la empresa
	 * @param  [type] $deposito_id Código del depósito
	 * @param  [type] $producto_id Código del producto
	 * @return [type]              [description]
	 */
	public function consultarDatosAjuste($empresa_id, $deposito_id, $producto_codigo) {

		try {

			$producto = Producto::select('productos.codigo', 'productos.nombre', DB::raw('IFNULL(inventarios.disponible,0) disponible'))
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->leftJoin('marcas', 'productos.marca_id', '=', 'marcas.id')
				->where("productos.empresa_id", $empresa_id)
				->where("productos.codigo", $producto_codigo)
				->get();

			return response()->json(["producto" => $producto], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos de ajustes (consultarDatosAjustes) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos de ajustes (consultarDatosAjustes)", 500);
		}
	}

	/**
	 * Método que realiza la consulta de los campos que se muestran en la vista de Pedidos (Codigo, Descripcion, Cantidad y Precio).
	 * @param  [type] $empresa_id      Código de la empresa
	 * @param  [type] $deposito_id     Código del depósito
	 * @param  [type] $producto_codigo Código del producto
	 * @return [type]                  [description]
	 */
	public function consultarDatosFormPedido($empresa_id, $deposito_id, $producto_codigo) {

		try {

			$producto = Producto::select('productos.codigo', 'productos.nombre', DB::raw('IFNULL(inventarios.precio_a,0) precio_a'))
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->where("productos.empresa_id", $empresa_id)
				->where("productos.codigo", $producto_codigo)
				->get();

			return response()->json(["producto" => $producto], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos a ubicar en el Form de Pedido (consultarDatosFormPedido) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos a ubicar en el Form de Pedido (consultarDatosFormPedido)", 500);
		}
	}

	/**
	 * Método para consultar los datos del producto que serán mostrados en la vista de ventas para cargarse en la factura.
	 * @param  [type] $empresa_id  Código de la empresa
	 * @param  [type] $deposito_id Código del depósito
	 * @param  [type] $producto_id Código del producto
	 * @return [type]              [description]
	 */
	public function consultarProductoFactura($empresa_id, $deposito_id, $precio_cliente, $producto_codigo) {

		try {

			$productoInv = Producto::select('productos.id', 'productos.codigo', 'productos.nombre', 'productos.tipo_producto', 'productos.activar_num_serie as serie', 'marcas.nombre as marca', 'inventarios.ubicacion as ubicacion', 'productos.porc_imp_venta as impuesto', 'productos.factura_decimal as decimal')
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->leftJoin('marcas', function ($leftJoin) {
					$leftJoin->on('productos.empresa_id', '=', 'marcas.empresa_id')
						->on('productos.marca_id', '=', 'marcas.id');
				})
				->where("productos.empresa_id", $empresa_id)
				->where("productos.codigo", $producto_codigo)
				->first();

			//Consulta del inventario para consultar los precios
			$inventarioController = new InventarioController();

			//Se consultan los precios, se retorna el precio según la Jerarquía de precios previamente explicada.
			$precio = $inventarioController->consultarPreciosGenerales($empresa_id, $deposito_id, $producto_codigo, $precio_cliente);

			$producto              = array();
			$producto["id"]        = $productoInv["id"];
			$producto["codigo"]    = $productoInv["codigo"];
			$producto["nombre"]    = $productoInv["nombre"];
			$producto["marca"]     = $productoInv["marca"];
			$producto["ubicacion"] = $productoInv["ubicacion"];
			$producto["precio"]    = $precio;
			$producto["tipo"]      = $productoInv["tipo_producto"];
			$producto["serie"]     = $productoInv["serie"];
			$producto["impuesto"]  = $productoInv["impuesto"];
			$producto["decimal"]   = $productoInv["decimal"];

			return response()->json(["producto" => $producto], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos del producto para la factura (consultarProductoFactura) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos del producto para la factura (consultarProductoFactura)", 500);
		}
	}

	/**
	 * Método para consultar los datos del producto para la venta según su código (id,codigo,kit,series,ficha)
	 * @param  [type] $empresa_id [description]
	 * @param  [type] $codigo     [description]
	 * @return [type]             [description]
	 */

	/**
	 * Método para consultar los datos del producto para la venta según su código (id,codigo,kit,series,ficha)
	 * @param [type] $empresa_id      [Id de la empresa]
	 * @param [type] $deposito_id     [Id del deposito]
	 * @param [type] $producto_codigo [Código del producto]
	 * @param [type] $precio_cliente  [precio por defecto para el cliente]
	 */
	public function Ventas_productoDatosVenta($empresa_id, $deposito_id, $producto_codigo, $precio_cliente) {
		try {

			$producto = array();

			//Se hace una cosnulta de los datos básicos del producto
			$producto2 = DB::table("productos")
				->where("empresa_id", $empresa_id)
				->where("codigo", $producto_codigo)
				->first();

			if ($producto2 != null) {

				//validar oferta, precio cliente, precio_a
				$producto[] = array("id" => $producto2->id, "codigo" => $producto2->codigo, "descripcion" => $producto2->nombre, "cantidad" => 1, "precio" => 1, "tipo" => $producto2->tipo_producto, "serie" => $producto2->activar_num_serie, "impuesto" => $producto2->porc_imp_venta);

				$kit = null;

				//Se consulta si el producto consultado pertenece a un producto de tipo KIT
				if ($producto2->tipo_producto == "K") {
					$kit = Kit::select('p2.codigo', 'p2.nombre', 'p2.porc_imp_venta', 'kits.producto_kid_id', 'kits.cantidad')
						->join('productos as p', 'kits.producto_id', 'p.id')
						->join('productos as p2', 'kits.producto_kid_id', 'p2.id')
						->whereColumn('kits.empresa_id', 'p.empresa_id')
						->whereColumn('kits.empresa_id', 'p2.empresa_id')
						->where('p.codigo', $producto_codigo)
						->where('p.empresa_id', $empresa_id)
						->get();

					$producto = array();

					//Consultar los precios de cada producto del Kit para agregarlos a la factura
					foreach ($kit as &$valor) {
						/*
							            La Jerarquía de los precios es la siguiente:
							            Oferta
							            Precio_Cliente
							            Precio_a
						*/

						//Consulta del inventario para consultar los precios
						$inventarioController = new InventarioController();

						//Se consultan los precios, se retorna el precio según la Jerarquía de precios previamente explicada.
						$precio = $inventarioController->consultarPreciosGenerales($empresa_id, $deposito_id, $valor["codigo"], $precio_cliente);

						$producto[] = array("id" => $valor["producto_kid_id"], "codigo" => $valor["codigo"], "descripcion" => $valor["nombre"], "cantidad" => $valor["cantidad"], "precio" => $precio, "tipo" => "K", "impuesto" => $valor["porc_imp_venta"]);
					}
				} else if ($producto2->activar_num_serie == "S") {
					//Se trata de un producto con números de serie
					$producto = array();

					//Consulta los números de serie de un producto
					$numeroSerie = new NumeroSerieController();

					$serie = $numeroSerie->numerosDeSerieFactura($empresa_id, $producto_codigo);

					$producto[] = array("id" => $producto2->id, "codigo" => $producto2->codigo, "descripcion" => $producto2->nombre, "cantidad" => 1, "precio" => 1, "tipo" => "serie", "serie" => $serie, "impuesto" => $producto2->porc_imp_venta);
				}

			}
			return response()->json(["producto" => $producto], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de obtener los datos del producto para la venta (Ventas_productoDatosVenta): {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de obtener los datos del producto para la venta", 500);
		}
	}

	/**
	 * Método para consultar los datos del producto que serán mostrados en la vista de compras para cargarse en la orden de compra.
	 * @param  [type] $empresa_id  Código de la empresa
	 * @param  [type] $deposito_id Código del depósito
	 * @param  [type] $producto_id Código del producto
	 * @return [type]              [object]
	 */
	public function consultarProductoCompra($empresa_id, $deposito_id, $producto_codigo) {

		try {

			$productoInv = Producto::select('productos.codigo',
				'productos.nombre',
				'productos.id',
				'inventarios.fob_origen',
				'productos.porc_imp_venta')
				->leftJoin('inventarios', function ($leftJoin) use ($deposito_id) {
					$leftJoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('productos.id', '=', 'inventarios.producto_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id));
				})
				->where("productos.empresa_id", $empresa_id)
				->where(DB::raw('replace(productos.codigo,"/","")'), $producto_codigo)
				->first();

			return response()->json(["producto" => $productoInv], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de Consultar los datos del producto para la compra (consultarProductoCompra) {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de Consultar los datos del producto para la compra (consultarProductoCompra)", 500);
		}
	}

	public function importarprodcutos() {
		Excel::load('migracionProductos.csv', function ($reader) {

			foreach ($reader->get() as $producto) {
				Producto::create([
					'codigo'         => $producto->codigo,
					'nombre'         => $producto->nombre,
					'codigo2'        => $producto->codigo2,
					'max_dias_venta' => $producto->max_dias_venta,
					'porc_imp_venta' => $producto->porc_imp_venta,
					'padre_grupo'    => $producto->padre_grupo,
					'estatus'        => $producto->estatus,
					'empresa_id'     => $producto->empresa_id,
					'trash'          => $producto->trash,
				]);
			}
		});
		return Producto::all();
	}

	/**
	 * Método que realiza la consulta de los datos del producto que se cargan en la tabla de Inventario.
	 * @param  [type] $empresa_id  [Empresa Id]
	 * @param  [type] $deposito_id [Deposito Id]
	 * @return [type]              [description]
	 */
	public function getDatatableProductos($empresa_id, $deposito_id) {

		try {

			return Datatables::eloquent(
				Producto::select('productos.id as id', 'productos.codigo as codigo', 'productos.codigo2 as codigo2', 'productos.nombre as nombre',
					DB::raw('i1.disponible as disponible'),
					DB::raw('IFNULL(i1.pedido,0) ipedido'),
					DB::raw('IFNULL(i1.ubicacion,"") ubicacion'),
					DB::raw('IFNULL(i1.precio_a,"") precio_a'),
					DB::raw('IFNULL(i1.precio_b,"") precio_b'),
					DB::raw('IFNULL(i1.cif_local,"") cif'))
					->leftJoin('inventarios as i1', function ($leftJoin) use ($deposito_id) {
						$leftJoin->on('productos.empresa_id', '=', 'i1.empresa_id')
							->on('productos.id', '=', 'i1.producto_id')
							->on('i1.deposito_id', '=', DB::raw($deposito_id));
					})
					->where("productos.empresa_id", $empresa_id)
					->where("productos.trash", 0)
					->orderBy("productos.codigo", "asc")
					->orderBy("productos.nombre", "asc")
			)->make(true);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de obtener datatable productos: {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de obtener datatable productos", 500);
		}
	}

	/**
	 * Método para cargar los íconos gráficos una vez seleccionado el producto del dataTable principal, y el total de inventarios de todos los depositos.
	 * @param  [type] $empresa_id  [description]
	 * @param  [type] $producto_id [description]
	 * @return [type]              [description]
	 */
	public function consultarIconos($empresa_id, $deposito_id, $producto_id) {
		try {

			$iconos = DB::table("productos")
				->select(
					"H.producto_id as Hproducto",
					"G.producto_id as Gproducto",
					DB::raw("
                    (case
                        when (0 > productos.max_dias_venta) then 7
                        when (inventarios.disponible >= inventarios.stock_minimo) then 1
                        when (inventarios.disponible < inventarios.stock_minimo and inventarios.disponible > 0) then 3
                        when (inventarios.disponible = 0) then 2
                        when (inventarios.disponible < 0) then 4
                        else -1
                    end) as columna_a"),
					DB::raw("
                    (case
                        when (H.producto_id is not null) then 14
                        when (G.producto_id is not null) then 15
                        else -1
                    end) as columna_b"),
					DB::raw("
                    (case
                        when (marcar_inventario_minimo='S' and inventarios.disponible<=0) then 17
                        when (marcar_inventario_minimo='S' and inventarios.disponible<=inventarios.stock_minimo) then 16
                        when (marcar_inventario_minimo='S') then 18
                        else -1
                    end) as columna_c"),
					DB::raw("
                    (case
                        when (coalesce(v_producto_compatible.cantidad,0) > 0) then 19
                        else -1
                    end) as columna_d"),
					DB::raw("
                    (case
                        when (productos.foto is not null) then 21
                        else -1
                    end) as columna_e"),
					DB::raw('SUM(i2.disponible) itotal')
				)
				->leftjoin('inventarios', function ($leftjoin) use ($deposito_id) {
					$leftjoin->on('productos.empresa_id', '=', 'inventarios.empresa_id')
						->on('inventarios.deposito_id', '=', DB::raw($deposito_id))
						->on('productos.id', '=', 'inventarios.producto_id');
				})
				->leftjoin('inventarios as i2', function ($leftjoin) {
					$leftjoin->on('productos.empresa_id', '=', 'i2.empresa_id')
						->on('productos.id', '=', 'i2.producto_id');
				})
				->leftjoin('v_producto_compatible', function ($leftjoin) {
					$leftjoin->on('productos.empresa_id', '=', 'v_producto_compatible.empresa_id')
						->on('productos.id', '=', 'v_producto_compatible.producto_id');
				})
				->leftjoin('pedido_estatus as G', function ($leftjoin) use ($deposito_id) {
					$leftjoin->on('productos.empresa_id', '=', 'G.empresa_id')
						->on('productos.id', '=', 'G.producto_id')
						->on('G.deposito_id', '=', DB::raw($deposito_id))
						->where('G.estatus', '=', "A");
				})
				->leftjoin('pedido_estatus as H', function ($leftjoin) use ($deposito_id) {
					$leftjoin->on('productos.empresa_id', '=', 'H.empresa_id')
						->on('productos.id', '=', 'H.producto_id')
						->on('H.deposito_id', '=', DB::raw($deposito_id))
						->where('H.estatus', '=', "C");
				})
				->where("productos.trash", 0)
				->where('productos.empresa_id', '=', $empresa_id)
				->where('productos.id', '=', $producto_id)
				->groupBy('Hproducto', 'Gproducto', 'columna_a', 'columna_b', 'columna_c', 'columna_d', 'columna_e')
				->first();

			//Se carga un array con el resultante de los iconos dependiendo del número retornado de la consulta anterior.
			$imagenes = array($this->getIconLabel($iconos->columna_a), $this->getIconLabel($iconos->columna_b), $this->getIconLabel($iconos->columna_c), $this->getIconLabel($iconos->columna_d), $this->getIconLabel($iconos->columna_e), $iconos->itotal);

			return response()->json(["iconos" => $imagenes], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de obtener los iconos del producto: {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de obtener los iconos del producto", 500);
		}
	}

	/**
	 * Método que recibe un valor y retorna un "i" para su reflejo en la vista.
	 * @param  [type] $valor [description]
	 * @return [type]        [description]
	 */
	public function getIconLabel($valor) {

		$columna = "";

		switch ($valor) {
		case 1: //Producto con inventario disponible
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Producto con inventario disponible' style='font-size:24px;color:green'>fiber_manual_record</i>";
			break;
		case 2: //Producto sin inventario disponible
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Producto sin inventario disponible' style='font-size:24px;color:red'>fiber_manual_record</i>";
			break;
		case 3: //Inventario se esta agotando
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Inventario se esta agotando' style='font-size:24px;color:orange'>fiber_manual_record</i>";
			break;
		case 4: //Inventario en negativo
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Inventario en negativo' style='font-size:24px;color:red'>pan_tool</i>";
			break;
		case 5: //Producto inactivo
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Producto inactivo' style='font-size:24px;'>clear</i>";
			break;
		case 6: //Falta información importante
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Falta información importante' style='font-size:24px;color:orange'>bug_report</i>";
			break;
		case 7: //Producto con N dias sin ventas
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Producto con N dias sin ventas' style='font-size:24px;color:orange'>warning</i>";
			break;
		case 8: //Item de producción
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Item de producción' style='font-size:24px;'>build</i>";
			break;
		case 9: //Producto variable
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Producto variable' style='font-size:24px;'>border_color</i>";
			break;
		case 10: //Servicio
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Servicio' style='font-size:24px;'>home</i>";
			break;
		case 11: //kit de productos
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='kit de productos' style='font-size:24px;'>settings_input_composite</i>";
			break;
		case 12: //Producto con números de serie
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Producto con números de serie' style='font-size:24px;'>list</i>";
			break;
		case 13: //Facturación permitida temporalmente
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Facturación permitida temporalmente'  style='font-size:24px;color:green'>swap_vertical_circle</i>";
			break;
		case 14: //Pedido en transito
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Pedido en transito' style='font-size:24px;color:red'>sync</i>";
			break;
		case 15: //Pedido efectuado sin registrar
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Pedido efectuado sin registrar' style='font-size:24px;color:blue'>sync</i>";
			break;
		case 16: //Marcado para pedir
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Marcado para pedir' style='font-size:24px;'>check_box</i>";
			break;
		case 17: //Marcado para pedir URGENTE
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Marcado para pedir URGENTE' style='font-size:24px;color:red'>check_box</i>";
			break;
		case 18: //Marcado para pedir URGENTE
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Marcado para pedir URGENTE' style='font-size:24px;'>check_box_outline_blank</i>";
			break;
		case 19: //El item tiene código compatibles
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='El item tiene código compatibles' style='font-size:24px;'>attach_file</i>";
			break;
		case 20: //foto relacionada al item
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Media Disponible' style='font-size:24px;'>photo_camera</i>";
			break;
		case 21: //video relacionado al item
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Media Disponible' style='font-size:24px;'>videocam</i>";
			break;
		case 22: //foto y video disponible
			$columna = "<i class='material-icons' data-toggle='tooltip' data-placement='right' title='Media Disponible' style='font-size:24px;'>add_a_photo</i>";
			break;
		default:
			$columna = '';
			break;
		}
		return $columna;
	}

	/**
	 * Método que consulta información detallada del producto, compras, ventas, inv en deposito.
	 * @param  [type] $empresa_id  [Empresa_id]
	 * @param  [type] $deposito_id [Deposito_id]
	 * @param  [type] $producto_id [Producto id]
	 * @return [type]              [description]
	 */
	public function getDetalleProducto($empresa_id, $deposito_id, $producto_id) {

		try {
			$detalle = DB::select(DB::raw("select
                    A.id producto_id,
                    A.codigo codigo_producto,
                    A.nombre nombre_producto,
                    B.disponible,
                    sum(IFNULL(C3.subtotal,0)) comprames3,
                    sum(IFNULL(C2.subtotal,0)) comprames2,
                    sum(IFNULL(C1.subtotal,0)) comprames1,
                    sum(IFNULL(C0.subtotal,0)) comprames,
                    sum(IFNULL(V3.subtotal,0)) ventames3,
                    sum(IFNULL(V2.subtotal,0)) ventames2,
                    sum(IFNULL(V1.subtotal,0)) ventames1,
                    sum(IFNULL(V0.subtotal,0)) ventames
                    FROM productos A
                LEFT OUTER JOIN inventarios B ON A.id = B.producto_id and A.empresa_id = B.empresa_id
                LEFT OUTER JOIN v_ventas V0 on V0.producto_id = A.id and V0.empresa_id = A.empresa_id and V0.deposito_id = B.deposito_id and V0.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 30 DAY)),INTERVAL 1 DAY) and last_day(now()) and V0.tipo_documento in ('CON','CRE','COT','PRO')
                LEFT OUTER JOIN v_ventas V1 on V1.producto_id = A.id and V1.empresa_id = A.empresa_id and V1.deposito_id = B.deposito_id and V1.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 60 DAY)),INTERVAL 1 DAY) and last_day(DATE_SUB(now(), INTERVAL 30 DAY)) and V1.tipo_documento in ('CON','CRE','COT','PRO')
                LEFT OUTER JOIN v_ventas V2 on V2.producto_id = A.id and V2.empresa_id = A.empresa_id and V2.deposito_id = B.deposito_id and V2.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 90 DAY)),INTERVAL 1 DAY) and last_day(DATE_SUB(now(), INTERVAL 60 DAY)) and V2.tipo_documento in ('CON','CRE','COT','PRO')
                LEFT OUTER JOIN v_ventas V3 on V3.producto_id = A.id and V3.empresa_id = A.empresa_id and V3.deposito_id = B.deposito_id and V3.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 120 DAY)),INTERVAL 1 DAY) and last_day(DATE_SUB(now(), INTERVAL 90 DAY)) and V3.tipo_documento in ('CON','CRE','COT','PRO')
                LEFT OUTER JOIN v_compras C0 on C0.producto_id = A.id and C0.empresa_id = A.empresa_id and C0.deposito_id = B.deposito_id and C0.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 30 DAY)),INTERVAL 1 DAY) and last_day(now()) and C0.tipo_documento in ('C')
                LEFT OUTER JOIN v_compras C1 on C1.producto_id = A.id and C1.empresa_id = A.empresa_id and C1.deposito_id = B.deposito_id and C1.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 60 DAY)),INTERVAL 1 DAY) and last_day(DATE_SUB(now(), INTERVAL 30 DAY)) and C1.tipo_documento in ('C')
                LEFT OUTER JOIN v_compras C2 on C2.producto_id = A.id and C2.empresa_id = A.empresa_id and C2.deposito_id = B.deposito_id and C2.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 90 DAY)),INTERVAL 1 DAY) and last_day(DATE_SUB(now(), INTERVAL 60 DAY)) and C2.tipo_documento in ('C')
                LEFT OUTER JOIN v_compras C3 on C3.producto_id = A.id and C3.empresa_id = A.empresa_id and C3.deposito_id = B.deposito_id and C3.fecha between DATE_ADD(last_day(DATE_SUB(now(), INTERVAL 120 DAY)),INTERVAL 1 DAY) and last_day(DATE_SUB(now(), INTERVAL 90 DAY)) and C3.tipo_documento in ('C')
                WHERE A.trash = 0 and A.empresa_id = :empresa_id and B.deposito_id = :deposito_id and A.id = :producto_id
                group by 1,2,3,4"), array(
				'empresa_id'  => $empresa_id,
				'deposito_id' => $deposito_id,
				'producto_id' => $producto_id,
			));
			return response()->json(["detalle" => $detalle], 200);

		} catch (\Exception $e) {
			Log::critical("Ha ocurrido un problema al tratar de obtener la informacion detallada del producto: {$e->getCode()} , {$e->getLine()} , {$e->getMessage()}");
			return response("Ha ocurrido un problema al tratar de obtener la informacion detallada del producto", 500);
		}
	}
}