package fr.dila.ss.ui.services.organigramme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Action Service (ex Bean) de gestion des migrations
 */
public class SSMigrationManagerUIServiceImpl implements SSMigrationManagerUIService {
    public static final String NEW_TIMBRE_EMPTY_VALUE = "empty_value";
    public static final String NEW_TIMBRE_UNCHANGED_ENTITY = "unchanged_entity";
    public static final String NEW_TIMBRE_DEACTIVATE_ENTITY = "deactivate_entity";

    // Gestion du changement de timbre
    protected Map<String, String> newTimbre = new HashMap<>();

    protected List<Pair<String, String>> newTimbreList;

    protected List<Pair<String, String>> gouvernementList;

    /**
     * Default constructor
     */
    public SSMigrationManagerUIServiceImpl() {
        // do nothing
    }

    @Override
    public Map<String, String> getNewTimbre() {
        return newTimbre;
    }

    @Override
    public void setNewTimbre(Map<String, String> newTimbre) {
        this.newTimbre = newTimbre;
    }
}
