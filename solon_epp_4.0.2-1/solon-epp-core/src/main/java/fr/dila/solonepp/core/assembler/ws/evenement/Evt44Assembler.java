package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.RapportParlementAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt44;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt44}
 *
 * @author asatre
 *
 */
public class Evt44Assembler extends BaseAssembler {
    private final EppEvt44 eppEvt;

    public Evt44Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt44();
        } else {
            eppEvt = new EppEvt44();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        version.setRapportParlement(
            RapportParlementAssembler.assembleXsdToRapportParlement(eppEvt.getRapportParlement())
        );
        if (eppEvt.getDate() != null) {
            version.setDate(eppEvt.getDate().toGregorianCalendar());
        }
        version.setNorLoi(eppEvt.getNor());
        if (eppEvt.getAnneeRapport() != null) {
            version.setAnneeRapport((long) eppEvt.getAnneeRapport());
        }
        version.setUrlBaseLegale(eppEvt.getUrlBaseLegale());
        version.setBaseLegale(eppEvt.getBaseLegale());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getRapport() != null) {
            pieceJointeList.add(eppEvt.getRapport());
        }
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_44;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        eppEvt.setRapportParlement(
            RapportParlementAssembler.assembleRapportParlementToXsd(version.getRapportParlement())
        );
        if (version.getDate() != null) {
            eppEvt.setDate(DateUtil.calendarToXMLGregorianCalendar(version.getDate()));
        }
        eppEvt.setNor(version.getNorLoi());

        if (version.getAnneeRapport() != null) {
            eppEvt.setAnneeRapport(version.getAnneeRapport().intValue());
        }

        eppEvt.setUrlBaseLegale(version.getUrlBaseLegale());
        eppEvt.setBaseLegale(version.getBaseLegale());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case LETTRE_PM:
                        eppEvt.setLettrePm(pieceJointe);
                        break;
                    case RAPPORT:
                        eppEvt.setRapport(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_44);
        eppEvtContainerResponse.setEvt44(eppEvt);
    }
}
