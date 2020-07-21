/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql.jdbc;

import java.util.List;

/**
 * Service for the registration of QueryMaker classes.
 */
public interface QueryMakerService {

    void registerQueryMaker(QueryMakerDescriptor descriptor);

    void unregisterQueryMaker(QueryMakerDescriptor descriptor);

    List<Class<? extends QueryMaker>> getQueryMakers();

}
