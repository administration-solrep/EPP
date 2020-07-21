/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume, jcarsique
 */

package org.nuxeo.runtime.jtajca;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geronimo.connector.outbound.AbstractConnectionManager;
import org.apache.geronimo.connector.outbound.GenericConnectionManager;
import org.apache.geronimo.connector.outbound.SubjectSource;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.PartitionedPool;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.PoolingSupport;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.TransactionSupport;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.XATransactions;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTrackingCoordinator;
import org.apache.geronimo.transaction.GeronimoUserTransaction;
import org.apache.geronimo.transaction.manager.RecoverableTransactionManager;
import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;




/**
 * Internal helper for the Nuxeo-defined transaction manager and connection
 * manager.
 * <p>
 * This code is called by the factories registered through JNDI, or by unit
 * tests mimicking JNDI bindings.
 */
public class NuxeoContainer {

    public static final String JNDI_TRANSACTION_MANAGER = "java:comp/TransactionManager";

    public static final String JNDI_USER_TRANSACTION = "java:comp/UserTransaction";

    public static final String JNDI_NUXEO_CONNECTION_MANAGER = "java:comp/NuxeoConnectionManager";

    private static final Log log = LogFactory.getLog(NuxeoContainer.class);
    
    private static RecoverableTransactionManager transactionManager;

    private static UserTransaction userTransaction;

    private static ConnectionManagerWrapper connectionManager;

    private static boolean isInstalled = false;

    private NuxeoContainer() {
    }

    /**
     * Install transaction and connection management "by hand" if the container
     * didn't do it using file-based configuration. Binds the names in JNDI.
     */
    public static void install() throws NamingException {
        install(new TransactionManagerConfiguration(),
                new ConnectionManagerConfiguration());
    }

    /**
     * Install transaction and connection management "by hand" if the container
     * didn't do it using file-based configuration. Binds the names in JNDI.
     *
     * @param txconfig the transaction manager configuration
     * @param cmconfig the connection manager configuration
     *
     * @since 5.4.2
     */
    public static synchronized void install(TransactionManagerConfiguration txconfig,
            ConnectionManagerConfiguration cmconfig) throws NamingException {
        initTransactionManager(txconfig);
        initConnectionManager(cmconfig);
        jndiBind();
        isInstalled = true;
    }

    public static synchronized boolean isInstalled() {
        return isInstalled;
    }

    public static synchronized void uninstall() {
        if (!isInstalled) {
            return;
        }
        isInstalled = false;
        try {
            InitialContext context = new InitialContext();
            context.unbind(JNDI_TRANSACTION_MANAGER);
            context.unbind(JNDI_USER_TRANSACTION);
            context.unbind(JNDI_NUXEO_CONNECTION_MANAGER);
        } catch (Exception e) {
            // do nothing
        } finally {
            transactionManager = null;
            userTransaction = null;
            connectionManager = null;
        }
    }

    protected static void jndiBind() throws NamingException {
        InitialContext context = new InitialContext();
        context.rebind(JNDI_TRANSACTION_MANAGER, getTransactionManager());
        context.rebind(JNDI_USER_TRANSACTION, getUserTransaction());
        context.rebind(JNDI_NUXEO_CONNECTION_MANAGER, getConnectionManager());
    }

    /** @deprecated use {@link #install} instead. */
    @Deprecated
    public static void initTransactionManagement() throws NamingException {
        install();
    }

    /**
     * Gets the transaction manager used by the container.
     *
     * @return the transaction manager
     */
    public static TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * Gets the user transaction used by the container.
     *
     * @return the user transaction
     */
    public static UserTransaction getUserTransaction() throws NamingException {
        if (transactionManager == null) {
            initTransactionManager();
        }
        return userTransaction;
    }

    /**
     * Gets the Nuxeo connection manager used by the container.
     *
     * @return the connection manager
     */
    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    protected static void initTransactionManager() throws NamingException {
        // doing a lookup will initialize it with its configuration parameters
        TransactionHelper.lookupTransactionManager();
    }

    public static synchronized void initTransactionManager(
            TransactionManagerConfiguration config) {
        if (transactionManager == null) {
            transactionManager = createTransactionManager(config);
            userTransaction = createUserTransaction(transactionManager);
        }
    }

    public static synchronized void initConnectionManager(
            ConnectionManagerConfiguration config) throws NamingException {
        if (transactionManager == null) {
            initTransactionManager();
        }
        if (connectionManager == null) {
            AbstractConnectionManager cm = createConnectionManager(
                    transactionManager, config);
            connectionManager = new ConnectionManagerWrapper(cm, config);
        }
    }

    public static synchronized void resetConnectionManager() throws Exception {
        ConnectionManagerWrapper cm = connectionManager;
        if (cm == null) {
            return;
        }
        cm.reset();
    }
    
    
    protected static String getMD5Hash(byte[] message) {
        if (message == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message);
            byte[] md5hash = md.digest();
            StringBuilder builder = new StringBuilder();
            for (byte b : md5hash) {
                builder.append(Integer.toString((b & 0xff) + 0x100, 16)
                        .substring(1));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected static byte[] generateTmId(){
        
        byte[] hostid;
        try {
            hostid = InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException e) {
            hostid = new byte[]{127, 0, 0, 1};
        }
        
        final int hashPartLength = 10;
        byte[] tmid = new byte[6 + hostid.length + hashPartLength];
        int uid = System.identityHashCode(hostid);
        tmid[0] = (byte) uid;
        tmid[1] = (byte) (uid >>> 8);
        tmid[2] = (byte) (uid >>> 16);
        tmid[3] = (byte) (uid >>> 24);

        byte[] entropy = new byte[2];
        new Random().nextBytes(entropy);
        tmid[4] = entropy[0];
        tmid[5] = entropy[1];

        int offset = 6;
        System.arraycopy(hostid, 0, tmid, offset, hostid.length);
        offset += hostid.length;
        
        // complete 
        String nuxeoconfstr;
        if(Framework.isTestModeSet()) {
        	nuxeoconfstr = "";
        } else {
        	nuxeoconfstr = Framework.getProperty("nuxeo.conf");
        }
        
        String nuxeoconfhash = getMD5Hash(nuxeoconfstr.getBytes());
        
        System.arraycopy(nuxeoconfhash.getBytes(), 0, tmid, offset, hashPartLength);
        offset += hashPartLength;
        
        StringBuilder strBuilder = new StringBuilder("tmId = [");
        for(int i = 0; i < offset; ++i) {
            strBuilder.append(tmid[i]).append(",");
        }
        strBuilder.append("]");        
        log.info(strBuilder.toString());

        return tmid;
    }

    protected static RecoverableTransactionManager createTransactionManager(
            TransactionManagerConfiguration config) {
        try {
        	final byte[] tmId = generateTmId();
            return new TransactionManagerImpl(config.transactionTimeoutSeconds, new XidFactoryImpl(tmId), null);
        } catch (Exception e) {
            // failed in recovery somewhere
            throw new RuntimeException(e.toString(), e);
        }
    }

    protected static UserTransaction createUserTransaction(
            TransactionManager transactionManager) {
        return new GeronimoUserTransaction(transactionManager);
    }

    /**
     * Creates a Geronimo pooled connection manager using a Geronimo transaction
     * manager.
     * <p>
     * The pool uses the transaction manager for recovery, and when using
     * XATransactions for cache + enlist/delist.
     */
    protected static AbstractConnectionManager createConnectionManager(
            RecoverableTransactionManager transactionManager,
            ConnectionManagerConfiguration config) {
        TransactionSupport transactionSupport = new XATransactions(
                config.useTransactionCaching, config.useThreadCaching);
        // note: XATransactions -> TransactionCachingInterceptor ->
        // ConnectorTransactionContext casts transaction to Geronimo's
        // TransactionImpl (from TransactionManagerImpl)
        PoolingSupport poolingSupport = new PartitionedPool(config.maxPoolSize,
                config.minPoolSize, config.blockingTimeoutMillis,
                config.idleTimeoutMinutes, config.matchOne, config.matchAll,
                config.selectOneNoMatch,
                config.partitionByConnectionRequestInfo,
                config.partitionBySubject);
        final Subject subject = new Subject();
        SubjectSource subjectSource = new SubjectSource() {
            @Override
            public Subject getSubject() {
                return subject;
            }
        };
        ConnectionTrackingCoordinator connectionTracker = new ConnectionTrackingCoordinator();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); // NuxeoContainer.class.getClassLoader();
        return new GenericConnectionManager(transactionSupport, poolingSupport,
                subjectSource, connectionTracker, transactionManager,
                config.name, classLoader);
    }

    public static class TransactionManagerConfiguration {
        public int transactionTimeoutSeconds = 600;

        public void setTransactionTimeoutSeconds(int transactionTimeoutSeconds) {
            this.transactionTimeoutSeconds = transactionTimeoutSeconds;
        }
    }

    /**
     * Wraps a Geronimo ConnectionManager and adds a {@link #reset} method to
     * flush the pool.
     */
    public static class ConnectionManagerWrapper implements ConnectionManager {
        private static final long serialVersionUID = 1L;

        protected AbstractConnectionManager cm;

        protected final ConnectionManagerConfiguration config;

        public ConnectionManagerWrapper(AbstractConnectionManager cm,
                ConnectionManagerConfiguration config) {
            this.cm = cm;
            this.config = config;
        }

        @Override
        public Object allocateConnection(
                ManagedConnectionFactory managedConnectionFactory,
                ConnectionRequestInfo connectionRequestInfo)
                throws ResourceException {
            return cm.allocateConnection(managedConnectionFactory,
                    connectionRequestInfo);
        }

        public void reset() throws Exception {
            cm.doStop();
            cm = createConnectionManager(transactionManager, config);
        }
    }

    public static class ConnectionManagerConfiguration {

        public String name = "NuxeoConnectionManager";

        // transaction

        public boolean useTransactionCaching = true;

        public boolean useThreadCaching = true;

        // pool

        public boolean matchOne = true; // unused by Geronimo?

        public boolean matchAll = true;

        public boolean selectOneNoMatch = false;

        public boolean partitionByConnectionRequestInfo = false;

        public boolean partitionBySubject = true;

        public int maxPoolSize = 20;

        public int minPoolSize = 0;

        public int blockingTimeoutMillis = 100;

        public int idleTimeoutMinutes = 0; // no timeout

        public void setName(String name) {
            this.name = name;
        }

        public void setUseTransactionCaching(boolean useTransactionCaching) {
            this.useTransactionCaching = useTransactionCaching;
        }

        public void setUseThreadCaching(boolean useThreadCaching) {
            this.useThreadCaching = useThreadCaching;
        }

        public void setMatchOne(boolean matchOne) {
            this.matchOne = matchOne;
        }

        public void setMatchAll(boolean matchAll) {
            this.matchAll = matchAll;
        }

        public void setSelectOneNoMatch(boolean selectOneNoMatch) {
            this.selectOneNoMatch = selectOneNoMatch;
        }

        public void setPartitionByConnectionRequestInfo(
                boolean partitionByConnectionRequestInfo) {
            this.partitionByConnectionRequestInfo = partitionByConnectionRequestInfo;
        }

        public void setPartitionBySubject(boolean partitionBySubject) {
            this.partitionBySubject = partitionBySubject;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public void setMinPoolSize(int minPoolSize) {
            this.minPoolSize = minPoolSize;
        }

        public void setBlockingTimeoutMillis(int blockingTimeoutMillis) {
            this.blockingTimeoutMillis = blockingTimeoutMillis;
        }

        public void setIdleTimeoutMinutes(int idleTimeoutMinutes) {
            this.idleTimeoutMinutes = idleTimeoutMinutes;
        }

    }

}
