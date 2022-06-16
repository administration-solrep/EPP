package fr.dila.st.core.mail;

import static fr.dila.st.api.constant.STConfigConstants.SOLON_MAIL_PREFIX_BODY;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_MAIL_PREFIX_OBJECT;
import static fr.dila.st.core.service.STMailServiceImpl.CONTENT_FORMAT;
import static fr.dila.st.core.service.STMailServiceImpl.LOG_SEND_ERROR_TO_PLURAL;
import static fr.dila.st.core.service.STMailServiceImpl.LOG_SEND_OBJECT_TO_PLURAL;
import static java.lang.String.format;
import static org.apache.commons.text.StringEscapeUtils.unescapeHtml4;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.MailSessionHelper;
import fr.dila.st.core.util.STMailHelper;
import fr.dila.st.core.work.SolonWork;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.transientstore.api.TransientStore;
import org.nuxeo.ecm.core.transientstore.api.TransientStoreService;
import org.nuxeo.runtime.api.Framework;

public class SendMailWork extends SolonWork {
    private final String solonMailPrefixObject;

    private final String solonMailPrefixBody;

    /**
     *  Valeur par défaut pour le délai entre deux envois successifs, en secondes (FEV 552)
     */
    private static final int DEFAULT_DELAY = 30;
    private static final int DEFAULT_MAX_RECIPIENTS_LIMIT = -1;

    private static final long serialVersionUID = 1138927001413060800L;

    private static final STLogger LOGGER = STLogFactory.getLog(SendMailWork.class);

    private static final String LOG_SEND_CC = " et en copie aux adresses suivantes : %s";
    private static final String LOG_SEND_BCC = " et en copie carbone aux adresses suivantes : %s";
    private static final String PREFIX_COMMENT = "Mél - ";

    // Nom du transient store
    private static final String TRANSIENT_STORE = "mailblobs";

    private String subject;
    private String content;
    private MailAddress mailAddress;
    private JournalSendMail journalSendMail;

    /** Nombre max de destinataires/cc/cci adressés par un mail */
    private Integer limit = null;
    /** Délai à respecter entre deux mails successifs */
    private Integer delay = null;

    public SendMailWork(
        final String subject,
        final String content,
        final MailAddress mailAddress,
        final JournalSendMail journalSendMail
    ) {
        super();
        ConfigService configService = STServiceLocator.getConfigService();
        solonMailPrefixObject = unescapeHtml4(configService.getValue(SOLON_MAIL_PREFIX_OBJECT, ""));
        solonMailPrefixBody = unescapeHtml4(configService.getValue(SOLON_MAIL_PREFIX_BODY, ""));
        this.subject = subject;
        this.content = content;
        this.mailAddress = mailAddress;
        this.journalSendMail = journalSendMail;
    }

    @Override
    public String getTitle() {
        return "Envoi d'un mail";
    }

    @Override
    public void doWork() {
        prepareAndSendMail();
    }

    /** Session mail à utiliser pour l'envoi */
    private transient Session mailSession;

    private Session getMailSession() {
        if (mailSession == null) {
            mailSession = MailSessionHelper.getMailSession();
        }
        return mailSession;
    }

    /** Transient store a utiliser pour les pièces jointes */
    private transient TransientStore store;

    public TransientStore getTransientStore() {
        if (store == null) {
            TransientStoreService storeService = Framework.getService(TransientStoreService.class);
            store = storeService.getStore(TRANSIENT_STORE);
        }
        return store;
    }

    private void prepareAndSendMail() {
        try {
            retrieveMailParameters();

            int nbDestinataires = CollectionUtils.size(mailAddress.getMailsTo());
            int nbCopies = CollectionUtils.size(mailAddress.getMailsCc());
            int nbCCI = CollectionUtils.size(mailAddress.getMailsCci());

            if (limit == DEFAULT_MAX_RECIPIENTS_LIMIT || nbDestinataires + nbCopies + nbCCI <= limit) {
                // cas avant FEV 552 => on envoie tout en un seul mail
                List<Address> appMail = mailAddress.getMailsTo();
                if (nbDestinataires == 0) {
                    appMail = new ArrayList<>();
                    appMail.add(mailAddress.getMailFrom());
                }
                send(buildMessage(appMail, mailAddress.getMailsCc(), mailAddress.getMailsCci()));
            } else {
                // à partir d'ici on envoie les mails par lots comme
                // définis dans la configuration.
                sendTo();
                sendCc();
                sendCci();
            }

            if (journalSendMail != null) {
                String username = journalSendMail.getUsername();
                String docId = journalSendMail.getDocId();
                if (docId == null) {
                    STServiceLocator
                        .getJournalService()
                        .journaliserActionAdministration(
                            username,
                            journalSendMail.getEventName(),
                            PREFIX_COMMENT + subject
                        );
                } else {
                    STServiceLocator
                        .getJournalService()
                        .journaliserAction(
                            username,
                            docId,
                            journalSendMail.getEventName(),
                            subject,
                            journalSendMail.getCategory()
                        );
                }
            }
        } catch (MessagingException e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                e,
                format(LOG_SEND_ERROR_TO_PLURAL, subject, StringUtils.join(mailAddress.getMailsTo(), ", "))
            );
        } finally {
            getTransientStore().release(getId());
        }
    }

    /**
     * On envoie des mails à l'@ de l'appli et ceux en copie cachée (cci) par lots
     * entrecoupés du délai.
     */
    private void sendCci() {
        if (CollectionUtils.isNotEmpty(mailAddress.getMailsCci())) {
            send(
                mailAddress.getMailsCci(),
                RecipientType.BCC,
                format(LOG_SEND_ERROR_TO_PLURAL, subject, "%1$s") + format(LOG_SEND_BCC, "%2$s")
            );
        }
    }

    private Message buildMessageRecipients(List<Address> senders, List<Address> addresses, RecipientType recipientType)
        throws MessagingException {
        if (recipientType == RecipientType.CC) {
            return buildMessage(senders, addresses, null);
        } else if (recipientType == RecipientType.BCC) {
            return buildMessage(senders, null, addresses);
        }

        // Cas par défaut : on envoie en destinataire TO
        return buildMessage(addresses, null, null);
    }

    /**
     * On envoie des mails à l'@ de l'appli et celles en copie conforme (CC) par
     * lots entrecoupés du délai
     *
     * @throws InterruptedException
     */
    private void sendCc() {
        if (CollectionUtils.isNotEmpty(mailAddress.getMailsCc())) {
            send(
                mailAddress.getMailsCc(),
                RecipientType.CC,
                format(LOG_SEND_ERROR_TO_PLURAL, subject, "%1$s") + format(LOG_SEND_CC, "%2$s")
            );
        }
    }

    private void send(List<Address> recipientList, RecipientType recipientType, String errorMessage) {
        int offset = 0;
        int end = 0;

        // Adresse de l'application (no-reply) en tant que
        // destinataire dans le cas des CC et CCi
        List<Address> appMail = new ArrayList<>();
        appMail.add(mailAddress.getMailFrom());

        int nb = recipientList.size();

        while (offset < nb) {
            end = Math.min(offset + limit, nb);
            List<Address> recipientSubList = recipientList.subList(offset, end);
            try {
                send(buildMessageRecipients(appMail, recipientSubList, recipientType));
                try {
                    TimeUnit.SECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new NuxeoException(e);
                }
            } catch (MessagingException e) {
                String msg = format(
                    errorMessage,
                    StringUtils.join(appMail, ", "),
                    StringUtils.join(recipientSubList, ", ")
                );
                LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e, msg);
                continue;
            } finally {
                offset = end;
            }
        }
    }

    /**
     * On envoie des mails aux destinataires directs par lots entrecoupés du délai
     *
     * @throws InterruptedException
     */
    private void sendTo() {
        if (CollectionUtils.isNotEmpty(mailAddress.getMailsTo())) {
            send(mailAddress.getMailsTo(), RecipientType.TO, format(LOG_SEND_ERROR_TO_PLURAL, subject, "%2$s"));
        }
    }

    /**
     * Récupération de l'ensemble des éléments de configuration.
     */
    private void retrieveMailParameters() {
        if (limit != null && delay != null) {
            // Tous les paramètres ont déjà été récupérés dans la config, pas nécessaire de
            // le refaire.
            return;
        }

        ConfigService configService = STServiceLocator.getConfigService();

        try {
            limit = configService.getIntegerValue(STConfigConstants.SEND_MAIL_LIMIT);
            delay = configService.getIntegerValue(STConfigConstants.SEND_MAIL_DELAY);
        } catch (NumberFormatException | NuxeoException e) {
            LOGGER.warn(
                STLogEnumImpl.SEND_MAIL_TEC,
                e,
                "Erreur lors de la récupération du paramétrage de l'envoi de masse : " + e.getMessage()
            );
            limit = DEFAULT_MAX_RECIPIENTS_LIMIT;
            delay = DEFAULT_DELAY;
        }
    }

    private Message buildMessage(
        final List<Address> emailAddress,
        final List<Address> emailCopy,
        final List<Address> emailCCI
    )
        throws MessagingException {
        Message message = new MimeMessage(getMailSession());

        // Destinataires
        message.setRecipients(Message.RecipientType.TO, emailAddress.toArray(new Address[0]));

        if (CollectionUtils.isNotEmpty(emailCopy)) {
            message.setRecipients(Message.RecipientType.CC, emailCopy.toArray(new Address[0]));
        }

        if (CollectionUtils.isNotEmpty(emailCCI)) {
            message.setRecipients(Message.RecipientType.BCC, emailCCI.toArray(new Address[0]));
        }

        // Contenu
        message.setSubject(solonMailPrefixObject + subject);

        List<Blob> blobsList = getTransientStore().getBlobs(getId());

        if (CollectionUtils.isNotEmpty(blobsList)) {
            message.setContent(STMailHelper.initMultipart(solonMailPrefixBody + content, blobsList), CONTENT_FORMAT);
        } else {
            message.setContent(solonMailPrefixBody + content, CONTENT_FORMAT);
        }

        message.setFrom(mailAddress.getMailFrom());

        // From et sent date
        STMailHelper.setSentDate(message);
        return message;
    }

    /**
     * Trace l'envoi du message avant de l'envoyer réellement.
     *
     * @param message un message
     * @throws MessagingException
     */
    private void send(Message message) throws MessagingException {
        StringBuilder strBuilder = new StringBuilder(
            format(
                LOG_SEND_OBJECT_TO_PLURAL,
                message.getSubject(),
                joinAddresses(message.getRecipients(RecipientType.TO))
            )
        );

        Address[] ccDest = message.getRecipients(RecipientType.CC);
        if (ArrayUtils.isNotEmpty(ccDest)) {
            strBuilder.append(format(LOG_SEND_CC, joinAddresses(ccDest)));
        }

        Address[] bccDest = message.getRecipients(RecipientType.BCC);
        if (ArrayUtils.isNotEmpty(bccDest)) {
            strBuilder.append(format(LOG_SEND_BCC, joinAddresses(bccDest)));
        }

        LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC, strBuilder.toString());

        Transport.send(message);
    }

    private static String joinAddresses(Address[] emailAdresses) {
        return Stream.of(emailAdresses).map(Address::toString).collect(Collectors.joining(", "));
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MailAddress getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(MailAddress mailAddress) {
        this.mailAddress = mailAddress;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getDelay() {
        return delay;
    }
}
