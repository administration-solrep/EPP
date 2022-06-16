package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppSD01;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class Sd01Assembler extends BaseAssembler {
    private final EppSD01 eppEvt;

    public Sd01Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getSD01();
        } else {
            eppEvt = new EppSD01();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) {
        if (eppEvt.getDateDemande() != null) {
            version.setDateDemande(eppEvt.getDateDemande().toGregorianCalendar());
        }

        final List<String> parlementaireList = new ArrayList<String>();
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        for (final Organisme groupeParlementaire : eppEvt.getGroupeParlementaire()) {
            if (groupeParlementaire != null) {
                final boolean exist = tableReferenceService.hasOrganisme(session, groupeParlementaire.getId());
                if (exist) {
                    parlementaireList.add(groupeParlementaire.getId());
                } else {
                    throw new NuxeoException("Commission saisie, organisme inexistant: " + groupeParlementaire.getId());
                }
            }
        }
        version.setGroupeParlementaire(parlementaireList);
    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getDemandeDeclaration() != null) {
            pieceJointeList.add(eppEvt.getDemandeDeclaration());
        }
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) {
        if (version.getDateDemande() != null) {
            eppEvt.setDateDemande(DateUtil.calendarToXMLGregorianCalendar(version.getDateDemande()));
        }

        if (version.getGroupeParlementaire() != null) {
            for (final String organismeId : version.getGroupeParlementaire()) {
                final Organisme organisme = assembleCommission(organismeId);
                if (organisme != null) {
                    eppEvt.getGroupeParlementaire().add(organisme);
                }
            }
        }
    }

    @Override
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) {
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case DEMANDE_DECLARATION:
                        eppEvt.setDemandeDeclaration(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.SD_01);
        eppEvtContainerResponse.setSD01(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.SD_01;
    }
}
