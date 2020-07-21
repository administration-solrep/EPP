package fr.dila.organigramme.xml.converter.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONStringer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.dila.organigramme.xml.converter.Attribute;
import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;

/**
 * Noeud unité structurelle de l'organigramme
 * 
 * @author Fabio Esposito
 * 
 */
public class UniteStructurelle extends BaseElement {

	public static final String	ID				= "ID";
	public static final String	NOR				= "NOR";

	public String[]				classesUst		= { "top", "uniteStructurelle", "groupOfUniqueNames" };
	public String[]				classesEntite	= { "top", "entite", "groupOfUniqueNames" };

	private String				norReference;
	private String				labelUniteStructurelle;
	private int					ordre;
	private static final String	CN				= "cn=";
	private static final Log	LOGGER			= LogFactory.getLog(UniteStructurelle.class);

	public UniteStructurelle(Element element, BaseElement parent) {
		super(element, parent);

		if (element != null) {
			this.parent = parent;
			NodeList list = element.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Object object = list.item(i);
				if (!(object instanceof Element)) {
					continue;
				}

				Element item = (Element) object;
				if (OrganigrammeConstant.TAG_CODE.equals(item.getNodeName())) {
					norReference = item.getChildNodes().item(0).getNodeValue();
				}
			}
		}
	}

	public String getNorReference() {
		return norReference;
	}

	public void setNorReference(String norReference) {
		this.norReference = norReference;
	}

	public String getLabelUniteStructurelle() {
		return labelUniteStructurelle;
	}

	public void setLabelUniteStructurelle(String labelUniteStructurelle) {
		this.labelUniteStructurelle = labelUniteStructurelle;
	}

	public String getBranchEpg() {
		if (parent.getParent() == null) {
			return OrganigrammeConstant.ENTITE;
		} else {
			return OrganigrammeConstant.UNITE_STRUCTURELLE;
		}
	}

	public String getLdifValue() {

		String[] classes = null;
		String type = "type";
		String uniqueMember = "uniqueMember";
		String aRenseigner = "à renseigner";

		List<Attribute> attributes = new ArrayList<Attribute>();

		attributes.add(new Attribute("cn", id));
		attributes.add(new Attribute("label", nom));
		attributes.add(new Attribute("dateDebut", convertDate(debutValidite)));
		attributes.add(new Attribute("dateFin", convertDate(finValidite)));
		attributes.add(new Attribute("deleted", "FALSE"));

		if (parent.getParent() == null) {
			if (OrganigrammeConstant.REPRISE.equals(OrganigrammeConstant.REPONSES)) {
				attributes.add(new Attribute(type, "MIN"));
				classes = classesUst;
			} else if (OrganigrammeConstant.REPRISE.equals(OrganigrammeConstant.EPG)) {
				classes = classesEntite;

				attributes.add(new Attribute("ordre", Integer.toString(ordre)));
				attributes.add(new Attribute("edition", aRenseigner));
				attributes.add(new Attribute("formule", aRenseigner));
				attributes.add(new Attribute("membreGouvernement", aRenseigner));
			}

			attributes.add(new Attribute("norMinistere", norReference));
		} else {
			classes = classesUst;
			if (parent.getParent().getParent() == null) {
				attributes.add(new Attribute(type, "DIR"));
				attributes.add(new Attribute("norDirectionMember", serializeNOR(parent.getId(), norReference)));
			} else {
				attributes.add(new Attribute(type, "AUT"));
			}
		}

		for (BaseElement be : childs) {
			attributes.add(new Attribute(uniqueMember, be.getDN()));
		}

		if (childs.isEmpty()) {
			attributes.add(new Attribute(uniqueMember, "cn=emptyRef"));
		}

		ldifValue = generateDN(getDN(), Arrays.asList(classes), attributes);
		return ldifValue;
	}

	public String getDN() {
		String distinguishedName = null;
		if (OrganigrammeConstant.REPRISE.equals(OrganigrammeConstant.REPONSES)) {
			distinguishedName = CN + id + "," + OrganigrammeConstant.BASE_DN_UNITE_STRUCTURELLE;
		} else if (OrganigrammeConstant.REPRISE.equals(OrganigrammeConstant.EPG)) {
			if (getBranchEpg().equals(OrganigrammeConstant.UNITE_STRUCTURELLE)) {
				distinguishedName = CN + id + "," + OrganigrammeConstant.BASE_DN_UNITE_STRUCTURELLE;
			} else if (getBranchEpg().equals(OrganigrammeConstant.ENTITE)) {
				distinguishedName = CN + id + "," + OrganigrammeConstant.BASE_DN_ENTITE;
			}
		}
		return distinguishedName;
	}

	public String getRDN() {
		return CN + id;
	}

	private String serializeNOR(String parentId, String norRef) {

		String json = null;
		try {
			json = new JSONStringer().object().key(ID).value(parentId).key(NOR).value(norRef).endObject().toString();
		} catch (JSONException e) {
			LOGGER.error("Error nor serialization", e);
		}
		return json;
	}

	/**
	 * @return the ordre
	 */
	public int getOrdre() {
		return ordre;
	}

	/**
	 * @param ordre
	 *            the ordre to set
	 */
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

}
