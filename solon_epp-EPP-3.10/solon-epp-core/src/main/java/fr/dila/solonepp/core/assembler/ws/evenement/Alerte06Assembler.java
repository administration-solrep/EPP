package fr.dila.solonepp.core.assembler.ws.evenement;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvtAlerte;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * {@link Assembler} pour {@link EppEvtAlerte}
 * 
 * @author asatre
 * 
 */
public class Alerte06Assembler extends AbstractAlerteAssembler {

    public Alerte06Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(eppEvtContainer, session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvtAlerte06();
        } else {
            eppEvt = new EppEvtAlerte();
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.ALERTE_06;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.ALERTE_06);
        eppEvtContainerResponse.setEvtAlerte06(eppEvt);
    }

    @Override
    protected boolean verifyCopy() {
        return true;
    }

}
