/*
 * (C) Copyright 2006-2007 Nuxeo SAS <http://nuxeo.com> and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jean-Marc Orliaguet, Chalmers
 *
 * $Id$
 */

package org.nuxeo.theme.test.themes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.nuxeo.runtime.test.NXRuntimeTestCase;
import org.nuxeo.theme.Manager;
import org.nuxeo.theme.elements.Element;
import org.nuxeo.theme.elements.ElementFactory;
import org.nuxeo.theme.elements.ElementFormatter;
import org.nuxeo.theme.elements.PageElement;
import org.nuxeo.theme.elements.ThemeElement;
import org.nuxeo.theme.formats.DefaultFormat;
import org.nuxeo.theme.formats.Format;
import org.nuxeo.theme.formats.FormatFactory;
import org.nuxeo.theme.formats.layouts.Layout;
import org.nuxeo.theme.formats.styles.Style;
import org.nuxeo.theme.formats.widgets.Widget;
import org.nuxeo.theme.fragments.FragmentFactory;
import org.nuxeo.theme.nodes.NodeException;
import org.nuxeo.theme.perspectives.PerspectiveManager;
import org.nuxeo.theme.perspectives.PerspectiveType;
import org.nuxeo.theme.test.DummyFragment;
import org.nuxeo.theme.themes.ThemeDescriptor;
import org.nuxeo.theme.themes.ThemeException;
import org.nuxeo.theme.themes.ThemeIOException;
import org.nuxeo.theme.themes.ThemeManager;
import org.nuxeo.theme.themes.ThemeParser;
import org.nuxeo.theme.types.TypeRegistry;

public class TestThemeManager extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.nuxeo.theme.core",
                "OSGI-INF/nxthemes-core-service.xml");
        deployContrib("org.nuxeo.theme.core",
                "OSGI-INF/nxthemes-core-contrib.xml");
        deployContrib("org.nuxeo.theme.core.tests", "fragment-config.xml");
    }

    private Style createNamedStyle(String name, String themeName)
            throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();
        Style s = (Style) FormatFactory.create("style");
        s.setName(name);
        themeManager.registerFormat(s);
        themeManager.setNamedObject(themeName, "style", s);
        return s;
    }

    public void testValidateThemeName() {
        assertTrue(ThemeManager.validateThemeName("default"));
        assertTrue(ThemeManager.validateThemeName("default-1"));
        assertTrue(ThemeManager.validateThemeName("default_1l"));
        assertTrue(ThemeManager.validateThemeName("default-1-2"));
        assertTrue(ThemeManager.validateThemeName("default-a"));
        assertTrue(ThemeManager.validateThemeName("a"));
        assertTrue(ThemeManager.validateThemeName("a1"));

        assertFalse(ThemeManager.validateThemeName("1"));
        assertFalse(ThemeManager.validateThemeName("default-1.2"));
        assertFalse(ThemeManager.validateThemeName("default-"));
        assertFalse(ThemeManager.validateThemeName("default_"));
        assertTrue(ThemeManager.validateThemeName("Default"));
        assertTrue(ThemeManager.validateThemeName("nuxeo-DM"));
        assertTrue(ThemeManager.validateThemeName("Nuxeo-DAM"));
        assertFalse(ThemeManager.validateThemeName("-_"));
        assertFalse(ThemeManager.validateThemeName("-"));
        assertFalse(ThemeManager.validateThemeName("-."));
        assertFalse(ThemeManager.validateThemeName("-1"));
    }

    public void testGetThemeNames() {
        TypeRegistry typeRegistry = Manager.getTypeRegistry();
        assertTrue(ThemeManager.getThemeNames().isEmpty());
        ThemeDescriptor themeDescriptor1 = new ThemeDescriptor();
        themeDescriptor1.setName("theme1");
        themeDescriptor1.setConfigured(true);
        typeRegistry.register(themeDescriptor1);
        assertTrue(ThemeManager.getThemeNames().contains("theme1"));

        ThemeDescriptor themeDescriptor2 = new ThemeDescriptor();
        themeDescriptor2.setName("theme2");
        themeDescriptor2.setConfigured(false);
        typeRegistry.register(themeDescriptor2);
        assertTrue(ThemeManager.getThemeNames().contains("theme2"));

        // customize theme2
        ThemeDescriptor themeDescriptor3 = new ThemeDescriptor();
        themeDescriptor3.setName("theme3");
        themeDescriptor3.setConfigured(false);
        themeDescriptor2.setCustomized(true);
        typeRegistry.register(themeDescriptor3);
        assertTrue(ThemeManager.getThemeNames().contains("theme3"));
        assertFalse(ThemeManager.getThemeNames().contains("theme2"));

        // template engines
        ThemeDescriptor themeDescriptor4 = new ThemeDescriptor();
        themeDescriptor4.setName("theme4");
        themeDescriptor4.setConfigured(true);
        typeRegistry.register(themeDescriptor4);
        assertTrue(ThemeManager.getThemeNames().contains("theme4"));
        assertTrue(ThemeManager.getThemeNames("jsf-facelets").contains("theme4"));
        assertTrue(ThemeManager.getThemeNames("freemarker").contains("theme4"));

        List<String> templateEngines = new ArrayList<String>();
        templateEngines.add("jsf-facelets");
        themeDescriptor4.setTemplateEngines(templateEngines);

        assertTrue(ThemeManager.getThemeNames("jsf-facelets").contains("theme4"));
        assertFalse(ThemeManager.getThemeNames("freemarker").contains("theme4"));

        templateEngines.add("freemarker");
        themeDescriptor4.setTemplateEngines(templateEngines);
        assertTrue(ThemeManager.getThemeNames().contains("theme4"));
        assertTrue(ThemeManager.getThemeNames("jsf-facelets").contains("theme4"));
        assertTrue(ThemeManager.getThemeNames("freemarker").contains("theme4"));
    }

    public void testGetPageNames() throws NodeException {
        ThemeManager themeManager = Manager.getThemeManager();
        assertTrue(themeManager.getPageNames("default").isEmpty());

        ThemeElement theme = new ThemeElement();
        theme.setName("default");
        PageElement page1 = new PageElement();
        page1.setName("page1");
        PageElement page2 = new PageElement();
        page2.setName("page2");
        theme.addChild(page1);
        theme.addChild(page2);
        themeManager.registerTheme(theme);
        assertTrue(themeManager.getPageNames("default").contains("page1"));
        assertTrue(themeManager.getPageNames("default").contains("page2"));
    }

    public void testGetThemeNameByUrl() throws MalformedURLException {
        URL themeUrl = new URL(
                "nxtheme://theme/engine/mode/templateEngine/theme/page/perspective");
        assertEquals("theme", ThemeManager.getThemeNameByUrl(themeUrl));
    }

    public void testGetUrlDescription() throws MalformedURLException {
        URL themeUrl = new URL(
                "nxtheme://theme/engine/mode/templateEngine/theme/page/perspective");
        assertEquals(
                "[THEME theme, PAGE page, ENGINE engine, TEMPLATE templateEngine, PERSPECTIVE perspective, MODE mode]",
                ThemeManager.getUrlDescription(themeUrl));
        URL elementUrl = new URL(
                "nxtheme://element/engine/mode/templateEngine/12345678");
        assertEquals(
                "[ELEMENT 12345678, ENGINE engine, TEMPLATE templateEngine, MODE mode]",
                ThemeManager.getUrlDescription(elementUrl));
    }

    public void testGetThemeOf() throws NodeException {
        Element theme = ElementFactory.create("theme");
        Element page = ElementFactory.create("page");
        Element section = ElementFactory.create("section");
        Element cell = ElementFactory.create("cell");
        theme.addChild(page).addChild(section);

        assertSame(theme, ThemeManager.getThemeOf(section));
        assertSame(theme, ThemeManager.getThemeOf(page));
        assertSame(theme, ThemeManager.getThemeOf(theme));
        assertNull(ThemeManager.getThemeOf(cell));
    }

    public void testBelongToSameTheme() throws NodeException {
        Element theme1 = ElementFactory.create("theme");
        Element page11 = ElementFactory.create("page");
        Element page12 = ElementFactory.create("page");
        Element theme2 = ElementFactory.create("theme");
        Element page21 = ElementFactory.create("page");
        Element page22 = ElementFactory.create("page");
        theme1.addChild(page11);
        theme1.addChild(page12);
        theme2.addChild(page21);
        theme2.addChild(page22);

        assertTrue(ThemeManager.belongToSameTheme(page11, page12));
        assertTrue(ThemeManager.belongToSameTheme(page21, page22));
        assertFalse(ThemeManager.belongToSameTheme(page11, page21));
        assertFalse(ThemeManager.belongToSameTheme(page11, page22));
        assertFalse(ThemeManager.belongToSameTheme(page12, page21));
        assertFalse(ThemeManager.belongToSameTheme(page12, page22));
    }

    public void testDuplicateElement() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();
        Element element = ElementFactory.create("page");
        element.setName("page 1");

        Format widget = FormatFactory.create("widget");
        widget.setName("page frame");
        ElementFormatter.setFormat(element, widget);

        Element duplicate = themeManager.duplicateElement(element, false);

        assertNotNull(duplicate);
        // do not duplicate the element's name
        assertNull(duplicate.getName());

        assertFalse(duplicate.getUid().equals(element.getUid()));
        assertSame(duplicate.getElementType(), element.getElementType());

        // compare formats
        assertSame(widget,
                ElementFormatter.getFormatsFor(duplicate).iterator().next());
    }

    public void testDuplicateFragment() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();
        DummyFragment fragment = (DummyFragment) FragmentFactory.create("dummy fragment");
        fragment.setField1("value of field 1");
        fragment.setField2("value of field 2");

        Widget widget = (Widget) FormatFactory.create("widget");
        widget.setDescription("description");
        themeManager.registerFormat(widget);
        ElementFormatter.setFormat(fragment, widget);

        // Duplicate the element, relink its formats
        Element duplicate = themeManager.duplicateElement(fragment, false);
        assertNotNull(duplicate);

        // Do not duplicate the element's name
        assertNull(duplicate.getName());
        assertFalse(duplicate.getUid().equals(fragment.getUid()));
        assertSame(duplicate.getElementType(), fragment.getElementType());

        // Compare fields
        assertEquals("value of field 1",
                ((DummyFragment) duplicate).getField1());
        assertEquals("value of field 2",
                ((DummyFragment) duplicate).getField2());

        // Compare formats
        Format duplicatedFormat1 = ElementFormatter.getFormatFor(duplicate,
                "widget");
        assertSame(ElementFormatter.getFormatFor(fragment, "widget"),
                duplicatedFormat1);
        assertEquals("description", duplicatedFormat1.getDescription());

        // Duplicate element, physically duplicate its formats
        Element duplicate2 = themeManager.duplicateElement(fragment, true);
        Format duplicatedFormat2 = ElementFormatter.getFormatFor(duplicate2,
                "widget");
        assertNotSame(ElementFormatter.getFormatFor(fragment, "widget"),
                duplicatedFormat2);
        assertEquals("description", duplicatedFormat2.getDescription());
    }

    public void testDuplicateWidget() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Widget widget = (Widget) FormatFactory.create("widget");
        widget.setName("vertical menu");
        widget.setDescription("Description");
        Widget duplicate = (Widget) themeManager.duplicateFormat(widget);

        assertNotNull(duplicate);
        assertEquals("vertical menu", duplicate.getName());
        assertEquals("Description", duplicate.getDescription());
        assertFalse(duplicate.getUid().equals(widget.getUid()));
        assertSame(duplicate.getFormatType(), widget.getFormatType());
    }

    public void testDuplicateLayout() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Layout layout = (Layout) FormatFactory.create("layout");
        layout.setProperty("width", "100%");
        layout.setProperty("height", "50px");

        Layout duplicate = (Layout) themeManager.duplicateFormat(layout);

        assertEquals("100%", duplicate.getProperty("width"));
        assertEquals("50px", duplicate.getProperty("height"));
    }

    public void testDuplicateStyle() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Style style = (Style) FormatFactory.create("style");
        Properties properties1 = new Properties();
        properties1.setProperty("color", "red");
        style.setPropertiesFor("vertical menu", "", properties1);
        Properties properties2 = new Properties();
        properties2.setProperty("color", "green");
        style.setPropertiesFor("vertical menu", "h1", properties2);

        Style duplicate = (Style) themeManager.duplicateFormat(style);
        Properties duplicateProperties1 = duplicate.getPropertiesFor(
                "vertical menu", "");
        Properties duplicateProperties2 = duplicate.getPropertiesFor(
                "vertical menu", "h1");

        assertEquals(properties1, duplicateProperties1);
        assertEquals(properties2, duplicateProperties2);
    }

    public void testDuplicateFormatWithAncestors() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Style style = (Style) FormatFactory.create("style");

        Style ancestor1 = (Style) FormatFactory.create("style");
        ancestor1.setName("common styles 1");

        Style ancestor2 = (Style) FormatFactory.create("style");
        ancestor2.setName("common styles 2");

        themeManager.makeFormatInherit(ancestor1, ancestor2);
        themeManager.makeFormatInherit(style, ancestor1);

        // Ancestors
        Style duplicate = (Style) themeManager.duplicateFormat(style);
        assertSame(ancestor1, ThemeManager.getAncestorFormatOf(duplicate));
        assertSame(ancestor2, ThemeManager.getAncestorFormatOf(ancestor1));
    }

    public void testListFormats() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        DefaultFormat widget0 = (DefaultFormat) FormatFactory.create("widget");
        DefaultFormat widget1 = (DefaultFormat) FormatFactory.create("widget");
        assertTrue(themeManager.listFormats().isEmpty());
        themeManager.registerFormat(widget0);
        themeManager.registerFormat(widget1);
        assertTrue(themeManager.listFormats().contains(widget0));
        assertTrue(themeManager.listFormats().contains(widget1));
        themeManager.unregisterFormat(widget0);
        themeManager.unregisterFormat(widget1);
        assertTrue(themeManager.listFormats().isEmpty());
    }

    public void testRemoveOrphanedFormats() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Element theme = ElementFactory.create("theme");

        DefaultFormat widget0 = (DefaultFormat) FormatFactory.create("widget");
        themeManager.registerFormat(widget0);

        DefaultFormat style1 = (DefaultFormat) FormatFactory.create("style");
        DefaultFormat style2 = (DefaultFormat) FormatFactory.create("style");
        DefaultFormat style3 = (DefaultFormat) FormatFactory.create("style");
        style3.setName("common styles");
        themeManager.registerFormat(style1);
        themeManager.registerFormat(style2);
        themeManager.registerFormat(style3);
        themeManager.makeFormatInherit(style1, style3);
        themeManager.makeFormatInherit(style2, style3);

        ElementFormatter.setFormat(theme, widget0);
        ElementFormatter.setFormat(theme, style1);
        assertTrue(themeManager.listFormats().contains(widget0));
        assertTrue(themeManager.listFormats().contains(style1));
        assertTrue(themeManager.listFormats().contains(style2));
        assertTrue(themeManager.listFormats().contains(style3));

        themeManager.removeOrphanedFormats();
        assertTrue(themeManager.listFormats().contains(style1));
        assertFalse(themeManager.listFormats().contains(style2));
        assertTrue(themeManager.listFormats().contains(style3));

        ElementFormatter.removeFormat(theme, widget0);
        assertTrue(themeManager.listFormats().contains(widget0));

        themeManager.removeOrphanedFormats();

        assertFalse(themeManager.listFormats().contains(widget0));
        assertTrue(themeManager.listFormats().contains(style1));
        assertTrue(themeManager.listFormats().contains(style3));

        ElementFormatter.removeFormat(theme, style1);
        themeManager.removeOrphanedFormats();

        assertFalse(themeManager.listFormats().contains(style1));
        assertTrue(themeManager.listFormats().contains(style3));
    }

    public void testRemoveOrphanedFormatsOnTestTheme() throws ThemeIOException,
            ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        ThemeDescriptor themeDef = new ThemeDescriptor();
        themeDef.setSrc("theme.xml");
        final boolean preload = false;
        ThemeParser.registerTheme(themeDef, preload);
        List<Format> formatsBefore = themeManager.listFormats();
        themeManager.removeOrphanedFormats();
        List<Format> formatsAfter = themeManager.listFormats();
        assertEquals(formatsBefore, formatsAfter);
    }

    public void testDestroyTestTheme() throws ThemeIOException, ThemeException,
            NodeException {
        ThemeManager themeManager = Manager.getThemeManager();

        ThemeDescriptor themeDef = new ThemeDescriptor();
        themeDef.setSrc("theme.xml");
        final boolean preload = false;
        ThemeParser.registerTheme(themeDef, preload);
        ThemeElement theme = themeManager.getThemeByName(themeDef.getName());

        themeManager.destroyElement(theme);

        assertTrue(themeManager.listFormats().isEmpty());
        assertTrue(Manager.getRelationStorage().list().isEmpty());
        assertTrue(Manager.getUidManager().listUids().isEmpty());

    }

    public void testStyleInheritance() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Style style = (Style) FormatFactory.create("style");
        style.setUid(1);
        Style ancestor1 = (Style) FormatFactory.create("style");
        ancestor1.setUid(2);
        Style ancestor2 = (Style) FormatFactory.create("style");
        ancestor2.setUid(3);
        Style ancestor3 = (Style) FormatFactory.create("style");
        ancestor3.setUid(4);

        assertNull(ThemeManager.getAncestorFormatOf(style));

        themeManager.makeFormatInherit(style, ancestor1);
        assertEquals(ancestor1, ThemeManager.getAncestorFormatOf(style));
        assertTrue(ThemeManager.listAncestorFormatsOf(style).contains(ancestor1));

        assertTrue(ThemeManager.listAncestorFormatsOf(ancestor2).isEmpty());
        themeManager.makeFormatInherit(ancestor1, ancestor2);
        assertEquals(ancestor2, ThemeManager.getAncestorFormatOf(ancestor1));

        // test transitivity
        themeManager.makeFormatInherit(ancestor2, ancestor3);
        assertTrue(ThemeManager.listAncestorFormatsOf(style).contains(ancestor3));

        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor1).contains(
                style));
        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor2).contains(
                ancestor1));
        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor3).contains(
                ancestor2));

        // replace ancestor
        themeManager.makeFormatInherit(style, ancestor2);
        assertEquals(ancestor2, ThemeManager.getAncestorFormatOf(style));
        assertTrue(ThemeManager.listAncestorFormatsOf(style).contains(ancestor2));
        assertFalse(ThemeManager.listAncestorFormatsOf(style).contains(
                ancestor1));

        assertFalse(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor1).contains(
                style));
        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor2).contains(
                style));
        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor2).contains(
                ancestor1));
        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor3).contains(
                ancestor2));

        // remove old inheritance relation
        ThemeManager.removeInheritanceTowards(style);
        assertNull(ThemeManager.getAncestorFormatOf(style));
        assertFalse(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor2).contains(
                style));

        // test common inheritance
        Style ancestor = (Style) FormatFactory.create("style");
        ancestor.setUid(5);
        Style style1 = (Style) FormatFactory.create("style");
        style1.setUid(6);
        Style style2 = (Style) FormatFactory.create("style");
        style1.setUid(7);
        themeManager.makeFormatInherit(style1, ancestor);
        themeManager.makeFormatInherit(style2, ancestor);
        assertTrue(ThemeManager.listAncestorFormatsOf(style1).contains(ancestor));
        assertTrue(ThemeManager.listAncestorFormatsOf(style2).contains(ancestor));
        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor).contains(
                style1));
        assertTrue(ThemeManager.listFormatsDirectlyInheritingFrom(ancestor).contains(
                style2));

        // test deleting a format that is inherited
        themeManager.deleteFormat(ancestor);
        assertTrue(ThemeManager.listAncestorFormatsOf(style1).isEmpty());
    }

    public void testStyleInheritanceCycles() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Style style = (Style) FormatFactory.create("style");
        style.setUid(1);
        Style ancestor1 = (Style) FormatFactory.create("style");
        ancestor1.setUid(2);
        Style ancestor2 = (Style) FormatFactory.create("style");
        ancestor2.setUid(3);
        Style ancestor3 = (Style) FormatFactory.create("style");
        ancestor3.setUid(4);

        // a style cannot inherit from itself.
        themeManager.makeFormatInherit(style, style);
        assertNull(ThemeManager.getAncestorFormatOf(style));

        // detect direct cycles
        themeManager.makeFormatInherit(style, ancestor1);
        themeManager.makeFormatInherit(ancestor1, style);
        assertNull(ThemeManager.getAncestorFormatOf(ancestor1));

        // test cycle through transitivity
        themeManager.makeFormatInherit(ancestor1, ancestor2);
        themeManager.makeFormatInherit(ancestor2, ancestor3);
        themeManager.makeFormatInherit(ancestor3, ancestor1);
        assertTrue(ThemeManager.listAncestorFormatsOf(ancestor3).isEmpty());
    }

    public void testDestroyElement() throws ThemeException, NodeException {
        ThemeManager themeManager = Manager.getThemeManager();

        ThemeElement theme = (ThemeElement) ElementFactory.create("theme");
        theme.setName("theme");
        Element page = ElementFactory.create("page");
        Element section = ElementFactory.create("section");
        theme.addChild(page).addChild(section);
        themeManager.registerTheme(theme);

        PerspectiveType perspective = new PerspectiveType("default",
                "default perspective");

        PerspectiveManager.setVisibleInPerspective(theme, perspective);
        PerspectiveManager.setVisibleInPerspective(page, perspective);
        PerspectiveManager.setVisibleInPerspective(section, perspective);

        DefaultFormat widget0 = (DefaultFormat) FormatFactory.create("widget");
        DefaultFormat widget1 = (DefaultFormat) FormatFactory.create("widget");
        DefaultFormat widget2 = (DefaultFormat) FormatFactory.create("widget");

        themeManager.registerFormat(widget0);
        themeManager.registerFormat(widget1);
        themeManager.registerFormat(widget2);

        ElementFormatter.setFormat(theme, widget0);
        ElementFormatter.setFormat(page, widget1);
        ElementFormatter.setFormat(section, widget2);

        assertFalse(themeManager.listFormats().isEmpty());
        assertFalse(Manager.getRelationStorage().list().isEmpty());
        assertFalse(Manager.getUidManager().listUids().isEmpty());

        Style style = (Style) FormatFactory.create("style");
        Style ancestor1 = (Style) FormatFactory.create("style");
        Style ancestor2 = (Style) FormatFactory.create("style");

        style.setName("style name");
        ancestor1.setName("ancestor 1");
        ancestor2.setName("ancestor 2");

        themeManager.registerFormat(style);
        themeManager.registerFormat(ancestor1);
        themeManager.registerFormat(ancestor2);

        themeManager.setNamedObject("theme", "style", style);
        themeManager.setNamedObject("theme", "style", ancestor1);
        themeManager.setNamedObject("theme", "style", ancestor2);

        themeManager.makeFormatInherit(style, ancestor1);
        themeManager.makeFormatInherit(ancestor1, ancestor2);

        ElementFormatter.setFormat(section, style);

        themeManager.destroyElement(theme);
        assertTrue(themeManager.listFormats().isEmpty());

        assertTrue(Manager.getRelationStorage().list().isEmpty());
        assertTrue(Manager.getUidManager().listUids().isEmpty());
    }

    public void testDeletePage() throws ThemeIOException, NodeException,
            ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        ThemeElement theme = (ThemeElement) ElementFactory.create("theme");
        theme.setName("theme");
        PageElement page = (PageElement) ElementFactory.create("page");
        page.setName("page");
        Element section = ElementFactory.create("section");
        theme.addChild(page).addChild(section);
        themeManager.registerTheme(theme);

        assertSame(page, themeManager.getPageByPath("theme/page"));
        themeManager.deletePage("theme/page");
        assertNull(themeManager.getPageByPath("theme/page"));
    }

    public void testMakeElementUseNamedStyle() throws ThemeException,
            NodeException {
        ThemeManager themeManager = Manager.getThemeManager();

        final String currentThemeName = "theme";

        ThemeElement theme = (ThemeElement) ElementFactory.create("theme");
        theme.setName(currentThemeName);
        PageElement page = (PageElement) ElementFactory.create("page");
        page.setName("default");
        theme.addChild(page);
        themeManager.registerTheme(theme);

        final String styleName1 = "named style 1";
        Style namedStyle1 = (Style) FormatFactory.create("style");
        namedStyle1.setName(styleName1);
        themeManager.registerFormat(namedStyle1);
        themeManager.setNamedObject(currentThemeName, "style", namedStyle1);

        final String styleName2 = "named style 2";
        Style namedStyle2 = (Style) FormatFactory.create("style");
        namedStyle2.setName(styleName2);
        themeManager.registerFormat(namedStyle2);
        themeManager.setNamedObject(currentThemeName, "style", namedStyle2);

        Style pageStyle = (Style) FormatFactory.create("style");
        themeManager.registerFormat(pageStyle);
        ElementFormatter.setFormat(page, pageStyle);

        themeManager.makeElementUseNamedStyle(page, styleName1,
                currentThemeName);
        assertSame(namedStyle1, ThemeManager.getAncestorFormatOf(pageStyle));

        themeManager.makeElementUseNamedStyle(page, styleName2,
                currentThemeName);
        assertSame(namedStyle2, ThemeManager.getAncestorFormatOf(pageStyle));
    }

    public void testSetStyleInheritance() throws NodeException, ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();
        TypeRegistry typeRegistry = Manager.getTypeRegistry();
        final String currentThemeName = "theme";

        ThemeDescriptor themeDescriptor = new ThemeDescriptor();
        themeDescriptor.setName(currentThemeName);
        typeRegistry.register(themeDescriptor);

        ThemeElement theme = (ThemeElement) ElementFactory.create("theme");
        theme.setName(currentThemeName);
        PageElement page = (PageElement) ElementFactory.create("page");
        page.setName("default");
        theme.addChild(page);
        themeManager.registerTheme(theme);

        final String ancestorName = "ancestor style";
        Style ancestorStyle = (Style) FormatFactory.create("style");
        ancestorStyle.setName(ancestorName);
        themeManager.registerFormat(ancestorStyle);
        themeManager.setNamedObject(currentThemeName, "style", ancestorStyle);

        final String styleName2 = "named style 2";
        Style namedStyle2 = (Style) FormatFactory.create("style");
        namedStyle2.setName(styleName2);
        themeManager.registerFormat(namedStyle2);
        themeManager.setNamedObject(currentThemeName, "style", namedStyle2);

        final String styleName3 = "named style 3";
        Style namedStyle3 = (Style) FormatFactory.create("style");
        namedStyle3.setName(styleName3);
        themeManager.registerFormat(namedStyle3);
        themeManager.setNamedObject(currentThemeName, "style", namedStyle3);

        boolean allowMany = true;
        ThemeManager.setStyleInheritance(styleName2, ancestorName,
                currentThemeName, allowMany);
        assertTrue(ThemeManager.listAncestorFormatsOf(namedStyle2).contains(
                ancestorStyle));

        ThemeManager.setStyleInheritance(styleName3, ancestorName,
                currentThemeName, allowMany);
        assertTrue(ThemeManager.listAncestorFormatsOf(namedStyle3).contains(
                ancestorStyle));
        // previous inheritance still here
        assertTrue(ThemeManager.listAncestorFormatsOf(namedStyle2).contains(
                ancestorStyle));

        allowMany = false;
        ThemeManager.setStyleInheritance(styleName3, ancestorName,
                currentThemeName, allowMany);
        // previous inheritance gone
        assertFalse(ThemeManager.listAncestorFormatsOf(namedStyle2).contains(
                ancestorStyle));
    }

    public void testRemoveInheritanceFrom() throws NodeException,
            ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Style ancestor = (Style) FormatFactory.create("style");
        ancestor.setUid(0);
        Style descendant1 = (Style) FormatFactory.create("style");
        descendant1.setUid(1);
        Style descendant2 = (Style) FormatFactory.create("style");
        descendant2.setUid(2);
        Style descendant3 = (Style) FormatFactory.create("style");
        descendant3.setUid(3);

        themeManager.makeFormatInherit(descendant1, ancestor);
        themeManager.makeFormatInherit(descendant2, ancestor);
        themeManager.makeFormatInherit(descendant3, ancestor);

        assertFalse(ThemeManager.listAncestorFormatsOf(descendant1).isEmpty());
        assertFalse(ThemeManager.listAncestorFormatsOf(descendant2).isEmpty());
        assertFalse(ThemeManager.listAncestorFormatsOf(descendant3).isEmpty());
        ThemeManager.removeInheritanceFrom(ancestor);
        assertTrue(ThemeManager.listAncestorFormatsOf(descendant1).isEmpty());
        assertTrue(ThemeManager.listAncestorFormatsOf(descendant2).isEmpty());
        assertTrue(ThemeManager.listAncestorFormatsOf(descendant3).isEmpty());
    }

    public void testRemoveInheritanceTowards() throws NodeException,
            ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        Style descendant = (Style) FormatFactory.create("style");
        descendant.setUid(0);
        Style ancestor1 = (Style) FormatFactory.create("style");
        ancestor1.setUid(1);
        Style ancestor2 = (Style) FormatFactory.create("style");
        ancestor2.setUid(2);
        Style ancestor3 = (Style) FormatFactory.create("style");
        ancestor3.setUid(3);

        themeManager.makeFormatInherit(descendant, ancestor1);
        themeManager.makeFormatInherit(descendant, ancestor2);
        themeManager.makeFormatInherit(descendant, ancestor3);

        assertFalse(ThemeManager.listAncestorFormatsOf(descendant).isEmpty());
        ThemeManager.removeInheritanceTowards(descendant);
        assertTrue(ThemeManager.listAncestorFormatsOf(descendant).isEmpty());
    }

    public void testRemoveNamedStyles() throws ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();

        String currentThemeName = "theme";

        final String styleName1 = "named style 1";
        Style namedStyle1 = (Style) FormatFactory.create("style");
        namedStyle1.setName(styleName1);
        themeManager.registerFormat(namedStyle1);
        themeManager.setNamedObject(currentThemeName, "style", namedStyle1);

        final String styleName2 = "named style 2";
        Style namedStyle2 = (Style) FormatFactory.create("style");
        namedStyle2.setName(styleName2);
        themeManager.registerFormat(namedStyle2);
        themeManager.setNamedObject(currentThemeName, "style", namedStyle2);

        assertEquals(2, themeManager.getNamedStyles(currentThemeName).size());

        themeManager.removeNamedStylesOf(currentThemeName);

        assertTrue(themeManager.getNamedStyles(currentThemeName).isEmpty());
    }

    public void testStyleDependencySort() throws NodeException, ThemeException {
        ThemeManager themeManager = Manager.getThemeManager();
        String currentThemeName = "theme";

        Style s1 = createNamedStyle("1", currentThemeName);
        Style s2 = createNamedStyle("2", currentThemeName);
        Style s3 = createNamedStyle("3", currentThemeName);
        Style s4 = createNamedStyle("4", currentThemeName);
        Style s5 = createNamedStyle("5", currentThemeName);
        Style s6 = createNamedStyle("6", currentThemeName);

        // (2 -> 1 -> 3) (4 -> 2) (6 -> 2) (5 -> 2)
        themeManager.makeFormatInherit(s2, s1);
        themeManager.makeFormatInherit(s1, s3);
        themeManager.makeFormatInherit(s4, s2);
        themeManager.makeFormatInherit(s6, s2);
        themeManager.makeFormatInherit(s5, s2);

        List<Style> sortedStyles = themeManager.getSortedNamedStyles(currentThemeName);
        assertEquals("4", sortedStyles.get(0).getName());
        assertEquals("6", sortedStyles.get(1).getName());
        assertEquals("5", sortedStyles.get(2).getName());
        assertEquals("2", sortedStyles.get(3).getName());
        assertEquals("1", sortedStyles.get(4).getName());
        assertEquals("3", sortedStyles.get(5).getName());
    }
}
