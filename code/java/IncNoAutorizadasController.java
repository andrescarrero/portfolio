package com.fenix.control.controller.gestiondepersonal.incidencias.incidenciasnoautorizadas;
//<editor-fold defaultstate="collapsed" desc="Importaciones">

import com.fenix.control.controller.reportes.ausencias.ausencia;
import com.fenix.control.controller.reportes.sobretiempo.sobretiempoDiurno;
import com.fenix.control.controller.reportes.sobretiempo.sobretiempoNocturno;
import com.fenix.control.fenixTools.util.Utilidades;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.reclamosdeincidencias.Capar001m;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Capar001mFacade;

import java.util.ResourceBundle;
import com.fenix.control.controller.util.JsfUtil;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;

//Lazy
import org.primefaces.model.LazyDataModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import com.fenix.control.session.sessionBean;
import com.fenix.control.session.Permisos;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.autorizaraterceros.Caate001m;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.incidenciasnoautorizadas.IncNoAutorizadas;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.incidenciasnoautorizadas.Ngnom018t;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.incidenciasnoautorizadas.Ngvar005t;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.incidenciasnoautorizadas.calculoIncidencias;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.incidenciasnoautorizadas.trabajadoresEnPrecalculo;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Casbt001m;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Casbt002d;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Ngvar001t;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Ngvar002t;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Ngvar002tPK_a;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Ngvar002t_a;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Ngvar006t;
import com.fenix.logica.entidades.marcajeenlinea.Camot001m;
import com.fenix.logica.entidades.pasonomina.Cacon008m;
import com.fenix.logica.entidades.talentohumano.trabajador.Ngbas001x;
import com.fenix.logica.entidades.talentohumano.trabajador.Ngnom001x;
import com.fenix.logica.entidades.vistasgenerales.Ngbas009t;
import com.fenix.util.Progreso;
//import com.fenix.util.ContadoresPorPersona_;
import com.fenix.util.jbVarios;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;

//Lazy
import javax.transaction.UserTransaction;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
//</editor-fold>

/**
 * <br><b>Title:</b> </br> <br><b>Description:</b> .</br> <br><b>Copyright:</b>
 * Copyright (c) 2011</br> <br><b>Company:</b> Pasteurizadora Tachira CA</br>
 *
 * @author Andres Carrero
 * @version 1.0 20/11/2015
 */
@ManagedBean(name = "incnoautorizadasController")
@ViewScoped
public class IncNoAutorizadasController {
//<editor-fold defaultstate="collapsed" desc="Declaracion de Variables">

    private Capar001m current;
    private Capar001m currentDestroy;
    private Capar001m currentAgregar;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Capar001mFacade ejbFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001mFacade casbt001mFacade;
    @EJB
    private com.fenix.logica.jpa.adminsistema.notificaciones.Tgcor003mFacade tgcor003mFacade;
    @EJB
    private com.fenix.logica.jpa.adminsistema.gruposparametros.Tgpar002dFacade tgpar002dFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.autorizaraterceros.Caate001mFacade caate001mFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002tFacade ngvar002tFacade;
    @EJB
    private com.fenix.logica.jpa.talentohumano.trabajador.Ngbas001xFacade ngbas001xFacade;
    @EJB
    private com.fenix.logica.jpa.vistasgenerales.Ngbas009tFacade ngbas009tFacade;
    @EJB
    private com.fenix.logica.jpa.talentohumano.trabajador.Ngnom001xFacade ngnom001xFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Cacon011tFacade cacon011tFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Capar001mFacade capar001mFacade;
    @EJB
    private com.fenix.logica.jpa.marcajeenlinea.Camot001mFacade camot001mFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt002dFacade casbt002dFacade;
    @EJB
    private com.fenix.logica.jpa.marcajeenlinea.Catur004aFacade catur004aFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Ngvar005tFacade ngvar005tFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Ngvar007tFacade ngvar007Facade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.reclamosdeincidencias.Ngvar001tFacade ngvar001tFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002tFacade_a ngvar002t_aFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Ngnom018tFacade ngnom018tFacade;
    @EJB
    private com.fenix.logica.jpa.funciones_procedimientos.funcProc funciones;
    @EJB
    private com.fenix.logica.jpa.pasonomina.Cacon008mFacade cacon008mFacade;
    @EJB
    private com.fenix.logica.jpa.pasonomina.Cacon015dFacade cacon015dFacade;
    private int selectedItemIndex;
    private String codModulo = "SCAF0799"; //SCAFL007 -> Local    SCAF0799->Pastca
    /*
     * Para el Lazy
     */
    private LazyDataModel<Capar001m> lazyModel;
    private Integer pagIndex = null;
    private Integer paginacion = null;
    private Map<String, Object> fields = new HashMap<String, Object>();
    private String sortF = null;
    private int cantElemTabla = 20;
    private SortOrder sortB = SortOrder.UNSORTED;
    private boolean permisologia[] = new boolean[10];
    private final static Logger logger = Logger.getLogger(Capar001m.class.getName());
    /*
     * Fin de Lazy
     */
    private String ftoFhBD = "%d/%m/%Y";
    private String ftoFhJav = "dd/MM/yyyy";
    private String ftoFhJavExt = "dd/MM/yyyy HH:mm:ss";
    private String ftoHora = "HH:mm:ss";
    /*
     * Variables del List
     */
    private Date fechaDesde = null;
    private Date fechaHasta = null;
    private int grupn = 0;
    private String filtroSelec[]; //Ausencias - Sobretiempo
    private String consulta = null;
    private int criterio = 0;
    private List<Ngbas009t> listaDepartamentos = new ArrayList<Ngbas009t>();
    private boolean userSupervisor; //Perfil supervisor
    private boolean userAdmin;      //Perfil administrador
    private List<Ngbas001x> listaPersonas = new ArrayList<Ngbas001x>(); //Catálogo personas
    private List<Ngnom001x> gruposNomina = new ArrayList<Ngnom001x>(); //Catálogo grupos de nómina
    private String codpesTerceros = ""; //Códigos de los trabajadores a visualizar.
    private String coddpsSubordinados = ""; //Códigos de los departamentos a visualizar (Subordinados).    
    private String coddpsTerceros = ""; //Códigos de los departamentos a visualizar (Terceros).    
    private String codgnStringSubordinados = ""; //Códigos de grupos de nómina visualizar (Subordinados).    
    private String codgnStringTerceros = ""; //Códigos de grupos de nómina visualizar (Terceros).    
    private List<Integer> codgnSubordinados; //Códigos de grupos de nómina visualizar (Subordinados).    
    private List<Integer> codgnTerceros; //Códigos de grupos de nómina visualizar (Terceros).    
    private List<Integer> trabTerceros;
    private List<Integer> depTerceros;
    private List<Integer> depSubordinados;
    jbVarios jbvarios = new jbVarios();
    private String condicionJpql;
    /*
     * Variables de Consulta.xhtml
     */
    private List<IncNoAutorizadas> incidencias;
    private List<IncNoAutorizadas> filteredIncidencias;
    private IncNoAutorizadas selIncidencia;
    private String[] tipos; //"Ausencia" "Sobretiempo"
    private List<Casbt002d> tiposSobretiempo;
    private List<Camot001m> motivosSobretiempo;
    private List<Camot001m> actividadSobretiempo;
    private List<Ngvar005t> tiposAusencia;
    private List<Ngvar001t> motivosAusencia;
    private boolean mostrarUnirDia = false;
    private IncNoAutorizadas incAnterior;
    private IncNoAutorizadas incSiguiente;
    private String incFechaIni;
    private String incFechaFin;
    private String incDuracion;
    private boolean rango;
    private IncNoAutorizadas rangoPrimerInc;
    private IncNoAutorizadas rangoUltimaInc;
    private String rangoInicial;
    private String rangoFinal;
    private String rangoDuracion;
    private Date minInicial;
    private Date maxInicial;
    private Date minSelected;
    private Date maxSelected;
    private List<IncNoAutorizadas> incEnRango;
    private boolean mostrar;
    private StreamedContent rutaArchivo;
    private String autSobretiempo;
    private String idCalculo;
    private List<trabajadoresEnPrecalculo> enPreCalculo = new ArrayList<>();

    /**
     * Se crearon dos HashMap de acumulado ya que se puede dar el caso de que por consulta se quieran ver las incidencias que involucren dos años
     * diferentes, por ejemplo una consulta de incidencias del 26 de Diciembre del 2016 al 06 de Enero de 2017, en ese caso segun la fecha de la
     * incidencia, se mostrará uno u otro acumulado, acum_Permiso_Remunerado_Anterior guardará el acumulado para el año anterior,
     * acum_Permiso_Remunerado_Actual lo hará para el año actual.
     */
    private Map<Integer, String> acum_Permiso_Remunerado_Actual = new HashMap<>();
    private Map<Integer, String> acum_Permiso_Remunerado_Anterior = new HashMap<>();

    private String etiqueta_Acum;
    private String horas_acumuladas;

    private String tipos_Remunerados = "";   //Tipos de ausencias remunerados (códigos) separados por ",".
    private String motivos_Remunerados = ""; //Motivos de ausencias remunerados (códigos) separados por ",".
    private IncNoAutorizadas editadaAnterior;
    private String fechaMinima;
    private String fechaMaxima;
    //Variables de la barra de progreso
    private Progreso progreso;
    private calculoIncidencias calcular;

//</editor-fold>
    public IncNoAutorizadasController() {
        java.util.Arrays.fill(permisologia, Boolean.TRUE);
        //consultarPermisos(codModulo);
    }
    //<editor-fold defaultstate="collapsed" desc="Encapsulamiento">

    public Capar001m getCurrent() {
        return current;
    }

    public void setCurrent(Capar001m current) {
        this.current = current;
    }

    public Capar001m getSelected() {
        if (current == null) {
            current = new Capar001m();
            //Si clave primaria Compuesta Inicializarla
            selectedItemIndex = -1;
        }
        return current;
    }

    private Capar001mFacade getFacade() {
        return ejbFacade;
    }

    public boolean[] getPermisologia() {
        return permisologia;
    }

    public void setPermisologia(boolean[] permisologia) {
        this.permisologia = permisologia;
    }

    public void setLazyModel(LazyDataModel<Capar001m> lazyModel) {
        this.lazyModel = lazyModel;
    }
//fin lazy

    public String getCodModulo() {
        return codModulo;
    }

    public void setCodModulo(String codModulo) {
        this.codModulo = codModulo;
    }

    public Capar001m getCurrentAgregar() {
        return currentAgregar;
    }

    public void setCurrentAgregar(Capar001m currentAgregar) {
        this.currentAgregar = currentAgregar;
    }

    public Capar001m getCurrentDestroy() {
        return currentDestroy;
    }

    public void setCurrentDestroy(Capar001m currentDestroy) {
        this.currentDestroy = currentDestroy;
    }

    public int getCantElemTabla() {
        return cantElemTabla;
    }

    public void setCantElemTabla(int cantElemTabla) {
        this.cantElemTabla = cantElemTabla;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public int getGrupn() {
        return grupn;
    }

    public void setGrupn(int grupn) {
        this.grupn = grupn;
    }

    public String[] getFiltroSelec() {
        return filtroSelec;
    }

    public void setFiltroSelec(String[] filtroSelec) {
        this.filtroSelec = filtroSelec;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public int getCriterio() {
        return criterio;
    }

    public void setCriterio(int criterio) {
        this.criterio = criterio;
    }

    public List<Ngbas009t> getListaDepartamentos() {
        return listaDepartamentos;
    }

    public void setListaDepartamentos(List<Ngbas009t> listaDepartamentos) {
        this.listaDepartamentos = listaDepartamentos;
    }

    public boolean isUserSupervisor() {
        return userSupervisor;
    }

    public void setUserSupervisor(boolean userSupervisor) {
        this.userSupervisor = userSupervisor;
    }

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    public List<Ngbas001x> getListaPersonas() {
        return listaPersonas;
    }

    public void setListaPersonas(List<Ngbas001x> listaPersonas) {
        this.listaPersonas = listaPersonas;
    }

    public Capar001mFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(Capar001mFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public String getCodpesTerceros() {
        return codpesTerceros;
    }

    public void setCodpesTerceros(String codpesTerceros) {
        this.codpesTerceros = codpesTerceros;
    }

    public String getCoddpsSubordinados() {
        return coddpsSubordinados;
    }

    public void setCoddpsSubordinados(String coddpsSubordinados) {
        this.coddpsSubordinados = coddpsSubordinados;
    }

    public String getCoddpsTerceros() {
        return coddpsTerceros;
    }

    public void setCoddpsTerceros(String coddpsTerceros) {
        this.coddpsTerceros = coddpsTerceros;
    }

    public List<Integer> getDepTerceros() {
        return depTerceros;
    }

    public void setDepTerceros(List<Integer> depTerceros) {
        this.depTerceros = depTerceros;
    }

    public List<Integer> getDepSubordinados() {
        return depSubordinados;
    }

    public void setDepSubordinados(List<Integer> depSubordinados) {
        this.depSubordinados = depSubordinados;
    }

    public List<Integer> getTrabTerceros() {
        return trabTerceros;
    }

    public void setTrabTerceros(List<Integer> trabTerceros) {
        this.trabTerceros = trabTerceros;
    }

    public List<Ngnom001x> getGruposNomina() {
        return gruposNomina;
    }

    public void setGruposNomina(List<Ngnom001x> gruposNomina) {
        this.gruposNomina = gruposNomina;
    }

    public List<Integer> getCodgnSubordinados() {
        return codgnSubordinados;
    }

    public void setCodgnSubordinados(List<Integer> codgnSubordinados) {
        this.codgnSubordinados = codgnSubordinados;
    }

    public List<Integer> getCodgnTerceros() {
        return codgnTerceros;
    }

    public void setCodgnTerceros(List<Integer> codgnTerceros) {
        this.codgnTerceros = codgnTerceros;
    }

    public String getCodgnStringSubordinados() {
        return codgnStringSubordinados;
    }

    public void setCodgnStringSubordinados(String codgnStringSubordinados) {
        this.codgnStringSubordinados = codgnStringSubordinados;
    }

    public String getCodgnStringTerceros() {
        return codgnStringTerceros;
    }

    public void setCodgnStringTerceros(String codgnStringTerceros) {
        this.codgnStringTerceros = codgnStringTerceros;
    }

    public String getFtoFhBD() {
        return ftoFhBD;
    }

    public void setFtoFhBD(String ftoFhBD) {
        this.ftoFhBD = ftoFhBD;
    }

    public String getFtoFhJav() {
        return ftoFhJav;
    }

    public void setFtoFhJav(String ftoFhJav) {
        this.ftoFhJav = ftoFhJav;
    }

    public String getFtoFhJavExt() {
        return ftoFhJavExt;
    }

    public void setFtoFhJavExt(String ftoFhJavExt) {
        this.ftoFhJavExt = ftoFhJavExt;
    }

    public List<IncNoAutorizadas> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<IncNoAutorizadas> incidencias) {
        this.incidencias = incidencias;
    }

    public IncNoAutorizadas getSelIncidencia() {
        return selIncidencia;
    }

    public void setSelIncidencia(IncNoAutorizadas selIncidencia) {
        this.selIncidencia = selIncidencia;
    }

    public List<IncNoAutorizadas> getFilteredIncidencias() {
        return filteredIncidencias;
    }

    public void setFilteredIncidencias(List<IncNoAutorizadas> filteredIncidencias) {
        this.filteredIncidencias = filteredIncidencias;
    }

    public String[] getTipos() {
        return tipos;
    }

    public void setTipos(String[] tipos) {
        this.tipos = tipos;
    }

    public List<Casbt002d> getTiposSobretiempo() {
        return tiposSobretiempo;
    }

    public void setTiposSobretiempo(List<Casbt002d> tiposSobretiempo) {
        this.tiposSobretiempo = tiposSobretiempo;
    }

    public List<Camot001m> getMotivosSobretiempo() {
        return motivosSobretiempo;
    }

    public void setMotivosSobretiempo(List<Camot001m> motivosSobretiempo) {
        this.motivosSobretiempo = motivosSobretiempo;
    }

    public List<Camot001m> getActividadSobretiempo() {
        return actividadSobretiempo;
    }

    public void setActividadSobretiempo(List<Camot001m> actividadSobretiempo) {
        this.actividadSobretiempo = actividadSobretiempo;
    }

    public List<Ngvar005t> getTiposAusencia() {
        return tiposAusencia;
    }

    public void setTiposAusencia(List<Ngvar005t> tiposAusencia) {
        this.tiposAusencia = tiposAusencia;
    }

    public List<Ngvar001t> getMotivosAusencia() {
        return motivosAusencia;
    }

    public void setMotivosAusencia(List<Ngvar001t> motivosAusencia) {
        this.motivosAusencia = motivosAusencia;
    }

    public boolean isMostrarUnirDia() {
        return mostrarUnirDia;
    }

    public void setMostrarUnirDia(boolean mostrarUnirDia) {
        this.mostrarUnirDia = mostrarUnirDia;
    }

    public IncNoAutorizadas getIncAnterior() {
        return incAnterior;
    }

    public void setIncAnterior(IncNoAutorizadas incAnterior) {
        this.incAnterior = incAnterior;
    }

    public IncNoAutorizadas getIncSiguiente() {
        return incSiguiente;
    }

    public void setIncSiguiente(IncNoAutorizadas incSiguiente) {
        this.incSiguiente = incSiguiente;
    }

    public jbVarios getJbvarios() {
        return jbvarios;
    }

    public void setJbvarios(jbVarios jbvarios) {
        this.jbvarios = jbvarios;
    }

    public String getIncFechaIni() {
        return incFechaIni;
    }

    public void setIncFechaIni(String incFechaIni) {
        this.incFechaIni = incFechaIni;
    }

    public String getIncFechaFin() {
        return incFechaFin;
    }

    public void setIncFechaFin(String incFechaFin) {
        this.incFechaFin = incFechaFin;
    }

    public String getIncDuracion() {
        return incDuracion;
    }

    public void setIncDuracion(String incDuracion) {
        this.incDuracion = incDuracion;
    }

    public boolean isRango() {
        return rango;
    }

    public void setRango(boolean rango) {
        this.rango = rango;
    }

    public String getRangoInicial() {
        return rangoInicial;
    }

    public void setRangoInicial(String rangoInicial) {
        this.rangoInicial = rangoInicial;
    }

    public String getRangoFinal() {
        return rangoFinal;
    }

    public void setRangoFinal(String rangoFinal) {
        this.rangoFinal = rangoFinal;
    }

    public String getRangoDuracion() {
        return rangoDuracion;
    }

    public void setRangoDuracion(String rangoDuracion) {
        this.rangoDuracion = rangoDuracion;
    }

    public IncNoAutorizadas getRangoPrimerInc() {
        return rangoPrimerInc;
    }

    public void setRangoPrimerInc(IncNoAutorizadas rangoPrimerInc) {
        this.rangoPrimerInc = rangoPrimerInc;
    }

    public IncNoAutorizadas getRangoUltimaInc() {
        return rangoUltimaInc;
    }

    public void setRangoUltimaInc(IncNoAutorizadas rangoUltimaInc) {
        this.rangoUltimaInc = rangoUltimaInc;
    }

    public Date getMinInicial() {
        return minInicial;
    }

    public void setMinInicial(Date minInicial) {
        this.minInicial = minInicial;
    }

    public Date getMaxInicial() {
        return maxInicial;
    }

    public void setMaxInicial(Date maxInicial) {
        this.maxInicial = maxInicial;
    }

    public Date getMinSelected() {
        return minSelected;
    }

    public void setMinSelected(Date minSelected) {
        this.minSelected = minSelected;
    }

    public Date getMaxSelected() {
        return maxSelected;
    }

    public void setMaxSelected(Date maxSelected) {
        this.maxSelected = maxSelected;
    }

    public List<IncNoAutorizadas> getIncEnRango() {
        return incEnRango;
    }

    public void setIncEnRango(List<IncNoAutorizadas> incEnRango) {
        this.incEnRango = incEnRango;
    }

    public boolean isMostrar() {
        return mostrar;
    }

    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    public String getFtoHora() {
        return ftoHora;
    }

    public void setFtoHora(String ftoHora) {
        this.ftoHora = ftoHora;
    }

    public StreamedContent getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(StreamedContent rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getAutSobretiempo() {
        return autSobretiempo;
    }

    public void setAutSobretiempo(String autSobretiempo) {
        this.autSobretiempo = autSobretiempo;
    }

    public String getCondicionJpql() {
        return condicionJpql;
    }

    public void setCondicionJpql(String condicionJpql) {
        this.condicionJpql = condicionJpql;
    }

    public String getEtiqueta_Acum() {
        return etiqueta_Acum;
    }

    public void setEtiqueta_Acum(String etiqueta_Acum) {
        this.etiqueta_Acum = etiqueta_Acum;
    }

    public String getHoras_acumuladas() {
        return horas_acumuladas;
    }

    public void setHoras_acumuladas(String horas_acumuladas) {
        this.horas_acumuladas = horas_acumuladas;
    }

    public String getTipos_Remunerados() {
        return tipos_Remunerados;
    }

    public void setTipos_Remunerados(String tipos_Remunerados) {
        this.tipos_Remunerados = tipos_Remunerados;
    }

    public String getMotivos_Remunerados() {
        return motivos_Remunerados;
    }

    public void setMotivos_Remunerados(String motivos_Remunerados) {
        this.motivos_Remunerados = motivos_Remunerados;
    }

    public String getFechaMinima() {
        return fechaMinima;
    }

    public void setFechaMinima(String fechaMinima) {
        this.fechaMinima = fechaMinima;
    }

    public String getFechaMaxima() {
        return fechaMaxima;
    }

    public void setFechaMaxima(String fechaMaxima) {
        this.fechaMaxima = fechaMaxima;
    }

    public List<trabajadoresEnPrecalculo> getEnPreCalculo() {
        return enPreCalculo;
    }

    public void setEnPreCalculo(List<trabajadoresEnPrecalculo> enPreCalculo) {
        this.enPreCalculo = enPreCalculo;
    }

    public Progreso getProgreso() {
        if (progreso != null) {
            if (progreso.progreso == 100) {
                RequestContext confirm = RequestContext.getCurrentInstance();
                confirm.execute("PF('procesando').hide();");
            }
        }
        return progreso;
    }

    public void setProgreso(Progreso progreso) {
        this.progreso = progreso;
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Metodos Por Defecto">
    public String prepareList() {
        current = new Capar001m();
        //Si clave primaria Compuesta Inicializarla
        // recreateModel();
        //consultarPermisos(codModulo);
        return "List";
    }

    public String prepareView() {
        current = (Capar001m) lazyModel.getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        //consultarPermisos(codModulo);
        return "View";
    }

    public String prepareCreate() {
        currentAgregar = new Capar001m();
        //Si clave primaria Compuesta Inicializarla
        selectedItemIndex = -1;
        //consultarPermisos(codModulo);
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(currentAgregar);
            Utilidades.mostrarMensaje(
                    0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("MsjAlmacenado") + " " + ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"));
            recargarLazyModel();
            return prepareCreate();
        } catch (Exception e) {
            logger.error(e.getMessage());
            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));

            return null;
        }
    }

    public String prepareEdit() {
        current = (Capar001m) lazyModel.getRowData();
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
        // current = (Capar001m)getItems().getRowData();
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

    public List<Capar001m> ListAvailableSelectOne() {
        return ejbFacade.findAll(codModulo);
    }

    public List<Capar001m> ListAvailableSelectOne(String _codmo) {
        return ejbFacade.findAll(_codmo);
    }

    @FacesConverter(forClass = Capar001m.class, value = "IncNoAutorizadasConverter")
    public static class IncNoAutorizadasControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            IncNoAutorizadasController controller = (IncNoAutorizadasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "capar001mController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Capar001m) {
                Capar001m o = (Capar001m) object;
                return getStringKey(o.getCodpm());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Capar001mController.class.getName());
            }
        }
    }

    public void consultarPermisos(String _codmo) {
        FacesContext ctx;
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
            userAdmin = tgpar002dFacade.perfilAdmin();
            if (!userAdmin) {
                userSupervisor = tgpar002dFacade.perfilSupervisor();
            }

            condicionJpql = "";

            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            String desde = (String) ec.getRequestMap().get("fdsd");
            String hasta = (String) ec.getRequestMap().get("fhst");

            if (desde != null && hasta != null) {
                fechaDesde = jbvarios.stringToFecha(desde, this.ftoFhJav);
                fechaHasta = jbvarios.stringToFecha(hasta, this.ftoFhJav);
                actualizarCodpes(fechaDesde, fechaHasta);
            }

            fechaMaxima = jbvarios.Hoy(this.ftoFhJav);

            String parametro = tgpar002dFacade.consultarParametro("DIATR").getValpa();

            int diasAtras = Integer.parseInt(parametro);

            Date aux_fecha1 = jbvarios.stringToFecha(jbvarios.SumarFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), this.ftoFhJavExt), -diasAtras, this.ftoFhJavExt), this.ftoFhJav);
            Date inicioSistema = jbvarios.stringToFecha(tgpar002dFacade.consultarParametro(ejbFacade.getCodem(), "DIAIN").getValpa(), this.ftoFhJav);

            int duracion = jbvarios.FechaDif(jbvarios.fechaToString(aux_fecha1, this.ftoFhJav), jbvarios.fechaToString(inicioSistema, this.ftoFhJav), this.ftoFhJav);

            //Fecha mínima desde la que se puede consultar
            if (duracion > 0) {
                fechaMinima = jbvarios.fechaToString(aux_fecha1, this.ftoFhJav);
            } else {
                fechaMinima = jbvarios.fechaToString(inicioSistema, this.ftoFhJav);
            }

            fechaMinima = jbvarios.SumarFecha(jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), this.ftoFhJavExt), -3000, this.ftoFhJavExt);

        } catch (Exception e) {
            getFacade().getLog().error("Error de consulta ");
        }
    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Nuevos Metodos">

    /**
     * Método para cargar el list de la vista con los datos del grupo de nómina
     *
     * @param _codmo Código de módulo
     * @param _codcm Código de la empresa
     * @return Ngnom001x con los datos del grupo de nómina
     */
    public List<Ngnom001x> ListAvailableSelectOne(String _codmo, String _codcm) throws Exception {
        return casbt001mFacade.buscarGruposNomina(_codmo, _codcm);
    }

    /**
     * Método para establecer el catálogo por el cual se establecerá la búsqueda
     */
    public void setEdoConsulta() {
        if (consulta.compareTo("Todos") == 0) {
            criterio = 0;
        }
        if (consulta.compareTo("Departamento") == 0) {
            criterio = 1;
        }
        if (consulta.compareTo("Individual") == 0) {
            criterio = 2;
        }
        if (consulta.compareTo("Nomina") == 0) {
            criterio = 3;
        }
    }

    /**
     * Método para enviar los valores de los filtros a la página donde serán mostrados los resultados de la consulta
     *
     * @return
     * @throws java.lang.Exception
     */
    public String enviarParametros() throws Exception {

        Date fini = jbvarios.stringToFecha(jbvarios.formatearFecha(fechaDesde, "dd/MM/yyyy"), "dd/MM/yyyy");
        Date ffin = jbvarios.stringToFecha(jbvarios.formatearFecha(fechaHasta, "dd/MM/yyyy"), "dd/MM/yyyy");

        //validando que sean mayores o iguales la fecha final de la inicial
        if (ffin.before(fini)) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasDifFechas"));
            return null;
        }

        //validando que las fechas no sean mayores al día actual
        if (ffin.after(ejbFacade.getCurrentDateTime()) || fini.after(ejbFacade.getCurrentDateTime())) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasDifFechasHoy"));
            return null;
        }

        //Se valida que haya elejido alguno de los seleccionables del filtroSelec (Ausencias o Sobretiempos)
        if (filtroSelec.length < 1) {
            Utilidades.mostrarMensaje(
                    1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasIncidenciasRQ"));
            return null;
        }

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

        //Si selecciona por Departamentos
        if (consulta.compareTo("Departamento") == 0) {
            if (listaDepartamentos.isEmpty()) {
                Utilidades.mostrarMensaje(
                        1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasDepReq"));
                return null;
            }
        }

        //Si selecciona por Trabajadores
        if (consulta.compareTo("Individual") == 0) {
            if (listaPersonas.isEmpty()) {
                Utilidades.mostrarMensaje(
                        1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasIndvRQ"));
                return null;
            }
        }

        //Si selecciona por Grupo de Nómina
        if (consulta.compareTo("Nomina") == 0) {
            if (gruposNomina.isEmpty()) {
                Utilidades.mostrarMensaje(
                        1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasGrupoNomRQ"));
                return null;
            }
        }

        enPreCalculo = new ArrayList<>();
        String codpes = "";

        //Iniciando
        progreso = new Progreso(1, "Iniciando", false, 1);

        //Cargar los trabajadores de los que se quiere consultar los contadores.
        ArrayList _trabajadores = new ArrayList<>();
        List<Integer> _trabajadoresAux = new ArrayList<>();

        Map<Object, Object> _pfeini = new HashMap<>();
        _pfeini.put("1", fechaDesde);
        //Se cargaran los trabajadores de los cuales se quieren consultar sus ausencias o sobretiempos.
        List<Ngbas001x> _trab = new ArrayList<>();

        //Cargando trabajadores
        progreso.setEstadoProgreso("Realizando carga de trabajadores");
        if (userAdmin) {

            if (consulta.compareTo("Departamento") == 0) {
                _trab = ngbas001xFacade.personalPorDepartamentoAcRet(_pfeini, ejbFacade.getCodpe(), listaDepartamentos, codpesTerceros, codModulo, true);
            } else if (consulta.compareTo("Nomina") == 0) {
                _trab = ngbas001xFacade.personalPorGrupoNominaAcRet(_pfeini, ejbFacade.getCodpe(), gruposNomina, codpesTerceros, codModulo, true);
            } else if (consulta.compareTo("Individual") == 0) {
                _trab = listaPersonas;
            } else {
                //Filtro todos..
                _trab = ngbas001xFacade.todosLosDisponiblesAcRet(_pfeini, ejbFacade.getCodpe(), codpesTerceros, codModulo, true);
            }
            for (int i = 0; i < _trab.size(); i++) {
                _trabajadores.add(_trab.get(i).getCodpe());
                codpes += _trab.get(i).getCodpe() + ",";
            }
        } else if (userSupervisor) {
            if (consulta.compareTo("Departamento") == 0) {
                _trab = ngbas001xFacade.personalPorDepartamentoAcRet(_pfeini, ejbFacade.getCodpe(), listaDepartamentos, codpesTerceros, codModulo, false);
            } else if (consulta.compareTo("Nomina") == 0) {
                _trab = ngbas001xFacade.personalPorGrupoNominaAcRet(_pfeini, ejbFacade.getCodpe(), gruposNomina, codpesTerceros, codModulo, false);
            } else if (consulta.compareTo("Individual") == 0) {
                _trab = listaPersonas;
            } else {
                //Filtro todos..
                _trab = ngbas001xFacade.todosLosDisponiblesAcRet(_pfeini, ejbFacade.getCodpe(), codpesTerceros, codModulo, false);
            }
            for (int i = 0; i < _trab.size(); i++) {
                _trabajadores.add(_trab.get(i).getCodpe());
                codpes += _trab.get(i).getCodpe() + ",";
            }
            _trabajadoresAux = _trabajadores;
        }
        progreso.setProgreso(6);
        progreso.setPorcentaje(progreso.getProgreso());

        codpes = codpes.substring(0, codpes.length() - 1);

        List<Integer> codgn = ngbas001xFacade.distincGrupoNomina(codModulo, codpes, ejbFacade.getCodem());
        Map<Object, Object> pfechas = new HashMap<>();
        pfechas.put("1", this.fechaDesde);
        pfechas.put("2", this.fechaHasta);
        //buscando pre calculos
        progreso.setEstadoProgreso("Buscando pre cálculos");
        double taza = 0;
        if (codgn.size() > 0) {
            taza = 13.0 / codgn.size();
        }
        double tazaInicial = taza;
        for (int i = 0; i < codgn.size(); i++) {
            taza = i == 0 ? 0 : tazaInicial + taza;
            incrementoDelProgreso(taza);
            String fechainicio = "";
            String fechafin = "";
            Cacon008m preCalculoAbierto = cacon008mFacade.ultimoPreCalculoNominaAbierto(codModulo, codgn.get(i), pfechas);
            if (preCalculoAbierto != null) {
                if (preCalculoAbierto.getFedes().after(this.fechaDesde)) {
                    fechainicio = jbvarios.fechaToString(preCalculoAbierto.getFedes(), this.ftoFhJav);
                } else {
                    fechainicio = jbvarios.fechaToString(this.fechaDesde, this.ftoFhJav);
                }

                if (preCalculoAbierto.getFefin().before(this.fechaHasta)) {
                    fechafin = jbvarios.fechaToString(preCalculoAbierto.getFefin(), this.ftoFhJav);
                } else {
                    fechainicio = jbvarios.fechaToString(this.fechaHasta, this.ftoFhJav);
                }

                for (Ngbas001x _trab1 : _trab) {
                    if (_trab1.getCodgn() == preCalculoAbierto.getCodgn()) {
                        trabajadoresEnPrecalculo nuevo = new trabajadoresEnPrecalculo();
                        nuevo.setNombre(_trab1.toStringNombre());
                        nuevo.setDepartamento(_trab1.getNgbas009t().getNomdp());
                        nuevo.setGrupoNomina(_trab1.getNgnom001x().getDesgn());
                        nuevo.setFechaPreCalculo(fechainicio + " - " + fechafin);
                        enPreCalculo.add(nuevo);
                    }
                }
            } else {

                List<Object[]> trabajadores = cacon015dFacade.ultimoPreCalculoNominaAbiertoIndividual(codModulo, codgn.get(i), codpes, this.fechaDesde, this.fechaHasta);
                if (trabajadores != null) {
                    for (Ngbas001x _trab1 : _trab) {
                        for (Object[] empleado : trabajadores) {
                            if (_trab1.getCodpe() == Integer.parseInt(empleado[0] + "")) {
                                trabajadoresEnPrecalculo nuevo = new trabajadoresEnPrecalculo();
                                nuevo.setNombre(_trab1.toStringNombre());
                                nuevo.setDepartamento(_trab1.getNgbas009t().getNomdp());
                                nuevo.setGrupoNomina(_trab1.getNgnom001x().getDesgn());

                                if (((Date) empleado[1]).after(this.fechaDesde)) {
                                    fechainicio = jbvarios.fechaToString(((Date) empleado[1]), this.ftoFhJav);
                                } else {
                                    fechainicio = jbvarios.fechaToString(this.fechaDesde, this.ftoFhJav);
                                }

                                if (((Date) empleado[2]).before(this.fechaHasta)) {
                                    fechafin = jbvarios.fechaToString(((Date) empleado[2]), this.ftoFhJav);
                                } else {
                                    fechafin = jbvarios.fechaToString(this.fechaHasta, this.ftoFhJav);
                                }
                                nuevo.setFechaPreCalculo(fechainicio + " - " + fechafin);
                                enPreCalculo.add(nuevo);
                            }
                        }
                    }
                }
            }
        }
        progreso.setProgreso(19);
        progreso.setPorcentaje(progreso.getProgreso());

        ordenarEnPrecalculo(enPreCalculo);

        incidencias = new ArrayList<>();
            acum_Permiso_Remunerado_Actual = new HashMap<>();
            acum_Permiso_Remunerado_Anterior = new HashMap<>();

        calcular = new calculoIncidencias(codModulo, ejbFacade.getCodem(), ejbFacade.getCusua(), ejbFacade.getCodpe(), tgpar002dFacade, cacon011tFacade, caate001mFacade, ngbas001xFacade, funciones, capar001mFacade, catur004aFacade, casbt001mFacade, ngbas009tFacade, camot001mFacade);

        calcular.realizarCalculoDeIncidencias(_trabajadores, _trabajadoresAux, trabTerceros, userAdmin, progreso, incidencias, acum_Permiso_Remunerado_Actual, acum_Permiso_Remunerado_Anterior, filtroSelec, fechaDesde, fechaHasta);

        ordenar(incidencias);
        progreso.setProgreso(99);
        progreso.setPorcentaje(progreso.getProgreso());
        System.out.println("Ordenado.");
     
        progreso.setEstadoProgreso("Operación completada");
        Thread.sleep(1200);
        progreso.setProgreso(100);
        progreso.setPorcentaje(progreso.getProgreso());

        //Espera de 1.2 segundos para que se pueda cerrar la ventana y se cancele la barra de _progreso, se haga la redirección y no ocurran errores
        Thread.sleep(1200);

        ec.getRequestMap().put("incidencias", incidencias);
        ec.getRequestMap().put("progreso", progreso);
        ec.getRequestMap().put("remActual", acum_Permiso_Remunerado_Actual);
        ec.getRequestMap().put("remAnterior", acum_Permiso_Remunerado_Anterior);
        ec.getRequestMap().put("check", filtroSelec);
        ec.getRequestMap().put("fechaDesde", fechaDesde);
        ec.getRequestMap().put("fechaHasta", fechaHasta);
        ec.getRequestMap().put("criterio", criterio);
        ec.getRequestMap().put("consulta", consulta);
        ec.getRequestMap().put("enPreCalculo", enPreCalculo);
        ec.getRequestMap().put("calcular", calcular);
        progreso.setProgreso(0);

        return "Consulta.xhtml";
    }

    /**
     * Método que se encarga de actualizar los códigos de los trabajadores que un supervisor puede ver, ya sean sus subordinados o terceros, segun las
     * fechas dadas
     *
     * @param _ini Fecha Inicial de la autorización
     * @param _fin Fecha Final de la autorización
     * @throws Exception Problema al realizar consultas en métodos iternos.
     */
    public void actualizarCodpes(Date _ini, Date _fin) throws Exception {
        condicionJpql = "";

        if (_ini != null && _fin != null) {
            //validando que sean mayores o iguales la fecha final de la inicial
            condicionJpql = "t.frepe > FUNC('to_date','" + jbvarios.fechaToString(_ini, this.ftoFhJav) + "','" + this.ftoFhBD + "')";

            if (_fin.before(_ini)) {
                Utilidades.mostrarMensaje(
                        1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasDifFechas"));
            } else if (userSupervisor) {

                Map<Object, Object> parametros2 = new HashMap<Object, Object>();
                parametros2.put("1", _ini);
                parametros2.put("2", _fin);
                parametros2.put("3", ejbFacade.getCodem());

                coddpsSubordinados = departamentoDeSubordinados(ejbFacade.getCodpe(), codModulo, parametros2, true);
                List<Ngbas009t> Departamentos = ngbas009tFacade.cargarDepartamentos(coddpsSubordinados, codModulo);

                /**
                 * Se carga un listado de los coddps de los subordinados.
                 */
                depSubordinados = new ArrayList<Integer>();
                if (Departamentos != null) {
                    depSubordinados = new ArrayList<Integer>();
                    for (int i = 0; i < Departamentos.size(); i++) {
                        depSubordinados.add(Departamentos.get(i).getNgbas009tPK().getCoddp());
                    }
                }

                Map<Object, Object> _pinicial = new HashMap<Object, Object>();
                _pinicial.put("1", _ini);

                codgnStringSubordinados = ngbas001xFacade.cargarCodgnSubordinados(ejbFacade.getCodpe(), codModulo, _pinicial, true);
                codgnSubordinados = new ArrayList<Integer>();
                if (codgnStringSubordinados.compareTo("") != 0) {
                    String[] _codgn = codgnStringSubordinados.split(",");
                    for (int i = 0; i < _codgn.length; i++) {
                        codgnSubordinados.add(Integer.parseInt(_codgn[i]));
                    }
                }

                trabTerceros = new ArrayList<Integer>();
                codpesTerceros = "";

                //Carga de terceros por deducción.
                Ngbas001x trabajador = ngbas001xFacade.find(ejbFacade.getCodpe());
                List<Ngbas001x> autSubordinados = caate001mFacade.deduccionTerceros(codModulo, trabajador, parametros2, true);
                if (autSubordinados != null) {
                    for (int i = 0; i < autSubordinados.size(); i++) {
                        codpesTerceros += autSubordinados.get(i).getCodpe() + ",";
                        trabTerceros.add(autSubordinados.get(i).getCodpe());
                    }
                }

                Map<Object, Object> parametros = new HashMap<Object, Object>();
                parametros.put("1", ejbFacade.getCodpe());
                parametros.put("2", _ini);
                parametros.put("3", _fin);

                List<Caate001m> fechasTerceros = caate001mFacade.fechasTerceros(parametros, codModulo);

                if (!fechasTerceros.isEmpty()) {
                    for (int i = 0; i < fechasTerceros.size(); i++) {
                        List<Ngbas001x> prepareTargetTerceros = caate001mFacade.prepareTargetTerceros(fechasTerceros.get(i).getCaate001mPK().getCodat(), codModulo);
                        if (prepareTargetTerceros != null) {
                            for (int j = 0; j < prepareTargetTerceros.size(); j++) {
                                if (prepareTargetTerceros.get(j).getFrepe() != null) { //Si tiene fecha de retiro...
                                    if (!prepareTargetTerceros.get(j).getFrepe().before(_ini)) { //Si la fecha de retiro está despues o es igual al día inicial, agrego a ese trabajador al listado
                                        codpesTerceros += String.valueOf(prepareTargetTerceros.get(j).getCodpe()) + ",";
                                        trabTerceros.add(prepareTargetTerceros.get(j).getCodpe());
                                    }
                                } else {
                                    codpesTerceros += String.valueOf(prepareTargetTerceros.get(j).getCodpe()) + ",";
                                    trabTerceros.add(prepareTargetTerceros.get(j).getCodpe());
                                }

                            }
                        }
                    }
                }

                depTerceros = new ArrayList<Integer>();
                codgnTerceros = new ArrayList<Integer>();

                coddpsTerceros = "";
                codgnStringTerceros = "";

                if (codpesTerceros.compareTo("") != 0) {
                    codpesTerceros = codpesTerceros.substring(0, codpesTerceros.length() - 1);
                    coddpsTerceros = departamentoDeTerceros(codpesTerceros, codModulo, _pinicial, true);
                    List<Ngbas009t> departamentos = ngbas009tFacade.cargarDepartamentos(coddpsTerceros, codModulo);
                    if (!departamentos.isEmpty()) {
                        for (int i = 0; i < departamentos.size(); i++) {
                            if (!depTerceros.contains(departamentos.get(i).getNgbas009tPK().getCoddp())) {
                                depTerceros.add(departamentos.get(i).getNgbas009tPK().getCoddp());
                            }
                        }
                    }

                    codgnStringTerceros = ngbas001xFacade.cargarCodgnTerceros(codpesTerceros, codModulo, _pinicial, true);
                    if (codgnStringTerceros.compareTo("") != 0) {
                        String[] _codgn = codgnStringTerceros.split(",");
                        for (int i = 0; i < _codgn.length; i++) {
                            codgnTerceros.add(Integer.parseInt(_codgn[i]));
                        }
                    }
                }

                if (coddpsTerceros.compareTo("") == 0) {
                    coddpsTerceros = "-1";
                }
                if (coddpsSubordinados.compareTo("") == 0) {
                    coddpsSubordinados = "-1";
                }
                if (codpesTerceros.compareTo("") == 0) {
                    codpesTerceros = "-1";
                }
                if (codgnStringTerceros.compareTo("") == 0) {
                    codgnStringTerceros = "-1";
                }
                if (codgnStringSubordinados.compareTo("") == 0) {
                    codgnStringSubordinados = "-1";
                }

                System.out.println("Terceros: " + codpesTerceros);
                System.out.println("Departamentos Sub: " + coddpsSubordinados);
                System.out.println("Departamentos Terceros: " + coddpsTerceros);
                System.out.println("Grupo Nomina Sub: " + codgnStringSubordinados);
                System.out.println("Grupo Nomina Terceros: " + codgnStringTerceros);

                /**
                 * Valido si cambiaron las condiciones de los grupos de nómina, departamentos o personas individuales para en el caso de terceros Por
                 * ejemplo si ya no aplica "juan" para ese nuevo periodo de consulta segun cambio de fechas, se saque de la lista de seleccionados, solo
                 * ocurrirá en caso de terceros.
                 */
                //Reviso los grupos de nómina.
                for (int i = 0; i < gruposNomina.size(); i++) {
                    if (!codgnTerceros.contains(gruposNomina.get(i).getNgnom001xPK().getCodgn()) && !codgnSubordinados.contains(gruposNomina.get(i).getNgnom001xPK().getCodgn())) {
                        gruposNomina.remove(i);
                    }
                }

                //Reviso los departamentos
                for (int i = 0; i < listaDepartamentos.size(); i++) {
                    if (!depTerceros.contains(listaDepartamentos.get(i).getNgbas009tPK().getCoddp()) && !depSubordinados.contains(listaDepartamentos.get(i).getNgbas009tPK().getCoddp())) {
                        listaDepartamentos.remove(i);
                    }
                }

                //Reviso Personas
                String[] terceros = codpesTerceros.split(",");
                for (int i = 0; i < listaPersonas.size(); i++) {
                    //Son terceros.
                    if (listaPersonas.get(i).getSuppe() != ejbFacade.getCodpe()) {
                        boolean existeTercero = false;
                        for (int j = 0; j < terceros.length; j++) {
                            //Se hace un recorrido por todos los codpes de terceros que se tienen, si no aparece, se remueve de la lista
                            //Debido a que por el nuevo cambio de las fechas, este tercero ya no puede ser visualizado por el supervisor.
                            if (Integer.parseInt(terceros[j]) == listaPersonas.get(i).getCodpe()) {
                                existeTercero = true;
                            }
                        }
                        if (!existeTercero) {
                            listaPersonas.remove(i);
                        }
                    }
                }
            }
        }

        System.out.println("JPQL: " + condicionJpql);
    }

    /**
     * Método para determinar los diferentes departamentos a los que pertenecen los subordinados de un trabajador.
     *
     * @param _codpe Código del supervisor
     * @param _codModulo Código del módulo
     * @param _parametros 1. Fecha Inicial 2.Fecha Final 3.Código de la empresa.
     * @param _retirados True: Si quiere consultar los departamentos de todos aquellos subordinados con condición "RETIRADO" pero que su fecha de retiro
     * este comprendida entre las fechas que han sido enviadas por parámetros
     */
    private String departamentoDeSubordinados(int _codpe, String _codModulo, Map<Object, Object> _parametros, boolean _retirados) throws Exception {
        return ngbas001xFacade.departamentoDeSubordinados(_codpe, _codModulo, _parametros, _retirados);
    }

    /**
     * Método para cargar los departamentos de los terceros.
     *
     * @param _codpes códigos de los terceros separados por , "123,123,123".
     * @param _codModulo código del módulo
     * @param _parametros 1. Fecha Inicial
     * @param _retirados True: Si quiere consultar los departamentos de todos aquellos subordinados con condición "RETIRADO" pero que su fecha de retiro
     * este comprendida entre las fechas que han sido enviadas por parámetros
     * @return String con los códigos de los departamentos asociados a sus terceros, separados por ","
     */
    private String departamentoDeTerceros(String _codpes, String _codModulo, Map<Object, Object> _parametros, boolean _retirado) throws Exception {
        return ngbas001xFacade.departamentoDeTerceros(_codpes, _codModulo, _parametros, _retirado);
    }

    /**
     * Método para realizar una Descripción más completa, del tipo de consulta que el supervisor quiere realizar
     *
     * @param _consulta String Consulta Seleccionada
     * @return Strign con la Descripcion.
     */
    public String descripcionConsulta(String _consulta) {
        String retorno = "";

        if (_consulta.compareTo("Todos") == 0) {
            retorno = "Todo el personal";
        } else if (_consulta.compareTo("Departamento") == 0) {
            retorno = "Por Departamento(s)";
        } else if (_consulta.compareTo("Individual") == 0) {
            retorno = "Individual(es)";
        } else if (_consulta.compareTo("Nomina") == 0) {
            retorno = "Por Grupo(s) De Nómina";
        }

        return retorno;
    }

    /**
     * Método que retorna si se están consultando Ausencias, Sobretiempos, o ambos.
     *
     * @return String
     */
    public String incidenciasSelected() {
        String retorno = "";
        if (filtroSelec.length > 1) {
            retorno = "Ausencias y Sobretiempo";
        } else if (filtroSelec[0].compareTo("Ausencias") == 0) {
            retorno = "Ausencias";
        } else {
            retorno = "Sobretiempo";
        }
        return retorno;
    }

    /**
     * Método para cargar los parámetros enviados desde la página Filtro, y cargar la información según esos mismos parámetros enviados.
     *
     * @return String página a cargar
     * @throws Exception Problema al consultar la BD.
     */
    public String cargarParametrosConsulta() throws Exception {

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

        //Valores iniciales de tipos, motivos y actividades
        tiposSobretiempo = casbt002dFacade.findAll(codModulo);

        String consultar = "tipo_ausencia%";
        List<Ngnom018t> tipo = ngnom018tFacade.consultaParametrosAusencias(ejbFacade.getCodem(), consultar, codModulo);
        if (tipo != null) {
            for (int i = 0; i < tipo.size(); i++) {
                tipos_Remunerados += tipo.get(i).getValpg() + ",";
            }
        }

        consultar = "motivo_ausencia%";
        List<Ngnom018t> motivos = ngnom018tFacade.consultaParametrosAusencias(ejbFacade.getCodem(), consultar, codModulo);
        if (motivos != null) {
            for (int i = 0; i < motivos.size(); i++) {
                motivos_Remunerados += motivos.get(i).getValpg() + ",";
            }
        }

        //Datos para mostrar en sobretiempo
        Map<Object, Object> pMotivo = new HashMap<Object, Object>();
        pMotivo.put("1", ejbFacade.getCodem());
        pMotivo.put("2", 'M');
        motivosSobretiempo = camot001mFacade.buscarListaMotAct(codModulo, pMotivo);

        Map<Object, Object> pActividad = new HashMap<Object, Object>();
        pActividad.put("1", ejbFacade.getCodem());
        pActividad.put("2", 'A');
        actividadSobretiempo = camot001mFacade.buscarListaMotAct(codModulo, pActividad);

        userAdmin = tgpar002dFacade.perfilAdmin();
        if (!userAdmin) {
            userSupervisor = tgpar002dFacade.perfilSupervisor();
        }

        System.out.println("ADMINISTRADOR: " + userAdmin);
        System.out.println("SUPERVISOR: " + userSupervisor);

        autSobretiempo = (String) ec.getRequestMap().get("autSobretiempo"); //Este valor es "S", si proviene del módulo de Autorizar modo de marcaje / sobretiempo
    
        incidencias = (List<IncNoAutorizadas>) ec.getRequestMap().get("incidencias");
        acum_Permiso_Remunerado_Actual = (Map<Integer, String>) ec.getRequestMap().get("remActual");
        acum_Permiso_Remunerado_Anterior = (Map<Integer, String>) ec.getRequestMap().get("remAnterior");
        filtroSelec = (String[]) ec.getRequestMap().get("check");
        fechaDesde = (Date) ec.getRequestMap().get("fechaDesde");
        fechaHasta = (Date) ec.getRequestMap().get("fechaHasta");
        criterio = (int) ec.getRequestMap().get("criterio");
        consulta = (String) ec.getRequestMap().get("consulta");
        progreso =  (Progreso) ec.getRequestMap().get("progreso");
        calcular =   (calculoIncidencias) ec.getRequestMap().get("calcular");
        enPreCalculo = (List<trabajadoresEnPrecalculo>) ec.getRequestMap().get("enPreCalculo");
        listaPersonas = (List<Ngbas001x>) ec.getRequestMap().get("trabajadores");

        return "Consulta.xhtml";
    }

    /**
     * Método para la descripcion de la incidencia (Ausencia,Sobretiempo).
     *
     * @param _incidencia false-> Ausencia true->Sobretiempo
     * @return Descripcion de la incidencia
     */
    public String descripcionIncidencia(boolean _incidencia) {
        String retorno = "";
        if (_incidencia) {
            retorno = "Sobretiempo";
        } else {
            retorno = "Ausencia";
        }

        return retorno;
    }

    /**
     * Método que ordena el listado de de las incidencias de los trabajadores. 1-> Departamento 2->Nombre 3->Incidencia 4->Fecha Inicial
     *
     * @param incidencias Lis<<IncNoAutorizadas> con el listado de incidencias ordenadas previamente.
     */
    private void ordenar(List<IncNoAutorizadas> incidencia) {
        Comparator<IncNoAutorizadas> comparador = new Comparator<IncNoAutorizadas>() {
            @Override
            public int compare(IncNoAutorizadas a, IncNoAutorizadas b) {
                int resultado = a.getNomdp().compareTo(b.getNomdp());
                if (resultado != 0) {
                    return resultado;
                }

                resultado = a.getNombre().compareTo(b.getNombre());
                if (resultado != 0) {
                    return resultado;
                }

                resultado = descripcionIncidencia(a.isIncidencia()).compareTo(descripcionIncidencia(b.isIncidencia()));
                if (resultado != 0) {
                    return resultado;
                }

                resultado = a.getFhini().after(b.getFhini()) ? 1 : -1;
                return resultado;
            }
        };

        if (!incidencia.isEmpty()) {
            Collections.sort(incidencia, comparador);
        }
    }

    /**
     * Método para ordenar el listado de trabajadores que se encuentran en pre cálculo, por nombre y departamento.
     *
     * @param _enPreCalculo Listado a ordenar.
     */
    private void ordenarEnPrecalculo(List<trabajadoresEnPrecalculo> _enPreCalculo) {
        Comparator<trabajadoresEnPrecalculo> comparador = new Comparator<trabajadoresEnPrecalculo>() {
            @Override
            public int compare(trabajadoresEnPrecalculo a, trabajadoresEnPrecalculo b) {
                int resultado = a.getNombre().compareTo(b.getNombre());
                if (resultado != 0) {
                    return resultado;
                }

                resultado = a.getDepartamento().compareTo(b.getDepartamento());
                return resultado;
            }
        };

        if (!_enPreCalculo.isEmpty()) {
            Collections.sort(_enPreCalculo, comparador);
        }
    }

    /**
     * Método para conocer la descripción del tipo de la incidencia (Sobretiempo diurno, nocturno, inasistencia o permiso)
     *
     * @param _tipoIncidencia String corto con el tipo de la incidencia
     * @return String con la descripción del tipo de la incidencia
     */
    public String descripcionTipo(String _tipoIncidencia) {
        String retorno = "";

        if (_tipoIncidencia.compareTo("STD") == 0) {
            retorno = "Sobretiempo Diurno";
        } else if (_tipoIncidencia.compareTo("STN") == 0) {
            retorno = "Sobretiempo Nocturno";
        } else if (_tipoIncidencia.compareTo("AUD") == 0) {
            retorno = "Ausencia Diurna";
        } else if (_tipoIncidencia.compareTo("AUN") == 0) {
            retorno = "Ausencia Nocturna";
        }

        return retorno;
    }

    /**
     * Método que cambia el estado de la incidencia que fue editada, estatus=true para luego ser guardadas en la base de datos, todas aquellas que se
     * encuentren con este estado
     *
     */
    public void guardarEdicion() {

        int indice = 0;
        //Se buscará el item en el listado, para llevar a cabo en él, los cambios realizados en el p:dialog.        
        for (int i = 0; i < incidencias.size(); i++) {
            if (incidencias.get(i).getSerial() == selIncidencia.getSerial()) {
                indice = i;
                //El campo se marca como editado.
                incidencias.get(i).setEstatus(true);
                if (incidencias.get(i).isIncidencia()) {//Es un sobretiempo, setéo los campos que surgieron cambios.
                    incidencias.get(i).setTipoSobretiempo(selIncidencia.getTipoSobretiempo());
                    incidencias.get(i).setMotivoSobretiempo(selIncidencia.getMotivoSobretiempo());
                    incidencias.get(i).setActividadSobretiempo(selIncidencia.getActividadSobretiempo());
                    incidencias.get(i).setObservacion(selIncidencia.getObservacion());
                    i = incidencias.size();
                } else {//Es una ausencia, setéo los campos que surgieron cambios.
                    incidencias.get(i).setTipoAusencia(selIncidencia.getTipoAusencia());
                    incidencias.get(i).setMotivoAusencia(selIncidencia.getMotivoAusencia());
                    incidencias.get(i).setObservacion(selIncidencia.getObservacion());
                    i = incidencias.size();
                }
            }
        }

        consultarAusenciasRemuneradas(selIncidencia.getTipoAusencia(), selIncidencia.getMotivoAusencia(), editadaAnterior);

        //Obtengo solo el año de la incidencia y el actual
        boolean mismoanio = anioActual(jbvarios.fechaToString(incidencias.get(indice).getFeper(), this.ftoFhJav).split("/")[2], ejbFacade.getCurrentDate().split("/")[2]);
        if (mismoanio) { //Es el mismo año
            acum_Permiso_Remunerado_Actual.put(selIncidencia.getCodpe(), horas_acumuladas);
        } else {    //Es el año anterior            
            acum_Permiso_Remunerado_Anterior.put(selIncidencia.getCodpe(), horas_acumuladas);
        }

        selIncidencia = new IncNoAutorizadas();

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('Dialog').hide();");

        Utilidades.mostrarMensaje(
                0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModal_PendienteImprimir"));
    }

    /**
     * Método para actualizar los motivos del permiso según los tipos elegidos.
     *
     * @param _tipoSelected Tipo Seleccionado
     * @param _motivoSelected Motivo seleccionado
     * @param _raiz True si viene de Tipo, False si viene de motivo..
     * @throws Exception Problema al consultar la BD
     */
    public void actualizarMotivos(Ngvar005t _tipoSelected, Ngvar001t _motivoSelected, boolean _raiz) throws Exception {
        //Filtro para mostrar motivos.
        String motrarMotivos = ngvar007Facade.motrarMotivos(_tipoSelected.getNgvar005tPK().getTippm(), codModulo);

        //Motivos segun filtro
        if (motrarMotivos.compareTo("") != 0) {
            motivosAusencia = ngvar001tFacade.cargarMotivos(motrarMotivos, codModulo);
            if (_raiz) {
                _motivoSelected = motivosAusencia.get(0);
            }
        } else {
            motivosAusencia = new ArrayList<Ngvar001t>();
            _motivoSelected = null;
        }

        //Consultar si el motivo y tipo seleccionado se encuentra dentro de los remunerados, para sumar horas a las acumuladas hasta el momento
        consultarAusenciasRemuneradas(_tipoSelected, _motivoSelected, editadaAnterior);

    }

    /**
     * Método para cargar las ausencias en horas, que serán unidas en un mismo registro en días.
     *
     * @param _item Incidencia Seleccionada.
     */
    public void cargarAusencias(IncNoAutorizadas _item) {
        incEnRango = new ArrayList<IncNoAutorizadas>();
        rango = evaluarAusenciasContinuas(_item);

        RequestContext confirm = RequestContext.getCurrentInstance();
        confirm.execute("PF('convDia').show();");
    }

    /**
     * Método que remueve las ausencias en horas involucradas, y agrega una nueva ausencia en días.
     */
    public void unirAusencia() {

        boolean continuar = true;

        if (minSelected == null || maxSelected == null) {
            Utilidades.mostrarMensaje(
                    2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModalUnionFechasRQ"));
            continuar = false;
        } else if (minSelected.after(maxSelected)) {
            Utilidades.mostrarMensaje(
                    2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModalUnionFeiniMenor"));
            continuar = false;
        } else if (minSelected.after(maxInicial) || minSelected.before(minInicial) || maxSelected.after(maxInicial) || maxSelected.before(minInicial)) {
            Utilidades.mostrarMensaje(
                    2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModalUnionEntreRango")
                    .replace("{0}", jbvarios.fechaToString(minInicial, this.ftoFhJav))
                    .replace("{1}", jbvarios.fechaToString(maxInicial, this.ftoFhJav)));
            continuar = false;
        }

        if (continuar) {
            int inferior = 0;
            int superior = 0;

            inferior = incEnRango.indexOf(rangoPrimerInc);
            superior = incEnRango.indexOf(rangoUltimaInc);

            System.out.println("Incidencias a Eliminar: ");

            while (inferior <= superior) {
                System.out.println("Dia: " + jbvarios.fechaToString(incEnRango.get(inferior).getFhini(), this.ftoFhJav));

                /**
                 * Al hacer una unión de ausencias en días, es posible que estas ya hayan sido editadas, al hacer la unión esta edición se pierde, y en esa
                 * edición pudo haberse sumado el total de las horas acumuladas remuneradas por lo tanto al hacer la unión se debe comprorbar si en esas
                 * incidencias a unir habían sido editadas y eran de tipo remuneradas para restar del acumulado la duración de la incidencia.
                 */
                if (incEnRango.get(inferior).isEstatus()) {
                    //Esta incidencia había sido editada se revisa si cumplia con tipo y motivo remunerado.
                    boolean tipoRemunerado = tipos_Remunerados.contains(incEnRango.get(inferior).getTipoAusencia().getNgvar005tPK().getTippm());
                    boolean motivoRemunerado = motivos_Remunerados.contains(String.valueOf(incEnRango.get(inferior).getMotivoAusencia().getNgvar001tPK().getCodmp()));

                    if (tipoRemunerado && motivoRemunerado) {
                        //Si tipo y motivo eran remunerados tengo que restar lo previamente sumado al acumulador (la duración de esa incidencia)
                        boolean mismoanio = anioActual(jbvarios.fechaToString(incEnRango.get(inferior).getFeper(), this.ftoFhJav).split("/")[2], ejbFacade.getCurrentDate().split("/")[2]);
                        String nuevo_acumulado = "";

                        if (mismoanio) { //Es el mismo año
                            nuevo_acumulado = restarDuracion(incEnRango.get(inferior).getDuracion(), acum_Permiso_Remunerado_Actual.get(incEnRango.get(inferior).getCodpe()), incEnRango.get(inferior));
                            acum_Permiso_Remunerado_Actual.put(incEnRango.get(inferior).getCodpe(), nuevo_acumulado);
                        } else {    //Es el año anterior
                            nuevo_acumulado = restarDuracion(incEnRango.get(inferior).getDuracion(), acum_Permiso_Remunerado_Anterior.get(incEnRango.get(inferior).getCodpe()), incEnRango.get(inferior));
                            acum_Permiso_Remunerado_Anterior.put(incEnRango.get(inferior).getCodpe(), nuevo_acumulado);
                        }
                    }
                }
                incidencias.remove(incEnRango.get(inferior));
                if (filteredIncidencias != null) {
                    filteredIncidencias.remove(incEnRango.get(inferior));
                }
                inferior++;
            }

            IncNoAutorizadas nuevo;
            nuevo = rangoPrimerInc;
            nuevo.setEstatus(false);
            nuevo.setFhini(rangoPrimerInc.getFhini());
            nuevo.setFhfin(rangoUltimaInc.getFhfin());
            nuevo.setDuracion(incDuracion);
            /**
             * Si la ausencia es unida a 1 día, se sigue mostrando los mensaje de marcaje de movilidad y de marcaje en reloj, de ser de más de 1 día, el
             * mensaje que muestra es "Registro de Movilidad: Ausencia en días" y "Marcaje en reloj: Ausencia en días".
             */
            if (incDuracion.contains("1")) {
                nuevo.setMarcajeMovilidad(rangoPrimerInc.getMarcajeMovilidad());
                nuevo.setMarcajeReloj(rangoPrimerInc.getMarcajeReloj());
            } else {
                nuevo.setMarcajeMovilidad("Ausencia en días");
                nuevo.setMarcajeReloj("Ausencia en días");
            }
            nuevo.setTipoAusencia(null);
            nuevo.setMotivoAusencia(null);
            nuevo.setObservacion(null);
            nuevo.setUnirDia(0);

            incidencias.add(nuevo);
            if (filteredIncidencias != null) {
                filteredIncidencias.add(nuevo);
            }

            //Ordeno de nuevo
            ordenar(incidencias);
            if (filteredIncidencias != null) {
                ordenar(filteredIncidencias);
            }

            Utilidades.mostrarMensaje(
                    0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModalUnionExitoso"));

            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('convDia').hide();");
        }
    }

    /**
     * Método que carga todas las ausencias consecutivas que pueden ser Unidas en una sola Incidencia.
     *
     * @param _item Incidencia Seleccionada
     * @return True: Si la incidencia a Unir es de más de 1 día.
     */
    private boolean evaluarAusenciasContinuas(IncNoAutorizadas _item) {

        incEnRango.add(_item);
        rangoPrimerInc = _item;
        rangoUltimaInc = _item;

        boolean retorno = false;
        int indice = incidencias.indexOf(_item);
        System.out.println("Indice: " + indice);
        boolean continuar = true;
        //Busco hacia atras        
        while (indice >= 0 && continuar) {
            if (indice - 1 >= 0) {
                //Calculo que la incidencia anterior haya ocurrido el día anterior a la incidencia actual.
                boolean dif = false;
                int FechaDif = jbvarios.FechaDif(jbvarios.fechaToString(incidencias.get(indice).getFeper(), this.ftoFhJav), jbvarios.fechaToString(incidencias.get(indice - 1).getFeper(), this.ftoFhJav), this.ftoFhJav);
                if (FechaDif == 1 || FechaDif == 0) {
                    dif = true; //Es el día consecutivo o el mismo día
                }
                if (incidencias.get(indice).getCodpe() == incidencias.get(indice - 1).getCodpe() //Mistro Trabajador
                        && incidencias.get(indice - 1).getUnirDia() != 0 //Esta incidencia se puede unir con otra.
                        && dif //Incidencia consecutiva en cuanto a fechas
                        //                        && incidencias.get(indice).getTipoIncidencia().compareTo(incidencias.get(indice - 1).getTipoIncidencia()) == 0 //Que sean del mismo tipo de ausencia
                        && incidencias.get(indice - 1).isIncidencia() == false) { //Es una ausencia;
                    rangoPrimerInc = incidencias.get(indice - 1);
                    if (!incEnRango.contains(incidencias.get(indice - 1))) {
                        incEnRango.add(incidencias.get(indice - 1)); //Agrego al listado de incidencias que pudieran ser unidas.
                    }
                    indice = indice - 1;
                } else {
                    continuar = false;
                }
            } else {
                continuar = false;
            }
        }

        continuar = true;
        //Busco hacia adelante        
        while (indice < incidencias.size() && continuar) {

            if (indice + 1 < incidencias.size()) {
                //Calculo que la incidencia siguiente haya ocurrido el día siguiente a la incidencia actual.
                boolean dif = false;
                int FechaDif = jbvarios.FechaDif(jbvarios.fechaToString(incidencias.get(indice + 1).getFeper(), this.ftoFhJav), jbvarios.fechaToString(incidencias.get(indice).getFeper(), this.ftoFhJav), this.ftoFhJav);
                if (FechaDif == 1 || FechaDif == 0) {
                    dif = true; //Es el día consecutivo o el mismo día
                }

                if (incidencias.get(indice).getCodpe() == incidencias.get(indice + 1).getCodpe() //Mistro Trabajador
                        && incidencias.get(indice + 1).getUnirDia() != 0 //Esta incidencia se puede unir con otra.
                        && dif //Incidencia consecutiva en cuanto a fechas
                        //                        && incidencias.get(indice).getTipoIncidencia().compareTo(incidencias.get(indice + 1).getTipoIncidencia()) == 0 //Que sean del mismo tipo de ausencia
                        && incidencias.get(indice + 1).isIncidencia() == false) { //Es una ausencia;
                    rangoUltimaInc = incidencias.get(indice + 1);
                    if (!incEnRango.contains(incidencias.get(indice + 1))) {
                        incEnRango.add(incidencias.get(indice + 1));//Agrego al listado de incidencias que pudieran ser unidas.
                    }
                    indice = indice + 1;
                } else {
                    continuar = false;
                }
            } else {
                continuar = false;
            }
        }

        int dif = jbvarios.FechaDif(jbvarios.fechaToString(rangoUltimaInc.getFeper(), this.ftoFhJav), jbvarios.fechaToString(rangoPrimerInc.getFeper(), this.ftoFhJav), this.ftoFhJav);
        dif = dif + 1;

        if (dif > 1) {
            retorno = true;
        }
        minInicial = rangoPrimerInc.getFeper();
        maxInicial = rangoUltimaInc.getFeper();
        minSelected = rangoPrimerInc.getFeper();
        maxSelected = rangoUltimaInc.getFeper();

        ordenar(incEnRango);

        actualizarIncidencias();

        return retorno;
    }

    /**
     * Método para actualizar las incidencias que se muestran en el modal, dependiendo de las fechas seleccionadas en el mismo, cargando cómo quedaría la
     * incidencia si se desea unir de esa manera
     */
    public void actualizarIncidencias() {
        mostrar = true;
        boolean continuar = true;

        if (minSelected == null || maxSelected == null) {
            Utilidades.mostrarMensaje(
                    2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModalUnionFechasRQ"));
            continuar = false;
            mostrar = false;
        } else if (minSelected.after(maxSelected)) {
            Utilidades.mostrarMensaje(
                    2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModalUnionFeiniMenor"));
            continuar = false;
            mostrar = false;
        } else if (minSelected.after(maxInicial) || minSelected.before(minInicial) || maxSelected.after(maxInicial) || maxSelected.before(minInicial)) {
            Utilidades.mostrarMensaje(
                    2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModalUnionEntreRango")
                    .replace("{0}", jbvarios.fechaToString(minInicial, this.ftoFhJav))
                    .replace("{1}", jbvarios.fechaToString(maxInicial, this.ftoFhJav)));
            continuar = false;
            mostrar = false;
        }

        if (continuar) {
            for (int i = 0; i < incEnRango.size(); i++) {
                if (incEnRango.get(i).getFeper().equals(minSelected) && (incEnRango.get(i).getUnirDia() == 1 || incEnRango.get(i).getUnirDia() == 3)) { //Cargando el límite inicial de la ausencia a unir
                    rangoPrimerInc = incEnRango.get(i);
                }
                if (incEnRango.get(i).getFeper().equals(maxSelected) && (incEnRango.get(i).getUnirDia() == 2 || incEnRango.get(i).getUnirDia() == 4)) { //Cargando el límite final de la ausencia a unir
                    rangoUltimaInc = incEnRango.get(i);
                }
            }
            int dif = jbvarios.FechaDif(jbvarios.fechaToString(rangoUltimaInc.getFeper(), this.ftoFhJav), jbvarios.fechaToString(rangoPrimerInc.getFeper(), this.ftoFhJav), this.ftoFhJav) + 1;
            incDuracion = dif > 1 ? dif + " Días" : dif + " Día";
        }
    }

    /**
     * Método para revertir las ediciones hechas sobre una incidencia, que se haya editado pero que no quiera procesarse (imprimirse), se deshace y no
     * surgiran cambios en la base de datos.
     *
     * @param _item Incidencia a deshacer.
     */
    public void revertirEdicion(IncNoAutorizadas _item) throws Exception {
        boolean remunerada = false;
        //Ya no aparce como editada.
        _item.setEstatus(false);

        if (!_item.isIncidencia()) { //Si es una ausencia se debe revisar si sumaba en el acumulado anual para restarlo.

            //Obtengo solo el año de la incidencia y el actual
            boolean mismoanio = anioActual(jbvarios.fechaToString(_item.getFeper(), this.ftoFhJav).split("/")[2], ejbFacade.getCurrentDate().split("/")[2]);

            String acumulado = "";
            if (mismoanio) {
                acumulado = acum_Permiso_Remunerado_Actual.get(_item.getCodpe());
            } else {
                acumulado = acum_Permiso_Remunerado_Anterior.get(_item.getCodpe());
            }

            //Se comprueba si la ausencia aplicaba para remunerada
            remunerada = aplicaRemunerada(_item.getTipoAusencia(), _item.getMotivoAusencia());

            //Si aplicaba para remunerada es necesario descontar las horas acumuladas remuneradas
            if (remunerada) {
                acumulado = restarDuracion(_item.getDuracion(), acumulado, _item);
            }

            if (mismoanio) { //Es el mismo año
                acum_Permiso_Remunerado_Actual.put(_item.getCodpe(), acumulado);
            } else {    //Es el año anterior
                acum_Permiso_Remunerado_Anterior.put(_item.getCodpe(), acumulado);
            }
        }

        //Datos que se cargaran posteriormente en la edición dependiendo del tipo de la incidencia.
        _item.setObservacion(null);
        Ngbas009t dep = ngbas009tFacade.buscarDepartamento(_item.getCoddp(), codModulo);
        if (dep.getCodmo() != null) {
            Camot001m motDefecto = camot001mFacade.buscarObjMotivo(codModulo, dep.getCodmo());
            _item.setMotivoSobretiempo(motDefecto);
        } else {
            _item.setMotivoSobretiempo(null);
        }
        _item.setTipoSobretiempo(null);
        _item.setActividadSobretiempo(null);

        _item.setTipoAusencia(null);
        _item.setMotivoAusencia(null);

        Utilidades.mostrarMensaje(
                0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasRevertir"));
    }

    /**
     * Método que carga la incidencia a editar, si es un sobretiempo solo la carga, si es una ausencia, dependerá de si es diurna o nocturna para mostrar
     * los tipos o motivos a escoger
     *
     * @param _item
     * @throws java.lang.Exception
     */
    public void editarAusencia(IncNoAutorizadas _item) throws Exception {

        selIncidencia = new IncNoAutorizadas();
        editadaAnterior = new IncNoAutorizadas();
        horas_acumuladas = "";

        if (_item.isIncidencia()) { //Sobretiempo
            setearIncidenciaItem(selIncidencia, _item);
        } else {
            setearIncidenciaItem(selIncidencia, _item);

            if (selIncidencia.isEstatus()) {
                editadaAnterior.setCodpe(selIncidencia.getCodpe());
                editadaAnterior.setTipoAusencia(selIncidencia.getTipoAusencia());
                editadaAnterior.setMotivoAusencia(selIncidencia.getMotivoAusencia());
                editadaAnterior.setCoddp(selIncidencia.getCoddp());
                editadaAnterior.setDuracion(selIncidencia.getDuracion());
            } else {
                editadaAnterior = new IncNoAutorizadas();
            }

            boolean dias = false;
            boolean diurno = false;

            if (_item.getDuracion().contains("Día")) {//La ausencia es en días.
                dias = true;
            } else { //La ausencia es en horas.
                dias = false;
                diurno = _item.getTipoIncidencia().compareTo("AUD") == 0; //AUD (Ausencia Diurna) de lo contrario false..
            }
            //Datos para mostrar en las ausencias       
            tiposAusencia = new ArrayList<>();
            tiposAusencia = ngvar005tFacade.tiposAusencias(codModulo, dias, diurno);
            motivosAusencia = new ArrayList<>();
            if (selIncidencia.isEstatus()) {
                //Si la ausencia a sido editada, se precarga lo que ya había sido editado.
                //Filtro para mostrar motivos.
                String motrarMotivos = ngvar007Facade.motrarMotivos(selIncidencia.getTipoAusencia().getNgvar005tPK().getTippm(), codModulo);
                //Motivos segun filtro
                motivosAusencia = ngvar001tFacade.cargarMotivos(motrarMotivos, codModulo);
            } else {
                //Filtro para mostrar motivos.
                selIncidencia.setTipoAusencia(tiposAusencia.get(0));
                String motrarMotivos = ngvar007Facade.motrarMotivos(tiposAusencia.get(0).getNgvar005tPK().getTippm(), codModulo);
                //Motivos segun filtro
                if (motrarMotivos.compareTo("") != 0) {
                    motivosAusencia = ngvar001tFacade.cargarMotivos(motrarMotivos, codModulo);
                    selIncidencia.setMotivoAusencia(motivosAusencia.get(0));
                } else {
                    motivosAusencia = new ArrayList<Ngvar001t>();
                    selIncidencia.setMotivoAusencia(null);
                }
            }

//            Se quitó para que al abrir el dialogo no se evaluen aún si ambas son remuneradas.
//            consultarAusenciasRemuneradas(selIncidencia.getTipoAusencia(), selIncidencia.getMotivoAusencia(), editadaAnterior);
        }

        //Obtengo solo el año de la incidencia y el actual
        boolean mismoanio = anioActual(jbvarios.fechaToString(selIncidencia.getFeper(), this.ftoFhJav).split("/")[2], ejbFacade.getCurrentDate().split("/")[2]);

        if (mismoanio) { //Es el mismo año
            etiqueta_Acum = ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModal_Au_Acumulado").replace("{0}", ejbFacade.getCurrentDate().split("/")[2]);
            if (horas_acumuladas.compareTo("") == 0) {
                horas_acumuladas = acum_Permiso_Remunerado_Actual.get(selIncidencia.getCodpe());
            }
        } else {    //Es el año anterior
            etiqueta_Acum = ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasModal_Au_Acumulado").replace("{0}", jbvarios.fechaToString(selIncidencia.getFeper(), this.ftoFhJav).split("/")[2]);
            if (horas_acumuladas.compareTo("") == 0) {
                horas_acumuladas = acum_Permiso_Remunerado_Anterior.get(selIncidencia.getCodpe());
            }
        }

        RequestContext confirm = RequestContext.getCurrentInstance();
        confirm.execute("PF('Dialog').show();");
    }

    /**
     * Método que guardara los cambios que eran hasta ahora parciales, y los guardará en la base de datos, luego imprime esas planillas con su nuevo código
     * de barras
     *
     * @throws Exception problemas al hacer consultas en sus métodos internos
     */
    public void procesarImprimir() throws Exception {

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.getRequestMap().put("fedsd", ejbFacade.getCurrentDateTime());
        ec.getRequestMap().put("fehst", ejbFacade.getCurrentDateTime());
        ec.getRequestMap().put("consulta", consulta);
        ec.getRequestMap().put("check", filtroSelec);
        ec.getRequestMap().put("autSobretiempo", autSobretiempo);
        ec.getRequestMap().put("idCalculo", idCalculo);
        ec.getRequestMap().put("trabajadores", listaPersonas);

        boolean generarReporte = false;
        boolean reValidarUnion = false;

        boolean editados = false;
        boolean procesar = true;
        int soloSobreTiemposNoRemunerados = 0;
        int contEditados = 0;
        for (int i = 0; i < incidencias.size(); i++) {
            if (incidencias.get(i).isEstatus()) {
                //Hay incidencias editadas, debo determinar si todas son de sobretiempos no remunerdos.
                if (incidencias.get(i).getTipoSobretiempo() != null) {
                    if (incidencias.get(i).getTipoSobretiempo().getCodtst() == 2) {
                        soloSobreTiemposNoRemunerados++;
                    }
                }
                editados = true;
                contEditados++;
            }
        }

        if (autSobretiempo != null) { //Viene del módulo de "Aut. Modo Marcaje/Sobretiempo", y debe por lo tanto, justificar todas las ausencias que aparezcan en la lista.
            if (contEditados != incidencias.size()) { //No se justificaron todas las ausencias
                procesar = false;
            }
        }

        if (editados && procesar) {
            Date fhmodi = ejbFacade.getCurrentDateTime();
            List stDiurno = new ArrayList<sobretiempoDiurno>();
            List stNocturno = new ArrayList<sobretiempoNocturno>();
            List ausencias = new ArrayList<ausencia>();

            List<Casbt001m> listSobretiempo = new ArrayList<Casbt001m>();
            List<Ngvar002t_a> listAusencia = new ArrayList<Ngvar002t_a>();
            for (int i = 0; i < incidencias.size(); i++) {
                if (incidencias.get(i).isEstatus()) { //Solo se imprimen las incidencias editadas por el usuario.

                    //Primero se cargarán todos los sobretiempos, luego todas las ausencias, se hará comit, se cargaran los atributos necesarios para cargar los formatos e imprimirlos
                    if (incidencias.get(i).getTipoIncidencia().contains("ST")) {
                        //Sobretiempos 
                        Casbt001m guardarSobretiempo = new Casbt001m();
                        guardarSobretiempo.setCodst(0); //Código 0 para que el asocie el campo serial
                        guardarSobretiempo.setCodcm(ejbFacade.getCodem()); //Código de la empresa
                        guardarSobretiempo.setCodpe(incidencias.get(i).getCodpe()); //Código del trabajador
                        Ngvar006t ngvar006t = new Ngvar006t(2);
                        guardarSobretiempo.setNgvar006t(ngvar006t); // Coded  Estado (2) Ingresado
                        guardarSobretiempo.setLogpi(ejbFacade.getCusua()); //Quien Genera la incidencia
                        guardarSobretiempo.setCasbt002d(incidencias.get(i).getTipoSobretiempo()); //Tipo sobretiempo

                        //NEW arcarrero 09/05/2016 por requerimiento.
                        if (incidencias.get(i).getTipoSobretiempo().getCodtst() == 2) {
                            //En caso de ser No Remunerado. (no se imprime el formato y se registra como Procesado directamente.
                            ngvar006t = new Ngvar006t(1);
                            guardarSobretiempo.setNgvar006t(ngvar006t); // Coded  Estado (1) PROCESADO
                        }

                        //Fecha y hora de inicio del sobretiempo
                        Date fechaInicio = jbvarios.stringToFecha(jbvarios.formatearFecha(incidencias.get(i).getFhini(), this.ftoFhJav), this.ftoFhJav);
                        Date horaInicio = jbvarios.stringToFecha(jbvarios.formatearFecha(incidencias.get(i).getFhini(), this.ftoHora), this.ftoHora);
                        guardarSobretiempo.setFhini(fechaInicio);
                        guardarSobretiempo.setHrini(horaInicio);

                        //Fecha y hora de fin del sobretiempo
                        Date fechaFin = jbvarios.stringToFecha(jbvarios.formatearFecha(incidencias.get(i).getFhfin(), this.ftoFhJav), this.ftoFhJav);
                        Date horaFin = jbvarios.stringToFecha(jbvarios.formatearFecha(incidencias.get(i).getFhfin(), this.ftoHora), this.ftoHora);
                        guardarSobretiempo.setFhfin(fechaFin);
                        guardarSobretiempo.setHrfin(horaFin);

                        //Las fechas de cálculo se determinan a partir del siguiente periodo de nómina.
                        String[] fechas = funciones.fng_obtener_proximo_periodo_pago_salarial(incidencias.get(i).getCodpe(), "P", ejbFacade.getCodem()).split(" ");

                        Date inicialCalculo = jbvarios.stringToFecha(fechas[0], this.ftoFhJav);

                        guardarSobretiempo.setFeini(inicialCalculo); //Fecha ini Cálculo
                        if (incidencias.get(i).getDuracion().contains("Día")) {
                            //Si es en días, se suma a la fecha inicial el cálculo de los días
                            int cantidad = Integer.parseInt(incidencias.get(i).getDuracion().split(" ")[0]);
                            String fin = jbvarios.SumarFecha(fechas[0], (cantidad - 1), this.ftoFhJav);
                            //-1, si la duración es de 1 día, y sumo 1 día las fechas inicial y final serían diferentes, pero al ser de 1 día de duración, debe ser la misma fecha.
                            Date finalCalculo = jbvarios.stringToFecha(fin, this.ftoFhJav);

                            guardarSobretiempo.setFefin(finalCalculo); //Fecha fin Cálculo                            
                        } else {
                            //Si la duración no es en días, la fecha final del cálculo es igual a la inicial.
                            guardarSobretiempo.setFefin(inicialCalculo); //Fecha fin Cálculo                            
                        }

                        //Fecha y hora de autorizacion
                        guardarSobretiempo.setFhast(fhmodi);

                        //Observación
                        guardarSobretiempo.setObsst(incidencias.get(i).getObservacion());

                        //Tipo de hora (D) Diurno (N) Nocturno
                        if (incidencias.get(i).getTipoIncidencia().contains("STD")) { //Diurno
                            guardarSobretiempo.setTipdn('D');
                        } else {//Nocturno
                            guardarSobretiempo.setTipdn('N');
                        }

                        //Motivo
                        guardarSobretiempo.setCodmo(incidencias.get(i).getMotivoSobretiempo().getCodmo());

                        //Actividad
                        guardarSobretiempo.setCodac(incidencias.get(i).getActividadSobretiempo().getCodmo());

                        //Duración
                        guardarSobretiempo.setCanct(jbvarios.stringToFecha(incidencias.get(i).getDuracion(), this.ftoHora));

                        //Tiempo a pagar de sobretiempo luego de aplicación de políticas de redondeo de sobretiempo
                        String sobretiempoAPagar = casbt001mFacade.sobretiempoAPagar(incidencias.get(i).getDuracion(), fechaInicio);
                        guardarSobretiempo.setCapst(jbvarios.stringToFecha(sobretiempoAPagar, this.ftoHora));

                        //Origen
                        guardarSobretiempo.setOrgst('I'); //Por ser el módulo de incidencias no autorizadas.
                        guardarSobretiempo.setCusua(ejbFacade.getCusua());
                        guardarSobretiempo.setFhmod(fhmodi);

                        //Falta el código de barras, pero este se guardará luego de hacer commit, ya que se necesita el código del sobreitempo para generarlo.
                        listSobretiempo.add(guardarSobretiempo);
                    }
                    //Ausencias
                    if (incidencias.get(i).getTipoIncidencia().contains("AU")) {

                        Ngvar002t_a guardarAusencia = new Ngvar002t_a(new Ngvar002tPK_a());

                        guardarAusencia.getNgvar002tPK().setCodcm(ejbFacade.getCodem());
                        guardarAusencia.getNgvar002tPK().setCodpe(incidencias.get(i).getCodpe());

                        //Las fechas de cálculo se determinan a partir del siguiente periodo de nómina.
                        String[] fechas = funciones.fng_obtener_proximo_periodo_pago_salarial(incidencias.get(i).getCodpe(), "P", ejbFacade.getCodem()).split(" ");

                        Date inicialCalculo = jbvarios.stringToFecha(fechas[0], this.ftoFhJav);

                        guardarAusencia.setFdepm(inicialCalculo); //Fecha inicial Cálculo
                        if (incidencias.get(i).getDuracion().contains("Día")) {
                            //Si es en días, se suma a la fecha inicial el cálculo de los días
                            int cantidad = Integer.parseInt(incidencias.get(i).getDuracion().split(" ")[0]);
                            String fin = jbvarios.SumarFecha(fechas[0], (cantidad - 1), this.ftoFhJav);
                            //-1, si la duración es de 1 día, y sumo 1 día las fechas inicial y final serían diferentes, pero al ser de 1 día de duración, debe ser la misma fecha.
                            Date finalCalculo = jbvarios.stringToFecha(fin, this.ftoFhJav);
                            guardarAusencia.setFhapm(finalCalculo); //Fecha fin Cálculo
                        } else {
                            //Si la duración no es en días, la fecha dinal del cálculo es igual a la inicial.
                            guardarAusencia.setFhapm(inicialCalculo); //Fecha fin Cálculo    
                        }

                        guardarAusencia.setFdehi(incidencias.get(i).getFhini()); //Fecha inicial 
                        guardarAusencia.setFhahi(incidencias.get(i).getFhfin()); //Fecha fin 

                        if (incidencias.get(i).getDuracion().contains("Día")) {//La ausencia es en días.
                            guardarAusencia.setDurua('d');
                            if (incidencias.get(i).getTipoAusencia().getNgvar005tPK().getTippm().compareTo("PCV") == 0) {
                                //Si es permiso a cuenta de vacaciones el campo diades toma valor
                                guardarAusencia.setDiades(Integer.parseInt(incidencias.get(i).getDuracion().split(" ")[0]));
                            }
                        } else { //La ausencia es en horas.
                            guardarAusencia.setDurua('h');
                            guardarAusencia.setHrini(jbvarios.stringToFecha(jbvarios.fechaToString(incidencias.get(i).getFhini(), this.ftoHora), this.ftoHora)); //Hora ini
                            guardarAusencia.setHrfin(jbvarios.stringToFecha(jbvarios.fechaToString(incidencias.get(i).getFhfin(), this.ftoHora), this.ftoHora)); //Hora fin
                        }

                        guardarAusencia.setCodmp(incidencias.get(i).getMotivoAusencia().getNgvar001tPK().getCodmp()); //Motivo de la ausencia

                        guardarAusencia.setObspm(incidencias.get(i).getObservacion()); //Observacion

                        guardarAusencia.setFhreg(fhmodi);//Fecha de registro

                        guardarAusencia.setLogpi(ejbFacade.getCusua()); //Usuario que ingresa la ausencia

                        guardarAusencia.setTippm(incidencias.get(i).getTipoAusencia().getNgvar005tPK().getTippm()); //Tipo ausencia.

                        Ngvar006t ngvar006t = new Ngvar006t(2);
                        guardarAusencia.setCoded(2); // Coded  Estado (2) Ingresado

                        guardarAusencia.setOrgpm('I'); //Origen "I" por proceder del módulo incidencias no autorizadas.

                        if (incidencias.get(i).getTipoIncidencia().compareTo("AUD") == 0 && guardarAusencia.getDurua() == 'h') {
                            guardarAusencia.setTipau('D');
                        }
                        if (incidencias.get(i).getTipoIncidencia().compareTo("AUN") == 0 && guardarAusencia.getDurua() == 'h') {
                            guardarAusencia.setTipau('N');
                        }

                        guardarAusencia.setCusua(ejbFacade.getCusua());
                        guardarAusencia.setFhmod(fhmodi);

                        //Falta el código de barras, pero este se guardará luego de hacer commit, ya que se necesita el código de la ausencia para generarlo.
                        listAusencia.add(guardarAusencia);
                    }
                }
            }

            //Ya estan los registros cargados, es hora de hacer commit para almacenarlos en la BD, generar el código de barras e imprimirlos
            UserTransaction transaction = null;
            transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
            try {
                transaction.begin();

                for (int i = 0; i < listSobretiempo.size(); i++) {
                    casbt001mFacade.create(listSobretiempo.get(i));
                }
                for (int i = 0; i < listAusencia.size(); i++) {
                    ngvar002t_aFacade.create(listAusencia.get(i));
                }
                for (int i = 0; i < listSobretiempo.size(); i++) {
                    Map<Object, Object> parametros = new HashMap<Object, Object>();
                    parametros.put("1", ejbFacade.getCusua());
                    parametros.put("2", listSobretiempo.get(i).getFhini());
                    parametros.put("3", listSobretiempo.get(i).getFhfin());
                    parametros.put("4", listSobretiempo.get(i).getCanct());
                    parametros.put("5", listSobretiempo.get(i).getFhmod());
                    parametros.put("6", listSobretiempo.get(i).getCodpe());
                    Casbt001m busquedaPorDetalle = casbt001mFacade.busquedaPorDetalle(parametros, codModulo);

                    String codigoBarras = casbt001mFacade.codigoBarras(busquedaPorDetalle.getCodst().toString(), fhmodi, ejbFacade.getCodem(), false);
                    busquedaPorDetalle.setCodba(codigoBarras);
                    casbt001mFacade.edit(busquedaPorDetalle);
                    casbt001mFacade.cargarSobretiempo(stDiurno, stNocturno, busquedaPorDetalle, codModulo);
                }

                for (int i = 0; i < listAusencia.size(); i++) {
                    Map<Object, Object> parametros = new HashMap<Object, Object>();
                    parametros.put("1", ejbFacade.getCusua());
                    parametros.put("2", listAusencia.get(i).getFdehi());
                    parametros.put("3", listAusencia.get(i).getFhahi());
                    parametros.put("4", listAusencia.get(i).getHrini());
                    parametros.put("5", listAusencia.get(i).getHrfin());
                    parametros.put("6", listAusencia.get(i).getFhmod());
                    parametros.put("7", listAusencia.get(i).getNgvar002tPK().getCodpe());

                    Ngvar002t busquedaPorDetalle = ngvar002tFacade.busquedaPorDetalle(parametros, codModulo);

                    String codigoBarras = casbt001mFacade.codigoBarras(String.valueOf(busquedaPorDetalle.getNgvar002tPK().getIdepm()), fhmodi, ejbFacade.getCodem(), true);
                    busquedaPorDetalle.setCodba(codigoBarras);
                    ngvar002tFacade.edit(busquedaPorDetalle);
                    ngvar002tFacade.cargarAusencia(busquedaPorDetalle, ausencias, codModulo);
                }
                transaction.commit();
                generarReporte = true;
            } catch (Exception e) {
                if (transaction != null) {
                    try {
                        transaction.rollback();
                    } catch (Exception ex) {
                        logger.debug(e.getMessage());
                        Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                                ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                    }
                }
            }

            if (generarReporte) {
                FacesContext fcontext = FacesContext.getCurrentInstance();
                ServletContext context = (ServletContext) fcontext.getExternalContext().getContext();
                String destino = context.getRealPath("") + "/resources/reportes/" + ejbFacade.getCusua() + ".pdf";
                HashMap parameters = new HashMap();
                parameters.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle("/content/reporte"));
                List<JasperPrint> print = new ArrayList<JasperPrint>();

                if (!stDiurno.isEmpty()) {
                    String ruta = context.getRealPath("") + "/resources/reportes/sobretiempoDiurno.jasper";
                    print.add(JasperFillManager.fillReport(ruta, parameters, new JRBeanCollectionDataSource(stDiurno)));
                }

                if (!stNocturno.isEmpty()) {
                    String ruta = context.getRealPath("") + "/resources/reportes/sobretiempoNocturno.jasper";
                    print.add(JasperFillManager.fillReport(ruta, parameters, new JRBeanCollectionDataSource(stNocturno)));
                }

                if (!ausencias.isEmpty()) {
                    String ruta = context.getRealPath("") + "/resources/reportes/ausencia.jasper";
                    print.add(JasperFillManager.fillReport(ruta, parameters, new JRBeanCollectionDataSource(ausencias)));
                }

                if (!ausencias.isEmpty() || !stNocturno.isEmpty() || !stDiurno.isEmpty()) {
                    JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destino);
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, print);
                    exporter.exportReport();
                }

                String fecha = jbvarios.fechaToString(ejbFacade.getCurrentDateTime(), "dd/MM/yyyy HH:mm a");
                String[] auxFecha = fecha.split(" ");
                String nombreArchivo = "Incidencias_No_Autorizadas_" + auxFecha[0].replace("/", "") + "_" + auxFecha[1] + "_" + auxFecha[2];
                InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/reportes/" + ejbFacade.getCusua() + ".pdf");
                this.rutaArchivo = new DefaultStreamedContent(stream, "application/pdf", nombreArchivo + ".pdf");

                RequestContext confirm = RequestContext.getCurrentInstance();
                if (autSobretiempo == null) {
                    if (soloSobreTiemposNoRemunerados == contEditados) {
                        //Si todos los registros editados pertenecen a sobretiempos no remunerados no se debe imprimir ningun formato..
                        //Se mostrará que se hicieron los registros en la BD. Y se actualiza la lista.
                        Utilidades.mostrarMensaje(0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                                ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasTodosSTNoRemunerados"));
                    } else {//Se imprimer formato.
                        confirm.execute("PF('confirmation').show();");
                    }
                } else {//viene del módulo de Autorización de Modo de Marcaje/Sobretiempo, debo retornar a él, muestro una ventana emergente para ello.  
                    confirm.execute("PF('confirmation2').show();");
                }

                System.out.println("Incidencias Removidas: ");
                for (int i = 0; i < incidencias.size(); i++) {
                    if (incidencias.get(i).isEstatus()) {
                        if (!incidencias.get(i).isIncidencia() && incidencias.get(i).getUnirDia() != 0) {
                            reValidarUnion = true;
                            /*
                             Si se imprime una incidencia (ausencia) y tiene la opcion de unirse en días, este registro se eliminará de la vista, quedando su par de unión, huérfano,
                             por tal motivo se debe revalidar las uniones para que de este huérfano desaparezca la flecha que da la posibilidad de unión.
                             */
                        }
                        incidencias.remove(incidencias.indexOf(incidencias.get(i)));
                        i--; //Evalúo de nuevo desde el registro anterior.
                    }
                }

                if (reValidarUnion) {
                    for (int i = 0; i < incidencias.size(); i++) {
                        if (!incidencias.get(i).isIncidencia()) {
                            //Es una ausencia?

                            String sentencia = "execute procedure horario_trabajo('" + incidencias.get(i).getCodpe() + "',to_date('" + jbvarios.fechaToString(incidencias.get(i).getFeper(), this.ftoFhJav) + "','%d/%m/%Y'))";
                            Object singleResult = ejbFacade.getEm().createNativeQuery(sentencia).setMaxResults(1).getSingleResult();
                            String horarioTrabajo = singleResult.toString();
                            String separador[] = horarioTrabajo.split(" - ");
                            String entrada = separador[0];
                            String salida = separador[1];

                            boolean UnirDia = false;

                            if (i + 1 < incidencias.size()) {
                                /*
                                 * Valido si la siguiente ausencia es el mismo día para el 2do periodo de trabajo (Luego del descanso de alimentación)
                                 * De ser así, se asigna el numero 1 al unirDia, que representa una felcha hacia abajo para indicar que se pudiera unir con el siguiente registro.
                                 */
                                int duracion = duracion(incidencias.get(i + 1).getFhini(), incidencias.get(i + 1).getFhfin(), incidencias.get(i).getFhini(), incidencias.get(i).getFhfin(), incidencias.get(i).getCodigoTurno(), incidencias.get(i).getMarcajeReloj());//Si la duración aplica para ser unidas las incidencias en días
                                if (incidencias.get(i).getCodpe() == incidencias.get(i + 1).getCodpe() //mismo trabajador
                                        && incidencias.get(i).getFeper().equals(incidencias.get(i + 1).getFeper()) //Mismo día
                                        && incidencias.get(i + 1).isIncidencia() == false //Es tambien una ausencia
                                        && jbvarios.fechaToString(incidencias.get(i).getFhini(), "HH:mm:ss").contains(entrada) //La ausencia actual contenga la hora de entrada
                                        && jbvarios.fechaToString(incidencias.get(i + 1).getFhfin(), "HH:mm:ss").contains(salida) //La ausencia siguiente contenga la hora de salida
                                        && duracion > 0) {
                                    incidencias.get(i).setUnirDia(1); //Flecha Hacia Abajo
                                    this.mostrarUnirDia = true;
                                    incidencias.get(i).setUnirDia(duracion == 2 ? 1 : 3); //Flecha Hacia Abajo 1.Verde 3.Roja
                                    UnirDia = true;
                                }
                            }

                            /**
                             * Valido el otro caso, que el periodo con unir sea con el anterior, en ese caso asigno 2 a unirDía.
                             */
                            if (i - 1 >= 0) {
                                int duracion = duracion(incidencias.get(i).getFhini(), incidencias.get(i).getFhfin(), incidencias.get(i - 1).getFhini(), incidencias.get(i - 1).getFhfin(), incidencias.get(i).getCodigoTurno(), incidencias.get(i).getMarcajeReloj());//Si la duración aplica para ser unidas las incidencias en días
                                if (incidencias.get(i).getCodpe() == incidencias.get(i - 1).getCodpe() //mismo trabajador
                                        && incidencias.get(i).getFeper().equals(incidencias.get(i - 1).getFeper()) //Mismo día
                                        && incidencias.get(i - 1).isIncidencia() == false //Es tambien una ausencia
                                        && jbvarios.fechaToString(incidencias.get(i).getFhfin(), "HH:mm:ss").contains(salida) //La ausencia actual contenga la hora de salida
                                        && jbvarios.fechaToString(incidencias.get(i - 1).getFhini(), "HH:mm:ss").contains(entrada) //La ausencia anterior contenga la hora de entrada4
                                        && duracion > 0) {
                                    this.mostrarUnirDia = true;
                                    incidencias.get(i).setUnirDia(duracion == 2 ? 2 : 4); //Flecha Hacia Arriba 2.Verde 4.Roja
                                    UnirDia = true;
                                }
                            }

                            if (!UnirDia) { //No entró en la validacines anteriores, no tiene par para realizar la unión.                                
                                incidencias.get(i).setUnirDia(0);
                            }
                        }
                    }
                }
                ordenar(incidencias);
                confirm.execute("PF('incTable').clearFilters();");
            } else {
                Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
            }

        } else {
            if (!procesar) {
                Utilidades.mostrarMensaje(1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasTodasIncRQ"));
            } else {
                Utilidades.mostrarMensaje(1, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                        ResourceBundle.getBundle(Utilidades.BUNDLE).getString("IncidenciasNoAutorizadasImprimirRQ"));
            }
        }
    }

    /**
     * Método para retornar al módulo de Aut. Modo Marcaje/Sobretiempo
     *
     * @return String con el valor del retorno.
     */
    public String retornoAutMarcaje() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String retornoIncNoAut = "S";
        ec.getRequestMap().put("empcat", listaPersonas.get(0));
        ec.getRequestMap().put("retornoIncNoAut", retornoIncNoAut);
        ec.getRequestMap().put("rutaArchivo", rutaArchivo);
        ec.getRequestMap().put("feini", fechaDesde);
        return "/Proyecto/gestiondepersonal/autorizarmodomarcaje_sobretiempo/caaut001m/incluirAut.xhtml";
    }

    /**
     * Mètodo para setear los atributos, del _item al selIncidencia...
     *
     * @param selIncidencia
     * @param _item
     */
    private void setearIncidenciaItem(IncNoAutorizadas selIncidencia, IncNoAutorizadas _item) {
        selIncidencia.setSerial(_item.getSerial());
        selIncidencia.setEstatus(_item.isEstatus());
        selIncidencia.setNombre(_item.getNombre());
        selIncidencia.setCodpe(_item.getCodpe());
        selIncidencia.setCidpe(_item.getCidpe());
        selIncidencia.setCoddp(_item.getCoddp());
        selIncidencia.setNomdp(_item.getNomdp());
        selIncidencia.setCargo(_item.getCargo());
        selIncidencia.setIncidencia(_item.isIncidencia());
        selIncidencia.setObservacion(_item.getObservacion());
        selIncidencia.setTipoIncidencia(_item.getTipoIncidencia());
        selIncidencia.setFeper(_item.getFeper());
        selIncidencia.setFhini(_item.getFhini());
        selIncidencia.setFhfin(_item.getFhfin());
        selIncidencia.setDuracion(_item.getDuracion());
        selIncidencia.setCantidadST(_item.getCantidadST());
        selIncidencia.setTipoSobretiempo(_item.getTipoSobretiempo());
        selIncidencia.setMotivoSobretiempo(_item.getMotivoSobretiempo());
        selIncidencia.setActividadSobretiempo(_item.getActividadSobretiempo());
        selIncidencia.setTipoAusencia(_item.getTipoAusencia());
        selIncidencia.setMotivoAusencia(_item.getMotivoAusencia());
        selIncidencia.setCodigoTurno(_item.getCodigoTurno());
    }

    /**
     * Método para consultar si los tipos y motivos de ausencia son remunerados para sumarle al acumulado anual.
     *
     * @param _motivoSelected
     * @param _tipoSelected
     * @param editadaAnterior
     */
    private void consultarAusenciasRemuneradas(Ngvar005t _tipoSelected, Ngvar001t _motivoSelected, IncNoAutorizadas editadaAnterior) {
        if (_motivoSelected != null && _tipoSelected != null) { //Habiendo elejido las 2 opciones se hace la validación. 

            //Obtengo solo el año de la incidencia y el actual
            boolean mismoanio = anioActual(jbvarios.fechaToString(selIncidencia.getFeper(), this.ftoFhJav).split("/")[2], ejbFacade.getCurrentDate().split("/")[2]);

            //Retorno al valor del acumulado de cuando se abrió el editar (pudo haber seleccionado un tipo y un motivo remunerado, se suma esa duración
            //al acumulado pero luego cambia los tipos y los motivos de nuevo, y estos pudieran no ser remunerados, entonces es necesario reverir la suma 
            //que se había hecho
            String acumulado = "";
            if (mismoanio) {
                acumulado = acum_Permiso_Remunerado_Actual.get(selIncidencia.getCodpe());
            } else {
                acumulado = acum_Permiso_Remunerado_Anterior.get(selIncidencia.getCodpe());
            }
            horas_acumuladas = acumulado;

            boolean remunerada_actual = false;
            boolean remunerada_anterior = false;

            //Se consulta si ya había sido editada esta incidencia anteriormente (para no sumar nuevamente las horas acumuladas si ya lo había hecho la primera vez).
            if (editadaAnterior.getCodpe() != 0) { //Ya había sido editada

                //Se comprueba si la que antes había editado aplicaba para REMUNERADA
                remunerada_anterior = aplicaRemunerada(editadaAnterior.getTipoAusencia(), editadaAnterior.getMotivoAusencia());

                //Se comprueba si la actual aplica para REMUNERADA
                remunerada_actual = aplicaRemunerada(_tipoSelected, _motivoSelected);

                //Comparo lo anterior con lo actual, si son iguales (no a nivel de tipo y motivo sino de si ambos aplican a remunerado)
                if (!remunerada_actual && remunerada_anterior) {
                    //La actual no es remunerada, y la anterior sí, por ende se debe restar la duración de la incidencia.
                    horas_acumuladas = restarDuracion(editadaAnterior.getDuracion(), acumulado, editadaAnterior);
                } else if (remunerada_actual && !remunerada_anterior) {
                    //La anterior no era remunerada, y la actual sí, por ende se debe sumar la duración de la incidencia 
                    horas_acumuladas = calcular.sumaDuracionTemporal(editadaAnterior.getDuracion(), acumulado, editadaAnterior);
                }
                //En caso de que ambas aplican a remunerada no se suma ni se resta, ya que apesar de que pudieran cambiar de tipos y motivos ya se habían sumando antes las horas

            } else {
                //Se comprueba si la actual aplica para REMUNERADA
                remunerada_actual = aplicaRemunerada(_tipoSelected, _motivoSelected);

                if (remunerada_actual) {
                    //Sumar la duración al acumulador.
                    horas_acumuladas = calcular.sumaDuracionTemporal(selIncidencia.getDuracion(), acumulado, selIncidencia);

                }
            }
        }
    }

    /**
     * Método que retorna si tipo y motivo son remunerados
     *
     * @param _tipoAusencia Tipo ausencia
     * @param _motivoAusencia Motivo ausencia
     * @return True es remunerada, false no es remunerada
     */
    private boolean aplicaRemunerada(Ngvar005t _tipoAusencia, Ngvar001t _motivoAusencia) {
        boolean retorno = false;
        String tipo = _tipoAusencia.getNgvar005tPK().getTippm();
        String motivo = String.valueOf(_motivoAusencia.getNgvar001tPK().getCodmp());

        //Busco si los tiempos y motivos aplican a remunerados
        boolean tipoRem = tipos_Remunerados.contains(tipo); //true: es remunerado.. false: caso contrario
        boolean motivoRem = motivos_Remunerados.contains(motivo); //true: es remunerado.. false: caso contrario

        if (tipoRem && motivoRem) { //Si ambos anteriores eran remunerados, aplica para ausencia remunerada
            retorno = true;
        }
        return retorno;
    }

    /**
     * Método que compara si dos años son iguales ej: 2016 y 2016
     *
     * @param _anioIncidencia Año 1
     * @param _anioActual Año 2
     * @return true si son iguales, false si no
     */
    private boolean anioActual(String _anioIncidencia, String _anioActual) {
        boolean retorno = false;

        //Comparamos si pertenece al año actual
        if (_anioIncidencia.compareTo(_anioActual) == 0) { //Es el mismo año
            retorno = true;
        }

        return retorno;
    }

    /**
     * Método para restar al acumulado, la duración de la incidencia.
     *
     * @param _duracion Duración de la incidencia
     * @param _acumulado Acumulado de horas remuneradas diurnas
     * @return String Fto "00:00:00"
     */
    private String restarDuracion(String _duracion, String _acumulado, IncNoAutorizadas _item) {
        int minuto_duracion = 0;
        int hora_duracion = 0;
        //Se consulta si la duración es en días
        if (_duracion.contains("Día")) {
            int dias = Integer.parseInt(_duracion.split(" ")[0]);
            double total = (_item.getCoddp() == 6 ? 7.5 : 8) * dias; //Haciendo referencia al mismo criterio consultado en la función  fng_hrs_ausencia
            int horas = Integer.parseInt(String.valueOf(total).split("\\.")[0]);
            int minutosParciales = Integer.parseInt(String.valueOf(total).split("\\.")[1]);
            hora_duracion = horas;

            minuto_duracion = minutosParciales == 5 ? 30 : 0; //Siempre retorna o 0 o 5, de ser 5 los minutos equivalen a 30min, y si es 0 es 0.
        } else {
            hora_duracion = Integer.parseInt(_duracion.split(":")[0]);
            minuto_duracion = Integer.parseInt(_duracion.split(":")[1]);
        }

        int hora_acumulado = Integer.parseInt(_acumulado.split(":")[0]);
        int minuto_acumulado = Integer.parseInt(_acumulado.split(":")[1]);

        int minutos = minuto_acumulado - minuto_duracion;
        int horas = hora_acumulado - hora_duracion;

        if (minutos < 0) {
            //En caso de que los minutos de la duración sean > al acumulado, da negativo, en ese caso se resta una hora y esa diferencia se resta a los 60 min. 
            minutos = 60 - Math.abs(minutos);
            horas = horas - 1;
        }

        String horas_retornadas = horas < 10 ? "0" + horas : "" + horas;
        String minutos_retornados = minutos < 10 ? "0" + minutos : "" + minutos;

        return horas_retornadas + ":" + minutos_retornados + ":00";
    }

    /**
     * Método que incrementa el progreso según la taza
     *
     * @param _taza
     */
    private void incrementoDelProgreso(double _taza) {
        System.out.println("---->Porcentaje: " + (progreso.getPorcentaje() + (int) _taza));
        progreso.setProgreso((progreso.getPorcentaje() + (int) _taza));
    }

    /**
     * Método para calcular si la duración de ambas incidencias a unir es superior al parámetro establecido, de ser así, se permite la union de estas
     * incidencias
     *
     * @param _incMayorFhini Fecha Inicial de la incidencia mayor
     * @param _incMayorFhfin Fecha Final de la incidencia mayor
     * @param _incMenorFhini Fecha Inicial de la incidencia menor
     * @param _incMenorFhfin Fecha Final de la incidencia menor
     * @param _marcaje Marcaje del trabajador en reloj
     * @return True si la duración es mayor o igual al parámetro, False en caso contrario.
     */
    private int duracion(Date _incMayorFhini, Date _incMayorFhfin, Date _incMenorFhini, Date _incMenorFhfin, int _codtn, String _marcaje) throws Exception {
        int retorno = 0;
        //Cáculo de la duracion  
        Date feini = jbvarios.stringToFecha(jbvarios.fechaToString(_incMayorFhini, this.ftoFhJav), this.ftoFhJav);
        Date fefin = jbvarios.stringToFecha(jbvarios.fechaToString(_incMayorFhfin, this.ftoFhJav), this.ftoFhJav);
        String hrini = jbvarios.fechaToString(_incMayorFhini, "HH:mm:ss");
        String hrfin = jbvarios.fechaToString(_incMayorFhfin, "HH:mm:ss");
        String duracionMayor = casbt001mFacade.calcularDuracion(feini, hrini, fefin, hrfin);

        feini = jbvarios.stringToFecha(jbvarios.fechaToString(_incMenorFhini, this.ftoFhJav), this.ftoFhJav);
        fefin = jbvarios.stringToFecha(jbvarios.fechaToString(_incMenorFhfin, this.ftoFhJav), this.ftoFhJav);
        hrini = jbvarios.fechaToString(_incMenorFhini, "HH:mm:ss");
        hrfin = jbvarios.fechaToString(_incMenorFhfin, "HH:mm:ss");
        String duracionMenor = casbt001mFacade.calcularDuracion(feini, hrini, fefin, hrfin);

        String duracionTotal = calcular.sumaDuracionTemporal(duracionMayor, duracionMenor, null); //Fto HH:MM:SS

        int minutosTotales = Integer.parseInt(duracionTotal.split(":")[0]) * 60 + Integer.parseInt(duracionTotal.split(":")[1]);

        /**
         * Si la diferencia con el parámetro es > (es mas la duración de las incidencias que el valor del parámetro) o es igual (es exactamente la misma
         * duración) Las incidencias se pueden unir.
         */
        String horasPermitidas = capar001mFacade.consultarParametro(ejbFacade.getCodem(), "HMDA").getValpm();

        int minutosPermitidos = horasPermitidas.contains(".") ? Integer.parseInt(horasPermitidas.split(".")[0]) * 60 + Integer.parseInt(horasPermitidas.split(".")[1]) : Integer.parseInt(horasPermitidas) * 60;

        if (minutosTotales >= minutosPermitidos) {
            if (_marcaje.compareTo("Sin Marcaje") == 0) {
                retorno = 2; //Flechas Verdes
            } else {
                retorno = 1; //Flechas Rojas (hay marcajes).
            }
        }

        return retorno;
    }
//</editor-fold>
}
