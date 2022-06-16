package fr.dila.ss.api.service;

import fr.dila.st.api.service.STExcelService;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentRef;

public interface SSExcelService extends STExcelService {
    /**
     * Créé un fichier Excel contenant les informations souhaitées pour une liste de dossiers.
     *
     * @param dossiersRefs
     *            la liste des {@link DocumentRef} des dossiers
     * @return fichier Excel sous forme de {@link Consumer} (afin d'envoyer le fichier comme pièce jointe dans un mail).
     */
    Consumer<OutputStream> creationExcelListDossiers(CoreSession session, List<DocumentRef> dossiersRefs);
}
