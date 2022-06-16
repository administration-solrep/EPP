package fr.dila.ss.ui.bean.actualites;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.DocumentDTO;
import fr.dila.st.ui.mapper.MapDoc2BeanFilesFilesProcess;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SwBean
public class ActualiteConsultationDTO implements ActualiteDTO {
    @NxProp(docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE, xpath = STSchemaConstant.ECM_UUID_XPATH)
    private String id;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_EMISSION
    )
    private Calendar dateEmission;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_VALIDITE
    )
    private Calendar dateValidite;

    @NxProp(docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE, xpath = ActualiteConstant.ACTUALITE_PREFIX_XPATH_OBJET)
    private String objet;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_PREFIX_XPATH_CONTENU
    )
    private String contenu;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_PREFIX_XPATH_DANS_HISTORIQUE
    )
    private boolean isInHistorique;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE,
        xpath = STConstant.FILES_SCHEMA + ":" + STConstant.FILES_PROPERTY_FILES,
        process = MapDoc2BeanFilesFilesProcess.class
    )
    private List<DocumentDTO> piecesJointes = new ArrayList<>();

    public ActualiteConsultationDTO() {
        // Default constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String getContenu() {
        return contenu;
    }

    @Override
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public boolean getIsInHistorique() {
        return isInHistorique;
    }

    public void setIsInHistorique(boolean isInHistorique) {
        this.isInHistorique = isInHistorique;
    }

    public String getStatut() {
        String key = isInHistorique
            ? "actualites.statut.actualite.archivee"
            : "actualites.statut.actualite.non.archivee";
        return ResourceHelper.getString(key);
    }

    public List<DocumentDTO> getPiecesJointes() {
        return piecesJointes;
    }

    public void setPiecesJointes(List<DocumentDTO> piecesJointes) {
        this.piecesJointes = piecesJointes;
    }
}
