package fr.dila.st.ui.th.impl;

import fr.dila.st.ui.utils.URLUtils;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;

public class SolonLinkBuilder extends StandardLinkBuilder {

    @Override
    protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
        String myContextPath = super.computeContextPath(context, base, parameters);

        final HttpServletRequest request = ((IWebContext) context).getRequest();

        return URLUtils.generateContextPath(myContextPath, base, request);
    }
}
