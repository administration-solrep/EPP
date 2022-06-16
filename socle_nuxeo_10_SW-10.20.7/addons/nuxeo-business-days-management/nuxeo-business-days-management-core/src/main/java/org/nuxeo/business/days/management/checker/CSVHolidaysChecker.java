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

package org.nuxeo.business.days.management.checker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Holidays checker implementation which loads the holidays dates in a CVS file. The extension point "configuration" of
 * the component "org.nuxeo.business.days.management.checker.csv" allows to specify where is located the CSV file.
 *
 * @author Nicolas Ulrich
 */
public class CSVHolidaysChecker extends DefaultComponent implements HolidaysChecker {

    private static final Log log = LogFactory.getLog(CSVHolidaysChecker.class);

    private static final FastDateFormat formater = FastDateFormat.getInstance("dd/MM/yyyy");

    private static Set<Date> dates = new HashSet<Date>();

    public boolean isHoliday(Date date) {
        return dates.contains(date);
    }

    /**
     * Read the CVS file in order to load hollidays.cvs
     */
    private void initDates(String fileName, boolean isEmbedded) {

        if (fileName == null) {
            return;
        }

        try (BufferedReader buffer = new BufferedReader(getReader(fileName, isEmbedded))) {

            String line;
            while ((line = buffer.readLine()) != null) {
                dates.add(formater.parse(line));
            }

        } catch (IOException e) {
            log.error("Unable to read the CSV file", e);
        } catch (ParseException e) {
            log.error("The CSV file is not formatted", e);
        }

    }

    protected Reader getReader(String fileName, boolean isEmbedded) throws IOException {
        if (isEmbedded) {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            return new InputStreamReader(is);
        } else {
            return new FileReader(fileName);
        }
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {

        if (extensionPoint.equals("configuration")) {

            CSVConfigurationDescriptor conf = ((CSVConfigurationDescriptor) contribution);
            initDates(conf.csvFilePath, conf.embedded);

        }
    }

}
