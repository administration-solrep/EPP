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

package org.nuxeo.ecm.core.storage.sql.ra;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;

import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.repository.RepositoryDescriptor;
import org.nuxeo.ecm.core.repository.RepositoryService;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.security.SecurityManager;
import org.nuxeo.ecm.core.storage.Credentials;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.ConnectionSpecImpl;
import org.nuxeo.ecm.core.storage.sql.Repository;
import org.nuxeo.ecm.core.storage.sql.Session;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLRepository;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLSecurityManager;
import org.nuxeo.ecm.core.storage.sql.coremodel.SQLSession;
import org.nuxeo.ecm.core.storage.sql.net.MapperClientInfo;
import org.nuxeo.runtime.api.Framework;

/**
 * The connection factory delegates connection requests to the application
 * server {@link ConnectionManager}.
 * <p>
 * An instance of this class is returned to the application when a JNDI lookup
 * is done. This is the datasource equivalent of {@link SQLRepository}.
 *
 * @author Florent Guillaume
 */
public class ConnectionFactoryImpl implements Repository,
        org.nuxeo.ecm.core.model.Repository {

    private static final long serialVersionUID = 1L;

    private final ManagedConnectionFactoryImpl managedConnectionFactory;

    private final ConnectionManager connectionManager;

    private final String name;

    private SecurityManager securityManager;

    private SchemaManager schemaManager;

    private Reference reference;

    /**
     * This is {@code true} if the connectionManager comes from an application
     * server, or {@code false} if the {@link ConnectionFactoryImpl} was
     * constructed by application code and passed our manual
     * {@link ConnectionManagerImpl}.
     */
    private final boolean managed;

    private boolean firstAccessInitialized;

    private boolean servicesInitialized;

    public ConnectionFactoryImpl(
            ManagedConnectionFactoryImpl managedConnectionFactory,
            ConnectionManager connectionManager) {
        this.managedConnectionFactory = managedConnectionFactory;
        this.connectionManager = connectionManager;
        managed = !(connectionManager instanceof ConnectionManagerImpl);
        name = managedConnectionFactory.getName();
    }

    // NXP 3992 -- exposed this for clean shutdown on cluster
    public ManagedConnectionFactoryImpl getManagedConnectionFactory() {
        return managedConnectionFactory;
    }

    protected void initializeServices() {
        if (!servicesInitialized) {
            servicesInitialized = true;
            /*
             * Look up the configuration for this repository.
             */
            try {
                RepositoryService repositoryService = Framework.getService(RepositoryService.class);
                if (repositoryService != null) {
                    RepositoryDescriptor descriptor = repositoryService.getRepositoryManager().getDescriptor(
                            name);
                    if (descriptor.getSecurityManagerClass() != null) {
                        securityManager = descriptor.getSecurityManager();
                    }
                }
                if (securityManager == null) {
                    securityManager = new SQLSecurityManager();
                }
                schemaManager = Framework.getService(SchemaManager.class);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * ----- javax.resource.cci.ConnectionFactory -----
     */

    /**
     * Gets a new connection, with no credentials.
     *
     * @return the connection
     */
    @Override
    public Session getConnection() throws StorageException {
        ConnectionRequestInfo connectionRequestInfo = new ConnectionRequestInfoImpl();
        try {
            return (Session) connectionManager.allocateConnection(
                    managedConnectionFactory, connectionRequestInfo);
        } catch (StorageException e) {
            throw e;
        } catch (ResourceException e) {
            throw new StorageException(e);
        }
    }

    /**
     * Gets a new connection.
     *
     * @param connectionSpec the connection spec, containing credentials
     * @return the connection
     */
    @Override
    public Session getConnection(ConnectionSpec connectionSpec)
            throws StorageException {
        assert connectionSpec instanceof ConnectionSpecImpl;
        // encapsulate connectionSpec into internal connectionRequestInfo
        ConnectionRequestInfo connectionRequestInfo = new ConnectionRequestInfoImpl(
                (ConnectionSpecImpl) connectionSpec);
        try {
            return (Session) connectionManager.allocateConnection(
                    managedConnectionFactory, connectionRequestInfo);
        } catch (StorageException e) {
            throw e;
        } catch (ResourceException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public ResourceAdapterMetaData getMetaData() throws ResourceException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public RecordFactory getRecordFactory() throws ResourceException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /*
     * ----- javax.naming.Referenceable -----
     */

    @Override
    public Reference getReference() {
        return reference;
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    /*
     * ----- Repository -----
     */

    @Override
    public void close() throws StorageException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /*
     * ----- org.nuxeo.ecm.core.model.Repository -----
     */

    @Override
    public String getName() {
        return name;
    }

    @Override
    public org.nuxeo.ecm.core.model.Session getSession(
            Map<String, Serializable> context) throws DocumentException {
        ConnectionSpec connectionSpec;
        if (context == null) {
            connectionSpec = null;
        } else {
            synchronized (this) {
                if (!firstAccessInitialized) {
                    firstAccessInitialized = true;
                    // Allow AbstractSession (our caller) to send an
                    // initialization event.
                    context.put("REPOSITORY_FIRST_ACCESS", Boolean.TRUE);
                }
            }
            NuxeoPrincipal principal = (NuxeoPrincipal) context.get("principal");
            String username = principal == null ? (String) context.get("username")
                    : principal.getName();
            connectionSpec = new ConnectionSpecImpl(new Credentials(username,
                    null));
        }
        Session session;
        try {
            session = getConnection(connectionSpec);
        } catch (StorageException e) {
            throw new DocumentException(e);
        }
        return new SQLSession(session, this, context);
    }

    /**
     * @deprecated unused
     */
    @Override
    @Deprecated
    public org.nuxeo.ecm.core.model.Session getSession(long sessionId)
            throws DocumentException {
        throw new UnsupportedOperationException("unused");
    }

    @Override
    public SecurityManager getNuxeoSecurityManager() {
        initializeServices();
        return securityManager;
    }

    @Override
    public SchemaManager getTypeManager() {
        initializeServices();
        return schemaManager;
    }

    /*
     * Used only by unit tests. Shouldn't be in public API.
     */
    @Override
    public void initialize() {
    }

    /*
     * Used only by core MBean.
     */
    @Override
    public synchronized org.nuxeo.ecm.core.model.Session[] getOpenedSessions() {
        return new org.nuxeo.ecm.core.model.Session[0];
    }

    @Override
    public void shutdown() {
        managedConnectionFactory.shutdown();
    }

    @Override
    public int getStartedSessionsCount() {
        return 0;
    }

    @Override
    public int getClosedSessionsCount() {
        return 0;
    }

    /*
     * ----- org.nuxeo.ecm.core.model.RepositoryManagement -----
     */

    @Override
    public int getActiveSessionsCount() {
        return managedConnectionFactory.getActiveSessionsCount();
    }

    @Override
    public int clearCaches() {
        return managedConnectionFactory.clearCaches();
    }

    @Override
    public void processClusterInvalidationsNext() {
        managedConnectionFactory.processClusterInvalidationsNext();
    }

    @Override
    public boolean supportsTags() {
        return true;
    }

    @Override
    public void activateServer() {
        managedConnectionFactory.activateServer();

    }

    @Override
    public void deactivateServer() {
        managedConnectionFactory.deactivateServer();

    }

    @Override
    public Collection<MapperClientInfo> getClientInfos() {
       return managedConnectionFactory.getClientInfos();
    }

    @Override
    public boolean isServerActivated() {
        return managedConnectionFactory.isServerActivated();
    }

    @Override
    public String getServerURL() {
        return managedConnectionFactory.getServerURL();
    }
}
