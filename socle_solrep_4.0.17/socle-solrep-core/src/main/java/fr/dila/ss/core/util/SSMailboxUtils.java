package fr.dila.ss.core.util;

import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static java.util.Optional.ofNullable;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.user.STUser;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class SSMailboxUtils {

    private SSMailboxUtils() {
        // Default constructor
    }

    /**
     * Retourne la liste des postes à afficher, ou par défaut les postes de
     * l'utilisateur courant. S'assure que l'utilisateur courant a les permissions
     * sur ces postes.
     *
     * @param ssPrincipal
     * @param corbeilleSelectionPoste
     * @param corbeilleSelectionUser
     * @return Liste d'identifiants de postes
     */
    public static Set<String> getSelectedPostes(
        SSPrincipal ssPrincipal,
        String corbeilleSelectionPoste,
        String corbeilleSelectionUser
    ) {
        Set<String> postesId = new HashSet<>();

        if (StringUtils.isNotBlank(corbeilleSelectionPoste)) {
            postesId.add(corbeilleSelectionPoste);
        }

        ofNullable(corbeilleSelectionUser)
            .filter(StringUtils::isNotEmpty)
            .map(getUserManager()::getUserModel)
            .map(u -> u.getAdapter(STUser.class))
            .map(STUser::getPostes)
            .ifPresent(postesId::addAll);

        // L'outil de sélection de poste ou d'utilisateur n'est pas utilisé,
        // fonctionnement normal
        if (postesId.isEmpty()) {
            postesId.addAll(ssPrincipal.getPosteIdSet());
        }

        return postesId;
    }
}
