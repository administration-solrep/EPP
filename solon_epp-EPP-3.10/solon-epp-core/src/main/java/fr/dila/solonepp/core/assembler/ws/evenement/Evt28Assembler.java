package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt28;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt28}
 * 
 * @author asatre
 * 
 */
public class Evt28Assembler extends BaseAssembler {

    private final EppEvt28 eppEvt;

    public Evt28Assembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);

        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt28();
        } else {
            eppEvt = new EppEvt28();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {
        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        version.setTitre(eppEvt.getTitre());
        version.setNor(eppEvt.getNor());
        if (eppEvt.getDatePromulgation() != null) {
            version.setDatePromulgation(eppEvt.getDatePromulgation().toGregorianCalendar());
        }
        if (eppEvt.getDatePublication() != null) {
            version.setDatePublication(eppEvt.getDatePublication().toGregorianCalendar());
        }
        if (eppEvt.getNumeroLoi() != null) {
            version.setNumeroLoi(eppEvt.getNumeroLoi());
        }
        version.setNumeroJo((long) eppEvt.getNumeroJo());
        version.setPageJo((long) eppEvt.getPageJo());
        version.setRectificatif(eppEvt.isRectificatif());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_28;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) throws ClientException {
        // Renseigne les données de l'événement
        if (principal.isInstitutionSenat()) {
            eppEvt.setIdSenat(version.getSenat());
        }
        eppEvt.setTitre(version.getTitre());
        eppEvt.setNor(version.getNor());
        if (version.getDatePromulgation() != null) {
            eppEvt.setDatePromulgation(DateUtil.calendarToXMLGregorianCalendar(version.getDatePromulgation()));
        }
        if (version.getDatePublication() != null) {
            eppEvt.setDatePublication(DateUtil.calendarToXMLGregorianCalendar(version.getDatePublication()));
        }
        if (version.getNumeroLoi() != null) {
            eppEvt.setNumeroLoi(version.getNumeroLoi());
        }
        if (version.getNumeroJo() != null) {
            eppEvt.setNumeroJo(version.getNumeroJo().intValue());
        }
        if (version.getPageJo() != null) {
            eppEvt.setPageJo(version.getPageJo().intValue());
        }
        eppEvt.setRectificatif(version.isRectificatif());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_28);
        eppEvtContainerResponse.setEvt28(eppEvt);
    }

}
