package fr.dila.ss.api.constant;

public class SmartFolderConstant {

    private SmartFolderConstant() {
        // Contant class
    }

    public static final String SMART_FOLDER_SCHEMA_PREFIX = "sf";

    public static final String SMART_FOLDER_PROPERTY_POS = "pos";

    public static final String SMART_FOLDER_PROPERTY_QUERY_PART = "queryPart";

    public static final String SMART_FOLDER_XPATH_POS = SMART_FOLDER_SCHEMA_PREFIX + ":" + SMART_FOLDER_PROPERTY_POS;

    public static final String SMART_FOLDER_XPATH_QUERY_PART =
        SMART_FOLDER_SCHEMA_PREFIX + ":" + SMART_FOLDER_PROPERTY_QUERY_PART;
}
