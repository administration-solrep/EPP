package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.NatureRapportAssembler;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt04Bis;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt04Bis}
 * 
 * @author asatre
 * 
 */
public class Evt04BisAssembler extends BaseAssembler {

    private final EppEvt04Bis eppEvt;

    public Evt04BisAssembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt04Bis();
        } else {
            eppEvt = new EppEvt04Bis();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        final XMLGregorianCalendar dateDistribution = eppEvt.getDateDistribution();
        if (dateDistribution != null) {
            version.setDateDistributionElectronique(dateDistribution.toGregorianCalendar());
        }
        version.setNatureRapport(NatureRapportAssembler.assembleXsdToNatureRapport(eppEvt.getNatureRapport()));
        version.setRapporteurList(getMandatsIdentifiant(eppEvt.getRapporteur()));
        version.setIntitule(eppEvt.getIntitule());
        version.setTitre(eppEvt.getTitre());
        assembleDepotTexteXsdToVersion(eppEvt.getDepotTexte(), version);
        assembleDepotRapportXsdToVersion(eppEvt.getDepotRapport(), version);
        final Organisme commission = eppEvt.getCommission();

        if (commission != null) {
            final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
            final boolean exist = tableReferenceService.hasOrganisme(session, commission.getId());

            if (exist) {
                version.setCommissionSaisie(commission.getId());
            } else {
                throw new ClientException("Commission saisie, organisme inexistant: " + commission.getId());
            }

        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getTextes() != null) {
            pieceJointeList.addAll(eppEvt.getTextes());
        }
        if (eppEvt.getRapports() != null) {
            pieceJointeList.addAll(eppEvt.getRapports());
        }
        if (eppEvt.getAvis() != null) {
            pieceJointeList.addAll(eppEvt.getAvis());
        }
        if (eppEvt.getAnnexes() != null) {
            pieceJointeList.addAll(eppEvt.getAnnexes());
        }

    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_04_BIS;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        // Renseigne les données de l'événement
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        if (principal.isInstitutionSenat()) {
            eppEvt.setIdSenat(version.getSenat());
        }
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
        final Calendar dateDistribution = version.getDateDistributionElectronique();
        if (dateDistribution != null) {
            eppEvt.setDateDistribution(DateUtil.calendarToXMLGregorianCalendar(dateDistribution));
        }
        eppEvt.setNatureRapport(NatureRapportAssembler.assembleNatureRapportToXsd(version.getNatureRapport()));
        if (version.getRapporteurList() != null) {
            for (final String mandatId : version.getRapporteurList()) {
                final DocumentModel mandatDoc = tableReferenceService.getMandatById(session, mandatId);
                if (mandatDoc != null) {
                    final Mandat mandat = TableReferenceAssembler.toMandatXsd(session, mandatDoc);
                    eppEvt.getRapporteur().add(mandat);
                }
            }
        }
        eppEvt.setIntitule(version.getIntitule());
        eppEvt.setTitre(version.getTitre());
        eppEvt.setDepotTexte(assembleDepotTexteVersionToXsd(version));
        eppEvt.setDepotRapport(assembleDepotRapportVersionToXsd(version));
        if (version.getCommissionSaisie() != null) {
            final DocumentModel organismeDoc = tableReferenceService.getOrganismeById(session, version.getCommissionSaisie());
            if (organismeDoc != null) {
                eppEvt.setCommission(TableReferenceAssembler.toOrganismeXsd(organismeDoc));
            }
        }

    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case TEXTE:
                    eppEvt.getTextes().add(pieceJointe);
                    break;
                case RAPPORT:
                    eppEvt.getRapports().add(pieceJointe);
                    break;
                case AVIS:
                    eppEvt.getAvis().add(pieceJointe);
                    break;
                case ANNEXE:
                    eppEvt.getAnnexes().add(pieceJointe);
                    break;
                }
            }
        }

    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_04_BIS);
        eppEvtContainerResponse.setEvt04Bis(eppEvt);

    }

}
