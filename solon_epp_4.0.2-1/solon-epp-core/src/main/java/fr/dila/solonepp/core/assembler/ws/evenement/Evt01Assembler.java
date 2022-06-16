package fr.dila.solonepp.core.assembler.ws.evenement;

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
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt01}
 *
 * @author asatre
 *
 */
public class Evt01Assembler extends BaseAssembler {
    private final EppEvt01 eppEvt;

    public Evt01Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt01();
        } else {
            eppEvt = new EppEvt01();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        // Assemble les métadonnées de l'événement
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        version.setNor(eppEvt.getNor());
        version.setNatureLoi(NatureLoiAssembler.assembleXsdToNatureLoi(eppEvt.getNatureLoi()));
        version.setTypeLoi(TypeLoiAssembler.assembleXsdToTypeLoi(eppEvt.getTypeLoi()));
        version.setAuteur(getMandatIdentifiant(eppEvt.getAuteur()));
        version.setCoauteur(getMandatsIdentifiant(eppEvt.getCoAuteur()));
        version.setIntitule(eppEvt.getIntitule());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        if (eppEvt.getTexte() != null) {
            pieceJointeList.add(eppEvt.getTexte());
        }
        if (eppEvt.getExposeDesMotifs() != null) {
            pieceJointeList.add(eppEvt.getExposeDesMotifs());
        }
        if (eppEvt.getEtudeImpact() != null) {
            pieceJointeList.add(eppEvt.getEtudeImpact());
        }
        if (eppEvt.getDecretPresentation() != null) {
            pieceJointeList.add(eppEvt.getDecretPresentation());
        }
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
        if (eppEvt.getTraite() != null) {
            pieceJointeList.add(eppEvt.getTraite());
        }
        if (eppEvt.getAccordInternational() != null) {
            pieceJointeList.add(eppEvt.getAccordInternational());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_01;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

        // Renseigne les données de l'événement
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
        eppEvt.setNor(version.getNor());
        eppEvt.setNatureLoi(NatureLoiAssembler.assembleNatureLoiToXsd(version.getNatureLoi()));
        eppEvt.setTypeLoi(TypeLoiAssembler.assembleTypeLoiToXsd(version.getTypeLoi()));
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
                    case EXPOSE_DES_MOTIFS:
                        eppEvt.setExposeDesMotifs(pieceJointe);
                        break;
                    case ETUDE_IMPACT:
                        eppEvt.setEtudeImpact(pieceJointe);
                        break;
                    case DECRET_PRESENTATION:
                        eppEvt.setDecretPresentation(pieceJointe);
                        break;
                    case LETTRE_PM:
                        eppEvt.setLettrePm(pieceJointe);
                        break;
                    case TRAITE:
                        eppEvt.setTraite(pieceJointe);
                        break;
                    case ACCORD_INTERNATIONAL:
                        eppEvt.setAccordInternational(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainer) {
        eppEvtContainer.setType(getEvenementType());
        eppEvtContainer.setEvt01(eppEvt);
    }
}