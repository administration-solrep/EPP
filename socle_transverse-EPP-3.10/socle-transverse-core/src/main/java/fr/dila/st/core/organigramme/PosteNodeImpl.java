package fr.dila.st.core.organigramme;

import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.nuxeo.common.utils.Base64;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

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
import fr.dila.st.core.util.StringUtil;

/**
 * Implémentation d'un noeud poste de l'organigramme.
 * 
 * @author Fabio Esposito
 */
@Entity(name = "PosteNode")
@Table(name = "POSTE")
public class PosteNodeImpl extends OrganigrammeNodeImpl implements PosteNode {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -7289101397935565839L;

	private static final STLogger			LOGGER				= STLogFactory.getLog(PosteNodeImpl.class);

	@Column(name = "ID_PARENT_ENTITE")
	private String						parentEntiteId;

	@Column(name = "ID_PARENT_UNITE")
	private String						parentUniteId;

	@Column(name = "ID_PARENT_INSTITUTION")
	private String						parentInstitId;

	@Column(name = "CHARGE_MISSION_SGG")
	private Boolean						chargeMissionSGG;

	@Column(name = "POSTE_BDC")
	private Boolean						posteBDC;

	@Column(name = "SUPERVISEUR_SGG")
	private Boolean						superviseurSGG;

	@Column(name = "CONSEILLER_PM")
	private Boolean						conseillerPM;

	@Column(name = "POSTE_WS")
	private Boolean						posteWS;

	@Column(name = "WS_URL")
	private String						wsURL;

	@Column(name = "WS_LOGIN")
	private String						wsLogin;

	@Column(name = "WS_MDP")
	private String						wsMdP;

	@Column(name = "WS_CLE")
	private String						wsKeystore;

	@Column(name = "MEMBRES")
	private String						membres;

	@Transient
	private List<String>				lstMembresCopy		= new ArrayList<String>();

	@Transient
	private List<UniteStructurelleNode>	lstUniteParent		= new ArrayList<UniteStructurelleNode>();

	@Transient
	private List<EntiteNode>			lstEntiteParent		= new ArrayList<EntiteNode>();

	@Transient
	private List<InstitutionNode>		lstInstitParent		= new ArrayList<InstitutionNode>();

	@Transient
	private List<String>				parentUniteIds		= new ArrayList<String>();

	@Transient
	private List<String>				parentEntiteIds		= new ArrayList<String>();

	@Transient
	private List<String>				parentInstitIds		= new ArrayList<String>();

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
	public List<UniteStructurelleNode> getUniteStructurelleParentList() throws ClientException {
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
	public List<EntiteNode> getEntiteParentList() throws ClientException {
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
		if ((lstMembresCopy == null || lstMembresCopy.isEmpty()) && membres != null) {
			lstMembresCopy = new ArrayList<String>(Arrays.asList(membres.split(";")));
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
			Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			set.addAll(membersParam);
			lstMembresCopy = new ArrayList<String>(set);

			membres = StringUtil.join(lstMembresCopy, ";", "");

		}
	}

	@Override
	public int getParentListSize() throws ClientException {
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
				if (StringUtil.isBlank(parentUniteId)) {
					parentUniteId = parentNode.getId();
				} else {
					parentUniteId = parentUniteId + ";" + parentNode.getId();
				}
			} else if (parentNode instanceof EntiteNode) {
				lstEntiteParent.add((EntiteNode) parentNode);
				if (StringUtil.isBlank(parentEntiteId)) {
					parentEntiteId = parentNode.getId();
				} else {
					parentEntiteId = parentEntiteId + ";" + parentNode.getId();
				}
			} else if (parentNode instanceof InstitutionNode) {
				lstInstitParent.add((InstitutionNode) parentNode);
				if (StringUtil.isBlank(parentInstitId)) {
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
		return chargeMissionSGG == null ? false : chargeMissionSGG;
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
		return conseillerPM == null ? false : conseillerPM;
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
	public List<STUser> getUserList() throws ClientException {
		List<STUser> userList = new ArrayList<STUser>();
		List<String> memberList = getMembers();

		final UserManager userManager = STServiceLocator.getUserManager();
		for (String memberId : memberList) {

			DocumentModel userDoc = userManager.getUserModel(memberId);
			if (userDoc == null) {
				LOGGER.warn(STLogEnumImpl.FAIL_GET_USER_TEC, "L'utilisateur avec l'id " + memberId + " est dans le poste " + getId()
						+ " mais n'existe pas la branche people");
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
		return posteBDC == null ? false : posteBDC;
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
		Cipher cipher;
		byte[] valueStored;
		try {
			// IV length: must be 16 bytes long
			IvParameterSpec iv = new IvParameterSpec("Randominit param".getBytes("UTF-8"));
			SecretKeySpec skeySpec = getSecretKey();
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			valueStored = cipher.doFinal(wsPassword.getBytes());
			this.wsMdP = Base64.encodeBytes(valueStored);
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_UPDATE_POSTE_TEC, e);
		}
	}
	
	private SecretKeySpec getSecretKey() {
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec("A random passphrase to help securing this".toCharArray(), "Salt is a movie with Angelina Jolie".getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			return new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_GENERATE_KEY_TEC, e);
		} 
		return null;
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
		if (this.wsMdP != null) {
			try {
				// IV length: must be 16 bytes long
				IvParameterSpec iv = new IvParameterSpec("Randominit param".getBytes("UTF-8"));
				SecretKeySpec skeySpec = getSecretKey();
	
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	
				byte[] original = cipher.doFinal(Base64.decode(this.wsMdP));
	
				return new String(original);
			} catch (Exception ex) {
				LOGGER.error(STLogEnumImpl.FAIL_GET_META_DONNEE_TEC, ex);
			}
		}
		return null;
		
	}

	@Override
	public String getWsKeyAlias() {
		return wsKeystore;
	}

	@Override
	public boolean isPosteWs() {
		return posteWS == null ? false : posteWS;
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
		return superviseurSGG == null ? false : superviseurSGG;
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
		if (StringUtil.isNotBlank(parentUniteId) && (parentUniteIds == null || parentUniteIds.isEmpty())) {
			String[] arrayParent = parentUniteId.split(";");
			parentUniteIds = new ArrayList<String>(Arrays.asList(arrayParent));
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
		if (StringUtil.isNotBlank(parentEntiteId) && (parentEntiteIds == null || parentEntiteIds.isEmpty())) {
			String[] arrayParent = parentEntiteId.split(";");
			parentEntiteIds = new ArrayList<String>(Arrays.asList(arrayParent));
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
	public List<InstitutionNode> getInstitutionParentList() throws ClientException {
		if (lstInstitParent == null || lstInstitParent.isEmpty()) {
			final OrganigrammeService orgaService = STServiceLocator.getOrganigrammeService();
			for (String idParent : getParentInstitIds()) {
				lstInstitParent.add((InstitutionNode) orgaService.getOrganigrammeNodeById(idParent,
						OrganigrammeType.INSTITUTION));
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
		if (StringUtil.isNotBlank(parentInstitId) && (parentInstitIds == null || parentInstitIds.isEmpty())) {
			String[] arrayParent = parentInstitId.split(";");
			parentInstitIds = new ArrayList<String>(Arrays.asList(arrayParent));
		}
		return parentInstitIds;
	}

	@Override
	public void setParentInstitIds(List<String> list) {
		this.parentInstitIds = list;
		if (list != null && !list.isEmpty()) {
			parentInstitId = StringUtil.join(list, ";", "");
		} else {
			parentInstitId = "";
		}
	}
}
