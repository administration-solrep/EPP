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
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public class SolonEppServiceLocator {

    public static CorbeilleService getCorbeilleService() {
        return ServiceUtil.getRequiredService(CorbeilleService.class);
    }

    public static CorbeilleTypeService getCorbeilleTypeService() {
        return ServiceUtil.getRequiredService(CorbeilleTypeService.class);
    }

    public static DossierService getDossierService() {
        return ServiceUtil.getRequiredService(DossierService.class);
    }

    public static EvenementService getEvenementService() {
        return ServiceUtil.getRequiredService(EvenementService.class);
    }

    public static EvenementDistributionService getEvenementDistributionService() {
        return ServiceUtil.getRequiredService(EvenementDistributionService.class);
    }

    public static EvenementTypeService getEvenementTypeService() {
        return ServiceUtil.getRequiredService(EvenementTypeService.class);
    }

    public static JetonService getJetonService() {
        return ServiceUtil.getRequiredService(JetonService.class);
    }

    public static MessageService getMessageService() {
        return ServiceUtil.getRequiredService(MessageService.class);
    }

    public static MailboxInstitutionService getMailboxInstitutionService() {
        return ServiceUtil.getRequiredService(MailboxInstitutionService.class);
    }

    public static OrganigrammeService getOrganigrammeService() {
        return ServiceUtil.getRequiredService(OrganigrammeService.class);
    }

    public static PieceJointeService getPieceJointeService() {
        return ServiceUtil.getRequiredService(PieceJointeService.class);
    }

    public static PieceJointeFichierService getPieceJointeFichierService() {
        return ServiceUtil.getRequiredService(PieceJointeFichierService.class);
    }

    public static VersionNumeroService getVersionNumeroService() {
        return ServiceUtil.getRequiredService(VersionNumeroService.class);
    }

    public static VersionActionService getVersionActionService() {
        return ServiceUtil.getRequiredService(VersionActionService.class);
    }

    public static VersionCreationService getVersionCreationService() {
        return ServiceUtil.getRequiredService(VersionCreationService.class);
    }

    public static VersionService getVersionService() {
        return ServiceUtil.getRequiredService(VersionService.class);
    }

    public static TableReferenceService getTableReferenceService() {
        return ServiceUtil.getRequiredService(TableReferenceService.class);
    }

    public static MetaDonneesService getMetaDonneesService() {
        return ServiceUtil.getRequiredService(MetaDonneesService.class);
    }

    public static ProfilUtilisateurService getProfilUtilisateurService() {
        return ServiceUtil.getRequiredService(ProfilUtilisateurService.class);
    }

    public static SolonEppVocabularyService getSolonEppVocabularyService() {
        return ServiceUtil.getRequiredService(SolonEppVocabularyService.class);
    }

    public static InformationsParlementairesService getInfosParlementaireservice() {
        return ServiceUtil.getRequiredService(InformationsParlementairesService.class);
    }
}
