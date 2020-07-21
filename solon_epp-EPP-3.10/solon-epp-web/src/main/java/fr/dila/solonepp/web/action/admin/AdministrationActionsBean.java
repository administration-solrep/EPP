package fr.dila.solonepp.web.action.admin;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.security.UserManagerActionsBean;

import fr.dila.solonepp.api.constant.SolonEppParametreConstant;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Seam de l'espace d'administration.
 * 
 * @author jtremeaux
 */
@Name("administrationActions")
@Scope(ScopeType.SESSION)
@Install(precedence = FRAMEWORK + 1)
public class AdministrationActionsBean implements Serializable {
    
    private static final String VIEW_EVTLIST = "view_evtlist";

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 956631279709404171L;

    protected static final String LEFT_MENU_ADMIN_ACTION = "LEFT_MENU_ESPACE_ADMIN";
    
    protected static final String MAIN_MENU_ADMIN_ACTION = "espace_administration";

    @In(required = true, create = true)
    protected EppPrincipal eppPrincipal;

    @In(create = true)
    protected transient OrganigrammeTreeBean organigrammeTree;
    
    @In(required = true)
    protected transient NavigationContextBean navigationContext;

    @In(required = false, create = true)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient ActionManager actionManager;
    
    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;

    @In(create = true)
    protected transient NavigationWebActionsBean navigationWebActions;
    
    @In(create = true)
    protected transient UserManagerActionsBean userManagerActions;
    
    /**
     * Initialise le contexte de l'espace d'administration.
     */
    protected void initEspaceAdministration() throws ClientException {
        // Renseigne le menu du haut
        Action mainMenuAction = actionManager.getAction(MAIN_MENU_ADMIN_ACTION);
        navigationWebActions.setCurrentMainMenuAction(mainMenuAction);

        // Renseigne le menu de gauche
        Action leftMenuAction = actionManager.getAction(LEFT_MENU_ADMIN_ACTION);
        navigationWebActions.setCurrentLeftMenuAction(leftMenuAction);

        // Réinitialise le document en cours
        navigationContext.resetCurrentDocument();
    }

    //***********************************************************************
    // Fonctions de navigation
    //***********************************************************************
    /**
     * Navigue vers l'espace d'administration.
     * 
     * @return Écran d'accueil de l'espace d'administration
     * @throws ClientException
     */
    public String navigateToEspaceAdministration() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceAdministration();
        navigationWebActions.setCurrentLeftMenuItemAction(null);
        navigationContext.setCurrentView(STViewConstant.EMPTY_VIEW);
        return STViewConstant.EMPTY_VIEW;
    }
    
    /**
     * Navigue vers l'écran de gestion de l'organigramme.
     * 
     * @return Écran de gestion de l'organigramme
     * @throws ClientException
     */
    public String navigateToOrganigramme() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceAdministration();
        
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_organigramme"));
        organigrammeTree.cleanTree();
        // Renseigne le vue courante
        navigationContext.setCurrentView(STViewConstant.ORGANIGRAMME_VIEW_MANAGE);
        return STViewConstant.ORGANIGRAMME_VIEW_MANAGE;
    }
    
    /**
     * Navigue vers l'écran de gestion des utilisateurs.
     * 
     * @return Écran de gestion de l'organigramme
     * @throws ClientException
     */
    public String navigateToUtilisateur() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceAdministration();

        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_utilisateur"));
        organigrammeTree.cleanTree();
        userManagerActions.clearSearch();
        // Renseigne le vue courante
        navigationContext.setCurrentView(null);
        
        return userManagerActions.viewUsers();
    }
    
    /**
     * Navigue vers l'écran des metadonnees.
     * 
     * @return 
     * @throws ClientException
     */
    public String navigateToViewMetadonnee() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceAdministration();

        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_evtlist"));
        // Renseigne le vue courante
        navigationContext.setCurrentView(VIEW_EVTLIST);
        return VIEW_EVTLIST;
    }    
    
    /**
     * Navigue vers l'écran des logs d'exécution des batchs
     * 
     * @return écran des logs des batchs
     * @throws ClientException
     */
    public String navigateToViewBatchSuivi() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceAdministration();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_batch_viewSuivi"));
        return STViewConstant.BATCH_SUIVI_VIEW;
    }
    
    /**
     * Navigue vers l'écran des planifications d'exécution des batchs
     * 
     * @return écran des planifications des batchs
     * @throws ClientException
     */
    public String navigateToViewBatchSuiviPlanification() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceAdministration();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_batch_viewSuiviPlanification"));
        return STViewConstant.BATCH_SUIVI_PLANIFICATION;
    }
    
    /**
     * Navigue vers l'écran des notifications de suivi des batchs
     * 
     * @return écran des notifications
     * @throws ClientException
     */
    public String navigateToViewBatchSuiviNotification() throws ClientException {
        // Initialise le contexte de l'espace d'administration
        initEspaceAdministration();
        navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_batch_viewSuiviNotification"));
        return STViewConstant.BATCH_SUIVI_NOTIFICATION;
    }
    
    /**
     * Navigue vers l'écran d'administration des correspondances entité/direction pour le WS creerDossier
     * @return
     * @throws ClientException
     */
    public String navigateToAdminCreerDossierEpg() throws ClientException {
    	initEspaceAdministration();
    	navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_creer_dossier_epg"));
    	return navigationContext.navigateToRef(new PathRef(SolonEppParametreConstant.CREER_DOSSIER_PARAM_PATH));
    }
    
    public boolean isCurrentUserInDila() {
    	return eppPrincipal.getInstitutionIdSet().contains(InstitutionsEnum.DILA.name());
    }
}
