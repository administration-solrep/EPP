package fr.dila.st.core.util;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

import org.nuxeo.ecm.automation.core.util.PageProviderHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;

public final class STPageProviderHelper {

    @SuppressWarnings("unchecked")
    public static <T extends PageProvider<?>> T getPageProvider(
        String providerName,
        CoreSession session,
        Object... queryParams
    ) {
        PageProviderDefinition descriptor = PageProviderHelper.getPageProviderDefinition(providerName);
        requireNonNull(descriptor, format("The page provider definition [%s] cannot de found", providerName));
        return (T) PageProviderHelper.getPageProvider(session, descriptor, emptyMap(), queryParams);
    }

    private STPageProviderHelper() {}
}
