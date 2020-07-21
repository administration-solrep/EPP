package fr.dila.solonepp.web.action.admin.profilutilisateur;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.solonepp.api.administration.ProfilUtilisateur;
import fr.dila.solonepp.api.service.ProfilUtilisateurService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.context.NavigationContextBean;


@Name("profilUtilisateurAdministrationActions")
@Scope(CONVERSATION)
public class ProfilUtilisateurAdministrationActionsBean implements Serializable {

    private static final long serialVersionUID = 8000877213568214374L;
    
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ProfilUtilisateurAdministrationActionsBean.class);

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    protected String errorName;

    protected Boolean noPageError;

    @In(create = true, required = false)
    protected NuxeoPrincipal currentUser;
    
    @In(create = true, required = false)
    protected transient UserManager userManager;
    
    protected DocumentModel selectedUser;
    protected boolean notification;
    
    /**
     * ProfilUtilisateur contenant les informations sur le profil utilisateur
     */
    protected ProfilUtilisateur profilUtilisateur;
    
    /**
     * Vrai si la popup de gestion du profil utilisateur doit être affichée
     */
    protected Boolean displayAdministrationProfil;

    /**
     * Vrai si la popup de modification du mot de passe doit être affichée
     */
    protected Boolean displayResetPassword;
    
    public void resetProfilUtilisateur() {
        displayResetPassword=null;
        displayAdministrationProfil=null;
    }


    
    /**
     * Enregistrement du nouveau profil utilisateur.
     * 
     * @throws ClientException
     */
    public void valider() throws ClientException {
      userManager.updateUser(selectedUser);
      //enregister notification par mail
      profilUtilisateur.setDesactivedNotificationMail(notification);
      documentManager.saveDocument(profilUtilisateur.getDocument());
    }

    /**
     * Annulation des modifcations effectuée sur le profil utilisateur.
     * 
     * @throws ClientException
     */
    public void annuler() throws ClientException {
        resetProfilUtilisateur();
    }

    /**
     * Signale à l'application que l'on doit afficher la popup du profil utilisateur.
     */
    public void displayAdministrationProfil() throws ClientException {
        selectedUser = userManager.getUserModel(currentUser.getName());
        //chercher notification status:
        profilUtilisateur = getProfilUtilisateur();
        if (profilUtilisateur == null) {
        	LOGGER.warn(documentManager, STLogEnumImpl.FAIL_GET_PROFIL_UTILISATEUR_FONC);
        } else {
        	notification = profilUtilisateur.isDesactivedNotificationMail();
        	setDisplayAdministrationProfil(true);
        }
    }

    /**
     * Signale à l'application que l'on doit afficher la popup de reset du mot de passe.
     */
    public void displayResetPassword() {
        setDisplayResetPassword(true);
    }
    
    
    public ProfilUtilisateur getProfilUtilisateur() throws ClientException {
    	if (documentManager == null) {
    		LOGGER.warn(documentManager, STLogEnumImpl.FAIL_GET_SESSION_FONC);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
    	}
        if (profilUtilisateur == null) {
            ProfilUtilisateurService profilUtilisateurService = SolonEppServiceLocator.getProfilUtilisateurService();
            profilUtilisateur = profilUtilisateurService.getOrCreateCurrentUserProfil(documentManager).getAdapter(ProfilUtilisateur.class);
        }
        return profilUtilisateur;
    }

    public void setProfilUtilisateur(ProfilUtilisateur profilUtilisateur) {
        this.profilUtilisateur = profilUtilisateur;
    }
    
    // Getter & setter
    
    public Boolean isDisplayAdministrationProfil() {
        return displayAdministrationProfil;
    }
    
    public void setDisplayAdministrationProfil(Boolean displayAdministrationProfil) {
        this.displayAdministrationProfil = displayAdministrationProfil;
    }

    public Boolean isDisplayResetPassword() {
        return displayResetPassword;
    }

    public void setDisplayResetPassword(Boolean displayResetPassword) {
        this.displayResetPassword = displayResetPassword;
    }



    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public Boolean getNoPageError() {
        return noPageError;
    }

    public void setNoPageError(Boolean noPageError) {
        this.noPageError = noPageError;
    }

    public DocumentModel getSelectedUser() {
      return selectedUser;
    }

    public void setSelectedUser(DocumentModel selectedUser) {
      this.selectedUser = selectedUser;
    }

    public Boolean getNotification() {
      return notification;
    }

    public void setNotification(Boolean notification) {
      this.notification = notification;
    }

}
