/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thomas Roger (troger@nuxeo.com)
 */

package org.nuxeo.ecm.activity;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Dummy {@link ActivityUpgrader} replacing all activities actors by 'Dummy Actor'.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.7
 */
public class DummyActivityUpgrader extends AbstractActivityUpgrader {

    @Override
    public void doUpgrade(ActivityStreamService activityStreamService) {
        EntityManager em = ((ActivityStreamServiceImpl) activityStreamService).getEntityManager();
        Query query = em.createQuery("select activity from Activity activity");
        ActivitiesList activities = new ActivitiesListImpl(query.getResultList());

        for (Activity activity : activities) {
            activity.setActor("Dummy Actor");
            em.merge(activity);
        }
    }

}
