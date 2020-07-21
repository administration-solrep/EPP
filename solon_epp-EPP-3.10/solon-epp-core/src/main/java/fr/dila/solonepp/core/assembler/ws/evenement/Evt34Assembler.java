package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.SensAvisAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt34;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt34}
 * 
 * @author asatre
 * 
 */
public class Evt34Assembler extends BaseAssembler {

    private final EppEvt34 eppEvt;

    public Evt34Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt34();
        } else {
            eppEvt = new EppEvt34();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        if (eppEvt.getDateAvis() != null) {
            version.setDate(eppEvt.getDateAvis().toGregorianCalendar());
        }
        if (eppEvt.getSensAvis() != null) {
            version.setSensAvis(SensAvisAssembler.assembleXsdToSensAvis(eppEvt.getSensAvis()));
        }
        if (eppEvt.getNombreSuffrage() != null) {
            version.setSuffrageExprime((long) eppEvt.getNombreSuffrage());
        }
        if (eppEvt.getBulletinBlanc() != null) {
            version.setBulletinBlanc((long) eppEvt.getBulletinBlanc());
        }
        if (eppEvt.getVotePour() != null) {
            version.setVotePour((long) eppEvt.getVotePour());
        }
        if (eppEvt.getVoteContre() != null) {
            version.setVoteContre((long) eppEvt.getVoteContre());
        }
        if (eppEvt.getAbstention() != null) {
            version.setAbstention((long) eppEvt.getAbstention());
        }

        final List<String> commissions = new ArrayList<String>();
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        for (final Organisme commission : eppEvt.getCommission()) {
            if (commission != null) {
                final boolean exist = tableReferenceService.hasOrganisme(session, commission.getId());
                if (exist) {
                    commissions.add(commission.getId());
                } else {
                    throw new ClientException("Commission saisie, organisme inexistant: " + commission.getId());
                }
                
            }
        }
        version.setCommissions(commissions);
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_34;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        if (version.getDate() != null) {
            eppEvt.setDateAvis(DateUtil.calendarToXMLGregorianCalendar(version.getDate()));
        }
        eppEvt.setSensAvis(SensAvisAssembler.assembleSensAvisToXsd(version.getSensAvis()));

        if (version.getSuffrageExprime() != null) {
            eppEvt.setNombreSuffrage(version.getSuffrageExprime().intValue());
        }
        if (version.getBulletinBlanc() != null) {
            eppEvt.setBulletinBlanc(version.getBulletinBlanc().intValue());
        }
        if (version.getVotePour() != null) {
            eppEvt.setVotePour(version.getVotePour().intValue());
        }
        if (version.getVoteContre() != null) {
            eppEvt.setVoteContre(version.getVoteContre().intValue());
        }
        if (version.getAbstention() != null) {
            eppEvt.setAbstention(version.getAbstention().intValue());
        }
        if (version.getCommissions() != null) {
            for (final String organismeId : version.getCommissions()) {
                final Organisme organisme = assembleCommission(organismeId);
                if (organisme != null) {
                    eppEvt.getCommission().add(organisme);
                }
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
                case LETTRE_PM:
                    eppEvt.setLettrePm(pieceJointe);
                    break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_34);
        eppEvtContainerResponse.setEvt34(eppEvt);
    }

}
