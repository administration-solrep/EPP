package fr.dila.solonepp.core.domain.tablereference;

import com.google.common.base.Objects;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.MembreGroupe;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier membre_groupe.
 *
 * @author sly
 */
public class MembreGroupeImpl implements MembreGroupe {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de MembreGroupeImpl.
     *
     * @param document Modèle de document
     */
    public MembreGroupeImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY
        );
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
            id
        );
    }

    @Override
    public String getMandat() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_MANDAT_PROPERTY
        );
    }

    @Override
    public void setMandat(String mandat) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_MANDAT_PROPERTY,
            mandat
        );
    }

    @Override
    public String getOrganisme() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_ORGANISME_PROPERTY
        );
    }

    @Override
    public void setOrganisme(String organisme) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_ORGANISME_PROPERTY,
            organisme
        );
    }

    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_DATE_DEBUT_PROPERTY
        );
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_DATE_DEBUT_PROPERTY,
            dateDebut
        );
    }

    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_DATE_FIN_PROPERTY
        );
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA,
            SolonEppSchemaConstant.MEMBRE_GROUPE_DATE_FIN_PROPERTY,
            dateFin
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MembreGroupe)) {
            return false;
        }
        MembreGroupe you = (MembreGroupe) obj;

        return (
            DateUtil.isSameDay(getDateDebut(), you.getDateDebut()) &&
            DateUtil.isSameDay(getDateFin(), you.getDateFin()) &&
            Objects.equal(getMandat(), you.getMandat()) &&
            Objects.equal(getOrganisme(), you.getOrganisme())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDateDebut(), getDateFin(), getMandat(), getOrganisme());
    }
}
