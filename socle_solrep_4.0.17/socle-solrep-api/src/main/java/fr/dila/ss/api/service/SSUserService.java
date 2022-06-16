package fr.dila.ss.api.service;

import fr.dila.st.api.user.STUser;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.nuxeo.ecm.core.api.SortInfo;

public interface SSUserService {
    Collection<STUser> getListUserNotConnectedSince(final Date dateDeConnexion, List<SortInfo> sortInfos);

    List<STUser> getAllUserConnected(List<SortInfo> sortInfos);

    List<STUser> getAllUserConnected();

    List<STUser> setLogoutTrueForAllUsers();
}
