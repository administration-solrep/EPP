package fr.dila.solonepp.core.assembler.ws;

import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.service.PieceJointeFichierService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.BlobUtils;
import fr.dila.st.core.util.MD5Util;
import fr.dila.st.core.util.SHA512Util;
import fr.dila.st.core.util.ZipUtil;
import fr.sword.xsd.solon.epp.CompressionFichier;
import fr.sword.xsd.solon.epp.ContenuFichier;
import fr.sword.xsd.solon.epp.PieceJointe;
import fr.sword.xsd.solon.epp.PieceJointeType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

/**
 * Assembleur des données des objets pièces jointes métier <-> Web service.
 *
 * @author sly
 * @author jtremeaux
 */
public class PieceJointeAssembler {

    /**
     * Assemblage d'une pièce jointe document Nuxeo -> WS.
     *
     * @param pieceJointeDoc
     *            Document pièce jointe à assembler
     * @return Pièce jointe assemblée
     * @throws IOException
     */
    public static PieceJointe toPieceJointeXsd(DocumentModel pieceJointeDoc, boolean addPjContent) {
        fr.dila.solonepp.api.domain.evenement.PieceJointe pieceJointeAdapter = pieceJointeDoc.getAdapter(
            fr.dila.solonepp.api.domain.evenement.PieceJointe.class
        );
        PieceJointe pieceJointe = new PieceJointe();
        pieceJointe.setIdInterneEpp(pieceJointeAdapter.getTitle());
        pieceJointe.setLibelle(pieceJointeAdapter.getNom());
        pieceJointe.setUrl(pieceJointeAdapter.getUrl());
        pieceJointe.setType(PieceJointeType.valueOf(pieceJointeAdapter.getTypePieceJointe()));

        if (pieceJointeAdapter.getModifiedMetaList() != null && !pieceJointeAdapter.getModifiedMetaList().isEmpty()) {
            pieceJointe.getMetadonneeModifiee().addAll(pieceJointeAdapter.getModifiedMetaList());
        }

        if (pieceJointeAdapter.getModifiedFileList() != null && !pieceJointeAdapter.getModifiedMetaList().isEmpty()) {
            pieceJointe.getFichierModifie().addAll(pieceJointeAdapter.getModifiedFileList());
        }

        List<DocumentModel> pieceJointeFichierDocList = pieceJointeAdapter.getPieceJointeFichierDocList();
        if (pieceJointeFichierDocList != null) {
            List<ContenuFichier> contenuFichierList = pieceJointe.getFichier();
            for (DocumentModel pieceJointeFichierDoc : pieceJointeFichierDocList) {
                PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
                ContenuFichier contenuFichier = new ContenuFichier();
                contenuFichier.setNomFichier(pieceJointeFichier.getSafeFilename());
                contenuFichier.setSha512(pieceJointeFichier.getDigestSha512());
                contenuFichier.setMimeType(pieceJointeFichier.getMimeType());
                contenuFichier.setCompression(CompressionFichier.AUCUNE);
                if (addPjContent) {
                    try {
                        contenuFichier.setContenu(pieceJointeFichier.getContent().getByteArray());
                    } catch (IOException e) {
                        throw new NuxeoException(
                            "Impossible de lire le contenu de la pièce jointe : " + pieceJointeFichierDoc.getId(),
                            e
                        );
                    }

                    if (pieceJointeFichier.getContent() != null) {
                        contenuFichier.setADuContenu(true);
                    }
                }

                contenuFichierList.add(contenuFichier);
            }
        }

        return pieceJointe;
    }

    /**
     * Assemblage d'une pièce jointe document
     *
     * @param session
     *            Session
     * @param pieceJointe
     *            Pièce jointe du WS à assembler
     * @return Document pièce jointe assemblé
     */
    public static DocumentModel toPieceJointeDoc(CoreSession session, PieceJointe pieceJointe) {
        if (pieceJointe.getType() == null) {
            return null;
        }
        PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        DocumentModel pieceJointeDoc = pieceJointeService.createBarePieceJointe(session);
        fr.dila.solonepp.api.domain.evenement.PieceJointe adapted = pieceJointeDoc.getAdapter(
            fr.dila.solonepp.api.domain.evenement.PieceJointe.class
        );
        adapted.setNom(pieceJointe.getLibelle());
        adapted.setUrl(pieceJointe.getUrl());
        final PieceJointeFichierService pieceJointeFichierService = SolonEppServiceLocator.getPieceJointeFichierService();
        List<DocumentModel> pieceJointeFichiers = new ArrayList<DocumentModel>();
        List<ContenuFichier> contenusFichiers = pieceJointe.getFichier();

        for (ContenuFichier contenuFichier : contenusFichiers) {
            DocumentModel pieceJointeFichierDoc = pieceJointeFichierService.createBarePieceJointeFichier(session);
            PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);

            // Renseigne le nom du fichier
            String fileName = contenuFichier.getNomFichier();
            if (fileName == null) {
                throw new NuxeoException("Le nom de la piece jointe est obligatoire.");
            }

            Blob blob = null;
            String digestSha512 = null;
            if (contenuFichier.isADuContenu() != null && contenuFichier.isADuContenu()) {
                // Renseigne le contenu du fichier
                byte[] contenu = contenuFichier.getContenu();
                if (contenu == null) {
                    throw new NuxeoException("Contenu du fichier  '" + fileName + "' de pièce jointe vide.");
                }

                if (StringUtils.isBlank(contenuFichier.getSha512())) {
                    throw new NuxeoException("Le digest SHA512 du fichier '" + fileName + "' est obligatoire.");
                }
                // Dézippe à la volée le contenu compressé
                ByteArrayInputStream is = new ByteArrayInputStream(contenu);
                if (CompressionFichier.ZIP.equals(contenuFichier.getCompression())) {
                    ByteArrayOutputStream os = ZipUtil.unzipSingleFile(is);
                    contenu = os.toByteArray();
                    is = new ByteArrayInputStream(contenu);
                }
                // Vérification du digest SHA512 (si présent)
                digestSha512 = SHA512Util.getSHA512Hash(contenu);
                if (contenuFichier.getSha512() != null && !digestSha512.equals(contenuFichier.getSha512())) {
                    throw new NuxeoException(
                        "Le digest SHA512 du fichier '" + fileName + "' est invalide. Digest attendu: " + digestSha512
                    );
                }

                blob = BlobUtils.createSerializableBlob(is, fileName, null);
                blob.setDigest(MD5Util.getMD5Hash(contenu));
                pieceJointeFichier.setDigestSha512(digestSha512);
            } else {
                // Si le contenu est non spécifié, renseigne uniquement le MD5 pour identifier le fichier existant
                blob = new StringBlob(contenuFichier.getSha512());
                pieceJointeFichier.setDigestSha512(contenuFichier.getSha512());
            }
            pieceJointeFichier.setContent(blob);
            pieceJointeFichier.setFilename(fileName);
            if (pieceJointe.getType() == null) {
                throw new NuxeoException("Le type du fichier est obligatoire.");
            }
            pieceJointeFichier.setTitle(pieceJointe.getType().value());
            pieceJointeFichiers.add(pieceJointeFichierDoc);
        }
        adapted.setPieceJointeFichierDocList(pieceJointeFichiers);
        adapted.setTitle(pieceJointe.getIdInterneEpp());
        adapted.setTypePieceJointe(pieceJointe.getType().value());

        return pieceJointeDoc;
    }

    /**
     * Assemblage d'une pièce jointe document Nuxeo -> WS avec le fichier correspondant
     *
     * @param pieceJointeDoc
     *            Document pièce jointe à assembler
     * @return Pièce jointe assemblée
     * @throws NuxeoException
     * @throws IOException
     */
    public static PieceJointe toPieceJointeXsd(CoreSession session, DocumentModel pieceJointeDoc, String filename)
        throws IOException {
        fr.dila.solonepp.api.domain.evenement.PieceJointe pieceJointeAdapter = pieceJointeDoc.getAdapter(
            fr.dila.solonepp.api.domain.evenement.PieceJointe.class
        );
        PieceJointe pieceJointe = new PieceJointe();
        pieceJointe.setIdInterneEpp(pieceJointeAdapter.getTitle());
        pieceJointe.setLibelle(pieceJointeAdapter.getNom());
        pieceJointe.setUrl(pieceJointeAdapter.getUrl());
        pieceJointe.setType(PieceJointeType.valueOf(pieceJointeAdapter.getTypePieceJointe()));

        // List<DocumentModel> pieceJointeFichierDocList = pieceJointeAdapter.getPieceJointeFichierDocList();

        final PieceJointeFichierService pjfService = SolonEppServiceLocator.getPieceJointeFichierService();

        List<DocumentModel> pjfList = pjfService.findAllPieceJointeFichier(session, pieceJointeDoc);

        if (pjfList != null) {
            List<ContenuFichier> contenuFichierList = pieceJointe.getFichier();
            for (DocumentModel pieceJointeFichierDoc : pjfList) {
                PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);

                if (pieceJointeFichier.getSafeFilename().equals(filename)) {
                    ContenuFichier contenuFichier = new ContenuFichier();
                    contenuFichier.setNomFichier(pieceJointeFichier.getSafeFilename());
                    contenuFichier.setSha512(pieceJointeFichier.getDigestSha512());
                    contenuFichier.setMimeType(pieceJointeFichier.getMimeType());
                    contenuFichier.setCompression(CompressionFichier.AUCUNE);
                    if (pieceJointeFichier.getContent() != null) {
                        contenuFichier.setContenu(pieceJointeFichier.getContent().getByteArray());
                        contenuFichier.setADuContenu(true);
                    }
                    contenuFichierList.add(contenuFichier);

                    break;
                }
            }
        }

        return pieceJointe;
    }
}
