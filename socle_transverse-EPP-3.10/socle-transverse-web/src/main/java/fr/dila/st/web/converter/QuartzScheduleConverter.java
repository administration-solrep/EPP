package fr.dila.st.web.converter;

import java.text.SimpleDateFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

/**
 * Converter schedule batchs Quartz
 * 
 * @author JBT
 * 
 */
public class QuartzScheduleConverter implements Converter {

	// private static final Log LOG = LogFactory.getLog(QuartzScheduleConverter.class);

	/**
	 * Default constructor
	 */
	public QuartzScheduleConverter() {
		// do nothing
	}

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent arg1, String string) {
		return null;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent arg1, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			String schedule = (String) object;
			long scheduledTime;
			try {
				scheduledTime = Long.parseLong(schedule);
			} catch (NumberFormatException e) {
				return null;
			}
			if (scheduledTime == -1) {
				return "Aucune exécution précédente";
			}

			final SimpleDateFormat frenchDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return frenchDate.format(scheduledTime);
		}
		return null;
	}

}
