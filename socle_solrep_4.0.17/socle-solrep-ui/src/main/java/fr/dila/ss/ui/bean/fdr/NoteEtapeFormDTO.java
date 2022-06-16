package fr.dila.ss.ui.bean.fdr;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;

@SwBean
public class NoteEtapeFormDTO {
    @FormParam("stepId")
    private String stepId;

    @FormParam("dossierLinkId")
    private String dossierLinkId;

    @FormParam("dossierId")
    private String dossierId;

    @FormParam("dossierLinkIds[]")
    private ArrayList<String> dossierLinkIds;

    @FormParam("commentId")
    private String commentId;

    @FormParam("commentContent")
    private String commentContent;

    @FormParam("commentParentId")
    private String commentParentId;

    @FormParam("typeRestriction")
    private String typeRestriction = "NON";

    @FormParam("idNode")
    private String idNode;

    private Map<String, String> mapIdNode;

    public NoteEtapeFormDTO() {}

    public NoteEtapeFormDTO(String typeRestriction) {
        this.typeRestriction = typeRestriction;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getDossierLinkId() {
        return dossierLinkId;
    }

    public void setDossierLinkId(String dossierLinkId) {
        this.dossierLinkId = dossierLinkId;
    }

    public String getDossierId() {
        return dossierId;
    }

    public void setDossierId(String dossierId) {
        this.dossierId = dossierId;
    }

    public ArrayList<String> getDossierLinkIds() {
        return dossierLinkIds;
    }

    public void setDossierLinkIds(ArrayList<String> dossierLinkIds) {
        this.dossierLinkIds = dossierLinkIds;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentParentId() {
        return commentParentId;
    }

    public void setCommentParentId(String commentParentId) {
        this.commentParentId = commentParentId;
    }

    public String getTypeRestriction() {
        return typeRestriction;
    }

    public void setTypeRestriction(String typeRestriction) {
        this.typeRestriction = typeRestriction;
    }

    public String getIdNode() {
        return idNode;
    }

    public void setIdNode(String idNode) {
        this.idNode = idNode;
    }

    public Map<String, String> getMapIdNode() {
        return mapIdNode;
    }

    public void setMapIdNode(Map<String, String> mapIdNode) {
        this.mapIdNode = mapIdNode;
    }

    public String getVisibility() {
        String visibility = null;
        if (StringUtils.isNotEmpty(idNode)) {
            visibility = typeRestriction + '-' + idNode;
        }
        return visibility;
    }
}
