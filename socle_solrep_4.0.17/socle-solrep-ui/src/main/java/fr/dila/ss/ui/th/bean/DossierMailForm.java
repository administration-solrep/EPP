package fr.dila.ss.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class DossierMailForm {
    @FormParam("destinataires[]")
    private ArrayList<String> destinataires;

    @FormParam("copie")
    private Boolean etreEnCopie = true;

    @FormParam("destinataireIds[]")
    private ArrayList<String> destinataireIds;

    @FormParam("autres")
    private String autresDestinataires;

    @FormParam("objet")
    private String objet;

    @FormParam("message")
    private String message;

    public DossierMailForm() {
        super();
    }

    public ArrayList<String> getDestinataires() {
        return destinataires;
    }

    public void setDestinataires(ArrayList<String> destinataires) {
        this.destinataires = destinataires;
    }

    public Boolean getEtreEnCopie() {
        return etreEnCopie;
    }

    public void setEtreEnCopie(Boolean etreEnCopie) {
        this.etreEnCopie = etreEnCopie;
    }

    public ArrayList<String> getDestinataireIds() {
        return destinataireIds;
    }

    public void setDestinataireIds(ArrayList<String> destinataireIds) {
        this.destinataireIds = destinataireIds;
    }

    public String getAutresDestinataires() {
        return autresDestinataires;
    }

    public void setAutresDestinataires(String autresDestinataires) {
        this.autresDestinataires = autresDestinataires;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
