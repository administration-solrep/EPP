package fr.dila.st.api.domain;

import java.util.Calendar;
import org.nuxeo.ecm.core.api.Blob;

public interface ExportDocument extends STDomainObject {
    /**
     *
     * @return le propriétaire de l'export
     */
    String getOwner();

    /**
     * Renseigne le propriétaire de l'export
     *
     * @param owner
     */
    void setOwner(String owner);

    /**
     *
     * @return la date de demande d'export
     */
    Calendar getDateRequest();

    /**
     * Renseigne la date de demande d'export
     *
     * @param date
     */
    void setDateRequest(Calendar date);

    /**
     *
     * @return vrai si le life cycle state du document = exporting
     */
    boolean isExporting();

    /**
     * modifie le life cycle state du document - vrai = exporting / faux = done
     *
     * @param exporting
     */
    void setExporting(boolean exporting);

    /**
     * Renseigne le contenu du document
     *
     * @param content
     */
    void setFileContent(Blob content);

    /**
     * Récupère le contenu du document
     *
     * @return
     */
    Blob getFileContent();
}
