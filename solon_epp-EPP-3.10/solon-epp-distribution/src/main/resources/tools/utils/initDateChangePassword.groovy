import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.solonepp.api.service.ProfilUtilisateurService;
import fr.dila.solonepp.api.administration.ProfilUtilisateur;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * script groovy d'initialisation des dates de changement de mot de passe
 * 
 */

    
print "Début du script groovy d'initialisation des dates de changement de mot de passe";
print "-------------------------------------------------------------------------------";

    final UserManager userManager = STServiceLocator.getUserManager();
    final STMailService mailService = STServiceLocator.getSTMailService();
    final ProfilUtilisateurService profilUtilisateurService = SolonEppServiceLocator.getProfilUtilisateurService();
    
    // On récupère tous les ids par ordre alphabétique
    Map<String, Serializable> filter = new HashMap<String, Serializable>();
    final DocumentModelList userModelList = userManager.searchUsers(filter, null);
    final List<STUser> allUsersList = new ArrayList<STUser>();
    
    for (DocumentModel userDocModel : userModelList) {
    	STUser user = userDocModel.getAdapter(STUser.class);
    	if (user.isActive()) {
    		allUsersList.add(user);
    	}
    }
    
    final List<ProfilUtilisateur> allUsersWithProfilList = new ArrayList<ProfilUtilisateur>();
    for (STUser user : allUsersList) {
    	try {
    		DocumentModel profilModel = profilUtilisateurService.getOrInitUserProfilFromId(Session,user.getUsername());
    		if (profilModel != null) {
    			ProfilUtilisateur profilUtilisateur =  profilModel.getAdapter(ProfilUtilisateur.class);
	    		if (profilUtilisateur != null) {
	        		allUsersWithProfilList.add(profilUtilisateur);
	        	}
    		}
    	} catch (ClientException e) {
    		print "ATTENTION : Impossible de récupérer le profil de " + user.getUsername();
    		print e.getMessage();
    	}
    }
    
    print allUsersWithProfilList.size() + " profils récupérés."
    
	Calendar calendar = Calendar.getInstance();
	calendar.set(2014,Calendar.APRIL,29,0,0,0);
	for (ProfilUtilisateur userWithProfil : allUsersWithProfilList ) {
		userWithProfil.setDernierChangementMotDePasse(calendar);
    	Session.saveDocument(userWithProfil.getDocument());
    	Session.save();
	}
    
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy d'initialisation des dates de changement de mot de passe";
return "Fin du script groovy";
