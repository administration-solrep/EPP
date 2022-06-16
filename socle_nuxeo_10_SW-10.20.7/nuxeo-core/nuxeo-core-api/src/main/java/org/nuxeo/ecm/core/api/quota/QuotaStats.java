/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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

package org.nuxeo.ecm.core.api.quota;

/**
 * Adapter giving statistics about a given {@link org.nuxeo.ecm.core.api.DocumentModel}.
 *
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.5
 */
public interface QuotaStats {

    /**
     * Returns the intrinsic cardinal value of the underlying document.
     */
    long getIntrinsic();

    /**
     * Returns the cardinal value of all the children of the underlying document.
     */
    long getChildren();

    /**
     * Returns the cardinal value of all the descendants of the underlying document. plus the value of
     * {@link #getIntrinsic()}.
     */
    long getTotal();

}
