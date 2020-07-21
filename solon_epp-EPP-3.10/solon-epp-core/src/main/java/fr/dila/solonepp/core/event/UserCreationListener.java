package fr.dila.solonepp.core.event;

import java.util.Random;

import org.nuxeo.runtime.services.event.Event;
import org.nuxeo.runtime.services.event.EventListener;


public class UserCreationListener implements EventListener {

//    private static final Log log = LogFactory.getLog(UserCreationListener.class);
    
    public UserCreationListener()
    {
    }

    @Override
	public void handleEvent(Event event) {

        // Traite uniquement les évènements de création d'utilisateur
/*        if (!(event.getId().equals(UserManagerImpl.USERCREATED_EVENT_ID))) {
            return;
        }

        String userId = (String)event.getData();
        
        UserManagerWithComputedGroups userManager = (UserManagerWithComputedGroups)event.getSource();
        
        DocumentModel doc;
		try {
			doc = userManager.getUserModel(userId);

	        //DocumentEventContext context = (DocumentEventContext) ctx;
	        
	        // Traite uniquement les documents de type User
	        //DocumentModel doc = context.getSourceDocument();
	        String docType = doc.getType();
	        if (!"user".equals(docType)) {
	            return;
	        }
	        
	        String email = (String)doc.getProperty("user", "email");
	        String fname = (String)doc.getProperty("user", "firstName");
	        String lname = (String)doc.getProperty("user", "lastName");
	        String password = getRandomPassword(8);
	        String message = "Bonjour,\n\nVos identifiants pour l'application Réponses sont les suivants :\n\n" +
	        		"- login : %s\n" +
	        		"- mot de passe : %s\n\n";

	        message = String.format(message, userId, password);
	        
            if (!PasswordHelper.isHashed(password)) {
                password = PasswordHelper.hashPassword(password, PasswordHelper.SSHA);
            }
	        
	        doc.setProperty("user", "password", password);
	        
	        userManager.updateUser(doc);
        
	        MailService mailService;

			mailService = Framework.getService(MailService.class);
			
	        Address[] emailAddress = new Address[1];
	        
	        emailAddress[0] =  new InternetAddress(email, fname + " " + lname);

	        mailService.sendMail(message, "Reponses", "mailSession", emailAddress);
			
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
        

    */    
	}

	@Override
	public boolean aboutToHandleEvent(Event event) {
		return false;
	}
	
	public static String getRandomPassword(int length)
	{
		final String charList = "abcdefghijklmonpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
			
        Random rand = new Random();
        for (int i=0; i<length; i++)
        {
        	sb.append(charList.charAt(rand.nextInt(charList.length())));
        }
        return sb.toString();
    }

}
