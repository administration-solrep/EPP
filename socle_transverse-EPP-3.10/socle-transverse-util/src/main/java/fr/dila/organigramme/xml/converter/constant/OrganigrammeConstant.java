package fr.dila.organigramme.xml.converter.constant;

/**
 * Interface de constante de l'organigramme utilisées lors de la conversion
 * 
 * @author Fabio Esposito
 * 
 */
public class OrganigrammeConstant {

	public static final String	REPONSES							= "REPONSES";
	public static final String	EPG									= "EPG";

	public static final String	REPRISE								= EPG;

	public static final String	TAG_ORGANIGRAMME					= "ORGANIGRAMME";
	public static final String	TAG_UNITE_STRUCTURELLE				= "UNITE_STRUCTURELLE";
	public static final String	TAG_POSTE							= "POSTE";
	public static final String	TAG_AGENT							= "AGENT";

	public static final String	TAG_ID								= "ID";
	public static final String	TAG_NOM								= "NOM";
	public static final String	TAG_PRENOM							= "PRENOM";
	public static final String	TAG_CODE							= "CODE";
	public static final String	TAG_DEBUT_VALIDITE					= "DEBUT_VALIDITE";
	public static final String	TAG_FIN_VALIDITE					= "FIN_VALIDITE";

	public static final String	TAG_Z_ADM_ENT						= "Z_ADM_ENT";										// administrateur
																														// ministériel
	public static final String	TAG_Z_CONT_ENT						= "Z_CONT_ENT";									// contributeur
																														// ministériel
	public static final String	TAG_Z_CONT_SGG						= "Z_CONT_SGG";									// contributeur
																														// du
																														// SGG
	public static final String	TAG_Z_RM_ADMIN						= "Z_RM_ADMIN";									// administrateur
																														// fonctionnel
	public static final String	TAG_Z_VIG_ENT						= "Z_VIG_ENT";										// vigie
																														// ministériel

	static final String			BASE_DN_POSTE_REPONSES				= "ou=poste,ou=Reponses,dc=dila,dc=fr";
	static final String			BASE_DN_UNITE_STRUCTURELLE_REPONSES	= "ou=uniteStructurelle,ou=Reponses,dc=dila,dc=fr";
	static final String			BASE_DN_GOUVERNEMENT_REPONSES		= "ou=gouvernement,ou=Reponses,dc=dila,dc=fr";
	static final String			BASE_DN_PEOPLE_REPONSES				= "ou=people,ou=Reponses,dc=dila,dc=fr";

	static final String			BASE_DN_POSTE_EPG					= "ou=poste,ou=SolonEpg,dc=dila,dc=fr";
	static final String			BASE_DN_UNITE_STRUCTURELLE_EPG		= "ou=uniteStructurelle,ou=SolonEpg,dc=dila,dc=fr";
	static final String			BASE_DN_GOUVERNEMENT_EPG			= "ou=gouvernement,ou=SolonEpg,dc=dila,dc=fr";
	static final String			BASE_DN_PEOPLE_EPG					= "ou=people,ou=SolonEpg,dc=dila,dc=fr";
	static final String			BASE_DN_ENTITE_EPG					= "ou=entite,ou=SolonEpg,dc=dila,dc=fr";

	public static final String	POSTE								= "poste";
	public static final String	UNITE_STRUCTURELLE					= "uniteStructurelle";
	public static final String	ENTITE								= "entite";
	public static final String	GOUVERNEMENT						= "gouvernement";

	public static String		BASE_DN_POSTE;
	public static String		BASE_DN_UNITE_STRUCTURELLE;
	public static String		BASE_DN_GOUVERNEMENT;
	public static String		BASE_DN_PEOPLE;
	public static String		BASE_DN_ENTITE;

	static {
		if (REPRISE.equals(EPG)) {
			BASE_DN_POSTE = BASE_DN_POSTE_EPG;
			BASE_DN_UNITE_STRUCTURELLE = BASE_DN_UNITE_STRUCTURELLE_EPG;
			BASE_DN_GOUVERNEMENT = BASE_DN_GOUVERNEMENT_EPG;
			BASE_DN_PEOPLE = BASE_DN_PEOPLE_EPG;
			BASE_DN_ENTITE = BASE_DN_ENTITE_EPG;
		} else {
			BASE_DN_POSTE = BASE_DN_POSTE_REPONSES;
			BASE_DN_UNITE_STRUCTURELLE = BASE_DN_UNITE_STRUCTURELLE_REPONSES;
			BASE_DN_GOUVERNEMENT = BASE_DN_GOUVERNEMENT_REPONSES;
			BASE_DN_PEOPLE = BASE_DN_PEOPLE_REPONSES;
		}
	}
}
