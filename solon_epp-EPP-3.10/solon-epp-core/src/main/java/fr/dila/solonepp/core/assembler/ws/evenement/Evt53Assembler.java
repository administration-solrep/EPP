package fr.dila.solonepp.core.assembler.ws.evenement;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvt53;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * {@link Assembler} pour {@link EppEvt53}
 * 
 * @author asatre
 * 
 */
public class Evt53Assembler extends AbstractEvt53Assembler {

    public Evt53Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(eppEvtContainer, session, principal);

        if (eppEvtContainer != null) {
            setEppEvt(eppEvtContainer.getEvt53());
        } else {
            setEppEvt(new EppEvt53());
        }

    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_53;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(getEvenementType());
        eppEvtContainerResponse.setEvt53((EppEvt53) getEppBaseEvenement());
    }

    @Override
    protected boolean verifyCopy() {
        return false;
    }

}
