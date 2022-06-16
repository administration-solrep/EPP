package fr.dila.st.core.util;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

/**
 * Classe utilitaire permettant de récupérer un document par son ID, en désactivant la gestion des ACL.
 *
 * @author jtremeaux
 */
public class UnrestrictedGetDocumentRunner extends UnrestrictedSessionRunner {
    /**
     * Référence du document.
     */
    private DocumentRef docRef;

    /**
     * Résultats de recherche.
     */
    private DocumentModel doc;

    /**
     * Résultats de recherche si on recherche les fils d'un document.
     */
    private DocumentModelList docModelList;

    /**
     * Indique si l'on effectue une recherche sur les fils d'un document.
     */
    private boolean isChildrenSearch = false;

    /**
     * if true, fetch all schemas before detaching CoreSession from DocumentModel
     */
    private boolean detach = false;

    /**
     * Informations supplémentaires sur les documents
     */
    private PrefetchInfo prefetchInfo;

    public UnrestrictedGetDocumentRunner(CoreSession session) {
        super(session);
    }

    public UnrestrictedGetDocumentRunner(CoreSession session, boolean detach) {
        super(session);
        this.detach = detach;
    }

    public UnrestrictedGetDocumentRunner(CoreSession session, String... schemas) {
        super(session);
        if (Objects.nonNull(schemas)) {
            this.prefetchInfo = new PrefetchInfo(join(schemas, ","));
        }
    }

    public UnrestrictedGetDocumentRunner(CoreSession session, PrefetchInfo prefetchInfo) {
        super(session);
        this.prefetchInfo = prefetchInfo;
    }

    @Override
    public void run() {
        if (isChildrenSearch) {
            docModelList = session.getChildren(docRef);
            if (Objects.nonNull(prefetchInfo)) {
                Collection<String> ids = docModelList.stream().map(DocumentModel::getId).collect(Collectors.toList());
                docModelList = session.getDocuments(ids, prefetchInfo);
            }
        } else {
            doc = null;
            Set<String> ids = new HashSet<>();
            if (docRef instanceof IdRef) {
                ids.add(docRef.toString());
            } else {
                doc = session.getDocument(docRef);
                ids.add(doc.getId());
            }
            if (Objects.nonNull(prefetchInfo)) {
                doc = session.getDocuments(ids, prefetchInfo).get(0);
            } else if (Objects.isNull(doc)) {
                doc = session.getDocument(docRef);
            }
            if (detach) {
                if (docModelList != null) {
                    docModelList.forEach(d -> d.detach(true));
                }
                if (doc != null) {
                    doc.detach(true);
                }
            }
        }
    }

    /**
     * Recherche un document par sa référence et retourne le document. Retourne null si aucun document n'est trouvé.
     *
     * @param docReference
     *            Référence du document
     * @return Document ou null
     *
     */
    public DocumentModel getByRef(DocumentRef docReference) {
        docRef = docReference;
        runUnrestricted();
        return doc;
    }

    /**
     * Recherche un document par son identifiant technique (UUID) et retourne le document. Retourne null si aucun
     * document n'est trouvé.
     *
     * @param uuidDocument
     *            Identifiant technique du document (UUID)
     * @return Document ou null
     *
     */
    public DocumentModel getById(String uuidDocument) {
        return getByRef(new IdRef(uuidDocument));
    }

    /**
     * Recherche une liste de document fils par l'identifiant technique (UUID) d'un document et retourne cette liste.
     * Retourne null si aucun document n'est trouvé.
     *
     * @param uuidDocument
     *            Identifiant technique du document (UUID)
     * @return Document List ou null
     *
     */
    public DocumentModelList getChildrenById(String uuidDocument) {
        return getChildrenByRef(new IdRef(uuidDocument));
    }

    /**
     * Recherche une liste de document fils par la référence d'un document et retourne cette liste. Retourne null si
     * aucun document n'est trouvé.
     *
     * @param docReference
     *            Référence du document
     * @return Document List ou null
     *
     */
    public DocumentModelList getChildrenByRef(DocumentRef docReference) {
        // on signale que l'on effectue une recherche sur les éléments fils d'un document
        isChildrenSearch = true;
        docRef = docReference;
        // on effectue la requete
        runUnrestricted();
        return docModelList;
    }

    /**
     * Recherche un document par son chemin et retourne le document. Retourne null si aucun document n'est trouvé.
     *
     * @param path
     *            Chemin du document
     * @return Document ou null
     *
     */
    public DocumentModel getByPath(String path) {
        return getByRef(new PathRef(path));
    }

    public PrefetchInfo getPrefetchInfo() {
        return prefetchInfo;
    }

    public void setPrefetchInfo(PrefetchInfo prefetchInfo) {
        this.prefetchInfo = prefetchInfo;
    }

    public void setPrefetchInfo(String... schemas) {
        this.prefetchInfo = Objects.nonNull(schemas) ? new PrefetchInfo(StringUtils.join(schemas, ",")) : null;
    }
}
