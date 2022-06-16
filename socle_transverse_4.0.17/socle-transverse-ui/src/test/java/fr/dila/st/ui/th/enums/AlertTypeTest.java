package fr.dila.st.ui.th.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import avro.shaded.com.google.common.collect.Lists;
import org.junit.Test;

public class AlertTypeTest {

    @Test
    public void testConstructor() {
        Lists
            .newArrayList(
                AlertType.ALERT_INFO,
                AlertType.ALERT_DANGER,
                AlertType.ALERT_WARNING,
                AlertType.ALERT_SUCCESS,
                AlertType.LIGHT_INFO,
                AlertType.LIGHT_DANGER,
                AlertType.LIGHT_WARNING,
                AlertType.LIGHT_SUCCESS,
                AlertType.PROGRESS_INFO,
                AlertType.PROGRESS_DANGER,
                AlertType.PROGRESS_WARNING,
                AlertType.PROGRESS_SUCCESS
            )
            .forEach(
                alert -> {
                    assertFalse(alert.getHasCloseButton());
                    assertNull(alert.getRole());
                }
            );

        Lists
            .newArrayList(AlertType.TOAST_INFO, AlertType.TOAST_WARNING, AlertType.TOAST_SUCCESS)
            .forEach(
                alert -> {
                    assertTrue(alert.getHasCloseButton());
                    assertEquals("status", alert.getRole());
                }
            );

        AlertType alert = AlertType.TOAST_DANGER;
        assertTrue(alert.getHasCloseButton());
        assertEquals("alert", alert.getRole());
    }
}
