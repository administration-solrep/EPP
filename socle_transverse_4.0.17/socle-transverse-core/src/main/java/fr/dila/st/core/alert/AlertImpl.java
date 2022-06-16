package fr.dila.st.core.alert;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.exception.UserNotFoundException;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation Alert
 *
 */
public class AlertImpl implements Alert {
    private static final STLogger LOGGER = STLogFactory.getLog(AlertImpl.class);

    private DocumentModel document;

    public AlertImpl(DocumentModel doc) {
        this.document = doc;
    }

    @Override
    public void setDocument(DocumentModel doc) {
        this.document = doc;
    }

    @Override
    public DocumentModel getDocument() {
        return this.document;
    }

    @Override
    public Boolean isActivated() {
        return PropertyUtil.getBooleanProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_IS_ACTIVATED
        );
    }

    @Override
    public void setIsActivated(Boolean isActivated) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_IS_ACTIVATED,
            isActivated
        );
    }

    @Override
    public List<String> getRecipientIds() {
        return PropertyUtil.getStringListProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_RECIPIENTS
        );
    }

    @Override
    public void setRecipientIds(List<String> recipients) {
        PropertyUtil.setProperty(document, STAlertConstant.ALERT_SCHEMA, STAlertConstant.ALERT_RECIPIENTS, recipients);
    }

    @Override
    public void setTitle(String title) {
        DublincoreSchemaUtils.setTitle(document, title);
    }

    @Override
    public String getTitle() {
        return DublincoreSchemaUtils.getTitle(document);
    }

    @Override
    public boolean shouldBeRun() {
        return isActivated() && isBetweenValidityRange() && periodicityChecked();
    }

    @Override
    public String getRequeteId() {
        return PropertyUtil.getStringProperty(document, STAlertConstant.ALERT_SCHEMA, STAlertConstant.ALERT_REQUETE_ID);
    }

    @Override
    public void setDateValidityBegin(Calendar dateBegin) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_DATE_VALIDITY_BEGIN,
            dateBegin
        );
    }

    @Override
    public Calendar getDateValidityBegin() {
        return PropertyUtil.getCalendarProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_DATE_VALIDITY_BEGIN
        );
    }

    @Override
    public void setDateValidityEnd(Calendar dateEnd) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_DATE_VALIDITY_END,
            dateEnd
        );
    }

    @Override
    public Calendar getDateValidityEnd() {
        return PropertyUtil.getCalendarProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_DATE_VALIDITY_END
        );
    }

    @Override
    public void setRequeteId(String requeteId) {
        PropertyUtil.setProperty(document, STAlertConstant.ALERT_SCHEMA, STAlertConstant.ALERT_REQUETE_ID, requeteId);
    }

    @Override
    public String getCronExpression() {
        return "*/7 * * * * ?";
    }

    @Override
    public void setPeriodicity(Integer periodicity) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_PERIODICITY,
            periodicity.toString()
        );
    }

    @Override
    public Integer getPeriodicity() {
        String periode = PropertyUtil.getStringProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_PERIODICITY
        );
        return Integer.parseInt(periode);
    }

    @Override
    public Boolean periodicityChecked() {
        return periodicityChecked(GregorianCalendar.getInstance());
    }

    @Override
    public Boolean periodicityChecked(Calendar calExecution) {
        DateTime dateExecution = new DateTime(calExecution);
        DateTime dateDebut = new DateTime(getDateValidityBegin());
        Days days = Days.daysBetween(dateDebut, dateExecution);
        Integer periodicity = getPeriodicity();
        if (Integer.valueOf(0).equals(periodicity)) {
            return false;
        }
        return (days.getDays() % periodicity) == 0;
    }

    @Override
    public Boolean isBetweenValidityRange() {
        if (getDateValidityBegin() == null || getDateValidityEnd() == null) {
            return false;
        }
        DateTime dateDebut = new DateTime(getDateValidityBegin());
        DateTime dateFin = new DateTime(getDateValidityEnd());
        return dateDebut.isBeforeNow() && dateFin.isAfterNow();
    }

    @Override
    public List<STUser> getRecipients() {
        STUserService stUserService = STServiceLocator.getSTUserService();
        return getRecipientIds()
            .stream()
            .map(
                id -> {
                    try {
                        return stUserService.getUser(id);
                    } catch (UserNotFoundException e) {
                        LOGGER.warn(STLogEnumImpl.FAIL_GET_USER_TEC, e, "Utilisateur [" + id + "] non trouvé");
                        return null;
                    }
                }
            )
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public RequeteExperte getRequeteExperte(CoreSession session) {
        DocumentModel requeteDoc;
        try {
            requeteDoc = session.getDocument(new IdRef(getRequeteId()));
        } catch (NuxeoException exc) {
            STLogFactory.getLog(AlertImpl.class).warn(session, STLogEnumImpl.FAIL_GET_ALERT_TEC, exc);
            return null;
        }
        return requeteDoc.getAdapter(RequeteExperte.class);
    }

    @Override
    public String getNameCreator() {
        return DublincoreSchemaUtils.getCreator(document);
    }

    @Override
    public List<String> getExternalRecipients() {
        return PropertyUtil.getStringListProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_EXTERNAL_RECIPIENTS
        );
    }

    @Override
    public void setExternalRecipients(List<String> externalRecipients) {
        PropertyUtil.setProperty(
            document,
            STAlertConstant.ALERT_SCHEMA,
            STAlertConstant.ALERT_EXTERNAL_RECIPIENTS,
            externalRecipients
        );
    }
}
