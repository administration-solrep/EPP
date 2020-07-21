package fr.dila.solonepp.core.assembler.ws;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.solon.epp.NatureRapport;

/**
 * Assembleur des données nature rapport vocabulaire <-> WS.
 * 
 * @author jtremeaux
 */
public class NatureRapportAssembler {
    
    /**
     * Assemble l'objet web service -> vocabulaire.
     * 
     * @param wsId Objet web service
     * @return Identifiant technique de l'enregistrement du vocabulaire
     * @throws ClientException
     */
    public static String assembleXsdToNatureRapport(NatureRapport wsId) throws ClientException {
        if (wsId == null) {
            return null;
        }
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        String id = wsId.name();
        if (vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY, id)) {
            return id;
        } else {
            throw new ClientException("Nature de rapport inconnue : " + wsId);
        }
    }
    
    /**
     * Assemble le vocabulaire -> web service.
     * 
     * @param id Identifiant technique de l'enregistrement du vocabulaire
     * @return Objet web service
     * @throws ClientException
     */
    public static NatureRapport assembleNatureRapportToXsd(String id) throws ClientException {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        try {
            NatureRapport wsId = NatureRapport.valueOf(id);
            return wsId;
        } catch (IllegalArgumentException e) {
            throw new ClientException("Nature de rapport inconnue : " + id);
        }
    }
}
