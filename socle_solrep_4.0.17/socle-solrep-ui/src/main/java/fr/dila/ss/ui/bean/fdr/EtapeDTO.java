package fr.dila.ss.ui.bean.fdr;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.NxProp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.platform.actions.Action;

public class EtapeDTO {
    public static final String MINISTERE = "ministere";
    public static final String NON = "non";
    public static final String SGG = "sgg";

    private EtatEtapeDTO etat;

    @NxProp(docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE, xpath = STSchemaConstant.ECM_UUID_XPATH)
    private String id;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY
    )
    private String action;

    private String posteId;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_POSTE_LABEL_PROPERTY
    )
    protected String poste;

    private Map<String, String> mapPoste = new HashMap<>();

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_MINISTERE_LABEL_PROPERTY
    )
    protected String ministere;

    @NxProp(docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE, xpath = SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_XPATH)
    private String mailBoxId;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_DEADLINE_PROPERTY
    )
    private Long deadLine;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_DUE_DATE_PROPERTY
    )
    private Calendar echeanceDate;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_DATE_FIN_ETAPE_PROPERTY
    )
    private Calendar traiteDate;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY
    )
    private boolean obligatoireMinistere;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY
    )
    private boolean obligatoireSgg;

    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY
    )
    private boolean valAuto;

    // représente le prénom et le nom de la personne qui a validé létape
    @NxProp(
        docType = SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
        xpath = SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
        ":" +
        SSFeuilleRouteConstant.ROUTING_TASK_VALIDATION_USER_LABEL_PROPERTY
    )
    private String utilisateur;

    private List<NoteEtapeDTO> notes = new ArrayList<>();
    private boolean isStripped = true;
    private ContainerDTO parent;
    private List<Action> actions = new ArrayList<>();
    private List<String> parentsId = new ArrayList<>();
    private Integer depth;

    public EtapeDTO() {
        super();
    }

    public EtatEtapeDTO getEtat() {
        return etat;
    }

    public void setEtat(EtatEtapeDTO etat) {
        this.etat = etat;
    }

    public List<NoteEtapeDTO> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteEtapeDTO> notes) {
        this.notes = notes;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getMinistere() {
        return ministere;
    }

    public void setMinistere(String ministere) {
        this.ministere = ministere;
    }

    public String getPosteInMinistere() {
        return (
            (StringUtils.isNotEmpty(this.ministere) ? this.ministere : "Sans ministère défini") +
            " <br /> " +
            (StringUtils.isNotEmpty(this.poste) ? this.poste : "sans poste")
        );
    }

    public Calendar getEcheanceDate() {
        return echeanceDate;
    }

    public void setEcheanceDate(Calendar echeanceDate) {
        this.echeanceDate = echeanceDate;
    }

    public Calendar getTraiteDate() {
        return traiteDate;
    }

    public void setTraiteDate(Calendar traiteDate) {
        this.traiteDate = traiteDate;
    }

    public boolean getIsStripped() {
        return isStripped;
    }

    public void setIsStripped(boolean isStripped) {
        this.isStripped = isStripped;
    }

    public ContainerDTO getParent() {
        return parent;
    }

    public void setParent(ContainerDTO parent) {
        this.parent = parent;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<String> getParentsId() {
        return parentsId;
    }

    public void setParentsId(List<String> parentsId) {
        this.parentsId = parentsId;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailBoxId() {
        return mailBoxId;
    }

    public void setMailBoxId(String mailBoxId) {
        this.mailBoxId = mailBoxId;
    }

    public String getPosteId() {
        return posteId;
    }

    public void setPosteId(String posteId) {
        this.posteId = posteId;
    }

    public String getObligatoire() {
        if (this.obligatoireMinistere) {
            return MINISTERE;
        }

        if (this.obligatoireSgg) {
            return SGG;
        }

        return NON;
    }

    public void setObligatoire(String obligatoire) {
        this.obligatoireMinistere = MINISTERE.equals(obligatoire);
        this.obligatoireSgg = SGG.equals(obligatoire);
    }

    public boolean isValAuto() {
        return valAuto;
    }

    public void setValAuto(boolean valAuto) {
        this.valAuto = valAuto;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Long getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Long deadLine) {
        this.deadLine = deadLine;
    }

    public Map<String, String> getMapPoste() {
        return mapPoste;
    }

    public void setMapPoste(Map<String, String> mapPoste) {
        this.mapPoste = mapPoste;
    }
}
