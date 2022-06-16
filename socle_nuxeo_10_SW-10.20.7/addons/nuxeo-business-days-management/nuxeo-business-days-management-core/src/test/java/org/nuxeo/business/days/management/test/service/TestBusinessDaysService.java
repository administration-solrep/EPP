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

package org.nuxeo.business.days.management.test.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.nuxeo.business.days.management.service.BusinessDaysService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

/**
 * @author Nicolas Ulrich
 */
public class TestBusinessDaysService extends NXRuntimeTestCase {

    @Before
    public void setUp() throws Exception {

        super.setUp();

        deployBundle("org.nuxeo.business.days.management.api");
        deployBundle("org.nuxeo.business.days.management.core");
        deployBundle("org.nuxeo.business.days.management.core.test");

    }

    @Test
    public void testLabel() {

        BusinessDaysService ms = Framework.getService(BusinessDaysService.class);
        assertNotNull(ms);

        Date currentdate = GregorianCalendar.getInstance().getTime();

        assertNotNull(ms.getLimitDate("courriel", currentdate));
        assertNull(ms.getLimitDate("fakeLabel", currentdate));

    }

    /**
     * Test a full week
     */
    @Test
    public void testLimiteDate() {

        assertEquals(8, getLimit(1));
        assertEquals(8, getLimit(2));
        assertEquals(8, getLimit(3));
        assertEquals(11, getLimit(4));
        assertEquals(13, getLimit(5));
        assertEquals(15, getLimit(6));
        assertEquals(18, getLimit(7));
        assertEquals(19, getLimit(8));

    }

    /**
     * @param dayOfMonth
     * @return
     */
    private int getLimit(int dayOfMonth) {

        BusinessDaysService ms = Framework.getService(BusinessDaysService.class);

        Calendar startCalendar = GregorianCalendar.getInstance();
        startCalendar.set(2010, Calendar.JANUARY, dayOfMonth, 0, 0, 0);

        Date limitDate = ms.getLimitDate("courriel", startCalendar.getTime());

        Calendar limitCalendar = GregorianCalendar.getInstance();
        limitCalendar.setTime(limitDate);

        return limitCalendar.get(Calendar.DAY_OF_MONTH);

    }
}
