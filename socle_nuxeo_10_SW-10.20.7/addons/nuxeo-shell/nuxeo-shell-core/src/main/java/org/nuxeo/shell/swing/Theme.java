/*
 * (C) Copyright 2006-2010 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.shell.swing;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.utils.StringUtils;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class Theme {

    protected static Map<String, Theme> themes = new HashMap<String, Theme>();

    protected static String defTheme = "Default";

    protected static Font defFont = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    static {
        themes.put("Green", new Theme("Default", defFont, Color.GREEN, Color.BLACK));
        themes.put("Linux", new Theme("Linux", defFont, Color.WHITE, Color.BLACK));
        themes.put("Default", new Theme("White", defFont, Color.BLACK, Color.WHITE));
    }

    public static void addTheme(Theme theme) {
        themes.put(theme.getName(), theme);
    }

    public static Theme getTheme(String name) {
        return themes.get(name);
    }

    public static Theme[] getThemes() {
        return themes.values().toArray(new Theme[themes.size()]);
    }

    public static Font getFont(String desc) {
        return Font.decode(desc);
    }

    public static int getFontStyle(String weight) {
        if ("bold".equals(weight)) {
            return Font.BOLD;
        } else if ("italic".equals(weight)) {
            return Font.ITALIC;
        } else {
            return Font.PLAIN;
        }
    }

    public static String getFontStyleName(int code) {
        switch (code) {
        case Font.BOLD:
            return "bold";
        case Font.ITALIC:
            return "italic";
        case Font.PLAIN:
            return "plain";
        default:
            if (code == (Font.BOLD | Font.ITALIC)) {
                return "bolditalic";
            }
            return "plain";
        }
    }

    public static String getFontString(Font font) {
        return font.getName().concat("-").concat(getFontStyleName(font.getStyle())).concat("-").concat(
                String.valueOf(font.getSize()));
    }

    public static String getColorName(Color color) {
        String r = Integer.toHexString(color.getRed());
        if (r.length() == 1) {
            r = "0" + r;
        }
        String g = Integer.toHexString(color.getGreen());
        if (g.length() == 1) {
            g = "0" + g;
        }
        String b = Integer.toHexString(color.getBlue());
        if (b.length() == 1) {
            b = "0" + b;
        }
        return r + g + b;
    }

    public static Color getColor(String rgb) {
        if (rgb.startsWith("#")) {
            rgb = rgb.substring(1);
        }
        if (rgb.length() != 6) {
            throw new ShellException("Invalid color: " + rgb
                    + ". Should be #RRGGBB in hexa. The # character may be omited.");
        }
        String r = rgb.substring(0, 2);
        String g = rgb.substring(2, 4);
        String b = rgb.substring(4);
        return new Color(Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16));
    }

    protected String name;

    protected Color bg;

    protected Color fg;

    protected Font font;

    public Theme(String name, Font font, Color fg, Color bg) {
        this.name = name;
        this.font = font;
        this.bg = bg;
        this.fg = fg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Font getFont() {
        return font;
    }

    public Color getBgColor() {
        return bg;
    }

    public Color getFgColor() {
        return fg;
    }

    public void setFgColor(Color fg) {
        this.fg = fg;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setBgColor(Color bg) {
        this.bg = bg;
    }

    public static Theme fromString(String name, String expr) {
        String[] ar = StringUtils.split(expr, ';', true);
        if (ar.length != 3) {
            throw new ShellException("Bad theme expression: " + expr);
        }
        Font font = Theme.getFont(ar[0]);
        Color color = Theme.getColor(ar[1]);
        Color bgcolor = Theme.getColor(ar[2]);
        return new Theme(name, font, color, bgcolor);
    }

    public String toString() {
        return getFontString(font).concat("; ").concat(getColorName(fg)).concat("; ").concat(getColorName(bg));
    }

}
