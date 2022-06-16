package fr.dila.ss.ui.bean;

import fr.dila.ss.ui.th.bean.AbstractDossierList;
import fr.dila.st.core.client.AbstractMapDTO;
import java.util.ArrayList;
import java.util.List;

public class SSDossierList<T extends AbstractMapDTO> extends AbstractDossierList {
    private List<T> liste = new ArrayList<>();

    public SSDossierList() {
        super(0);
    }

    public List<T> getListe() {
        return liste;
    }

    public void setListe(List<T> liste) {
        this.liste = liste;
    }
}
