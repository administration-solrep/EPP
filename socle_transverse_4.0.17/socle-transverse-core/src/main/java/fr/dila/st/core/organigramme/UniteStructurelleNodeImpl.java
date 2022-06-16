package fr.dila.st.core.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.NorDirection;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Représentation d'un noeud unité structurelle de l'organigramme.
 *
 * @author Fabio Esposito
 */
@Entity(name = "UniteStructurelleNode")
@Table(name = "UNITE_STRUCTURELLE")
public class UniteStructurelleNodeImpl extends OrganigrammeNodeImpl implements UniteStructurelleNode {
    /**
     *
     */
    private static final long serialVersionUID = -8153545559801388966L;

    @Column(name = "ID_PARENT_ENTITE")
    private String parentEntiteId;

    @Column(name = "ID_PARENT_UNITE")
    private String parentUniteId;

    @Column(name = "ID_PARENT_INSTITUTION")
    private String parentInstitId;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "NOR_DIRECTION")
    protected String norDirectionMember;

    @Transient
    private List<UniteStructurelleNode> lstUniteEnfant;

    @Transient
    private List<PosteNode> lstPosteEnfant;

    @Transient
    protected List<UniteStructurelleNode> lstUniteParent = new ArrayList<>();

    @Transient
    protected List<EntiteNode> lstEntiteParent = new ArrayList<>();

    @Transient
    private List<InstitutionNode> lstInstitParent = new ArrayList<>();

    @Transient
    protected List<String> parentUnitIds = new ArrayList<>();

    @Transient
    protected List<String> parentEntiteIds = new ArrayList<>();

    @Transient
    private List<String> parentInstitIds = new ArrayList<>();

    public UniteStructurelleNodeImpl() {}

    public UniteStructurelleNodeImpl(UniteStructurelleNode node) {
        super(node);
        this.type = node.getType().getValue();
        this.parentEntiteId = node.getParentEntiteId();
        this.parentUniteId = node.getParentUniteId();
        this.lstPosteEnfant = node.getSubPostesList();
        this.lstUniteEnfant = node.getSubUnitesStructurellesList();
        setNorDirectionList(node.getNorDirectionList());
        getParentEntiteIds();
        getParentUnitIds();
    }

    @Override
    public OrganigrammeType getType() {
        if (OrganigrammeType.MINISTERE.getValue().equals(type)) {
            return OrganigrammeType.MINISTERE;
        } else if (OrganigrammeType.DIRECTION.getValue().equals(type)) {
            return OrganigrammeType.DIRECTION;
        } else {
            return OrganigrammeType.UNITE_STRUCTURELLE;
        }
    }

    @Override
    public void setType(OrganigrammeType type) {
        this.type = Optional.ofNullable(type).map(OrganigrammeType::getValue).orElse(null);
    }

    @Override
    public List<UniteStructurelleNode> getSubUnitesStructurellesList() {
        if (lstUniteEnfant == null) {
            lstUniteEnfant =
                STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleEnfant(getId(), getType());
        }
        return lstUniteEnfant;
    }

    @Override
    public void setSubUnitesStructurellesList(List<UniteStructurelleNode> subUnitesStructurellesList) {
        lstUniteEnfant = subUnitesStructurellesList;
    }

    @Override
    public List<PosteNode> getSubPostesList() {
        if (lstPosteEnfant == null) {
            lstPosteEnfant = STServiceLocator.getSTPostesService().getPosteNodeEnfant(getId(), getType());
        }
        return lstPosteEnfant;
    }

    @Override
    public void setSubPostesList(List<PosteNode> subPostesList) {
        lstPosteEnfant = subPostesList;
    }

    @Override
    public List<UniteStructurelleNode> getUniteStructurelleParentList() {
        lstUniteParent.clear();
        final STUsAndDirectionService dirService = STServiceLocator.getSTUsAndDirectionService();
        List<String> lstUnitParentIds = getParentUnitIds();
        if (lstUnitParentIds != null) {
            for (String idParent : lstUnitParentIds) {
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
        lstEntiteParent.clear();
        final STMinisteresService minService = STServiceLocator.getSTMinisteresService();
        for (String idParent : getParentEntiteIds()) {
            lstEntiteParent.add(minService.getEntiteNode(idParent));
        }
        return lstEntiteParent;
    }

    @Override
    public void setEntiteParentList(List<EntiteNode> parentList) {
        this.lstEntiteParent = parentList;
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

    @Override
    public boolean containsBDC() {
        if (lstPosteEnfant == null) {
            getSubPostesList();
        }
        for (PosteNode poste : lstPosteEnfant) {
            if (poste.isPosteBdc() && !poste.getDeleted()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setTypeValue(String type) {
        this.type = type;
    }

    @Override
    public String getTypeValue() {
        return type;
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
    public List<String> getParentUnitIds() {
        if (CollectionUtils.isEmpty(parentUnitIds) && StringUtils.isNotBlank(parentUniteId)) {
            parentUnitIds = Arrays.asList(parentUniteId.split(";"));
        }
        return parentUnitIds;
    }

    @Override
    public void setParentUnitIds(List<String> list) {
        this.parentUnitIds = list;
        if (list != null && !list.isEmpty()) {
            parentUniteId = StringUtil.join(list, ";", "");
        } else {
            parentUniteId = "";
        }
    }

    @Override
    public List<String> getParentEntiteIds() {
        if (CollectionUtils.isEmpty(parentEntiteIds) && StringUtils.isNotBlank(parentEntiteId)) {
            parentEntiteIds = Arrays.asList(parentEntiteId.split(";"));
        }
        return parentEntiteIds;
    }

    @Override
    public void setParentEntiteIds(List<String> parentEntiteIds) {
        this.parentEntiteIds = parentEntiteIds;
        if (parentEntiteIds != null && !parentEntiteIds.isEmpty()) {
            parentEntiteId = StringUtil.join(parentEntiteIds, ";", "");
        } else {
            parentEntiteId = "";
        }
    }

    @Override
    public List<NorDirection> getNorDirectionList() {
        List<NorDirection> norRefList = new ArrayList<>();
        if (norDirectionMember != null) {
            List<String> norRefStringList = Arrays.asList(norDirectionMember.split(";"));
            if (norRefStringList != null && !norRefStringList.isEmpty()) {
                for (String norRefString : norRefStringList) {
                    if (StringUtils.isNotBlank(norRefString)) {
                        norRefList.add(new NorDirection(norRefString));
                    }
                }
            }
        }
        return norRefList;
    }

    @Override
    public void setNorDirectionList(List<NorDirection> norDirectionList) {
        StringBuilder builder = new StringBuilder();
        for (NorDirection norRef : norDirectionList) {
            builder.append(norRef.getNorJSON());
            builder.append(";");
        }
        norDirectionMember = builder.toString();
    }

    @Override
    public String getNorDirectionForMinistereId(String ministereId) {
        List<NorDirection> norRefList = getNorDirectionList();
        if (StringUtils.isNotEmpty(ministereId)) {
            for (NorDirection norRef : norRefList) {
                if (ministereId.equals(norRef.getId())) {
                    return norRef.getNor();
                }
            }
        }
        return "";
    }

    @Override
    public String getNorDirection(String ministereId) {
        List<NorDirection> norRefList = getNorDirectionList();
        if (StringUtils.isNotEmpty(ministereId)) {
            for (NorDirection norRef : norRefList) {
                if (ministereId.equals(norRef.getId())) {
                    return norRef.getNor();
                }
            }
            if (norRefList != null && !norRefList.isEmpty()) {
                return norRefList.get(0).getNor();
            }
        }
        return "";
    }

    @Override
    public void setNorDirectionForMinistereId(String ministereId, String nor) {
        // On met à jour la liste des nor direction existante
        List<NorDirection> norRefList = getNorDirectionList();
        // On regarde d'abord si l'entrée pour le ministère id existe déjà
        // pour pouvoir mettre à jour la lettre
        for (NorDirection norRef : norRefList) {
            if (ministereId.equals(norRef.getId())) {
                norRef.setNor(nor);
                setNorDirectionList(norRefList);
                return;
            }
        }

        // Sinon on ajoute la nouvelle entrée à la liste
        norRefList.add(new NorDirection(ministereId, nor));
        setNorDirectionList(norRefList);
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
        if (CollectionUtils.isEmpty(parentInstitIds) && StringUtils.isNotBlank(parentInstitId)) {
            parentInstitIds = Arrays.asList(parentInstitId.split(";"));
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
    public String getLabelWithNor(String idMinistere) {
        return Stream
            .of(getNorDirection(idMinistere), getLabel())
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(" - "));
    }
}
