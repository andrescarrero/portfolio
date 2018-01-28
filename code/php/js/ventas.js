//validaciones para ejecutar luego de que cargue la pagina
$(document).ready(function() {

    //Mask para campo aniversario
    $("#venta_cliente_fecha_nacimiento").mask('00/00');

    //Se hace click en la pestaña Ventas
    $('#menu a[href="#Ventas"]').click(function(e) {

        $.when(ventas_obtenerDepositoporUsuario($('#user_id').val())).done(function() {
            e.preventDefault()
            $(this).tab('show')
            //Cargar aquí todos los servicios, desplegables, autocomplete, calendarios, etc.

            //Cargando de información el autocomplete de Clientes
            cargarListadoClientes(true);

            //Cargando información al autoComplete de Vendedores
            cargarAutoCompleteVendedores();

            //Cargando información al autoComplete de Comisionistas
            cargarAutoCompleteComisionistas();

            //Cargando la fecha actual en el campo fecha
            cargarFechaActual();

            //Cargando autoComplete de productos activos
            Ventas_ProductosActivos();

            //Cargar vendedor (usuario actual)
            cargarVendedorActual();

            //Cargar cliente por defecto.
            ventas_cargarClienteFrecuente();
        });
    })

    //Cargando el dataTable de CLientes
    cargarVentas_clientesDataTable();

    //Cargando dataTable de Factura
    cargarVentas_facturaDataTable();

    //Cargando el dataTable de devoluciones
    cargarVentas_devolucionDataTable();

    //Ocultando formulario y tabla de devoluciones
    ventas_OcutarDevoluciones();

    //Cargando dataTable de Números de serie
    cargarVentas_NumeroSerieDataTable();

    //Apertura del modal de clientes, se hace click en el botón
    $('#ventas_clienteModal').on('show.bs.modal', function(event) {
        //Cargar aquí todos los servicios, desplegables, autocomplete, calendarios, etc.
        tiposDeClientes();
        areasClientes();
        cargarListadoClientes(false);
        //Inicializar formulario
        ventas_inicializarFormulario();
    })

    //Se hace click en la pestaña doc1
    $('#ventas_docs a[href="#doc1"]').click(function(e) {
        e.preventDefault()
        $(this).tab('show')
        Documento = "_1";
    })

    //Se hace click en la pestaña doc2
    $('#ventas_docs a[href="#doc2"]').click(function(e) {
        e.preventDefault()
        $(this).tab('show')
        Documento = "_2";
    })

    //Se hace click en la pestaña doc3
    $('#ventas_docs a[href="#doc3"]').click(function(e) {
        e.preventDefault()
        $(this).tab('show')
        Documento = "_3";
    })

    //Se hace click en la pestaña doc4
    $('#ventas_docs a[href="#doc4"]').click(function(e) {
        e.preventDefault()
        $(this).tab('show')
        Documento = "_4";
    })

});

/**
 * Variable que lleva el control de la pestaña DOC en la que se encuentra el usuario, inicialmente es la Doc1 = _1
 * @type {String}
 */
var Documento = "_1";

/**
 * Método para ejecutar articulos como promosionales
 * @param  {[type]} _doc Documento en pantalla (1-2-3-4)
 * @return {[type]}      [description]
 */
function ventas_artPromosional() {

    //Tomo como base el array original.
    ventas_quitarDescuentos(false);

    //Recorro el array de productos seleccionados
    $(rowSelectedFact[Documento]).each(function(indice, valor) {
        //Busco los elementos que coinciden en el listado total de la venta, para modificarlos
        $(ventas_fact[Documento]).each(function(indice2, valor2) {
            if (valor2.id == valor.id && valor2.pos == valor.pos) { //Mismo id del producto, actualizar data.
                valor2.descripcion = valor2.descripcion + " (pr: $" + valor2.total + ")";
                valor2.precio = 0;
                valor2.total = 0;
                valor2.impuesto = 0;
                valor2.subtotal = 0;
                valor2.exento = 0;
            }
        });
    });

    //Actualizo la tabla de ventas
    actualizarVentas_FactDataTable();

    //Limpio (inicializo) el array donde llevo los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Muestro mensaje de éxito
    var artPromo = '{"message": "Se marcaron los artículos seleccionados como artículos precio 0"}';
    successMsj(JSON.parse(artPromo), 'msjOkFact' + Documento, 'formOkFact' + Documento);
}

/**
 * Método para consultar neto
 * @param  {[type]} _doc Documento en pantalla (1-2-3-4)
 * @return {[type]}      [description]
 */
function ventas_neto() {

    //Tomo como base el array original.
    ventas_quitarDescuentos(false);

    //Se buscan todos los campos de impuestos de productos y al ser la venta neta, se hacen 0
    $(ventas_fact[Documento]).each(function(indice, valor) {
        ventas_fact[Documento][indice]["impuesto"] = 0;
    });

    //Se actualiza la tabla
    actualizarVentas_FactDataTable();

    //Limpio (inicializo) el array donde llevo los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Muestro mensaje de éxito
    var neto = '{"message": "Se aplico a los productos de la venta, un valor neto"}';
    successMsj(JSON.parse(neto), 'msjOkFact' + Documento, 'formOkFact' + Documento);
}


/**
 * Método para consultar flecha
 * @return {[type]}      [description]
 */
function ventas_flecha() {
    //Cambiando las opciones por defecto del modal
    $('#ventas_descuentoFlechaModal').modal({
        keyboard: false,
        backdrop: 'static',
    });

    $('#ventas_descuentoFlechaModal').modal('show');
}

/**
 * Método para consultar otros descuentos
 * @param  {[type]} _doc Documento en pantalla (1-2-3-4)
 * @return {[type]}      [description]
 */
function ventas_otrosDescuentos() {
    //Cambiando las opciones por defecto del modal
    $('#ventas_otrosDescuentosModal').modal({
        keyboard: false,
        backdrop: 'static',
    });

    $('#ventas_otrosDescuentosModal').modal('show');
}

/**
 * Método para registrar la venta
 * @return {[type]}      [description]
 */
function ventas_registrar(_tipo) {

    var token = $("#token").val();

    //Se cargan los datos básicos del pedido
    formVenta = consultarFormVenta();

    //Se identifica la ruta
    var route = "/ventas";
    var type = "POST";
    var data = {
        formVenta: formVenta,
    }

    if (_tipo == "NC") {

        var error = true;

        //Validar que al menos, haya realizado la revolución de 1 producto.
        $(ventas_factDev[Documento]).each(function(i, v) { // indice, valor
            if (ventas_factDev[Documento][i]["cantidad"] != 0) {
                error = false;
            }
        })

        if (error == false) {
            //Notas de crédito
            formVenta["detalleVenta"] = ventas_factDev[Documento];

            //Se asigna el valor del exento a un campo
            formVenta["exento"] = $("#venta_table_exentoDev" + Documento).val();

            //Se asigna el valor del subtotal a un campo
            formVenta["subtotal"] = $("#venta_table_subtotalDev" + Documento).val();

            //Se asigna el valor del impuesto a un campo
            formVenta["impuesto"] = $("#venta_table_impuestoDev" + Documento).val();

            //Se asigna el valor del total a un campo
            formVenta["total"] = $("#venta_table_totalDev" + Documento).val();

            //Se lleva la referencia, de a cual id de venta, se hará la devolución
            formVenta["id_ventaNC"] = ventas_obtenerIdVentaNC();
        } else {
            $("#msjOkFactDev" + Documento).hide();
            var errorFact = '{"message": "Antes de guardar una Nota de Crédito debe realizar la devolución de un producto"}';
            errorMsj(JSON.parse(errorFact), 'msjErrorFactDev' + Documento, 'formErrorFactDev' + Documento);
        }

    } else {
        //Todos los documentos que no son notas de crédito.        
        formVenta["detalleVenta"] = ventas_fact[Documento];

        if (ventas_CotProSelected != 0) { //Se trata de una cotización o proforma que se quiere modificar.
            route = "/ventas/" + ventas_CotProSelected;
            type = "PUT";
            data = {
                formVenta: formVenta,
                id: ventas_CotProSelected,
            }
        }
    }

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: type,
        dataType: 'json',
        data: data,
        success: function(obj) {

            //Se limpia el formulario
            ventas_limpiarFormulario(true);

            //Abro la ventana que llama a un servicio, en el que se encuentra la descarga del pdf.
            window.open(obj.pdf);

            //Se oculta el mensaje de error
            $("#msjErrorGral").hide();

            //Se muestra el mensaje de éxito
            successMsj(obj, 'msjOkGral', 'formOkGral');

        },
        error: function(obj) {
            //mostrar mensaje de error
            $("#msjOkGral").hide();
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
}

/**
 * Método para agregar producto al listado
 * @return {[type]}      [description]
 */
function ventas_agregarProducto() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var producto_codigo = autoCompleteFormatValue($("#venta_codigo" + Documento).val());
    var deposito_id = $("#deposito_id").val();
    var precio_cliente = ventas_clienteSelected[Documento][0]["precio"];

    if (producto_codigo == "") {
        producto_codigo = "-1";
    }

    var error = false;

    //Validar si el producto permite cantidades en decimales.
    //De no permitirlas, verificar que "cantidad", no contenga ".", si lo contiene, mostrar un error al usuario
    if ($("#ventas_precioDecimal" + Documento).val() != "S") {
        cadena = $("#venta_cantidad" + Documento).val();
        if (cadena.indexOf('.') != -1) {
            //Muestro msj de error.
            $("#msjOkFact" + Documento).hide();
            var errorFact = '{"message": "Este producto no permite cantidades con Decimales"}';
            errorMsj(JSON.parse(errorFact), 'msjErrorFact' + Documento, 'formErrorFact' + Documento);
            error = true;
        }
    }

    //Valido que cantidad sea numérica.
    var cantidad = $("#venta_cantidad" + Documento).val();
    var precio = $("#venta_precio" + Documento).val();
    if ((isNaN(cantidad) || cantidad == "" || cantidad <= 0) || (isNaN(precio) || precio == "" || precio <= 0)) {
        $("#msjOkFact" + Documento).hide();
        var errorFact = '{"message": "La cantidad y precio indicado debe ser un valor numérico mayor a 0. Como separador decimal debe usarse el punto (.)."}';
        errorMsj(JSON.parse(errorFact), 'msjErrorFact' + Documento, 'formErrorFact' + Documento);
        error = true;
    }

    if (error == false) {

        inicializarPrecioDecimal(Documento);

        //Corresponde a una edición de un registro existente, hago el update en el array.
        if (rowSelectedFact[Documento].length == 1) {

            //Busco el obj seleccionado dentro del array original
            var index = ventas_fact[Documento].indexOf(rowSelectedFact[Documento][0]);

            //Conociendo su indice procedo a realizar la edición
            ventas_fact[Documento][index]["cantidad"] = $("#venta_cantidad" + Documento).val();
            ventas_fact[Documento][index]["precio"] = $("#venta_precio" + Documento).val();

            //llevando en paralelo el array original
            ventas_factOriginal[Documento][index]["cantidad"] = $("#venta_cantidad" + Documento).val();

            $total = 0;
            //Se recalculan los valores de impuesto, total, subtotal etc posterior a la edición.
            if (ventas_fact[Documento][index]["itbms"] == 0) {
                $total = parseFloat($("#venta_cantidad" + Documento).val() * ventas_fact[Documento][index]["precio"]).toFixed(2);
                ventas_fact[Documento][index]["total"] = $total;
                ventas_fact[Documento][index]["subtotal"] = $total;
                ventas_fact[Documento][index]["exento"] = $total;

                //LLevando en paralelo el array original
                ventas_factOriginal[Documento][index]["total"] = $total;
                ventas_factOriginal[Documento][index]["subtotal"] = $total;
                ventas_factOriginal[Documento][index]["exento"] = $total;

            } else {
                ventas_fact[Documento][index]["total"] = parseFloat(($("#venta_cantidad" + Documento).val() * ventas_fact[Documento][index]["precio"]) * (1 + ventas_fact[Documento][index]["itbms"] / 100)).toFixed(2);
                ventas_fact[Documento][index]["subtotal"] = parseFloat($("#venta_cantidad" + Documento).val() * ventas_fact[Documento][index]["precio"]).toFixed(2);
                ventas_fact[Documento][index]["impuesto"] = parseFloat(($("#venta_cantidad" + Documento).val() * ventas_fact[Documento][index]["precio"]) * (ventas_fact[Documento][index]["itbms"] / 100)).toFixed(2);

                //LLevando en paralelo el array original
                ventas_factOriginal[Documento][index]["total"] = parseFloat(($("#venta_cantidad" + Documento).val() * ventas_factOriginal[Documento][index]["precio"]) * (1 + ventas_factOriginal[Documento][index]["itbms"] / 100)).toFixed(2);
                ventas_factOriginal[Documento][index]["subtotal"] = parseFloat($("#venta_cantidad" + Documento).val() * ventas_factOriginal[Documento][index]["precio"]).toFixed(2);
                ventas_factOriginal[Documento][index]["impuesto"] = parseFloat(($("#venta_cantidad" + Documento).val() * ventas_factOriginal[Documento][index]["precio"]) * (ventas_factOriginal[Documento][index]["itbms"] / 100)).toFixed(2);
            }

            //Habilito la edición del código del producto
            $("#venta_codigo" + Documento).prop('disabled', false);

            //Se deshabilita el botón de quitar
            $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

            //Limpio el formulario
            limpiarFormularioFactProducto();

            //Limpio (inicializo) el array donde llevo los productos seleccionados
            rowSelectedFact[Documento] = new Array();

            //Actualizo el dataTable
            actualizarVentas_FactDataTable();

            //limpiar el inventario
            limpiarInventario();

            //Se limpia a lista de precios
            limpiarListaPrecios();

            //Se limpiar el listado de los 2 últimos precios de venta
            limpiarUltimos2Precios();

            //mostrar mensaje de Producto agregado al listado.
            $("#msjErrorFact" + Documento).hide();
            var editFactProducto = '{"message": "Producto actualizado correctamente"}';
            successMsj(JSON.parse(editFactProducto), 'msjOkFact' + Documento, 'formOkFact' + Documento);

            //Dejo el focus en el código del producto
            $("#venta_codigo" + Documento).focus();
        } else {
            //Se valida que el código del producto ingresado sea de un producto existente.
            //Se identifica la ruta
            var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/producto_codigo/" + producto_codigo + "/precio/" + precio_cliente + "/Ventas_productoDatosVenta";
            $.ajax({
                url: route,
                headers: { 'X-CSRF-TOKEN': token },
                type: 'GET',
                dataType: 'json',
                data: {
                    empresa_id: empresa_id,
                    deposito_id: deposito_id,
                    producto_codigo: producto_codigo,
                    precio_cliente: precio_cliente
                },
                success: function(obj) {

                    //Se determina si la consulta trajo información
                    if (obj.producto.length > 0) {

                        $(obj.producto).each(function(i, v) { // indice, valor

                            $precio = 0;

                            if (obj.producto[i].tipo != "K") {
                                $precio = $("#venta_precio" + Documento).val();
                            } else {
                                $precio = obj.producto[i].precio;
                            }

                            //Si el producto es de tipo "serie" (posee números de serie para la venta)
                            if (obj.producto[i].tipo == "serie") {

                                //Inicializo los números de serie
                                ventas_NumeroSerie = new Array();

                                $(obj.producto[i].serie).each(function(i, v) { // indice, valor
                                    //Cargo los números de seria para el llenado de la tabla
                                    ventas_NumeroSerie.push({
                                        nro: "",
                                        nro_serie: v.numero_serie,
                                        fact_compra: v.factura_compra,
                                        producto_id: v.producto_id,
                                        nro_serie_id: v.id,
                                    });

                                });

                                //Actualizo la tabla de números de serie para mostrar la información correcta.
                                actualizarVentas_NumeroSerieDataTable();

                                //Cambiando las opciones por defecto del modal
                                $('#ventas_numeroSerie').modal({
                                    keyboard: false,
                                    backdrop: 'static',
                                });

                                //Abro el modal de número de serie.
                                $("#ventas_numeroSerie").modal('show');

                                //Deshabilito el botón
                                $('#venta_numeroSerieSelected').attr("disabled", true);
                            }

                            $total = 0;
                            $subtotal = 0;
                            $itbms = 0;
                            $exento = 0;

                            //Se calculan los totales, subTotales y exentos
                            if (obj.producto[i].impuesto == null || obj.producto[i].impuesto == 0) {
                                $total = parseFloat(obj.producto[i].cantidad * $("#venta_cantidad" + Documento).val() * $precio).toFixed(2);
                                $subtotal = $total;
                                $exento = $total;
                                $itbms = 0;
                            } else {
                                $total = parseFloat((obj.producto[i].cantidad * $("#venta_cantidad" + Documento).val() * $precio) * (1 + obj.producto[i].impuesto / 100)).toFixed(2);
                                $subtotal = parseFloat(obj.producto[i].cantidad * $("#venta_cantidad" + Documento).val() * $precio).toFixed(2);
                                $itbms = obj.producto[i].impuesto;
                            }

                            //Agregando el elemento a la factura
                            ventas_fact[Documento].push({
                                nro: "",
                                id: obj.producto[i].id,
                                codigo: obj.producto[i].codigo,
                                descripcion: obj.producto[i].descripcion,
                                img: "vacio",
                                cantidad: (obj.producto[i].cantidad * $("#venta_cantidad" + Documento).val()),
                                precio: $precio,
                                itbms: $itbms,
                                impuesto: parseFloat((obj.producto[i].cantidad * $("#venta_cantidad" + Documento).val() * $precio) * (obj.producto[i].impuesto / 100)).toFixed(2),
                                subtotal: $subtotal,
                                total: $total,
                                id_serie: "",
                                exento: $exento,
                                porc_descuento: 0,
                                monto_descuento: 0,
                                pos: ventas_fact[Documento].length + 1,
                            });

                            //Agregando el elemento a la factura (llevando el control en el array original)
                            ventas_factOriginal[Documento].push({
                                nro: "",
                                id: obj.producto[i].id,
                                codigo: obj.producto[i].codigo,
                                descripcion: obj.producto[i].descripcion,
                                img: "vacio",
                                cantidad: (obj.producto[i].cantidad * $("#venta_cantidad" + Documento).val()),
                                precio: $precio,
                                itbms: $itbms,
                                impuesto: parseFloat((obj.producto[i].cantidad * $("#venta_cantidad" + Documento).val() * $precio) * (obj.producto[i].impuesto / 100)).toFixed(2),
                                subtotal: $subtotal,
                                total: $total,
                                id_serie: "",
                                exento: $exento,
                                porc_descuento: 0,
                                monto_descuento: 0,
                                pos: ventas_factOriginal[Documento].length + 1,
                            });

                            //Método para validar y aplicar en caso de que la venta sea exenta
                            ventas_VentaExenta(true);

                        })

                        //Limpiando el formulariocde agregar productos
                        limpiarFormularioFactProducto();

                        //Se actualiza el listado del dataTable de la factura
                        actualizarVentas_FactDataTable();

                        //limpiar el inventario
                        limpiarInventario();

                        //Se limpia a lista de precios
                        limpiarListaPrecios();

                        //Se limpiar el listado de los 2 últimos precios de venta
                        limpiarUltimos2Precios();

                        //mostrar mensaje de Producto agregado al listado.
                        $("#msjErrorFact" + Documento).hide();
                        var agregadoFactProducto = '{"message": "Producto agregado al listado correctamente"}';
                        successMsj(JSON.parse(agregadoFactProducto), 'msjOkFact' + Documento, 'formOkFact' + Documento);

                        //Dejo el focus en el código del producto
                        $("#venta_codigo" + Documento).focus();
                    } else {
                        //Mostrar error de indicar producto válido
                        $("#msjOkFact" + Documento).hide();
                        var errorFactProducto = '{"message": "Debe indicar un producto existente para agregar al listado"}';
                        errorMsj(JSON.parse(errorFactProducto), 'msjErrorFact' + Documento, 'formErrorFact' + Documento);
                    }

                },
                error: function(obj) {
                    //mostrar mensaje de error
                    errorMsj(obj, 'msjErrorGral', 'formErrorGral');
                }
            });
        }
    }

    //Se habilita nuevamente el campo de cantidad
    $("#venta_cantidad" + Documento).prop("disabled", false);
}

/**
 * Método para eliminar producto del listado en DOC 1
 * @return {[type]}      [description]
 */
$("#ventas_eliminar_producto_1").click(function(event) {

    //Habilito nuevamente la edición del código
    $("#venta_codigo" + Documento).prop('disabled', false);

    //Limpio el formulario
    limpiarFormularioFactProducto();

    Ventas_factTable_1.row('.selected').remove().draw(false);

    //Se deshabilita el botón de quitar
    $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

    var index = ventas_fact[Documento].indexOf(rowSelectedFact[Documento][0]); //Busco si mi obj auxiliar ya tiene la data seleccionada

    //Se remueve ese elemento del array
    ventas_fact[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Para llevar el control del array original
    ventas_factOriginal[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Inicializo el array que lleva el control de los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Se muestra el msj de registro removido
    $("#msjErrorFact" + Documento).hide();
    var removidoDeLaFact = '{"message": "Producto removido de la factura"}';
    successMsj(JSON.parse(removidoDeLaFact), 'msjOkFact' + Documento, 'formOkFact' + Documento);
});

/**
 * Método para eliminar producto del listado en DOC 2
 * @return {[type]}      [description]
 */
$("#ventas_eliminar_producto_2").click(function(event) {

    //Habilito nuevamente la edición del código
    $("#venta_codigo" + Documento).prop('disabled', false);

    //Limpio el formulario
    limpiarFormularioFactProducto();

    Ventas_factTable_2.row('.selected').remove().draw(false);

    //Se deshabilita el botón de quitar
    $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

    var index = ventas_fact[Documento].indexOf(rowSelectedFact[Documento][0]); //Busco si mi obj auxiliar ya tiene la data seleccionada

    //Se remueve ese elemento del array
    ventas_fact[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Para llevar el control del array original
    ventas_factOriginal[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Inicializo el array que lleva el control de los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Se muestra el msj de registro removido
    $("#msjErrorFact" + Documento).hide();
    var removidoDeLaFact = '{"message": "Producto removido de la factura"}';
    successMsj(JSON.parse(removidoDeLaFact), 'msjOkFact' + Documento, 'formOkFact' + Documento);
});

/**
 * Método para eliminar producto del listado en DOC 3
 * @return {[type]}      [description]
 */
$("#ventas_eliminar_producto_3").click(function(event) {

    //Habilito nuevamente la edición del código
    $("#venta_codigo" + Documento).prop('disabled', false);

    //Limpio el formulario
    limpiarFormularioFactProducto();

    Ventas_factTable_3.row('.selected').remove().draw(false);

    //Se deshabilita el botón de quitar
    $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

    var index = ventas_fact[Documento].indexOf(rowSelectedFact[Documento][0]); //Busco si mi obj auxiliar ya tiene la data seleccionada

    //Se remueve ese elemento del array
    ventas_fact[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Para llevar el control del array original
    ventas_factOriginal[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Inicializo el array que lleva el control de los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Se muestra el msj de registro removido
    $("#msjErrorFact" + Documento).hide();
    var removidoDeLaFact = '{"message": "Producto removido de la factura"}';
    successMsj(JSON.parse(removidoDeLaFact), 'msjOkFact' + Documento, 'formOkFact' + Documento);
});

/**
 * Método para eliminar producto del listado en DOC 4
 * @return {[type]}      [description]
 */
$("#ventas_eliminar_producto_4").click(function(event) {

    //Habilito nuevamente la edición del código
    $("#venta_codigo" + Documento).prop('disabled', false);

    //Limpio el formulario
    limpiarFormularioFactProducto();

    Ventas_factTable_4.row('.selected').remove().draw(false);

    //Se deshabilita el botón de quitar
    $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

    var index = ventas_fact[Documento].indexOf(rowSelectedFact[Documento][0]); //Busco si mi obj auxiliar ya tiene la data seleccionada

    //Se remueve ese elemento del array
    ventas_fact[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Para llevar el control del array original
    ventas_factOriginal[Documento].splice(index, 1); // 1 es la cantidad de elemento a eliminar

    //Inicializo el array que lleva el control de los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Se muestra el msj de registro removido
    $("#msjErrorFact" + Documento).hide();
    var removidoDeLaFact = '{"message": "Producto removido de la factura"}';
    successMsj(JSON.parse(removidoDeLaFact), 'msjOkFact' + Documento, 'formOkFact' + Documento);
});


/**
 * Método para consultar los tipos de clinetes existentes
 * @return {[type]} [description]
 */
var tiposDeClientes = function() {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();

    //Se identifica la ruta
    var route = "/tiposCliente";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {},
        success: function(obj) {
            //Agregando la información encontrada.
            //Primero se limpiar el desplegable para no acumular información (concatenarla).
            $('#venta_cliente_tipo').find('option').remove();

            $(obj.tipo_clientes).each(function(i, v) { // indice, valor
                $('#venta_cliente_tipo').append('<option value="' + v.id + '">' + v.descripcion + '</option>');
            })

            //Haciendo la validación de los días de crédito dependiendo de la opción por defecto.
            ventas_cambioTipoCliente($('#venta_cliente_tipo').val());

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};

/**
 * Método para consultar lás áreas a las que puede pertenecer un cliente
 * @return {[type]} [description]
 */
var areasClientes = function() {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/areaCliente";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
        },
        success: function(obj) {
            //Agregando la información encontrada.
            //Primero se limpiar el desplegable para no acumular información (concatenarla).
            $('#venta_cliente_area').find('option').remove();
            //Agrego un valor vacío
            $('#venta_cliente_area').append('<option value=""></option>');

            $(obj.area_clientes).each(function(i, v) { // indice, valor
                $('#venta_cliente_area').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};

/**
 * Método para procesar el formulario de registrar/Editar Cliente
 * @return {[type]} [description]
 */
$("#registrarCliente").click(function() {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    var formCliente = consultarFormCliente();

    formCliente["empresa_id"] = empresa_id;

    //Se identifica la ruta
    var route = "/cliente";
    var type = "POST";
    var data = {
        formCliente: formCliente,
    }

    //Se valida si estamos en el caso de un update (que haya un cliente seleccionado) al guardar.
    if (ventas_clienteSelected[Documento].length > 0) {
        var id = ventas_clienteSelected[Documento][0]["id"];
        formCliente["id"] = id;
        type = "PUT";
        route = "/cliente/" + id;
        data = {
            formCliente: formCliente,
            id: id,
        }
    }

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: type,
        dataType: 'json',
        data: data,
        success: function(obj) {
            //limpiar formulario
            limpiarFormCliente();

            //Asigna el cliente recién creado al formulario principal
            $("#venta_cliente" + Documento).val("ID: " + obj.cliente.id + " Ced: " + obj.cliente.cedula + " Nombre: " + obj.cliente.primer_nombre);
            $("#venta_telefono" + Documento).val(obj.cliente.movil);
            $("#venta_puntos" + Documento).val(obj.cliente.puntos);
            $("#venta_direccion" + Documento).val(obj.cliente.direccion);

            //Agrego datos a la variable del nuevo cliente seleccionado, precio null ya que por formulario no es posible escoger el tipo de precio
            ventas_clienteSelected[Documento] = new Array();
            ventas_clienteSelected[Documento].push({ id: obj.cliente.id, precio: null });

            //Quito la posible seleccion del cliente
            Ventas_clientesTable.rows().deselect();

            //Oculto el posible mensaje de error del formulario
            $("#msjErrorVenta_Cliente").hide();

            //Cierro la ventana emergente de cliente
            $("#ventas_clienteModal .close").click();
            successMsj(obj, 'msjOkGral', 'formOkGral');
            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorVenta_Cliente', 'formErrorVenta_Cliente');
            dfd.reject();
        }
    });

    return dfd.promise();
});

/**
 * Método para consultar los datos ingresados en el formulario que pertenecen a un cliente.
 * @return {[obj]} [todos los datos del cliente]
 */
function consultarFormCliente() {

    var cliente = {
        'tipo_cliente_id': $("#venta_cliente_tipo").val(),
        'primer_nombre': $("#venta_cliente_nombre").val(),
        'nombre_alternativo': $("#venta_cliente_nombre_alternativo").val(),
        'cedula': $("#venta_cliente_cedula").val(),
        'dv': $("#venta_cliente_dv").val(),
        'direccion': $("#venta_cliente_direccion").val(),
        'movil': $("#venta_cliente_movil").val(),
        'telefono_1': $("#venta_cliente_telefono_1").val(),
        'telefono_2': $("#venta_cliente_telefono_2").val(),
        'telefono_3': $("#venta_cliente_telefono_3").val(),
        'email': $("#venta_cliente_email").val(),
        'area_cliente_id': $("#venta_cliente_area").val(),
        'dias_credito': $("#venta_cliente_credito").val(),
        'vendedor_id': autoCompleteVendedor_ClienteFormatValue($("#venta_cliente_vendedor").val()),
        'exento': $("#venta_cliente_exento").is(':checked'),
        'fecha_nacimiento': $("#venta_cliente_fecha_nacimiento").val(),
        'estatus': "Activo",
        'id': 0,
    }

    return cliente;
}

/**
 * Método para limpiar los datos del formulario del cliente.
 */
function limpiarFormCliente() {

    $("#venta_cliente_tipo").val("1");
    //Haciendo la validación de los días de crédito dependiendo de la opción por defecto.
    ventas_cambioTipoCliente($('#venta_cliente_tipo').val());
    $("#venta_cliente_nombre").val("");
    $("#venta_cliente_nombre_alternativo").val("");
    $("#venta_cliente_cedula").val("");
    $("#venta_cliente_dv").val("");
    $("#venta_cliente_direccion").val("");
    $("#venta_cliente_movil").val("");
    $("#venta_cliente_telefono_1").val("");
    $("#venta_cliente_telefono_2").val("");
    $("#venta_cliente_telefono_3").val("");
    $("#venta_cliente_email").val("");
    $("#venta_cliente_area").val("");
    $("#venta_cliente_credito").val("");
    $("#venta_cliente_vendedor").val("");
    $("#venta_cliente_exento").attr('checked', false);
    $("#venta_cliente_fecha_nacimiento").val("");
    $("#venta_cliente_codigo").val("");
}

/**
 * Variable creada para llevar la estructura de la tabla de clientes
 */
var ventas_clientes = new Array(); //cedula, nombre, tipo, credito, codigo_precio
var Ventas_clientesTable; //Variable del dataTable

/**
 * Método donde se define el dataTable de los clientes en ventas
 * @return {[type]} [description]
 */
var cargarVentas_clientesDataTable = function() {
    //Inicialización de tabla de clientes en ventas.
    Ventas_clientesTable = $('#Ventas_clientesTable').DataTable({
        destroy: true,
        data: ventas_clientes,
        columns: [
            { "data": "id" },
            { "data": "cedula" },
            { "data": "nombre" },
            { "data": "tipo" },
            { "data": "credito" },
            { "data": "codigo_precio" }
        ],
        columnDefs: [
            { "visible": false, "searchable": false, "targets": [0], },
            { "visible": false, "searchable": false, "targets": [5], },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "scrollY": "130px",
        "scrollCollapse": true,
        "paging": false,
        "info": false,
    });

    //Seleccionable de la tabla creada
    $('#Ventas_clientesTable tbody').on('click', 'tr', function() {

        var table = $('#Ventas_clientesTable').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) { //Método que deselecciona un elemento de la tabla
            $(this).removeClass('selected');

            //Limpio los datos del cliente seleccionado
            ventas_clienteSelected[Documento] = new Array();

            //Limpiamos campos del formulario
            limpiarFormCliente();
        } else {
            //Método cuando se selecciona un elemento de la tabla.
            Ventas_clientesTable.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = ventas_clientes.indexOf(data);

            //Agrego datos a la variable del nuevo cliente seleccionado
            ventas_clienteSelected[Documento] = new Array();
            ventas_clienteSelected[Documento].push({ id: ventas_clientes[indice]["id"], precio: ventas_clientes[indice]["codigo_precio"] });

            //Se limpia el formulario de venta
            ventas_limpiarFormulario(false);

            //Limpiamos el formulario
            limpiarFormCliente();

            //Cargamos los datos del cliente al formulario
            cargarClientePorId(ventas_clientes[indice]["id"]);

            //Asigna el cliente seleccionado al formulario principal
            $("#venta_cliente" + Documento).val("ID: " + ventas_clientes[indice]["id"] + " Ced: " + ventas_clientes[indice]["cedula"] + " Nombre: " + ventas_clientes[indice]["nombre"]);
            consultarDatosVenta_cliente(ventas_clientes[indice].id);

            $(this).addClass('selected');
        }
    });
}

/**
 * Método para consultar el listado de clientes
 * @param  {[type]} _autoComplete boolean que indica si se carga el autoComplete o en caso contrario la tabla de clientes
 * @return {[type]}               [description]
 */
var cargarListadoClientes = function(_autoComplete) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/consultarClientes";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
        },
        success: function(obj) {

            //Inicializo el array de clientes
            ventas_clientes = new Array();

            //Si trae valores, se agregan al array correspondiente
            if (obj.clientes.length > 0) {
                //Si autocomplete es false, carga la tabla de clientes
                if (_autoComplete == false) {
                    $(obj.clientes).each(function(i, v) { // indice, valor
                        ventas_clientes.push({
                            id: v.id,
                            cedula: v.cedula,
                            nombre: v.primer_nombre,
                            tipo: v.descripcion,
                            credito: v.dias_credito,
                            codigo_precio: v.codigo_precio
                        });
                    })

                    //recargo la tabla
                    actualizarVentas_clientesDataTable();
                } else {
                    //Si autocomplete es true, carga data al autoComplete
                    $(obj.clientes).each(function(i, v) { // indice, valor
                        Venta_clientes_options.data[i] = {
                            id: v.id,
                            cedula: v.cedula,
                            nombre: v.primer_nombre,
                        };
                    })

                    //Aplicando el auto complete al campo donde se registra el nombre del cliente
                    $("#venta_cliente_1").easyAutocomplete(Venta_clientes_options);
                    $("#venta_cliente_2").easyAutocomplete(Venta_clientes_options);
                    $("#venta_cliente_3").easyAutocomplete(Venta_clientes_options);
                    $("#venta_cliente_4").easyAutocomplete(Venta_clientes_options);
                }
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};

/**
 * Método para actualizar el DataTable de Clientes
 * @return {[type]} [description]
 */
var actualizarVentas_clientesDataTable = function() {
    $('#Ventas_clientesTable').dataTable().fnClearTable();
    if (ventas_clientes.length > 0) {
        $('#Ventas_clientesTable').dataTable().fnAddData(ventas_clientes);
    }
}

/**
 * Método para consultar los datos del cliente cuyo id se envía por parámetro
 * @param  {[type]} _cliente_id [id del cliente]
 * @return {[type]}             [description]
 */
var cargarClientePorId = function(_cliente_id) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/consultarCliente/" + _cliente_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            cliente_id: _cliente_id,
        },
        success: function(obj) {

            //Cargar data del cliente consultado
            cargarFormCliente(obj.cliente);

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};

/**
 * Método para cargar en el formulario de clientes los datos del cliente seleccionado
 * @param  {[type]} _datosCliente [cliente de BD]
 * @return {[type]}               [description]
 */
var cargarFormCliente = function(_datosCliente) {
    $("#venta_cliente_tipo").val(_datosCliente["tipo_cliente_id"]);
    ventas_cambioTipoCliente($('#venta_cliente_tipo').val());
    $("#venta_cliente_codigo").val(_datosCliente["id"]);
    $("#venta_cliente_nombre").val(_datosCliente["primer_nombre"]);
    $("#venta_cliente_nombre_alternativo").val(_datosCliente["nombre_alternativo"]);
    $("#venta_cliente_cedula").val(_datosCliente["cedula"]);
    $("#venta_cliente_dv").val(_datosCliente["dv"]);
    $("#venta_cliente_direccion").val(_datosCliente["direccion"]);
    $("#venta_cliente_movil").val(_datosCliente["movil"]);
    $("#venta_cliente_telefono_1").val(_datosCliente["telefono_1"]);
    $("#venta_cliente_telefono_2").val(_datosCliente["telefono_2"]);
    $("#venta_cliente_telefono_3").val(_datosCliente["telefono_3"]);
    $("#venta_cliente_email").val(_datosCliente["email"]);
    $("#venta_cliente_area").val(_datosCliente["area_cliente_id"]);
    $("#venta_cliente_credito").val(_datosCliente["dias_credito"]);
    $("#venta_cliente_exento").prop('checked', _datosCliente["exento"] == "S" ? true : false);
    $("#venta_cliente_fecha_nacimiento").val(_datosCliente["fecha_nacimiento"]);
}

/**
 * Método para limpiar los campos del formulario de cliente
 * @param  {[type]} e [description]
 * @return {[type]}   [description]
 */
$("#limpiar_Ventas_Cliente").click(function(e) {
    //Se limpiar el formulario del cliente
    limpiarFormCliente();

    //Quito la posible seleccion del cliente
    Ventas_clientesTable.rows().deselect();
});

//Se carga la configuración del autoComplete de clientes
var Venta_clientes_options = {
    data: [],
    placeholder: "Ced o Nombre del cliente",

    getValue: function(element) {
        return "ID: " + $(element).prop("id") + " Ced: " + $(element).prop("cedula") + " Nombre: " + $(element).prop("nombre");
    },

    list: {
        maxNumberOfElements: 8,
        showAnimation: {
            type: "fade", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        hideAnimation: {
            type: "slide", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        match: {
            enabled: true
        }
    },
    theme: "dark", //dark, blue, purple, yellow, blue-light, green-light, bootstrap
    adjustWidth: false,
};


/**
 * Método para cortar el valor del campo del autocomplete y solamente tomar el código del cliente para ser procesado
 * @param  {[type]} _value [ID: qwerty Ced: qwerty Nombre: qwerty]
 * @return {[type]}        [description]
 */
function autoCompleteClienteFormatValue(_value) {

    //Se limpia el formulario al tratar de cambiar de cliente
    ventas_limpiarFormulario(false);

    if (_value != "") {
        var auxValue = _value.split(" Ced:");
        if (auxValue.length > 1) {
            var id = auxValue[0].split("ID: ");
            return id[1];
        }
    }
    //En caso de no conseguir el patrón para extraer el ID, no se trata de una cadena válida y se limpian todos los campos
    $("#venta_cliente" + Documento).val("");
    $("#venta_telefono" + Documento).val("");
    $("#venta_puntos" + Documento).val("");
    $("#venta_direccion" + Documento).val("");

    //Inicializo el cliente seleccionado
    ventas_clienteSelected[Documento] = new Array();

    //Si no hay un cliente seleccionado no se pueden agregar detalle a la factura
    $("#ventas_fieldset" + Documento).prop('disabled', true);


    return "";
}

/**
 * Método para al seleccionar un clinete del autoComplete de Clientes, haga el seteo de las variables del formulario
 * @param  {[type]} _cliente_id ID DEL CLIENTE
 * @return {[type]}          [description]
 */
var consultarDatosVenta_cliente = function(_cliente_id) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    if (_cliente_id != "") {

        //Se identifica la ruta
        var route = "/empresa/" + empresa_id + "/consultarCliente/" + _cliente_id;

        $.ajax({
            url: route,
            headers: { 'X-CSRF-TOKEN': token },
            type: 'GET',
            dataType: 'json',
            data: {
                empresa_id: empresa_id,
                cliente_id: _cliente_id,
            },
            success: function(obj) {

                //Se inicializa el formulario en caso de no encontrar un cliente en BD.
                ventas_inicializarFormulario();

                //Seteando variables según lo encontrado
                if (obj.cliente != null) {
                    //Asigna el cliente seleccionado al formulario principal
                    $("#venta_telefono" + Documento).val(obj.cliente.movil);
                    $("#venta_puntos" + Documento).val(obj.cliente.puntos);
                    $("#venta_direccion" + Documento).val(obj.cliente.direccion);

                    //Agrego datos a la variable del nuevo cliente seleccionado
                    ventas_clienteSelected[Documento] = new Array();
                    ventas_clienteSelected[Documento].push({ id: obj.cliente.id, precio: obj.cliente.codigo_precio });

                    $("#venta_exento" + Documento).prop('checked', obj.cliente.exento == "S" ? true : false);

                    //Inicializo los valores exentos
                    ventas_exento[Documento] = new Array();

                    //seteando cliente exento o no.
                    if (obj.cliente.exento == "N") {
                        ventas_exento[Documento].push({
                            exento: false,
                        });
                    } else {
                        ventas_exento[Documento].push({
                            exento: true,
                        });
                    }

                    if (obj.cliente.tipo_cliente_id == 1) {
                        $('input[type=radio][name=venta_tipo_documento' + Documento + '][value="CRE"]').attr('disabled', 'disabled');
                    }
                    //Habilito para ingresar datos a la factura
                    $("#ventas_fieldset" + Documento).prop('disabled', false);
                }
            },
            error: function(obj) {
                //mostrar mensaje de error
                errorMsj(obj, 'msjErrorGral', 'formErrorGral');
            }
        });
    }
}

//Se carga la configuración del autoComplete de vendedor
var Venta_vendedor_options = {
    data: [],
    placeholder: "Id o Nombre del vendedor",

    getValue: function(element) {
        return "ID: " + $(element).prop("id") + " Nombre: " + $(element).prop("nombre");
    },

    list: {
        maxNumberOfElements: 8,
        showAnimation: {
            type: "fade", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        hideAnimation: {
            type: "slide", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        match: {
            enabled: true
        }
    },
    theme: "dark", //dark, blue, purple, yellow, blue-light, green-light, bootstrap
    adjustWidth: false,
};

/**
 * Método para consultar el listado de vendedores
 * @return {[type]}               [description]
 */
var cargarAutoCompleteVendedores = function() {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/ " + deposito_id + "/consultarVendedores";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
        },
        success: function(obj) {

            $(obj.vendedores).each(function(i, v) { // indice, valor
                Venta_vendedor_options.data[i] = {
                    id: v.id,
                    nombre: v.name,
                };
            })

            //Aplicando el auto complete al campo donde se registra el nombre del vendedor
            $("#venta_vendedor_1").easyAutocomplete(Venta_vendedor_options);
            $("#venta_vendedor_2").easyAutocomplete(Venta_vendedor_options);
            $("#venta_vendedor_3").easyAutocomplete(Venta_vendedor_options);
            $("#venta_vendedor_4").easyAutocomplete(Venta_vendedor_options);
            $("#venta_cliente_vendedor").easyAutocomplete(Venta_vendedor_options);

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};

/**
 * Método para cortar el valor del campo del autocomplete y solamente tomar el código del vendedor para ser procesado
 * @param  {[type]} _value [ID: qwerty Nombre: qwerty]
 * @return {[type]}        [description]
 */
function autoCompleteVendedorFormatValue(_value) {

    if (_value != "") {
        var auxValue = _value.split("Nombre: ");
        if (auxValue.length > 1) {
            var auxID = auxValue[0].split("ID: ");
            $("#ventas_vendedorSelected" + Documento).val(auxID[1].slice(0, auxID[1].length - 1));
        } else {
            $("#ventas_vendedorSelected" + Documento).val("");
            $("#venta_vendedor" + Documento).val("");
        }
    }
}

//Se carga la configuración del autoComplete de comisionista
var Venta_comisionista_options = {
    data: [],
    placeholder: "Id o Nombre del comisionista",

    getValue: function(element) {
        return "ID: " + $(element).prop("id") + " Nombre: " + $(element).prop("nombre");
    },

    list: {
        maxNumberOfElements: 8,
        showAnimation: {
            type: "fade", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        hideAnimation: {
            type: "slide", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        match: {
            enabled: true
        }
    },
    theme: "dark", //dark, blue, purple, yellow, blue-light, green-light, bootstrap
    adjustWidth: false,
};

/**
 * Método para consultar el listado de comisionistas
 * @return {[type]}               [description]
 */
var cargarAutoCompleteComisionistas = function() {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/ " + deposito_id + "/consultarComisionistas";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
        },
        success: function(obj) {

            $(obj.comisionistas).each(function(i, v) { // indice, valor
                Venta_comisionista_options.data[i] = {
                    id: v.id,
                    nombre: v.name,
                };
            })

            //Aplicando el auto complete al campo donde se registra el nombre del comisionista
            $("#venta_comisionista_1").easyAutocomplete(Venta_comisionista_options);
            $("#venta_comisionista_2").easyAutocomplete(Venta_comisionista_options);
            $("#venta_comisionista_3").easyAutocomplete(Venta_comisionista_options);
            $("#venta_comisionista_4").easyAutocomplete(Venta_comisionista_options);

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};

/**
 * Método para cortar el valor del campo del autocomplete y solamente tomar el código del comisionista para ser procesado
 * @param  {[type]} _value [ID: qwerty Nombre: qwerty]
 * @return {[type]}        [description]
 */
function autoCompleteComisionistaFormatValue(_value) {
    if (_value != "") {
        var auxValue = _value.split("Nombre: ");
        if (auxValue.length > 1) {
            var auxID = auxValue[0].split("ID: ");
            $("#ventas_comisionistaSelected" + Documento).val(auxID[1].slice(0, auxID[1].length - 1));
        } else {
            $("#ventas_comisionistaSelected" + Documento).val("");
            $("#venta_comisionista" + Documento).val("");
        }
    }
}

/**
 * Método para cargar la fecha actual en los campos destinados para dicho fin en Ventas
 * @return {[type]} [description]
 */
function cargarFechaActual() {
    $("#venta_fecha_1").datepicker({ maxDate: '0', dateFormat: 'dd/mm/yy', disabled: true }).datepicker("setDate", new Date());
    $("#venta_fecha_2").datepicker({ maxDate: '0', dateFormat: 'dd/mm/yy', disabled: true }).datepicker("setDate", new Date());
    $("#venta_fecha_3").datepicker({ maxDate: '0', dateFormat: 'dd/mm/yy', disabled: true }).datepicker("setDate", new Date());
    $("#venta_fecha_4").datepicker({ maxDate: '0', dateFormat: 'dd/mm/yy', disabled: true }).datepicker("setDate", new Date());
}

/**
 * Método para cargar el dataTable de facturas
 * @return {[type]} [description]
 */
function cargarVentas_facturaDataTable() {
    cargarVentas_factDataTable_1();
    cargarVentas_factDataTable_2();
    cargarVentas_factDataTable_3();
    cargarVentas_factDataTable_4();
}

/**
 * Variable creada para llevar la estructura de la tabla de ventas _1_2_3_4
 */
var ventas_fact = {};
ventas_fact["_1"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total
ventas_fact["_2"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total
ventas_fact["_3"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total
ventas_fact["_4"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total

/**
 * Variable creada para llevar la estructura de la tabla de ventas _1_2_3_4
 */
var ventas_factOriginal = {};
ventas_factOriginal["_1"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total
ventas_factOriginal["_2"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total
ventas_factOriginal["_3"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total
ventas_factOriginal["_4"] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total


var rowSelectedFact = {};
rowSelectedFact["_1"] = new Array();
rowSelectedFact["_2"] = new Array();
rowSelectedFact["_3"] = new Array();
rowSelectedFact["_4"] = new Array();

var Ventas_factTable_1; //Variable del dataTable
var Ventas_factTable_2; //Variable del dataTable
var Ventas_factTable_3; //Variable del dataTable
var Ventas_factTable_4; //Variable del dataTable

/**
 * Método para guardar la escrutura del vendedor seleccionado, id|codigo_precio
 * @type {Object}
 */
var ventas_clienteSelected = {};
ventas_clienteSelected["_1"] = new Array(); //id, codigo_precio
ventas_clienteSelected["_2"] = new Array(); //id, codigo_precio
ventas_clienteSelected["_3"] = new Array(); //id, codigo_precio
ventas_clienteSelected["_4"] = new Array(); //id, codigo_precio

/**
 * Método donde se define el dataTable de las facturas en DOC 1
 * @return {[type]} [description]
 */
var cargarVentas_factDataTable_1 = function() {
    //Inicialización de tabla de facturas en ventas.
    Ventas_factTable_1 = $('#venta_table_1').DataTable({
        destroy: true,
        data: ventas_fact["_1"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "img" },
            { "data": "cantidad" },
            { "data": "precio" },
            { "data": "impuesto" },
            { "data": "subtotal" },
            { "data": "exento" },
            { "data": "total" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [5, 6, 8] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1], }, //El Id no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [7], }, //El impuesto no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [9], }, //El exento no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [10], }, //El total no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [4], }, //El Img no se muestra

            { width: '2%', "targets": 0 },
            { width: '20%', "targets": 2 },
            { width: '48%', "targets": 3 },
            // { width: '5%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '10%', "targets": 6 },
            { width: '10%', "targets": 8 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "rowCallback": function(row, data) {
            if ($.inArray(data.DT_RowId, rowSelectedFact[Documento]) !== -1) {
                $(row).addClass('selected');
            }
        },
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(8).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(7).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(subtotal) + parseFloat(impuesto);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', Ventas_factTable_1.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', Ventas_factTable_1.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', Ventas_factTable_1.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', Ventas_factTable_1.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exento" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotal" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuesto" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_total" + Documento).val(total);
        }
    });

    Ventas_factTable_1.on('order.dt search.dt', function() {
        Ventas_factTable_1.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección múltiple.
    $('#venta_table_1 tbody').on('click', 'tr', function() {

        var table = $('#venta_table_1').DataTable();
        var data = table.row(this).data(); //Obtengo la información de la fila seleccionada

        if (data != undefined) {

            var index = rowSelectedFact[Documento].indexOf(data); //Busco si mi obj auxiliar ya tiene la data seleccionada
            if (index === -1) { //No está aún en el obj auxiliar, lo agrega
                rowSelectedFact[Documento].push(data);
            } else {
                rowSelectedFact[Documento].splice(index, 1); //Ya se encuentra en el obj auxliar, lo elimina (Deselecciona)
            }

            $(this).toggleClass('selected'); //Si tiene la clase selected se la quita, caso contrario se la agrega
            if (rowSelectedFact[Documento].length == 1) { //Si solamente el seleccionado es 1 solo.
                //Cargando los valores en el formulario para permitir la edición.
                $("#venta_codigo" + Documento).val(rowSelectedFact[Documento][0]["codigo"]);
                $("#venta_cantidad" + Documento).val(rowSelectedFact[Documento][0]["cantidad"]);
                $("#venta_precio" + Documento).val(rowSelectedFact[Documento][0]["precio"]);
                $("#venta_total" + Documento).val(rowSelectedFact[Documento][0]["total"]);
                $("#venta_descripcion" + Documento).val(rowSelectedFact[Documento][0]["descripcion"]);

                //Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', false);

                //Deshabilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', true);

                //Cargar los datos de inventario
                consultarInformacionProducto(rowSelectedFact[Documento][0]["codigo"]);
            } else {
                //Limpiando el formulariocde agregar productos
                limpiarFormularioFactProducto();

                //DES-Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

                //Habilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', false)

                //Limpio los datos del inventario
                limpiarInventario();

                //Se limpia a lista de precios
                limpiarListaPrecios();

                //Se limpiar el listado de los 2 últimos precios de venta
                limpiarUltimos2Precios();
            }

            if (rowSelectedFact[Documento].length > 1) {
                $("#ventas_fieldset" + Documento).prop('disabled', true);
            } else {
                $("#ventas_fieldset" + Documento).prop('disabled', false);
            }

            if (rowSelectedFact[Documento].length >= 1) {
                $("#ventas_promocional" + Documento).prop('disabled', false);
            } else {
                $("#ventas_promocional" + Documento).prop('disabled', true);
            }
        }
    });

}

/**
 * Método donde se define el dataTable de las facturas en DOC 2
 * @return {[type]} [description]
 */
var cargarVentas_factDataTable_2 = function() {
    //Inicialización de tabla de facturas en ventas.
    Ventas_factTable_2 = $('#venta_table_2').DataTable({
        destroy: true,
        data: ventas_fact["_2"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "img" },
            { "data": "cantidad" },
            { "data": "precio" },
            { "data": "impuesto" },
            { "data": "subtotal" },
            { "data": "exento" },
            { "data": "total" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [5, 6, 8] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1], }, //El Id no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [7], }, //El impuesto no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [9], }, //El exento no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [10], }, //El total no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [4], }, //La Img no se muestra

            { width: '2%', "targets": 0 },
            { width: '20%', "targets": 2 },
            { width: '48%', "targets": 3 },
            // { width: '5%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '10%', "targets": 6 },
            { width: '10%', "targets": 8 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "rowCallback": function(row, data) {
            if ($.inArray(data.DT_RowId, rowSelectedFact[Documento]) !== -1) {
                $(row).addClass('selected');
            }
        },
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(8).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(7).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(subtotal) + parseFloat(impuesto);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', Ventas_factTable_2.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', Ventas_factTable_2.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', Ventas_factTable_2.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', Ventas_factTable_2.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exento" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotal" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuesto" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_total" + Documento).val(total);
        }
    });

    Ventas_factTable_2.on('order.dt search.dt', function() {
        Ventas_factTable_2.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección múltiple.
    $('#venta_table_2 tbody').on('click', 'tr', function() {

        var table = $('#venta_table_2').DataTable();
        var data = table.row(this).data(); //Obtengo la información de la fila seleccionada

        if (data != undefined) {
            var index = rowSelectedFact[Documento].indexOf(data); //Busco si mi obj auxiliar ya tiene la data seleccionada

            if (index === -1) { //No está aún en el obj auxiliar, lo agrega
                rowSelectedFact[Documento].push(data);
            } else {
                rowSelectedFact[Documento].splice(index, 1); //Ya se encuentra en el obj auxliar, lo elimina (Deselecciona)
            }

            $(this).toggleClass('selected'); //Si tiene la clase selected se la quita, caso contrario se la agrega

            if (rowSelectedFact[Documento].length == 1) { //Si solamente el seleccionado es 1 solo.
                //Cargando los valores en el formulario para permitir la edición.
                $("#venta_codigo" + Documento).val(rowSelectedFact[Documento][0]["codigo"]);
                $("#venta_cantidad" + Documento).val(rowSelectedFact[Documento][0]["cantidad"]);
                $("#venta_precio" + Documento).val(rowSelectedFact[Documento][0]["precio"]);
                $("#venta_total" + Documento).val(rowSelectedFact[Documento][0]["total"]);
                $("#venta_descripcion" + Documento).val(rowSelectedFact[Documento][0]["descripcion"]);

                //Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', false);

                //Deshabilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', true);

                //Cargar los datos de inventario
                consultarInformacionProducto(rowSelectedFact[Documento][0]["codigo"]);
            } else {
                //Limpiando el formulariocde agregar productos
                limpiarFormularioFactProducto();

                //DES-Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

                //Habilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', false)

                //Limpio los datos del inventario
                limpiarInventario();

                //Se limpia a lista de precios
                limpiarListaPrecios();

                //Se limpiar el listado de los 2 últimos precios de venta
                limpiarUltimos2Precios();
            }

            if (rowSelectedFact[Documento].length > 1) {
                $("#ventas_fieldset" + Documento).prop('disabled', true);
            } else {
                $("#ventas_fieldset" + Documento).prop('disabled', false);
            }
        }
    });

}

/**
 * Método donde se define el dataTable de las facturas en DOC 3
 * @return {[type]} [description]
 */
var cargarVentas_factDataTable_3 = function() {
    //Inicialización de tabla de facturas en ventas.
    Ventas_factTable_3 = $('#venta_table_3').DataTable({
        destroy: true,
        data: ventas_fact["_3"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "img" },
            { "data": "cantidad" },
            { "data": "precio" },
            { "data": "impuesto" },
            { "data": "subtotal" },
            { "data": "exento" },
            { "data": "total" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [5, 6, 8] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1], }, //El Id no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [7], }, //El impuesto no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [9], }, //El exento no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [10], }, //El total no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [4], }, //La Img no se muestra

            { width: '2%', "targets": 0 },
            { width: '20%', "targets": 2 },
            { width: '48%', "targets": 3 },
            // { width: '5%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '10%', "targets": 6 },
            { width: '10%', "targets": 8 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "rowCallback": function(row, data) {
            if ($.inArray(data.DT_RowId, rowSelectedFact[Documento]) !== -1) {
                $(row).addClass('selected');
            }
        },
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(8).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(7).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(subtotal) + parseFloat(impuesto);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', Ventas_factTable_3.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', Ventas_factTable_3.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', Ventas_factTable_3.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', Ventas_factTable_3.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exento" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotal" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuesto" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_total" + Documento).val(total);
        }
    });

    Ventas_factTable_3.on('order.dt search.dt', function() {
        Ventas_factTable_3.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección múltiple.
    $('#venta_table_3 tbody').on('click', 'tr', function() {

        var table = $('#venta_table_3').DataTable();
        var data = table.row(this).data(); //Obtengo la información de la fila seleccionada

        if (data != undefined) {
            var index = rowSelectedFact[Documento].indexOf(data); //Busco si mi obj auxiliar ya tiene la data seleccionada

            if (index === -1) { //No está aún en el obj auxiliar, lo agrega
                rowSelectedFact[Documento].push(data);
            } else {
                rowSelectedFact[Documento].splice(index, 1); //Ya se encuentra en el obj auxliar, lo elimina (Deselecciona)
            }

            $(this).toggleClass('selected'); //Si tiene la clase selected se la quita, caso contrario se la agrega

            if (rowSelectedFact[Documento].length == 1) { //Si solamente el seleccionado es 1 solo.
                //Cargando los valores en el formulario para permitir la edición.
                $("#venta_codigo" + Documento).val(rowSelectedFact[Documento][0]["codigo"]);
                $("#venta_cantidad" + Documento).val(rowSelectedFact[Documento][0]["cantidad"]);
                $("#venta_precio" + Documento).val(rowSelectedFact[Documento][0]["precio"]);
                $("#venta_total" + Documento).val(rowSelectedFact[Documento][0]["total"]);
                $("#venta_descripcion" + Documento).val(rowSelectedFact[Documento][0]["descripcion"]);

                //Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', false);

                //Deshabilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', true);

                //Cargar los datos de inventario
                consultarInformacionProducto(rowSelectedFact[Documento][0]["codigo"]);
            } else {
                //Limpiando el formulariocde agregar productos
                limpiarFormularioFactProducto();

                //DES-Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

                //Habilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', false);

                //Limpio los datos del inventario
                limpiarInventario();

                //Se limpia a lista de precios
                limpiarListaPrecios();

                //Se limpiar el listado de los 2 últimos precios de venta
                limpiarUltimos2Precios();
            }

            if (rowSelectedFact[Documento].length > 1) {
                $("#ventas_fieldset" + Documento).prop('disabled', true);
            } else {
                $("#ventas_fieldset" + Documento).prop('disabled', false);
            }
        }
    });

}

/**
 * Método donde se define el dataTable de las facturas en DOC 4
 * @return {[type]} [description]
 */
var cargarVentas_factDataTable_4 = function() {
    //Inicialización de tabla de facturas en ventas.
    Ventas_factTable_4 = $('#venta_table_4').DataTable({
        destroy: true,
        data: ventas_fact["_4"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "img" },
            { "data": "cantidad" },
            { "data": "precio" },
            { "data": "impuesto" },
            { "data": "subtotal" },
            { "data": "exento" },
            { "data": "total" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [5, 6, 8] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1], }, //El Id no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [7], }, //El impuesto no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [9], }, //El exento no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [10], }, //El total no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [4], }, //La Img no se muestra

            { width: '2%', "targets": 0 },
            { width: '20%', "targets": 2 },
            { width: '48%', "targets": 3 },
            // { width: '5%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '10%', "targets": 6 },
            { width: '10%', "targets": 8 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "rowCallback": function(row, data) {
            if ($.inArray(data.DT_RowId, rowSelectedFact[Documento]) !== -1) {
                $(row).addClass('selected');
            }
        },
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(8).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(7).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(subtotal) + parseFloat(impuesto);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', Ventas_factTable_4.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', Ventas_factTable_4.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', Ventas_factTable_4.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', Ventas_factTable_4.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exento" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotal" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuesto" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_total" + Documento).val(total);
        }
    });

    Ventas_factTable_4.on('order.dt search.dt', function() {
        Ventas_factTable_4.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección múltiple.
    $('#venta_table_4 tbody').on('click', 'tr', function() {

        var table = $('#venta_table_4').DataTable();
        var data = table.row(this).data(); //Obtengo la información de la fila seleccionada

        if (data != undefined) {
            var index = rowSelectedFact[Documento].indexOf(data); //Busco si mi obj auxiliar ya tiene la data seleccionada

            if (index === -1) { //No está aún en el obj auxiliar, lo agrega
                rowSelectedFact[Documento].push(data);
            } else {
                rowSelectedFact[Documento].splice(index, 1); //Ya se encuentra en el obj auxliar, lo elimina (Deselecciona)
            }

            $(this).toggleClass('selected'); //Si tiene la clase selected se la quita, caso contrario se la agrega

            if (rowSelectedFact[Documento].length == 1) { //Si solamente el seleccionado es 1 solo.
                //Cargando los valores en el formulario para permitir la edición.
                $("#venta_codigo" + Documento).val(rowSelectedFact[Documento][0]["codigo"]);
                $("#venta_cantidad" + Documento).val(rowSelectedFact[Documento][0]["cantidad"]);
                $("#venta_precio" + Documento).val(rowSelectedFact[Documento][0]["precio"]);
                $("#venta_total" + Documento).val(rowSelectedFact[Documento][0]["total"]);
                $("#venta_descripcion" + Documento).val(rowSelectedFact[Documento][0]["descripcion"]);

                //Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', false);

                //Deshabilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', true);

                //Cargar los datos de inventario
                consultarInformacionProducto(rowSelectedFact[Documento][0]["codigo"]);
            } else {
                //Limpiando el formulariocde agregar productos
                limpiarFormularioFactProducto();

                //DES-Habilito el botón de eliminar un producto
                $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

                //Habilito la edición del código del producto
                $("#venta_codigo" + Documento).prop('disabled', false);

                //Limpio los datos del inventario
                limpiarInventario();

                //Se limpia a lista de precios
                limpiarListaPrecios();

                //Se limpiar el listado de los 2 últimos precios de venta
                limpiarUltimos2Precios();
            }

            if (rowSelectedFact[Documento].length > 1) {
                $("#ventas_fieldset" + Documento).prop('disabled', true);
            } else {
                $("#ventas_fieldset" + Documento).prop('disabled', false);
            }
        }
    });

}

/**
 * Se carga las opciones del autoComplete de productos en Ventas
 * @type {Object}
 */
var Venta_Fact_options = {
    data: [],
    placeholder: "Código | nombre del producto | codigo de barra | codigo 2",

    getValue: function(element) {

        var query = $(element).prop("codigo") + " : " + $(element).prop("nombre");
        if ($(element).prop("codigo2") == null ? query += "" : query += " : " + $(element).prop("codigo2"));
        if ($(element).prop("barcode") == null ? query += "" : query += " : " + $(element).prop("barcode"));
        return query;
    },

    list: {
        maxNumberOfElements: 8,
        showAnimation: {
            type: "fade", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        hideAnimation: {
            type: "slide", //normal|slide|fade
            time: 400,
            callback: function() {}
        },
        match: {
            enabled: true
        },
        onClickEvent: function() {
            $("#venta_cantidad" + Documento).focus();
        }
    },
    theme: "dark", //dark, blue, purple, yellow, blue-light, green-light, bootstrap
    adjustWidth: false,
};

/**
 * Métodi para consultar los productos que se encuentran activos y cargar el autocomplete mostrando código - descripción
 */
var Ventas_ProductosActivos = function() {
    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/getProductosActivos";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
        },
        success: function(obj) {
            $(obj.productos).each(function(i, v) { // indice, valor
                Venta_Fact_options.data[i] = { codigo: v.codigo, nombre: v.nombre, codigo2: v.codigo2, barcode: v.barcode }; //Agregando al autocomplete codigo, descripcion, codigo2, barcode
            })

            //Aplicando el auto complete a los campos
            $("#venta_codigo_1").easyAutocomplete(Venta_Fact_options);
            $("#venta_codigo_2").easyAutocomplete(Venta_Fact_options);
            $("#venta_codigo_3").easyAutocomplete(Venta_Fact_options);
            $("#venta_codigo_4").easyAutocomplete(Venta_Fact_options);
            $("#venta_agregarDetalle_codigo").easyAutocomplete(Venta_Fact_options);
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

/**
 * Metodo para inicializar el tipo de cantidad quepermite el producto (decimal o no, por defecto Sí permite decimales)
 * @param  {[type]} _documento [description]
 * @return {[type]}            [description]
 */
var inicializarPrecioDecimal = function(_documento) {
    $("#ventas_precioDecimal" + _documento).val("S");
}

/**
 * Método para al seleccionar un producto del autoComplete de Fact Ventas, haga el seteo de las variables de Cantidad, Precio, Total y Descripción
 * @param  {[type]} $producto_codigo Código del Producto Seleccionado.
 * @return {[type]}                  [description]
 */
var consultarVentas_FactCodigo = function(producto_codigo) {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();
    var precio_cliente = ventas_clienteSelected[Documento][0]["precio"];

    if (producto_codigo == "") {
        producto_codigo = " ";
    }

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/precio_cliente/" + precio_cliente + "/consultarProductoFactura/" + producto_codigo;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            precio_cliente: precio_cliente,
            producto_codigo: producto_codigo
        },
        success: function(obj) {
            //Seteando variables según lo encontrado
            //Se establece un booleando con la opción de cambiar o no algun contenido
            $cambiar = true;

            //Quitar lo seleccionado de los radio butoms
            $('input[type=radio][name=venta_nivelPrecio_1]').prop('checked', false);

            if (rowSelectedFact[Documento][0] != undefined) { //Se determina si hay un elemento seleccionado, de se así cambiar se hace falso
                $cambiar = false;
            }
            if (obj.producto != null) {

                if ($cambiar == true) {
                    $("#venta_cantidad" + Documento).val("1");
                    $("#venta_precio" + Documento).val(obj.producto.precio);
                    $("#ventas_productoImpuesto" + Documento).val(obj.producto.impuesto);
                    if (obj.producto.impuesto == null || obj.producto.impuesto == 0) {
                        $("#venta_total" + Documento).val(parseFloat(1 * obj.producto.precio).toFixed(2));
                    } else {
                        $("#venta_total" + Documento).val(parseFloat((1 * obj.producto.precio) * (1 + obj.producto.impuesto / 100)).toFixed(2));
                    }

                    //valido si la venta es exenta
                    if (ventas_exento[Documento][0]["exento"] == true) {
                        $("#venta_total" + Documento).val(parseFloat(1 * obj.producto.precio).toFixed(2));
                    }

                    $("#venta_descripcion" + Documento).val(obj.producto.nombre);
                }

                $("#ventas_precioDecimal" + Documento).val(obj.producto.decimal);

                if (obj.producto.serie == "S") {
                    $("#venta_cantidad" + Documento).prop("disabled", true);
                } else {
                    $("#venta_cantidad" + Documento).prop("disabled", false);
                }

                if (obj.producto.ubicacion != null) {
                    $("#venta_ubicacionValue" + Documento).html(obj.producto.ubicacion);
                } else {
                    $("#venta_ubicacionValue" + Documento).html("");
                }

                if (obj.producto.marca != null) {
                    $("#venta_marcaValue" + Documento).html(obj.producto.marca);
                } else {
                    $("#venta_marcaValue" + Documento).html("");
                }

                //Id del producto
                $("#venta_producto_id" + Documento).val(obj.producto.id);

            } else {
                $("#venta_cantidad" + Documento).val("");
                $("#venta_precio" + Documento).val("");
                $("#venta_total" + Documento).val("");
                $("#venta_descripcion" + Documento).val("");
                $("#venta_producto_id" + Documento).val("");
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
}

/**
 * Método para cortar el valor del campo del autocomplete y solamente tomar el código para procesar las diversas consultas
 * @param  {[type]} _value [description]
 * @return {[type]}        [description]
 */
function autoCompleteVentaFactFormatValue(_value) {
    if (_value != "") {
        var auxValue = _value.split(" : ");
        return auxValue[0];
    }
    return null;
}

/**
 * Método para actualizar el DataTable de Factura
 * @return {[type]} [description]
 */
var actualizarVentas_FactDataTable = function() {
    $('#venta_table' + Documento).dataTable().fnClearTable();
    if (ventas_fact[Documento].length > 0) {
        $('#venta_table' + Documento).dataTable().fnAddData(ventas_fact[Documento]);
    }
}

/**
 * Método para limpiar los campos del formulario de Ventas, el que nos sirve para agregar productos a la factura
 * @return {[type]} [description]
 */
var limpiarFormularioFactProducto = function() {
    $("#venta_codigo" + Documento).val("");
    $("#venta_cantidad" + Documento).val("");
    $("#venta_precio" + Documento).val("");
    $("#venta_total" + Documento).val("");
    $("#venta_descripcion" + Documento).val("");
}

/**
 * Método para realizar la actualización del total cuando el precio o la cantidad cambia
 * @return {[type]} [description]
 */
function ventas_actualizarTotal() {

    var error = false;
    //Validar si el producto permite cantidades en decimales.
    //De no permitirlas, verificar que "cantidad", no contenga ".", si lo contiene, mostrar un error al usuario
    if ($("#ventas_precioDecimal" + Documento).val() != "S") {
        cadena = $("#venta_cantidad" + Documento).val();
        if (cadena.indexOf('.') != -1) {
            //Muestro msj de error.
            $("#msjOkFact" + Documento).hide();
            var errorFact = '{"message": "Este producto no permite cantidades con Decimales"}';
            errorMsj(JSON.parse(errorFact), 'msjErrorFact' + Documento, 'formErrorFact' + Documento);
            error = true;
        }
    }

    if (error == false) {
        $("#venta_total" + Documento).val(
            parseFloat(
                ($("#venta_cantidad" + Documento).val() * $("#venta_precio" + Documento).val()) *
                (1 + (ventas_exento[Documento][0]["exento"] == false ? $("#ventas_productoImpuesto" + Documento).val() : 0) / 100)
            ).toFixed(2));

        //Validando si el total es incorrecto.
        if ($("#venta_total" + Documento).val() == "NaN") {

            $("#venta_total" + Documento).val("");
            //Muestro msj de error.
            $("#msjOkFact" + Documento).hide();
            var errorFact = '{"message": "El separador decimal utilizado debe ser el Punto (.)"}';
            errorMsj(JSON.parse(errorFact), 'msjErrorFact' + Documento, 'formErrorFact' + Documento);
        }
    }
}


/**
 * Método para cortar el valor del campo del autocomplete y solamente tomar el código del vendedor para ser añadido a BD
 * @param  {[type]} _value [ID: qwerty Nombre: qwerty]
 * @return {[type]}        [description]
 */
function autoCompleteVendedor_ClienteFormatValue(_value) {

    if (_value != "") {
        var auxValue = _value.split("Nombre: ");
        if (auxValue.length > 1) {
            var auxID = auxValue[0].split("ID: ");
            return auxID[1];
        } else {
            return "-1";
        }
    }
    return "";
}

/**
 * Método para determinar cuando una opción del radio buttom cambia en DOC1
 */
$('input[type=radio][name=venta_tipo_documento_1]').change(function() {
    cambiosEnRadioTipoDocumento(this);
});
/**
 * Método para determinar cuando una opción del radio buttom cambia en DOC2
 */
$('input[type=radio][name=venta_tipo_documento_2]').change(function() {
    cambiosEnRadioTipoDocumento(this);
});
/**
 * Método para determinar cuando una opción del radio buttom cambia en DOC3
 */
$('input[type=radio][name=venta_tipo_documento_3]').change(function() {
    cambiosEnRadioTipoDocumento(this);
});
/**
 * Método para determinar cuando una opción del radio buttom cambia en DOC4
 */
$('input[type=radio][name=venta_tipo_documento_4]').change(function() {
    cambiosEnRadioTipoDocumento(this);
});

/**
 * Método que se ejecutará cuando el tipo de documento sea cambiado por el usuario.
 * @param  {[type]} _this [description]
 * @return {[type]}       [description]
 */
function cambiosEnRadioTipoDocumento(_this) {
    if (_this.value == 'CON') {
        $("#venta_header_table" + Documento).html("Factura a Contado");
    } else if (_this.value == 'CRE') {
        $("#venta_header_table" + Documento).html("Factura a Crédito");
    } else if (_this.value == 'COT') {
        $("#venta_header_table" + Documento).html("Cotización");
    } else if (_this.value == 'PRO') {
        $("#venta_header_table" + Documento).html("Proforma");
    } else if (_this.value == 'NOT') {
        $("#venta_header_table" + Documento).html("Nota de Crédito");

        //Cambiando las opciones por defecto del modal, quitando el "esc" del teclado y evitando que se cierre al hacer click por fuera del modal
        $("#ventas_notaCreditoModal").modal({
            keyboard: false,
            backdrop: 'static',
        });

        //Abrir ventana emergente para solicitar el # de Factura o CCO.
        $('#ventas_notaCreditoModal').modal('show');
    }
}

/**
 * Método para cargar el vendedor que está ejecutando el módulo de ventas
 * @return {[type]} [description]
 */
var cargarVendedorActual = function() {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();
    var vendedor_id = $("#user_id").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/consultarVendedor/" + vendedor_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            vendedor_id: vendedor_id
        },
        success: function(obj) {
            //Seteando variables según lo encontrado
            $("#venta_vendedor_1").val("ID: " + obj.vendedor.id + " Nombre: " + obj.vendedor.name);
            $("#venta_vendedor_2").val("ID: " + obj.vendedor.id + " Nombre: " + obj.vendedor.name);
            $("#venta_vendedor_3").val("ID: " + obj.vendedor.id + " Nombre: " + obj.vendedor.name);
            $("#venta_vendedor_4").val("ID: " + obj.vendedor.id + " Nombre: " + obj.vendedor.name);

            //Estableciendo el Id del vendedor en las varibales ocultas
            $("#ventas_vendedorSelected_1").val(obj.vendedor.id);
            $("#ventas_vendedorSelected_2").val(obj.vendedor.id);
            $("#ventas_vendedorSelected_3").val(obj.vendedor.id);
            $("#ventas_vendedorSelected_4").val(obj.vendedor.id);
            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
}

/**
 * Método para consultar toda la información del producto, desde sus datos básicos para colocar en la factura, hasta los precios e inventario del mismo que se cargan en la vista
 * @return {[type]} [description]
 */
var consultarInformacionProducto = function(_producto_codigo) {

    //Método para limpiar los campos de la sección de inventario para posteriormente cargarlos con los datos consultados
    limpiarInventario();

    //Se hace un llamado de los metodos necesarios para carga la información del producto en la venta, el inventario y los precios.
    $.when(
            consultarVentas_FactCodigo(_producto_codigo),
            consultarInvtDisponibleYSeparado(_producto_codigo),
            consultarProductoEnTransito(_producto_codigo),
            consultarListaPrecios(_producto_codigo),
            consultarUltimosPrecios(_producto_codigo)
        )
        .done(function() {

            console.log("Terminé");
        });
}

/**
 * Método para consultar el invetario disponible del producto seleccionado.
 * @param  {[type]} _producto_codigo [description]
 * @return {[type]}                  [description]
 */
var consultarInvtDisponibleYSeparado = function(_producto_codigo) {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_codigo = _producto_codigo;

    var route = "/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_codigo/" + producto_codigo + "/consultarDisponibleYSeparado";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_codigo: producto_codigo
        },
        success: function(obj) {

            if (obj.producto != null) {

                if (obj.producto.disponible != null) {
                    $("#venta_disponibleValue" + Documento).html(parseFloat(obj.producto.disponible).toFixed(2));
                }

                if (obj.producto.disponible != null) {
                    $("#venta_separadoValue" + Documento).html(parseFloat(obj.producto.separado).toFixed(2));
                }
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
}

/**
 * Método para consultar la cantidad de producto que se encuentra en transito
 * @param  {[type]} _producto_codigo [código del producto]
 * @return {[type]}                  [description]
 */
var consultarProductoEnTransito = function(_producto_codigo) {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_codigo = _producto_codigo;

    var route = "/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_codigo/" + producto_codigo + "/consultarTransito";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_codigo: producto_codigo
        },
        success: function(obj) {
            if (obj.transito != null) {
                $("#venta_transitoValue" + Documento).html(parseFloat(obj.transito).toFixed(2));
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
}

/**
 * Método para limpiar los campos de la sección de inventario.
 * 
 * @return {[type]} [description]
 */
var limpiarInventario = function() {
    $("#venta_disponibleValue" + Documento).html("0.00");
    $("#venta_transitoValue" + Documento).html("0.00");
    $("#venta_separadoValue" + Documento).html("0.00");
    $("#venta_ubicacionValue" + Documento).html("");
    $("#venta_marcaValue" + Documento).html("");
}

/**
 * Método para consultar el listado de precios del producto
 * @param  {[type]} _producto_codigo [código del producto]
 * @return {[type]}                  [description]
 */
var consultarListaPrecios = function(_producto_codigo) {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_codigo = _producto_codigo;

    var route = "/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_codigo/" + producto_codigo + "/consultarListaPrecios";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_codigo: producto_codigo
        },
        success: function(obj) {

            //Se limpia a lista de precios
            limpiarListaPrecios();

            if (obj.precios != null) {
                $('label[for=venta_precioA' + Documento + ']').html('A. ' + parseFloat(obj.precios.precio_a).toFixed(2));
                $('#venta_precioA' + Documento).val(parseFloat(obj.precios.precio_a).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioA" + Documento, obj.precios.precio_a);

                $('label[for=venta_precioB' + Documento + ']').html('B. ' + parseFloat(obj.precios.precio_b).toFixed(2));
                $('#venta_precioB' + Documento).val(parseFloat(obj.precios.precio_b).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioB" + Documento, obj.precios.precio_b);

                $('label[for=venta_precioC' + Documento + ']').html('C. ' + parseFloat(obj.precios.precio_c).toFixed(2));
                $('#venta_precioC' + Documento).val(parseFloat(obj.precios.precio_c).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioC" + Documento, obj.precios.precio_c);

                $('label[for=venta_precioE' + Documento + ']').html('E. ' + parseFloat(obj.precios.precio_e).toFixed(2));
                $('#venta_precioE' + Documento).val(parseFloat(obj.precios.precio_e).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioE" + Documento, obj.precios.precio_e);

                $('label[for=venta_precioPRD' + Documento + ']').html('PrD ' + parseFloat(obj.precios.precio_f).toFixed(2));
                $('#venta_precioPRD' + Documento).val(parseFloat(obj.precios.precio_f).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioPRD" + Documento, obj.precios.precio_f);

                $('label[for=venta_precioPRM' + Documento + ']').html('PrM ' + parseFloat(obj.precios.precio_g).toFixed(2));
                $('#venta_precioPRM' + Documento).val(parseFloat(obj.precios.precio_g).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioPRM" + Documento, obj.precios.precio_g);
            }

            if (obj.oferta != null) {
                $('label[for=venta_precioD' + Documento + ']').html('D. ' + parseFloat(obj.oferta.precio_d).toFixed(2));
                $('#venta_precioD' + Documento).val(parseFloat(obj.oferta.precio_d).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioD" + Documento, obj.precios.precio_d);
            } else {
                $('#venta_precioD' + Documento).val(parseFloat(0.00).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioD" + Documento, 0);
            }

            if (obj.UltimoPrecio != null) {
                $('label[for=venta_precioUP' + Documento + ']').html('UP ' + parseFloat(obj.UltimoPrecio.precio).toFixed(2));
                $('#venta_precioUP' + Documento).val(parseFloat(obj.UltimoPrecio.precio).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioUP" + Documento, obj.precios.precio);
            } else {
                $('#venta_precioUP' + Documento).val(parseFloat(0.00).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_precioUP" + Documento, 0);
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
}

/**
 * Método para limpiar el listado de precios.
 * @return {[type]} [description]
 */
var limpiarListaPrecios = function() {
    $('label[for=venta_precioA' + Documento + ']').html('A. 0.00');
    $('label[for=venta_precioB' + Documento + ']').html('B. 0.00');
    $('label[for=venta_precioC' + Documento + ']').html('C. 0.00');
    $('label[for=venta_precioD' + Documento + ']').html('D. 0.00');
    $('label[for=venta_precioE' + Documento + ']').html('E. 0.00');
    $('label[for=venta_precioUP' + Documento + ']').html('UP 0.00');
    $('label[for=venta_precioPRD' + Documento + ']').html('PrD 0.00');
    $('label[for=venta_precioPRM' + Documento + ']').html('PrM 0.00');

    //Habilitando la selección de los radio butoms nuevamente
    $('#venta_precioA' + Documento).attr('disabled', false);
    $('#venta_precioB' + Documento).attr('disabled', false);
    $('#venta_precioC' + Documento).attr('disabled', false);
    $('#venta_precioD' + Documento).attr('disabled', false);
    $('#venta_precioE' + Documento).attr('disabled', false);
    $('#venta_precioUP' + Documento).attr('disabled', false);
    $('#venta_precioPRD' + Documento).attr('disabled', false);
    $('#venta_precioPRM' + Documento).attr('disabled', false);


    //Quitar lo seleccionado de los radio butoms
    $('input[type=radio][name=venta_nivelPrecio' + Documento + ']').prop('checked', false);
}

/**
 * Método para cargar el vendedor que está ejecutando el módulo de ventas
 * @return {[type]} [description]
 */
var ventas_cargarClienteFrecuente = function() {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();
    var cliente_id = "1"; //Cargado en la inicialización del sistema

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/consultarCliente/" + cliente_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            cliente_id: cliente_id
        },
        success: function(obj) {
            //Se limpian los array de cliente selected
            inicializarClientesSelected();

            //Seteando variables según lo encontrado
            $("#venta_cliente_1").val("ID: " + obj.cliente.id + " Ced: " + obj.cliente.cedula + " Nombre: " + obj.cliente.primer_nombre);
            $("#venta_cliente_2").val("ID: " + obj.cliente.id + " Ced: " + obj.cliente.cedula + " Nombre: " + obj.cliente.primer_nombre);
            $("#venta_cliente_3").val("ID: " + obj.cliente.id + " Ced: " + obj.cliente.cedula + " Nombre: " + obj.cliente.primer_nombre);
            $("#venta_cliente_4").val("ID: " + obj.cliente.id + " Ced: " + obj.cliente.cedula + " Nombre: " + obj.cliente.primer_nombre);
            ventas_clienteSelected["_1"].push({ id: obj.cliente.id, precio: obj.cliente.codigo_precio });
            ventas_clienteSelected["_2"].push({ id: obj.cliente.id, precio: obj.cliente.codigo_precio });
            ventas_clienteSelected["_3"].push({ id: obj.cliente.id, precio: obj.cliente.codigo_precio });
            ventas_clienteSelected["_4"].push({ id: obj.cliente.id, precio: obj.cliente.codigo_precio });

            //Determinando si el cliente es de crédito o de contado
            if (obj.cliente.tipo_cliente_id == 1) {
                $('input[type=radio][name=venta_tipo_documento_1][value="CRE"]').attr('disabled', 'disabled');
                $('input[type=radio][name=venta_tipo_documento_2][value="CRE"]').attr('disabled', 'disabled');
                $('input[type=radio][name=venta_tipo_documento_3][value="CRE"]').attr('disabled', 'disabled');
                $('input[type=radio][name=venta_tipo_documento_4][value="CRE"]').attr('disabled', 'disabled');
            }

            //Inicializo los valores exentos
            ventas_exento["_1"] = new Array();
            ventas_exento["_2"] = new Array();
            ventas_exento["_3"] = new Array();
            ventas_exento["_4"] = new Array();

            //seteando cliente exento o no.
            if (obj.cliente.exento == "N") {

                ventas_exento["_1"].push({
                    exento: false,
                });
                ventas_exento["_2"].push({
                    exento: false,
                });
                ventas_exento["_3"].push({
                    exento: false,
                });
                ventas_exento["_4"].push({
                    exento: false,
                });

                $("#venta_exento" + Documento).prop('checked', false);

            } else {
                ventas_exento["_1"].push({
                    exento: true,
                });
                ventas_exento["_2"].push({
                    exento: true,
                });
                ventas_exento["_3"].push({
                    exento: true,
                });
                ventas_exento["_4"].push({
                    exento: true,
                });

                $("#venta_exento" + Documento).prop('checked', true);
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
}

/**
 * Método para limpiar el arreglo de cliente seleccionado de todos los docs.
 * @return {[type]} [description]
 */
var inicializarClientesSelected = function() {
    ventas_clienteSelected["_1"] = new Array(); //id, codigo_precio
    ventas_clienteSelected["_2"] = new Array(); //id, codigo_precio
    ventas_clienteSelected["_3"] = new Array(); //id, codigo_precio
    ventas_clienteSelected["_4"] = new Array(); //id, codigo_precio
}

/**
 * Variable creada para llevar la estructura de la tabla de Números de Serie
 */
var ventas_NumeroSerie = new Array(); //nro, nro serie, fact compra, producto_id, nro serie id
var ventas_NumeroSerieDataTable; //Variable del dataTable

var rowSelectedNumeroSerie = ""; //LLeva el número de serie seleccionado


/**
 * Método donde se define el dataTable de los números de serie
 * @return {[type]} [description]
 */
var cargarVentas_NumeroSerieDataTable = function() {
    //Inicialización de tabla de números de serie
    ventas_NumeroSerieDataTable = $('#Ventas_NumeroSerieTable').DataTable({
        destroy: true,
        data: ventas_NumeroSerie,
        columns: [
            { "data": "nro" },
            { "data": "nro_serie" },
            { "data": "fact_compra" },
            { "data": "producto_id" },
            { "data": "nro_serie_id" },
        ],
        columnDefs: [
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [3], }, //El Producto Id no se muestra
            { "visible": false, "searchable": false, "orderable": false, "targets": [4], }, //El Id del número de serie no se muestra
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
    });

    ventas_NumeroSerieDataTable.on('order.dt search.dt', function() {
        ventas_NumeroSerieDataTable.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección múltiple.
    $('#Ventas_NumeroSerieTable tbody').on('click', 'tr', function() {

        var data = ventas_NumeroSerieDataTable.row(this).data();

        if ($(this).hasClass('selected')) { //Método que deselecciona un elemento de la tabla

            $(this).removeClass('selected');

            //Al no haber nada seleccionado, el valor es vacío
            rowSelectedNumeroSerie = "";

            //Deshabilito el botón
            $('#venta_numeroSerieSelected').attr("disabled", true);

        } else {
            //Método cuando se selecciona un elemento de la tabla.
            ventas_NumeroSerieDataTable.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = ventas_NumeroSerie.indexOf(data);

            //Se le asigna el valor del índice donde está la información
            rowSelectedNumeroSerie = ventas_NumeroSerie[indice];

            //Añadiendo la clase se "selección"
            $(this).addClass('selected');

            //Habilito el boton
            $('#venta_numeroSerieSelected').removeAttr("disabled");
        }
    });
}

/**
 * Método para actualizar el DataTable de Número de Serie
 * @return {[type]} [description]
 */
var actualizarVentas_NumeroSerieDataTable = function() {
    $("#Ventas_NumeroSerieTable").dataTable().fnClearTable();
    if (ventas_NumeroSerie.length > 0) {
        $("#Ventas_NumeroSerieTable").dataTable().fnAddData(ventas_NumeroSerie);
    }
}

/**
 * Método que se ejecuta al seleccionar un elemento de la tabla de números de serie y se presiona aceptar.
 * @param  {[type]} event){} [description]
 * @return {[type]}            [description]
 */
$("#venta_numeroSerieSelected").click(function(event) {
    //Obteniendo del úlitmo elemento agregado la descripción para concatenarle el número de serie seleccionado.
    ventas_fact[Documento].slice(-1).pop()["descripcion"] += " Nro Serie: " + rowSelectedNumeroSerie["nro_serie"];

    //Asignando el id del número de serie al arreglo que lleva el control de las facturas
    ventas_fact[Documento].slice(-1).pop()["id_serie"] = rowSelectedNumeroSerie["nro_serie_id"];

    //Al actualizar la descripción, es necesario actualizar la tabla de ventas
    actualizarVentas_FactDataTable();

    //Cierro el modal de número de serie.
    $("#ventas_numeroSerie").modal('hide');

    //Quito la selección realizada
    rowSelectedNumeroSerie = "";
});

/**
 * Método que se llama cuando uno de los radio button del listado de precios es seleccionado en DOC1
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_nivelPrecio_1]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_nivelPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método que se llama cuando uno de los radio button del listado de precios es seleccionado en DOC2
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_nivelPrecio_2]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_nivelPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método que se llama cuando uno de los radio button del listado de precios es seleccionado en DOC3
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_nivelPrecio_3]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_nivelPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método que se llama cuando uno de los radio button del listado de precios es seleccionado en DOC4
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_nivelPrecio_4]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_nivelPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método para cerrar el modal de consultar documentos y abrir el de consultar factura
 * @param  {[type]} e) {            [description]
 * @return {[type]}    [description]
 */
$("#ventas_ConsultarFactura").click(function(event) {

    //Ocultar el modal de consultar documentos
    $("#ventas_ConsultarDocumentos").modal('hide');

    //Cambiando las opciones por defecto del modal, quitando el "esc" del teclado y evitando que se cierre al hacer click por fuera del modal
    $("#ventas_ConsultarFacturaModal").modal({
        keyboard: false,
        backdrop: 'static',
    });

    //Cargar todas las facturas con estatus "F" (Facturadas) ordenadas por fecha, de la más reciente a la más antigua.
    ventas_ConsultarFacturas();

    //Cargar el detalle de la factura, inicialmente al no haber selección, carga vacío
    ventas_ConsultarDetallleFactura("-1");

    //Abro el modal de consultar factura
    $("#ventas_ConsultarFacturaModal").modal('show');

    //Deshabilitar los botones del modal, solo se activan cuando hay una factura seleccionada.
    $("#ventas_reimprimirFacturaModal").prop('disabled', true);
    $("#ventas_anularFacturaModal").prop('disabled', true);
    $("#ventas_modificarFacturaModal").prop('disabled', true);
    $("#ventas_nuevoDocumentoFacturaModal").prop('disabled', true);
});

//Varible para llevar el control del tipo de documento consultado, cotirzación o proforma (1 - Cotización, 2 - Proforma);
var ventas_tipoDocumentoCotPro = false;

/**
 * Método para cerrar el modal de consultar documentos y abrir el de consultar cotizaciones/proformas
 * @param  {[type]} e) {            [description]
 * @return {[type]}    [description]
 */
var ventas_ConsultarCotPro = function(_value) {

    ventas_tipoDocumentoCotPro = _value;

    //Ocultar el modal de consultar documentos
    $("#ventas_ConsultarDocumentos").modal('hide');

    //Cambiando las opciones por defecto del modal, quitando el "esc" del teclado y evitando que se cierre al hacer click por fuera del modal
    $("#ventas_ConsultarCotProModal").modal({
        keyboard: false,
        backdrop: 'static',
    });

    if (ventas_tipoDocumentoCotPro == 1) { //Se trata de una cotización
        $("#ConsultarCotProModalLabel").html("Consultar Cotizaciones");
        $("#ventas_CotPro_Title").html("Cotizaciones");
    } else { //Se trata de una proforma
        $("#ConsultarCotProModalLabel").html("Consultar Proformas");
        $("#ventas_CotPro_Title").html("Proformas");
    }

    //Abro el modal de consultar 
    $("#ventas_ConsultarCotProModal").modal('show');

    //Cargar todas las cotizaciones ordenadas por fecha, de la más reciente a la más antigua.
    ventas_ConsultarCotProTable(ventas_tipoDocumentoCotPro);

    //Elimino el contenido de la tabla
    // $('#venta_table_consultarCotProDetalle').empty();

    //Cargar el detalle de la factura, inicialmente al no haber selección, carga vacío
    ventas_ConsultarDetalleCorProTable("-1");

    //Deshabilitar los botones del modal, solo se activan cuando hay una cotización seleccionada.
    $("#ventas_cotPro_Reimprimir").prop('disabled', true);
    $("#ventas_cotPro_Modificar").prop('disabled', true);
    $("#ventas_cotPro_Nuevo").prop('disabled', true);
    $("#ventas_cotPro_Anular").prop('disabled', true);
    $("#ventas_cotPro_Convertir").prop('disabled', true);

    //Deshabilitar los botones del detalle puesto que no hay ningún detalle seleccionado
    $("#ventas_CotPro_eliminarReglon").prop('disabled', true);
}

/**
 * Método para inicializar el formulario cuando el cliente no es válido
 * @return {[type]} [description]
 */
var ventas_inicializarFormulario = function() {
    //Header de contado
    $("#venta_header_table" + Documento).html("Factura a Contado");
    //Teléfono vacío
    $("#venta_telefono" + Documento).val("");
    //Puntos vacío
    $("#venta_puntos" + Documento).val("");
    //Dirección vacío
    $("#venta_direccion" + Documento).val("");
    //Inicializo el cliente seleccionado
    ventas_clienteSelected[Documento] = new Array();
    //Exento false
    $("#venta_exento" + Documento).prop('checked', false);
    //Se habilita la elección de tipo Crédito
    $('input[type=radio][name=venta_tipo_documento' + Documento + '][value="CRE"]').attr('disabled', false);
    //Se selecciona por defecto el cliente a Contado.
    $("#ventas_documento_con" + Documento).prop("checked", true);
}

/**
 * Método para cargar la data cargada en el formulario.
 * @return {[type]} [description]
 */
var consultarFormVenta = function() {

    var venta = {
        'codigo_factura': $("#venta_nro_documento" + Documento).val(),
        'codigo_orden': $("#venta_compra" + Documento).val(),
        'tipo_documento': $('input[name=venta_tipo_documento' + Documento + ']:checked').val(),
        'fecha_hora': $("#venta_fecha" + Documento).val(),
        'exonerada': $("#venta_exento" + Documento).is(':checked') ? "S" : "N",
        'subtotal': $("#venta_table_subtotal" + Documento).val(),
        // 'itbms': "12",
        'exento': $("#venta_table_exento" + Documento).val(),
        'contra_entrega': $("#venta_pce" + Documento).is(':checked') ? "S" : "N",
        'total': $("#venta_table_total" + Documento).val(),
        'impuesto': $("#venta_table_impuesto" + Documento).val(),
        'comentario': $("#venta_comentario" + Documento).val(),
        'estado': "P", //Prefacturada
        'id_ventaNC': null,
        'cliente_id': ventas_clienteSelected[Documento][0]["id"],
        'vendedor_id': $("#ventas_vendedorSelected" + Documento).val(),
        'comisionista_id': $("#ventas_comisionistaSelected" + Documento).val(),
        'empresa_id': $("#empresa_id").val(),
        'deposito_id': $("#deposito_id").val(),
        'id': 0,
    }

    return venta;
}

/**
 * Método para inicializar el formulario de ventas
 * @return {[type]} [description]
 */
var ventas_limpiarFormulario = function(_boolean) {
    //Quitar descuentos
    ventas_quitarDescuentos(false);

    //Si se limpia el formulario se pierde la selección de la cotización o proforma seleccionada.
    ventas_CotProTipoSelected = "";
    ventas_CotProSelected = 0;

    //Formulario del cliente
    // $("#venta_cliente" + Documento).val("");
    $("#venta_telefono" + Documento).val("");
    $("#venta_puntos" + Documento).val("");
    $("#venta_direccion" + Documento).val("");

    if (_boolean == true) {
        //se setea el cliente frecuente
        //Cargar cliente por defecto.
        ventas_cargarClienteFrecuente();

        //Limpio (inicializo) el array donde llevo los productos seleccionados
        rowSelectedFact[Documento] = new Array();
    }

    //Formualrio de documento
    //Se selecciona por defecto el cliente a Contado.
    $("#ventas_documento_con" + Documento).prop("checked", true);
    $("#venta_nro_documento" + Documento).val("");
    $("#venta_fecha" + Documento).val("");
    $("#venta_vendedor" + Documento).val("");
    $("#venta_comisionista" + Documento).val("");
    $("#venta_compra" + Documento).val("");
    $("#venta_exento" + Documento).prop('checked', false);
    $("#venta_pce" + Documento).prop('checked', false);

    //Formulario para agregar a la venta
    $("#venta_codigo" + Documento).val("");
    $("#venta_cantidad" + Documento).val("");
    $("#venta_precio" + Documento).val("");
    $("#venta_total" + Documento).val("");
    $("#venta_descripcion" + Documento).val("");

    //Inicializo el array del detalle de la venta
    ventas_fact[Documento] = new Array(); //nro,id,codigo,descripcion, IMG?, cantidad, precio, total

    //Se deshabilita el botón de "articulo promocional" debido a que se pierden las selecciones de los artículos
    $("#ventas_promocional" + Documento).prop('disabled', true);

    //Se hace una copia del array que lleva las ventas, para mantenerlo original
    ventas_factOriginal[Documento] = $.extend(true, [], ventas_fact[Documento]);

    //Actualizando la tabla de ventas
    actualizarVentas_FactDataTable();

    //Cargando la fecha actual en el campo fecha
    cargarFechaActual();

    //Cargando autoComplete de productos activos
    Ventas_ProductosActivos();

    //Cargar vendedor (usuario actual)
    cargarVendedorActual();

    //Ocultando mensaje de error del detalle de factura
    $("#msjErrorFact" + Documento).hide();

    //Ocultando mensaje de Ok del detalle de factura
    $("#msjOkFact" + Documento).hide();

    //Motivado a los cambios realizados para poder guardar una nota de crédito (Devolución) es necesario realizar las siguientes acciones.

    //Desbloquear los datos del cliente
    $("#ventas_fieldsetCliente" + Documento).prop('disabled', false);

    //Desbloquear los datos del documento
    $("#ventas_fieldsetDoc" + Documento).prop('disabled', false);

    //Desbloquear los datos del tipo de documento
    $("#ventas_fieldsetTipoDoc" + Documento).prop('disabled', false);

    //Habilitar el botón de consulta de documentos.
    $("#ventas_BtnDocs" + Documento).removeAttr('disabled');

    //Ocultar el form de devoluciones
    $("#ventas_agregarProductosDevolucion" + Documento).hide();

    //Ocultar el registro de NC
    $("#ventas_registroAcccionesDev" + Documento).hide();

    //Quitar la tabla de devoluciones
    $("#ventas_tablaVentaDev" + Documento).hide();

    //Mostrar el form de ventas
    $("#ventas_agregarProductos" + Documento).show();

    //Mostrar la tabla de ventas.
    $("#ventas_tablaVenta" + Documento).show();

    //Mostrar el registro de ventas
    $("#ventas_registroAccciones" + Documento).show();

    //Limpiar datos de notas de crédito.
    ventas_limpiarDataNC();
}

/**
 * Método para eliminar los descuentos previamente calculados.
 * @param  {[type]} _boolean [true: motrar mensaje, false: no muestra mensaje]
 * @return {[type]}          [description]
 */
function ventas_quitarDescuentos(_boolean) {

    //Se inicializa el array que lleva el control de la venta, sobre el array original
    ventas_fact[Documento] = $.extend(true, [], ventas_factOriginal[Documento]);

    //Habilito la edición del código del producto
    $("#venta_codigo" + Documento).prop('disabled', false);

    //Se deshabilita el botón de quitar
    $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

    //Limpio el formulario
    limpiarFormularioFactProducto();

    //Habilitando la edición del formulario de ingreso de articulos a la venta
    $("#ventas_fieldset" + Documento).prop('disabled', false);

    //Se actualiza la tabla
    actualizarVentas_FactDataTable();

    //limpiar el inventario
    limpiarInventario();

    //Se limpia a lista de precios
    limpiarListaPrecios();

    //Se limpiar el listado de los 2 últimos precios de venta
    limpiarUltimos2Precios();

    //Se deshabilita el botón de "articulo promocional" debido a que se pierden las selecciones de los artículos
    $("#ventas_promocional" + Documento).prop('disabled', true);

    if (_boolean == true) {

        //Limpio (inicializo) el array donde llevo los productos seleccionados
        rowSelectedFact[Documento] = new Array();

        //Muestro mensaje de éxito
        var quitarDescuentos = '{"message": "Los descuentos han sido eliminados correctamente"}';
        successMsj(JSON.parse(quitarDescuentos), 'msjOkFact' + Documento, 'formOkFact' + Documento);
    }

    //recalcular..
    ventas_VentaExenta(true);
}

/**
 * Método para eliminar los descuentos previamente calculados a los artículos seleccionados
 * @param  {[type]} _boolean [true: motrar mensaje, false: no muestra mensaje]
 * @return {[type]}          [description]
 */
function ventas_quitarDescuentosArtSelected(_boolean) {

    //Recorro el array de productos seleccionados
    $(rowSelectedFact[Documento]).each(function(indice, valor) {
        //Busco los elementos que coinciden en el listado total de la venta, para modificarlos
        $(ventas_fact[Documento]).each(function(indice2, valor2) {
            if (valor2.id == valor.id && valor2.pos == valor.pos) { //Mismo id del producto, actualizar data.
                ventas_fact[Documento][indice2]["descripcion"] = ventas_factOriginal[Documento][indice2]["descripcion"];
                ventas_fact[Documento][indice2]["precio"] = ventas_factOriginal[Documento][indice2]["precio"];
                ventas_fact[Documento][indice2]["itbms"] = ventas_factOriginal[Documento][indice2]["itbms"];
                ventas_fact[Documento][indice2]["impuesto"] = ventas_factOriginal[Documento][indice2]["impuesto"];
                ventas_fact[Documento][indice2]["subtotal"] = ventas_factOriginal[Documento][indice2]["subtotal"];
                ventas_fact[Documento][indice2]["total"] = ventas_factOriginal[Documento][indice2]["total"];
                ventas_fact[Documento][indice2]["exento"] = ventas_factOriginal[Documento][indice2]["exento"];
                ventas_fact[Documento][indice2]["porc_descuento"] = ventas_factOriginal[Documento][indice2]["porc_descuento"];
                ventas_fact[Documento][indice2]["monto_descuento"] = ventas_factOriginal[Documento][indice2]["monto_descuento"];
            }
        });
    });

    //Habilito la edición del código del producto
    $("#venta_codigo" + Documento).prop('disabled', false);

    //Se deshabilita el botón de quitar
    $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

    //Limpio el formulario
    limpiarFormularioFactProducto();

    //Habilitando la edición del formulario de ingreso de articulos a la venta
    $("#ventas_fieldset" + Documento).prop('disabled', false);

    //Se actualiza la tabla
    actualizarVentas_FactDataTable();

    //limpiar el inventario
    limpiarInventario();

    //Se limpia a lista de precios
    limpiarListaPrecios();

    //Se limpiar el listado de los 2 últimos precios de venta
    limpiarUltimos2Precios();

    //Se deshabilita el botón de "articulo promocional" debido a que se pierden las selecciones de los artículos
    $("#ventas_promocional" + Documento).prop('disabled', true);

    if (_boolean == true) {

        //Limpio (inicializo) el array donde llevo los productos seleccionados
        rowSelectedFact[Documento] = new Array();

        //Muestro mensaje de éxito
        var quitarDescuentos = '{"message": "Los descuentos han sido eliminados correctamente"}';
        successMsj(JSON.parse(quitarDescuentos), 'msjOkFact' + Documento, 'formOkFact' + Documento);
    }

    //recalcular..
    ventas_VentaExenta(false);
}

/**
 * Método para aplicar el 10$ de descuento a todo lo que se encuentra en la venta.
 * @return {[type]}              [description]
 */
$("#venta_10_descuento").click(function(event) {

    //Limpio el campo de texto de descuento
    $("#venta_otro_descuentoText").val("");

    //Cierro el modal
    $('#ventas_descuentoFlechaModal').modal('hide');

    //Tomo como base el array original.
    ventas_quitarDescuentos(false);

    //Aplicando descuentos a la factura de venta
    ventas_aplicarDescuentos(10, true);

    //Se actualiza la tabla
    actualizarVentas_FactDataTable();

    //Limpio (inicializo) el array donde llevo los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Ocultando el posible mensaje de error de descuento
    $("#msjErrorVenta_flechaDescuento").hide();

    //Muestro mensaje de éxito
    var descuento = '{"message": "Descuento del 10% aplicado satisfactoriamente."}';
    successMsj(JSON.parse(descuento), 'msjOkFact' + Documento, 'formOkFact' + Documento);

});

/**
 * Método para aplicar el 10$ de descuento a todo lo que se encuentra en la venta.
 * @return {[type]}              [description]
 */
$("#venta_15_descuento").click(function(event) {

    //Limpio el campo de texto de descuento
    $("#venta_otro_descuentoText").val("");

    //Cierro el modal
    $('#ventas_descuentoFlechaModal').modal('hide');

    //Tomo como base el array original.
    ventas_quitarDescuentos(false);

    //Aplicando descuentos a la factura de venta
    ventas_aplicarDescuentos(15, true);

    //Se actualiza la tabla
    actualizarVentas_FactDataTable();

    //Limpio (inicializo) el array donde llevo los productos seleccionados
    rowSelectedFact[Documento] = new Array();

    //Ocultando el posible mensaje de error de descuento
    $("#msjErrorVenta_flechaDescuento").hide();

    //Muestro mensaje de éxito
    var descuento = '{"message": "Descuento del 15% aplicado satisfactoriamente."}';
    successMsj(JSON.parse(descuento), 'msjOkFact' + Documento, 'formOkFact' + Documento);

});

/**
 * Método para aplicar el 10$ de descuento a todo lo que se encuentra en la venta.
 * @return {[type]}              [description]
 */
$("#venta_otro_descuento").click(function(event) {

    //Valido que cantidad sea numérica.
    var cantidad = $("#venta_otro_descuentoText").val();

    if (isNaN(cantidad) || cantidad == "") {
        var errorDescuentos = '{"message": "El valor del Descuento debe ser numérico"}';
        errorMsj(JSON.parse(errorDescuentos), 'msjErrorVenta_flechaDescuento', 'formErrorVenta_flechaDescuento');
        error = true;
    } else {

        //Obtengo el valor del campo de texto
        var _descuento = $("#venta_otro_descuentoText").val();

        //Limpio el campo de texto de descuento
        $("#venta_otro_descuentoText").val("");

        //Cierro el modal
        $('#ventas_descuentoFlechaModal').modal('hide');

        //Tomo como base el array original.
        ventas_quitarDescuentos(false);

        //Aplicando descuentos a la factura de venta
        ventas_aplicarDescuentos(_descuento, true);

        //Se actualiza la tabla
        actualizarVentas_FactDataTable();

        //Limpio (inicializo) el array donde llevo los productos seleccionados
        rowSelectedFact[Documento] = new Array();

        //Ocultando el posible mensaje de error de descuento
        $("#msjErrorVenta_flechaDescuento").hide();

        //Muestro mensaje de éxito
        var descuento = { "message": "Descuento del " + _descuento + "% aplicado satisfactoriamente." };
        successMsj(descuento, 'msjOkFact' + Documento, 'formOkFact' + Documento);
    }
});

/**
 * Método para aplicar descuentos, donde _descuento es el descuento a aplicar.
 * 
 * @param  {[type]} _descuento [Valor del descuento que se desea aplicar]
 * @param  {[type]} _descripcion [true: agregar el Desc en la descipción del producto. False: no agrega nada a la descripción.]
 * @return {[type]}            [description]
 */
var ventas_aplicarDescuentos = function(_descuento, _descripcion) {

    //Se realiza un ciclo por todos los productos que hay en la factura para aplicarle el descuento a estos
    $(ventas_fact[Documento]).each(function(indice, valor) {

        $nuevoTotal = 0;
        $nuevoPrecio = 0;
        $nuevoImpuesto = 0;
        $nuevoSubTotal = 0;
        $nuevoExento = 0;

        //Se recalculan los valores de precio, impuesto, total, subtotal etc posterior al descuento "_descuento"
        $nuevoTotal = parseFloat(valor.total - (valor.total * (_descuento / 100))).toFixed(2);
        $nuevoPrecio = parseFloat($nuevoTotal / valor.cantidad / (1 + (valor.itbms / 100))).toFixed(2);
        $nuevoImpuesto = parseFloat((valor.cantidad * $nuevoPrecio) * (valor.itbms / 100)).toFixed(2);

        if (valor.itbms == 0) {
            $nuevoSubTotal = $nuevoTotal;
            $nuevoExento = $nuevoTotal;
        } else {
            $nuevoSubTotal = parseFloat(valor.cantidad * $nuevoPrecio).toFixed(2);
        }

        if (_descripcion == true) {
            valor.descripcion = valor.descripcion + " ***DESCUENTO: " + _descuento + "% ***"
        }

        valor.porc_descuento = _descuento;
        valor.monto_descuento = parseFloat(valor.total * (_descuento / 100)).toFixed(2);
        valor.exento = $nuevoExento;
        valor.precio = $nuevoPrecio;
        valor.impuesto = $nuevoImpuesto;
        valor.subtotal = $nuevoSubTotal;
        valor.total = $nuevoTotal;
    });
}

/**
 * Método para aplicar el descuento a todo lo que se encuentra en la venta.
 * @return {[type]}              [description]
 */
$("#venta_otroDescuento_GlobalBtn").click(function(event) {

    //Obtengo el valor del campo del formulario
    var cantidad = $("#venta_otroDescuento_Global").val();

    //Valido que cantidad sea numérica.
    if (isNaN(cantidad) || cantidad == "") {
        var errorDescuentos = '{"message": "El valor del Descuento debe ser numérico"}';
        errorMsj(JSON.parse(errorDescuentos), 'msjErrorVenta_otroDescuento', 'formErrorVenta_otroDescuento');
        error = true;
    } else {

        //Cierro el modal
        $('#ventas_otrosDescuentosModal').modal('hide');

        //Tomo como base el array original.
        ventas_quitarDescuentos(false);

        //Aplicando descuentos a la factura de venta
        ventas_aplicarDescuentos(cantidad, !$("#venta_otroDescuento_Descripcion").is(':checked'));

        //Se actualiza la tabla
        actualizarVentas_FactDataTable();

        //Limpio (inicializo) el array donde llevo los productos seleccionados
        rowSelectedFact[Documento] = new Array();

        //Limpio todos los campos de texto de descuento
        $("#venta_otroDescuento_Global").val("");
        $("#venta_otroDescuento_Seleccionados").val("");
        $("#venta_otroDescuento_Total").val("");
        //Añadir el descuento en la descripción del producto
        $("#venta_otroDescuento_Descripcion" + Documento).prop('checked', false);

        //Ocultando el posible mensaje de error de descuento
        $("#msjErrorVenta_otroDescuento").hide();

        //Muestro mensaje de éxito
        var descuento = { "message": "Descuento Global del " + cantidad + "% aplicado satisfactoriamente." };
        successMsj(descuento, 'msjOkFact' + Documento, 'formOkFact' + Documento);
    }
});

/**
 * Método para aplicar el descuento a los artículos seleccionados
 * @return {[type]}              [description]
 */
$("#venta_otroDescuento_SeleccionadosBtn").click(function(event) {

    //Obtengo el valor del campo del formulario
    var cantidad = $("#venta_otroDescuento_Seleccionados").val();

    //Valido que cantidad sea numérica.
    if (isNaN(cantidad) || cantidad == "") {
        var errorDescuentos = '{"message": "El valor del Descuento debe ser numérico"}';
        errorMsj(JSON.parse(errorDescuentos), 'msjErrorVenta_otroDescuento', 'formErrorVenta_otroDescuento');
        error = true;
    } else if (rowSelectedFact[Documento].length < 1) { //No hay elementos seleccionados de la venta.
        var errorDescuentos = '{"message": "Debe seleccionar artículos existentes en la venta para aplicarles el descuento solicitado."}';
        errorMsj(JSON.parse(errorDescuentos), 'msjErrorVenta_otroDescuento', 'formErrorVenta_otroDescuento');
    } else {

        //Cierro el modal
        $('#ventas_otrosDescuentosModal').modal('hide');

        //Tomo como base el array original de los artículos seleccionados.
        ventas_quitarDescuentosArtSelected(false);

        //Aplicando descuentos a la factura de venta
        ventas_aplicarDescuentosArtSelected(cantidad, rowSelectedFact[Documento]);

        //Se actualiza la tabla
        actualizarVentas_FactDataTable();

        //Limpio todos los campos de texto de descuento
        $("#venta_otroDescuento_Global").val("");
        $("#venta_otroDescuento_Seleccionados").val("");
        $("#venta_otroDescuento_Total").val("");

        //Añadir el descuento en la descripción del producto
        $("#venta_otroDescuento_Descripcion" + Documento).prop('checked', false);

        //Limpio (inicializo) el array donde llevo los productos seleccionados
        rowSelectedFact[Documento] = new Array();

        //Actualizo el dataTable
        actualizarVentas_FactDataTable();

        //limpiar el inventario
        limpiarInventario();

        //Se limpia a lista de precios
        limpiarListaPrecios();

        //Se limpiar el listado de los 2 últimos precios de venta
        limpiarUltimos2Precios();

        //Ocultando el posible mensaje de error de descuento
        $("#msjErrorVenta_otroDescuento").hide();

        //Muestro mensaje de éxito
        var descuento = { "message": "Descuento del " + cantidad + "% aplicado artículos seleccionados." };
        successMsj(descuento, 'msjOkFact' + Documento, 'formOkFact' + Documento);
    }
});

/**
 * Método donde se aplica descuento a los artículos seleccionados de la tabla de ventas
 * @param  {[type]} _descuento     [Descuento a aplicar]
 * @param  {[type]} _arraySelected [Artículos a los que se aplicará el descuento]
 * @return {[type]}                [description]
 */
var ventas_aplicarDescuentosArtSelected = function(_descuento, _arraySelected) {

    //Se buscan todos los campos de impuestos de productos y al ser la venta neta, se hacen 0
    $(_arraySelected).each(function(indice, valor) {

        //Busco los elementos que coinciden en el listado total de la venta, para modificarlos
        $(ventas_fact[Documento]).each(function(indice2, valor2) {
            if (valor2.id == valor.id && valor2.pos == valor.pos) { //Mismi id del producto, actualizar data.
                $nuevoTotal = 0;
                $nuevoPrecio = 0;
                $nuevoImpuesto = 0;
                $nuevoSubTotal = 0;
                $nuevoExento = 0;

                //Se recalculan los valores de precio, impuesto, total, subtotal etc posterior al descuento "_descuento"
                $nuevoTotal = parseFloat(valor2.total - (valor2.total * (_descuento / 100))).toFixed(2);
                $nuevoPrecio = parseFloat($nuevoTotal / valor2.cantidad / (1 + (valor2.itbms / 100))).toFixed(2);
                $nuevoImpuesto = parseFloat((valor2.cantidad * $nuevoPrecio) * (valor2.itbms / 100)).toFixed(2);

                if (valor2.itbms == 0) {
                    $nuevoSubTotal = $nuevoTotal;
                    $nuevoExento = $nuevoTotal;
                } else {
                    $nuevoSubTotal = parseFloat(valor2.cantidad * $nuevoPrecio).toFixed(2);
                }

                valor2.porc_descuento = _descuento;
                valor2.monto_descuento = parseFloat(valor2.total * (_descuento / 100)).toFixed(2);
                valor2.exento = $nuevoExento;
                valor2.precio = $nuevoPrecio;
                valor2.impuesto = $nuevoImpuesto;
                valor2.subtotal = $nuevoSubTotal;
                valor2.total = $nuevoTotal;
                valor2.descripcion = valor2.descripcion + " ***DESCUENTO: " + _descuento + "% ***";
            }
        });

    });
}

/**
 * Método para aplicar el descuento a los artículos seleccionados, haciendo que el total de la factura se aproxime a un monto en específico.
 * @return {[type]}              [description]
 */
$("#venta_otroDescuento_TotalBtn").click(function(event) {

    //Obtengo el valor del campo del formulario
    var totalNuevo = $("#venta_otroDescuento_Total").val();

    //Total de la venta
    var totalVenta = 0;

    //Valido que cantidad sea numérica.
    if (isNaN(totalNuevo) || totalNuevo == "") {
        var errorDescuentos = '{"message": "El valor del Total debe ser numérico"}';
        errorMsj(JSON.parse(errorDescuentos), 'msjErrorVenta_otroDescuento', 'formErrorVenta_otroDescuento');
        error = true;
    } else {

        //Tomo como base el array original.
        ventas_quitarDescuentos(false);

        $(ventas_fact[Documento]).each(function(indice, valor) {
            totalVenta = parseFloat(totalVenta) + parseFloat(valor.total);
        });

        //Calculo en cuanto % debe disminuir el total para llegar al nuevo total.
        var descuento = parseFloat((totalVenta - totalNuevo) / totalVenta).toFixed(4);
        descuento = parseFloat(descuento * 100).toFixed(4);

        //Cierro el modal
        $('#ventas_otrosDescuentosModal').modal('hide');

        //Aplicando descuentos a la factura de venta
        ventas_aplicarDescuentos(descuento, true);

        //Limpio todos los campos de texto de descuento
        $("#venta_otroDescuento_Global").val("");
        $("#venta_otroDescuento_Seleccionados").val("");
        $("#venta_otroDescuento_Total").val("");

        //Check deseleccionado
        $("#venta_otroDescuento_Descripcion" + Documento).prop('checked', false);

        //Limpio (inicializo) el array donde llevo los productos seleccionados
        rowSelectedFact[Documento] = new Array();

        //Actualizo el dataTable
        actualizarVentas_FactDataTable();

        //limpiar el inventario
        limpiarInventario();

        //Se limpia a lista de precios
        limpiarListaPrecios();

        //Se limpiar el listado de los 2 últimos precios de venta
        limpiarUltimos2Precios();

        //Ocultando el posible mensaje de error de descuento
        $("#msjErrorVenta_otroDescuento").hide();

        //Muestro mensaje de éxito
        var descuentoMsj = { "message": "Descuento por aproximación de total aplicado correctamente." };
        successMsj(descuentoMsj, 'msjOkFact' + Documento, 'formOkFact' + Documento);
    }
});

var ventas_FacturaSelected = 0; //Variable para llevar el control de la factura seleccionada de la vista.
var ventas_ConsultarFacturasTable; //variable para llevar el control de la tabla de consultar Facturas
/**
 * Método para cargar el dataTable de ventas facturadas con estatus F.
 * @return {[type]} [description]
 */
var ventas_ConsultarFacturas = function() {

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $("#deposito_id").val();

    ventas_ConsultarFacturasTable = $('#venta_table_consultarFactura').DataTable({
        "destroy": true,
        "info": false,
        "processing": true,
        "serverSide": true,
        "lengthChange": false,
        "pageLength": 3,
        "autoWidth": false,
        "paging": true,
        "scrollY": "90px",
        "ajax": "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/ventasFacturadas",
        "columns": [
            { data: 'tipo_documento', name: 'tipo_documento', orderable: true, searchable: true },
            { data: 'codigo_factura', name: 'codigo_factura', orderable: true, searchable: true },
            { data: 'fecha_hora', name: 'fecha_hora', orderable: true, searchable: true },
            { data: 'vendedor_id', name: 'users.name', orderable: true, searchable: true },
            { data: 'cliente_id', name: 'clientes.primer_nombre', orderable: true, searchable: true },
            { data: 'total', name: 'total', orderable: true, searchable: true },
            { data: 'venta_id', name: 'venta_id', orderable: false, searchable: false, visible: false },
        ],
        "columnDefs": [
            { className: "text-right", "targets": [5] }, //Total alineado a la derecha.
            { width: '10%', "targets": 0 },
            { width: '10%', "targets": 1 },
            { width: '10%', "targets": 2 },
            { width: '30%', "targets": 3 },
            { width: '30%', "targets": 4 },
            { width: '10%', "targets": 5 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
        },
    });


    $('#venta_table_consultarFactura tbody').on('click', 'tr', function() {

        var data = ventas_ConsultarFacturasTable.row(this).data(); //Obtengo la información de la fila seleccionada

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');

            //Se deselecciona la fila, por ende el detalle pasa a ser vacío
            ventas_ConsultarDetallleFactura(-1);

            //Deshabilitar los botones del modal, solo se activan cuando hay una factura seleccionada.
            $("#ventas_reimprimirFacturaModal").prop('disabled', true);
            $("#ventas_anularFacturaModal").prop('disabled', true);
            $("#ventas_modificarFacturaModal").prop('disabled', true);
            $("#ventas_nuevoDocumentoFacturaModal").prop('disabled', true);

            //Setenado la factura seleccionada como 0 (no hay factura seleccionada)
            ventas_FacturaSelected = 0;
        } else {
            ventas_ConsultarFacturasTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');

            //Consultar el detalle de la factura.
            ventas_ConsultarDetallleFactura(data.venta_id);

            //Habilitar los botones del modal, existe una venta seleccionada
            $("#ventas_reimprimirFacturaModal").prop('disabled', false);
            $("#ventas_anularFacturaModal").prop('disabled', false);
            //Solo se activan los botones de Nuevo y Modificar si se trata de una factura de venta. (Que no sea Nota de Crédito)
            if (data.tipo_documento != "Nota C.") {
                $("#ventas_modificarFacturaModal").prop('disabled', false);
                $("#ventas_nuevoDocumentoFacturaModal").prop('disabled', false);
            } else {
                $("#ventas_modificarFacturaModal").prop('disabled', true);
                $("#ventas_nuevoDocumentoFacturaModal").prop('disabled', true);
            }

            //Setenado la factura seleccionada (Id de la factura)
            ventas_FacturaSelected = data.venta_id;
        }
    });
}

var ventas_ConsultarDetallleFacturaTable; //variable para llevar el control de la tabla de consultar Facturas
/**
 * Método para cargar el dataTable de ventas facturadas con estatus F.
 * @return {[type]} [description]
 */
var ventas_ConsultarDetallleFactura = function(_venta_id) {

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $("#deposito_id").val();

    ventas_ConsultarDetallleFacturaTable = $('#venta_table_consultarFacturaDetalle').DataTable({
        "destroy": true,
        "info": false,
        "processing": true,
        "serverSide": true,
        "lengthChange": false,
        "autoWidth": false,
        "paging": false,
        "scrollY": "90px",
        // "scrollCollapse": true,
        "ajax": "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + _venta_id + "/consultarDetalle",
        "columns": [
            { data: 'codigo', name: 'productos.codigo' },
            { data: 'descripcion', name: 'descripcion' },
            { data: 'cantidad', name: 'cantidad' },
            { data: 'precio', name: 'precio' },
            { data: 'subtotal', name: 'subtotal' },
            { data: 'id_detalle', name: 'id_detalle', orderable: false, searchable: false, visible: false },
        ],
        "columnDefs": [
            { className: "text-right", "targets": [4] }, //Total alineado a la derecha.
            { width: '10%', "targets": 0 },
            { width: '60%', "targets": 1 },
            { width: '10%', "targets": 2 },
            { width: '10%', "targets": 3 },
            { width: '8%', "targets": 4 },

        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
        },
    });
}

/**
 * Método para vaciar la tabla de Facturas de ventas una vez cerrada la ventana.
 * @return {[type]} [description]
 */
var ventas_cerrarConsultaDocumentos = function() {
    //Elimino el contenido de la tabla
    $('#venta_table_consultarFactura').empty();

    //Se oculta el mensaje de error
    $("#msjErrorVenta_ConsultarFactura").hide();

    //Se oculta el mensaje de éxito
    $("#msjOkVenta_ConsultarFactura").hide();
}

/**
 * Método que al hacer click en el botón ventas_reimprimirFacturaModal ejecuta la acción de reimprimir la factura seleccionada.
 * @param  {[type]} event) {               } [description]
 * @return {[type]}        [description]
 */
$("#ventas_reimprimirFacturaModal").click(function(event) {
    //Cierro la ventana modal de facturas.
    $('#ventas_ConsultarFacturaModal').modal('hide');

    ventas_reimprimirFactura(ventas_FacturaSelected);
});

/**
 * Método que al hacer click en el botón ventas_modificarFacturaModal ejecuta la acción de anular una factura y cargar todos los valores en la vista principal 
 * @param  {[type]} event) {               } [description]
 * @return {[type]}        [description]
 */
$("#ventas_modificarFacturaModal").click(function(event) {
    //Cierro la ventana modal de facturas.
    $('#ventas_ConsultarFacturaModal').modal('hide');

    //Se limpia el formulario de venta
    $.when(ventas_limpiarFormulario(true)).done(function() {

        //seteo los valores al documento.
        //Hago el llamado del método que puede consultar facturas, anulando la misma
        ventas_nuevoDocumento(ventas_FacturaSelected, true);
    });
});

/**
 * Método que al hacer click en el botón ventas_nuevoDocumentoFacturaModal lleva todos los valores a la vista principal (hace un copia de la factura seleccionada)
 * @param  {[type]} event) {               } [description]
 * @return {[type]}        [description]
 */
$("#ventas_nuevoDocumentoFacturaModal").click(function(event) {

    //Cierro la ventana modal de facturas.
    $('#ventas_ConsultarFacturaModal').modal('hide');

    //Se limpia el formulario de venta
    $.when(ventas_limpiarFormulario(true)).done(function() {

        //seteo los valores al documento.
        //Hago el llamado del método que puede consultar facturas, sin anular la misma
        ventas_nuevoDocumento(ventas_FacturaSelected, false);
    });

});
/**
 * Método que al hacer click en el botón ventas_anularFacturaModal anula la factura seleccionada.
 * @param  {[type]} event) {               } [description]
 * @return {[type]}        [description]
 */
$("#ventas_anularFacturaModal").click(function(event) {

    //Se hace un llamado del método que se encarga de anular un documento
    $.when(ventas_anularDocumento(ventas_FacturaSelected, "Ven")).done(function() {
        //Posterior a su ejecución se realizan las siguientes acciones.

        //Se actualiza la tabla de facturas.
        //Cargar todas las facturas con estatus "F" (Facturadas) ordenadas por fecha, de la más reciente a la más antigua.
        $('#venta_table_consultarFactura').empty();
        ventas_ConsultarFacturas();

        //Deshabilitar los botones del modal, solo se activan cuando hay una factura seleccionada.
        $("#ventas_reimprimirFacturaModal").prop('disabled', true);
        $("#ventas_anularFacturaModal").prop('disabled', true);
        $("#ventas_modificarFacturaModal").prop('disabled', true);
        $("#ventas_nuevoDocumentoFacturaModal").prop('disabled', true);

        //Limpiar el detalle de la factura
        ventas_ConsultarDetallleFactura(-1);
    });
});

/**
 * Método para anular un documento
 * @return {[type]} [description]
 */
var ventas_anularDocumento = function(_venta_id, _modal) {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + _venta_id + "/anularDocumento";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'PUT',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            nro_documento: _venta_id,
        },
        success: function(obj) {

            if (_modal == "CotPro") {
                //Se oculta el mensaje de error
                $("#msjErrorVenta_ConsultarCotPro").hide();

                //Se muestra el mensaje de éxito
                successMsj(obj, 'msjOkVenta_ConsultarCotPro', 'formOkVenta_ConsultarCotPro');
            } else {
                //Se oculta el mensaje de error
                $("#msjErrorVenta_ConsultarFactura").hide();

                //Se muestra el mensaje de éxito
                successMsj(obj, 'msjOkVenta_ConsultarFactura', 'formOkVenta_ConsultarFactura');
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorVenta_ConsultarFactura', 'formErrorVenta_ConsultarFactura');
        }
    });
    return dfd.promise();
}

/**
 * Método para crear un nuevo documento en base a el seleccionado
 * @return {[type]} [description]
 */
var ventas_nuevoDocumento = function(_venta_id, _anular) {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + _venta_id + "/consultarDocumento/Anular/" + _anular;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            nro_documento: _venta_id,
            anular: _anular,
        },
        success: function(obj) {

            //Limpiamos el formulario
            limpiarFormCliente();

            //limpiar el inventario
            limpiarInventario();

            //Se limpia a lista de precios
            limpiarListaPrecios();

            //Se limpiar el listado de los 2 últimos precios de venta
            limpiarUltimos2Precios();

            //Se limpian lo datos de la tabla y se quitan los mensajes de error
            ventas_cerrarConsultaDocumentos();

            //Cargando la fecha actual en el campo fecha
            cargarFechaActual();

            //Cargando los datos del cliente
            $("#venta_cliente" + Documento).val(obj.venta.cliente);
            $("#venta_telefono" + Documento).val(obj.venta.movil);
            $("#venta_puntos" + Documento).val(obj.venta.puntos);
            $("#venta_direccion" + Documento).val(obj.venta.direccion);

            //Agrego datos a la variable del nuevo cliente seleccionado, precio null ya que por formulario no es posible escoger el tipo de precio
            ventas_clienteSelected[Documento] = new Array();
            ventas_clienteSelected[Documento].push({ id: obj.venta.cliente_id, precio: obj.venta.codigo_precio });

            //Cargando los datos básicos de la venta
            $("#venta_vendedor" + Documento).val(obj.venta.vendedor);
            $("#venta_comisionista" + Documento).val(obj.venta.comisionista);

            //Tipo de documento
            $('input[name=venta_tipo_documento' + Documento + ']').prop('checked', false);

            switch (obj.venta.tipo_documento) {
                case 'CON':
                    $("#ventas_documento_con" + Documento).prop("checked", true);
                    break;
                case 'CRE':
                    $("#ventas_documento_cre" + Documento).prop("checked", true);
                    break;
                case 'COT':
                    $("#ventas_documento_cot" + Documento).prop("checked", true);
                    break;
                case 'PRO':
                    $("#ventas_documento_pro" + Documento).prop("checked", true);
                    break;
                case 'NOT':
                    $("#ventas_documento_not" + Documento).prop("checked", true);
                    break;
            }
            //Se habilita la elección de tipo Crédito
            $('input[type=radio][name=venta_tipo_documento' + Documento + '][value="CRE"]').attr('disabled', false);

            //Validando si el cliente es de contado, de ser así el crédito no es posible realizarlo
            if (obj.venta.tipo_cliente_id == 1) {
                $('input[type=radio][name=venta_tipo_documento' + Documento + '][value="CRE"]').attr('disabled', 'disabled');
            }

            //Exonerado
            $("#venta_exento" + Documento).prop('checked', obj.venta.exonerada == "S" ? true : false);

            //Pago contra entrega
            $("#venta_pce" + Documento).prop('checked', obj.venta.contra_entrega == "S" ? true : false);

            //Cargo el detalle de la venta
            $(obj.detalle).each(function(i, v) { // indice, valor
                //Agregando el elemento a la factura
                ventas_fact[Documento].push({
                    nro: "",
                    id: v.producto_id,
                    codigo: v.codigo,
                    descripcion: v.descripcion,
                    img: "vacio",
                    cantidad: v.cantidad,
                    precio: v.precio,
                    itbms: v.porc_impuesto,
                    impuesto: v.impuesto,
                    subtotal: v.subtotal,
                    total: v.total,
                    id_serie: "",
                    exento: v.exento,
                    porc_descuento: v.porc_descuento,
                    monto_descuento: v.descuento,
                    pos: ventas_fact[Documento].length + 1,
                });
            });

            //Se hace una copia del array que lleva las ventas, para mantenerlo original
            ventas_factOriginal[Documento] = $.extend(true, [], ventas_fact[Documento]);

            //Se actualiza el listado del dataTable de la factura
            actualizarVentas_FactDataTable();

            //Habilito para ingresar datos a la factura
            $("#ventas_fieldset" + Documento).prop('disabled', false);

            //Se oculta el mensaje de error
            $("#msjErrorGral").hide();

            //Se muestra el mensaje de éxito
            successMsj(obj, 'msjOkGral', 'formOkGral');

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
}

/**
 * Método para imprimir la factura de la venta seleccionada.
 * @param  {[type]} _venta_id [description]
 * @return {[type]}           [description]
 */
var ventas_reimprimirFactura = function(_venta_id) {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + _venta_id + "/reimprimir";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            venta_id: _venta_id,
        },
        success: function(obj) {

            //Se limpian lo datos de la tabla y se quitan los mensajes de error
            ventas_cerrarConsultaDocumentos();

            //Descarga del documento
            window.open(obj.pdf);

            //Se oculta el mensaje de error
            $("#msjErrorGral").hide();

            //Se muestra el mensaje de éxito
            successMsj(obj, 'msjOkGral', 'formOkGral');

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
}

/*
 * Variable creada para llevar el control de las ventas exentas.
 */
var ventas_exento = {};
ventas_exento["_1"] = new Array();
ventas_exento["_2"] = new Array();
ventas_exento["_3"] = new Array();
ventas_exento["_4"] = new Array();

/**
 * Método para determinar si la venta es exenta
 */
$('input[type=checkbox][name=venta_exento_1]').change(function() {
    ventas_setearExentos($("#venta_exento_1").is(':checked'));
});

/**
 * Método para determinar si la venta es exenta
 */
$('input[type=checkbox][name=venta_exento_2]').change(function() {
    ventas_setearExentos($("#venta_exento_2").is(':checked'));
});

/**
 * Método para determinar si la venta es exenta
 */
$('input[type=checkbox][name=venta_exento_3]').change(function() {
    ventas_setearExentos($("#venta_exento_3").is(':checked'));
});

/**
 * Método para determinar si la venta es exenta
 */
$('input[type=checkbox][name=venta_exento_4]').change(function() {
    ventas_setearExentos($("#venta_exento_4").is(':checked'));
});

/**
 * Método para setear la variable exento en el documento en que se encuentra el usuario
 * @return {[type]} [description]
 */
var ventas_setearExentos = function(_value) {

    //Inicializo el valor
    ventas_exento[Documento] = new Array();

    //indicando si la venta es exenta o no
    ventas_exento[Documento].push({
        exento: _value,
    });

    //recalcular..
    ventas_VentaExenta(true);
}

/**
 * Método para pasar una venta, a ser de tipo exenta.
 * @param  {[type]} _boolean [true para inicializar array de venta con el original, false para no hacerlo]
 * @return {[type]}          [description]
 */
var ventas_VentaExenta = function(_boolean) {

    //Solo si es true el parametro enviado, se inicializa el array
    if (_boolean == true) {
        /**
         * Se inicializa el array que lleva el control de la venta, sobre el array original, si la venta ya llevaba descuentos, 
         * estos se pierden puesto que no es lo mismo calcular un 50% de descuento a un producto con impuesto, que sin él.
         */
        ventas_fact[Documento] = $.extend(true, [], ventas_factOriginal[Documento]);
    }

    //Si la venta es exenta, recalcular valores.
    if (ventas_exento[Documento][0]["exento"] == true) {

        $(ventas_fact[Documento]).each(function(indice, valor) {
            valor.itbms = 0;
            valor.impuesto = 0;
            valor.total = parseFloat(valor.cantidad * valor.precio).toFixed(2);
            valor.exento = parseFloat(valor.cantidad * valor.precio).toFixed(2);
        });
    }

    //Habilito la edición del código del producto
    $("#venta_codigo" + Documento).prop('disabled', false);

    //Se deshabilita el botón de quitar
    $("#ventas_eliminar_producto" + Documento).prop('disabled', true);

    //Limpio el formulario
    limpiarFormularioFactProducto();

    //Habilitando la edición del formulario de ingreso de articulos a la venta
    $("#ventas_fieldset" + Documento).prop('disabled', false);

    //Actualizo la tabla
    actualizarVentas_FactDataTable();

    //limpiar el inventario
    limpiarInventario();

    //Se limpia a lista de precios
    limpiarListaPrecios();

    //Se limpiar el listado de los 2 últimos precios de venta
    limpiarUltimos2Precios();

    //Se deshabilita el botón de "articulo promocional" debido a que se pierden las selecciones de los artículos
    $("#ventas_promocional" + Documento).prop('disabled', true);
}

/**
 * Variable creada para llevar la estructura de la tabla de ventas-Devoluciones _1_2_3_4
 */
var ventas_factDev = {}; //las variables v_impuesto, v_exento, v_subtotal son los valores que vienen de la venta, los mismo nombre sin la preposición "v_" pertenecen a los montos de la devolucion
ventas_factDev["_1"] = new Array(); //nro,id,codigo,descripcion, cantidad, precio, v_impuesto, v_exento, v_subtotal, impuesto, exento, devolucion, subTotal devol
ventas_factDev["_2"] = new Array(); //nro,id,codigo,descripcion, cantidad, precio, v_impuesto, v_exento, v_subtotal, impuesto, exento, devolucion, subTotal devol
ventas_factDev["_3"] = new Array(); //nro,id,codigo,descripcion, cantidad, precio, v_impuesto, v_exento, v_subtotal, impuesto, exento, devolucion, subTotal devol
ventas_factDev["_4"] = new Array(); //nro,id,codigo,descripcion, cantidad, precio, v_impuesto, v_exento, v_subtotal, impuesto, exento, devolucion, subTotal devol

/**
 * Método que captura el evento de cerrar el modal de nota de crédito.
 * @param  {[type]}          [description]
 * @return {[type]}          [description]
 */
$("#ventas_notaCredito_cerrar").click(function(event) {
    //Se selecciona por defecto el cliente a Contado.
    $("#ventas_documento_con" + Documento).prop("checked", true);
    //Cambiando la cabecera a factura a contado.
    $("#venta_header_table" + Documento).html("Factura a Contado");
    //Limpiando el valor escrito en el text
    $("#venta_nc").val("");
});

/**
 * Método para buscar una factura de venta, con estatus "F" para realizar una devolución.
 * 
 * @param  {[type]} event){                     $("#ventas_documento_con" + Documento).prop("checked", true);} [description]
 * @return {[type]}          [description]
 */
$("#venta_buscarFactura").click(function(event) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var venta_id = $("#venta_nc").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + venta_id + "/consultarDocumentoNC";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            nro_documento: venta_id,
        },
        success: function(obj) {

            if (obj.venta != null) {

                //Limpiamos el formulario
                limpiarFormCliente();

                //limpiar el inventario
                limpiarInventario();

                //Se limpia a lista de precios
                limpiarListaPrecios();

                //Se limpiar el listado de los 2 últimos precios de venta
                limpiarUltimos2Precios();

                //Se limpian lo datos de la tabla y se quitan los mensajes de error
                ventas_cerrarConsultaDocumentos();

                //Cargando la fecha actual en el campo fecha
                cargarFechaActual();

                //Cargando los datos del cliente
                $("#venta_cliente" + Documento).val(obj.venta.cliente);
                $("#venta_telefono" + Documento).val(obj.venta.movil);
                $("#venta_puntos" + Documento).val(obj.venta.puntos);
                $("#venta_direccion" + Documento).val(obj.venta.direccion);

                //Cargando los datos básicos de la venta
                $("#venta_vendedor" + Documento).val(obj.venta.vendedor);
                $("#venta_comisionista" + Documento).val(obj.venta.comisionista);

                //#O. Compra
                $("#venta_compra" + Documento).val(obj.venta.codigo_orden);

                //Exonerado
                $("#venta_exento" + Documento).prop('checked', obj.venta.exonerada == "S" ? true : false);

                //Pago contra entrega
                $("#venta_pce" + Documento).prop('checked', obj.venta.contra_entrega == "S" ? true : false);

                //Cierro el modal
                $("#ventas_notaCreditoModal").modal('hide');

                //Oculto el formulario de venta
                $("#ventas_agregarProductos" + Documento).hide();

                //Oculto la tabla de ventas
                $("#ventas_tablaVenta" + Documento).hide();

                //Oculto el registro de acciones...
                $("#ventas_registroAccciones" + Documento).hide();

                //Muestro acciones de devolución
                $("#ventas_registroAcccionesDev" + Documento).show();

                //Muestro formulario de devolucion
                $("#ventas_agregarProductosDevolucion" + Documento).show();

                //Muetro table de devolución
                $("#ventas_tablaVentaDev" + Documento).show();

                //Cargo el detalle de la venta
                $(obj.detalle).each(function(i, v) { // indice, valor
                    //Agregando el elemento a la factura
                    ventas_factDev[Documento].push({
                        nro: "",
                        id: v.producto_id,
                        codigo: v.codigo,
                        descripcion: v.descripcion,
                        v_cantidad: v.cantidad,
                        precio: v.precio,
                        v_impuesto: v.impuesto,
                        v_exento: v.exento,
                        v_subtotal: v.subtotal,
                        impuesto: 0,
                        exento: 0,
                        subtotal: 0,
                        total: 0,
                        cantidad: 0, //Devolucion
                        porc_descuento: v.porc_descuento,
                        monto_descuento: v.descuento,
                        itbms: v.porc_impuesto,
                        pos: ventas_factDev[Documento].length + 1,
                    });
                });

                //Limpiando el valor escrito en el text
                $("#venta_nc").val("");

                //Se actualiza el listado del dataTable de la factura
                actualizarVentas_FactDevDataTable();

                //Bloqueo los datos del cliente.
                $("#ventas_fieldsetCliente" + Documento).prop('disabled', true);

                //Bloqueo los datos del documento
                $("#ventas_fieldsetDoc" + Documento).prop('disabled', true);

                //Bloqueo Bloqueo tipo de documento
                $("#ventas_fieldsetTipoDoc" + Documento).prop('disabled', true);

                //Bloqueo consulta de documento (disable)
                $("#ventas_BtnDocs" + Documento).attr('disabled', true);

                //Se oculta el mensaje de error
                $("#msjErrorVenta_NC").hide();

                //Guardo en la variable el id de la venta a la que se aplicará la devolución, para posteriormente de ella restar el saldo devuelto.
                //Agarro la varible dependiendo del documento en cuestión.
                ventas_asignarIdVentaNC(obj.venta.id_venta);

                //Se muestra el mensaje de éxito
                successMsj(obj, 'msjOkGral', 'formOkGral');
            } else {
                errorMsj(obj, 'msjErrorVenta_NC', 'formErrorVenta_NC');
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
    return dfd.promise();
});

/**
 * Método para cargar el dataTable de devoluciones
 * @return {[type]} [description]
 */
function cargarVentas_devolucionDataTable() {
    cargarVentas_factDevDataTable_1();
    cargarVentas_factDevDataTable_2();
    cargarVentas_factDevDataTable_3();
    cargarVentas_factDevDataTable_4();
}

/**
 * Método para ocultar la tabla y el formuario de devoluciones.
 * @return {[type]} [description]
 */
function ventas_OcutarDevoluciones() {

    //Ocultando el formulario
    $("#ventas_agregarProductosDevolucion_1").hide();
    $("#ventas_agregarProductosDevolucion_2").hide();
    $("#ventas_agregarProductosDevolucion_3").hide();
    $("#ventas_agregarProductosDevolucion_4").hide();

    //Ocultando las tablas
    $("#ventas_tablaVentaDev_1").hide();
    $("#ventas_tablaVentaDev_2").hide();
    $("#ventas_tablaVentaDev_3").hide();
    $("#ventas_tablaVentaDev_4").hide();

    //Ocultando las acciones de las devoluciones
    $("#ventas_registroAcccionesDev_1").hide();
    $("#ventas_registroAcccionesDev_2").hide();
    $("#ventas_registroAcccionesDev_3").hide();
    $("#ventas_registroAcccionesDev_4").hide();
}

//Variables de la tabla de devoluciones
var ventas_factDevTable_1;
var ventas_factDevTable_2;
var ventas_factDevTable_3;
var ventas_factDevTable_4;

//Variables para llevar el indice del producto seleccionado
var ventas_factDevIndice_1;
var ventas_factDevIndice_2;
var ventas_factDevIndice_3;
var ventas_factDevIndice_4;

//Variables para llevar el indice del producto seleccionado
var ventas_factDevId_1;
var ventas_factDevId_2;
var ventas_factDevId_3;
var ventas_factDevId_4;

/**
 * Método donde se define el dataTable de las devoluciones en Doc _1
 * @return {[type]} [description]
 */
var cargarVentas_factDevDataTable_1 = function() {
    //Inicialización de tabla de facturas en ventas.
    ventas_factDevTable_1 = $('#venta_tableDev_1').DataTable({
        destroy: true,
        data: ventas_factDev["_1"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "v_cantidad" },
            { "data": "precio" },
            { "data": "v_impuesto" },
            { "data": "v_exento" },
            { "data": "v_subtotal" },
            { "data": "impuesto" },
            { "data": "exento" },
            { "data": "cantidad" }, //devolucion
            { "data": "subtotal" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [4, 5, 11, 12] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1, 6, 7, 8, 9, 10], }, //El Id no se muestra, ni el impuesto ni exento

            { width: '2%', "targets": 0 },
            { width: '17%', "targets": 2 },
            { width: '35%', "targets": 3 },
            { width: '10%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '15%', "targets": 11 },
            { width: '10%', "targets": 12 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(12).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(10).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(parseFloat(subtotal) + parseFloat(impuesto)).toFixed(2);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', ventas_factDevTable_1.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', ventas_factDevTable_1.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', ventas_factDevTable_1.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', ventas_factDevTable_1.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exentoDev" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotalDev" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuestoDev" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_totalDev" + Documento).val(total);
        }
    });

    ventas_factDevTable_1.on('order.dt search.dt', function() {
        ventas_factDevTable_1.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección simple.
    $('#venta_tableDev_1 tbody').on('click', 'tr', function() {

        var table = $('#venta_tableDev_1').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');

            //No existe nada seleccionador
            ventas_factDevIndice_1 = -1;

            //Limpio el formulario de devoluciones.
            ventas_limpiarFormularioDevolucion();

        } else {
            //Método cuando se selecciona un elemento de la tabla.
            ventas_factDevTable_1.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = ventas_factDev[Documento].indexOf(data);

            ventas_factDevIndice_1 = indice;

            //Se carga la data de lo seleccionado en el formulario de devolución
            ventas_cargarFormularioDevolucion(indice);

            $("#venta_devolucionDev_1").focus();

            //Se habilita el boton
            $('#ventas_agregarDev' + Documento).removeAttr("disabled");

            $(this).addClass('selected');
        }
    });
}

/**
 * Método donde se define el dataTable de las devoluciones en Doc _2
 * @return {[type]} [description]
 */
var cargarVentas_factDevDataTable_2 = function() {
    //Inicialización de tabla de facturas en ventas.
    ventas_factDevTable_2 = $('#venta_tableDev_2').DataTable({
        destroy: true,
        data: ventas_factDev["_2"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "v_cantidad" },
            { "data": "precio" },
            { "data": "v_impuesto" },
            { "data": "v_exento" },
            { "data": "v_subtotal" },
            { "data": "impuesto" },
            { "data": "exento" },
            { "data": "cantidad" }, //devolucion
            { "data": "subtotal" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [4, 5, 11, 12] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1, 6, 7, 8, 9, 10], }, //El Id no se muestra, ni el impuesto ni exento

            { width: '2%', "targets": 0 },
            { width: '17%', "targets": 2 },
            { width: '35%', "targets": 3 },
            { width: '10%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '15%', "targets": 11 },
            { width: '10%', "targets": 12 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(12).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(10).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(parseFloat(subtotal) + parseFloat(impuesto)).toFixed(2);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', ventas_factDevTable_2.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', ventas_factDevTable_2.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', ventas_factDevTable_2.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', ventas_factDevTable_2.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exentoDev" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotalDev" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuestoDev" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_totalDev" + Documento).val(total);
        }
    });

    ventas_factDevTable_2.on('order.dt search.dt', function() {
        ventas_factDevTable_2.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección simple.
    $('#venta_tableDev_2 tbody').on('click', 'tr', function() {

        var table = $('#venta_tableDev_2').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');

            //No existe nada seleccionador
            ventas_factDevIndice_2 = -1;

            //Limpio el formulario de devoluciones.
            ventas_limpiarFormularioDevolucion();

        } else {
            //Método cuando se selecciona un elemento de la tabla.
            ventas_factDevTable_2.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = ventas_factDev[Documento].indexOf(data);

            ventas_factDevIndice_2 = indice;

            //Se carga la data de lo seleccionado en el formulario de devolución
            ventas_cargarFormularioDevolucion(indice);

            $("#venta_devolucionDev_2").focus();

            //Se habilita el boton
            $('#ventas_agregarDev' + Documento).removeAttr("disabled");

            $(this).addClass('selected');
        }
    });
}

/**
 * Método donde se define el dataTable de las devoluciones en Doc _3
 * @return {[type]} [description]
 */
var cargarVentas_factDevDataTable_3 = function() {
    //Inicialización de tabla de facturas en ventas.
    ventas_factDevTable_3 = $('#venta_tableDev_3').DataTable({
        destroy: true,
        data: ventas_factDev["_3"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "v_cantidad" },
            { "data": "precio" },
            { "data": "v_impuesto" },
            { "data": "v_exento" },
            { "data": "v_subtotal" },
            { "data": "impuesto" },
            { "data": "exento" },
            { "data": "cantidad" }, //devolucion
            { "data": "subtotal" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [4, 5, 11, 12] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1, 6, 7, 8, 9, 10], }, //El Id no se muestra, ni el impuesto ni exento

            { width: '2%', "targets": 0 },
            { width: '17%', "targets": 2 },
            { width: '35%', "targets": 3 },
            { width: '10%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '15%', "targets": 11 },
            { width: '10%', "targets": 12 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(12).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(10).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(parseFloat(subtotal) + parseFloat(impuesto)).toFixed(2);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', ventas_factDevTable_3.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', ventas_factDevTable_3.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', ventas_factDevTable_3.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', ventas_factDevTable_3.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exentoDev" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotalDev" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuestoDev" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_totalDev" + Documento).val(total);
        }
    });

    ventas_factDevTable_3.on('order.dt search.dt', function() {
        ventas_factDevTable_3.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección simple.
    $('#venta_tableDev_3 tbody').on('click', 'tr', function() {

        var table = $('#venta_tableDev_3').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');

            //No existe nada seleccionador
            ventas_factDevIndice_3 = -1;

            //Limpio el formulario de devoluciones.
            ventas_limpiarFormularioDevolucion();

        } else {
            //Método cuando se selecciona un elemento de la tabla.
            ventas_factDevTable_3.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = ventas_factDev[Documento].indexOf(data);

            ventas_factDevIndice_3 = indice;

            //Se carga la data de lo seleccionado en el formulario de devolución
            ventas_cargarFormularioDevolucion(indice);

            $("#venta_devolucionDev_3").focus();

            //Se habilita el boton
            $('#ventas_agregarDev' + Documento).removeAttr("disabled");

            $(this).addClass('selected');
        }
    });
}

/**
 * Método donde se define el dataTable de las devoluciones en Doc _4
 * @return {[type]} [description]
 */
var cargarVentas_factDevDataTable_4 = function() {
    //Inicialización de tabla de facturas en ventas.
    ventas_factDevTable_4 = $('#venta_tableDev_4').DataTable({
        destroy: true,
        data: ventas_factDev["_4"],
        autoWidth: false,
        columns: [
            { "data": "nro" },
            { "data": "id" },
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "v_cantidad" },
            { "data": "precio" },
            { "data": "v_impuesto" },
            { "data": "v_exento" },
            { "data": "v_subtotal" },
            { "data": "impuesto" },
            { "data": "exento" },
            { "data": "cantidad" }, //devolucion
            { "data": "subtotal" }
        ],
        columnDefs: [
            { className: "text-right", "targets": [4, 5, 11, 12] }, //Cantidad, Precio y Total alineados a la izquieda.
            { "searchable": false, "orderable": false, "targets": [0], }, //El campo que lleva la cuenta no se ordena y no se hace búsqueda
            { "visible": false, "searchable": false, "orderable": false, "targets": [1, 6, 7, 8, 9, 10], }, //El Id no se muestra, ni el impuesto ni exento

            { width: '2%', "targets": 0 },
            { width: '17%', "targets": 2 },
            { width: '35%', "targets": 3 },
            { width: '10%', "targets": 4 },
            { width: '10%', "targets": 5 },
            { width: '15%', "targets": 11 },
            { width: '10%', "targets": 12 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "paging": false,
        "info": false,
        "footerCallback": function(row, data, start, end, display) {
            var api = this.api(),
                data;

            //Calculando el subtotal. (aqui va lo exento y no exento)
            subtotal = api.column(12).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando el impuesto (el acumulado en la factura)
            impuesto = api.column(9).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Calculando exento (el acumulado en la factura)
            exento = api.column(10).data().reduce(function(a, b) {
                return (parseFloat((parseFloat(a) + parseFloat(b))).toFixed(2));
            }, 0);

            //Total de venta
            total = parseFloat(parseFloat(subtotal) + parseFloat(impuesto)).toFixed(2);

            //Se setean los valores al th del footer
            $('tr:eq(0) th:eq(1)', ventas_factDevTable_4.table().footer()).html(parseFloat(exento).toFixed(2));
            $('tr:eq(1) th:eq(1)', ventas_factDevTable_4.table().footer()).html(parseFloat(subtotal).toFixed(2));
            $('tr:eq(2) th:eq(1)', ventas_factDevTable_4.table().footer()).html(parseFloat(impuesto).toFixed(2));
            $('tr:eq(3) th:eq(1)', ventas_factDevTable_4.table().footer()).html(parseFloat(total).toFixed(2));

            //Se asigna el valor del exento a un campo
            $("#venta_table_exentoDev" + Documento).val(exento);

            //Se asigna el valor del subtotal a un campo
            $("#venta_table_subtotalDev" + Documento).val(subtotal);

            //Se asigna el valor del impuesto a un campo
            $("#venta_table_impuestoDev" + Documento).val(impuesto);

            //Se asigna el valor del total a un campo
            $("#venta_table_totalDev" + Documento).val(total);
        }
    });

    ventas_factDevTable_4.on('order.dt search.dt', function() {
        ventas_factDevTable_4.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Gestionando la selección simple.
    $('#venta_tableDev_4 tbody').on('click', 'tr', function() {

        var table = $('#venta_tableDev_4').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');

            //No existe nada seleccionador
            ventas_factDevIndice_4 = -1;

            //Limpio el formulario de devoluciones.
            ventas_limpiarFormularioDevolucion();

        } else {
            //Método cuando se selecciona un elemento de la tabla.
            ventas_factDevTable_4.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = ventas_factDev[Documento].indexOf(data);

            ventas_factDevIndice_4 = indice;

            //Se carga la data de lo seleccionado en el formulario de devolución
            ventas_cargarFormularioDevolucion(indice);

            $("#venta_devolucionDev_4").focus();

            //Se habilita el boton
            $('#ventas_agregarDev' + Documento).removeAttr("disabled");

            $(this).addClass('selected');
        }
    });
}


/**
 * Método para actualizar el DataTable de Factura
 * @return {[type]} [description]
 */
var actualizarVentas_FactDevDataTable = function() {
    $('#venta_tableDev' + Documento).dataTable().fnClearTable();
    if (ventas_factDev[Documento].length > 0) {
        $('#venta_tableDev' + Documento).dataTable().fnAddData(ventas_factDev[Documento]);
    }
}

/**
 * Método para cargar los datos en el formulario de devoluciones.
 * 
 * @param  {[type]} _indice [description]
 * @return {[type]}         [description]
 */
var ventas_cargarFormularioDevolucion = function(_indice) {

    //Cargo la data según lo seleccionado, llenando con ello los campos del formulario para devoluciones.
    $("#venta_codigoDev" + Documento).val(ventas_factDev[Documento][_indice]["codigo"]);
    $("#venta_cantidadDev" + Documento).val(ventas_factDev[Documento][_indice]["v_cantidad"]);
    $("#venta_precioDev" + Documento).val(ventas_factDev[Documento][_indice]["precio"]);
    $("#venta_totalDev" + Documento).val(ventas_factDev[Documento][_indice]["v_subtotal"]);
    $("#venta_descripcionDev" + Documento).val(ventas_factDev[Documento][_indice]["descripcion"]);
    $("#venta_devolucionDev" + Documento).val(ventas_factDev[Documento][_indice]["cantidad"]);

}

/**
 * Método para limpiar el formulario de devoluciones.
 * 
 * @return {[type]} [description]
 */
var ventas_limpiarFormularioDevolucion = function() {

    $("#venta_codigoDev" + Documento).val("");
    $("#venta_cantidadDev" + Documento).val("");
    $("#venta_precioDev" + Documento).val("");
    $("#venta_totalDev" + Documento).val("");
    $("#venta_descripcionDev" + Documento).val("");
    $("#venta_devolucionDev" + Documento).val("");

    //Deshabilito el boton de aceptar
    $('#ventas_agregarDev' + Documento).attr("disabled", true);
}

/**
 * Método que agraga la cantidad a devolver de un producto y lo agrega a la tabla, calculando el total, exento, etc.
 * @type {[type]}
 */
var ventas_devolverProducto = function() {

    var error = false;
    var seleccionado;

    //Agarro la varible dependiendo del documento en cuestión.
    switch (Documento) {
        case '_1':
            seleccionado = ventas_factDevIndice_1;
            break;
        case '_2':
            seleccionado = ventas_factDevIndice_2;
            break;
        case '_3':
            seleccionado = ventas_factDevIndice_3;
            break;
        case '_4':
            seleccionado = ventas_factDevIndice_4;
            break;
    }
    //Valido que devolución sea numérico.
    var cantidad = $("#venta_devolucionDev" + Documento).val();

    if (isNaN(cantidad) || cantidad == "") {
        $("#msjOkFactDev" + Documento).hide();
        var errorFact = '{"message": "La cantidad a devolver debe ser un valor numérico"}';
        errorMsj(JSON.parse(errorFact), 'msjErrorFactDev' + Documento, 'formErrorFactDev' + Documento);
        error = true;
    }

    //si no da aerror
    if (error == false) {
        //Validar que lo que se va a devolver no sea mayor a lo comprado
        var v_cantidad = parseFloat(ventas_factDev[Documento][seleccionado]["v_cantidad"]);
        if (cantidad > v_cantidad) {
            $("#msjOkFactDev" + Documento).hide();
            var errorFact = '{"message": "No puede devolver más cantidad de la comprada."}';
            errorMsj(JSON.parse(errorFact), 'msjErrorFactDev' + Documento, 'formErrorFactDev' + Documento);
            error = true;
        }
    }

    //si no da aerror
    if (error == false) {
        //Hacer cambios en la tabla.. calcular exento, subtotal, total, impuesto.
        var impuestoUnidad = parseFloat(ventas_factDev[Documento][seleccionado]["v_impuesto"] / ventas_factDev[Documento][seleccionado]["v_cantidad"]).toFixed(2);
        var exentoUnidad = parseFloat(ventas_factDev[Documento][seleccionado]["v_exento"] / ventas_factDev[Documento][seleccionado]["v_cantidad"]).toFixed(2);
        var subtotalUnidad = parseFloat(ventas_factDev[Documento][seleccionado]["v_subtotal"] / ventas_factDev[Documento][seleccionado]["v_cantidad"]).toFixed(2);

        //Asignando valor de impuesto.
        ventas_factDev[Documento][seleccionado]["impuesto"] = parseFloat(impuestoUnidad * cantidad).toFixed(2);

        //Asignando valor de exento
        ventas_factDev[Documento][seleccionado]["exento"] = parseFloat(exentoUnidad * cantidad).toFixed(2);

        //Asignando valor de subtotal
        ventas_factDev[Documento][seleccionado]["subtotal"] = parseFloat(subtotalUnidad * cantidad).toFixed(2);

        //Asignando valor de total
        ventas_factDev[Documento][seleccionado]["total"] = parseFloat((Number(ventas_factDev[Documento][seleccionado]["impuesto"]) + Number(ventas_factDev[Documento][seleccionado]["subtotal"]))).toFixed(2);

        //Asignando el valor de la devolución (cantidad) al campo
        ventas_factDev[Documento][seleccionado]["cantidad"] = parseFloat(cantidad).toFixed(2);

        //Mensaje de devolución agregada
        $("#msjErrorFactDev" + Documento).hide();
        $("#msjErrorGral").hide();
        $("#msjOkGral").hide();

        var succesDev = '{"message": "Devolución agregada al listado."}';
        successMsj(JSON.parse(succesDev), 'msjOkFactDev' + Documento, 'formOkFactDev' + Documento);

        //Actualizando la tabla.
        actualizarVentas_FactDevDataTable();

        //Limpio el formulario
        ventas_limpiarFormularioDevolucion();
    }
}

//Método para limpiar el formulario de notas de crédito y su tabla
var ventas_limpiarDataNC = function() {
    //Limpiarndo formuario de notas de crédito.
    $("#venta_codigoDev" + Documento).val("");
    $("#venta_cantidadDev" + Documento).val("");
    $("#venta_precioDev" + Documento).val("");
    $("#venta_totalDev" + Documento).val("");
    $("#venta_descripcionDev" + Documento).val("");
    $("#venta_devolucionDev" + Documento).val("");

    //Limpiando data de nota de crédito
    ventas_factDev[Documento] = new Array();

    //Actualizando la tabla
    actualizarVentas_FactDevDataTable();
}

/**
 * Método para asinar dependiendo de la posición del documento, el valor de la variable para llevar el ID de la Venta a la que se hará NC.
 * @param  {[type]} _idValor [description]
 * @return {[type]}          [description]
 */
var ventas_asignarIdVentaNC = function(_idValor) {

    switch (Documento) {
        case '_1':
            ventas_factDevId_1 = _idValor;
            break;
        case '_2':
            ventas_factDevId_2 = _idValor;
            break;
        case '_3':
            ventas_factDevId_3 = _idValor;
            break;
        case '_4':
            ventas_factDevId_4 = _idValor;
            break;
    }
}

/**
 * Método para retornar el valor del ID de la venta para NC según la posición del documento en donde nos encontremos.
 * @return {[type]} [description]
 */
var ventas_obtenerIdVentaNC = function() {

    switch (Documento) {
        case '_1':
            return ventas_factDevId_1;
            break;
        case '_2':
            return ventas_factDevId_2;
            break;
        case '_3':
            return ventas_factDevId_3;
            break;
        case '_4':
            return ventas_factDevId_4;
            break;
    }
}


var ventas_CotProSelected = 0; //Variable para llevar el control del documento seleccionado de la vista.
var ventas_CotProTipoSelected = "";
var ventas_CotProTable; //variable para llevar el control de la tabla de consultar Cotizaciones o proformas
/**
 * Método para cargar el dataTable Cotizaciones o proformas
 * @return {[type]} [description]
 */
var ventas_ConsultarCotProTable = function(_value) {

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $("#deposito_id").val();

    if (_value == 1) { //Se trata de una cotización
        ajax = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/ventasCotizacion";
    } else { //Se trata de una proforma
        ajax = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/ventasProforma";
    }

    ventas_CotProTable = $('#venta_table_consultarCotPro').DataTable({
        "destroy": true,
        "info": false,
        "processing": true,
        "serverSide": true,
        "lengthChange": false,
        "pageLength": 3,
        "autoWidth": false,
        "paging": true,
        "scrollY": "90px",
        "ajax": ajax,
        "columns": [
            { data: 'tipo_documento', name: 'tipo_documento', orderable: true, searchable: true },
            { data: 'codigo_factura', name: 'codigo_factura', orderable: true, searchable: true },
            { data: 'fecha_hora', name: 'fecha_hora', orderable: true, searchable: true },
            { data: 'vendedor_id', name: 'users.name', orderable: true, searchable: true },
            { data: 'cliente_id', name: 'clientes.primer_nombre', orderable: true, searchable: true },
            { data: 'total', name: 'total', orderable: true, searchable: true },
            { data: 'venta_id', name: 'venta_id', orderable: false, searchable: false, visible: false },
        ],
        "columnDefs": [
            { className: "text-right", "targets": [5] }, //Total alineado a la derecha.
            { width: '10%', "targets": 0 },
            { width: '10%', "targets": 1 },
            { width: '10%', "targets": 2 },
            { width: '30%', "targets": 3 },
            { width: '30%', "targets": 4 },
            { width: '10%', "targets": 5 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
        },
    });


    $('#venta_table_consultarCotPro tbody').on('click', 'tr', function() {

        var data = ventas_CotProTable.row(this).data(); //Obtengo la información de la fila seleccionada

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');

            //Elimino el contenido de la tabla
            $('#venta_table_consultarCotProDetalle').empty();

            //Se deselecciona la fila, por ende el detalle pasa a ser vacío
            ventas_ConsultarDetalleCorProTable(-1);

            //Deshabilitar los botones del modal, solo se activan cuando hay una cotización seleccionada.
            $("#ventas_cotPro_Reimprimir").prop('disabled', true);
            $("#ventas_cotPro_Modificar").prop('disabled', true);
            $("#ventas_cotPro_Nuevo").prop('disabled', true);
            $("#ventas_cotPro_Anular").prop('disabled', true);
            $("#ventas_cotPro_Convertir").prop('disabled', true);

            //Deshabilitar los botones del detalle puesto que no hay ningún detalle seleccionado
            $("#ventas_CotPro_eliminarReglon").prop('disabled', true);

            //Setenado la factura seleccionada como 0 (no hay factura seleccionada)
            ventas_CotProSelected = 0;
            ventas_CotProTipoSelected = "";
        } else {
            ventas_CotProTable.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');

            //Elimino el contenido de la tabla
            $('#venta_table_consultarCotProDetalle').empty();

            //Consultar el detalle de la factura.
            ventas_ConsultarDetalleCorProTable(data.venta_id);

            //Habilitar los botones del modal, existe una cotización seleccionada
            $("#ventas_cotPro_Reimprimir").prop('disabled', false);
            $("#ventas_cotPro_Modificar").prop('disabled', false);
            $("#ventas_cotPro_Nuevo").prop('disabled', false);
            $("#ventas_cotPro_Anular").prop('disabled', false);
            $("#ventas_cotPro_Convertir").prop('disabled', false);

            //Setenado la factura seleccionada (Id de la factura)
            ventas_CotProSelected = data.venta_id;
            ventas_CotProTipoSelected = data.tipo_documento;
        }
    });
}

/**
 * Método que realiza la acción de encoger y desplegar los paneles de los Doc's
 * @param  {[type]} e) {               var $this [description]
 * @return {[type]}    [description]
 */
$(document).on('click', '.panel-heading span.clickable', function(e) {
    var $this = $(this);
    if (!$this.hasClass('panel-collapsed')) {
        $this.parents('.panel').find('.panel-body').slideUp();
        $this.addClass('panel-collapsed');
        $this.find('i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
    } else {
        $this.parents('.panel').find('.panel-body').slideDown();
        $this.removeClass('panel-collapsed');
        $this.find('i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
    }
})

var ventas_ConsultarDetalleCotPro; //variable para llevar el control de la tabla de consultar Cotizaciones
var ventas_CotProDetalleSelected = 0; //Variable para llevar el control del documento seleccionado de la vista.
/**
 * Método para cargar el dataTable de ventas facturadas con estatus F.
 * @return {[type]} [description]
 */
var ventas_ConsultarDetalleCorProTable = function(_venta_id) {

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $("#deposito_id").val();

    ventas_ConsultarDetalleCotPro = $('#venta_table_consultarCotProDetalle').DataTable({
        "destroy": true,
        "info": false,
        "processing": true,
        "serverSide": true,
        "lengthChange": false,
        "autoWidth": false,
        "paging": false,
        "scrollY": "90px",
        // "scrollCollapse": true,
        "ajax": "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + _venta_id + "/consultarDetalle",
        "columns": [
            { data: 'codigo', name: 'productos.codigo' },
            { data: 'descripcion', name: 'descripcion' },
            { data: 'cantidad', name: 'cantidad' },
            { data: 'precio', name: 'precio' },
            { data: 'subtotal', name: 'subtotal' },
            { data: 'id_detalle', name: 'id_detalle', orderable: false, searchable: false, visible: false },

        ],
        "columnDefs": [
            { className: "text-right", "targets": [4] }, //Total alineado a la derecha.
            { width: '10%', "targets": 0 },
            { width: '60%', "targets": 1 },
            { width: '10%', "targets": 2 },
            { width: '10%', "targets": 3 },
            { width: '8%', "targets": 4 },
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
        },
    });

    $('#venta_table_consultarCotProDetalle tbody').on('click', 'tr', function() {

        var data = ventas_ConsultarDetalleCotPro.row(this).data(); //Obtengo la información de la fila seleccionada

        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');

            //Deshabilitar los botones del detalle puesto que no hay ningún detalle seleccionado
            $("#ventas_CotPro_modificarReglon").prop('disabled', true);
            $("#ventas_CotPro_eliminarReglon").prop('disabled', true);

            //Setenado la factura seleccionada como 0 (no hay factura seleccionada)
            ventas_CotProDetalleSelected = 0;
        } else {
            ventas_ConsultarDetalleCotPro.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');

            //Habilitar los botones de los reglones
            $("#ventas_CotPro_modificarReglon").prop('disabled', false);
            $("#ventas_CotPro_eliminarReglon").prop('disabled', false);

            //Setenado la factura seleccionada (Id de la factura)
            ventas_CotProDetalleSelected = data.id_detalle;
        }
    });
}

/**
 * Método para vaciar la tabla de Cotizaciones y proformas
 * @return {[type]} [description]
 */
var ventas_cerrarCotPro = function() {

    //Elimino el contenido de la tabla Padre
    $('#venta_table_consultarCotPro').empty();

    //Elimino el contenido de la tabla Hijo
    $('#venta_table_consultarCotProDetalle').empty();

    //Se oculta el mensaje de error
    $("#msjErrorVenta_ConsultarCotPro").hide();

    //Se oculta el mensaje de éxito
    $("#msjOkVenta_ConsultarCotPro").hide();
}

/**
 * Método que se ejecuata al realizar clic sobre el botón de reimprimir
 * @param  {[type]} event) {               } [description]
 * @return {[type]}        [description]
 */
$("#ventas_cotPro_Reimprimir").click(function(event) {
    //Cierro la ventana modal de facturas.
    $('#ventas_ConsultarCotProModal').modal('hide');

    //Elimino el contenido de la tabla Padre
    $('#venta_table_consultarCotPro').empty();

    //Elimino el contenido de la tabla Hijo
    $('#venta_table_consultarCotProDetalle').empty();

    ventas_reimprimirFactura(ventas_CotProSelected);
});

/**
 * Método que al hacer click en el botón ventas_cotPro_Anular anula la cotización o proforma seleccionada.
 * @param  {[type]}        [description]
 * @return {[type]}        [description]
 */
$("#ventas_cotPro_Anular").click(function(event) {

    //Se hace un llamado del método que se encarga de anular un documento
    $.when(ventas_anularDocumento(ventas_CotProSelected, "CotPro")).done(function() {
        //Posterior a su ejecución se realizan las siguientes acciones.

        //Se actualiza la tabla de cotizaciones o proformas.
        //Cargar todas las cotizaciones o proformas ordenadas por fecha, de la más reciente a la más antigua.
        $('#venta_table_consultarCotPro').empty();
        ventas_ConsultarCotProTable(ventas_tipoDocumentoCotPro);

    });
});

/**
 * Método que al hacer click en el botón ventas_cotPro_Nuevo lleva todos los valores a la vista principal (hace un copia deL DOCUMENTO seleccionado)
 * @param  {[type]}        [description]
 * @return {[type]}        [description]
 */
$("#ventas_cotPro_Nuevo").click(function(event) {

    var aux_ventas_CotProTipoSelected = ventas_CotProTipoSelected;
    var aux_ventas_CotProSelected = ventas_CotProSelected;

    //Cierro la ventana modal de facturas.
    $('#ventas_ConsultarCotProModal').modal('hide');

    //Se limpia el formulario de venta
    $.when(ventas_limpiarFormulario(true)).done(function() {

        //seteo los valores al documento.
        //Hago el llamado del método que puede consultar facturas, sin anular la misma
        ventas_nuevoDocumento(aux_ventas_CotProSelected, false);

        ventas_CotProTipoSelected = 0;
        ventas_CotProSelected = 0;
    });

});


var ventas_cotpro_contado = false;

/**
 * Método para convertir una cotización o proforma en una factura.
 * @param  {[type]}        [description]
 * @return {[type]}        [description]
 */
$("#ventas_cotPro_Convertir").click(function(event) {

    //Consultar los datos del cliente a la que aplica la factura, para determinar el tipo de cliente (credito - contado)
    ventas_cotpro_contado = false;;

    $.when(ventas_consultarDatosCliente(ventas_CotProSelected)).done(function() {

        //Si el cliente es a contado, se hace la conversión directa de la cotizacion/proforma a factura
        if (ventas_cotpro_contado == true) {
            ventas_convertirFactura(ventas_CotProSelected, ventas_cotpro_contado)
        } else {
            //Si el cliente es a crédito, abrir modal para determinar si lo facturará a crédito o a contado (ya que pudiera ser cualquiera de las dos)

            //Cambiando las opciones por defecto del modal, quitando el "esc" del teclado y evitando que se cierre al hacer click por fuera del modal
            $("#ventas_convertirFacturaTipo").modal({
                keyboard: false,
                backdrop: 'static',
            });

            //Abro el modal de consultar 
            $("#ventas_convertirFacturaTipo").modal('show');
        }

    });
});

/**
 * Método para cosnultar los datos del cliente, especificamente para determinar si es cliente a crédito o a contado
 * @param  {[type]} _ventaSelected [description]
 * @return {[type]}                [description]
 */
var ventas_consultarDatosCliente = function(_ventaSelected) {

    //Se realiza la consulta del tipo de cliente
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + _ventaSelected + "/tipoCliente";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            venta_id: _ventaSelected,
        },
        success: function(obj) {

            if (obj.tipoCliente.tipo_cliente_id == 1) {
                //Es a contado
                ventas_cotpro_contado = true;
            } else if (obj.tipoCliente.tipo_cliente_id == 2) {
                //Es a Crédito
                ventas_cotpro_contado = false;
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
}

/**
 * Método para convertir una cotización/proforma a ser factura (cuando el cliente es de contado.)
 * @param  {[type]} _ventaSelected [description]
 * @return {[type]}                [description]
 */
var ventas_convertirFactura = function(_ventaSelected, _contado) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + _ventaSelected + "/convertirFactura/" + _contado;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            venta_id: _ventaSelected,
            contado: _contado,
        },
        success: function(obj) {
            ////Se oculta el mensaje de error
            $("#msjErrorVenta_ConsultarCotPro").hide();

            //mostrar mensaje de éxito
            successMsj(obj, 'msjOkVenta_ConsultarCotPro', 'formOkVenta_ConsultarCotPro');

            //Recargar la tabla Maestro y detalle
            //Cargar todas las cotizaciones ordenadas por fecha, de la más reciente a la más antigua.
            $('#venta_table_consultarCotPro').empty();
            ventas_ConsultarCotProTable(ventas_tipoDocumentoCotPro);

            //Elimino el contenido de la tabla
            $('#venta_table_consultarCotProDetalle').empty();

            //Cargar el detalle de la factura, inicialmente al no haber selección, carga vacío
            ventas_ConsultarDetalleCorProTable("-1");

            //Deshabilitar los botones del modal, solo se activan cuando hay una cotización seleccionada.
            $("#ventas_cotPro_Reimprimir").prop('disabled', true);
            $("#ventas_cotPro_Modificar").prop('disabled', true);
            $("#ventas_cotPro_Nuevo").prop('disabled', true);
            $("#ventas_cotPro_Anular").prop('disabled', true);
            $("#ventas_cotPro_Convertir").prop('disabled', true);

            //Deshabilitar los botones del detalle puesto que no hay ningún detalle seleccionado
            $("#ventas_CotPro_eliminarReglon").prop('disabled', true);

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
}

/**
 * Método que se ejecuta al usuario elegir una opción para convertir la factura
 * @param  {[type]}        [description]
 * @return {[type]}        [description]
 */
$("#venta_ConvertirFacturaTipo_Aceptar").click(function(event) {

    var tipoDoc = $('input[name=venta_convertirFactura_Tipo]:checked').val();

    if (tipoDoc == "Cre") { //El usuario escogió convertir la factura a crédito
        ventas_convertirFactura(ventas_CotProSelected, false);
    } else { //El usuario escogío convertir la factura a contado.
        ventas_convertirFactura(ventas_CotProSelected, true);
    }

    $("#ventas_convertirFacturaTipo").modal("hide");

});


//Variable para llevar el control del reglón a eliminar
var ventas_eliminarReglon_id = new Array();

/**
 * Método para ejecutar la acción de elimnar un reglón seleccionado.
 * @param  {[type]}        [description]
 * @return {[type]}        [description]
 */
$("#ventas_CotPro_eliminarReglon").click(function(event) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var venta_id = ventas_CotProSelected;
    var detalle_id = ventas_CotProDetalleSelected;

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/venta/" + venta_id + "/detalle/" + detalle_id + "/eliminarReglon";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'DELETE',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            venta_id: venta_id,
            detalle_id: detalle_id,
        },
        success: function(obj) {

            //Se oculta el mensaje de error
            $("#msjErrorVenta_ConsultarCotPro").hide();

            //mostrar mensaje de éxito
            successMsj(obj, 'msjOkVenta_ConsultarCotPro', 'formOkVenta_ConsultarCotPro');

            //Cargar todas las cotizaciones o proformas ordenadas por fecha, de la más reciente a la más antigua.
            // ventas_ConsultarCotProTable(ventas_tipoDocumentoCotPro);

            //Elimino el contenido de la tabla detalle, y vuelvo a cargarlo
            $('#venta_table_consultarCotProDetalle').empty();

            //Cargar el detalle de la factura, inicialmente al no haber selección, carga vacío
            ventas_ConsultarDetalleCorProTable(ventas_CotProSelected);

            //Deshabilitar los botones del detalle puesto que no hay ningún detalle seleccionado
            $("#ventas_CotPro_eliminarReglon").prop('disabled', true);

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
});

/**
 * Método para realizar la modificación de una cotización o proforma, según haya sido seleccionada.
 * @param  {[type]}        [description]
 * @return {[type]}        [description]
 */
$("#ventas_cotPro_Modificar").click(function(event) {

    var aux_ventas_CotProTipoSelected = ventas_CotProTipoSelected;
    var aux_ventas_CotProSelected = ventas_CotProSelected;

    //Cierro la ventana modal de facturas.
    $('#ventas_ConsultarCotProModal').modal('hide');

    //Se limpia el formulario de venta
    $.when(ventas_limpiarFormulario(true)).done(function() {

        //Hago el llamado del método que puede consultar facturas, sin anular la misma.
        ventas_nuevoDocumento(aux_ventas_CotProSelected, false);

        ventas_CotProTipoSelected = aux_ventas_CotProTipoSelected;
        ventas_CotProSelected = aux_ventas_CotProSelected;

        //Bloquear q no pueda cambiarse el tipo de documento.
        $("#ventas_fieldsetTipoDoc" + Documento).prop('disabled', true);

    });
});

/**
 * Método para consultar los últimos 2 precios de venta del producto.
 * @return {[type]} [description]   
 */
var consultarUltimosPrecios = function(_producto_codigo) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_codigo = _producto_codigo;

    //Se identifica la ruta
    var route = "/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_codigo/" + producto_codigo + "/ultimos2Precios";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_codigo: producto_codigo,
        },
        success: function(obj) {

            //Se limpia a lista de precios
            limpiarUltimos2Precios();

            //Se le asigna el precio al primer radio button
            if (obj.ultimos2Precios[0] != undefined) {
                $('label[for=venta_ultimoPrecioA' + Documento + ']').html(parseFloat(obj.ultimos2Precios[0].precio).toFixed(2));
                $('#venta_ultimoPrecioA' + Documento).val(parseFloat(obj.ultimos2Precios[0].precio).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_ultimoPrecioA" + Documento, obj.ultimos2Precios[0].precio);
            } else {
                $('label[for=venta_ultimoPrecioA' + Documento + ']').html("N/D");
                $('#venta_ultimoPrecioA' + Documento).val(null);
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_ultimoPrecioA" + Documento, "N/D");
            }

            //Se le asigna el precio al segundo radio button
            if (obj.ultimos2Precios[1] != undefined) {
                $('label[for=venta_ultimoPrecioB' + Documento + ']').html(parseFloat(obj.ultimos2Precios[1].precio).toFixed(2));
                $('#venta_ultimoPrecioB' + Documento).val(parseFloat(obj.ultimos2Precios[1].precio).toFixed(2));
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_ultimoPrecioB" + Documento, obj.ultimos2Precios[1].precio);
            } else {
                $('label[for=venta_ultimoPrecioB' + Documento + ']').html("N/D");
                $('#venta_ultimoPrecioB' + Documento).val(null);
                //Evaluando valor no permitido para selección (0 o N/D)
                ventas_evaluarSeleccion("#venta_ultimoPrecioB" + Documento, "N/D");
            }

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};

/**
 * Método para limpiar el listado de los útlimos 2 precios
 * @return {[type]} [description]
 */
var limpiarUltimos2Precios = function() {
    $('label[for=venta_ultimoPrecioA' + Documento + ']').html('N/D');
    $('label[for=venta_ultimoPrecioB' + Documento + ']').html('N/D');

    $('#venta_ultimoPrecioA' + Documento).val(null);
    $('#venta_ultimoPrecioB' + Documento).val(null);

    $('#venta_ultimoPrecioA' + Documento).attr('disabled', false);
    $('#venta_ultimoPrecioB' + Documento).attr('disabled', false);

    //Quitar lo seleccionado de los radio butoms
    $('input[type=radio][name=venta_ultimoPrecio' + Documento + ']').prop('checked', false);
}

/**
 * Método que se llama cuando uno de los radio button del listado de los 2 últimosprecios es seleccionado en DOC1
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_ultimoPrecio_1]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_ultimoPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método que se llama cuando uno de los radio button del listado de los 2 últimosprecios es seleccionado en DOC2
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_ultimoPrecio_2]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_ultimoPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método que se llama cuando uno de los radio button del listado de los 2 últimosprecios es seleccionado en DOC3
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_ultimoPrecio_3]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_ultimoPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método que se llama cuando uno de los radio button del listado de los 2 últimos precios es seleccionado en DOC4
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
$('input[type=radio][name=venta_ultimoPrecio_4]').change(function() {
    //Asignando al campo precio, el valor seleccionado del radio buttom
    $("#venta_precio" + Documento).val($('input[name=venta_ultimoPrecio' + Documento + ']:checked').val());
    //Actualizo el total
    ventas_actualizarTotal();
});

/**
 * Método para actualiza el listado de precios recientes.
 * @param  {[type]}        [description]
 * @return {[type]}        [description]
 */
$("#ventas_ultimosPrecios").click(function(event) {

    //Obtengo el código del producto al que deseo consultarle los dos últimos precios (Actualizar valores)
    var codigo = autoCompleteVentaFactFormatValue($("#venta_codigo" + Documento).val());

    if (codigo != null) {
        //Llamo al método que consulta los 2 últimos precios del producto.
        consultarUltimosPrecios(codigo);
    }
});

/**
 * Método para que con la tecla enter se vaya navegando entre inputs.
 * @param  {[type]}    [description]
 * @return {[type]}    [description]
 */
$(document).on('keydown', ':tabbable', function(e) {

    if (e.which == 13 || e.keyCode == 13) {
        e.preventDefault();
        var $canfocus = $(':tabbable:visible');
        /**
         * Condicional para determinar si el focus se encuentra sobre el input "cantidad" cuando se presiona "enter", de ser así se debe hacer el focus sobre el botón "Aceptar" del formulario (Ventas)
         */
        if ($canfocus.eq($canfocus.index(document.activeElement)).focus().attr("id") == "venta_cantidad" + Documento) {
            //Focus al boton "Aceptar"
            $canfocus.eq($canfocus.index($("#venta_aceptar" + Documento))).focus();
        } else if ($canfocus.eq($canfocus.index(document.activeElement)).focus().attr("id") == "mcp_cantidad_prod") {
            /**
             * Condicional para determinar si el focus se encuentra sobre el input "cantidad" cuando se presiona "enter", de ser así se debe hacer el focus sobre el botón "Aceptar" del formulario (Compras)
             */
            //Focus al boton "Aceptar"
            $canfocus.eq($canfocus.index($("#mc_agregar"))).focus();
        } else {
            //Si no se encuentra en el campo cantidad, se hacen otras validaciones.
            //Si está posicionado en el botón "ACEPTAR" (Ventas)
            if ($canfocus.eq($canfocus.index(document.activeElement)).focus().attr("id") == "venta_aceptar" + Documento) {
                //Al precionar "Enter" se ejecuta la acción del boton
                ventas_agregarProducto();
            } else if ($canfocus.eq($canfocus.index(document.activeElement)).focus().attr("id") == "mc_agregar") {
                //Al precionar "Enter" se ejecuta la acción del boton
                $("#mc_agregar").click();
            } else {
                //Si no cumple con ninguna de las validaciones anteriores, se trata del funcionamiento comun, que con "enter" se cambia de input.
                var index = $canfocus.index(document.activeElement) + 1;
                if (index >= $canfocus.length) index = 0;
                $canfocus.eq(index).focus();
            }
        }
    }

});

/**
 * Método para bloquear el campo de días de crédito dependiendo del tipo de cliente.
 * @param  {[type]} _value [description]
 * @return {[type]}        [description]
 */
var ventas_cambioTipoCliente = function(_value) {
    if (_value == 2) { //Un cliente a crédito.
        $("#venta_cliente_credito").prop('disabled', false);
    } else if (_value == 1) { //Un cliente a contado
        $("#venta_cliente_credito").prop('disabled', true);
        $("#venta_cliente_credito").val("");
    }
}

/**
 * Método para habilitar o deshabilitar la selcción de los radio button, si estos tienen precio 0.00 o N/D
 * @param  {[type]} _idComponent    [description]
 * @param  {[type]} _value          [description]
 * @return {[type]}                 [description]
 */
var ventas_evaluarSeleccion = function(_idComponent, _value) {

    //Si el valor del campo es 0 o N/D no se permite la selección de dicho radio Button
    if (_value == 0 || _value == "N/D") {
        $(_idComponent).attr('disabled', 'disabled');
    } else {
        $(_idComponent).attr('disabled', false);
    }
}

/**
 * Método para abrir el modal que carga el histórico del producto.
 * @param  {[type]}   [description]
 * @return {[type]}   [description]
 */
var ventas_historiaProducto = function() {

    var codigo_producto = autoCompleteVentaFactFormatValue($("#venta_codigo" + Documento).val());
    if (codigo_producto == null) {

        var errorFact = '{"message": "Debe seleccionar un producto para ver su historia."}';
        errorMsj(JSON.parse(errorFact), 'msjErrorGral', 'formErrorGral');

        $("#historiaItem").click(); //cerrar el modal
    } else {
        $('#ventasHistorialItem').modal('show');
        ventas_cargarHistoriaItemDatatable($("#venta_producto_id" + Documento).val());
        $("#ventasHistorialItemtitle").text("Historia del item: " + codigo_producto + " - " + $("#venta_descripcion" + Documento).val());
    }
};

/**
 * Método para cargar el historial de un producto.
 * @param  {[type]} _IdProducto      [Id del producto]
 * @return {[type]}                  [description]
 */
var ventas_cargarHistoriaItemDatatable = function(_IdProducto) {

    //Inicialización de tabla de compras
    var token = $("#token").val();

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();

    historiaItemTable = $('#ventas_historiaItemTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            headers: { 'X-CSRF-TOKEN': token },
            url: '/getHistoriaItem/datatable/' + empresa_id + '/' + deposito_id + '/' + _IdProducto,
            type: "POST"
        },
        "columns": [
            { data: 'fecha', name: 'historia_item.fecha', orderable: false, searchable: false },
            { data: 'documento', name: 'historia_item.documento' },
            { data: 'tipo', name: 'historia_item.tipo' },
            { data: 'op', name: 'historia_item.op', orderable: false, searchable: false },
            { data: 'cantidad', name: 'historia_item.cantidad', orderable: false, searchable: false },
            { data: 'saldo', name: 'historia_item.saldo', orderable: false, searchable: false },
            { data: 'fob', name: 'historia_item.fob', orderable: false, searchable: false },
            { data: 'cif', name: 'historia_item.cif', orderable: false, searchable: false },
            { data: 'comentario', name: 'historia_item.comentario' }
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        },
        "scrollX": true,
        "fixedHeader": {
            header: true,
            footer: true
        },
        "destroy": true,
        "select": {
            style: 'single'
        }
    });

};

/**
 * Método para obtener el depósito
 * @param  {[type]} user_id [description]
 * @return {[type]}         [description]
 */
var ventas_obtenerDepositoporUsuario = function(user_id) {

    var dfd = new $.Deferred();
    var token = $("#token").val();
    //Se identifica la ruta
    var route = "/deposito/get/" + user_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        success: function(obj) {
            $('#deposito_id').val(obj.deposito[0].id);
            $("#nombre_deposito").html(obj.deposito[0].nombre);
            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};