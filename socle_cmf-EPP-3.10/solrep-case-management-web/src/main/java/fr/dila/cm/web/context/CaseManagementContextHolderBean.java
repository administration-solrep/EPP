/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Anahide Tchertchian
 *
 * $Id$
 */

package fr.dila.cm.web.context;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.cm.cases.Case;
import fr.dila.cm.mailbox.Mailbox;


/**
 * Minimal context holder.
 * <p>
 * Has to stay light-weight to be easily injected in other components.
 *
 * @author Anahide Tchertchian
 */
@Name("cmContextHolder")
@Scope(ScopeType.CONVERSATION)
public class CaseManagementContextHolderBean implements
        CaseManagementContextHolder {

    public static final String SEAM_COMPONENT_NAME = "cmContextHolder";

    private static final long serialVersionUID = 1L;

    protected Mailbox currentMailbox;

    protected Case currentEnvelope;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @Factory(value = "currentMailbox", scope = ScopeType.EVENT)
    public Mailbox getCurrentMailbox() throws ClientException {
        return currentMailbox;
    }

    @Factory(value = "currentCase", scope = ScopeType.EVENT)
    public Case getCurrentCase() throws ClientException {
        return currentEnvelope;
    }

    public void setCurrentMailbox(Mailbox currentMailbox) {
        this.currentMailbox = currentMailbox;
        this.currentEnvelope = null;
    }

    public void setCurrentCase(Case currentEnvelope) {
        this.currentEnvelope = currentEnvelope;
    }

}
