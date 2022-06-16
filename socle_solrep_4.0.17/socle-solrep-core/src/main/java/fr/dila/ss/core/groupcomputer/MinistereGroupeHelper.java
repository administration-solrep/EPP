package fr.dila.ss.core.groupcomputer;

import fr.dila.ss.api.constant.SSConstant;

public final class MinistereGroupeHelper {

    /**
     * Utility class
     */
    private MinistereGroupeHelper() {
        // do nothing
    }

    /**
     * Build group from ministere id : add prefix
     * @param ministereId
     * @return group linked to a ministere
     */
    public static String ministereidToGroup(final String ministereId) {
        return SSConstant.GROUP_MINISTERE_PREFIX + ministereId;
    }
}
