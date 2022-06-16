package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.SensAvisAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppSD03;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class Sd03Assembler extends BaseAssembler {
    private final EppSD03 eppEvt;

    public Sd03Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getSD03();
        } else {
            eppEvt = new EppSD03();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) {
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
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {}

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) {
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
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) {}

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.SD_03);
        eppEvtContainerResponse.setSD03(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.SD_03;
    }
}
