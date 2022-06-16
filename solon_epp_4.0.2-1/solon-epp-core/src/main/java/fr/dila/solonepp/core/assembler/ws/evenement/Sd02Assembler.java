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
import fr.sword.xsd.solon.epp.EppSD02;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class Sd02Assembler extends BaseAssembler {
    private final EppSD02 eppEvt;

    public Sd02Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getSD02();
        } else {
            eppEvt = new EppSD02();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) {
        if (eppEvt.getDateDeclaration() != null) {
            version.setDateDeclaration(eppEvt.getDateDeclaration().toGregorianCalendar());
        }

        if (eppEvt.getDateLettrePm() != null) {
            version.setDateLettrePm(eppEvt.getDateLettrePm().toGregorianCalendar());
        }

        version.setDemandeVote(eppEvt.isDemandeVote());

        final List<String> groupeParlementaire = new ArrayList<String>();
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        for (final Organisme organisme : eppEvt.getGroupeParlementaire()) {
            if (organisme != null) {
                final boolean exist = tableReferenceService.hasOrganisme(session, organisme.getId());
                if (exist) {
                    groupeParlementaire.add(organisme.getId());
                } else {
                    throw new NuxeoException("Commission saisie, organisme inexistant: " + organisme.getId());
                }
            }
        }
        version.setGroupeParlementaire(groupeParlementaire);
    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) {
        if (version.getDateDeclaration() != null) {
            eppEvt.setDateDeclaration(DateUtil.calendarToXMLGregorianCalendar(version.getDateDeclaration()));
        }

        if (version.getDateLettrePm() != null) {
            eppEvt.setDateLettrePm(DateUtil.calendarToXMLGregorianCalendar(version.getDateLettrePm()));
        }

        if (version.getGroupeParlementaire() != null) {
            for (final String organismeId : version.getGroupeParlementaire()) {
                if (organismeId != null) {
                    final Organisme organisme = assembleCommission(organismeId);
                    if (organisme != null) {
                        eppEvt.getGroupeParlementaire().add(organisme);
                    }
                }
            }
        }

        eppEvt.setDemandeVote(version.getDemandeVote());
    }

    @Override
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) {
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case LETTRE_PM:
                        eppEvt.setLettrePm(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.SD_02);
        eppEvtContainerResponse.setSD02(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.SD_02;
    }
}
