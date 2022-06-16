package fr.dila.solonepp.rest.management;

import static fr.sword.xsd.commons.TraitementStatut.OK;
import static org.mockito.Mockito.when;

import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.SolonEppFeature;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.sword.xsd.solon.epp.Acteur;
import fr.sword.xsd.solon.epp.ActionObjetReference;
import fr.sword.xsd.solon.epp.ChercherCorbeilleRequest;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.ChercherIdentiteRequest;
import fr.sword.xsd.solon.epp.ChercherIdentiteRequest.DescriptifRequete;
import fr.sword.xsd.solon.epp.ChercherIdentiteResponse;
import fr.sword.xsd.solon.epp.ChercherIdentiteResponse.DescriptifResultat;
import fr.sword.xsd.solon.epp.ChercherMandatParNORRequest;
import fr.sword.xsd.solon.epp.ChercherMandatParNORResponse;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceResponse;
import fr.sword.xsd.solon.epp.Circonscription;
import fr.sword.xsd.solon.epp.Civilite;
import fr.sword.xsd.solon.epp.Corbeille;
import fr.sword.xsd.solon.epp.Gouvernement;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesRequest;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesResponse;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesResponse.CorbeilleInfos;
import fr.sword.xsd.solon.epp.Identite;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.MajTableRequest;
import fr.sword.xsd.solon.epp.MajTableResponse;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.MembreGroupe;
import fr.sword.xsd.solon.epp.Ministere;
import fr.sword.xsd.solon.epp.ObjetContainer;
import fr.sword.xsd.solon.epp.ObjetType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.Periode;
import fr.sword.xsd.solon.epp.Section;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJORequest;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJOResponse;
import fr.sword.xsd.solon.epp.TypeMandat;
import fr.sword.xsd.solon.epp.TypeOrganisme;
import fr.sword.xsd.solon.epp.TypePeriode;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.uidgen.DummyUIDSequencerImpl;
import org.nuxeo.ecm.core.uidgen.UIDGeneratorService;
import org.nuxeo.ecm.core.uidgen.UIDSequencer;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ SolonEppFeature.class, MockitoFeature.class })
public class EppDelegateTest {
    @Inject
    protected CoreFeature coreFeature;

    @Inject
    protected DossierService dossierService;

    @Inject
    protected TableReferenceService tableReferenceService;

    @Mock
    private EppPrincipal principal;

    @Mock
    @RuntimeService
    private UIDGeneratorService uidGeneratorService;

    private EppDelegate delegate;
    private CloseableCoreSession session;

    private XMLGregorianCalendar date;
    private DocumentModel acteurDoc;
    private DocumentModel identiteDoc;
    private DocumentModel gouvernementDoc;
    private DocumentModel ministereDoc;
    private DocumentModel circonscriptionDoc;
    private DocumentModel mandatDoc;

    @Before
    public void setUp() throws Exception {
        session = coreFeature.openCoreSession(principal);
        delegate = new EppDelegate(session);

        when(principal.isAdministrator()).thenReturn(true);

        UIDSequencer sequencer = new DummyUIDSequencerImpl();
        when(uidGeneratorService.getSequencer()).thenReturn(sequencer);

        date = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
    }

    @After
    public void tearDown() {
        session.close();
    }

    @Test
    public void testChercherCorbeille() {
        ChercherCorbeilleRequest request = new ChercherCorbeilleRequest();
        ChercherCorbeilleResponse response;

        when(principal.getInstitutionId()).thenReturn(InstitutionsEnum.GOUVERNEMENT.name());
        response = delegate.chercherCorbeille(request);
        Assert.assertEquals(OK, response.getStatut());
        List<Corbeille> corbeillesGouv = response.getCorbeille();
        Assert.assertNotNull(corbeillesGouv);
        Corbeille corbeilleGouv = corbeillesGouv.get(0);
        Assert.assertEquals("CORBEILLE_GOUVERNEMENT_CENSURE", corbeilleGouv.getIdCorbeille());
        Assert.assertEquals("Motion de censure", corbeilleGouv.getNom());
        Assert.assertTrue(corbeilleGouv.getDescription().contains("l'alinéa 2 de l'article 49"));
        List<Section> sectionsGouv = response.getSection();
        Assert.assertNotNull(sectionsGouv);
        Section sectionGouv = sectionsGouv.get(0);
        Assert.assertEquals("SECTION_GOUVERNEMENT_PROCEDURE_LEGISLATIVE", sectionGouv.getIdSection());
        Assert.assertEquals("Procédure législative", sectionGouv.getNom());
        Assert.assertTrue(sectionGouv.getDescription().contains("les procédures législatives"));

        when(principal.getInstitutionId()).thenReturn(InstitutionsEnum.ASSEMBLEE_NATIONALE.name());
        response = delegate.chercherCorbeille(request);
        Assert.assertEquals(OK, response.getStatut());
        List<Corbeille> corbeillesAN = response.getCorbeille();
        Assert.assertNotNull(corbeillesAN);
        Corbeille corbeilleAN = corbeillesAN.get(0);
        Assert.assertEquals("CORBEILLE_AN_RAPPORT_PARLEMENT", corbeilleAN.getIdCorbeille());
        Assert.assertEquals("Rapports au Parlement", corbeilleAN.getNom());
        Assert.assertTrue(corbeilleAN.getDescription().contains("procédure de dépôt des rapports"));
        List<Section> sectionsAN = response.getSection();
        Assert.assertNotNull(sectionsAN);
        Section sectionAN = sectionsAN.get(0);
        Assert.assertEquals("SECTION_AN_SEANCE_PROCEDURE_LEGISLATIVE", sectionAN.getIdSection());
        Assert.assertEquals("Séance procédure législative", sectionAN.getNom());
        Assert.assertTrue(sectionAN.getDescription().contains("intéressant la division"));

        when(principal.getInstitutionId()).thenReturn(InstitutionsEnum.SENAT.name());
        response = delegate.chercherCorbeille(request);
        Assert.assertEquals(OK, response.getStatut());
        List<Corbeille> corbeillesSenat = response.getCorbeille();
        Assert.assertNotNull(corbeillesSenat);
        Corbeille corbeilleSenat = corbeillesSenat.get(0);
        Assert.assertEquals("CORBEILLE_SENAT_ADOPTION", corbeilleSenat.getIdCorbeille());
        Assert.assertEquals("Adoption", corbeilleSenat.getNom());
        Assert.assertTrue(corbeilleSenat.getDescription().contains("envoyés par le Sénat"));
        List<Section> sectionsSenat = response.getSection();
        Assert.assertNotNull(sectionsSenat);
        Section sectionSenat = sectionsSenat.get(0);
        Assert.assertEquals("SECTION_SENAT_DEPOT", sectionSenat.getIdSection());
        Assert.assertEquals("Dépôt", sectionSenat.getNom());
        Assert.assertTrue(sectionSenat.getDescription().contains("émis concernant les procédures"));

        when(principal.getInstitutionId()).thenReturn(InstitutionsEnum.DILA.name());
        response = delegate.chercherCorbeille(request);
        Assert.assertEquals(OK, response.getStatut());
        List<Corbeille> corbeillesDILA = response.getCorbeille();
        Assert.assertNotNull(corbeillesDILA);
        Corbeille corbeilleDILA = corbeillesDILA.get(0);
        Assert.assertEquals("CORBEILLE_DILA_EMETTEUR", corbeilleDILA.getIdCorbeille());
        Assert.assertEquals("Émetteur", corbeilleDILA.getNom());
        Assert.assertEquals("Corbeille émetteur", corbeilleDILA.getDescription());
        List<Section> sectionsDILA = response.getSection();
        Assert.assertNotNull(sectionsDILA);
        Assert.assertTrue(sectionsDILA.isEmpty());
    }

    @Test
    public void testMajTable() throws Exception {
        MajTableRequest request = new MajTableRequest();
        MajTableResponse response;

        ObjetContainer objetContainer = new ObjetContainer();
        request.setObjetContainer(objetContainer);
        when(principal.getInstitutionId()).thenReturn(InstitutionsEnum.ASSEMBLEE_NATIONALE.name());

        // Acteur
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.ACTEUR);
        objetContainer.getActeur().add(new Acteur());
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.ACTEUR, response.getObjetContainer().getType());
        List<Acteur> acteurList = response.getObjetContainer().getActeur();
        Assert.assertNotNull(acteurList);
        Acteur act = acteurList.get(0);
        Assert.assertTrue(tableReferenceService.hasActeur(session, act.getId()));

        // Circonscription
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.CIRCONSCRIPTION);
        Circonscription circ = new Circonscription();
        circ.setNom("Circonscription");
        circ.setProprietaire(Institution.ASSEMBLEE_NATIONALE);
        circ.setDateDebut(date);
        objetContainer.getCirconscription().add(circ);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.CIRCONSCRIPTION, response.getObjetContainer().getType());
        circ = response.getObjetContainer().getCirconscription().get(0);
        Assert.assertTrue(tableReferenceService.hasCirconscription(session, circ.getId()));
        Assert.assertEquals("Circonscription", circ.getNom());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, circ.getProprietaire());
        Assert.assertEquals(date, circ.getDateDebut());
        Assert.assertNull(circ.getDateFin());

        request.setAction(ActionObjetReference.MODIFIER);
        circ.setNom("Circonscription Modif");
        objetContainer.getCirconscription().clear();
        objetContainer.getCirconscription().add(circ);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.CIRCONSCRIPTION, response.getObjetContainer().getType());
        circ = response.getObjetContainer().getCirconscription().get(0);
        Assert.assertEquals("Circonscription Modif", circ.getNom());
        Assert.assertNull(circ.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getCirconscription().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.Circonscription circonscription = tableReferenceService
            .getCirconscriptionById(session, circ.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.Circonscription.class);
        Assert.assertNotNull(circonscription);
        Assert.assertNotNull(circonscription.getDateFin());

        // Gouvernement
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.GOUVERNEMENT);
        Gouvernement gouv = new Gouvernement();
        gouv.setAppellation("Gouvernement");
        gouv.setDateDebut(date);
        objetContainer.getGouvernement().add(gouv);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.GOUVERNEMENT, response.getObjetContainer().getType());
        gouv = response.getObjetContainer().getGouvernement().get(0);
        Assert.assertTrue(tableReferenceService.hasGouvernement(session, gouv.getId()));
        Assert.assertEquals("Gouvernement", gouv.getAppellation());
        Assert.assertEquals(date, gouv.getDateDebut());
        Assert.assertNull(gouv.getDateFin());

        request.setAction(ActionObjetReference.MODIFIER);
        gouv.setAppellation("Gouvernement Modif");
        objetContainer.getGouvernement().clear();
        objetContainer.getGouvernement().add(gouv);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.GOUVERNEMENT, response.getObjetContainer().getType());
        gouv = response.getObjetContainer().getGouvernement().get(0);
        Assert.assertEquals("Gouvernement Modif", gouv.getAppellation());
        Assert.assertNull(gouv.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getGouvernement().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.Gouvernement gouvernement = tableReferenceService
            .getGouvernementById(session, gouv.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.Gouvernement.class);
        Assert.assertNotNull(gouvernement);
        Assert.assertNotNull(gouvernement.getDateFin());

        // Identite
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.IDENTITE);
        Identite ident = new Identite();
        ident.setCivilite(Civilite.M);
        ident.setPrenom("Prénom");
        ident.setNom("Nom");
        ident.setIdActeur(act.getId());
        ident.setProprietaire(Institution.ASSEMBLEE_NATIONALE);
        ident.setDateDebut(date);
        objetContainer.getIdentite().add(ident);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.IDENTITE, response.getObjetContainer().getType());
        ident = response.getObjetContainer().getIdentite().get(0);
        Assert.assertTrue(tableReferenceService.hasIdentite(session, ident.getId()));
        Assert.assertEquals(Civilite.M, ident.getCivilite());
        Assert.assertEquals("Prénom", ident.getPrenom());
        Assert.assertEquals("Nom", ident.getNom());
        Assert.assertEquals(act.getId(), ident.getIdActeur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, ident.getProprietaire());
        Assert.assertEquals(date, ident.getDateDebut());
        Assert.assertNull(ident.getDateFin());

        request.setAction(ActionObjetReference.MODIFIER);
        ident.setCivilite(Civilite.MME);
        ident.setPrenom("Prénom Modif");
        ident.setNom("Nom Modif");
        objetContainer.getIdentite().clear();
        objetContainer.getIdentite().add(ident);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.IDENTITE, response.getObjetContainer().getType());
        ident = response.getObjetContainer().getIdentite().get(0);
        Assert.assertEquals(Civilite.MME, ident.getCivilite());
        Assert.assertEquals("Prénom Modif", ident.getPrenom());
        Assert.assertEquals("Nom Modif", ident.getNom());
        Assert.assertNull(ident.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getIdentite().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.Identite identite = tableReferenceService
            .getIdentiteById(session, ident.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.Identite.class);
        Assert.assertNotNull(identite);
        Assert.assertNotNull(identite.getDateFin());

        // Ministere
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.MINISTERE);
        Ministere min = new Ministere();
        min.setAppellation("Ministère");
        min.setEdition("Edition");
        min.setLibelle("Libellé");
        min.setNom("Nom");
        min.setIdGouvernement(gouv.getId());
        min.setDateDebut(date);
        objetContainer.getMinistere().add(min);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.MINISTERE, response.getObjetContainer().getType());
        min = response.getObjetContainer().getMinistere().get(0);
        Assert.assertTrue(tableReferenceService.hasMinistere(session, min.getId()));
        Assert.assertEquals("Ministère", min.getAppellation());
        Assert.assertEquals("Edition", min.getEdition());
        Assert.assertEquals("Libellé", min.getLibelle());
        Assert.assertEquals("Nom", min.getNom());
        Assert.assertEquals(gouv.getId(), min.getIdGouvernement());
        Assert.assertEquals(date, min.getDateDebut());
        Assert.assertNull(min.getDateFin());

        request.setAction(ActionObjetReference.MODIFIER);
        min.setAppellation("Ministère Modif");
        objetContainer.getMinistere().clear();
        objetContainer.getMinistere().add(min);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.MINISTERE, response.getObjetContainer().getType());
        min = response.getObjetContainer().getMinistere().get(0);
        Assert.assertEquals("Ministère Modif", min.getAppellation());
        Assert.assertNull(min.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getMinistere().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.Ministere ministere = tableReferenceService
            .getMinistereById(session, min.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.Ministere.class);
        Assert.assertNotNull(ministere);
        Assert.assertNotNull(ministere.getDateFin());

        // Mandat
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.MANDAT);
        Mandat mand = new Mandat();
        mand.setAppellation("Mandat");
        mand.setType(TypeMandat.HAUT_COMMISSAIRE);
        mand.setProprietaire(Institution.ASSEMBLEE_NATIONALE);
        mand.setIdIdentite(ident.getId());
        mand.setIdMinistere(min.getId());
        mand.setIdCirconscription(circ.getId());
        mand.setDateDebut(date);
        objetContainer.getMandat().add(mand);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.MANDAT, response.getObjetContainer().getType());
        mand = response.getObjetContainer().getMandat().get(0);
        Assert.assertTrue(tableReferenceService.hasMandat(session, mand.getId()));
        Assert.assertEquals("Mandat", mand.getAppellation());
        Assert.assertEquals(TypeMandat.HAUT_COMMISSAIRE, mand.getType());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, mand.getProprietaire());
        Assert.assertEquals(ident.getId(), mand.getIdIdentite());
        Assert.assertEquals(min.getId(), mand.getIdMinistere());
        Assert.assertEquals(circ.getId(), mand.getIdCirconscription());
        Assert.assertEquals(date, mand.getDateDebut());
        Assert.assertNull(mand.getDateFin());

        request.setAction(ActionObjetReference.MODIFIER);
        mand.setAppellation("Mandat Modif");
        objetContainer.getMandat().clear();
        objetContainer.getMandat().add(mand);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.MANDAT, response.getObjetContainer().getType());
        mand = response.getObjetContainer().getMandat().get(0);
        Assert.assertEquals("Mandat Modif", mand.getAppellation());
        Assert.assertNull(mand.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getMandat().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.Mandat mandat = tableReferenceService
            .getMandatById(session, mand.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.Mandat.class);
        Assert.assertNotNull(mandat);
        Assert.assertNotNull(mandat.getDateFin());

        // Organisme
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.ORGANISME);
        Organisme org = new Organisme();
        org.setNom("Organisme");
        org.setType(TypeOrganisme.GROUPE_AN);
        org.setProprietaire(Institution.ASSEMBLEE_NATIONALE);
        org.setDateDebut(date);
        objetContainer.getOrganisme().add(org);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.ORGANISME, response.getObjetContainer().getType());
        org = response.getObjetContainer().getOrganisme().get(0);
        Assert.assertTrue(tableReferenceService.hasOrganisme(session, org.getId()));
        Assert.assertEquals("Organisme", org.getNom());
        Assert.assertEquals(TypeOrganisme.GROUPE_AN, org.getType());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, org.getProprietaire());
        Assert.assertEquals(date, org.getDateDebut());
        Assert.assertNull(org.getDateFin());

        request.setAction(ActionObjetReference.MODIFIER);
        org.setNom("Organisme Modif");
        objetContainer.getOrganisme().clear();
        objetContainer.getOrganisme().add(org);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.ORGANISME, response.getObjetContainer().getType());
        org = response.getObjetContainer().getOrganisme().get(0);
        Assert.assertEquals("Organisme Modif", org.getNom());
        Assert.assertNull(org.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getOrganisme().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.Organisme organisme = tableReferenceService
            .getOrganismeById(session, org.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.Organisme.class);
        Assert.assertNotNull(organisme);
        Assert.assertNotNull(organisme.getDateFin());

        // Membre Groupe
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.MEMBRE_GROUPE);
        MembreGroupe mbrGrp = new MembreGroupe();
        mbrGrp.setIdMandat(mand.getId());
        mbrGrp.setIdOrganisme(org.getId());
        mbrGrp.setDateDebut(date);
        objetContainer.getMembreGroupe().add(mbrGrp);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.MEMBRE_GROUPE, response.getObjetContainer().getType());
        mbrGrp = response.getObjetContainer().getMembreGroupe().get(0);
        Assert.assertTrue(tableReferenceService.hasMembreGroupe(session, mbrGrp.getId()));
        Assert.assertEquals(mand.getId(), mbrGrp.getIdMandat());
        Assert.assertEquals(org.getId(), mbrGrp.getIdOrganisme());
        Assert.assertEquals(date, mbrGrp.getDateDebut());
        Assert.assertNull(mbrGrp.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getMembreGroupe().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.MembreGroupe membreGroupe = tableReferenceService
            .getMembreGroupeById(session, mbrGrp.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.MembreGroupe.class);
        Assert.assertNotNull(membreGroupe);
        Assert.assertNotNull(membreGroupe.getDateFin());

        // Periode
        request.setAction(ActionObjetReference.AJOUTER);
        objetContainer.setType(ObjetType.PERIODE);
        Periode per = new Periode();
        per.setNumero("111");
        per.setProprietaire(Institution.ASSEMBLEE_NATIONALE);
        per.setType(TypePeriode.LEGISLATURE);
        per.setDateDebut(date);
        objetContainer.getPeriode().add(per);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.PERIODE, response.getObjetContainer().getType());
        per = response.getObjetContainer().getPeriode().get(0);
        Assert.assertTrue(tableReferenceService.hasPeriode(session, per.getId()));
        Assert.assertEquals("111", per.getNumero());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, per.getProprietaire());
        Assert.assertEquals(TypePeriode.LEGISLATURE, per.getType());
        Assert.assertEquals(date, per.getDateDebut());
        Assert.assertNull(per.getDateFin());

        request.setAction(ActionObjetReference.MODIFIER);
        per.setNumero("112");
        objetContainer.getPeriode().clear();
        objetContainer.getPeriode().add(per);
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(ObjetType.PERIODE, response.getObjetContainer().getType());
        per = response.getObjetContainer().getPeriode().get(0);
        Assert.assertEquals("112", per.getNumero());
        Assert.assertNull(per.getDateFin());

        request.setAction(ActionObjetReference.RENOUVELER);
        objetContainer.getPeriode().clear();
        response = delegate.majTable(request);
        Assert.assertEquals(OK, response.getStatut());
        fr.dila.solonepp.api.domain.tablereference.Periode periode = tableReferenceService
            .getPeriodeById(session, per.getId())
            .getAdapter(fr.dila.solonepp.api.domain.tablereference.Periode.class);
        Assert.assertNotNull(periode);
        Assert.assertNotNull(periode.getDateFin());
    }

    @Test
    public void testChercherTableDeReference() {
        ChercherTableDeReferenceRequest request = new ChercherTableDeReferenceRequest();
        ChercherTableDeReferenceResponse response;

        fillTableReference();

        request.setTypeObjet(ObjetType.ACTEUR);
        request.setActifsUniquement(true);
        response = delegate.chercherTableDeReference(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertFalse(response.getObjetContainer().getActeur().isEmpty());
        Acteur acteur = response.getObjetContainer().getActeur().get(0);
        Assert.assertEquals(
            acteurDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Acteur.class).getIdentifiant(),
            acteur.getId()
        );

        request
            .getIdObjet()
            .add(acteurDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Acteur.class).getIdentifiant());
        response = delegate.chercherTableDeReference(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertFalse(response.getObjetContainer().getActeur().isEmpty());
        acteur = response.getObjetContainer().getActeur().get(0);
        Assert.assertEquals(
            acteurDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Acteur.class).getIdentifiant(),
            acteur.getId()
        );

        request.setTypeObjet(ObjetType.GOUVERNEMENT);
        request.getIdObjet().clear();
        response = delegate.chercherTableDeReference(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertFalse(response.getObjetContainer().getGouvernement().isEmpty());
        Gouvernement gouvernement = response.getObjetContainer().getGouvernement().get(0);
        Assert.assertEquals("Gouvernement", gouvernement.getAppellation());

        request.setTypeObjet(ObjetType.MINISTERE);
        request.setParentId(gouvernement.getId());
        response = delegate.chercherTableDeReference(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertFalse(response.getObjetContainer().getMinistere().isEmpty());
        Ministere ministere = response.getObjetContainer().getMinistere().get(0);
        Assert.assertEquals("Ministère", ministere.getAppellation());
        Assert.assertEquals(gouvernement.getId(), ministere.getIdGouvernement());
    }

    @Test
    public void testChercherIdentite() throws Exception {
        ChercherIdentiteRequest request = new ChercherIdentiteRequest();
        ChercherIdentiteResponse response;

        fillTableReference();

        DescriptifRequete descriptifRequete = new DescriptifRequete();
        descriptifRequete.setPrenom("Prénom");
        descriptifRequete.setNom("Nom");
        descriptifRequete.setDateNaissance(date);

        request.getDescriptifRequete().add(descriptifRequete);
        response = delegate.chercherIdentite(request);
        Assert.assertEquals(OK, response.getStatut());
        DescriptifResultat descriptifResultat = response.getDescriptifResultat().get(0);
        Assert.assertFalse(descriptifResultat.getIdentites().isEmpty());
        Identite identite = descriptifResultat.getIdentites().get(0);
        Assert.assertEquals("Prénom", identite.getPrenom());
        Assert.assertEquals("Nom", identite.getNom());
        Assert.assertEquals(date, identite.getDateNaissance());
    }

    @Test
    public void testChercherMandatParNor() {
        ChercherMandatParNORRequest request = new ChercherMandatParNORRequest();
        ChercherMandatParNORResponse response;

        fillTableReference();

        request.setNor("PRMX2012345L");
        response = delegate.chercherMandatParNor(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertNotNull(response.getObjetContainer().getMandat());
        Assert.assertFalse(response.getObjetContainer().getMandat().isEmpty());
        Mandat mandat = response.getObjetContainer().getMandat().get(0);
        Assert.assertEquals("Mandat", mandat.getAppellation());
    }

    @Test
    public void testHasCommunication() {
        HasCommunicationNonTraiteesRequest request = new HasCommunicationNonTraiteesRequest();
        HasCommunicationNonTraiteesResponse response;

        when(principal.getInstitutionId()).thenReturn(InstitutionsEnum.SENAT.name());

        response = delegate.hasCommunication(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertNotNull(response.getCorbeilleInfos());
        Assert.assertFalse(response.getCorbeilleInfos().isEmpty());
        CorbeilleInfos corbeilleInfos = response.getCorbeilleInfos().get(0);
        Assert.assertNotNull(corbeilleInfos.getIdCorbeille());
        Assert.assertFalse(corbeilleInfos.isHasNonTraitees());

        request.getIdCorbeilles().add("CORBEILLE_SENAT_EMETTEUR");
        response = delegate.hasCommunication(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertNotNull(response.getCorbeilleInfos());
        Assert.assertFalse(response.getCorbeilleInfos().isEmpty());
        corbeilleInfos = response.getCorbeilleInfos().get(0);
        Assert.assertEquals("CORBEILLE_SENAT_EMETTEUR", corbeilleInfos.getIdCorbeille());
        Assert.assertFalse(corbeilleInfos.isHasNonTraitees());
    }

    @Test
    public void testTransmissionDatePublicationJO() throws Exception {
        TransmissionDatePublicationJORequest request = new TransmissionDatePublicationJORequest();
        TransmissionDatePublicationJOResponse response;

        when(principal.getInstitutionIdSet()).thenReturn(Collections.singleton(InstitutionsEnum.DILA.name()));
        DocumentModel dossierDoc = createDossier("Dossier");

        request.setIdDossier(dossierDoc.getAdapter(Dossier.class).getTitle());
        request.setDatePublication(date);
        response = delegate.transmissionDatePublicationJO(request);
        Assert.assertEquals(OK, response.getStatut());
        Assert.assertEquals(date.toGregorianCalendar(), dossierDoc.getAdapter(Dossier.class).getDatePublication());
    }

    private DocumentModel createDossier(String idDossier) {
        DocumentModel dossierDoc = dossierService.createBareDossier(session);
        dossierDoc.getAdapter(Dossier.class).setTitle(idDossier);
        return dossierService.createDossier(session, dossierDoc);
    }

    private void fillTableReference() {
        acteurDoc = tableReferenceService.createBareActeurDoc(session);
        acteurDoc = tableReferenceService.createActeur(session, acteurDoc);

        identiteDoc = tableReferenceService.createBareIdentiteDoc(session);
        Identite identite = new Identite();
        identite.setIdActeur(
            acteurDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Acteur.class).getIdentifiant()
        );
        identite.setCivilite(Civilite.M);
        identite.setNom("Nom");
        identite.setPrenom("Prénom");
        identite.setDateNaissance(date);
        identite.setDateDebut(date);
        identiteDoc = TableReferenceAssembler.toIdentiteDoc(session, identite, identiteDoc);
        identiteDoc = tableReferenceService.createIdentite(session, identiteDoc);

        gouvernementDoc = tableReferenceService.createBareGouvernementDoc(session);
        Gouvernement gouvernement = new Gouvernement();
        gouvernement.setAppellation("Gouvernement");
        gouvernement.setDateDebut(date);
        gouvernementDoc = TableReferenceAssembler.toGouvernementDoc(session, gouvernement, gouvernementDoc);
        gouvernementDoc = tableReferenceService.createGouvernement(session, gouvernementDoc);

        ministereDoc = tableReferenceService.createBareMinistereDoc(session);
        Ministere ministere = new Ministere();
        ministere.setIdGouvernement(
            gouvernementDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Gouvernement.class).getIdentifiant()
        );
        ministere.setAppellation("Ministère");
        ministere.setNom("Nom");
        ministere.setLibelle("Libellé");
        ministere.setEdition("Edition");
        ministere.setDateDebut(date);
        ministereDoc = TableReferenceAssembler.toMinistereDoc(session, ministere, ministereDoc);
        ministereDoc = tableReferenceService.createMinistere(session, ministereDoc);

        circonscriptionDoc = tableReferenceService.createBareCirconscriptionDoc(session);
        Circonscription circonscription = new Circonscription();
        circonscription.setNom("Circonscription");
        circonscription.setProprietaire(Institution.ASSEMBLEE_NATIONALE);
        circonscription.setDateDebut(date);
        circonscriptionDoc = TableReferenceAssembler.toCirconscriptionDoc(session, circonscription, circonscriptionDoc);
        circonscriptionDoc = tableReferenceService.createCirconscription(session, circonscriptionDoc);

        mandatDoc = tableReferenceService.createBareMandatDoc(session);
        Mandat mandat = new Mandat();
        mandat.setAppellation("Mandat");
        mandat.setType(TypeMandat.DEPUTE);
        mandat.setProprietaire(Institution.ASSEMBLEE_NATIONALE);
        mandat.setNor("PRMX2012345L");
        mandat.setIdCirconscription(
            circonscriptionDoc
                .getAdapter(fr.dila.solonepp.api.domain.tablereference.Circonscription.class)
                .getIdentifiant()
        );
        mandat.setIdIdentite(
            identiteDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Identite.class).getIdentifiant()
        );
        mandat.setIdMinistere(
            ministereDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Ministere.class).getIdentifiant()
        );
        mandat.setDateDebut(date);
        mandatDoc = TableReferenceAssembler.toMandatDoc(session, mandat, mandatDoc);
        mandatDoc = tableReferenceService.createMandat(session, mandatDoc);
    }
}
