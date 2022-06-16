package fr.dila.ss.core.service;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.SSMinisteresService;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.core.service.organigramme.STMinisteresServiceImpl;
import fr.dila.st.core.util.PermissionHelper;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public class SSMinisteresServiceImpl extends STMinisteresServiceImpl implements SSMinisteresService {

    public SSMinisteresServiceImpl() {
        super();
    }

    @Override
    public String getMinisteresQuery(CoreSession session) {
        Set<String> ministereIds = new HashSet<>();
        NuxeoPrincipal currentUser = session.getPrincipal();

        if (currentUser != null) {
            // On récupère le ministère d'appartenance de l'utilisateur.
            final SSPrincipal ssPrincipal = (SSPrincipal) currentUser;
            if (PermissionHelper.isAdminFonctionnel(ssPrincipal)) {
                ministereIds = getMinisteres(true).stream().map(EntiteNode::getId).collect(Collectors.toSet());
            } else {
                ministereIds = ssPrincipal.getMinistereIdSet();
            }
        }

        return "('" + StringUtils.join(ministereIds.toArray(), "','") + "')";
    }
}
