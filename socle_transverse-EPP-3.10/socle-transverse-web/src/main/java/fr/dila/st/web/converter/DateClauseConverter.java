package fr.dila.st.web.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Une classe pour afficher les clauses date de manière plus jolie : DATE '2007-10-03', DATE '2007-12-06' ->
 * '10/03/2007' et '12/06/2007'.
 * 
 * @author jgomez
 * 
 */
public class DateClauseConverter implements Converter {

	private static final Log	LOG	= LogFactory.getLog(DateClauseConverter.class);

	/**
	 * Default constructor
	 */
	public DateClauseConverter() {
		// do nothing
	}

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent arg1, String string) {
		return string;
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent arg1, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			final SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");
			final SimpleDateFormat frenchDate = new SimpleDateFormat("dd/MM/yyyy");

			String dateClause = (String) object;
			String dateResult = dateClause.replaceAll("DATE", "").replaceAll("TIMESTAMP", "").replace(",", "#")
					.replace("'", "");
			List<String> dateItemsResults = new ArrayList<String>();
			for (String dateItemStr : dateResult.split("#")) {
				Date dateItem = null;
				try {
					dateItem = isoDate.parse(dateItemStr);
				} catch (ParseException e) {
					LOG.warn("Parse erreur dans la conversion des dates du requêteur : " + dateItemStr);
				}
				dateItemsResults.add(frenchDate.format(dateItem));
			}
			dateResult = StringUtils.join(dateItemsResults, " et ");

			return dateResult;
		}
		return null;
	}

}
