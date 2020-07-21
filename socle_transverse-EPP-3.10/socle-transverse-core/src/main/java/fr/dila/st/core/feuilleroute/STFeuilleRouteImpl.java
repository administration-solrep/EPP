package fr.dila.st.core.feuilleroute;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.ecm.platform.routing.core.impl.DocumentRouteImpl;
import fr.dila.ecm.platform.routing.core.impl.ElementRunner;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.feuilleroute.STFeuilleRoute;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation d'une feuille de route du socle transverse.
 * 
 * @author jtremeaux
 */
public abstract class STFeuilleRouteImpl extends DocumentRouteImpl implements STFeuilleRoute {
	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Constructeur de STFeuilleRouteImpl.
	 * 
	 * @param doc
	 *            doc
	 * @param runner
	 *            runner
	 */
	public STFeuilleRouteImpl(DocumentModel doc, ElementRunner runner) {
		super(doc, runner);
	}

	// *************************************************************
	// Propriétés du document.
	// *************************************************************
	@Override
	public String getTitle() {
		return DublincoreSchemaUtils.getTitle(document);
	}

	@Override
	public void setTitle(String title) {
		DublincoreSchemaUtils.setTitle(document, title);
	}

	@Override
	public void setCreator(String creator) {
		DublincoreSchemaUtils.setCreator(document, creator);
	}

	// *************************************************************
	// Propriétés de la feuille de route (socle SOLREP).
	// *************************************************************
	@Override
	public boolean isFeuilleRouteDefaut() {
		return PropertyUtil.getBooleanProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_DEFAUT_PROPERTY);
	}

	@Override
	public void setFeuilleRouteDefaut(boolean defaultQuestion) {
		PropertyUtil.setProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_DEFAUT_PROPERTY, defaultQuestion);
	}

	@Override
	public String getMinistere() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY);
	}

	@Override
	public void setMinistere(String ministere) {
		PropertyUtil.setProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY, ministere);
	}

	@Override
	public boolean isDemandeValidation() {
		return PropertyUtil.getBooleanProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY);
	}

	@Override
	public void setDemandeValidation(boolean demandeValidation) {
		PropertyUtil.setProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY, demandeValidation);
	}

	@Override
	public String getTypeCreation() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_TYPE_CREATION_PROPERTY);
	}

	@Override
	public void setTypeCreation(String typeCreation) {
		PropertyUtil.setProperty(document, STSchemaConstant.FEUILLE_ROUTE_SCHEMA,
				STSchemaConstant.FEUILLE_ROUTE_TYPE_CREATION_PROPERTY, typeCreation);
	}

	// *************************************************************
	// Propriétés calculées.
	// *************************************************************
	@Override
	public boolean isModel() {
		return checkLifeCycleState(ElementLifeCycleState.draft) || checkLifeCycleState(ElementLifeCycleState.validated);
	}
}
