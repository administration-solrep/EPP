package fr.dila.solonepp.core.assembler.ws.evenement;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppEvtGenerique;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * {@link Assembler} pour {@link EppEvtGenerique}
 * 
 * @author asatre
 * 
 */
public class Generique05Assembler extends AbstractGeneriqueAssembler {

    public Generique05Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(eppEvtContainer, session, principal);
        if (eppEvtContainer != null) {
            setEppEvt(eppEvtContainer.getEvtGenerique05());
        } else {
            setEppEvt(new EppEvtGenerique());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.GENERIQUE_05;
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.GENERIQUE_05);
        eppEvtContainerResponse.setEvtGenerique05((EppEvtGenerique) getEppBaseEvenement());
    }

    @Override
    protected boolean verifyCopy() {
        return true;
    }

}
