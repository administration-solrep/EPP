package fr.dila.ss.ui.services.impl;

import fr.dila.ss.ui.bean.parametres.ParametreDTO;
import fr.dila.ss.ui.bean.parametres.ParametreList;
import fr.dila.ss.ui.services.SSParametreUIService;
import fr.dila.ss.ui.th.bean.ParametreForm;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.contentview.PaginatedPageDocumentProvider;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.nuxeo.ecm.automation.core.util.PageProviderHelper;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.runtime.api.Framework;

public class SSParametreUIServiceImpl implements SSParametreUIService {

    public ParametreList getParametres(SpecificContext context) {
        PaginationForm listForm = context.getFromContextData("listForm");
        PaginatedPageDocumentProvider provider = buildProvider(context, listForm, "parametrePageProvider");
        return handleListResult(provider);
    }

    public ParametreList getParametresArchive(SpecificContext context) {
        PaginationForm listForm = context.getFromContextData("listForm");
        PaginatedPageDocumentProvider provider = buildProvider(context, listForm, "parametreArchivagePageProvider");
        return handleListResult(provider);
    }

    private ParametreList handleListResult(PaginatedPageDocumentProvider provider) {
        ParametreList listResult = new ParametreList();

        List<DocumentModel> docs = provider.getCurrentPage();
        listResult.setListe(
            docs.stream().map(doc -> MapDoc2Bean.docToBean(doc, ParametreDTO.class)).collect(Collectors.toList())
        );
        listResult.setNbTotal((int) provider.getResultsCount());

        return listResult;
    }

    private PaginatedPageDocumentProvider buildProvider(
        SpecificContext context,
        PaginationForm listForm,
        String providerName
    ) {
        STParametreService paramService = STServiceLocator.getSTParametreService();

        PageProviderService providerService = Framework.getService(PageProviderService.class);
        PageProviderDefinition descriptor = providerService.getPageProviderDefinition(providerName);
        PaginatedPageDocumentProvider provider = (PaginatedPageDocumentProvider) PageProviderHelper.getPageProvider(
            context.getSession(),
            descriptor,
            Collections.emptyMap()
        );

        provider.setParameters(new String[] { paramService.getParametreFolder(context.getSession()).getId() });

        provider.setPageSize(listForm.getSize());
        if (listForm.getPage() > 0) {
            provider.setCurrentPageIndex(listForm.getPage() - 1L);
        } else {
            provider.setCurrentPageIndex(0);
        }
        provider.setMaxPageSize(listForm.getSize());

        return provider;
    }

    public void updateParametre(SpecificContext context) {
        ParametreForm form = context.getFromContextData("form");

        STParametre param = context.getCurrentDocument().getAdapter(STParametre.class);

        param.setValue(form.getValeur());

        context.getSession().saveDocument(param.getDocument());
        context.getSession().save();
        context
            .getMessageQueue()
            .addMessageToQueue(ResourceHelper.getString("parametres.modifier.message.succes"), AlertType.TOAST_SUCCESS);
    }
}
