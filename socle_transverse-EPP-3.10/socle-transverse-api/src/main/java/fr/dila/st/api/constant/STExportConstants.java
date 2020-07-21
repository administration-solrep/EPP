package fr.dila.st.api.constant;

/**
 * Constantes liées aux document d'Export
 * 
 * 
 */
public final class STExportConstants {

	/* **********************************************************************
	 * Export Document ***********************************************************************
	 */
	public static final String	EXPORT_DOC_SCHEMA			= "export_document";
	public static final String	EXPORT_DOC_TYPE				= "ExportDocument";
	public static final String	EXPORT_DOC_OWNER_PROPERTY	= "owner";
	public static final String	EXPORT_DOC_DATE_PROPERTY	= "dateRequest";
	public static final String	EXPORT_DOC_CONTENT_PROPERTY	= "content";

	/* **********************************************************************
	 * Folder Export Document ***********************************************************************
	 */
	public static final String	EXPORT_DOC_FOLDER_ROOT_TYPE	= "ExportFolderRoot";

	/* **********************************************************************
	 * Export Service ***********************************************************************
	 */
	public static final String	EXPORT_ENCODING				= "UTF-8";
	public static final String	EXPORT_ZIP_EXTENSION		= ".zip";

	/**
	 * Default constructor
	 */
	private STExportConstants() {
		// do nothing
	}
}
