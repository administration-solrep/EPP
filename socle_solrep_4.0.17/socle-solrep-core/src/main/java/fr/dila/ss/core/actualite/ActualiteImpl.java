package fr.dila.ss.core.actualite;

import fr.dila.ss.api.actualite.Actualite;
import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ActualiteImpl implements Actualite {
    private DocumentModel actualiteDoc;

    public ActualiteImpl(DocumentModel doc) {
        this.actualiteDoc = doc;
    }

    @Override
    public LocalDate getDateEmission() {
        Calendar date = PropertyUtil.getCalendarProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DATE_EMISSION
        );
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return null;
    }

    @Override
    public void setDateEmission(LocalDate dateEmission) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DATE_EMISSION,
            DateUtil.localDateToGregorianCalendar(dateEmission)
        );
    }

    @Override
    public LocalDate getDateValidite() {
        Calendar date = PropertyUtil.getCalendarProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DATE_VALIDITE
        );
        if (date != null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return null;
    }

    @Override
    public void setDateValidite(LocalDate dateValidite) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DATE_VALIDITE,
            DateUtil.localDateToGregorianCalendar(dateValidite)
        );
    }

    @Override
    public String getObjet() {
        return PropertyUtil.getStringProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_OBJET
        );
    }

    @Override
    public void setObjet(String objet) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_OBJET,
            objet
        );
    }

    @Override
    public String getContenu() {
        return PropertyUtil.getStringProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_CONTENU
        );
    }

    @Override
    public void setContenu(String contenu) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_CONTENU,
            contenu
        );
    }

    @Override
    public boolean getHasPj() {
        return PropertyUtil.getBooleanProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_HASPJ
        );
    }

    @Override
    public void setHasPj(boolean hasPj) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_HASPJ,
            hasPj
        );
    }

    @Override
    public boolean getIsInHistorique() {
        return PropertyUtil.getBooleanProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DANS_HISTORIQUE
        );
    }

    @Override
    public void setIsInHistorique(boolean isInhistorique) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_DANS_HISTORIQUE,
            isInhistorique
        );
    }

    @Override
    public List<Map<String, Serializable>> getPiecesJointes() {
        return PropertyUtil.getMapStringSerializableListProperty(
            actualiteDoc,
            STConstant.FILES_SCHEMA,
            STConstant.FILES_PROPERTY_FILES
        );
    }

    @Override
    public void setPiecesJointes(List<Map<String, Serializable>> fichiers) {
        actualiteDoc.setProperty(STConstant.FILES_SCHEMA, STConstant.FILES_PROPERTY_FILES, fichiers);
    }

    @Override
    public List<String> getLecteurs() {
        return PropertyUtil.getStringListProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_LECTEURS
        );
    }

    @Override
    public void setLecteurs(List<String> lecteurs) {
        PropertyUtil.setProperty(
            actualiteDoc,
            ActualiteConstant.ACTUALITE_SCHEMA,
            ActualiteConstant.ACTUALITE_PROPERTY_LECTEURS,
            lecteurs
        );
    }
}
