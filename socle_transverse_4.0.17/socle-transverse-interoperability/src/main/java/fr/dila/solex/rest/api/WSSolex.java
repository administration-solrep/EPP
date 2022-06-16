package fr.dila.solex.rest.api;

import fr.sword.xsd.solex.ExporterTableEppRequest;
import fr.sword.xsd.solex.ExporterTableEppResponse;
import fr.sword.xsd.solex.InitialiserLegislaturesRequest;
import fr.sword.xsd.solex.InitialiserLegislaturesResponse;
import fr.sword.xsd.solex.NotifierMajTableTribunRequest;
import fr.sword.xsd.solex.NotifierMajTableTribunResponse;
import fr.sword.xsd.solex.SyncObjetsEppRequest;
import fr.sword.xsd.solex.SyncObjetsEppResponse;
import fr.sword.xsd.solon.epp.NotifierEvenementRequest;
import fr.sword.xsd.solon.epp.NotifierEvenementResponse;

public interface WSSolex {

	public static final String	SERVICE_NAME							= "WSsolex";

	public static final String	METHOD_TEST								= "test";

	public static final String	METHOD_NAME_NOTIFIER_EVENEMENT			= "WSnotification/notifierEvenement";

	public static final String	METHOD_NAME_NOTIFIER_MAJ_TABLE_TRIBUN	= "notifierMajTableTribun";

	public static final String	METHOD_NAME_INIT_LEGISLATURES			= "initialiserLegislatures";

	public static final String	METHOD_NAME_EXPORTER_TABLE_EPP			= "exporterTableEpp";

	public static final String	METHOD_NAME_SYNC_OBJETS_EPP				= "syncObjetsEpp";

	public String test() throws Exception;

	public ExporterTableEppResponse exporterTableEpp(ExporterTableEppRequest request) throws Exception;

	public NotifierMajTableTribunResponse notifierMajTableTribun(NotifierMajTableTribunRequest request)
			throws Exception;

	public NotifierEvenementResponse notifierEvenement(NotifierEvenementRequest request) throws Exception;

	public InitialiserLegislaturesResponse initialiserLegislatures(InitialiserLegislaturesRequest request)
			throws Exception;

	public SyncObjetsEppResponse syncObjetsEpp(SyncObjetsEppRequest request) throws Exception;

	boolean isAuthorized(String login, String password);
}
