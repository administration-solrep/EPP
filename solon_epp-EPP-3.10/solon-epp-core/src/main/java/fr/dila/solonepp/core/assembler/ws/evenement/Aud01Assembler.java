package fr.dila.solonepp.core.assembler.ws.evenement;

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
import fr.sword.xsd.solon.epp.EppAUD01;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.PieceJointe;

public class Aud01Assembler extends BaseAssembler {

    private final EppAUD01 eppEvt;

    public Aud01Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getAUD01();
        } else {
            eppEvt = new EppAUD01();
        }
    }

    @Override
    public void buildObject(Evenement evenement, Version version) throws ClientException {
        this.doVerifyCopy();
        if (eppEvt.getDateLettrePm() != null) {
            version.setDateLettrePm(eppEvt.getDateLettrePm().toGregorianCalendar());
        }

        Organisme organisme = eppEvt.getOrganisme();
        if (organisme != null) {
            final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
            final boolean exist = tableReferenceService.hasOrganisme(session, organisme.getId());
            if (exist) {
                version.setOrganisme(organisme.getId());
            } else {
                throw new ClientException("Organisme saisie, organisme inexistant: " + organisme.getId());
            }

        }

        version.setFonction(eppEvt.getFonction());
        version.setBaseLegale(eppEvt.getBaseLegale());
        version.setPersonne(eppEvt.getPersonne());

    }

    @Override
    public void buildPieceJointe(List<PieceJointe> pieceJointeList) {
        if (eppEvt.getLettrePm() != null) {
            pieceJointeList.add(eppEvt.getLettrePm());
        }
        if (eppEvt.getCurriculumVitae() != null) {
            pieceJointeList.addAll(eppEvt.getCurriculumVitae());
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

        if (version.getOrganisme() != null) {

            final Organisme organisme = assembleCommission(version.getOrganisme());
            if (organisme != null) {
                eppEvt.setOrganisme(organisme);
            }

        }

        eppEvt.setFonction(version.getFonction());
        eppEvt.setPersonne(version.getPersonne());
        eppEvt.setBaseLegale(version.getBaseLegale());

    }

    @Override
    public void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent) throws ClientException {

        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case LETTRE_PM:
                    eppEvt.setLettrePm(pieceJointe);
                    break;

                case CURRICULUM_VITAE:
                    eppEvt.getCurriculumVitae().add(pieceJointe);
                }
            }
        }

    }

    @Override
    public void setEvtInContainer(EppEvtContainer eppEvtContainer) {
        eppEvtContainer.setType(getEvenementType());
        eppEvtContainer.setAUD01(eppEvt);
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.AUD_01;
    }

}
