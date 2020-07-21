package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.service.CorbeilleService;
import fr.dila.solonepp.api.service.CorbeilleTypeService;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementDistributionService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.InformationsParlementairesService;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.api.service.PieceJointeFichierService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.ProfilUtilisateurService;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.api.service.VersionActionService;
import fr.dila.solonepp.api.service.VersionCreationService;
import fr.dila.solonepp.api.service.VersionNumeroService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.st.core.util.ServiceUtil;
/**
 * ServiceLocator de l'application SOLON EPP : permet de retourner les services de l'application.
 * 
 * @author jtremeaux
 */
public class SolonEppServiceLocator {

    /**
     * Retourne le service Corbeille.
     * 
     * @return Service Corbeille
     */
    public static CorbeilleService getCorbeilleService() {
        CorbeilleService service = ServiceUtil.getService(CorbeilleService.class);

        return service;
    }

    /**
     * Retourne le service CorbeilleType.
     * 
     * @return Service CorbeilleType
     */
    public static CorbeilleTypeService getCorbeilleTypeService() {
        CorbeilleTypeService service = ServiceUtil.getService(CorbeilleTypeService.class);

        return service;
    }

    /**
     * Retourne le service Dossier.
     * 
     * @return Service Dossier
     */
    public static DossierService getDossierService() {
        DossierService service = ServiceUtil.getService(DossierService.class);

        return service;
    }

    /**
     * Retourne le service Evenement.
     * 
     * @return Service Evenement
     */
    public static EvenementService getEvenementService() {
        EvenementService service = ServiceUtil.getService(EvenementService.class);

        return service;
    }

    /**
     * Retourne le service EvenementDistribution.
     * 
     * @return Service EvenementDistribution
     */
    public static EvenementDistributionService getEvenementDistributionService() {
        EvenementDistributionService service = ServiceUtil.getService(EvenementDistributionService.class);

        return service;
    }

    /**
     * Retourne le service EvenementTypeService.
     * 
     * @return Service EvenementTypeService
     */
    public static EvenementTypeService getEvenementTypeService() {
        EvenementTypeService service = ServiceUtil.getService(EvenementTypeService.class);

        return service;
    }

    /**
     * Retourne le service de gestion des jetons.
     * 
     * @return Service de gestion des jetons
     */
    public static JetonService getJetonService() {
        return ServiceUtil.getService(JetonService.class);
    }
    
    /**
     * Retourne le service MessageService.
     * 
     * @return Service MessageService
     */
    public static MessageService getMessageService() {
        MessageService service = ServiceUtil.getService(MessageService.class);

        return service;
    }

    /**
     * Retourne le service MailboxInstitution.
     * 
     * @return Service MailboxInstitution
     */
    public static MailboxInstitutionService getMailboxInstitutionService() {
        MailboxInstitutionService service = ServiceUtil.getService(MailboxInstitutionService.class);

        return service;
    }

    /**
     * Retourne le service Organigramme.
     * 
     * @return Service Organigramme
     */
    public static OrganigrammeService getOrganigrammeService() {
        OrganigrammeService service = ServiceUtil.getService(OrganigrammeService.class);

        return service;
    }

    /**
     * Retourne le service PieceJointe.
     * 
     * @return Service PieceJointe
     */
    public static PieceJointeService getPieceJointeService() {
        PieceJointeService service = ServiceUtil.getService(PieceJointeService.class);

        return service;
    }

    /**
     * Retourne le service PieceJointeFichier.
     * 
     * @return Service PieceJointeFichier
     */
    public static PieceJointeFichierService getPieceJointeFichierService() {
        PieceJointeFichierService service = ServiceUtil.getService(PieceJointeFichierService.class);

        return service;
    }

    /**
     * Retourne le service VersionNumero.
     * 
     * @return Service VersionNumero
     */
    public static VersionNumeroService getVersionNumeroService() {
        VersionNumeroService service = ServiceUtil.getService(VersionNumeroService.class);

        return service;
    }

    /**
     * Retourne le service VersionActionService.
     * 
     * @return Service VersionActionService
     */
    public static VersionActionService getVersionActionService() {
        VersionActionService service = ServiceUtil.getService(VersionActionService.class);

        return service;
    }

    /**
     * Retourne le service VersionCreationService.
     * 
     * @return Service VersionCreationService
     */
    public static VersionCreationService getVersionCreationService() {
        VersionCreationService service = ServiceUtil.getService(VersionCreationService.class);

        return service;
    }

    /**
     * Retourne le service Version.
     * 
     * @return Service Version
     */
    public static VersionService getVersionService() {
        VersionService service = ServiceUtil.getService(VersionService.class);

        return service;
    }
    
    /**
     * Retourne le service Table de référence.
     * 
     * @return Service Table de référence
     */
    public static TableReferenceService getTableReferenceService() {
        TableReferenceService service = ServiceUtil.getService(TableReferenceService.class);

        return service;
    }  
    
    /**
     * Retourne le service {@link MetaDonneesService}.
     * 
     * @return {@link MetaDonneesService}
     */
    public static MetaDonneesService getMetaDonneesService() {
        return ServiceUtil.getService(MetaDonneesService.class);
    }  
    
    /**
     * Retourne le service de gestion du profil utilisateur
     * 
     * @return {@link ProfilUtilisateurService}
     */
    public static ProfilUtilisateurService getProfilUtilisateurService() {
        ProfilUtilisateurService service = ServiceUtil.getService(ProfilUtilisateurService.class);
        return service;
    }
    
    public static SolonEppVocabularyService getSolonEppVocabularyService() {
    	return ServiceUtil.getService(SolonEppVocabularyService.class);
    }
    
    /**
     * Retourne le service informationsparlementaires.
     * 
     * @return Service Corbeille
     */
    public static InformationsParlementairesService getInfosParlementaireservice() {
        return ServiceUtil.getService(InformationsParlementairesService.class);
    }
}
