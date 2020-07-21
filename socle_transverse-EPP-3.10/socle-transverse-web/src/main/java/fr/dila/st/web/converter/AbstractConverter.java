package fr.dila.st.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public abstract class AbstractConverter implements Converter {

	protected AbstractConverter() {
		// do nothing
	}

	@Override
	public final Object getAsObject(FacesContext context, UIComponent component, String string) {
		return string;
	}

}
