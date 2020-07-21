package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppDOC01;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.PieceJointe;

public class Doc01Assembler extends BaseAssembler {

    private final EppDOC01 eppEvt;

    public Doc01Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getDOC01();
        } else {
            eppEvt = new EppDOC01();
        }
    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {
        if (eppEvt.getDocuments() != null) {
            pieceJointeList.addAll(eppEvt.getDocuments());
        }

        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }

    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) throws ClientException {

        if (version.getDateLettrePm() != null) {
            eppEvt.setDateLettrePm(DateUtil.calendarToXMLGregorianCalendar(version.getDateLettrePm()));
        }

        eppEvt.setBaseLegale(version.getBaseLegale());

        if (version.getCommissions() != null) {
            for (final String organismeId : version.getCommissions()) {
                final Organisme organisme = assembleCommission(organismeId);
                if (organisme != null) {
                    eppEvt.getCommission().add(organisme);
                }
            }
        }

    }

    @Override
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) throws ClientException {
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case DOCUMENTS:
                    eppEvt.getDocuments().add(pieceJointe);
                    break;

                case LETTRE_PM:
                    eppEvt.setLettrePm(pieceJointe);
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainer) {
        eppEvtContainer.setType(getEvenementType());
        eppEvtContainer.setDOC01(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.DOC_01;
    }

    @Override
    public void buildObject(Evenement evenement, Version version) throws ClientException {
        this.doVerifyCopy();

        // Date Lettre Pm
        if (eppEvt.getDateLettrePm() != null) {
            version.setDateLettrePm(eppEvt.getDateLettrePm().toGregorianCalendar());
        }

        version.setBaseLegale(eppEvt.getBaseLegale());

        final List<String> commissions = new ArrayList<String>();
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        for (final Organisme organisme : eppEvt.getCommission()) {
            if (organisme != null) {
                final boolean exist = tableReferenceService.hasOrganisme(session, organisme.getId());
                if (exist) {
                    commissions.add(organisme.getId());
                } else {
                    throw new ClientException("Commission saisie, organisme inexistant: " + organisme.getId());
                }

            }
        }
        version.setCommissions(commissions);

    }

}
