package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.MotifIrrecevabiliteAssembler;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt41;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt41}
 *
 * @author asatre
 *
 */
public class Evt41Assembler extends BaseAssembler {
    private final EppEvt41 eppEvt;

    public Evt41Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt41();
        } else {
            eppEvt = new EppEvt41();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        if (eppEvt.getCopie() != null && eppEvt.getCopie().size() > 0) {
            throw new EppNuxeoException("Cette communication ne peut pas contenir des copies");
        }
        version.setMotifIrrecevabilite(
            MotifIrrecevabiliteAssembler.assembleXsdToMotifIrrecevabilite(eppEvt.getMotifIrrecevabilite())
        );
        if (eppEvt.getDateDemande() != null) {
            version.setDate(eppEvt.getDateDemande().toGregorianCalendar());
            version.setDateDemande(eppEvt.getDateDemande().toGregorianCalendar());
        }
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
        return EvenementType.EVT_41;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        eppEvt.setMotifIrrecevabilite(
            MotifIrrecevabiliteAssembler.assembleMotifIrrecevabiliteToXsd(version.getMotifIrrecevabilite())
        );
        if (version.getDate() != null) {
            eppEvt.setDateDemande(DateUtil.calendarToXMLGregorianCalendar(version.getDate()));
        }
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
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_41);
        eppEvtContainerResponse.setEvt41(eppEvt);
    }
}
