package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.exception.EppClientException;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt40;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt40}
 * 
 * @author asatre
 * 
 */
public class Evt40Assembler extends BaseAssembler {

    private final EppEvt40 eppEvt;

    public Evt40Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt40();
        } else {
            eppEvt = new EppEvt40();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        if (eppEvt.getCopie() != null && eppEvt.getCopie().size() > 0) {
            throw new EppClientException("Cette communication ne peut pas contenir des copies");
          }
        
        if (eppEvt.getDateDemande() != null) {
            version.setDateDemande(eppEvt.getDateDemande().toGregorianCalendar());
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
        return EvenementType.EVT_40;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        if (version.getDateDemande() != null) {
            eppEvt.setDateDemande(DateUtil.calendarToXMLGregorianCalendar(version.getDateDemande()));
        }
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case LETTRE:
                    eppEvt.setLettre(pieceJointe);
                    break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_40);
        eppEvtContainerResponse.setEvt40(eppEvt);
    }

}
