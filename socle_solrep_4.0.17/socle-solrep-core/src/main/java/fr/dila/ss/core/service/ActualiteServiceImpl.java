package fr.dila.ss.core.service;

import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_ROOT_PATH;

import com.google.common.collect.ImmutableMap;
import fr.dila.ss.api.actualite.Actualite;
import fr.dila.ss.api.service.ActualiteService;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryAndFetchPageProvider;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;

public class ActualiteServiceImpl implements ActualiteService {
    public static final PathRef ACTUALITES_ROOT_PATH_REF = new PathRef(ACTUALITE_ROOT_PATH);

    /**
     * default constructor
     */
    public ActualiteServiceImpl() {
        // do nothing
    }

    @Override
    public DocumentModel createActualite(CoreSession session, DocumentModel actualiteDoc) {
        actualiteDoc.setPathInfo(ACTUALITE_ROOT_PATH, "actualite");

        validateActualite(actualiteDoc.getAdapter(Actualite.class));

        return session.createDocument(actualiteDoc);
    }

    @Override
    public List<DocumentModel> fetchUserActualitesNonLues(CoreSession session) {
        String today = SolonDateConverter.DATE_DASH_REVERSE.formatNow();
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            String.format(
                "Select a.ecm:uuid AS id from Actualite as a where a.act:lecteurs <> ? AND a.act:dateEmission <= DATE '%s' AND a.act:dateValidite >= DATE '%s' ORDER BY a.act:dateEmission",
                today,
                today
            ),
            new String[] { session.getPrincipal().getName() }
        );
    }

    @Override
    public PageProvider<DocumentModel> getActualitesPageProvider(
        CoreSession session,
        DocumentModel actualiteRequeteDoc,
        List<SortInfo> sortInfos,
        long pageSize,
        long currentPage
    ) {
        PageProviderService pageProviderService = ServiceUtil.getService(PageProviderService.class);
        return (CoreQueryDocumentPageProvider) pageProviderService.getPageProvider(
            "actualite",
            actualiteRequeteDoc,
            sortInfos,
            pageSize,
            currentPage,
            ImmutableMap.<String, Serializable>of(
                CoreQueryAndFetchPageProvider.CORE_SESSION_PROPERTY,
                (Serializable) session
            ),
            ArrayUtils.EMPTY_OBJECT_ARRAY
        );
    }

    private void validateActualite(Actualite actualite) {
        if (actualite.getDateEmission() == null) {
            throw new STValidationException("actualite.dateEmission.required");
        }

        if (actualite.getDateEmission().isBefore(LocalDate.now())) {
            throw new STValidationException("actualite.dateEmission.notPast");
        }

        if (actualite.getDateValidite() == null) {
            throw new STValidationException("actualite.dateValidite.required");
        }

        if (actualite.getDateValidite().isBefore(actualite.getDateEmission())) {
            throw new STValidationException("actualite.dateValidite.after.dateEmission");
        }

        if (StringUtils.isBlank(actualite.getObjet())) {
            throw new STValidationException("actualite.objet.required");
        }

        if (StringUtils.isBlank(actualite.getContenu())) {
            throw new STValidationException("actualite.contenu.required");
        }

        if (!actualite.getIsInHistorique() && !actualite.getPiecesJointes().isEmpty()) {
            throw new STValidationException("actualite.piecesJointes.empty");
        }
    }
}
