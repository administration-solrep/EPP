package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.NatureLoiAssembler;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.assembler.ws.TypeLoiAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt13Ter;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt13Ter}
 * 
 * @author asatre
 * 
 */
public class Evt13TerAssembler extends BaseAssembler {

    private final EppEvt13Ter eppEvt;

    public Evt13TerAssembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt13Ter();
        } else {
            eppEvt = new EppEvt13Ter();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        version.setNatureLoi(NatureLoiAssembler.assembleXsdToNatureLoi(eppEvt.getNatureLoi()));
        version.setTypeLoi(TypeLoiAssembler.assembleXsdToTypeLoi(eppEvt.getTypeLoi()));
        version.setAuteur(getMandatIdentifiant(eppEvt.getAuteur()));
        version.setCoauteur(getMandatsIdentifiant(eppEvt.getCoAuteur()));
        version.setIntitule(eppEvt.getIntitule());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getTexteTransmis() != null) {
            pieceJointeList.add(eppEvt.getTexteTransmis());
        }
        if (eppEvt.getLettre() != null) {
            pieceJointeList.add(eppEvt.getLettre());
        }
        if (eppEvt.getTravauxPreparatoires() != null) {
            pieceJointeList.add(eppEvt.getTravauxPreparatoires());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_13_TER;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        // Renseigne les données de l'événement
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        if (principal.isInstitutionSenat()) {
            eppEvt.setIdSenat(version.getSenat());
        }
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
        eppEvt.setNatureLoi(NatureLoiAssembler.assembleNatureLoiToXsd(version.getNatureLoi()));
        eppEvt.setTypeLoi(TypeLoiAssembler.assembleTypeLoiToXsd(version.getTypeLoi()));
        eppEvt.setIntitule(version.getIntitule());
        if (version.getAuteur() != null) {
            final DocumentModel mandatDoc = tableReferenceService.getMandatById(session, version.getAuteur());
            if (mandatDoc != null) {
                eppEvt.setAuteur(TableReferenceAssembler.toMandatXsd(session, mandatDoc));
            }
        }
        if (version.getCoauteur() != null) {
            for (final String coAuteur : version.getCoauteur()) {
                final DocumentModel mandatDoc = tableReferenceService.getMandatById(session, coAuteur);
                if (mandatDoc != null) {
                    eppEvt.getCoAuteur().add(TableReferenceAssembler.toMandatXsd(session, mandatDoc));
                }
            }
        }

    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case TEXTE_TRANSMIS:
                    eppEvt.setTexteTransmis(pieceJointe);
                    break;
                case LETTRE:
                    eppEvt.setLettre(pieceJointe);
                    break;
                case TRAVAUX_PREPARATOIRES:
                    eppEvt.setTravauxPreparatoires(pieceJointe);
                    break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_13_TER);
        eppEvtContainerResponse.setEvt13Ter(eppEvt);

    }

}
