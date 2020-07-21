package fr.dila.st.core.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.util.PropertyUtil;

public class MockSTUser implements STUser {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5050438251918724714L;

	/**
	 * Modèle de document.
	 */
	protected DocumentModel		document;

	/**
	 * Constructeur de STUserImpl.
	 * 
	 * @param document
	 *            Modèle de document
	 */
	public MockSTUser(DocumentModel document) {
		super();
		this.document = document;
	}

	@Override
	public DocumentModel getDocument() {
		// TODO Auto-generated method stub
		return this.document;
	}

	@Override
	public void setDocument(DocumentModel document) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_USERNAME);
	}

	@Override
	public void setUsername(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPassword(String password) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFirstName() {
		// TODO Auto-generated method stub
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME);
	}

	@Override
	public void setFirstName(String firstName) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLastName() {
		// TODO Auto-generated method stub
		return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME);
	}

	@Override
	public void setLastName(String lastName) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getEmployeeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmployeeType(String employeeType) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPostalAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPostalAddress(String postalAddress) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPostalCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPostalCode(String postalCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLocality() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocality(String locality) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTelephoneNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTelephoneNumber(String telephoneNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmail(String email) {
		// TODO Auto-generated method stub

	}

	@Override
	public Calendar getDateDebut() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDateDebut(Calendar dateDebut) {
		// TODO Auto-generated method stub

	}

	@Override
	public Calendar getDateFin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDateFin(Calendar dateFin) {
		// TODO Auto-generated method stub

	}

	@Override
	public Calendar getDateLastConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDateLastConnection(Calendar dateLastConnection) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTemporary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTemporary(boolean temporary) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOccasional() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setOccasional(boolean occasional) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getGroups() {
		return new ArrayList<String>();
	}

	@Override
	public void setGroups(List<String> groups) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getPostes() {
		return new ArrayList<String>();
	}

	@Override
	public void setPostes(List<String> postes) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPwdReset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPwdReset(boolean pwdReset) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPermanent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDeleted(boolean deleted) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

}
