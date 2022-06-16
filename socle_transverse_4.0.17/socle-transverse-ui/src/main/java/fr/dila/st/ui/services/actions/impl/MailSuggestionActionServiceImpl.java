package fr.dila.st.ui.services.actions.impl;

import static fr.dila.st.api.constant.STConstant.ORGANIGRAMME_USER_DIR;
import static fr.dila.st.api.constant.STSchemaConstant.ORGANIGRAMME_USER_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.USER_EMAIL;
import static fr.dila.st.api.constant.STSchemaConstant.USER_FIRST_NAME;
import static fr.dila.st.api.constant.STSchemaConstant.USER_LAST_NAME;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.services.actions.MailSuggestionActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.directory.api.DirectoryService;

public class MailSuggestionActionServiceImpl implements MailSuggestionActionService {
    private static final Log LOGGER = LogFactory.getLog(MailSuggestionActionServiceImpl.class);

    public static final String ID_KEY_NAME = "id";

    public static final String ENTRY_KEY_NAME = "entry";

    public static final String CACHED_INPUT_KEY = "cachedInput";

    public static final String CACHED_SUGGESTIONS_KEY = "cachedSuggestions";

    public static final String CACHED_MAIL_SUGGESTIONS_MAX_SEARCH_RESULTS_KEY = "cachedMailSuggestionMaxSearchResults";

    @Override
    public Set<DocumentModel> getMailSuggestions(
        SpecificContext context,
        String pattern,
        String userSuggestionMessageId
    ) {
        Set<DocumentModel> docs = new HashSet<>();

        try (Session session = getRequiredService(DirectoryService.class).open(ORGANIGRAMME_USER_DIR)) {
            if (StringUtils.isNotBlank(pattern)) {
                Map<String, Serializable> filter = new HashMap<>();
                filter.put(STSchemaConstant.USER_EMAIL, pattern);
                Map<String, String> orderBy = new HashMap<>();
                Set<String> fulltext = new HashSet<>();
                fulltext.add(STSchemaConstant.USER_EMAIL);
                docs.addAll(session.query(filter, fulltext, orderBy, false));

                filter.clear();
                filter.put(USER_FIRST_NAME, pattern);
                fulltext.clear();
                fulltext.add(USER_FIRST_NAME);
                docs.addAll(session.query(filter, fulltext, orderBy, false));
            }
        } catch (SizeLimitExceededException e) {
            addSearchOverflowMessage(context, userSuggestionMessageId);
            LOGGER.warn(e);
        } catch (NuxeoException e) {
            LOGGER.error("error searching for functions", e);
        }
        return docs;
    }

    private void addSearchOverflowMessage(SpecificContext context, String userSuggestionMessageId) {
        if (StringUtils.isBlank(userSuggestionMessageId)) {
            LOGGER.error("Search overflow");
        } else {
            context.getMessageQueue().addInfoToQueue("label.security.searchOverFlow");
        }
    }

    @Override
    public Map<String, String> getMailInfo(String userId) {
        Map<String, String> res = new HashMap<>();
        res.put(ID_KEY_NAME, userId);

        DocumentModel mailDocument = getMailModel(userId);
        if (mailDocument == null) {
            res.put(ENTRY_KEY_NAME, userId);
        } else {
            final String description = (String) mailDocument.getProperty(
                STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
                USER_EMAIL
            );
            res.put(ENTRY_KEY_NAME, description);
            String lastName = (String) mailDocument.getProperty(
                STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
                USER_LAST_NAME
            );
            res.put(USER_LAST_NAME, lastName);
            String firstName = (String) mailDocument.getProperty(
                STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
                USER_FIRST_NAME
            );
            res.put(USER_FIRST_NAME, firstName);
        }

        return res;
    }

    @Override
    public String getMailInfoName(String userId) {
        return getMailInfo(userId).get(ENTRY_KEY_NAME);
    }

    private DocumentModel getMailModel(String profileName) {
        if (profileName == null) {
            return null;
        }
        try (Session session = getRequiredService(DirectoryService.class).open(ORGANIGRAMME_USER_DIR)) {
            // retourne le mailDocument
            return session.getEntry(profileName);
        }
    }

    private boolean areEquals(Object item1, Object item2) {
        if (item1 == null && item2 == null) {
            return true;
        } else if (item1 == null) {
            return false;
        } else {
            return item1.equals(item2);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSuggestions(
        SpecificContext context,
        String input,
        Integer mailSuggestionMaxSearchResults,
        String userSuggestionMessageId
    ) {
        Integer cachedSuggestions = (Integer) context.getProperty(CACHED_SUGGESTIONS_KEY);
        Integer cachedMailSuggestionMaxSearchResults = (Integer) context.getProperty(
            CACHED_MAIL_SUGGESTIONS_MAX_SEARCH_RESULTS_KEY
        );
        if (
            cachedSuggestions != null &&
            areEquals(cachedMailSuggestionMaxSearchResults, mailSuggestionMaxSearchResults) &&
            areEquals(context.getStringProperty(CACHED_INPUT_KEY), input)
        ) {
            return (List<Map<String, Object>>) context.getProperty(CACHED_SUGGESTIONS_KEY);
        }

        Set<DocumentModel> groups = getMailSuggestions(context, input, userSuggestionMessageId);

        int groupSize = groups.size();
        int totalSize = groupSize;

        List<Map<String, Object>> result = new ArrayList<>(totalSize);
        Map<String, Object> entry = new HashMap<>();
        for (DocumentModel group : groups) {
            entry.clear();
            final String description = (String) group.getProperty(ORGANIGRAMME_USER_SCHEMA, USER_EMAIL);
            entry.put(ENTRY_KEY_NAME, description);
            String username = (String) group.getProperty(ORGANIGRAMME_USER_SCHEMA, "username");
            entry.put(ID_KEY_NAME, username);
            String firstName = (String) group.getProperty(ORGANIGRAMME_USER_SCHEMA, USER_FIRST_NAME);
            entry.put(USER_FIRST_NAME, firstName);
            String lastName = (String) group.getProperty(ORGANIGRAMME_USER_SCHEMA, USER_LAST_NAME);
            entry.put(USER_LAST_NAME, lastName);
            result.add(entry);
        }

        context.getContextData().put(CACHED_INPUT_KEY, input);
        context.getContextData().put(CACHED_MAIL_SUGGESTIONS_MAX_SEARCH_RESULTS_KEY, mailSuggestionMaxSearchResults);
        context.getContextData().put(CACHED_SUGGESTIONS_KEY, result);

        return result;
    }
}
