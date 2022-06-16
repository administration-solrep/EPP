/*
 * (C) Copyright 2011-2016 Nuxeo SA (http://nuxeo.com/) and others.
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
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public class TweetActivityStreamFilter implements ActivityStreamFilter {

    public static final String ID = "TweetActivityStreamFilter";

    public static final String TWEET_VERB = "tweet";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isInterestedIn(Activity activity) {
        return true;
    }

    @Override
    public void handleNewActivity(ActivityStreamService activityStreamService, Activity activity) {
        if (TWEET_VERB.equals(activity.getVerb())) {
            EntityManager em = ((ActivityStreamServiceImpl) activityStreamService).getEntityManager();
            TweetActivity tweetActivity = new TweetActivity();
            tweetActivity.setActivityId(activity.getId());
            tweetActivity.setSeenBy("Bob");
            em.persist(tweetActivity);
            tweetActivity = new TweetActivity();
            tweetActivity.setActivityId(activity.getId());
            tweetActivity.setSeenBy("Joe");
            em.persist(tweetActivity);
            tweetActivity = new TweetActivity();
            tweetActivity.setActivityId(activity.getId());
            tweetActivity.setSeenBy("John");
            em.persist(tweetActivity);
        }
    }

    @Override
    public void handleRemovedActivities(ActivityStreamService activityStreamService, ActivitiesList activities) {
        EntityManager em = ((ActivityStreamServiceImpl) activityStreamService).getEntityManager();
        Query query = em.createQuery("delete from Tweet tweet where tweet.activityId in (:activityIds)");
        query.setParameter("activityIds", activities.toActivityIds());
        query.executeUpdate();
    }

    @Override
    public void handleRemovedActivityReply(ActivityStreamService activityStreamService, Activity activity,
            ActivityReply activityReply) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public ActivitiesList query(ActivityStreamService activityStreamService, Map<String, Serializable> parameters,
            long offset, long limit) {
        if (parameters.containsKey("seenBy")) {
            String seenBy = (String) parameters.get("seenBy");
            EntityManager em = ((ActivityStreamServiceImpl) activityStreamService).getEntityManager();
            Query query = em.createQuery(
                    "select activity from Tweet tweet, Activity activity where tweet.seenBy=:seenBy and tweet.activityId = activity.id");
            query.setParameter("seenBy", seenBy);
            return new ActivitiesListImpl(query.getResultList());
        }
        return new ActivitiesListImpl();
    }

}
