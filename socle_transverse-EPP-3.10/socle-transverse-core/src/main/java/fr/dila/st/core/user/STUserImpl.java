package fr.dila.st.core.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.restlet.util.DateUtils;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier utilisateur.
 * 
 * @author Fabio Esposito
 */
public class STUserImpl implements STUser {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1412037507419664380L;

	private static final STLogger	LOGGER				= STLogFactory.getLog(STUserImpl.class);

	/**
	 * Modèle de document.
	 */
	protected DocumentModel			document;

	/**
	 * Le champ supprimé dateLastConnection est exploité dans les statistiques
	 * pour transférer la valeur du profil utilisateur.
	 */
	@Deprecated
	protected Calendar				dateLastConnection;

	/**
	 * Constructeur de STUserImpl.
	 * 
	 * @param document
	 *            Modèle de document
	 */
	public STUserImpl(DocumentModel document) {
		super();
		this.document = document;
	}

	@Override
	public DocumentModel getDocument() {
		return document;
	}

	@Override
	public void setDocument(DocumentModel document) {
		this.document = document;
	}

	@Override
	public String getUsername() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_USERNAME);
	}

	@Override
	public void setUsername(String username) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_USERNAME, username);
	}

	@Override
	public String getPassword() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "password");
	}

	@Override
	public void setPassword(String password) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "password", password);
	}

	@Override
	public String getFirstName() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME);
	}

	@Override
	public void setFirstName(String firstName) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME, firstName);
	}

	@Override
	public String getLastName() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME);
	}

	@Override
	public void setLastName(String lastName) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME, lastName);
	}

	@Override
	public String getTitle() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_TITLE);
	}

	@Override
	public void setTitle(String title) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_TITLE, title);
	}

	@Override
	public String getEmployeeType() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA,
				STSchemaConstant.USER_EMPLOYEE_TYPE);
	}

	@Override
	public void setEmployeeType(String employeeType) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_EMPLOYEE_TYPE,
				employeeType);
	}

	@Override
	public String getPostalAddress() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA,
				STSchemaConstant.USER_POSTAL_ADRESS);
	}

	@Override
	public void setPostalAddress(String postalAddress) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_POSTAL_ADRESS,
				postalAddress);
	}

	@Override
	public String getPostalCode() {
		return PropertyUtil
				.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_POSTAL_CODE);
	}

	@Override
	public void setPostalCode(String postalCode) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_POSTAL_CODE, postalCode);
	}

	@Override
	public String getLocality() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "locality");
	}

	@Override
	public void setLocality(String locality) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "locality", locality);
	}

	@Override
	public String getTelephoneNumber() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "telephoneNumber");
	}

	@Override
	public void setTelephoneNumber(String telephoneNumber) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "telephoneNumber", telephoneNumber);
	}

	@Override
	public String getEmail() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "email");
	}

	@Override
	public void setEmail(String email) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "email", email);
	}

	@Override
	public Calendar getDateDebut() {
		return PropertyUtil.getCalendarProperty(document, STSchemaConstant.USER_SCHEMA, "dateDebut");
	}

	@Override
	public void setDateDebut(Calendar dateDebut) {
		if (dateDebut != null) {
			// M156903 - mise en place d'une date tronquée à minuit
			// truncate to midnight as form push input date + current time value
			dateDebut = (Calendar) dateDebut.clone();
			dateDebut.set(Calendar.MILLISECOND, 0);
			dateDebut.set(Calendar.SECOND, 0);
			dateDebut.set(Calendar.MINUTE, 0);
			dateDebut.set(Calendar.HOUR_OF_DAY, 0);
		}
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "dateDebut", dateDebut);
	}

	@Override
	public Calendar getDateFin() {
		return PropertyUtil.getCalendarProperty(document, STSchemaConstant.USER_SCHEMA, "dateFin");
	}

	@Override
	public void setDateFin(Calendar dateFin) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "dateFin", dateFin);
	}

	@Override
	@Deprecated
	public Calendar getDateLastConnection() {
		return dateLastConnection;
	}

	@Override
	@Deprecated
	public void setDateLastConnection(Calendar dateLastConnection) {
		this.dateLastConnection = dateLastConnection;
	}

	@Override
	public boolean isTemporary() {
		String temporary = PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "temporary");
		return Boolean.parseBoolean(temporary);
	}

	@Override
	public void setTemporary(boolean temporary) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "temporary", Boolean.valueOf(temporary)
				.toString().toUpperCase());
	}

	@Override
	public boolean isOccasional() {
		String occasional = PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "occasional");
		return Boolean.parseBoolean(occasional);
	}

	@Override
	public void setOccasional(boolean occasional) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "occasional", Boolean.valueOf(occasional)
				.toString().toUpperCase());
	}

	@Override
	public List<String> getGroups() {
		return PropertyUtil.getStringListProperty(document, STSchemaConstant.USER_SCHEMA, "groups");
	}

	@Override
	public void setGroups(List<String> groups) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "groups", groups);
	}

	@Override
	public List<String> getPostes() {
		try {
			return STServiceLocator.getSTPostesService().getAllPosteIdsForUser(getUsername());
		} catch (ClientException ce) {
			LOGGER.error(null, STLogEnumImpl.FAIL_UPDATE_USER, document, ce);
		}
		return new ArrayList<String>();
	}

	@Override
	public void setPostes(List<String> postes) {
		try {
			if (postes != null) {
				List<String> oldPostes = getPostes();
				final STPostesService posteService = STServiceLocator.getSTPostesService();
				final String userName = getUsername();
				for (String oldPoste : oldPostes) {
					if (!postes.contains(oldPoste)) {
						posteService.removeUserFromPoste(oldPoste, userName);
					}
				}
				posteService.addUserToPostes(postes, userName);
			}
		} catch (ClientException ce) {
			LOGGER.error(null, STLogEnumImpl.FAIL_UPDATE_USER, document, ce);
		}
	}

	/**
	 * @return the pwdReset
	 */
	@Override
	public boolean isPwdReset() {
		String booleanString = PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "pwdReset");
		return Boolean.valueOf(booleanString);
	}

	/**
	 * @param pwdReset
	 *            the pwdReset to set
	 */
	@Override
	public void setPwdReset(boolean pwdReset) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "pwdReset", Boolean.valueOf(pwdReset)
				.toString().toUpperCase());
	}

	/**
	 * @return the deleted
	 */
	@Override
	public boolean isDeleted() {
		String booleanString = PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "deleted");
		return Boolean.valueOf(booleanString);
	}

	/**
	 * @param deleted
	 *            the deleted to set
	 */
	@Override
	public void setDeleted(boolean deleted) {
		PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, "deleted", Boolean.valueOf(deleted).toString()
				.toUpperCase());
	}

	@Override
	public boolean isPermanent() {
		return !isTemporary();
	}

	@Override
	public boolean isActive() {
		return (getDateFin() == null || DateUtils.before(getDateFin().getTime(), new Date()))
				&& (getDateDebut() != null && DateUtils.after(getDateDebut().getTime(), new Date()))
				&& !isDeleted();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		final String userName = getUsername();
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		STUserImpl other = (STUserImpl) obj;
		final String userName0 = getUsername();
		final String userName1 = other.getUsername();
		if (userName0 == null) {
			if (userName1 != null) {
				return false;
			}
		} else if (!userName0.equals(userName1)) {
			return false;
		}
		return true;
	}
}
