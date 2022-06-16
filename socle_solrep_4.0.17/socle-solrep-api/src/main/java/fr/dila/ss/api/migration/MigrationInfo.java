package fr.dila.ss.api.migration;

import fr.dila.ss.api.constant.SSConstant;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class MigrationInfo implements Serializable {
    private String id;

    private int ordre;

    private Long loggerId;

    public static final int MIGRATION_ID_LENGTH = 6;
    private static final String MIGRATION_TITLE = "Migration n°";

    /**
     * Champ correspondant au type de noeud organigramme à migrer : par défaut
     */
    protected String typeMigration = SSConstant.POSTE_TYPE;

    /**
     * Champ correspondant au l'ancien noeud organigramme que l'on va migrer
     */
    private String oldElementOrganigramme;

    /**
     * Champ correspondant au nouveau noeud organigramme où l'on migrer les éléments de l'ancien noeud
     */
    private String newElementOrganigramme;

    /**
     * Champ correspondant au l'ancien ministère du noeud organigramme que l'on va migrer (utilisé si le type de noeud est un direction)
     */
    private String oldMinistereElementOrganigramme;

    /**
     * Champ correspondant au nouveau ministère noeud organigramme où l'on migrer les éléments de l'ancien noeud (utilisé si le type de noeud est un direction)
     */
    private String newMinistereElementOrganigramme;

    // supprimer l'ancien element ou non à la fin de la migration
    private Boolean deleteOldElementOrganigramme = false;

    // migrer les dossiers clos au niveau des ministères ou des directions
    private Boolean migrationWithDossierClos = true;

    // migrer les modeles de feuille de route
    private Boolean migrationModeleFdr = true;

    public MigrationInfo(String id) {
        this.id = id;
    }

    /**
     * @return message d'erreur code
     */
    public String validateMigration() {
        String errorMessage = this.checkElementOrganigrammePresent();
        if (errorMessage == null) {
            errorMessage = this.checkElementOrganigrammeNotEqual();
        }
        return errorMessage;
    }

    protected String checkElementOrganigrammePresent() {
        boolean isOrganigrammeElementEmpty =
            StringUtils.isEmpty(oldElementOrganigramme) || StringUtils.isEmpty(newElementOrganigramme);
        boolean isMinistereElementEmpty =
            typeMigration.equals(SSConstant.DIR_TYPE) &&
            (
                StringUtils.isEmpty(oldMinistereElementOrganigramme) ||
                StringUtils.isEmpty(newMinistereElementOrganigramme)
            );
        if (isOrganigrammeElementEmpty || isMinistereElementEmpty) {
            return "changement.gouvernement.message.vide." + typeMigration;
        }
        return null;
    }

    protected String checkElementOrganigrammeNotEqual() {
        if (oldElementOrganigramme.equals(newElementOrganigramme)) {
            boolean isValide =
                typeMigration.equals(SSConstant.DIR_TYPE) &&
                !oldMinistereElementOrganigramme.equals(newMinistereElementOrganigramme);
            if (!isValide) {
                return "changement.gouvernement.message.element.identique." + typeMigration;
            }
        }
        return null;
    }

    public String getTypeMigration() {
        return typeMigration;
    }

    public void setTypeMigration(String typeMigration) {
        this.typeMigration = typeMigration;
        oldElementOrganigramme = null;
        newElementOrganigramme = null;
    }

    public String getOldElementOrganigramme() {
        return oldElementOrganigramme;
    }

    public void setOldElementOrganigramme(String oldElementOrganigramme) {
        this.oldElementOrganigramme = oldElementOrganigramme;
    }

    public String getNewElementOrganigramme() {
        return newElementOrganigramme;
    }

    public void setNewElementOrganigramme(String newElementOrganigramme) {
        this.newElementOrganigramme = newElementOrganigramme;
    }

    public String getOldMinistereElementOrganigramme() {
        return oldMinistereElementOrganigramme;
    }

    public void setOldMinistereElementOrganigramme(String oldMinistereElementOrganigramme) {
        this.oldMinistereElementOrganigramme = oldMinistereElementOrganigramme;
    }

    public String getNewMinistereElementOrganigramme() {
        return newMinistereElementOrganigramme;
    }

    public void setNewMinistereElementOrganigramme(String newMinistereElementOrganigramme) {
        this.newMinistereElementOrganigramme = newMinistereElementOrganigramme;
    }

    public Boolean getDeleteOldElementOrganigramme() {
        return deleteOldElementOrganigramme;
    }

    public void setDeleteOldElementOrganigramme(Boolean deleteOldElementOrganigramme) {
        this.deleteOldElementOrganigramme = deleteOldElementOrganigramme;
    }

    public Boolean getMigrationWithDossierClos() {
        return migrationWithDossierClos;
    }

    public void setMigrationWithDossierClos(Boolean migrationWithDossierClos) {
        this.migrationWithDossierClos = migrationWithDossierClos;
    }

    public void resetElementOrganigramme() {
        oldElementOrganigramme = null;
        newElementOrganigramme = null;
        oldMinistereElementOrganigramme = null;
        newMinistereElementOrganigramme = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return MIGRATION_TITLE + this.getOrdre();
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setLoggerId(Long loggerId) {
        this.loggerId = loggerId;
    }

    public Long getLoggerId() {
        return loggerId;
    }

    public void assignLoggerInfos(MigrationLoggerModel model) {
        model.setDeleteOldValue(getDeleteOldElementOrganigramme());
        model.setOldElement(getOldElementOrganigramme());
        model.setNewElement(getNewElementOrganigramme());
        model.setOldMinistere(getOldMinistereElementOrganigramme());
        model.setNewMinistere(getNewMinistereElementOrganigramme());
        model.setTypeMigration(getTypeMigration());
        model.setElementsFils(0);
        model.setMigrationModeleFdr(getMigrationModeleFdr());
        model.setMigrationWithDossierClos(getMigrationWithDossierClos());
    }

    public Boolean getMigrationModeleFdr() {
        return migrationModeleFdr;
    }

    public void setMigrationModeleFdr(Boolean migrationModeleFdr) {
        this.migrationModeleFdr = migrationModeleFdr;
    }
}
