package com.fenix.control.controller.gestiondepersonal.autorizarmodomarcaje_sobretiempo;
//<editor-fold defaultstate="collapsed" desc="Importaciones">
import com.fenix.control.controller.util.JsfUtil;
import com.fenix.control.fenixTools.util.Utilidades;
import com.fenix.control.session.Permisos;
import com.fenix.control.session.sessionBean;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002m;
import com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mPK;
import com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
//</editor-fold>

/**
 * <br><b>Title:</b> </br> <br><b>Description:</b> .</br> <br><b>Copyright:</b> Copyright (c) 2011</br> <br><b>Company:</b> Pasteurizadora Tachira CA</br>
 *
 * @author user
 * @version 0.xv dia, hora
 */
@ManagedBean(name = "caaut002mController")
@SessionScoped
public class Caaut002mController {
//<editor-fold defaultstate="collapsed" desc="Declaracion de Variables">

    private Caaut002m current;
    private Caaut002m currentDestroy;
    private Caaut002m currentAgregar;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mFacade ejbFacade;
    @EJB
    private com.fenix.logica.jpa.adminsistema.gruposparametros.Tgpar002dFacade tgpar002dFacade;
    @EJB
    private com.fenix.logica.jpa.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mFacade caaut002mFacade;
    private static final Logger logger = Logger.getLogger(Caaut002m.class.getName());
    private int selectedItemIndex;
    private String codModulo = "SCAF0779";
    /*Para el Lazy */
    private LazyDataModel<Caaut002m> lazyModel;
    private Integer pagIndex = null;
    private Integer paginacion = null;
    private Map<String, Object> fields = new HashMap<String, Object>();
    private String sortF = null;
    private int cantElemTabla = 20;
    private SortOrder sortB = SortOrder.UNSORTED;
    private boolean permisologia[] = new boolean[10];
    private boolean valoresDefault = false;

    /*Fin de Lazy*/
//</editor-fold>
    public Caaut002mController() {
        java.util.Arrays.fill(permisologia, Boolean.TRUE);
        //consultarPermisos(codModulo);
    }
    //<editor-fold defaultstate="collapsed" desc="Encapsulamiento">

    public Caaut002m getCurrent() {
        return current;
    }

    public void setCurrent(Caaut002m current) {
        this.current = current;
    }

    public Caaut002m getSelected() {
        if (current == null) {
            current = new Caaut002m();
            //Si clave primaria Compuesta Inicializarla
            selectedItemIndex = -1;
        }
        return current;
    }

    private Caaut002mFacade getFacade() {
        return ejbFacade;
    }

    public boolean[] getPermisologia() {
        return permisologia;
    }

    public void setPermisologia(boolean[] permisologia) {
        this.permisologia = permisologia;
    }

    public LazyDataModel<Caaut002m> getLazyModel() {
        if (lazyModel == null || lazyModel.getRowCount() == 0) {
            inicializarLazy();
        }
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Caaut002m> lazyModel) {
        this.lazyModel = lazyModel;
    }
//fin lazy

    public String getCodModulo() {
        return codModulo;
    }

    public void setCodModulo(String codModulo) {
        this.codModulo = codModulo;
    }

    public Caaut002m getCurrentAgregar() {
        return currentAgregar;
    }

    public void setCurrentAgregar(Caaut002m currentAgregar) {
        this.currentAgregar = currentAgregar;
    }

    public Caaut002m getCurrentDestroy() {
        return currentDestroy;
    }

    public void setCurrentDestroy(Caaut002m currentDestroy) {
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
        current = new Caaut002m();
        //Si clave primaria Compuesta Inicializarla
        // recreateModel();
        //consultarPermisos(codModulo);
        return "List";
    }

    public String prepareView() {
        current = (Caaut002m) lazyModel.getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        //consultarPermisos(codModulo);
        return "View";
    }

    public String prepareCreate() {
        currentAgregar = new Caaut002m(new Caaut002mPK());
        //Si clave primaria Compuesta Inicializarla
        selectedItemIndex = -1;
        //consultarPermisos(codModulo);
        return "Create";
    }

    public String create() {
        try {
            currentAgregar.getCaaut002mPK().setCodem(ejbFacade.getCodem());
            currentAgregar.setFhcre(ejbFacade.getCurrentDateTime());
            currentAgregar.setFhmod(ejbFacade.getCurrentDateTime());
            currentAgregar.setCusua(ejbFacade.getCusua());
            currentAgregar.setCusuc(ejbFacade.getCusua());
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
        current = (Caaut002m) lazyModel.getRowData();
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
        // current = (Caaut002m)getItems().getRowData();
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

    public List<Caaut002m> ListAvailableSelectOne() {
        return ejbFacade.findAll(codModulo);
    }

    public List<Caaut002m> ListAvailableSelectOne(String _codmo) {
        return ejbFacade.findAll(_codmo);
    }

    /**
     * Método que retorna a un selectOneMenu los tipos de motivo de autorización permitidos para un Supervisor
     *
     * @param _codmo Código del módulo
     * @return List<Caaut002m>
     */
    public List<Caaut002m> ListAvailableSelectOneSupervisor(String _codmo) {
        try {
            List<Caaut002m> lista = new ArrayList<Caaut002m>();

            if (perfilAdmin()) {
                String valpaSobreTiempo = tgpar002dFacade.consultarParametro("MASOB").getValpa();
                Caaut002m buscarRegistro = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaSobreTiempo), codModulo, ejbFacade.getCodem());
                String valpaMHabitual = tgpar002dFacade.consultarParametro("MAMAH").getValpa();
                Caaut002m buscarRegistroMH = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaMHabitual), codModulo, ejbFacade.getCodem());
                lista.add(buscarRegistroMH);
                lista.add(buscarRegistro);
            } else if (perfilSupervisor()) {
                //Cambió el 03/03/2016 anteriormente el supervisor solo podía crear autorizacion de sobretiempo, se agregó el habitual.
                String valpaMHabitual = tgpar002dFacade.consultarParametro("MAMAH").getValpa();
                Caaut002m buscarRegistroMH = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaMHabitual), codModulo, ejbFacade.getCodem());
                lista.add(buscarRegistroMH);
                //Sobretiempo
                String valpaSobreTiempo = tgpar002dFacade.consultarParametro("MASOB").getValpa();
                Caaut002m buscarRegistro = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaSobreTiempo), codModulo, ejbFacade.getCodem());
                lista.add(buscarRegistro);
            }

            if (!lista.isEmpty()) {
                return lista;
            }

        } catch (Exception e) {
            logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Método que retorna a un selectOneMenu los tipos de motivo de autorización permitidos para un administrador
     *
     * @param _codmo Código del módulo
     * @return List<Caaut002m>
     */
    public List<Caaut002m> ListAvailableSelectOneAdmin(String _codmo) {
        try {
            String valpaMHabitual = tgpar002dFacade.consultarParametro("MAMAH").getValpa();
            String valpaSobreTiempo = tgpar002dFacade.consultarParametro("MASOB").getValpa();
            Caaut002m buscarRegistro = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaMHabitual), codModulo, ejbFacade.getCodem());
            Caaut002m buscarRegistro2 = caaut002mFacade.buscarRegistro(Integer.parseInt(valpaSobreTiempo), codModulo, ejbFacade.getCodem());

            List<Caaut002m> lista = new ArrayList<Caaut002m>();
            lista.add(buscarRegistro);
            lista.add(buscarRegistro2);

            if (!lista.isEmpty()) {
                return lista;
            }

        } catch (Exception e) {
            logger.debug(ejbFacade.getCusua() + " " + e.getLocalizedMessage());
        }
        return null;
    }

    @FacesConverter(forClass = Caaut002m.class, value = "Caaut002mConverter")
    public static class Caaut002mControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            Caaut002mController controller = (Caaut002mController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "caaut002mController");
            return controller.ejbFacade.find(getKey(value));
        }

        com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mPK getKey(String value) {
            com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mPK();
            key.setCodem(values[0]);
            key.setCodma(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(com.fenix.logica.entidades.gestiondepersonal.autorizarmodomarcaje_sobretiempo.Caaut002mPK value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.getCodem());
            sb.append(SEPARATOR);
            sb.append(value.getCodma());
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Caaut002m) {
                Caaut002m o = (Caaut002m) object;
                return getStringKey(o.getCaaut002mPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Caaut002mController.class.getName());
            }
        }
    }
//Lazy inicio

    public void inicializarLazy() {
        lazyModel = new LazyDataModel<Caaut002m>() {
            @Override
            public List<Caaut002m> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
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
            currentAgregar = new Caaut002m(new Caaut002mPK());
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
    //</editor-fold>
}
