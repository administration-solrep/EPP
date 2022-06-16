package fr.dila.st.ui.services;

import org.nuxeo.ecm.webengine.model.WebContext;

public interface CSRFService {
    String generateToken(WebContext ctx);
}
