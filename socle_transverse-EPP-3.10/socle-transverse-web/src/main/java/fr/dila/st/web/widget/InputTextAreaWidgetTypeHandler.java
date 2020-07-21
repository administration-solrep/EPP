package fr.dila.st.web.widget;

import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlOutputText;

import org.nuxeo.ecm.platform.forms.layout.api.BuiltinWidgetModes;
import org.nuxeo.ecm.platform.forms.layout.api.Widget;
import org.nuxeo.ecm.platform.forms.layout.api.exceptions.WidgetException;
import org.nuxeo.ecm.platform.forms.layout.facelets.FaceletHandlerHelper;
import org.nuxeo.ecm.platform.forms.layout.facelets.LeafFaceletHandler;
import org.nuxeo.ecm.platform.forms.layout.facelets.TagConfigFactory;
import org.nuxeo.ecm.platform.forms.layout.facelets.converter.ConvertTextareaHandler;
import org.nuxeo.ecm.platform.forms.layout.facelets.plugins.TextareaWidgetTypeHandler;
import org.nuxeo.ecm.platform.ui.web.component.seam.UIHtmlText;
import org.nuxeo.ecm.platform.ui.web.converter.TextareaConverter;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.CompositeFaceletHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ConvertHandler;
import com.sun.facelets.tag.jsf.ConverterConfig;

/**
 * InputTextarea widget.
 * 
 */
public class InputTextAreaWidgetTypeHandler extends TextareaWidgetTypeHandler {

	private static final long	serialVersionUID	= -2905563502170154092L;

	@Override
	public FaceletHandler getFaceletHandler(FaceletContext ctx, TagConfig tagConfig, Widget widget,
			FaceletHandler[] subHandlers) throws WidgetException {
		FaceletHandlerHelper helper = new FaceletHandlerHelper(ctx, tagConfig);
		String mode = widget.getMode();
		String widgetId = widget.getId();
		String widgetName = widget.getName();
		TagAttributes attributes;
		if (BuiltinWidgetModes.isLikePlainMode(mode)) {
			// use attributes without id
			attributes = helper.getTagAttributes(widget);
		} else {
			attributes = helper.getTagAttributes(widgetId, widget);
		}
		FaceletHandler leaf = new LeafFaceletHandler();
		if (BuiltinWidgetModes.EDIT.equals(mode)) {
			ComponentHandler input = helper.getHtmlComponentHandler(attributes, leaf, HtmlInputTextarea.COMPONENT_TYPE,
					null);
			String msgId = helper.generateMessageId(widgetName);
			ComponentHandler message = helper.getMessageComponentHandler(msgId, widgetId, null);
			FaceletHandler[] handlers = { input, message };
			return new CompositeFaceletHandler(handlers);
		} else {
			// default on text with nl2br converter for other modes
			ConverterConfig convertConfig = TagConfigFactory.createConverterConfig(tagConfig, new TagAttributes(
					new TagAttribute[0]), leaf, TextareaConverter.CONVERTER_ID);
			String escaping = (String) widget.getProperty("escape");
			if (escaping != null && !"true".equals(escaping)) {
				escaping = "false";
			} else {
				escaping = "true";
			}
			TagAttribute escape = helper.createAttribute("escape", escaping);
			attributes = FaceletHandlerHelper.addTagAttribute(attributes, escape);
			ConvertHandler convert = new ConvertTextareaHandler(convertConfig);
			ComponentHandler output = helper.getHtmlComponentHandler(attributes, convert,
					HtmlOutputText.COMPONENT_TYPE, null);
			if (BuiltinWidgetModes.PDF.equals(mode)) {
				// add a surrounding p:html tag handler
				return helper.getHtmlComponentHandler(new TagAttributes(new TagAttribute[0]), output,
						UIHtmlText.class.getName(), null);
			} else {
				return output;
			}
		}
	}
}
