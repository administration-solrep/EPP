package fr.dila.ss.core.event;

import static fr.dila.st.core.util.ResourceHelper.getString;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationInfo;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.runtime.transaction.TransactionHelper;

public abstract class SSAbstractMigrationGouvernementListener implements PostCommitEventListener {
    private static final Log LOG = LogFactory.getLog(SSAbstractMigrationGouvernementListener.class);

    // Les services déclarés ici. Instanciés dans initServices
    protected OrganigrammeService organigrammeService;
    protected STGouvernementService gouvernementService;
    protected STMinisteresService ministeresService;
    protected STPostesService postesService;
    protected MailboxPosteService mailboxPosteService;
    protected FeuilleRouteModelService feuilleRouteModelService;

    protected SSChangementGouvernementService changementGouvernementService;

    protected CoreSession coreSession;

    protected String typeMigration;
    protected String oldElementOrganigramme;
    protected String newElementOrganigramme;
    protected boolean deleteOldElementOrganigramme;
    protected Serializable oldMinistere;
    protected String oldMinistereElementOrganigramme;
    protected Serializable newMinistere;
    protected String newMinistereElementOrganigramme;
    protected boolean migrationWithDossierClos;
    protected boolean migrationModeleFdr;

    @Override
    public void handleEvent(final EventBundle events) {
        if (events.containsEventName(SSEventConstant.MIGRATION_GVT_EVENT)) {
            for (final Event event : events) {
                handleEvent(event);
            }
        }
    }

    protected void handleEvent(final Event event) {
        if (!event.getName().equals(SSEventConstant.MIGRATION_GVT_EVENT)) {
            return;
        }

        coreSession = event.getContext().getCoreSession();

        initServices();

        execMigrationOrganigramme(event, coreSession);
    }

    protected void initServices() {
        organigrammeService = initOrganigrammeService();
        gouvernementService = STServiceLocator.getSTGouvernementService();
        ministeresService = STServiceLocator.getSTMinisteresService();
        postesService = STServiceLocator.getSTPostesService();
        mailboxPosteService = SSServiceLocator.getMailboxPosteService();

        changementGouvernementService = initChangementGouvernementService();
    }

    protected abstract SSChangementGouvernementService initChangementGouvernementService();

    protected abstract OrganigrammeService initOrganigrammeService();

    protected void execMigrationOrganigramme(final Event event, final CoreSession coreSession) {
        final Map<String, Serializable> properties = event.getContext().getProperties();

        LOG.info("Début migration");
        @SuppressWarnings("unchecked")
        List<MigrationInfo> migrationList = (List<MigrationInfo>) properties.get(SSEventConstant.MIGRATION_GVT_DATA);
        for (MigrationInfo migrationInfo : migrationList) {
            this.lancerMigration(migrationInfo, coreSession);
            TransactionHelper.commitOrRollbackTransaction();
            TransactionHelper.startTransaction();
            // Envoi par mail de l'export des détails de la migration
            List<MigrationDetailModel> detailDocs = changementGouvernementService.getMigrationDetailModelList(
                migrationInfo.getLoggerId()
            );
            MigrationLoggerModel migrationLogger = changementGouvernementService.findMigrationById(
                migrationInfo.getLoggerId()
            );
            ConfigService configService = STServiceLocator.getConfigService();
            String recipient = configService.getValue("mail.migration.details");
            if (StringUtils.isNotBlank(recipient)) {
                // Post commit event
                EventProducer eventProducer = STServiceLocator.getEventProducer();
                Map<String, Serializable> eventProperties = new HashMap<>();
                eventProperties.put(SSEventConstant.SEND_MIGRATION_DETAILS_DETAILS_PROPERTY, (Serializable) detailDocs);
                eventProperties.put(SSEventConstant.SEND_MIGRATION_DETAILS_RECIPIENT_PROPERTY, recipient);
                eventProperties.put(SSEventConstant.SEND_MIGRATION_DETAILS_LOGGER_PROPERTY, migrationLogger);

                InlineEventContext eventContext = new InlineEventContext(
                    coreSession,
                    coreSession.getPrincipal(),
                    eventProperties
                );
                eventProducer.fireEvent(eventContext.newEvent(SSEventConstant.SEND_MIGRATION_DETAILS_EVENT));

                LOG.info(
                    "La génération de l'export a commencé. Il sera transmis par courrier électronique dès qu'il sera disponible."
                );
            }
        }
        coreSession.save();
        LOG.info("Fin de la migration");
    }

    protected void lancerMigration(MigrationInfo migrationInfo, final CoreSession coreSession) {
        MigrationLoggerModel model = null;
        try {
            model = changementGouvernementService.findMigrationById(migrationInfo.getLoggerId());

            initMigrationParameters(migrationInfo);

            final MigrationLoggerModel migrationLoggerModel = model;

            new UnrestrictedSessionRunner(coreSession) {

                @Override
                public void run() {
                    try {
                        migrationLoggerModel.setStatus(SSConstant.EN_COURS_STATUS);
                        changementGouvernementService.flushMigrationLogger(migrationLoggerModel);

                        if (typeMigration.equals(SSConstant.MIN_TYPE)) {
                            migrationMinistere(session, migrationLoggerModel);
                        } else if (typeMigration.equals(SSConstant.DIR_TYPE)) {
                            migrationDirection(session, migrationLoggerModel);
                        } else if (typeMigration.equals(SSConstant.UST_TYPE)) {
                            migrationUniteStructurelle(session, migrationLoggerModel);
                        } else if (typeMigration.equals(SSConstant.POSTE_TYPE)) {
                            migrationPoste(coreSession, migrationLoggerModel);
                        }

                        migrationLoggerModel.setEndDate(Calendar.getInstance().getTime());
                        migrationLoggerModel.setStatus(SSConstant.TERMINEE_STATUS);
                        changementGouvernementService.flushMigrationLogger(migrationLoggerModel);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                        migrationLoggerModel.setStatus(SSConstant.FAILED_STATUS);
                    }
                }
            }
            .runUnrestricted();

            LOG.info("Fin de la migration " + oldElementOrganigramme + " vers " + newElementOrganigramme);
        } finally {
            coreSession.save();
            if (model != null) {
                model.setEndDate(Calendar.getInstance().getTime());
                changementGouvernementService.flushMigrationLogger(model);
            }
        }
    }

    private void initMigrationParameters(MigrationInfo migrationInfo) {
        typeMigration = migrationInfo.getTypeMigration();
        oldElementOrganigramme = migrationInfo.getOldElementOrganigramme();
        newElementOrganigramme = migrationInfo.getNewElementOrganigramme();
        deleteOldElementOrganigramme = migrationInfo.getDeleteOldElementOrganigramme();
        oldMinistere = migrationInfo.getOldMinistereElementOrganigramme();
        oldMinistereElementOrganigramme = oldMinistere != null ? (String) oldMinistere : null;
        newMinistere = migrationInfo.getNewMinistereElementOrganigramme();
        newMinistereElementOrganigramme = newMinistere != null ? (String) newMinistere : null;
        migrationWithDossierClos = migrationInfo.getMigrationWithDossierClos();
        migrationModeleFdr = migrationInfo.getMigrationModeleFdr();
    }

    /**
     * Migration d'un ministère.
     */
    protected void migrationMinistere(CoreSession session, MigrationLoggerModel migrationLoggerModel) {
        EntiteNode oldMinistereNode = ministeresService.getEntiteNode(oldElementOrganigramme);
        EntiteNode newMinistereNode = ministeresService.getEntiteNode(newElementOrganigramme);

        notifyMigration(session, oldMinistereNode, newMinistereNode);
        doMigrationMinistere(session, migrationLoggerModel, oldMinistereNode, newMinistereNode);
        suppressionElementOrganigramme(session, deleteOldElementOrganigramme, oldMinistereNode, migrationLoggerModel);
    }

    protected void doMigrationMinistere(
        CoreSession session,
        MigrationLoggerModel migrationLoggerModel,
        EntiteNode oldMinistereNode,
        EntiteNode newMinistereNode
    ) {
        // Si la migration des modèles de feuille de route n'est pas coché : on doit
        // désactiver les modèles.
        boolean desactivateModeleFdr = !migrationModeleFdr;

        // migration des éléments fils.
        changementGouvernementService.migrerElementsFils(
            session,
            oldMinistereNode,
            newMinistereNode,
            migrationLoggerModel
        );

        // migration des modèles de feuilles de route.
        changementGouvernementService.migrerModeleFdrMinistere(
            session,
            oldMinistereNode,
            newMinistereNode,
            migrationLoggerModel,
            desactivateModeleFdr
        );
        session.save();

        if (migrationWithDossierClos) {
            // Mise à jour du ministère de rattachement.
            changementGouvernementService.updateDossierMinistereRattachement(
                session,
                oldMinistereNode,
                newMinistereNode,
                migrationLoggerModel
            );

            session.save();
        }
        // note : pas besoin de Migrer les jetons du ministère CE car ils ne sont pas
        // directement affectés à un
        // ministère.
    }

    /**
     * Migration d'une direction.
     */
    protected void migrationDirection(CoreSession session, MigrationLoggerModel migrationLoggerModel) {
        final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();

        EntiteNode oldMinistereNode = ministeresService.getEntiteNode(oldMinistereElementOrganigramme);
        EntiteNode newMinistereNode = ministeresService.getEntiteNode(newMinistereElementOrganigramme);
        UniteStructurelleNode oldDirectionNode = usService.getUniteStructurelleNode(oldElementOrganigramme);
        UniteStructurelleNode newDirectionNode = usService.getUniteStructurelleNode(newElementOrganigramme);

        notifyMigration(session, oldDirectionNode, newDirectionNode);
        doMigrationDirection(
            session,
            migrationLoggerModel,
            oldMinistereNode,
            newMinistereNode,
            oldDirectionNode,
            newDirectionNode
        );
        suppressionElementOrganigramme(
            session,
            deleteOldElementOrganigramme,
            oldDirectionNode,
            migrationLoggerModel,
            oldMinistereNode
        );
    }

    protected void doMigrationDirection(
        CoreSession session,
        MigrationLoggerModel migrationLoggerModel,
        EntiteNode oldMinistereNode,
        EntiteNode newMinistereNode,
        UniteStructurelleNode oldDirectionNode,
        UniteStructurelleNode newDirectionNode
    ) {
        // Si la migration des modèles de feuille de route n'est pas coché : on doit
        // désactiver les modèles.
        boolean desactivateModeleFdr = !migrationModeleFdr;

        // migration des éléments fils.
        changementGouvernementService.migrerElementsFils(
            session,
            oldDirectionNode,
            newDirectionNode,
            migrationLoggerModel
        );

        // migration des modèles de feuilles de route.
        changementGouvernementService.migrerModeleFdrDirection(
            session,
            oldMinistereNode,
            oldDirectionNode,
            newMinistereNode,
            newDirectionNode,
            migrationLoggerModel,
            desactivateModeleFdr
        );
        session.save();

        if (migrationWithDossierClos) {
            // Mise à jour du ministère de rattachement.
            changementGouvernementService.updateDossierDirectionRattachement(
                session,
                oldMinistereNode,
                oldDirectionNode,
                newMinistereNode,
                newDirectionNode,
                migrationLoggerModel
            );
        }
        session.save();
    }

    /**
     * Migration d'une unité structurelle.
     */
    protected void migrationUniteStructurelle(CoreSession session, MigrationLoggerModel migrationLoggerModel) {
        final STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();

        OrganigrammeNode oldDirectionNode = usService.getUniteStructurelleNode(oldElementOrganigramme);
        OrganigrammeNode newDirectionNode = usService.getUniteStructurelleNode(newElementOrganigramme);

        notifyMigration(session, oldDirectionNode, newDirectionNode);

        // migration des éléments fils.
        changementGouvernementService.migrerElementsFils(
            session,
            oldDirectionNode,
            newDirectionNode,
            migrationLoggerModel
        );

        session.save();

        suppressionElementOrganigramme(session, deleteOldElementOrganigramme, oldDirectionNode, migrationLoggerModel);
    }

    /**
     * Migration d'un poste.
     */
    protected void migrationPoste(CoreSession session, MigrationLoggerModel migrationLoggerModel) {
        OrganigrammeNode oldPosteNode = postesService.getPoste(oldElementOrganigramme);
        OrganigrammeNode newPosteNode = postesService.getPoste(newElementOrganigramme);

        notifyMigration(session, oldPosteNode, newPosteNode);
        doMigrationPoste(session, migrationLoggerModel, oldPosteNode, newPosteNode);
        suppressionElementOrganigramme(session, deleteOldElementOrganigramme, oldPosteNode, migrationLoggerModel);
    }

    protected void doMigrationPoste(
        CoreSession session,
        MigrationLoggerModel migrationLoggerModel,
        OrganigrammeNode oldPosteNode,
        OrganigrammeNode newPosteNode
    ) {
        // migration des éléments fils
        changementGouvernementService.migrerElementsFils(session, oldPosteNode, newPosteNode, migrationLoggerModel);

        // migration des étapes de feuilles de route
        changementGouvernementService.migrerModeleStepFdr(session, oldPosteNode, newPosteNode, migrationLoggerModel);
        session.save();

        // Mise à jour des corbeilles du poste : change les droits et le nom.
        changementGouvernementService.updateMailBox(session, oldPosteNode, newPosteNode, migrationLoggerModel);
        session.save();
    }

    /**
     * Suppression logique de l'élément de l'organigramme.
     */
    protected void suppressionElementOrganigramme(
        CoreSession session,
        boolean deleteOldElementOrganigramme,
        OrganigrammeNode oldElementOrganigrammeToDelete,
        MigrationLoggerModel migrationLoggerModel
    ) {
        if (BooleanUtils.isTrue(deleteOldElementOrganigramme)) {
            migrationLoggerModel.setDeleteOld(0);
            changementGouvernementService.flushMigrationLogger(migrationLoggerModel);
            // suppression de l'ancien element
            organigrammeService.deleteFromDn(session, oldElementOrganigrammeToDelete, true);
            migrationLoggerModel.setDeleteOld(1);
            changementGouvernementService.flushMigrationLogger(migrationLoggerModel);
        }
    }

    protected void suppressionElementOrganigramme(
        CoreSession session,
        boolean deleteOldElementOrganigramme,
        OrganigrammeNode oldDirectionNode,
        MigrationLoggerModel migrationLoggerModel,
        OrganigrammeNode oldMinistereNode
    ) {
        if (BooleanUtils.isTrue(deleteOldElementOrganigramme)) {
            migrationLoggerModel.setDeleteOld(0);

            List<OrganigrammeNode> parents = organigrammeService.getParentList(oldDirectionNode);
            if (parents != null && !parents.isEmpty()) {
                parents.remove(oldMinistereNode);
                if (parents.isEmpty()) {
                    suppressionElementOrganigramme(
                        session,
                        deleteOldElementOrganigramme,
                        oldDirectionNode,
                        migrationLoggerModel
                    );
                } else {
                    oldDirectionNode.setParentList(parents);
                    STServiceLocator
                        .getSTUsAndDirectionService()
                        .updateUniteStructurelle((UniteStructurelleNode) oldDirectionNode);
                }
            } else {
                suppressionElementOrganigramme(
                    session,
                    deleteOldElementOrganigramme,
                    oldDirectionNode,
                    migrationLoggerModel
                );
            }

            migrationLoggerModel.setDeleteOld(1);
            changementGouvernementService.flushMigrationLogger(migrationLoggerModel);
        }
    }

    protected void notifyMigration(CoreSession session, OrganigrammeNode oldNodeToDelete, OrganigrammeNode newNode) {
        final JournalService journalService = STServiceLocator.getJournalService();
        OrganigrammeType type = oldNodeToDelete.getType();
        String comment = getString(getCommentParam(type), oldNodeToDelete.getLabel(), newNode.getLabel());
        journalService.journaliserActionAdministration(session, STEventConstant.CHANGEMENT_GVT_EVENT, comment);
    }

    protected String getCommentParam(OrganigrammeType type) {
        String param;
        if (type == OrganigrammeType.MINISTERE) {
            param = STEventConstant.MIGRATION_MIN_COMMENT;
        } else if (type == OrganigrammeType.DIRECTION) {
            param = STEventConstant.MIGRATION_DIR_COMMENT;
        } else if (type == OrganigrammeType.UNITE_STRUCTURELLE) {
            param = STEventConstant.MIGRATION_UST_COMMENT;
        } else {
            param = STEventConstant.MIGRATION_PST_COMMENT;
        }
        return param;
    }
}
