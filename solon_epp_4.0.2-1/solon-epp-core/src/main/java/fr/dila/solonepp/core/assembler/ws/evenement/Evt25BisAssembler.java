package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt25;
import fr.sword.xsd.solon.epp.EppEvt25Bis;
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
public class Evt25BisAssembler extends BaseAssembler {
    private final EppEvt25Bis eppEvt;

    public Evt25BisAssembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt25Bis();
        } else {
            eppEvt = new EppEvt25Bis();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
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

        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        version.setIntitule(eppEvt.getIntitule());
        version.setNor(eppEvt.getNor());
        assembleDepotTexteXsdToVersion(eppEvt.getDepot(), version);
        assembleCommissionXsdToVersion(eppEvt.getCommission(), version);
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getTexte() != null) {
            pieceJointeList.add(eppEvt.getTexte());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_25_BIS;
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
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
        eppEvt.setUrlDossierAn(version.getUrlDossierAn());
        eppEvt.setUrlDossierSenat(version.getUrlDossierSenat());
        eppEvt.setIntitule(version.getIntitule());
        eppEvt.setDepot(assembleDepotTexteVersionToXsd(version));
        eppEvt.setCommission(assembleCommissionVersionToXsd(version));
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
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_25_BIS);
        eppEvtContainerResponse.setEvt25Bis(eppEvt);
    }
}
