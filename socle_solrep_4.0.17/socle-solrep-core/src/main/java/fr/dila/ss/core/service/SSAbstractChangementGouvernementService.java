package fr.dila.ss.core.service;

import static fr.dila.cm.cases.CaseConstants.ALL_ACTION_PARTICIPANTS_PROPERTY_NAME;
import static fr.dila.cm.cases.CaseConstants.INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_NAME;
import static fr.dila.cm.cases.CaseDistribConstants.DISTRIBUTION_SCHEMA;
import static java.util.Collections.singletonList;

import fr.dila.cm.cases.CaseDistribConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationDiscriminatorConstants;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.api.service.SSOrganigrammeService;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.transaction.TransactionHelper;

public abstract class SSAbstractChangementGouvernementService
    extends DefaultComponent
    implements SSChangementGouvernementService {
    private static final STLogger LOGGER = STLogFactory.getLog(SSAbstractChangementGouvernementService.class);

    protected static final String BATCH_SIZE_PROP = "solonepg.chgt.gouv.batch.size";
    protected static final int BATCH_SIZE_DEFAULT = 100;

    /**
     * Récupère dossiers clos (publié, abandonné, clos, terminé sans publication,
     * nor attribué)
     */
    protected abstract String getQueryFromDossWhereClosSql();

    private static volatile PersistenceProvider persistenceProvider;

    protected abstract SSOrganigrammeService getSSOrganigrammeService();

    @Override
    public void migrerElementsFils(
        final CoreSession session,
        final OrganigrammeNode oldNode,
        final OrganigrammeNode newNode,
        final MigrationLoggerModel migrationLoggerModel
    ) {
        migrationLoggerModel.setElementsFils(0);
        flushMigrationLogger(migrationLoggerModel);
        final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
            migrationLoggerModel,
            MigrationDiscriminatorConstants.STEP,
            "Migration éléments fils"
        );

        final SSOrganigrammeService organigrammeService = getSSOrganigrammeService();
        organigrammeService.migrateNode(session, oldNode, newNode);

        migrationLoggerModel.setElementsFils(1);
        flushMigrationLogger(migrationLoggerModel);

        migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
        flushMigrationDetail(migrationDetailModel);
    }

    @Override
    public void migrerModeleStepFdr(
        final CoreSession session,
        final OrganigrammeNode oldNode,
        final OrganigrammeNode newNode,
        final MigrationLoggerModel migrationLoggerModel
    ) {
        migrationLoggerModel.setFdrStep(0);
        flushMigrationLogger(migrationLoggerModel);

        // récupération des identifiants de mailbox
        final String oldPostMailboxId = SSConstant.MAILBOX_POSTE_ID_PREFIX + oldNode.getId();
        final String newPostMailboxId = SSConstant.MAILBOX_POSTE_ID_PREFIX + newNode.getId();

        int nbStepFdrToUpdate = 0;

        final List<String> params = new ArrayList<>();
        params.add(oldPostMailboxId);

        // on modifie les étapes et les dossierLink
        final StringBuilder query = new StringBuilder();
        query.append("SELECT r.ecm:uuid as id FROM ");
        query.append(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        query.append(" as r WHERE r.");
        query.append(CaseDistribConstants.STEP_DISTRIBUTION_MAILBOX_ID_PROPERTY_NAME);
        query.append(" = ? AND r.");
        query.append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH);
        query.append(" IN ('running', 'ready', 'validated', 'draft') ");

        final List<DocumentModel> stepFdrToUpdate = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            SSConstant.ROUTE_STEP_DOCUMENT_TYPE,
            query.toString(),
            params.toArray()
        );

        if (stepFdrToUpdate == null || stepFdrToUpdate.isEmpty()) {
            migrationLoggerModel.setFdrStepCount(0);
            migrationLoggerModel.setFdrStepCurrent(0);
            flushMigrationLogger(migrationLoggerModel);

            final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
                migrationLoggerModel,
                MigrationDiscriminatorConstants.STEP,
                "Migration étape : aucune étape à migrer"
            );
            migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
            flushMigrationDetail(migrationDetailModel);
        } else {
            nbStepFdrToUpdate = stepFdrToUpdate.size();

            migrationLoggerModel.setFdrStepCount(nbStepFdrToUpdate);

            int index = 0;
            migrationLoggerModel.setFdrStepCurrent(index);
            flushMigrationLogger(migrationLoggerModel);

            LOGGER.info(
                session,
                SSLogEnumImpl.MIGRATE_MOD_FDR_TEC,
                nbStepFdrToUpdate + " étapes de feuille de route à mettre à jour."
            );
            // liste des feuilles de route à mettre à jour pour les droits de lecture
            for (final DocumentModel documentModel : stepFdrToUpdate) {
                final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
                    migrationLoggerModel,
                    MigrationDiscriminatorConstants.STEP,
                    "Migration étape : " + documentModel.getId()
                );
                // on verrouille le document pour s'assurer que personne ne le modifie
                lockDocument(session, documentModel);

                final SSRouteStep sTRouteStep = documentModel.getAdapter(SSRouteStep.class);
                sTRouteStep.setDistributionMailboxId(newPostMailboxId);
                sTRouteStep.setPosteLabel(newNode.getLabel());

                // modification du dc:title (sauf si c'est la première étape )
                final String oldTitle = documentModel.getTitle();
                final int lastIndex = oldTitle.lastIndexOf(SSConstant.MAILBOX_POSTE_ID_PREFIX);
                if (lastIndex != -1) {
                    final String newTitle = oldTitle.substring(0, lastIndex) + newPostMailboxId;
                    DublincoreSchemaUtils.setTitle(sTRouteStep.getDocument(), newTitle);
                }

                migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
                flushMigrationDetail(migrationDetailModel);
                sTRouteStep.save(session);
                // on enlève le verrou
                session.removeLock(documentModel.getRef());

                migrationLoggerModel.setFdrStepCurrent(++index);
                flushMigrationLogger(migrationLoggerModel);

                createMigrationDetailFor(
                    migrationLoggerModel,
                    MigrationDiscriminatorConstants.STEP,
                    "Migration étape : " + documentModel.getId() + " : Fin"
                );
            }
        }

        migrationLoggerModel.setFdrStep(1);
        flushMigrationLogger(migrationLoggerModel);
    }

    protected abstract List<DocumentModel> getDossiersToUpdate(
        final CoreSession session,
        final List<String> params,
        final StringBuilder query
    );

    @Override
    public void updateMailBox(
        final CoreSession session,
        final OrganigrammeNode oldNode,
        final OrganigrammeNode newNode,
        final MigrationLoggerModel migrationLoggerModel
    ) {
        migrationLoggerModel.setMailboxPoste(0);
        flushMigrationLogger(migrationLoggerModel);

        // récupération des identifiants de mailbox
        final String oldPostMailboxId = SSConstant.MAILBOX_POSTE_ID_PREFIX + oldNode.getId();
        final String newNodeId = newNode.getId();
        final String newPostMailboxId = SSConstant.MAILBOX_POSTE_ID_PREFIX + newNodeId;
        final String oldPostLabel = oldNode.getLabel();
        final String newPostLabel = newNode.getLabel();

        // on récupère la mailbox contenant les DossierLink de l'ancien poste.
        final StringBuilder query = getMailboxesHavingGroupsField();

        final List<String> params = new ArrayList<>();
        params.add(oldPostMailboxId);

        final List<DocumentModel> oldMlbxPosteList = getOldMlbxPosteList(session, query, params);

        params.clear();
        params.add(newPostMailboxId);

        final List<DocumentModel> newMlbxPosteList = getNewMlbxPosteList(session, query, params);

        if (oldMlbxPosteList == null || oldMlbxPosteList.isEmpty()) {
            migrationLoggerModel.setMailboxPosteCount(0);
            migrationLoggerModel.setMailboxPosteCurrent(0);
            flushMigrationLogger(migrationLoggerModel);

            final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
                migrationLoggerModel,
                MigrationDiscriminatorConstants.MAILBOX,
                "Aucune mailbox trouvée"
            );
            migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
            flushMigrationDetail(migrationDetailModel);
        } else if (oldMlbxPosteList.size() > 1) {
            migrationLoggerModel.setMailboxPosteCount(0);
            migrationLoggerModel.setMailboxPosteCurrent(0);
            flushMigrationLogger(migrationLoggerModel);
            final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
                migrationLoggerModel,
                MigrationDiscriminatorConstants.MAILBOX,
                "Plus d'une mailbox trouvée pour " + oldPostLabel
            );
            migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
            flushMigrationDetail(migrationDetailModel);
            throw new NuxeoException("Plus d'une mailbox pour le poste " + oldPostLabel + " ont été trouvées !");
        } else {
            DocumentModel newMlbxPoste = null;

            if (newMlbxPosteList == null || newMlbxPosteList.isEmpty()) {
                final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
                    migrationLoggerModel,
                    MigrationDiscriminatorConstants.MAILBOX,
                    "Migration mailbox : céation de " + newPostLabel
                );
                migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
                flushMigrationDetail(migrationDetailModel);
                // création de la mailbox poste
                final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
                final Mailbox newMailBox = mailboxPosteService.createPosteMailbox(session, newNodeId, newPostLabel);
                newMlbxPoste = newMailBox.getDocument();
            } else if (newMlbxPosteList.size() > 1) {
                final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
                    migrationLoggerModel,
                    MigrationDiscriminatorConstants.MAILBOX,
                    "Plus d'une mailbox trouvée pour " + newPostLabel
                );
                migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
                flushMigrationDetail(migrationDetailModel);
                throw new NuxeoException("Plus d'une mailbox pour le poste " + newPostLabel + " ont été trouvées !");
            }

            final DocumentModel oldMlbxPoste = oldMlbxPosteList.get(0);
            if (newMlbxPoste == null && newMlbxPosteList != null && !newMlbxPosteList.isEmpty()) {
                newMlbxPoste = newMlbxPosteList.get(0);
            }

            if (newMlbxPoste != null) {
                final MigrationDetailModel migrationDetailModel = createMigrationDetailFor(
                    migrationLoggerModel,
                    MigrationDiscriminatorConstants.MAILBOX,
                    "Migration mailbox : " + oldMlbxPoste.getTitle() + " vers " + newMlbxPoste.getTitle()
                );

                migrationLoggerModel.setMailboxPosteCount(1);
                flushMigrationLogger(migrationLoggerModel);

                // déplacement des dossiers link depuis l'ancienne mailbox vers la nouvelle
                final List<DocumentRef> documentRefList = session.getChildrenRefs(oldMlbxPoste.getRef(), null);
                session.move(documentRefList, newMlbxPoste.getRef());
                // MAJ de l'id de la mailbox dans les dossiers link
                List<String> newPostes = singletonList(newPostMailboxId);
                for (DocumentModel dl : session.getDocuments(documentRefList.toArray(new DocumentRef[0]))) {
                    PropertyUtil.setProperty(dl, DISTRIBUTION_SCHEMA, ALL_ACTION_PARTICIPANTS_PROPERTY_NAME, newPostes);
                    PropertyUtil.setProperty(
                        dl,
                        DISTRIBUTION_SCHEMA,
                        INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_NAME,
                        newPostes
                    );
                    session.saveDocument(dl);
                }

                migrationLoggerModel.setMailboxPosteCurrent(1);

                migrationDetailModel.setEndDate(Calendar.getInstance().getTime());
                flushMigrationDetail(migrationDetailModel);
            }
        }

        migrationLoggerModel.setMailboxPoste(1);
        flushMigrationLogger(migrationLoggerModel);
    }

    protected abstract List<DocumentModel> getNewMlbxPosteList(
        final CoreSession session,
        final StringBuilder query,
        final List<String> params
    );

    protected abstract List<DocumentModel> getOldMlbxPosteList(
        final CoreSession session,
        final StringBuilder query,
        final List<String> params
    );

    protected abstract StringBuilder getMailboxesHavingGroupsField();

    @Override
    public void updateDossierMinistereRattachement(
        final CoreSession session,
        final OrganigrammeNode oldNode,
        final OrganigrammeNode newNode,
        final MigrationLoggerModel migrationLoggerModel
    ) {
        updateDossierDirectionRattachement(session, oldNode, null, newNode, null, migrationLoggerModel);
    }

    /**
     * Lock le document même si le document en question était locké par un autre
     * utilisateur.
     *
     * @
     */
    protected void lockDocument(final CoreSession session, final DocumentModel docModel) {
        final DocumentRef docRef = docModel.getRef();
        // si le document est verrouillé, on le déverrouille
        if (docModel.isLocked()) {
            session.removeLock(docRef);
        }
        // on verrouille le document pour s'assurer que personne ne le modifie
        session.setLock(docRef);
    }

    /**
     * Recuperation du persistence provider
     *
     * @return
     */
    protected static PersistenceProvider getOrCreatePersistenceProvider() {
        if (persistenceProvider == null) {
            synchronized (SSAbstractChangementGouvernementService.class) {
                if (persistenceProvider == null) {
                    activatePersistenceProvider();
                }
            }
        }
        return persistenceProvider;
    }

    private static void activatePersistenceProvider() {
        final Thread thread = Thread.currentThread();
        final ClassLoader last = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
            final PersistenceProviderFactory persistenceProviderFactory = ServiceUtil.getRequiredService(
                PersistenceProviderFactory.class
            );
            persistenceProvider = persistenceProviderFactory.newProvider("SWMIGRATIONLOGGER");
            persistenceProvider.openPersistenceUnit();
        } finally {
            thread.setContextClassLoader(last);
        }
    }

    @Override
    public void deactivate(final ComponentContext context) {
        deactivatePersistenceProvider();
    }

    private static void deactivatePersistenceProvider() {
        if (persistenceProvider != null) {
            synchronized (SSAbstractChangementGouvernementService.class) {
                if (persistenceProvider != null) {
                    persistenceProvider.closePersistenceUnit();
                    persistenceProvider = null;
                }
            }
        }
    }

    @Override
    public MigrationLoggerModel findMigrationById(final Long idLogger) {
        return getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    try {
                        return (MigrationLoggerModel) entityManager
                            .createQuery("Select log from MigrationLogger log where log.id = :idLogger")
                            .setParameter("idLogger", idLogger)
                            .getSingleResult();
                    } catch (final NoResultException e) {
                        return null;
                    }
                }
            );
    }

    @Override
    public void flushMigrationLogger(final MigrationLoggerModel migrationLoggerModel) {
        getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    final MigrationLoggerModel model = entityManager.merge(migrationLoggerModel);
                    entityManager.persist(model);
                    entityManager.flush();
                    TransactionHelper.commitOrRollbackTransaction();
                    TransactionHelper.startTransaction();
                    return null;
                }
            );
    }

    @Override
    public MigrationDetailModel createMigrationDetailFor(
        final MigrationLoggerModel migrationLoggerModel,
        final String type,
        final String detail
    ) {
        return createMigrationDetailFor(migrationLoggerModel, type, detail, "OK");
    }

    @Override
    public void flushMigrationDetail(final MigrationDetailModel migrationDetailModel) {
        getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    final MigrationDetailModel model = entityManager.merge(migrationDetailModel);
                    entityManager.persist(model);
                    entityManager.flush();
                    TransactionHelper.commitOrRollbackTransaction();
                    TransactionHelper.startTransaction();
                    return null;
                }
            );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MigrationLoggerModel> getMigrationWithoutEndDate() {
        return getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    try {
                        return entityManager
                            .createQuery("Select log from MigrationLogger log where log.endDate is null")
                            .getResultList();
                    } catch (final NoResultException e) {
                        return new ArrayList<>();
                    }
                }
            );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MigrationLoggerModel> getMigrationLoggerModelList() {
        return getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    try {
                        return entityManager
                            .createQuery("Select log from MigrationLogger log order by log.startDate")
                            .getResultList();
                    } catch (final NoResultException e) {
                        return new ArrayList<>();
                    }
                }
            );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MigrationDetailModel> getMigrationDetailModelList(final Long loggerId) {
        return getOrCreatePersistenceProvider()
            .run(
                true,
                entityManager -> {
                    try {
                        final Query query = entityManager.createQuery(
                            "Select log from MigrationDetail log where log.migration.id = :migration order by log.startDate"
                        );
                        query.setParameter("migration", loggerId);
                        return query.getResultList();
                    } catch (final NoResultException e) {
                        return new ArrayList<>();
                    }
                }
            );
    }

    @Override
    public String getLogMessage(MigrationLoggerModel migrationLoggerModel) {
        String typeMigration = migrationLoggerModel.getTypeMigration();
        StringBuilder message = new StringBuilder();
        if (SSConstant.MIN_TYPE.equals(typeMigration)) {
            final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
            OrganigrammeNode oldMinistereNode = ministeresService.getEntiteNode(migrationLoggerModel.getOldElement());
            OrganigrammeNode newMinistereNode = ministeresService.getEntiteNode(migrationLoggerModel.getNewElement());
            message.append("Migration du ");
            if (oldMinistereNode == null) {
                appendPost(message, "** Ministère inconnu **", "Ministère");
            } else {
                appendPost(message, oldMinistereNode.getLabel(), "Ministère");
            }
            message.append(" vers ");
            if (newMinistereNode == null) {
                appendPost(message, "** Ministère inconnu **", "Ministère");
            } else {
                appendPost(message, newMinistereNode.getLabel(), "Ministère");
            }
        } else if (SSConstant.DIR_TYPE.equals(typeMigration)) {
            final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
            OrganigrammeNode oldDirectionNode = usService.getUniteStructurelleNode(
                migrationLoggerModel.getOldElement()
            );
            OrganigrammeNode newDirectionNode = usService.getUniteStructurelleNode(
                migrationLoggerModel.getNewElement()
            );

            message.append("Migration du ");
            appendPost(message, oldDirectionNode.getLabel(), "Direction");
            message.append(" vers ");
            appendPost(message, newDirectionNode.getLabel(), "Direction");
        } else if (SSConstant.UST_TYPE.equals(typeMigration)) {
            final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
            OrganigrammeNode oldUniteStruct = usService.getUniteStructurelleNode(migrationLoggerModel.getOldElement());
            OrganigrammeNode newUniteStruct = usService.getUniteStructurelleNode(migrationLoggerModel.getNewElement());

            message.append("Migration de ");
            appendPost(message, oldUniteStruct.getLabel(), "l' Unité structurelle");
            message.append(" vers ");
            appendPost(message, newUniteStruct.getLabel(), "l' Unité structurelle");
        } else if (SSConstant.POSTE_TYPE.equals(typeMigration) || "POSTE_TYPE".equals(typeMigration)) {
            final STPostesService postesService = STServiceLocator.getSTPostesService();
            OrganigrammeNode oldPosteNode = postesService.getPoste(migrationLoggerModel.getOldElement());
            OrganigrammeNode newPosteNode = postesService.getPoste(migrationLoggerModel.getNewElement());

            message.append("Migration du ");
            appendPost(message, oldPosteNode.getLabel(), "Poste");
            message.append(" vers ");
            appendPost(message, newPosteNode.getLabel(), "Poste");
        }

        return message.toString();
    }

    private void appendPost(StringBuilder builder, String label, String post) {
        if (!label.contains(post)) {
            builder.append(" ");
            builder.append(post);
            builder.append(" ");
        }
        builder.append(label);
    }
}
