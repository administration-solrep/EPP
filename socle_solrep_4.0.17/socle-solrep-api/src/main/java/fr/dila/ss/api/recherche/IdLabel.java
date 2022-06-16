package fr.dila.ss.api.recherche;

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

public class IdLabel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -7118708193977203078L;

    private String id;
    private String idDossier;
    private String label;

    public IdLabel(String id, String label) {
        this(id, label, null);
    }

    public IdLabel(String id, String label, String idDossier) {
        this.id = id;
        this.label = label;
        this.idDossier = idDossier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(String idDossier) {
        this.idDossier = idDossier;
    }

    public String getUrl() {
        if (StringUtils.isNotBlank(idDossier)) {
            return String.format("/dossier/%s/parapheur?dossierLinkId=%s", idDossier, id);
        }
        return "#";
    }

    public String getIcon() {
        return null;
    }
}
