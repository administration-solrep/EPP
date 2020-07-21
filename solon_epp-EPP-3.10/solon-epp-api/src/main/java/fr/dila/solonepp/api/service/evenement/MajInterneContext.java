package fr.dila.solonepp.api.service.evenement;

/**
 * Context de mise a jour du Visa Interne de SOLEX
 * @author asatre
 *
 */
public class MajInterneContext {

    /**
     * Requête d'initialisation d'evenement.
     */
    private MajInterneRequest majInterneRequest;

    /**
     * Reponse d'initialisation d'evenement.
     */
    private MajInterneResponse majInterneResponse;
    
    /**
     * Constructeur de AccuserReceptionContext.
     */
    public MajInterneContext() {
        majInterneRequest = new MajInterneRequest();
        majInterneResponse = new MajInterneResponse();
    }

    public void setMajInterneRequest(MajInterneRequest majVisaInterneRequest) {
        this.majInterneRequest = majVisaInterneRequest;
    }

    public MajInterneRequest getMajInterneRequest() {
        return majInterneRequest;
    }

    public void setMajInterneResponse(MajInterneResponse majVisaInterneResponse) {
        this.majInterneResponse = majVisaInterneResponse;
    }

    public MajInterneResponse getMajInterneResponse() {
        return majInterneResponse;
    }

   
}
