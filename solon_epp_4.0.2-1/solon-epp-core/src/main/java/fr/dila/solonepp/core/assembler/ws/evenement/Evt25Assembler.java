package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt25;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt25}
 *
 * @author asatre
 *
 */
public class Evt25Assembler extends BaseAssembler {
    private final EppEvt25 eppEvt;

    public Evt25Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt25();
        } else {
            eppEvt = new EppEvt25();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        version.setIntitule(eppEvt.getIntitule());
        version.setAuteur(getMandatIdentifiant(eppEvt.getAuteur()));
        version.setCoauteur(getMandatsIdentifiant(eppEvt.getCoAuteur()));
        version.setNor(eppEvt.getNor());
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
        if (eppEvt.getDecretPresidentRepublique() != null) {
            pieceJointeList.add(eppEvt.getDecretPresidentRepublique());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_25;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

        // Renseigne les données de l'événement
        if (principal.isInstitutionSenat()) {
            eppEvt.setIdSenat(version.getSenat());
        }
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
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
        eppEvt.setNor(version.getNor());
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
                    case DECRET_PRESIDENT_REPUBLIQUE:
                        eppEvt.setDecretPresidentRepublique(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_25);
        eppEvtContainerResponse.setEvt25(eppEvt);
    }
}
