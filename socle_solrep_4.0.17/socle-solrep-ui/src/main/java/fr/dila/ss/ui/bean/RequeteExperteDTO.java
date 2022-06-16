package fr.dila.ss.ui.bean;

import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequeteExperteDTO implements Serializable {
    private static final long serialVersionUID = -1602487626077660816L;

    public RequeteExperteDTO() {}

    private List<RequeteLigneDTO> requetes = new ArrayList<>();

    private List<ChampDescriptor> champs = new ArrayList<>();

    public List<RequeteLigneDTO> getRequetes() {
        return requetes;
    }

    public void setRequetes(List<RequeteLigneDTO> requetes) {
        this.requetes = requetes;
    }

    public List<ChampDescriptor> getChamps() {
        return champs;
    }

    public void setChamps(List<ChampDescriptor> champs) {
        this.champs = champs;
    }
}
