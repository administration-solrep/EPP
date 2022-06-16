package fr.dila.st.api.util;

import org.nuxeo.runtime.api.Framework;

/**
 * Helper appelant simplement les méthodes de {@link Framework} afin de pouvoir
 * powermocker les méthodes de {@link Framework} dans les tests. La préparation
 * de {@link Framework} pour les tests dure trop longtemps et les pipelines
 * gitlab tombent en timeout.<br>
 * Utiliser {@link FrameworkHelper} à la place.
 *
 * @author SCE
 *
 */
public final class FrameworkHelper {

    public static boolean isDevModeSet() {
        return Framework.isDevModeSet();
    }

    private FrameworkHelper() {}
}
