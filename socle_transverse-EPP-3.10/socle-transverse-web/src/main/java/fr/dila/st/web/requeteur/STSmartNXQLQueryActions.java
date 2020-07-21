/**
 * 
 * Surcharge le beam seam SmartNXQLQueryActions du package smartSearch de Nuxeo,
 * afin d'apporter d'avantages de fonctionnalité.
 * Correction de problème d'échappement dans la requête et traduction d'une requête en language
 * utilisateur.
 *
 */
package fr.dila.st.web.requeteur;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.smart.query.jsf.IncrementalSmartNXQLQuery;
import org.nuxeo.ecm.platform.smart.query.jsf.SmartNXQLQueryActions;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.core.query.Requeteur;
import fr.dila.st.core.query.translation.TranslatedStatement;
import fr.dila.st.core.requeteur.STIncrementalSmartNXQLQuery;
import fr.dila.st.web.context.NavigationContextBean;
import fr.dila.st.web.event.STEventNames;

/**
 * Surcharge du bean seam SmartNXQLQueryActions, initialement pour écraser la méthode nitCurrentSmartQuery(String
 * existingQueryPart) et mettre une nouvelle implémentation de IncrementalSmartNXQLQuery.
 * 
 * @since 5.4
 * @author jgomez
 **/

@Name("smartNXQLQueryActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.APPLICATION + 1)
public class STSmartNXQLQueryActions extends SmartNXQLQueryActions implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long					serialVersionUID	= 5709882408975692070L;

	private static final Log					LOG					= LogFactory.getLog(STSmartNXQLQueryActions.class);

	protected String							completeQuery;

	protected Requeteur							requeteur;

	@In(create = true)
	protected transient ResourcesAccessor		resourcesAccessor;

	@In(create = true, required = false)
	protected transient FacesMessages			facesMessages;

	@In(create = true)
	protected transient NavigationContextBean	navigationContext;

	@RequestParameter
	protected Integer							index;

	public STSmartNXQLQueryActions() {
		super();
	}

	public void initCurrentSmartQuery(String existingQueryPart, boolean resetHistory) {
		super.initCurrentSmartQuery(existingQueryPart, resetHistory);
		currentSmartQuery = new STIncrementalSmartNXQLQuery(existingQueryPart);
		requeteur = new Requeteur();
		this.completeQuery = "SELECT * FROM Dossier AS d WHERE " + existingQueryPart;
		requeteur.setQuery(this.completeQuery);
	}

	/**
	 * Cette méthode est appelé par la classe mère lors de l'ajout d'une entrée. On veut déclencher un évenement seam.
	 * 
	 */
	public void buildQueryPart(ActionEvent event) throws ClientException {
		Events.instance().raiseEvent(STEventNames.REQUETEUR_QUERY_PART_ADDED);
		super.buildQueryPart(event);
	}

	/**
	 * Cette méthode est appelé par la classe mère lors de la modification de la requête. On veut déclencher un
	 * événement seam.
	 * 
	 */
	public void setQueryPart(ActionEvent event, String newQuery, boolean rebuildSmartQuery) throws ClientException {
		Events.instance().raiseEvent(STEventNames.REQUETEUR_QUERY_PART_CHANGED);
		if (!rebuildSmartQuery) {
			this.completeQuery = "SELECT * FROM Dossier AS d WHERE ";
			if (LOG.isDebugEnabled()) {
				LOG.debug("Set QueryPart completeQuery = " + completeQuery);
			}
		}
		super.setQueryPart(event, newQuery, rebuildSmartQuery);
	}

	/**
	 * Met le libellé utilisateur correspondant à la portion de requête ajouté.
	 */
	@Observer(STEventNames.REQUETEUR_QUERY_PART_CHANGED)
	public void updateQuery() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Update User Info");
		}
		updateCompleteQuery();
		requeteur.setQuery(this.completeQuery);
		requeteur.updateTranslation();
		if (LOG.isDebugEnabled()) {
			LOG.debug("User info : " + completeQuery);
		}
	}

	/**
	 * Met à jour l'attribut completeQuery qui contient la requête.
	 */
	protected void updateCompleteQuery() {
		IncrementalSmartNXQLQuery query = currentSmartQuery;
		this.completeQuery = "SELECT * FROM Dossier AS d WHERE " + query.getExistingQueryPart();
	}

	public List<TranslatedStatement> getUserInfo() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Get User Info");
		}
		List<TranslatedStatement> statements = new ArrayList<TranslatedStatement>();
		try {
			requeteur.updateTranslation();
			statements = requeteur.getStatements();
			if (LOG.isDebugEnabled()) {
				LOG.debug("QUERY = " + requeteur.getQuery());
			}
		} catch (Exception e) {
			// Erreur de parsing qui ne devrait pas arriver dans cette méthode, l'utilisateur ne devrait pouvoir
			// constituer que des
			// requêtes valides.
			// On ne peut pas récupérer l'erreur à ce stade. On réinitialise la requête et le tableau de traduction.
			// La requête de l'utilisateur est perdue.
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("error.smart.query.invalidQuery"));
			return statements;
		}
		return statements;
	}

	/**
	 * Validates the query part: throws a {@link ValidatorException} if query is not a String, or if
	 * {@link IncrementalSmartNXQLQuery#isValid(String)} returns false.
	 * 
	 * @see IncrementalSmartNXQLQuery#isValid(String)
	 */
	public void validateQueryPart(FacesContext context, UIComponent component, Object value) {
		if (value == null || !(value instanceof String)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"error.smart.query.invalidQuery"), null);
			context.addMessage(null, message);
			throw new ValidatorException(message);
		}
	}

	@Override
	public void clearQueryPart(ActionEvent event) throws ClientException {
		super.clearQueryPart(event);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Clear requeteur");
		}
		this.completeQuery = "SELECT * FROM Dossier AS d WHERE ";
		this.queryPart = StringUtils.EMPTY;
		requeteur.setQuery(this.completeQuery);
	}

	private void reinitialiseSmartQuery() {
		this.setQueryPart(requeteur.getWherePart());
		currentSmartQuery.setExistingQueryPart(requeteur.getWherePart());
		initCurrentSmartQuery(requeteur.getWherePart(), false);
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIViewRoot root = context.getViewRoot();
			UIComponent composant = findComponent(root, queryPartComponentId);
			EditableValueHolder queryPartComp = ComponentUtils.getComponent(composant, queryPartComponentId,
					EditableValueHolder.class);
			if (queryPartComp != null) {
				queryPartComp.setSubmittedValue(requeteur.getWherePart());
				queryPartComp.setValue(requeteur.getWherePart());
			}
		} catch (Exception e) {
			LOG.error(e);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Portion: " + this.queryPart);
		}
	}

	public void delete(ActionEvent event) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Supprimer à l'index " + index);
		}
		requeteur.remove(index);
		requeteur.updateTranslation();
		LOG.info("Met la requête à vide");
		reinitialiseSmartQuery();
	}

	public void up(ActionEvent event) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Monter à l'index " + index);
		}
		requeteur.up(index);
		requeteur.updateTranslation();
		reinitialiseSmartQuery();
	}

	public void down(ActionEvent event) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Descendre à l'index " + index);
		}
		requeteur.down(index);
		requeteur.updateTranslation();
		reinitialiseSmartQuery();
	}

	public Requeteur getCurrentRequeteur() {
		requeteur.setQuery(this.completeQuery);
		return requeteur;
	}

	/**
	 * Finds component with the given id
	 */
	private UIComponent findComponent(UIComponent c, String id) {
		if (id.equals(c.getId())) {
			return c;
		}
		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent found = findComponent(kids.next(), id);
			if (found != null) {
				return found;
			}
		}
		return null;
	}
}
