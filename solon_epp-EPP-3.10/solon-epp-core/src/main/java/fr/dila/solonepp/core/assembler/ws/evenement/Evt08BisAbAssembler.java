package fr.dila.solonepp.core.assembler.ws.evenement;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt08BisAB;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * {@link Assembler} pour {@link EppEvt08BisAB}
 * 
 * @author asatre
 * 
 */
public class Evt08BisAbAssembler extends BaseAssembler {

    private final EppEvt08BisAB eppEvt;

    public Evt08BisAbAssembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final EppPrincipal principal) {
        super(session, principal);

        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt08BisAB();
        } else {
            eppEvt = new EppEvt08BisAB();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) throws ClientException {

        // Assemble les métadonnées de l'événement
        if (principal.isInstitutionSenat()) {
            version.setSenat(eppEvt.getIdSenat());
        }
        assembleNiveauLectureXsdToVersion(eppEvt.getNiveauLecture(), version);
        version.setIntitule(eppEvt.getIntitule());
        final XMLGregorianCalendar dateRetrait = eppEvt.getDateRetrait();
        if (dateRetrait != null) {
            version.setDateRetrait(dateRetrait.toGregorianCalendar());
        }
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes

    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_08_BIS_AB;
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
        eppEvt.setNiveauLecture(assembleNiveauLectureVersionToXsd(version));
        final Calendar dateRetrait = version.getDateRetrait();
        if (dateRetrait != null) {
            eppEvt.setDateRetrait(DateUtil.calendarToXMLGregorianCalendar(dateRetrait));
        }
        eppEvt.setIntitule(version.getIntitule());

    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) throws ClientException {

    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_08_BIS_AB);
        eppEvtContainerResponse.setEvt08BisAB(eppEvt);

    }

}
