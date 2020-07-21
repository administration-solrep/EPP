import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.solonepp.core.util.ExcelUtil;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * script groovy de récupération des utilisateurs dont l'identifiant ne remplit pas les critères
 * 
 */

    
print "Début du script groovy de récupération des utilisateurs dont l'identifiant ne remplit pas les critères";
print "-------------------------------------------------------------------------------";

    final UserManager userManager = STServiceLocator.getUserManager();
    final STMailService mailService = STServiceLocator.getSTMailService();
    
    // On récupère tous les ids
    final List<String> userIds = userManager.getUserIds();
    
    print userIds.size() + " utilisateurs trouvés au total."
    
    List<String> invalidUserIds = new ArrayList<String>();
    
    for (String userId : userIds) {
    	if (userId.length()<8) {
    		invalidUserIds.add(userId);
    	}
    }
    
    List<STUser> invalidUsers = new ArrayList<STUser>();
    for (String invalidUserId : invalidUserIds) {
        DocumentModel userDoc = userManager.getUserModel(invalidUserId);
        STUser invalidUser = userDoc.getAdapter(STUser.class);
        if (invalidUser == null ) {
        	print "L'utilisateur suivant dont l'identifiant est invalide  n'a pu être récupéré : " + invalidUserId;
        } else if (invalidUser.isActive()) {
        	invalidUsers.add(invalidUser);
        }
    }
    
    print invalidUserIds.size() + " utilisateurs ayant un identifiant ne remplissant pas les critères."
    print invalidUsers.size() + " utilisateurs actifs ayant un identifiant ne remplissant pas les critères."
    
    // Récupération des utilisateurs auxquels envoyer les mails
    final List<STUser> adminUsers = STServiceLocator.getProfileService().getUsersFromBaseFunction(STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER);
    print adminUsers.size() + " mails vont être envoyés."
    
    String textMail = "Bonjour, vous trouverez ci-joint la liste des " + invalidUsers.size() + " identifiants ne satisfaisant pas les critères.";
    String object = "[SOLON EPP] Liste des identifiants ne satisfaisant pas les critères";
    String nomFichier = "Resultat_identifiants_invalides.xls";
    DataSource fichierExcelResultat = ExcelUtil.creationListUtilisateurExcel(Session, invalidUsers);
    try {
    	mailService.sendMailWithAttachementToUserList(adminUsers, object, textMail, nomFichier, fichierExcelResultat);
    } catch (Exception e) {
    	print "Erreur d'envoi du mail de signalisation des identifiants ne répondant pas aux critères";
    }
    
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de récupération des utilisateurs dont l'identifiant ne remplit pas les critères";
return "Fin du script groovy";
