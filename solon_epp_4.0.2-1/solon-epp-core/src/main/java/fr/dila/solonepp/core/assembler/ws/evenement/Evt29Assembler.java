package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.TypeActeAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt29;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt29}
 *
 * @author asatre
 *
 */
public class Evt29Assembler extends BaseAssembler {
    private final EppEvt29 eppEvt;

    public Evt29Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt29();
        } else {
            eppEvt = new EppEvt29();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        version.setTypeActe(TypeActeAssembler.assembleXsdToTypeActe(eppEvt.getTypeActe()));
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
        if (eppEvt.getAmpliationDecret() != null) {
            pieceJointeList.add(eppEvt.getAmpliationDecret());
        }
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_29;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        eppEvt.setTypeActe(TypeActeAssembler.assembleTypeActeToXsd(version.getTypeActe()));
        if (version.getDateActe() != null) {
            eppEvt.setDateActe(DateUtil.calendarToXMLGregorianCalendar(version.getDateActe()));
        }
        if (version.getNumeroJo() != null) {
            eppEvt.setNumeroJo(version.getNumeroJo().intValue());
        }
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
                    case AMPLIATION_DECRET:
                        eppEvt.setAmpliationDecret(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_29);
        eppEvtContainerResponse.setEvt29(eppEvt);
    }
}
