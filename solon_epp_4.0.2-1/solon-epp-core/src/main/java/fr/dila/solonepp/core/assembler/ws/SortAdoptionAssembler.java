package fr.dila.solonepp.core.assembler.ws;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.solon.epp.SortAdoption;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Assembleur des données sort adoption vocabulaire <-> WS.
 *
 * @author jtremeaux
 */
public class SortAdoptionAssembler {

    /**
     * Assemble l'objet web service -> vocabulaire.
     *
     * @param wsId Objet web service
     * @return Identifiant technique de l'enregistrement du vocabulaire
     */
    public static String assembleXsdToSortAdoption(SortAdoption wsId) {
        if (wsId == null) {
            return null;
        }
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        String id = wsId.name();
        if (vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY, id)) {
            return id;
        } else {
            throw new NuxeoException("Sort d'adoption inconnu : " + wsId);
        }
    }

    /**
     * Assemble le vocabulaire -> web service.
     *
     * @param id Identifiant technique de l'enregistrement du vocabulaire
     * @return Objet web service
     */
    public static SortAdoption assembleSortAdoptionToXsd(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        try {
            SortAdoption wsId = SortAdoption.valueOf(id);
            return wsId;
        } catch (IllegalArgumentException e) {
            throw new NuxeoException("Sort d'adoption inconnu : " + id);
        }
    }
}
