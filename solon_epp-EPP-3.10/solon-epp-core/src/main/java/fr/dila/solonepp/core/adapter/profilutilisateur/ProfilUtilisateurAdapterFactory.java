package fr.dila.solonepp.core.adapter.profilutilisateur;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.solonepp.api.constant.SolonEppProfilUtilisateurConstants;
import fr.dila.solonepp.core.administration.ProfilUtilisateurImpl;

public class ProfilUtilisateurAdapterFactory implements DocumentAdapterFactory {

	protected void checkDocument(DocumentModel doc) {
		if (!doc.hasSchema(SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA)) {
			throw new CaseManagementRuntimeException("Document should contain schema "
					+ SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA);
		}
	}

	@Override
	public Object getAdapter(DocumentModel doc, Class<?> arg1) {
		checkDocument(doc);
		return new ProfilUtilisateurImpl(doc);
	}

}
