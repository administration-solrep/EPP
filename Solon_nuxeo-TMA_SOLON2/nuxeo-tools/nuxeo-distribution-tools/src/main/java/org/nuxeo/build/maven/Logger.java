/*
 * (C) Copyright 2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Julien Carsique
 *
 * $$Id$$
 */
package org.nuxeo.build.maven;

/**
 * 
 * @author jcarsique
 *
 */
public interface Logger {

    public void info(String message);

    public void info(String message, Throwable error);

    public void warn(String message);

    public void warn(String message, Throwable error);

    public void error(String message);

    public void error(String message, Throwable error);

    public void debug(String message);

    public void debug(String message, Throwable error);

    public boolean isDebugEnabled();

}
