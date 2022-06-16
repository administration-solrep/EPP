package fr.dila.st.core.organigramme;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.CryptoUtils;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Implémentation d'un noeud poste de l'organigramme.
 *
 * @author Fabio Esposito
 */
@Entity(name = "PosteNode")
@Table(name = "POSTE")
public class PosteNodeImpl extends OrganigrammeNodeImpl implements PosteNode {
    private static final long serialVersionUID = -7289101397935565839L;

    private static final STLogger LOGGER = STLogFactory.getLog(PosteNodeImpl.class);

    @Column(name = "ID_PARENT_ENTITE")
    private String parentEntiteId;

    @Column(name = "ID_PARENT_UNITE")
    private String parentUniteId;

    @Column(name = "ID_PARENT_INSTITUTION")
    private String parentInstitId;

    @Column(name = "CHARGE_MISSION_SGG")
    private Boolean chargeMissionSGG;

    @Column(name = "POSTE_BDC")
    private Boolean posteBDC;

    @Column(name = "SUPERVISEUR_SGG")
    private Boolean superviseurSGG;

    @Column(name = "CONSEILLER_PM")
    private Boolean conseillerPM;

    @Column(name = "POSTE_WS")
    private Boolean posteWS;

    @Column(name = "WS_URL")
    private String wsURL;

    @Column(name = "WS_LOGIN")
    private String wsLogin;

    @Column(name = "WS_MDP")
    private String wsMdP;

    @Column(name = "WS_CLE")
    private String wsKeystore;

    @Column(name = "MEMBRES")
    private String membres;

    @Transient
    private List<String> lstMembresCopy = new ArrayList<>();

    @Transient
    private List<UniteStructurelleNode> lstUniteParent = new ArrayList<>();

    @Transient
    private List<EntiteNode> lstEntiteParent = new ArrayList<>();

    @Transient
    private List<InstitutionNode> lstInstitParent = new ArrayList<>();

    @Transient
    private List<String> parentUniteIds = new ArrayList<>();

    @Transient
    private List<String> parentEntiteIds = new ArrayList<>();

    @Transient
    private List<String> parentInstitIds = new ArrayList<>();

    public PosteNodeImpl() {
        super();
    }

    public PosteNodeImpl(PosteNode node) {
        super(node);
        this.chargeMissionSGG = node.isChargeMissionSGG();
        this.conseillerPM = node.isConseillerPM();
        setMembers(node.getMembers());
        this.parentEntiteId = node.getParentEntiteId();
        this.parentUniteId = node.getParentUniteId();
        this.posteBDC = node.isPosteBdc();
        this.posteWS = node.isPosteWs();
        this.superviseurSGG = node.isSuperviseurSGG();
        this.wsKeystore = node.getWsKeyAlias();
        this.wsLogin = node.getWsUser();
        this.wsMdP = node.getWsPassword();
        this.wsURL = node.getWsUrl();
    }

    public String getTypeValue() {
        return getType().getValue();
    }

    @Override
    public OrganigrammeType getType() {
        return OrganigrammeType.POSTE;
    }

    @Override
    public List<UniteStructurelleNode> getUniteStructurelleParentList() {
        if (lstUniteParent == null || lstUniteParent.isEmpty()) {
            final STUsAndDirectionService dirService = STServiceLocator.getSTUsAndDirectionService();
            for (String idParent : getParentUnitIds()) {
                lstUniteParent.add(dirService.getUniteStructurelleNode(idParent));
            }
        }
        return lstUniteParent;
    }

    @Override
    public void setUniteStructurelleParentList(List<UniteStructurelleNode> parentList) {
        this.lstUniteParent = parentList;
    }

    @Override
    public List<EntiteNode> getEntiteParentList() {
        if (lstEntiteParent == null || lstEntiteParent.isEmpty()) {
            final STMinisteresService minService = STServiceLocator.getSTMinisteresService();
            for (String idParent : getParentEntiteIds()) {
                lstEntiteParent.add(minService.getEntiteNode(idParent));
            }
        }
        return lstEntiteParent;
    }

    @Override
    public void setEntiteParentList(List<EntiteNode> parentList) {
        this.lstEntiteParent = parentList;
    }

    @Override
    public List<String> getMembers() {
        if ((lstMembresCopy == null || lstMembresCopy.isEmpty()) && StringUtils.isNotEmpty(membres)) {
            lstMembresCopy = new ArrayList<>(Arrays.asList(membres.split(";")));
        }
        return lstMembresCopy;
    }

    @Override
    public void setMembers(List<String> membersParam) {
        if (membersParam == null || membersParam.isEmpty()) {
            lstMembresCopy.clear();
            membres = "";
        } else {
            // Suppression des doublons grâce à un set. L'ordre n'est pas forcément conservé
            Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            set.addAll(membersParam);
            lstMembresCopy = new ArrayList<>(set);

            membres = StringUtil.join(lstMembresCopy, ";", "");
        }
    }

    @Override
    public int getParentListSize() {
        int size = 0;
        List<UniteStructurelleNode> usParent = getUniteStructurelleParentList();
        if (usParent != null) {
            size += usParent.size();
        }
        List<EntiteNode> entiteParent = getEntiteParentList();
        if (entiteParent != null) {
            size += entiteParent.size();
        }
        return size;
    }

    @Override
    public void setParentList(List<OrganigrammeNode> parentList) {
        parentEntiteId = "";
        parentUniteId = "";
        parentInstitId = "";

        for (OrganigrammeNode parentNode : parentList) {
            if (parentNode instanceof UniteStructurelleNode) {
                lstUniteParent.add((UniteStructurelleNode) parentNode);
                if (StringUtils.isBlank(parentUniteId)) {
                    parentUniteId = parentNode.getId();
                } else {
                    parentUniteId = parentUniteId + ";" + parentNode.getId();
                }
            } else if (parentNode instanceof EntiteNode) {
                lstEntiteParent.add((EntiteNode) parentNode);
                if (StringUtils.isBlank(parentEntiteId)) {
                    parentEntiteId = parentNode.getId();
                } else {
                    parentEntiteId = parentEntiteId + ";" + parentNode.getId();
                }
            } else if (parentNode instanceof InstitutionNode) {
                lstInstitParent.add((InstitutionNode) parentNode);
                if (StringUtils.isBlank(parentInstitId)) {
                    parentInstitId = parentNode.getId();
                } else {
                    parentInstitId = parentInstitId + ";" + parentNode.getId();
                }
            }
        }
    }

    /**
     * @return the chargeMissionSGG
     */
    @Override
    public boolean isChargeMissionSGG() {
        return toBoolean(chargeMissionSGG);
    }

    /**
     * @param chargeMissionSGG
     *            the chargeMissionSGG to set
     */
    @Override
    public void setChargeMissionSGG(boolean chargeMissionSGG) {
        this.chargeMissionSGG = chargeMissionSGG;
    }

    /**
     * @return the conseillerPM
     */
    @Override
    public boolean isConseillerPM() {
        return toBoolean(conseillerPM);
    }

    /**
     * @param conseillerPM
     *            the conseillerPM to set
     */
    @Override
    public void setConseillerPM(boolean conseillerPM) {
        this.conseillerPM = conseillerPM;
    }

    @Override
    public List<STUser> getUserList() {
        List<STUser> userList = new ArrayList<>();
        List<String> memberList = getMembers();

        final UserManager userManager = STServiceLocator.getUserManager();
        for (String memberId : memberList) {
            DocumentModel userDoc = userManager.getUserModel(memberId);
            if (userDoc == null) {
                LOGGER.warn(
                    STLogEnumImpl.FAIL_GET_USER_TEC,
                    "L'utilisateur avec l'id " + memberId + " est dans le poste " + getId() + " mais n'existe pas"
                );
                continue;
            }
            STUser user = userDoc.getAdapter(STUser.class);
            if (user.isActive()) {
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public int getUserListSize() {
        int size = 0;
        List<String> memberList = getMembers();
        if (memberList != null) {
            size = memberList.size();
        }
        return size;
    }

    @Override
    public boolean isPosteBdc() {
        return toBoolean(posteBDC);
    }

    @Override
    public void setPosteBdc(boolean posteBdc) {
        this.posteBDC = posteBdc;
    }

    @Override
    public void setWsUrl(String wsUrl) {
        this.wsURL = wsUrl;
    }

    @Override
    public void setWsUser(String wsUser) {
        this.wsLogin = wsUser;
    }

    @Override
    public void setWsPassword(String wsPassword) {
        this.wsMdP = CryptoUtils.encodeValue(wsPassword);
    }

    @Override
    public void setWsKeyAlias(String keyAlias) {
        this.wsKeystore = keyAlias;
    }

    @Override
    public String getWsUrl() {
        return wsURL;
    }

    @Override
    public String getWsUser() {
        return wsLogin;
    }

    @Override
    public String getWsPassword() {
        return CryptoUtils.decodeValue(this.wsMdP);
    }

    @Override
    public String getWsKeyAlias() {
        return wsKeystore;
    }

    @Override
    public boolean isPosteWs() {
        return toBoolean(posteWS);
    }

    @Override
    public void setPosteWs(boolean posteWs) {
        this.posteWS = posteWs;
    }

    @Override
    public String getParentId() {
        if (parentEntiteId != null) {
            return parentEntiteId;
        } else {
            return parentUniteId;
        }
    }

    @Override
    public boolean isSuperviseurSGG() {
        return toBoolean(superviseurSGG);
    }

    @Override
    public void setSuperviseurSGG(boolean superviseurSGG) {
        this.superviseurSGG = superviseurSGG;
    }

    @Override
    public String getParentEntiteId() {
        return parentEntiteId;
    }

    @Override
    public void setParentEntiteId(String parentEntiteId) {
        this.parentEntiteId = parentEntiteId;
    }

    @Override
    public String getParentUniteId() {
        return parentUniteId;
    }

    @Override
    public void setParentUniteId(String parentUniteId) {
        this.parentUniteId = parentUniteId;
    }

    @Override
    public List<String> getParentUnitIds() {
        if (StringUtils.isNotBlank(parentUniteId) && CollectionUtils.isEmpty(parentUniteIds)) {
            String[] arrayParent = parentUniteId.split(";");
            parentUniteIds = new ArrayList<>(Arrays.asList(arrayParent));
        }
        return parentUniteIds;
    }

    @Override
    public void setParentUnitIds(List<String> list) {
        this.parentUniteIds = list;
        if (list != null && !list.isEmpty()) {
            parentUniteId = StringUtil.join(list, ";", "");
        } else {
            parentUniteId = "";
        }
    }

    @Override
    public List<String> getParentEntiteIds() {
        if (StringUtils.isNotBlank(parentEntiteId) && CollectionUtils.isEmpty(parentEntiteIds)) {
            String[] arrayParent = parentEntiteId.split(";");
            parentEntiteIds = new ArrayList<>(Arrays.asList(arrayParent));
        }
        return parentEntiteIds;
    }

    @Override
    public void setParentEntiteIds(List<String> list) {
        this.parentEntiteIds = list;
        if (list != null && !list.isEmpty()) {
            parentEntiteId = StringUtil.join(list, ";", "");
        } else {
            parentEntiteId = "";
        }
    }

    @Override
    public List<InstitutionNode> getInstitutionParentList() {
        if (lstInstitParent == null || lstInstitParent.isEmpty()) {
            final OrganigrammeService orgaService = STServiceLocator.getOrganigrammeService();
            for (String idParent : getParentInstitIds()) {
                lstInstitParent.add(
                    (InstitutionNode) orgaService.getOrganigrammeNodeById(idParent, OrganigrammeType.INSTITUTION)
                );
            }
        }
        return lstInstitParent;
    }

    @Override
    public void setInstitutionParentList(List<InstitutionNode> instututionParentList) {
        lstInstitParent = instututionParentList;
    }

    @Override
    public List<String> getParentInstitIds() {
        if (StringUtils.isNotBlank(parentInstitId) && CollectionUtils.isEmpty(parentInstitIds)) {
            String[] arrayParent = parentInstitId.split(";");
            parentInstitIds = new ArrayList<>(Arrays.asList(arrayParent));
        }
        return parentInstitIds;
    }

    @Override
    public void setParentInstitIds(List<String> list) {
        this.parentInstitIds = list;
        if (list != null && !list.isEmpty()) {
            parentInstitId = StringUtils.join(list, ";");
        } else {
            parentInstitId = "";
        }
    }

    @Override
    public EntiteNode getFirstEntiteParent() {
        List<String> parentEntIds = getParentEntiteIds();
        if (CollectionUtils.isNotEmpty(parentEntIds)) {
            return STServiceLocator.getSTMinisteresService().getEntiteNode(parentEntIds.get(0));
        }
        return null;
    }

    @Override
    public UniteStructurelleNode getFirstUSParent() {
        List<String> parentEntIds = getParentUnitIds();
        if (CollectionUtils.isNotEmpty(parentEntIds)) {
            return STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(parentEntIds.get(0));
        }
        return null;
    }
}
