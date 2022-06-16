package fr.dila.solonepp.core.assembler;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Assembleur des données des tables de références.
 *
 * @author sly
 */
public class TableReferenceAssembler {

    // *************************************************************
    // Acteur
    // *************************************************************
    /**
     * Assemble les propriétés d'un Acteur pour modifier un Acteur existant.
     *
     * @param newActeurDoc nouvel objet Acteur
     * @param currentActeurDoc objet Acteur à modifier
     */
    public static void assembleActeurForUpdate(DocumentModel newActeurDoc, DocumentModel currentActeurDoc) {
        //        fr.dila.solonepp.api.domain.tablereference.Acteur newActeur = newActeurDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Acteur.class);
        //        fr.dila.solonepp.api.domain.tablereference.Acteur currentActeur = currentActeurDoc.getAdapter(fr.dila.solonepp.api.domain.tablereference.Acteur.class);
    }

    // *************************************************************
    // Circonscription
    // *************************************************************
    /**
     * Assemble les propriétés d'une Circonscription pour modifier une Circonscription existante.
     *
     * @param newCirconscriptionDoc nouvel objet Circonscription
     * @param currentCirconscriptionDoc objet Circonscription à modifier
     */
    public static void assembleCirconscriptionForUpdate(
        DocumentModel newCirconscriptionDoc,
        DocumentModel currentCirconscriptionDoc
    ) {
        fr.dila.solonepp.api.domain.tablereference.Circonscription newCirconscription = newCirconscriptionDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Circonscription.class
        );
        fr.dila.solonepp.api.domain.tablereference.Circonscription currentCirconscription = currentCirconscriptionDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Circonscription.class
        );

        currentCirconscription.setDateDebut(newCirconscription.getDateDebut());
        currentCirconscription.setDateFin(newCirconscription.getDateFin());
        currentCirconscription.setNom(newCirconscription.getNom());
    }

    // *************************************************************
    // Gouvernement
    // *************************************************************
    /**
     * Assemble les propriétés d'un Gouvernement pour modifier un Gouvernement existant.
     *
     * @param newGouvernementDoc nouvel objet Gouvernement
     * @param currentGouvernementDoc objet Gouvernement à modifier
     */
    public static void assembleGouvernementForUpdate(
        DocumentModel newGouvernementDoc,
        DocumentModel currentGouvernementDoc
    ) {
        fr.dila.solonepp.api.domain.tablereference.Gouvernement newGouvernement = newGouvernementDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Gouvernement.class
        );
        fr.dila.solonepp.api.domain.tablereference.Gouvernement currentGouvernement = currentGouvernementDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Gouvernement.class
        );

        currentGouvernement.setAppellation(newGouvernement.getAppellation());
        currentGouvernement.setDateDebut(newGouvernement.getDateDebut());
        currentGouvernement.setDateFin(newGouvernement.getDateFin());
    }

    // *************************************************************
    // Identite
    // *************************************************************
    /**
     * Assemble les propriétés d'une Identite pour modifier une Identite existante.
     *
     * @param newIdentiteDoc nouvel objet Identite
     * @param currentIdentiteDoc objet Identite à modifier
     */
    public static void assembleIdentiteForUpdate(DocumentModel newIdentiteDoc, DocumentModel currentIdentiteDoc) {
        fr.dila.solonepp.api.domain.tablereference.Identite newIdentite = newIdentiteDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Identite.class
        );
        fr.dila.solonepp.api.domain.tablereference.Identite currentIdentite = currentIdentiteDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Identite.class
        );

        currentIdentite.setCivilite(newIdentite.getCivilite());
        currentIdentite.setDateDebut(newIdentite.getDateDebut());
        currentIdentite.setDateFin(newIdentite.getDateFin());
        currentIdentite.setDateNaissance(newIdentite.getDateNaissance());
        currentIdentite.setDepartementNaissance(newIdentite.getDepartementNaissance());
        currentIdentite.setLieuNaissance(newIdentite.getLieuNaissance());
        currentIdentite.setNom(newIdentite.getNom());
        currentIdentite.setPaysNaissance(newIdentite.getPaysNaissance());
        currentIdentite.setPrenom(newIdentite.getPrenom());
        currentIdentite.setActeur(newIdentite.getActeur());
    }

    // *************************************************************
    // Mandat
    // *************************************************************
    /**
     * Assemble les propriétés d'un Mandat pour modifier un Mandat existant.
     *
     * @param newMandatDoc nouvel objet Mandat
     * @param currentMandatDoc objet Mandat à modifier
     */
    public static void assembleMandatForUpdate(DocumentModel newMandatDoc, DocumentModel currentMandatDoc) {
        fr.dila.solonepp.api.domain.tablereference.Mandat newMandat = newMandatDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Mandat.class
        );
        fr.dila.solonepp.api.domain.tablereference.Mandat currentMandat = currentMandatDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Mandat.class
        );

        currentMandat.setAppellation(newMandat.getAppellation());
        currentMandat.setCirconscription(newMandat.getCirconscription());
        currentMandat.setDateDebut(newMandat.getDateDebut());
        currentMandat.setDateFin(newMandat.getDateFin());
        currentMandat.setIdentite(newMandat.getIdentite());
        currentMandat.setMinistere(newMandat.getMinistere());
        currentMandat.setOrdreProtocolaire(newMandat.getOrdreProtocolaire());
        currentMandat.setTitre(newMandat.getTitre());
        currentMandat.setTypeMandat(newMandat.getTypeMandat());
        currentMandat.setNor(newMandat.getNor());
    }

    // *************************************************************
    // MembreGroupe
    // *************************************************************
    /**
     * Assemble les propriétés d'un MembreGroupe pour modifier un MembreGroupe existant.
     *
     * @param newMembreGroupeDoc nouvel objet MembreGroupe
     * @param currentMembreGroupeDoc objet MembreGroupe à modifier
     */
    public static void assembleMembreGroupeForUpdate(
        DocumentModel newMembreGroupeDoc,
        DocumentModel currentMembreGroupeDoc
    ) {
        fr.dila.solonepp.api.domain.tablereference.MembreGroupe newMembreGroupe = newMembreGroupeDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.MembreGroupe.class
        );
        fr.dila.solonepp.api.domain.tablereference.MembreGroupe currentMembreGroupe = currentMembreGroupeDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.MembreGroupe.class
        );

        currentMembreGroupe.setDateDebut(newMembreGroupe.getDateDebut());
        currentMembreGroupe.setDateFin(newMembreGroupe.getDateFin());
        currentMembreGroupe.setMandat(newMembreGroupe.getMandat());
        currentMembreGroupe.setOrganisme(newMembreGroupe.getOrganisme());
    }

    // *************************************************************
    // Ministere
    // *************************************************************
    /**
     * Assemble les propriétés d'un Ministere pour modifier un Ministere existant.
     *
     * @param newMinistereDoc nouvel objet Ministere
     * @param currentMinistereDoc objet Ministere à modifier
     */
    public static void assembleMinistereForUpdate(DocumentModel newMinistereDoc, DocumentModel currentMinistereDoc) {
        fr.dila.solonepp.api.domain.tablereference.Ministere newMinistere = newMinistereDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Ministere.class
        );
        fr.dila.solonepp.api.domain.tablereference.Ministere currentMinistere = currentMinistereDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Ministere.class
        );

        currentMinistere.setAppellation(newMinistere.getAppellation());
        currentMinistere.setDateDebut(newMinistere.getDateDebut());
        currentMinistere.setDateFin(newMinistere.getDateFin());
        currentMinistere.setEdition(newMinistere.getEdition());
        currentMinistere.setGouvernement(newMinistere.getGouvernement());
        currentMinistere.setLibelleMinistre(newMinistere.getLibelleMinistre());
        currentMinistere.setNom(newMinistere.getNom());
    }

    // *************************************************************
    // Organisme
    // *************************************************************
    /**
     * Assemble les propriétés d'un Organisme pour modifier un Organisme existant.
     *
     * @param newOrganismeDoc nouvel objet Organisme
     * @param currentOrganismeDoc objet Organisme à modifier
     */
    public static void assembleOrganismeForUpdate(DocumentModel newOrganismeDoc, DocumentModel currentOrganismeDoc) {
        fr.dila.solonepp.api.domain.tablereference.Organisme newOrganisme = newOrganismeDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Organisme.class
        );
        fr.dila.solonepp.api.domain.tablereference.Organisme currentOrganisme = currentOrganismeDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Organisme.class
        );

        currentOrganisme.setDateDebut(newOrganisme.getDateDebut());
        currentOrganisme.setDateFin(newOrganisme.getDateFin());
        currentOrganisme.setNom(newOrganisme.getNom());
        currentOrganisme.setTypeOrganisme(newOrganisme.getTypeOrganisme());
        currentOrganisme.setIdCommun(newOrganisme.getIdCommun());
        currentOrganisme.setBaseLegale(newOrganisme.getBaseLegale());
    }

    // *************************************************************
    // Periode
    // *************************************************************
    /**
     * Assemble les propriétés d'une Periode pour modifier une Periode existante.
     *
     * @param newPeriodeDoc nouvel objet Periode
     * @param currentPeriodeDoc objet Periode à modifier
     */
    public static void assemblePeriodeForUpdate(DocumentModel newPeriodeDoc, DocumentModel currentPeriodeDoc) {
        fr.dila.solonepp.api.domain.tablereference.Periode newPeriode = newPeriodeDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Periode.class
        );
        fr.dila.solonepp.api.domain.tablereference.Periode currentPeriode = currentPeriodeDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Periode.class
        );

        currentPeriode.setDateDebut(newPeriode.getDateDebut());
        currentPeriode.setDateFin(newPeriode.getDateFin());
        currentPeriode.setNumero(newPeriode.getNumero());
        currentPeriode.setTypePeriode(newPeriode.getTypePeriode());
    }
}
