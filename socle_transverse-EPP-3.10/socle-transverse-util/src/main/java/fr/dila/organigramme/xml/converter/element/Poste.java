package fr.dila.organigramme.xml.converter.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import fr.dila.organigramme.xml.converter.Attribute;
import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;

/**
 * Noeud poste de l'organigramme
 * 
 * @author Fabio Esposito
 * 
 */
public class Poste extends BaseElement {

	protected String	code;

	public String[]		classes	= { "top", "poste", "groupOfUniqueNames" };

	public Poste(Element element, BaseElement parent) {
		super(element, parent);
		if (element != null) {
			code = element.getAttribute(OrganigrammeConstant.TAG_CODE);
		}
	}

	public Poste() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLdifValue() {

		List<Attribute> attributes = new ArrayList<Attribute>();

		attributes.add(new Attribute("cn", id));
		attributes.add(new Attribute("label", nom));
		attributes.add(new Attribute("dateDebut", convertDate(debutValidite)));
		attributes.add(new Attribute("dateFin", convertDate(finValidite)));
		attributes.add(new Attribute("deleted", "FALSE"));

		for (BaseElement be : childs) {
			attributes.add(new Attribute("uniqueMember", be.getDN()));
		}

		if (childs.isEmpty()) {
			attributes.add(new Attribute("uniqueMember", "cn=emptyRef"));
		}
		ldifValue = generateDN(getDN(), Arrays.asList(classes), attributes);
		return ldifValue;
	}

	public String getDN() {
		return "cn=" + id + "," + OrganigrammeConstant.BASE_DN_POSTE;
	}

	public String getRDN() {
		return "cn=" + id;
	}
}
