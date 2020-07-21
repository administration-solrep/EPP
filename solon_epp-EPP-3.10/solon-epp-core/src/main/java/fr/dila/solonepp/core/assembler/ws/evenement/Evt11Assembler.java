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
import fr.sword.xsd.solon.epp.EppEvt11;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt11}
 * 
 * @author asatre
 * 
 */
public class Evt11Assembler extends BaseAssembler {

    private final EppEvt11 eppEvt;

    public Evt11Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt11();
        } else {
            eppEvt = new EppEvt11();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        version.setIntitule(eppEvt.getIntitule());
        if (eppEvt.getDateRefusAn() != null) {
            version.setDateRefusProcedureEngagementAn(eppEvt.getDateRefusAn().toGregorianCalendar());
        }
        if (eppEvt.getDateRefusSenat() != null) {
            version.setDateRefusProcedureEngagementSenat(eppEvt.getDateRefusSenat().toGregorianCalendar());
        }
        if (eppEvt.getDateRefusEngagementProcedure() != null) {
        	version.setDateRefusEngagementProcedure(eppEvt.getDateRefusEngagementProcedure().toGregorianCalendar());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getLettre() != null) {
            pieceJointeList.add(eppEvt.getLettre());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_11;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        // Renseigne les données de l'événement
        if (principal.isInstitutionSenat()) {
            eppEvt.setIdSenat(version.getSenat());
        }
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
        eppEvt.setIntitule(version.getIntitule());
        if (version.getDateRefusProcedureEngagementAn() != null) {
            eppEvt.setDateRefusAn(DateUtil.calendarToXMLGregorianCalendar(version.getDateRefusProcedureEngagementAn()));
        }
        if (version.getDateRefusProcedureEngagementSenat() != null) {
            eppEvt.setDateRefusSenat(DateUtil.calendarToXMLGregorianCalendar(version.getDateRefusProcedureEngagementSenat()));
        }
        if (version.getDateRefusEngagementProcedure() != null) {
        	eppEvt.setDateRefusEngagementProcedure(DateUtil.calendarToXMLGregorianCalendar(version.getDateRefusEngagementProcedure()));
        }

    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case LETTRE:
                    eppEvt.setLettre(pieceJointe);
                    break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_11);
        eppEvtContainerResponse.setEvt11(eppEvt);
    }

}
