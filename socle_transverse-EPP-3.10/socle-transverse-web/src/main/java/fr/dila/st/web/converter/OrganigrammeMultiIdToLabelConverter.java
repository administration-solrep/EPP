package fr.dila.st.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;

/**
 * Converter JSF qui fournit le label d'un noeud.
 * 
 * @author asatre
 */
public class OrganigrammeMultiIdToLabelConverter extends AbstractConverter {

	public OrganigrammeMultiIdToLabelConverter() {
		super();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			String nodeId = (String) object;
			if (nodeId.contains(OrganigrammeNodeIdConverter.PREFIX_MIN)) {
				OrganigrammeMinIdToLabelConverter o = new OrganigrammeMinIdToLabelConverter();
				return o.getAsString(context, component,
						nodeId.replaceFirst(OrganigrammeNodeIdConverter.PREFIX_MIN, ""));
			} else if (nodeId.contains(OrganigrammeNodeIdConverter.PREFIX_US)) {
				OrganigrammeUSIdToLabelConverter o = new OrganigrammeUSIdToLabelConverter();
				return o.getAsString(context, component, nodeId.replaceFirst(OrganigrammeNodeIdConverter.PREFIX_US, ""));
			} else if (nodeId.contains(OrganigrammeNodeIdConverter.PREFIX_POSTE)) {
				OrganigrammePosteIdToLabelConverter o = new OrganigrammePosteIdToLabelConverter();
				return o.getAsString(context, component,
						nodeId.replaceFirst(OrganigrammeNodeIdConverter.PREFIX_POSTE, ""));
			} else {
				return firstNonNullResult(context, component, nodeId);
			}
		}

		return null;
	}

	/**
	 * A partir d'un identifiant sans préfixe (min, dir ou poste), teste les 3 converteurs possibles et renvoie la
	 * première chaîne non nulle
	 * 
	 * @param context
	 *            le contexte
	 * @param component
	 *            le composant
	 * @param nodeId
	 *            l'identifant de noeud
	 * @return la première chaîne non nulle, null si aucun résultat
	 */
	private String firstNonNullResult(FacesContext context, UIComponent component, String nodeId) {
		String minLabel = getLabel(context, component, new OrganigrammeMinIdToLabelConverter(), nodeId);
		if (minLabel != null) {
			return minLabel;
		}

		String dirLabel = getLabel(context, component, new OrganigrammeUSIdToLabelConverter(), nodeId);
		if (dirLabel != null) {
			return dirLabel;
		}

		String posteLabel = getLabel(context, component, new OrganigrammePosteIdToLabelConverter(), nodeId);
		if (posteLabel != null) {
			return posteLabel;
		}

		return null;
	}

	private String getLabel(FacesContext context, UIComponent component, Converter converter, String nodeId) {
		String label = converter.getAsString(context, component, nodeId);
		if (isLabelFound(label)) {
			return null;
		} else {
			return label;
		}
	}

	private boolean isLabelFound(String label) {
		return StringUtils.isEmpty(label) || (label.contains("inconnu") && label.contains("**"));
	}

}
