package fr.dila.ss.ui.services.organigramme;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationInfo;
import fr.dila.ss.api.migration.MigrationLoggerInfos;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.migration.MigrationWidget;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.api.service.SSOrganigrammeService;
import fr.dila.ss.ui.bean.MigrationDTO;
import fr.dila.ss.ui.bean.MigrationDetailDTO;
import fr.dila.ss.ui.bean.MigrationProgressDTO;
import fr.dila.ss.ui.bean.SSHistoriqueMigrationDTO;
import fr.dila.ss.ui.bean.SSHistoriqueMigrationDetailDTO;
import fr.dila.ss.ui.bean.SSHistoriqueMigrationDetailLigneDTO;
import fr.dila.ss.ui.enums.MigrationStatus;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.jaxrs.webobject.page.admin.SSMigration;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

/**
 * Bean seam de gestion du changement de gouvernement.
 *
 * @author arn
 */
public abstract class SSMigrationGouvernementUIServiceImpl
    extends SSMigrationManagerUIServiceImpl
    implements SSMigrationGouvernementUIService {
    /**
     * Champ utilisé pour afficher les erreurs de validation
     */
    protected String errorName = null;

    /**
     * Champ correspondant au type de noeud organigramme à migrer : par défaut
     */

    protected MigrationLoggerModel migration;

    /**
     * Clé du paramètre stocké en session contenant les ordres de migration stockés
     * en session, potentiellement pas encore persistés.
     */
    public static final String MIGRATIONS_LIST = "migrationsList";

    /**
     * Boolean permettant de cacher les icons d'état de la mugration
     * (réinitialisation page)
     */
    protected boolean affichageEtat;

    @Override
    public boolean isAffichageEtat() {
        return affichageEtat;
    }

    @Override
    public void setAffichageEtat(boolean affichageEtat) {
        this.affichageEtat = affichageEtat;
    }

    private static final String MIGRATION_TYPE_SUFFIX = "_TYPE";

    /**
     * Default constructor
     */
    public SSMigrationGouvernementUIServiceImpl() {
        // do nothing
    }

    @Override
    public void initialize(SpecificContext context) {
        // Create migration by default
        List<MigrationLoggerModel> migrationEnCoursList = getMigrationEnCoursList();
        if (migrationEnCoursList == null || migrationEnCoursList.isEmpty()) {
            ajouterMigration(context);
        } else {
            for (MigrationLoggerModel migrationLoggerModel : migrationEnCoursList) {
                migrationLoggerModel.assignMigrationInfo(this.ajouterMigration(context));
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<MigrationInfo> getMigrationsList(SpecificContext context) {
        List<MigrationInfo> migrations = UserSessionHelper.getUserSessionParameter(
            context,
            MIGRATIONS_LIST,
            List.class
        );
        if (migrations == null) {
            migrations = new ArrayList<>();
            UserSessionHelper.putUserSessionParameter(context, MIGRATIONS_LIST, migrations);
        }

        return migrations;
    }

    @Override
    public void supprimerMigration(MigrationInfo migrationInfo, SpecificContext context) {
        getMigrationsList(context).remove(migrationInfo);
        adjustOrdres(context);
    }

    @Override
    public MigrationInfo ajouterMigration(SpecificContext context) {
        String randomId = RandomStringUtils.randomAlphabetic(MigrationInfo.MIGRATION_ID_LENGTH);
        MigrationInfo migrationInfo = new MigrationInfo(randomId);

        List<MigrationInfo> migrationsList = getMigrationsList(context);
        migrationInfo.setOrdre(migrationsList.size() + 1);
        migrationsList.add(migrationInfo);

        return migrationInfo;
    }

    private void adjustOrdres(SpecificContext context) {
        int index = 1;
        for (MigrationInfo migrationInfo : getMigrationsList(context)) {
            migrationInfo.setOrdre(index++);
        }
    }

    @Override
    public String lancerMigration(SpecificContext context) {
        CoreSession session = context.getSession();
        errorName = null;
        affichageEtat = true;
        // Valider les migration avant de lancer le traitement
        if (!isValid(context) || CollectionUtils.isNotEmpty(getMigrationEnCoursList())) {
            UserSessionHelper.putUserSessionParameter(context, MIGRATIONS_LIST, null);
            context
                .getMessageQueue()
                .addErrorToQueue(
                    Optional.ofNullable(errorName).orElse(ResourceHelper.getString("migration.error.deja.en.cours"))
                );
            return null;
        }

        final SSChangementGouvernementService chgtGvtServ = getChangementGouvernementService();
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        final String principalName = ssPrincipal.getName();
        // Create model logger for each migration
        for (MigrationInfo migrationInfo : getMigrationsList(context)) {
            MigrationLoggerModel migrationLogger = chgtGvtServ.createMigrationLogger(principalName);
            migrationInfo.assignLoggerInfos(migrationLogger);
            chgtGvtServ.flushMigrationLogger(migrationLogger);
            // Assign the logger id
            migrationInfo.setLoggerId(migrationLogger.getId());
        }
        adjustOrdres(context);

        // Post commit event
        fireEvent(context, session, ssPrincipal);

        return ResourceHelper.getString("info.organigrammeManager.migration.started");
    }

    protected void fireEvent(SpecificContext context, CoreSession session, SSPrincipal ssPrincipal) {
        EventProducer eventProducer = STServiceLocator.getEventProducer();
        Map<String, Serializable> eventProperties = new HashMap<>();
        eventProperties.put(SSEventConstant.MIGRATION_GVT_DATA, (ArrayList<MigrationInfo>) getMigrationsList(context));

        InlineEventContext eventContext = new InlineEventContext(session, ssPrincipal, eventProperties);
        eventProducer.fireEvent(eventContext.newEvent(SSEventConstant.MIGRATION_GVT_EVENT));
    }

    @Override
    public boolean isAccessAuthorized(CoreSession session) {
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        return (
            ssPrincipal.isAdministrator() || ssPrincipal.isMemberOf(SSConstant.ADMINISTRATION_CHANGEMENT_ORGANIGRAMME)
        );
    }

    @Override
    public MigrationLoggerInfos getMigrationLoggerInfos(String widgetName, Long loggerId) {
        MigrationLoggerInfos result = null;
        MigrationLoggerModel loggerModel = null;

        loggerModel = getChangementGouvernementService().findMigrationById(loggerId);

        if (widgetName != null && loggerModel != null) {
            String status = ObjectHelper.requireNonNullElse(loggerModel.getStatus(), SSConstant.EN_COURS_STATUS);

            result = MigrationWidget.valueOf(widgetName).getMigrationLoggerInfos(loggerModel, status);
        }

        return result;
    }

    @Override
    public boolean getDeleteOldElementOrganigramme(String migrationId, SpecificContext context) {
        MigrationInfo migrationInfo = getMigration(migrationId, context);
        return migrationInfo != null && migrationInfo.getDeleteOldElementOrganigramme();
    }

    @Override
    public boolean getMigrationWithDossierClos(String migrationId, SpecificContext context) {
        MigrationInfo migrationInfo = getMigration(migrationId, context);
        return migrationInfo != null && migrationInfo.getMigrationWithDossierClos();
    }

    @Override
    public boolean getMigrationModeleFdr(String migrationId, SpecificContext context) {
        MigrationInfo migrationInfo = getMigration(migrationId, context);
        return migrationInfo != null && migrationInfo.getMigrationModeleFdr();
    }

    private boolean isValid(SpecificContext context) {
        for (MigrationInfo migrationInfo : getMigrationsList(context)) {
            String errorMessage = migrationInfo.validateMigration();
            if (errorMessage != null) {
                errorName = ResourceHelper.getString(errorMessage);
                return false;
            }
        }
        return true;
    }

    private MigrationInfo getMigration(String migrationId, SpecificContext context) {
        // Pas besoin de faire toute la boucle si on sait que l'identifiant est null ou
        // vide
        if (StringUtils.isNotBlank(migrationId)) {
            for (MigrationInfo migrationInfo : getMigrationsList(context)) {
                if (migrationInfo.getId().equals(migrationId)) {
                    return migrationInfo;
                }
            }
        }
        return null;
    }

    @Override
    public void resetElementOrganigramme(String migrationId, SpecificContext context) {
        MigrationInfo migrationInfo = getMigration(migrationId, context);
        if (migrationInfo != null) {
            migrationInfo.resetElementOrganigramme();
        }
    }

    @Override
    public String getErrorName() {
        return errorName;
    }

    @Override
    public List<MigrationLoggerModel> getMigrationEnCoursList() {
        return getChangementGouvernementService().getMigrationWithoutEndDate();
    }

    @Override
    public boolean candisplaySucceed(Long loggerId) {
        MigrationLoggerModel miggrationLooger = getChangementGouvernementService().findMigrationById(loggerId);
        return miggrationLooger != null && miggrationLooger.terminee();
    }

    @Override
    public boolean candisplayFailure(Long loggerId) {
        MigrationLoggerModel miggrationLooger = getChangementGouvernementService().findMigrationById(loggerId);
        return miggrationLooger != null && miggrationLooger.failed();
    }

    @Override
    public boolean candisplayWaiter(Long loggerId) {
        MigrationLoggerModel miggrationLooger = getChangementGouvernementService().findMigrationById(loggerId);
        return miggrationLooger != null && miggrationLooger.enCours();
    }

    @Override
    public List<MigrationLoggerModel> getMigrationLoggerModelList() {
        return getChangementGouvernementService().getMigrationLoggerModelList();
    }

    @Override
    public String getLogMessage(MigrationLoggerModel migrationLoggerModel) {
        return getChangementGouvernementService().getLogMessage(migrationLoggerModel);
    }

    @Override
    public void resetData(SpecificContext context) {
        affichageEtat = false;
        for (MigrationInfo migrationInfo : getMigrationsList(context)) {
            migrationInfo.resetElementOrganigramme();
        }
    }

    @Override
    public void sendMailMigrationDetails(SpecificContext context) {
        Long loggerId = Long.parseLong(context.getFromContextData(SSContextDataKey.ID_MIGRATION_LOGGER));

        List<MigrationDetailModel> detailDocs = getChangementGouvernementService()
            .getMigrationDetailModelList(loggerId);
        MigrationLoggerModel migrationLogger = getChangementGouvernementService().findMigrationById(loggerId);
        String recipient = context.getSession().getPrincipal().getEmail();
        if (StringUtils.isBlank(recipient)) {
            throw new SSException(ResourceHelper.getString("export.message.erreur.no.mail"));
        } else {
            // Post commit event
            EventProducer eventProducer = STServiceLocator.getEventProducer();
            Map<String, Serializable> eventProperties = new HashMap<>();
            eventProperties.put(SSEventConstant.SEND_MIGRATION_DETAILS_DETAILS_PROPERTY, (Serializable) detailDocs);
            eventProperties.put(SSEventConstant.SEND_MIGRATION_DETAILS_RECIPIENT_PROPERTY, recipient);
            eventProperties.put(SSEventConstant.SEND_MIGRATION_DETAILS_LOGGER_PROPERTY, migrationLogger);

            CoreSession session = context.getSession();
            InlineEventContext eventContext = new InlineEventContext(session, session.getPrincipal(), eventProperties);
            eventProducer.fireEvent(eventContext.newEvent(SSEventConstant.SEND_MIGRATION_DETAILS_EVENT));
        }
    }

    @Override
    public SSHistoriqueMigrationDetailDTO getHistoriqueMigrationDetailDTO(Long migrationDetailId) {
        SSHistoriqueMigrationDetailDTO historiqueMigrationDetailDTO = new SSHistoriqueMigrationDetailDTO();
        historiqueMigrationDetailDTO.setId(Long.toString(migrationDetailId));

        MigrationLoggerModel migrationLoggerModel = getChangementGouvernementService()
            .findMigrationById(migrationDetailId);
        historiqueMigrationDetailDTO.setLabel(getLogMessage(migrationLoggerModel));

        historiqueMigrationDetailDTO.setLignes(
            getChangementGouvernementService()
                .getMigrationDetailModelList(migrationDetailId)
                .stream()
                .map(SSMigrationGouvernementUIServiceImpl::toHistoriqueMigrationDetailLigneDTO)
                .collect(Collectors.toList())
        );

        return historiqueMigrationDetailDTO;
    }

    private static SSHistoriqueMigrationDetailLigneDTO toHistoriqueMigrationDetailLigneDTO(
        MigrationDetailModel migrationDetailModel
    ) {
        SSHistoriqueMigrationDetailLigneDTO historiqueMigrationDetailLigneDTO = new SSHistoriqueMigrationDetailLigneDTO();

        historiqueMigrationDetailLigneDTO.setDateDebut(
            DateUtil.dateToGregorianCalendar(migrationDetailModel.getStartDate())
        );
        historiqueMigrationDetailLigneDTO.setDateFin(
            DateUtil.dateToGregorianCalendar(migrationDetailModel.getEndDate())
        );

        historiqueMigrationDetailLigneDTO.setMessage(migrationDetailModel.getDetail());
        historiqueMigrationDetailLigneDTO.setStatus(migrationDetailModel.getStatut());

        return historiqueMigrationDetailLigneDTO;
    }

    @Override
    public MigrationDTO getMigrationDTO(SpecificContext context) {
        MigrationDTO migrationDto = new MigrationDTO();

        ArrayList<MigrationDetailDTO> details = new ArrayList<>();

        SSOrganigrammeService organigrammeService = getOrganigrammeService();

        String globalStatus = "";

        List<Long> migEnCoursIds = getMigrationsList(context)
            .stream()
            .map(MigrationInfo::getLoggerId)
            .collect(Collectors.toList());
        List<MigrationLoggerModel> migEnCours = getMigrationLoggerModelList()
            .stream()
            .filter(mig -> migEnCoursIds.contains(mig.getId()))
            .collect(Collectors.toList());
        migEnCours.forEach(
            mig -> mig.setStatus(ObjectHelper.requireNonNullElse(mig.getStatus(), SSConstant.EN_COURS_STATUS))
        );
        if (CollectionUtils.isNotEmpty(migEnCours)) {
            globalStatus =
                migEnCours
                    .stream()
                    .map(MigrationLoggerModel::getStatus)
                    .filter(SSConstant.EN_COURS_STATUS::equals)
                    .findFirst()
                    .orElse(SSConstant.TERMINEE_STATUS);
        } else {
            // On rajoute un MigrationDetailDTO pour initialiser le formulaire
            MigrationDetailDTO detail = new MigrationDetailDTO();
            details.add(detail);
        }

        for (MigrationLoggerModel migLogger : migEnCours) {
            MigrationDetailDTO detail = new MigrationDetailDTO();

            detail.setStatus(migLogger.getStatus());
            detail.setDeleteOld(migLogger.isDeleteOldValue());
            detail.setMigrerModelesFdr(migLogger.isMigrationModeleFdr());
            detail.setMigrationWithDossierClos(migLogger.isMigrationWithDossierClos());
            Long loggerId = migLogger.getId();
            detail.setMigrationId(Long.toString(loggerId));
            String errorMessage = SSConstant.FAILED_STATUS.equals(migLogger.getStatus())
                ? ResourceHelper.getString("historique.migration.echouee")
                : "";
            detail.setErrorMessage(errorMessage);

            String type = toUIMigrationType(migLogger.getTypeMigration());
            detail.setMigrationType(type);

            String nodeId = migLogger.getNewElement();
            detail.setNewElementId(nodeId);
            detail.setNewElement(organigrammeService.getOrganigrammeNodeById(nodeId).getLabel());
            detail.setMapNewElement(Collections.singletonMap(nodeId, detail.getNewElement()));

            nodeId = migLogger.getOldElement();
            detail.setOldElementId(nodeId);
            detail.setOldElement(organigrammeService.getOrganigrammeNodeById(nodeId).getLabel());
            detail.setMapOldElement(Collections.singletonMap(nodeId, detail.getOldElement()));

            nodeId = migLogger.getOldMinistere();
            if (StringUtils.isNotEmpty(nodeId)) {
                detail.setOldMinistereId(nodeId);
                detail.setOldMinistere(
                    organigrammeService.getOrganigrammeNodeById(nodeId, OrganigrammeType.MINISTERE).getLabel()
                );
                detail.setMapOldMinistere(Collections.singletonMap(nodeId, detail.getOldMinistere()));
            }

            nodeId = migLogger.getNewMinistere();
            if (StringUtils.isNotEmpty(nodeId)) {
                detail.setNewMinistereId(nodeId);
                detail.setNewMinistere(
                    organigrammeService.getOrganigrammeNodeById(nodeId, OrganigrammeType.MINISTERE).getLabel()
                );
                detail.setMapNewMinistere(Collections.singletonMap(nodeId, detail.getNewMinistere()));
            }

            Map<String, MigrationProgressDTO> map = new HashMap<>();

            for (String action : getActions().get(type)) {
                MigrationWidget widget = toMigrationWidget(action);
                if (widget != null) {
                    MigrationLoggerInfos migLoggerInfo = getMigrationLoggerInfos(widget.name(), loggerId);

                    MigrationProgressDTO migProgressDTO = new MigrationProgressDTO(
                        migLoggerInfo.getNbStart(),
                        migLoggerInfo.getNbCurrent(),
                        migLoggerInfo.getNbTotal(),
                        migLoggerInfo.getStatus()
                    );
                    map.put(action, migProgressDTO);
                }
            }

            detail.setActionProgress(map);

            details.add(detail);
        }

        migrationDto.setStatus(globalStatus);
        migrationDto.setDetails(details);

        return migrationDto;
    }

    protected SSOrganigrammeService getOrganigrammeService() {
        throw new UnsupportedOperationException("Implemented in app-specific classes");
    }

    private String toUIMigrationType(String migrationType) {
        return StringUtils.removeEnd(migrationType, MIGRATION_TYPE_SUFFIX);
    }

    private static MigrationWidget toMigrationWidget(String property) {
        switch (property) {
            case SSMigration.MIGRATION_DEPLACER_DIRECTION_PILOTES_MODELES:
                return MigrationWidget.migre_fdr_modele;
            case SSMigration.MIGRATION_DEPLACER_ELEMENT_FILS:
                return MigrationWidget.deplacer_element_fils;
            case SSMigration.MIGRATION_MIGRER_ETAPES_FDR_MODELES:
                return MigrationWidget.migrer_fdr_etape;
            case SSMigration.MIGRATION_MISE_A_JOUR_CORBEILLE_POSTE:
                return MigrationWidget.update_mailboxes;
            case SSMigration.MIGRATION_MISE_A_JOUR_DROITS_QE:
                return MigrationWidget.update_dossiers;
            case SSMigration.MIGRATION_MODIFIER_MINISTERE_DIRECTION_RATTACHEMENT:
                return MigrationWidget.rattachement_direction;
            case SSMigration.MIGRATION_REATTRIBUER_NOR_INITIES_LANCES:
                return MigrationWidget.reattribution_nor_direction;
            case SSMigration.MIGRATION_BULLETINS_OFFICIELS:
                return MigrationWidget.migrer_bulletin_officiel;
            case SSMigration.MIGRATION_LISTE_MOTS_CLES_GESTION_INDEXATION:
                return MigrationWidget.migrer_mots_cles;
            case SSMigration.MIGRATION_MISE_A_JOUR_DROITS_DOSSIERS:
                return MigrationWidget.update_creator_poste;
            default:
                return null;
        }
    }

    @Override
    public void ajouterMigrations(List<MigrationDetailDTO> migrationDetailDTOs, SpecificContext context) {
        for (MigrationDetailDTO migrationDetail : migrationDetailDTOs) {
            MigrationInfo migrationInfo = ajouterMigration(context);

            migrationDetail.setMigrationId(migrationInfo.getId());

            migrationInfo.setTypeMigration(toDbMigrationType(migrationDetail.getMigrationType()));
            migrationInfo.setOldMinistereElementOrganigramme(migrationDetail.getOldMinistereId());
            migrationInfo.setOldElementOrganigramme(migrationDetail.getOldElementId());
            migrationInfo.setNewMinistereElementOrganigramme(migrationDetail.getNewMinistereId());
            migrationInfo.setNewElementOrganigramme(migrationDetail.getNewElementId());

            migrationInfo.setDeleteOldElementOrganigramme(migrationDetail.getDeleteOld());
            migrationInfo.setMigrationModeleFdr(migrationDetail.getMigrerModelesFdr());
            migrationInfo.setMigrationWithDossierClos(migrationDetail.getMigrationWithDossierClos());
        }
    }

    private String toDbMigrationType(String migrationType) {
        return migrationType + MIGRATION_TYPE_SUFFIX;
    }

    public Map<String, List<String>> getActions() {
        return new HashMap<>();
    }

    protected <T extends SSHistoriqueMigrationDTO> void toSSHistoriqueMigrationDTO(
        MigrationLoggerModel migrationLoggerModel,
        T historiqueMigrationDTO
    ) {
        historiqueMigrationDTO.setId(Long.toString(migrationLoggerModel.getId()));

        historiqueMigrationDTO.setLabel(getChangementGouvernementService().getLogMessage(migrationLoggerModel));

        historiqueMigrationDTO.setStatus(MigrationStatus.getFrom(migrationLoggerModel));

        String elementsFils = Long.toString(migrationLoggerModel.getElementsFils());
        historiqueMigrationDTO.setElementFils(elementsFils + '/' + elementsFils);

        String modelesFDR = migrationLoggerModel.getModeleFdrCurrent() + "/" + migrationLoggerModel.getModeleFdrCount();
        historiqueMigrationDTO.setModeleFDR(modelesFDR);

        String postesCreateurs =
            migrationLoggerModel.getCreatorPosteCurrent() + "/" + migrationLoggerModel.getCreatorPosteCount();
        historiqueMigrationDTO.setPosteCreateur(postesCreateurs);

        historiqueMigrationDTO.setDateDebut(DateUtil.dateToGregorianCalendar(migrationLoggerModel.getStartDate()));
        historiqueMigrationDTO.setDateFin(DateUtil.dateToGregorianCalendar(migrationLoggerModel.getEndDate()));
    }
}
