package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt23Quater;
import fr.sword.xsd.solon.epp.EppEvt23Ter;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt23Ter}
 *
 * @author asatre
 *
 */
public class Evt23QuaterAssembler extends BaseAssembler {
    private final EppEvt23Quater eppEvt;

    public Evt23QuaterAssembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt23Quater();
        } else {
            eppEvt = new EppEvt23Quater();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        if (eppEvt.getDate() != null) {
            version.setDate(eppEvt.getDate().toGregorianCalendar());
        }
        version.setIntitule(eppEvt.getIntitule());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getTexte() != null) {
            pieceJointeList.add(eppEvt.getTexte());
        }
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_23_QUATER;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        // Renseigne les données de l'événement
        if (principal.isInstitutionSenat()) {
            eppEvt.setIdSenat(version.getSenat());
        }
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
        if (version.getDate() != null) {
            eppEvt.setDate(DateUtil.calendarToXMLGregorianCalendar(version.getDate()));
        }
        eppEvt.setIntitule(version.getIntitule());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case TEXTE:
                        eppEvt.setTexte(pieceJointe);
                        break;
                    case LETTRE_PM:
                        eppEvt.setLettrePm(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_23_QUATER);
        eppEvtContainerResponse.setEvt23Quater(eppEvt);
    }
}
