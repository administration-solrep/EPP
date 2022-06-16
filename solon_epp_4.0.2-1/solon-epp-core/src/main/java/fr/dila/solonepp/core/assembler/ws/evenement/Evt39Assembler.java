package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt39;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt39}
 *
 * @author asatre
 *
 */
public class Evt39Assembler extends BaseAssembler {
    private final EppEvt39 eppEvt;

    public Evt39Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt39();
        } else {
            eppEvt = new EppEvt39();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        if (eppEvt.getCopie() != null && eppEvt.getCopie().size() > 0) {
            throw new EppNuxeoException("Cette communication ne peut pas contenir des copies");
        }

        version.setSenat(eppEvt.getIdSenat());

        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setUrlDossierSenat(eppEvt.getUrlDossierSenat());
        } else {
            if (eppEvt.getUrlDossierSenat() != null) {
                throw new EppNuxeoException("l'URL du dossier du Sénat ne peut etre modifé que par le Sénat");
            }
        }

        if (principal.isInstitutionAn()) {
            version.setUrlDossierAn(eppEvt.getUrlDossierAn());
        } else {
            if (eppEvt.getUrlDossierAn() != null) {
                throw new EppNuxeoException("l'URL du dossier de l'AN ne peut etre modifé que par l'AN ");
            }
        }
        version.setAuteur(getMandatIdentifiant(eppEvt.getAuteur()));
        if (eppEvt.getCoAuteur() != null) {
            version.setCoauteur(getMandatsIdentifiant(eppEvt.getCoAuteur()));
        }
        version.setCosignataire(eppEvt.getCoSignataireCollectif());
        version.setIntitule(eppEvt.getIntitule());
        assembleDepotTexteXsdToVersion(eppEvt.getDepot(), version);
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getLettre() != null) {
            pieceJointeList.add(eppEvt.getLettre());
        }
        if (eppEvt.getTextes() != null) {
            pieceJointeList.add(eppEvt.getTextes());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_39;
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

        eppEvt.setUrlDossierAn(version.getUrlDossierAn());
        eppEvt.setUrlDossierSenat(version.getUrlDossierSenat());
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
        eppEvt.setCoSignataireCollectif(version.getCosignataire());
        eppEvt.setIntitule(version.getIntitule());
        eppEvt.setDepot(assembleDepotTexteVersionToXsd(version));
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case TEXTE:
                        eppEvt.setTextes(pieceJointe);
                        break;
                    case LETTRE:
                        eppEvt.setLettre(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_39);
        eppEvtContainerResponse.setEvt39(eppEvt);
    }
}
