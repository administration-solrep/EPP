package fr.dila.solonepp.web.client;

import java.util.Date;

import fr.dila.st.core.client.AbstractMapDTO;


public class MessageDTOImpl extends AbstractMapDTO implements MessageDTO {


    /**
     * UID 
     */
    private static final long serialVersionUID = -1011375753184121960L;

    /**
     * @return the objetDossier
     */
    @Override
	public String getObjetDossier() {
        return (String) get(OBJET_DOSSIER);
    }

    /**
     * @param objetDossier the objetDossier to set
     */
    @Override
	public void setObjetDossier(String objetDossier) {
        put(OBJET_DOSSIER, objetDossier);
    }

    /**
     * @return the idDossier
     */
    @Override
	public String getIdDossier() {
        return (String) get(ID_DOSSIER);
    }

    /**
     * @param idDossier the idDossier to set
     */
    @Override
	public void setIdDossier(String idDossier) {
        put(ID_DOSSIER, idDossier);
    }

    /**
     * @return the niveauLecturenumero
     */
    @Override
	public String getNiveauLectureNumero() {
        return (String) get(NIVEAU_LECTURE_NUMERO);
    }

    /**
     * @param niveauLecturenumero the niveauLecturenumero to set
     */
    @Override
	public void setNiveauLectureNumero(String niveauLecturenumero) {
        put(NIVEAU_LECTURE_NUMERO, niveauLecturenumero);
    }

    /**
     * @return the emetteur
     */
    @Override
	public String getEmetteur() {
        return (String) get(EMETTEUR);
    }

    /**
     * @param emetteur the emetteur to set
     */
    @Override
	public void setEmetteur(String emetteur) {
        put(EMETTEUR, emetteur);
    }

    /**
     * @return the destinataire
     */
    @Override
	public String getDestinataire() {
        return (String) get(DESTINATAIRE);
    }

    /**
     * @param destinataire the destinataire to set
     */
    @Override
	public void setDestinataire(String destinataire) {
        put(DESTINATAIRE, destinataire);
    }

    /**
     * @return the copie
     */
    @Override
	public String getCopie() {
        return (String) get(COPIE);
    }

    /**
     * @param copie the copie to set
     */
    @Override
	public void setCopie(String copie) {
        put(COPIE, copie);
    }

    /**
     * @return the evenement
     */
    @Override
	public String getEvenement() {
        return (String) get(EVENEMENT);
    }

    /**
     * @param evenement the evenement to set
     */
    @Override
	public void setEvenement(String evenement) {
        put(EVENEMENT, evenement);
    }

    /**
     * @return the date
     */
    @Override
	public Date getDate() {
        return (Date) get(DATE);
    }

    /**
     * @param date the date to set
     */
    @Override
	public void setDate(Date date) {
        put(DATE, date);
    }

    /**
     * @return the idEvenement
     */
    @Override
	public String getIdEvenement() {
        return (String) get(ID_EVENEMENT);
    }

    /**
     * @param idEvenement the idEvenement to set
     */
    @Override
	public void setIdEvenement(String idEvenement) {
        put(ID_EVENEMENT, idEvenement);
    }

    /**
     * @return the uidEvenement
     */
    @Override
	public String getUidEvenement() {
        return (String) get(UID_EVENEMENT);
    }

    /**
     * @param uidEvenement the uidEvenement to set
     */
    @Override
	public void setUidEvenement(String uidEvenement) {
        put(UID_EVENEMENT, uidEvenement);
    }
    
    /**
     * @return the idMessage
     */
    @Override
	public String getIdMessage() {
        return (String) get(ID_MESSAGE);
    }

    /**
     * @param idMessage the idMessage to set
     */
    @Override
	public void setIdMessage(String idMessage) {
        put(ID_MESSAGE, idMessage);
    }
    
    @Override
    public String getType() {
        return "Message";
    }

    @Override
    public String getDocIdForSelection() {
        return getUidEvenement();
    }

    @Override
    public boolean isPieceJointe() {
        return (Boolean) get(PIECE_JOINTE);
    }

    @Override
    public void setPieceJointe(boolean pieceJointe) {
        put(PIECE_JOINTE, pieceJointe);
    }

    @Override
    public void setEtatEvenement(String etatEvenement) {
        put(ETAT_EVENEMENT, etatEvenement);
    }

    @Override
    public String getEtatEvenement() {
        return (String) get(ETAT_EVENEMENT);
    }
    
    @Override
    public void setEtatMessage(String etatMessage) {
        put(ETAT_MESSAGE, etatMessage);
    }

    @Override
    public String getEtatMessage() {
        return (String) get(ETAT_MESSAGE);
    }
    
    @Override
    public void setEtatDossier(String etatDossier) {
        put(ETAT_DOSSIER, etatDossier);
    }

    @Override
    public String getEtatDossier() {
        return (String) get(ETAT_DOSSIER);
    }
    
    @Override
    public String getNiveauLecture() {
        return (String) get(NIVEAU_LECTURE);
    }

    @Override
    public void setNiveauLecture(String niveauLecture) {
        put(NIVEAU_LECTURE, niveauLecture); 
    }

    @Override
    public boolean isTypeEmetteur() {
        return (Boolean) get(TYPE_EMETTEUR);
    }

    @Override
    public void setTypeEmetteur(boolean typeEmetteur) {
        put(TYPE_EMETTEUR, typeEmetteur); 
    }

    @Override
    public void setLocker(String locker) {
        put(LOCKER, locker);
    }

    @Override
    public String getLocker() {
        return (String) get(LOCKER);
    }

    @Override
    public void setLockTime(String lockTime) {
        put(LOCK_TIME, lockTime);
    }

    @Override
    public String getLockTime() {
        return (String) get(LOCK_TIME);
    }

    @Override
    public boolean isAlerte() {
        return (Boolean)get(ALERTE);
    }

    @Override
    public void setAlerte(boolean alerte) {
        put(ALERTE, alerte);
    }

    @Override
    public void setModeCreationVersion(String modeCreationVersion) {
        put(MODE_CREATION_VERSION, modeCreationVersion);
    }

    @Override
    public String getModeCreationVersion() {
        return (String) get(MODE_CREATION_VERSION);
    }

    @Override
    public void setNumeroVersion(String numeroVersion) {
        put(NUMERO_VERSION, numeroVersion);
    }

    @Override
    public String getNumeroVersion() {
        return (String) get(NUMERO_VERSION);
    }

    @Override
    public String getIdSenat() {
        return (String) get(ID_SENAT);
    }

    @Override
    public void setIdSenat(String idSenat) {
        put(ID_SENAT, idSenat);
    }    
}
