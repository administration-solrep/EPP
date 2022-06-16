package fr.dila.ss.core.actualite;

import fr.dila.ss.api.actualite.ActualiteRequete;
import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ActualiteRequeteImpl implements ActualiteRequete {
    private DocumentModel actualiteDoc;

    public ActualiteRequeteImpl(DocumentModel doc) {
        this.actualiteDoc = doc;
    }

    @Override
    public LocalDate getDateEmissionDebut() {
        Calendar date = PropertyUtil.getCalendarProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_DEBUT
        );
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return null;
    }

    @Override
    public void setDateEmissionDebut(LocalDate dateEmissionDebut) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_DEBUT,
            DateUtil.localDateToGregorianCalendar(dateEmissionDebut)
        );
    }

    @Override
    public LocalDate getDateEmissionFin() {
        Calendar date = PropertyUtil.getCalendarProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_FIN
        );
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return null;
    }

    @Override
    public void setDateEmissionFin(LocalDate dateEmissionFin) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_FIN,
            DateUtil.localDateToGregorianCalendar(dateEmissionFin)
        );
    }

    @Override
    public LocalDate getDateValiditeDebut() {
        Calendar date = PropertyUtil.getCalendarProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_DEBUT
        );
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return null;
    }

    @Override
    public void setDateValiditeDebut(LocalDate dateValiditeDebut) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_DEBUT,
            DateUtil.localDateToGregorianCalendar(dateValiditeDebut)
        );
    }

    @Override
    public LocalDate getDateValiditeFin() {
        Calendar date = PropertyUtil.getCalendarProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_FIN
        );
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return null;
    }

    @Override
    public void setDateValiditeFin(LocalDate dateValiditeFin) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_FIN,
            DateUtil.localDateToGregorianCalendar(dateValiditeFin)
        );
    }

    @Override
    public String getObjet() {
        return PropertyUtil.getStringProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_OBJET
        );
    }

    @Override
    public void setObjet(String objet) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_OBJET,
            objet
        );
    }

    @Override
    public boolean getIsInHistorique() {
        return PropertyUtil.getBooleanProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DANS_HISTORIQUE
        );
    }

    @Override
    public void setDansHistorique(boolean isInhistorique) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DANS_HISTORIQUE,
            isInhistorique
        );
    }

    @Override
    public boolean getHasPj() {
        return PropertyUtil.getBooleanProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_HASPJ
        );
    }

    @Override
    public void setHasPj(boolean hasPj) {
        actualiteDoc.setProperty(
            ActualiteConstant.ACTUALITE_REQUETE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_HASPJ,
            hasPj
        );
    }
}
