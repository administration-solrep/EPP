package fr.dila.st.ui.bean.parlement;

import fr.dila.st.core.client.AbstractMapDTO;

public class STMessageDTO extends AbstractMapDTO {
    private static final long serialVersionUID = 1L;

    private static final String ID = "id";
    private static final String ID_DOSSIER = "idDossier";
    private static final String OBJET_DOSSIER = "objetDossier";
    private static final String EMETTEUR = "emetteur";
    private static final String DESTINATAIRE = "destinataire";
    private static final String COPIE = "copie";
    private static final String COMMUNICATION = "communication";
    private static final String DATE = "date";

    // Indicateurs du message
    private static final String ETAT_MESSAGE = "etatMessage";
    private static final String ETAT_EVENEMENT = "etatEvenement";
    private static final String PIECE_JOINTE = "pieceJointe";
    private static final String EN_ALERTE = "enAlerte";
    private static final String DOSSIER_EN_ALERTE = "dossierEnAlerte";
    private static final String LOCKER = "locker";
    private static final String LOCK_TIME = "lockTime";

    private static final String DOC_ID_FOR_SELECTION = "docIdForSelection";

    public STMessageDTO() {
        super();
    }

    public STMessageDTO(
        String idDossier,
        String objetDossier,
        String emetteur,
        String destinataire,
        String copie,
        String communication,
        String date
    ) {
        setIdDossier(idDossier);
        setObjetDossier(objetDossier);
        setEmetteur(emetteur);
        setDestinataire(destinataire);
        setCopie(copie);
        setCommunication(communication);
        setDate(date);
    }

    public String getId() {
        return getString(ID);
    }

    public void setId(String id) {
        put(ID, id);
    }

    public String getIdDossier() {
        return getString(ID_DOSSIER);
    }

    public void setIdDossier(String idDossier) {
        put(ID_DOSSIER, idDossier);
    }

    public String getObjetDossier() {
        return getString(OBJET_DOSSIER);
    }

    public void setObjetDossier(String objetDossier) {
        put(OBJET_DOSSIER, objetDossier);
    }

    public String getEmetteur() {
        return getString(EMETTEUR);
    }

    public void setEmetteur(String emetteur) {
        put(EMETTEUR, emetteur);
    }

    public String getDestinataire() {
        return getString(DESTINATAIRE);
    }

    public void setDestinataire(String destinataire) {
        put(DESTINATAIRE, destinataire);
    }

    public String getCopie() {
        return getString(COPIE);
    }

    public void setCopie(String copie) {
        put(COPIE, copie);
    }

    public String getCommunication() {
        return getString(COMMUNICATION);
    }

    public void setCommunication(String communication) {
        put(COMMUNICATION, communication);
    }

    public String getDate() {
        return getString(DATE);
    }

    public void setDate(String date) {
        put(DATE, date);
    }

    public String getEtatMessage() {
        return getString(ETAT_MESSAGE);
    }

    public void setEtatMessage(String etatMessage) {
        put(ETAT_MESSAGE, etatMessage);
    }

    public String getEtatEvenement() {
        return getString(ETAT_EVENEMENT);
    }

    public void setEtatEvenement(String etatEvenement) {
        put(ETAT_EVENEMENT, etatEvenement);
    }

    public boolean isPieceJointe() {
        return getBoolean(PIECE_JOINTE);
    }

    public void setPieceJointe(boolean pieceJointe) {
        put(PIECE_JOINTE, pieceJointe);
    }

    public boolean isEnAlerte() {
        return getBoolean(EN_ALERTE);
    }

    public void setEnAlerte(boolean enAlerte) {
        put(EN_ALERTE, enAlerte);
    }

    public boolean isDossierEnAlerte() {
        return getBoolean(DOSSIER_EN_ALERTE);
    }

    public void setDossierEnAlerte(boolean dossierEnAlerte) {
        put(DOSSIER_EN_ALERTE, dossierEnAlerte);
    }

    public void setLocker(String locker) {
        put(LOCKER, locker);
    }

    public String getLocker() {
        return getString(LOCKER);
    }

    public void setLockTime(String lockTime) {
        put(LOCK_TIME, lockTime);
    }

    public String getLockTime() {
        return getString(LOCK_TIME);
    }

    @Override
    public String getType() {
        return "Message";
    }

    @Override
    public String getDocIdForSelection() {
        return getString(DOC_ID_FOR_SELECTION);
    }
}
