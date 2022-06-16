package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt52;
import fr.sword.xsd.solon.epp.EppEvt53;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt52}
 *
 * @author asatre
 *
 */
public abstract class AbstractEvt53Assembler extends BaseAssembler {
    private EppEvt53 eppEvt;

    public AbstractEvt53Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        if (this.verifyCopy()) {
            this.doVerifyCopy();
        }
        version.setDossierCible(eppEvt.getDossierCible());
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

    public void setEppEvt(final EppEvt53 eppEvt) {
        this.eppEvt = eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        eppEvt.setDossierCible(version.getDossierCible());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {}
}
