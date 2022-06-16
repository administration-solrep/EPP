package fr.dila.ss.ui.bean;

import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.st.ui.bean.TreeDTO;
import fr.dila.st.ui.bean.TreeElementDTO;

public class MailboxListDTO extends TreeDTO<TreeElementDTO> {
    private TypeRegroupement modeTri = TypeRegroupement.PAR_POSTE;

    private String selectionPoste = "";

    private String selectionUser = "";

    public MailboxListDTO() {
        super();
    }

    public TypeRegroupement getModeTri() {
        return modeTri;
    }

    public void setModeTri(TypeRegroupement modeTri) {
        this.modeTri = modeTri;
    }

    public String getSelectionPoste() {
        return selectionPoste;
    }

    public void setSelectionPoste(String selectionPoste) {
        this.selectionPoste = selectionPoste;
    }

    public String getSelectionUser() {
        return selectionUser;
    }

    public void setSelectionUser(String selectionUser) {
        this.selectionUser = selectionUser;
    }
}
