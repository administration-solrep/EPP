package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.SortAdoptionAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt43Bis;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt43Bis}
 *
 * @author asatre
 *
 */
public class Evt43BisAssembler extends BaseAssembler {
    private final EppEvt43Bis eppEvt;

    public Evt43BisAssembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt43Bis();
        } else {
            eppEvt = new EppEvt43Bis();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        version.setIntitule(eppEvt.getIntitule());
        version.setNumeroTexteAdopte(eppEvt.getNumeroTexteAdopte());
        if (eppEvt.getDateAdoption() != null) {
            version.setDateAdoption(eppEvt.getDateAdoption().toGregorianCalendar());
        }
        version.setSortAdoption(SortAdoptionAssembler.assembleXsdToSortAdoption(eppEvt.getSortAdoption()));
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getTexteAdopte() != null) {
            pieceJointeList.add(eppEvt.getTexteAdopte());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_43_BIS;
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
        eppEvt.setIntitule(version.getIntitule());
        eppEvt.setNumeroTexteAdopte(version.getNumeroTexteAdopte());
        if (version.getDateAdoption() != null) {
            eppEvt.setDateAdoption(DateUtil.calendarToXMLGregorianCalendar(version.getDateAdoption()));
        }
        eppEvt.setSortAdoption(SortAdoptionAssembler.assembleSortAdoptionToXsd(version.getSortAdoption()));
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case TEXTE_ADOPTE:
                        eppEvt.setTexteAdopte(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_43_BIS);
        eppEvtContainerResponse.setEvt43Bis(eppEvt);
    }
}
