package fr.dila.ss.core.operation.distribution;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STOperationConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

/**
 * Remplace la classe CreateCaseLinkOperation de CaseManagement.
 * Le caseLink n'est pas créé pour une étape de type pour information et on passe à la suivante.
 *
 * @author FEO
 */
@Operation(
    id = STCreateCaseLinkOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "ST Case Link creation",
    description = "Create a CaseLink to be used latter in the chain."
)
public class STCreateCaseLinkOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "ST.Distribution.CreateCaseLink";

    @Context
    protected OperationContext context;

    /**
     * Default constructor
     */
    public STCreateCaseLinkOperation() {
        // do nothing
    }

    @OperationMethod
    public DocumentModelList createCaseLink(DocumentModelList docs) {
        final CaseManagementDocumentTypeService typeService = STServiceLocator.getCaseManagementDocumentTypeService();
        final SSFeuilleRouteService feuilleRouteService = SSServiceLocator.getSSFeuilleRouteService();

        final CoreSession session = context.getCoreSession();

        // Récupère l'étape en cours
        FeuilleRouteStep step = (FeuilleRouteStep) context.get(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY);
        SSRouteStep routeStep = step.getDocument().getAdapter(SSRouteStep.class);

        // Renseigne la date de debut de l'étape
        routeStep.setDateDebutEtape(GregorianCalendar.getInstance());

        // Crée les documents DossierLink (pour les passer aux opérations suivantes, pas encore sauvegardés)
        final List<STDossierLink> links = new ArrayList<>();
        if (feuilleRouteService.canDistributeStep(session, routeStep, docs)) {
            for (DocumentModel doc : docs) {
                DocumentModel model = session.createDocumentModel(typeService.getCaseLinkType());
                model.setPropertyValue(CaseLinkConstants.CASE_DOCUMENT_ID_FIELD, doc.getId());
                STDossierLink cl = model.getAdapter(STDossierLink.class);
                links.add(cl);
            }
        }
        routeStep.save(session);

        context.put(STOperationConstant.OPERATION_CASES_KEY, docs);
        context.put(CaseConstants.OPERATION_CASE_LINKS_KEY, links);
        return docs;
    }

    @OperationMethod
    public DocumentModel createCaseLink(DocumentModel doc) {
        DocumentModelList list = new DocumentModelListImpl();
        list.add(doc);
        return createCaseLink(list).get(0);
    }
}
