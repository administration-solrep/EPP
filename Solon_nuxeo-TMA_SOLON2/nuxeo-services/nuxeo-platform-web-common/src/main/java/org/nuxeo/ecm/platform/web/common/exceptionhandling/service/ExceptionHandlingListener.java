/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     arussel
 */
package org.nuxeo.ecm.platform.web.common.exceptionhandling.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Listener performing operations when dealing with an error. Order of methods
 * called:
 *
 * <ul>
 * <li>1. startHandling</li>
 * <li>2. beforeSetErrorPageAttribute</li>
 * <li>3. beforeForwardToErrorPage</li>
 * <li>4. afterDispatch</li>
 * </ul>
 *
 * @author arussel
 */
public interface ExceptionHandlingListener {

    /**
     * Error has happened, things to do before error is dealt with.
     */
    void startHandling(Throwable t, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;

    void beforeSetErrorPageAttribute(Throwable t, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;

    void beforeForwardToErrorPage(Throwable t, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;

    void afterDispatch(Throwable t, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException;
}
