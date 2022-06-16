package fr.dila.solonepp.core.adapter.profilutilisateur;

import fr.dila.solonepp.api.constant.SolonEppProfilUtilisateurConstants;
import fr.dila.solonepp.core.administration.ProfilUtilisateurImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ProfilUtilisateurAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA);
        return new ProfilUtilisateurImpl(doc);
    }
}
