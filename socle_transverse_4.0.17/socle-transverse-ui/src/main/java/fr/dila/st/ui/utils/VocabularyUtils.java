package fr.dila.st.ui.utils;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.VocabularyServiceImpl;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public final class VocabularyUtils {

    public static String getLabelFromVocabulary(String directoryName, String entryId) {
        return Optional
            .of(STServiceLocator.getVocabularyService().getEntryLabel(directoryName, entryId))
            .filter(label -> !VocabularyServiceImpl.UNKNOWN_ENTRY.equals(label))
            .orElse(entryId);
    }

    public static String getLabelFromVocabularyWithDefaultEmpty(String directoryName, String entryId) {
        return Optional
            .ofNullable(entryId)
            .map(id -> getLabelFromVocabulary(directoryName, id))
            .orElse(StringUtils.EMPTY);
    }

    private VocabularyUtils() {}
}
