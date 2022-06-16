package fr.dila.st.core.util;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STMailServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;

/**
 * Helper dédié aux envois de mails.
 *
 * @author tlombard
 *
 */
public class STMailHelper {
    protected static final String DEFAULT_MAIL_FROM = "ne-pas-repondre@dila.gouv.fr";

    private static final STLogger LOGGER = STLogFactory.getLog(STMailHelper.class);

    private STMailHelper() {
        // Do nothing
    }

    public static InternetAddress retrieveSenderMailFromConfig() {
        String solonMailPrefixForm = STServiceLocator
            .getConfigService()
            .getValue(STConfigConstants.SOLON_MAIL_PREFIX_FROM, "");
        String mailFrom;
        if (StringUtils.isNotEmpty(solonMailPrefixForm)) {
            mailFrom = solonMailPrefixForm + STServiceLocator.getConfigService().getValue(STConfigConstants.MAIL_FROM);
        } else {
            mailFrom = STServiceLocator.getConfigService().getValue(STConfigConstants.MAIL_FROM);
        }
        return toInternetAddress(StringUtils.defaultIfBlank(mailFrom, DEFAULT_MAIL_FROM));
    }

    public static List<Address> toMailAdresses(List<String> recipientsEmail) {
        return ObjectHelper
            .requireNonNullElseGet(recipientsEmail, (Supplier<List<String>>) ArrayList::new)
            .stream()
            .map(STMailHelper::toInternetAddress)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Construit un objet InternetAddress à partir d'une adresse mail String. Si la
     * String en entrée ne peut pas être parsée en adresse mail, on renvoie null =>
     * bien penser à filtrer ces valeurs.
     */
    private static InternetAddress toInternetAddress(String mailStr) {
        try {
            return new InternetAddress(mailStr);
        } catch (AddressException e) {
            LOGGER.warn(STLogEnumImpl.FAIL_GET_MAIL_TEC, e);
            return null;
        }
    }

    public static List<AbstractBlob> initAbstractBlobList(String nomFichier, DataSource datasource) throws IOException {
        return ImmutableList.of(BlobUtils.toByteArrayBlob(datasource, nomFichier));
    }

    public static List<AbstractBlob> initAbstractBlobList(DocumentModel docModel) throws IOException {
        if (docModel == null) {
            return new ArrayList<>();
        }
        // Attach the files to the data handler
        BlobHolder blobHolder = docModel.getAdapter(BlobHolder.class);

        Blob blob = blobHolder.getBlob();

        return ImmutableList.of(BlobUtils.toByteArrayBlob(blob));
    }

    /**
     * Initialise une instance Multipart avec les data à envoyer
     *
     * @param content  : contenu texte du multipart
     * @param dataMime : contenu pj du multipart
     * @param fileName : nom du fichier de la pj
     * @return le multipart créé
     * @throws MessagingException
     */
    public static Multipart initMultipart(String content, List<Blob> blobs) {
        Multipart multipart = new MimeMultipart();
        // Create and fill the first message part
        MimeBodyPart mainBodyPart = new MimeBodyPart();
        try {
            mainBodyPart.setContent(content, STMailServiceImpl.CONTENT_FORMAT);
            multipart.addBodyPart(mainBodyPart);

            if (CollectionUtils.isNotEmpty(blobs)) {
                for (Blob blob : blobs) {
                    MimeBodyPart bodyPart = new MimeBodyPart();

                    bodyPart.setDataHandler(toDataHandler(blob));
                    bodyPart.setFileName(MimeUtility.encodeText(blob.getFilename(), "UTF-8", null));
                    multipart.addBodyPart(bodyPart);
                }
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new NuxeoException(e);
        }

        return multipart;
    }

    private static DataHandler toDataHandler(Blob blob) {
        try {
            return new DataHandler(new ByteArrayDataSource(blob.getByteArray(), blob.getMimeType()));
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    /**
     * Renseigne les listes d'adresse et de destinataires à partir d'une liste
     * d'user
     *
     * @param userList     la liste d'utilisateur source
     * @param recipients   liste d'Address des destinataires
     * @param listeAdresse liste des mails des destinataires
     * @return true s'il existe des destinataires
     */
    public static boolean fillRecipients(List<STUser> userList, List<Address> recipients, List<String> listeAdresse) {
        boolean isRecipients = false;
        if (userList != null) {
            for (STUser user : userList) {
                String mailAdress = user.getEmail();
                if (StringUtils.isNotEmpty(mailAdress) && !listeAdresse.contains(mailAdress)) {
                    Address address = toInternetAddress(mailAdress);
                    if (address != null) {
                        isRecipients = true;
                        listeAdresse.add(mailAdress);
                        recipients.add(address);
                    }
                }
            }
        }
        return isRecipients;
    }

    public static List<Address> toListAddress(List<STUser> users) {
        return users
            .stream()
            .map(STUser::getEmail)
            .map(mailStr -> toInternetAddress(mailStr))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<String> toListMailString(List<STUser> users) {
        return users.stream().map(STUser::getEmail).collect(Collectors.toList());
    }

    /**
     * Ajoute la date d'expedition a un mail
     */
    public static void setSentDate(Message message) throws MessagingException {
        message.setSentDate(Calendar.getInstance().getTime());
    }

    /**
     * Initialisation d'une liste de DataHandler avec un zip contenant l'ensemble des fichiers passés en paramètre.
     */
    public static List<AbstractBlob> initAbstractBlobListWithZip(List<ExportBlob> listFiles) throws IOException {
        // now write the ZIP content to the output stream
        return ImmutableList.of(BlobUtils.toZipByteArrayBlob(listFiles, "pieces_jointes.zip"));
    }
}
