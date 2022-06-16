package fr.dila.st.ui.utils;

import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.ThEngineService;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ControllerUtils {

    private ControllerUtils() {
        // Classe non instanciable
    }

    public static String renderHtmlFromTemplate(ThTemplate template) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ThEngineService engineService = STUIServiceLocator.getThEngineService();
        engineService.render(template, baos);
        baos.close();
        return baos.toString();
    }
}
