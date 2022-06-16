package fr.dila.ss.api.constant;

/**
 * Liste des fonctions unitaires communes à EPG et Réponses. Ces fonctions déterminent la possibilité de cliquer sur un
 * bouton, afficher un menu, accéder à un document où à une vue...
 *
 * @author tlombard
 */
public class SSBaseFunctionConstant {

    private SSBaseFunctionConstant() {
        // Non instanciable class
    }

    /**
     * Droit d'accès à l'administration de la migration
     */
    public static final String ADMIN_ACCESS_MIGRATION = "AdminAccessMigration";

    /**
     * Nom du groupe "Vigie du SGG" (commun à EPG et REP)
     */
    public static final String VIGIE_DU_SGG_GROUP_NAME = "Vigie du SGG";
}
