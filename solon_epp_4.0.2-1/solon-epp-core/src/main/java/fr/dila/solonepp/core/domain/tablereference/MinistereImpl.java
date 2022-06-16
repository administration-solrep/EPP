package fr.dila.solonepp.core.domain.tablereference;

import com.google.common.base.Objects;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Ministere;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier mandat.
 *
 * @author sly
 */
public class MinistereImpl implements Ministere {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;
    protected Boolean mandatAttache;

    /**
     * Constructeur de MinistereImpl.
     *
     * @param document Modèle de document
     */
    public MinistereImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY
        );
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
            id
        );
    }

    @Override
    public String getNom() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_NOM_PROPERTY
        );
    }

    @Override
    public void setNom(String nom) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_NOM_PROPERTY,
            nom
        );
    }

    @Override
    public String getLibelleMinistre() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_LIBELLE_MINISTRE_PROPERTY
        );
    }

    @Override
    public void setLibelleMinistre(String libelleMinistre) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_LIBELLE_MINISTRE_PROPERTY,
            libelleMinistre
        );
    }

    @Override
    public String getAppellation() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_APPELLATION_PROPERTY
        );
    }

    @Override
    public void setAppellation(String appellation) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_APPELLATION_PROPERTY,
            appellation
        );
    }

    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_DATE_DEBUT_PROPERTY
        );
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_DATE_DEBUT_PROPERTY,
            dateDebut
        );
    }

    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_DATE_FIN_PROPERTY
        );
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_DATE_FIN_PROPERTY,
            dateFin
        );
    }

    @Override
    public String getEdition() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_EDITION_PROPERTY
        );
    }

    @Override
    public void setEdition(String edition) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_EDITION_PROPERTY,
            edition
        );
    }

    @Override
    public String getGouvernement() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_GOUVERNEMENT_PROPERTY
        );
    }

    @Override
    public void setGouvernement(String gouvernement) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MINISTERE_SCHEMA,
            SolonEppSchemaConstant.MINISTERE_GOUVERNEMENT_PROPERTY,
            gouvernement
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Ministere)) {
            return false;
        }
        Ministere you = (Ministere) obj;

        return (
            DateUtil.isSameDay(getDateDebut(), you.getDateDebut()) &&
            DateUtil.isSameDay(getDateFin(), you.getDateFin()) &&
            Objects.equal(getAppellation(), you.getAppellation()) &&
            Objects.equal(getEdition(), you.getEdition()) &&
            Objects.equal(getGouvernement(), you.getGouvernement()) &&
            Objects.equal(getLibelleMinistre(), you.getLibelleMinistre()) &&
            Objects.equal(getNom(), you.getNom())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
            getDateDebut(),
            getDateFin(),
            getAppellation(),
            getEdition(),
            getGouvernement(),
            getLibelleMinistre(),
            getNom()
        );
    }

    @Override
    public Boolean isMandatAttache() {
        return mandatAttache;
    }

    @Override
    public void setMandatAttache(Boolean mandatAttache) {
        this.mandatAttache = mandatAttache;
    }
}
