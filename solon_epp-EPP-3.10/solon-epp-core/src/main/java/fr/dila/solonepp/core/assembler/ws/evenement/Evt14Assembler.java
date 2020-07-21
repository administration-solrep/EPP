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
import fr.dila.solonepp.core.assembler.ws.SortAdoptionAssembler;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt14;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt14}
 * 
 * @author asatre
 * 
 */
public class Evt14Assembler extends BaseAssembler {

    private final EppEvt14 eppEvt;

    public Evt14Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt14();
        } else {
            eppEvt = new EppEvt14();
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
        if (eppEvt.getTravauxPreparatoires() != null) {
            pieceJointeList.add(eppEvt.getTravauxPreparatoires());
        }
        if (eppEvt.getCoherent() != null) {
            pieceJointeList.add(eppEvt.getCoherent());
        }
        if (eppEvt.getPetiteLoi() != null) {
            pieceJointeList.add(eppEvt.getPetiteLoi());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_14;
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
        eppEvt.setNumeroTexteAdopte(version.getNumeroTexteAdopte());
        if (version.getDateAdoption() != null) {
            eppEvt.setDateAdoption(DateUtil.calendarToXMLGregorianCalendar(version.getDateAdoption()));
        }
        eppEvt.setSortAdoption(SortAdoptionAssembler.assembleSortAdoptionToXsd(version.getSortAdoption()));
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case TEXTE_ADOPTE:
                    eppEvt.setTexteAdopte(pieceJointe);
                    break;
                case TRAVAUX_PREPARATOIRES:
                    eppEvt.setTravauxPreparatoires(pieceJointe);
                    break;
                case COHERENT:
                    eppEvt.setCoherent(pieceJointe);
                    break;
                case PETITE_LOI:
                    eppEvt.setPetiteLoi(pieceJointe);
                    break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_14);
        eppEvtContainerResponse.setEvt14(eppEvt);

    }

}
