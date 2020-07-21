package fr.dila.solonepp.core.assembler.ws;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.solon.epp.MotifIrrecevabilite;

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
     * @throws ClientException
     */
    public static String assembleXsdToMotifIrrecevabilite(MotifIrrecevabilite wsId) throws ClientException {
        if (wsId == null) {
            return null;
        }
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        String id = wsId.name();
        if (vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY, id)) {
            return id;
        } else {
            throw new ClientException("Motif d'irrecevabilité inconnu : " + wsId);
        }
    }
    
    /**
     * Assemble le vocabulaire -> web service.
     * 
     * @param id Identifiant technique de l'enregistrement du vocabulaire
     * @return Objet web service
     * @throws ClientException
     */
    public static MotifIrrecevabilite assembleMotifIrrecevabiliteToXsd(String id) throws ClientException {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        try {
            MotifIrrecevabilite wsId = MotifIrrecevabilite.valueOf(id);
            return wsId;
        } catch (IllegalArgumentException e) {
            throw new ClientException("Motif d'irrecevabilité inconnu : " + id);
        }
    }
}