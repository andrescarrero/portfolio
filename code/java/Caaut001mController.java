package com.fenix.control.controller.gestiondepersonal.autorizarmodomarcaje_sobretiempo;
//<editor-fold defaultstate="collapsed" desc="Importaciones">

import com.fenix.control.controller.gestiondepersonal.incidencias.reclamosdeincidencias.ReclamoIncidenciasController;
import com.fenix.control.controller.util.JsfUtil;
import com.fenix.control.fenixTools.util.Utilidades;
import com.fenix.control.session.Permisos;
import com.fenix.control.session.sessionBean;
import com.fenix.logica.conexion.Conexion;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001m;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001mPK;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002m;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mPK;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Cacon011t;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Marcajes;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.autorizaraterceros.Caate001m;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.incidenciasnoautorizadas.IncNoAutorizadas;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.incidenciasnoautorizadas.calculoIncidencias;
import com.fenix.logica.entidades.marcajeenlinea.Camot001m;
import com.fenix.logica.entidades.marcajeenlinea.Carel001x;
import com.fenix.logica.entidades.marcajeenlinea.Catur004a;
import com.fenix.logica.entidades.pasonomina.Cacon008m;
import com.fenix.logica.entidades.talentohumano.trabajador.Ngbas001x;
import com.fenix.logica.entidades.talentohumano.trabajador.Ngnom001x;
import com.fenix.logica.jpa.adminsistema.gruposparametros.Tgpar002dFacade;
import com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001mFacade;
import com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mFacade;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.autorizaraterceros.Caate001mFacade;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002tFacade;
import com.fenix.logica.jpa.marcajeenlinea.Camot001mFacade;
import com.fenix.logica.jpa.talentohumano.trabajador.Ngbas001xFacade;
import com.fenix.util.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
//</editor-fold>

/**
 * <br><b>Title:</b> </br> <br><b>Description:</b> .</br> <br><b>Copyright:</b> Copyright (c) 2011</br> <br><b>Company:</b> Pasteurizadora Tachira CA</br>
 *
 * @author user
 * @version 0.xv dia, hora
 */
@ManagedBean(name = "caaut001mController")
@ViewScoped
public class Caaut001mController {
//<editor-fold defaultstate="collapsed" desc="Declaracion de Variables">

    private Caaut001m current;
    private Caaut001m currentDestroy;
    private Caaut001m currentAgregar;
    private List<Caaut001m> listaAutorizaciones;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001mFacade ejbFacade;
    @EJB
    private com.fenix.logica.jpa.talentohumano.trabajador.Ngbas001xFacade ngbas001xFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.autorizaraterceros.Caate001mFacade caate001mFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002tFacade ngvar002tFacade;
    @EJB
    private com.fenix.logica.jpa.adminsistema.gruposparametros.Tgpar002dFacade tgpar002dFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mFacade caaut002mFacade;
    @EJB
    private com.fenix.logica.jpa.marcajeenlinea.Carel001xFacade carel001xFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Cacon011tFacade cacon011tFacade;
    @EJB
    private com.fenix.logica.jpa.marcajeenlinea.Camot001mFacade camot001mFacade;
    @EJB
    private com.fenix.logica.jpa.marcajeenlinea.Catur004aFacade catur004aFacade;
    @EJB
    private com.fenix.logica.jpa.funciones_procedimientos.funcProc funciones;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Capar001mFacade capar001mFacade;
    @EJB
    private com.fenix.logica.jpa.vistasgenerales.Ngbas009tFacade ngbas009tFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001mFacade casbt001mFacade;
    @EJB
    private com.fenix.logica.jpa.pasonomina.Cacon015dFacade cacon015dFacade;
    @EJB
    private com.fenix.logica.jpa.pasonomina.Cacon008mFacade cacon008mFacade;
    jbVarios jbvarios = new jbVarios();
    private int selectedItemIndex;
    private String codModulo = "SCAF0779";
    /*Para el Lazy */
    private String codpesTerceros = ""; //Códigos de los trabajadores a visualizar.
    private LazyDataModel<Caaut001m> lazyModel;
    private Integer pagIndex = null;
    private Integer paginacion = null;
    private Map<String, Object> fields = new HashMap<String, Object>();
    private String sortF = null;
    private int cantElemTabla = 20;
    private SortOrder sortB = SortOrder.UNSORTED;
    private boolean permisologia[] = new boolean[10];
    private boolean valoresDefault = false;
    private final static Logger logger = Logger.getLogger(Caaut001m.class.getName());
    private List<Ngbas001x> listaPersona;
    private Ngbas001x empleadoCatalogo;
    private Ngnom001x grupoNomina;
    private Ngbas001x supervisor;
    ReclamoIncidenciasController reclamocontroller = new ReclamoIncidenciasController();
    private boolean userAdmin;
    private boolean userSupervisor;
    /*Variables detalleConsulta*/
    private Date fechaIni;
    private int ST;
    private int MH;
    private Caaut001m selectedAut;
    /*Variables de incluirAut*/
    private Caaut002m caaut002m;
    private Caaut002mController caaut002mController = new Caaut002mController();
    private int criterio;
    private Carel001x reloj;
    private Date fechaIniC1;
    private Date fechaFinC1;
    private String Fhsis;
    private String fhsinhoras;
    private int codtr;
    private List<Cacon011t> cacon11t;
    private List<Cacon011t> cacon11tAux;
    private boolean muestroAusenciaDia;
    private boolean mostrarHorarios;
    private String FechaHoy;
    private List<Marcajes> marcaje;
    /*Variables Incluir Aut. SobreTiempo*/
    private Date fhiniST;
    private Date fhfinST;
    private List<Camot001m> motivoST;
    private List<Camot001m> actividadST;
    private Camot001m motivoSTagregar;
    private Camot001m actividadSTagregar;
    private Carel001x relojST;
    private String obsthST;
    private int codtrST;
    private String fechaElegida;
    private boolean msjSolapdo;
    private String textoMsjSolapado;
    private int edit;
    private Caaut001m caaut001mrecibido;
    private Caaut001m caaut001mMH;
    private int editarMH;
    private int extra = 3;
    private int descanso;
    private int trabajo;
    private String condicion;
    private String consulta = null;
    private String filtroSelec[]; //Ausencias - Sobretiempo
    private List<Ngbas001x> listaPersonas = new ArrayList<Ngbas001x>(); //Catálogo personas
    private String autSobretiempo;
    private int idCal = 0;
    private String retornoIncNoAut;
    private StreamedContent rutaArchivo;
    private boolean imprimir;
    private List<Caaut002m> tipoAutorizacion = new ArrayList<>();
    private Map<Integer, String> acum_Permiso_Remunerado_Actual = new HashMap<>(); //Acumulado de permisos remunerados año actual
    private Map<Integer, String> acum_Permiso_Remunerado_Anterior = new HashMap<>(); //Acumulado de permisos remunerados año anterior
    private List<IncNoAutorizadas> incidencias = new ArrayList<>(); //Listado de incidencias cargadas
    private calculoIncidencias calcular; //Método que llama al cálculo de contadores.

    /*Fin de Lazy*/
//</editor-fold>
    public Caaut001mController() {
        java.util.Arrays.fill(permisologia, Boolean.TRUE);
        //consultarPermisos(codModulo);
    }
    //<editor-fold defaultstate="collapsed" desc="Encapsulamiento">

    public Caaut001m getCurrent() {
        return current;
    }

    public void setCurrent(Caaut001m current) {
        this.current = current;
    }

    public Caaut001m getSelected() {
        if (current == null) {
//            current = new Caaut001m(new Caaut001mPK());
//            current.setCodsu(ejbFacade.getCodpe());
            //Si clave primaria Compuesta Inicializarla
            selectedItemIndex = -1;
        }
        return current;
    }

    private Caaut001mFacade getFacade() {
        return ejbFacade;
    }

    public boolean[] getPermisologia() {
        return permisologia;
    }

    public void setPermisologia(boolean[] permisologia) {
        this.permisologia = permisologia;
    }

    public LazyDataModel<Caaut001m> getLazyModel() {
        if (lazyModel == null || lazyModel.getRowCount() == 0) {
            inicializarLazy();
        }
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Caaut001m> lazyModel) {
        this.lazyModel = lazyModel;
    }
//fin lazy

    public String getCodModulo() {
        return codModulo;
    }

    public void setCodModulo(String codModulo) {
        this.codModulo = codModulo;
    }

    public Caaut001m getCurrentAgregar() {
        return currentAgregar;
    }

    public void setCurrentAgregar(Caaut001m currentAgregar) {
        this.currentAgregar = currentAgregar;
    }

    public Caaut001m getCurrentDestroy() {
        return currentDestroy;
    }

    public void setCurrentDestroy(Caaut001m currentDestroy) {
        this.currentDestroy = currentDestroy;
    }

    public int getCantElemTabla() {
        return cantElemTabla;
    }

    public void setCantElemTabla(int cantElemTabla) {
        this.cantElemTabla = cantElemTabla;
    }

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    public boolean isUserSupervisor() {
        return userSupervisor;
    }

    public void setUserSupervisor(boolean userSupervisor) {
        this.userSupervisor = userSupervisor;
    }

    public Ngvar002tFacade getNgvar002tFacade() {
        return ngvar002tFacade;
    }

    public void setNgvar002tFacade(Ngvar002tFacade ngvar002tFacade) {
        this.ngvar002tFacade = ngvar002tFacade;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Ngbas001xFacade getNgbas001xFacade() {
        return ngbas001xFacade;
    }

    public void setNgbas001xFacade(Ngbas001xFacade ngbas001xFacade) {
        this.ngbas001xFacade = ngbas001xFacade;
    }

    public List<Caaut001m> getListaAutorizaciones() {
        return listaAutorizaciones;
    }

    public void setListaAutorizaciones(List<Caaut001m> listaAutorizaciones) {
        this.listaAutorizaciones = listaAutorizaciones;
    }

    public Caaut002m getCaaut002m() {
        return caaut002m;
    }

    public void setCaaut002m(Caaut002m caaut002m) {
        this.caaut002m = caaut002m;
    }

    public Caaut002mController getCaaut002mController() {
        return caaut002mController;
    }

    public void setCaaut002mController(Caaut002mController caaut002mController) {
        this.caaut002mController = caaut002mController;
    }

    public Tgpar002dFacade getTgpar002dFacade() {
        return tgpar002dFacade;
    }

    public void setTgpar002dFacade(Tgpar002dFacade tgpar002dFacade) {
        this.tgpar002dFacade = tgpar002dFacade;
    }

    public int getCriterio() {
        return criterio;
    }

    public void setCriterio(int criterio) {
        this.criterio = criterio;
    }

    public Carel001x getReloj() {
        return reloj;
    }

    public void setReloj(Carel001x reloj) {
        this.reloj = reloj;
    }

    public Date getFechaIniC1() {
        return fechaIniC1;
    }

    public void setFechaIniC1(Date fechaIniC1) {
        this.fechaIniC1 = fechaIniC1;
    }

    public Date getFechaFinC1() {
        return fechaFinC1;
    }

    public void setFechaFinC1(Date fechaFinC1) {
        this.fechaFinC1 = fechaFinC1;
    }

    public String getFhsis() {
        return Fhsis;
    }

    public void setFhsis(String Fhsis) {
        this.Fhsis = Fhsis;
    }

    public int getCodtr() {
        return codtr;
    }

    public void setCodtr(int codtr) {
        this.codtr = codtr;
    }

    public Caate001mFacade getCaate001mFacade() {
        return caate001mFacade;
    }

    public void setCaate001mFacade(Caate001mFacade caate001mFacade) {
        this.caate001mFacade = caate001mFacade;
    }

    public Caaut002mFacade getCaaut002mFacade() {
        return caaut002mFacade;
    }

    public void setCaaut002mFacade(Caaut002mFacade caaut002mFacade) {
        this.caaut002mFacade = caaut002mFacade;
    }

    public List<Cacon011t> getCacon11t() {
        return cacon11t;
    }

    public void setCacon11t(List<Cacon011t> cacon11t) {
        this.cacon11t = cacon11t;
    }

    public boolean isMuestroAusenciaDia() {
        return muestroAusenciaDia;
    }

    public void setMuestroAusenciaDia(boolean muestroAusenciaDia) {
        this.muestroAusenciaDia = muestroAusenciaDia;
    }

    public List<Cacon011t> getCacon11tAux() {
        return cacon11tAux;
    }

    public void setCacon11tAux(List<Cacon011t> cacon11tAux) {
        this.cacon11tAux = cacon11tAux;
    }

    public String getFechaHoy() {
        return FechaHoy;
    }

    public void setFechaHoy(String FechaHoy) {
        this.FechaHoy = FechaHoy;
    }

    public List<Marcajes> getMarcaje() {
        return marcaje;
    }

    public void setMarcaje(List<Marcajes> marcaje) {
        this.marcaje = marcaje;
    }

    public boolean isMostrarHorarios() {
        return mostrarHorarios;
    }

    public void setMostrarHorarios(boolean mostrarHorarios) {
        this.mostrarHorarios = mostrarHorarios;
    }

    public Date getFhiniST() {
        return fhiniST;
    }

    public void setFhiniST(Date fhiniST) {
        this.fhiniST = fhiniST;
    }

    public Date getFhfinST() {
        return fhfinST;
    }

    public void setFhfinST(Date fhfinST) {
        this.fhfinST = fhfinST;
    }

    public List<Camot001m> getMotivoST() {
        return motivoST;
    }

    public void setMotivoST(List<Camot001m> motivoST) {
        this.motivoST = motivoST;
    }

    public List<Camot001m> getActividadST() {
        return actividadST;
    }

    public void setActividadST(List<Camot001m> actividadST) {
        this.actividadST = actividadST;
    }

    public Carel001x getRelojST() {
        return relojST;
    }

    public void setRelojST(Carel001x relojST) {
        this.relojST = relojST;
    }

    public String getObsthST() {
        return obsthST;
    }

    public void setObsthST(String obsthST) {
        this.obsthST = obsthST;
    }

    public int getCodtrST() {
        return codtrST;
    }

    public void setCodtrST(int codtrST) {
        this.codtrST = codtrST;
    }

    public Camot001m getMotivoSTagregar() {
        return motivoSTagregar;
    }

    public void setMotivoSTagregar(Camot001m motivoSTagregar) {
        this.motivoSTagregar = motivoSTagregar;
    }

    public Camot001m getActividadSTagregar() {
        return actividadSTagregar;
    }

    public void setActividadSTagregar(Camot001m actividadSTagregar) {
        this.actividadSTagregar = actividadSTagregar;
    }

    public String getFechaElegida() {
        return fechaElegida;
    }

    public void setFechaElegida(String fechaElegida) {
        this.fechaElegida = fechaElegida;
    }

    public boolean isMsjSolapdo() {
        return msjSolapdo;
    }

    public void setMsjSolapdo(boolean msjSolapdo) {
        this.msjSolapdo = msjSolapdo;
    }

    public String getTextoMsjSolapado() {
        return textoMsjSolapado;
    }

    public void setTextoMsjSolapado(String textoMsjSolapado) {
        this.textoMsjSolapado = textoMsjSolapado;
    }

    public int getST() {
        return ST;
    }

    public void setST(int ST) {
        this.ST = ST;
    }

    public int getMH() {
        return MH;
    }

    public void setMH(int MH) {
        this.MH = MH;
    }

    public Caaut001m getSelectedAut() {
        return selectedAut;
    }

    public void setSelectedAut(Caaut001m selectedAut) {
        this.selectedAut = selectedAut;
    }

    public Camot001mFacade getCamot001mFacade() {
        return camot001mFacade;
    }

    public void setCamot001mFacade(Camot001mFacade camot001mFacade) {
        this.camot001mFacade = camot001mFacade;
    }

    public int getEdit() {
        return edit;
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }

    public Caaut001m getCaaut001mrecibido() {
        return caaut001mrecibido;
    }

    public void setCaaut001mrecibido(Caaut001m caaut001mrecibido) {
        this.caaut001mrecibido = caaut001mrecibido;
    }

    public Caaut001m getCaaut001mMH() {
        return caaut001mMH;
    }

    public void setCaaut001mMH(Caaut001m caaut001mMH) {
        this.caaut001mMH = caaut001mMH;
    }

    public int getEditarMH() {
        return editarMH;
    }

    public void setEditarMH(int editarMH) {
        this.editarMH = editarMH;
    }

    public String getFhsinhoras() {
        return fhsinhoras;
    }

    public void setFhsinhoras(String fhsinhoras) {
        this.fhsinhoras = fhsinhoras;
    }

    public int getExtra() {
        return extra;
    }

    public void setExtra(int extra) {
        this.extra = extra;
    }

    public int getDescanso() {
        return descanso;
    }

    public void setDescanso(int descanso) {
        this.descanso = descanso;
    }

    public int getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(int trabajo) {
        this.trabajo = trabajo;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public Caaut001mFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(Caaut001mFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public String getCodpesTerceros() {
        return codpesTerceros;
    }

    public void setCodpesTerceros(String codpesTerceros) {
        this.codpesTerceros = codpesTerceros;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public String[] getFiltroSelec() {
        return filtroSelec;
    }

    public void setFiltroSelec(String[] filtroSelec) {
        this.filtroSelec = filtroSelec;
    }

    public List<Ngbas001x> getListaPersonas() {
        return listaPersonas;
    }

    public void setListaPersonas(List<Ngbas001x> listaPersonas) {
        this.listaPersonas = listaPersonas;
    }

    public String getAutSobretiempo() {
        return autSobretiempo;
    }

    public void setAutSobretiempo(String autSobretiempo) {
        this.autSobretiempo = autSobretiempo;
    }

    public int getIdCal() {
        return idCal;
    }

    public void setIdCal(int idCal) {
        this.idCal = idCal;
    }

    public String getRetornoIncNoAut() {
        return retornoIncNoAut;
    }

    public void setRetornoIncNoAut(String retornoIncNoAut) {
        this.retornoIncNoAut = retornoIncNoAut;
    }

    public StreamedContent getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(StreamedContent rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public boolean isImprimir() {
        return imprimir;
    }

    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }

    public List<Caaut002m> getTipoAutorizacion() {
        return tipoAutorizacion;
    }

    public void setTipoAutorizacion(List<Caaut002m> tipoAutorizacion) {
        this.tipoAutorizacion = tipoAutorizacion;
    }

    public List<IncNoAutorizadas> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<IncNoAutorizadas> incidencias) {
        this.incidencias = incidencias;
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Metodos Por Defecto">
    public String prepareList() {
        current = new Caaut001m();
        //current.setCodsu(ejbFacade.getCodpe());
        //Si clave primaria Compuesta Inicializarla
        // recreateModel();
        //consultarPermisos(codModulo);
        return "List";
    }

    public String prepareView() {
        current = (Caaut001m) lazyModel.getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        //consultarPermisos(codModulo);
        return "View";
    }

    public String prepareCreate() {
        currentAgregar = new Caaut001m(new Caaut001mPK());
        listaPersona = new ArrayList<Ngbas001x>();

        //current.setCodsu(ejbFacade.getCodpe());
        //Si clave primaria Compuesta Inicializarla
        selectedItemIndex = -1;
        //consultarPermisos(codModulo);
        return "Create";
    }

    public String create() {
        try {
            currentAgregar.getCaaut001mPK().setCodem(ejbFacade.getCodem());
            currentAgregar.setFhcre(ejbFacade.getCurrentDateTime());
            currentAgregar.setFhmod(ejbFacade.getCurrentDateTime());
            currentAgregar.setCusua(ejbFacade.getCusua());
            currentAgregar.setCusuc(ejbFacade.getCusua());

            for (Ngbas001x ngbas001x : listaPersona) {
                Caaut001m objGuardar = new Caaut001m(new Caaut001mPK(ejbFacade.getCodem(), 0));
                objGuardar.getCaaut001mPK().setCodem(ejbFacade.getCodem());
                objGuardar.setFhcre(ejbFacade.getCurrentDateTime());
                objGuardar.setFhmod(ejbFacade.getCurrentDateTime());
                objGuardar.setCusua(ejbFacade.getCusua());
                objGuardar.setCusuc(ejbFacade.getCusua());
                objGuardar.setNgbas001tCodpe(ngbas001x);
                objGuardar.setNgbas001tCodsu(supervisor);
                objGuardar.setCarel001x(currentAgregar.getCarel001x());
                objGuardar.setCamot001mCodac(currentAgregar.getCamot001mCodac());
                objGuardar.setCamot001mCodmo(currentAgregar.getCamot001mCodmo());
                objGuardar.setFhiau(currentAgregar.getFhiau());
                objGuardar.setFhfau(currentAgregar.getFhfau());
                objGuardar.setObsth(currentAgregar.getObsth());
                objGuardar.setStaau(currentAgregar.getStaau());

                objGuardar.setCaaut002m(currentAgregar.getCaaut002m());

                getFacade().create(objGuardar);
            }

            Utilidades.mostrarMensaje(
                    0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado") + " " + ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"));
            recargarLazyModel();
//            inicializarValores();
            return prepareCreate();
        } catch (Exception e) {
            logger.error(e.getMessage());
            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));

            return null;
        }
    }

    public String prepareEdit() {
        current = (Caaut001m) lazyModel.getRowData();
        //consultarPermisos(codModulo);
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            Utilidades.mostrarMensaje(
                    0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado"));
            return "View";
        } catch (Exception e) {
            logger.error(e.getMessage());
            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjErrUdpate"));
            return null;
        }
    }

    public String destroy() {
        // current = (Caaut001m)getItems().getRowData();
        performDestroy();
        recargarLazyModel();
        return "List";
    }

    public void recargarLazyModel() {
        if (pagIndex != null && pagIndex > 0) {
            if (((List) lazyModel.getWrappedData()).size() == 1) {
                //pagIndex--;
                DataTable t = ((DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("fmlista:tlazy"));
                //  t.setPage(pagIndex / lazyModel.getPageSize());
                pagIndex -= lazyModel.getPageSize();
            }
        }
        if (pagIndex != null) {
            lazyModel.setWrappedData(lazyModel.load(pagIndex, lazyModel.getPageSize(), sortF, sortB, fields));
        }
    }

    public String destroyAndView() {
        performDestroy();

        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            // recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            Utilidades.mostrarMensaje(
                    0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjEliminado"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            Utilidades.mostrarMensaje(1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("PersistenceErrorOccured"));
        }
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(codModulo), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(codModulo), true);
    }

    public List<Caaut001m> ListAvailableSelectOne() {
        return ejbFacade.findAll(codModulo);
    }

    public List<Caaut001m> ListAvailableSelectOne(String _codmo) {
        return ejbFacade.findAll(_codmo);
    }

    /**
     * Método que muestra los datos que se mostraran en pantalla, referente al turno del trabajador y los periodos extra.
     *
     * @param conexion Conexión a BD
     * @param iniciarCalculo String con lo que retorna el cálculo de contadores
     * @param fecha_fString
     * @param _idTurno Código del turno
     * @throws Exception
     */
    private void cargarDatos(Connection conexion, String iniciarCalculo, String fecha_fString, int _idTurno) throws Exception {
        DataCalculo_ objDC = new DataCalculo_(conexion, ejbFacade.getCusua(), "dd/MM/yyyy", "%d/%m/%Y");
        if (iniciarCalculo.split("&")[1].toString().compareTo("E") != 0) {
            /**
             * Es necesario setear para la clase DataCalculo el "idcal", este "idcal" es retornado por cpp.iniciarCalculo("..."), pero viene concatenado
             * con "&", por ello se hace el split para hacer el seteo
             */
            objDC.setIdcal(iniciarCalculo.split("&")[0].toString());
            objDC.cargarData(String.valueOf(empleadoCatalogo.getCodpe()), fecha_fString, String.valueOf(_idTurno), "1");
            if (objDC.getPosPr() != 0) {
                String[] priPr = objDC.getPriPr();
                String[] fhiniPr = objDC.getFhiniPr();
                String[] fhfinPr = objDC.getFhfinPr();
                marcaje = new ArrayList<Marcajes>();
                for (int i = 0; i < objDC.getPosPr(); i++) {

                    boolean band = true;
                    if (Integer.parseInt(priPr[i].toString()) == extra) {
                        Marcajes objN = new Marcajes("", "", "");
                        objN.setTipo(priPr[i]);
                        i = acoplarTurnos(priPr, fhiniPr, fhfinPr, i, objDC.getPosPr(), band, objN, String.valueOf(extra));
                    } else {
                        Marcajes obj = new Marcajes(fhiniPr[i], fhfinPr[i], priPr[i]);
                        marcaje.add(obj);
                    }
                }

                if (!marcaje.isEmpty()) {
                    Fhsis = jbvarios.fechaToString(ngvar002tFacade.getCurrentDateTime(), "dd/MM/yyyy HH:mm:ss");
                    if (edit == 0) {
                        relojST = new Carel001x();
                        fhiniST = null;
                        fhfinST = null;
                        obsthST = null;
                    }
                    Map<Object, Object> parametrosmotST = new HashMap<Object, Object>();
                    parametrosmotST.put("1", ejbFacade.getCodem());
                    parametrosmotST.put("2", 'M');

                    motivoST = camot001mFacade.buscarListaMotAct(codModulo, parametrosmotST);

                    Map<Object, Object> parametrosactST = new HashMap<Object, Object>();
                    parametrosactST.put("1", ejbFacade.getCodem());
                    parametrosactST.put("2", 'A');
                    actividadST = camot001mFacade.buscarListaMotAct(codModulo, parametrosactST);

                    fechaElegida = jbvarios.fechaToString(fechaIni, "dd/MM/yyyy");
                    mostrarHorarios = true;

                }
            }
        } else {
            System.out.println("El calculo de contadores dio error");
            Utilidades.mostrarMensaje(
                    2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasErrorContadores"));
        }
    }

    @FacesConverter(forClass = Caaut001m.class, value = "Caaut001mConverter")
    public static class Caaut001mControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            Caaut001mController controller = (Caaut001mController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "caaut001mController");
            return controller.ejbFacade.find(getKey(value));
        }

        com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001mPK getKey(String value) {
            com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001mPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001mPK();
            key.setCodem(values[0]);
            key.setCodau(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut001mPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getCodem());
            sb.append(SEPARATOR);
            sb.append(value.getCodau());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Caaut001m) {
                Caaut001m o = (Caaut001m) object;
                return getStringKey(o.getCaaut001mPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Caaut001mController.class.getName());
            }
        }
    }
//Lazy inicio

    public void inicializarLazy() {
        lazyModel = new LazyDataModel<Caaut001m>() {
            @Override
            public List<Caaut001m> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                logger.info("LazyModal Carga elementos  entre " + first + "y " + (first + pageSize));

                if (valoresDefault == true) {
                    valoresDefault = false;
                    if (pagIndex != null && paginacion != null) {
                        first = pagIndex;
                        pageSize = paginacion;
                        DataTable t = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("hfr_lista:tlazy");
                        System.err.println(t);
                        t.getPage();
//                  t.setRowIndex(pagIndex);
                        t.setFirst(first);
                        // t.calculatePage();
                        t.getPage();
                    }
                }

                Map<String, Object> mapa = new HashMap<String, Object>(filters);
                String cadena = "";
                int cantidad = getFacade().count(mapa, cadena, codModulo);
                if (cantidad > 0) {
                    cantElemTabla = pageSize;
                    pagIndex = first;
                    paginacion = pageSize;
                    fields = filters;
                    sortF = sortField;
                    sortB = sortOrder;
                    lazyModel.setWrappedData(null);

                    lazyModel.setRowCount(cantidad);
                    return getFacade().findRange(sortField, sortOrder, new int[]{first, first + pageSize}, mapa, cadena, codModulo);

                }
                return null;
                // return lazymv;
            }
        };

        lazyModel.setRowCount(getFacade().count(null, "", codModulo));
        if (lazyModel.getRowCount() == 0) {
            lazyModel.setRowCount(10);
        }
    }

    public void consultarPermisos(String _codmo) {
        FacesContext ctx;
        valoresDefault = true;
        try {
            ctx = FacesContext.getCurrentInstance();
            sessionBean sb = ctx.getApplication().evaluateExpressionGet(ctx, "#{sessionBean}", sessionBean.class);
            this.setPermisologia(new Permisos().consultarPermisos(sb.getLogin(), _codmo));
            System.out.println(permisologia);
        } catch (Exception ex) {
            java.util.Arrays.fill(permisologia, Boolean.FALSE);
            if (ex.getMessage().contains("GENERAL")) {
                String[] mensaje = ex.getMessage().split(":");
                logger.error(ex);
            } else {
                logger.error(ex.getMessage());
            }
        }
    }

    public void inicializarValores() {
        try {
            RequestContext confirm = RequestContext.getCurrentInstance();
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            currentAgregar = new Caaut001m(new Caaut001mPK());
            fechaIni = null;
            empleadoCatalogo = new Ngbas001x();
            System.out.println(":" + ejbFacade.getIdepa());
            userAdmin = perfilAdmin();
            if (!userAdmin) {
                userSupervisor = perfilSupervisor();
            }

            if (userSupervisor) {
                codpesTerceros = "";
                Map<Object, Object> parametros2 = new HashMap<Object, Object>();
                parametros2.put("1", jbvarios.stringToFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));
                parametros2.put("2", jbvarios.stringToFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));
                parametros2.put("3", ejbFacade.getCodem());

                //Carga de terceros por deducción.
                Ngbas001x trabajador = ngbas001xFacade.find(ejbFacade.getCodpe());
                List<Ngbas001x> autSubordinados = caate001mFacade.deduccionTerceros(codModulo, trabajador, parametros2, false);
                if (autSubordinados != null) {
                    for (int i = 0; i < autSubordinados.size(); i++) {
                        codpesTerceros += autSubordinados.get(i).getCodpe() + ",";
                    }
                }

                Map<Object, Object> parametros = new HashMap<Object, Object>();
                parametros.put("1", ejbFacade.getCodpe());
                parametros.put("2", jbvarios.stringToFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));
                parametros.put("3", jbvarios.stringToFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));

                List<Caate001m> fechasTerceros = caate001mFacade.fechasTerceros(parametros, codModulo);

                if (!fechasTerceros.isEmpty()) {
                    for (int i = 0; i < fechasTerceros.size(); i++) {
                        List<Ngbas001x> prepareTargetTerceros = caate001mFacade.prepareTargetTerceros(fechasTerceros.get(i).getCaate001mPK().getCodat(), codModulo);
                        if (prepareTargetTerceros != null) {
                            for (int j = 0; j < prepareTargetTerceros.size(); j++) {
                                codpesTerceros += String.valueOf(prepareTargetTerceros.get(j).getCodpe()) + ",";
                            }
                        }
                    }
                }

                if (codpesTerceros.compareTo("") != 0) {
                    codpesTerceros = codpesTerceros.substring(0, codpesTerceros.length() - 1);
                } else {
                    codpesTerceros = "-1";
                }
            }
        } catch (Exception e) {
            getFacade().getLog().error("Error de consulta ");
        }
    }

    public Ngbas001x getEmpleadoCatalogo() {
        return empleadoCatalogo;
    }

    public void setEmpleadoCatalogo(Ngbas001x empleadoCatalogo) {
        this.empleadoCatalogo = empleadoCatalogo;
        if (empleadoCatalogo != null || empleadoCatalogo.getCodpe() != null) {
            supervisor = empleadoCatalogo;
            listaPersona = new ArrayList<Ngbas001x>();
        } else {
            supervisor = ngbas001xFacade.find(ejbFacade.getCodpe());
            listaPersona = new ArrayList<Ngbas001x>();
            empleadoCatalogo = supervisor;
        }
    }

    public Ngnom001x getGrupoNomina() {
        return grupoNomina;
    }

    public void setGrupoNomina(Ngnom001x grupoNomina) {
        this.grupoNomina = grupoNomina;
    }

    public List<Ngbas001x> getListaPersona() {
        return listaPersona;
    }

    public void setListaPersona(List<Ngbas001x> listaPersona) {
        this.listaPersona = listaPersona;
    }

    public Ngbas001x getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Ngbas001x supervisor) {
        this.supervisor = supervisor;
    }

    public ReclamoIncidenciasController getReclamocontroller() {
        return reclamocontroller;
    }

    public void setReclamocontroller(ReclamoIncidenciasController reclamocontroller) {
        this.reclamocontroller = reclamocontroller;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Nuevos Metodos">
    /**
     * Método para consultar si el perfil del usuario que inicia sesión es de administrador
     *
     * @return True: Si es administrador, False:No es administrador
     */
    public boolean perfilAdmin() {
        StringBuilder sentencia = new StringBuilder("SELECT t.valpa FROM tgpar002d t where t.codem='").append(ejbFacade.getCodem()).append("' and t.codpa='PAADM'");
        Object singleResult = ejbFacade.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
        String perfil = singleResult.toString();
        if (perfil != null) {
            if (perfil.contains(",")) {
                String[] separador = perfil.split(",");
                for (int i = 0; i < separador.length; i++) {
                    if (ejbFacade.getIdepa() == Integer.parseInt(separador[i])) {
                        return true;
                    }
                }
            } else {
                if (ejbFacade.getIdepa() == Integer.parseInt(perfil)) {
                    return true;
                }
            }

        } else {
            return false;
        }
        return false;
    }

    /**
     * Método para consultar si el perfil del usuario que inicia sesión es de Supervisor
     *
     * @return True: Si es Supervisor, False:No es Supervisor
     */
    public boolean perfilSupervisor() {
        StringBuilder sentencia = new StringBuilder("SELECT t.valpa FROM tgpar002d t where t.codem='").append(ejbFacade.getCodem()).append("' and t.codpa='PASUP'");
        Object singleResult = ejbFacade.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
        String perfil = singleResult.toString();
        if (perfil != null) {
            if (perfil.contains(",")) {
                String[] separador = perfil.split(",");
                for (int i = 0; i < separador.length; i++) {
                    if (ejbFacade.getIdepa() == Integer.parseInt(separador[i])) {
                        return true;
                    }
                }
            } else {
                if (ejbFacade.getIdepa() == Integer.parseInt(perfil)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * Método que envía a detalleConsulta los datos para cargar la información
     *
     * @return detalleConsulta.xhtml
     * @throws Exception Problema en consultas de métodos internos
     */
    public String enviarParametros() throws Exception {

        if (empleadoCatalogo.getCidpe() == null) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("CreateCaaut001mLabelemplcat"));
            return null;
        }
        Ngbas001x buscarporCedula = ngbas001xFacade.buscarporCedula(empleadoCatalogo.getCidpe(), codModulo);
        if (buscarporCedula == null) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("CreateCaaut001mLabelemplcatInvCed"));
            return null;
        }
        if (userSupervisor) {
            String[] codp = codpesTerceros.split(",");
            List _codp = new ArrayList();
            _codp.addAll(Arrays.asList(codp));

            if (!_codp.contains(String.valueOf(buscarporCedula.getCodpe())) && buscarporCedula.getSuppe() != ejbFacade.getCodpe()) {
                Utilidades.mostrarMensaje(
                        1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("CreateCaaut001mLabelemplcatInv"));
                empleadoCatalogo = new Ngbas001x();
                return null;
            }
        }
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put("empcat", empleadoCatalogo);

        return "detalleConsulta.xhtml";
    }

    /**
     * Método que devuelve un String con el Estado dado su inicial
     *
     * @param _staau Estado 'A' o 'E'
     * @return String con el Estado
     */
    public String obtenerEstatus(char _staau) {
        if (_staau == 'A') {
            return "Activo";
        }
        if (_staau == 'E') {
            return "Inactivo";
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Métodos de detalleConsulta.xhtml">
    /**
     * Método que cargar los datos que provienen de la página principal
     *
     * @throws Exception Problema en consulta de métodos internos
     */
    public void cargarParametrosList() throws Exception {
        //recibir
        RequestContext confirm = RequestContext.getCurrentInstance();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        empleadoCatalogo = (Ngbas001x) ec.getRequestMap().get("empcat");

        userAdmin = perfilAdmin();
        if (!userAdmin) {
            userSupervisor = perfilSupervisor();
        }

        listaAutorizaciones = ejbFacade.buscarCaaut001m(empleadoCatalogo.getCodpe(), codModulo);

        java.sql.Connection conexion = null;
        conexion = new Conexion().getConexion();
        Trabajador trab = new Trabajador(conexion, ejbFacade.getCusua(), ejbFacade.getCodem());
        trab.setCodpe(String.valueOf(empleadoCatalogo.getCodpe()));
        trab.setConpe(String.valueOf(empleadoCatalogo.getConpe()));
        trab.setCodcp(String.valueOf(empleadoCatalogo.getCodcp()));

        condicion = trab.SituacionTrababjador(trab);
        conexion.close();

        try {
            ST = Integer.parseInt(tgpar002dFacade.consultarParametro("MASOB").getValpa());
            MH = Integer.parseInt(tgpar002dFacade.consultarParametro("MAMAH").getValpa());
        } catch (Exception e) {
            logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
        }

        retornoIncNoAut = (String) ec.getRequestMap().get("retornoIncNoAut");
        if (retornoIncNoAut != null) {
            rutaArchivo = (StreamedContent) ec.getRequestMap().get("rutaArchivo");
            confirm.execute("PF('confirmation2').show();");
        }
    }

    /**
     * Método para enviar los parametros a incluirAut.xhtml
     *
     * @return incluirAut.xhtml
     */
    public String enviarParametrosIncluirAut() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put("empcat", empleadoCatalogo);
        userAdmin = perfilAdmin();
        if (!userAdmin) {
            userSupervisor = perfilSupervisor();
        }
        return "incluirAut";
    }

    /**
     * Método que redireciona a la vista principal
     *
     * @return List.xhtml
     */
    public String vistaPrincipal() {
        return "List.xhtml";
    }

    /**
     * Método para mostrar en la vista, la información del registro en el modal
     *
     * @param _item Item seleccionado de la tabla
     */
    public String Ver(Caaut001m _item) {
        selectedAut = _item;
        RequestContext context = RequestContext.getCurrentInstance();
        //execute javascript
        context.execute("PF('modalver').show();");
        return null;
    }

    /**
     * Método para desplegar el modal que pregunta si desea anular un registro (desactivarlo)
     *
     * @param _item Item seleccionado de la tabla
     */
    public String Anular(Caaut001m _item) {
        selectedAut = _item;

        if (selectedAut.getFhfau() != null) {
            RequestContext context = RequestContext.getCurrentInstance();
            //execute javascript
            context.execute("PF('modalanular').show();");
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            //execute javascript
            context.execute("PF('modalanularNull').show();");
        }
        return null;
    }

    /**
     * Método para desplegar en la vista el módulo para editar los registros
     *
     * @param _item Item seleccionado de la tabla
     */
    public String Editar(Caaut001m _item) {
        selectedAut = _item;
        RequestContext context = RequestContext.getCurrentInstance();
        //execute javascript
        context.execute("PF('modaledit').show();");
        return null;
    }

    /**
     * Método que realiza la edición de un SobreTiempo
     *
     * @param _item Item seleccionado de la tabla
     * @return String "incluirAut.xhtml"
     */
    public String updateRegistro(Caaut001m _item) {
        int STiempo = 0;
        try {
            STiempo = Integer.parseInt(tgpar002dFacade.consultarParametro("MASOB").getValpa());
        } catch (Exception e) {
            logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
        }
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        if (_item.getCaaut002m().getCaaut002mPK().getCodma() == STiempo) {
            ec.getRequestMap().put("detalleFini", _item.getFhiau());
            ec.getRequestMap().put("detalleFfin", _item.getFhfau());
            ec.getRequestMap().put("empcat", empleadoCatalogo);
            ec.getRequestMap().put("detalleEdit", 1);
            ec.getRequestMap().put("detalleReloj", _item.getCarel001x());
            ec.getRequestMap().put("detalleCaaut002m", _item.getCaaut002m());
            ec.getRequestMap().put("detalleActividad", _item.getCamot001mCodac());
            ec.getRequestMap().put("detalleMotivo", _item.getCamot001mCodmo());
            ec.getRequestMap().put("detalleObsth", _item.getObsth());
            ec.getRequestMap().put("detalleAut", _item);
        } else {
            ec.getRequestMap().put("detalleAutMH", _item);
            ec.getRequestMap().put("empcat", empleadoCatalogo);
            ec.getRequestMap().put("detalleMHEdit", 1);
        }

        return "incluirAut";
    }

    /**
     * Método para desactivar los registros de sobretiempo o marcaje habitual
     *
     * @param _seleccionado Item Seleccionado
     */
    public void anularRegistro(Caaut001m _seleccionado) {

        if (_seleccionado.getCaaut002m().getCaaut002mPK().getCodma() == ST) {
            if (_seleccionado.getStaau() == 'A') {
                UserTransaction transaction = null;
                try {
                    transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                    transaction.begin();
                    Date fhsis = ejbFacade.getCurrentDateTime();
                    _seleccionado.setStaau('E');
                    _seleccionado.setCusua(ejbFacade.getCusua());
                    _seleccionado.setFhmod(fhsis);
                    ejbFacade.edit(_seleccionado);
                    transaction.commit();
                    Utilidades.mostrarMensaje(0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                            ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado"));
                } catch (Exception e) {
                    if (transaction != null) {
                        try {
                            transaction.rollback();
                        } catch (Exception ex) {
                            logger.debug(e.getMessage());
                            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                        }
                    }
                }
            } else {
                Utilidades.mostrarMensaje(1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleDetalleAnularEdoE"));
            }
        } else {
            if (_seleccionado.getStaau() == 'A') {
                UserTransaction transaction = null;
                try {
                    transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                    transaction.begin();
                    Date fhsis = ejbFacade.getCurrentDateTime();
                    _seleccionado.setStaau('E');
                    _seleccionado.setCusua(ejbFacade.getCusua());
                    _seleccionado.setFhmod(fhsis);
                    ejbFacade.edit(_seleccionado);
                    transaction.commit();
                    Utilidades.mostrarMensaje(0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                            ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado"));
                } catch (Exception e) {
                    if (transaction != null) {
                        try {
                            transaction.rollback();
                        } catch (Exception ex) {
                            logger.debug(e.getMessage());
                            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                        }
                    }
                }
            } else {
                Utilidades.mostrarMensaje(1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleDetalleAnularEdoE"));
            }
        }
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Métodos de incluirAut.xhtml">
    /**
     * Método que carga los parametros para procesar la vista
     */
    public void cargarParametrosincluirAut() throws Exception {
        tipoAutorizacion = new ArrayList<>();
        RequestContext confirm = RequestContext.getCurrentInstance();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        msjSolapdo = false;
        textoMsjSolapado = "";
        muestroAusenciaDia = false;
        mostrarHorarios = false;
        fechaFinC1 = null;
        fechaIniC1 = null;
        fechaIni = null;
        imprimir = false;
        edit = 0;
        editarMH = 0;
        empleadoCatalogo = (Ngbas001x) ec.getRequestMap().get("empcat");

        java.sql.Connection conexion = null;
        conexion = new Conexion().getConexion();

        Trabajador trab = new Trabajador(conexion, ejbFacade.getCusua(), ejbFacade.getCodem());
        trab.setCodpe(String.valueOf(empleadoCatalogo.getCodpe()));
        trab.setConpe(String.valueOf(empleadoCatalogo.getConpe()));
        trab.setCodcp(String.valueOf(empleadoCatalogo.getCodcp()));

        condicion = trab.SituacionTrababjador(trab);
        conexion.close();

        userAdmin = perfilAdmin();
        userSupervisor = perfilSupervisor();
        caaut002m = new Caaut002m(new Caaut002mPK());
        caaut002mController = new Caaut002mController();
        reloj = new Carel001x();
        relojST = new Carel001x();
        if (userAdmin) {
            criterio = 1;
            String _codtr = "";
            try {
                _codtr = tgpar002dFacade.consultarParametro("TRMCJ").getValpa();
                codtr = Integer.parseInt(_codtr);
            } catch (Exception e) {
                logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
            }
            String valpaSobreTiempo = tgpar002dFacade.consultarParametro("MASOB").getValpa();
            Caaut002m buscarRegistro = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaSobreTiempo), codModulo, ejbFacade.getCodem());
            String valpaMHabitual = tgpar002dFacade.consultarParametro("MAMAH").getValpa();
            Caaut002m buscarRegistroMH = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaMHabitual), codModulo, ejbFacade.getCodem());
            tipoAutorizacion.add(buscarRegistroMH);
            tipoAutorizacion.add(buscarRegistro);
        } else if (userSupervisor) {
            criterio = 1;
            //Cambió el 03/03/2016 anteriormente el supervisor solo podía crear autorizacion de sobretiempo, se agregó el habitual.
            String valpaMHabitual = tgpar002dFacade.consultarParametro("MAMAH").getValpa();
            Caaut002m buscarRegistroMH = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaMHabitual), codModulo, ejbFacade.getCodem());
            tipoAutorizacion.add(buscarRegistroMH);
            //Sobretiempo
            String valpaSobreTiempo = tgpar002dFacade.consultarParametro("MASOB").getValpa();
            Caaut002m buscarRegistro = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaSobreTiempo), codModulo, ejbFacade.getCodem());
            tipoAutorizacion.add(buscarRegistro);
        }
        Fhsis = jbvarios.fechaToString(ngvar002tFacade.getCurrentDateTime(), "dd/MM/yyyy HH:mm:ss");
        fhsinhoras = jbvarios.fechaToString(ngvar002tFacade.getCurrentDateTime(), "dd/MM/yyyy");
        System.out.println("fecha: " + ngvar002tFacade.getCurrentDateTime());
        fechaFinC1 = null;
        fechaIniC1 = null;

        caaut001mMH = (Caaut001m) ec.getRequestMap().get("detalleAutMH");
        if (caaut001mMH != null) {
            reloj = caaut001mMH.getCarel001x();
            fechaIniC1 = caaut001mMH.getFhiau();
            fechaFinC1 = caaut001mMH.getFhfau();
            editarMH = Integer.parseInt(ec.getRequestMap().get("detalleMHEdit").toString());
        }

        fechaIni = (Date) ec.getRequestMap().get("detalleFini");
        if (fechaIni != null) {
            try {
                caaut002m = (Caaut002m) ec.getRequestMap().get("detalleCaaut002m");
                edit = Integer.parseInt(ec.getRequestMap().get("detalleEdit").toString());
                relojST = (Carel001x) ec.getRequestMap().get("detalleReloj");
                actividadSTagregar = (Camot001m) ec.getRequestMap().get("detalleActividad");
                motivoSTagregar = (Camot001m) ec.getRequestMap().get("detalleMotivo");
                obsthST = ec.getRequestMap().get("detalleObsth").toString();
                fhiniST = (Date) ec.getRequestMap().get("detalleFini");
                fhfinST = (Date) ec.getRequestMap().get("detalleFfin");
                caaut001mrecibido = (Caaut001m) ec.getRequestMap().get("detalleAut");
                procesarInsert();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(Caaut001mController.class.getName()).log(Level.SEVERE, null, ex);
                logger.debug(ejbFacade.getCusua() + " " + ex.getMessage());
            }
        }

        retornoIncNoAut = (String) ec.getRequestMap().get("retornoIncNoAut");
        if (retornoIncNoAut != null) {
            fechaIni = (Date) ec.getRequestMap().get("feini");
            String valpaSobreTiempo = tgpar002dFacade.consultarParametro("MASOB").getValpa();
            Caaut002m buscarRegistro = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaSobreTiempo), codModulo, ejbFacade.getCodem());
            caaut002m = buscarRegistro;
            procesarInsert();
            imprimir = true;
            //Parámetro recibido con el archivo que se va a descargar
            rutaArchivo = (StreamedContent) ec.getRequestMap().get("rutaArchivo");
        }
    }

    /**
     * Método que envia los parametros a detalleConsulta.xhtml
     *
     * @return detalleConsulta.xhtml
     */
    public String enviarParametrosincluirAut() {

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put("empcat", empleadoCatalogo);

        return "detalleConsulta";
    }

    /**
     * Método para procesar el registro de un sobretiempo
     */
    public void procesarInsert() throws Exception {
        muestroAusenciaDia = false;
        mostrarHorarios = false;
        try {
            codtrST = Integer.parseInt(tgpar002dFacade.consultarParametro("TRMCJ").getValpa());
        } catch (Exception e) {
            logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
        }
        //- Se autorizará un sobretiempo

        Date fhoy = jbvarios.stringToFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy"), "dd/MM/yyyy");
        Date fini = jbvarios.stringToFecha(jbvarios.fechaToString(fechaIni, "dd/MM/yyyy"), "dd/MM/yyyy");

        if (fhoy.after(fini) && edit == 0) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutTabFinimayorFsis"));
        } else {
            boolean permisoVacaciones = caate001mFacade.validarAutorizado(fechaIni, fechaIni, empleadoCatalogo.getCodpe(), codModulo);

            Date _fecha = fechaIni;

            if (permisoVacaciones) {

                int _idTurno = validarTurno(empleadoCatalogo.getCodpe(), _fecha);

                if (_idTurno > 0) {

                    String _sobreTi = "";
                    try {

                        _sobreTi = tgpar002dFacade.consultarParametro("MASOB").getValpa();

                    } catch (Exception e) {
                        logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
                    }

                    if (caaut002m.getCaaut002mPK().getCodma() == Integer.parseInt(_sobreTi)) {

                        String _validarAu = "";
                        try {
                            _validarAu = tgpar002dFacade.consultarParametro("VAASO").getValpa();
                        } catch (Exception e) {
                            logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
                        }
                        Date fecha_hoy = jbvarios.stringToFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy"), "dd/MM/yyyy");
                        Date fecha_formulario = jbvarios.stringToFecha(jbvarios.fechaToString(fechaIni, "dd/MM/yyyy"), "dd/MM/yyyy");
                        String fecha_fString = jbvarios.fechaToString(fechaIni, "dd/MM/yyyy");

                        if (_validarAu.compareTo("S") == 0 && fecha_hoy.equals(fecha_formulario) && retornoIncNoAut == null) {
                            //Ejecuto la consulta de contadores.
                            java.sql.Connection conexion = null;
                            conexion = new Conexion().getConexion();

                            calcular = new calculoIncidencias(codModulo, ejbFacade.getCodem(), ejbFacade.getCusua(), ejbFacade.getCodpe(), tgpar002dFacade, cacon011tFacade, caate001mFacade, ngbas001xFacade, funciones, capar001mFacade, catur004aFacade, casbt001mFacade, ngbas009tFacade, camot001mFacade);

                            ArrayList _empCat = new ArrayList<Integer>();
                            _empCat.add(empleadoCatalogo.getCodpe());

                            incidencias = new ArrayList<>();
                            acum_Permiso_Remunerado_Actual = new HashMap<>();
                            acum_Permiso_Remunerado_Anterior = new HashMap<>();
                            Progreso progreso = new Progreso(0, "", false, 0);
                            filtroSelec = new String[1];
                            filtroSelec[0] = "Ausencias";
                            List<Integer> trabajadoresAux = new ArrayList<>();
                            trabajadoresAux = _empCat;

                            List<Integer> trabTerceros = new ArrayList<>();

                            Map<Object, Object> pfechas = new HashMap<>();
                            pfechas.put("1", this.fechaIni);
                            pfechas.put("2", this.fechaIni);

                            Cacon008m preCalculoAbierto = cacon008mFacade.ultimoPreCalculoNominaAbierto(codModulo, empleadoCatalogo.getCodgn(), pfechas);
                            List<Object[]> trabajadores = cacon015dFacade.ultimoPreCalculoNominaAbiertoIndividual(codModulo, empleadoCatalogo.getCodgn(), String.valueOf(empleadoCatalogo.getCodpe()), fechaIni, fechaIni);

                            String iniciarCalculo = calcular.realizarCalculoDeIncidencias(_empCat, trabajadoresAux, trabTerceros, userAdmin, progreso, incidencias, acum_Permiso_Remunerado_Actual, acum_Permiso_Remunerado_Anterior, filtroSelec, fechaIni, fechaIni);

                            if (trabajadores.isEmpty() && preCalculoAbierto==null) { //No está en un pre cálculo ni individual ni general
                                if (incidencias.isEmpty()) {
                                    //Cargar los datos que serán mostrados en pantalla.
                                    cargarDatos(conexion, iniciarCalculo, fecha_fString, _idTurno);

                                } else {
                                    muestroAusenciaDia = true;
                                    FechaHoy = jbvarios.fechaToString(fechaIni, "dd/MM/yyyy");
                                }
                            } else {
                                //Está en un pre cálculo, dejo crear la autorización
                                //Cargar los datos que serán mostrados en pantalla.
                                cargarDatos(conexion, iniciarCalculo, fecha_fString, _idTurno);
                            }
                            conexion.close();
                        } else {
                            //no valido contadores pero consulto horario según las tablas catur004a y catur005m dado el codigo de turno..
                            Map<Object, Object> param = new HashMap<Object, Object>();
                            param.put("1", _idTurno);

                            List<Catur004a> catur004a = catur004aFacade.buscarListaTurno(codModulo, param);

                            String[] priPr = new String[catur004a.size()];
                            String[] fhiniPr = new String[catur004a.size()];
                            String[] fhfinPr = new String[catur004a.size()];
                            for (int i = 0; i < catur004a.size(); i++) {
                                if (catur004a.get(i).getCatur005m().getCodct() != null) {
                                    priPr[i] = String.valueOf(catur004a.get(i).getCatur005m().getCodct());
                                } else {
                                    priPr[i] = String.valueOf(2);
                                }

                                fhiniPr[i] = jbvarios.fechaToString(fecha_formulario, "dd/MM/yyyy") + " " + jbvarios.fechaToString(catur004a.get(i).getHrini(), "HH:mm:ss");

                                if (catur004a.get(i).getDiafin() == 2) {
                                    fhfinPr[i] = jbvarios.fechaToString(fecha_formulario, "dd/MM/yyyy") + " " + jbvarios.fechaToString(catur004a.get(i).getHrfin(), "HH:mm:ss");
                                } else if (catur004a.get(i).getDiafin() == 3) {
                                    String SumarFecha = jbvarios.SumarFecha(jbvarios.fechaToString(fecha_formulario, "dd/MM/yyyy"), 1, "dd/MM/yyyy");
                                    fhfinPr[i] = SumarFecha + " " + jbvarios.fechaToString(catur004a.get(i).getHrfin(), "HH:mm:ss");
                                }
                            }

                            marcaje = new ArrayList<Marcajes>();
                            for (int i = 0; i < catur004a.size(); i++) {
                                boolean band = true;
                                if (Integer.parseInt(priPr[i]) == extra) {
                                    Marcajes objN = new Marcajes("", "", "");
                                    objN.setTipo(priPr[i]);
                                    i = acoplarTurnos(priPr, fhiniPr, fhfinPr, i, catur004a.size(), band, objN, String.valueOf(extra));
                                } else {
                                    Marcajes obj = new Marcajes(fhiniPr[i], fhfinPr[i], priPr[i]);
                                    marcaje.add(obj);
                                }
                            }

                            if (!marcaje.isEmpty()) {
                                Fhsis = jbvarios.fechaToString(ngvar002tFacade.getCurrentDateTime(), "dd/MM/yyyy HH:mm:ss");
                                if (edit == 0) {
                                    relojST = new Carel001x();
                                    fhiniST = null;
                                    fhfinST = null;
                                    obsthST = null;
                                }
                                Map<Object, Object> parametrosmotST = new HashMap<Object, Object>();
                                parametrosmotST.put("1", ejbFacade.getCodem());
                                parametrosmotST.put("2", 'M');

                                motivoST = camot001mFacade.buscarListaMotAct(codModulo, parametrosmotST);

                                Map<Object, Object> parametrosactST = new HashMap<Object, Object>();
                                parametrosactST.put("1", ejbFacade.getCodem());
                                parametrosactST.put("2", 'A');
                                actividadST = camot001mFacade.buscarListaMotAct(codModulo, parametrosactST);

                                fechaElegida = jbvarios.fechaToString(fechaIni, "dd/MM/yyyy");
                                mostrarHorarios = true;
                            }
                        }
                    }
                } else {
                    Utilidades.mostrarMensaje(
                            1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                            ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincSinTurno"));
                }
            } else {
                Utilidades.mostrarMensaje(
                        1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutVac"));
            }
        }
    }

    /**
     * Método para determinar si un trabajador, tiene un turno asignado para un día en específico
     *
     * @param _codpe Código del trabajador
     * @param _fecha Fecha en la que se quiere revisar si el trabajador posee un turno
     * @return True: si posee un turno, False: si no tiene un turno asignado para ese día
     */
    public int validarTurno(int _codpe, Date _fecha) {
        try {
            String fechaString = jbvarios.fechaToString(_fecha, "dd/MM/yyyy");
            StringBuilder sentencia = new StringBuilder("execute procedure buscar_turno(" + _codpe + ",to_date('" + fechaString + "','%d/%m/%Y'))");
            Object singleResult = ejbFacade.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();

            if (Integer.parseInt(singleResult.toString()) > 0) {
                return Integer.parseInt(singleResult.toString());
            } else {
                return -1;
            }
        } catch (Exception ex) {
            logger.debug(ejbFacade.getCusua() + " " + ex.getMessage());
            java.util.logging.Logger.getLogger(Caaut001mController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * Método que determina los componentes que se visualizan en el formulario, dependiendo de esto se muestran otros más definidos por el valor del
     * rendered del h:panelGroup
     */
    public void setListConsulta() {

        String _marcHab = "";
        String valpaSobreTiempo = "";
        try {
            _marcHab = tgpar002dFacade.consultarParametro("MAMAH").getValpa();
            valpaSobreTiempo = tgpar002dFacade.consultarParametro("MASOB").getValpa();
        } catch (Exception e) {
            logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
        }

        if ((userAdmin || userSupervisor) && caaut002m.getCaaut002mPK().getCodma() == Integer.parseInt(_marcHab)) {
            criterio = 1;
        } else if (caaut002m.getCaaut002mPK().getCodma() == Integer.parseInt(valpaSobreTiempo)) {
            criterio = 0;
        }
    }

    /**
     * Método para procesar los marcajes habituales
     *
     */
    public String procesarInsertMH() {
        //enviar
        String retorno = "";
        boolean procesar = true;
        Date fhoy = jbvarios.stringToFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy"), "dd/MM/yyyy");
        if (reloj.getNomre() == null) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutCatRej"));
            procesar = false;
        } else if (fechaIniC1 == null) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutFini"));
            procesar = false;
        } else if (fechaFinC1 != null && fechaFinC1.before(fechaIniC1)) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutFfin"));
            procesar = false;
        } else if (fhoy.after(fechaIniC1)) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutFhoy"));
            procesar = false;
        }

        if (procesar) {
            if (editarMH == 0) {
                //realizo el registro
                UserTransaction transaction = null;
                try {
                    transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                    transaction.begin();
                    Caaut001m maestro = new Caaut001m(new Caaut001mPK());
                    maestro.getCaaut001mPK().setCodem(ejbFacade.getCodem());
                    Caaut002m caaut002mp = caaut002mFacade.buscarRegistro(Integer.parseInt(tgpar002dFacade.consultarParametro("MAMAH").getValpa()), codModulo, ejbFacade.getCodem());
                    maestro.setCaaut002m(caaut002mp);
                    maestro.setNgbas001tCodpe(ngbas001xFacade.find(empleadoCatalogo.getCodpe()));
                    maestro.setNgbas001tCodsu(ngbas001xFacade.find(empleadoCatalogo.getCodpe()));

                    Carel001x carel001x = carel001xFacade.find(reloj.getCodre());
                    maestro.setCarel001x(carel001x);

                    maestro.setFhiau(fechaIniC1);
                    maestro.setFhfau(fechaFinC1);
                    maestro.setStaau('A');

                    maestro.setCusua(ejbFacade.getCusua());
                    maestro.setCusuc(ejbFacade.getCusua());
                    Date fh = ejbFacade.getCurrentDateTime();
                    maestro.setFhmod(fh);
                    maestro.setFhcre(fh);
                    getFacade().create(maestro);
                    transaction.commit();
                    reloj = new Carel001x();
                    fechaFinC1 = null;
                    fechaIniC1 = null;
                    editarMH = 0;
                    retorno = "detalleConsulta.xhtml";
                    Utilidades.mostrarMensaje(
                            0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                            ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado"));
                } catch (Exception e) {
                    if (transaction != null) {
                        try {
                            transaction.rollback();
                        } catch (Exception ex) {
                            logger.debug(e.getMessage());
                            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                        }
                    }
                }
            }
            if (editarMH == 1) {
                //realizo el editar
                UserTransaction transaction = null;
                try {
                    transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                    transaction.begin();

                    Carel001x carel001x = carel001xFacade.find(reloj.getCodre());
                    caaut001mMH.setCarel001x(carel001x);

                    caaut001mMH.setFhiau(fechaIniC1);
                    caaut001mMH.setFhfau(fechaFinC1);

                    caaut001mMH.setCusua(ejbFacade.getCusua());

                    Date fh = ejbFacade.getCurrentDateTime();
                    caaut001mMH.setFhmod(fh);

                    getFacade().edit(caaut001mMH);
                    transaction.commit();

                    reloj = new Carel001x();
                    fechaFinC1 = null;
                    fechaIniC1 = null;
                    editarMH = 0;
                    Utilidades.mostrarMensaje(
                            0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                            ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado"));
                } catch (Exception e) {
                    if (transaction != null) {
                        try {
                            transaction.rollback();
                        } catch (Exception ex) {
                            logger.debug(e.getMessage());
                            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                        }
                    }
                }
            }
        }

        if (retorno.compareTo("") != 0) {
            FacesContext instance = FacesContext.getCurrentInstance();
            ExternalContext ec = instance.getExternalContext();
            ec.getRequestMap().put("empcat", empleadoCatalogo);
        }

        return retorno;
    }

    /**
     * Método que acopla todos los turnos que son demoninados "Extra"
     *
     * @param _priPr String[]
     * @param _fhiniPr String[] Fechas Iniciales
     * @param _fhfinPr String[] Fechas Finales
     * @param _i Valor i de la iteración
     * @param _posPr Tamaño de los arreglos
     * @param _band Bandera True para el primer ingreso
     * @param objN Objeto de tipo Marcajes
     * @param _paramExtra Parámetro para determinar el valor de contar extra ("3" que viene de los parametros)
     * @return i donde quedó en la iteración
     */
    private int acoplarTurnos(String[] _priPr, String[] _fhiniPr, String[] _fhfinPr, int _i, int _posPr, boolean _band, Marcajes objN, String paramExtra) {
        if (_band) {
            objN.setFechaI(_fhiniPr[_i]);
            _band = false;
        }

        if (_i + 1 < _posPr) {
            if (_priPr[_i].compareTo(paramExtra) == 0 && _priPr[_i + 1].compareTo(paramExtra) == 0) {
                return acoplarTurnos(_priPr, _fhiniPr, _fhfinPr, _i + 1, _posPr, _band, objN, paramExtra);
            } else {
                objN.setFechaF(_fhfinPr[_i]);
                marcaje.add(objN);
                return _i;
            }
        } else {
            objN.setFechaF(_fhfinPr[_i]);
            marcaje.add(objN);
            return _i;
        }
    }

    /**
     * Método que dado el código del tipo, retorna el nombre del tipo
     *
     * @param _tipo 1.Trabajo 2.Descanso 3.Extra
     * @return String con el tipo
     */
    public String consultarTipo(String _tipo) {
        if (_tipo.compareTo("1") == 0) {
            return "Trabajo";
        } else if (_tipo.compareTo("2") == 0) {
            return "Descanso";
        } else {
            return "Extra";
        }
    }

    /**
     * Método para agregar el sobretiempo en la base de datos
     *
     * @param edit 0-Agregar 1-Editar
     * @return Null si a ocurrido un inconveniente, detalleConsulta.xhtml en caso de ralizar bien el registro
     */
    public String guardarST(int edit) {
        String retorno = "";
        imprimir = false;
        msjSolapdo = false;
        textoMsjSolapado = "";
        if (relojST.getNomre() == null) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutCatRej"));
        } else {

            if (!marcaje.isEmpty()) {

                String fecha = jbvarios.fechaToString(fechaIni, "dd/MM/yyyy");
                fhiniST = jbvarios.stringToFecha(fecha + " " + jbvarios.fechaToString(fhiniST, "HH:mm:ss"), "dd/MM/yyyy HH:mm:ss");
                fhfinST = jbvarios.stringToFecha(fecha + " " + jbvarios.fechaToString(fhfinST, "HH:mm:ss"), "dd/MM/yyyy HH:mm:ss");

                if (fhfinST.before(fhiniST)) {
                    Utilidades.mostrarMensaje(
                            1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                            ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutFfin"));
                } else {
                    List<Date> fhiniExtra = new ArrayList<Date>();
                    List<Date> fhfinExtra = new ArrayList<Date>();
                    for (int m = 0; m < marcaje.size(); m++) {
                        if (marcaje.get(m).getTipo().compareTo(String.valueOf(extra)) == 0) {
                            Date fini = jbvarios.stringToFecha(marcaje.get(m).getFechaI(), "dd/MM/yyyy HH:mm:ss");
                            Date ffin = jbvarios.stringToFecha(marcaje.get(m).getFechaF(), "dd/MM/yyyy HH:mm:ss");
                            fhiniExtra.add(fini);
                            fhfinExtra.add(ffin);
                        }
                    }

                    boolean procesoST = false;
                    for (int i = 0; i < fhiniExtra.size(); i++) {
                        if ((fhiniST.after(fhiniExtra.get(i)) || fhiniST.equals(fhiniExtra.get(i))) && (fhfinST.before(fhfinExtra.get(i)) || fhfinST.equals(fhfinExtra.get(i)))) {
                            procesoST = true;
                        }
                    }

                    if (procesoST) {
                        //Consultar solapamiento de sobretiempos...
                        Map<Object, Object> pSolapamiento = new HashMap<Object, Object>();
                        pSolapamiento.put("1", ejbFacade.getCodem());
                        pSolapamiento.put("2", fhiniST);
                        pSolapamiento.put("3", fhfinST);
                        pSolapamiento.put("4", empleadoCatalogo.getCodpe());

                        if (edit == 0) {
                            List<Caaut001m> solapado = ejbFacade.solapamientoSobretiempo(pSolapamiento, codModulo);
                            //Registro el sobre tiempo
                            if (solapado == null) {
                                Caaut001m maestro = new Caaut001m(new Caaut001mPK());
                                UserTransaction transaction = null;
                                try {
                                    transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                                    transaction.begin();

                                    maestro.getCaaut001mPK().setCodem(ejbFacade.getCodem());
                                    Caaut002m caaut002mp = caaut002mFacade.buscarRegistro(Integer.parseInt(tgpar002dFacade.consultarParametro("MASOB").getValpa()), codModulo, ejbFacade.getCodem());
                                    maestro.setCaaut002m(caaut002mp);

                                    maestro.setNgbas001tCodpe(ngbas001xFacade.find(empleadoCatalogo.getCodpe()));
                                    maestro.setNgbas001tCodsu(ngbas001xFacade.find(empleadoCatalogo.getCodpe()));

                                    Carel001x carel001x = carel001xFacade.find(relojST.getCodre());
                                    maestro.setCarel001x(carel001x);

                                    maestro.setFhiau(fhiniST);
                                    maestro.setFhfau(fhfinST);
                                    maestro.setStaau('A');

                                    maestro.setCamot001mCodmo(motivoSTagregar);
                                    maestro.setCamot001mCodac(actividadSTagregar);
                                    maestro.setObsth(obsthST);

                                    maestro.setCusua(ejbFacade.getCusua());
                                    maestro.setCusuc(ejbFacade.getCusua());
                                    Date fh = ejbFacade.getCurrentDateTime();
                                    maestro.setFhmod(fh);
                                    maestro.setFhcre(fh);
                                    getFacade().create(maestro);

                                    transaction.commit();
                                    Utilidades.mostrarMensaje(
                                            0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                                            ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado"));
                                    caaut002m = new Caaut002m(new Caaut002mPK());
                                    fechaIni = null;
                                    mostrarHorarios = false;
                                    muestroAusenciaDia = false;
                                    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                                    ec.getRequestMap().put("empcat", empleadoCatalogo);
                                    retorno = "detalleConsulta.xhtml";

                                } catch (Exception e) {
                                    if (transaction != null) {
                                        try {
                                            transaction.rollback();
                                        } catch (Exception ex) {
                                            logger.debug(e.getMessage());
                                            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                                        }
                                    }
                                }
                            } else {
                                textoMsjSolapado = jbvarios.fechaToString(solapado.get(0).getFhiau(), "dd/MM/yyyy HH:mm:ss") + " " + jbvarios.fechaToString(solapado.get(0).getFhfau(), "dd/MM/yyyy HH:mm:ss");
                                msjSolapdo = true;
                            }
                        } else {
                            //EDITO REGISTRO
                            pSolapamiento.put("5", caaut001mrecibido.getCaaut001mPK().getCodau());
                            List<Caaut001m> solapado = ejbFacade.solapamientoSobretiempoEdit(pSolapamiento, codModulo);
                            //Registro el sobre tiempo
                            if (solapado == null) {

                                UserTransaction transaction = null;
                                try {
                                    transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                                    transaction.begin();

                                    Carel001x carel001x = carel001xFacade.find(relojST.getCodre());
                                    caaut001mrecibido.setCarel001x(carel001x);

                                    caaut001mrecibido.setFhiau(fhiniST);
                                    caaut001mrecibido.setFhfau(fhfinST);

                                    caaut001mrecibido.setCamot001mCodmo(motivoSTagregar);
                                    caaut001mrecibido.setCamot001mCodac(actividadSTagregar);
                                    caaut001mrecibido.setObsth(obsthST);

                                    caaut001mrecibido.setCusua(ejbFacade.getCusua());

                                    Date fh = ejbFacade.getCurrentDateTime();
                                    caaut001mrecibido.setFhmod(fh);

                                    getFacade().edit(caaut001mrecibido);

                                    transaction.commit();
                                    Utilidades.mostrarMensaje(
                                            0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                                            ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado"));
                                    caaut002m = new Caaut002m(new Caaut002mPK());
                                    fechaIni = null;
                                    mostrarHorarios = false;
                                    muestroAusenciaDia = false;
                                    this.edit = 0;

                                    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                                    ec.getRequestMap().put("empcat", empleadoCatalogo);
                                    retorno = "detalleConsulta.xhtml";

                                } catch (Exception e) {
                                    if (transaction != null) {
                                        try {
                                            transaction.rollback();
                                        } catch (Exception ex) {
                                            logger.debug(e.getMessage());
                                            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                                        }
                                    }
                                }
                            } else {
                                textoMsjSolapado = jbvarios.fechaToString(solapado.get(0).getFhiau(), "dd/MM/yyyy HH:mm:ss") + " " + jbvarios.fechaToString(solapado.get(0).getFhfau(), "dd/MM/yyyy HH:mm:ss");
                                msjSolapdo = true;
                            }
                        }
                    } else {
                        Utilidades.mostrarMensaje(
                                1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                                ResourceBundle.getBundle(Utilidades.BUNDLE).getString("Caaut001mTitleincAutTabHorariosMsjSTExtra"));
                    }
                }
            }
        }
        return retorno;
    }

    /**
     * Método que carga los parametros y redirecciona al módulo de incidencias no autorizadas para priomeramente justificar estas incidencias antes de
     * crear la autorizacion para un sobretiempo
     *
     * @return
     */
    public String enviarIncNoAutorizadas() {

        consulta = "Individual";
        criterio = 2; //individual
        autSobretiempo = "S";
        listaPersonas = new ArrayList<Ngbas001x>();
        listaPersonas.add(empleadoCatalogo);

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put("incidencias", incidencias);
        ec.getRequestMap().put("remActual", acum_Permiso_Remunerado_Actual);
        ec.getRequestMap().put("remAnterior", acum_Permiso_Remunerado_Anterior);
        ec.getRequestMap().put("check", filtroSelec);
        ec.getRequestMap().put("fechaDesde", ejbFacade.getCurrentDateTime());
        ec.getRequestMap().put("fechaHasta", ejbFacade.getCurrentDateTime());
        ec.getRequestMap().put("consulta", consulta);
        ec.getRequestMap().put("autSobretiempo", autSobretiempo);
        ec.getRequestMap().put("trabajadores", listaPersonas);
        ec.getRequestMap().put("criterio", criterio);
        ec.getRequestMap().put("calcular", calcular);

        return "/Proyecto/gestiondepersonal/incidencias/incidenciasnoautorizadas/Consulta.xhtml";
    }
    //</editor-fold>
}
