package fr.dila.st.api.constant;

/**
 * Constantes de cycle de vie des documents du socle transverse.
 * 
 * @author jtremeaux
 */
public final class STLifeCycleConstant {
	public static final String	TO_DRAFT_BY_COPY_TRANSITION	= "toDraftByCopy";

	public static final String	TO_READY_BY_COPY_TRANSITION	= "toReadyByCopy";

	public static final String	TO_DELETE_TRANSITION		= "delete";

	public static final String	DELETED_STATE				= "deleted";

	public static final String	UNDEFINED_STATE				= "undefined";

	public static final String	EXPORTING_STATE				= "exporting";

	public static final String	TO_EXPORTING_TRANSITION		= "toExporting";

	public static final String	TO_DONE_TRANSITION			= "toDone";

	public static final String	DONE_STATE					= "done";

	public static final String	RUNNING_STATE				= "running";

	/**
	 * utility class
	 */
	private STLifeCycleConstant() {
		// do nothing
	}
}
