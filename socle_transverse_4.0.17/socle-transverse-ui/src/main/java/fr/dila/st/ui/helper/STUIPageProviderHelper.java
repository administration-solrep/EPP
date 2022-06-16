package fr.dila.st.ui.helper;

import static fr.dila.st.ui.helper.UserSessionHelper.getUserSessionParameter;

import fr.dila.st.ui.enums.UserSessionKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.Map;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;

public final class STUIPageProviderHelper {

    public static <T extends PageProvider<?>> T getProviderFromContext(
        SpecificContext context,
        UserSessionKey parameterKey
    ) {
        T provider = getUserSessionParameter(context, parameterKey);
        if (provider == null) {
            return null;
        }
        Map<String, Serializable> props = provider.getProperties();
        props.put(CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY, (Serializable) context.getSession());
        provider.setProperties(props);
        return provider;
    }

    private STUIPageProviderHelper() {}
}
