package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.PieceJointeCriteria;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.dto.PieceJointeDTO;
import fr.dila.solonepp.api.service.PieceJointeFichierService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.assembler.PieceJointeAssembler;
import fr.dila.solonepp.core.dto.PieceJointeDTOImpl;
import fr.dila.solonepp.core.validator.PieceJointeValidator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.DocUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation du service des pièces jointes des versions.
 *
 * @author jtremeaux
 */
public class PieceJointeServiceImpl implements PieceJointeService {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 7763997555404156120L;
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(PieceJointeServiceImpl.class);

    @Override
    public DocumentModel createBarePieceJointe(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.PIECE_JOINTE_DOC_TYPE);
    }

    @Override
    public List<DocumentModel> getVersionPieceJointeList(final CoreSession session, final DocumentModel versionDoc) {
        return session.getChildren(versionDoc.getRef());
    }

    @Override
    public List<DocumentModel> getVersionPieceJointeListWithFichier(
        final CoreSession session,
        final DocumentModel versionDoc
    ) {
        // Recherche les pièces jointes
        final List<DocumentModel> pieceJointeDocList = getVersionPieceJointeList(session, versionDoc);

        // Recherche les fichiers de pièces jointes
        final PieceJointeFichierService pieceJointeFichierService = SolonEppServiceLocator.getPieceJointeFichierService();
        for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
            final PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
            final List<DocumentModel> pieceJointeFichierDocList = pieceJointeFichierService.findAllPieceJointeFichier(
                session,
                pieceJointeDoc
            );
            pieceJointe.setPieceJointeFichierDocList(pieceJointeFichierDocList);
        }
        return pieceJointeDocList;
    }

    @Override
    public List<DocumentModel> findPieceJointeByCriteria(
        final CoreSession session,
        final PieceJointeCriteria criteria
    ) {
        final StringBuilder query = new StringBuilder("SELECT f.ecm:uuid as id FROM ");
        query.append(SolonEppConstant.PIECE_JOINTE_DOC_TYPE);
        query.append(" as f ");

        final List<String> criterialist = new ArrayList<String>();

        final List<String> paramList = new ArrayList<String>();

        if (StringUtils.isNotBlank(criteria.getIdNot())) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" f.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_UUID_PROPERTY);
            sb.append(" != ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getIdNot());
        }

        if (StringUtils.isNotBlank(criteria.getPieceJointeFichierId())) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" f.");
            sb.append(SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.PIECE_JOINTE_PIECE_JOINTE_FICHIER_LIST_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getPieceJointeFichierId());
        }

        if (StringUtils.isNotBlank(criteria.getTypePieceJointe())) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" f.");
            sb.append(SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.PIECE_JOINTE_TYPE_PIECE_JOINTE_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getTypePieceJointe());
        }

        if (StringUtils.isNotBlank(criteria.getIdVersion())) {
            final StringBuilder sb = new StringBuilder();
            sb.append(" f.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_PARENT_ID_PROPERTY);
            sb.append(" = ? ");

            criterialist.add(sb.toString());
            paramList.add(criteria.getIdVersion());
        }

        if (!criterialist.isEmpty()) {
            query.append(" WHERE ").append(StringUtils.join(criterialist, " AND "));
        }

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            SolonEppConstant.PIECE_JOINTE_DOC_TYPE,
            query.toString(),
            paramList.toArray()
        );
    }

    @Override
    public List<DocumentModel> updatePieceJointeList(
        final CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final List<DocumentModel> pieceJointeDocList,
        final boolean strictMode
    ) {
        // Récupère les UUID des pièces jointes de la version actuelle
        final List<DocumentModel> currentPieceJointeDocList = session.getChildren(versionDoc.getRef());
        final Map<String, DocumentModel> currentPieceJointeDocMap = DocUtil.collectTitleMap(currentPieceJointeDocList);
        final Set<String> currentPieceJointeTitleSet = currentPieceJointeDocMap.keySet();

        // Récupère les UUID des pièces jointes à modifier
        final Set<String> newPieceJointeTitleSet = new HashSet<String>();
        for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
            final PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
            final String title = pieceJointe.getTitle();
            if (!StringUtils.isBlank(title)) {
                newPieceJointeTitleSet.add(pieceJointe.getTitle());
            }
        }

        // Supprime les pièces jointes qui ne sont pas dans la liste fournie
        final Set<String> pieceJointeToRemoveTitleSet = new HashSet<String>();
        for (final String title : currentPieceJointeTitleSet) {
            if (!newPieceJointeTitleSet.contains(title)) {
                pieceJointeToRemoveTitleSet.add(title);
            }
        }

        // Récupère la version précédente publié pour la comparaison
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final DocumentModel previousVersionDoc = versionService.getVersionToCompare(session, evenementDoc, versionDoc);
        Map<String, DocumentModel> previousPieceJointeDocMap = null;
        if (previousVersionDoc != null) {
            final List<DocumentModel> previousPieceJointeDocList = session.getChildren(previousVersionDoc.getRef());
            previousPieceJointeDocMap = DocUtil.collectTitleMap(previousPieceJointeDocList);
        }
        Version version = versionDoc.getAdapter(Version.class);
        final boolean etatBrouillon = version.isEtatBrouillon();

        final PieceJointeValidator pieceJointeValidator = new PieceJointeValidator();
        pieceJointeValidator.validatePieceJointeList(
            evenementDoc,
            previousPieceJointeDocMap,
            currentPieceJointeDocMap,
            pieceJointeDocList,
            pieceJointeToRemoveTitleSet,
            strictMode,
            etatBrouillon
        );

        // Supprime les pièces jointes marquées comme à supprimer
        for (final String title : pieceJointeToRemoveTitleSet) {
            final DocumentModel pieceJointeToRemoveDoc = currentPieceJointeDocMap.get(title);
            session.removeDocument(new IdRef(pieceJointeToRemoveDoc.getId()));
        }

        // Crée les nouvelles pièces jointes / modifie les pièces jointes existantes
        final List<DocumentModel> pieceJointeAfterUpdateList = new ArrayList<DocumentModel>();
        for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
            final PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
            final String title = pieceJointe.getTitle();
            if (StringUtils.isBlank(title)) {
                // Crée la nouvelle pièce jointe
                final DocumentModel pieceJointeCreatedDoc = createPieceJointe(
                    session,
                    evenementDoc,
                    versionDoc,
                    pieceJointeDoc
                );
                pieceJointeAfterUpdateList.add(pieceJointeCreatedDoc);
            } else {
                // Modifie la pièce jointe existante
                final DocumentModel pieceJointeToUpdateDoc = currentPieceJointeDocMap.get(title);
                if (pieceJointeToUpdateDoc == null) {
                    throw new NuxeoException("Pièce jointe inconnue: " + title);
                }
                DocumentModel previousPieceJointeDoc = null;
                if (previousPieceJointeDocMap != null) {
                    previousPieceJointeDoc = previousPieceJointeDocMap.get(pieceJointe.getTitle());
                }
                final DocumentModel pieceJointeUpdateDoc = updatePieceJointe(
                    session,
                    evenementDoc,
                    versionDoc,
                    pieceJointeToUpdateDoc,
                    pieceJointeDoc,
                    previousPieceJointeDoc,
                    strictMode
                );
                pieceJointeAfterUpdateList.add(pieceJointeUpdateDoc);
            }
        }

        // Met à jour la version pour indiquer la présence de pièce(s) jointe(s)
        version = versionDoc.getAdapter(Version.class);
        final boolean pieceJointePresente = !pieceJointeAfterUpdateList.isEmpty();
        version.setPieceJointePresente(pieceJointePresente);
        session.saveDocument(versionDoc);

        return pieceJointeAfterUpdateList;
    }

    /**
     * Compare 2 pieces jointes
     *
     * @param newPieceJointe
     * @param oldPieceJointe
     */
    private List<String> comparePieceJointe(final PieceJointe newPieceJointe, final PieceJointe oldPieceJointe) {
        final List<String> metaList = new ArrayList<String>();
        if (newPieceJointe != null && oldPieceJointe != null) {
            if (!equalsProperty(newPieceJointe.getUrl(), oldPieceJointe.getUrl())) {
                metaList.add(SolonEppSchemaConstant.PIECE_JOINTE_URL_PROPERTY);
            }
            if (!equalsProperty(newPieceJointe.getNom(), oldPieceJointe.getNom())) {
                metaList.add(SolonEppSchemaConstant.PIECE_JOINTE_NOM_PROPERTY);
            }
        }
        return metaList;
    }

    private boolean equalsProperty(final Object propertyValue1, final Object propertyValue2) {
        if (propertyValue1 instanceof String) {
            if (propertyValue1 == null || "".equals(propertyValue1)) {
                if (propertyValue2 == null || "".equals(propertyValue2)) {
                    return true;
                }
            }
        }
        if (propertyValue1 == null) {
            if (propertyValue2 != null) {
                return false;
            } else {
                return true;
            }
        }

        return propertyValue1.equals(propertyValue2);
    }

    @Override
    public DocumentModel createPieceJointe(
        final CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final DocumentModel pieceJointeDoc
    ) {
        LOGGER.info(
            session,
            STLogEnumImpl.CREATE_PIECE_JOINTE_TEC,
            "communication " + evenementDoc.getTitle() + " version " + versionDoc.getTitle()
        );

        final PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
        final Version version = versionDoc.getAdapter(Version.class);
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        final boolean etatBrouillon = version.isEtatBrouillon();

        // Vérifie les données de la pièce jointe
        final PieceJointeValidator pieceJointeValidator = new PieceJointeValidator();
        pieceJointeValidator.validatePieceJointe(pieceJointeDoc, etatBrouillon, evenement.getTypeEvenement());

        // Crée les fichiers de pièces jointes
        final PieceJointeFichierService pieceJointeFichierService = SolonEppServiceLocator.getPieceJointeFichierService();
        final List<DocumentModel> pieceJointeFichierDocList = pieceJointe.getPieceJointeFichierDocList();
        final List<String> modifiedFileList = new ArrayList<String>();
        final List<String> pieceJointeFichierList = new ArrayList<String>();
        if (pieceJointeFichierDocList != null) {
            for (final DocumentModel pieceJointeFichierDoc : pieceJointeFichierDocList) {
                final DocumentModel pieceJointeFichierCreatedDoc = pieceJointeFichierService.createPieceJointeFichier(
                    session,
                    evenementDoc,
                    pieceJointeFichierDoc
                );
                final PieceJointeFichier pieceJointeFichierCreated = pieceJointeFichierCreatedDoc.getAdapter(
                    PieceJointeFichier.class
                );
                pieceJointeFichierList.add(pieceJointeFichierCreatedDoc.getId());
                modifiedFileList.add(pieceJointeFichierCreated.getSafeFilename());
            }
        }
        pieceJointe.setPieceJointeFichierList(pieceJointeFichierList);

        // pieceJointe.setModifiedFileList(modifiedFileList);
        // pieceJointe.setModifiedMetaList(Lists.newArrayList(SolonEppSchemaConstant.PIECE_JOINTE_NOM_PROPERTY));

        // Renseigne le chemin et le nom du document
        final String docTitle = pieceJointe.getTypePieceJointe();
        pieceJointe.setTitle(UUID.randomUUID().toString());
        pieceJointeDoc.setPathInfo(versionDoc.getPathAsString(), docTitle);

        // Crée le document pièce jointe
        final DocumentModel pieceJointeCreatedDoc = session.createDocument(pieceJointeDoc);

        // Renseigne les documents fichiers de pièces jointe dans la pièce jointe
        final PieceJointe pieceJointeCreated = pieceJointeCreatedDoc.getAdapter(PieceJointe.class);
        pieceJointeCreated.setPieceJointeFichierDocList(pieceJointeFichierDocList);

        return pieceJointeCreatedDoc;
    }

    /**
     * Met à jour une pièce jointe d'une version.
     *
     * @param session
     *            Session
     * @param versionDoc
     *            Document version
     * @param pieceJointeToUpdateDoc
     *            Document pièce jointe à mettre à jour
     * @param pieceJointeDoc
     *            Document nouvelle pièce jointe
     * @param previousPieceJointeDoc
     *            Document de la pièce jointe de la version précédante pour comparaison
     * @param strictMode
     *            Interdit la suppression de pièce jointe, et la modification ou suppression de fichiers
     * @return Pièce jointe mise à jour
     */
    protected DocumentModel updatePieceJointe(
        final CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final DocumentModel pieceJointeToUpdateDoc,
        final DocumentModel pieceJointeDoc,
        final DocumentModel previousPieceJointeDoc,
        final boolean strictMode
    ) {
        LOGGER.info(
            session,
            STLogEnumImpl.UPDATE_PIECE_JOINTE_TEC,
            "communication " + evenementDoc.getTitle() + " version " + versionDoc.getTitle()
        );

        final PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
        final PieceJointe pieceJointeToUpdate = pieceJointeToUpdateDoc.getAdapter(PieceJointe.class);

        final Version version = versionDoc.getAdapter(Version.class);
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        final boolean etatBrouillon = version.isEtatBrouillon();

        // comparaison du nom et de l'url
        PieceJointe previousPieceJointe = null;
        if (previousPieceJointeDoc != null) {
            previousPieceJointe = previousPieceJointeDoc.getAdapter(PieceJointe.class);
        }
        final List<String> modifiedMeta = comparePieceJointe(pieceJointe, previousPieceJointe);
        pieceJointeToUpdate.setModifiedMetaList(modifiedMeta);

        // Vérifie les données de la pièce jointe
        final PieceJointeValidator pieceJointeValidator = new PieceJointeValidator();
        pieceJointeValidator.validatePieceJointe(pieceJointeDoc, etatBrouillon, evenement.getTypeEvenement());

        // Met à jour les données de la pièce jointe
        final PieceJointeAssembler pieceJointeAssembler = new PieceJointeAssembler();
        pieceJointeAssembler.assemblePieceJointeForUpdate(pieceJointeDoc, pieceJointeToUpdateDoc);

        // Met à jour les fichiers de pièce jointe
        final PieceJointeFichierService pieceJointeFichierService = SolonEppServiceLocator.getPieceJointeFichierService();
        final List<DocumentModel> pieceJointeFichierDocList = pieceJointeFichierService.updatePieceJointeFichierList(
            session,
            evenementDoc,
            pieceJointeToUpdateDoc,
            pieceJointe.getPieceJointeFichierDocList(),
            strictMode
        );

        // Charge les documents des fichiers de la pièce jointe précédente
        final Map<String, PieceJointeFichier> previousPieceJointeFichierMap = new HashMap<String, PieceJointeFichier>();
        if (previousPieceJointe != null) {
            final List<DocumentModel> previousPieceJointeFichierList = QueryUtils.retrieveDocuments(
                session,
                SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE,
                previousPieceJointe.getPieceJointeFichierList()
            );
            for (final DocumentModel pieceJointeFichierDoc : previousPieceJointeFichierList) {
                final PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(
                    PieceJointeFichier.class
                );
                previousPieceJointeFichierMap.put(pieceJointeFichier.getSafeFilename(), pieceJointeFichier);
            }
        }

        final List<String> modifiedFileList = new ArrayList<String>();
        final List<String> pieceJointeFichierList = new ArrayList<String>();
        for (final DocumentModel pieceJointeFichierDoc : pieceJointeFichierDocList) {
            pieceJointeFichierList.add(pieceJointeFichierDoc.getId());
            final PieceJointeFichier pjf = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
            // comparaison des fichiers de la piece jointe
            if (previousPieceJointe != null) {
                final PieceJointeFichier previousPjf = previousPieceJointeFichierMap.get(pjf.getSafeFilename());
                if (previousPjf != null) {
                    previousPieceJointeFichierMap.remove(previousPjf.getSafeFilename());
                }
                if (previousPjf != null && !pjf.getDigestSha512().equals(previousPjf.getDigestSha512())) {
                    modifiedFileList.add(pjf.getSafeFilename());
                } else if (previousPjf == null) {
                    modifiedFileList.add(pjf.getSafeFilename());
                }
            }
        }
        final List<String> deletedFileList = new ArrayList<String>();
        for (final String pjfName : previousPieceJointeFichierMap.keySet()) {
            deletedFileList.add(pjfName);
        }

        pieceJointeToUpdate.setPieceJointeFichierList(pieceJointeFichierList);
        pieceJointeToUpdate.setModifiedFileList(modifiedFileList);
        pieceJointeToUpdate.setDeletedFileList(deletedFileList);

        // Met à jour le document pièce jointe
        final DocumentModel pieceJointeUpdatedDoc = session.saveDocument(pieceJointeToUpdateDoc);

        // Renseigne les documents fichiers de pièces jointe dans la pièce jointe
        final PieceJointe pieceJointeUpdated = pieceJointeUpdatedDoc.getAdapter(PieceJointe.class);
        pieceJointeUpdated.setPieceJointeFichierDocList(pieceJointeFichierDocList);

        return pieceJointeUpdatedDoc;
    }

    @Override
    public List<PieceJointeDTO> findAllPieceJointeFichierByVersionAndType(
        final String typePieceJointe,
        final String idVersion,
        final CoreSession session
    ) {
        final StringBuilder sb = new StringBuilder("SELECT p.");
        sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
        sb.append(":");
        sb.append(STSchemaConstant.ECM_UUID_PROPERTY);
        sb.append(" as id, p.");
        sb.append(SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA_PREFIX);
        sb.append(":");
        sb.append(SolonEppSchemaConstant.PIECE_JOINTE_PIECE_JOINTE_FICHIER_LIST_PROPERTY);
        sb.append(" as idf ");
        sb.append(" FROM ");
        sb.append(SolonEppConstant.PIECE_JOINTE_DOC_TYPE);
        sb.append(" as p ");
        sb.append(" WHERE p.");
        sb.append(SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA_PREFIX);
        sb.append(":");
        sb.append(SolonEppSchemaConstant.PIECE_JOINTE_TYPE_PIECE_JOINTE_PROPERTY);
        sb.append(" = ? ");
        sb.append(" AND p.");
        sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
        sb.append(":");
        sb.append(STSchemaConstant.ECM_PARENT_ID_PROPERTY);
        sb.append(" = ? ");

        final List<Object> paramList = new ArrayList<Object>();
        paramList.add(typePieceJointe);
        paramList.add(idVersion);

        IterableQueryResult res = null;
        final List<PieceJointeDTO> listResult = new ArrayList<PieceJointeDTO>();
        try {
            res = QueryUtils.doUFNXQLQuery(session, sb.toString(), paramList.toArray());
            final Map<String, PieceJointeDTO> mapResult = new LinkedHashMap<String, PieceJointeDTO>();

            for (final Map<String, Serializable> result : res) {
                final String idPJ = (String) result.get("id");
                final String idPJF = (String) result.get("idf");

                PieceJointeDTO pieceJointeDTO = mapResult.get(idPJ);

                if (pieceJointeDTO == null) {
                    final DocumentModel docPJ = session.getDocument(new IdRef(idPJ));
                    final PieceJointe pieceJointe = docPJ.getAdapter(PieceJointe.class);

                    pieceJointeDTO =
                        new PieceJointeDTOImpl(
                            idPJ,
                            pieceJointe.getNom(),
                            pieceJointe.getUrl(),
                            pieceJointe.getModifiedMetaList(),
                            pieceJointe.getModifiedFileList(),
                            pieceJointe.getDeletedFileList()
                        );
                    mapResult.put(idPJ, pieceJointeDTO);
                    listResult.add(pieceJointeDTO);
                }

                if (StringUtils.isNotBlank(idPJF)) {
                    pieceJointeDTO.addPieceJointeFichier(session.getDocument(new IdRef(idPJF)));
                }
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }

        return listResult;
    }

    @Override
    public List<PieceJointeDTO> getDeletedPieceJointeList(
        final CoreSession session,
        final String typePieceJointe,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        // Récupère les UUID des pièces jointes de la version actuelle
        final List<PieceJointeDTO> currentPieceJointeDocList =
            this.findAllPieceJointeFichierByVersionAndType(typePieceJointe, versionDoc.getId(), session);
        List<PieceJointeDTO> previousPieceJointeDocList = new ArrayList<PieceJointeDTO>();
        final Map<String, PieceJointeDTO> currentPieceJointeDocMap = new HashMap<String, PieceJointeDTO>();
        final List<PieceJointeDTO> pieceJointePublieToRemoveList = new ArrayList<PieceJointeDTO>();

        if (currentPieceJointeDocList != null) {
            for (final PieceJointeDTO pieceJointeDTO : currentPieceJointeDocList) {
                currentPieceJointeDocMap.put(pieceJointeDTO.getPieceJointeTitre(), pieceJointeDTO);
            }

            final Set<String> currentPieceJointeTitleSet = currentPieceJointeDocMap.keySet();
            // Récupère la version précédente publié pour la comparaison
            final VersionService versionService = SolonEppServiceLocator.getVersionService();
            final DocumentModel previousVersionDoc = versionService.getVersionToCompare(
                session,
                evenementDoc,
                versionDoc
            );

            if (previousVersionDoc != null) {
                previousPieceJointeDocList =
                    this.findAllPieceJointeFichierByVersionAndType(
                            typePieceJointe,
                            previousVersionDoc.getId(),
                            session
                        );
            }
            if (previousPieceJointeDocList != null) {
                for (final PieceJointeDTO pieceJointe : previousPieceJointeDocList) {
                    if (!currentPieceJointeTitleSet.contains(pieceJointe.getPieceJointeTitre())) {
                        pieceJointePublieToRemoveList.add(pieceJointe);
                    }
                }
            }
        }
        return pieceJointePublieToRemoveList;
    }

    @Override
    public String getDeletedUrl(
        final CoreSession session,
        final String typePieceJointe,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        // Récupère la version précédente publié pour la comparaison
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final DocumentModel previousVersionDoc = versionService.getVersionToCompare(session, evenementDoc, versionDoc);

        if (previousVersionDoc != null) {
            final List<PieceJointeDTO> previousPieceJointeDocList =
                this.findAllPieceJointeFichierByVersionAndType(typePieceJointe, previousVersionDoc.getId(), session);
            if (previousPieceJointeDocList != null && !previousPieceJointeDocList.isEmpty()) {
                return previousPieceJointeDocList.get(0).getPieceJointeUrl();
            }
        }

        return "";
    }

    @Override
    public Set<String> getNewPieceJointeTitreList(
        final CoreSession session,
        final String typePieceJointe,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        // Récupère les UUID des pièces jointes de la version actuelle
        final List<PieceJointeDTO> currentPieceJointeDocList =
            this.findAllPieceJointeFichierByVersionAndType(typePieceJointe, versionDoc.getId(), session);
        List<PieceJointeDTO> previousPieceJointeDocList = new ArrayList<PieceJointeDTO>();
        final Map<String, PieceJointeDTO> previousPieceJointeDocMap = new HashMap<String, PieceJointeDTO>();
        final Set<String> pieceJointePublieNewList = new HashSet<String>();

        // Récupère la version précédente publié pour la comparaison
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final DocumentModel previousVersionDoc = versionService.getVersionToCompare(session, evenementDoc, versionDoc);

        if (previousVersionDoc != null) {
            previousPieceJointeDocList =
                this.findAllPieceJointeFichierByVersionAndType(typePieceJointe, previousVersionDoc.getId(), session);

            if (previousPieceJointeDocList != null) {
                for (final PieceJointeDTO pieceJointeDTO : previousPieceJointeDocList) {
                    previousPieceJointeDocMap.put(pieceJointeDTO.getPieceJointeTitre(), pieceJointeDTO);
                }
            }

            final Set<String> previousPieceJointeTitleSet = previousPieceJointeDocMap.keySet();

            if (currentPieceJointeDocList != null) {
                for (final PieceJointeDTO pieceJointe : currentPieceJointeDocList) {
                    if (!previousPieceJointeTitleSet.contains(pieceJointe.getPieceJointeTitre())) {
                        pieceJointePublieNewList.add(pieceJointe.getPieceJointeTitre());
                    }
                }
            }
        }

        return pieceJointePublieNewList;
    }

    @Override
    public List<String> getPieceJointeAjouteeListe(
        final CoreSession session,
        final DocumentModel versionDoc,
        final DocumentModel evenementDoc
    ) {
        PieceJointe pj;
        final List<String> pjAjouteeList = new ArrayList<String>();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final DocumentModel previousVersionDoc = versionService.getVersionToCompare(session, evenementDoc, versionDoc);
        if (previousVersionDoc != null) {
            final List<DocumentModel> currentPieceJointeDocList = session.getChildren(versionDoc.getRef());
            final List<DocumentModel> oldPieceJointeDocList = session.getChildren(previousVersionDoc.getRef());
            final Map<String, String> currentPieceJointeDocMap = new HashMap<String, String>();

            for (final DocumentModel doc : currentPieceJointeDocList) {
                pj = doc.getAdapter(PieceJointe.class);
                currentPieceJointeDocMap.put(pj.getTitle(), pj.getTypePieceJointe());
            }

            for (final DocumentModel doc : oldPieceJointeDocList) {
                pj = doc.getAdapter(PieceJointe.class);
                if (currentPieceJointeDocMap.get(pj.getTitle()) != null) {
                    currentPieceJointeDocMap.remove(pj.getTitle());
                }
            }
            pjAjouteeList.addAll(currentPieceJointeDocMap.values());
        }
        return pjAjouteeList;
    }

    @Override
    public List<String> getPieceJointeSupprimeeListe(
        final CoreSession session,
        final DocumentModel versionDoc,
        final DocumentModel evenementDoc
    ) {
        PieceJointe pj;
        final List<String> pjSuprimeeList = new ArrayList<String>();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final DocumentModel previousVersionDoc = versionService.getVersionToCompare(session, evenementDoc, versionDoc);

        if (previousVersionDoc != null) {
            final List<DocumentModel> currentPieceJointeDocList = session.getChildren(versionDoc.getRef());
            final List<DocumentModel> oldPieceJointeDocList = session.getChildren(previousVersionDoc.getRef());
            final Map<String, String> currentPieceJointeDocMap = new HashMap<String, String>();

            for (final DocumentModel doc : currentPieceJointeDocList) {
                pj = doc.getAdapter(PieceJointe.class);
                currentPieceJointeDocMap.put(pj.getTitle(), pj.getTypePieceJointe());
            }

            for (final DocumentModel doc : oldPieceJointeDocList) {
                pj = doc.getAdapter(PieceJointe.class);
                if (currentPieceJointeDocMap.get(pj.getTitle()) == null) {
                    pjSuprimeeList.add(pj.getTypePieceJointe());
                }
            }
        }
        return pjSuprimeeList;
    }
}
