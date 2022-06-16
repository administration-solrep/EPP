package fr.dila.ss.core.service;

import fr.dila.ss.api.service.SSProfilUtilisateurService;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STProfilUtilisateur;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.AbstractSTProfilUtilisateurServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

public class SSProfilUtilisateurServiceImpl<T extends STProfilUtilisateur>
    extends AbstractSTProfilUtilisateurServiceImpl<T>
    implements SSProfilUtilisateurService<T> {
    /**
     * Nombre d'éléments max dans la liste des derniers dossiers consultés
     */
    private static final int NB_DOSSIERS_MAX = 10;

    /**
     * Séparateur dans la liste des dossiers dernièrement consultés
     */
    private static final String IDS_DOSSIERS_SEP = ",";

    private static final STLogger LOGGER = STLogFactory.getLog(SSProfilUtilisateurServiceImpl.class);

    @Override
    public void addDossierToListDerniersDossierIntervention(final CoreSession session, final String idDossier) {
        try {
            final DocumentModel userProfilDoc = getOrCreateCurrentUserProfil(session);
            STProfilUtilisateur userProfil = userProfilDoc.getAdapter(STProfilUtilisateur.class);
            String dossiersIds = userProfil.getDerniersDossiersIntervention();

            if (StringUtils.isBlank(dossiersIds)) {
                // la liste est pour l'instant vide donc on rajoute notre dossier courant
                userProfil.setDerniersDossiersIntervention(idDossier);
            } else {
                // Convertir la liste d'ids concaténés en list
                List<String> dossiersIdsList = Stream
                    .of(dossiersIds.split(IDS_DOSSIERS_SEP))
                    .collect(Collectors.toList());

                int index = dossiersIdsList.indexOf(idDossier);
                if (index != 0) {
                    if (index > 0) {
                        dossiersIdsList.remove(index);
                    }
                    dossiersIdsList.add(0, idDossier);
                }

                // On vérifie la taille maximum de la liste
                if (dossiersIdsList.size() > NB_DOSSIERS_MAX) {
                    dossiersIdsList.remove(NB_DOSSIERS_MAX);
                }

                userProfil.setDerniersDossiersIntervention(String.join(IDS_DOSSIERS_SEP, dossiersIdsList));
            }
            session.saveDocument(userProfil.getDocument());
        } catch (NuxeoException e) {
            // là on logge que ça s'est mal passé, et c'est tout
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_GET_DOCUMENT_TEC,
                "Erreur lors de la récupération des derniers dossiers sur lesquels l'utilisateur est intervenu",
                e
            );
        }
    }

    /**
     * Récupère la liste des derniers dossiers traités par l'utilisateur sous forme de liste de documentModel
     */
    @Override
    public List<DocumentModel> getListeDerniersDossiersIntervention(final CoreSession session) {
        try {
            final DocumentModel userProfilDoc = getOrCreateCurrentUserProfil(session);
            STProfilUtilisateur userProfil = userProfilDoc.getAdapter(STProfilUtilisateur.class);
            // Récupération de la liste des ids des documents
            String dossiersIds = userProfil.getDerniersDossiersIntervention();
            if (StringUtils.isNotBlank(dossiersIds)) {
                return Stream
                    .of(dossiersIds.split(IDS_DOSSIERS_SEP))
                    .filter(StringUtils::isNotBlank)
                    .map(IdRef::new)
                    .filter(session::exists)
                    .map(session::getDocument)
                    .collect(Collectors.toList());
            }
        } catch (NuxeoException e) {
            // plantage lors de la récupération du profil utilisateur. On logge l'erreur
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_GET_DOCUMENT_TEC,
                "Erreur lors de la récupération des derniers dossiers sur lesquels l'utilisateur est intervenu",
                e
            );
        }
        return new ArrayList<>();
    }

    @Override
    protected String getReminderMDPQuery(String nxqlDateInf, String nxqlDateSup) {
        StringBuilder query = new StringBuilder("SELECT * FROM ")
            .append(STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DOCUMENT_TYPE)
            .append(" WHERE ")
            .append("(pusr:dernierChangementMotDePasse >= DATE '")
            .append(nxqlDateInf)
            .append("')")
            .append(" AND (pusr:dernierChangementMotDePasse <= DATE '")
            .append(nxqlDateSup)
            .append("')");
        return query.toString();
    }

    @Override
    protected DocumentModel getProfilUtilisateurDocFromWorkspace(CoreSession session, DocumentModel userWorkspaceDoc) {
        return userWorkspaceDoc;
    }
}
