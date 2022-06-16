package fr.dila.solonepp.core.domain.tablereference;

import com.google.common.base.Objects;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Periode;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier mandat.
 *
 * @author sly
 */
public class PeriodeImpl implements Periode {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de PeriodeImpl.
     *
     * @param document Modèle de document
     */
    public PeriodeImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY
        );
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
            id
        );
    }

    @Override
    public String getTypePeriode() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_TYPE_PERIODE_PROPERTY
        );
    }

    @Override
    public void setTypePeriode(String typePeriode) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_TYPE_PERIODE_PROPERTY,
            typePeriode
        );
    }

    @Override
    public String getProprietaire() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY
        );
    }

    @Override
    public void setProprietaire(String proprietaire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY,
            proprietaire
        );
    }

    @Override
    public String getNumero() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_NUMERO_PROPERTY
        );
    }

    @Override
    public void setNumero(String numero) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_NUMERO_PROPERTY,
            numero
        );
    }

    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_DATE_DEBUT_PROPERTY
        );
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_DATE_DEBUT_PROPERTY,
            dateDebut
        );
    }

    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_DATE_FIN_PROPERTY
        );
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.PERIODE_SCHEMA,
            SolonEppSchemaConstant.PERIODE_DATE_FIN_PROPERTY,
            dateFin
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Periode)) {
            return false;
        }
        Periode you = (Periode) obj;

        return (
            DateUtil.isSameDay(getDateDebut(), you.getDateDebut()) &&
            DateUtil.isSameDay(getDateFin(), you.getDateFin()) &&
            Objects.equal(getNumero(), you.getNumero()) &&
            Objects.equal(getTypePeriode(), you.getTypePeriode())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDateDebut(), getDateFin(), getNumero(), getTypePeriode());
    }
}
