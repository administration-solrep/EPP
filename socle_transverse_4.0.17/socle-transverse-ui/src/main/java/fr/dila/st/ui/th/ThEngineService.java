package fr.dila.st.ui.th;

import fr.dila.st.ui.th.model.ThTemplate;
import java.io.OutputStream;

public interface ThEngineService {
    long getDelaiRafraichissement();

    void render(ThTemplate tpl, OutputStream outputStream);
}
