package fr.dila.solonepp.web.utilisateur;

import static org.jboss.seam.ScopeType.PAGE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * @author JBT
 * @see org.nuxeo.ecm.webapp.security.UserSuggestionActionsBean
 */
@Name("userSuggestionActions")
@SerializedConcurrentAccess
@Scope(PAGE)
@Install(precedence = Install.APPLICATION + 1)
public class UserSuggestionActionsBean  extends  org.nuxeo.ecm.webapp.security.UserSuggestionActionsBean{

	private static final long serialVersionUID = 2774501488951539686L;
    
	public List<DocumentModel> getUserSuggestions(Object input) throws ClientException {
		return super.getUserSuggestions(input);
	}
	
	public Object getSuggestionSuiviBatchNotification(Object input) throws ClientException {
		List<DocumentModel> users = Collections.emptyList();
        if (USER_TYPE.equals(userSuggestionSearchType)
                || StringUtils.isEmpty(userSuggestionSearchType)) {
            users = getUserSuggestions(input);
        }
        final List<STUser> adminUsers = STServiceLocator.getProfileService().getUsersFromBaseFunction(STBaseFunctionConstant.BATCH_READER);
        if (users==null || users.size()==0 || adminUsers==null){
        	return null;
        }
        List<String> result = new ArrayList<String>();
        for (DocumentModel user : users) {
        	STUser STuser = user.getAdapter(STUser.class);
        	if (adminUsers.contains(STuser)) {
        		result.add(STuser.getUsername());
        	}
        }
        return result;
	}

}
