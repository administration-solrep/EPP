package fr.dila.ss.ui.services;

import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.List;

public interface SSSelectValueUIService {
    List<SelectValueDTO> getRoutingTaskTypes();

    List<SelectValueDTO> getSelectValuesFromVocabulary(String vocabulary);

    List<SelectValueDTO> getSelectValuesFromVocabulary(String vocabulary, String schema);

    List<SelectValueDTO> getUnarySelectValuesFromVocabulary(String vocabulary);

    List<SelectValueDTO> getCurrentMinisteres();
}
