package fr.dila.st.core.service;

import static fr.dila.st.core.util.CoreSessionUtil.getRepo;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.cache.ParamRefRelationCache;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

/**
 * Implémentation du service permettant de gérer les paramètres applicatifs administrables dans l'IHM de l'application.
 *
 * @author Fabio Esposito
 */
public class STParametreServiceImpl implements STParametreService {
    private static final String QUERY_PARAMETRE_FOLDER = "SELECT * FROM " + STConstant.PARAMETRE_FOLDER_DOCUMENT_TYPE;

    protected static final String QUERY_PARAMETRE =
        "SELECT p.ecm:uuid AS id FROM " + STConstant.PARAMETRE_DOCUMENT_TYPE + " AS p WHERE p.ecm:name = ?";

    protected ParamRefRelationCache<String> cache;
    private String parametreFolderDocId;

    public STParametreServiceImpl() {
        cache = new ParamRefRelationCache<>(QUERY_PARAMETRE);
    }

    /**
     * charge le document associé au folder contenant les parametres. L'id du document est gardé dans
     * parametreFolderDocId pour des chargement ulterieur évitant une requete SQL à la base.
     */
    @Override
    public DocumentModel getParametreFolder(CoreSession session) {
        if (parametreFolderDocId == null) {
            DocumentModelList list = session.query(QUERY_PARAMETRE_FOLDER);
            if (list.isEmpty()) {
                throw new NuxeoException("Racine des parametres non trouvée");
            } else if (list.size() > 1) {
                throw new NuxeoException("Plusieurs racines des parametres trouvées");
            }

            DocumentModel doc = list.get(0);
            parametreFolderDocId = doc.getId();
            return doc;
        } else {
            return session.getDocument(new IdRef(parametreFolderDocId));
        }
    }

    @Override
    public String getParametreValue(CoreSession session, String id) {
        STParametre stParametre = getParametre(session, id);
        if (stParametre != null) {
            return stParametre.getValue();
        }

        return null;
    }

    @Override
    public STParametre getParametre(CoreSession session, String name) {
        DocumentModel doc = cache.retrieveDocument(session, name);
        return doc == null ? null : doc.getAdapter(STParametre.class);
    }

    @Override
    public String getParametreWithoutSession(String anid) {
        return Framework.doPrivileged(
            () -> {
                return CoreInstance.doPrivileged(
                    getRepo(),
                    session -> {
                        return getParametreValue(session, anid);
                    }
                );
            }
        );
    }

    @Override
    public void clearCache() {
        cache = new ParamRefRelationCache<>(QUERY_PARAMETRE);
    }
}
