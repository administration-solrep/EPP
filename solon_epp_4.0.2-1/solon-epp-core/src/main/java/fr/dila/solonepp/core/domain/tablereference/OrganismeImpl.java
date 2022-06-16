package fr.dila.solonepp.core.domain.tablereference;

import com.google.common.base.Objects;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Organisme;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier organisme.
 *
 * @author sly
 */
public class OrganismeImpl implements Organisme {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de OrganismeImpl.
     *
     * @param document Modèle de document
     */
    public OrganismeImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY
        );
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
            id
        );
    }

    @Override
    public String getNom() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_NOM_PROPERTY
        );
    }

    @Override
    public void setNom(String nom) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_NOM_PROPERTY,
            nom
        );
    }

    @Override
    public String getTypeOrganisme() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_TYPE_ORGANISME_PROPERTY
        );
    }

    @Override
    public void setTypeOrganisme(String typeOrganisme) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_TYPE_ORGANISME_PROPERTY,
            typeOrganisme
        );
    }

    @Override
    public String getProprietaire() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY
        );
    }

    @Override
    public void setProprietaire(String proprietaire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY,
            proprietaire
        );
    }

    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_DATE_DEBUT_PROPERTY
        );
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_DATE_DEBUT_PROPERTY,
            dateDebut
        );
    }

    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_DATE_FIN_PROPERTY
        );
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_DATE_FIN_PROPERTY,
            dateFin
        );
    }

    @Override
    public String getIdCommun() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_ID_COMMUN_PROPERTY
        );
    }

    @Override
    public void setIdCommun(String idCommun) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_ID_COMMUN_PROPERTY,
            idCommun
        );
    }

    @Override
    public String getBaseLegale() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_BASE_LEGALE_PROPERTY
        );
    }

    @Override
    public void setBaseLegale(String BaseLegale) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ORGANISME_SCHEMA,
            SolonEppSchemaConstant.ORGANISME_BASE_LEGALE_PROPERTY,
            BaseLegale
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Organisme)) {
            return false;
        }
        Organisme you = (Organisme) obj;

        return (
            DateUtil.isSameDay(getDateDebut(), you.getDateDebut()) &&
            DateUtil.isSameDay(getDateFin(), you.getDateFin()) &&
            Objects.equal(getTypeOrganisme(), you.getTypeOrganisme()) &&
            Objects.equal(getNom(), you.getNom())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDateDebut(), getDateFin(), getTypeOrganisme(), getNom());
    }
}
