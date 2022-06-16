/*
 * (C) Copyright 2009-2017 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.runtime.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.datasource.DatasourceExceptionSorter.Configuration;
import org.nuxeo.runtime.jtajca.NuxeoContainer;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Nuxeo component allowing the JNDI registration of datasources by extension
 * point contributions.
 * <p>
 * For now only the internal Nuxeo JNDI server is supported.
 */
public class DataSourceComponent extends DefaultComponent {

    private final Log log = LogFactory.getLog(DataSourceComponent.class);

    static DataSourceComponent instance;

    public static final String DATASOURCES_XP = "datasources";

    public static final String ENV_CTX_NAME = "java:comp/env/";

    protected Map<String, DataSourceDescriptor> datasources = new HashMap<>();

    protected Map<String, DataSourceLinkDescriptor> links = new HashMap<>();

    protected final DatasourceExceptionSorter.Registry sorterRegistry = new DatasourceExceptionSorter.Registry();

    protected final PooledDataSourceRegistry poolRegistry = new PooledDataSourceRegistry();

    protected Context namingContext;

    @Override
    public void activate(ComponentContext context) {
        instance = this;
        datasources = new HashMap<>();
        links = new HashMap<>();
    }

    @Override
    public void deactivate(ComponentContext context) {
        super.deactivate(context);
        links = null;
        datasources = null;
        instance = null;
        //TODO should poolRegistry and sorterRegistry be removed?
    }

    @Override
    public void registerContribution(Object contrib, String extensionPoint, ComponentInstance component) {
        if (contrib instanceof DataSourceDescriptor) {
            addDataSource((DataSourceDescriptor) contrib);
        } else if (contrib instanceof DataSourceLinkDescriptor) {
            addDataSourceLink((DataSourceLinkDescriptor) contrib);
        } else if (contrib instanceof DatasourceExceptionSorter.Configuration) {
            sorterRegistry.addContribution((Configuration) contrib);
        } else {
            log.error("Wrong datasource extension type " + contrib.getClass().getName());
        }
    }

    @Override
    public void unregisterContribution(Object contrib, String extensionPoint, ComponentInstance component) {
        if (contrib instanceof DataSourceDescriptor) {
            removeDataSource((DataSourceDescriptor) contrib);
        } else if (contrib instanceof DataSourceLinkDescriptor) {
            removeDataSourceLink((DataSourceLinkDescriptor) contrib);
        } else if (contrib instanceof DatasourceExceptionSorter.Configuration) {
            sorterRegistry.removeContribution((Configuration) contrib);
        }
    }

    @Override
    public int getApplicationStartedOrder() {
        return -1000;
    }

    public boolean isStarted() {
        return namingContext != null;
    }

    @Override
    public void start(ComponentContext context) {
        if (namingContext != null) {
            return;
        }
        namingContext = NuxeoContainer.getRootContext();
        // allocate datasource sub-contexts
        Name comp;
        try {
            comp = new CompositeName(DataSourceHelper.getDataSourceJNDIPrefix());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        Context ctx = namingContext;
        for (int i = 0; i < comp.size(); i++) {
            try {
                ctx = (Context) ctx.lookup(comp.get(i));
            } catch (NamingException e) {
                try {
                    ctx = ctx.createSubcontext(comp.get(i));
                } catch (NamingException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        // bind datasources
        for (DataSourceDescriptor datasourceDesc : datasources.values()) {
            bindDataSource(datasourceDesc);
        }
        // bind links
        for (DataSourceLinkDescriptor linkDesc : links.values()) {
            bindDataSourceLink(linkDesc);
        }
    }

    @Override
    public void stop(ComponentContext context) {
        try {
            for (DataSourceLinkDescriptor desc : links.values()) {
                unbindDataSourceLink(desc);
            }
            for (DataSourceDescriptor desc : datasources.values()) {
                unbindDataSource(desc);
            }
            namingContext = null;
        } finally {
            namingContext = null;
        }
    }

    protected void addDataSource(DataSourceDescriptor contrib) {
        datasources.put(contrib.getName(), contrib);
        bindDataSource(contrib);
    }

    protected void removeDataSource(DataSourceDescriptor contrib) {
        unbindDataSource(contrib);
        datasources.remove(contrib.getName());
    }

    protected void bindDataSource(DataSourceDescriptor descr) {
        if (namingContext == null) {
            return;
        }
        log.info("Registering datasource: " + descr.getName());
        try {
            descr.bindSelf(namingContext);
        } catch (NamingException e) {
            log.error("Cannot bind datasource '" + descr.getName() + "' in JNDI", e);
        }
    }

    protected void unbindDataSource(DataSourceDescriptor descr) {
        if (namingContext == null) {
            return;
        }
        log.info("Unregistering datasource: " + descr.name);
        try {
            descr.unbindSelf(namingContext);
        } catch (NamingException cause) {
            log.error("Cannot unbind datasource '" + descr.name + "' in JNDI", cause);
        }
    }

    protected void addDataSourceLink(DataSourceLinkDescriptor contrib) {
        links.put(contrib.name, contrib);
        bindDataSourceLink(contrib);
    }

    protected void removeDataSourceLink(DataSourceLinkDescriptor contrib) {
        unbindDataSourceLink(contrib);
        links.remove(contrib.name);
    }

    protected void bindDataSourceLink(DataSourceLinkDescriptor descr) {
        if (namingContext == null) {
            return;
        }
        log.info("Registering DataSourceLink: " + descr.name);
        try {
            descr.bindSelf(namingContext);
        } catch (NamingException e) {
            log.error("Cannot bind DataSourceLink '" + descr.name + "' in JNDI", e);
        }
    }

    protected void unbindDataSourceLink(DataSourceLinkDescriptor descr) {
        if (namingContext == null) {
            return;
        }
        log.info("Unregistering DataSourceLink: " + descr.name);
        try {
            descr.unbindSelf(namingContext);
        } catch (NamingException e) {
            log.error("Cannot unbind DataSourceLink '" + descr.name + "' in JNDI", e);
        }
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter.isAssignableFrom(PooledDataSourceRegistry.class)) {
            return adapter.cast(poolRegistry);
        }
        return super.getAdapter(adapter);
    }

}
