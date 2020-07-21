package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt48;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt48}
 * 
 * @author asatre
 * 
 */
public class Evt48Assembler extends BaseAssembler {

    private final EppEvt48 eppEvt;

    public Evt48Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt48();
        } else {
            eppEvt = new EppEvt48();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        if (eppEvt.getDateJo() != null) {
            version.setDateJo(eppEvt.getDateJo().toGregorianCalendar());
        }
        version.setNor(eppEvt.getNor());
        if (eppEvt.getPageJo() != null) {
            version.setPageJo((long) eppEvt.getPageJo());
        }
        if (eppEvt.getNumeroRubrique() != null) {
            version.setNumeroRubrique((long) eppEvt.getNumeroRubrique());
        }
        version.setUrlPublication(eppEvt.getUrlPublication());
        if (eppEvt.getNumeroJo() != null) {
            version.setNumeroJo((long) eppEvt.getNumeroJo());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_48;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        if (version.getDateJo() != null) {
            eppEvt.setDateJo(DateUtil.calendarToXMLGregorianCalendar(version.getDateJo()));
        }
        eppEvt.setNor(version.getNor());

        if (version.getPageJo() != null) {
            eppEvt.setPageJo(version.getPageJo().intValue());
        }
        if (version.getNumeroRubrique() != null) {
            eppEvt.setNumeroRubrique(version.getNumeroRubrique().intValue());
        }

        eppEvt.setUrlPublication(version.getUrlPublication());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_48);
        eppEvtContainerResponse.setEvt48(eppEvt);

    }

}
