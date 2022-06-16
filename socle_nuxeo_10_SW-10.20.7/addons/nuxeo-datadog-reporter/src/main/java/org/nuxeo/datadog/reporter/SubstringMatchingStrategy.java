/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     dmetzler
 */
package org.nuxeo.datadog.reporter;

import com.google.common.collect.ImmutableSet;

/**
 * @since 10.1
 */
class SubstringMatchingStrategy implements StringMatchingStrategy {
    @Override
    public boolean containsMatch(ImmutableSet<String> matchExpressions, String metricName) {
        for (String matchExpression : matchExpressions) {
            if (metricName.contains(matchExpression)) {
                // just need to match on a single value - return as soon as we do
                return true;
            }
        }
        return false;
    }
}