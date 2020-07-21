package fr.dila.organigramme.xml.converter.element;

import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;

/**
 * Base de l'organigramme Crée les 3 branches principales
 * 
 * @author Fabio Esposito
 * 
 */
public class Organigramme extends BaseElement {

	public Organigramme() {
		super();
		parent = null;
	}

	@Override
	public String getDN() {
		return null;
	}

	@Override
	public String getRDN() {
		return null;
	}

	@Override
	public String getLdifValue() {
		ldifValue = "dn: " + OrganigrammeConstant.BASE_DN_GOUVERNEMENT
				+ "\nobjectClass: top\nobjectClass: organizationalUnit\nou: " + OrganigrammeConstant.GOUVERNEMENT
				+ "\n\n" + "dn: " + OrganigrammeConstant.BASE_DN_UNITE_STRUCTURELLE
				+ "\nobjectClass: top\nobjectClass: organizationalUnit\nou: " + OrganigrammeConstant.UNITE_STRUCTURELLE
				+ "\n\n" + "dn: " + OrganigrammeConstant.BASE_DN_POSTE
				+ "\nobjectClass: top\nobjectClass: organizationalUnit\nou: " + OrganigrammeConstant.POSTE + "\n\n";

		return ldifValue;
	}

}
