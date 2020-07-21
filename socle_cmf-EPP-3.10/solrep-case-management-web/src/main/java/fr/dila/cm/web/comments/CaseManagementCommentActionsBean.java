/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package fr.dila.cm.web.comments;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.comment.web.CommentManagerActions;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

import fr.dila.cm.cases.Case;
import fr.dila.cm.web.context.CaseManagementContextHolder;
import fr.dila.cm.web.invalidations.CaseManagementContextBound;
import fr.dila.cm.web.invalidations.CaseManagementContextBoundInstance;

/**
 * @author <a href="mailto:ldoguin@nuxeo.com">Laurent Doguin</a>
 *
 */
@Name("cmCommentActions")
@Scope(CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
@CaseManagementContextBound
public class CaseManagementCommentActionsBean extends
        CaseManagementContextBoundInstance {

    private static final long serialVersionUID = 6994714264125928209L;

    public static final String CASE_MANAGEMENT_COMMENT_ACTIONS = "CASE_MANAGEMENT_COMMENT_ACTIONS";

    @In(create = true)
    protected transient CommentManagerActions commentManagerActions;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @Override
    public void onMailboxContextChange(
            CaseManagementContextHolder correspContextHolder)
            throws ClientException {
        super.onMailboxContextChange(correspContextHolder);
        commentManagerActions.documentChanged();
    }

    @Override
    protected void resetCurrentCaseItemCache(DocumentModel cachedEmail,
            DocumentModel newEmail) throws ClientException {
        commentManagerActions.documentChanged();
    }

    public List<Action> getActionsForComment() throws ClientException {
        if (!isCurrentDocumentCase()) {
            return commentManagerActions.getActionsForComment();
        }
        return commentManagerActions.getActionsForComment(CASE_MANAGEMENT_COMMENT_ACTIONS);
    }

    protected boolean isCurrentDocumentCase() {
        DocumentModel currentDoc = navigationContext.getCurrentDocument();
        Case currentCase = currentDoc.getAdapter(Case.class);
        if (currentCase != null) {
            return true;
        }
        return false;
    }
}