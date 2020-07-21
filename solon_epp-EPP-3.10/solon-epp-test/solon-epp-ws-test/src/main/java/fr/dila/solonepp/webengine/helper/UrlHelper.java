package fr.dila.solonepp.webengine.helper;

import fr.dila.solonepp.webengine.constant.URLConstant;



public class UrlHelper extends fr.sword.naiad.commons.webtest.helper.UrlHelper {

	private static UrlHelper instance = new UrlHelper();
	
	private UrlHelper(){
		super(URLConstant.DEFAULT_APP_URL);
	}
	
	public static UrlHelper getInstance(){
		return instance;
	}
	
	public static String getEndPoint(){
		return getInstance().getAppUrl();
	}
	
}
