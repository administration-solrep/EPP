package fr.dila.epp.ui;

import fr.dila.st.core.messages.SolonMessagesProperties;

public class EppMessagesPropertiesTest extends SolonMessagesProperties {
    private static final String APPLI_PROPERTIES_FILE_PATH =
        "src/main/resources/web/nuxeo.war/th-templates/messages/appli/messages.properties";

    @Override
    protected String getPropertiesFilePath() {
        return APPLI_PROPERTIES_FILE_PATH;
    }
}
