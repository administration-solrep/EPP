package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt51;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt51}
 *
 * @author asatre
 *
 */
public class Evt51Assembler extends BaseAssembler {
    private final EppEvt51 eppEvt;

    public Evt51Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt51();
        } else {
            eppEvt = new EppEvt51();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        version.setBaseLegale(eppEvt.getBaseLegale());
        version.setIdentifiantMetier(eppEvt.getIdentifiantMetier());
        version.setParlementaireTitulaireList(getMandatsIdentifiant(eppEvt.getTitulaires()));
        version.setParlementaireSuppleantList(getMandatsIdentifiant(eppEvt.getSuppleant()));
        if (eppEvt.getDateJo() != null) {
            version.setDateJo(eppEvt.getDateJo().toGregorianCalendar());
        }
        if (eppEvt.getDateDesignation() != null) {
            version.setDateDesignation(eppEvt.getDateDesignation().toGregorianCalendar());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getLettre() != null) {
            pieceJointeList.add(eppEvt.getLettre());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_51;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        eppEvt.setBaseLegale(version.getBaseLegale());
        eppEvt.setIdentifiantMetier(version.getIdentifiantMetier());
        if (version.getParlementaireTitulaireList() != null) {
            for (final String titulaire : version.getParlementaireTitulaireList()) {
                final DocumentModel mandatDoc = tableReferenceService.getMandatById(session, titulaire);
                if (mandatDoc != null) {
                    eppEvt.getTitulaires().add(TableReferenceAssembler.toMandatXsd(session, mandatDoc));
                }
            }
        }
        if (version.getParlementaireSuppleantList() != null) {
            for (final String suppleant : version.getParlementaireSuppleantList()) {
                final DocumentModel mandatDoc = tableReferenceService.getMandatById(session, suppleant);
                if (mandatDoc != null) {
                    eppEvt.getSuppleant().add(TableReferenceAssembler.toMandatXsd(session, mandatDoc));
                }
            }
        }
        if (version.getDateJo() != null) {
            eppEvt.setDateJo(DateUtil.calendarToXMLGregorianCalendar(version.getDateJo()));
        }
        if (version.getDateDesignation() != null) {
            eppEvt.setDateDesignation(DateUtil.calendarToXMLGregorianCalendar(version.getDateDesignation()));
        }
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
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
        eppEvtContainerResponse.setType(EvenementType.EVT_51);
        eppEvtContainerResponse.setEvt51(eppEvt);
    }
}
