package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt54;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt54}
 * 
 * @author asatre
 * 
 */
public class Evt54Assembler extends BaseAssembler {

    private final EppEvt54 eppEvt;

    public Evt54Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt54();
        } else {
            eppEvt = new EppEvt54();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_54;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {

    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_54);
        eppEvtContainerResponse.setEvt54(eppEvt);
    }
}
