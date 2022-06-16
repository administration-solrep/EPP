package fr.dila.st.ui;

import fr.dila.st.core.messages.SolonMessagesProperties;

public class STMessagesPropertiesTest extends SolonMessagesProperties {
    private static final String PROPERTIES_FILE_PATH =
        "src/main/resources/web/nuxeo.war/th-templates/messages/messages.properties";

    @Override
    protected String getPropertiesFilePath() {
        return PROPERTIES_FILE_PATH;
    }
}
