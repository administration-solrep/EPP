package fr.dila.ss.ui.services.organigramme;

import java.util.Map;

public interface SSMigrationManagerUIService {
    /**
     * @return the newTimbre
     */
    Map<String, String> getNewTimbre();

    /**
     * @param newTimbre the newTimbre to set
     */
    void setNewTimbre(Map<String, String> newTimbre);
}
