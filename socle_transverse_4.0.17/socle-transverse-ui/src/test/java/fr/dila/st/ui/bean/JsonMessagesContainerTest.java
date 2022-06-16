package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class JsonMessagesContainerTest {

    @Test
    public void testConstructorNoArgs() {
        JsonMessagesContainer container = new JsonMessagesContainer();
        assertNotNull(container.getDangerMessageQueue());
        assertTrue(container.getDangerMessageQueue().isEmpty());
        assertNotNull(container.getInfoMessageQueue());
        assertTrue(container.getInfoMessageQueue().isEmpty());
        assertNotNull(container.getSuccessMessageQueue());
        assertTrue(container.getSuccessMessageQueue().isEmpty());
        assertNotNull(container.getWarningMessageQueue());
        assertTrue(container.getWarningMessageQueue().isEmpty());
    }

    @Test
    public void testConstructorWithFourArgs() {
        List<AlertContainer> dangerQueue = new ArrayList<>();
        dangerQueue.add(new AlertContainer());
        dangerQueue.add(new AlertContainer());

        List<AlertContainer> warnQueue = new ArrayList<>();
        warnQueue.add(new AlertContainer());

        List<AlertContainer> successQueue = new ArrayList<>();
        successQueue.add(new AlertContainer());
        successQueue.add(new AlertContainer());
        successQueue.add(new AlertContainer());

        JsonMessagesContainer container = new JsonMessagesContainer(null, warnQueue, dangerQueue, successQueue);
        assertNull(container.getInfoMessageQueue());

        assertEquals(warnQueue, container.getWarningMessageQueue());
        assertEquals(1, container.getWarningMessageQueue().size());

        assertEquals(dangerQueue, container.getDangerMessageQueue());
        assertEquals(2, container.getDangerMessageQueue().size());

        assertEquals(successQueue, container.getSuccessMessageQueue());
        assertEquals(3, container.getSuccessMessageQueue().size());
    }

    @Test
    public void testSetter() {
        JsonMessagesContainer container = new JsonMessagesContainer();
        assertNotNull(container.getDangerMessageQueue());
        assertTrue(container.getDangerMessageQueue().isEmpty());
        assertNotNull(container.getInfoMessageQueue());
        assertTrue(container.getInfoMessageQueue().isEmpty());
        assertNotNull(container.getSuccessMessageQueue());
        assertTrue(container.getSuccessMessageQueue().isEmpty());
        assertNotNull(container.getWarningMessageQueue());
        assertTrue(container.getWarningMessageQueue().isEmpty());

        List<AlertContainer> dangerQueue = new ArrayList<>();
        dangerQueue.add(new AlertContainer());
        assertNotEquals(dangerQueue, container.getDangerMessageQueue());
        container.setDangerMessageQueue(dangerQueue);
        assertEquals(dangerQueue, container.getDangerMessageQueue());

        List<AlertContainer> warnQueue = new ArrayList<>();
        warnQueue.add(new AlertContainer());
        assertNotEquals(warnQueue, container.getWarningMessageQueue());
        container.setWarningMessageQueue(warnQueue);
        assertEquals(warnQueue, container.getWarningMessageQueue());

        List<AlertContainer> successQueue = new ArrayList<>();
        successQueue.add(new AlertContainer());
        assertNotEquals(successQueue, container.getSuccessMessageQueue());
        container.setSuccessMessageQueue(successQueue);
        assertEquals(successQueue, container.getSuccessMessageQueue());

        List<AlertContainer> infoQueue = new ArrayList<>();
        infoQueue.add(new AlertContainer());
        assertNotEquals(infoQueue, container.getInfoMessageQueue());
        container.setInfoMessageQueue(infoQueue);
        assertEquals(infoQueue, container.getInfoMessageQueue());
    }
}
