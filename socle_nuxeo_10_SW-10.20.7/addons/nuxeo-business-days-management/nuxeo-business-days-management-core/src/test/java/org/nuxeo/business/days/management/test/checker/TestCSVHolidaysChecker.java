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

package org.nuxeo.business.days.management.test.checker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.business.days.management.checker.CSVHolidaysChecker;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

/**
 * @author Nicolas Ulrich
 */
public class TestCSVHolidaysChecker extends NXRuntimeTestCase {

    @Before
    public void setUp() throws Exception {

        super.setUp();

        deployBundle("org.nuxeo.business.days.management.api");
        deployBundle("org.nuxeo.business.days.management.core");
        deployBundle("org.nuxeo.business.days.management.core.test");

    }

    @Test
    public void testLabel() throws ParseException {

        FastDateFormat formater = FastDateFormat.getInstance("dd/MM/yyyy");

        CSVHolidaysChecker check = new CSVHolidaysChecker();
        assertTrue(check.isHoliday(formater.parse("12/01/2010")));
        assertFalse(check.isHoliday(formater.parse("13/01/2010")));
        assertTrue(check.isHoliday(formater.parse("14/01/2010")));

    }
}
