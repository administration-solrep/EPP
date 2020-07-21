package fr.dila.st.api.user;

import java.util.Calendar;

import fr.dila.st.api.domain.STDomainObject;

public interface STProfilUtilisateur extends STDomainObject {

	/**
	 * @return la date à laquelle le mot de passe a été changé
	 */
	Calendar getDernierChangementMotDePasse();

	/**
	 * @param dernierChangementMotDePasse
	 *            La date à laquelle le mot de passe a été changé
	 */
	void setDernierChangementMotDePasse(Calendar dernierChangementMotDePasse);

}
