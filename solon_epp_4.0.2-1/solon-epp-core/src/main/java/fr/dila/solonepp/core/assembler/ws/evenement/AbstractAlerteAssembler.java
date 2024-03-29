package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtAlerte;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvtAlerte}
 *
 * @author asatre
 *
 */
public abstract class AbstractAlerteAssembler extends BaseAssembler {
    protected EppEvtAlerte eppEvt;

    public AbstractAlerteAssembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
    }

    protected abstract boolean verifyCopy();

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        if (this.verifyCopy()) {
            this.doVerifyCopy();
        }

        version.setPositionAlerte(eppEvt.isPositionAlerte());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {}

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        eppEvt.setPositionAlerte(version.isPositionAlerte());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {}
}
