package fr.dila.st.core.event;

import static fr.dila.st.core.operation.acl.CalculateUserAclMD5.SW_PREPARE_USERS_MD5_PROC;
import static fr.dila.st.core.service.STServiceLocator.getUserManager;

import fr.dila.st.core.acl.work.ScrollingCallProcedure;
import java.util.List;
import org.nuxeo.ecm.core.work.api.Work;

public class GroupListener extends UserGroupListener {

    @Override
    protected Work getWork(String userGroupId) {
        List<String> userIds = getUserManager().getGroup(userGroupId).getMemberUsers();
        return new ScrollingCallProcedure(SW_PREPARE_USERS_MD5_PROC, userIds, 5);
    }
}
