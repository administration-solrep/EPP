/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Julien Carsique
 *
 */

package org.nuxeo.launcher.gui;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Colored text pane. Allow to choose the style when appending some text.
 *
 * @author jcarsique
 */
public class ColoredTextPane extends JTextPane {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ColoredTextPane.class);

    private SimpleAttributeSet style;

    private Document doc;

    public ColoredTextPane() {
        style = new SimpleAttributeSet();
        this.setContentType("text/rtf");
        this.setEditorKit(new RTFEditorKit());
        doc = this.getDocument();
    }

    /**
     * Append text at the end of document, choosing foreground and background
     * colors, and bold attribute.
     *
     * @param text Text to append
     * @param color Foreground color
     * @param bgColor Background color
     * @param isBold Is the text bold ?
     */
    public void append(String text, Color color, Color bgColor, boolean isBold) {
        StyleConstants.setForeground(style, color);
        StyleConstants.setBackground(style, bgColor);
        StyleConstants.setBold(style, isBold);
        int len = doc.getLength();
        try {
            doc.insertString(len, text + "\n", style);
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Calls {@link #append(String, Color)} with Color.WHITE foreground color.
     *
     * @see #append(String, Color) #append(String, Color, Color, boolean)
     */
    public void append(String text) {
        append(text, Color.WHITE);
    }

    /**
     * Calls {@link #append(String, Color, Color, Boolean)} with foreground
     * color given as parameter, background color equal to component background
     * and isBold equal to false.
     *
     * @see #append(String, Color, Color, boolean)
     */
    public void append(String text, Color color) {
        append(text, color, getBackground(), false);
    }

    /**
     * Calls {@link #append(String, Color, Color, Boolean)} with background
     * color equal to component background.
     *
     * @see #append(String, Color, Color, boolean)
     */
    public void append(String text, Color color, boolean isBold) {
        append(text, color, getBackground(), isBold);
    }

}
