package fr.dila.organigramme.xml.converter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;
import fr.dila.organigramme.xml.converter.element.Agent;
import fr.dila.organigramme.xml.converter.element.BaseElement;
import fr.dila.organigramme.xml.converter.element.Gouvernement;
import fr.dila.organigramme.xml.converter.element.Poste;
import fr.dila.organigramme.xml.converter.element.UniteStructurelle;

/**
 * Point d'entrée du convertisseur XML vers LDIF
 * 
 * @author Fabio Esposito
 * 
 */
public class XmlToLdifConverter {

	public static final String						FILE_PATH			= "src/main/resources/Organigramme_SOLON.xml";

	/**
	 * Logger.
	 */
	private static final Log						LOGGER				= LogFactory.getLog(XmlToLdifConverter.class);

	private static Set<String>						users				= new TreeSet<String>();
	private static Set<String>						postes				= new TreeSet<String>();
	private static Set<String>						unitesStructurelles	= new TreeSet<String>();

	private static Map<String, ArrayList<String>>	profils				= new HashMap<String, ArrayList<String>>();

	/**
	 * @param args
	 * @author Fabio Esposito
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		profils.put(OrganigrammeConstant.TAG_Z_ADM_ENT, new ArrayList<String>());
		profils.put(OrganigrammeConstant.TAG_Z_CONT_ENT, new ArrayList<String>());
		profils.put(OrganigrammeConstant.TAG_Z_CONT_SGG, new ArrayList<String>());
		profils.put(OrganigrammeConstant.TAG_Z_RM_ADMIN, new ArrayList<String>());
		profils.put(OrganigrammeConstant.TAG_Z_VIG_ENT, new ArrayList<String>());

		String filePath;
		if (args != null && args.length > 0) {
			filePath = args[0];
		} else {
			filePath = FILE_PATH;
		}
		// Chargement du XML
		OrganigrammeDOM orgDOM = new OrganigrammeDOM();
		BaseElement organigramme = orgDOM.getOrganigrammeFromXML(filePath);

		List<BaseElement> posteList = new ArrayList<BaseElement>();

		if (OrganigrammeConstant.REPRISE.equals(OrganigrammeConstant.EPG)) {
			triEntite(organigramme);
			posteList.addAll(addPosteReprise(organigramme));
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n# LISTE DES POSTES DE REPRISE");
		stringBuilder.append("\n#");
		for (BaseElement poste : posteList) {
			stringBuilder.append("\n# " + poste.getNom());
			stringBuilder.append(" : " + poste.getId());
		}

		// Parcours de l'arbre et écriture du fichier ldif
		FileWriter writer = null;
		try {
			writer = new FileWriter("organigramme.ldif", false);
			String value = organigramme.getLdifValue();
			// Write value in file
			writer.write(value, 0, value.length());

			writeUniqueEntry(writer, organigramme);

			if (OrganigrammeConstant.REPRISE.equals(OrganigrammeConstant.EPG)) {
				// ecris la liste des users
				writeUsersProfileList(writer);
				// écrit la liste des postes de reprise
				value = stringBuilder.toString();
				writer.write(value, 0, value.length());
			}

		} catch (IOException ex) {
			LOGGER.error("Erreur dans l'écriture du fichier ldif de l'organigramme. ", ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Ecrit la liste des users en commentaire
	 * 
	 * @param writer
	 * @throws IOException
	 */
	// private static void writeUsersList(FileWriter writer) throws IOException {
	// String value;
	// StringBuilder sb = new StringBuilder();
	//
	// for (String id : users) {
	// sb.append("#uniqueMember: uid=")
	// .append(id)
	// .append(",ou=people,ou=SolonEpg,dc=dila,dc=fr\n");
	// value = sb.toString();
	// writer.write(value, 0, value.length());
	// sb.setLength(0);
	// }
	// }

	/**
	 * Ecrit la liste des users en commentaire classé par profils
	 * 
	 * @param writer
	 * @throws IOException
	 */
	private static void writeUsersProfileList(FileWriter writer) throws IOException {
		String value;
		StringBuilder stringBuilder = new StringBuilder();

		for (String profil : profils.keySet()) {

			List<String> agentList = profils.get(profil);

			stringBuilder.append("\n# Profil : " + profil);
			stringBuilder.append("\n");
			value = stringBuilder.toString();
			writer.write(value, 0, value.length());
			stringBuilder.setLength(0);

			for (String id : agentList) {
				stringBuilder.append("#uniqueMember: uid=").append(id).append(",ou=people,ou=SolonEpg,dc=dila,dc=fr\n");
				value = stringBuilder.toString();
				writer.write(value, 0, value.length());
				stringBuilder.setLength(0);
			}
		}
	}

	/**
	 * Tri des ministères par ordre alphabetique
	 * 
	 * @param organigramme
	 */
	private static void triEntite(BaseElement organigramme) {

		List<BaseElement> childsGvt = organigramme.getChilds();

		List<BaseElement> childs = childsGvt.get(0).getChilds();

		Collections.sort(childs, new Comparator<BaseElement>() {
			@Override
			public int compare(BaseElement elt1, BaseElement elt2) {
				return elt1.getNom().compareTo(elt2.getNom());
			}
		});

		int ordre = 1;
		for (BaseElement baseElt : childs) {
			if (baseElt instanceof UniteStructurelle) {
				UniteStructurelle uniteStruct = (UniteStructurelle) baseElt;
				uniteStruct.setOrdre(ordre);
				ordre++;
			}
		}
	}

	/**
	 * Liste des postes de reprise
	 * 
	 * @param organigramme
	 */
	private static List<BaseElement> addPosteReprise(BaseElement organigramme) {

		List<BaseElement> childsGvt = organigramme.getChilds();
		List<BaseElement> childs = childsGvt.get(0).getChilds();

		List<BaseElement> posteList = new ArrayList<BaseElement>();

		String posteId = "poste-reprise-";
		String posteName = "Poste de reprise ";
		for (BaseElement baseElt : childs) {
			if (baseElt instanceof UniteStructurelle) {
				UniteStructurelle uniteStruct = (UniteStructurelle) baseElt;

				Poste poste = new Poste();
				poste.setId(posteId + uniteStruct.getId());
				poste.setNom(posteName + uniteStruct.getNom());

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String formatedDate = sdf.format(new Date());
				poste.setDebutValidite(formatedDate);
				poste.setFinValidite("31/12/9999");
				posteList.add(poste);
				uniteStruct.add(poste);
			}
		}

		return posteList;
	}

	/**
	 * parcours récursif de l'arbre
	 * 
	 * @param writer
	 * @param baseElt
	 * @throws IOException
	 * @author Fabio Esposito
	 */
	private static void writeUniqueEntry(FileWriter writer, BaseElement baseElt) throws IOException {

		// Ajout de l'élément sauf s'il a déjà été ajouté
		if (baseElt instanceof Agent) {
			if (!users.contains(baseElt.getId())) {
				String value = baseElt.getLdifValue();
				writer.write(value, 0, value.length());
				users.add(baseElt.getId());

				Agent agent = (Agent) baseElt;
				for (String profil : agent.getProfilList()) {
					profils.get(profil).add(agent.getId());
				}
			}
		} else if (baseElt instanceof UniteStructurelle) {
			if (!unitesStructurelles.contains(baseElt.getId())) {
				String value = baseElt.getLdifValue();
				writer.write(value, 0, value.length());
				unitesStructurelles.add(baseElt.getId());
			}
		} else if (baseElt instanceof Poste) {
			if (!postes.contains(baseElt.getId())) {
				String value = baseElt.getLdifValue();
				writer.write(value, 0, value.length());
				postes.add(baseElt.getId());
			}
		} else if (baseElt instanceof Gouvernement) {
			// if(!postes.contains(be.getId())) {
			String value = baseElt.getLdifValue();
			writer.write(value, 0, value.length());
			// postes.add(be.getId());
			// }
		}

		for (BaseElement beChilds : baseElt.getChilds()) {
			writeUniqueEntry(writer, beChilds);
		}

	}

}
