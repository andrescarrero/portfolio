package com.fenix.control.controller.gestiondepersonal.incidencias.incidenciasnoautorizadas;
//<editor-fold defaultstate="collapsed" desc="Importaciones">
import com.fenix.control.fenixTools.util.Utilidades;
import com.fenix.logica.entidades.gestiondepersonal.incidencias.reclamosdeincidencias.Capar001m;
import com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Capar001mFacade;

import java.util.ResourceBundle;
import com.fenix.control.controller.util.JsfUtil;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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

//Lazy
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.SortOrder;
//</editor-fold>

/**
 * <br><b>Title:</b> </br> <br><b>Description:</b> .</br> <br><b>Copyright:</b> Copyright (c) 2011</br> <br><b>Company:</b> Pasteurizadora
 * Tachira CA</br>
 *
 * @author user
 * @version 0.xv dia, hora
 */
@ManagedBean(name = "capar001mController")
@SessionScoped
public class Capar001mController {
//<editor-fold defaultstate="collapsed" desc="Declaracion de Variables">

    private Capar001m current;
    private Capar001m currentDestroy;
    private Capar001m currentAgregar;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.incidencias.incidenciasnoautorizadas.Capar001mFacade ejbFacade;
    private int selectedItemIndex;
    private String codModulo = "";
    /*Para el Lazy */
    private LazyDataModel<Capar001m> lazyModel;
    private Integer pagIndex = null;
    private Integer paginacion = null;
    private Map<String, Object> fields = new HashMap<String, Object>();
    private String sortF = null;
    private int cantElemTabla = 20;
    private SortOrder sortB = SortOrder.UNSORTED;
    private boolean permisologia[] = new boolean[10];
    private boolean valoresDefault = false;
    private final static Logger logger = Logger.getLogger(Capar001m.class.getName());
    /*Fin de Lazy*/
//</editor-fold>

    public Capar001mController() {
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

    public LazyDataModel<Capar001m> getLazyModel() {
        if (lazyModel == null || lazyModel.getRowCount() == 0) {
            inicializarLazy();
        }
        return lazyModel;
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

    @FacesConverter(forClass = Capar001m.class, value = "Capar001mConverter")
    public static class Capar001mControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            Capar001mController controller = (Capar001mController) facesContext.getApplication().getELResolver().
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
//Lazy inicio

    public void inicializarLazy() {
        lazyModel = new LazyDataModel<Capar001m>() {
            @Override
            public List<Capar001m> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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
            currentAgregar = new Capar001m();
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
    //</editor-fold>
}
