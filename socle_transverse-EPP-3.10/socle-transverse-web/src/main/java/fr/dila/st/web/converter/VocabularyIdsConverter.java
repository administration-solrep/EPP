package fr.dila.st.web.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;

import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Convertit une suite d'identifiants d'un vocabulaire donné en suite une de label. Les identifiant sont representés par
 * une chaine de caractère, chaque identifiant étant séparé par une virgule. 1, 2 -> Réglementaire, Non réglementaire
 * 
 * @author jgomez
 */
public class VocabularyIdsConverter implements Converter {

	private String	directoryName;
	private boolean	hasToConvertLabel;

	public VocabularyIdsConverter() {
		this.hasToConvertLabel = false;
	}

	public VocabularyIdsConverter(String directoryName, boolean hasToConvertLabel) {
		this.directoryName = directoryName;
		this.hasToConvertLabel = hasToConvertLabel;
	}

	public VocabularyIdsConverter(String directoryName) {
		this.directoryName = directoryName;
		this.hasToConvertLabel = false;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public void setHasToConvertLabel(Boolean hasToConvertLabel) {
		this.hasToConvertLabel = hasToConvertLabel;
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String string) {
		return string;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent arg1, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			String idsStr = (String) object;
			String[] ids = idsStr.split(",");
			VocabularyService vocService = STServiceLocator.getVocabularyService();
			List<String> labels = new ArrayList<String>();
			for (String id : ids) {
				String idTrimmed = id.trim();
				String label = vocService.getEntryLabel(directoryName, idTrimmed);
				if (!hasToConvertLabel) {
					labels.add(label);
				} else {
					labels.add(ComponentUtils.translate(context, label));
				}
			}
			return StringUtils.join(labels, ", ");
		}
		return null;
	}

}
