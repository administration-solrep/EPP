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
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt09;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt09}
 * 
 * @author asatre
 * 
 */
public class Evt09Assembler extends BaseAssembler {

    private final EppEvt09 eppEvt;

    public Evt09Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt09();
        } else {
            eppEvt = new EppEvt09();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        version.setAuteur(getMandatIdentifiant(eppEvt.getAuteur()));
        version.setCoauteur(getMandatsIdentifiant(eppEvt.getCoAuteur()));
        version.setIntitule(eppEvt.getIntitule());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
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
        return EvenementType.EVT_09;
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
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {
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
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_09);
        eppEvtContainerResponse.setEvt09(eppEvt);
    }

}
