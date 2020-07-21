package fr.dila.organigramme.xml.converter.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.dila.organigramme.xml.converter.Attribute;
import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;

/**
 * Agent enregistré dans la branche people
 * 
 * @author Fabio Esposito
 * 
 */
public class Agent extends BaseElement {

	public static final String	UID			= "uid";
	public String[]				classes		= { "top", "personne", "person", "organizationalPerson", "inetOrgPerson" };

	protected String			prenom;

	private ArrayList<String>	profilList	= new ArrayList<String>();

	public Agent(Element element, BaseElement parent) {
		super(element, parent);

		if (element != null) {
			NodeList list = element.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Object o = list.item(i);
				if (!(o instanceof Element)) {
					continue;
				}

				Element item = (Element) o;

				if (OrganigrammeConstant.TAG_Z_ADM_ENT.equals(item.getNodeName())) {
					profilList.add(OrganigrammeConstant.TAG_Z_ADM_ENT);
				} else if (OrganigrammeConstant.TAG_Z_CONT_ENT.equals(item.getNodeName())) {
					profilList.add(OrganigrammeConstant.TAG_Z_CONT_ENT);
				} else if (OrganigrammeConstant.TAG_Z_CONT_SGG.equals(item.getNodeName())) {
					profilList.add(OrganigrammeConstant.TAG_Z_CONT_SGG);
				} else if (OrganigrammeConstant.TAG_Z_RM_ADMIN.equals(item.getNodeName())) {
					profilList.add(OrganigrammeConstant.TAG_Z_RM_ADMIN);
				} else if (OrganigrammeConstant.TAG_Z_VIG_ENT.equals(item.getNodeName())) {
					profilList.add(OrganigrammeConstant.TAG_Z_VIG_ENT);
				} else if (OrganigrammeConstant.TAG_PRENOM.equals(item.getNodeName())) {
					prenom = item.getChildNodes().item(0).getNodeValue();
				}
			}
		}
	}

	/**
	 * @return the profilList
	 */
	public ArrayList<String> getProfilList() {
		return profilList;
	}

	/**
	 * @param profilList
	 *            the profilList to set
	 */
	public void setProfilList(ArrayList<String> profilList) {
		this.profilList = profilList;
	}

	public String getLdifValue() {

		List<Attribute> attributes = new ArrayList<Attribute>();

		attributes.add(new Attribute(UID, id));

		if (prenom != null && !"".equals(prenom)) {
			attributes.add(new Attribute("givenName", prenom));
		}
		attributes.add(new Attribute("sn", nom));
		attributes.add(new Attribute("cn", nom));
		attributes.add(new Attribute("dateDebut", convertDate(debutValidite)));
		attributes.add(new Attribute("dateFin", convertDate(finValidite)));
		attributes.add(new Attribute("occasional", "FALSE"));
		attributes.add(new Attribute("temporary", "FALSE"));
		attributes.add(new Attribute("userPassword", id));
		attributes.add(new Attribute("deleted", "FALSE"));

		ldifValue = generateDN(getDN(), Arrays.asList(classes), attributes);
		return ldifValue;
	}

	public String getDN() {
		return UID + "=" + id + "," + OrganigrammeConstant.BASE_DN_PEOPLE;
	}

	public String getRDN() {
		return UID + "=" + id;
	}
}
