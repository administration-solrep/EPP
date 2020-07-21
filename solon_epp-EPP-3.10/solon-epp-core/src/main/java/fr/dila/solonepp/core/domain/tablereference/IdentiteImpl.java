package fr.dila.solonepp.core.domain.tablereference;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

import com.google.common.base.Objects;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Identite;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier Identite.
 * 
 * @author sly
 */
public class IdentiteImpl implements Identite {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de IdentiteImpl.
     * 
     * @param document Modèle de document
     */
    public IdentiteImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY);
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
                id);
       
    }
    
    @Override
    public String getNom() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_NOM_PROPERTY);
    }

    @Override
    public void setNom(String nom) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_NOM_PROPERTY,
                nom);
        
    }
    
    
    @Override
    public String getPrenom() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_PRENOM_PROPERTY);
    }

    @Override
    public void setPrenom(String prenom) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_PRENOM_PROPERTY,
                prenom);
        
    }
    
    @Override
    public String getCivilite() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_CIVILITE_PROPERTY);
    }

    @Override
    public void setCivilite(String civilite) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_CIVILITE_PROPERTY,
                civilite);
        
    }
    
    @Override
    public Calendar getDateDebut() {
        return PropertyUtil.getCalendarProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DATE_DEBUT_PROPERTY);
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DATE_DEBUT_PROPERTY,
                dateDebut);
    }      
    
    @Override
    public Calendar getDateFin() {
        return PropertyUtil.getCalendarProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DATE_FIN_PROPERTY);
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DATE_FIN_PROPERTY,
                dateFin);
        
    }    
    
    
    @Override
    public Calendar getDateNaissance() {
        return PropertyUtil.getCalendarProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DATE_NAISSANCE_PROPERTY);
    }

    @Override
    public void setDateNaissance(Calendar dateNaissance) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DATE_NAISSANCE_PROPERTY,
                dateNaissance);
        
    }
    
    @Override
    public String getLieuNaissance() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_LIEU_NAISSANCE_PROPERTY);
    }

    @Override
    public void setLieuNaissance(String lieuNaissance) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_LIEU_NAISSANCE_PROPERTY,
                lieuNaissance);
    }
    
    @Override
    public String getDepartementNaissance() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DEPARTEMENT_NAISSANCE_PROPERTY);
    }

    @Override
    public void setDepartementNaissance(String departementNaissance) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_DEPARTEMENT_NAISSANCE_PROPERTY,
                departementNaissance);
    }
    
    @Override
    public String getPaysNaissance() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_PAYS_NAISSANCE_PROPERTY);
    }

    @Override
    public void setPaysNaissance(String paysNaissance) {
        PropertyUtil.setProperty(document, 
                SolonEppSchemaConstant.IDENTITE_SCHEMA, 
                SolonEppSchemaConstant.IDENTITE_PAYS_NAISSANCE_PROPERTY, 
                paysNaissance);
    }

    @Override
    public String getFullName() {
        return getCivilite() + " " + getPrenom() + " " + getNom();
    }

    @Override
    public String getActeur() {
        return PropertyUtil.getStringProperty(document, 
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.IDENTITE_ACTEUR_PROPERTY);
    }

    @Override
    public void setActeur(String acteur) {
        PropertyUtil.setProperty(document, 
                SolonEppSchemaConstant.IDENTITE_SCHEMA, 
                SolonEppSchemaConstant.IDENTITE_ACTEUR_PROPERTY, 
                acteur);
    }
    
    @Override
    public String getProprietaire() {
        return PropertyUtil.getStringProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY);
    }

    @Override
    public void setProprietaire(String proprietaire) {
        PropertyUtil.setProperty(document,
                SolonEppSchemaConstant.IDENTITE_SCHEMA,
                SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY,
                proprietaire);
    }    
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Identite)) {
            return false;
        }
        Identite you = (Identite) obj;
        
        return Objects.equal(getCivilite(), you.getCivilite())
                && DateUtil.isSameDay(getDateDebut(), you.getDateDebut())
                && DateUtil.isSameDay(getDateFin(), you.getDateFin())
                && DateUtil.isSameDay(getDateNaissance(), you.getDateNaissance())
                && Objects.equal(getDepartementNaissance(), you.getDepartementNaissance())
                && Objects.equal(getLieuNaissance(), you.getLieuNaissance())
                && Objects.equal(getNom(), you.getNom())
                && Objects.equal(getPaysNaissance(), you.getPaysNaissance())
                && Objects.equal(getPrenom(), you.getPrenom())
                && Objects.equal(getActeur(), you.getActeur());
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(getCivilite(),
                getDateDebut(),
                getDateFin(),
                getDateNaissance(),
                getDepartementNaissance(),
                getLieuNaissance(),
                getNom(),
                getPaysNaissance(),
                getPrenom(),
                getNom());
    }
}

