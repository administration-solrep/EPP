package fr.dila.solonepp.api.constant;

import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.st.api.constant.STSchemaConstant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Constantes des schémas de l'application SOLON EPP.
 *
 * @author jtremeaux
 */
public final class SolonEppSchemaConstant {
    /**
     * Ensemble des institutions.
     */
    public static final Set<InstitutionsEnum> INSTITUTIONS_VALUES = new HashSet<InstitutionsEnum>(
        Arrays.asList(
            InstitutionsEnum.GOUVERNEMENT,
            InstitutionsEnum.ASSEMBLEE_NATIONALE,
            InstitutionsEnum.SENAT,
            InstitutionsEnum.DILA,
            InstitutionsEnum.CMP,
            InstitutionsEnum.CONGRES_PARLEMENT,
            InstitutionsEnum.GRP_AN_SENAT,
            InstitutionsEnum.OFFICES_DELEGATIONS
        )
    );

    // *************************************************************
    // Schéma organigramme unité structurelle
    // *************************************************************
    /**
     * Propriété du schéma unité structurelle : institutions parentes.
     */
    public static final String UNITE_STRUCTURELLE_PARENT_INSTITUTIONS_PROPERTY = "parentInstitutions";

    // *************************************************************
    // Schéma organigramme poste
    // *************************************************************
    /**
     * Propriété du schéma poste : institutions parentes.
     */
    public static final String POSTE_PARENT_INSTITUTIONS_PROPERTY = "parentInstitutions";

    // *************************************************************
    // Schéma liaison jeton maitre / notification
    // *************************************************************
    /**
     * Propriété du schéma jeton_doc : Valeur du type de jeton : TABLE_REF.
     */
    public static final String JETON_DOC_TYPE_WEBSERVICE_TABLE_REF_VALUE = "TABLE_REF";

    /**
     * Propriété du schéma jeton_doc : Valeur du type de jeton : EVENEMENT.
     */
    public static final String JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE = "EVENEMENT";

    /**
     * Propriété du schéma jeton_doc : Type de notification.
     */
    public static final String JETON_DOC_NOTIFICATION_TYPE_PROPERTY = "notificationType";

    /**
     * Propriété du schéma jeton_doc : Type de notification création / modification / suppression d'un objet de référence.
     */
    public static final String JETON_DOC_NOTIFICATION_TYPE_OBJET_REF_UPDATE_VALUE = "OBJET_REF_UPDATE";

    /**
     * Propriété du schéma jeton_doc : Type de notification réinitialisation d'un objet de référence.
     */
    public static final String JETON_DOC_NOTIFICATION_TYPE_OBJET_REF_RESET_VALUE = "OBJET_REF_RESET";

    /**
     * Propriété du schéma jeton_doc : Type de notification publication initiale d'une version.
     */
    public static final String JETON_DOC_NOTIFICATION_PUBLIER_INITIALE_VALUE = "PUBLIER_INITIALE";

    /**
     * Propriété du schéma jeton_doc : Type de notification publication d'une version complétée.
     */
    public static final String JETON_DOC_NOTIFICATION_PUBLIER_COMPLEMENT_VALUE = "PUBLIER_COMPLEMENT";

    /**
     * Propriété du schéma jeton_doc : Type de notification publication d'une version rectifiée.
     */
    public static final String JETON_DOC_NOTIFICATION_PUBLIER_RECTIFICATION_VALUE = "PUBLIER_RECTIFICATION";

    /**
     * Propriété du schéma jeton_doc : Type de notification publication d'une version annulée.
     */
    public static final String JETON_DOC_NOTIFICATION_PUBLIER_ANNULATION_VALUE = "PUBLIER_ANNULATION";

    /**
     * Propriété du schéma jeton_doc : Type de notification initiale d'une version brouillon.
     */
    public static final String JETON_DOC_NOTIFICATION_BROUILLON_INITIALE_VALUE = "BROUILLON_INITIALE";

    /**
     * Propriété du schéma jeton_doc : Type de notification demande de validation (d'une rectification ou annulation).
     */
    public static final String JETON_DOC_NOTIFICATION_DEMANDER_VALIDATION_VALUE = "DEMANDER_VALIDATION";

    /**
     * Propriété du schéma jeton_doc : Type de notification acceptation d'une demande.
     */
    public static final String JETON_DOC_NOTIFICATION_ACCEPTER_VALUE = "ACCEPTER";

    /**
     * Propriété du schéma jeton_doc : Type de notification rejet d'une demande.
     */
    public static final String JETON_DOC_NOTIFICATION_REJETER_VALUE = "REJETER";

    /**
     * Propriété du schéma jeton_doc : Type de notification abandon d'une demande.
     */
    public static final String JETON_DOC_NOTIFICATION_ABANDONNER_VALUE = "ABANDONNER";

    /**
     * Propriété du schéma jeton_doc : Type de notification accusé réception d'un message.
     */
    public static final String JETON_DOC_NOTIFICATION_ACCUSER_RECEPTION_VALUE = "ACCUSER_RECEPTION";

    /**
     * Propriété du schéma jeton_doc : Type de notification mise a jour d'un visa interne d'un evenement.
     */
    public static final String JETON_DOC_NOTIFICATION_MAJ_VISA_INTERNE_VALUE = "MAJ_VISA_INTERNE";

    /**
     * Propriété du schéma jeton_doc : Type de notification passage d'un événement à en instance.
     */
    public static final String JETON_DOC_NOTIFICATION_EVENEMENT_EN_INSTANCE_VALUE = "EVENEMENT_EN_INSTANCE";

    /**
     * Propriété du schéma jeton_doc : Nombre d'essais restants de notifications WS ( > 0 si il faut réessayer).
     */
    public static final String JETON_DOC_WS_RETRY_LEFT_PROPERTY = "wsRetryLeft";

    /**
     * Propriété du schéma jeton_doc : Identifiant technique de l'objet de référence mis à jour.
     */
    public static final String JETON_DOC_OBJET_REF_ID_PROPERTY = "objetRefId";

    /**
     * Propriété du schéma jeton_doc : Type de l'objet de référence mis à jour (Acteur)...
     */
    public static final String JETON_DOC_OBJET_REF_TYPE_PROPERTY = "objetRefType";

    /**
     * Propriété du schéma jeton_doc : Identifiant technique de l'événement.
     */
    public static final String JETON_DOC_EVENEMENT_ID_PROPERTY = "evenementId";

    /**
     * Propriété du schéma jeton_doc : Type d'événement.
     */
    public static final String JETON_DOC_EVENEMENT_TYPE_PROPERTY = "evenementType";

    /**
     * Propriété du schéma jeton_doc : Etat du cycle de vie de l'événement.
     */
    public static final String JETON_DOC_EVENEMENT_LIFE_CYCLE_STATE_PROPERTY = "evenementLifeCycleState";

    /**
     * Propriété du schéma jeton_doc : Identifiant technique de l'émetteur (annuaire LDAP).
     */
    public static final String JETON_DOC_EVENEMENT_EMETTEUR_PROPERTY = "evenementEmetteur";

    /**
     * Propriété du schéma jeton_doc : Identifiant technique du destinataire (annuaire LDAP).
     */
    public static final String JETON_DOC_EVENEMENT_DESTINATAIRE_PROPERTY = "evenementDestinataire";

    /**
     * Propriété du schéma jeton_doc : Identifiant technique des destinataires en copie (annuaire LDAP).
     */
    public static final String JETON_DOC_EVENEMENT_DESTINATAIRE_COPIE_PROPERTY = "evenementDestinataireCopie";

    /**
     * Propriété du schéma jeton_doc : Présence de pièce jointes.
     */
    public static final String JETON_DOC_VERSION_PRESENCE_PIECE_JOINTE_PROPERTY = "versionPresencePieceJointe";

    /**
     * Propriété du schéma jeton_doc : État du cycle de vie de la version.
     */
    public static final String JETON_DOC_VERSION_LIFE_CYCLE_STATE_PROPERTY = "versionLifeCycleState";

    /**
     * Propriété du schéma jeton_doc : Objet de la version.
     */
    public static final String JETON_DOC_VERSION_OBJET_PROPERTY = "versionObjet";

    /**
     * Propriété du schéma jeton_doc : Date d'horodatage de la version.
     */
    public static final String JETON_DOC_VERSION_HORODATAGE_PROPERTY = "versionHorodatage";

    /**
     * Propriété du schéma jeton_doc : Niveau de lecture.
     */
    public static final String JETON_DOC_VERSION_NIVEAU_LECTURE_PROPERTY = "versionNiveauLecture";

    /**
     * Propriété du schéma jeton_doc : Niveau de lecture numéro.
     */
    public static final String JETON_DOC_VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY = "versionNiveauLectureNumero";

    /**
     * Propriété du schéma jeton_doc : Numéro de version majeur.
     */
    public static final String JETON_DOC_VERSION_MAJOR_VERSION_PROPERTY = "versionMajorVersion";

    /**
     * Propriété du schéma jeton_doc : Numéro de version mineur.
     */
    public static final String JETON_DOC_VERSION_MINOR_VERSION_PROPERTY = "versionMinorVersion";

    /**
     * Propriété du schéma jeton_doc : Identifiant sénat (chaîne libre renseignable uniquement par le sénat).
     */
    public static final String JETON_DOC_VERSION_SENAT_PROPERTY = "versionSenat";

    /**
     * Propriété du schéma jeton_doc : Identifiant technique du dossier.
     */
    public static final String JETON_DOC_DOSSIER_ID_PROPERTY = "dossierId";

    /**
     * Propriété du schéma jeton_doc : Nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte".
     */
    public static final String JETON_DOC_DOSSIER_ALERTE_COUNT_PROPERTY = "dossierAlerteCount";

    /**
     * Propriété du schéma jeton_doc : Liste des corbeilles de distribution du message.
     */
    public static final String JETON_DOC_MESSAGE_CORBEILLE_LIST_PROPERTY = "messageCorbeilleList";

    /**
     * Propriété du schéma jeton_doc : jeton id
     */
    public static final String JETON_DOC_JETON_ID_PROPERTY = "id_jeton";

    /**
     * Propriété du schéma jeton_doc : Etat du cycle de vie du message.
     */
    public static final String JETON_DOC_MESSAGE_LIFE_CYCLE_STATE_PROPERTY = "messageLifeCycleState";

    /**
     * Propriété du schéma jeton_doc : Type de message (EMETTEUR, DESTINATAIRE, COPIE).
     */
    public static final String JETON_DOC_MESSAGE_TYPE_PROPERTY = "messageType";

    /**
     * Propriété du schéma jeton_doc : Vrai si les versions de cet événement nécessitent un accusé de réception.
     */
    public static final String JETON_DOC_MESSAGE_AR_NECESSAIRE_PROPERTY = "messageArNecessaire";

    /**
     * Propriété du schéma jeton_doc : Nombre de versions qui n'ont pas encore accusé réception par le destinataire.
     */
    public static final String JETON_DOC_MESSAGE_AR_NON_DONNE_COUNT_PROPERTY = "messageArNonDonneCount";

    // *************************************************************
    // Attribut commun aux objets de référence
    // *************************************************************
    /**
     * Identifiant technique de l'enregistrement.
     */
    public static final String TABLE_REFERENCE_IDENTIFIANT_PROPERTY = "identifiant";

    /**
     * Propriété des schémas tables de référence : proprietaire de l'objet de référence (GOUVERNEMENT, AN, SENAT).
     */
    public static final String TABLE_REFERENCE_PROPRIETAIRE_PROPERTY = "proprietaire";

    /**
     * Propriété des schémas tables de référence : Date de début de l'enregistrement (suppression logique).
     */
    public static final String TABLE_REFERENCE_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * Propriété des schémas tables de référence : Date de fin de l'enregistrement (suppression logique).
     */
    public static final String TABLE_REFERENCE_DATE_FIN_PROPERTY = "dateFin";

    // *************************************************************
    // Schéma table de référence acteur.
    // *************************************************************
    /**
     * Nom du schéma acteur.
     */
    public static final String ACTEUR_SCHEMA = "acteur";

    /**
     * Préfixe du schéma acteur.
     */
    public static final String ACTEUR_SCHEMA_PREFIX = "act";

    // *************************************************************
    // Schéma table de référence circonscription.
    // *************************************************************
    /**
     * Nom du schéma circonscription.
     */
    public static final String CIRCONSCRIPTION_SCHEMA = "circonscription";

    /**
     * Préfixe du schéma circonscription.
     */
    public static final String CIRCONSCRIPTION_SCHEMA_PREFIX = "cir";

    /**
     * Propriété du schéma circonscription : Nom.
     */
    public static final String CIRCONSCRIPTION_NOM_PROPERTY = "nom";

    /**
     * Propriété du schéma circonscription : Date de début.
     */
    public static final String CIRCONSCRIPTION_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * Propriété du schéma circonscription : Date de fin.
     */
    public static final String CIRCONSCRIPTION_DATE_FIN_PROPERTY = "dateFin";

    // *************************************************************
    // Schéma table de référence gouvernement.
    // *************************************************************
    /**
     * Nom du schéma gouvernement.
     */
    public static final String GOUVERNEMENT_SCHEMA = "gouvernement_schema";

    /**
     * Préfixe du schéma gouvernement.
     */
    public static final String GOUVERNEMENT_SCHEMA_PREFIX = "gvt";

    /**
     * Attribut : appellation
     */
    public static final String GOUVERNEMENT_APPELLATION_PROPERTY = "appellation";

    /**
     * Attribut : dateDebut
     */
    public static final String GOUVERNEMENT_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * Attribut : dateFin
     */
    public static final String GOUVERNEMENT_DATE_FIN_PROPERTY = "dateFin";

    // *************************************************************
    // Schéma table de référence identité.
    // *************************************************************
    /**
     * Nom du schéma identité.
     */
    public static final String IDENTITE_SCHEMA = "identite";

    /**
     * Préfixe du schéma identité.
     */
    public static final String IDENTITE_SCHEMA_PREFIX = "idt";

    /**
     * Attribut : Nom
     */
    public static final String IDENTITE_NOM_PROPERTY = "nom";

    /**
     * Attribut : Prénom
     */
    public static final String IDENTITE_PRENOM_PROPERTY = "prenom";

    /**
     * Attribut : Civilité
     */
    public static final String IDENTITE_CIVILITE_PROPERTY = "civilite";

    /**
     * Attribut : dateDebut
     */
    public static final String IDENTITE_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * Attribut : dateFin
     */
    public static final String IDENTITE_DATE_FIN_PROPERTY = "dateFin";

    /**
     * Attribut : dateNaissance
     */
    public static final String IDENTITE_DATE_NAISSANCE_PROPERTY = "dateNaissance";

    /**
     * Attribut : lieuNaissance
     */
    public static final String IDENTITE_LIEU_NAISSANCE_PROPERTY = "lieuNaissance";

    /**
     * Attribut : departementNaissance
     */
    public static final String IDENTITE_DEPARTEMENT_NAISSANCE_PROPERTY = "departementNaissance";

    /**
     * Attribut : paysNaissance
     */
    public static final String IDENTITE_PAYS_NAISSANCE_PROPERTY = "paysNaissance";

    /**
     * Attribut : acteur
     */
    public static final String IDENTITE_ACTEUR_PROPERTY = "acteur";

    // *************************************************************
    // Schéma table de référence mandat.
    // *************************************************************
    /**
     * Nom du schéma mandat.
     */
    public static final String MANDAT_SCHEMA = "mandat";

    /**
     * Préfixe du schéma mandat.
     */
    public static final String MANDAT_SCHEMA_PREFIX = "man";

    /**
     * Attribut : typeMandat
     */
    public static final String MANDAT_TYPE_MANDAT_PROPERTY = "typeMandat";

    /**
     * Attribut : dateDebut
     */
    public static final String MANDAT_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * Attribut : dateFin
     */
    public static final String MANDAT_DATE_FIN_PROPERTY = "dateFin";

    /**
     * Attribut : ordreProtocolaire
     */
    public static final String MANDAT_ORDRE_PROTOCOLAIRE_PROPERTY = "ordreProtocolaire";

    /**
     * Attribut : titre
     */
    public static final String MANDAT_TITRE_PROPERTY = "titre";

    /**
     * Attribut : identite
     */
    public static final String MANDAT_IDENTITE_PROPERTY = "identite";

    /**
     * Attribut : ministere
     */
    public static final String MANDAT_MINISTERE_PROPERTY = "ministere";

    /**
     * Attribut : circonscription
     */
    public static final String MANDAT_CIRCONSCRIPTION_PROPERTY = "circonscription";

    /**
     * Attribut : appellation
     */
    public static final String MANDAT_APPELLATION_PROPERTY = "appellation";

    /**
     * Attribut : nor
     */
    public static final String MANDAT_NOR_PROPERTY = "nor";

    /**
     * Attribut : proprietaire
     */
    public static final String MANDAT_PROPRIETAIRE_PROPERTY = "proprietaire";

    // *************************************************************
    // Schéma table de référence membre de groupe.
    // *************************************************************
    /**
     * Nom du schéma membre_groupe.
     */
    public static final String MEMBRE_GROUPE_SCHEMA = "membre_groupe";

    /**
     * Préfixe du schéma membre_groupe.
     */
    public static final String MEMBRE_GROUPE_SCHEMA_PREFIX = "mgr";

    /**
     * Attribut : organisme
     */
    public static final String MEMBRE_GROUPE_ORGANISME_PROPERTY = "organisme";

    /**
     * Attribut : mandat
     */
    public static final String MEMBRE_GROUPE_MANDAT_PROPERTY = "mandat";

    /**
     * Attribut : dateDebut
     */
    public static final String MEMBRE_GROUPE_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * Attribut : dateFin
     */
    public static final String MEMBRE_GROUPE_DATE_FIN_PROPERTY = "dateFin";

    // *************************************************************
    // Schéma table de référence membre de ministère.
    // *************************************************************
    /**
     * Nom du schéma ministere.
     */
    public static final String MINISTERE_SCHEMA = "ministere";

    /**
     * Préfixe du schéma ministere.
     */
    public static final String MINISTERE_SCHEMA_PREFIX = "min";

    /**
     * attribut : nom
     */
    public static final String MINISTERE_NOM_PROPERTY = "nom";

    /**
     * attribut : libelleMinistre
     */
    public static final String MINISTERE_LIBELLE_MINISTRE_PROPERTY = "libelleMinistre";

    /**
     * attribut : edition
     */
    public static final String MINISTERE_EDITION_PROPERTY = "edition";

    /**
     * attribut : gouvernement
     */
    public static final String MINISTERE_GOUVERNEMENT_PROPERTY = "gouvernement";

    /**
     * attribut : appellation
     */
    public static final String MINISTERE_APPELLATION_PROPERTY = "appellation";

    /**
     * attribut : dateDebut
     */
    public static final String MINISTERE_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * attribut : dateFin
     */
    public static final String MINISTERE_DATE_FIN_PROPERTY = "dateFin";

    // *************************************************************
    // Schéma table de référence membre de organisme.
    // *************************************************************
    /**
     * Nom du schéma organisme.
     */
    public static final String ORGANISME_SCHEMA = "organisme";

    /**
     * Préfixe du schéma organisme.
     */
    public static final String ORGANISME_SCHEMA_PREFIX = "org";

    /**
     * attribut : nom
     */
    public static final String ORGANISME_NOM_PROPERTY = "nom";

    /**
     * attribut : typeOrganisme
     */
    public static final String ORGANISME_TYPE_ORGANISME_PROPERTY = "typeOrganisme";

    /**
     * attribut : proprietaire
     */
    public static final String ORGANISME_PROPRIETAIRE_PROPERTY = "proprietaire";

    /**
     * attribut : dateDebut
     */
    public static final String ORGANISME_DATE_DEBUT_PROPERTY = "dateDebut";
    /**
     * attribut : dateFin
     */
    public static final String ORGANISME_DATE_FIN_PROPERTY = "dateFin";

    /**
     * attribut : idCommun
     */
    public static final String ORGANISME_ID_COMMUN_PROPERTY = "idCommun";
    /**
     * attribut : baseLEgale
     */
    public static final String ORGANISME_BASE_LEGALE_PROPERTY = "baseLegale";

    // *************************************************************
    // Schéma table de référence membre de période.
    // *************************************************************
    /**
     * Nom du schéma periode.
     */
    public static final String PERIODE_SCHEMA = "periode";

    /**
     * Préfixe du schéma periode.
     */
    public static final String PERIODE_SCHEMA_PREFIX = "per";

    /**
     * attribut : typePeriode
     */
    public static final String PERIODE_TYPE_PERIODE_PROPERTY = "typePeriode";

    /**
     * attribut : proprietaire
     */
    public static final String PERIODE_PROPRIETAIRE_PROPERTY = "proprietaire";

    /**
     * attribut : numero
     */
    public static final String PERIODE_NUMERO_PROPERTY = "numero";

    /**
     * attribut : dateDebut
     */
    public static final String PERIODE_DATE_DEBUT_PROPERTY = "dateDebut";

    /**
     * attribut : dateFin
     */
    public static final String PERIODE_DATE_FIN_PROPERTY = "dateFin";

    // *************************************************************
    // Schéma dossier.
    // *************************************************************
    /**
     * Nom du schéma dossier.
     */
    public static final String DOSSIER_SCHEMA = "dossier";

    /**
     * Préfixe du schéma dossier.
     */
    public static final String DOSSIER_SCHEMA_PREFIX = "dos";

    /**
     * Propriété du schéma dossier : Nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte".
     */
    public static final String DOSSIER_ALERTE_COUNT_PROPERTY = "alerteCount";

    /**
     * attribut : emetteur
     */
    public static final String DOSSIER_EMETTEUR_PROPERTY = "emetteur";

    /**
     * attribut : destinataire
     */
    public static final String DOSSIER_DESTINATAIRE_PROPERTY = "destinataire";

    /**
     * attribut : niveauLectureNumero
     */
    public static final String DOSSIER_NIVEAU_LECTURE_NUMERO_PROPERTY = "niveauLectureNumero";

    /**
     * attribut : niveauLecture
     */
    public static final String DOSSIER_NIVEAU_LECTURE_PROPERTY = "niveauLecture";

    /**
     * attribut : horodatage
     */
    public static final String DOSSIER_HORODATAGE_PROPERTY = "horodatage";

    /**
     * attribut : objet
     */
    public static final String DOSSIER_OBJET_PROPERTY = "objet";

    /**
     * attribut : objet
     */
    public static final String DOSSIER_IDENTIFIANT_METIER_PROPERTY = "identifiantMetier";

    /**
     * attribut : senat
     */
    public static final String DOSSIER_SENAT_PROPERTY = "senat";

    /**
     * attribut : nor
     */
    public static final String DOSSIER_NOR_PROPERTY = "nor";

    /**
     * attribut : natureLoi
     */
    public static final String DOSSIER_NATURE_LOI_PROPERTY = "natureLoi";

    /**
     * attribut : typeLoi
     */
    public static final String DOSSIER_TYPE_LOI_PROPERTY = "typeLoi";

    /**
     * attribut : auteur
     */
    public static final String DOSSIER_AUTEUR_PROPERTY = "auteur";

    /**
     * attribut : coauteur
     */
    public static final String DOSSIER_COAUTEUR_PROPERTY = "coauteur";

    /**
     * attribut : intitule
     */
    public static final String DOSSIER_INTITULE_PROPERTY = "intitule";

    /**
     * attribut : urlDossierAn
     */
    public static final String DOSSIER_URL_DOSSIER_AN_PROPERTY = "urlDossierAn";

    /**
     * attribut : urlDossierSenat
     */
    public static final String DOSSIER_URL_DOSSIER_SENAT_PROPERTY = "urlDossierSenat";

    /**
     * attribut : cosignataire
     */
    public static final String DOSSIER_COSIGNATAIRE_PROPERTY = "cosignataire";

    /**
     * attribut : dateDepotTexte
     */
    public static final String DOSSIER_DATE_DEPOT_TEXTE_PROPERTY = "dateDepotTexte";

    /**
     * attribut : numeroDepotText
     */
    public static final String DOSSIER_NUMERO_DEPOT_TEXTE_PROPERTY = "numeroDepotTexte";

    /**
     * attribut : commissionSaisieAuFond
     */
    public static final String DOSSIER_COMMISSION_SAISIE_AU_FOND_PROPERTY = "commissionSaisieAuFond";

    /**
     * attribut : commissionSaisiePourAvis
     */
    public static final String DOSSIER_COMMISSION_SAISIE_POUR_AVIS_PROPERTY = "commissionSaisiePourAvis";

    /**
     * attribut : date
     */
    public static final String DOSSIER_DATE_PROPERTY = "date";

    /**
     * attribut : dateVote
     */
    public static final String DOSSIER_DATE_VOTE_PROPERTY = "dateVote";

    /**
     * attribut : dateDeclaration
     */
    public static final String DOSSIER_DATE_DECLARATION_PROPERTY = "dateDeclaration";

    /**
     * attribut : datePresentation
     */
    public static final String DOSSIER_DATE_PRESENTATION_PROPERTY = "datePresentation";

    /**
     * attribut : dateLettrePm
     */
    public static final String DOSSIER_DATE_LETTRE_PM_PROPERTY = "dateLettrePm";

    /**
     * attribut : dateAudition
     */
    public static final String DOSSIER_DATE_AUDITION_PROPERTY = "dateAudition";

    /**
     * attribut : dateRefusAssemblee1
     */
    public static final String DOSSIER_DATE_REFUS_ASSEMBLEE_1_PROPERTY = "dateRefusAssemblee1";

    /**
     * attribut : dateConferenceAssemblee2
     */
    public static final String DOSSIER_DATE_CONFERENCE_ASSEMBLEE_2_PROPERTY = "dateConferenceAssemblee2";

    /**
     * attribut : dateRetrait
     */
    public static final String DOSSIER_DATE_RETRAIT_PROPERTY = "dateRetrait";

    /**
     * attribut : dateDistributionElectronique
     */
    public static final String DOSSIER_DATE_DISTRIBUTION_ELECTRONIQUE_PROPERTY = "dateDistributionElectronique";

    /**
     * attribut : rapporteurList
     */
    public static final String DOSSIER_RAPPORTEUR_LIST_PROPERTY = "rapporteurList";

    /**
     * attribut : titre
     */
    public static final String DOSSIER_TITRE_PROPERTY = "titre";

    /**
     * attribut : dateDepotRapport
     */
    public static final String DOSSIER_DATE_DEPOT_RAPPORT_PROPERTY = "dateDepotRapport";

    /**
     * attribut : numeroDepotRapport
     */
    public static final String DOSSIER_NUMERO_DEPOT_RAPPORT_PROPERTY = "numeroDepotRapport";

    /**
     * attribut : commissionSaisie
     */
    public static final String DOSSIER_COMMISSION_SAISIE_PROPERTY = "commissionSaisie";

    /**
     * attribut : dateRefus
     */
    public static final String DOSSIER_DATE_REFUS_PROPERTY = "dateRefus";

    /**
     * attribut : libelleAnnexe
     */
    public static final String DOSSIER_LIBELLE_ANNEXE_PROPERTY = "libelleAnnexe";

    /**
     * attribut : dateEngagementProcedure
     */
    public static final String DOSSIER_DATE_ENGAGEMENT_PROCEDURE_PROPERTY = "dateEngagementProcedure";

    /**
     * attribut : dateRefusProcedureEngagementAn
     */
    public static final String DOSSIER_DATE_REFUS_PROCEDURE_ENGAGEMENT_AN_PROPERTY = "dateRefusProcedureEngagementAn";

    /**
     * attribut : dateRefusProcedureEngagementSenat
     */
    public static final String DOSSIER_DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT_PROPERTY =
        "dateRefusProcedureEngagementSenat";

    /**
     * attribut : numeroTexteAdopte
     */
    public static final String DOSSIER_NUMERO_TEXTE_ADOPTE_PROPERTY = "numeroTexteAdopte";

    /**
     * attribut : dateAdoption
     */
    public static final String DOSSIER_DATE_ADOPTION_PROPERTY = "dateAdoption";

    /**
     * attribut : sortAdoption
     */
    public static final String DOSSIER_SORT_ADOPTION_PROPERTY = "sortAdoption";

    /**
     * attribut : redepot
     */
    public static final String DOSSIER_REDEPOT_PROPERTY = "redepot";

    /**
     * attribut : demandeVote
     */
    public static final String DOSSIER_DEMANDE_VOTE_PROPERTY = "demandeVote";

    /**
     *  attribut : Position de l'alerte (vrai : posée, faux : levée).
     */
    public static final String DOSSIER_POSITION_ALERTE_PROPERTY = "positionAlerte";

    /**
     * attribut : datePromulgation
     */
    public static final String DOSSIER_DATE_PROMULGATION_PROPERTY = "datePromulgation";

    /**
     * attribut : datePublication
     */
    public static final String DOSSIER_DATE_PUBLICATION_PROPERTY = "datePublication";

    /**
     * attribut : numeroLoi
     */
    public static final String DOSSIER_NUMERO_LOI_PROPERTY = "numeroLoi";

    /**
     * attribut : numeroJo
     */
    public static final String DOSSIER_NUMERO_JO_PROPERTY = "numeroJo";

    /**
     * attribut : pageJo
     */
    public static final String DOSSIER_PAGE_JO_PROPERTY = "pageJo";

    /**
     * attribut : dateCMP
     */
    public static final String DOSSIER_DATE_CMP_PROPERTY = "dateCMP";

    /**
     * attribut : resultatCMP
     */
    public static final String VERSION_RESULTAT_CMP_PROPERTY = "resultatCMP";

    /**
     * attribut : dateList
     */
    public static final String DOSSIER_DATE_LIST_PROPERTY = "dateList";

    /**
     * attribut : echeance
     */
    public static final String DOSSIER_ECHEANCE_PROPERTY = "echeance";

    /**
     * attribut : typeActe
     */
    public static final String DOSSIER_TYPE_ACTE_PROPERTY = "typeActe";

    /**
     * attribut : dateActe
     */
    public static final String DOSSIER_DATE_ACTE_PROPERTY = "dateActe";

    /**
     * attribut : sensAvis
     */
    public static final String DOSSIER_SENS_AVIS_PROPERTY = "sensAvis";

    /**
     * attribut : suffrageExprime
     */
    public static final String DOSSIER_SUFFRAGE_EXPRIME_PROPERTY = "suffrageExprime";

    /**
     * attribut : bulletinBlanc
     */
    public static final String DOSSIER_BULLETIN_BLANC_PROPERTY = "bulletinBlanc";

    /**
     * attribut : votePour
     */
    public static final String DOSSIER_VOTE_POUR_PROPERTY = "votePour";

    /**
     * attribut : voteContre
     */
    public static final String DOSSIER_VOTE_CONTRE_PROPERTY = "voteContre";

    /**
     * attribut : abstention
     */
    public static final String DOSSIER_ABSTENTION_PROPERTY = "abstention";

    /**
     * attribut : commissions
     */
    public static final String DOSSIER_COMMISSIONS_PROPERTY = "commissions";

    /**
     * attribut : groupeParlementaire
     */
    public static final String DOSSIER_GROUPE_PARLEMENTAIRE_PROPERTY = "groupeParlementaire";

    /**
     * attribut : dateDemande
     */
    public static final String DOSSIER_DATEDEMANDE_PROPERTY = "dateDemande";

    /**
     * attribut : dossierCible
     */
    public static final String DOSSIER_DOSSIERCIBLE_PROPERTY = "dossierCible";

    /**
     * attribut : baseLegale
     */
    public static final String DOSSIER_BASELEGALE_PROPERTY = "baseLegale";

    /**
     * attribut : organisme
     */
    public static final String DOSSIER_ORGANISME_PROPERTY = "organisme";

    public static final String DOSSIER_DATE_REFUS_ENGAGEMENT_PROCEDURE_PROPERTY = "dateRefusEngagementProcedure";

    /**
     * attribut : decisionProcAcc
     */
    public static final String DOSSIER_DECISION_PROC_ACC_PROPERTY = "decisionProcAcc";

    // *************************************************************
    // Schéma événement.
    // *************************************************************
    /**
     * Nom du schéma événement.
     */
    public static final String EVENEMENT_SCHEMA = "evenement";

    /**
     * Préfixe du schéma événement.
     */
    public static final String EVENEMENT_SCHEMA_PREFIX = "evt";

    /**
     * Propriété du schéma événement : Identifiant technique du type d'événement (vocabulaire).
     */
    public static final String EVENEMENT_TYPE_EVENEMENT_PROPERTY = "typeEvenement";

    /**
     * Propriété du schéma événement : visa internes.
     */
    public static final String CASE_LINK_VISA_INTERNES_PROPERTY = "visaInternes";

    /**
     * Propriété du schéma événement : Identifiant technique de l'événement parent (uniquement pour les événements non créateurs).
     */
    public static final String EVENEMENT_EVENEMENT_PARENT_PROPERTY = "evenementParent";

    /**
     * Propriété du schéma événement : Identifiant technique du dossier.
     */
    public static final String EVENEMENT_DOSSIER_PROPERTY = "dossier";

    /**
     * Propriété du schéma événement : Identifiant technique du dossier precedent.
     */
    public static final String EVENEMENT_DOSSIER_PRECEDENT_PROPERTY = "dossierPrecedent";

    /**
     * Propriété du schéma événement : Identifiant technique de l'émetteur (annuaire LDAP).
     */
    public static final String EVENEMENT_EMETTEUR_PROPERTY = "emetteur";

    /**
     * Propriété du schéma événement : Identifiant technique du destinataire (annuaire LDAP).
     */
    public static final String EVENEMENT_DESTINATAIRE_PROPERTY = "destinataire";

    /**
     * Propriété du schéma événement : Identifiant technique des destinataires en copie (annuaire LDAP).
     */
    public static final String EVENEMENT_DESTINATAIRE_COPIE_PROPERTY = "destinataireCopie";

    /**
     * Propriété du schéma événement : Identifiant technique des destinataires en copie concaténé
     */
    public static final String EVENEMENT_DESTINATAIRE_COPIE_CONCAT_PROPERTY = "destinataireCopieConcat";

    /**
     * Propriété du schéma événement : État en alerte de la branche (POSEE, LEVEE).
     */
    public static final String EVENEMENT_BRANCHE_ALERTE_PROPERTY = "brancheAlerte";

    /**
     * Propriété du schéma événement : Valeur posée de l'alerte, un et un seul des événements parents a posé l'alerte.
     */
    public static final String EVENEMENT_BRANCHE_ALERTE_POSEE_VALUE = "POSEE";

    /**
     * Propriété du schéma événement : Valeur levée de l'alerte, un et un seul des événements parents a levé l'alerte.
     */
    public static final String EVENEMENT_BRANCHE_ALERTE_LEVEE_VALUE = "LEVEE";

    /**
     * uuid du dossier parent
     */
    public static final String EVENEMENT_ID_DOSSIER_PROPERTY = "idDocumentDossier";

    // *************************************************************
    // Schéma version.
    // *************************************************************
    /**
     * Nom du schéma version.
     */
    public static final String VERSION_SCHEMA = "version";

    /**
     * Préfixe du schéma version.
     */
    public static final String VERSION_SCHEMA_PREFIX = "ver";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "procédure législative".
    // *************************************************************
    /**
     * Propriété du schéma version : Liste des champs modifiés par rapport à la dernière version publié, obsolète ou en attente de validation
     */
    public static final String VERSION_MODIFIED_META_LIST_PROPERTY = "modifiedMetaList";

    /**
     * Propriété du schéma version : Identifiant technique de l'événement.
     */
    public static final String VERSION_EVENEMENT_PROPERTY = "evenement";

    /**
     * Propriété du schéma version : Numéro du niveau de lecture.
     */
    public static final String VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY = "niveauLectureNumero";

    /**
     * Propriété du schéma version : Identifiant technique du niveau de lecture (vocabulaire).
     */
    public static final String VERSION_NIVEAU_LECTURE_PROPERTY = "niveauLecture";

    /**
     * Propriété du schéma version : Horodatage de la publication de la version.
     */
    public static final String VERSION_HORODATAGE_PROPERTY = "horodatage";

    /**
     * Propriété du schéma version : Date de l'accusé de réception.
     */
    public static final String VERSION_DATE_AR_PROPERTY = "dateAr";

    /**
     * Propriété du schéma version : Date d'audition.
     */
    public static final String VERSION_DATE_AUDITION_PROPERTY = "dateAudition";

    /**
     * Propriété du schéma version : Objet de l'événement.
     */
    public static final String VERSION_OBJET_PROPERTY = "objet";

    /**
     * Propriété du schéma version : identifiant Metier.
     */
    public static final String VERSION_IDENTIFIANT_METIER_PROPERTY = "identifiantMetier";

    /**
     * Propriété du schéma version : Numéro de version majeur.
     */
    public static final String VERSION_MAJOR_VERSION_PROPERTY = "majorVersion";

    /**
     * Propriété du schéma version : Numéro de version mineur.
     */
    public static final String VERSION_MINOR_VERSION_PROPERTY = "minorVersion";

    /**
     * Propriété du schéma version : Identifiant sénat (chaîne libre renseignable uniquement par le sénat).
     */
    public static final String VERSION_SENAT_PROPERTY = "senat";

    /**
     * Propriété du schéma version : Mode de création de la version brouillon.
     */
    public static final String VERSION_MODE_CREATION_PROPERTY = "modeCreation";

    /**
     * Propriété du schéma version : Indique la présence de pièce jointe.
     */
    public static final String VERSION_PIECE_JOINTE_PRESENTE_PROPERTY = "pieceJointePresente";

    /**
     * Propriété du schéma version : Indique la nature de version
     */
    public static final String VERSION_NATURE_PROPERTY = "nature";

    /**
     * Propriété du schéma version : Indique si la version est la version courante
     */
    public static final String VERSION_VERSION_COURANTE_PROPERTY = "versionCourante";

    /**
     * Propriété du schéma version : Indique la nature ANNULEE de version
     */
    public static final String VERSION_NATURE_ANNULEE_PROPERTY = "ANNULEE";

    /**
     * Propriété du schéma version : Indique la nature RECTIFIEE de version
     */
    public static final String VERSION_NATURE_RECTIFIEE_PROPERTY = "RECTIFIEE";

    /**
     * Propriété du schéma version : Indique la nature COMPLETEE de version
     */
    public static final String VERSION_NATURE_COMPLETEE_PROPERTY = "COMPLETEE";
    /**
     * Propriété du schéma version : Indique la nature ANNULATION_EN_COURS de version
     */
    public static final String VERSION_NATURE_ANNULATION_EN_COURS_PROPERTY = "ANNULATION_EN_COURS";
    /**
     * Propriété du schéma version : Indique la nature RECTIFICATION_EN_COURS de version
     */
    public static final String VERSION_NATURE_RECTIFICATION_EN_COURS_PROPERTY = "RECTIFICATION_EN_COURS";
    /**
     * Propriété du schéma version : Indique la nature ANNULATION_REJETEE de version
     */
    public static final String VERSION_NATURE_ANNULATION_REJETEE_PROPERTY = "ANNULATION_REJETEE";
    /**
     * Propriété du schéma version : Indique la nature RECTIFICATION_REJETEE de version
     */
    public static final String VERSION_NATURE_RECTIFICATION_REJETEE_PROPERTY = "RECTIFICATION_REJETEE";
    /**
     * Propriété du schéma version : Indique la nature ANNULATION_ABANDONNEE de version
     */
    public static final String VERSION_NATURE_ANNULATION_ABANDONNEE_PROPERTY = "ANNULATION_ABANDONNEE";
    /**
     * Propriété du schéma version : Indique la nature RECTIFICATION_ABANDONNEE de version
     */
    public static final String VERSION_NATURE_RECTIFICATION_ABANDONNEE_PROPERTY = "RECTIFICATION_ABANDONNEE";
    /**
     * Propriété du schéma version : Indique la nature VERSION_COURANTE de version
     */
    public static final String VERSION_NATURE_VERSION_COURANTE_PROPERTY = "VERSION_COURANTE";

    /**
     * Propriété mode de création du schéma version : Valeur version brouillon initiale (pas encore de version publiée).
     */
    public static final String VERSION_MODE_CREATION_BROUILLON_INIT_VALUE = "BROUILLON_INIT";

    /**
     * Propriété mode de création du schéma version : Publication initiale.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_INIT_VALUE = "PUBLIE_INIT";

    /**
     * Propriété mode de création du schéma version : Brouillon pour complétion.
     */
    public static final String VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE = "BROUILLON_COMPLETION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version complétée.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_COMPLETION_VALUE = "PUBLIE_COMPLETION";

    /**
     * Propriété mode de création du schéma version : Brouillon pour rectification.
     */
    public static final String VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE = "BROUILLON_RECTIFICATION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version pour demande de rectification.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE =
        "PUBLIE_DEMANDE_RECTIFICATION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version en mode delta pour demande de rectification.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_DELTA_DEMANDE_RECTIFICATION_VALUE =
        "PUBLIE_DELTA_DEMANDE_RECTIFICATION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version suite à une validation de rectification.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_VALIDATION_RECTIFICATION_VALUE =
        "PUBLIE_VALIDATION_RECTIFICATION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version rectifiée.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_RECTIFICATION_VALUE = "PUBLIE_RECTIFICATION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version pour demande d'annulation.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE = "PUBLIE_DEMANDE_ANNULATION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version suite à une validation de l'annulation.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_VALIDATION_ANNULATION_VALUE =
        "PUBLIE_VALIDATION_ANNULATION";

    /**
     * Propriété mode de création du schéma version : Publication d'une version pour annulation.
     */
    public static final String VERSION_MODE_CREATION_PUBLIE_ANNULATION_VALUE = "PUBLIE_ANNULATION";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "procédure législative".
    // *************************************************************
    /**
     * Propriété du schéma version : Numéro de NOR.
     */
    public static final String VERSION_NOR_PROPERTY = "nor";

    /**
     * Propriété du schéma version : Numéro de NOR Loi.
     */
    public static final String VERSION_NOR_LOI_PROPERTY = "norLoi";

    /**
     * Propriété du schéma version : Nature de loi (vocabulaire).
     */
    public static final String VERSION_NATURE_LOI_PROPERTY = "natureLoi";

    /**
     * Propriété du schéma version : Type de loi (vocabulaire).
     */
    public static final String VERSION_TYPE_LOI_PROPERTY = "typeLoi";

    /**
     * Propriété du schéma version : Identifiant technique de l'auteur (table de référence).
     */
    public static final String VERSION_AUTEUR_PROPERTY = "auteur";

    /**
     * Propriété du schéma version : Identifiant technique de l'Organisme (table de référence).
     */
    public static final String VERSION_ORGANISME_PROPERTY = "organisme";

    /**
     * Propriété du schéma version : Identifiant technique du coauteur (table de référence).
     */
    public static final String VERSION_COAUTEUR_PROPERTY = "coauteur";

    public static final String VERSION_COAUTEUR_XPATH = VERSION_SCHEMA_PREFIX + ":" + VERSION_COAUTEUR_PROPERTY;

    /**
     * Propriété du schéma version : Intitulé.
     */
    public static final String VERSION_INTITULE_PROPERTY = "intitule";

    /**
     * Propriété du schéma version : URL du dossier AN.
     */
    public static final String VERSION_URL_DOSSIER_AN_PROPERTY = "urlDossierAn";

    /**
     * Propriété du schéma version : URL du dossier Sénat.
     */
    public static final String VERSION_URL_DOSSIER_SENAT_PROPERTY = "urlDossierSenat";

    /**
     * Propriété du schéma version : Cosignataire (s) collectif (s).
     */
    public static final String VERSION_COSIGNATAIRE_PROPERTY = "cosignataire";

    /**
     * Propriété du schéma version : Date de dépot du texte.
     */
    public static final String VERSION_DATE_DEPOT_TEXTE_PROPERTY = "dateDepotTexte";

    /**
     * Propriété du schéma version : N° de dépôt du texte.
     */
    public static final String VERSION_NUMERO_DEPOT_TEXTE_PROPERTY = "numeroDepotTexte";

    /**
     * Propriété du schéma version : Identifiant technique de la commission de saisie au fond (table de référence).
     */
    public static final String VERSION_COMMISSION_SAISIE_AU_FOND_PROPERTY = "commissionSaisieAuFond";

    /**
     * Propriété du schéma version : Identifiant technique de la commission de saisie pour avis (table de référence).
     */
    public static final String VERSION_COMMISSION_SAISIE_POUR_AVIS_PROPERTY = "commissionSaisiePourAvis";

    /**
     * Propriété du schéma version : Date (générique).
     */
    public static final String VERSION_DATE_PROPERTY = "date";

    /**
     * Propriété du schéma version : Date de saisine.
     */
    public static final String VERSION_DATE_SAISINE_PROPERTY = "dateSaisine";

    /**
     * Propriété du schéma version : Date de retrait.
     */
    public static final String VERSION_DATE_RETRAIT_PROPERTY = "dateRetrait";

    /**
     * Propriété du schéma version : Date de distribution électronique.
     */
    public static final String VERSION_DATE_DISTRIBUTION_ELECTRONIQUE_PROPERTY = "dateDistributionElectronique";

    /**
     * Propriété du schéma version : Identifiant technique de la nature de rapport (vocabulaire).
     */
    public static final String VERSION_NATURE_RAPPORT_PROPERTY = "natureRapport";

    /**
     * Propriété du schéma version : Identifiant technique du (des) rapporteur (s) (table de référence).
     */
    public static final String VERSION_RAPPORTEUR_LIST_PROPERTY = "rapporteurList";

    /**
     * Propriété du schéma version : Titre (intitulé).
     */
    public static final String VERSION_TITRE_PROPERTY = "titre";

    /**
     * Propriété du schéma version : Date de dépot du rapport.
     */
    public static final String VERSION_DATE_DEPOT_RAPPORT_PROPERTY = "dateDepotRapport";

    /**
     * Propriété du schéma version : Numéro de dépot du rapport.
     */
    public static final String VERSION_NUMERO_DEPOT_RAPPORT_PROPERTY = "numeroDepotRapport";

    /**
     * Propriété du schéma version : Nom de la commission saisie (table de référence).
     */
    public static final String VERSION_COMMISSION_SAISIE_PROPERTY = "commissionSaisie";

    /**
     * Propriété du schéma version : Identifiant technique de l'attribution commission (table de référence).
     */
    public static final String VERSION_ATTRIBUTION_COMMISSION_PROPERTY = "attributionCommission";

    /**
     * Propriété du schéma version : Date de refus.
     */
    public static final String VERSION_DATE_REFUS_PROPERTY = "dateRefus";

    /**
     * Propriété du schéma version : Libellé des annexes.
     */
    public static final String VERSION_LIBELLE_ANNEXE_PROPERTY = "libelleAnnexe";

    /**
     * Propriété du schéma version : Date de l'engagement de la procédure.
     */
    public static final String VERSION_DATE_ENGAGEMENT_PROCEDURE_PROPERTY = "dateEngagementProcedure";

    /**
     * Propriété du schéma version : Date de refus de la procédure d'engagement par l'Assemblée Nationale.
     */
    public static final String VERSION_DATE_REFUS_PROCEDURE_ENGAGEMENT_AN_PROPERTY = "dateRefusProcedureEngagementAn";

    /**
     * Propriété du schéma version : Date de refus de la procédure d'engagement par le sénat.
     */
    public static final String VERSION_DATE_REFUS_PROCEDURE_ENGAGEMENT_SENAT_PROPERTY =
        "dateRefusProcedureEngagementSenat";

    /**
     * Propriété du schéma version : N° du texte adopté.
     */
    public static final String VERSION_NUMERO_TEXTE_ADOPTE_PROPERTY = "numeroTexteAdopte";

    /**
     * Propriété du schéma version : Date d'adoption.
     */
    public static final String VERSION_DATE_ADOPTION_PROPERTY = "dateAdoption";

    /**
     * Propriété du schéma version : Identifiant technique du sort d'adoption (vocabulaire).
     */
    public static final String VERSION_SORT_ADOPTION_PROPERTY = "sortAdoption";

    /**
     * Propriété du schéma version : Redépot.
     */
    public static final String VERSION_REDEPOT_PROPERTY = "redepot";

    /**
     * Propriété du schéma version : Date de promulgation.
     */
    public static final String VERSION_DATE_PROMULGATION_PROPERTY = "datePromulgation";

    /**
     * Propriété du schéma version : Date de publication.
     */
    public static final String VERSION_DATE_PUBLICATION_PROPERTY = "datePublication";

    /**
     * Propriété du schéma version : Numéro de la loi.
     */
    public static final String VERSION_NUMERO_LOI_PROPERTY = "numeroLoi";

    /**
     * Propriété du schéma version : Numéro du JO.
     */
    public static final String VERSION_NUMERO_JO_PROPERTY = "numeroJo";

    /**
     * Propriété du schéma version : Page du JO.
     */
    public static final String VERSION_PAGE_JO_PROPERTY = "pageJo";

    /**
     * Propriété du schéma version : rectificatif.
     */
    public static final String VERSION_RECTIFICATIF_PROPERTY = "rectificatif";

    /**
     * Propriété du schéma version : Date de la réunion CMP.
     */
    public static final String VERSION_DATE_CMP_PROPERTY = "dateCMP";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "organisation des sessions extraordinaires".
    // *************************************************************
    /**
     * Identifiant technique du type d'acte (vocabulaire)
     */
    public static final String VERSION_TYPE_ACTE_PROPERTY = "typeActe";

    /**
     * Date de l'acte
     */
    public static final String VERSION_DATE_ACTE_PROPERTY = "dateActe";

    /**
     * attribut : datePresentation
     */
    public static final String VERSION_DATE_PRESENTATION_PROPERTY = "datePresentation";

    /**
     * attribut : dateLettrePm
     */
    public static final String VERSION_DATE_LETTRE_PM_PROPERTY = "dateLettrePm";

    /**
     * attribut : groupeParlementaire
     */
    public static final String VERSION_GROUPE_PARLEMENTAIRE = "groupeParlementaire";

    /**
     * N° de publication du décret ou N° acte
     */
    public static final String VERSION_NUMERO_PUBLICATION_PROPERTY = "numeroPublication";

    /**
     * attribut : demandeVote
     */
    public static final String VERSION_DEMANDE_VOTE = "demandeVote";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "Révision de la constitution".
    // *************************************************************
    /**
     * Identifiant dossiers législatifs concernés
     */
    public static final String VERSION_DOSSIER_LEGISLATIF_PROPERTY = "dossierLegislatif";

    /**
     * Date de convocation
     */
    public static final String VERSION_DATE_CONVOCATION_PROPERTY = "dateConvocation";

    /**
     * Année du JO
     */
    public static final String VERSION_ANNEE_JO_PROPERTY = "anneeJo";

    /**
     * Date JO
     */
    public static final String VERSION_DATE_JO_PROPERTY = "dateJo";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "Consultation des assemblées sur les projets de nomination".
    // *************************************************************
    /**
     * Echeance
     */
    public static final String VERSION_ECHEANCE_PROPERTY = "echeance";

    /**
     * Date de constultation
     */
    public static final String VERSION_DATE_CONSULTATION_PROPERTY = "dateConsultation";

    /**
     * Date de vote
     */
    public static final String VERSION_DATE_VOTE_PROPERTY = "dateVote";

    /**
     * Date de dateDeclaration
     */
    public static final String VERSION_DATE_DECLARATION_PROPERTY = "dateDeclaration";

    /**
     * Identifiant technique du sens de l'avis (vocabulaire)
     */
    public static final String VERSION_SENS_AVIS_PROPERTY = "sensAvis";

    /**
     * Nombre de suffrages exprimes
     */
    public static final String VERSION_SUFFRAGE_EXPRIME_PROPERTY = "suffrageExprime";

    /**
     * Nombre de bulletin blanc ou nul
     */
    public static final String VERSION_BULLETIN_BLANC_PROPERTY = "bulletinBlanc";

    /**
     * Nombre de votes pour
     */
    public static final String VERSION_VOTE_POUR_PROPERTY = "votePour";

    /**
     * Nombre de votes contre
     */
    public static final String VERSION_VOTE_CONTRE_PROPERTY = "voteContre";

    /**
     * Nombre d'abstentions
     */
    public static final String VERSION_ABSTENTION_PROPERTY = "abstention";

    /**
     * Identifiant technique des commissions
     */
    public static final String VERSION_COMMISSIONS_PROPERTY = "commissions";

    /**
     * Date de refus de la première assemblée lors des procédure accélerée (LEX 40)
     */
    public static final String VERSION_DATE_REFUS_ASSEMBLEE_1 = "dateRefusAssemblee1";

    /**
     * Date de conférences des présidents de l'assemblée 2 (LEX 40)
     */
    public static final String VERSION_DATE_CONF_PRES_ASSEMBLEE_2 = "dateConferenceAssemblee2";

    /**
     * Décision sur l'engagement de la procédure accélérée
     */
    public static final String VERSION_DECISION_PROC_ACC = "decisionProcAcc";

    /**
     * Date de refus d'engagement de procédure accélérée par une assemblée
     */
    public static final String VERSION_DATE_REFUS_ENGAGEMENT_PROCEDURE_ACC = "dateRefusEngagementProcedure";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "Convocation du congres au titre de l'article 18 de la constitution".
    // *************************************************************
    /**
     * Date du congres
     */
    public static final String VERSION_DATE_CONGRES_PROPERTY = "dateCongres";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "Demande de prolongation d'une intervention extérieure".
    // *************************************************************

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "Résolution de l'article 34-1 de la constitution".
    // *************************************************************
    /**
     * Date de la demande
     */
    public static final String VERSION_DATE_DEMANDE_PROPERTY = "dateDemande";

    /**
     * Identifiant technique du motif d'irrecevabilite (vocabulaire)
     */
    public static final String VERSION_MOTIF_IRRECEVABILITE_PROPERTY = "motifIrrecevabilite";

    // *************************************************************
    // Schéma version : données de la catégorie d'événement "Dépôt de rapports au parlement".
    // *************************************************************
    /**
     * Identifiant technique du rapport du parlement (vocabulaire)
     */
    public static final String VERSION_RAPPORT_PARLEMENT_PROPERTY = "rapportParlement";

    /**
     * Annee du rapport
     */
    public static final String VERSION_ANNEE_RAPPORT_PROPERTY = "anneeRapport";

    /**
     * URL de la base legale
     */
    public static final String VERSION_URL_BASE_LEGALE_PROPERTY = "urlBaseLegale";

    /**
     * Base legale
     */
    public static final String VERSION_BASE_LEGALE_PROPERTY = "baseLegale";

    // *************************************************************
    // Schéma version : données de la d'événement "Insertion d'information parlementaires au JO lois et décrets".
    // *************************************************************
    /**
     * Numero de la rubrique
     */
    public static final String VERSION_NUMERO_RUBRIQUE_PROPERTY = "numeroRubrique";

    /**
     * URL de publication
     */
    public static final String VERSION_URL_PUBLICATION_PROPERTY = "urlPublication";

    public static final String VERSION_RUBRIQUE = "rubrique";

    // *************************************************************
    // Schéma version : données de la d'événement "Organismes extra-parlementaires".
    // *************************************************************
    /**
     * Identifiants techniques des parlementaires titulaires (table de reference)
     */
    public static final String VERSION_PARLEMENTAIRE_TITULAIRE_LIST_PROPERTY = "parlementaireTitulaireList";

    /**
     * Identifiants techniques des parlementaires suppleants (table de reference)
     */
    public static final String VERSION_PARLEMENTAIRE_SUPPLEANT_LIST_PROPERTY = "parlementaireSuppleantList";

    /**
     * Date de designation
     */
    public static final String VERSION_DATE_DESIGNATION_PROPERTY = "dateDesignation";

    // *************************************************************
    // Schéma version : données de la d'événement "Autres".
    // *************************************************************
    /**
     * Propriété du schéma version : Position de l'alerte (vrai : posée, faux : levée).
     */
    public static final String VERSION_POSITION_ALERTE_PROPERTY = "positionAlerte";

    /**
     * date de caducité
     */
    public static final String VERSION_DATE_CADUCITE_PROPERTY = "dateCaducite";

    /**
     * Dossier cible
     */
    public static final String VERSION_DOSSIER_CIBLE_PROPERTY = "dossierCible";

    /**
     * Liste de date
     */
    public static final String VERSION_DATE_LIST_PROPERTY = "dateList";

    // *************************************************************
    // Schéma pièce jointe.
    // *************************************************************
    /**
     * Nom du schéma pièce jointe.
     */
    public static final String PIECE_JOINTE_SCHEMA = "piece_jointe";

    /**
     * Préfixe du schéma pièce jointe.
     */
    public static final String PIECE_JOINTE_SCHEMA_PREFIX = "pj";

    /**
     * Propriété du schéma pièce jointe : Identifiant technique du type de pièce jointe (vocabulaire).
     */
    public static final String PIECE_JOINTE_TYPE_PIECE_JOINTE_PROPERTY = "typePieceJointe";

    /**
     * Propriété du schéma pièce jointe : Libellé de la pièce jointe.
     */
    public static final String PIECE_JOINTE_NOM_PROPERTY = "nom";

    /**
     * Propriété du schéma pièce jointe : URL vers le site Web de l'émetteur.
     */
    public static final String PIECE_JOINTE_URL_PROPERTY = "url";

    /**
     * Propriété du schéma pièce jointe : Liste des fichiers.
     */
    public static final String PIECE_JOINTE_PIECE_JOINTE_FICHIER_LIST_PROPERTY = "pieceJointeFichierList";

    /**
     * Propriété du schéma pièce jointe : Liste des champs modifiés par rapport à la dernière version publié, obsolète ou en attente de validation
     */
    public static final String PIECE_JOINTE_MODIFIED_META_LIST_PROPERTY = "modifiedMetaList";

    /**
     * Propriété du schéma pièce jointe : Liste des champs modifiés par rapport à la dernière version publié, obsolète ou en attente de validation
     */
    public static final String PIECE_JOINTE_MODIFIED_FILE_LIST_PROPERTY = "modifiedFileList";

    /**
     * Propriété du schéma pièce jointe : Liste des champs supprimés par rapport à la dernière version publié, obsolète ou en attente de validation
     */
    public static final String PIECE_JOINTE_DELETED_FILE_LIST_PROPERTY = "deletedFileList";

    // *************************************************************
    // Schéma case_link.
    // *************************************************************
    /**
     * Nom du schéma version.
     */
    public static final String CASE_LINK_SCHEMA = "case_link";

    /**
     * Préfixe du schéma version.
     */
    public static final String CASE_LINK_SCHEMA_PREFIX = STSchemaConstant.CASE_LINK_SCHEMA_PREFIX;

    /**
     * Propriété du schéma case_link : Type du message (EMETTEUR, DESTINATAIRE, COPIE).
     */
    public static final String CASE_LINK_MESSAGE_TYPE_PROPERTY = "messageType";

    /**
     * Propriété du schéma case_link : Valeur du type de message EMETTEUR.
     */
    public static final String CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE = "EMETTEUR";

    /**
     * Propriété du schéma case_link : Valeur du type de message EMETTEUR.
     */
    public static final String CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE = "DESTINATAIRE";

    /**
     * Propriété du schéma case_link : Valeur du type de message COPIE.
     */
    public static final String CASE_LINK_MESSAGE_TYPE_COPIE_VALUE = "COPIE";

    /**
     * Propriété du schéma case_link : UUID de la version active associée.
     */
    public static final String CASE_LINK_ACTIVE_VERSION_ID_PROPERTY = "activeVersionId";

    /**
     * Propriété du schéma case_link : Liste des corbeilles de distribution du message.
     */
    public static final String CASE_LINK_CORBEILLE_LIST_PROPERTY = "corbeilleList";

    /**
     * Propriété du schéma case_link : date de traitement du message.
     */
    public static final String CASE_LINK_DATE_TRAITEMENT_PROPERTY = "dateTraitement";

    /**
     * Propriété du schéma case_link : AR nécessaire pour ce type d'événement.
     */
    public static final String CASE_LINK_AR_NECESSAIRE_PROPERTY = "arNecessaire";

    /**
     * Propriété du schéma case_link : nombre d'AR non donnés par l'émetteur.
     */
    public static final String CASE_LINK_AR_NON_DONNE_COUNT_PROPERTY = "arNonDonneCount";

    /**
     * Propriété du schéma case_link : état du message (NON_TRAITE, EN_COURS_TRAITEMENT, TRAITE, EN_COURS_REDACTION, EN_ATTENTE_AR, EMIS, AR_RECU).
     */
    public static final String CASE_LINK_ETAT_MESSAGE_PROPERTY = "etatMessage";

    public static final String ID_EVENEMENT = "idEvenement";

    public static final String ID_DOSSIER = "idDossier";

    public static final String COMMENTAIRE = "commentaire";

    public static final String FONCTION = "fonction";

    public static final String PERSONNE = "personne";

    /**
     * utility class
     */
    private SolonEppSchemaConstant() {
        // do nothing
    }
}
