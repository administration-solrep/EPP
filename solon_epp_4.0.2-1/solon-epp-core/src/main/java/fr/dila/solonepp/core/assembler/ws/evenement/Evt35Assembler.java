package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt35;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt35}
 *
 * @author asatre
 *
 */
public class Evt35Assembler extends BaseAssembler {
    private final EppEvt35 eppEvt;

    public Evt35Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt35();
        } else {
            eppEvt = new EppEvt35();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        if (eppEvt.getDateCongres() != null) {
            version.setDateCongres(eppEvt.getDateCongres().toGregorianCalendar());
        }
        version.setIntitule(eppEvt.getIntitule());
        if (eppEvt.getDateActe() != null) {
            version.setDateActe(eppEvt.getDateActe().toGregorianCalendar());
        }
        if (eppEvt.getNumeroJo() != null) {
            version.setNumeroJo((long) eppEvt.getNumeroJo());
        }
        if (eppEvt.getAnneeJo() != null) {
            version.setAnneeJo((long) eppEvt.getAnneeJo());
        }
        if (eppEvt.getDateJo() != null) {
            version.setDateJo(eppEvt.getDateJo().toGregorianCalendar());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
        if (eppEvt.getAmpliationDecretPresidentRepublique() != null) {
            pieceJointeList.add(eppEvt.getAmpliationDecretPresidentRepublique());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_35;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        if (version.getDateCongres() != null) {
            eppEvt.setDateCongres(DateUtil.calendarToXMLGregorianCalendar(version.getDateCongres()));
        }
        eppEvt.setIntitule(version.getIntitule());
        if (version.getDateActe() != null) {
            eppEvt.setDateActe(DateUtil.calendarToXMLGregorianCalendar(version.getDateActe()));
        }

        if (version.getNumeroJo() != null) {}
        if (version.getAnneeJo() != null) {
            eppEvt.setAnneeJo(version.getAnneeJo().intValue());
        }

        if (version.getDateJo() != null) {
            eppEvt.setDateJo(DateUtil.calendarToXMLGregorianCalendar(version.getDateJo()));
        }
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case LETTRE_PM:
                        eppEvt.setLettrePm(pieceJointe);
                        break;
                    case AMPLIATION_DECRET_PRESIDENT_REPUBLIQUE:
                        eppEvt.setAmpliationDecretPresidentRepublique(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_35);
        eppEvtContainerResponse.setEvt35(eppEvt);
    }
}
