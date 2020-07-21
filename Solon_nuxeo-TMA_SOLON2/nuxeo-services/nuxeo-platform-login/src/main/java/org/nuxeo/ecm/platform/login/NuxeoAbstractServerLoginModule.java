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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.login;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation of the {@link LoginModule} SPI from {@code javax.security.auth.spi}.
 */
public abstract class NuxeoAbstractServerLoginModule implements LoginModule {

    private static final Log log = LogFactory.getLog(NuxeoAbstractServerLoginModule.class);

    protected Subject subject;
    protected Map sharedState;
    protected Map options;

    protected boolean loginOk;

    /** An optional custom Principal class implementation */
    protected String principalClassName;

    /** the principal to use when a null username and password are seen */
    protected Principal unauthenticatedIdentity;

    protected CallbackHandler callbackHandler;

    /** Flag indicating if the shared credential should be used */
    protected boolean useFirstPass;


    protected abstract Principal getIdentity();

    protected abstract Group[] getRoleSets() throws LoginException;

    protected abstract Principal createIdentity(String username) throws Exception;


    public boolean abort() throws LoginException {
        log.trace("abort");
        return true;
    }

    public boolean commit() throws LoginException {
        log.trace("commit, loginOk=" + loginOk);
        if (!loginOk) {
            return false;
        }

        Set<Principal> principals = subject.getPrincipals();
        Principal identity = getIdentity();
        principals.add(identity);
        Group[] roleSets = getRoleSets();
        for (Group group : roleSets) {
            String name = group.getName();
            Group subjectGroup = createGroup(name, principals);

            /*
             * if( subjectGroup instanceof NestableGroup ) { SimpleGroup tmp =
             * new SimpleGroup("Roles"); subjectGroup.addMember(tmp);
             * subjectGroup = tmp; }
             */

            // Copy the group members to the Subject group
            Enumeration<? extends Principal> members = group.members();
            while (members.hasMoreElements()) {
                Principal role = members.nextElement();
                subjectGroup.addMember(role);
            }
        }
        return true;
    }

    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        if (log.isTraceEnabled()) {
            log.trace("initialize, instance=@" + System.identityHashCode(this));
        }

        /*
         * Check for password sharing options. Any non-null value for
         * password_stacking sets useFirstPass as this module has no way to
         * validate any shared password.
         */
        String passwordStacking = (String) options.get("password-stacking");
        if (passwordStacking != null
                && passwordStacking.equalsIgnoreCase("useFirstPass")) {
            useFirstPass = true;
        }

        // Check for a custom Principal implementation
        principalClassName = (String) options.get("principalClass");

        // Check for unauthenticatedIdentity option.
        String name = (String) options.get("unauthenticatedIdentity");
        if (name != null) {
            try {
                unauthenticatedIdentity = createIdentity(name);
                log.trace("Saw unauthenticatedIdentity=" + name);
            } catch (Exception e) {
                log.warn("Failed to create custom unauthenticatedIdentity", e);
            }
        }
    }

    public boolean logout() throws LoginException {
        log.trace("logout");
        // Remove the user identity
        Principal identity = getIdentity();
        Set<Principal>principals = subject.getPrincipals();
        principals.remove(identity);
        // Remove any added Groups...
        return true;
    }

    /**
     * Finds or creates a Group with the given name. Subclasses should use this
     * method to locate the 'Roles' group or create additional types of groups.
     *
     * @return A named Group from the principals set.
     */
    protected Group createGroup(String name, Set<Principal> principals) {
        Group roles = null;
        for (Principal principal : principals) {
            if (!(principal instanceof Group)) {
                continue;
            }
            Group grp = (Group) principal;
            if (grp.getName().equals(name)) {
                roles = grp;
                break;
            }
        }
        // If we did not find a group, create one
        if (roles == null) {
            roles = new GroupImpl(name);
            principals.add(roles);
        }
        return roles;
    }

}
