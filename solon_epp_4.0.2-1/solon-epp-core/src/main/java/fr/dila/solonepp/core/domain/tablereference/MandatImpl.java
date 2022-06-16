package fr.dila.solonepp.core.domain.tablereference;

import com.google.common.base.Objects;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Mandat;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier mandat.
 *
 * @author sly
 */
public class MandatImpl implements Mandat {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de MandatImpl.
     *
     * @param document Modèle de document
     */
    public MandatImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY
        );
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
            id
        );
    }

    @Override
    public String getProprietaire() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY
        );
    }

    @Override
    public void setProprietaire(String proprietaire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY,
            proprietaire
        );
    }

    @Override
    public String getTypeMandat() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_TYPE_MANDAT_PROPERTY
        );
    }

    @Override
    public void setTypeMandat(String typeMandat) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_TYPE_MANDAT_PROPERTY,
            typeMandat
        );
    }

    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_DATE_DEBUT_PROPERTY
        );
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_DATE_DEBUT_PROPERTY,
            dateDebut
        );
    }

    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_DATE_FIN_PROPERTY
        );
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_DATE_FIN_PROPERTY,
            dateFin
        );
    }

    @Override
    public Long getOrdreProtocolaire() {
        return PropertyUtil.getLongProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_ORDRE_PROTOCOLAIRE_PROPERTY
        );
    }

    @Override
    public void setOrdreProtocolaire(Long ordreProtocolaire) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_ORDRE_PROTOCOLAIRE_PROPERTY,
            ordreProtocolaire
        );
    }

    @Override
    public String getTitre() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_TITRE_PROPERTY
        );
    }

    @Override
    public void setTitre(String titre) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_TITRE_PROPERTY,
            titre
        );
    }

    @Override
    public String getIdentite() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_IDENTITE_PROPERTY
        );
    }

    @Override
    public void setIdentite(String identite) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_IDENTITE_PROPERTY,
            identite
        );
    }

    @Override
    public String getMinistere() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_MINISTERE_PROPERTY
        );
    }

    @Override
    public void setMinistere(String ministere) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_MINISTERE_PROPERTY,
            ministere
        );
    }

    @Override
    public String getCirconscription() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_CIRCONSCRIPTION_PROPERTY
        );
    }

    @Override
    public void setCirconscription(String circonscription) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_CIRCONSCRIPTION_PROPERTY,
            circonscription
        );
    }

    @Override
    public String getAppellation() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_APPELLATION_PROPERTY
        );
    }

    @Override
    public void setAppellation(String appellation) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_APPELLATION_PROPERTY,
            appellation
        );
    }

    @Override
    public String getNor() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_NOR_PROPERTY
        );
    }

    @Override
    public void setNor(String nor) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.MANDAT_SCHEMA,
            SolonEppSchemaConstant.MANDAT_NOR_PROPERTY,
            nor
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mandat)) {
            return false;
        }
        Mandat you = (Mandat) obj;

        return (
            DateUtil.isSameDay(getDateDebut(), you.getDateDebut()) &&
            DateUtil.isSameDay(getDateFin(), you.getDateFin()) &&
            Objects.equal(getAppellation(), you.getAppellation()) &&
            Objects.equal(getCirconscription(), you.getCirconscription()) &&
            Objects.equal(getIdentite(), you.getIdentite()) &&
            Objects.equal(getMinistere(), you.getMinistere()) &&
            Objects.equal(getOrdreProtocolaire(), you.getOrdreProtocolaire()) &&
            Objects.equal(getTitre(), you.getTitre()) &&
            Objects.equal(getTypeMandat(), you.getTypeMandat())
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
            getDateDebut(),
            getDateFin(),
            getAppellation(),
            getCirconscription(),
            getIdentite(),
            getMinistere(),
            getOrdreProtocolaire(),
            getTitre(),
            getTypeMandat()
        );
    }
}
