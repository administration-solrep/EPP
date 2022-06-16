package fr.dila.st.ui.helper;

import fr.dila.st.ui.enums.UserSessionKey;
import fr.dila.st.ui.th.model.SpecificContext;

public final class UserSessionHelper {

    private UserSessionHelper() {
        // Default constructor
    }

    public static <T> T getUserSessionParameter(SpecificContext context, String parameterKey, Class<T> returnClass) {
        return returnClass.cast(context.getWebcontext().getUserSession().get(parameterKey));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getUserSessionParameter(SpecificContext context, String parameterKey) {
        return (T) context.getWebcontext().getUserSession().get(parameterKey);
    }

    public static <T> void putUserSessionParameter(SpecificContext context, String parameterKey, T parameter) {
        context.getWebcontext().getUserSession().put(parameterKey, parameter);
    }

    public static <T> T getUserSessionParameter(SpecificContext context, UserSessionKey key) {
        return getUserSessionParameter(context, key.getName());
    }

    public static <T> void putUserSessionParameter(SpecificContext context, UserSessionKey key, T parameter) {
        putUserSessionParameter(context, key.getName(), parameter);
    }

    public static void clearUserSessionParameter(SpecificContext context, String parameterKey) {
        putUserSessionParameter(context, parameterKey, null);
    }

    public static void clearUserSessionParameter(SpecificContext context, UserSessionKey key) {
        putUserSessionParameter(context, key, null);
    }
}
