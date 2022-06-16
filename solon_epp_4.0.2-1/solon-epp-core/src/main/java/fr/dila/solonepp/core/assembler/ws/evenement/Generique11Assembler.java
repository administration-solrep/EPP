package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppEvtGenerique;
import fr.sword.xsd.solon.epp.EvenementType;
import org.nuxeo.ecm.core.api.CoreSession;

public class Generique11Assembler extends AbstractGeneriqueAssembler {

    public Generique11Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(eppEvtContainer, session, principal);
        if (eppEvtContainer != null) {
            setEppEvt(eppEvtContainer.getGENERIQUE11());
        } else {
            setEppEvt(new EppEvtGenerique());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.GENERIQUE_11;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.GENERIQUE_11);
        eppEvtContainerResponse.setGENERIQUE11((EppEvtGenerique) getEppBaseEvenement());
    }

    @Override
    protected boolean verifyCopy() {
        return false;
    }
}
