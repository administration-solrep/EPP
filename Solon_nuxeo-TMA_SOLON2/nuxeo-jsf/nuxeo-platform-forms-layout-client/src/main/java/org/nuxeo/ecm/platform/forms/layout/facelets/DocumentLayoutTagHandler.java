/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: DocumentLayoutTagHandler.java 26053 2007-10-16 01:45:43Z atchertchian $
 */

package org.nuxeo.ecm.platform.forms.layout.facelets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.forms.layout.api.BuiltinModes;
import org.nuxeo.ecm.platform.types.adapter.TypeInfo;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.CompositeFaceletHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * Document layout tag handler.
 * <p>
 * Computes layouts in given facelet context, for given mode and document
 * attributes.
 * <p>
 * Document must be resolved at the component tree construction so it cannot be
 * bound to an iteration value.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public class DocumentLayoutTagHandler extends TagHandler {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(DocumentLayoutTagHandler.class);

    protected final TagConfig config;

    protected final TagAttribute mode;

    protected final TagAttribute documentMode;

    protected final TagAttribute value;

    protected final TagAttribute template;

    /**
     * @since 5.4.2
     */
    protected final TagAttribute defaultLayout;

    /**
     * @since 5.4.2
     */
    protected final TagAttribute includeAnyMode;

    protected final TagAttribute[] vars;

    protected final String[] reservedVarsArray = { "id", "name", "mode",
            "documentMode", "value", "template", "defaultLayout",
            "includeAnyMode" };

    public DocumentLayoutTagHandler(TagConfig config) {
        super(config);
        this.config = config;
        mode = getRequiredAttribute("mode");
        documentMode = getAttribute("documentMode");
        value = getRequiredAttribute("value");
        template = getAttribute("template");
        defaultLayout = getAttribute("defaultLayout");
        includeAnyMode = getAttribute("includeAnyMode");
        vars = tag.getAttributes().getAll();
    }

    /**
     * If resolved document has layouts, apply each of them.
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        Object document = value.getObject(ctx, DocumentModel.class);
        if (!(document instanceof DocumentModel)) {
            return;
        }

        TypeInfo typeInfo = ((DocumentModel) document).getAdapter(TypeInfo.class);
        if (typeInfo == null) {
            return;
        }
        String modeValue = mode.getValue(ctx);
        String documentModeValue = null;
        if (documentMode != null) {
            documentModeValue = documentMode.getValue(ctx);
        }
        boolean useAnyMode = true;
        if (includeAnyMode != null) {
            useAnyMode = includeAnyMode.getBoolean(ctx);
        }
        String[] layoutNames = typeInfo.getLayouts(
                documentModeValue == null ? modeValue : documentModeValue,
                useAnyMode ? BuiltinModes.ANY : null);
        if (layoutNames == null || layoutNames.length == 0) {
            // fallback on default layout
            if (defaultLayout != null) {
                layoutNames = new String[] { defaultLayout.getValue() };
            } else {
                // no layout => do nothing
                return;
            }
        }

        FaceletHandlerHelper helper = new FaceletHandlerHelper(ctx, config);
        TagAttribute modeAttr = helper.createAttribute("mode", modeValue);
        List<FaceletHandler> handlers = new ArrayList<FaceletHandler>();
        FaceletHandler leaf = new LeafFaceletHandler();
        for (String layoutName : layoutNames) {
            TagAttributes attributes = FaceletHandlerHelper.getTagAttributes(
                    helper.createAttribute("name", layoutName), modeAttr, value);
            if (template != null) {
                attributes = FaceletHandlerHelper.addTagAttribute(attributes,
                        template);
            }
            // add other variables put on original tag
            List<String> reservedVars = Arrays.asList(reservedVarsArray);
            for (TagAttribute var : vars) {
                String localName = var.getLocalName();
                if (!reservedVars.contains(localName)) {
                    attributes = FaceletHandlerHelper.addTagAttribute(
                            attributes, var);
                }
            }
            TagConfig tagConfig = TagConfigFactory.createTagConfig(config,
                    attributes, leaf);
            handlers.add(new LayoutTagHandler(tagConfig));
        }
        CompositeFaceletHandler composite = new CompositeFaceletHandler(
                handlers.toArray(new FaceletHandler[] {}));
        composite.apply(ctx, parent);
    }
}
