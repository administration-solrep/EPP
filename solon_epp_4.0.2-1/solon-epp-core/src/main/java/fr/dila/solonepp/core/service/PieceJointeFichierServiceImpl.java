package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.dao.criteria.PieceJointeCriteria;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.PieceJointeFichierService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation du service des fichiers des pièces jointes.
 *
 * @author jtremeaux
 */
public class PieceJointeFichierServiceImpl implements PieceJointeFichierService {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -4134242185525569442L;
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(PieceJointeFichierServiceImpl.class);
    /**
     * Document racine des fichiers de pièces jointes.
     */
    private static DocumentModel pieceJointeFichierRootDoc;

    @Override
    public DocumentModel getPieceJointeFichierRoot(final CoreSession session) {
        if (pieceJointeFichierRootDoc != null) {
            return pieceJointeFichierRootDoc;
        }
        synchronized (this) {
            final StringBuilder sb = new StringBuilder("SELECT pjr.ecm:uuid as id FROM ")
                .append(SolonEppConstant.PIECE_JOINTE_FICHIER_ROOT_DOC_TYPE)
                .append(" AS pjr");
            final String[] params = new String[] {};
            final List<DocumentModel> list = QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(
                session,
                sb.toString(),
                params,
                1,
                0
            );
            if (list == null || list.size() <= 0) {
                throw new NuxeoException("Racine des fichiers de pièces jointes non trouvée");
            }

            pieceJointeFichierRootDoc = list.get(0);
            return pieceJointeFichierRootDoc;
        }
    }

    @Override
    public DocumentModel createBarePieceJointeFichier(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE);
    }

    @Override
    public List<DocumentModel> updatePieceJointeFichierList(
        final CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel pieceJointeDoc,
        final List<DocumentModel> pieceJointeFichierDocList,
        final boolean strictMode
    ) {
        // Récupère les UUID des fichiers de la pièce jointe actuelle
        final PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
        final List<String> currentPieceJointeFichierIdList = pieceJointe.getPieceJointeFichierList();

        // Charge les documents des fichiers de la pièce jointe actuelle
        final List<DocumentModel> currentPieceJointeFichierList = QueryUtils.retrieveDocuments(
            session,
            SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE,
            currentPieceJointeFichierIdList
        );

        EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        String typeEvenement = evenement.getTypeEvenement();

        // Construit l'ensemble des noms des nouveaux fichiers de pièces jointes
        final Set<String> pieceJointeFichierFilenameSet = new HashSet<String>();
        for (final DocumentModel pieceJointeFichierDoc : pieceJointeFichierDocList) {
            final PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
            pieceJointeFichierFilenameSet.add(pieceJointeFichier.getSafeFilename());
        }

        // Classe les fichiers par nom de fichier
        final Map<String, DocumentModel> currentPieceJointeFichierMap = new HashMap<String, DocumentModel>();
        for (final DocumentModel pieceJointeFichierDoc : currentPieceJointeFichierList) {
            final PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
            currentPieceJointeFichierMap.put(pieceJointeFichier.getSafeFilename(), pieceJointeFichierDoc);
        }

        // Supprime les fichiers de pièce jointe qui ne sont plus dans la liste si non obligatoire
        for (final DocumentModel pieceJointeFichierDoc : currentPieceJointeFichierList) {
            final PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
            if (!pieceJointeFichierFilenameSet.contains(pieceJointeFichier.getSafeFilename())) {
                if (
                    !(
                        pieceJointe.getModifiedFileList() != null &&
                        pieceJointe.getModifiedFileList().contains(pieceJointeFichier.getSafeFilename())
                    ) &&
                    evenementTypeService.isPieceJointeObligatoire(typeEvenement, pieceJointe.getTypePieceJointe())
                ) {
                    if (strictMode) {
                        throw new NuxeoException(
                            "Il est interdit de supprimer le fichier de pièce jointe obligatoire : " +
                            pieceJointeFichier.getSafeFilename()
                        );
                    }
                }
                // Supprime le fichier de pièce jointe si il n'est utilisé dans aucune autre pièce jointe
                removePieceJointeFichier(session, pieceJointeDoc, pieceJointeFichierDoc.getId());
            }
        }

        // Crée / modifie les fichiers de pièces jointes
        final List<DocumentModel> pieceJointeFichierAfterUpdateList = new ArrayList<DocumentModel>();
        for (final DocumentModel pieceJointeFichierDoc : pieceJointeFichierDocList) {
            final PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);

            final DocumentModel currentPieceJointeFichierDoc = currentPieceJointeFichierMap.get(
                pieceJointeFichier.getSafeFilename()
            );
            if (currentPieceJointeFichierDoc == null) {
                // Crée le nouveau fichier de pièce jointe
                final DocumentModel pieceJointeFichierCreatedDoc = createPieceJointeFichier(
                    session,
                    evenementDoc,
                    pieceJointeFichierDoc
                );
                pieceJointeFichierAfterUpdateList.add(pieceJointeFichierCreatedDoc);
            } else {
                final PieceJointeFichier currentPieceJointeFichier = currentPieceJointeFichierDoc.getAdapter(
                    PieceJointeFichier.class
                );

                // prise en compte des modifications de fichier
                if (!currentPieceJointeFichier.getDigestSha512().equals(pieceJointeFichier.getDigestSha512())) {
                    if (
                        !(
                            pieceJointe.getModifiedFileList() != null &&
                            pieceJointe.getModifiedFileList().contains(pieceJointeFichier.getSafeFilename())
                        ) &&
                        evenementTypeService.isPieceJointeObligatoire(typeEvenement, pieceJointe.getTypePieceJointe())
                    ) {
                        if (strictMode) {
                            throw new NuxeoException(
                                "Il est interdit de modifier le fichier de pièce jointe obligatoire : " +
                                pieceJointeFichier.getSafeFilename()
                            );
                        }
                    }
                    pieceJointe.getModifiedFileList().add(pieceJointeFichier.getTitle());

                    // Le fichier de pièce jointe est modifié : créer un nouveau document
                    final DocumentModel pieceJointeFichierCreatedDoc = createPieceJointeFichier(
                        session,
                        evenementDoc,
                        pieceJointeFichierDoc
                    );
                    pieceJointeFichierAfterUpdateList.add(pieceJointeFichierCreatedDoc);

                    // Supprime le fichier de pièce jointe si il n'est utilisé dans aucune autre pièce jointe
                    removePieceJointeFichier(session, pieceJointeDoc, currentPieceJointeFichierDoc.getId());
                } else {
                    // Le fichier de pièce jointe n'est pas modifié
                    pieceJointeFichierAfterUpdateList.add(currentPieceJointeFichierDoc);
                }
            }
        }

        return pieceJointeFichierAfterUpdateList;
    }

    @Override
    public DocumentModel createPieceJointeFichier(
        final CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel pieceJointeFichierDoc
    ) {
        final PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);

        // Vérifie les données du fichier de pièce jointe
        if (StringUtils.isBlank(pieceJointeFichier.getSafeFilename())) {
            throw new NuxeoException("Le nom du fichier de pièce jointe est obligatoire");
        }

        if (pieceJointeFichier.getContent() == null) {
            throw new NuxeoException("Le contenu du fichier de pièce jointe est obligatoire");
        }
        if (pieceJointeFichier.getContent().getDigest() == null) {
            // Dans le cas d'une création de pièce jointe, il faut que le contenu soit disponible
            // (fourni par le client) et le digest calculé (pris en charge par les services intermédiaires)
            throw new IllegalStateException("Création de pièce jointe demandée sur un blob sans digest");
        }

        LOGGER.info(
            session,
            STLogEnumImpl.CREATE_PIECE_JOINTE_TEC,
            "Pièce jointe: " + pieceJointeFichier.getSafeFilename() + " Communication :" + evenementDoc.getTitle()
        );

        // Renseigne le chemin et le nom du document
        final DocumentModel pieceJointeFichierRootDoc = getPieceJointeFichierRoot(session);
        final String evenementTitle = evenementDoc.getTitle();
        final String docTitle = evenementTitle + "_" + pieceJointeFichier.getSafeFilename();
        pieceJointeFichier.setTitle(docTitle);
        pieceJointeFichierDoc.setPathInfo(pieceJointeFichierRootDoc.getPathAsString(), docTitle);

        // Crée le document fichier de pièce jointe
        final DocumentModel pieceJointeFichierCreatedDoc = session.createDocument(pieceJointeFichierDoc);

        return pieceJointeFichierCreatedDoc;
    }

    @Override
    public void removePieceJointeFichier(
        final CoreSession session,
        final DocumentModel pieceJointeDoc,
        final String pieceJointeFichierId
    ) {
        final PieceJointeCriteria pieceJointeCriteria = new PieceJointeCriteria();
        pieceJointeCriteria.setIdNot(pieceJointeDoc.getId());
        pieceJointeCriteria.setPieceJointeFichierId(pieceJointeFichierId);

        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        final List<DocumentModel> pieceJointeList = pieceJointeService.findPieceJointeByCriteria(
            session,
            pieceJointeCriteria
        );
        if (pieceJointeList.isEmpty()) {
            try {
                session.removeDocument(new IdRef(pieceJointeFichierId));
            } catch (final java.lang.NullPointerException e) {
                LOGGER.warn(session, STLogEnumImpl.FAIL_DEL_PIECE_JOINTE_TEC, "Piece Jointe" + pieceJointeFichierId, e);
            }
        }
    }

    @Override
    public List<DocumentModel> findAllPieceJointeFichier(
        final CoreSession session,
        final DocumentModel pieceJointeDoc
    ) {
        final PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
        final List<DocumentModel> pieceJointeFichierDocList = new ArrayList<DocumentModel>();
        final List<String> pieceJointeFichierIdList = pieceJointe.getPieceJointeFichierList();

        for (final String pieceJointeFichierId : pieceJointeFichierIdList) {
            pieceJointeFichierDocList.add(session.getDocument(new IdRef(pieceJointeFichierId)));
        }

        return pieceJointeFichierDocList;
    }
}
