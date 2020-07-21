package fr.dila.st.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

import fr.dila.st.api.constant.STConstant;

/**
 * Converter JSF qui fournit un identifiant unique pour un noeud de l'organigramme suivant son type.
 * 
 * @author jtremeaux
 */
public class OrganigrammeNodeIdConverter implements Converter {
	/**
	 * Préfixe ajouté à l'identifiant technique du noeud de type ministère.
	 */
	public static final String	PREFIX_MIN		= STConstant.PREFIX_MIN;

	/**
	 * Préfixe ajouté à l'identifiant technique du noeud de type unité structurelle.
	 */
	public static final String	PREFIX_US		= STConstant.PREFIX_US;

	/**
	 * Préfixe ajouté à l'identifiant technique du noeud.
	 */
	public static final String	PREFIX_POSTE	= STConstant.PREFIX_POSTE;

	/**
	 * Préfixe utilisé.
	 */
	private final String		prefix;

	/**
	 * Constructeur du convertisseur.
	 * 
	 * @param prefix
	 *            Préfixe utilisé
	 */
	public OrganigrammeNodeIdConverter(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String string) {
		if (StringUtils.isEmpty(string)) {
			return null;
		}
		return string;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (!(object instanceof String) || StringUtils.isEmpty((String) object)) {
			return null;
		}
		return prefix + (String) object;

	}
}
