package fr.dila.ss.ui.services.comment;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSCommentManagerUIComponent
    extends ServiceEncapsulateComponent<SSCommentManagerUIService, SSCommentManagerUIServiceImpl> {

    /**
     * Default constructor
     */
    public SSCommentManagerUIComponent() {
        super(SSCommentManagerUIService.class, new SSCommentManagerUIServiceImpl());
    }
}
