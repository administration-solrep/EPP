package fr.dila.st.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class BatchNotifForm {
    @FormParam("activee")
    private String activee;

    @FormParam("listeUtilisateurs")
    private ArrayList<String> listeUtilisateurs = new ArrayList<>();

    private Map<String, String> mapUtilisateurs = new HashMap<>();

    public BatchNotifForm() {
        super();
    }

    public String getActivee() {
        return activee;
    }

    public void setActivee(String activee) {
        this.activee = activee;
    }

    public ArrayList<String> getListeUtilisateurs() {
        return listeUtilisateurs;
    }

    public void setListeUtilisateurs(ArrayList<String> listeUtilisateurs) {
        this.listeUtilisateurs = listeUtilisateurs;
    }

    public Map<String, String> getMapUtilisateurs() {
        return mapUtilisateurs;
    }

    public void setMapUtilisateurs(Map<String, String> mapUtilisateurs) {
        this.mapUtilisateurs = mapUtilisateurs;
    }
}
