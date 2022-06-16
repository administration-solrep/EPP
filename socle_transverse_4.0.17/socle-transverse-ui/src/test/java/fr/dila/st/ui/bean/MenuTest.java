package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class MenuTest {

    @Test
    public void testConstructor() {
        Menu menu = new Menu("url", "key");
        assertNotNull(menu);
        assertEquals("url", menu.getUrl());
        assertEquals("key", menu.getTitleKey());
        assertNull(menu.getChilds());
        assertFalse(menu.getIsCurrent());
    }

    @Test
    public void testSetters() {
        Menu menu = new Menu("url", "key");
        assertNotNull(menu);
        assertEquals("url", menu.getUrl());
        assertEquals("key", menu.getTitleKey());
        assertNull(menu.getChilds());
        assertFalse(menu.getIsCurrent());

        menu.setIsCurrent(true);
        assertTrue(menu.getIsCurrent());
    }

    @Test
    public void testCurrentPath() {
        Menu subMenu1 = new Menu("", "subMenu1");
        Menu subMenu2 = new Menu("", "subMenu2");
        Menu[] submenus = new Menu[2];

        subMenu1.setIsCurrent(true);
        submenus[0] = subMenu2;
        submenus[1] = subMenu1;
        Menu main = new Menu("", "main", submenus);

        List<Menu> pathToCurrent = new ArrayList<>();
        main.getPathToCurrent(pathToCurrent);

        assertNotNull(pathToCurrent);
        assertFalse(pathToCurrent.isEmpty());
        assertEquals(2, pathToCurrent.size());

        pathToCurrent = new ArrayList<>();
        subMenu1.getPathToCurrent(pathToCurrent);

        assertNotNull(pathToCurrent);
        assertFalse(pathToCurrent.isEmpty());
        assertEquals(1, pathToCurrent.size());

        pathToCurrent = new ArrayList<>();
        subMenu2.getPathToCurrent(pathToCurrent);
        assertNotNull(pathToCurrent);
        assertTrue(pathToCurrent.isEmpty());
    }
}
