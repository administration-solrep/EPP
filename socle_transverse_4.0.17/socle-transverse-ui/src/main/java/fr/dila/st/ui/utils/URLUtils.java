package fr.dila.st.ui.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;

public class URLUtils {
    private static final String X_FORWARDED_HOST = "x-forwarded-host";
    public static final String APP_CONTEXT_PATH = "site/app-ui";
    public static final String AJAX_PATH = "ajax";
    private static final String SLASH = "/";
    private static final List<String> RESOURCES_PATH = Arrays.asList(
        "/css/",
        "/fonts/",
        "/icons/",
        "/img/",
        "/scripts/"
    );

    /**
     * utility class
     */
    private URLUtils() {
        // do nothing
    }

    public static String generateContextPath(String contextPath, String resourceURL, HttpServletRequest request) {
        if (contextPath.endsWith(SLASH) && resourceURL.startsWith(SLASH)) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        } else if (!contextPath.endsWith(SLASH) && !resourceURL.startsWith(SLASH)) {
            contextPath = contextPath + SLASH;
        }

        //Si la requête ne vient pas d'un LB ou RP ou ajoute le contexte applicatif et que ce n'est pas une ressource css,js etc
        if (!StringUtils.isNotBlank(request.getHeader(X_FORWARDED_HOST)) && isNotResourcePath(resourceURL)) {
            //Si je n'ai pas le slash dans mon context je l'ajoute
            if (!contextPath.endsWith(SLASH)) {
                contextPath = contextPath + SLASH;
            }

            //Ajout du contexte applicatif
            contextPath = contextPath + APP_CONTEXT_PATH;

            //Si la ressource ne commence pas par un slash on l'ajoute au contexte
            if (!resourceURL.startsWith(SLASH)) {
                contextPath = contextPath + SLASH;
            }
        }

        return contextPath;
    }

    public static String generateRedirectPath(String resourceURL, HttpServletRequest request) {
        String baseUrl = VirtualHostHelper.getBaseURL(request);
        return generateContextPath(baseUrl, resourceURL, request) + resourceURL;
    }

    public static String constructPagePath(String pageURL, HttpServletRequest request) {
        String completePagePath = pageURL;

        //Si la requête ne vient pas d'un LB ou RP ou ajoute le contexte applicatif et que ce n'est pas une ressource css,js etc
        if (!StringUtils.isNotBlank(request.getHeader(X_FORWARDED_HOST)) && isNotResourcePath(pageURL)) {
            //Si la page ne commence pas par un slash on l'ajoute entre le contexte applicatif et l'URL de la page
            if (!completePagePath.startsWith(SLASH)) {
                completePagePath = APP_CONTEXT_PATH + SLASH + completePagePath;
            } else {
                completePagePath = SLASH + APP_CONTEXT_PATH + completePagePath;
            }
        }
        return completePagePath;
    }

    private static Boolean isNotResourcePath(String path) {
        return (
            StringUtils.isNotBlank(path) &&
            RESOURCES_PATH
                .stream()
                .filter(str -> path.startsWith(str) || path.startsWith(str.substring(1)))
                .collect(Collectors.toList())
                .isEmpty()
        );
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
