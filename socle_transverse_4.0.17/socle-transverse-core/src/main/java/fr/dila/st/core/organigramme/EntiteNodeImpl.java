package fr.dila.st.core.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;

/**
 * Représentation d'un noeud ministère de l'organigramme.
 *
 * @author jtremeaux
 */
@Entity(name = "EntiteNode")
@Table(name = "ENTITE")
public class EntiteNodeImpl extends OrganigrammeNodeImpl implements EntiteNode {
    /**
     *
     */
    private static final long serialVersionUID = -2738117495623774686L;

    @Column(name = "ID_PARENT_GOUV")
    private String parentId;

    @Column(name = "EDITION")
    private String edition;

    @Column(name = "FORMULE")
    private String formule;

    @Column(name = "CIVILITE_MINISTRE")
    private String civiliteMembre;

    @Column(name = "NOM_MINISTRE")
    private String nomMembre;

    @Column(name = "PRENOM_MINISTRE")
    private String prenomMembre;

    @Column(name = "ORDRE")
    private Long ordre;

    @Column(name = "NOR_MINISTERE")
    private String norMinistere;

    @Column(name = "SUIVI_ACTIVITE_NORMATIVE")
    private Boolean suiviAN;

    @Transient
    private List<UniteStructurelleNode> lstSubUnite;

    @Transient
    private List<PosteNode> lstSubPoste;

    @Transient
    private GouvernementNode gvtParent;

    public EntiteNodeImpl() {}

    public EntiteNodeImpl(EntiteNode node) {
        super(node);
        this.civiliteMembre = node.getMembreGouvernementCivilite();
        this.edition = node.getEdition();
        this.formule = node.getFormule();
        this.lstSubPoste = node.getSubPostesList();
        this.lstSubUnite = node.getSubUnitesStructurellesList();
        this.nomMembre = node.getMembreGouvernementNom();
        this.ordre = node.getOrdre();
        this.prenomMembre = node.getMembreGouvernementPrenom();
        this.parentId = node.getParentId();
        this.norMinistere = node.getNorMinistere();
        this.suiviAN = node.isSuiviActiviteNormative();

        if (StringUtils.isNotBlank(node.getMembreGouvernement())) {
            this.nomMembre = node.getMembreGouvernement();
        }
    }

    @Override
    public void setParentList(List<OrganigrammeNode> parentList) {
        OrganigrammeNode node = parentList.get(0);
        if (node != null && node instanceof GouvernementNode) {
            setParentGouvernement(node.getId());
        }
    }

    @Override
    public int getParentListSize() {
        return 1;
    }

    @Override
    public GouvernementNode getParentGouvernement() {
        if (gvtParent == null) {
            gvtParent = STServiceLocator.getSTGouvernementService().getGouvernement(parentId);
        }
        return gvtParent;
    }

    @Override
    public void setParentGouvernement(String parentGouvernementId) {
        this.parentId = parentGouvernementId;
    }

    /**
     * @return the edition
     */
    @Override
    public String getEdition() {
        return edition;
    }

    /**
     * @param edition
     *            the edition to set
     */
    @Override
    public void setEdition(String edition) {
        this.edition = edition;
    }

    /**
     * @return the ordre
     */
    @Override
    public Long getOrdre() {
        return ordre;
    }

    /**
     * @param ordre
     *            the ordre to set
     */
    @Override
    public void setOrdre(Long ordre) {
        this.ordre = ordre;
    }

    /**
     * @return the formule
     */
    @Override
    public String getFormule() {
        return formule;
    }

    /**
     * @param formule
     *            the formule to set
     */
    @Override
    public void setFormule(String formule) {
        this.formule = formule;
    }

    /**
     * @return the membreGouvernement
     */
    @Override
    public String getMembreGouvernement() {
        if (!StringUtils.isBlank(prenomMembre) && !StringUtils.isBlank(nomMembre)) {
            return prenomMembre + " " + nomMembre;
        } else {
            if (StringUtils.isNotBlank(prenomMembre)) {
                return prenomMembre;
            } else if (StringUtils.isNotBlank(nomMembre)) {
                return nomMembre;
            }
        }
        return "";
    }

    @Override
    public void setMembreGouvernement(String membreGouvernement) {
        this.nomMembre = membreGouvernement;
    }

    @Override
    public OrganigrammeType getType() {
        return OrganigrammeType.MINISTERE;
    }

    @Override
    public List<UniteStructurelleNode> getSubUnitesStructurellesList() {
        if (lstSubUnite == null) {
            lstSubUnite = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleEnfant(getId(), getType());
        }
        return lstSubUnite;
    }

    @Override
    public void setSubUnitesStructurellesList(List<UniteStructurelleNode> subUnitesStructurellesList) {
        lstSubUnite = subUnitesStructurellesList;
    }

    @Override
    public List<PosteNode> getSubPostesList() {
        if (lstSubPoste == null) {
            lstSubPoste = STServiceLocator.getSTPostesService().getPosteNodeEnfant(getId(), getType());
        }
        return lstSubPoste;
    }

    @Override
    public void setSubPostesList(List<PosteNode> subPostesList) {
        lstSubPoste = subPostesList;
    }

    @Override
    public String getMembreGouvernementNom() {
        return nomMembre;
    }

    @Override
    public void setMembreGouvernementNom(String membreGouvernementNom) {
        this.nomMembre = membreGouvernementNom;
    }

    @Override
    public String getMembreGouvernementPrenom() {
        return prenomMembre;
    }

    @Override
    public void setMembreGouvernementPrenom(String membreGouvernementPrenom) {
        this.prenomMembre = membreGouvernementPrenom;
    }

    @Override
    public String getMembreGouvernementCivilite() {
        return this.civiliteMembre;
    }

    @Override
    public void setMembreGouvernementCivilite(String membreGouvernementCivilite) {
        this.civiliteMembre = membreGouvernementCivilite;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public String getNorMinistere() {
        return norMinistere;
    }

    @Override
    public void setNorMinistere(String norRef) {
        this.norMinistere = norRef;
    }

    @Override
    public boolean isSuiviActiviteNormative() {
        return suiviAN == null ? false : suiviAN;
    }

    @Override
    public void setSuiviActiviteNormative(boolean suiviActiviteNormative) {
        suiviAN = suiviActiviteNormative;
    }

    @Override
    public String getLabelWithNor(String idMinistere) {
        return Stream.of(norMinistere, getLabel()).filter(StringUtils::isNotBlank).collect(Collectors.joining(" - "));
    }
}
