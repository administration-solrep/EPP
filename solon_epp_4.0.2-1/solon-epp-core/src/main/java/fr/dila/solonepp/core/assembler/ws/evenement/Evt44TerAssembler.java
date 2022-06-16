package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt44Ter;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt44Ter}
 *
 * @author asatre
 *
 */
public class Evt44TerAssembler extends BaseAssembler {
    private final EppEvt44Ter eppEvt;

    public Evt44TerAssembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt44Ter();
        } else {
            eppEvt = new EppEvt44Ter();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        version.setNorLoi(eppEvt.getNor());
        if (eppEvt.getDate() != null) {
            version.setDate(eppEvt.getDate().toGregorianCalendar());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getEcheancier() != null) {
            pieceJointeList.add(eppEvt.getEcheancier());
        }
        if (eppEvt.getLettreTransmission() != null) {
            pieceJointeList.add(eppEvt.getLettreTransmission());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_44_TER;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        eppEvt.setNor(version.getNorLoi());
        if (version.getDate() != null) {
            eppEvt.setDate(DateUtil.calendarToXMLGregorianCalendar(version.getDate()));
        }
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case ECHEANCIER:
                        eppEvt.setEcheancier(pieceJointe);
                        break;
                    case LETTRE_TRANSMISSION:
                        eppEvt.setLettreTransmission(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_44_TER);
        eppEvtContainerResponse.setEvt44Ter(eppEvt);
    }
}
