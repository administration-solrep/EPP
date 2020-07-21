package fr.dila.solonepp.api.service.evenement;


/**
 * Données de la requête pour initilaisaser l'evenement suivant.
 * 
 * @author asatre
 */
public class InitialiserEvenementRequest {
	
    /**
     * Identifiant technique de l'événement precedente
     */
    private String idEvenementPrecedent;
    
    /**
     * Type d'evenement a initialiser.
     */
    private String typeEvenement;

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("InitialiserEvenementRequest: ");
        sb.append("[ idEvenementPrecedent : ");
        sb.append(idEvenementPrecedent);
        sb.append(", typeEvenement : ");
        sb.append(typeEvenement);
        sb.append(" ]");
        return sb.toString();
    }


	public String getIdEvenementPrecedent() {
		return idEvenementPrecedent;
	}


	public void setIdEvenementPrecedent(String idEvenementPrecedent) {
		this.idEvenementPrecedent = idEvenementPrecedent;
	}


	public String getTypeEvenement() {
		return typeEvenement;
	}


	public void setTypeEvenement(String typeEvenement) {
		this.typeEvenement = typeEvenement;
	}
}
