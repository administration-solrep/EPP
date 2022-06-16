package fr.dila.ss.core.client;

import fr.dila.ss.api.client.InjectionGvtDTO;
import java.io.Serializable;
import java.util.Date;

public class InjectionGvtDTOImpl implements InjectionGvtDTO, Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nor;
    private String ordreProtocolaireSolon;
    private String ordreProtocolaireReponses;
    private boolean aCreerSolon;
    private boolean aModifierSolon;
    private boolean aCreerReponses;
    private String libelleCourt;
    private String libelleLong;
    private String formule;
    private String civilite;
    private String prenom;
    private String nom;
    private String prenomNom;
    private Date dateDeDebut;
    private Date dateDeFin;
    private String norEPP;
    private boolean nouvelleEntiteEPP;
    private boolean isGvt;
    private boolean aModifierReponses;
    private String oldOrdreProtocolaireReponses;
    private String idOrganigramme;
    private String typeModification = "Nouveau";

    public InjectionGvtDTOImpl() {}

    /**
     * Constructeur par défaut
     *
     * @param nor
     * @param ordreProtocolaireSolon
     * @param ordreProtocolaireReponses
     * @param aCreerSolon
     * @param aModifierSolon
     * @param aCreerReponses
     * @param libelleCourt
     * @param libelleLong
     * @param formule
     * @param civilite
     * @param prenom
     * @param nom
     * @param prenomNom
     * @param dateDeDebut
     * @param dateDeFin
     * @param norEPP
     * @param nouvelleEntiteEPP
     * @param idSolonEpg
     * @param isGvt
     */
    public InjectionGvtDTOImpl(
        final String nor,
        final String ordreProtocolaireSolon,
        final String ordreProtocolaireReponses,
        final boolean aCreerSolon,
        final boolean aModifierSolon,
        final boolean aCreerReponses,
        final String libelleCourt,
        final String libelleLong,
        final String formule,
        final String civilite,
        final String prenom,
        final String nom,
        final String prenomNom,
        final Date dateDeDebut,
        final Date dateDeFin,
        final String norEPP,
        final boolean nouvelleEntiteEPP,
        final boolean isGvt
    ) {
        this.id = null;
        this.nor = nor;
        this.ordreProtocolaireReponses = ordreProtocolaireReponses;
        this.ordreProtocolaireSolon = ordreProtocolaireSolon;
        this.aCreerReponses = aCreerReponses;
        this.aCreerSolon = aCreerSolon;
        this.aModifierSolon = aModifierSolon;
        this.libelleCourt = libelleCourt;
        this.libelleLong = libelleLong;
        this.formule = formule;
        this.civilite = civilite;
        this.prenom = prenom;
        this.nom = nom;
        this.prenomNom = prenomNom;
        this.dateDeDebut = (dateDeDebut == null ? null : (Date) dateDeDebut.clone());
        this.dateDeFin = (dateDeFin == null ? null : (Date) dateDeFin.clone());
        this.norEPP = norEPP;
        this.nouvelleEntiteEPP = nouvelleEntiteEPP;
        this.isGvt = isGvt;
    }

    /**
     * Constructeur pour données de Réponses
     *
     * @param ordreProtocolaireReponses
     * @param aCreerReponses
     * @param libelleCourt
     * @param libelleLong
     * @param formule
     * @param civilite
     * @param prenom
     * @param nom
     * @param prenomNom
     * @param dateDeDebut
     * @param dateDeFin
     * @param isGvt
     */
    public InjectionGvtDTOImpl(
        final String ordreProtocolaireReponses,
        final boolean aCreerReponses,
        final String libelleCourt,
        final String libelleLong,
        final String formule,
        final String civilite,
        final String prenom,
        final String nom,
        final String prenomNom,
        final Date dateDeDebut,
        final Date dateDeFin,
        final boolean isGvt,
        final boolean aModifierReponses,
        String idOrganigramme
    ) {
        this.id = null;
        this.ordreProtocolaireReponses = ordreProtocolaireReponses;
        this.aCreerReponses = aCreerReponses;
        this.libelleCourt = libelleCourt;
        this.libelleLong = libelleLong;
        this.formule = formule;
        this.civilite = civilite;
        this.prenom = prenom;
        this.nom = nom;
        this.prenomNom = prenomNom;
        this.dateDeDebut = (dateDeDebut == null ? null : (Date) dateDeDebut.clone());
        this.dateDeFin = (dateDeFin == null ? null : (Date) dateDeFin.clone());
        this.isGvt = isGvt;
        this.aModifierReponses = aModifierReponses;
        this.idOrganigramme = idOrganigramme;
    }

    /**
     * Constructeur pour données de Solon
     *
     * @param nor
     * @param ordreProtocolaireSolon
     * @param aCreerSolon
     * @param aModifierSolon
     * @param libelleCourt
     * @param libelleLong
     * @param formule
     * @param civilite
     * @param prenom
     * @param nom
     * @param prenomNom
     * @param dateDeDebut
     * @param dateDeFin
     * @param norEPP
     * @param nouvelleEntiteEPP
     * @param idSolonEpg
     * @param isGvt
     */
    public InjectionGvtDTOImpl(
        final String nor,
        final String ordreProtocolaireSolon,
        final boolean aCreerSolon,
        final boolean aModifierSolon,
        final String libelleCourt,
        final String libelleLong,
        final String formule,
        final String civilite,
        final String prenom,
        final String nom,
        final String prenomNom,
        final Date dateDeDebut,
        final Date dateDeFin,
        final String norEPP,
        final boolean nouvelleEntiteEPP,
        final boolean isGvt
    ) {
        this.id = null;
        this.nor = nor;
        this.ordreProtocolaireSolon = ordreProtocolaireSolon;
        this.aCreerSolon = aCreerSolon;
        this.aModifierSolon = aModifierSolon;
        this.libelleCourt = libelleCourt;
        this.libelleLong = libelleLong;
        this.formule = formule;
        this.civilite = civilite;
        this.prenom = prenom;
        this.nom = nom;
        this.prenomNom = prenomNom;
        this.dateDeDebut = (dateDeDebut == null ? null : (Date) dateDeDebut.clone());
        this.dateDeFin = (dateDeFin == null ? null : (Date) dateDeFin.clone());
        this.norEPP = norEPP;
        this.nouvelleEntiteEPP = nouvelleEntiteEPP;
        this.isGvt = isGvt;
    }

    @Override
    public String getNor() {
        return nor;
    }

    @Override
    public void setNor(final String nor) {
        this.nor = nor;
    }

    @Override
    public String getOrdreProtocolaireSolon() {
        return ordreProtocolaireSolon;
    }

    @Override
    public void setOrdreProtocolaireSolon(final String ordreProtocolaireSolon) {
        this.ordreProtocolaireSolon = ordreProtocolaireSolon;
    }

    @Override
    public String getOrdreProtocolaireReponses() {
        return ordreProtocolaireReponses;
    }

    @Override
    public void setOrdreProtocolaireReponses(final String ordreProtocolaireReponses) {
        this.ordreProtocolaireReponses = ordreProtocolaireReponses;
    }

    @Override
    public String getLibelleCourt() {
        return libelleCourt;
    }

    @Override
    public void setLibelleCourt(final String libelleCourt) {
        this.libelleCourt = libelleCourt;
    }

    @Override
    public String getLibelleLong() {
        return libelleLong;
    }

    @Override
    public void setLibelleLong(final String libelleLong) {
        this.libelleLong = libelleLong;
    }

    @Override
    public String getFormule() {
        return formule;
    }

    @Override
    public void setFormule(final String formule) {
        this.formule = formule;
    }

    @Override
    public String getCivilite() {
        return civilite;
    }

    @Override
    public void setCivilite(final String civilite) {
        this.civilite = civilite;
    }

    @Override
    public String getPrenom() {
        return prenom;
    }

    @Override
    public void setPrenom(final String prenom) {
        this.prenom = prenom;
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public void setNom(final String nom) {
        this.nom = nom;
    }

    @Override
    public String getPrenomNom() {
        return prenomNom;
    }

    @Override
    public void setPrenomNom(final String prenomNom) {
        this.prenomNom = prenomNom;
    }

    @Override
    public Date getDateDeDebut() {
        return dateDeDebut;
    }

    @Override
    public void setDateDeDebut(final Date dateDeDebut) {
        this.dateDeDebut = dateDeDebut;
    }

    @Override
    public Date getDateDeFin() {
        return dateDeFin;
    }

    @Override
    public void setDateDeFin(final Date dateDeFin) {
        this.dateDeFin = dateDeFin;
    }

    @Override
    public String getNorEPP() {
        return norEPP;
    }

    @Override
    public void setNorEPP(final String norEPP) {
        this.norEPP = norEPP;
    }

    @Override
    public boolean isNouvelleEntiteEPP() {
        return nouvelleEntiteEPP;
    }

    @Override
    public void setNouvelleEntiteEPP(final boolean nouvelleEntiteEPP) {
        this.nouvelleEntiteEPP = nouvelleEntiteEPP;
    }

    @Override
    public boolean isaCreerSolon() {
        return aCreerSolon;
    }

    @Override
    public void setaCreerSolon(boolean aCreerSolon) {
        this.aCreerSolon = aCreerSolon;
    }

    @Override
    public boolean isaModifierSolon() {
        return aModifierSolon;
    }

    @Override
    public void setaModifierSolon(boolean aModifierSolon) {
        this.aModifierSolon = aModifierSolon;
    }

    @Override
    public boolean isaCreerReponses() {
        return aCreerReponses;
    }

    @Override
    public void setaCreerReponses(boolean aCreerReponses) {
        this.aCreerReponses = aCreerReponses;
    }

    @Override
    public boolean isGvt() {
        return isGvt;
    }

    @Override
    public void setGvt(boolean isGvt) {
        this.isGvt = isGvt;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isaModifierReponses() {
        return aModifierReponses;
    }

    public boolean getAModifierReponses() {
        return aModifierReponses;
    }

    @Override
    public void setaModifierReponses(boolean aModifierReponses) {
        this.aModifierReponses = aModifierReponses;
    }

    @Override
    public String getOldOrdreProtocolaireReponses() {
        return oldOrdreProtocolaireReponses;
    }

    @Override
    public void setOldOrdreProtocolaireReponses(String ordreProtocolaireReponses) {
        oldOrdreProtocolaireReponses = ordreProtocolaireReponses;
    }

    @Override
    public String getIdOrganigramme() {
        return idOrganigramme;
    }

    @Override
    public void setIdOrganigramme(String idOrganigramme) {
        this.idOrganigramme = idOrganigramme;
    }

    @Override
    public String getTypeModification() {
        return typeModification;
    }

    @Override
    public void setTypeModification(String typeModification) {
        this.typeModification = typeModification;
    }
}
