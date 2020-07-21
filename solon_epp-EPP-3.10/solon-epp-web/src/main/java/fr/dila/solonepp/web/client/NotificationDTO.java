package fr.dila.solonepp.web.client;

/**
 * DTO utilisé lors de l'appel régulier au serveur pour alerter
 * l'utilisateur d'une modification de sa corbeille/communication courante
 * 
 * @author bgamard
 */
public class NotificationDTO {
    
    private boolean isCorbeilleModified = false;
    
    private boolean isEvenementModified = false;
    
    public boolean isEvenementModified() {
        return isEvenementModified;
    }

    public void setEvenementModified(boolean isEvenementModified) {
        this.isEvenementModified = isEvenementModified;
    }

    public boolean isCorbeilleModified() {
        return isCorbeilleModified;
    }

    public void setCorbeilleModified(boolean isCorbeilleModified) {
        this.isCorbeilleModified = isCorbeilleModified;
    }
}
