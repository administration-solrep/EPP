package fr.dila.solonepp.core.validator;

import fr.dila.solonepp.api.domain.tablereference.Acteur;
import fr.dila.solonepp.api.domain.tablereference.Circonscription;
import fr.dila.solonepp.api.domain.tablereference.Gouvernement;
import fr.dila.solonepp.api.domain.tablereference.Identite;
import fr.dila.solonepp.api.domain.tablereference.Mandat;
import fr.dila.solonepp.api.domain.tablereference.MembreGroupe;
import fr.dila.solonepp.api.domain.tablereference.Ministere;
import fr.dila.solonepp.api.domain.tablereference.Organisme;
import fr.dila.solonepp.api.domain.tablereference.Periode;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Validateur d'objet de Table de Référence
 *
 * @author sly
 */
public final class TableReferenceValidator {

    private TableReferenceValidator() {
        // default empty constructor
    }

    /**
     * valide les données contenues dans Acteur en mise à jour
     *
     * @param acteur acteur à Valider
     */
    public static void validateActeurForUpdate(final Acteur acteur) {
        // : Identifiant
        final String identifiant = acteur.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Circonscription est obligatoire.");
        }
    }

    /**
     * valide les données contenues dans Circonscription en mise à jour
     *
     * @param circonscription circonscription à Valider
     */
    public static void validateCirconscriptionForUpdate(final Circonscription circonscription) {
        // : Identifiant
        final String identifiant = circonscription.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Circonscription est obligatoire.");
        }

        validateCirconscriptionForCreation(circonscription);
    }

    /**
     * valide les données contenues dans Circonscription en creation
     *
     * @param circonscription circonscription à Valider
     */
    public static void validateCirconscriptionForCreation(final Circonscription circonscription) {
        // : Nom
        final String nom = circonscription.getNom();
        if (StringUtils.isEmpty(nom)) {
            throw new NuxeoException("L'attribut Nom de l'objet Circonscription est obligatoire.");
        }
    }

    /**
     * valide les données contenues dans Gouvernement en creation
     *
     * @param gouvernement gouvernement à Valider
     */
    public static void validateGouvernementForCreation(final Gouvernement gouvernement) {
        // : Appellation
        final String appellation = gouvernement.getAppellation();
        if (StringUtils.isEmpty(appellation)) {
            throw new NuxeoException("L'attribut Appellation de l'objet Gouvernement est obligatoire.");
        }

        // : DateDebut
        final Calendar dateDebut = gouvernement.getDateDebut();
        if (dateDebut == null) {
            throw new NuxeoException("L'attribut DateDebut de l'objet Gouvernement est obligatoire.");
        }

        // : DateFin
        final Calendar dateFin = gouvernement.getDateFin();
        if (dateFin != null) {
            if (dateFin.before(dateDebut)) {
                throw new NuxeoException("L'attribut DateFin de l'objet Gouvernement doit être ultérieur à DateDebut.");
            }
        }
    }

    /**
     * valide les données contenues dans Gouvernement en mise à jour
     *
     * @param gouvernement gouvernement à Valider
     */
    public static void validateGouvernementForUpdate(final Gouvernement gouvernement) {
        // : Identifiant
        final String identifiant = gouvernement.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Gouvernement est obligatoire.");
        }

        validateGouvernementForCreation(gouvernement);
    }

    /**
     * valide les données contenues dans Identite en creation
     *
     * @param identite identite à Valider
     */
    public static void validateIdentiteForCreation(final CoreSession session, final Identite identite) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

        // : Civilite
        final String civilite = identite.getCivilite();
        if (StringUtils.isEmpty(civilite)) {
            throw new NuxeoException("L'attribut Civilite de l'objet Identite est obligatoire.");
        }

        // : Nom
        final String nom = identite.getNom();
        if (StringUtils.isEmpty(nom)) {
            throw new NuxeoException("L'attribut Nom de l'objet Identite est obligatoire.");
        }

        // : Prenom
        final String prenom = identite.getPrenom();
        if (StringUtils.isEmpty(prenom)) {
            throw new NuxeoException("L'attribut Prenom de l'objet Identite est obligatoire.");
        }

        // : DateDebut
        final Calendar dateDebut = identite.getDateDebut();
        if (dateDebut == null) {
            throw new NuxeoException("L'attribut DateDebut de l'objet Identite est obligatoire.");
        }

        // : DateFin
        final Calendar dateFin = identite.getDateFin();
        if (dateFin != null) {
            if (dateFin.before(dateDebut)) {
                throw new NuxeoException("L'attribut DateFin de l'objet Identite doit être ultérieur à DateDebut.");
            }
        }

        // : Acteur
        final String acteur = identite.getActeur();
        if (!StringUtils.isEmpty(acteur)) {
            final boolean exist = tableReferenceService.hasActeur(session, acteur);
            if (!exist) {
                throw new NuxeoException("L'attribut Acteur identifié par " + acteur + " n'existe pas.");
            }
        }
        // : DateNaissance
        // : LieuNaissance
        // : DeptNaissance
        // : PaysNaissance

    }

    /**
     * valide les données contenues dans Identite en mise à jour
     *
     * @param identite identite à Valider
     */
    public static void validateIdentiteForUpdate(final CoreSession session, final Identite identite) {
        // : Identifiant
        final String identifiant = identite.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Identite est obligatoire.");
        }

        validateIdentiteForCreation(session, identite);
    }

    /**
     * valide les données contenues dans Mandat en creation
     *
     * @param mandat mandat à Valider
     */
    public static void validateMandatForCreation(final CoreSession session, final Mandat mandat) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

        // : TypeMandat
        final String typeMandat = mandat.getTypeMandat();
        if (StringUtils.isEmpty(typeMandat)) {
            throw new NuxeoException("L'attribut TypeMandat de l'objet Mandat est obligatoire.");
        }

        // : Proprietaire
        final String proprietaire = mandat.getProprietaire();
        if (proprietaire == null) {
            throw new NuxeoException("L'attribut Proprietaire de l'objet Mandat est obligatoire.");
        }

        // : Identite
        final String identite = mandat.getIdentite();
        if (StringUtils.isEmpty(identite)) {
            throw new NuxeoException("L'attribut Identite de l'objet Mandat est obligatoire.");
        }

        boolean exist = tableReferenceService.hasIdentite(session, identite);
        if (!exist) {
            throw new NuxeoException("L'attribut Identite identifié par " + identite + " n'existe pas.");
        }

        // : DateDebut
        final Calendar dateDebut = mandat.getDateDebut();
        if (dateDebut == null) {
            throw new NuxeoException("L'attribut DateDebut de l'objet Mandat est obligatoire.");
        }

        // : DateFin
        final Calendar dateFin = mandat.getDateFin();
        if (dateFin != null) {
            if (dateFin.before(dateDebut)) {
                throw new NuxeoException("L'attribut DateFin de l'objet Identite doit être ultérieur à DateDebut.");
            }
        }

        // : ordreProtocolaire
        // : titre

        // : Ministere
        final String ministere = mandat.getMinistere();
        if (!StringUtils.isEmpty(ministere)) {
            exist = tableReferenceService.hasMinistere(session, ministere);
            if (!exist) {
                throw new NuxeoException("L'attribut Ministere identifié par " + ministere + " n'existe pas.");
            }
        }

        // : Circonscription
        final String circonscription = mandat.getCirconscription();
        if (!StringUtils.isEmpty(circonscription)) {
            exist = tableReferenceService.hasCirconscription(session, circonscription);
            if (!exist) {
                throw new NuxeoException(
                    "L'attribut Circonscription identifié par " + circonscription + " n'existe pas."
                );
            }
        }
    }

    /**
     * valide les données contenues dans Circonscription en mise à jour
     *
     * @param session la session de l'utilisateur
     * @param circonscription circonscription à Valider
     */
    public static void validateMandatForUpdate(final CoreSession session, final Mandat mandat) {
        // : Identifiant
        final String identifiant = mandat.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Mandat est obligatoire.");
        }

        validateMandatForCreation(session, mandat);
    }

    /**
     * valide les données contenues dans MembreGroupe en creation
     *
     * @param membreGroupe membreGroupe à Valider
     */
    public static void validateMembreGroupeForCreation(final CoreSession session, final MembreGroupe membreGroupe) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

        // : Organisme
        final String organisme = membreGroupe.getOrganisme();
        if (StringUtils.isEmpty(organisme)) {
            throw new NuxeoException("L'attribut Organisme de l'objet MembreGroupe est obligatoire.");
        }
        final boolean exist = tableReferenceService.hasOrganisme(session, organisme);
        if (!exist) {
            throw new NuxeoException("L'attribut Organisme identifié par " + organisme + " n'existe pas.");
        }

        // : DateDebut
        final Calendar dateDebut = membreGroupe.getDateDebut();
        if (dateDebut == null) {
            throw new NuxeoException("L'attribut DateDebut de l'objet MembreGroupe est obligatoire.");
        }

        // : DateFin
        final Calendar dateFin = membreGroupe.getDateFin();
        if (dateFin != null) {
            if (dateFin.before(dateDebut)) {
                throw new NuxeoException("L'attribut DateFin de l'objet Identite doit être ultérieur à DateDebut.");
            }
        }

        // : Mandat
        final String mandat = membreGroupe.getMandat();
        if (StringUtils.isEmpty(mandat)) {
            throw new NuxeoException("L'attribut Organisme de l'objet MembreGroupe est obligatoire.");
        }
        final DocumentModel existingMandatDoc = tableReferenceService.getMandatById(session, mandat);
        if (existingMandatDoc == null) {
            throw new NuxeoException("L'attribut Organisme identifié par " + mandat + " n'existe pas.");
        }
    }

    /**
     * valide les données contenues dans MembreGroupe en mise à jour
     *
     * @param session la session de l'utilisateur
     * @param membreGroupe membreGroupe à Valider
     */
    public static void validateMembreGroupeForUpdate(final CoreSession session, final MembreGroupe membreGroupe) {
        // : Identifiant
        final String identifiant = membreGroupe.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet MembreGroupe est obligatoire.");
        }

        validateMembreGroupeForCreation(session, membreGroupe);
    }

    /**
     * valide les données contenues dans Ministere en creation
     *
     * @param session session de l'utilisateur
     * @param circonscription circonscription à Valider
     */
    public static void validateMinistereForCreation(final CoreSession session, final Ministere ministere) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

        // : Nom
        final String nom = ministere.getNom();
        if (StringUtils.isEmpty(nom)) {
            throw new NuxeoException("L'attribut Nom de l'objet Ministere est obligatoire.");
        }

        // : Libelle
        final String libelle = ministere.getLibelleMinistre();
        if (StringUtils.isEmpty(libelle)) {
            throw new NuxeoException("L'attribut Libelle de l'objet Ministere est obligatoire.");
        }

        // : Edition
        final String edition = ministere.getEdition();
        if (StringUtils.isEmpty(edition)) {
            throw new NuxeoException("L'attribut Libelle de l'objet Ministere est obligatoire.");
        }

        // : Appellation
        final String appellation = ministere.getAppellation();
        if (StringUtils.isEmpty(appellation)) {
            throw new NuxeoException("L'attribut Appellation de l'objet Ministere est obligatoire.");
        }

        // : Gouvernement
        final String gouvernement = ministere.getGouvernement();
        if (StringUtils.isEmpty(gouvernement)) {
            throw new NuxeoException("L'attribut Ministere de l'objet Ministere est obligatoire.");
        }

        final boolean exist = tableReferenceService.hasGouvernement(session, gouvernement);
        if (!exist) {
            throw new NuxeoException("L'attribut Ministere identifié par " + gouvernement + " n'existe pas.");
        }

        // : DateDebut
        final Calendar dateDebut = ministere.getDateDebut();
        if (dateDebut == null) {
            throw new NuxeoException("L'attribut DateDebut de l'objet Ministere est obligatoire.");
        }

        // : DateFin
        final Calendar dateFin = ministere.getDateFin();
        if (dateFin != null) {
            if (dateFin.before(dateDebut)) {
                throw new NuxeoException("L'attribut DateFin de l'objet Ministere doit être ultérieur à DateDebut.");
            }
        }
    }

    /**
     * valide les données contenues dans Ministere en mise à jour
     *
     * @param session la session de l'utilisateur
     * @param ministere ministere à Valider
     */
    public static void validateMinistereForUpdate(final CoreSession session, final Ministere ministere) {
        // : Identifiant
        final String identifiant = ministere.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Ministere est obligatoire.");
        }

        validateMinistereForCreation(session, ministere);
    }

    /**
     * valide les données contenues dans Organisme en creation
     *
     * @param organisme organisme à Valider
     */
    public static void validateOrganismeForCreation(final Organisme organisme) {
        // : Nom
        final String nom = organisme.getNom();
        if (StringUtils.isEmpty(nom)) {
            throw new NuxeoException("L'attribut Nom de l'objet Organisme est obligatoire.");
        }

        // : Type
        final String type = organisme.getTypeOrganisme();
        if (StringUtils.isEmpty(type)) {
            throw new NuxeoException("L'attribut type de l'objet Organisme est obligatoire.");
        }

        // : Proprietaire
        final String proprietaire = organisme.getProprietaire();
        if (StringUtils.isEmpty(proprietaire)) {
            throw new NuxeoException("L'attribut proprietaire de l'objet Organisme est obligatoire.");
        }

        // : DateDebut
        final Calendar dateDebut = organisme.getDateDebut();
        if (dateDebut == null) {
            throw new NuxeoException("L'attribut DateDebut de l'objet Organisme est obligatoire.");
        }

        // : DateFin
        final Calendar dateFin = organisme.getDateFin();
        if (dateFin != null) {
            if (dateFin.before(dateDebut)) {
                throw new NuxeoException("L'attribut DateFin de l'objet Organisme doit être ultérieur à DateDebut.");
            }
        }
    }

    /**
     * valide les données contenues dans Organisme en mise à jour
     *
     * @param organisme organisme ministere à Valider
     */
    public static void validateOrganismeForUpdate(final Organisme organisme) {
        // : Identifiant
        final String identifiant = organisme.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Organisme est obligatoire.");
        }

        validateOrganismeForCreation(organisme);
    }

    /**
     * valide les données contenues dans Periode en creation
     *
     * @param periode periode à Valider
     */
    public static void validatePeriodeForCreation(final Periode periode) {
        // : Numero
        final String nom = periode.getNumero();
        if (StringUtils.isEmpty(nom)) {
            throw new NuxeoException("L'attribut numero de l'objet Periode est obligatoire.");
        }

        // : Type
        final String type = periode.getTypePeriode();
        if (StringUtils.isEmpty(type)) {
            throw new NuxeoException("L'attribut type de l'objet Periode est obligatoire.");
        }

        // : Proprietaire
        final String proprietaire = periode.getProprietaire();
        if (StringUtils.isEmpty(proprietaire)) {
            throw new NuxeoException("L'attribut proprietaire de l'objet Periode est obligatoire.");
        }

        // : DateDebut
        final Calendar dateDebut = periode.getDateDebut();
        if (dateDebut == null) {
            throw new NuxeoException("L'attribut DateDebut de l'objet Periode est obligatoire.");
        }

        // : DateFin
        final Calendar dateFin = periode.getDateFin();
        if (dateFin != null) {
            if (dateFin.before(dateDebut)) {
                throw new NuxeoException("L'attribut DateFin de l'objet Periode doit être ultérieur à DateDebut.");
            }
        }
    }

    /**
     * valide les données contenues dans Periode en mise à jour
     *
     * @param periode periode à Valider
     */
    public static void validatePeriodeForUpdate(final Periode periode) {
        // : Identifiant
        final String identifiant = periode.getIdentifiant();
        if (StringUtils.isEmpty(identifiant)) {
            throw new NuxeoException("L'attribut Identifiant de l'objet Periode est obligatoire.");
        }

        validatePeriodeForCreation(periode);
    }
}
