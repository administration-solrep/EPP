package fr.dila.st.web.administration.batch;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchLoggerResultModel;
import fr.dila.st.api.event.batch.BatchResultModel;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.batch.BatchLoggerResultModelImpl;
import fr.dila.st.core.service.STServiceLocator;

/**
 * 
 * Bean pour le suivi des batchs.
 * 
 * @author JBT
 * 
 */
@Name("suiviBatchActions")
@SerializedConcurrentAccess
@Scope(CONVERSATION)
public class SuiviBatchActionsBean implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long				serialVersionUID	= 4078743293824530146L;

	@In(create = true, required = false)
	protected transient CoreSession			documentManager;

	@In(create = true)
	protected transient NavigationContext	navigationContext;

	@In(create = true, required = false)
	protected FacesMessages					facesMessages;

	@In(create = true)
	protected ResourcesAccessor				resourcesAccessor;

	@In(create = true)
	protected EventManager					eventManager;

	@In(create = true)
	protected Principal						currentUser;

	private BatchLoggerModel				currentBatchLogger;

	private Date							currentDateDebut;

	private Date							currentDateFin;

	private String							currentUserBatch;

	private boolean							isFirstAccess;

	public SuiviBatchActionsBean() {
		// Default constructor
	}

	@PostConstruct
	public void initialize() {
		this.reset();
		Calendar defaultTime = Calendar.getInstance();
		// Par défaut, date de la veille à 22h
		defaultTime.add(Calendar.DATE, -1);
		defaultTime.set(Calendar.SECOND, 0);
		defaultTime.set(Calendar.MINUTE, 0);
		defaultTime.set(Calendar.MILLISECOND, 0);
		defaultTime.set(Calendar.HOUR_OF_DAY, 22);
		currentDateDebut = defaultTime.getTime();
		isFirstAccess = true;
	}

	/**
	 * Controle l'accès à la vue correspondante
	 * 
	 */
	public boolean isAccessAuthorized() {
		STPrincipal ssPrincipal = (STPrincipal) currentUser;
		return (ssPrincipal.isAdministrator() || ssPrincipal
				.isMemberOf(STBaseFunctionConstant.BATCH_READER));
	}

	/**
	 * Retourne la liste des logs
	 * 
	 * @throws ClientException
	 * 
	 */
	public List<BatchLoggerModel> getAllBatchLogger() throws ClientException {
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		Calendar startTime = null;
		if (currentDateDebut != null) {
			startTime = Calendar.getInstance();
			startTime.setTime(currentDateDebut);
			// On ne change l'heure (par défaut) s'il s'agit du premier accès
			if (!isFirstAccess) {
				startTime.set(Calendar.HOUR_OF_DAY, 0);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.SECOND, 0);
				startTime.set(Calendar.MILLISECOND, 0);
			}
		}
		Calendar endTime = null;
		if (currentDateFin != null) {
			endTime = Calendar.getInstance();
			endTime.setTime(currentDateFin);
			endTime.set(Calendar.HOUR_OF_DAY, 23);
			endTime.set(Calendar.MINUTE, 59);
			endTime.set(Calendar.SECOND, 59);
			endTime.set(Calendar.MILLISECOND, 999);
		}
		return suiviBatchService.findBatchLog(startTime, endTime, 0L);
	}

	public void setCurrentLog(long idBatchLogger) throws ClientException {
		currentBatchLogger = STServiceLocator.getSuiviBatchService().findBatchLogById(idBatchLogger);
	}

	public void cleanCurrentLog() {
		currentBatchLogger = null;
	}

	public List<BatchLoggerResultModel> getCurrentBatchResult() throws ClientException {
		final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
		List<BatchLoggerResultModel> batchLogResult = new ArrayList<BatchLoggerResultModel>();
		if (currentBatchLogger != null) {
			// Récupération des batch result du batch log sélectionné
			List<BatchResultModel> batchResult = suiviBatchService.findBatchResult(currentBatchLogger);
			if (batchResult != null && !batchResult.isEmpty()) {
				for (BatchResultModel result : batchResult) {
					batchLogResult.add(new BatchLoggerResultModelImpl(currentBatchLogger, result));
				}
			}
			// Création d'un objet qui est le résultat des batch log et
			// leurs batch result associés pour les enfants du batch log sélectionné
			List<BatchLoggerModel> listBatchLog = suiviBatchService.findBatchLog(null, null,
					currentBatchLogger.getIdLog());
			for (BatchLoggerModel log : listBatchLog) {
				List<BatchResultModel> listBatchResult = suiviBatchService.findBatchResult(log);
				if (listBatchResult != null && !listBatchResult.isEmpty()) {
					for (BatchResultModel result : listBatchResult) {
						batchLogResult.add(new BatchLoggerResultModelImpl(log, result));
					}
				}
			}
			return batchLogResult;
		} else {
			return null;
		}
	}

	public Date getCurrentDateDebut() {
		return currentDateDebut;
	}

	public void setCurrentDateDebut(Date currentDateDebut) {
		if (isFirstAccess && currentDateDebut != null && this.currentDateDebut != null
				&& this.currentDateDebut.compareTo(currentDateDebut) != 0) {
			isFirstAccess = false;
		}
		this.currentDateDebut = currentDateDebut;
	}

	public Date getCurrentDateFin() {
		return currentDateFin;
	}

	public void setCurrentDateFin(Date currentDateFin) {
		this.currentDateFin = currentDateFin;
	}

	public String getCurrentUserBatch() {
		return currentUserBatch;
	}

	public void setCurrentUserBatch(String currentUserBatch) {
		this.currentUserBatch = currentUserBatch;
	}

	public void reset() {
		currentDateDebut = null;
		currentDateFin = null;
		currentUserBatch = null;
		isFirstAccess = false;
	}

	public List<List<String>> getAllPlanification() {
		try {
			return STServiceLocator.getSuiviBatchService().findQrtzInfo(documentManager);
		} catch (ClientException e) {
			return null;
		}
	}

	public void addUserToNotify() throws ClientException {
		if (currentUserBatch != null) {
			STServiceLocator.getNotificationsSuiviBatchsService().addUserName(documentManager, getCurrentUserBatch());
			currentUserBatch = null;
		}
	}

	public List<String> getAllUsername() throws ClientException {
		return STServiceLocator.getNotificationsSuiviBatchsService().getAllUserName(documentManager);
	}

	public void removeUsername(String username) throws ClientException {
		STServiceLocator.getNotificationsSuiviBatchsService().removeUserName(documentManager, username);
	}

	public boolean isNotificationActive() throws ClientException {
		return STServiceLocator.getNotificationsSuiviBatchsService().sontActiveesNotifications(documentManager);
	}

	public void activateNotification() throws ClientException {
		STServiceLocator.getNotificationsSuiviBatchsService().activerNotifications(documentManager);
	}

	public void deactivateNotification() throws ClientException {
		STServiceLocator.getNotificationsSuiviBatchsService().desactiverNotifications(documentManager);
	}

	public String getTypeBatch(String typeBatch) {
		TypeBatch type = TypeBatch.findByName(typeBatch);
		if (type == null) {
			return TypeBatch.TECHNIQUE.getLabel();
		} else {
			return type.getLabel();
		}
	}
}
