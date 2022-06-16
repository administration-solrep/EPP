package fr.dila.ss.ui.jaxrs.webobject.page.journal;

import static java.util.stream.Collectors.toList;

import fr.dila.ss.ui.bean.JournalDossierResultList;
import fr.dila.ss.ui.services.SSJournalUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractSsJournal extends SolonWebObject {

    public AbstractSsJournal() {
        super();
    }

    protected List<SelectValueDTO> getCategories() {
        SSJournalUIService<JournalDossierResultList> service = getJournalUIService();
        return service
            .getCategoryList()
            .stream()
            .map(service::getCategorySelectValueDTO)
            .sorted(Comparator.comparing(SelectValueDTO::getLabel))
            .collect(toList());
    }

    protected SSJournalUIService<JournalDossierResultList> getJournalUIService() {
        return SSUIServiceLocator.getJournalUIService();
    }
}
