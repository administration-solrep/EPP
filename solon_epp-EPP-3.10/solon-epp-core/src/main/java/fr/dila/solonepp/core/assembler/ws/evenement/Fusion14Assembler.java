package fr.dila.solonepp.core.assembler.ws.evenement;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvt53;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;

public class Fusion14Assembler extends AbstractEvt53Assembler {

    public Fusion14Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(eppEvtContainer, session, principal);
        if (eppEvtContainer != null) {
            setEppEvt(eppEvtContainer.getFUSION14());
        } else {
            setEppEvt(new EppEvt53());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.FUSION_14;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(getEvenementType());
        eppEvtContainerResponse.setFUSION14((EppEvt53) getEppBaseEvenement());
    }
    
    @Override
    protected boolean verifyCopy() {
        return false;
    }
}
