package fr.dila.st.ui.th.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import fr.dila.st.ui.bean.AlertContainer;
import fr.dila.st.ui.th.enums.AlertType;
import java.util.List;
import org.junit.Test;

public class SolonAlertManagerTest {

    private void assertQueueFilledButNotOthers(
        List<AlertContainer> expectedFilledQueue,
        List<AlertContainer> actualFilledQueue,
        List<AlertContainer> emptyQueue1,
        List<AlertContainer> emptyQueue2,
        List<AlertContainer> emptyQueue3
    ) {
        assertEquals(expectedFilledQueue, actualFilledQueue);
        assertTrue(emptyQueue1.isEmpty());
        assertTrue(emptyQueue2.isEmpty());
        assertTrue(emptyQueue3.isEmpty());
    }

    @Test
    public void testInfoQueue() {
        SolonAlertManager manager = new SolonAlertManager();

        assertFalse(manager.hasMessageInQueue());
        manager.addInfoToQueue("Test");

        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test"), null, AlertType.ALERT_INFO)),
            manager.getInfoQueue(),
            manager.getSuccessQueue(),
            manager.getWarnQueue(),
            manager.getErrorQueue()
        );

        manager.addInfoToQueue("Test 2");

        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_INFO)),
            manager.getInfoQueue(),
            manager.getSuccessQueue(),
            manager.getWarnQueue(),
            manager.getErrorQueue()
        );

        manager.addInfoToQueue("Test 3", "target 2");

        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(
                new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_INFO),
                new AlertContainer(Lists.newArrayList("Test 3"), "target 2", AlertType.ALERT_INFO)
            ),
            manager.getInfoQueue(),
            manager.getSuccessQueue(),
            manager.getWarnQueue(),
            manager.getErrorQueue()
        );
    }

    @Test
    public void testSuccessQueue() {
        SolonAlertManager manager = new SolonAlertManager();

        assertFalse(manager.hasMessageInQueue());

        manager.addSuccessToQueue("Test");
        assertTrue(manager.hasMessageInQueue());

        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test"), null, AlertType.ALERT_SUCCESS)),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getWarnQueue(),
            manager.getErrorQueue()
        );

        manager.addSuccessToQueue("Test 2");

        assertTrue(manager.hasMessageInQueue());

        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_SUCCESS)),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getWarnQueue(),
            manager.getErrorQueue()
        );

        manager.addSuccessToQueue("Test 3", "target 2");

        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(
                new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_SUCCESS),
                new AlertContainer(Lists.newArrayList("Test 3"), "target 2", AlertType.ALERT_SUCCESS)
            ),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getWarnQueue(),
            manager.getErrorQueue()
        );
    }

    @Test
    public void testWarnQueue() {
        SolonAlertManager manager = new SolonAlertManager();

        assertFalse(manager.hasMessageInQueue());

        manager.addWarnToQueue("Test");
        assertTrue(manager.hasMessageInQueue());

        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test"), null, AlertType.ALERT_WARNING)),
            manager.getWarnQueue(),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getErrorQueue()
        );

        manager.addWarnToQueue("Test 2");
        assertTrue(manager.hasMessageInQueue());

        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_WARNING)),
            manager.getWarnQueue(),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getErrorQueue()
        );

        manager.addWarnToQueue("Test 3", "target 2");

        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(
                new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_WARNING),
                new AlertContainer(Lists.newArrayList("Test 3"), "target 2", AlertType.ALERT_WARNING)
            ),
            manager.getWarnQueue(),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getErrorQueue()
        );
    }

    @Test
    public void testErrorQueue() {
        SolonAlertManager manager = new SolonAlertManager();

        assertFalse(manager.hasMessageInQueue());

        manager.addErrorToQueue("Test");
        assertTrue(manager.hasMessageInQueue());

        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test"), null, AlertType.ALERT_DANGER)),
            manager.getErrorQueue(),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getWarnQueue()
        );

        manager.addErrorToQueue("Test 2");
        assertTrue(manager.hasMessageInQueue());

        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_DANGER)),
            manager.getErrorQueue(),
            manager.getSuccessQueue(),
            manager.getInfoQueue(),
            manager.getWarnQueue()
        );

        manager.addErrorToQueue("Test 3", "target 2");

        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(
                new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.ALERT_DANGER),
                new AlertContainer(Lists.newArrayList("Test 3"), "target 2", AlertType.ALERT_DANGER)
            ),
            manager.getErrorQueue(),
            manager.getSuccessQueue(),
            manager.getWarnQueue(),
            manager.getInfoQueue()
        );
    }

    @Test
    public void testMessageQueue() {
        SolonAlertManager manager = new SolonAlertManager();

        assertFalse(manager.hasMessageInQueue());

        manager.addMessageToQueue("Test", AlertType.LIGHT_INFO);
        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test"), null, AlertType.LIGHT_INFO)),
            manager.getInfoQueue(),
            manager.getErrorQueue(),
            manager.getSuccessQueue(),
            manager.getWarnQueue()
        );

        manager.addMessageToQueue("Test 2", AlertType.LIGHT_INFO);
        assertTrue(manager.hasMessageInQueue());

        assertQueueFilledButNotOthers(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.LIGHT_INFO)),
            manager.getInfoQueue(),
            manager.getErrorQueue(),
            manager.getSuccessQueue(),
            manager.getWarnQueue()
        );

        manager.addMessageToQueue("Test 3", AlertType.LIGHT_INFO, "target 2");

        assertTrue(manager.hasMessageInQueue());
        assertQueueFilledButNotOthers(
            Lists.newArrayList(
                new AlertContainer(Lists.newArrayList("Test", "Test 2"), null, AlertType.LIGHT_INFO),
                new AlertContainer(Lists.newArrayList("Test 3"), "target 2", AlertType.LIGHT_INFO)
            ),
            manager.getInfoQueue(),
            manager.getErrorQueue(),
            manager.getSuccessQueue(),
            manager.getWarnQueue()
        );
    }
}
