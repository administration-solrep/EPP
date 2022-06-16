package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.QueryParam;

@SwBean
public class ArrayFromQuery {
    @QueryParam("valeurs")
    private ArrayList<String> liste = new ArrayList<>();

    public ArrayList<String> getListe() {
        return liste;
    }

    public void setListe(ArrayList<String> liste) {
        this.liste = liste;
    }
}
