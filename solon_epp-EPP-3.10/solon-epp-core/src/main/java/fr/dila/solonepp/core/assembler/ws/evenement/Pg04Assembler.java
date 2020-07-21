package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.SensAvisAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppPG04;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

public class Pg04Assembler extends BaseAssembler {

    private final EppPG04 eppEvt;

    public Pg04Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getPG04();
        } else {
            eppEvt = new EppPG04();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) throws ClientException {

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
        if (eppEvt.getDateVote() != null) {
            version.setDateVote(eppEvt.getDateVote().toGregorianCalendar());
        }

        if (eppEvt.getSensVote() != null) {
            version.setSensAvis(SensAvisAssembler.assembleXsdToSensAvis(eppEvt.getSensVote()));
        }

    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {

    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) throws ClientException {

        if (version.getVotePour() != null) {
            eppEvt.setVotePour(version.getVotePour().intValue());
        }

        if (version.getVoteContre() != null) {
            eppEvt.setVoteContre(version.getVoteContre().intValue());
        }
        if (version.getAbstention() != null) {
            eppEvt.setAbstention(version.getAbstention().intValue());
        }

        if (version.getSuffrageExprime() != null) {
            eppEvt.setNombreSuffrage(version.getSuffrageExprime().intValue());
        }

        if (version.getDateVote() != null) {
            eppEvt.setDateVote(DateUtil.calendarToXMLGregorianCalendar(version.getDateVote()));
        }
        eppEvt.setSensVote(SensAvisAssembler.assembleSensAvisToXsd(version.getSensAvis()));
    }

    @Override
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) throws ClientException {
    }

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.PG_04);
        eppEvtContainerResponse.setPG04(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.PG_04;
    }

}
