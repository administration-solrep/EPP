package fr.dila.st.core.domain.file;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.file.File;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier fichier.
 * 
 * @author jtremeaux
 */
public class FileImpl implements File {

	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Modèle de document.
	 */
	protected DocumentModel		document;

	/**
	 * Constructeur de PieceJointeFichierImpl.
	 * 
	 * @param document
	 *            Modèle de document
	 */
	public FileImpl(DocumentModel document) {
		this.document = document;
	}

	@Override
	public String getFilename() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.FILE_SCHEMA,
				STSchemaConstant.FILE_FILENAME_PROPERTY);
	}

	@Override
	public void setFilename(String filename) {
		PropertyUtil.setProperty(document, STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_FILENAME_PROPERTY,
				filename);
	}

	@Override
	public Blob getContent() {
		return PropertyUtil.getBlobProperty(document, STSchemaConstant.FILE_SCHEMA,
					STSchemaConstant.FILE_CONTENT_PROPERTY);		
	}

	@Override
	public void setContent(Blob content) {
		PropertyUtil.setProperty(document, STSchemaConstant.FILE_SCHEMA, STSchemaConstant.FILE_CONTENT_PROPERTY,
				content);
	}


	@Override
	public String getMimeType() {
		Blob blob = getContent();
		if (blob == null) {
			return null;
		} else {
			return blob.getMimeType();
		}
	}

	@Override
	public void setMimeType(String mimeType) {
		Blob blob = getContent();
		if (blob != null) {
			blob.setMimeType(mimeType);
		}
	}
}
