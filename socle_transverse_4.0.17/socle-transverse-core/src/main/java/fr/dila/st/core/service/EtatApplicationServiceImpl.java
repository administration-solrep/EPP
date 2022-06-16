package fr.dila.st.core.service;

import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED;
import static fr.dila.st.api.constant.STConstant.ETAT_APPLICATION_DOCUMENT_TYPE;
import static fr.dila.st.api.constant.STSchemaConstant.ETAT_APPLICATION_RESTRICTION_ACCES_TECHNIQUE_XPATH;
import static fr.dila.st.api.constant.STSchemaConstant.ETAT_APPLICATION_RESTRICTION_ACCES_XPATH;
import static fr.dila.st.core.query.QueryHelper.doUfnxqlCountQuery;
import static fr.dila.st.core.util.CoreSessionUtil.getRepo;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STPathConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.security.auth.login.LoginException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Service de gestion de l'état de l'application (restriction d'accès)
 *
 * @author Fabio Esposito
 *
 */
public class EtatApplicationServiceImpl implements EtatApplicationService {
    private static final String GET_ETAT_APP_DOC_QUERY = "Select * from " + ETAT_APPLICATION_DOCUMENT_TYPE;

    private String idEtatApplication = null;

    public static final String ERROR_MSG =
        "Le document état application n'existe pas ou est présent en plus d'un exemplaire";

    /**
     * Default constructor
     */
    public EtatApplicationServiceImpl() {
        // do nothing
    }

    @Override
    public EtatApplication getEtatApplicationDocument(CoreSession session) {
        DocumentModel etatApplication;
        if (idEtatApplication == null) {
            DocumentModelList etatApplicationList = CoreInstance.doPrivileged(
                session,
                privilegedSession -> {
                    return QueryUtils.doQuery(privilegedSession, GET_ETAT_APP_DOC_QUERY, 2, 0);
                }
            );
            if (etatApplicationList.size() != 1) {
                throw new NuxeoException(ERROR_MSG);
            }
            etatApplication = etatApplicationList.get(0);
            idEtatApplication = etatApplication.getId();
        } else {
            etatApplication = session.getDocument(new IdRef(idEtatApplication));
        }
        return etatApplication.getAdapter(EtatApplication.class);
    }

    @Override
    public void createEtatApplication(CoreSession session) {
        if (idEtatApplication != null) {
            return;
        }

        List<String> lstEtatAppliIds = QueryUtils.doQueryForIds(session, GET_ETAT_APP_DOC_QUERY, 1, 0);
        if (!lstEtatAppliIds.isEmpty()) {
            idEtatApplication = lstEtatAppliIds.get(0);
            return;
        }

        DocumentModel doc = session.createDocumentModel(
            getEtatApplicationParentPath(),
            "etat-application",
            ETAT_APPLICATION_DOCUMENT_TYPE
        );
        session.createDocument(doc);
    }

    protected String getEtatApplicationParentPath() {
        return STPathConstant.ADMIN_WORKSPACE_PATH;
    }

    @Override
    public void restrictAccess(CoreSession session, String description) {
        EtatApplication etatApplication = getEtatApplicationDocument(session);

        etatApplication.setRestrictionAcces(true);
        etatApplication.setDescriptionRestriction(description);

        session.saveDocument(etatApplication.getDocument());
    }

    @Override
    public void restoreAccess(CoreSession session) {
        EtatApplication etatApplication = getEtatApplicationDocument(session);

        etatApplication.setRestrictionAcces(false);
        etatApplication.setDescriptionRestriction("");

        session.saveDocument(etatApplication.getDocument());
    }

    @Override
    public void restrictTechnicalAccess(CoreSession session) {
        setTechnicalAccess(session, true);
    }

    @Override
    public void restoreTechnicalAccess(CoreSession session) {
        setTechnicalAccess(session, false);
    }

    private void setTechnicalAccess(CoreSession session, boolean restrictionAcces) {
        TransactionHelper.runInNewTransaction(
            () -> {
                EtatApplication etatApplication = getEtatApplicationDocument(session);
                etatApplication.setRestrictionAccesTechnique(restrictionAcces);
                session.saveDocument(etatApplication.getDocument());
            }
        );
    }

    /**
     * Retourne RestrictionAcces
     *
     * Attention à utiliser que pour la page de login car l'utilisateur n'est
     * pas encore connecté
     *
     * @return
     * @throws LoginException
     */
    @Override
    public Map<String, Object> getRestrictionAccesUnrestricted() {
        return doPrivilegedInNewTransaction(
            session -> {
                final EtatApplication etat = getEtatApplicationDocument(session);

                final Map<String, Object> result = new HashMap<>();
                result.put(RESTRICTION_ACCESS, etat.getRestrictionAcces());
                final String description = etat.getDescriptionRestriction();
                if (description == null) {
                    result.put(RESTRICTION_DESCRIPTION, null);
                } else {
                    result.put(RESTRICTION_DESCRIPTION, description.replace("<script", ""));
                }

                final String message = etat.getMessage();
                if (message == null) {
                    result.put(BANNIERE_INFO, null);
                } else {
                    result.put(BANNIERE_INFO, message.replace("<script", ""));
                }
                result.put(AFFICHAGE_BANNIERE, etat.getAffichage());
                return result;
            }
        );
    }

    @Override
    public boolean isApplicationRestricted(NuxeoPrincipal principal) {
        if (principal != null && (principal.isAdministrator() || principal.isMemberOf(ADMIN_ACCESS_UNRESTRICTED))) {
            return false;
        }
        String queryDocAccessRestricted = String.format(
            "Select d.ecm:uuid AS id from %s AS d WHERE d.%s = 1",
            ETAT_APPLICATION_DOCUMENT_TYPE,
            ETAT_APPLICATION_RESTRICTION_ACCES_XPATH
        );
        return doPrivilegedInNewTransaction(session -> doUfnxqlCountQuery(session, queryDocAccessRestricted) > 0L);
    }

    @Override
    public boolean isApplicationRestricted() {
        return doPrivilegedInNewTransaction(session -> getEtatApplicationDocument(session).getRestrictionAcces());
    }

    @Override
    public void resetCache() {
        idEtatApplication = null;
    }

    @Override
    public boolean isApplicationTechnicallyRestricted() {
        String queryDocAccessTechniqueRestricted = String.format(
            "Select d.ecm:uuid AS id from %s AS d WHERE d.%s = 1",
            ETAT_APPLICATION_DOCUMENT_TYPE,
            ETAT_APPLICATION_RESTRICTION_ACCES_TECHNIQUE_XPATH
        );
        return doPrivilegedInNewTransaction(
            session -> doUfnxqlCountQuery(session, queryDocAccessTechniqueRestricted) > 0L
        );
    }

    private static <T> T doPrivilegedInNewTransaction(Function<CoreSession, T> func) {
        return Framework.doPrivileged(
            () -> TransactionHelper.runInNewTransaction(() -> CoreInstance.doPrivileged(getRepo(), func))
        );
    }
}
