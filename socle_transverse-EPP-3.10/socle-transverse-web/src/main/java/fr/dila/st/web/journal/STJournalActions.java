package fr.dila.st.web.journal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * 
 * @author asatre
 * 
 */
public abstract class STJournalActions implements Serializable {

	private static final long				serialVersionUID	= 1648035283849405049L;

	private static final Log				LOG					= LogFactory.getLog(STJournalActions.class);

	@In(create = true, required = false)
	protected transient CoreSession			documentManager;

	@In(create = true, required = false)
	protected transient NavigationContext	navigationContext;

	@In(create = true, required = false)
	protected WebActions					webActions;

	@In(create = true, required = false)
	protected transient FacesMessages		facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor	resourcesAccessor;

	protected String						contentViewName		= "";

	protected Set<String>					categoryList		= new HashSet<String>();

	protected String						currentCategory;

	protected String						currentUtilisateur;

	protected Date							currentDateDebut;

	protected Date							currentDateFin;

	/**
	 * Default constructor
	 */
	public STJournalActions() {
		// do nothing
	}

	@PostConstruct
	public void initialize() {
		// initialisation du nom de la contentView
		this.setContentViewName(getDefaultContentViewName());
	}

	public void suggestionChange() {
		// System.out.println("user change");
	}

	public void reset() {
		currentCategory = null;
		currentUtilisateur = null;
		currentDateDebut = null;
		currentDateFin = null;
	}

	public String getContentViewName() {
		return contentViewName;
	}

	public void setContentViewName(String contentViewName) {
		this.contentViewName = contentViewName;
	}

	@Destroy
	public void destroy() {
		LOG.debug("Removing user workspace actions bean");
	}

	/**
	 * Retourne la liste des categories
	 * 
	 * @return List<String>
	 */
	protected void initCategoryList() {
		setContentViewName(getDefaultContentViewName());
		// categorie d'opération :feuille de route, bordereau , fond de dossier , parapheur et journal.
		String[] category = new String[] { "", STEventConstant.CATEGORY_FEUILLE_ROUTE,
				STEventConstant.CATEGORY_BORDEREAU, STEventConstant.CATEGORY_FDD, STEventConstant.CATEGORY_PARAPHEUR,
				STEventConstant.CATEGORY_JOURNAL, };
		// ReponsesEventConstant.CATEGORY_INTERFACE
		// STEventConstant.CATEGORY_ADMINISTRATION
		categoryList.addAll(Arrays.asList(category));
	}

	/**
	 * Retourne le contenu de la chaine d'entrée traduite mot à mot
	 * 
	 */
	public String translate(String entry) {
		if (entry == null)
			return " ";
		else {
			final String[] splitted = entry.split(" ");
			String result = "";
			String translation = null;
			for (String part : splitted) {
				translation = resourcesAccessor.getMessages().get(part);
				if (translation == null || translation.isEmpty()) {
					result = result + " " + part;
				} else {
					result = result + " " + translation;
				}
			}
			return result.trim();
		}
	}

	/**
	 * Affiche les postes d'un utilisateur à partir de son identifiant.
	 * 
	 * @param string
	 * @return String listes des postes
	 */
	public String getPostes(String idUser) {
		try {
			final UserManager userManager = STServiceLocator.getUserManager();
			if (idUser == null || idUser.isEmpty() || userManager == null) {
				return null;
			}
			// récupération de l'utilisateur
			DocumentModel userModel = userManager.getUserModel(idUser);
			if (userModel == null) {
				return idUser;
			}
			STUser stUserModel = userModel.getAdapter(STUser.class);
			if (stUserModel == null) {
				return null;
			}
			// récupération des identifiants de postes
			List<String> postesIds = stUserModel.getPostes();
			if (postesIds == null || postesIds.size() < 1) {
				// Si on n'a pas de poste le poste n'est pas inconnu mais absent
				return "";
			}
			// récupérations des labels des postes
			List<String> listPosteLabels = new ArrayList<String>();

			List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(postesIds);

			if (listNode != null) {
				for (OrganigrammeNode node : listNode) {

					// récpération du label du poste
					if (node != null) {
						if (node.getLabel() != null) {
							listPosteLabels.add(node.getLabel());
						} else {
							// Pas de libellé pour l'identifiant du poste donc
							listPosteLabels.add("**poste (" + node.getId() + ") inconnu**");
						}
					}
				}
			}
			// affichage de la liste des postes
			if (!listPosteLabels.isEmpty()) {
				return StringUtils.join(listPosteLabels, ", ");
			} else {
				// Si on n'a pas de poste le poste n'est pas inconnu mais absent
				return "";
			}

		} catch (ClientException e) {
			return null;
		}
	}

	/**
	 * Affiche la référence du dossier à partir de l'identifiant du dossier.
	 * 
	 * @param string
	 *            dossierRef
	 * @return String dossierRef
	 */
	public String getDossierRef(String dossierRef) {
		return dossierRef;
	}

	// Getters et Setters

	/**
	 * Retourne la liste des categories
	 * 
	 * @return Set<String>
	 */
	public Set<String> getCategoryList() {
		// La liste des catégories dépend du dossier, il ne faut pas la mettre dans un attribut de session.
		initCategoryList();

		return categoryList;
	}

	/**
	 * définit la liste des categories
	 * 
	 * @param categoryList
	 */
	public void setCategoryList(Set<String> categoryList) {
		this.categoryList = categoryList;
	}

	public String getCurrentCategory() {
		return currentCategory;
	}

	public void setCurrentCategory(String currentCategory) {
		this.currentCategory = currentCategory;
	}

	public String getCurrentUtilisateur() {
		return currentUtilisateur;
	}

	public void setCurrentUtilisateur(String currentUtilisateur) {
		if (currentUtilisateur != null && !currentUtilisateur.isEmpty()) {
			this.currentUtilisateur = STServiceLocator.getSTUserService().getUserFullName(currentUtilisateur);
		} else {
			this.currentUtilisateur = currentUtilisateur;
		}
	}

	public Date getCurrentDateDebut() {
		return currentDateDebut;
	}

	public void setCurrentDateDebut(Date currentDateDebut) {
		this.currentDateDebut = currentDateDebut;
	}

	public Date getCurrentDateFin() {
		return currentDateFin;
	}

	public void setCurrentDateFin(Date currentDateFin) {
		this.currentDateFin = currentDateFin;
	}

	/**
	 * Vue par défaut à surcharger dans le bean parent
	 * 
	 * @return
	 */
	public abstract String getDefaultContentViewName();

}
