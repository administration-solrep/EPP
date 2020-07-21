package fr.dila.solonepp.web.client;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface MessageDTO  extends Map<String,Serializable> {

    
    public static final String OBJET_DOSSIER = "objetDossier";

    public static final String ID_DOSSIER = "idDossier";
    
    public static final String NIVEAU_LECTURE = "niveauLecture";
    
    public static final String NIVEAU_LECTURE_NUMERO = "niveauLectureNumero";
    
    public static final String EMETTEUR = "emetteur";
    
    public static final String DESTINATAIRE = "destinataire";
    
    public static final String COPIE = "copie";
    
    public static final String EVENEMENT = "evenement";
    
    public static final String DATE = "date";
    
    public static final String ID_EVENEMENT = "idEvenement";

    public static final String ID_SENAT = "idSenat";
    
    public static final String UID_EVENEMENT = "uidEvenement";
    
    public static final String ID_MESSAGE = "idMessage";
    
    public static final String PIECE_JOINTE = "pieceJointe";
    
    public static final String ETAT_EVENEMENT = "etatEvenement";
    
    public static final String ETAT_MESSAGE = "etatMessage";
    
    public static final String ETAT_DOSSIER = "etatDossier";
    
    public static final String TYPE_EMETTEUR = "typeEmetteur";
    
    public static final String LOCKER = "locker";
    
    public static final String LOCK_TIME = "lockTime";
    
    public static final String ALERTE = "alerte";
    
    public static final String MODE_CREATION_VERSION = "modeCreationVersion";
    
    public static final String NUMERO_VERSION = "numeroVersion";
    
    
    /**
     * Constante d'état pour le widget 
     */
    public static final String EN_COURS_REDACTION = "EN_COURS_REDACTION";
    public static final String EMIS = "EMIS";
    public static final String EN_ATTENTE_AR = "EN_ATTENTE_AR";
    public static final String AR_RECU = "AR_RECU";
    public static final String NON_TRAITE = "NON_TRAITE";
    public static final String EN_COURS_TRAITEMENT = "EN_COURS_TRAITEMENT";
    public static final String TRAITE = "TRAITE";
    public static final String AR_RECU_VERSION_REJETE = "AR_RECU_VERSION_REJETE";
    
    public static final String ANNULER = "ANNULER";
    public static final String EN_ATTENTE_VALIDATION = "EN_ATTENTE_VALIDATION";
    public static final String EN_ATTENTE_VALIDATION_ANNULATION = "EN_ATTENTE_VALIDATION_ANNULATION";
    public static final String EN_INSTANCE = "EN_INSTANCE";
    public static final String EN_ALERTE = "EN_ALERTE";
    public static final String PUBLIE = "PUBLIE";
    public static final String BROUILLON = "BROUILLON";

    
    
    
    /**
     * @return the objetDossier
     */
    String getObjetDossier();
    
    void setObjetDossier(String objetDossier);

    /**
     * @return the idDossier
     */
    String getIdDossier();

    /**
     * @param idDossier the idDossier to set
     */
    void setIdDossier(String idDossier);

    /**
     * @return the niveauLectureNumero
     */
    String getNiveauLectureNumero();

    /**
     * @param niveauLectureNumero the niveauLectureNumero to set
     */
    void setNiveauLectureNumero(String niveauLectureNumero);

    /**
     * @return the niveauLecture
     */
    String getNiveauLecture();

    /**
     * @param niveauLecture the niveauLecture to set
     */
    void setNiveauLecture(String niveauLecture);
    
    /**
     * @return the emetteur
     */
    String getEmetteur();

    /**
     * @param emetteur the emetteur to set
     */
    void setEmetteur(String emetteur);

    /**
     * @return the destinataire
     */
    String getDestinataire();

    /**
     * @param destinataire the destinataire to set
     */
    void setDestinataire(String destinataire);

    /**
     * @return the copie
     */
    String getCopie();
    
    /**
     * @param copie the copie to set
     */
    void setCopie(String copie);

    /**
     * @return the evenement
     */
    String getEvenement();

    /**
     * @param evenement the evenement to set
     */
    void setEvenement(String evenement);

    /**
     * @return the date
     */
    Date getDate();

    /**
     * @param date the date to set
     */
    void setDate(Date date);

    /**
     * @return the idEvenement
     */
    String getIdEvenement();
    
    /**
     * @param idEvenement the uidEvenement to set
     */
    void setUidEvenement(String uidEvenement);
    
    /**
     * @return the uidEvenement
     */
    String getUidEvenement();
    
    /**
     * @param idEvenement the idEvenement to set
     */
    void setIdEvenement(String idEvenement);
    
    /**
     * @return the idMessage
     */
    String getIdMessage();

    /**
     * @param idMessage the idMessage to set
     */
    void setIdMessage(String idMessage);
    
    /**
     * @return the pieceJointe
     */
    boolean isPieceJointe();

    /**
     * @param pieceJointe the pieceJointe to set
     */
    void setPieceJointe(boolean pieceJointe);
    
    /**
     * @param etatMessage the etatMessage to set
     */
    void setEtatMessage(String etatMessage);
    
    /**
     * @return the etatMessage
     */
    String getEtatMessage();
    
    /**
     * @param etatEvenement the etatEvenement to set
     */
    void setEtatEvenement(String etatEvenement);
    
    /**
     * @return the etatEvenement
     */
    String getEtatEvenement();
    
    /**
     * @return type emetteur
     */
    boolean isTypeEmetteur();
    
    /**
     * @param typeEmetteur
     */
    void setTypeEmetteur(boolean typeEmetteur);
    
    /**
     * @param locker
     */
    void setLocker(String locker);
    
    /**
     * @return the locker
     */
    String getLocker();
    
    /**
     * @param lockTime
     */
    void setLockTime(String lockTime);
    
    /**
     * @return the lockTime
     */
    String getLockTime();
    
    /**
     * @return the alerte
     */
    boolean isAlerte();

    /**
     * @param alerte the alerte to set
     */
    void setAlerte(boolean alerte);
    
    /**
     * @param modeCreationVersion the modeCreationVersion to set
     */
    void setModeCreationVersion(String modeCreationVersion);
    
    /**
     * @return the modeCreationVersion
     */
    String getModeCreationVersion();

    /**
     * @param numeroVersion
     */
    void setNumeroVersion(String numeroVersion);

    /**
     * @return
     */
    String getNumeroVersion();

    /**
     * @param etatDossier
     */
    void setEtatDossier(String etatDossier);

    /**
     * @return
     */
    String getEtatDossier();
    
    /**
     * @return the idSenat
     */
    String getIdSenat();

    /**
     * @param idSenat the idSenat to set
     */
    void setIdSenat(String idSenat);
    
}
