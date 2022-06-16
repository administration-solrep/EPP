package fr.dila.st.core.user;

import static fr.dila.st.api.constant.STSchemaConstant.USER_GROUPS;
import static fr.dila.st.api.constant.STSchemaConstant.USER_SCHEMA;


import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation de l'objet métier utilisateur.
 *
 * @author Fabio Esposito
 */
public class STUserImpl implements STUser {
    /**
     *
     */
    private static final long serialVersionUID = 1412037507419664380L;

    private static final STLogger LOGGER = STLogFactory.getLog(STUserImpl.class);

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

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
    public String getSalt() {
        return PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_SALT);
    }

    @Override
    public void setSalt(String salt) {
        PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_SALT, salt);
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
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            STSchemaConstant.USER_EMPLOYEE_TYPE
        );
    }

    @Override
    public void setEmployeeType(String employeeType) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            STSchemaConstant.USER_EMPLOYEE_TYPE,
            employeeType
        );
    }

    @Override
    public String getPostalAddress() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            STSchemaConstant.USER_POSTAL_ADRESS
        );
    }

    @Override
    public void setPostalAddress(String postalAddress) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            STSchemaConstant.USER_POSTAL_ADRESS,
            postalAddress
        );
    }

    @Override
    public String getPostalCode() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            STSchemaConstant.USER_POSTAL_CODE
        );
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
            DateUtil.setDateToBeginingOfDay(dateDebut);
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
    public boolean isTemporary() {
        String temporary = PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "temporary");
        return Boolean.parseBoolean(temporary);
    }

    @Override
    public void setTemporary(boolean temporary) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            "temporary",
            Boolean.toString(temporary).toUpperCase()
        );
    }

    @Override
    public boolean isOccasional() {
        String occasional = PropertyUtil.getStringProperty(document, STSchemaConstant.USER_SCHEMA, "occasional");
        return Boolean.parseBoolean(occasional);
    }

    @Override
    public void setOccasional(boolean occasional) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            "occasional",
            Boolean.toString(occasional).toUpperCase()
        );
    }

    @Override
    public List<String> getGroups() {
        return PropertyUtil.getStringListProperty(document, USER_SCHEMA, USER_GROUPS);
    }

    @Override
    public void setGroups(List<String> groups) {
        PropertyUtil.setProperty(document, USER_SCHEMA, USER_GROUPS, groups);
    }

    @Override
    public List<String> getPostes() {
        try {
            return STServiceLocator.getSTPostesService().getAllPosteIdsForUser(getUsername());
        } catch (NuxeoException ce) {
            LOGGER.error(null, STLogEnumImpl.FAIL_UPDATE_USER, document, ce);
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getMinisteres() {
        try {
            Set<String> postes = new HashSet<String>(getPostes());
            List<EntiteNode> ministeres = STServiceLocator.getSTMinisteresService().getMinistereParentFromPostes(postes);
            Set<String> ministeresLabels = new HashSet<String>(ministeres.stream().map(ministere -> ministere.getLabel()).collect(Collectors.toList()));
            return ministeresLabels.stream().collect(Collectors.toList());

        } catch (NuxeoException ce) {
            LOGGER.error(null, STLogEnumImpl.FAIL_UPDATE_USER, document, ce);
        }
        return new ArrayList<>();
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
        } catch (NuxeoException ce) {
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
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            "pwdReset",
            Boolean.toString(pwdReset).toUpperCase()
        );
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
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            "deleted",
            Boolean.toString(deleted).toUpperCase()
        );
    }

    @Override
    public boolean isPermanent() {
        return !isTemporary();
    }

    @Override
    public boolean isActive() {
        Calendar now = Calendar.getInstance();
        return (
            !isDeleted() &&
            (getDateFin() == null || getDateFin().after(now)) &&
            (getDateDebut() != null && getDateDebut().before(now))
        );
    }

    @Override
    public Calendar getDateDerniereConnexion() {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            STSchemaConstant.USER_DATE_DERNIERE_CONNEXION
        );
    }

    @Override
    public void setDateDerniereConnexion(Calendar dateDerniereConnexion) {
        PropertyUtil.setProperty(
            document,
            STSchemaConstant.USER_SCHEMA,
            STSchemaConstant.USER_DATE_DERNIERE_CONNEXION,
            dateDerniereConnexion
        );
    }

    @Override
    public boolean isLogout() {
        return PropertyUtil.getBooleanProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_IS_LOGOUT);
    }

    @Override
    public void setLogout(boolean isLogout) {
        PropertyUtil.setProperty(document, STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_IS_LOGOUT, isLogout);
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

    private String computeFullName(Stream<String> names) {
        return StringUtils.defaultIfBlank(joinNames(names), getUsername());
    }

    private String joinNames(Stream<String> names) {
        return names.filter(StringUtils::isNotBlank).map(String::trim).collect(Collectors.joining(" "));
    }

    @Override
    public String getFullName() {
        return computeFullName(Stream.of(getFirstName(), getLastName()));
    }

    @Override
    public String getFullNameOrEmpty() {
        return joinNames(Stream.of(getFirstName(), getLastName()));
    }

    @Override
    public String getReversedFullName() {
        return computeFullName(Stream.of(getLastName(), getFirstName()));
    }

    @Override
    public String getFullNameWithUsername() {
        return getFullName() + " (" + getUsername() + ")";
    }

    @Override
    public int compareTo(STUser stUser) {
        return getUsername().compareTo(stUser.getUsername());
    }
}
