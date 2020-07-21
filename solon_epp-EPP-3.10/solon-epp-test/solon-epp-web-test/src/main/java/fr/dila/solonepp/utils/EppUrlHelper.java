package fr.dila.solonepp.utils;

import fr.sword.naiad.commons.webtest.helper.UrlHelper;

public class EppUrlHelper extends UrlHelper {
	// public static final String ACTUAL_APP_URL = "http://idlv-solrep-epp-qa.lyon-dev2.local:8080/";
	public static final String			ACTUAL_APP_URL	= "http://localhost:8080/";
	private static final String			EPP_NAME		= "solon-epp";
	private static final String			AUTOMATION_SITE	= "/site/automation";

	private static volatile UrlHelper	instance;

	public static UrlHelper getInstance() {
		if (instance == null) {
			synchronized (UrlHelper.class) {
				UrlHelper helper = new EppUrlHelper();
				instance = helper;
			}
		}
		return instance;
	}

	@Override
	public String getAppUrl() {
		return ACTUAL_APP_URL;
	}

	public String getEppUrl() {
		String appurl = getAppUrl();
		if (!appurl.endsWith("/")) {
			appurl += "/";
		}
		appurl += EPP_NAME;
		return appurl;
	}

	public String getAutomationUrl() {
		String repurl = getEppUrl();
		return repurl + AUTOMATION_SITE;
	}

	private EppUrlHelper() {
		super(ACTUAL_APP_URL);
	}
}
