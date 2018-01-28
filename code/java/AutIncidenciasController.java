package com.fenix.control.controller.gestiondepersonal.incidencias.procesarincidencias;
//<editor-fold defaultstate="collapsed" desc="Importaciones">

import com.fenix.control.controller.util.JsfUtil;
import com.fenix.control.fenixTools.util.Utilidades;
import com.fenix.control.session.Permisos;
import com.fenix.control.session.sessionBean;
import com.fenix.logica.conexion.Conexion;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Casbt001l;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Casbt001m;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Ngvar002l;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Ngvar002t;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.autorizaraterceros.Caate001m;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.autorizaraterceros.Caate002d;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.Cadup001m;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.procesarincidencias.incidenciasDuplicadas;
import com.fenix.logica.entidades.talentohumano.trabajador.Ngbas001x;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001lFacade;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001mFacade;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002lFacade;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002tFacade;
import com.fenix.util.Trabajador;
import com.fenix.util.jbVarios;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

//</editor-fold>
/**
 * <br><b>Title:</b> </br> <br><b>Description:</b> .</br> <br><b>Copyright:</b>
 * Copyright (c) 2011</br> <br><b>Company:</b> Pasteurizadora Tachira CA</br>
 *
 * @author user
 * @version 0.xv dia, hora
 */
@ManagedBean(name = "AutIncidenciasController")
@ViewScoped
public class AutIncidenciasController {
//<editor-fold defaultstate="collapsed" desc="Declaracion de Variables">

    private Ngbas001x solicitanteImagen;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001mFacade ejbFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001mFacade ejbFacadeS;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001lFacade ejbFacadeSH;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002tFacade ejbFacadeA;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Ngvar002lFacade ejbFacadeAH;
    @EJB
    private com.fenix.control.fenixTools.mail.CorreoFacade correoFacade;
    @EJB
    private com.fenix.logica.jpa.talentohumano.trabajador.Ngbas001xFacade ngbas001xFacade;
    @EJB
    private com.fenix.logica.jpa.adminsistema.gruposparametros.Tgpar002dFacade tgpar002dFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Casbt001mFacade casbt001mFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.procesarincidencias.Cadup001mFacade cadup001mFacade;
    jbVarios jbvarios = new jbVarios();
    private int selectedItemIndex;
    private String codModulo = "SCAF0775";
    /*Para el Lazy */
    private LazyDataModel<Casbt001m> lazyModel;
    private Integer pagIndex = null;
    private Integer paginacion = null;
    private Map<String, Object> fields = new HashMap<String, Object>();
    private String sortF = null;
    private int cantElemTabla = 20;
    private SortOrder sortB = SortOrder.UNSORTED;
    private boolean permisologia[] = new boolean[10];
    private boolean valoresDefault = false;
    private final static Logger logger = Logger.getLogger(Casbt001m.class.getName());
    private String tipoInc;
    private String compania;
    private int codigo;
    private String fecha;
    private String gestion;
    private boolean mostrar = false;
    private boolean rechazo = false;
    private boolean revertir = false;
    /*Info Datos Incidencia*/
    private String nombre;
    private String cedula;
    private String incidencia;
    private Date fedsd;
    private Date fehst;
    private Date hrini;
    private Date hrfin;
    private String tipo;
    private String motivo;
    private String actividad;
    private String shini;
    private String shrfin;
    private String motivoRechazo;
    private String tipoRechazo;
    private String mensaje;
    private String mensajeAzul;
    private String msjTipoRechazo;
    private String msjMotivoRechazo;
    private String departamento;
    private String cargo;
    private String gerencia;
    private String condicion;
    boolean estatusFormato = false;
    private String diahora = "dd/mm/yyyy hh:mm:ss";
    private String autoriza = "";
    private int cont;
    boolean ausencia = false;
    /*Foto trabajador*/
    private DefaultStreamedContent imagenSolicitante;
    /*Variables SobreTiempo*/
    private Casbt001m current;
    private Casbt001m currentDestroy;
    private Casbt001m currentAgregar;
    private Casbt001m sobretiempo;
    private Casbt001l histSobretiempo;
    /*Variables de Ausencia*/
    private Ngvar002t ngvar002t;
    private Ngvar002l ngvar002tHistorico;
    /*Variables - Supervisado por un 3ero*/
    private Caate001m registroTercero;
    private Caate002d detalleTercero;
    private String periodo;
    private String Speriodo;
    private String msjReintegro;
    private boolean casoRevertir = false;
    private List<incidenciasDuplicadas> incidenciasDuplicadas;
    private boolean sinPermisos = false; //False: Puede procesar planillas de ese trabajador, True: No puede procesar la planilla de ese trabajador, no tiene permisos
    /*Fin de Lazy*/
//</editor-fold>

    public AutIncidenciasController() {
        java.util.Arrays.fill(permisologia, Boolean.TRUE);
        //consultarPermisos(codModulo);
    }
    //<editor-fold defaultstate="collapsed" desc="Encapsulamiento">

    public Casbt001m getCurrent() {
        return current;
    }

    public void setCurrent(Casbt001m current) {
        this.current = current;
    }

    public Casbt001m getSelected() {
        if (current == null) {
            current = new Casbt001m();
            //Si clave primaria Compuesta Inicializarla
            selectedItemIndex = -1;
        }
        return current;
    }

    public Casbt001mFacade getFacade() {
        return ejbFacade;
    }

    public boolean[] getPermisologia() {
        return permisologia;
    }

    public void setPermisologia(boolean[] permisologia) {
        this.permisologia = permisologia;
    }

    public LazyDataModel<Casbt001m> getLazyModel() {
        if (lazyModel == null || lazyModel.getRowCount() == 0) {
            inicializarLazy();
        }
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Casbt001m> lazyModel) {
        this.lazyModel = lazyModel;
    }
//fin lazy

    public String getCodModulo() {
        return codModulo;
    }

    public void setCodModulo(String codModulo) {
        this.codModulo = codModulo;
    }

    public Casbt001m getCurrentAgregar() {
        return currentAgregar;
    }

    public void setCurrentAgregar(Casbt001m currentAgregar) {
        this.currentAgregar = currentAgregar;
    }

    public Casbt001m getCurrentDestroy() {
        return currentDestroy;
    }

    public void setCurrentDestroy(Casbt001m currentDestroy) {
        this.currentDestroy = currentDestroy;
    }

    public int getCantElemTabla() {
        return cantElemTabla;
    }

    public void setCantElemTabla(int cantElemTabla) {
        this.cantElemTabla = cantElemTabla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(String incidencia) {
        this.incidencia = incidencia;
    }

    public Date getFedsd() {
        return fedsd;
    }

    public void setFedsd(Date fedsd) {
        this.fedsd = fedsd;
    }

    public Date getFehst() {
        return fehst;
    }

    public void setFehst(Date fehst) {
        this.fehst = fehst;
    }

    public Date getHrini() {
        return hrini;
    }

    public void setHrini(Date hrini) {
        this.hrini = hrini;
    }

    public Date getHrfin() {
        return hrfin;
    }

    public void setHrfin(Date hrfin) {
        this.hrfin = hrfin;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getShini() {
        return shini;
    }

    public void setShini(String shini) {
        this.shini = shini;
    }

    public String getShrfin() {
        return shrfin;
    }

    public void setShrfin(String shrfin) {
        this.shrfin = shrfin;
    }

    public String getGestion() {
        return gestion;
    }

    public void setGestion(String gestion) {
        this.gestion = gestion;
    }

    public boolean getMostrar() {
        return mostrar;
    }

    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    public boolean isRechazo() {
        return rechazo;
    }

    public void setRechazo(boolean rechazo) {
        this.rechazo = rechazo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public String getTipoRechazo() {
        return tipoRechazo;
    }

    public void setTipoRechazo(String tipoRechazo) {
        this.tipoRechazo = tipoRechazo;
    }

    public int getCont() {
        return cont;
    }

    public void setCont(int cont) {
        this.cont = cont;
    }

    public String getDiahora() {
        return diahora;
    }

    public void setDiahora(String diahora) {
        this.diahora = diahora;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Casbt001m getSobretiempo() {
        return sobretiempo;
    }

    public void setSobretiempo(Casbt001m sobretiempo) {
        this.sobretiempo = sobretiempo;
    }

    public Casbt001mFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(Casbt001mFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public Casbt001mFacade getEjbFacadeS() {
        return ejbFacadeS;
    }

    public void setEjbFacadeS(Casbt001mFacade ejbFacadeS) {
        this.ejbFacadeS = ejbFacadeS;
    }

    public Casbt001lFacade getEjbFacadeSH() {
        return ejbFacadeSH;
    }

    public void setEjbFacadeSH(Casbt001lFacade ejbFacadeSH) {
        this.ejbFacadeSH = ejbFacadeSH;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getGerencia() {
        return gerencia;
    }

    public void setGerencia(String gerencia) {
        this.gerencia = gerencia;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public boolean isEstatusFormato() {
        return estatusFormato;
    }

    public void setEstatusFormato(boolean estatusFormato) {
        this.estatusFormato = estatusFormato;
    }

    public String getAutoriza() {
        return autoriza;
    }

    public void setAutoriza(String autoriza) {
        this.autoriza = autoriza;
    }

    public boolean isAusencia() {
        return ausencia;
    }

    public void setAusencia(boolean ausencia) {
        this.ausencia = ausencia;
    }

    public Ngvar002t getNgvar002t() {
        return ngvar002t;
    }

    public void setNgvar002t(Ngvar002t ngvar002t) {
        this.ngvar002t = ngvar002t;
    }

    public Ngvar002l getNgvar002tHistorico() {
        return ngvar002tHistorico;
    }

    public void setNgvar002tHistorico(Ngvar002l ngvar002tHistorico) {
        this.ngvar002tHistorico = ngvar002tHistorico;
    }

    public Ngvar002tFacade getEjbFacadeA() {
        return ejbFacadeA;
    }

    public void setEjbFacadeA(Ngvar002tFacade ejbFacadeA) {
        this.ejbFacadeA = ejbFacadeA;
    }

    public Ngvar002lFacade getEjbFacadeAH() {
        return ejbFacadeAH;
    }

    public void setEjbFacadeAH(Ngvar002lFacade ejbFacadeAH) {
        this.ejbFacadeAH = ejbFacadeAH;
    }

    public Caate001m getRegistroTercero() {
        return registroTercero;
    }

    public void setRegistroTercero(Caate001m registroTercero) {
        this.registroTercero = registroTercero;
    }

    public Caate002d getDetalleTercero() {
        return detalleTercero;
    }

    public void setDetalleTercero(Caate002d detalleTercero) {
        this.detalleTercero = detalleTercero;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getSperiodo() {
        return Speriodo;
    }

    public void setSperiodo(String Speriodo) {
        this.Speriodo = Speriodo;
    }

    public String getMensajeAzul() {
        return mensajeAzul;
    }

    public void setMensajeAzul(String mensajeAzul) {
        this.mensajeAzul = mensajeAzul;
    }

    public String getMsjTipoRechazo() {
        return msjTipoRechazo;
    }

    public void setMsjTipoRechazo(String msjTipoRechazo) {
        this.msjTipoRechazo = msjTipoRechazo;
    }

    public String getMsjMotivoRechazo() {
        return msjMotivoRechazo;
    }

    public void setMsjMotivoRechazo(String msjMotivoRechazo) {
        this.msjMotivoRechazo = msjMotivoRechazo;
    }

    public String getMsjReintegro() {
        return msjReintegro;
    }

    public void setMsjReintegro(String msjReintegro) {
        this.msjReintegro = msjReintegro;
    }

    public boolean isCasoRevertir() {
        return casoRevertir;
    }

    public void setCasoRevertir(boolean casoRevertir) {
        this.casoRevertir = casoRevertir;
    }

    public List<incidenciasDuplicadas> getIncidenciasDuplicadas() {
        return incidenciasDuplicadas;
    }

    public void setIncidenciasDuplicadas(List<incidenciasDuplicadas> incidenciasDuplicadas) {
        this.incidenciasDuplicadas = incidenciasDuplicadas;
    }

    public boolean isSinPermisos() {
        return sinPermisos;
    }

    public void setSinPermisos(boolean sinPermisos) {
        this.sinPermisos = sinPermisos;
    }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Metodos Por Defecto">
    public String prepareList() {
        current = new Casbt001m();
        //Si clave primaria Compuesta Inicializarla
        // recreateModel();
        //consultarPermisos(codModulo);
        return "List";
    }

    public String prepareView() {
        current = (Casbt001m) lazyModel.getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        //consultarPermisos(codModulo);
        return "View";
    }

    public String prepareCreate() {
        currentAgregar = new Casbt001m();
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
        current = (Casbt001m) lazyModel.getRowData();
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
        // current = (Casbt001m)getItems().getRowData();
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

    public List<Casbt001m> ListAvailableSelectOne() {
        return ejbFacade.findAll(codModulo);
    }

    public List<Casbt001m> ListAvailableSelectOne(String _codmo) {
        return ejbFacade.findAll(_codmo);
    }

    /**
     * Método para imprimir el listado de los duplicados, el padre con sus hijos.
     *
     * @param _incidenciasDuplicadas Listado de duplicados que se quiere imprimir.
     */
    private void imprimirDuplicados(List<incidenciasDuplicadas> _incidenciasDuplicadas) {
        System.out.println("---------------------------------------------------DUPLICADOS!!!------------------------------");
        for (int i = 0; i < _incidenciasDuplicadas.size(); i++) {
            System.out.println("PADRE: " + _incidenciasDuplicadas.get(i).getIdIncidencia());
            for (int j = 0; j < _incidenciasDuplicadas.get(i).getDuplicados().size(); j++) {
                System.out.println("Hijo: " + _incidenciasDuplicadas.get(i).getDuplicados().get(j).getIdIncidencia());

            }
        }
    }

    @FacesConverter(forClass = Casbt001m.class, value = "Casbt001mConverter")
    public static class Casbt001mControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AutIncidenciasController controller = (AutIncidenciasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "casbt001mController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Casbt001m) {
                Casbt001m o = (Casbt001m) object;
                return getStringKey(o.getCodst());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AutIncidenciasController.class.getName());
            }
        }
    }
//Lazy inicio

    public void inicializarLazy() {
        lazyModel = new LazyDataModel<Casbt001m>() {
            @Override
            public List<Casbt001m> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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

    public void inicializarValores() throws Exception {
        mostrar = false;
        rechazo = false;
        msjMotivoRechazo = "";
        msjTipoRechazo = "";
        msjReintegro = "";
        mensaje = "";
        mensajeAzul = "";
        sinPermisos = false;

        incidenciasDuplicadas = new ArrayList<>();

        //Se cargan las incidencias duplicadas que existen.
        cargarDuplicados();

        gestion = "Procesar";

        try {
            currentAgregar = new Casbt001m();
            //inicializarPK
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
            //filtroEstado = request.getParameter("filtroEstado");
//podria leer parametros para cargar por default en la tabla
        } catch (Exception e) {
            getFacade().getLog().error("Error de consulta ");
        }
    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Nuevos Metodos">

    /**
     * Método que ejecuta la acción al leer del código de barras, procesa o rechaza ausencias o sobretiempos
     *
     * @throws Exception Problema al realizar las consultas que este método contiene
     */
    public void procesarPlanilla() throws Exception {
        int procesarSobretiempo = -1;
        estatusFormato = false;
        msjTipoRechazo = "";
        msjMotivoRechazo = "";
        msjReintegro = "";
        casoRevertir = false;
        sinPermisos = false; //False: Puede procesar planillas de ese trabajador, True: No puede procesar la planilla de ese trabajador, no tiene permisos

        try {

            if (currentAgregar.getCodba().length() == 25) {
                tipo = null;
                sobretiempo = null;
                ngvar002t = null;
                ngvar002tHistorico = null;
                histSobretiempo = null;
                diahora = "";
                mensaje = "";
                tipoRechazo = "";
                motivoRechazo = "";
                tipoInc = (currentAgregar.getCodba().substring(0, 1));
                compania = (currentAgregar.getCodba().substring(1, 4));
                codigo = (Integer.parseInt(currentAgregar.getCodba().substring(4, 11)));

                String dia, mes, anio, hora, minuto, segundo;
                dia = (currentAgregar.getCodba().substring(11, 13));
                mes = (currentAgregar.getCodba().substring(13, 15));
                anio = (currentAgregar.getCodba().substring(15, 19));
                hora = (currentAgregar.getCodba().substring(19, 21));
                minuto = (currentAgregar.getCodba().substring(21, 23));
                segundo = (currentAgregar.getCodba().substring(23, 25));
                SimpleDateFormat formatoDelTexto2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String strFecha = dia + "/" + mes + "/" + anio + " " + hora + ":" + minuto + ":" + segundo;
                Date fecha_planilla = formatoDelTexto2.parse(strFecha);
                fecha_planilla = jbvarios.stringToFecha(jbvarios.formatearFecha(fecha_planilla, "dd/MM/yyyy HH:mm:ss"), "dd/MM/yyyy HH:mm:ss");

                Map<Object, Object> parametros = new HashMap<Object, Object>();
                parametros.put("1", codigo);
                parametros.put("2", fecha_planilla);

                if (tipoInc.compareTo("1") == 0) {
                    //Ausencia
                    parametros.put("3", ejbFacade.getCodem());
                    ausencia = true;

                    ngvar002t = ejbFacadeA.buscarNgvar002t(currentAgregar.getCodba(), codModulo);
                    //De no encontrar por el "codba" puede que ocurra el caso del "Revertir", cuando es procesado
                    //De forma automática por TH y luego llega la planilla para ser procesada, por eso se crea este caso, fue una modificación
                    //modificación a.carrero 24-04-2015
                    ngvar002tHistorico = ejbFacadeAH.historicoRevertirPlanilla(codigo, codModulo, ejbFacade.getCodem(), currentAgregar.getCodba());
                    if (ngvar002t == null && ngvar002tHistorico != null) {
                        ngvar002t = ejbFacadeA.buscarNgvar002tReclamo(codigo, codModulo);
                        casoRevertir = true;
                    }
                    if (ngvar002t != null) {

                        String trabajador = ngbas001xFacade.nombreCompleto(ngvar002t.getNgvar002tPK().getCodpe(), codModulo);

                        if (trabajador == null) {
                            sinPermisos = true; //No tiene permisos por restricción de seguridad.
                            currentAgregar.setCodba("");
                        }

                        if (!sinPermisos) {
                            shini = null;
                            shrfin = null;
                            solicitanteImagen = ngbas001xFacade.find(ngvar002t.getNgvar002tPK().getCodpe());
                            nombre = solicitanteImagen.getNompe() + " " + solicitanteImagen.getNo1pe() + " " + solicitanteImagen.getApepe() + " " + solicitanteImagen.getAp1pe();
                            incidencia = "Ausencia";
                            cedula = solicitanteImagen.getCidpe();

                            StringBuilder sentencia = new StringBuilder("SELECT t2.nomcg FROM ngbas001t t, ngbas004t t2 where t.codcg=t2.codcg and t.codpe=").append(ngvar002t.getNgvar002tPK().getCodpe()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("' ").append(" and t2.codcm='").append(ejbFacade.getCodem()).append("'");
                            Object singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            cargo = "" + singleResult.toString();

                            sentencia = new StringBuilder("SELECT t2.nomdp FROM ngbas001t t, ngbas009t t2 where t.coddp=t2.coddp and t.codpe=").append(ngvar002t.getNgvar002tPK().getCodpe()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("' ").append(" and t2.codcm='").append(ejbFacade.getCodem()).append("'");
                            singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            departamento = "" + singleResult.toString();

                            sentencia = new StringBuilder("SELECT t3.nomge FROM ngbas001t t, ngbas009t t2, ngbas008t t3 where t.coddp=t2.coddp and t2.codge=t3.codge and t.codpe=").append(ngvar002t.getNgvar002tPK().getCodpe()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("' ").append(" and t2.codcm='").append(ejbFacade.getCodem()).append("'").append(" and t3.codcm='").append(ejbFacade.getCodem()).append("'");
                            singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            gerencia = "" + singleResult.toString();

                            java.sql.Connection conexion = null;
                            conexion = new Conexion().getConexion();

                            Trabajador trab = new Trabajador(conexion, ejbFacadeS.getCusua(), ejbFacadeS.getCodem());
                            trab.setCodpe(String.valueOf(solicitanteImagen.getCodpe()));
                            trab.setConpe(String.valueOf(solicitanteImagen.getConpe()));
                            trab.setCodcp(String.valueOf(solicitanteImagen.getCodcp()));

                            condicion = trab.SituacionTrababjador(trab);

                            if (!conexion.isClosed()) {
                                conexion.commit();
                                conexion.close();
                            }

                            fedsd = ngvar002t.getFdehi();
                            fehst = ngvar002t.getFhahi();
                            hrini = ngvar002t.getHrini();
                            hrfin = ngvar002t.getHrfin();
                            jbVarios jvar = new jbVarios();

                            if (hrini != null && hrfin != null) {
                                shini = jvar.fechaToString(hrini, "hh:mm:ss");
                                shrfin = jvar.fechaToString(hrfin, "hh:mm:ss");
                            }

                            //Tipo
                            String motivoPreCalculo = tgpar002dFacade.consultarParametro(ejbFacade.getCodem(), "ADFPN").getValpa(); //Actividad De La Ausencia Por Defecto Para El Pre Cálculo - No Remunerado

                            if (motivoPreCalculo.contains(ngvar002t.getTippm())) { //Se valida el caso de que haya sido procesada de forma automática por TH y se le asigne este tipo para registrar la incidencia.
                                tipo = "No Remunerado";
                            } else {
                                sentencia = new StringBuilder("SELECT t.rempm FROM Ngvar005t t where t.codcm='").append(ejbFacade.getCodem()).append("' and t.tippm='").append(ngvar002t.getTippm()).append("'");
                                singleResult = ejbFacadeA.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                                String Remunerado = "" + (singleResult != null ? singleResult.toString() : "");
                                if (Remunerado.compareTo("S") == 0) {
                                    tipo = "Remunerado";
                                } else {
                                    tipo = "No Remunerado";
                                }
                            }

                            //Motivo
                            sentencia = new StringBuilder("SELECT t.desmp FROM ngvar001t t where t.codcm='").append(ejbFacade.getCodem()).append("' and t.codmp=").append(ngvar002t.getNgvar001tPK().getCodmp());
                            singleResult = ejbFacadeA.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            motivo = "" + singleResult.toString();
                        }
                    } else {
                        estatusFormato = false;
                    }
                } else {
                    ausencia = false;
                    sobretiempo = ejbFacadeS.buscarCasbt001m(currentAgregar.getCodba(), codModulo);

                    //De no encontrar por el "codba" puede que ocurra el caso del "Revertir", cuando es procesado
                    //De forma automática por TH y luego llega la planilla para ser procesada, por eso se crea este caso, fue una modificación
                    //modificación a.carrero 24-04-2015
                    histSobretiempo = ejbFacadeSH.historicoRevertirPlanilla(codigo, codModulo, ejbFacade.getCodem(), currentAgregar.getCodba());
                    if (sobretiempo == null && histSobretiempo != null) {
                        sobretiempo = ejbFacadeS.buscarCasbt001m(codigo, codModulo);
                        casoRevertir = true;
                    }
                    if (sobretiempo != null) {

                        String trabajador = ngbas001xFacade.nombreCompleto(sobretiempo.getCodpe(), codModulo);

                        if (trabajador == null) {
                            sinPermisos = true; //No tiene permisos por restricción de seguridad.
                            currentAgregar.setCodba("");
                        }

                        if (!sinPermisos) {
                            solicitanteImagen = ngbas001xFacade.find(sobretiempo.getCodpe());
                            nombre = solicitanteImagen.getNompe() + " " + solicitanteImagen.getNo1pe() + " " + solicitanteImagen.getApepe() + " " + solicitanteImagen.getAp1pe();
                            incidencia = "Sobretiempo";
                            cedula = solicitanteImagen.getCidpe();
                            fedsd = sobretiempo.getFhini();
                            fehst = sobretiempo.getFhfin();
                            hrini = sobretiempo.getHrini();
                            hrfin = sobretiempo.getHrfin();
                            jbVarios jvar = new jbVarios();
                            shini = jvar.fechaToString(hrini, "hh:mm:ss");
                            shrfin = jvar.fechaToString(hrfin, "hh:mm:ss");

                            StringBuilder sentencia = new StringBuilder("SELECT t2.nomcg FROM ngbas001t t, ngbas004t t2 where t.codcg=t2.codcg and t.codpe=").append(sobretiempo.getCodpe()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("' ").append(" and t2.codcm='").append(ejbFacade.getCodem()).append("'");
                            Object singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            cargo = "" + singleResult.toString();

                            sentencia = new StringBuilder("SELECT t2.nomdp FROM ngbas001t t, ngbas009t t2 where t.coddp=t2.coddp and t.codpe=").append(sobretiempo.getCodpe()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("' ").append(" and t2.codcm='").append(ejbFacade.getCodem()).append("'");
                            singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            departamento = "" + singleResult.toString();

                            sentencia = new StringBuilder("SELECT t3.nomge FROM ngbas001t t, ngbas009t t2, ngbas008t t3 where t.coddp=t2.coddp and t2.codge=t3.codge and t.codpe=").append(sobretiempo.getCodpe()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("' ").append(" and t2.codcm='").append(ejbFacade.getCodem()).append("'").append(" and t3.codcm='").append(ejbFacade.getCodem()).append("'");
                            singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            gerencia = "" + singleResult.toString();

                            java.sql.Connection conexion = null;
                            conexion = new Conexion().getConexion();

                            Trabajador trab = new Trabajador(conexion, ejbFacadeS.getCusua(), ejbFacadeS.getCodem());
                            trab.setCodpe(String.valueOf(solicitanteImagen.getCodpe()));
                            trab.setConpe(String.valueOf(solicitanteImagen.getConpe()));
                            trab.setCodcp(String.valueOf(solicitanteImagen.getCodcp()));

                            condicion = trab.SituacionTrababjador(trab);

                            if (!conexion.isClosed()) {
                                conexion.commit();
                                conexion.close();
                            }

                            ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();
                            HttpSession sesion = (HttpSession) ex.getSession(true);

                            if (sobretiempo.getCasbt002d().getCodtst() == 1) {
                                tipo = "Remunerado";
                            } else {
                                tipo = "No Remunerado";
                            }
                            sentencia = new StringBuilder("SELECT t.desmo FROM camot001m t where t.codmo=").append(sobretiempo.getCodmo()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("'");
                            singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            motivo = "" + singleResult.toString();

                            sentencia = new StringBuilder("SELECT t.desmo FROM camot001m t where t.codmo=").append(sobretiempo.getCodac()).append(" and t.codcm='").append(ejbFacade.getCodem()).append("'");
                            singleResult = ejbFacadeS.getEm().createNativeQuery(sentencia.toString()).setMaxResults(1).getSingleResult();
                            actividad = "" + singleResult.toString();
                        }
                    } else {
                        estatusFormato = false;
                    }
                }

                if (!sinPermisos) {
                    if (gestion.compareTo("Procesar") == 0) {
                        if (ausencia) {
                            if (ngvar002t != null) {
                                estatusFormato = procesarAusencia();
                            }
                        } else {
                            if (sobretiempo != null) {
                                procesarSobretiempo = procesarSobretiempo();
                                if (procesarSobretiempo == 0) {
                                    estatusFormato = false;
                                } else {
                                    estatusFormato = true;
                                }
                            }
                        }
                    } else if (gestion.compareTo("Rechazar") == 0) {
                        if (ausencia) {
                            estatusFormato = rechazarAusencia();
                        } else {
                            if (sobretiempo != null) {
                                estatusFormato = rechazarSobretiempo();
                            }
                        }
                    }
                    currentAgregar.setCodba("");
                    if (!estatusFormato) {
                        currentAgregar.setCodba("");
                        RequestContext context = RequestContext.getCurrentInstance();
                        //execute javascript oncomplete                
                        context.execute("PF('formatoInvalido').show();");
                    } else {
                        if (procesarSobretiempo == 2) {
                            System.out.println("debo mostrar un nuevo alert, con el nuevo periodo para el calculo. etc");
                            RequestContext context = RequestContext.getCurrentInstance();
                            //execute javascript oncomplete
                            context.execute("PF('procesarTardiaPrecalculada').show();");
                        } else {
                            mostrar = true;
                        }
                    }
                }
            } else {
                currentAgregar.setCodba("");
                RequestContext context = RequestContext.getCurrentInstance();
                //execute javascript oncomplete
                context.execute("PF('formatoInvalido').show();");
            }

        } catch (Exception e) {
            System.out.println(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
            Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('formatoInvalido').show();");
        }
    }

    /**
     * Método para procesar las planillas en caso de ser Sobretiempos.
     *
     * @return 0: en caso de formato no válido, 1: En caso de encontrar un formáto valido, 2: Para el caso de tardía
     *
     * @throws Exception Maneja el error de UserTransaction
     */
    public int procesarSobretiempo() throws Exception {
        UserTransaction transaction = null;
        transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        periodo = "";
        try {
            //Procesar 
            if (sobretiempo.getNgvar006t().getCoded() == 2 && sobretiempo.getOrgst() != 'T' && !casoRevertir) {
                System.out.println(" " + transaction.getStatus());
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                sobretiempo.getNgvar006t().setCoded(1);

                //actualizar datos
                sobretiempo.setLogpa(ejbFacadeS.getCusua());
                sobretiempo.setFhast(ejbFacadeS.getCurrentDateTime());
                getEjbFacadeS().edit(sobretiempo);

                transaction.commit();
                System.out.println(" " + transaction.getStatus());
                mensaje = "Incidencia Procesada Con Éxito";
                //Agregado para solventar las incidencias duplicadas.
                agregarDuplicadas();
                return 1;
            } //Procesar Tardía
            else if (sobretiempo.getNgvar006t().getCoded() == 2 && sobretiempo.getOrgst() == 'T' && !casoRevertir) {
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                sobretiempo.getNgvar006t().setCoded(1);
                //Actualizar Datos
                sobretiempo.setLogpa(ejbFacadeS.getCusua());
                sobretiempo.setFhast(ejbFacadeS.getCurrentDateTime());
                getEjbFacadeS().edit(sobretiempo);
                transaction.commit();

                histSobretiempo = ejbFacadeSH.obtenerHistorico(codigo, codModulo);

                if (histSobretiempo != null) {
                    if (sobretiempo.getCasbt002d().getCodtst() == 1 && ((histSobretiempo.getOrgst() == 'A'
                            || histSobretiempo.getOrgst() == 'S') && histSobretiempo.getCasbt002d().getCodtst() == 2)) {

                        java.sql.Connection conexion = null;
                        conexion = new Conexion().getConexion();

                        Ngbas001x trabajador = ngbas001xFacade.find(sobretiempo.getCodpe());
                        //Mostrar Advertencia.. 
                        Trabajador trab = new Trabajador(conexion, ejbFacadeS.getCusua(), ejbFacadeS.getCodem());
                        trab.setCodpe(String.valueOf(trabajador.getCodpe()));
                        trab.setConpe(String.valueOf(trabajador.getConpe()));
                        trab.setCodcp(String.valueOf(trabajador.getCodcp()));
                        String[] ProximoPeriodo = trab.ProximoPeriodo(trabajador.getCodpe());
                        String[] fechas = ProximoPeriodo[2].split(" ");
                        int horas = (int) (jbvarios.diferenciaMin(jbvarios.fechaToString(sobretiempo.getFhini(), "dd/MM/yyyy"), jbvarios.fechaToString(sobretiempo.getFhfin(), "dd/MM/yyyy"), "dd/MM/yyyy") / 60);
                        int dias = horas / 24;
                        String SumarFecha = "";
                        if (ProximoPeriodo[1].compareTo("D") == 0) {
                            //No hay inconveniente, realizo la modificación con las fechas del proximo periodo que me dan..

                            sobretiempo.setFeini(jbvarios.stringToFecha(fechas[0], "dd/MM/yyyy"));
                            if (dias == 0) {
                                sobretiempo.setFefin(jbvarios.stringToFecha(fechas[0], "dd/MM/yyyy"));
                                periodo = fechas[0] + " - " + fechas[0];
                            } else {
                                SumarFecha = jbvarios.SumarFecha(fechas[0], dias, "dd/MM/yyyy");
                                sobretiempo.setFefin(jbvarios.stringToFecha(SumarFecha, "dd/MM/yyyy"));
                                periodo = fechas[0] + " - " + SumarFecha;
                            }
                            getEjbFacadeS().edit(sobretiempo);
                            transaction.commit();
                            mensaje = "Incidencia Procesada Con Éxito";
                            msjReintegro = "El sobre tiempo será reintegrado en el período de nómina " + periodo;
                            diahora = jbvarios.formatearFecha(histSobretiempo.getFhast(), "dd/MM/yyyy HH:mm:ss");

                            RequestContext context = RequestContext.getCurrentInstance();
                            //execute javascript oncomplete
                            context.execute("PF('procesarTardia').show();");
                            //Agregado para solventar las incidencias duplicadas.
                            agregarDuplicadas();
                            return 1;
                        } else if (ProximoPeriodo[1].compareTo("P") == 0) {
                            //La nómina esta precalculandose, se debe preguntar si se desea realizar en ese periodo que se está precalculando
                            //O si se desea registrar para el próximo pase a nómina.
                            diahora = jbvarios.formatearFecha(histSobretiempo.getFhast(), "dd/MM/yyyy HH:mm:ss");
                            String[] PeriodoDisponible = trab.PeriodoDisponible(trabajador.getCodpe());
                            String[] fechasDisp = PeriodoDisponible[1].split(" ");
                            if (dias == 0) {
                                periodo = fechas[0] + " - " + fechas[0];
                                Speriodo = fechasDisp[0] + " - " + fechasDisp[0];
                            } else {
                                SumarFecha = jbvarios.SumarFecha(fechas[0], dias, "dd/MM/yyyy");
                                periodo = fechas[0] + " - " + SumarFecha;
                                SumarFecha = jbvarios.SumarFecha(fechasDisp[0], dias, "dd/MM/yyyy");
                                Speriodo = fechasDisp[0] + " - " + SumarFecha;
                            }

                            return 2;
                        }
                    }
                }
                mensaje = "Incidencia Procesada Con Éxito";
                return 1;
            } //Revertir
            else if (sobretiempo.getNgvar006t().getCoded() == 1 && sobretiempo.getOrgst() == 'A' && sobretiempo.getFepsn() == null) {
                diahora = jbvarios.formatearFecha(sobretiempo.getFhast(), "dd/MM/yyyy HH:mm:ss");
                autoriza = sobretiempo.getLogpa();
                RequestContext context = RequestContext.getCurrentInstance();
                //execute javascript oncomplete
                context.execute("PF('revertir').show();");
                return 1;
            } //Reconsiderar
            else if (sobretiempo.getNgvar006t().getCoded() == 3 && sobretiempo.getFepsn() == null && !casoRevertir) {
                diahora = jbvarios.formatearFecha(sobretiempo.getFhrst(), "dd/MM/yyyy HH:mm:ss");
                autoriza = sobretiempo.getLogpr();
                RequestContext context = RequestContext.getCurrentInstance();
                //execute javascript oncomplete
                context.execute("PF('confirmationP').show();");
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    logger.debug(e.getMessage());
                    transaction.rollback();
                } catch (Exception ex) {
                    logger.debug(e.getMessage());
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                    return 0;
                }
            }
        }
        return 0;
    }

    /**
     * Método para rechazar las planillas en caso de ser Sobretiempos
     *
     * @return True: En caso de encontrar un formáto valido, en caso contrario FALSE
     * @throws Exception Maneja el error de UserTransaction
     */
    public boolean rechazarSobretiempo() throws Exception {
        //Rechaza
        if (sobretiempo.getNgvar006t().getCoded() == 2) {
            rechazo = true;
            return true;
        } //Rechaza luego de haber procesado
        else if (sobretiempo.getNgvar006t().getCoded() == 1 && sobretiempo.getFepsn() == null) {
            diahora = jbvarios.formatearFecha(sobretiempo.getFhast(), "dd/MM/yyyy HH:mm:ss");
            autoriza = sobretiempo.getLogpa();
            RequestContext context = RequestContext.getCurrentInstance();
            //execute javascript oncomplete
            context.execute("PF('confirmation').show();");
            return true;
        } else {
            return false;
        }

    }

    /**
     * Método para procesar las planillas en caso de ser Ausencias.
     *
     * @return True: En caso de encontrar un formáto valido, en caso contrario FALSE
     * @throws Exception Maneja el error de UserTransaction
     */
    public boolean procesarAusencia() throws Exception {

        UserTransaction transaction = null;
        transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");

        try {
            //Procesar 
            if (ngvar002t.getNgvar006t().getCoded() == 2 && ngvar002t.getOrgpm() != 'T' && !casoRevertir) {
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }

                ngvar002t.getNgvar006t().setCoded(1);
                //actualizar datos
                ngvar002t.setLogpa(ejbFacadeA.getCusua());
                ngvar002t.setFhaut(ejbFacadeA.getCurrentDateTime());
                getEjbFacadeA().edit(ngvar002t);
                transaction.commit();
                mensaje = "Incidencia Procesada Con Éxito";
                //Agregado para solventar las incidencias duplicadas.
                agregarDuplicadas();
                return true;
            } //Procesar Tardía
            else if (ngvar002t.getNgvar006t().getCoded() == 2 && ngvar002t.getOrgpm() == 'T' && !casoRevertir) {
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                ngvar002t.getNgvar006t().setCoded(1);
                //Actualizar Datos
                ngvar002t.setLogpa(ejbFacadeA.getCusua());
                ngvar002t.setFhaut(ejbFacadeA.getCurrentDateTime());
                getEjbFacadeA().edit(ngvar002t);
                transaction.commit();

                String fecha_aut = ejbFacadeAH.obtenerHistorico(codigo, codModulo, ejbFacade.getCodem());
                if (tipo.compareTo("Remunerado") == 0 && fecha_aut != null) {
                    diahora = fecha_aut;
                    autoriza = ngvar002t.getLogpa();
                    RequestContext context = RequestContext.getCurrentInstance();
                    //execute javascript oncomplete
                    context.execute("PF('procesarTardia').show();");
                    if (transaction.getStatus() == 6) {
                        transaction.begin();
                    }
                    ngvar002t.setReipm('S');
                    getEjbFacadeA().edit(ngvar002t);
                    transaction.commit();
                    msjReintegro = "La ausencia será reintregada en el próximo cálculo de nómina salarial";
                }
                if (tipo.compareTo("No Remunerado") == 0) {
                    if (transaction.getStatus() == 6) {
                        transaction.begin();
                    }
                    ngvar002t.setReipm('N');
                    getEjbFacadeA().edit(ngvar002t);
                    transaction.commit();
                }
                mensaje = "Incidencia Procesada Con Éxito";
                //Agregado para solventar las incidencias duplicadas.
                agregarDuplicadas();
                return true;
            } //Revertir
            else if (ngvar002t.getNgvar006t().getCoded() == 1 && ngvar002t.getOrgpm() == 'A' && ngvar002t.getFepsn() == null) {
                diahora = jbvarios.formatearFecha(ngvar002t.getFhaut(), "dd/MM/yyyy HH:mm:ss");
                autoriza = ngvar002t.getLogpa();
                RequestContext context = RequestContext.getCurrentInstance();
                //execute javascript oncomplete
                context.execute("PF('revertir').show();");
                return true;
            }//Reconsiderar
            else if (ngvar002t.getNgvar006t().getCoded() == 3 && ngvar002t.getFepsn() == null && !casoRevertir) {

                diahora = jbvarios.formatearFecha(ngvar002t.getFhrpm(), "dd/MM/yyyy HH:mm:ss");
                autoriza = ngvar002t.getLogpr();
                RequestContext context = RequestContext.getCurrentInstance();
                //execute javascript oncomplete
                context.execute("PF('confirmationP').show();");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception ex) {
                    logger.debug(e.getMessage());
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                    return false;
                }
            }

        }

        return false;
    }

    /**
     * Método para rechazar las planillas en caso de ser Ausencias
     *
     * @return True: En caso de encontrar un formáto valido, en caso contrario FALSE
     * @throws Exception Maneja el error de UserTransaction
     */
    public boolean rechazarAusencia() {

        if (ngvar002t.getNgvar006t().getCoded() == 2) {
            rechazo = true;
            return true;
        } else if (ngvar002t.getNgvar006t().getCoded() == 1 && ngvar002t.getFepsn() == null) {
            diahora = jbvarios.formatearFecha(ngvar002t.getFhaut(), "dd/MM/yyyy HH:mm:ss");
            autoriza = ngvar002t.getLogpa();
            RequestContext context = RequestContext.getCurrentInstance();
            //execute javascript oncomplete
            context.execute("PF('confirmation').show();");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método que ejecuta la acción del boton para guardar el tipo y el motivo del rechazo de una incidencia, y envía un correo.
     */
    public void guardoRechazo() {
        rechazo = false;
        UserTransaction transaction = null;
        String ccAddress = "";
        try {

            transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
            //AQUI
            String toAddress = "psistemas@paisa.com.ve";
            if (!ausencia) {
                String correo_jefe = ngbas001xFacade.find(ngbas001xFacade.find(sobretiempo.getCodpe()).getSuppe()).getCorpe();

                Date fhini = jbvarios.stringToFecha(jbvarios.formatearFecha(sobretiempo.getFhini(), "dd/MM/yyyy"), "dd/MM/yyyy");

                Map<Object, Object> parametros = new HashMap<Object, Object>();
                parametros.put("1", fhini);
                parametros.put("2", ejbFacade.getCodem());
                String correoTercero = ejbFacade.buscarTercero(parametros, codModulo, sobretiempo.getCodpe());
                if (correoTercero != null) {
                    ccAddress = correoTercero;
                }
            } else {
                String correo_jefe = ngbas001xFacade.find(ngbas001xFacade.find(ngvar002t.getNgvar002tPK().getCodpe()).getSuppe()).getCorpe();

                Date fhini = jbvarios.stringToFecha(jbvarios.formatearFecha(ngvar002t.getFdehi(), "dd/MM/yyyy"), "dd/MM/yyyy");

                Map<Object, Object> parametros = new HashMap<Object, Object>();
                parametros.put("1", fhini);
                parametros.put("2", ngvar002t.getNgvar002tPK().getCodcm());
                String correoTercero = ejbFacade.buscarTercero(parametros, codModulo, ngvar002t.getNgvar002tPK().getCodpe());
                if (correoTercero != null) {
                    ccAddress = correoTercero;
                }

            }

            if (!ausencia) {
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                if (revertir) {
                    Casbt001l histAnterior = ejbFacadeSH.obtenerHistoricoAnterior(codigo, codModulo, sobretiempo.getCodcm());
                    if (histAnterior != null) {
                        if (histAnterior.getCasbt002d().getCodtst() != null) {
                            sobretiempo.getCasbt002d().setCodtst(histAnterior.getCasbt002d().getCodtst());
                        }
                        if (histAnterior.getCodmo() != null) {
                            sobretiempo.setCodmo(histAnterior.getCodmo());
                        }
                        if (histAnterior.getCodac() != null) {
                            sobretiempo.setCodac(histAnterior.getCodac());
                        }
                        if (histAnterior.getOrgst() != ' ') {
                            sobretiempo.setOrgst(histAnterior.getOrgst());
                        }
                        if (histAnterior.getFhmod() != null) {
                            sobretiempo.setFhmod(histAnterior.getFhmod());
                            //Actualizar Código de barras
                            String codbaNuevo;

                            String codigoNuevo;
                            String agregoFaltante = "";
                            codigoNuevo = String.valueOf(codigo);
                            int tamanio = codigoNuevo.length();
                            int ciclo = 7 - tamanio;
                            if (ciclo > 0) {
                                for (int i = 0; i < ciclo; i++) {
                                    agregoFaltante = agregoFaltante + "0";
                                }
                            }
                            codigoNuevo = agregoFaltante + String.valueOf(codigo);
                            String fhmd = jbvarios.fechaToString(histAnterior.getFhmod(), "dd/MM/yyyy HH:mm:ss");
                            String splitFhmd[] = fhmd.split(" ");
                            String splitFecha[] = splitFhmd[0].split("/");
                            String splitHoras[] = splitFhmd[1].split(":");
                            String Fecha = splitFecha[0] + splitFecha[1] + splitFecha[2];
                            String Horas = splitHoras[0] + splitHoras[1] + splitHoras[2];
                            codbaNuevo = tipoInc + compania + codigoNuevo + Fecha + Horas;
                            System.out.println("TAMANIO " + codbaNuevo.length());
                            sobretiempo.setCodba(codbaNuevo);
                        }
                    }
                }
                sobretiempo.getNgvar006t().setCoded(3);

                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                String returnToUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
                        + context.getApplication().getViewHandler().getActionURL(context, "/login/index.xhtml");

                String idIncidencia = com.fenix.control.fenixTools.mail.Cripto.encripta(String.valueOf(sobretiempo.getCodst()));
                String tipoIncidencia = com.fenix.control.fenixTools.mail.Cripto.encripta(String.valueOf("S"));

                enviarCorreo("Sobretiempo", toAddress, ccAddress, true, returnToUrl, idIncidencia, tipoIncidencia);
                //Actualizar Datos
                sobretiempo.setLogpa(null);
                sobretiempo.setFhast(null);
                //getEjbFacadeS().edit(sobretiempo);
                sobretiempo.setLogpr(ejbFacadeS.getCusua());
                sobretiempo.setFhrst(ejbFacadeS.getCurrentDateTime()); //Fecha Rechazo            
                sobretiempo.setCamot002m(currentAgregar.getCamot002m());
                sobretiempo.setMorst(motivoRechazo);
                getEjbFacadeS().edit(sobretiempo);
                transaction.commit();

                mensaje = "Incidencia Rechazada Con Éxito";
                msjTipoRechazo = currentAgregar.getCamot002m().getDesmr();
                msjMotivoRechazo = motivoRechazo;
            } else {

                if (revertir) {
                    String separador[];
                    String sentencia;
                    String codpm = ejbFacadeAH.obtenerHistoricoCodmp(codigo, codModulo, ngvar002t.getNgvar002tPK().getCodcm());
                    if (transaction.getStatus() == 6) {
                        transaction.begin();
                    }
                    if (codpm != null) {
                        sentencia = ejbFacadeAH.obtenerHistoricoAnterior(codigo, codModulo, ngvar002t.getNgvar002tPK().getCodcm(), Integer.parseInt(codpm));
                        separador = sentencia.split("\\*\\*");
                        if (separador[1].compareTo("vacio") != 0) {
                            ngvar002t.setTippm(separador[1].trim());
                        }
                        if (separador[2].compareTo("vacio") != 0) {
                            ngvar002t.getNgvar001tPK().setCodmp(Integer.parseInt(separador[2].trim()));
                        }
                        if (separador[3].compareTo("vacio") != 0) {
                            ngvar002t.setOrgpm(separador[3].trim().charAt(0));
                        }
                        if (separador[4].compareTo("vacio") != 0) {
                            //2013-06-26 15:11:15
                            String fecha[] = separador[4].split(" ");
                            String dia[] = fecha[1].split("-");
                            SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String strFecha = dia[2] + "/" + dia[1] + "/" + dia[0] + " " + fecha[2];

                            Date fhmod = formatoDelTexto.parse(strFecha);
                            fhmod = jbvarios.stringToFecha(jbvarios.formatearFecha(fhmod, "dd/MM/yyyy HH:mm:ss"), "dd/MM/yyyy HH:mm:ss");
                            ngvar002t.setFhmod(fhmod);

                            //Actualizar Código de barras
                            String codbaNuevo;
                            String codigoNuevo;
                            String agregoFaltante = "";
                            codigoNuevo = String.valueOf(codigo);
                            int tamanio = codigoNuevo.length();
                            int ciclo = 7 - tamanio;
                            if (ciclo > 0) {
                                for (int i = 0; i < ciclo; i++) {
                                    agregoFaltante = agregoFaltante + "0";
                                }
                            }
                            codigoNuevo = agregoFaltante + String.valueOf(codigo);
                            String Fecha = dia[2] + dia[1] + dia[0];
                            String Horas[] = fecha[2].split(":");
                            codbaNuevo = tipoInc + compania + codigoNuevo + Fecha + Horas[0] + Horas[1] + Horas[2];
                            System.out.println("TAMANIO " + codbaNuevo.length());
                            ngvar002t.setCodba(codbaNuevo);
                        }
                    }

                    getEjbFacadeA().edit(ngvar002t);
                    transaction.commit();
                }

                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                ngvar002t.setLogpa(null);
                ngvar002t.setFhaut(null);

                ngvar002t.getNgvar006t().setCoded(3);

                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                String returnToUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
                        + context.getApplication().getViewHandler().getActionURL(context, "/login/index.xhtml");

                String idIncidencia = com.fenix.control.fenixTools.mail.Cripto.encripta(String.valueOf(ngvar002t.getNgvar002tPK().getIdepm()));
                String tipoIncidencia = com.fenix.control.fenixTools.mail.Cripto.encripta(String.valueOf("A"));

                enviarCorreo("Ausencia", toAddress, ccAddress, false, returnToUrl, idIncidencia, tipoIncidencia);

                //Actualizar Datos
                ngvar002t.setLogpr(ejbFacadeA.getCusua());
                ngvar002t.setFhrpm(ejbFacadeA.getCurrentDateTime()); //Fecha Rechazo            
                ngvar002t.setCamot002m(currentAgregar.getCamot002m());
                ngvar002t.setMorpm(motivoRechazo);
                getEjbFacadeA().edit(ngvar002t);
                transaction.commit();

                msjTipoRechazo = currentAgregar.getCamot002m().getDesmr();
                msjMotivoRechazo = motivoRechazo;
                mensaje = "Incidencia Rechazada Con Éxito";

            }
            //Agregado para solventar las incidencias duplicadas.
            agregarDuplicadas();
            tipoRechazo = "";
            motivoRechazo = "";
            revertir = false;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                } catch (Exception ex) {
                    logger.debug(e.getMessage());
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));

                }
            }
        }

    }

    /**
     * Método que ejecuta la acción del boton "Sí" a la pregunta: Esta incidencia fue procesada previamente el dd/mm/yyyy por el usuario "logpe", ¿desea
     * cambiar su estatus a rechazada?
     */
    public void siProcesadaRechaza() {
        rechazo = true;
    }

    /**
     * Método que se ejecuta al "Periodo PRECALCULADO" al procesar una incidencia tardia.
     *
     * @throws Exception Error en el manejo de la transacción
     */
    public void periodoActual() throws Exception {
        UserTransaction transaction = null;
        transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        try {
            if (transaction.getStatus() == 6) {
                transaction.begin();
            }

            sobretiempo.setFeini(jbvarios.stringToFecha(periodo.split(" - ")[0], "dd/MM/yyyy"));
            sobretiempo.setFefin(jbvarios.stringToFecha(periodo.split(" - ")[1], "dd/MM/yyyy"));
            msjReintegro = "El sobre tiempo será reintegrado en el período de nómina " + periodo;
            getEjbFacadeS().edit(sobretiempo);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                try {
                    logger.debug(e.getMessage());
                    transaction.rollback();
                } catch (Exception ex) {
                    logger.debug(e.getMessage());
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                }
            }
        }
        mensaje = "Incidencia Procesada Con Éxito";
        mostrar = true;
        //Agregado para solventar las incidencias duplicadas.
        agregarDuplicadas();
    }

    /**
     * Método que se ejecuta al "SIGUIENTE Periodo" al procesar una incidencia tardia.
     */
    public void periodoSiguiente() throws Exception {
        UserTransaction transaction = null;
        transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        try {
            if (transaction.getStatus() == 6) {
                transaction.begin();
            }

            sobretiempo.setFeini(jbvarios.stringToFecha(Speriodo.split(" - ")[0], "dd/MM/yyyy"));
            sobretiempo.setFefin(jbvarios.stringToFecha(Speriodo.split(" - ")[1], "dd/MM/yyyy"));
            msjReintegro = "El sobre tiempo será reintegrado en el período de nómina " + Speriodo;
            getEjbFacadeS().edit(sobretiempo);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                try {
                    logger.debug(e.getMessage());
                    transaction.rollback();
                } catch (Exception ex) {
                    logger.debug(e.getMessage());
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                }
            }
        }
        mensaje = "Incidencia Procesada Con Éxito";
        mostrar = true;
        //Agregado para solventar las incidencias duplicadas.
        agregarDuplicadas();
    }

    /**
     * Método que ejecuta la acción del boton "Sí" a la pregunta: Esta incidencia fue rechazada previamente el dd/mm/yyyy por el usuario "logpe", ¿desea
     * cambiar su estatus a procesada?
     */
    public void siRechazadaProcesa() {

        UserTransaction transaction = null;
        try {
            if (!ausencia) {
                transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                sobretiempo.getNgvar006t().setCoded(1);
                //actualizar datos
                sobretiempo.setLogpa(ejbFacadeS.getCusua());
                sobretiempo.setFhast(ejbFacadeS.getCurrentDateTime());
                //campos que se haran null
                sobretiempo.setLogpr(null);
                sobretiempo.setFhrst(null);
                sobretiempo.setCamot002m(null);
                sobretiempo.setMorst(null);

                getEjbFacadeS().edit(sobretiempo);
                transaction.commit();
                mensaje = "Incidencia Reconsiderada Con Éxito";
            } else {
                transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                ngvar002t.getNgvar006t().setCoded(1);
                //actualizar datos
                ngvar002t.setLogpa(ejbFacadeA.getCusua());
                ngvar002t.setFhaut(ejbFacadeA.getCurrentDateTime());
                //campos que se haran null
                ngvar002t.setLogpr(null);
                ngvar002t.setFhrpm(null);
                ngvar002t.setCamot002m(null);
                ngvar002t.setMorpm(null);

                getEjbFacadeA().edit(ngvar002t);
                transaction.commit();
                mensaje = "Incidencia Reconsiderada Con Éxito";
            }

            //Agregado para solventar las incidencias duplicadas.
            agregarDuplicadas();
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

    /**
     * Método que ejecuta la acción del boton "No" para CreateCasbt001mRechazaProcesa o CreateCasbt001mProcesaRechaza, al no realizar ningun cambio,
     * muestra la etiqueta "Sin cambios" ya que no se llevó ninguno a cabo.
     */
    public void sinCambios() {
        mensaje = "Sin cambios";
    }

    /**
     * Método que valida que el Msj a mostrar en el label, no esté vacío, si esta vacío no lo muestra. (Mensaje de aprobación o rechazo)
     *
     * @return true si mensaje tiene contenido, false en sentido contrario.
     */
    public boolean validarMensaje() {
        if (mensaje.compareTo("") == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Método que valida que el Msj a mostrar en el label, no esté vacío, si esta vacío no lo muestra. (Mensaje de periodos)
     *
     * @return true si mensaje tiene contenido, false en sentido contrario.
     */
    public boolean validarMensajeAzul() {
        if (mensajeAzul.compareTo("") == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Método que determina que el motivo del rechazo no este vacío, de esta manera muestra el motivo del rechazo cuando la incidencia qes procesada
     *
     * @return True si el mensaje del motivo del rechazo tiene un contenido, false si tiene un valor
     */
    public boolean validarRechazo() {
        if (msjMotivoRechazo.compareTo("") == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Método que determina que el mensaje de Reintegro no esté vacío, de esta manera muestra el periodo de reintegro cuando la incidencia qes procesada
     *
     * @return True si el mensaje del motivo del rechazo tiene un contenido, false si tiene un valor
     */
    public boolean validarReintegro() {
        if (msjReintegro.compareTo("") == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Método que ejecuta la acción del boton "Aprobar" a la pregunta: Esta por revertir una incidencia que había sido procesada de Forma Automática por TH
     * el día dd/mm/yyyy por el usuario "logpe", ¿Desea Aprobar o Rechazar esta incidencia?
     */
    public void revertirAprobar() {

        UserTransaction transaction = null;
        try {

            if (!ausencia) {
                transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                Casbt001l histAnterior = ejbFacadeSH.obtenerHistoricoAnterior(codigo, codModulo, sobretiempo.getCodcm());
                if (histAnterior != null) {
                    if (histAnterior.getCasbt002d().getCodtst() != null) {
                        sobretiempo.getCasbt002d().setCodtst(histAnterior.getCasbt002d().getCodtst());
                    }
                    if (histAnterior.getCodmo() != null) {
                        sobretiempo.setCodmo(histAnterior.getCodmo());
                    }
                    if (histAnterior.getCodac() != null) {
                        sobretiempo.setCodac(histAnterior.getCodac());
                    }
                    if (histAnterior.getOrgst() != ' ') {
                        sobretiempo.setOrgst(histAnterior.getOrgst());
                    }
                    if (histAnterior.getFhmod() != null) {
                        sobretiempo.setFhmod(histAnterior.getFhmod());
                        //Actualizar Código de barras
                        String codbaNuevo;

                        String codigoNuevo;
                        String agregoFaltante = "";
                        codigoNuevo = String.valueOf(codigo);
                        int tamanio = codigoNuevo.length();
                        int ciclo = 7 - tamanio;
                        if (ciclo > 0) {
                            for (int i = 0; i < ciclo; i++) {
                                agregoFaltante = agregoFaltante + "0";
                            }
                        }
                        codigoNuevo = agregoFaltante + String.valueOf(codigo);
                        String fhmd = jbvarios.fechaToString(histAnterior.getFhmod(), "dd/MM/yyyy HH:mm:ss");
                        String splitFhmd[] = fhmd.split(" ");
                        String splitFecha[] = splitFhmd[0].split("/");
                        String splitHoras[] = splitFhmd[1].split(":");
                        String Fecha = splitFecha[0] + splitFecha[1] + splitFecha[2];
                        String Horas = splitHoras[0] + splitHoras[1] + splitHoras[2];
                        codbaNuevo = tipoInc + compania + codigoNuevo + Fecha + Horas;
                        System.out.println("TAMANIO " + codbaNuevo.length());
                        sobretiempo.setCodba(codbaNuevo);
                    }
                }
                //actualizar datos
                sobretiempo.getNgvar006t().setCoded(1);
                sobretiempo.setLogpa(ejbFacadeS.getCusua());
                sobretiempo.setFhast(ejbFacadeS.getCurrentDateTime());
                getEjbFacadeS().edit(sobretiempo);
                transaction.commit();
                mensaje = "Incidencia Revertida Con Éxito";
                revertir = false;
            } else {
                String separador[];
                String sentencia;
                String codpm = ejbFacadeAH.obtenerHistoricoCodmp(codigo, codModulo, ngvar002t.getNgvar002tPK().getCodcm());
                transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                if (transaction.getStatus() == 6) {
                    transaction.begin();
                }
                if (codpm != null) {
                    sentencia = ejbFacadeAH.obtenerHistoricoAnterior(codigo, codModulo, ngvar002t.getNgvar002tPK().getCodcm(), Integer.parseInt(codpm));
                    separador = sentencia.split("\\*\\*");
                    if (separador[1].compareTo("vacio") != 0) {
                        ngvar002t.setTippm(separador[1].trim());
                    }
                    if (separador[2].compareTo("vacio") != 0) {
                        ngvar002t.getNgvar001tPK().setCodmp(Integer.parseInt(separador[2].trim()));
                    }
                    if (separador[3].compareTo("vacio") != 0) {
                        ngvar002t.setOrgpm(separador[3].trim().charAt(0));
                    }
                    if (separador[4].compareTo("vacio") != 0) {
                        String fecha[] = separador[4].split(" ");
                        String dia[] = fecha[1].split("-");
                        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String strFecha = dia[2] + "/" + dia[1] + "/" + dia[0] + " " + fecha[2];

                        Date fhmod = formatoDelTexto.parse(strFecha);
                        fhmod = jbvarios.stringToFecha(jbvarios.formatearFecha(fhmod, "dd/MM/yyyy HH:mm:ss"), "dd/MM/yyyy HH:mm:ss");
                        ngvar002t.setFhmod(fhmod);

                        //Actualizar Código de barras
                        String codbaNuevo;
                        String codigoNuevo;
                        String agregoFaltante = "";
                        codigoNuevo = String.valueOf(codigo);
                        int tamanio = codigoNuevo.length();
                        int ciclo = 7 - tamanio;
                        if (ciclo > 0) {
                            for (int i = 0; i < ciclo; i++) {
                                agregoFaltante = agregoFaltante + "0";
                            }
                        }
                        codigoNuevo = agregoFaltante + String.valueOf(codigo);
                        String Fecha = dia[2] + dia[1] + dia[0];
                        String Horas[] = fecha[2].split(":");
                        codbaNuevo = tipoInc + compania + codigoNuevo + Fecha + Horas[0] + Horas[1] + Horas[2];
                        System.out.println("TAMANIO " + codbaNuevo.length());
                        ngvar002t.setCodba(codbaNuevo);
                    }
                }

                //actualizar datos
                ngvar002t.getNgvar006t().setCoded(1);
                ngvar002t.setLogpa(ejbFacadeA.getCusua());
                ngvar002t.setFhaut(ejbFacadeA.getCurrentDateTime());
                getEjbFacadeA().edit(ngvar002t);
                transaction.commit();
                mensaje = "Incidencia Revertida Con Éxito";
            }

            //Agregado para solventar las incidencias duplicadas.
            agregarDuplicadas();

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

    /**
     * Método que ejecuta la acción del boton "Rechazar" a la pregunta: Esta por revertir una incidencia que había sido procesada de Forma Automática por
     * TH el día dd/mm/yyyy por el usuario "logpe", ¿Desea Aprobar o Rechazar esta incidencia?
     */
    public void revertirRechazar() {
        revertir = true;
        rechazo = true;
    }

    /**
     * Método para realizar el envío de correo cuando se rechazan las incidencias
     *
     * @param incidencia String que indica "Sobretiempo" o "Ausencia"
     * @param _toAddress String con el (los) correo(s) del destinatario
     * @param _ccAddress String que indíca el correo a quien se le quiere hacer copia
     * @param sobretiempo boolean-> True: Es un Sobretiempo, False: Es una ausencia
     * @param link String de la dirección del link a la que se hace referencia en el correo.
     * @throws Exception Maneja el error del envío del correo.
     */
    public void enviarCorreo(String incidencia, String _toAddress, String _ccAddress, boolean sobretiempo, String returnToUrl, String idIncidencia, String tipoIncidencia) throws Exception {
        Future<Boolean> enviarCorreoAsync;
        String bccAddress;
        String subject;
        StringBuilder body = new StringBuilder();

        bccAddress = "";

        subject = "Incidencia Rechazada (" + incidencia + ") "
                + "del Trabajador " + nombre + " de fecha " + jbvarios.formatearFecha(fedsd, "dd/MM/yyyy");

        if (sobretiempo) {
            incidencia = "el Sobretiempo";
        } else {
            incidencia = "la Ausencia";
        }

        body.append("<p></p>");
        body.append("<p lang='es-ES' align='JUSTIFY'><font face='Arial, sans-serif'><font size='2'><strong>Estimado</strong>:</font></font></p>");
        body.append("<br lang='es-ES' align='JUSTIFY'><font face='Arial, sans-serif'><font size='2'>&nbsp; &nbsp; &nbsp;El &aacute;rea de Talento Humano le informa que ").append(incidencia).append(" perteneciente al trabajador '<em><u>").append(nombre).append("'</u></em>, portador de la c&eacute;dula '<em><u>").append(cedula).append("'</u></em>, ocupante del cargo '<em><u>").append(cargo).append("</u></em>, correspondiente a:</font></font>");
        body.append("<br/>");
        body.append("<br/>");

        body.append("<table style='height: 179px;'>");
        body.append("<tbody>");
        body.append("<tr>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'><strong>Fecha desde:  </strong></font></font></td>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'>").append(jbvarios.formatearFecha(fedsd, "dd/MM/yyyy")).append("</font></font></td>");
        body.append("</tr>");
        body.append("<tr>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'><strong>Fecha hasta:  </strong></font></font></td>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'>").append(jbvarios.formatearFecha(fehst, "dd/MM/yyyy")).append("</font></font></td>");
        body.append("</tr>");
        if (shini != null && shrfin != null) {
            body.append("<tr>");
            body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'><strong>Hora de Inicio:  </strong></font></font></td>");
            body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'>").append(shini).append("</font></font></td>");
            body.append("</tr>");
            body.append("<tr>");
            body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'><strong>Hora Fin:  </strong></font></font></td>");
            body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'>").append(shrfin).append("</font></font></td>");
            body.append("</tr>");
        }
        body.append("<tr>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'><strong>Tipo:  </strong></font></font></td>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'>").append(tipo).append("</font></font></td>");
        body.append("</tr>");
        body.append("<tr>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'><strong>Motivo:  </strong></font></font></td>");
        body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'>").append(motivo).append("</font></font></td>");
        body.append("</tr>");
        if (sobretiempo) {
            body.append("<tr>");
            body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'><strong>Actividad:  </strong></font></font></td>");
            body.append("<td style='text-align: left;'><font face='Arial, sans-serif'><font size='2'>").append(actividad).append("</font></font></td>");
            body.append("</tr>");
        }
        body.append("</tbody>");
        body.append("</table>");

        body.append("<br lang='es-ES' align='JUSTIFY'><font face='Arial, sans-serif'><font size='2'>&nbsp; &nbsp; &nbsp;Fue rechazada por '<em><u>").append(ejbFacade.getCusua()).append("'</u></em>&nbsp;por el siguiente motivo: </font></font>");
        body.append("<p lang='es-ES' align='JUSTIFY'><font face='Arial, sans-serif'><font size='2'><h4 style='text-align: center;'><strong>").append(motivoRechazo).append("</strong></h4></font></font></p>");

//        body.append("<p lang='es-ES' align='JUSTIFY'><font face='Arial, sans-serif'><font size='2'>&nbsp; &nbsp; &nbsp;Debe ingresar a trav&eacute;s del siguiente '<a href='").append(returnToUrl).append("?ii=").append(idIncidencia).append("&ti=").append(tipoIncidencia).append("'").append(" target='_blank'>link</a>' y aplicar los cambios sugeridos, imprimir el formato, firmarlo por el supervisor inmediato y el trabajador y hacerlo llegar nuevamente al &aacute;rea de Talento Humano.</font></font></p>");
        body.append("<p lang='es-ES' style='text-align: center;' align='JUSTIFY'><font face='Arial, sans-serif'><font size='2'> Cualquier inquietud por favor gestionarlo con el &aacute;rea de n&oacute;mina y compensaci&oacute;n.</font></font></p>");
        body.append("<p>&nbsp;</p>");

        //Quien envía:
        correoFacade.setSmtpHost(tgpar002dFacade.consultarParametro("smtco").getValpa());
        correoFacade.setSmtpUser(tgpar002dFacade.consultarParametro("usecs").getValpa());
        correoFacade.setSmtpUserPass(tgpar002dFacade.consultarParametro("pasco").getValpa());
        correoFacade.setSmtpAutenticar(tgpar002dFacade.consultarParametro("autco").getValpa());
        correoFacade.setSmtpLocalHost("");
        correoFacade.setSmtpPort(tgpar002dFacade.consultarParametro("porco").getValpa());
        enviarCorreoAsync = correoFacade.enviarCorreo(_toAddress.toString(), _ccAddress.toString(), bccAddress.toString(), subject, true, body, true);
    }

    /**
     * Método para llenar el listado que mostrará las incidencias duplicadas.
     *
     * @param _ngvar002t Obj de ausencia (El padre).
     * @param _auDuplicadas Obj de ausencia (Los hijos).
     * @param _incidenciasDuplicadas Listado que será llenado para ser mostrado en la vista.
     */
    private void cargarListadoAusenciasDuplicadas(Ngvar002t _ngvar002t, List<Ngvar002t> _auDuplicadas, List<incidenciasDuplicadas> _incidenciasDuplicadas) throws Exception {

        if (_auDuplicadas != null) { //Existen ausencias duplicadas.

            UserTransaction transaction = null;
            transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");

            try {

                boolean existente = cadup001mFacade.buscarIncidencias(_ngvar002t.getNgvar002tPK().getIdepm(), "A", codModulo);

                if (!existente) {
                    if (transaction.getStatus() == 6) {
                        transaction.begin();
                    }

                    String hrini = "";
                    String hrfin = "";
                    String duracion = "";
                    //Calculando la duración.
                    if (_ngvar002t.getDurua() == 'd') {
                        int dif = jbvarios.FechaDif(jbvarios.fechaToString(_ngvar002t.getFhahi(), jbvarios.getFtoFhJav()), jbvarios.fechaToString(_ngvar002t.getFdehi(), jbvarios.getFtoFhJav()), jbvarios.getFtoFhJav()) + 1;
                        duracion = dif > 1 ? dif + " Días" : dif + " Día";
                    } else {
                        hrini = jbvarios.fechaToString(_ngvar002t.getHrini(), jbvarios.getFtoHora());
                        hrfin = jbvarios.fechaToString(_ngvar002t.getHrfin(), jbvarios.getFtoHora());
                        duracion = casbt001mFacade.calcularDuracion(_ngvar002t.getFdehi(), hrini, _ngvar002t.getFhahi(), hrfin);
                    }

                    Cadup001m dupPadre = new Cadup001m();
                    List<Cadup001m> hijos = new ArrayList<>();
                    Date fechaActual = ejbFacade.getCurrentDateTime();

                    dupPadre.setCodcm(ejbFacade.getCodem());
                    dupPadre.setCodpe(_ngvar002t.getNgvar002tPK().getCodpe());
                    dupPadre.setIdinc(_ngvar002t.getNgvar002tPK().getIdepm());
                    dupPadre.setTpinc('A');
                    Date ini = jbvarios.stringToFecha(jbvarios.fechaToString(_ngvar002t.getFdehi(), jbvarios.getFtoFhJav()) + " "
                            + (_ngvar002t.getHrini() != null ? jbvarios.fechaToString(_ngvar002t.getHrini(), jbvarios.getFtoHora()) : "00:00:00"),
                            jbvarios.getFtoFhJavExt());
                    Date fin = jbvarios.stringToFecha(jbvarios.fechaToString(_ngvar002t.getFhahi(), jbvarios.getFtoFhJav()) + " "
                            + (_ngvar002t.getHrfin() != null ? jbvarios.fechaToString(_ngvar002t.getHrfin(), jbvarios.getFtoHora()) : "00:00:00"),
                            jbvarios.getFtoFhJavExt());
                    dupPadre.setFhini(ini);
                    dupPadre.setFhfin(fin);

                    dupPadre.setDuinc(duracion);
                    dupPadre.setCusuc(ejbFacade.getCusua());
                    dupPadre.setFhcre(fechaActual);

                    cadup001mFacade.create(dupPadre);

                    for (int i = 0; i < _auDuplicadas.size(); i++) {
                        //Hijos
                        Cadup001m dupHijos = new Cadup001m();
                        dupHijos.setCodcm(ejbFacade.getCodem());
                        dupHijos.setCodpe(_auDuplicadas.get(i).getNgvar002tPK().getCodpe());
                        dupHijos.setIdinc(_auDuplicadas.get(i).getNgvar002tPK().getIdepm());
                        dupHijos.setTpinc('A');
                        ini = jbvarios.stringToFecha(jbvarios.fechaToString(_auDuplicadas.get(i).getFdehi(), jbvarios.getFtoFhJav()) + " "
                                + (_auDuplicadas.get(i).getHrini() != null ? jbvarios.fechaToString(_auDuplicadas.get(i).getHrini(), jbvarios.getFtoHora()) : "00:00:00"),
                                jbvarios.getFtoFhJavExt());
                        fin = jbvarios.stringToFecha(jbvarios.fechaToString(_auDuplicadas.get(i).getFhahi(), jbvarios.getFtoFhJav()) + " "
                                + (_auDuplicadas.get(i).getHrfin() != null ? jbvarios.fechaToString(_auDuplicadas.get(i).getHrfin(), jbvarios.getFtoHora()) : "00:00:00"),
                                jbvarios.getFtoFhJavExt());
                        dupHijos.setFhini(ini);
                        dupHijos.setFhfin(fin);

                        if (_auDuplicadas.get(i).getDurua() == 'd') {
                            int dif = jbvarios.FechaDif(jbvarios.fechaToString(_auDuplicadas.get(i).getFhahi(), jbvarios.getFtoFhJav()), jbvarios.fechaToString(_auDuplicadas.get(i).getFdehi(), jbvarios.getFtoFhJav()), jbvarios.getFtoFhJav()) + 1;
                            duracion = dif > 1 ? dif + " Días" : dif + " Día";
                        } else {
                            hrini = jbvarios.fechaToString(_auDuplicadas.get(i).getHrini(), jbvarios.getFtoHora());
                            hrfin = jbvarios.fechaToString(_auDuplicadas.get(i).getHrfin(), jbvarios.getFtoHora());
                            duracion = casbt001mFacade.calcularDuracion(_auDuplicadas.get(i).getFdehi(), hrini, _auDuplicadas.get(i).getFhahi(), hrfin);
                        }

                        dupHijos.setDuinc(duracion);
                        dupHijos.setCusuc(ejbFacade.getCusua());
                        dupHijos.setFhcre(fechaActual);
                        dupHijos.setIdpad(dupPadre.getIddup());
                        hijos.add(dupHijos);

                        cadup001mFacade.create(dupHijos);
                    }

                    incluirEnListado(dupPadre, hijos, _incidenciasDuplicadas);
                    //            imprimirDuplicados(_incidenciasDuplicadas);
                    transaction.commit();
                }

            } catch (Exception e) {
                if (transaction != null) {
                    try {
                        transaction.rollback();
                        logger.debug(e.getMessage());
                        Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("AutIncidenciaErrorDuplicado"));
                    } catch (Exception ex) {
                        logger.debug(e.getMessage());
                        Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("AutIncidenciaErrorDuplicado"));
                    }
                }
            }
        }
    }

    /**
     * Método para llenar el listado que mostrará las incidencias duplicadas.
     *
     * @param _sobretiempo Obj de sobretiempo (El padre).
     * @param _stDuplicados Obj de sobretiempo (Los hijos).
     * @param _incidenciasDuplicadas Listado que será llenado para ser mostrado en la vista.
     */
    private void cargarListadoSobretiemposDuplicados(Casbt001m _sobretiempo, List<Casbt001m> _stDuplicados, List<incidenciasDuplicadas> _incidenciasDuplicadas) throws Exception {

        if (_stDuplicados != null) { //Existen sobretiempos duplicados.

            UserTransaction transaction = null;
            transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");

            try {

                boolean existente = cadup001mFacade.buscarIncidencias(_sobretiempo.getCodst(), "S", codModulo);

                if (!existente) {
                    if (transaction.getStatus() == 6) {
                        transaction.begin();
                    }

                    Cadup001m dupPadre = new Cadup001m();
                    List<Cadup001m> hijos = new ArrayList<>();
                    Date fechaActual = ejbFacade.getCurrentDateTime();

                    dupPadre.setCodcm(ejbFacade.getCodem());
                    dupPadre.setCodpe(_sobretiempo.getCodpe());
                    dupPadre.setIdinc(_sobretiempo.getCodst());
                    dupPadre.setTpinc('S');
                    Date ini = jbvarios.stringToFecha(jbvarios.fechaToString(_sobretiempo.getFhini(), jbvarios.getFtoFhJav() + " "
                            + jbvarios.fechaToString(_sobretiempo.getHrini(), jbvarios.getFtoHora())), jbvarios.getFtoFhJavExt());
                    Date fin = jbvarios.stringToFecha(jbvarios.fechaToString(_sobretiempo.getFhfin(), jbvarios.getFtoFhJav()) + " "
                            + jbvarios.fechaToString(_sobretiempo.getHrfin(), jbvarios.getFtoHora()), jbvarios.getFtoFhJavExt());
                    dupPadre.setFhini(ini);
                    dupPadre.setFhfin(fin);

                    String hrini = "";
                    String hrfin = "";
                    String duracion = "";
                    //Calculando la duración.
                    hrini = jbvarios.fechaToString(_sobretiempo.getHrini(), jbvarios.getFtoHora());
                    hrfin = jbvarios.fechaToString(_sobretiempo.getHrfin(), jbvarios.getFtoHora());
                    duracion = casbt001mFacade.calcularDuracion(_sobretiempo.getFhini(), hrini, _sobretiempo.getFhfin(), hrfin);

                    dupPadre.setDuinc(duracion);
                    dupPadre.setCusuc(ejbFacade.getCusua());
                    dupPadre.setFhcre(fechaActual);

                    cadup001mFacade.create(dupPadre);

                    for (int i = 0; i < _stDuplicados.size(); i++) {

                        //Hijos
                        Cadup001m dupHijos = new Cadup001m();
                        dupHijos.setCodcm(ejbFacade.getCodem());
                        dupHijos.setCodpe(_stDuplicados.get(i).getCodpe());
                        dupHijos.setIdinc(_stDuplicados.get(i).getCodst());
                        dupHijos.setTpinc('S');
                        ini = jbvarios.stringToFecha(jbvarios.fechaToString(_stDuplicados.get(i).getFhini(), jbvarios.getFtoFhJav() + " "
                                + jbvarios.fechaToString(_stDuplicados.get(i).getHrini(), jbvarios.getFtoHora())), jbvarios.getFtoFhJavExt());
                        fin = jbvarios.stringToFecha(jbvarios.fechaToString(_stDuplicados.get(i).getFhfin(), jbvarios.getFtoFhJav()) + " "
                                + jbvarios.fechaToString(_stDuplicados.get(i).getHrfin(), jbvarios.getFtoHora()), jbvarios.getFtoFhJavExt());
                        dupHijos.setFhini(ini);
                        dupHijos.setFhfin(fin);

                        hrini = jbvarios.fechaToString(_stDuplicados.get(i).getHrini(), jbvarios.getFtoHora());
                        hrfin = jbvarios.fechaToString(_stDuplicados.get(i).getHrfin(), jbvarios.getFtoHora());
                        duracion = casbt001mFacade.calcularDuracion(_stDuplicados.get(i).getFhini(), hrini, _stDuplicados.get(i).getFhfin(), hrfin);

                        dupHijos.setDuinc(duracion);
                        dupHijos.setCusuc(ejbFacade.getCusua());
                        dupHijos.setFhcre(fechaActual);
                        dupHijos.setIdpad(dupPadre.getIddup());
                        hijos.add(dupHijos);

                        cadup001mFacade.create(dupHijos);

                    }

                    incluirEnListado(dupPadre, hijos, _incidenciasDuplicadas);
//            imprimirDuplicados(_incidenciasDuplicadas);
                    transaction.commit();
                }
            } catch (Exception e) {
                if (transaction != null) {
                    try {
                        transaction.rollback();
                        logger.debug(e.getMessage());
                        Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("AutIncidenciaErrorDuplicado"));
                    } catch (Exception ex) {
                        logger.debug(e.getMessage());
                        Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("AutIncidenciaErrorDuplicado"));
                    }
                }
            }
        }

    }

    /**
     * Método que returna true si existen incidencias duplicadas.
     *
     * @return true: Existen incidencias duplicadas, false: No existen incidencias duplicadas.
     */
    public boolean existenDuplicadas() {

        return !incidenciasDuplicadas.isEmpty();
    }

    /**
     * Método que realiza el llenado del listado que se visualizar en la vista donde aparecen los padres e hijos de las incidencias duplicadas.
     *
     * @param _padre Datos del padre.
     * @param _hijos Datos de los hijos.
     * @param _incidenciasDuplicadas listado de incidencias que se cargarán para ser mostradas en la vista.
     */
    private void incluirEnListado(Cadup001m _padre, List<Cadup001m> _hijos, List<incidenciasDuplicadas> _incidenciasDuplicadas) throws Exception {

        String nombreCompleto = ngbas001xFacade.nombreCompleto(_padre.getCodpe(), codModulo);
        if (nombreCompleto != null) {//Si es distinto de null, posee permisos para visualizar dicho trabajador.
            boolean ausencia = false;
            incidenciasDuplicadas padre = new incidenciasDuplicadas();
            padre.setIdIncidencia(_padre.getIdinc());
            padre.setNombreTrabajador(nombreCompleto);
            padre.setSerial(_padre.getIddup());

            if (_padre.getTpinc() == 'A') {
                ausencia = true;
                Ngvar002t ngvar002t = ejbFacadeA.ausenciaPorCodigo(_padre.getIdinc(), codModulo);
                String estado = ngvar002t.getNgvar006t().getDesed() + " - " + determinarOrigen(ngvar002t.getOrgpm());
                padre.setOrigen(estado);
            } else {
                Casbt001m casbt001m = ejbFacadeS.buscarCasbt001m(_padre.getIdinc(), codModulo);
                String estado = casbt001m.getNgvar006t().getDesed() + " - " + determinarOrigen(casbt001m.getOrgst());
                padre.setOrigen(estado);
            }

            if (ausencia) {
                padre.setTipoIncidencia("Ausencia");
            } else {
                padre.setTipoIncidencia("Sobretiempo");
            }

            String[] inicialCompleta = jbvarios.fechaToString(_padre.getFhini(), jbvarios.getFtoFhJavExt()).split(" ");
            String[] finalCompleta = jbvarios.fechaToString(_padre.getFhfin(), jbvarios.getFtoFhJavExt()).split(" ");

            if (inicialCompleta[1].compareTo(finalCompleta[1]) == 0) {
                //Se trata de incidencias en días.
                padre.setFechaInicio(jbvarios.fechaToString(_padre.getFhini(), jbvarios.getFtoFhJav()));
                padre.setFechaFin(jbvarios.fechaToString(_padre.getFhfin(), jbvarios.getFtoFhJav()));
            } else {
                //Se trata de incidencias en horas.
                padre.setFechaInicio(jbvarios.fechaToString(_padre.getFhini(), jbvarios.getFtoFhJavExt()));
                padre.setFechaFin(jbvarios.fechaToString(_padre.getFhfin(), jbvarios.getFtoFhJavExt()));
            }

            padre.setDuracion(_padre.getDuinc());

            for (int i = 0; i < _hijos.size(); i++) {
                incidenciasDuplicadas hijos = new incidenciasDuplicadas();
                hijos.setIdIncidencia(_hijos.get(i).getIdinc());
                hijos.setNombreTrabajador(nombreCompleto);
                hijos.setTipoIncidencia(padre.getTipoIncidencia());
                hijos.setSerial(_hijos.get(i).getIddup());

                inicialCompleta = jbvarios.fechaToString(_hijos.get(i).getFhini(), jbvarios.getFtoFhJavExt()).split(" ");
                finalCompleta = jbvarios.fechaToString(_hijos.get(i).getFhfin(), jbvarios.getFtoFhJavExt()).split(" ");

                if (inicialCompleta[1].compareTo(finalCompleta[1]) == 0) {
                    //Se trata de incidencias en días.
                    hijos.setFechaInicio(jbvarios.fechaToString(_hijos.get(i).getFhini(), jbvarios.getFtoFhJav()));
                    hijos.setFechaFin(jbvarios.fechaToString(_hijos.get(i).getFhfin(), jbvarios.getFtoFhJav()));
                } else {
                    //Se trata de incidencias en horas.
                    hijos.setFechaInicio(jbvarios.fechaToString(_hijos.get(i).getFhini(), jbvarios.getFtoFhJavExt()));
                    hijos.setFechaFin(jbvarios.fechaToString(_hijos.get(i).getFhfin(), jbvarios.getFtoFhJavExt()));
                }

                if (ausencia) {
                    Ngvar002t ngvar002t = ejbFacadeA.ausenciaPorCodigo(_hijos.get(i).getIdinc(), codModulo);
                    String estado = ngvar002t.getNgvar006t().getDesed() + " - " + determinarOrigen(ngvar002t.getOrgpm());
                    hijos.setOrigen(estado);
                } else {
                    Casbt001m casbt001m = ejbFacadeS.buscarCasbt001m(_hijos.get(i).getIdinc(), codModulo);
                    String estado = casbt001m.getNgvar006t().getDesed() + " - " + determinarOrigen(casbt001m.getOrgst());
                    hijos.setOrigen(estado);
                }

                hijos.setDuracion(_hijos.get(i).getDuinc());
                hijos.setDuplicados(null);
                padre.getDuplicados().add(hijos);
            }

            _incidenciasDuplicadas.add(padre);
        }

    }

    /**
     * Método que consulta si la incidencia posee duplicadas para agregarlas a la vista y guardarlas en BD
     */
    private void agregarDuplicadas() throws Exception {
        if (ausencia) {
            if (ngvar002t != null) {
                List<Ngvar002t> auDuplicadas = ejbFacadeA.buscarAusenciasDuplicadas(ngvar002t, ngvar002t.getDurua() == 'h', codModulo);

                cargarListadoAusenciasDuplicadas(ngvar002t, auDuplicadas, incidenciasDuplicadas);
            }
        } else {
            if (sobretiempo != null) {
                List<Casbt001m> stDuplicados = ejbFacadeS.buscarSobretiempoDuplicados(sobretiempo, codModulo);

                cargarListadoSobretiemposDuplicados(sobretiempo, stDuplicados, incidenciasDuplicadas);
            }
        }
    }

    /**
     * Método que dado un char, determina el tipo de origen de la incidencia.
     *
     * @param _orgpm Origen de la incidencia.
     */
    private String determinarOrigen(Character orgpm) {
        String origen = "";
        switch (orgpm) {
            case 'N':
                origen = "Nómina/SCA";
                break;
            case 'I':
                origen = "Módulo de Incidencias No Autorizadas";
                break;
            case 'T':
                origen = "Módulo de Reclamos";
                break;
            case 'A':
                origen = "Procesado de Forma Automática con planilla no procesada";
                break;
            case 'S':
                origen = "Procesado de Forma Automática Sin Planilla";
                break;
            default:
                origen = "No Aplica";
                break;
        }

        return origen;
    }

    /**
     * Método para eliminar los hijos de la duplicidad.
     *
     * @param _padre Datos de la incidencia padre.
     * @param _eliminarPadre True: está mandando a eliminar el registro padre, False: está mandado a eliminar los registros hijos.
     */
    public void eliminarDuplicados(incidenciasDuplicadas _padre, boolean _eliminarPadre) {
        UserTransaction transaction = null;
        try {
            transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
            transaction.begin();

            if (_eliminarPadre) {//Eliminar incidencia padre.
                if (_padre.getTipoIncidencia().compareTo("Sobretiempo") == 0) { //Es un sobretiempo
                    Casbt001m casbt001m = casbt001mFacade.buscarCasbt001m(_padre.getIdIncidencia(), codModulo);
                    //Elimino sobretiempo
                    casbt001mFacade.remove(casbt001m);
                } else {//Es una ausencia
                    Ngvar002t ngvar002t = ejbFacadeA.ausenciaPorCodigo(_padre.getIdIncidencia(), codModulo);
                    //Elimino ausencia
                    ejbFacadeA.remove(ngvar002t);
                }
            } else {//Eliminar hijos.
                List<incidenciasDuplicadas> hijos = _padre.getDuplicados();
                if (_padre.getTipoIncidencia().compareTo("Sobretiempo") == 0) { //Es un sobretiempo
                    for (int i = 0; i < hijos.size(); i++) { //Elimino todos los sobretiempo hijos
                        Casbt001m casbt001m = casbt001mFacade.buscarCasbt001m(hijos.get(i).getIdIncidencia(), codModulo);
                        casbt001mFacade.remove(casbt001m);
                    }
                } else {//Es una ausencia
                    for (int i = 0; i < hijos.size(); i++) {//Elimino todas las ausencias hijos
                        Ngvar002t ngvar002t = ejbFacadeA.ausenciaPorCodigo(hijos.get(i).getIdIncidencia(), codModulo);
                        ejbFacadeA.remove(ngvar002t);
                    }
                }
            }

            List<Cadup001m> eliminar = cadup001mFacade.buscarFamiliares(_padre.getSerial(), codModulo);

            //Ya se gestionaron los duplicados, se eliminarán los registros de la BD.
            if (eliminar != null) {
                for (int i = 0; i < eliminar.size(); i++) {
                    cadup001mFacade.remove(eliminar.get(i));
                }
            }

            //Borro toda la información del listado
            incidenciasDuplicadas.clear();

            //Se cargan las incidencias duplicadas que existen.
            cargarDuplicados();

            transaction.commit();

            Utilidades.mostrarMensaje(
                    0, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"),
                    ResourceBundle.getBundle(Utilidades.BUNDLE).getString("AutIncidenciasMsjDuplicadasExitoso"));
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                    logger.debug(e.getMessage());
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                } catch (Exception ex) {
                    logger.debug(e.getMessage());
                    Utilidades.mostrarMensaje(2, ResourceBundle.getBundle(Utilidades.BUNDLE_FENIX).getString("VacioTitle"), ResourceBundle.getBundle(Utilidades.BUNDLE).getString("PersistenceErrorOccured"));
                }
            }
        }
    }

    /**
     * Método para cargar los duplicados en el listado.
     */
    private void cargarDuplicados() throws Exception {
        List<Cadup001m> padres = cadup001mFacade.buscarRegistrosPadre(codModulo);

        if (padres != null) {
            for (Cadup001m padre : padres) {
                List<Cadup001m> hijos = cadup001mFacade.buscarRegistrosHijos(padre.getIddup(), codModulo);
                incluirEnListado(padre, hijos, incidenciasDuplicadas);
            }
        }
    }
    //</editor-fold>    
}
