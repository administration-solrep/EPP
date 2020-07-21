/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.runtime;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentManager;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.RuntimeContext;
import org.osgi.framework.Bundle;

/**
 * The runtime service: a singleton object that provides access to the Nuxeo
 * Runtime. The runtime service must be started before any other runtime
 * component or object that accesses the runtime.
 * <p>
 * This service is usually implemented for each target platform where Nuxeo
 * Runtime should run.
 * <p>
 * It is recommended to extend the {@link AbstractRuntimeService} class instead
 * of directly implementing this interface.
 * <p>
 * After the runtime service was initialized, it may be accessed through the
 * facade class {@link org.nuxeo.runtime.api.Framework}.
 * <p>
 * See: {@link org.nuxeo.runtime.api.Framework}
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface RuntimeService {

    /**
     * Starts the runtime.
     *
     * @throws Exception if any exception occurred during startup
     */
    void start() throws Exception;

    /**
     * Stops the runtime.
     *
     * @throws Exception if any exception occurred during shutdown
     */
    void stop() throws Exception;

    /**
     * Tests whether or not the runtime is started.
     *
     * @return true if the runtime is started, false otherwise
     */
    boolean isStarted();

    /**
     * Gets the home directory of the runtime.
     *
     * @return the home directory
     */
    File getHome();

    /**
     * Gets the name of this runtime service.
     *
     * @return the runtime service name
     */
    String getName();

    /**
     * Gets the description of this runtime service.
     *
     * @return the runtime service description
     */
    String getDescription();

    /**
     * Gets the version of this runtime service.
     *
     * @return the runtime service version
     */
    Version getVersion();

    /**
     * Gets runtime service properties.
     *
     * @return the runtime properties
     */
    Properties getProperties();

    /**
     * Reread all property files loaded at startup.
     *
     * @throws Exception
     */
    void reloadProperties() throws Exception;

    /**
     * Gets a runtime service property given its name.
     *
     * @param name the property name
     * @return the property value if any or null if none
     */
    String getProperty(String name);

    /**
     * Gets a property value using a default value if the property was not set.
     *
     * @param name the property name
     * @param defaultValue the default value to use when the property doesn't
     *            exists
     * @return the property value
     */
    String getProperty(String name, String defaultValue);

    /**
     * Replaces any substring in the form <code>${property.name}</code> with the
     * corresponding runtime property value if any, otherwise leaves the
     * substring unchanged.
     *
     * @param expression the expression to process
     * @return the expanded expression
     */
    String expandVars(String expression);

    /**
     * Gets the component manager.
     *
     * @return the component manager
     */
    ComponentManager getComponentManager();

    /**
     * Gets a component given its name as a string.
     *
     * @param name the component name as a string
     * @return the component
     */
    Object getComponent(String name);

    /**
     * Gets a component given its name.
     *
     * @param name the component name
     * @return the component, or null if no such component was registered
     */
    Object getComponent(ComponentName name);

    /**
     * Gets a component implementation instance given its name as a string.
     *
     * @param name the component name as a string
     * @return the component
     */
    ComponentInstance getComponentInstance(String name);

    /**
     * Gets a component implementation instance given its name.
     *
     * @param name the component name
     * @return the component or null if no such component was registered
     */
    ComponentInstance getComponentInstance(ComponentName name);

    /**
     * Gets the context of the runtime bundle.
     *
     * @return the context object
     */
    RuntimeContext getContext();

    /**
     * Gets the service of type serviceClass if such a service was declared by a
     * resolved runtime component.
     * <p>
     * If the component is not yet activated, it will be prior to return the
     * service.
     *
     * @param <T> the service type
     * @param serviceClass the service class
     * @return the service object
     */
    <T> T getService(Class<T> serviceClass);

    /**
     * Gets a list of startup warnings. Can be modified to add new warnings.
     *
     * @return the warning list
     */
    List<String> getWarnings();

    /**
     * OSGi frameworks are using a string {@link Bundle#getLocation()} to
     * identify bundle locations.
     * <p>
     * This method try to convert the bundle location to real file if possible.
     * If this bundle location cannot be converted to a file (e.g. it may be a
     * remote URL), null is returned.
     * <p>
     * This method works only for bundles that are installed as files on the
     * host file system.
     *
     * @return the bundle file, or null
     */
    File getBundleFile(Bundle bundle);

    /**
     * Get an installed  bundle given its symbolic name. This method is not handling versions.
     * @param symbolicName
     * @return
     */
    Bundle getBundle(String symbolicName);

}
