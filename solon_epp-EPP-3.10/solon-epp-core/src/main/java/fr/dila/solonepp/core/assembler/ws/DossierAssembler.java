package fr.dila.solonepp.core.assembler.ws;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.Commission;
import fr.sword.xsd.solon.epp.Depot;
import fr.sword.xsd.solon.epp.FicheDossier;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.NiveauLecture;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.Organisme;

/**
 * Assembleur des données des objets dossier métier <-> Web service. 
 * 
 * @author jtremeaux
 */
public class DossierAssembler {
    /**
     * Session.
     */
    protected CoreSession session;

    /**
     * Constructeur de DossierAssembler.
     * 
     * @param session Session
     */
    public DossierAssembler(CoreSession session) {
        this.session = session;
    }
    
    /**
     * Assemble les données du dossier document Nuxeo -> WS.
     * 
     * @param session Session
     * @param dossierDoc Document dossier à assembler
     * @return Objet WS dossier assemblé
     * @throws ClientException
     */
    public fr.sword.xsd.solon.epp.Dossier assembleDossierToXsd(CoreSession session, DocumentModel dossierDoc) throws ClientException {
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

        fr.sword.xsd.solon.epp.Dossier dossierWS = new fr.sword.xsd.solon.epp.Dossier();
        FicheDossier ficheDossier = new FicheDossier();
        dossierWS.setFicheDossier(ficheDossier);
        
        ficheDossier.setIdDossier(dossier.getTitle());
        
        if (StringUtils.isNotBlank(dossier.getAuteur())) {
            DocumentModel mandatModel = tableReferenceService.getMandatById(session, dossier.getAuteur());
            ficheDossier.setAuteur(TableReferenceAssembler.toMandatXsd(session, mandatModel));
        }
        
        if (dossier.getCoauteur() != null && !dossier.getCoauteur().isEmpty()) {
            for (String coAuteur : dossier.getCoauteur()) {
                DocumentModel mandatDoc = tableReferenceService.getMandatById(session, coAuteur);
                if (mandatDoc != null) {
                    ficheDossier.getCoAuteur().add(TableReferenceAssembler.toMandatXsd(session, mandatDoc));
                }
            }
        }
        
        if (dossier.isRedepot()) {
            ficheDossier.setRedepot(dossier.isRedepot());
        }
        
        if (StringUtils.isNotBlank(dossier.getCommissionSaisie())) {
            DocumentModel organismeDoc = tableReferenceService.getOrganismeById(session, dossier.getCommissionSaisie());
            if (organismeDoc != null) {
                ficheDossier.setCommissionSaisie(TableReferenceAssembler.toOrganismeXsd(organismeDoc));
            }
        }
        
        if (StringUtils.isNotBlank(dossier.getCommissionSaisieAuFond()) || dossier.getCommissionSaisiePourAvis() != null) {
            assembleCommissionDossierToXsd(dossierDoc);
        }
        
        if (dossier.getCosignataire() != null && !dossier.getCosignataire().isEmpty()) {
            ficheDossier.setCoSignataireCollectif(dossier.getCosignataire());
        }
        
        if (dossier.getDate() != null) {
            ficheDossier.setDate(DateUtil.calendarToXMLGregorianCalendar(dossier.getDate()));
        }

        if (dossier.getDateAdoption() != null) {
            ficheDossier.setDateAdoption(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateAdoption()));
        }
        
        if (dossier.getDateCmp() != null) {
            ficheDossier.setDateCMP(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateCmp()));
        }
        
        if (dossier.getDateDepotRapport() != null || !StringUtils.isBlank(dossier.getNumeroDepotRapport())) {
            assembleDepotRapportDossierToXsd(dossierDoc);
        }
        
        if (dossier.getDateDepotTexte() != null || !StringUtils.isBlank(dossier.getNumeroDepotTexte())) {
            ficheDossier.setDepotTexte(assembleDepotTexteDossierToXsd(dossierDoc));
        }
        
        if (dossier.getDateDistributionElectronique() != null) {
            ficheDossier.setDateDistribution(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateDistributionElectronique()));
        }
        
        if (dossier.getDateEngagementProcedure() != null) {
            ficheDossier.setDateEngagementProcedure(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateEngagementProcedure()));
        }
        
        if (dossier.getDatePromulgation() != null) {
            ficheDossier.setDatePromulgation(DateUtil.calendarToXMLGregorianCalendar(dossier.getDatePromulgation()));
        }
        
        if (dossier.getDatePublication() != null) {
            ficheDossier.setDatePublication(DateUtil.calendarToXMLGregorianCalendar(dossier.getDatePublication()));
        }
        
        if (dossier.getDateRefus() != null) {
            ficheDossier.setDateRefus(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateRefus()));
        }
        
        if (dossier.getDateRefusProcedureEngagementAn() != null) {
            ficheDossier.setDateRefusAn(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateRefusProcedureEngagementAn()));
        }
        
        if (dossier.getDateRefusProcedureEngagementSenat() != null) {
            ficheDossier.setDateRefusSenat(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateRefusProcedureEngagementSenat()));
        }
        
        if (dossier.getDateRetrait() != null) {
            ficheDossier.setDateRefus(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateRetrait()));
        }
        
        if (dossier.getHorodatage() != null) {
            ficheDossier.setHorodatage(DateUtil.calendarToXMLGregorianCalendar(dossier.getHorodatage()));
        }
        
        if (StringUtils.isNotBlank(dossier.getIntitule())) {
            ficheDossier.setIntitule(dossier.getIntitule());
        }
        
        if (dossier.getLibelleAnnexe() != null && !dossier.getLibelleAnnexe().isEmpty()) {
            ficheDossier.getLibelleAnnexes().addAll(dossier.getLibelleAnnexe());
        }
        
        if (dossier.getNatureLoi() != null) {
            ficheDossier.setNatureLoi(NatureLoiAssembler.assembleNatureLoiToXsd(dossier.getNatureLoi()));
        }
        
        if (dossier.getNiveauLectureNumero() != null || dossier.getNiveauLecture() != null) {
            assembleNiveauLectureDossierToXsd(dossierDoc);
        }
        
        if (StringUtils.isNotBlank(dossier.getNor())) {
            ficheDossier.setNor(dossier.getNor());
        }
        
        if (dossier.getNumeroJo() != null) {
            ficheDossier.setNumeroJo(dossier.getNumeroJo().intValue());
        }
        
        if (dossier.getNumeroLoi() != null) {
            ficheDossier.setNumeroLoi(dossier.getNumeroLoi().intValue());
        }    
            
        if (StringUtils.isNotBlank(dossier.getNumeroTexteAdopte())) {
            ficheDossier.setNumeroTexteAdopte(dossier.getNumeroTexteAdopte());
        }
        
        if (StringUtils.isNotBlank(dossier.getObjet())) {
            ficheDossier.setObjet(dossier.getObjet());
        }
        
        if (dossier.getPageJo() != null) {
            ficheDossier.setPageJo(dossier.getPageJo().intValue());
        }
         
        if (dossier.getRapporteurList()!= null && !dossier.getRapporteurList().isEmpty()) {
            for (String mandatId : dossier.getRapporteurList()) {
                DocumentModel mandatDoc = tableReferenceService.getMandatById(session, mandatId);
                if (mandatDoc != null) {
                    Mandat mandat = TableReferenceAssembler.toMandatXsd(session, mandatDoc);
                    ficheDossier.getRapporteur().add(mandat);
                }
            }
        }
        
        if (StringUtils.isNotBlank(dossier.getSenat())) {
            ficheDossier.setIdSenat(dossier.getSenat());
        }
        
        if (StringUtils.isNotBlank(dossier.getSortAdoption())) {
            ficheDossier.setSortAdoption(SortAdoptionAssembler.assembleSortAdoptionToXsd(dossier.getSortAdoption()));
        }
        
        if (StringUtils.isNotBlank(dossier.getTitre())) {
            ficheDossier.setTitre(dossier.getTitre());
        }
        
        if (StringUtils.isNotBlank(dossier.getTypeLoi())) {
            ficheDossier.setTypeLoi(TypeLoiAssembler.assembleTypeLoiToXsd(dossier.getTypeLoi()));
        }
        
        if (StringUtils.isNotBlank(dossier.getUrlDossierAn())) {
            ficheDossier.setUrlDossierAn(dossier.getUrlDossierAn());
        }
        
        if (StringUtils.isNotBlank(dossier.getUrlDossierSenat())) {
            ficheDossier.setUrlDossierSenat(dossier.getUrlDossierSenat());
        }
        
        if (StringUtils.isNotBlank(dossier.getEcheance())) {
            ficheDossier.setEcheance(dossier.getEcheance());
        }
        
        if (dossier.getDateActe() != null) {
            ficheDossier.setDateActe(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateActe()));
        }
        
        if (StringUtils.isNotBlank(dossier.getSensAvis())) {
            ficheDossier.setSensAvis(SensAvisAssembler.assembleSensAvisToXsd(dossier.getSensAvis()));
        }
        
        if (dossier.getSuffrageExprime() != null) {
            ficheDossier.setNombreSuffrage(dossier.getSuffrageExprime().intValue());
        }
        
        if (dossier.getBulletinBlanc() != null) {
            ficheDossier.setBulletinBlanc(dossier.getBulletinBlanc().intValue());
        }
        
        if (dossier.getVotePour() != null) {
            ficheDossier.setVotePour(dossier.getVotePour().intValue());
        }
        
        if (dossier.getVoteContre() != null) {
            ficheDossier.setVoteContre(dossier.getVoteContre().intValue());
        }
        
        if (dossier.getAbstention() != null) {
            ficheDossier.setAbstention(dossier.getAbstention().intValue());
        }
        
        if (dossier.getCommissions() != null) {
            for (final String organismeId : dossier.getCommissions()) {
                final Organisme organisme = assembleCommission(organismeId);
                if (organisme != null) {
                    ficheDossier.getCommissions().add(organisme);
                }
            }
        }
        
        return dossierWS;
    }
    
    /**
     * Récupère et assemble une commission (organisme)
     * 
     * @param organismeId
     * @return
     * @throws ClientException
     */
    protected Organisme assembleCommission(final String organismeId) throws ClientException {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        final DocumentModel organismeDoc = tableReferenceService.getOrganismeById(session, organismeId);
        if (organismeDoc == null) {
            throw new ClientException("Commission, organisme inexistant: " + organismeId);
        }
        return TableReferenceAssembler.toOrganismeXsd(organismeDoc);
    }
    
    /**
     * Assemble les données des commissions objet métier -> XSD.
     * 
     * @param dossierDoc Dossier à assembler
     * @return Objet dépot WS
     * @throws ClientException
     */
    public Commission assembleCommissionDossierToXsd(DocumentModel dossierDoc) throws ClientException {
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        Commission commission = null;
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        if (StringUtils.isNotBlank(dossier.getCommissionSaisieAuFond()) || dossier.getCommissionSaisiePourAvis() != null) {
            commission = new Commission();
            DocumentModel organismeDoc = tableReferenceService.getOrganismeById(session, dossier.getCommissionSaisieAuFond());
            if (organismeDoc != null) {
                commission.setSaisieAuFond(TableReferenceAssembler.toOrganismeXsd(organismeDoc));
            }
            if (dossier.getCommissionSaisiePourAvis() != null) {
                for (String organismeId : dossier.getCommissionSaisiePourAvis()) {
                    organismeDoc = tableReferenceService.getOrganismeById(session, organismeId);
                    if (organismeDoc != null) {
                        commission.getSaisiePourAvis().add(TableReferenceAssembler.toOrganismeXsd(organismeDoc));
                    }
                }
            }
        }
        return commission;
    }
    
    /**
     * Assemble les données du dépot de texte objet métier -> XSD.
     * 
     * @param dossierDoc Version à assembler
     * @return Objet dépot WS
     * @throws ClientException
     */
    public Depot assembleDepotRapportDossierToXsd(DocumentModel dossierDoc) throws ClientException {
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        Depot depot = null;
        if (dossier.getDateDepotRapport() != null || !StringUtils.isBlank(dossier.getNumeroDepotRapport())) {
            depot = new Depot();
            if (dossier.getDateDepotRapport() != null) {
                depot.setDate(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateDepotRapport()));
            }
            depot.setNumero(dossier.getNumeroDepotRapport());
        }
        return depot;
    }
    

    /**
     * Assemble les données du dépot de texte objet métier -> XSD.
     * 
     * @param dossierDoc dossier à assembler
     * @return Objet dépot WS
     * @throws ClientException
     */
    public Depot assembleDepotTexteDossierToXsd(DocumentModel dossierDoc) throws ClientException {
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        Depot depot = null;
        if (dossier.getDateDepotTexte() != null || !StringUtils.isBlank(dossier.getNumeroDepotTexte())) {
            depot = new Depot();
            if (dossier.getDateDepotTexte() != null) {
                depot.setDate(DateUtil.calendarToXMLGregorianCalendar(dossier.getDateDepotTexte()));
            }
            depot.setNumero(dossier.getNumeroDepotTexte());
        }
        return depot;
    }

    /**
     * Assemble les données du niveau de lecture objet métier -> XSD.
     * 
     * @param dossierDoc Document dossier à assembler
     * @return Niveau de lecture assemblé
     * @throws ClientException
     */
    public NiveauLecture assembleNiveauLectureDossierToXsd(DocumentModel dossierDoc) throws ClientException {
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        NiveauLecture niveauLecture = new NiveauLecture();
        if (dossier.getNiveauLectureNumero() != null) {
            niveauLecture.setNiveau(dossier.getNiveauLectureNumero().intValue());
        }
        if (dossier.getNiveauLecture() != null) {
            NiveauLectureCode niveauLectureCode = NiveauLectureCodeAssembler.assembleNiveauLectureCodeToXsd(dossier.getNiveauLecture());
            niveauLecture.setCode(niveauLectureCode);
        }
        return niveauLecture;
    }
    
}
