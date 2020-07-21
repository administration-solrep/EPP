package fr.dila.organigramme.xml.converter.element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.dila.organigramme.xml.converter.Attribute;
import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;

/**
 * Element père des éléments d'organigramme
 * 
 * @author Fabio Esposito
 * 
 */
public abstract class BaseElement {

	private static final Log	LOGGER				= LogFactory.getLog(BaseElement.class);

	static final String			OBJECT_CLASS		= "objectClass";
	static final String			DISTINGUISHED_NAME	= "dn";
	static final String			NEW_LINE			= "\n";
	static final String			COLON				= ": ";

	protected BaseElement		parent;
	protected List<BaseElement>	childs;

	protected String			id;
	protected String			nom;
	protected String			debutValidite;
	protected String			finValidite;
	protected String			ldifValue;

	public BaseElement() {
		childs = new ArrayList<BaseElement>();
	}

	public BaseElement(Element element, BaseElement parent) {
		if (element != null) {
			this.parent = parent;
			NodeList childNodeList = element.getChildNodes();
			for (int i = 0; i < childNodeList.getLength(); i++) {
				Object childNode = childNodeList.item(i);
				if (!(childNode instanceof Element)) {
					continue;
				}

				Element item = (Element) childNode;
				if (OrganigrammeConstant.TAG_ID.equals(item.getNodeName())) {
					id = item.getChildNodes().item(0).getNodeValue();
				}
				if (OrganigrammeConstant.TAG_NOM.equals(item.getNodeName())) {
					nom = item.getChildNodes().item(0).getNodeValue();
				}
				if (OrganigrammeConstant.TAG_DEBUT_VALIDITE.equals(item.getNodeName())) {
					debutValidite = item.getChildNodes().item(0).getNodeValue();
				}
				if (OrganigrammeConstant.TAG_FIN_VALIDITE.equals(item.getNodeName())) {
					finValidite = item.getChildNodes().item(0).getNodeValue();
				}
				childs = new ArrayList<BaseElement>();
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDebutValidite() {
		return debutValidite;
	}

	public void setDebutValidite(String debutValidite) {
		this.debutValidite = debutValidite;
	}

	public String getFinValidite() {
		return finValidite;
	}

	public void setFinValidite(String finValidite) {
		this.finValidite = finValidite;
	}

	public BaseElement getParent() {
		return parent;
	}

	public void setParent(BaseElement parent) {
		this.parent = parent;
	}

	public List<BaseElement> getChilds() {
		return childs;
	}

	public void setChilds(List<BaseElement> childs) {
		this.childs = childs;
	}

	public boolean add(BaseElement element) {
		return childs.add(element);
	}

	/**
	 * Crée la commande ldif pour un dn
	 * 
	 * @param distinguishedName
	 * @param classes
	 * @param attributes
	 * @return
	 * @author Fabio Esposito
	 */
	public static String generateDN(String distinguishedName, List<String> classes, List<Attribute> attributes) {

		StringBuilder stringBuilder = new StringBuilder();

		// ajout du dn
		stringBuilder.append(DISTINGUISHED_NAME + COLON + distinguishedName + NEW_LINE);

		// ajout des objectClass
		for (String s : classes) {
			stringBuilder.append(OBJECT_CLASS + COLON + s + NEW_LINE);
		}

		// ajout des autres attributs

		for (Attribute attribute : attributes) {
			stringBuilder.append(attribute);
		}

		stringBuilder.append(NEW_LINE);

		return stringBuilder.toString();
	}

	public abstract String getDN();

	public abstract String getRDN();

	public abstract String getLdifValue();

	public String convertDate(String date) {
		String formatedDate = null;
		if (date != null) {
			SimpleDateFormat sdfIn = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdfOut = new SimpleDateFormat("yyyyMMdd000000");

			try {
				Date tmpDate = sdfIn.parse(date);
				formatedDate = sdfOut.format(tmpDate);
			} catch (ParseException e) {
				LOGGER.error("Erreur de parsing date", e);
			}
		}
		return formatedDate + "Z";
	}
}
