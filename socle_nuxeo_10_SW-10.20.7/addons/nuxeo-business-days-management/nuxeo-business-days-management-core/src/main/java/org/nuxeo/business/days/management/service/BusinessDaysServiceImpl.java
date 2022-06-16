/*
 * (C) Copyright 2006-2010 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     <a href="mailto:nulrich@nuxeo.com">Nicolas Ulrich</a>
 *
 */

package org.nuxeo.business.days.management.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.business.days.management.checker.HolidaysChecker;
import org.nuxeo.business.days.management.service.BusinessDaysService;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author Nicolas Ulrich
 */
public class BusinessDaysServiceImpl extends DefaultComponent implements BusinessDaysService {

    private final Map<String, Integer> values = new HashMap<String, Integer>();

    private HolidaysChecker check;

    public Date getLimitDate(String label, Date from) {

        if (!values.containsKey(label)) {
            return null;
        }

        int duration = values.get(label);

        Calendar fromCalendar = GregorianCalendar.getInstance();
        fromCalendar.setTime(from);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0); // set hour to midnight
        fromCalendar.set(Calendar.MINUTE, 0); // set minute in hour
        fromCalendar.set(Calendar.SECOND, 0); // set second in minute
        fromCalendar.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < duration; i++) {

            fromCalendar.add(Calendar.DAY_OF_YEAR, 1);

            // If this is a non working day, increase the limit
            if (isHolidayDay(fromCalendar)) {
                duration++;
            }

        }

        return fromCalendar.getTime();
    }

    private boolean isHolidayDay(Calendar day) {
        if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                || (check != null && check.isHoliday(day.getTime())))
            return true;

        return false;

    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {

        if (extensionPoint.equals("duration")) {

            DurationDescriptor duration = ((DurationDescriptor) contribution);
            values.put(duration.label, duration.numberOfDays);

        } else if (extensionPoint.equals("holidaysChecker")) {

            HolidaysCheckerDescriptor distributionType = ((HolidaysCheckerDescriptor) contribution);
            try {
                check = (HolidaysChecker) Class.forName(distributionType.clazz).newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {

        if (extensionPoint.equals("duration")) {

            DurationDescriptor duration = ((DurationDescriptor) contribution);
            values.remove(duration.label);

        }

    }

}
