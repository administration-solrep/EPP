package fr.dila.solonepp.core.assembler.ws.evenement;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvt52;
import fr.sword.xsd.solon.epp.EppEvt53;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * {@link Assembler} pour {@link EppEvt52}
 * 
 * @author asatre
 * 
 */
public class Evt5301Assembler extends AbstractEvt53Assembler {

    public Evt5301Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(eppEvtContainer, session, principal);

        if (eppEvtContainer != null) {
            setEppEvt(eppEvtContainer.getEvt5301());
        } else {
            setEppEvt(new EppEvt53());
        }

    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_53_01;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(getEvenementType());
        eppEvtContainerResponse.setEvt5301((EppEvt53) getEppBaseEvenement());
    }

    @Override
    protected boolean verifyCopy() {
        return false;
    }

}
