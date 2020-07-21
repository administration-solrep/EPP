/*
 * (C) Copyright 2007-2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.platform.scheduler.core.service;

import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.scheduler.core.EventJob;
import org.nuxeo.ecm.platform.scheduler.core.interfaces.Schedule;
import org.nuxeo.ecm.platform.scheduler.core.interfaces.SchedulerRegistry;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.Extension;
import org.nuxeo.runtime.model.RuntimeContext;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;

/**
 * Schedule registry service.
 *
 * Since the cleanup of the quartz job is done when service is activated, ( see
 * see https://jira.nuxeo.com/browse/NXP-7303 ) in cluster mode, the schedules
 * contributions MUST be the same on all nodes. Due the fact that all jobs are
 * removed when service starts on a node it may be a short period with no
 * schedules in quartz table even other node is running.
 *
 */
public class SchedulerRegistryService extends DefaultComponent implements
        SchedulerRegistry {

    public static final ComponentName NAME = new ComponentName(
            "org.nuxeo.ecm.platform.scheduler.core.service.SchedulerService");

    private static final Log log = LogFactory.getLog(SchedulerRegistryService.class);

    private RuntimeContext bundle;

    private Scheduler scheduler;

    public void activate(ComponentContext context) throws Exception {
        log.debug("Activate");
        bundle = context.getRuntimeContext();

        // Find a scheduler
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        URL cfg = context.getRuntimeContext().getResource(
                "config/quartz.properties");
        if (cfg != null) {
            schedulerFactory.initialize(cfg.openStream());
        }
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        // server = MBeanServerFactory.createMBeanServer();
        // server.createMBean("org.quartz.ee.jmx.jboss.QuartzService",
        // quartzObjectName);

        // clean up all nuxeo jobs
        // https://jira.nuxeo.com/browse/NXP-7303
        GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupEquals("nuxeo") ;
        Set<JobKey> jobs = scheduler.getJobKeys(matcher );
        scheduler.deleteJobs(new ArrayList<JobKey>(jobs));
    }

    private Map<String, JobKey> jobKeys = new HashMap<String, JobKey>();

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        log.debug("Deactivate");
        scheduler.shutdown();
    }

    @Override
    public void registerExtension(Extension extension) {
        Object[] contribs = extension.getContributions();
        for (Object contrib : contribs) {
            Schedule schedule = (Schedule) contrib;
            registerSchedule(schedule);
        }
    }

    @Override
    public void unregisterExtension(Extension extension) {
        // do nothing to do ;
        // clean up will be done when service is activated
        // see https://jira.nuxeo.com/browse/NXP-7303
    }

    public RuntimeContext getContext() {
        return bundle;
    }

    @Override
    public void registerSchedule(Schedule schedule) {
        registerSchedule(schedule, null);
    }

    @Override
    public void registerSchedule(Schedule schedule,
            Map<String, Serializable> parameters) {
        log.info("Registering " + schedule);
        JobDetail job = new JobDetailImpl(schedule.getId(), "nuxeo", EventJob.class);
        JobDataMap map = job.getJobDataMap();
        map.put("eventId", schedule.getEventId());
        map.put("eventCategory", schedule.getEventCategory());
        map.put("username", schedule.getUsername());

        if (parameters != null) {
            map.putAll(parameters);
        }

        Trigger trigger;
        try {
            trigger = new CronTriggerImpl(schedule.getId(), "nuxeo",
                    schedule.getCronExpression());
        } catch (ParseException e) {
            log.error(String.format(
                    "invalid cron expresion '%s' for schedule '%s'",
                    schedule.getCronExpression(), schedule.getId()), e);
            return;
        }
        // This is useful when testing to avoid multiple threads:
        // trigger = new SimpleTrigger(schedule.getId(), "nuxeo");

        try {
            scheduler.scheduleJob(job, trigger);
            jobKeys.put(schedule.getId(), job.getKey());
        } catch (ObjectAlreadyExistsException e) {
            ; // when jobs are persisted in a database, the job should already
              // be there
        } catch (SchedulerException e) {
            log.error(String.format("failed to schedule job with id '%s': %s",
                    schedule.getId(), e.getMessage()), e);
        }
    }

    public boolean unregisterSchedule(String scheduleId) {
        log.info("Unregistering schedule with id" + scheduleId);
        try {
            return scheduler.deleteJob(jobKeys.get(scheduleId));
        } catch (SchedulerException e) {
            log.error(String.format("failed to unschedule job with '%s': %s",
                    scheduleId, e.getMessage()), e);
        }
        return false;
    }

    @Override
    public boolean unregisterSchedule(Schedule schedule) {
        return unregisterSchedule(schedule.getId());
    }

}
