package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt14Ter;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt14Ter}
 *
 * @author asatre
 *
 */
public class Evt14TerAssembler extends BaseAssembler {
    private final EppEvt14Ter eppEvt;

    public Evt14TerAssembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt14Ter();
        } else {
            eppEvt = new EppEvt14Ter();
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
        assembleDepotTexteXsdToVersion(eppEvt.getDepot(), version);
        assembleCommissionXsdToVersion(eppEvt.getCommission(), version);
        version.setRedepot(eppEvt.isRedepot());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_14_TER;
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
        eppEvt.setRedepot(version.isRedepot());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {}

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_14_TER);
        eppEvtContainerResponse.setEvt14Ter(eppEvt);
    }
}
