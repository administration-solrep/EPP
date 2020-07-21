package fr.dila.solonepp.core.domain.tablereference;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

import com.google.common.base.Objects;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Gouvernement;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier gouvernement.
 * 
 * @author sly
 */
public class GouvernementImpl implements Gouvernement {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;
    protected Boolean ministereAttache;

    /**
     * Constructeur de GouvernementImpl.
     * 
     * @param document Modèle de document
     */
    public GouvernementImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY);
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
                id);
        
    }
    
    @Override
    public String getAppellation() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.GOUVERNEMENT_APPELLATION_PROPERTY);
    }

    @Override
    public void setAppellation(String appellation) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.GOUVERNEMENT_APPELLATION_PROPERTY,
                appellation);
        
    }
    
    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.GOUVERNEMENT_DATE_DEBUT_PROPERTY);
    }
    
    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.GOUVERNEMENT_DATE_DEBUT_PROPERTY,
                dateDebut);
    }
    
    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.GOUVERNEMENT_DATE_FIN_PROPERTY);
    }
    
    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA,
                SolonEppSchemaConstant.GOUVERNEMENT_DATE_FIN_PROPERTY,
                dateFin);
    }
        
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Gouvernement)) {
            return false;
        }
        Gouvernement you = (Gouvernement) obj;
        
        return DateUtil.isSameDay(getDateDebut(), you.getDateDebut())
                && DateUtil.isSameDay(getDateFin(), you.getDateFin())
                && Objects.equal(getAppellation(), you.getAppellation());
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(getAppellation(), getDateDebut(), getDateFin());
    }
    
    /**
     * Gets the value of the ministereAttache property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Override
    public Boolean isMinistereAttache() {
        return ministereAttache;
    }

    /**
     * Sets the value of the MinistereAttache property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Override
    public void setMinistereAttache(Boolean ministereAttache) {
        this.ministereAttache = ministereAttache;
    }

}
