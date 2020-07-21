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
import fr.sword.xsd.solon.epp.DecisionProcedureAcc;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppLex40;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppLex40}
 * 
 * @author asatre
 * 
 */
public class Lex40Assembler extends BaseAssembler {

    private final EppLex40 eppEvt;

    public Lex40Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getLex40();
        } else {
            eppEvt = new EppLex40();
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
        version.setObjet(eppEvt.getObjet());
        version.setDateRefusASsemblee1(DateUtil.xmlGregorianCalendarToCalendar(eppEvt.getDateRefusAssemblee1()));
        if (eppEvt.getDateConferenceAssemblee2() != null) {
            version.setDateConferencePresidentsAssemblee2(DateUtil.xmlGregorianCalendarToCalendar(eppEvt.getDateConferenceAssemblee2()));
        }
        if (eppEvt.getDecisionProcAcc() != null) {
            version.setDecisionProcAcc(eppEvt.getDecisionProcAcc().toString());
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
        return EvenementType.LEX_40;
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
        eppEvt.setObjet(version.getObjet());
        eppEvt.setDateRefusAssemblee1(DateUtil.calendarToXMLGregorianCalendar(version.getDateRefusAssemblee1()));
        if (version.getDateConferencePresidentsAssemblee2() != null) {
            eppEvt.setDateConferenceAssemblee2(DateUtil.calendarToXMLGregorianCalendar(version.getDateConferencePresidentsAssemblee2()));
        }
        if (version.getDecisionProcAcc() != null) {
            eppEvt.setDecisionProcAcc(DecisionProcedureAcc.valueOf(version.getDecisionProcAcc()));
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
        eppEvtContainerResponse.setType(EvenementType.LEX_40);
        eppEvtContainerResponse.setLex40(eppEvt);
    }

}
