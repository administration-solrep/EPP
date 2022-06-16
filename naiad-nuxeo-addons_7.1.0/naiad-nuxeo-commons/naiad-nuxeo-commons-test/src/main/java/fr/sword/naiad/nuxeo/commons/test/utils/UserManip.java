package fr.sword.naiad.nuxeo.commons.test.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.schema.UserPropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

/**
 * Helper to manipulate user in test :
 * Creation of user with some groups, check right on document for user
 * @author SPL
 *
 */
public final class UserManip {

	public static final List<String> READ = Arrays.asList(SecurityConstants.READ);
	public static final List<String> WRITE = Arrays.asList(SecurityConstants.WRITE);
	public static final List<String> READWRITE = Arrays.asList(SecurityConstants.READ, SecurityConstants.WRITE);
	
	/**
	 * utility class
	 */
	private UserManip(){
		// do nothing
	}
	
	/**
	 * Create a user for right test with some group
	 * @param userId id of the user to create
	 * @param groups groups of the user 
	 * @return the created user
	 * @throws NuxeoException
	 */
	public static DocumentModel createUser(String userId, List<String> groups) throws NuxeoException{
		final UserManager userManager = ServiceUtil.getService(UserManager.class);
		
		DocumentModel userModel = userManager.getBareUserModel();
		UserPropertyUtil.setUserName(userModel, userId);
		
		if(groups != null && !groups.isEmpty()){
			UserPropertyUtil.setGroups(userModel, groups);
		}
		return userManager.createUser(userModel);
	}
	
	/**
	 * Change the groups of an existing user
	 * @param userId id of the user
	 * @param groups the new group for the user
	 * @throws NuxeoException
	 */
	public static void setUserGroup(String userId, List<String> groups) throws NuxeoException{		
		final UserManager userManager = ServiceUtil.getService(UserManager.class);
		
		DocumentModel userModel = userManager.getUserModel(userId);
		List<String> grps = groups; 		
		if(grps == null){
			grps = Collections.emptyList();
		}
		
		PropertyUtil.setProperty(userModel, CommonSchemaConstant.SCHEMA_USER, "groups", grps);
		userManager.updateUser(userModel);
	}
	
	/**
	 * Check the rights for a user on one document
	 * @param session
	 * @param userId id of the user
	 * @param docRef reference of the document
	 * @param hasRights rights the user should have
	 * @param hasNotRights rights the user should not have
	 * @throws NuxeoException
	 */
	public static void checkRights(CoreSession session, String userId, DocumentRef docRef, Collection<String> hasRights,
			Collection<String> hasNotRights) throws NuxeoException {
		final UserManager userManager = ServiceUtil.getRequiredService(UserManager.class);
		NuxeoPrincipal principal = userManager.getPrincipal(userId);
		Assert.assertNotNull(principal);
		if(hasRights != null){
			for(String right : hasRights){
				checkRight(session, principal, docRef, right, true);
			}
		}
		
		if(hasNotRights != null){
			for(String right : hasNotRights){
				checkRight(session, principal, docRef, right, false);
			}
		}
		
	}
	
	public static void checkRight(CoreSession session, NuxeoPrincipal principal, DocumentRef docRef, String right, boolean has) throws NuxeoException {
		String checkStr;
		if(has){
			checkStr = "check has right ";
		} else {
			checkStr = "check has not right ";
		}
		Assert.assertEquals(checkStr + right, has, session.hasPermission(principal, docRef, right));
	}
	
	
	/**
	 * Check if some group don't exist
	 * @param groupNames group id to check
	 * @throws NuxeoException
	 */
	public static void checkGroupNotExist(String... groupNames) throws NuxeoException{	
		final UserManager userManager = getUserManager();
		for(String groupName : groupNames){
			DocumentModel group = userManager.getGroupModel(groupName);
			Assert.assertNull(group);
		}
	}

	/**
	 * Check if some group exist
	 * @param groupNames group id to check
	 * @throws NuxeoException
	 */
	public static void checkGroupExist(String... groupNames) throws NuxeoException{
		final UserManager userManager = getUserManager();
		for(String groupName : groupNames){
			DocumentModel group = userManager.getGroupModel(groupName);
			Assert.assertNotNull(group);
		}
	}

	
	/**
	 * Retrieve the service UserManager
	 * @return
	 * @throws NuxeoException
	 */
	public static UserManager getUserManager() throws NuxeoException{
		UserManager userManager = ServiceUtil.getService(UserManager.class);
		Assert.assertNotNull(userManager);
		return userManager;
	}
	
}
