package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvtAlerte;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * {@link Assembler} pour {@link EppEvtAlerte}
 *
 * @author asatre
 *
 */
public class Alerte02Assembler extends AbstractAlerteAssembler {

    public Alerte02Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(eppEvtContainer, session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvtAlerte02();
        } else {
            eppEvt = new EppEvtAlerte();
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.ALERTE_02;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.ALERTE_02);
        eppEvtContainerResponse.setEvtAlerte01(eppEvt);
    }

    @Override
    protected boolean verifyCopy() {
        return true;
    }
}
