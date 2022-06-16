/*
 * (C) Copyright 2012-2013 Nuxeo SA (http://nuxeo.com/) and others.
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
 */
package org.nuxeo.connect.tools.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

/**
 * Run reports and feed the output with, exposed as a service in the runtime.
 *
 * @since 8.3
 */
public interface ReportRunner {

    void run(OutputStream out, Set<String> names) throws IOException;

    Set<String> list();
}
