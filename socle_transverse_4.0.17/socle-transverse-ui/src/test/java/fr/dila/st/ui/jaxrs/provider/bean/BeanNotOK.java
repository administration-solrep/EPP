package fr.dila.st.ui.jaxrs.provider.bean;

import java.util.ArrayList;

public class BeanNotOK {
    private ArrayList<String> liste = new ArrayList<>();

    public ArrayList<String> getListe() {
        return liste;
    }

    public void setListe(ArrayList<String> liste) {
        this.liste = liste;
    }
}
