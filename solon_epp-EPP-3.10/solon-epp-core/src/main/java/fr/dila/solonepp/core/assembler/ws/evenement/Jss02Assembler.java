package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppJSS02;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

public class Jss02Assembler extends BaseAssembler {

    private final EppJSS02 eppEvt;

    public Jss02Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getJSS02();
        } else {
            eppEvt = new EppJSS02();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) throws ClientException {

    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {
        if (eppEvt.getLettre() != null) {
            pieceJointeList.add(eppEvt.getLettre());
        }
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) throws ClientException {

    }

    @Override
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) throws ClientException {
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case LETTRE:
                    eppEvt.setLettre(pieceJointe);
                    break;
                }
            }
        }

    }

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.JSS_02);
        eppEvtContainerResponse.setJSS02(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.JSS_02;
    }

}
