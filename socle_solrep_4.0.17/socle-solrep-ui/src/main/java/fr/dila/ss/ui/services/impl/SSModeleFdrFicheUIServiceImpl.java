package fr.dila.ss.ui.services.impl;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.ui.services.SSModeleFdrFicheUIService;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSModeleFdrFicheUIServiceImpl
    extends SSAbstractModeleFdrFicheUIService<ModeleFdrForm, SSFeuilleRoute>
    implements SSModeleFdrFicheUIService {

    @Override
    public List<SelectValueDTO> getTypeEtapeRecherche() {
        return getTypeEtape()
            .entrySet()
            .stream()
            .map(entry -> new SelectValueDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getTypeEtape() {
        return STServiceLocator
            .getVocabularyService()
            .getAllEntry(STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY)
            .stream()
            .collect(
                Collectors.toMap(
                    DocumentModel::getId,
                    vocDoc ->
                        PropertyUtil.getStringProperty(
                            vocDoc,
                            STVocabularyConstants.VOCABULARY,
                            STVocabularyConstants.COLUMN_LABEL
                        )
                )
            );
    }
}
