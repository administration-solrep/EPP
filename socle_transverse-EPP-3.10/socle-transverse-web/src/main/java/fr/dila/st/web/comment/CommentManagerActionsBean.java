package fr.dila.st.web.comment;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Calendar;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.DocumentModel;


@Name("commentManagerActions")
@Scope(CONVERSATION)
@Install(precedence = Install.APPLICATION + 1)
public class CommentManagerActionsBean extends
			org.nuxeo.ecm.platform.comment.web.CommentManagerActionsBean {
    /**
	 * Serial UID
	 */
	private static final long serialVersionUID = 5055222198770334944L;
	    
    @Override
    protected DocumentModel initializeComment(DocumentModel comment) {
        if (comment != null) {
            try {
                if (comment.getProperty("dublincore", "created") == null) {
                    comment.setProperty("dublincore", "created",
                            Calendar.getInstance());
                }
            } catch (ClientException e) {
                throw new ClientRuntimeException(e);
            }
        }
        return comment;
    }

}
