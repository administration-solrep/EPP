package fr.dila.solonepp.web.tree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.faces.event.ActionEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;
import org.nuxeo.ecm.webapp.filemanager.UploadItemHolder;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import fr.dila.solonepp.api.descriptor.evenementtype.MimetypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.core.validator.PieceJointeFichierValidator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.MD5Util;
import fr.dila.st.core.util.SHA512Util;

/**
 * Bean seam de gestion des fichiers des pieces jointes
 * 
 * @author asatre
 */
@Synchronized(timeout = 10000)
@Name("fileTreeManagerActions")
@Scope(ScopeType.SESSION)
public class FileTreeManagerActionsBean implements Serializable {

	private static final long		serialVersionUID	= -3486558978767123945L;

	/**
	 * Logger surcouche socle de log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(FileTreeManagerActionsBean.class);

	@In(create = true, required = false)
	protected FacesMessages			facesMessages;

	@In(create = true)
	protected ResourcesAccessor		resourcesAccessor;

	@In(create = true, required = false)
	protected UploadItemHolder		fileUploadHolder;

	@In(create = true, required = false)
	protected transient CoreSession	documentManager;

	private PieceJointe				currentPieceJointe;

	private String					errorName;

	private String					fileName;

	/**
	 * Nombre de fichier téléchargeable par défaut
	 */
	private int						uploadsAvailable	= Integer.MAX_VALUE;

	private DocumentModel			pieceJointeFichierDoc;

	private Boolean					visible				= false;
	
	/**
	 * Mime types autorisés pour ce manager.
	 */
	private Map<String, MimetypeDescriptor> mimeTypes;

	@Remove
	@Destroy
	public void destroy() {
		LOGGER.debug(documentManager, STLogEnumImpl.REMOVE_WORKSPACE_ACTIONS_BEAN_TEC);
	}

	/**
	 * Reset the temp properties
	 * 
	 */
	public void resetProperties() {
		uploadsAvailable = Integer.MAX_VALUE;
		setUploadedFiles(null);
		if (fileUploadHolder != null) {
			fileUploadHolder.reset();
		}
		errorName = null;

		setVisible(false);
	}

	/**
	 * Ajout ou Modification d'un fichier dans un répertoire.
	 * 
	 * @throws IOException
	 * 
	 */
	public void ajoutDocument() throws ClientException, IOException {

		LOGGER.info(documentManager, STLogEnumImpl.CREATE_FILE_FONC);
		if (currentPieceJointe == null) {
			errorName = "Aucune pièce jointe séléctionnée";
		} else if (getUploadedFiles() == null || getUploadedFiles().size() < 1) {
			errorName = "Aucun fichier sélectionné";
		} else {
			// récupération des données du fichier courant
			List<UploadItem> uploadItemList = new ArrayList<UploadItem>();
			uploadItemList.addAll(getUploadedFiles());
			
			List<DocumentModel> list = currentPieceJointe.getPieceJointeFichierDocList();
			if (list == null) {
				list = new ArrayList<DocumentModel>();
			}
			
			try {
				for (UploadItem uploadItem : uploadItemList) {
					// récupération du nom du fichier
					String filename = FileUtils.getCleanFileName(uploadItem.getFileName());
					File file = uploadItem.getFile();

					PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();
					try {
						pieceJointeFichierValidator.validatePieceJointeFichierFileName(filename);
					} catch (ClientException e) {
						errorName = e.getMessage();
						return;
					}
					
					// récupération du contenu du fichier
					Blob blob = FileUtils.createSerializableBlob(new FileInputStream(file), filename, null);

					blob.setDigest(MD5Util.getMD5Hash(blob.getByteArray()));
					DocumentModel doc = SolonEppServiceLocator.getPieceJointeFichierService()
							.createBarePieceJointeFichier(documentManager);
					PieceJointeFichier pieceJointeFichier = doc.getAdapter(PieceJointeFichier.class);
					pieceJointeFichier.setContent(blob);
					pieceJointeFichier.setFilename(filename);
					pieceJointeFichier.setDigestSha512(SHA512Util.getSHA512Hash(blob.getByteArray()));
					list.add(doc);

				}
				currentPieceJointe.setPieceJointeFichierDocList(list);

				resetProperties();
				currentPieceJointe = null;

			} finally {
				if (fileUploadHolder != null) {
					fileUploadHolder.reset();
				}
			}
		}
	}

	public void fileUploadListener(UploadEvent event) throws Exception {
		errorName = "";
		if (event == null || event.getUploadItem() == null || event.getUploadItem().getFileName() == null) {
			errorName = "Le fichier est vide";
			return;
		}
		
		if (currentPieceJointe == null) {
			errorName = "Aucune pièce jointe séléctionnée";
			return;
		}
		
		// TODO : TLD : rendre cela plus générique...
		if("INSERTION_JOLD".equals(currentPieceJointe.getTypePieceJointe())) {
			if (fileUploadHolder.getUploadedFiles().size()>=1) {
				// Pas de message d'erreur car des fois 2 events consécutifs sont envoyés et le msg s'affiche systématiquement....
				return;
			}
		}

		PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();
		try {
			String filename = FileUtils.getCleanFileName(event.getUploadItem().getFileName());
			pieceJointeFichierValidator.validatePieceJointeFichierFileName(filename);
		} catch (ClientException e) {
			errorName = e.getMessage();
			return;
		}
		
		try {
			pieceJointeFichierValidator.validatePieceJointeFichierMimeType(event.getUploadItem().getContentType(), getMimeTypes());
		} catch (ClientException e) {
			errorName = e.getMessage();
			return;
		}

		errorName = null;
		// on transmet le fichier dans le bean dédié
		addUploadedFile(event.getUploadItem());
	}

	/**
	 * Getter/setter pour les fichiers.
	 */

	public Collection<UploadItem> getUploadedFiles() {
		if (fileUploadHolder != null) {
			return fileUploadHolder.getUploadedFiles();
		} else {
			return null;
		}
	}

	public void setUploadedFiles(Collection<UploadItem> uploadedFiles) {
		if (fileUploadHolder != null) {
			fileUploadHolder.setUploadedFiles(uploadedFiles);
		}
	}
	
	private void addUploadedFile(UploadItem uploadedItem) {
		if (fileUploadHolder != null) {
			Collection<UploadItem> alreadyUploadedItems = new ArrayList<UploadItem>(getUploadedFiles());
			for (UploadItem item : alreadyUploadedItems) {
				if (item.getFileName().equals(uploadedItem.getFileName()) && item.getFileSize() == uploadedItem.getFileSize()) {
					return;
				}
			}
			alreadyUploadedItems.add(uploadedItem);
			fileUploadHolder.setUploadedFiles(alreadyUploadedItems);
		}
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public DocumentModel getPieceJointeFichierDoc() {
		return pieceJointeFichierDoc;
	}

	public void setPieceJointeFichierDoc(DocumentModel pieceJointeFichierDoc) {
		this.pieceJointeFichierDoc = pieceJointeFichierDoc;
	}

	public PieceJointe getCurrentPieceJointe() {
		return currentPieceJointe;
	}

	public void setCurrentPieceJointe(PieceJointe currentPieceJointe) {
		this.currentPieceJointe = currentPieceJointe;
		if (fileUploadHolder != null) {
			fileUploadHolder.reset();
		}
		setVisible(true);
		setMimeTypes(null);
	}
	
	public void setCurrentPieceJointe(PieceJointe currentPieceJointe, Map<String, MimetypeDescriptor> mimeTypes, boolean multiPj) {
		setCurrentPieceJointe(currentPieceJointe);
		
		setMimeTypes(mimeTypes);
		if(!multiPj) {
			setUploadsAvailable(1);
		}
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	public String getErrorName() {
		return errorName;
	}

	public String getPieceJointeType() {
		if (currentPieceJointe != null) {
			return currentPieceJointe.getTypePieceJointe();
		}
		return null;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void clearUploadData(ActionEvent even) {
		try {
			// test if a single file was cleared....
			if (fileName != null && !"".equals(fileName)) {

				Iterator<UploadItem> iter = fileUploadHolder.getUploadedFiles().iterator();
				while (iter.hasNext()) {
					UploadItem uploadItem = iter.next();
					if (fileName.equals(uploadItem.getFileName())) {
						fileUploadHolder.getUploadedFiles().remove(uploadItem);
						break;
					}
				}
			}

			else {
				if (fileUploadHolder.getUploadedFiles() != null) {
					fileUploadHolder.getUploadedFiles().clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Map<String, MimetypeDescriptor> getMimeTypes() {
		return mimeTypes;
	}

	public void setMimeTypes(Map<String, MimetypeDescriptor> mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
}
