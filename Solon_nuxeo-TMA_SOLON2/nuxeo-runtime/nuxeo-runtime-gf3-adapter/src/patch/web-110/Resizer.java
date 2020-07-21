/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.enterprise.resource.pool.resizer;

import com.sun.enterprise.resource.ResourceHandle;
import com.sun.enterprise.resource.ResourceState;
import com.sun.enterprise.resource.pool.PoolProperties;
import com.sun.appserv.connectors.internal.api.PoolingException;
import com.sun.enterprise.resource.pool.ResourceHandler;
import com.sun.enterprise.resource.pool.datastructure.DataStructure;
import com.sun.logging.LogDomains;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resizer to remove unusable connections, maintain steady-pool <br>
 * <code>
 * Remove all invalid and idle resources, as a result one of the following may happen<br>
 * i)   equivalent to "pool-resize" quantity of resources are removed<br>
 * ii)  less than "pool-reize" quantity of resources are removed<br>
 * remove more resources to match pool-resize quantity, atmost scale-down till steady-pool-size<br>
 * iii) more than "pool-resize" quantity of resources are removed<br>
 * (1) if pool-size is less than steady-pool-size, bring it back to steady-pool-size.<br>
 * (2) if pool-size is greater than steady-pool-size, don't do anything.<br></code>
 *
 * @author Jagadish Ramu
 */
public class Resizer extends TimerTask {
    /*
    TODO V3 can we have a flag to disable validation during resizer (since validation=true will any way validate resources)
    */
    private String poolName;
    private DataStructure ds;
    private PoolProperties pool;
    private ResourceHandler handler;

    public Resizer(String poolName, DataStructure ds, PoolProperties pp, ResourceHandler handler) {
        this.poolName = poolName;
        this.ds = ds;
        this.pool = pp;
        this.handler = handler;
    }

    @Override
    public void run() {
        debug("Resizer for pool " + poolName);
        resizePool(true);
    }

    /**
     * Resize the pool.
     *
     * @param forced when force is true, scale down the pool.
     */
    public void resizePool(boolean forced) {

        //If the wait queue is NOT empty, don't do anything.
        if (pool.getWaitQueueLength() > 0) {
            return;
        }

        //remove invalid and idle resource(s)
        int noOfResourcesRemoved = removeIdleAndInvalidResources();
        int poolScaleDownQuantity = pool.getResizeQuantity() - noOfResourcesRemoved;

        //scale down pool by atmost "resize-quantity"
        scaleDownPool(poolScaleDownQuantity, forced);

        //ensure that steady-pool-size is maintained
        ensureSteadyPool();

        debug("No. of resources held for pool [ " + poolName + " ] : " + ds.getResourcesSize());
    }

    /**
     * Makes sure that steady pool size is maintained after all idle-timed-out,
     * invalid and scale-down resource removals.
     */
    private void ensureSteadyPool() {
        if (ds.getResourcesSize() < pool.getSteadyPoolSize()) {
            // Create resources to match the steady pool size
            for (int i = ds.getResourcesSize(); i < pool.getSteadyPoolSize(); i++) {
                try {
                    handler.createResourceAndAddToPool();
                } catch (PoolingException ex) {
                    Object[] params = new Object[]{poolName, ex.getMessage()};
                    _logger.log(Level.WARNING, "resource_pool.resize_pool_error", params);
                }
            }
        }
    }

    /**
     * Scales down pool by a <code>size &lt;= pool-resize-quantity</code>
     *
     * @param forced            scale-down only when forced
     * @param scaleDownQuantity no. of resources to remove
     */
    private void scaleDownPool(int scaleDownQuantity, boolean forced) {

        if (pool.getResizeQuantity() > 0 && forced) {

            scaleDownQuantity = (scaleDownQuantity <= (ds.getResourcesSize() - pool.getSteadyPoolSize())) ? scaleDownQuantity : 0;

            ResourceHandle h;
            while (scaleDownQuantity > 0 && ((h = ds.getResource()) != null)) {
                ds.removeResource(h);
                scaleDownQuantity--;
            }
        }
    }

    /**
     * Get the free connections list from the pool, remove idle-timed-out resources
     * and then invalid resources.
     *
     * @return int number of resources removed
     */
    private int removeIdleAndInvalidResources() {

        int poolSizeBeforeRemoval = ds.getResourcesSize();
        int noOfResourcesRemoved;
        //Find all Connections that are free/not-in-use
        ResourceState state;
        int size = ds.getFreeListSize();
        // let's cache the current time since precision is not required here.
        long currentTime = System.currentTimeMillis();

        //iterate through all thre active resources to find idle-time lapsed ones.
        ResourceHandle h;
        Set<ResourceHandle> activeHandles = new HashSet<ResourceHandle>();
        while ((h = ds.getResource()) != null ) {
            state = h.getResourceState();
            //remove all idle-time lapsed resources.
            if (currentTime - state.getTimestamp() > pool.getIdleTimeout()) {
                ds.removeResource(h);
            } else {
                //returning resource at this point may generate infintie loops
                //ds.returnResource(h);
                activeHandles.add(h);
            }
        }

        //remove invalid resources from the free (active) resources list.
        //Since the whole pool is not locked, it may happen that some of these resources may be
        //given to applications.
        removeInvalidResources(activeHandles);

        //These statistic computations will work fine as long as resizer locks the pool throughout its operations.
        debug("Number of Idle resources freed for pool [ " + poolName + " ] - " +
                (size - activeHandles.size()));
        debug("Number of Invalid resources removed for pool [ " + poolName + " ] - " +
                (activeHandles.size() - ds.getFreeListSize()));
        noOfResourcesRemoved = poolSizeBeforeRemoval - ds.getResourcesSize();
        return noOfResourcesRemoved;
    }

    /**
     * Removes invalid resource handles in the pool while resizing the pool.
     * Uses the Connector 1.5 spec 6.5.3.4 optional RA feature to obtain
     * invalid ManagedConnections
     *
     * @param freeConnectionsToValidate Set of free connections
     */
    private void removeInvalidResources(Set<ResourceHandle> freeConnectionsToValidate) {
        try {
            debug("Sending a set of free connections to RA, " +
                    "of size : " + freeConnectionsToValidate.size());
            int invalidConnectionsCount = 0;
            Set<ResourceHandle> validResources = new HashSet<ResourceHandle>();
            try {
                for (ResourceHandle handle : freeConnectionsToValidate) {
                    Set connectionsToTest = new HashSet();
                    connectionsToTest.add(handle.getResource());
                    Set invalidConnections = handler.getInvalidConnections(connectionsToTest);
                    if (invalidConnections != null && invalidConnections.size() > 0) {
                       for (Object o : invalidConnections) {
                          ManagedConnection invalidConnection = (ManagedConnection)o;
                          if(invalidConnection.equals(handle.getResource())){
                            ds.removeResource(handle);
                            handler.invalidConnectionDetected(handle);
                            invalidConnectionsCount++;
                          }else{
                            //TODO V3 log fine - cannot happen
                          }
                        }
                    } else {
                       //valid resource, return to pool
                       validResources.add(handle);
                    }
                }
            } finally {
                for(ResourceHandle resourceHandle : validResources){
                    ds.returnResource(resourceHandle);
                }
                validResources.clear();
                debug("No. of invalid connections received from RA : " + invalidConnectionsCount);
            }
        } catch (ResourceException re) {
            _logger.log(Level.FINE, "ResourceException while trying to get invalid connections from MCF", re);
        } catch (Exception e) {
            _logger.log(Level.FINE, "Exception while trying to get invalid connections from MCF", e);
        }
    }

    private static final Logger _logger = LogDomains.getLogger(Resizer.class, LogDomains.RSR_LOGGER);

    private static void debug(String debugStatement) {
        if (_logger.isLoggable(Level.FINE))
            _logger.log(Level.FINE, debugStatement);
    }
}
