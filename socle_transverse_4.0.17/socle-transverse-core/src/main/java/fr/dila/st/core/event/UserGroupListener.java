package fr.dila.st.core.event;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;
import static org.nuxeo.ecm.core.event.impl.DocumentEventContext.CATEGORY_PROPERTY_KEY;
import static org.nuxeo.ecm.platform.usermanager.UserManagerImpl.ID_PROPERTY_KEY;
import static org.nuxeo.ecm.platform.usermanager.UserManagerImpl.USER_GROUP_CATEGORY;
import static org.nuxeo.runtime.api.Framework.isDevModeSet;
import static org.nuxeo.runtime.api.Framework.isTestModeSet;

import java.util.Objects;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.work.api.Work;
import org.nuxeo.ecm.core.work.api.WorkManager;

abstract class UserGroupListener extends AbstractPostCommitEventListener {

    @Override
    protected void handleEvent(Event event, CoreSession session) {
        String userGroupId = (String) event.getContext().getProperty(ID_PROPERTY_KEY);
        Objects.requireNonNull(userGroupId);

        getRequiredService(WorkManager.class).schedule(getWork(userGroupId));
    }

    protected abstract Work getWork(String userGroupId);

    @Override
    protected boolean accept(Event event) {
        String category = (String) event.getContext().getProperty(CATEGORY_PROPERTY_KEY);
        return USER_GROUP_CATEGORY.equals(category) && !isDevModeSet() && !isTestModeSet();
    }
}
