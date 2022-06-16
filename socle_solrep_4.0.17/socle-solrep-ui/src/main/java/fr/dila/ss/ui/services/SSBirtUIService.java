package fr.dila.ss.ui.services;

import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.core.Response;

public interface SSBirtUIService {
    Response generateDoc(SpecificContext context);
}
