package fr.dila.ss.api.service;

import fr.dila.ss.api.exception.SSException;
import fr.dila.st.api.dossier.STDossier;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.zip.ZipOutputStream;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

/**
 * Interface du service d'archive.
 * contient les methodes pour supprimer un dossier, pour exporter le contenu d'un fichier sous forme de zip et pour envoyer un mail contenant le zip du fichier.
 *
 *
 * @author bgamard, arolin
 *
 */
public interface SSArchiveService extends Serializable {
    /**
     * Envoi de mails
     * @param documentManager
     * @param files fichier à ajouter en pièce jointe
     * @param listMail destinataires
     * @param formCopieMail l'utilisateur courant est en copie
     * @param formObjetMail objet du mail
     * @param formTexteMail contenu du mail
     * @param dossierDoc dossier auquel on va rattacher le log
     */
    void prepareAndSendArchiveMail(
        CoreSession documentManager,
        List<DocumentModel> files,
        List<String> listMail,
        Boolean formCopieMail,
        String formObjetMail,
        String formTexteMail,
        List<DocumentModel> dossiersDoc
    )
        throws SSException;

    /**
     * Suppression d'un dossier du système
     * @param session
     * @param doc dossier à supprimer
     * @throws Exception
     */
    void supprimerDossier(CoreSession session, DocumentModel doc);

    /**
     * Suppression d'un dossier du système avec le nom utilisateur
     * @param session
     * @param doc dossier à supprimer
     * @throws Exception
     */
    void supprimerDossier(CoreSession session, DocumentModel doc, NuxeoPrincipal principal);

    /**
     * Ecriture des documents d'un dossier sous forme de fichiers zip compressés
     * @param files documents à ajouter au flux
     * @param outputStream flux de sortie
     * @throws IOException
     */
    void writeZipStream(CoreSession session, List<DocumentModel> files, ZipOutputStream outputStream)
        throws IOException;

    /**
     * Ecriture des documents d'un dossier sous forme de fichiers zip compressés
     * @param files documents à ajouter au flux
     * @param outputStream flux de sortie
     */
    void writeZipStream(
        List<DocumentModel> files,
        OutputStream outputStream,
        DocumentModel dossierDoc,
        CoreSession session
    );

    void writeZipStream(
        List<DocumentModel> files,
        OutputStream outputStream,
        List<DocumentModel> dossiers,
        CoreSession documentManager
    );

    List<DocumentModel> getListDocsToSend(CoreSession session, STDossier sTdossier);
}
