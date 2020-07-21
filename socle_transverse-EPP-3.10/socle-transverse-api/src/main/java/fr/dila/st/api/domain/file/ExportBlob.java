package fr.dila.st.api.domain.file;

import org.nuxeo.ecm.core.api.Blob;

/**
 * Contient un blob et un chemin associé
 * 
 * @author Fabio Esposito
 * 
 */
public class ExportBlob {

	private Blob	blob;

	private String	path;

	public ExportBlob(String path, Blob blob) {
		super();
		this.blob = blob;
		this.path = path;
	}

	/**
	 * @return the blob
	 */
	public Blob getBlob() {
		return blob;
	}

	/**
	 * @param blob
	 *            the blob to set
	 */
	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
