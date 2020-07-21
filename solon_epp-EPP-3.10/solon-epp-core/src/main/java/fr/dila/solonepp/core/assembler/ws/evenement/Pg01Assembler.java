package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppPG01;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

public class Pg01Assembler extends BaseAssembler {

    private final EppPG01 eppEvt;

    public Pg01Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getPG01();
        } else {
            eppEvt = new EppPG01();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) throws ClientException {

        // Date Presentaion
        if (eppEvt.getDatePresentation() != null) {
            version.setDatePresentation(eppEvt.getDatePresentation().toGregorianCalendar());
        }

        // Date Lettre du premier ministere
        if (eppEvt.getDateLettrePm() != null) {
            version.setDateLettrePm(eppEvt.getDateLettrePm().toGregorianCalendar());
        }

    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }

    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) throws ClientException {
        if (version.getDateLettrePm() != null) {
            eppEvt.setDateLettrePm(DateUtil.calendarToXMLGregorianCalendar(version.getDateLettrePm()));
        }
        if (version.getDatePresentation() != null) {
            eppEvt.setDatePresentation(DateUtil.calendarToXMLGregorianCalendar(version.getDatePresentation()));
        }
    }

    @Override
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) throws ClientException {

        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case LETTRE_PM:
                    eppEvt.setLettrePm(pieceJointe);
                    break;
                }
            }
        }

    }

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.PG_01);
        eppEvtContainerResponse.setPG01(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.PG_01;
    }

}
