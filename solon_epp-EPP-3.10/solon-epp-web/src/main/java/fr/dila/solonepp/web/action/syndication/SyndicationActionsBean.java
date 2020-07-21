package fr.dila.solonepp.web.action.syndication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.syndication.FeedItem;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.platform.util.RepositoryLocation;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

import fr.dila.solonepp.api.constant.SolonEppBaseFunctionConstant;
import fr.dila.solonepp.api.constant.SolonEppConfigConstant;
import fr.dila.solonepp.api.constant.SolonEppParametreConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.jeton.JetonDoc;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Bean Seam permettant de gérer les flux RSS de SOLON EPP.
 *
 * @author jtremeaux
 */
@Name("syndication")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.APPLICATION + 1)
public class SyndicationActionsBean extends org.nuxeo.ecm.platform.syndication.SyndicationActionsBean {
    /**
     * Identifiant du flux (table_ref ou evenement).
     */
    @RequestParameter
    protected String feed;

    @In(create = true, required = false)
    protected transient EppPrincipal eppPrincipal;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;
    
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SyndicationActionsBean.class); 
    
    /**
     * Clé du paramètre HTTP flux intérrogé.
     */
    protected static final String FEED_KEY = "feed";
    
    /**
     * Valeur du flux intérrogé : tables de référence.
     */
    protected static final String FEED_TABLE_REF_VALUE = "tableReference";
    
    /**
     * Valeur du flux intérrogé : événements.
     */
    protected static final String FEED_EVENEMENT_KEY = "evenement";
    
    /**
     * Construit le flux RSS de notification et écrit le résultat directement dans la réponse HTTP.
     */
    @Begin(id = "#{conversationIdGenerator.currentOrNewMainConversationId}", join = true)
    public void getSyndicationNotification() throws ClientException {
        if (StringUtils.isBlank(feed)) {
            throw new ClientException("Identifiant du flux de notification non spécifié");
        }
        if (StringUtils.isBlank(feedType)) {
            feedType = DEFAULT_TYPE;
        }
        
        NavigationContext navigationContext = (NavigationContext) Component.getInstance("navigationContext", true);
        navigationContext.setCurrentServerLocation(new RepositoryLocation("default"));
        CoreSession session = navigationContext.getOrCreateDocumentManager();

        // Recherche les notifications
        final ConfigService configService = STServiceLocator.getConfigService();
        long limit = configService.getIntegerValue(SolonEppConfigConstant.SYNDICATION_LIST_SIZE);
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        List<DocumentModel> jetonDocList = null;
        if (FEED_TABLE_REF_VALUE.equals(feed)) {
            jetonDocList = jetonService.findNotification(session, SolonEppBaseFunctionConstant.NOTIFICATION_TABLE_REF_READER, SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_TABLE_REF_VALUE, limit);
        } else if (FEED_EVENEMENT_KEY.equals(feed)) {
            EppPrincipal principal = (EppPrincipal) session.getPrincipal();
            jetonDocList = jetonService.findNotification(session, principal.getInstitutionId(), SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE, limit);
        } else {
            throw new ClientException("Flux de notification invalide: " + feed);
        }

        // Initialise le flux de syndication
        SyndFeed syndFeed = new SyndFeedImpl();
        syndFeed.setFeedType(feedType);
        String title = resourcesAccessor.getMessages().get("epp.rss.feed." + feed + ".title");
        syndFeed.setTitle(title);
        syndFeed.setDescription("");
        syndFeed.setLink(getFeedUrl("getSyndicationNotification.faces", FEED_KEY, feed, feedType));

        // Renseigne les éléments du flux de syndication
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        for (DocumentModel jetonDoc : jetonDocList) {
            feedItems.add(getFeedItem(session, jetonDoc));
        }
        syndFeed.setEntries(feedItems);
        writeFeed(syndFeed);
    }
    
    /**
     * Crée un élément du flux de syndication à partir du document de notification.
     * 
     * @param session Session
     * @param jetonDocModel Document de notification
     * @return Élément du flux
     * @throws ClientException
     */
    protected FeedItem getFeedItem(CoreSession session, DocumentModel jetonDocModel) throws ClientException {
        JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);
        FeedItem feedItem = new FeedItem();

        // Détermine l'objet et le corps de l'élément
        String jetonType = jetonDoc.getTypeWebservice();
        final STParametreService parametreService = STServiceLocator.getSTParametreService();
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        String title = "";
        String description = "";
        String link = "";
        if (SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_TABLE_REF_VALUE.equals(jetonType)) {
            String mailObjet = parametreService.getParametreValue(session, SolonEppParametreConstant.MAIL_NOTIFICATION_TABLE_REFERENCE_OBJET);
            String mailCorps = parametreService.getParametreValue(session, SolonEppParametreConstant.MAIL_NOTIFICATION_TABLE_REFERENCE_CORPS);
            Map<String, Object> paramMap = jetonService.getNotificationTableReferenceParam(session, jetonDocModel);
            title = StringUtil.renderFreemarker(mailObjet, paramMap);
            description = StringUtil.renderFreemarker(mailCorps, paramMap);
        } else if (SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE.equals(jetonType)) {
            String mailObjet = parametreService.getParametreValue(session, SolonEppParametreConstant.MAIL_NOTIFICATION_EVENEMENT_OBJET);
            String mailCorps = parametreService.getParametreValue(session, SolonEppParametreConstant.MAIL_NOTIFICATION_EVENEMENT_CORPS);
            String eventId = jetonDoc.getEvenementId();
            link = getLinkHtmlToEvent(session, eventId);
            Map<String, Object> paramMap = jetonService.getNotificationEvenementParam(session, jetonDocModel);
            title = StringUtil.renderFreemarker(mailObjet, paramMap);
            description = StringUtil.renderFreemarker(mailCorps, paramMap);
        } else {
            throw new ClientException("Type de notification inconnue: " + jetonType);
        }

        // Crée l'élément du flux
        feedItem.setTitle(title);
        feedItem.setDescription(description);
        feedItem.setAuthor(null);
        List<String> contributors = new ArrayList<String>();
        feedItem.setContributors(contributors);
        feedItem.setLink(link);

        Date creationDate = jetonDoc.getCreated().getTime();

        try {
            DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            feedItem.setPublishedDate(dateParser.parse(dateParser.format(creationDate)));
        } catch (ParseException e) {
            throw new ClientException(e);
        }
        feedItem.setUpdatedDate(null);

        return feedItem;
    }
    
    /**
     * Lien vers la recherche de l'événement
     * @param session
     * @param eventId
     * @return
     * @throws ClientException
     */
    protected String getLinkHtmlToEvent(CoreSession session, String eventId) throws ClientException {
        
        LOGGER.debug(session, STLogEnumImpl.CREATE_EVENT_LINK_TEC, "événement : "  + eventId) ;
        
        StringBuilder url = new StringBuilder();
        url.append(BaseURL.getBaseURL());
        url.append("searchEvent.faces");
        url.append('?');
        url.append("evenementId");
        url.append('=');
        url.append(eventId);
        
        return url.toString();
    }

    
}
