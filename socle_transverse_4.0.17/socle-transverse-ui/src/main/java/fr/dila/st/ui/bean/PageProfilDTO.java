package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class PageProfilDTO {
    private List<String> profils = new ArrayList<>();
    private List<ColonneInfo> lstColonnes = new ArrayList<>();

    public PageProfilDTO() {
        super();
    }

    public List<String> getProfils() {
        return profils;
    }

    public void setProfils(List<String> profils) {
        this.profils = profils;
    }

    public List<ColonneInfo> getLstColonnes() {
        return lstColonnes;
    }

    public void setLstColonnes(List<ColonneInfo> lstColonnes) {
        this.lstColonnes = lstColonnes;
    }
}
