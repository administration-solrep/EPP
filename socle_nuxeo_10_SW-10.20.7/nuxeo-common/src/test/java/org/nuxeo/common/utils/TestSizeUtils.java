/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Florent Guillaume
 */
package org.nuxeo.common.utils;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestSizeUtils {

    public long parse(String string) {
        return SizeUtils.parseSizeInBytes(string);
    }

    @Test
    public void testParseSize() throws Exception {
        assertEquals(0, parse("0"));
        assertEquals(1, parse("1"));
        assertEquals(1, parse("1B"));
        assertEquals(2 * 1024, parse("2K"));
        assertEquals(2 * 1024, parse("2 kB"));
        assertEquals(3 * 1024 * 1024, parse("3M"));
        assertEquals(3 * 1024 * 1024, parse("3MB"));
        assertEquals(4L * 1024 * 1024 * 1024, parse("4G"));
        assertEquals(4L * 1024 * 1024 * 1024, parse("4Gb"));
        assertEquals(5L * 1024 * 1024 * 1024 * 1024, parse("5TB"));
        assertEquals(5L * 1024 * 1024 * 1024 * 1024, parse("5 TB"));
    }

}
