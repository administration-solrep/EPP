package fr.dila.epp.ui.services.actions;

import org.nuxeo.ecm.core.api.CoreSession;

public interface OrganigrammeActionService {
    boolean isUserAllowedToEditOrganigramme(CoreSession session, String institutionId);
}
