var activarTab = function() {
    if ($("#p_inve").val() === "1") {

    } else {
        if ($("#p_inve").val() === "1") {
            //no es necsario porque por defecto se carga,si se deja se cargaria dos veces
            //$('#menu a[href="#Inventario"]').click();
        } else {
            if ($("#p_vent").val() === "1") {
                $('#menu a[href="#Ventas"]').click();
            } else {
                if ($("#p_comp").val() === "1") {
                    $('#menu a[href="#Compras"]').click();
                } else {
                    if ($("#p_clie").val() === "1") {
                        $('#menu a[href="#Clientes"]').click();
                    } else {
                        if ($("#p_prov").val() === "1") {
                            $('#menu a[href="#Proveedores"]').click();
                        } else {
                            if ($("#p_info").val() === "1") {
                                $('#menu a[href="#Informes"]').click();
                            } else {
                                if ($("#p_cont").val() === "1") {
                                    $('#menu a[href="#Contabilidad"]').click();
                                } else {
                                    if ($("#p_caja").val() === "1") {
                                        $('#menu a[href="#Caja"]').click();
                                    } else {
                                        if ($("#p_conf").val() === "1") {
                                            $('#menu a[href="#Configuracion"]').click();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


//validaciones para ejecutar luego de que cargue la pagina
$(document).ready(function() {
    //obtener permiso del módulo
    //obtenerPermiso();
    //cargar datos de la empresa 
    $.when(obtenerEmpresa()).done(function() {
        activarTab();


        if ($("#p_inve").val() === "1") {
            cargarCompatibilidadDataTable();

            cargarKitDataTable();

            cargarSerieDataTable();

            cargarInvAlternos(0);
        }

    });


    //Se deshabilita el botón.
    $("#productoKit").attr('disabled', true);
    $("#serie").attr('disabled', true);

    //Se deshabilita la opción de seleccionar si es padre del grupo, siendo "" no puede ser padre del grupo
    $("#padre_grupo").attr('checked', false);
    $("#padre_grupo").attr("disabled", true);


    //validaciones campos decimales sección inventario
    //$('#Origen').numeric("."); 
    $('.class_float').mask("#,##0.00", { reverse: true });
    $('.class_int').mask("#,##0", { reverse: true });
    $('.input_recibido').mask("#,##0.00", { reverse: true });

    //Campos de tipo fecha
    $.datepicker.regional['es'] = {
        closeText: 'Cerrar',
        prevText: '< Ant',
        nextText: 'Sig >',
        currentText: 'Hoy',
        monthNames: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
        monthNamesShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
        dayNames: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
        dayNamesShort: ['Dom', 'Lun', 'Mar', 'Mié', 'Juv', 'Vie', 'Sáb'],
        dayNamesMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sá'],
        weekHeader: 'Sm',
        dateFormat: 'dd/mm/yy',
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: false,
        yearSuffix: ''
    };

    $(function() {

        $.datepicker.setDefaults($.datepicker.regional["es"]);
        $("#fecha_oferta_d").datepicker();
        $("#fecha_inicio").datepicker();


    });



    $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
        if (e.target.hash == '#Inventario') {
            //limpiar los labels			
            limpiarIconos();
            limpiarIconosLabels();
            productosTable.columns.adjust().draw();
        }
    })


});

///Funciones Inventario
///
var procesarCalcularMargen = function(modulo) {

    switch (modulo) {
        case 1: //ventas
            calcularMargen("cif_local", "margen_a", "precio_a", "margen_b", "precio_b", "margen_c", "precio_c", "margen_d", "precio_d", "margen_e", "precio_e", "margen_f", "precio_f", "margen_g", "precio_g");
            break;
        case 2: //compras
            calcularMargen("mc_cif_local", "mc_margen_a", "mc_precio_a", "mc_margen_b", "mc_precio_b", "mc_margen_c", "mc_precio_c", "mc_margen_d", "mc_precio_d", "mc_margen_e", "mc_precio_e", "mc_margen_f", "mc_precio_f", "mc_margen_g", "mc_precio_g");
            break;
    }

}

var fob_origenOnChange = function(_fob_origen, _porc_fob_cif, _cif_local, modulo) {

    var _fob_origen = $('#' + _fob_origen);
    var _porc_fob_cif = $('#' + _porc_fob_cif);
    var _cif_local = $('#' + _cif_local);

    if (_fob_origen.val() == null) {
        _fob_origen.val("0.00");
    }
    if (_fob_origen.val() == 0) {
        _porc_fob_cif.val("-100");
        _cif_local.val("00.00");
    } else {
        if (_porc_fob_cif.val() == "-100" || _porc_fob_cif.val().length <= 0) {
            _porc_fob_cif.val("00.00");
        }
        var resultado = parseFloat(_fob_origen.val()) + parseFloat(((_fob_origen.val() * _porc_fob_cif.val()) / 100));
        _cif_local.val(resultado.toFixed(2));

    }
    _fob_origen.val(parseFloat(_fob_origen.val()).toFixed(2));
    _porc_fob_cif.val(parseFloat(_porc_fob_cif.val()).toFixed(2));
    //alert(modulo);
    procesarCalcularMargen(modulo);


};

var cif_localOnChange = function(_fob_origen, _porc_fob_cif, _cif_local, modulo) {

    var _fob_origen = $('#' + _fob_origen);
    var _porc_fob_cif = $('#' + _porc_fob_cif);
    var _cif_local = $('#' + _cif_local);

    if (_fob_origen.val() == 0) {
        _porc_fob_cif.val("-100");
        _cif_local.val("00.00");


    } else {
        if (_porc_fob_cif.val() == "-100" || _porc_fob_cif.val().length <= 0) {
            _porc_fob_cif.val("00.00");
        }
        var valorX = parseFloat(_cif_local.val()) - parseFloat(_fob_origen.val());
        var resultado = (valorX * 100) / parseFloat(_fob_origen.val());
        _porc_fob_cif.val(resultado.toFixed(2));

    }
    _fob_origen.val(parseFloat(_fob_origen.val()).toFixed(2));
    _cif_local.val(parseFloat(_cif_local.val()).toFixed(2));

    procesarCalcularMargen(modulo);

};

var onchangePrecio = function(_cif_local, _Porc, _Precio) {

    if (_cif_local.val().length <= 0) {
        _cif_local.val("00.00");
    }
    if (_Porc.val().length <= 0) {
        _Porc.val("00.00");
    }
    if (_Precio.val().length <= 0) {
        _Precio.val("00.00");
    }

    _Porc.val(calcularPorcMargen(_cif_local, _Precio));
    _Precio.val(parseFloat(_Precio.val()).toFixed(2));

};

var onchangePorcentaje = function(_cif_local, _Porc, _Precio) {

    if (_cif_local.val().length <= 0) {
        _cif_local.val("00.00");
    }
    if (_Porc.val().length <= 0) {
        _Porc.val("00.00");
    }
    if (_Precio.val().length <= 0) {
        _Precio.val("00.00");
    }

    if (parseFloat(_cif_local.val()) > 0 && parseFloat(_Porc.val()) > 0 ) {
        var valor = parseFloat(_cif_local.val()) + (parseFloat(_cif_local.val()) * parseFloat(_Porc.val()) / 100);
    } else {
        _Precio.val("00.00");
    }

    _Precio.val(valor.toFixed(2));
    _Porc.val(parseFloat(_Porc.val()).toFixed(2));
};

var calcularMargen = function(_cif_local, _margen_a, _precio_a, _margen_b, _precio_b, _margen_c, _precio_c, _margen_e, _precio_e, _margen_d, _precio_d, _margen_f, _precio_f, _margen_g, _precio_g) {

    var _cif_local = $('#' + _cif_local);
    //alert("_cif_local="+_cif_local.val());
    var _margen_a = $('#' + _margen_a);
    //alert("_margen_a="+_margen_a.val());
    var _precio_a = $('#' + _precio_a);
    //alert("_precio_a="+_precio_a.val());
    var _margen_b = $('#' + _margen_b);
    //alert("_margen_b="+_margen_b.val());
    var _precio_b = $('#' + _precio_b);
    //alert("_precio_b="+_precio_b.val());
    var _margen_c = $('#' + _margen_c);
    //alert("_margen_c="+_margen_c.val());
    var _precio_c = $('#' + _precio_c);
    //alert("_precio_c="+_precio_c.val());
    var _margen_d = $('#' + _margen_d);
    //alert("_margen_d="+_margen_d.val());
    var _precio_d = $('#' + _precio_d);
    //alert("_precio_d="+_precio_d.val());
    var _margen_e = $('#' + _margen_e);
    //alert("_margen_e="+_margen_e.val());
    var _precio_e = $('#' + _precio_e);
    //alert("_precio_e="+_precio_e.val());
    var _margen_f = $('#' + _margen_f);
    //alert("_margen_f="+_margen_f.val());
    var _precio_f = $('#' + _precio_f);
    //alert("_precio_f="+_precio_f.val());
    var _margen_g = $('#' + _margen_g);
    //alert("_margen_g="+_margen_g.val());
    var _precio_g = $('#' + _precio_g);
    //alert("_precio_g="+_precio_g.val());

    /* 
    //considerando el porcentaje fijo y el que cambia es el precio
    _margen_a.val(calcularPorcMargen(_cif_local, _precio_a));
    _margen_b.val(calcularPorcMargen(_cif_local, _precio_b));
    _margen_c.val(calcularPorcMargen(_cif_local, _precio_c));
    _margen_d.val(calcularPorcMargen(_cif_local, _precio_d));
    _margen_e.val(calcularPorcMargen(_cif_local, _precio_e));
    _margen_f.val(calcularPorcMargen(_cif_local, _precio_f));
    _margen_g.val(calcularPorcMargen(_cif_local, _precio_g));
    */
    onchangePorcentaje(_cif_local, _margen_a, _precio_a);
    onchangePorcentaje(_cif_local, _margen_b, _precio_b);
    onchangePorcentaje(_cif_local, _margen_c, _precio_c);
    onchangePorcentaje(_cif_local, _margen_d, _precio_d);
    onchangePorcentaje(_cif_local, _margen_e, _precio_e);
    onchangePorcentaje(_cif_local, _margen_f, _precio_f);
    onchangePorcentaje(_cif_local, _margen_g, _precio_g);
};

var calcularPorcMargen = function(_cif_local, _Precio) {

    var valorX;
    var resultado;

    if (_cif_local.val().length <= 0) {
        _cif_local.val("00.00");
    }

    if (_Precio.val().length <= 0) {
        _Precio.val("00.00");
    }


    if (parseFloat(_cif_local.val()) > 0 && parseFloat(_Precio.val()) > 0) {
        valorX = parseFloat(_Precio.val()) - parseFloat(_cif_local.val());
        resultado = (valorX * 100) / parseFloat(_cif_local.val());
    } else {
        resultado = 0;
    }


    return resultado.toFixed(2);
};


var consultarFormInventario = function() {
    var inventario = {
        'fob_origen': $("#fob_origen").val(),
        'zclicol': $("#zclicol").val(),
        'porc_fob_cif': $("#porc_fob_cif").val(),
        'cif_local': $("#cif_local").val(),
        'margen_a': $("#margen_a").val(),
        'margen_b': $("#margen_b").val(),
        'margen_c': $("#margen_c").val(),
        'margen_d': $("#margen_d").val(),
        'precio_a': $("#precio_a").val(),
        'precio_b': $("#precio_b").val(),
        'precio_c': $("#precio_c").val(),
        'precio_d': $("#precio_d").val(),
        'fecha_oferta_d': $("#fecha_oferta_d").val(),
        'margen_e': $("#margen_e").val(),
        'margen_f': $("#margen_f").val(),
        'margen_g': $("#margen_g").val(),
        'precio_e': $("#precio_e").val(),
        'precio_f': $("#precio_f").val(),
        'precio_g': $("#precio_g").val(),
        'comentario': $("#comentario").val(),
        'comentario_publico': $("#comentario_publico").is(':checked'),
        'comentario_privado': $("#comentario_privado").val(),
        'stock_minimo': $("#stock_minimo").val(),
        'stock_maximo': $("#stock_maximo").val(),
        'cantidad_inicial': $("#cantidad_inicial").val(),
        'fecha_inicio': $("#fecha_inicio").val(),
        'ubicacion': $("#ubicacion").val(),
        'deposito_id': $("#deposito_id").val(),
        'empresa_id': $("#empresa_id").val(),
        'pedir': $("#pedir").val(),
    }

    return inventario;
};

function limpiarFormInventario() {

    $("#fob_origen").val("");
    $("#zclicol").val("");
    $("#porc_fob_cif").val("");
    $("#cif_local").val("");
    $("#margen_a").val("");
    $("#margen_b").val("");
    $("#margen_c").val("");
    $("#margen_d").val("");
    $("#precio_a").val("");
    $("#precio_b").val("");
    $("#precio_c").val("");
    $("#precio_d").val("");
    $("#fecha_oferta_d").val("");
    $("#margen_e").val("");
    $("#margen_f").val("");
    $("#margen_g").val("");
    $("#precio_e").val("");
    $("#precio_f").val("");
    $("#precio_g").val("");
    $("#comentario").val("");
    $("#comentario_publico").attr('checked', false);
    $("#comentario_privado").val("");
    $("#stock_minimo").val("");
    $("#stock_maximo").val("");
    $("#cantidad_inicial").val("");
    $("#fecha_inicio").val("");
    $("#ubicacion").val("");
    $("#disponible").val("");
    $("#separado").val("");
    $("#pedido").val("");
    $("#pedir").val("");

    //desactivar botonos de inventario
    $('#btnInvAlternos').attr("disabled", 'true');
    $('#btnDisponible').attr("disabled", 'true');
    $('#btnSeparado').attr("disabled", 'true');
    $('#btnPedido').attr("disabled", 'true');
    $('#btnPedir').attr("disabled", 'true');
}

var valorFloat = function(_value) {

    var valor = 0.00;
    if (_value === null) {
        _value = "0.00";
    }

    if (parseFloat(_value) > 0) {
        valor = parseFloat(_value);
    }


    return valor.toFixed(2);
};

function cargarFormInventario($inventario) {


    $("#fob_origen").val(valorFloat($inventario["fob_origen"]));
    $("#zclicol").val(valorFloat($inventario["zclicol"]));
    $("#porc_fob_cif").val(valorFloat($inventario["porc_fob_cif"]));
    $("#cif_local").val(valorFloat($inventario["cif_local"]));
    $("#margen_a").val(valorFloat($inventario["margen_a"]));
    $("#margen_b").val(valorFloat($inventario["margen_b"]));
    $("#margen_c").val(valorFloat($inventario["margen_c"]));
    $("#margen_d").val(valorFloat($inventario["margen_d"]));
    $("#margen_e").val(valorFloat($inventario["margen_e"]));
    $("#margen_f").val(valorFloat($inventario["margen_f"]));
    $("#margen_g").val(valorFloat($inventario["margen_g"]));
    $("#precio_a").val(valorFloat($inventario["precio_a"]));
    $("#precio_b").val(valorFloat($inventario["precio_b"]));
    $("#precio_c").val(valorFloat($inventario["precio_c"]));
    $("#precio_d").val(valorFloat($inventario["precio_d"]));
    $("#precio_e").val(valorFloat($inventario["precio_e"]));
    $("#precio_f").val(valorFloat($inventario["precio_f"]));
    $("#precio_g").val(valorFloat($inventario["precio_g"]));

    $("#comentario").val($inventario["comentario"]);
    $("#comentario_privado").val($inventario["comentario_privado"]);
    $("#ubicacion").val($inventario["ubicacion"]);

    $("#stock_minimo").val($inventario["stock_minimo"]);
    $("#stock_maximo").val($inventario["stock_maximo"]);
    $("#cantidad_inicial").val($inventario["cantidad_inicial"]);

    $("#fecha_oferta_d").val($inventario["fecha_oferta_d"]);
    $("#fecha_inicio").val($inventario["fecha_inicio"]);

    $("#comentario_publico").prop('checked', $inventario["comentario_publico"] == "S" ? true : false);

    $("#disponible").val(valorFloat($inventario["disponible"]));
    $("#separado").val(valorFloat($inventario["separado"]));
    $("#pedido").val(valorFloat($inventario["pedido"]));
    $("#pedir").val(valorFloat($inventario["pedir"]));

    //activar botones inventario
    $('#btnInvAlternos').removeAttr("disabled");
    $('#btnDisponible').removeAttr("disabled");
    $('#btnSeparado').removeAttr("disabled");
    $('#btnPedido').removeAttr("disabled");
    $('#btnPedir').removeAttr("disabled");

}

var getInventario = function(producto_id) {

    //Objeto para manejar la promesa.
    //var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    //Se identifica la ruta
    var route = "/Inventario/getInventario/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_id/" + producto_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {

            //Limpiar si hay campos en el formulario
            limpiarFormInventario();
            //setear aquí todos los valores del inventario
            cargarFormInventario(obj.inventario[0]);

            //Se carga el código del producto en el campo oculto.
            $('#inventarioSelect').val(obj.inventario[0].id);

            //dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    //return dfd.promise();
};

$("#btnDisponible").click(function() {

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_id = $('#productoSelect').val();

    var route = "/Inventario/consultarDisponibilidad/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_id/" + producto_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {

            $('#disponible').val(parseFloat(obj[0].disponible).toFixed(2));

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
});

$("#btnSeparado").click(function() {

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_id = $('#productoSelect').val();

    var route = "/Inventario/consultarSeparado/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_id/" + producto_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {

            $('#separado').val(parseFloat(obj[0].separado).toFixed(2));

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
});

$("#btnPedido").click(function() {

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_id = $('#productoSelect').val();

    var route = "/Inventario/consultarPedido/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_id/" + producto_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {

            $('#pedido').val(parseFloat(obj[0].pedido).toFixed(2));

            //Abrir ventana emergente. Issue Task #64
            var url = '/empresa/' + $('#empresa_id').val() + "/deposito/" + $('#deposito_id').val() + "/pedidosCerrados/producto/" + producto_id;
            pedidos_CerradosPorProducto.ajax.url(url).load();

            $("#bnt_pedido_modal").modal("show");

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
});

var pedidos_CerradosPorProducto; //variable para llevar el control de la tabla a consultar de Pedidos

/**
 * Método para cargar el dataTable de pedidos cerrados por Producto
 * @return {[type]} [description]
 */
var pedidos_CerradosPorProducto_Consultar = function(_prodcutoId) {

    var empresa_id = $('#empresa_id').val();
    var deposito_id = $("#deposito_id").val();

    pedidos_CerradosPorProducto = $('#pedidosCerrados_Producto').DataTable({
        "destroy": true,
        "processing": true,
        "serverSide": true,
        "autoWidth": false,
        "ajax": {
            headers: { 'X-CSRF-TOKEN': token },
            url: "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/pedidosCerrados/producto/" + _prodcutoId,
            type: "GET"
        },
        "columns": [
            { data: 'numero_pedido', name: 'pedidos.numero_pedido' },
            { data: 'proveedor', name: 'proveedores.nombre' },
            { data: 'fecha', name: 'fecha', orderable: false, searchable: false },
            { data: 'cantidad', name: 'detalle_pedidos.cantidad' },
            { data: 'costo', name: 'costo', orderable: false, searchable: false },
        ],
        "columnDefs": [
            { width: '8%', "targets": 0 },
            { width: '55%', "targets": 1 },
            { width: '10%', "targets": 2 },
            { width: '10%', "targets": 3 },
            { width: '17%', "targets": 4 },
        ],
        "order": [
            [0, 'desc']
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
        },
    });
}

$("#btnPedir").click(function() {

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var producto_id = $('#productoSelect').val();

    var route = "/Inventario/consultarPedir/empresa_id/" + empresa_id + "/deposito_id/" + deposito_id + "/producto_id/" + producto_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {

            $('#pedir').val(parseFloat(obj[0].pedir).toFixed(2));

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
});

var inventarioAlternoTable;

var cargarInvAlternos = function(producto_id) {
    //tabla de productos
    var valores = $('#empresa_id').val() + ":" + $('#deposito_id').val() + ":" + producto_id;
    inventarioAlternoTable = $('#invAlternosTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": '/Inventario/getDatatableInvAlternos/' + valores,
        "columns": [
            { data: 'codigo_producto', name: 'codigo_producto' },
            { data: 'nombre_producto', name: 'nombre_producto' },
            { data: 'nombre', name: 'nombre_deposito' },
            { data: 'disponible', name: 'disponible' }
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
        },
        "autoWidth": false,
        "columnDefs": [
            { "width": "25%", "targets": 0 },
            { "width": "25%", "targets": 1 },
            { "width": "25%", "targets": 2 },
            { "width": "25%", "targets": 3 },
        ]
    });

};

$("#btnInvAlternos").click(function() {
    var url = '/Inventario/getDatatableInvAlternos/' + $('#empresa_id').val() + ":" + $('#deposito_id').val() + ":" + $('#productoSelect').val();
    inventarioAlternoTable.ajax.url(url).load();
});

//Funciones de Producto
/**
 * Variable creada para llevar la estructura de Productos Compatibles
 */
var productosCompatibles = new Array(); //Código, Marca, Descripción, Inventario
var compatibilidadDataTable; //Variable que guarda el DataTable de Productos Compatibles.

/**
 * Método donde se define el dataTable de productos compatibles.
 * @return {[type]} [description]
 */
var cargarCompatibilidadDataTable = function() {
    //Inicialización de tabla de compatibilidad.
    compatibilidadDataTable = $('#compatibilidadDataTable').DataTable({
        destroy: true,
        data: productosCompatibles,
        columns: [
            { "data": "codigo" },
            { "data": "marca" },
            { "data": "descripcion" },
            { "data": "inventario" }
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        }
    });

    //Seleccionable de la tabla creada
    $('#compatibilidadDataTable tbody').on('click', 'tr', function() {

        var table = $('#compatibilidadDataTable').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) { //Método que deselecciona un elemento de la tabla
            $(this).removeClass('selected');
            //Al no haber nada seleccionado, el botón se inhabilita y el valor del hidden es vacío
            $('#eliminarProductoCompatible').attr("disabled", 'true');
            $("#compatibilidadSelect").val("");
        } else {
            //Método cuando se selecciona un elemento de la tabla.
            compatibilidadDataTable.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = productosCompatibles.indexOf(data);
            //Se le asigna el valor del índice donde está la información
            $("#compatibilidadSelect").val(indice);

            //Se habilita el boton
            $('#eliminarProductoCompatible').removeAttr("disabled");
            $(this).addClass('selected');
        }
    });
}

/**
 * Método para habilitar el botón del kit, solo cuando el producto sea de tipo kit.
 * @param  {[type]} ) {	if($('input[name [description]
 * @return {[type]}   [description]
 */
$('input[name=tipo_producto]').click(function() {
    if ($('input[name=tipo_producto]:checked').val() == "K") {
        $("#productoKit").attr('disabled', false);
    } else {
        $("#productoKit").attr('disabled', true);
    }
});

/**
 * Método que ejecuta la acción de hacer clic sobre el botón eliminar de la vista de productos compatibles
 * @param  {String} ) {	compatibilidadDataTable.row('.selected').remove().draw( false );		$('#eliminarProductoCompatible').attr("disabled",'true')		productosCompatibles.splice($("#compatibilidadSelect").val(), 1); 		$("#compatibilidadSelect").val("");		$("#msjErrorCompatibilidad").hide();	var removidoCompatibilidad [description]
 * @return {[type]}   [description]
 */
$('#eliminarProductoCompatible').click(function() {

    compatibilidadDataTable.row('.selected').remove().draw(false);
    //Se deshabilita el botón.
    $('#eliminarProductoCompatible').attr("disabled", 'true')

    //Se remueve ese elemento del array
    productosCompatibles.splice($("#compatibilidadSelect").val(), 1); // 1 es la cantidad de elemento a eliminar

    //Se hace el campo oculto como vacío.
    $("#compatibilidadSelect").val("");

    //Se muestra el msj de registro removido
    $("#msjErrorCompatibilidad").hide();
    var removidoCompatibilidad = '{"message": "Producto compatible removido del listado correctamente"}';
    successMsj(JSON.parse(removidoCompatibilidad), 'msjOkCompatibilidad', 'formOkCompatibilidad');
});


/**
 * Variable creada para llevar la estructura de Productos Compatibles
 */
var productosKit = new Array(); //Código, Descripción, Cantidad, Precio
var kitDataTable; //Variable que guarda el DataTable de Kid admin

/**
 * Método donde se define el dataTable del kit admin
 * @return {[type]} [description]
 */
var cargarKitDataTable = function() {
    //Inicialización de tabla de compatibilidad.
    kitDataTable = $('#kitDataTable').DataTable({
        destroy: true,
        data: productosKit,
        columns: [
            { "data": "codigo" },
            { "data": "descripcion" },
            { "data": "cantidad" },
            { "data": "precio" }
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        }
    });

    //Seleccionable de la tabla creada
    $('#kitDataTable tbody').on('click', 'tr', function() {

        var table = $('#kitDataTable').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) { //Método que deselecciona un elemento de la tabla
            $(this).removeClass('selected');
            //Al no haber nada seleccionado, el botón se inhabilita y el valor del hidden es vacío
            $('#eliminarProductoKit').attr("disabled", true);
            $("#kitSelect").val("");

            //Limpio el formulario.
            $("#codigo_producto_kit").prop('disabled', false);
            $("#codigo_producto_kit").val("");
            $("#descripcion_producto_kit").val("");
            $("#cantidad_producto_kit").val("");
            $("#precio_producto_kit").val("");
        } else {
            //Método cuando se selecciona un elemento de la tabla.
            kitDataTable.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = productosKit.indexOf(data);

            //Se le asigna el valor del índice donde está la información
            $("#kitSelect").val(indice);


            cargarEditKitAdmin(indice);

            //Se habilita el boton
            $('#eliminarProductoKit').removeAttr("disabled");
            $(this).addClass('selected');
        }
    });
}

//Método para al seleccionar un elemento de la tabla, cargar la información del mismo sobre el formulario para su posible edición
var cargarEditKitAdmin = function(indice) {
    $("#codigo_producto_kit").prop('disabled', true);
    $("#codigo_producto_kit").val(productosKit[indice]['codigo']);
    $("#descripcion_producto_kit").val(productosKit[indice]['descripcion']);
    $("#cantidad_producto_kit").val(productosKit[indice]['cantidad']);
    $("#precio_producto_kit").val(productosKit[indice]['precio']);
}

/**
 * Método para actualizar el DataTable de productos Compatibles.
 * @return {[type]} [description]
 */
var actualizarKitDataTable = function() {
    $('#kitDataTable').dataTable().fnClearTable();
    $('#kitDataTable').dataTable().fnAddData(productosKit);
}

/**
 * Método que ejecuta la acción de hacer clic sobre el botón eliminar de la vista de kit admin
 * @param  {String} ) 
 * @return {[type]}   [description]
 */
$('#eliminarProductoKit').click(function() {

    //Habilito nuevamente la edición del código
    $("#codigo_producto_kit").prop('disabled', false);
    //Limpio el formulario
    $("#codigo_producto_kit").val("");
    $("#descripcion_producto_kit").val("");
    $("#cantidad_producto_kit").val("");
    $("#precio_producto_kit").val("");

    kitDataTable.row('.selected').remove().draw(false);
    //Se deshabilita el botón.
    $('#eliminarProductoKit').attr("disabled", true)

    //Se remueve ese elemento del array
    productosKit.splice($("#kitSelect").val(), 1); // 1 es la cantidad de elemento a eliminar

    //Se hace el campo oculto como vacío.
    $("#kitSelect").val("");

    //Se muestra el msj de registro removido
    $("#msjErrorKit").hide();
    var removidoDelKit = '{"message": "Producto removido del Kit"}';
    successMsj(JSON.parse(removidoDelKit), 'msjOkKit', 'formOkKit');
});

/**
 * Método para al seleccionar un producto del autoComplete de Productos del Kit, haga el seteo de las variables de
 * Codigo, Descripcion, Cantidad, Precio
 * @param  {[type]} $producto_codigo Código del Producto Seleccionado.
 * @return {[type]}                  [description]
 */
var consultarProductoKit = function(producto_codigo) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#listDeposito").val();

    if (producto_codigo == "") {
        producto_codigo = " ";
    }

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/consultarDatosKit/" + producto_codigo;

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

            //Seteando variables según lo encontrado
            if (obj.producto.length > 0) {
                $("#descripcion_producto_kit").val(obj.producto[0].nombre);
                $("#cantidad_producto_kit").val("1");
                $("#precio_producto_kit").val(obj.producto[0].precio_a);
            } else {
                $("#descripcion_producto_kit").val("");
                $("#cantidad_producto_kit").val("");
                $("#precio_producto_kit").val("");
            }

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
}

/**
 * Método para agregar un elemento al kit de forma pacial, no se guarda en BD, es algo local
 * para al guardar el producto allí si hacer el guardado en BD
 * @param  {[type]} ){} [description]
 * @return {[type]}       [description]
 */
$("#agregar-Kit").click(function() {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var codigo = autoCompleteFormatValue($("#codigo_producto_kit").val());

    if (codigo == "") {
        codigo = "-1";
    }

    //Corresponde a una edición de un registro existente, hago el update en el array.
    if ($("#kitSelect").val() != "") {

        productosKit[$("#kitSelect").val()]['cantidad'] = $("#cantidad_producto_kit").val();

        $("#codigo_producto_kit").prop('disabled', false);
        $("#codigo_producto_kit").val("");
        $("#descripcion_producto_kit").val("");
        $("#cantidad_producto_kit").val("");
        $("#precio_producto_kit").val("");
        $("#kitSelect").val("");
        $('#kitDataTable tbody').find('tr').removeClass('selected');
        $('#eliminarProductoKit').attr("disabled", true);

        actualizarKitDataTable();

        //mostrar mensaje de Producto agregado al listado.
        $("#msjErrorKit").hide();
        var editKit = '{"message": "Producto actualizado correctamente"}';
        successMsj(JSON.parse(editKit), 'msjOkKit', 'formOkKit');
    } else {
        //Se valida que el código del producto ingresado sea de un producto existente.
        //Se identifica la ruta
        var route = "/producto/getProductByCodigo/empresa_id/" + empresa_id + "/codigo/" + codigo;
        $.ajax({
            url: route,
            headers: { 'X-CSRF-TOKEN': token },
            type: 'GET',
            dataType: 'json',
            data: {
                empresa_id: empresa_id,
                codigo: codigo
            },
            success: function(obj) {

                if (obj.producto.length > 0) {

                    //Se valida que no esté ya el producto
                    var encontrado = productosKit.filter(function(productosKit) {
                        return productosKit.id === obj.producto[0].id;
                    })[0];

                    if (encontrado != undefined) {

                        //Ya se encuentra el producto en la lista de compatibles
                        $("#msjOkKit").hide();
                        var errorKit = '{"message": "El producto actualmente ya se encuentra dentro del Kit"}';
                        errorMsj(JSON.parse(errorKit), 'msjErrorKit', 'formErrorKit');
                    } else {

                        //Validar que lo que venga en cantidad sea de valor numérico solamente.
                        var cantidad = $("#cantidad_producto_kit").val();
                        if (!isNaN(cantidad)) {
                            productosKit.push({
                                id: obj.producto[0].id,
                                codigo: codigo,
                                descripcion: $("#descripcion_producto_kit").val(),
                                cantidad: $("#cantidad_producto_kit").val(),
                                precio: $("#precio_producto_kit").val()
                            });

                            $("#codigo_producto_kit").val("");
                            $("#descripcion_producto_kit").val("");
                            $("#cantidad_producto_kit").val("");
                            $("#precio_producto_kit").val("");

                            actualizarKitDataTable();

                            //mostrar mensaje de Producto agregado al listado.
                            $("#msjErrorKit").hide();
                            var agregadoKit = '{"message": "Producto agregado al listado correctamente"}';
                            successMsj(JSON.parse(agregadoKit), 'msjOkKit', 'formOkKit');
                        } else {
                            //Mostrar error de indicar que la cantidad debe ser numérica
                            $("#msjOkKit").hide();
                            var errorKit = '{"message": "La cantidad indicada debe ser numérica"}';
                            errorMsj(JSON.parse(errorKit), 'msjErrorKit', 'formErrorKit');
                        }
                    }

                } else {
                    //Mostrar error de indicar producto válido
                    $("#msjOkKit").hide();
                    var errorKit = '{"message": "Debe indicar un producto existente para agregar al listado"}';
                    errorMsj(JSON.parse(errorKit), 'msjErrorKit', 'formErrorKit');
                }

            },
            error: function(obj) {
                //mostrar mensaje de error
                $("#kit-producto .close").click();
                errorMsj(obj, 'msjErrorGral', 'formErrorGral');
            }
        });
    }
});

/**
 * Método para consultar los productos compatibles que tiene el producto seleccionado previamente desde la tabla.
 * 
 * @param  {[type]} producto_id Id del producto seleccionado.
 * @return {[type]}             [description]
 */
var consultarKit = function(producto_id) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#listDeposito").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/producto/" + producto_id + "/consultarProductosKit";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {

            productosKit = new Array();

            //Si trae valores, se agregan al array correspondiente para el manejo de productos compatibles
            if (obj.productoKit.length > 0) {
                $(obj.productoKit).each(function(i, v) { // indice, valor
                    productosKit.push({
                        id: v.id,
                        codigo: v.codigo,
                        descripcion: v.nombre,
                        cantidad: v.cantidad,
                        precio: v.precio_a
                    });
                })
                //Actualizo la tabla (Se redibuja)
                actualizarKitDataTable();
            } else {
                $('#kitDataTable').dataTable().fnClearTable();
            }

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

}

var cargarListsSelEmpresa = function() {
    //alert('empresa_id=' + $('#empresa_id').val() + 'deposito_id=' + $('#deposito_id').val());
    $('#listEmpresa > option[value="' + $('#empresa_id').val() + '"]').attr("selected", true);
    obtenerListDeposito($('#empresa_id').val(), "listDeposito");

};

var cargarLists = function() {
    //cargar combos 
    obtenerListEmpresa();
    cargaGrupos();
    cargaCategorias();
    cargaSubgrupo();
    cargaMarca();

    cargarPaises();
    cargarFabricas();
    cargarUnidadMedida();
    cargarAranceles();
    getProductosActivos();

    consultarTiposDeAjustes($('#empresa_id').val(), 'ajustes_tipo');

    obtenerProveedores();

};

var obtenobtenerPermisoerPermiso = function() {
    var user_id = $("#user_id").val();
    var modulo = $("#modulo").val();
    var token = $("#token").val();
    //Se identifica la ruta
    var route = "/user/getPermiso";
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'POST',
        dataType: 'json',
        data: { user_id: user_id, modulo: modulo },
        success: function(obj) {

            //actualizar dropdown
            if (obj.permiso[0] == '0') {
                //no tiene permiso de accede al modulo
                //redirect
                window.location = '/noauth';
            } else {
                //se mantiene el valor en página
                $("#permiso").val(obj.permiso);
            }

        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};


var obtenerEmpresa = function() {

    var dfd = new $.Deferred();

    var user_id = $("#user_id").val();
    var token = $("#token").val();
    //Se identifica la ruta
    var route = "/empresa/get/" + user_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { user_id: user_id },
        success: function(obj) {

            //actualizar dropdown
            if (jQuery.isEmptyObject(obj.empresas)) {
                //debe seleccionar empresa
                obtenerListEmpresa();
                $('#btnSeleccionarEmpresa').trigger('click');

            } else {
                //imprimir valor de la empresa
                $('#nombre_empresa').html(obj.empresas[0].nombre);
                $('#empresa_id').val(obj.empresas[0].id);
                //
                obtenerDepositoporUsuario($('#user_id').val());
                //
                if ($("#p_inve").val() === "1") {
                    cargarLists();
                }
                //preseleccionar valores de list empresa y deposito

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

///----> Servicios para empresa

var obtenerListEmpresa = function() {

    var token = $("#token").val();
    //Se identifica la ruta
    var route = "/empresa/get/T";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        success: function(obj) {
            //para limpiar el combo
            $('#listEmpresa').find('option').remove();

            //Listado de empresas en ajustes (agregando también la opción de empresa "vacía")
            $('#ajustes_tras_compania').find('option').remove();
            $('#ajustes_tras_compania').append('<option value=""></option>');

            $(obj.empresas).each(function(i, v) { // indice, valor
                $('#listEmpresa').append('<option value="' + v.id + '">' + v.nombre + '</option>');

                //Cargando el listado de empresas al dropDown de ajustes.
                $('#ajustes_tras_compania').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })

            //cargar el list de deposito
            obtenerListDeposito($('#listEmpresa').val(), "listDeposito");

        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

$("#seleccionEmpresaBtn").click(function() {

    //Se obtienen los campos
    var user_id = $("#user_id").val();
    var empresa_id = $("#listEmpresa").val();
    var deposito_id = $("#listDeposito").val();

    //Se identifica la ruta
    var route = "/user/setEmpresa";

    //valor del token erro cc+srf
    var token = $("#token").val();

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'POST',
        dataType: 'json',
        data: { user_id: user_id, empresa_id: empresa_id, deposito_id: deposito_id },
        success: function(obj) {
            //imprimir valor de la empresa;

            $("#nombre_empresa").html($("#listEmpresa option:selected").html());
            $('#empresa_id').val($("#listEmpresa").val());
            $('#deposito_id').val($("#listDeposito").val());
            $("#nombre_deposito").html($("#listDeposito option:selected").html());
            $("#seleccionarEmpresa .close").click();

            cargarLists();

            //limpiar los labels			
            limpiarIconos();
            limpiarIconosLabels();
            var url = '/empresa/' + $('#empresa_id').val() + "/deposito/" + $('#deposito_id').val() + "/producto/getDatatableProductos";
            productosTable.ajax.url(url).load();
            //productosTable.ajax.reload();

        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'error-empresa', 'form-empresa');
        }
    });

});

///---> Servicios para Depositos
var obtenerListDeposito = function(empresa_id, _inputDeposito) {

    var dfd = new $.Deferred();
    var token = $("#token").val();
    //Se identifica la ruta
    var route = "/deposito/getAll/" + empresa_id;

    if (empresa_id == "") {
        $('#' + _inputDeposito).find('option').remove();
    } else {

        $.ajax({
            url: route,
            headers: { 'X-CSRF-TOKEN': token },
            type: 'GET',
            dataType: 'json',
            success: function(obj) {
                //para limpiar el combo
                $('#' + _inputDeposito).find('option').remove();

                $(obj.depositos).each(function(i, v) { // indice, valor
                    $('#' + _inputDeposito).append('<option value="' + v.id + '">' + v.nombre + '</option>');
                })

                //precargar según empresa deposito_id guardado del usuario
                $('#' + _inputDeposito + ' > option[value="' + $('#deposito_id').val() + '"]').attr("selected", true);

                dfd.resolve();

            },
            error: function(obj) {

                //mostrar mensaje de error
                errorMsj(obj, 'msjErrorGral', 'formErrorGral');
            }
        });


    }
    return dfd.promise();
};

var obtenerDepositoporUsuario = function(user_id) {

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
            if ($("#p_inve").val() === "1") {
                //limpiar los labels			
                limpiarIconos();
                limpiarIconosLabels();
                cargarTablaProductos();
                pedidos_CerradosPorProducto_Consultar(0);
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


///---> Servicios para grupos

var cargaGrupos = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/grupo/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#grupo_id').find('option').remove();

            //Agrego un valor vacío
            $('#grupo_id').append('<option value=""></option>');

            $(obj.grupos).each(function(i, v) { // indice, valor
                $('#grupo_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })
        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

$("#crearGrupo").click(function() {

    //Se obtienen los campos
    var nombre = $("#nombre-grupo").val();
    var ganancia_1 = $("#ganancia_1").val();
    var ganancia_2 = $("#ganancia_2").val();
    var ganancia_3 = $("#ganancia_3").val();
    var ganancia_4 = $("#ganancia_4").val();
    var ganancia_5 = $("#ganancia_5").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/grupo";

    //valor del token erro csrf
    var token = $("#token").val();

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'POST',
        dataType: 'json',
        data: {
            nombre: nombre,
            ganancia_1: ganancia_1,
            ganancia_2: ganancia_2,
            ganancia_3: ganancia_3,
            ganancia_4: ganancia_4,
            ganancia_5: ganancia_5,
            empresa_id: empresa_id
        },
        success: function(obj) {

            //actualizar dropdown
            $("#create-Grupo .close").click();

            //limpiar loscamos
            $("#nombre-grupo").val("");
            $("#ganancia_1").val("");
            $("#ganancia_2").val("");
            $("#ganancia_3").val("");
            $("#ganancia_4").val("");
            $("#ganancia_5").val("");

            //actualizar combo
            cargaGrupos();
            //mostrar mensaje existoso


        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'error-grupo', 'form-ErrorGrupo');
        }
    });
});

//--->Servicios de Categorias
var cargaCategorias = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/categoria/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#categoria_id').find('option').remove();
            $('#categoria_id').append('<option value=""></option>');
            $(obj.categorias).each(function(i, v) { // indice, valor
                $('#categoria_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })
        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

$("#crearCategoria").click(function() {

    //Se obtienen los campos
    var nombre = $("#nombre-categoria").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/categoria";

    //valor del token erro csrf
    var token = $("#token").val();

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'POST',
        dataType: 'json',
        data: {
            nombre: nombre,
            empresa_id: empresa_id
        },
        success: function(obj) {

            //actualizar dropdown
            $("#create-Categoria .close").click();

            //limpiar loscamos
            $("#nombre-categoria").val("");

            //actualizar combo
            cargaCategorias();
            //mostrar mensaje existoso


        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'error-categoria', 'form-ErrorCategoria');
        }
    });
});

///--> Servicios para subgrupos
var cargaSubgrupo = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/subgrupo/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#sub_grupo_id').find('option').remove();
            $('#sub_grupo_id').append('<option value=""></option>');
            $(obj.subgrupos).each(function(i, v) { // indice, valor
                $('#sub_grupo_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })
        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

$("#crearSubgrupo").click(function() {

    //Se obtienen los campos
    var nombre = $("#nombre-subgrupo").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/subgrupo";

    //valor del token erro csrf
    var token = $("#token").val();

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'POST',
        dataType: 'json',
        data: {
            nombre: nombre,
            empresa_id: empresa_id
        },
        success: function(obj) {

            //actualizar dropdown
            $("#create-Subgrupo .close").click();

            //limpiar loscamos
            $("#nombre-subgrupo").val("");

            //actualizar combo
            cargaSubgrupo();
            //mostrar mensaje existoso


        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'error-subgrupo', 'form-ErrorSubgrupo');
        }
    });
});

///--> Servicios para marcas
var cargaMarca = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/marca/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#marca_id').find('option').remove();
            $('#marca_id').append('<option value=""></option>');

            $(obj.marcas).each(function(i, v) { // indice, valor
                $('#marca_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })
        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

$("#crearMarca").click(function() {

    //Se obtienen los campos
    var nombre = $("#nombre-marca").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/marca";

    //valor del token erro csrf
    var token = $("#token").val();

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'POST',
        dataType: 'json',
        data: {
            nombre: nombre,
            empresa_id: empresa_id
        },
        success: function(obj) {

            //actualizar dropdown
            $("#create-Marca .close").click();

            //limpiar loscamos
            $("#nombre-marca").val("");

            //actualizar combo
            cargaMarca();
            //mostrar mensaje existoso


        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'error-marca', 'form-ErrorMarca');
        }
    });
});

///---> Servicios para paises
var cargarPaises = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/pais/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#pais_id').find('option').remove();
            $('#pais_id').append('<option value=""></option>');

            $(obj.paises).each(function(i, v) { // indice, valor
                $('#pais_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })

        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

///---> Servicios para Proveedores
var obtenerproveedor_id = function(pais_id) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Validando cuando viene vacío
    if (pais_id == "") {
        pais_id = 0;
    }

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/pais/" + pais_id + "/proveedores";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            pais_id: pais_id
        },
        success: function(obj) {
            //para limpiar el combo
            $('#proveedor_id').find('option').remove();
            $('#proveedor_id').append('<option value=""></option>');

            $(obj.proveedores).each(function(i, v) { // indice, valor
                $('#proveedor_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
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

///---> Servicios para fabricas
var cargarFabricas = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/fabrica/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#fabrica_id').find('option').remove();

            $('#fabrica_id').append('<option value=""></option>');
            $(obj.fabricas).each(function(i, v) { // indice, valor
                $('#fabrica_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })

        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};


///---> Servicios para Unidades de Medidas
var cargarUnidadMedida = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/umedida/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#unidad_medida_id').find('option').remove();

            $('#unidad_medida_id').append('<option value=""></option>');
            $(obj.unidadMedida).each(function(i, v) { // indice, valor
                $('#unidad_medida_id').append('<option value="' + v.id + '">' + v.nombre + '</option>');
            })

        },
        error: function(obj) {

            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

///---> Servicios para Aranceles
var cargarAranceles = function() {
    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/arancel/getAll/" + empresa_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: { empresa_id: empresa_id },
        success: function(obj) {
            //para limpiar el combo
            $('#arancel_id').find('option').remove();

            $('#arancel_id').append('<option value=""></option>');
            $(obj.aranceles).each(function(i, v) { // indice, valor
                $('#arancel_id').append('<option value="' + v.id + '">' + v.monto + '</option>');
            })

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

///---> Servicios para Consultar si un Grupo ya posee un padre.
var consultarPadreDelGrupo = function(grupo_id, status) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se valida si el grupo es null (no se seleccionó ninguno), para enviar ID=0 y eviatar error.
    if (grupo_id == "") {
        grupo_id = 0;
    }

    //Se identifica la ruta
    var route = "/producto/getPadre/empresa/" + empresa_id + "/grupo/" + grupo_id;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            grupo_id: grupo_id
        },
        success: function(obj) {

            $("#padre_grupo").prop('checked', status);

            if (obj.productoPadre.length > 0 || grupo_id == 0) {
                $("#padre_grupo").attr("disabled", true);
            } else {
                $("#padre_grupo").attr("disabled", false);
            }

            //El producto padre es el mismo seleccionado, permitir edidcipon (seleccionar o deseleccionar elemento.)
            if (obj.productoPadre.length > 0 && $("#productoSelect").val() != "") {
                if ($("#productoSelect").val() == obj.productoPadre[0].id) {
                    $("#padre_grupo").attr("disabled", false);
                    $("#padre_grupo").prop('checked', true);
                }
            }

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};


/**
 * Método para consultar los datos ingresados en el formulario que pertenecen a un producto.
 * @return {[obj]} [todos los datos del producto]
 */
function consultarFormProducto() {
    //aqui, hacer el cambio por el Id del producto
    var promo = autoCompleteFormatValue($("#venta_promo").val());
    var producto = {
        //Ficha A
        'codigo': $("#codigo").val(),
        'estatus': $('input[name=estatus]:checked').val(),
        'grupo_id': $("#grupo_id").val(),
        'padre_grupo': $("#padre_grupo").is(':checked'),
        'nombre': $("#nombre").val(),
        'max_dias_venta': $("#max_dias_venta").val(),
        'porc_imp_venta': $("#porc_imp_venta").val(),
        'codigo2': $("#codigo2").val(),
        'barcode': $("#barcode").val(),
        'nombre2': $("#nombre2").val(),
        'categoria_id': $("#categoria_id").val(),
        'sub_grupo_id': $("#sub_grupo_id").val(),
        'marca_id': $("#marca_id").val(),
        'pais_id': $("#pais_id").val(),
        'fabrica_id': $("#fabrica_id").val(),
        'garantia': $("#garantia").val(),
        'proveedor_id': $("#proveedor_id").val(),
        'tipo_producto': $('input[name=tipo_producto]:checked').val(),
        'photo': imagenBlod,
        //Ficha B
        'ancho': $("#ancho").val(),
        'largo': $("#largo").val(),
        'alto': $("#alto").val(),
        'factura_decimal': $("#factura_decimal").is(':checked'),
        'comision_vendedor': $("#comision_vendedor").is(':checked'),
        'comision_comisionista': $("#comision_comisionista").is(':checked'),
        'peso_bruto': $("#peso_bruto").val(),
        'activar_num_serie': $("#activar_num_serie").is(':checked'),
        'peso_neto': $("#peso_neto").val(),
        'unidad_medida_id': $("#unidad_medida_id").val(),
        'cantidad_por_empaque': $("#cantidad_por_empaque").val(),
        'acepta_ajuste_precio': $("#acepta_ajuste_precio").is(':checked'),
        'art_lento_mov': $("#art_lento_mov").is(':checked'),
        'marcar_inventario_minimo': $("#marcar_inventario_minimo").is(':checked'),
        'cantidad_sugerida': $("#cantidad_sugerida").is(':checked'),
        'factura_tiempo': $("#factura_tiempo").is(':checked'),
        'porcentaje_comision': $("#porcentaje_comision").val(),
        'porcentaje_descuento': $("#porcentaje_descuento").val(),
        'suplir_codigo2': $("#suplir_codigo2").is(':checked'),
        'dimesion_precio_fact': $("#dimesion_precio_fact").is(':checked'),
        'arancel_id': $("#arancel_id").val(),
        'lento_movimiento': $("#lento_movimiento").val(),
        'venta_promo': promo,
        'precio': $("#precio").val(),
        'puntos_venta': $("#puntos_venta").val(),
        'id': 0,
    }

    return producto;
}

/**
 * Método para limpiar los datos ingresados en el formulario que pertenece a un producto.
 */
function limpiarFormProducto() {

    //Ficha A
    $("#codigo").val("");

    //estatus
    $('input[name=estatus]').prop('checked', false);
    $("#activo").prop("checked", true);
    $("#grupo_id").val("");
    //Se deshabilita la opción de seleccionar si es padre del grupo, siendo "" no puede ser padre del grupo
    $("#padre_grupo").attr('checked', false);
    $("#padre_grupo").attr("disabled", true);

    $("#nombre").val("");
    $("#max_dias_venta").val("");
    $("#porc_imp_venta").val("");
    $("#codigo2").val("");
    $("#barcode").val("");
    $("#nombre2").val("");
    $("#categoria_id").val("");
    $("#sub_grupo_id").val("");
    $("#marca_id").val("");
    $("#pais_id").val("");
    $("#fabrica_id").val("");
    $("#garantia").val("");

    $("#proveedor_id").val("");

    //Quito cualquier seleccion previa
    $('input[name=tipo_producto]').prop('checked', false);
    $("#Articulo").prop('checked', true);

    $("#imgFormProducto").attr("src", "img/imgNoDisponible.jpg");

    //Ficha B
    $("#ancho").val("");
    $("#largo").val("");
    $("#alto").val("");

    $("#factura_decimal").attr('checked', false);
    $("#comision_vendedor").attr('checked', false);
    $("#comision_comisionista").attr('checked', false);

    $("#peso_bruto").val("");
    $("#activar_num_serie").attr('checked', false);
    $("#peso_neto").val("");
    //$("#unidad_medida_id").val();
    $("#cantidad_por_empaque").val("");
    $("#acepta_ajuste_precio").attr('checked', false);
    $("#art_lento_mov").attr('checked', false);
    $("#marcar_inventario_minimo").attr('checked', false);
    $("#cantidad_sugerida").attr('checked', false);
    $("#factura_tiempo").attr('checked', false);
    $("#porcentaje_comision").val("");
    $("#porcentaje_descuento").val("");
    $("#suplir_codigo2").attr('checked', false);
    $("#dimesion_precio_fact").attr('checked', false);
    //$("#arancel_id").val();
    $("#lento_movimiento").val("");
    $("#venta_promo").val("");
    $("#precio").val("");
    $("#puntos_venta").val("");

    //Cambio de tab a #fichaA
    $('#productoTab a[href="#fichaA"]').tab('show');


    //Quito una posible selección de un producto en la tabla
    //Quito el producto seleccionado
    $('#productoSelect').val("");
    //Desactivo el boton de eliminar.
    $('#eliminar-Producto').attr("disabled", 'true');

    productosCompatibles = new Array();
    productosKit = new Array();
    productosSerie = new Array();

    $("#productoKit").attr('disabled', true);
    $("#serie").attr('disabled', true);
    //OJO preguntar a daniel xq este funciona solo para la tabla 
    // $('#productoTable tbody').find('tr').removeClass('selected');
}

/**
 * Método para cargar los datos en el formulario que pertenecen al un producto seleccionado de la tabla
 * @param  {[array]} $producto [todos los campos de un producto 'clave': 'valor']
 * @return {[type]}           [description]
 */
function cargarFormProducto($producto) {

    //Ficha A
    $("#codigo").val($producto["codigo"]);

    //estatus
    //Quito cualquier selección previa
    $('input[name=estatus]').prop('checked', false);
    //Asigno selección proveniente de BD.
    $producto["estatus"] == 'A' ? $("#activo").prop("checked", true) : $("#inactivo").prop("checked", true);
    $("#grupo_id").val($producto["grupo_id"] == null ? "" : $producto["grupo_id"]);

    //Se aplica o no el check, dependiendo del 2do parametro enviado.
    consultarPadreDelGrupo($("#grupo_id").val(), $producto["padre_grupo"] == "S" ? true : false);

    //Se consulta la ficha ténica del grupo
    fichaDelGrupo($("#grupo_id").val());

    $("#nombre").val($producto["nombre"]);
    $("#max_dias_venta").val($producto["max_dias_venta"]);
    $("#porc_imp_venta").val($producto["porc_imp_venta"]);
    $("#codigo2").val($producto["codigo2"]);
    $("#barcode").val($producto["barcode"]);
    $("#nombre2").val($producto["nombre2"]);
    $("#categoria_id").val($producto["categoria_id"]);
    $("#sub_grupo_id").val($producto["sub_grupo_id"]);
    $("#marca_id").val($producto["marca_id"]);
    $("#pais_id").val($producto["pais_id"] == null ? "" : $producto["pais_id"]);
    $("#fabrica_id").val($producto["fabrica_id"]);
    $("#garantia").val($producto["garantia"]);

    $.when(obtenerproveedor_id($("#pais_id").val())).then(function() {
        $("#proveedor_id").val($producto["proveedor_id"]);
    });


    //Quito cualquier seleccion previa
    $('input[name=tipo_producto]').prop('checked', false);
    //Asigno selección según datos de BD
    switch ($producto["tipo_producto"]) {
        case 'A':
            $("#Articulo").prop("checked", true);
            break;
        case 'S':
            $("#Servicio").prop("checked", true);
            break;
        case 'P':
            $("#Produccion").prop("checked", true);
            break;
        case 'K':
            $("#Kit").prop("checked", true);
            $("#productoKit").attr('disabled', false);
            break;
        case 'V':
            $("#Variable").prop("checked", true);
            break;
    }

    if ($producto["foto"] != null) {
        $("#imgFormProducto").attr("src", $producto["foto"] + "?" + Math.random());
        imagenBlod = "Foto";
    }

    //Ficha B
    $("#ancho").val($producto["ancho"]);
    $("#largo").val($producto["largo"]);
    $("#alto").val($producto["alto"]);

    $("#factura_decimal").prop('checked', $producto["factura_decimal"] == "S" ? true : false);
    $("#comision_vendedor").prop('checked', $producto["comision_vendedor"] == "S" ? true : false);
    $("#comision_comisionista").prop('checked', $producto["comision_comisionista"] == "S" ? true : false);

    $("#peso_bruto").val($producto["peso_bruto"]);
    $("#activar_num_serie").prop('checked', $producto["activar_num_serie"] == "S" ? true : false);
    activarSerie($producto["activar_num_serie"] == "S" ? true : false);
    $("#peso_neto").val($producto["peso_neto"]);
    $("#unidad_medida_id").val($producto["unidad_medida_id"]);
    $("#cantidad_por_empaque").val($producto["cantidad_por_empaque"]);
    $("#acepta_ajuste_precio").prop('checked', $producto["acepta_ajuste_precio"] == "S" ? true : false);
    $("#art_lento_mov").prop('checked', $producto["art_lento_mov"] == "S" ? true : false);
    $("#marcar_inventario_minimo").prop('checked', $producto["marcar_inventario_minimo"] == "S" ? true : false);
    $("#cantidad_sugerida").prop('checked', $producto["cantidad_sugerida"] == "S" ? true : false);
    $("#factura_tiempo").prop('checked', $producto["factura_tiempo"] == "S" ? true : false);
    $("#porcentaje_comision").val($producto["porcentaje_comision"]);
    $("#porcentaje_descuento").val($producto["porcentaje_descuento"]);
    $("#suplir_codigo2").prop('checked', $producto["suplir_codigo2"] == "S" ? true : false);
    $("#dimesion_precio_fact").prop('checked', $producto["dimesion_precio_fact"] == "S" ? true : false);
    $("#arancel_id").val();
    $("#lento_movimiento").val($producto["lento_movimiento"]);
    $("#venta_promo").val($producto["venta_promo"]);
    $("#precio").val($producto["precio"]);
    $("#puntos_venta").val($producto["puntos_venta"]);

    //Cambio de tab a #fichaA
    $('#productoTab a[href="#fichaA"]').tab('show');

}


//--->Método para procesar el formulario de guardar Producto / Inventario.
var formProducto;
if ($("#p_inve").val() === "1") {
    formProducto = consultarFormProducto();
}
$("#guardarProducto").click(function() {

    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var trash = '0';


    var formProducto = consultarFormProducto();
    var formInventario = consultarFormInventario();

    formProducto["trash"] = trash;
    formProducto["empresa_id"] = empresa_id;
    formProducto["compatibilidad"] = productosCompatibles;
    formProducto["kitAdmin"] = productosKit;
    formProducto["series"] = productosSerie;

    //Se identifica la ruta
    var route = "/producto";
    var type = "POST";
    var data = {
        formProducto: formProducto,
        formInventario: formInventario
    }

    //Se valida si estamos en el caso de un update (que haya un producto seleccionado) al guardar.
    if ($("#productoSelect").val() != "") {
        var id = $("#productoSelect").val();
        formProducto["id"] = id;
        type = "PUT";
        route = "/producto/" + id;
        data = {
            formProducto: formProducto,
            id: id,
            formInventario: formInventario
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
            limpiarFormProducto();

            //Quito la posible seleccion del producto
            productosTable.rows().deselect();

            limpiarFormInventario();
            //Dejar la tabla vacía
            $('#compatibilidadDataTable').dataTable().fnClearTable();
            $('#kitDataTable').dataTable().fnClearTable();
            $('#serieDataTable').dataTable().fnClearTable();

            //OJO recargar TABLA
            // $('#productoTable').DataTable().ajax.reload();
            //limpiar los labels			
            limpiarIconos();
            limpiarIconosLabels();
            productosTable.ajax.reload();
            successMsj(obj, 'msjOkGral', 'formOkGral');

            imagenBlod = null;
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
});


///----> Servicios para productos
var productosTable;

/**
 * arcarrero este código fue comentado, se hizo la solicitud de un cambio debido a que la tabla principal duraba mucho
 * para realizar la carga de la data, por instrucciones de gerardo, se manda a simplificar la data que se carga en la tabla
 * y una vez seleccionado alguno de los productos, y precionando otro botón de la vista, el usuario podrá observar
 * la información restante. Para ello se hará uso del mismo nombre del método para simplificar la nueva implementación
 * y los posteriores llamados a la carga de esta tabla.
 */
/*
var cargarTablaProductos = function(){
	//tabla de productos
	var valores = $('#empresa_id').val() + ":" + $('#deposito_id').val();
	var token = $("#token").val();
    productosTable = $('#productoTable').DataTable({
            "processing": true,
            "serverSide": true,
            "ajax": {
            	headers: {'X-CSRF-TOKEN': token},
            	url: '/producto/getDatatable/' + valores,
            	type: "POST"
        	},
            "columns": [
                {data: 'codigo_producto', name: 'codigo_producto'},
                {data: 'columna_a', name: 'columna_a', orderable: false, searchable: false},
                {data: 'columna_b', name: 'columna_b', orderable: false, searchable: false},
                {data: 'columna_c', name: 'columna_c', orderable: false, searchable: false},
                {data: 'columna_d', name: 'columna_d', orderable: false, searchable: false},
                {data: 'columna_e', name: 'columna_e', orderable: false, searchable: false},
                {data: 'nombre_producto', name: 'nombre_producto'},
                {data: 'itotal', name: 'itotal', orderable: false, searchable: false},
                {data: 'disponible', name: 'disponible', orderable: false, searchable: false},
                {data: 'ubicacion', name: 'ubicacion'},
                {data: 'codigo2', name: 'codigo2'},
                {data: 'comprames3', name: 'comprames3', orderable: false, searchable: false},
                {data: 'comprames2', name: 'comprames2', orderable: false, searchable: false},
                {data: 'comprames1', name: 'comprames1', orderable: false, searchable: false},
                {data: 'comprames', name: 'comprames', orderable: false, searchable: false},
                {data: 'ventames3', name: 'ventames3', orderable: false, searchable: false},
                {data: 'ventames2', name: 'ventames2', orderable: false, searchable: false},
                {data: 'ventames1', name: 'ventames1', orderable: false, searchable: false},
                {data: 'ventames', name: 'ventames', orderable: false, searchable: false}
            ],
            "language": {
                "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
            },
            "scrollX": true,
            "columnDefs": [
		      { "width": "150px", "targets": 0 },
		      { "width": "20px", "targets": 1 },
		      { "width": "20px", "targets": 2 },
		      { "width": "20px", "targets": 3 },
		      { "width": "20px", "targets": 4 },
		      { "width": "20px", "targets": 5 },
		      { "width": "300px", "targets": 6 },
		      { "width": "100px", "targets": 7 },
		      { "width": "100px", "targets": 8 },
		      { "width": "150px", "targets": 9 },
		      { "width": "100px", "targets": 10 },
		      { "width": "100px", "targets": 11 },
		      { "width": "100px", "targets": 12 },
		      { "width": "100px", "targets": 13 },
		      { "width": "100px", "targets": 14 },
		      { "width": "100px", "targets": 15 },
		      { "width": "100px", "targets": 16 },
		      { "width": "100px", "targets": 17 },
		      { "width": "100px", "targets": 18 }
		    ],
	        "autoWidth": false,
	        "fixedColumns": {
		        "leftColumns": 7
		    },
		    "select": {
	            style: 'single'
	        },
	        "pageLength": 5,
	        "lengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
	        "responsive": true
        });


    $('#productoTable tbody').on( 'click', 'tr', function () {
    	var table = $('#productoTable').DataTable();
    	var data = table.row( this ).data();
    	if ( !($(this).hasClass('selected') )) {
    		//cuando la fila no esta seleccionada el usuario hace clic
    		//llamar servicio para cargar datos del producto en ficha A y B
    		//busca el producto
    		$.when( productoByCodigo(data['codigo_producto']) ).then(function(){
    			//Busca la compatibilidad una vez haya buscando el producto.
				consultarCompatibilidad($('#productoSelect').val());
				//Busca los datos del kit
				consultarKit($('#productoSelect').val());
				//Busca los datos de # de Serie
				consultarSerie($('#productoSelect').val());
				//busca el inventario del producto
				//getInventario($('#productoSelect').val());
				
				//Se inicializan los arrays utilizados para manejar las transacciones
				transacciones = new Array();
				transaccionesFiltro = new Array();

				//Se hace un llamado de los 3 métodos para llenar las transacciones, Compra, Venta y Ajuste
				$.when(consultarTransaccionesCompra($('#productoSelect').val()) , consultarTransaccionesVenta($('#productoSelect').val()), consultarTransaccionesAjuste($('#productoSelect').val()))
					.done(function(){
					transaccionesFiltro = $.extend(true, [], transacciones);

					//Actualizo la tabla de transacciones (temporal).
					if(transaccionesFiltro.length>0){
				    	actualizarTransaccionDataTable();
					}else{
						$('#transaccionDataTable').dataTable().fnClearTable();
					}

					calcularContadores(transaccionesFiltro);
				});
				

			});
    		
    		
    	}else{
    		
    		//Limpiar todos los campos del formulario.
    		limpiarFormProducto();

    		limpiarFormInventario();

    		//Se inicializan los arrays utilizados para manejar las transacciones
			transacciones = new Array();
			transaccionesFiltro = new Array();

			//Se hace la limpieza de la tabla
			$('#transaccionDataTable').dataTable().fnClearTable();

			//Se inicializan contadores
			inicializarTransaccionesContadores();
    	}
	    
	} );
 

};*/

/**
 * Método para manejar la carga de productos en la vista principal del inventario.
 * @return {[type]} [description]
 */
var cargarTablaProductos = function() {
    //tabla de productos
    var empresa_id = $('#empresa_id').val();
    var deposito_id = $('#deposito_id').val();
    var token = $("#token").val();

    productosTable = $('#productoTable').DataTable({
        "processing": true,
        "serverSide": true,
        "scrollX": true,
        "autoWidth": false,
        "pageLength": 5,
        "lengthMenu": [
            [5, 10, 25, 50, -1],
            [5, 10, 25, 50, "All"]
        ],
        "ajax": "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/producto/getDatatableProductos",
        "columns": [
            { data: 'id', name: 'id', visible: false, orderable: false, searchable: false },
            { data: 'codigo', name: 'codigo' },
            { data: 'codigo2', name: 'codigo2' },
            { data: 'nombre', name: 'nombre' },
            { data: 'disponible', name: 'disponible', orderable: false, searchable: false },
            { data: 'ipedido', name: 'ipedido', orderable: false, searchable: false },
            { data: 'ubicacion', name: 'ubicacion', orderable: false, searchable: false },
            { data: 'precio_a', name: 'precio_a', orderable: false, searchable: false },
            { data: 'precio_b', name: 'precio_b', orderable: false, searchable: false },
            { data: 'cif', name: 'cif', orderable: false, searchable: false }
        ],
        "language": {
            "url": "//cdn.datatables.net/plug-ins/1.10.13/i18n/Spanish.json"
        },
        "columnDefs": [
            { width: '120px', "targets": 1 },
            { width: '120px', "targets": 2 },
            { width: '250px', "targets": 3 },
            { width: '60px', "targets": 4 },
            { width: '40px', "targets": 5 },
            { width: '40px', "targets": 6 },
            { width: '40px', "targets": 7 },
            { width: '40px', "targets": 8 },
            { width: '40px', "targets": 9 }
        ],
        "fixedColumns": {
            "leftColumns": 4
        },
        "select": {
            style: 'single'
        },
    });

    $('#productoTable tbody').on('click', 'tr', function() {

        var table = $('#productoTable').DataTable();
        var data = table.row(this).data();

        if (!($(this).hasClass('selected'))) {
            //cuando la fila no esta seleccionada el usuario hace clic
            //llamar servicio para cargar datos del producto en ficha A y B
            //busca el producto
            $.when(productoByCodigo(data['codigo'])).then(function() {
                //Busca la compatibilidad una vez haya buscando el producto.
                consultarCompatibilidad($('#productoSelect').val());
                //Busca los datos del kit
                consultarKit($('#productoSelect').val());
                //Busca los datos de # de Serie
                consultarSerie($('#productoSelect').val());
                //Buscar Iconos
                consultarIconos($('#productoSelect').val());

                //Se inicializan los arrays utilizados para manejar las transacciones
                transacciones = new Array();
                transaccionesFiltro = new Array();

                //Se hace un llamado de los 3 métodos para llenar las transacciones, Compra, Venta y Ajuste
                $.when(consultarTransaccionesCompra($('#productoSelect').val()), consultarTransaccionesVenta($('#productoSelect').val()), consultarTransaccionesAjuste($('#productoSelect').val()))
                    .done(function() {
                        transaccionesFiltro = $.extend(true, [], transacciones);

                        //Actualizo la tabla de transacciones (temporal).
                        if (transaccionesFiltro.length > 0) {
                            actualizarTransaccionDataTable();
                        } else {
                            $('#transaccionDataTable').dataTable().fnClearTable();
                        }

                        calcularContadores(transaccionesFiltro);
                    });
            });

        } else {
            //Limpiar todos los campos del formulario.
            limpiarFormProducto();
            limpiarFormInventario();
            //limpiar los labels			
            limpiarIconos();
            limpiarIconosLabels();

            //Se inicializan los arrays utilizados para manejar las transacciones
            transacciones = new Array();
            transaccionesFiltro = new Array();

            //Se hace la limpieza de la tabla
            $('#transaccionDataTable').dataTable().fnClearTable();

            //Se inicializan contadores
            inicializarTransaccionesContadores();
        }
    });
};

var productoByCodigo = function(codigo_producto) {

    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    //Se identifica la ruta
    var route = "/producto/getProductByCodigo/empresa_id/" + empresa_id + "/codigo/" + codigo_producto;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            codigo: codigo_producto
        },
        success: function(obj) {
            //alert('Producto obtenido ' + obj.producto[0].nombre);
            //Limpiar si hay campos en el formulario
            limpiarFormProducto();
            //setear aquí todos los valores del producto (cargarlos en el formulario)
            cargarFormProducto(obj.producto[0]);
            $('#eliminar-Producto').removeAttr("disabled");

            //Se carga el código del producto en el campo oculto.
            $('#productoSelect').val(obj.producto[0].id);

            //cargar Inventario
            getInventario(obj.producto[0].id);

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};


//Se carga la configuración del autoComplete del formulario del producto FICHA B
var options = {
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
        }
    },
    theme: "dark", //dark, blue, purple, yellow, blue-light, green-light, bootstrap
    adjustWidth: false,
};


// Servicio para consultar los productos que se encuentran activos y cargar el autocomplete mostrando código - descripción
var getProductosActivos = function() {
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
                options.data[i] = { codigo: v.codigo, nombre: v.nombre, codigo2: v.codigo2, barcode: v.barcode }; //Agregando al autocomplete codigo, descripcion, codigo2, barcode
            })

            //Aplicando el auto complete al campo venta_promo
            $("#venta_promo").easyAutocomplete(options);
            $("#codigo_compatible").easyAutocomplete(options);
            $("#codigo_producto_kit").easyAutocomplete(options);
            //AutoComplete para Los Ajustes
            $("#ajustes_codigo").easyAutocomplete(options);
            //Autocomplete para los pedidos
            $("#pedido_producto_codigo").easyAutocomplete(options);
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

//Pre cargar la imagen en el div de la ficha.
$('#cargarImagen').click(function() {

    //Se optiene el resultado de la imagen cortada
    $crooperImage.croppie('result', {
        type: 'canvas',
        size: 'viewport'
    }).then(function(resp) {

        //Se captura la imagen cortada
        imagenBlod = resp;
        this.picture = $("#inventario_image img");
        var image = new Image();
        image.src = resp;
        //Se asigna al campo destinado para ello en la fichaA
        this.picture.attr('src', image.src);

    });

    //Se oculta el modal
    $("#create-Imagen").modal("hide");

    //Se destruye el crop.
    $('#main-cropper').croppie('destroy');
});

var imagenBlod = null; //Variable utilizada para guardar la imagen en el sistema (blob)

var $crooperImage = null; //Variable que se encargará de lo relacionado al corte de la imagen

/**
 * Método que se encarga de cargar en el cropie la imagen seleccionada.
 * @param  {[type]} input [description]
 * @return {[type]}       [description]
 */
function readFile(input) {

    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function(e) {
            $('#main-cropper').croppie('bind', {
                url: e.target.result
            });
        }

        reader.readAsDataURL(input.files[0]);
    }
}

/**
 * Método que se encarga de hacer el llamado a cargar el archivo, cuando el input-file tiene un cambio
 * @param  {[type]} ) {            	readFile(this); } [description]
 * @return {[type]}   [description]
 */
$('.actionUpload input').on('change', function() {
    readFile(this);
});


/**
 * Método que se ejecuta al hacer click para abrir el modal donde se hará el corte de la imagen.
 */
$('#createImagenModal').click(function() {
    //Instanciando al JS de cortar imagenes
    $crooperImage = $('#main-cropper').croppie({
        enableExif: true,
        viewport: { width: 250, height: 250 },
        boundary: { width: 400, height: 400 },
        showZoomer: true,
    });
});

//Eliminar media
$('#eliminar-Media').click(function() {
    $('#imgFormProducto').attr("src", "img/imgNoDisponible.jpg");
    imagenBlod = "SinFoto";
});

//Método para limpiar el formulario de productos.
$('#limpiar-Formulario').click(function() {
    limpiarFormProducto();

    //Quito la posible seleccion del producto
    //limpiar los labels			
    limpiarIconos();
    limpiarIconosLabels();
    productosTable.rows().deselect();

    //Dejar la tabla vacía
    $('#compatibilidadDataTable').dataTable().fnClearTable();
    $('#kitDataTable').dataTable().fnClearTable();
    $('#serieDataTable').dataTable().fnClearTable();

    limpiarFormInventario();
});

//Método para eliminar el producto seleccionado. (DE MANERA LÓGICA, NO SE BORRA DE BD)
$('#eliminar-Producto').click(function() {

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();
    var producto_id = $('#productoSelect').val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/eliminacionLogica/" + producto_id;

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'PUT',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            producto_codigo: producto_id
        },
        success: function(obj) {
            //Recargar la tabla.
            //OJO, DANIEL (Preguntar si no deberían solo cargarse en la tabla productos con TRASH='0' => visibles), se haría a nivel de consulta? creo que sería más facil a nivel de la vista.
            //cargarTablaProductos();

            //Quitar seleción de la tabla (quizá recargando la tabla se quite...)
            //OJO
            //limpiar los labels			
            limpiarIconos();
            limpiarIconosLabels();
            productosTable.ajax.reload();

            //Limpiar los campos del formulario.
            limpiarFormProducto();
            limpiarFormInventario();
            successMsj(obj, 'msjOkGral', 'formOkGral');
        },
        error: function(obj) {
            //mostrar mensaje de error.
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
});

/**
 * Variable creada para llevar la estructura de la fichaDelGrupo
 */
var fichaGrupo = new Array(); //id, nombre, valor

/**
 * Servicio para consultar la ficha del grupo.
 * @param  {[type]} grupo_id grupo_id proveniente de la vista.
 */
var fichaDelGrupo = function(grupo_id) {

    var token = $("#token").val();
    var empresa_id = $('#empresa_id').val();

    if (grupo_id == "") {
        grupo_id = 0;
    }

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/grupo_id/" + grupo_id + "/fichaGrupoAll";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            grupo_id: grupo_id
        },
        success: function(obj) {

            //Inicializo el Nro que aparecerá en la tabla.			
            var nro = 0;
            //Inicializo el array
            fichaGrupo = new Array();

            //Se hace un ciclo por cada dato obtenido.
            $(obj.fichaGrupo).each(function(i, v) { // indice, valor
                nro++;
                var aux = { nro: nro, id: v.id, nombre: v.nombre };

                //Se agregan los elementos a la variable global para luego mostrarlos en la tabla.
                fichaGrupo.push({ nro: nro, id: v.id, nombre: v.nombre });
            })

            //Si el vector contiene valores, el botón se habilita, coso contrario se deshabilita.
            if (fichaGrupo.length > 0) {
                $("#ficha_grupo").attr('disabled', false);
            } else {
                $("#ficha_grupo").attr('disabled', true);
            }

            $('#fichaGrupoDataTable').DataTable({
                destroy: true,
                data: fichaGrupo,
                columns: [
                    { "data": "nro" },
                    { "data": "id" },
                    { "data": "nombre" }
                ],
                "columnDefs": [{
                    "targets": [1],
                    "visible": false,
                    "searchable": false
                }],
                "language": {
                    "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
                }
            });
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
};

/**
 * Método para agregar una "compatibilidad parcial", no se guarda en BD, es algo locol, 
 * para al guardar el producto allí si hacer el guardado en BD
 * @param  {[type]} ){} [description]
 * @return {[type]}       [description]
 */
$("#agregar-Compatibilidad").click(function() {

    //Se valida que el código del producto ingresado sea de un producto existente.

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var codigo = autoCompleteFormatValue($("#codigo_compatible").val());

    if (codigo == "") {
        codigo = "-1";
    }

    //Se identifica la ruta
    var route = "/producto/getProductByCodigo/empresa_id/" + empresa_id + "/codigo/" + codigo;
    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            codigo: codigo
        },
        success: function(obj) {

            if (obj.producto.length > 0) {

                //Se valida que no esté ya el producto
                var encontrado = productosCompatibles.filter(function(productosCompatibles) {
                    return productosCompatibles.id === obj.producto[0].id;
                })[0];

                if (encontrado != undefined) {

                    //Ya se encuentra el producto en la lista de compatibles
                    $("#msjOkCompatibilidad").hide();
                    var errorCompatibilidad = '{"message": "El producto actualmente ya se encuentra entre los compatibles"}';
                    errorMsj(JSON.parse(errorCompatibilidad), 'msjErrorCompatibilidad', 'formErrorCompatibilidad');
                } else {
                    //validar que no esté ya el producto...
                    productosCompatibles.push({
                        id: obj.producto[0].id,
                        codigo: codigo,
                        marca: $("#marca_compatible").val(),
                        descripcion: $("#descripcion_compatible").val(),
                        inventario: $("#inventario_compatible").val()
                    });

                    $("#codigo_compatible").val("");
                    $("#marca_compatible").val("");
                    $("#inventario_compatible").val("");
                    $("#descripcion_compatible").val("");

                    actualizarCompatibilidadDataTable();

                    //mostrar mensaje de Producto agregado al listado.
                    $("#msjErrorCompatibilidad").hide();
                    var agregadoCompatibilidad = '{"message": "Producto compatible agregado al listado correctamente"}';
                    successMsj(JSON.parse(agregadoCompatibilidad), 'msjOkCompatibilidad', 'formOkCompatibilidad');
                }

            } else {
                //Mostrar error de indicar producto válido
                $("#msjOkCompatibilidad").hide();
                var errorCompatibilidad = '{"message": "Debe indicar un producto existente para agregar al listado"}';
                errorMsj(JSON.parse(errorCompatibilidad), 'msjErrorCompatibilidad', 'formErrorCompatibilidad');
            }

        },
        error: function(obj) {
            //mostrar mensaje de error
            $("#compatibilidad-producto .close").click();
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
});

/**
 * Método para al seleccionar un producto del autoComplete de Productos Compatibles, haga el seteo de las variables de
 * Marca, Inventario y Descripción.
 * @param  {[type]} $producto_codigo Código del Producto Seleccionado.
 * @return {[type]}                  [description]
 */
var consultarProductoCompatible = function(producto_codigo) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#listDeposito").val();

    if (producto_codigo == "") {
        producto_codigo = " ";
    }

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/consultarDatosCompatibilidad/" + producto_codigo;

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

            //Seteando variables según lo encontrado
            if (obj.producto.length > 0) {
                $("#marca_compatible").val(obj.producto[0].marca);
                $("#inventario_compatible").val(obj.producto[0].disponible);
                $("#descripcion_compatible").val(obj.producto[0].nombre);
            } else {
                $("#marca_compatible").val("");
                $("#inventario_compatible").val("");
                $("#descripcion_compatible").val("");
            }

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });
}

/**
 * Método para consultar los productos compatibles que tiene el producto seleccionado previamente desde la tabla.
 * 
 * @param  {[type]} producto_id Id del producto seleccionado.
 * @return {[type]}             [description]
 */
var consultarCompatibilidad = function(producto_id) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#listDeposito").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/producto/" + producto_id + "/consultarProductosCompatibles";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {

            productosCompatibles = new Array();

            //Si trae valores, se agregan al array correspondiente para el manejo de productos compatibles
            if (obj.productoCompatible.length > 0) {
                $(obj.productoCompatible).each(function(i, v) { // indice, valor
                    productosCompatibles.push({
                        id: v.id,
                        codigo: v.codigo,
                        marca: v.marca,
                        descripcion: v.nombre,
                        inventario: v.disponible
                    });
                })

                //Actualizo la tabla (Se redibuja)
                actualizarCompatibilidadDataTable();
            } else {
                $('#compatibilidadDataTable').dataTable().fnClearTable();
            }

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

}

/**
 * Método para actualizar el DataTable de productos Compatibles.
 * @return {[type]} [description]
 */
var actualizarCompatibilidadDataTable = function() {
    $('#compatibilidadDataTable').dataTable().fnClearTable();
    $('#compatibilidadDataTable').dataTable().fnAddData(productosCompatibles);
}

/**
 * Variable creada para llevar la estructura de Productos con SERIE
 */
var productosSerie = new Array(); //Serie, Fact Compra, Fact Venta, Doc N/C, Depo
var serieDataTable; //Variable que guarda el DataTable de productos en Serie

/**
 * Método donde se define el dataTable del kit admin
 * @return {[type]} [description]
 */
var cargarSerieDataTable = function() {
    //Inicialización de tabla de compatibilidad.
    serieDataTable = $('#serieDataTable').DataTable({
        destroy: true,
        data: productosSerie,
        columns: [
            { "data": "nro" },
            { "data": "serie" },
            { "data": "fact_c" },
            { "data": "fact_v" },
            { "data": "doc" },
            { "data": "depo" }
        ],
        columnDefs: [{
            "searchable": false,
            "orderable": false,
            "targets": 0
        }],
        language: {
            "url": "//cdn.datatables.net/plug-ins/1.10.15/i18n/Spanish.json"
        }
    });

    //Método que lleva el orden de la numeración
    serieDataTable.on('order.dt search.dt', function() {
        serieDataTable.column(0, { search: 'applied', order: 'applied' }).nodes().each(function(cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();

    //Seleccionable de la tabla creada
    $('#serieDataTable tbody').on('click', 'tr', function() {

        var table = $('#serieDataTable').DataTable();
        var data = table.row(this).data();

        if ($(this).hasClass('selected')) { //Método que deselecciona un elemento de la tabla
            $(this).removeClass('selected');
            // Al no haber nada seleccionado, el botón se inhabilita y el valor del hidden es vacío
            $('#eliminarSerie').attr("disabled", true);
            $("#serieSelect").val("");

            //Limpio el formulario.
            $("#serie_serie").val("");
            $("#serie_fact_c").val("");
            $("#serie_fact_v").val("");
            $("#serie_doc").val("");
            $("#serie_depo").val("");
        } else {
            //Método cuando se selecciona un elemento de la tabla.
            serieDataTable.$('tr.selected').removeClass('selected');

            //Se busca donde se encuentra el objeto dentro del array.
            var indice = productosSerie.indexOf(data);

            //Se le asigna el valor del índice donde está la información
            $("#serieSelect").val(indice);


            cargarEditSerie(indice);

            //Se habilita el boton
            $('#eliminarSerie').removeAttr("disabled");
            $(this).addClass('selected');
        }
    });
}

/**
 * Método para agregar una serie de forma pacial, no se guarda en BD, es algo local,
 * para al guardar el producto allí si hacer el guardado en BD
 * @param  {[type]} ){} [description]
 * @return {[type]}       [description]
 */
$("#agregar-Serie").click(function() {

    //Se valida que el mismo número de serie no haya sido agregado previamente.
    var encontrado = productosSerie.filter(function(serie) {
        return serie.serie == $("#serie_serie").val();
    })[0];

    //Corresponde a una edición de un registro existente, hago el update en el array.
    if ($("#serieSelect").val() != "") {

        //Si el registro no se encuentra dentro del array o es el mismo que está editando
        if (encontrado == undefined || productosSerie[$("#serieSelect").val()]['serie'] == $("#serie_serie").val()) {

            //Se hace el update de cada campo
            productosSerie[$("#serieSelect").val()]['serie'] = $("#serie_serie").val();
            productosSerie[$("#serieSelect").val()]['fact_c'] = $("#serie_fact_c").val();
            productosSerie[$("#serieSelect").val()]['fact_v'] = $("#serie_fact_v").val();
            productosSerie[$("#serieSelect").val()]['doc'] = $("#serie_doc").val();
            productosSerie[$("#serieSelect").val()]['depo'] = $("#serie_depo").val();

            //Se limpia el formulario
            $("#serie_serie").val("");
            $("#serie_fact_c").val("");
            $("#serie_fact_v").val("");
            $("#serie_doc").val("");
            $("#serie_depo").val("");
            $("#serieSelect").val("");

            //Se actualiza la tabla
            actualizarSerieDataTable();

            //Quito la selección de la tabla
            $('#serieDataTable tbody').find('tr').removeClass('selected');
            //Deshabilito la opción de eliminar
            $('#eliminarSerie').attr("disabled", true);

            //mostrar mensaje de Serie Editada Correctamente
            $("#msjErrorSerie").hide();
            var editSerie = '{"message": "Serie editada correctamente"}';
            successMsj(JSON.parse(editSerie), 'msjOkSerie', 'formOkSerie');

        } else {
            //Ya se encuentra ese número de serie
            $("#msjOkSerie").hide();
            var errorSerie = '{"message": "Ya existe este número de serie"}';
            errorMsj(JSON.parse(errorSerie), 'msjErrorSerie', 'formErrorSerie');
        }

    } else {

        if (encontrado != undefined) {

            //Ya se encuentra ese número de serie
            $("#msjOkSerie").hide();
            var errorSerie = '{"message": "Ya existe este número de serie"}';
            errorMsj(JSON.parse(errorSerie), 'msjErrorSerie', 'formErrorSerie');
        } else {
            productosSerie.push({
                nro: " ",
                serie: $("#serie_serie").val(),
                fact_c: $("#serie_fact_c").val(),
                fact_v: $("#serie_fact_v").val(),
                doc: $("#serie_doc").val(),
                depo: $("#serie_depo").val()
            });

            //Limpio formulario
            $("#serie_serie").val("");
            $("#serie_fact_c").val("");
            $("#serie_fact_v").val("");
            $("#serie_doc").val("");
            $("#serie_depo").val("");

            //Actualizo la tabla
            actualizarSerieDataTable();

            //Quito la selección de la tabla
            $('#serieDataTable tbody').find('tr').removeClass('selected');
            //Deshabilito la opción de eliminar
            $('#eliminarSerie').attr("disabled", true);

            //mostrar mensaje de Serie Agregada al Listado
            $("#msjErrorSerie").hide();
            var agregadoSerie = '{"message": "Serie agregada al listado correctamente"}';
            successMsj(JSON.parse(agregadoSerie), 'msjOkSerie', 'formOkSerie');
        }

    }
});

/**
 * Método para actualizar el DataTable de Series.
 * @return {[type]} [description]
 */
var actualizarSerieDataTable = function() {
    $('#serieDataTable').dataTable().fnClearTable();
    $('#serieDataTable').dataTable().fnAddData(productosSerie);
}

//Método para al seleccionar un elemento de la tabla Serie DataTable, cargar la información del mismo sobre el formulario para su posible edición
var cargarEditSerie = function(indice) {
    $("#serie_serie").val(productosSerie[indice]['serie']);
    $("#serie_fact_c").val(productosSerie[indice]['fact_c']);
    $("#serie_fact_v").val(productosSerie[indice]['fact_v']);
    $("#serie_doc").val(productosSerie[indice]['doc']);
    $("#serie_depo").val(productosSerie[indice]['depo']);
}

/**
 * Método que ejecuta la acción de hacer clic sobre el botón eliminar de la vista de números de serie
 * @param  {String} )		productosCompatibles.splice($("#compatibilidadSelect").val(), 1); 		$("#compatibilidadSelect").val("");		$("#msjErrorCompatibilidad").hide();	var removidoCompatibilidad [description]
 * @return {[type]}   [description]
 */
$('#eliminarSerie').click(function() {

    serieDataTable.row('.selected').remove().draw(false);

    //Se deshabilita el botón.
    $('#eliminarSerie').attr("disabled", true)

    //Se remueve ese elemento del array
    productosSerie.splice($("#serieSelect").val(), 1); // 1 es la cantidad de elemento a eliminar

    //Se hace el campo oculto como vacío.
    $("#serieSelect").val("");

    //Se limpia el formulario
    $("#serie_serie").val("");
    $("#serie_fact_c").val("");
    $("#serie_fact_v").val("");
    $("#serie_doc").val("");
    $("#serie_depo").val("");

    //Se muestra el msj de registro removido
    $("#msjErrorSerie").hide();
    var removidoDeLaSerie = '{"message": "Serie Removida del Listado"}';
    successMsj(JSON.parse(removidoDeLaSerie), 'msjOkSerie', 'formOkSerie');
});

/**
 * Método para consultar los posibles números de serie del producto
 * 
 * @param  {[type]} producto_id Id del producto seleccionado.
 * @return {[type]}             [description]
 */
var consultarSerie = function(producto_id) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/producto/" + producto_id + "/numerosDeSerie";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            producto_id: producto_id
        },
        success: function(obj) {

            productosSerie = new Array();

            //Si trae valores, se agregan al array correspondiente para el manejo de productos compatibles
            if (obj.serie.length > 0) {
                $(obj.serie).each(function(i, v) { // indice, valor
                    productosSerie.push({
                        nro: " ",
                        serie: v.numero_serie,
                        fact_c: v.factura_compra,
                        fact_v: v.factura_venta,
                        doc: v.nota_credito,
                        depo: v.deposito
                    });
                })
                //Actualizo la tabla (Se redibuja)
                actualizarSerieDataTable();
            } else {
                $('#serieDataTable').dataTable().fnClearTable();
            }

        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

}

/**
 * Método para habilitar o deshabilitar el botón de Números de serie dependiendo del check de "Activar control de números de serie"
 * @param  {[type]} serie Valor del check
 * @return {[type]}       [description]
 */
var activarSerie = function(serie) {
    if (serie == true) {
        $("#serie").attr('disabled', false);
    } else {
        $("#serie").attr('disabled', true);
    }
}

///--->Funciones generales
var errorMsj = function(_json, _panel, _form) {

    $("#msjOkGral").hide();
    // alert(_json.status);
    $("#" + _panel).fadeIn();
    errorsHtml = '<ul>';
    switch (_json.status) {

        case 401:
            //Unauthorized: Access is denied due to invalid credentials
            break;

        case 400:
            //400 Bad Request The server cannot or will not process the request due to an apparent client error
            break;

        case 422:

            //422 Unprocessable Entity The request was well-formed but was unable to be followed due to semantic errors
            //procesar error
            var errors = _json.responseJSON; //obtener la data del error
            //construir el mensaje de error
            $.each(errors, function(key, value) {
                errorsHtml += '<li>' + value[0] + '</li>'; //agregar cada uno de los errores

            });

            break;
        case 403:

            ////403 Forbidden The request was valid, but the server is refusing action
            errorsHtml += '<li>No tiene autorización para la operación</li>';

            break;

        case 404:

            //404 Not Found The requested resource could not be found but may be available in the future.
            //construir el mensaje de error
            errorsHtml += '<li>Servicio no disponible</li>';

            break;

        case 500:

            //500 Internal Server Error A generic error message, given when an unexpected condition was encountered and no more specific message is suitable.
            errorsHtml += '<li>Error interno de la aplicación.</li>';

            break;

        default:
            errorsHtml += '<li>' + _json.message + '</li>';
    }

    errorsHtml += '</ul>';
    $('#' + _form).html(errorsHtml); //cerrando el mensaje de error

    window.location.href = '#refErrorGral';

};

/**
 * Método para mostrar en pantalla los mensajes de éxito
 * @param  {[type]} _json  respuesta json enviada generalmente desde el controlador
 * @param  {[type]} _panel panel (div) al que se quiere hacer acercamiento
 * @param  {[type]} _form  div al que se le agregará la información obtenida del json
 */
var successMsj = function(_json, _panel, _form) {

    $("#msjErrorGral").hide();

    $("#" + _panel).fadeIn();
    successHtml = '<ul>';

    //Añado el mensaje enviado desde el controller.
    successHtml += '<li>' + _json.message + '</li>';

    successHtml += '</ul>';

    //Lo añado al formulario.
    $('#' + _form).html(successHtml); //cerrando el mensaje de éxito

    window.location.href = '#refOkGral';
}

/**
 * Método para retornar la fecha actual con el formato YYYYMMDDHHHmmss
 */
var TodayStringFull = function() {
    // var date = moment(new Date(), 'YYYY-MM-DD hh:mm:ss a');
    var date = moment(new Date()).format("YYYYMMDDHHmmss")

    return date;
}

/**
 * Método para convertir una fecha de String con formato dd/mm/yyyy a una variable de tipo fecha de JS
 * @param  {[type]} _dateString [description]
 * @return {[type]}             [description]
 */
var stringToDate = function(_stringDate) {
    var parts = _stringDate.split("/");
    return new Date(parts[2], parts[1] - 1, parts[0]);
}

/**
 * Método para cortar el valor del campo del autocomplete y solamente tomar el código para procesar las diversas consultas
 * @param  {[type]} _value [description]
 * @return {[type]}        [description]
 */
function autoCompleteFormatValue(_value) {

    if (_value != "") {
        var auxValue = _value.split(" : ");
        return auxValue[0];
    }
    return null;
}

/**
 * Método para que una vez un producto esté seleccionado, haga la consulta de los iconos de dicho producto.
 * @param  {[type]} producto_id [Producto_Id]
 * @return {[type]}             [description]
 */
var consultarIconos = function(producto_id) {

    //valor del token erro csrf
    var token = $("#token").val();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/producto/" + producto_id + "/consultarIconos";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id
        },
        success: function(obj) {
            //limpiar los labels			
            limpiarIconos();

            //Asignar valores de Labels
            $(".producto_columna_A_Label").html("A");
            $(".producto_columna_B_Label").html("B");
            $(".producto_columna_C_Label").html("C");
            $(".producto_columna_D_Label").html("D");
            $(".producto_columna_E_Label").html("E");
            $(".producto_columna_InvTotal_Label").html("Inv. Total");
            $(".producto_columna_Detalle_Label").html("Detalle");

            //Asignando valores
            $(".producto_columna_A").append(obj.iconos[0]);
            $(".producto_columna_B").append(obj.iconos[1]);
            $(".producto_columna_C").append(obj.iconos[2]);
            $(".producto_columna_D").append(obj.iconos[3]);
            $(".producto_columna_E").append(obj.iconos[4]);
            $(".producto_columna_InvTotal").append(obj.iconos[5]);
            $(".producto_columna_Detalle").append("<button type='button' class='btn btn-info btn-sm' style='width: 100%;'' id='producto_columna_Detalle' onclick='inventario_det_producto();'><span class='glyphicon glyphicon-option-vertical' aria-hidden='true'></span></button>");
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

}

/**
 * Método para limpiar los iconos que salen en la vista
 * @return {[type]} [description]
 */
var limpiarIconos = function() {
    $(".producto_columna_A").html("");
    $(".producto_columna_B").html("");
    $(".producto_columna_C").html("");
    $(".producto_columna_D").html("");
    $(".producto_columna_E").html("");
    $(".producto_columna_InvTotal").html("");
    $(".producto_columna_Detalle").html("");
}

/**
 * Método para limpiar los iconos que salen en la vista
 * @return {[type]} [description]
 */
var limpiarIconosLabels = function() {
    $(".producto_columna_A_Label").html("");
    $(".producto_columna_B_Label").html("");
    $(".producto_columna_C_Label").html("");
    $(".producto_columna_D_Label").html("");
    $(".producto_columna_E_Label").html("");
    $(".producto_columna_InvTotal_Label").html("");
    $(".producto_columna_Detalle_Label").html("");
}

/**
 * Método para consultar información adicional del producto seleccionado.
 * @return {[type]} [description]
 */
var inventario_det_producto = function() {
    //Objeto para manejar la promesa.
    var dfd = new $.Deferred();
    var empresa_id = $("#empresa_id").val();
    var deposito_id = $("#deposito_id").val();
    var producto_id = $('#productoSelect').val();

    var token = $("#token").val();

    //Se identifica la ruta
    var route = "/empresa/" + empresa_id + "/deposito/" + deposito_id + "/producto/" + producto_id + "/getDetalleProducto";

    $.ajax({
        url: route,
        headers: { 'X-CSRF-TOKEN': token },
        type: 'GET',
        dataType: 'json',
        data: {
            empresa_id: empresa_id,
            deposito_id: deposito_id,
            producto_id: producto_id,
        },
        success: function(obj) {

            //Estableciendo las opciones del modal
            $('#inventario_detalleProducto').modal({
                keyboard: false,
                backdrop: 'static',
            });

            //Mostrando el modal para mostrar la información del producto.
            $("#inventario_detalleProducto").modal("show");

            //Asignando valores al modal
            //Nombre y Código del producto
            $("#detalle_nombre_producto").html(obj.detalle[0].codigo_producto + " : " + obj.detalle[0].nombre_producto);
            //Inventario disponible
            $("#detalle_invDeposito").html(obj.detalle[0].disponible);
            //Compra mes 3
            $("#detalle_cm3").html(obj.detalle[0].comprames3);
            //Compra mes 2
            $("#detalle_cm2").html(obj.detalle[0].comprames2);
            //Compra mes 1
            $("#detalle_cm1").html(obj.detalle[0].comprames1);
            //Compra mes
            $("#detalle_cm").html(obj.detalle[0].comprames);
            //Venta mes 3
            $("#detalle_vm3").html(obj.detalle[0].ventames3);
            //Venta mes 2
            $("#detalle_vm2").html(obj.detalle[0].ventames2);
            //Venta mes 1
            $("#detalle_vm1").html(obj.detalle[0].ventames1);
            //Venta mes 
            $("#detalle_vm").html(obj.detalle[0].ventames);

            dfd.resolve();
        },
        error: function(obj) {
            //mostrar mensaje de error
            errorMsj(obj, 'msjErrorGral', 'formErrorGral');
        }
    });

    return dfd.promise();
};