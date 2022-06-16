package fr.dila.ss.ui.contentview;

import fr.dila.ss.api.recherche.IdLabel;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.core.client.AbstractMapDTO;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public abstract class AbstractSSCorbeillePageProvider<S extends AbstractMapDTO, T extends STDossierLink>
    extends AbstractDTOPageProvider {
    private static final long serialVersionUID = 1L;

    protected transient List<DocumentModel> dml;

    protected transient Map<String, S> mapDossierIdDTO = new HashMap<>();

    protected transient List<String> lstUserVisibleColumns = new ArrayList<>();

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        currentItems = new ArrayList<>();

        resultsCount = QueryUtils.doCountQuery(coreSession, query);
        // recupere la liste des ids triés
        // se baser sur ce tri pour retourner une liste de map triée dans le bon
        // ordre
        if (resultsCount > 0) {
            List<String> ids = QueryUtils.doQueryForIds(coreSession, query, getPageSize(), offset);
            populateFromDossierLinkIds(coreSession, ids);
        }
    }

    protected void populateFromDossierLinkIds(CoreSession session, List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            dml = QueryUtils.retrieveDocuments(session, STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE, ids, true);

            mapDossierIdDTO = new HashMap<>();
            // traitement case link, initialisation DTO

            dml
                .stream()
                .forEach(
                    dm -> {
                        T dossierLink = getDossierLinkAdapter(dm);

                        S dto = createAndPopulateDossierListingDTO(session, dossierLink);

                        mapDossierIdDTO.put(dossierLink.getDossierId(), dto);

                        if (currentItems != null) {
                            currentItems.add(dto);
                        }
                    }
                );
        }
    }

    private S createAndPopulateDossierListingDTO(CoreSession session, T dossierLink) {
        S dto = createDossierListingDTO();
        buildDTOFromDossierLinks(dto, dossierLink, session);
        populateDossierListingDTO(
            dto,
            dossierLink,
            new IdLabel[] { new IdLabel(dossierLink.getId(), dossierLink.getRoutingTaskLabel()) }
        );
        return dto;
    }

    public List<String> getLstUserVisibleColumns() {
        return lstUserVisibleColumns;
    }

    public void setLstUserVisibleColumns(List<String> lstUserVisibleColumns) {
        this.lstUserVisibleColumns = lstUserVisibleColumns;
    }

    protected abstract void buildDTOFromDossierLinks(S dto, T dossierLink, CoreSession session);

    protected abstract void populateDossierListingDTO(S dto, T dossierLink, IdLabel[] currentDossierLink);

    protected abstract S createDossierListingDTO();

    @SuppressWarnings("unchecked")
    protected final T getDossierLinkAdapter(DocumentModel doc) {
        return (T) doc.getAdapter(STDossierLink.class);
    }
}
