package fr.sword.naiad.nuxeo.commons.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.types.Type;

/**
 * Classe utilitaire permettant la manipulation des types de documents Nuxeo.
 * 
 * @author fmh
 */
public final class DocumentTypeUtil {
	private static final int ESTIMATED_MAX_DOCTYPE_HIERARCHY_DEPTH = 5;

	/**
	 * Constructeur privé.
	 */
	private DocumentTypeUtil() {
		super();
	}

	/**
	 * Retourne l'ensemble des types du document, y compris les types hérités et
	 * son propre type.
	 * 
	 * @param document
	 *            DocumentModel du document.
	 * @return Ensemble des types du document.
	 */
	public static Set<String> typeHierarchy(DocumentModel document) {
		Set<String> docTypes = new HashSet<String>();

		for (Type type : document.getDocumentType().getTypeHierarchy()) {
			docTypes.add(type.getName());
		}

		docTypes.add(document.getType());

		return docTypes;
	}

	/**
	 * Vérifie si un document possède bien un type de document Nuxeo parmi sa
	 * hiérarchie de types.
	 * 
	 * @param document
	 *            DocumentModel du document à tester.
	 * @param type
	 *            Type à rechercher.
	 * @return true si le document est bien du type recherché, false sinon.
	 */
	public static boolean hasType(DocumentModel document, String type) {
		return typeHierarchy(document).contains(type);
	}

	/**
	 * Vérifie si un document possède au moins un des types de document Nuxeo
	 * spécifiés parmi sa hiérarchie de types.
	 * 
	 * @param document
	 *            DocumentModel du document à tester.
	 * @param types
	 *            Types à rechercher.
	 * @return true si le document possède au moins l'un des types recherchés,
	 *         false sinon.
	 */
	public static boolean hasOneType(DocumentModel document, Collection<String> types) {
		Set<String> typeHierarchy = typeHierarchy(document);

		for (String type : types) {
			if (typeHierarchy.contains(type)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retourne la hierarchie de type d'un document. En ordonnant les types
	 * suivant l'héritage : du plus proche au plus lointain parents
	 * 
	 * @param document
	 * @return les types parent du type du document
	 */
	public static List<String> orderedTypeHierarchy(DocumentModel document) {
		List<String> docTypes = new ArrayList<String>(
				ESTIMATED_MAX_DOCTYPE_HIERARCHY_DEPTH);
		docTypes.add(document.getType());
		for (Type type : document.getDocumentType().getTypeHierarchy()) {
			docTypes.add(type.getName());
		}
		return docTypes;

	}
}
