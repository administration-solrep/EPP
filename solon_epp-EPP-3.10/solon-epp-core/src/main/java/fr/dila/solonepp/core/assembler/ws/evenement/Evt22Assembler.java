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
import fr.dila.solonepp.core.assembler.ws.ResultatCmpAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt22;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt22}
 * 
 * @author asatre
 * 
 */
public class Evt22Assembler extends BaseAssembler {

    private final EppEvt22 eppEvt;

    public Evt22Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);

        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt22();
        } else {
            eppEvt = new EppEvt22();
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
        assembleDepotTexteXsdToVersion(eppEvt.getDepotTexte(), version);
        version.setNumeroDepotRapport(eppEvt.getNumeroDepotRapport());
        version.setResultatCMP(ResultatCmpAssembler.assembleXsdToResultatCmp(eppEvt.getResultatCmp()));
        version.setRapporteurList(getMandatsIdentifiant(eppEvt.getRapporteur()));
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getTexte() != null) {
            pieceJointeList.add(eppEvt.getTexte());
        }
        if (eppEvt.getRapport() != null) {
            pieceJointeList.add(eppEvt.getRapport());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_22;
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
        eppEvt.setIntitule(version.getIntitule());
        eppEvt.setDepotTexte(assembleDepotTexteVersionToXsd(version));
        eppEvt.setNumeroDepotRapport(version.getNumeroDepotRapport());
        eppEvt.setResultatCmp(ResultatCmpAssembler.assembleResultatCmpToXsd(version.getResultatCMP()));
        if (version.getRapporteurList() != null) {
            for (final String mandatId : version.getRapporteurList()) {
                final DocumentModel mandatDoc = tableReferenceService.getMandatById(session, mandatId);
                if (mandatDoc != null) {
                    final Mandat mandat = TableReferenceAssembler.toMandatXsd(session, mandatDoc);
                    eppEvt.getRapporteur().add(mandat);
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
                case TEXTE:
                    eppEvt.setTexte(pieceJointe);
                    break;
                case RAPPORT:
                    eppEvt.setRapport(pieceJointe);
                    break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_22);
        eppEvtContainerResponse.setEvt22(eppEvt);
    }

}
