package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt32;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt32}
 * 
 * @author asatre
 * 
 */
public class Evt32Assembler extends BaseAssembler {

    private final EppEvt32 eppEvt;

    public Evt32Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt32();
        } else {
            eppEvt = new EppEvt32();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        version.setIntitule(eppEvt.getIntitule());
        version.setEcheance(eppEvt.getEcheance());
        if (eppEvt.getDateActe() != null) {
            version.setDateActe(eppEvt.getDateActe().toGregorianCalendar());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
        if (eppEvt.getCurriculumVitae() != null) {
            pieceJointeList.add(eppEvt.getCurriculumVitae());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_32;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        eppEvt.setIntitule(version.getIntitule());
        eppEvt.setEcheance(version.getEcheance());
        if (version.getDateActe() != null) {
            eppEvt.setDateActe(DateUtil.calendarToXMLGregorianCalendar(version.getDateActe()));
        }

    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case LETTRE_PM:
                    eppEvt.setLettrePm(pieceJointe);
                    break;
                case CURRICULUM_VITAE:
                    eppEvt.setCurriculumVitae(pieceJointe);
                    break;
                }
            }
        }

    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_32);
        eppEvtContainerResponse.setEvt32(eppEvt);
    }

}
