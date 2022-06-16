package fr.dila.ss.core.status;

import static org.apache.commons.lang3.StringUtils.SPACE;

import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.status.DefaultCommandStatusInfo;

public class BirtStatus extends DefaultCommandStatusInfo {

    @Override
    protected String getTestCommand() {
        ConfigService configService = STServiceLocator.getConfigService();
        return new StringBuilder("java -cp ")
            .append(configService.getValue(SSBirtConstants.BIRT_APP_CLASSPATH_PROP))
            .append(SPACE)
            .append(configService.getValue(SSBirtConstants.BIRT_APP_MAIN_CLASS_PROP))
            .append(" version")
            .toString();
    }
}
