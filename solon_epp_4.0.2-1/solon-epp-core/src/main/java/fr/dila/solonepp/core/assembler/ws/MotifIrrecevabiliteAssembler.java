package fr.dila.solonepp.core.assembler.ws;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.solon.epp.MotifIrrecevabilite;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Assembleur des données type acte vocabulaire <-> WS.
 *
 * @author jtremeaux
 */
public class MotifIrrecevabiliteAssembler {

    /**
     * Assemble l'objet web service -> vocabulaire.
     *
     * @param wsId Objet web service
     * @return Identifiant technique de l'enregistrement du vocabulaire
     */
    public static String assembleXsdToMotifIrrecevabilite(MotifIrrecevabilite wsId) {
        if (wsId == null) {
            return null;
        }
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        String id = wsId.name();
        if (vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY, id)) {
            return id;
        } else {
            throw new NuxeoException("Motif d'irrecevabilité inconnu : " + wsId);
        }
    }

    /**
     * Assemble le vocabulaire -> web service.
     *
     * @param id Identifiant technique de l'enregistrement du vocabulaire
     * @return Objet web service
     */
    public static MotifIrrecevabilite assembleMotifIrrecevabiliteToXsd(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        try {
            MotifIrrecevabilite wsId = MotifIrrecevabilite.valueOf(id);
            return wsId;
        } catch (IllegalArgumentException e) {
            throw new NuxeoException("Motif d'irrecevabilité inconnu : " + id);
        }
    }
}
