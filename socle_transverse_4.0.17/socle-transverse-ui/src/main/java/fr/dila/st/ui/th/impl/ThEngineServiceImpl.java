package fr.dila.st.ui.th.impl;

import static fr.dila.st.api.constant.STConfigConstants.BUILD_TAG;
import static fr.dila.st.ui.th.constants.STTemplateConstants.BUILD_VERSION;
import static fr.dila.st.ui.th.constants.STTemplateConstants.CSRFTOKEN;
import static fr.dila.st.ui.th.constants.STTemplateConstants.REFRESH_ESPACE_TRAVAIL_DELAY;
import static fr.dila.st.ui.th.constants.STTemplateConstants.SESSION_TIMEOUT;
import static java.lang.Boolean.parseBoolean;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.nuxeo.runtime.api.Framework.getProperty;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.ThEngineService;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.platform.web.common.ServletHelper;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.model.impl.AbstractWebContext;
import org.nuxeo.runtime.api.Framework;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * Init and do render
 *
 * @author SPL
 *
 */
public class ThEngineServiceImpl implements ThEngineService {
    // Délai de rafraichissement par default à 2 minutes
    private static final int DELAI_RAFRAICHISSEMENT = 2;

    private ThEngineDescriptor configDescr = new ThEngineDescriptor();
    private volatile boolean init = false;
    private TemplateEngine templateEngine;

    public ThEngineServiceImpl() {
        // do nothing
    }

    public void initIfNeeded() {
        if (!init) {
            initEngine();
        }
    }

    public synchronized void initEngine() {
        if (!init) {
            ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(
                ServletHelper.getServletContext()
            );

            templateResolver.setTemplateMode(TemplateMode.HTML);
            // This will convert "home" to "/th-templates/home.html"
            templateResolver.setPrefix("/th-templates/");
            templateResolver.setCharacterEncoding("UTF-8");
            templateResolver.setSuffix(".html");
            // Template cache TTL=1h. If not set, entries would be cached until expelled
            templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

            // Cache is set to true by default. Set to false if you want templates to
            // be automatically updated when modified.
            templateResolver.setCacheable(parseBoolean(getProperty("thymeleaf.cache.enabled", Boolean.toString(true))));

            templateEngine = new TemplateEngine();
            templateEngine.setMessageResolver(new ThAppMessageResolver());
            templateEngine.addMessageResolver(new ThSolrepMessageResolver());
            templateEngine.addMessageResolver(new ThMessageResolver());
            templateEngine.setTemplateResolver(templateResolver);
            templateEngine.setLinkBuilder(new SolonLinkBuilder());
            init = true;
        }
    }

    @Override
    public void render(ThTemplate tpl, OutputStream outputStream) {
        initIfNeeded();

        WebContext ctx = WebEngine.getActiveContext();
        org.thymeleaf.context.WebContext thctx = new org.thymeleaf.context.WebContext(
            ctx.getRequest(),
            ((AbstractWebContext) ctx).getResponse(),
            ServletHelper.getServletContext(),
            ctx.getRequest().getLocale()
        );
        tpl.processData();
        if (tpl.getData() != null) {
            thctx.setVariables(tpl.getData());
        }

        SolonAlertManager alertManager = tpl.getContext().getMessageQueue();
        if (alertManager != null && alertManager.hasMessageInQueue()) {
            if (CollectionUtils.isNotEmpty(alertManager.getInfoQueue())) {
                thctx.setVariable("infoMessages", alertManager.getInfoQueue());
            }
            if (CollectionUtils.isNotEmpty(alertManager.getSuccessQueue())) {
                thctx.setVariable("successMessages", alertManager.getSuccessQueue());
            }

            if (CollectionUtils.isNotEmpty(alertManager.getWarnQueue())) {
                thctx.setVariable("warnMessages", alertManager.getWarnQueue());
            }

            if (CollectionUtils.isNotEmpty(alertManager.getErrorQueue())) {
                thctx.setVariable("errorMessages", alertManager.getErrorQueue());
            }
        }

        //Ajout du token csrf dans la page générée
        thctx.setVariable(CSRFTOKEN, STUIServiceLocator.getCSRFService().generateToken(ctx));

        //Mise à disposition du num de build (revision git) pour création des liens
        thctx.setVariable(BUILD_VERSION, STServiceLocator.getConfigService().getValue(BUILD_TAG, EMPTY));

        //Mise à disposition du temps d'expiration de session
        thctx.setVariable(SESSION_TIMEOUT, Framework.getProperty("session.timeout"));

        // Délai rafraichissement espace de travail  ex: 2min
        thctx.setVariable(REFRESH_ESPACE_TRAVAIL_DELAY, getDelaiRafraichissement());

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        if (StringUtils.isNotEmpty(tpl.getLayout())) {
            thctx.setVariable("fragmentFile", tpl.getName());
            templateEngine.process(tpl.getLayout(), thctx, writer);
        } else {
            templateEngine.process(tpl.getName(), thctx, writer);
        }
    }

    @Override
    public long getDelaiRafraichissement() {
        return TimeUnit.MINUTES.toMillis(DELAI_RAFRAICHISSEMENT);
    }

    public void setConfiguration(ThEngineDescriptor descr) {
        this.configDescr = descr;
    }

    public ThEngineDescriptor getConfiguration() {
        return this.configDescr;
    }
}
