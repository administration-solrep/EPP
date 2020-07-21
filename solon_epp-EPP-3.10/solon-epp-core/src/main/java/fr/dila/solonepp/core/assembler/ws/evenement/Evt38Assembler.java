package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.SensAvisAssembler;
import fr.dila.solonepp.core.exception.EppClientException;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt38;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt38}
 * 
 * @author asatre
 * 
 */
public class Evt38Assembler extends BaseAssembler {

    private final EppEvt38 eppEvt;

    public Evt38Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt38();
        } else {
            eppEvt = new EppEvt38();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        if (eppEvt.getCopie() != null && eppEvt.getCopie().size() > 0) {
            throw new EppClientException("Cette communication ne peut pas contenir des copies");
          }
        
        if (eppEvt.getDate() != null) {
            version.setDate(eppEvt.getDate().toGregorianCalendar());
        }
        if (eppEvt.getSensAvis() != null) {
            version.setSensAvis(SensAvisAssembler.assembleXsdToSensAvis(eppEvt.getSensAvis()));
        }
        if (eppEvt.getNombreSuffrage() != null) {
            version.setSuffrageExprime((long) eppEvt.getNombreSuffrage());
        }
        if (eppEvt.getVotePour() != null) {
            version.setVotePour((long) eppEvt.getVotePour());
        }
        if (eppEvt.getVoteContre() != null) {
            version.setVoteContre((long) eppEvt.getVoteContre());
        }
        if (eppEvt.getAbstention() != null) {
            version.setAbstention((long) eppEvt.getAbstention());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_38;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        if (version.getDate() != null) {
            eppEvt.setDate(DateUtil.calendarToXMLGregorianCalendar(version.getDate()));
        }
        eppEvt.setSensAvis(SensAvisAssembler.assembleSensAvisToXsd(version.getSensAvis()));

        if (version.getSuffrageExprime() != null) {
            eppEvt.setNombreSuffrage(version.getSuffrageExprime().intValue());
        }
        if (version.getVotePour() != null) {
            eppEvt.setVotePour(version.getVotePour().intValue());
        }
        if (version.getVoteContre() != null) {
            eppEvt.setVoteContre(version.getVoteContre().intValue());
        }
        if (version.getAbstention() != null) {
            eppEvt.setAbstention(version.getAbstention().intValue());
        }
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_38);
        eppEvtContainerResponse.setEvt38(eppEvt);
    }

}
