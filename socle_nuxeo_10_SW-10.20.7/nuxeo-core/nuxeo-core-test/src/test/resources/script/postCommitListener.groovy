package script

import org.nuxeo.ecm.core.event.test.PostCommitEventListenerTest

bundle.each
{
    PostCommitEventListenerTest.SCRIPT_CNT++;
    // System.out.println("Hello from a post commit Groovy listener. Event name: "+ it.name + ". CNT: "+org.nuxeo.ecm.core.event.test.PostCommitEventListenerTest.SCRIPT_CNT);
}
