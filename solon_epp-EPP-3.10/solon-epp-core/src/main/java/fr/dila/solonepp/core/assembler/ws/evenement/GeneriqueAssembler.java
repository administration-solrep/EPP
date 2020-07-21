package fr.dila.solonepp.core.assembler.ws.evenement;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
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
public class GeneriqueAssembler extends AbstractGeneriqueAssembler {

    public GeneriqueAssembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(eppEvtContainer, session, principal);
        if (eppEvtContainer != null) {
            setEppEvt(eppEvtContainer.getEvtGenerique());
        } else {
            setEppEvt(new EppEvtGenerique());
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.GENERIQUE;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.GENERIQUE);
        eppEvtContainerResponse.setEvtGenerique((EppEvtGenerique) getEppBaseEvenement());
    }

    @Override
    protected boolean verifyCopy() {
        return false;
    }

}
