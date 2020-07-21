package fr.dila.organigramme.xml.converter.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.dila.organigramme.xml.converter.Attribute;
import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;

/**
 * Noeud Racine de l'organigramme
 * 
 * @author Fabio Esposito
 * 
 */
public class Gouvernement extends BaseElement {

	public String[]	classes	= { "top", "gouvernement", "groupOfUniqueNames" };

	public Gouvernement() {
		id = "gouv_1";
		nom = "Gouvernement Fillon VII";
		debutValidite = "14/11/2010";
		finValidite = "31/12/9999";
		parent = null;
	}

	@Override
	public String getDN() {
		return "cn=" + id + "," + OrganigrammeConstant.BASE_DN_GOUVERNEMENT;
	}

	@Override
	public String getRDN() {
		return "cn=" + id;
	}

	@Override
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

}
