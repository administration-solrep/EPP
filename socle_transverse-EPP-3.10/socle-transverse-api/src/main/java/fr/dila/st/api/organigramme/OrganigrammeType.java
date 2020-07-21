package fr.dila.st.api.organigramme;

import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Type contenu dans le champs ou des noeud de l'organigramme.
 * 
 * @author Fabio Esposito
 */
public enum OrganigrammeType {
	BASE(STSchemaConstant.ORGANIGRAMME_TYPE_BASE), INSTITUTION(STSchemaConstant.ORGANIGRAMME_TYPE_INSTITUTION), MINISTERE(
			STSchemaConstant.ORGANIGRAMME_TYPE_MINISTERE), UNITE_STRUCTURELLE(
			STSchemaConstant.ORGANIGRAMME_TYPE_UNITE_STRUCTURELLE), POSTE(STSchemaConstant.ORGANIGRAMME_TYPE_POSTE), GOUVERNEMENT(
			STSchemaConstant.ORGANIGRAMME_TYPE_GOUVERNEMENT), DIRECTION(STSchemaConstant.ORGANIGRAMME_TYPE_DIRECTION), USER(
			STSchemaConstant.ORGANIGRAMME_TYPE_USER), OTHER(STSchemaConstant.ORGANIGRAMME_TYPE_OTHER);

	private String	value;

	OrganigrammeType(String type) {
		this.setValue(type);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getMessageCode() {
		String message = null;
		if (INSTITUTION.equals(this)) {
			message = "organigramme.type.institution";
		} else if (MINISTERE.equals(this)) {
			message = "organigramme.type.ministere";
		} else if (UNITE_STRUCTURELLE.equals(this)) {
			message = "organigramme.type.uniteStructurelle";
		} else if (POSTE.equals(this)) {
			message = "organigramme.type.poste";
		}

		return message;
	}

	public static OrganigrammeType getEnum(String enumValue) {
		OrganigrammeType type = null;
		if (BASE.value.equals(enumValue)) {
			type = BASE;
		} else if (INSTITUTION.value.equals(enumValue)) {
			type = INSTITUTION;
		} else if (MINISTERE.value.equals(enumValue)) {
			type = MINISTERE;
		} else if (UNITE_STRUCTURELLE.value.equals(enumValue)) {
			type = UNITE_STRUCTURELLE;
		} else if (DIRECTION.value.equals(enumValue)) {
			type = DIRECTION;
		} else if (POSTE.value.equals(enumValue)) {
			type = POSTE;
		} else if (GOUVERNEMENT.value.equals(enumValue)) {
			type = GOUVERNEMENT;
		} else if (USER.value.equals(enumValue)) {
			type = USER;
		}

		return type;
	}
}
