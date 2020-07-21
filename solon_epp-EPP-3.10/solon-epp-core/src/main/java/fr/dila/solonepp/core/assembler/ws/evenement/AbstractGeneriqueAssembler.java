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
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppEvtGenerique;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvtGenerique}
 * 
 * @author asatre
 * 
 */
public abstract class AbstractGeneriqueAssembler extends BaseAssembler {

    protected EppEvtGenerique eppEvt;

    public AbstractGeneriqueAssembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {
        if (this.verifyCopy()) {
            this.doVerifyCopy();
        }
    }

    protected abstract boolean verifyCopy();

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    public void setEppEvt(final EppEvtGenerique eppEvt) {
        this.eppEvt = eppEvt;
    }

	@Override
	public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
	}

	@Override
	public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {
	}

}
