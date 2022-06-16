package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.SortAdoptionAssembler;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt43;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt43}
 *
 * @author asatre
 *
 */
public class Evt43Assembler extends BaseAssembler {
    private final EppEvt43 eppEvt;

    public Evt43Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt43();
        } else {
            eppEvt = new EppEvt43();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        if (eppEvt.getCopie() != null && eppEvt.getCopie().size() > 0) {
            throw new EppNuxeoException("Cette communication ne peut pas contenir des copies");
        }

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
        if (eppEvt.getPetiteLoi() != null) {
            pieceJointeList.add(eppEvt.getPetiteLoi());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_43;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) { // Renseigne les données de l'événement
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
                    case PETITE_LOI:
                        eppEvt.setPetiteLoi(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_43);
        eppEvtContainerResponse.setEvt43(eppEvt);
    }
}
