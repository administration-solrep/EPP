package fr.dila.ss.ui.bean.actualites;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.mapper.MapDoc2BeanFilesUploadProcess;
import java.util.Calendar;
import javax.ws.rs.FormParam;

@SwBean
public class ActualiteCreationDTO implements ActualiteDTO {
    @FormParam("dateEmission")
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_SCHEMA_PREFIX + ":" + ActualiteConstant.ACTUALITE_PROPERTY_DATE_EMISSION
    )
    private Calendar dateEmission;

    @FormParam("dateValidite")
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_SCHEMA_PREFIX + ":" + ActualiteConstant.ACTUALITE_PROPERTY_DATE_VALIDITE
    )
    private Calendar dateValidite;

    @FormParam("objet")
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_SCHEMA_PREFIX + ":" + ActualiteConstant.ACTUALITE_PROPERTY_OBJET
    )
    private String objet;

    @FormParam("statut")
    private String statut;

    @FormParam("contenu")
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_SCHEMA_PREFIX + ":" + ActualiteConstant.ACTUALITE_PROPERTY_CONTENU
    )
    private String contenu;

    @FormParam("hasPj")
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_SCHEMA_PREFIX + ":" + ActualiteConstant.ACTUALITE_PROPERTY_HASPJ
    )
    private boolean hasPj;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_SCHEMA_PREFIX + ":" + ActualiteConstant.ACTUALITE_PROPERTY_DANS_HISTORIQUE
    )
    private boolean isInHistorique = true;

    @FormParam("uploadBatchId")
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = STConstant.FILES_SCHEMA + ":" + STConstant.FILES_PROPERTY_FILES,
        process = MapDoc2BeanFilesUploadProcess.class
    )
    private String uploadBatchId;

    public ActualiteCreationDTO() {
        // Default constructor
    }

    public ActualiteCreationDTO(
        Calendar dateEmission,
        Calendar dateValidite,
        String objet,
        String statut,
        String contenu
    ) {
        super();
        this.dateEmission = dateEmission;
        this.dateValidite = dateValidite;
        this.objet = objet;
        this.statut = statut;
        this.contenu = contenu;
    }

    @Override
    public Calendar getDateEmission() {
        return dateEmission;
    }

    @Override
    public void setDateEmission(Calendar dateEmission) {
        this.dateEmission = dateEmission;
    }

    @Override
    public Calendar getDateValidite() {
        return dateValidite;
    }

    @Override
    public void setDateValidite(Calendar dateValidite) {
        this.dateValidite = dateValidite;
    }

    @Override
    public String getObjet() {
        return objet;
    }

    @Override
    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
        this.isInHistorique = "ARCHIVEE".equals(statut);
    }

    @Override
    public String getContenu() {
        return contenu;
    }

    @Override
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public boolean getHasPj() {
        return hasPj;
    }

    public void setHasPj(boolean hasPj) {
        this.hasPj = hasPj;
    }

    public String getUploadBatchId() {
        return uploadBatchId;
    }

    public void setUploadBatchId(String uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
    }
}
