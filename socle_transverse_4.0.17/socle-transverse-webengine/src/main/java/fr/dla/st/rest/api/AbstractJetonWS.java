package fr.dla.st.rest.api;

import fr.dila.reponses.rest.api.JetonWS;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

public abstract class AbstractJetonWS extends DefaultObject implements JetonWS {

    @Override
    public String parseJeton(String jeton) {
        return Long.toString(Long.parseUnsignedLong(jeton));
    }

    protected String getJeton(String jeton) {
        try {
            return parseJeton(jeton);
        } catch (NumberFormatException e) {
            getLogger().error(String.format("Le jeton [%s] doit être au format numérique", jeton), e);
        }
        return null;
    }

    protected abstract Logger getLogger();
}
