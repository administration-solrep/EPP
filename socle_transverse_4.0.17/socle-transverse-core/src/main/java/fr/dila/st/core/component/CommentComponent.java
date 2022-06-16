package fr.dila.st.core.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.CommentManagerImpl;
import org.nuxeo.ecm.platform.comment.api.CommentManager;

public class CommentComponent extends ServiceEncapsulateComponent<CommentManager, CommentManagerImpl> {

    public CommentComponent() {
        super(CommentManager.class, new CommentManagerImpl());
    }
}
