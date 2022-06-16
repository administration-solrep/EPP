package fr.dila.st.ui.services.batch;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchLoggerResultModel;
import fr.dila.st.api.event.batch.QuartzInfo;
import fr.dila.st.api.model.Page;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.batch.BatchLoggerResultModelImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.BatchDTO;
import fr.dila.st.ui.bean.BatchDetailDTO;
import fr.dila.st.ui.bean.BatchDetailListe;
import fr.dila.st.ui.bean.BatchListe;
import fr.dila.st.ui.bean.BatchPlanificationDTO;
import fr.dila.st.ui.bean.BatchPlanificationListe;
import fr.dila.st.ui.th.bean.BatchNotifForm;
import fr.dila.st.ui.th.bean.BatchSearchForm;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Bean pour le suivi des batchs.
 *
 * @author JBT
 */
public class SuiviBatchUIServiceImpl implements SuiviBatchUIService {

    public SuiviBatchUIServiceImpl() {
        // Default constructor
    }

    @Override
    public Date getDefaultCurrentDateDebut() {
        Calendar defaultTime = Calendar.getInstance();
        // Par défaut, date de la veille à 22h
        defaultTime.add(Calendar.DATE, -1);
        defaultTime.set(Calendar.SECOND, 0);
        defaultTime.set(Calendar.MINUTE, 0);
        defaultTime.set(Calendar.MILLISECOND, 0);
        defaultTime.set(Calendar.HOUR_OF_DAY, 22);
        return defaultTime.getTime();
    }

    @Override
    public boolean isAccessAuthorized(STPrincipal currentUser) {
        return (currentUser.isAdministrator() || currentUser.isMemberOf(STBaseFunctionConstant.BATCH_READER));
    }

    @Override
    public Page<BatchLoggerModel> getAllBatchLogger(
        Date currentDateDebut,
        Date currentDateFin,
        boolean isFirstAccess,
        Integer page,
        Integer pageSize
    ) {
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        Calendar startTime = null;
        if (currentDateDebut != null) {
            startTime = DateUtil.toCalendarFromNotNullDate(currentDateDebut);
            // On ne change l'heure (par défaut) s'il s'agit du premier accès
            if (!isFirstAccess) {
                DateUtil.setDateToBeginingOfDay(startTime);
            }
        }
        Calendar endTime = null;
        if (currentDateFin != null) {
            endTime = DateUtil.toCalendarFromNotNullDate(currentDateFin);
            DateUtil.setDateToEndOfDay(endTime);
        }

        return suiviBatchService.findBatchLog(startTime, endTime, 0L, page, pageSize);
    }

    @Override
    public List<BatchLoggerResultModel> getCurrentBatchResult(BatchLoggerModel currentBatchLogger) {
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        List<BatchLoggerResultModel> batchLogResult = new ArrayList<>();

        Consumer<BatchLoggerModel> batchLogResultCollector = batchLoggerModel ->
            Optional
                .ofNullable(batchLoggerModel)
                .map(cbl -> suiviBatchService.findBatchResult(batchLoggerModel))
                .map(List::stream)
                .map(br -> br.map(result -> new BatchLoggerResultModelImpl(batchLoggerModel, result)))
                .ifPresent(blr -> batchLogResult.addAll(blr.collect(Collectors.toList())));

        // Récupération des batch result du batch log sélectionné
        batchLogResultCollector.accept(currentBatchLogger);

        // Création d'un objet qui est le résultat des batch log et
        // leurs batch result associés pour les enfants du batch log sélectionné
        suiviBatchService
            .findBatchLog(null, null, currentBatchLogger.getIdLog(), null, null)
            .getResults()
            .forEach(batchLogResultCollector::accept);

        return batchLogResult;
    }

    @Override
    public List<QuartzInfo> getAllPlanification(CoreSession session) {
        return STServiceLocator.getSuiviBatchService().findQrtzInfo(session);
    }

    @Override
    public BatchNotifForm initBatchNotifForm(CoreSession session) {
        BatchNotifForm form = new BatchNotifForm();
        form.setActivee(Boolean.toString(isNotificationActive(session)));
        form.setMapUtilisateurs(getMapUsernames(session));
        return form;
    }

    /**
     * Renvoie une map des utilisateurs à notifier pour affichage.
     *
     * @param session
     * @return Une map login -> Nom Prénom
     */
    private Map<String, String> getMapUsernames(CoreSession session) {
        return STServiceLocator
            .getNotificationsSuiviBatchsService()
            .getAllUsers(session)
            .stream()
            .collect(Collectors.toMap(STUser::getUsername, stUser -> stUser.getReversedFullName()));
    }

    private boolean isNotificationActive(CoreSession session) {
        return STServiceLocator.getNotificationsSuiviBatchsService().sontActiveesNotifications(session);
    }

    private void activateNotification(CoreSession session) {
        STServiceLocator.getNotificationsSuiviBatchsService().activerNotifications(session);
    }

    private void deactivateNotification(CoreSession session) {
        STServiceLocator.getNotificationsSuiviBatchsService().desactiverNotifications(session);
    }

    @Override
    public void updateNotification(BatchNotifForm batchNotifForm, CoreSession session) {
        // Activation
        if (Boolean.parseBoolean(batchNotifForm.getActivee())) {
            activateNotification(session);
        } else {
            deactivateNotification(session);
        }

        // Users
        STServiceLocator
            .getNotificationsSuiviBatchsService()
            .updateUserNames(batchNotifForm.getListeUtilisateurs(), session);
    }

    @Override
    public String getTypeBatch(String typeBatch) {
        TypeBatch type = TypeBatch.findByName(typeBatch);
        if (type == null) {
            return TypeBatch.TECHNIQUE.getLabel();
        } else {
            return type.getLabel();
        }
    }

    @Override
    public BatchListe getBatchListe(BatchSearchForm batchSearchForm) {
        BatchListe batchListe = new BatchListe();
        String dateDebStr = batchSearchForm.getDebut();
        String dateFinStr = batchSearchForm.getFin();
        Page<BatchLoggerModel> pageAllBatchs = getAllBatchLogger(
            SolonDateConverter.DATE_SLASH.parseToDateOrNull(dateDebStr),
            SolonDateConverter.DATE_SLASH.parseToDateOrNull(dateFinStr),
            true,
            batchSearchForm.getPage(),
            batchSearchForm.getSize()
        );
        List<BatchDTO> dtos = pageAllBatchs
            .getResults()
            .stream()
            .map(SuiviBatchUIServiceImpl::toBatchDTO)
            .collect(Collectors.toList());
        batchListe.setListe(dtos);
        batchListe.setNbTotal(pageAllBatchs.getResultsCount());
        batchListe.buildColonnes();
        return batchListe;
    }

    private static BatchDTO toBatchDTO(BatchLoggerModel batchLoggerModel) {
        BatchDTO batchDTO = new BatchDTO();

        Calendar calBeg = batchLoggerModel.getStartTime();
        if (calBeg != null) {
            batchDTO.setDebut(calBeg.getTime());
        }
        Calendar calEnd = batchLoggerModel.getEndTime();
        if (calEnd != null) {
            batchDTO.setFin(calEnd.getTime());
        }

        batchDTO.setErreurs(batchLoggerModel.getErrorCount());
        batchDTO.setId(Long.toString(batchLoggerModel.getIdLog()));
        batchDTO.setNom(batchLoggerModel.getName());
        batchDTO.setServeur(batchLoggerModel.getServer());
        batchDTO.setTomcat((int) batchLoggerModel.getTomcat());
        batchDTO.setType(batchLoggerModel.getType());

        return batchDTO;
    }

    @Override
    public BatchDetailListe getBatchDetailListe(String batchLoggerModelId) {
        BatchDetailListe batchDetailListe = new BatchDetailListe();
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        BatchLoggerModel batchLoggerModel = suiviBatchService.findBatchLogById(new Long(batchLoggerModelId));

        List<BatchDetailDTO> batchDetailDTOs = getCurrentBatchResult(batchLoggerModel)
            .stream()
            .map(SuiviBatchUIServiceImpl::toBatchDetailDTO)
            .collect(Collectors.toList());

        batchDetailListe.setListe(batchDetailDTOs);
        batchDetailListe.setNbTotal(batchDetailDTOs.size());
        batchDetailListe.setNom(batchLoggerModel.getName());

        return batchDetailListe;
    }

    private static BatchDetailDTO toBatchDetailDTO(BatchLoggerResultModel batchLoggerResultModel) {
        BatchDetailDTO batchDetailDTO = new BatchDetailDTO();

        Calendar calBeg = batchLoggerResultModel.getStartTime();
        if (calBeg != null) {
            batchDetailDTO.setDebut(calBeg.getTime());
        }
        Calendar calEnd = batchLoggerResultModel.getEndTime();
        if (calEnd != null) {
            batchDetailDTO.setFin(calEnd.getTime());
        }

        batchDetailDTO.setDuree(batchLoggerResultModel.getExecutionTime());
        batchDetailDTO.setErreurs((int) batchLoggerResultModel.getErrorCount());
        batchDetailDTO.setMessage(batchLoggerResultModel.getText());
        batchDetailDTO.setNom(batchLoggerResultModel.getName());
        batchDetailDTO.setResultat(batchLoggerResultModel.getExecutionResult());
        batchDetailDTO.setType(batchLoggerResultModel.getType());

        return batchDetailDTO;
    }

    @Override
    public BatchPlanificationListe getBatchPlanificationListe(CoreSession session) {
        BatchPlanificationListe batchPlanificationListe = new BatchPlanificationListe();

        batchPlanificationListe.setListe(
            getAllPlanification(session)
                .stream()
                .map(SuiviBatchUIServiceImpl::toBatchPlanificationTDO)
                .collect(Collectors.toList())
        );

        return batchPlanificationListe;
    }

    private static BatchPlanificationDTO toBatchPlanificationTDO(QuartzInfo batchPlanification) {
        BatchPlanificationDTO batchPlanificationDTO = new BatchPlanificationDTO();

        batchPlanificationDTO.setNom(batchPlanification.getJobName());
        batchPlanificationDTO.setDateDernierDeclenchement(batchPlanification.getPrevTime());
        batchPlanificationDTO.setDateProchainDeclenchement(batchPlanification.getNextTime());
        batchPlanificationDTO.setPeriodicite(batchPlanification.getCronExpr());

        return batchPlanificationDTO;
    }
}
