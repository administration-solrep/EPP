package fr.dila.solonepp.core.assembler.ws.evenement;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppEvtGenerique;
import fr.sword.xsd.solon.epp.EvenementType;

public class Generique12Assembler extends AbstractGeneriqueAssembler {

    public Generique12Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(eppEvtContainer, session, principal);
        if (eppEvtContainer != null) {
            setEppEvt(eppEvtContainer.getGENERIQUE12());
        } else {
            setEppEvt(new EppEvtGenerique());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.GENERIQUE_12;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.GENERIQUE_12);
        eppEvtContainerResponse.setGENERIQUE12((EppEvtGenerique) getEppBaseEvenement());
    }
    @Override
    protected boolean verifyCopy() {
        return true;
    }

}
