package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvt45;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * {@link Assembler} pour {@link EppEvt45}
 *
 * @author asatre
 *
 */
public class Evt45Assembler extends BaseAssembler {
    private final EppEvt45 eppEvt;

    public Evt45Assembler(
        final EppEvtContainer eppEvtContainer,
        final CoreSession session,
        final EppPrincipal principal
    ) {
        super(session, principal);
        if (eppEvtContainer != null) {
            eppEvt = eppEvtContainer.getEvt45();
        } else {
            eppEvt = new EppEvt45();
        }
    }

    @Override
    public void buildObject(final Evenement evenement, final Version version) {
        if (eppEvt.getDatePublicationSouhaite() != null) {
            version.setDateJo(eppEvt.getDatePublicationSouhaite().toGregorianCalendar());
        }
        final SolonEppVocabularyService eppVocabularyService = SolonEppServiceLocator.getSolonEppVocabularyService();
        String rubriqueId = eppVocabularyService.getRubriqueIdForLabel(eppEvt.getRubrique());
        if (rubriqueId != null && !rubriqueId.isEmpty()) {
            version.setRubrique(rubriqueId);
        }
        version.setCommentaire(eppEvt.getCommentaire());
    }

    @Override
    public void buildPieceJointe(final List<PieceJointe> pieceJointeList) {
        // Assemble les pièces jointes
        if (eppEvt.getInsertionJoLD() != null) {
            pieceJointeList.add(eppEvt.getInsertionJoLD());
        }
    }

    @Override
    public EvenementType getEvenementType() {
        return EvenementType.EVT_45;
    }

    @Override
    public EppBaseEvenement getEppBaseEvenement() {
        return eppEvt;
    }

    @Override
    public void buildXsd(final Evenement evenement, final Version version) {
        if (version.getDateJo() != null) {
            eppEvt.setDatePublicationSouhaite(DateUtil.calendarToXMLGregorianCalendar(version.getDateJo()));
        }
        final SolonEppVocabularyService eppVocabularyService = SolonEppServiceLocator.getSolonEppVocabularyService();
        eppEvt.setRubrique(
            eppVocabularyService.getLabelFromId(
                SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY,
                version.getRubrique(),
                STVocabularyConstants.COLUMN_LABEL
            )
        );
        eppEvt.setCommentaire(version.getCommentaire());
    }

    @Override
    public void buildPieceJointeXsd(final List<DocumentModel> pieceJointeDocList, final boolean addPjContent) {
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                    case INSERTION_JOLD:
                        eppEvt.setInsertionJoLD(pieceJointe);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void setEvtInContainer(final EppEvtContainer eppEvtContainerResponse) {
        eppEvtContainerResponse.setType(EvenementType.EVT_45);
        eppEvtContainerResponse.setEvt45(eppEvt);
    }
}
