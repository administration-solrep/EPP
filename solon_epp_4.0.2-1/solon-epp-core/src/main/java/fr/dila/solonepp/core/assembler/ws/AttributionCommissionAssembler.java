package fr.dila.solonepp.core.assembler.ws;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.solon.epp.AttributionCommission;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Assembleur des données attribution commission vocabulaire <-> WS.
 *
 * @author jtremeaux
 */
public class AttributionCommissionAssembler {

    /**
     * Assemble l'objet web service -> vocabulaire.
     *
     * @param wsId Objet web service
     * @return Identifiant technique de l'enregistrement du vocabulaire
     */
    public static String assembleXsdToAttributionCommission(AttributionCommission wsId) {
        if (wsId == null) {
            return null;
        }
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        String id = wsId.name();
        if (vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.ATTRIBUTION_COMMISSION_VOCABULARY, id)) {
            return id;
        } else {
            throw new NuxeoException("Attribution de commission inconnue : " + wsId);
        }
    }

    /**
     * Assemble le vocabulaire -> web service.
     *
     * @param id Identifiant technique de l'enregistrement du vocabulaire
     * @return Objet web service
     */
    public static AttributionCommission assembleAttributionCommissionToXsd(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        try {
            AttributionCommission wsId = AttributionCommission.valueOf(id);
            return wsId;
        } catch (IllegalArgumentException e) {
            throw new NuxeoException("Attribution de commission inconnue : " + id);
        }
    }
}
