package fr.dila.solonepp.core.domain.tablereference;

import com.google.common.base.Objects;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Circonscription;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier Circonscription.
 *
 * @author sly
 */
public class CirconscriptionImpl implements Circonscription {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de CirconscriptionImpl.
     *
     * @param document Modèle de document
     */
    public CirconscriptionImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY
        );
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
            id
        );
    }

    @Override
    public String getProprietaire() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY
        );
    }

    @Override
    public void setProprietaire(String proprietaire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY,
            proprietaire
        );
    }

    @Override
    public String getNom() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.CIRCONSCRIPTION_NOM_PROPERTY
        );
    }

    @Override
    public void setNom(String nom) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.CIRCONSCRIPTION_NOM_PROPERTY,
            nom
        );
    }

    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.CIRCONSCRIPTION_DATE_DEBUT_PROPERTY
        );
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.CIRCONSCRIPTION_DATE_DEBUT_PROPERTY,
            dateDebut
        );
    }

    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.CIRCONSCRIPTION_DATE_FIN_PROPERTY
        );
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA,
            SolonEppSchemaConstant.CIRCONSCRIPTION_DATE_FIN_PROPERTY,
            dateFin
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Circonscription)) {
            return false;
        }
        Circonscription you = (Circonscription) obj;

        return (
            DateUtil.isSameDay(getDateDebut(), you.getDateDebut()) &&
            DateUtil.isSameDay(getDateFin(), you.getDateFin()) &&
            Objects.equal(getNom(), you.getNom())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDateDebut(), getDateFin(), getNom());
    }
}
