/*
 * (C) Copyright 2011-2018 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Thomas Roger <troger@nuxeo.com>
 */
package org.nuxeo.ecm.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.i18n.I18NUtils;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.ecm.core.repository.RepositoryInitializationHandler;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Default implementation of {@link ActivityStreamService}.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public class ActivityStreamServiceImpl extends DefaultComponent implements ActivityStreamService {

    private static final Log log = LogFactory.getLog(ActivityStreamServiceImpl.class);

    public static final String ACTIVITIES_PROVIDER = "nxactivities";

    public static final String ACTIVITY_STREAM_FILTER_EP = "activityStreamFilters";

    public static final String ACTIVITY_STREAMS_EP = "activityStreams";

    public static final String ACTIVITY_VERBS_EP = "activityVerbs";

    public static final String ACTIVITY_LINK_BUILDERS_EP = "activityLinkBuilders";

    public static final String ACTIVITY_UPGRADERS_EP = "activityUpgraders";

    protected final ThreadLocal<EntityManager> localEntityManager = new ThreadLocal<>();

    protected final Map<String, ActivityStreamFilter> activityStreamFilters = new HashMap<>();

    protected ActivityStreamRegistry activityStreamRegistry;

    protected ActivityVerbRegistry activityVerbRegistry;

    protected ActivityLinkBuilderRegistry activityLinkBuilderRegistry;

    protected ActivityUpgraderRegistry activityUpgraderRegistry;

    protected PersistenceProvider persistenceProvider;

    protected RepositoryInitializationHandler initializationHandler;

    public void upgradeActivities() {
        for (final ActivityUpgrader upgrader : activityUpgraderRegistry.getOrderedActivityUpgraders()) {
            try {
                getOrCreatePersistenceProvider().run(false, em -> {
                    upgradeActivities(em, upgrader);
                });
            } catch (NuxeoException e) {
                log.error(String.format("Error while running '%s' activity upgrader: %s", upgrader.getName(),
                        e.getMessage()));
                log.debug(e, e);
            }
        }
    }

    protected void upgradeActivities(EntityManager em, ActivityUpgrader upgrader) {
        try {
            localEntityManager.set(em);
            upgrader.doUpgrade(this);
        } finally {
            localEntityManager.remove();
        }
    }

    @Override
    public ActivitiesList query(String filterId, final Map<String, Serializable> parameters) {
        return query(filterId, parameters, 0, 0);
    }

    @Override
    public ActivitiesList query(String filterId, final Map<String, Serializable> parameters, final long offset,
            final long limit) {
        if (ALL_ACTIVITIES.equals(filterId)) {
            return queryAll(offset, limit);
        }

        final ActivityStreamFilter filter = activityStreamFilters.get(filterId);
        if (filter == null) {
            throw new NuxeoException(String.format("Unable to retrieve '%s' ActivityStreamFilter", filterId));
        }

        return query(filter, parameters, offset, limit);
    }

    protected ActivitiesList query(final ActivityStreamFilter filter, final Map<String, Serializable> parameters,
            final long offset, final long limit) {
        return getOrCreatePersistenceProvider().run(false, em -> {
            return query(em, filter, parameters, offset, limit);
        });
    }

    protected ActivitiesList query(EntityManager em, ActivityStreamFilter filter, Map<String, Serializable> parameters,
            long offset, long limit) {
        try {
            localEntityManager.set(em);
            return filter.query(this, parameters, offset, limit);
        } finally {
            localEntityManager.remove();
        }

    }

    protected ActivitiesList queryAll(final long offset, final long limit) {
        return getOrCreatePersistenceProvider().run(false, em -> {
            return queryAll(em, offset, limit);
        });
    }

    @SuppressWarnings("unchecked")
    protected ActivitiesList queryAll(EntityManager em, long offset, long limit) {
        Query query = em.createQuery("select activity from Activity activity order by activity.id asc");
        if (limit > 0) {
            query.setMaxResults((int) limit);
        }
        if (offset > 0) {
            query.setFirstResult((int) offset);
        }
        return new ActivitiesListImpl(query.getResultList());
    }

    @Override
    public Activity addActivity(final Activity activity) {
        if (activity.getPublishedDate() == null) {
            activity.setPublishedDate(new Date());
        }
        getOrCreatePersistenceProvider().run(true, em -> {
            addActivity(em, activity);
        });
        return activity;
    }

    protected void addActivity(EntityManager em, Activity activity) {
        try {
            localEntityManager.set(em);
            em.persist(activity);
            for (ActivityStreamFilter filter : activityStreamFilters.values()) {
                if (filter.isInterestedIn(activity)) {
                    filter.handleNewActivity(this, activity);
                }
            }
        } finally {
            localEntityManager.remove();
        }
    }

    @Override
    public void removeActivities(final Collection<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            return;
        }
        getOrCreatePersistenceProvider().run(true, em -> {
            removeActivities(em, activities);
        });
    }

    protected void removeActivities(EntityManager em, Collection<Activity> activities) {
        try {
            localEntityManager.set(em);

            ActivitiesList l = new ActivitiesListImpl(activities);
            for (ActivityStreamFilter filter : activityStreamFilters.values()) {
                filter.handleRemovedActivities(this, l);
            }

            Query query = em.createQuery("delete from Activity activity where activity.id in (:ids)");
            query.setParameter("ids", l.toActivityIds());
            query.executeUpdate();
        } finally {
            localEntityManager.remove();
        }
    }

    @Override
    public ActivityMessage toActivityMessage(final Activity activity, Locale locale) {
        return toActivityMessage(activity, locale, null);
    }

    @Override
    public ActivityMessage toActivityMessage(Activity activity, Locale locale, String activityLinkBuilderName) {
        ActivityLinkBuilder activityLinkBuilder = getActivityLinkBuilder(activityLinkBuilderName);

        Map<String, String> fields = activity.toMap();

        String actor = activity.getActor();
        String displayActor = activity.getDisplayActor();
        String displayActorLink;
        if (ActivityHelper.isUser(actor)) {
            displayActorLink = activityLinkBuilder.getUserProfileLink(actor, activity.getDisplayActor());
        } else {
            displayActorLink = activity.getDisplayActor();
        }

        List<ActivityReplyMessage> activityReplyMessages = toActivityReplyMessages(activity.getActivityReplies(),
                locale, activityLinkBuilderName);

        ActivityVerb verb = activityVerbRegistry.get(activity.getVerb());

        if (verb == null || verb.getLabelKey() == null) {
            return new ActivityMessage(activity.getId(), actor, displayActor, displayActorLink, activity.getVerb(),
                    activity.toString(), activity.getPublishedDate(), null, activityReplyMessages);
        }

        String labelKey = verb.getLabelKey();
        String messageTemplate;
        try {
            messageTemplate = I18NUtils.getMessageString("messages", labelKey, null, locale);
        } catch (MissingResourceException e) {
            log.error(e.getMessage());
            log.debug(e, e);
            // just return the labelKey if we have no resource bundle
            return new ActivityMessage(activity.getId(), actor, displayActor, displayActorLink, activity.getVerb(),
                    labelKey, activity.getPublishedDate(), verb.getIcon(), activityReplyMessages);
        }

        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher m = pattern.matcher(messageTemplate);
        while (m.find()) {
            String param = m.group().replaceAll("[\\|$\\|{\\}]", "");
            if (fields.containsKey(param)) {
                String value = fields.get(param);
                final String displayValue = fields.get("display" + StringUtils.capitalize(param));
                if (ActivityHelper.isDocument(value)) {
                    value = activityLinkBuilder.getDocumentLink(value, displayValue);
                } else if (ActivityHelper.isUser(value)) {
                    value = activityLinkBuilder.getUserProfileLink(value, displayValue);
                } else {
                    // simple text
                    value = ActivityMessageHelper.replaceURLsByLinks(value);
                }
                messageTemplate = messageTemplate.replace(m.group(), value);
            }
        }

        return new ActivityMessage(activity.getId(), actor, displayActor, displayActorLink, activity.getVerb(),
                messageTemplate, activity.getPublishedDate(), verb.getIcon(), activityReplyMessages);
    }

    @Override
    public ActivityLinkBuilder getActivityLinkBuilder(String name) {
        ActivityLinkBuilder activityLinkBuilder;
        if (StringUtils.isBlank(name)) {
            activityLinkBuilder = activityLinkBuilderRegistry.getDefaultActivityLinkBuilder();
        } else {
            activityLinkBuilder = activityLinkBuilderRegistry.get(name);
            if (activityLinkBuilder == null) {
                log.warn("Fallback on default Activity link builder");
                activityLinkBuilder = activityLinkBuilderRegistry.getDefaultActivityLinkBuilder();
            }
        }
        return activityLinkBuilder;
    }

    @Override
    public ActivityReplyMessage toActivityReplyMessage(ActivityReply activityReply, Locale locale) {
        return toActivityReplyMessage(activityReply, locale, null);
    }

    @Override
    public ActivityReplyMessage toActivityReplyMessage(ActivityReply activityReply, Locale locale,
            String activityLinkBuilderName) {
        ActivityLinkBuilder activityLinkBuilder = getActivityLinkBuilder(activityLinkBuilderName);

        String actor = activityReply.getActor();
        String displayActor = activityReply.getDisplayActor();
        String displayActorLink = activityLinkBuilder.getUserProfileLink(actor, displayActor);
        String message = ActivityMessageHelper.replaceURLsByLinks(activityReply.getMessage());
        return new ActivityReplyMessage(activityReply.getId(), actor, displayActor, displayActorLink, message,
                activityReply.getPublishedDate());

    }

    private List<ActivityReplyMessage> toActivityReplyMessages(List<ActivityReply> replies, Locale locale,
            String activityLinkBuilderName) {
        List<ActivityReplyMessage> activityReplyMessages = new ArrayList<>();
        for (ActivityReply reply : replies) {
            activityReplyMessages.add(toActivityReplyMessage(reply, locale, activityLinkBuilderName));
        }
        return activityReplyMessages;
    }

    @Override
    public ActivityStream getActivityStream(String name) {
        return activityStreamRegistry.get(name);
    }

    @Override
    public ActivityReply addActivityReply(Serializable activityId, ActivityReply activityReply) {
        Activity activity = getActivity(activityId);
        if (activity != null) {
            List<ActivityReply> replies = activity.getActivityReplies();
            String newReplyId = computeNewReplyId(activity);
            activityReply.setId(newReplyId);
            replies.add(activityReply);
            activity.setActivityReplies(replies);
            updateActivity(activity);
        }
        return activityReply;
    }

    /**
     * @since 5.6
     */
    protected String computeNewReplyId(Activity activity) {
        String replyIdPrefix = activity.getId() + "-reply-";
        List<ActivityReply> replies = activity.getActivityReplies();
        long maxId = 0;
        for (ActivityReply reply : replies) {
            String replyId = reply.getId();
            long currentId = Long.parseLong(replyId.replace(replyIdPrefix, ""));
            if (currentId > maxId) {
                maxId = currentId;
            }
        }
        return replyIdPrefix + (maxId + 1);
    }

    public Activity getActivity(final Serializable activityId) {
        return getOrCreatePersistenceProvider().run(false, em -> {
            return getActivity(em, activityId);
        });
    }

    public ActivitiesList getActivities(final Collection<Serializable> activityIds) {
        return getOrCreatePersistenceProvider().run(false, em -> {
            return getActivities(em, activityIds);
        });
    }

    @Override
    public ActivityReply removeActivityReply(final Serializable activityId, final String activityReplyId) {
        return getOrCreatePersistenceProvider().run(true, em -> {
            return removeActivityReply(em, activityId, activityReplyId);
        });
    }

    /**
     * @since 5.6
     */
    protected ActivityReply removeActivityReply(EntityManager em, Serializable activityId, String activityReplyId) {
        try {
            localEntityManager.set(em);

            Activity activity = getActivity(activityId);
            if (activity != null) {
                List<ActivityReply> replies = activity.getActivityReplies();
                for (Iterator<ActivityReply> it = replies.iterator(); it.hasNext();) {
                    ActivityReply reply = it.next();
                    if (reply.getId().equals(activityReplyId)) {
                        for (ActivityStreamFilter filter : activityStreamFilters.values()) {
                            filter.handleRemovedActivityReply(this, activity, reply);
                        }
                        it.remove();
                        activity.setActivityReplies(replies);
                        updateActivity(activity);
                        return reply;
                    }
                }
            }
            return null;
        } finally {
            localEntityManager.remove();
        }
    }

    protected Activity getActivity(EntityManager em, Serializable activityId) {
        Query query = em.createQuery("select activity from Activity activity where activity.id = :activityId");
        query.setParameter("activityId", activityId);
        return (Activity) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    protected ActivitiesList getActivities(EntityManager em, Collection<Serializable> activityIds) {
        Query query = em.createQuery("select activity from Activity activity where activity.id in (:ids)");
        query.setParameter("ids", activityIds);
        return new ActivitiesListImpl(query.getResultList());
    }

    protected void updateActivity(final Activity activity) {
        getOrCreatePersistenceProvider().run(false, em -> {
            activity.setLastUpdatedDate(new Date());
            return em.merge(activity);
        });
    }

    public EntityManager getEntityManager() {
        return localEntityManager.get();
    }

    public PersistenceProvider getOrCreatePersistenceProvider() {
        if (persistenceProvider == null) {
            activatePersistenceProvider();
        }
        return persistenceProvider;
    }

    protected void activatePersistenceProvider() {
        Thread thread = Thread.currentThread();
        ClassLoader last = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
            PersistenceProviderFactory persistenceProviderFactory = Framework.getService(
                    PersistenceProviderFactory.class);
            persistenceProvider = persistenceProviderFactory.newProvider(ACTIVITIES_PROVIDER);
            persistenceProvider.openPersistenceUnit();
        } finally {
            thread.setContextClassLoader(last);
        }
    }

    protected void deactivatePersistenceProvider() {
        if (persistenceProvider != null) {
            persistenceProvider.closePersistenceUnit();
            persistenceProvider = null;
        }
    }

    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
        activityStreamRegistry = new ActivityStreamRegistry();
        activityVerbRegistry = new ActivityVerbRegistry();
        activityLinkBuilderRegistry = new ActivityLinkBuilderRegistry();
        activityUpgraderRegistry = new ActivityUpgraderRegistry();

        initializationHandler = new ActivityRepositoryInitializationHandler();
        initializationHandler.install();
    }

    @Override
    public void deactivate(ComponentContext context) {
        deactivatePersistenceProvider();

        if (initializationHandler != null) {
            initializationHandler.uninstall();
        }

        super.deactivate(context);
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (ACTIVITY_STREAM_FILTER_EP.equals(extensionPoint)) {
            registerActivityStreamFilter((ActivityStreamFilterDescriptor) contribution);
        } else if (ACTIVITY_STREAMS_EP.equals(extensionPoint)) {
            registerActivityStream((ActivityStream) contribution);
        } else if (ACTIVITY_VERBS_EP.equals(extensionPoint)) {
            registerActivityVerb((ActivityVerb) contribution);
        } else if (ACTIVITY_LINK_BUILDERS_EP.equals(extensionPoint)) {
            registerActivityLinkBuilder((ActivityLinkBuilderDescriptor) contribution);
        } else if (ACTIVITY_UPGRADERS_EP.equals(extensionPoint)) {
            registerActivityUpgrader((ActivityUpgraderDescriptor) contribution);
        }
    }

    private void registerActivityStreamFilter(ActivityStreamFilterDescriptor descriptor) {
        ActivityStreamFilter filter = descriptor.getActivityStreamFilter();

        String filterId = filter.getId();

        boolean enabled = descriptor.isEnabled();
        if (activityStreamFilters.containsKey(filterId)) {
            log.info("Overriding activity stream filter with id " + filterId);
            if (!enabled) {
                activityStreamFilters.remove(filterId);
                log.info("Disabled activity stream filter with id " + filterId);
            }
        }
        if (enabled) {
            log.info("Registering activity stream filter with id " + filterId);
            activityStreamFilters.put(filterId, descriptor.getActivityStreamFilter());
        }
    }

    private void registerActivityStream(ActivityStream activityStream) {
        log.info(String.format("Registering activity stream '%s'", activityStream.getName()));
        activityStreamRegistry.addContribution(activityStream);
    }

    private void registerActivityVerb(ActivityVerb activityVerb) {
        log.info(String.format("Registering activity verb '%s'", activityVerb.getVerb()));
        activityVerbRegistry.addContribution(activityVerb);
    }

    private void registerActivityLinkBuilder(ActivityLinkBuilderDescriptor activityLinkBuilderDescriptor) {
        log.info(String.format("Registering activity link builder '%s'", activityLinkBuilderDescriptor.getName()));
        activityLinkBuilderRegistry.addContribution(activityLinkBuilderDescriptor);
    }

    private void registerActivityUpgrader(ActivityUpgraderDescriptor activityUpgraderDescriptor) {
        log.info(String.format("Registering activity upgrader '%s'", activityUpgraderDescriptor.getName()));
        activityUpgraderRegistry.addContribution(activityUpgraderDescriptor);
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (ACTIVITY_STREAM_FILTER_EP.equals(extensionPoint)) {
            unregisterActivityStreamFilter((ActivityStreamFilterDescriptor) contribution);
        } else if (ACTIVITY_STREAMS_EP.equals(extensionPoint)) {
            unregisterActivityStream((ActivityStream) contribution);
        } else if (ACTIVITY_VERBS_EP.equals(extensionPoint)) {
            unregisterActivityVerb((ActivityVerb) contribution);
        } else if (ACTIVITY_LINK_BUILDERS_EP.equals(extensionPoint)) {
            unregisterActivityLinkBuilder((ActivityLinkBuilderDescriptor) contribution);
        } else if (ACTIVITY_UPGRADERS_EP.equals(extensionPoint)) {
            unregisterActivityUpgrader((ActivityUpgraderDescriptor) contribution);
        }
    }

    private void unregisterActivityStreamFilter(ActivityStreamFilterDescriptor descriptor) {
        ActivityStreamFilter filter = descriptor.getActivityStreamFilter();
        String filterId = filter.getId();
        activityStreamFilters.remove(filterId);
        log.info("Unregistering activity stream filter with id " + filterId);
    }

    private void unregisterActivityStream(ActivityStream activityStream) {
        activityStreamRegistry.removeContribution(activityStream);
        log.info(String.format("Unregistering activity stream '%s'", activityStream.getName()));
    }

    private void unregisterActivityVerb(ActivityVerb activityVerb) {
        activityVerbRegistry.removeContribution(activityVerb);
        log.info(String.format("Unregistering activity verb '%s'", activityVerb.getVerb()));
    }

    private void unregisterActivityLinkBuilder(ActivityLinkBuilderDescriptor activityLinkBuilderDescriptor) {
        activityLinkBuilderRegistry.removeContribution(activityLinkBuilderDescriptor);
        log.info(String.format("Unregistering activity link builder '%s'", activityLinkBuilderDescriptor.getName()));
    }

    private void unregisterActivityUpgrader(ActivityUpgraderDescriptor activityUpgraderDescriptor) {
        activityUpgraderRegistry.removeContribution(activityUpgraderDescriptor);
        log.info(String.format("Unregistering activity upgrader '%s'", activityUpgraderDescriptor.getName()));
    }

}
