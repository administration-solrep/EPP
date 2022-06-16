package fr.dila.st.api.service;

import java.util.Collection;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface STUserSearchService {
    List<DocumentModel> getUsersFromIds(Collection<String> userIds);
}
