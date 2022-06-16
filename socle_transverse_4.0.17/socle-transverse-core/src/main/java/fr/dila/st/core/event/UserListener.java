package fr.dila.st.core.event;

import static fr.dila.st.core.operation.acl.CalculateUserAclMD5.SW_PREPARE_USERS_MD5_PROC;
import static java.util.Collections.singletonList;

import fr.dila.st.core.acl.work.CallProcedure;
import org.nuxeo.ecm.core.work.api.Work;

public class UserListener extends UserGroupListener {

    @Override
    protected Work getWork(String userGroupId) {
        return new CallProcedure(SW_PREPARE_USERS_MD5_PROC, singletonList(userGroupId));
    }
}
