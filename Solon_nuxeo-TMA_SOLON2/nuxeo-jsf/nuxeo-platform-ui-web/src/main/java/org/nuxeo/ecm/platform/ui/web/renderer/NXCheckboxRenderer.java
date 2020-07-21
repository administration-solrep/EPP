/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.platform.ui.web.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.CheckboxRenderer;
import com.sun.faces.util.RequestStateManager;

/**
 * Renderer that does not ignore the converter set on the component on submit
 *
 * @author Anahide Tchertchian
 */
public class NXCheckboxRenderer extends CheckboxRenderer {

    private static final Log log = LogFactory.getLog(NXCheckboxRenderer.class);

    private static final String[] ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.SELECTBOOLEANCHECKBOX);

    public static final String RENDERER_TYPE = "javax.faces.NXCheckbox";

    @Override
    public Object getConvertedValue(FacesContext context,
            UIComponent component, Object submittedValue)
            throws ConverterException {

        String newValue = null;
        if (submittedValue instanceof Boolean) {
            newValue = ((Boolean) submittedValue).toString();
        } else if (submittedValue instanceof String) {
            newValue = (String) submittedValue;
        } else if (submittedValue != null) {
            log.error("Unsupported submitted value, should be a string or boolean: '"
                    + submittedValue + "' => using false");
        }

        Converter converter = null;
        // If there is a converter attribute, use it to to ask application
        // instance for a converter with this identifer.
        if (component instanceof ValueHolder) {
            converter = ((ValueHolder) component).getConverter();
        }

        if (converter != null) {
            // If the conversion eventually falls to needing to use EL type
            // coercion,
            // make sure our special ConverterPropertyEditor knows about this
            // value.
            RequestStateManager.set(context,
                    RequestStateManager.TARGET_COMPONENT_ATTRIBUTE_NAME,
                    component);
            return converter.getAsObject(context, component, newValue);
        } else {
            return Boolean.valueOf(newValue);
        }
    }

    protected void getEndTextToRender(FacesContext context,
            UIComponent component, String currentValue) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        assert (writer != null);
        String styleClass;

        writer.startElement("input", component);
        writeIdAttributeIfNecessary(context, writer, component);
        writer.writeAttribute("type", "checkbox", "type");
        writer.writeAttribute("name", component.getClientId(context),
                "clientId");

        // NXP-5813: check current value instead of calling
        // UISelectBoolean#isSelected that does not handle conversion correctly
        if ("true".equals(currentValue)) {
            writer.writeAttribute("checked", Boolean.TRUE, "value");
        }
        if (null != (styleClass = (String) component.getAttributes().get(
                "styleClass"))) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        RenderKitUtils.renderPassThruAttributes(writer, component, ATTRIBUTES);
        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, component);

        writer.endElement("input");

    }

}
