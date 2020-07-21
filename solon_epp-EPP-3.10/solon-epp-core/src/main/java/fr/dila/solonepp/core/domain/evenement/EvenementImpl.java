package fr.dila.solonepp.core.domain.evenement;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier évenement.
 * 
 * @author jtremeaux
 */
public class EvenementImpl implements Evenement {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Modèle de document.
	 */
	protected DocumentModel document;

	/**
	 * Constructeur de EvenementImpl.
	 * 
	 * @param document
	 *            Modèle de document
	 */
	public EvenementImpl(DocumentModel document) {
		this.document = document;
	}

	@Override
	public DocumentModel getDocument() {
		return document;
	}

	@Override
	public String toString() {
		return "[EvenementImpl : Id :" + document.getId() + ", title : " + getTitle() + ", type : " + getTypeEvenement()
				+ "]";
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
	public String getTypeEvenement() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY);
	}

	@Override
	public void setTypeEvenement(String typeEvenement) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY, typeEvenement);
	}

	@Override
	public String getEvenementParent() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_EVENEMENT_PARENT_PROPERTY);
	}

	@Override
	public void setEvenementParent(String evenementParent) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_EVENEMENT_PARENT_PROPERTY, evenementParent);
	}

	@Override
	public String getDossier() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY);
	}

	@Override
	public void setDossier(String dossier) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY, dossier);
	}

	@Override
	public String getDossierPrecedent() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DOSSIER_PRECEDENT_PROPERTY);
	}

	@Override
	public void setDossierPrecedent(String dossierPrecedent) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DOSSIER_PRECEDENT_PROPERTY, dossierPrecedent);
	}

	@Override
	public String getEmetteur() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY);
	}

	@Override
	public void setEmetteur(String emetteur) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY, emetteur);
	}

	@Override
	public String getDestinataire() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY);
	}

	@Override
	public void setDestinataire(String destinataire) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY, destinataire);
	}

	@Override
	public List<String> getDestinataireCopie() {
		return PropertyUtil.getStringListProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY);
	}

	@Override
	public void setDestinataireCopie(List<String> destinataireCopie) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY, destinataireCopie);
		setDestinataireCopieConcat(StringUtils.join(destinataireCopie, ","));
	}

	@Override
	public String getDestinataireCopieConcat() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_CONCAT_PROPERTY);
	}

	@Override
	public void setDestinataireCopieConcat(String destinataireCopieConcat) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_CONCAT_PROPERTY, destinataireCopieConcat);
	}

	@Override
	public String getBrancheAlerte() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_BRANCHE_ALERTE_PROPERTY);
	}

	@Override
	public void setBrancheAlerte(String brancheAlerte) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.EVENEMENT_BRANCHE_ALERTE_PROPERTY, brancheAlerte);
	}

	@Override
	public boolean isEtatInit() {
		return SolonEppLifecycleConstant.EVENEMENT_INIT_STATE.equals(PropertyUtil.getCurrentLifeCycleState(document));
	}

	@Override
	public boolean isEtatBrouillon() {
		return SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE
				.equals(PropertyUtil.getCurrentLifeCycleState(document));
	}

	@Override
	public boolean isEtatPublie() {
		return SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE.equals(PropertyUtil.getCurrentLifeCycleState(document));
	}

	@Override
	public boolean isEtatInstance() {
		return SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE
				.equals(PropertyUtil.getCurrentLifeCycleState(document));
	}

	@Override
	public boolean isEtatAttenteValidation() {
		return SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE
				.equals(PropertyUtil.getCurrentLifeCycleState(document));
	}

	@Override
	public boolean isEtatAnnule() {
		return SolonEppLifecycleConstant.EVENEMENT_ANNULE_STATE.equals(PropertyUtil.getCurrentLifeCycleState(document));
	}

	@Override
	public void setIdEvenement(String idEvenement) {
		PropertyUtil.setProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA, SolonEppSchemaConstant.ID_EVENEMENT,
				idEvenement);
	}

	@Override
	public String getIdEvenement() {
		return PropertyUtil.getStringProperty(document, SolonEppSchemaConstant.EVENEMENT_SCHEMA,
				SolonEppSchemaConstant.ID_EVENEMENT);
	}
}
