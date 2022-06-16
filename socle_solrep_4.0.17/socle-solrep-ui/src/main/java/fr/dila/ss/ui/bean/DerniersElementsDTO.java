package fr.dila.ss.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class DerniersElementsDTO {
    private List<DernierElementDTO> derniersElementsList = new ArrayList<>();

    public List<DernierElementDTO> getDerniersElementsList() {
        return derniersElementsList;
    }

    public void setDerniersElementsList(List<DernierElementDTO> derniersElementsList) {
        this.derniersElementsList = derniersElementsList;
    }
}
