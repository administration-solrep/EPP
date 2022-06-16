package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppAUD02;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class Aud02Assembler extends BaseAssembler {
    private final EppAUD02 eppEvt;

    public Aud02Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getAUD02();
        } else {
            eppEvt = new EppAUD02();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) {
        this.doVerifyCopy();

        if (eppEvt.getDateAudition() != null) {
            version.setDateAudition(eppEvt.getDateAudition().toGregorianCalendar());
        }

        final List<String> commissions = new ArrayList<String>();
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        for (final Organisme commission : eppEvt.getCommission()) {
            if (commission != null) {
                final boolean exist = tableReferenceService.hasOrganisme(session, commission.getId());
                if (exist) {
                    commissions.add(commission.getId());
                } else {
                    throw new NuxeoException("Commission saisie, organisme inexistant: " + commission.getId());
                }
            }
        }
        version.setCommissions(commissions);
    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {}

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(Evenement evenement, Version version) {
        if (version.getDateAudition() != null) {
            eppEvt.setDateAudition(DateUtil.calendarToXMLGregorianCalendar(version.getDateAudition()));
        }

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
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) {}

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainer) {
        eppEvtContainer.setType(getEvenementType());
        eppEvtContainer.setAUD02(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.AUD_02;
    }
}
