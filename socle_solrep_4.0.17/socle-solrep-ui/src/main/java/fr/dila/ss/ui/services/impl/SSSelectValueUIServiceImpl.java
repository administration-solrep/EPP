package fr.dila.ss.ui.services.impl;

import static java.util.stream.Collectors.toList;

import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.service.AbstractCommonVocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSSelectValueUIServiceImpl implements SSSelectValueUIService {

    @Override
    public List<SelectValueDTO> getRoutingTaskTypes() {
        return getSelectValues(SSServiceLocator.getRoutingTaskTypeService(), Object::toString);
    }

    protected static <T> List<SelectValueDTO> getSelectValues(
        AbstractCommonVocabularyService<T> service,
        Function<T, String> toString
    ) {
        return getSelectValues(service.getEntries(), pair -> toString.apply(pair.getLeft()), ImmutablePair::getRight);
    }

    protected static <T> List<SelectValueDTO> getSelectValues(
        List<T> entries,
        Function<T, String> getId,
        Function<T, String> getLabel
    ) {
        return entries
            .stream()
            .map(entry -> new SelectValueDTO(getId.apply(entry), getLabel.apply(entry)))
            .collect(toList());
    }

    protected static <T> List<SelectValueDTO> getUnarySelectValues(AbstractCommonVocabularyService<T> service) {
        return getUnarySelectValues(service.getEntries(), ImmutablePair::getRight);
    }

    protected static <T> List<SelectValueDTO> getUnarySelectValues(List<T> entries, Function<T, String> getValue) {
        return entries
            .stream()
            .map(entry -> new SelectValueDTO(getValue.apply(entry), getValue.apply(entry)))
            .collect(toList());
    }

    @Override
    public List<SelectValueDTO> getSelectValuesFromVocabulary(String vocabulary) {
        return getSelectValuesFromVocabulary(vocabulary, STVocabularyConstants.VOCABULARY);
    }

    @Override
    public List<SelectValueDTO> getSelectValuesFromVocabulary(String vocabulary, String schema) {
        return getSelectValues(
            getEntries(vocabulary, schema),
            DocumentModel::getId,
            doc -> (String) doc.getProperty(schema, STVocabularyConstants.COLUMN_LABEL)
        );
    }

    @Override
    public List<SelectValueDTO> getUnarySelectValuesFromVocabulary(String vocabulary) {
        return getUnarySelectValues(
            getEntries(vocabulary),
            doc -> (String) doc.getProperty(STVocabularyConstants.VOCABULARY, STVocabularyConstants.COLUMN_LABEL)
        );
    }

    private List<DocumentModel> getEntries(String vocabulary) {
        return getEntries(vocabulary, STVocabularyConstants.VOCABULARY);
    }

    private List<DocumentModel> getEntries(String vocabulary, String schema) {
        return STServiceLocator
            .getVocabularyService()
            .getAllEntry(vocabulary)
            .stream()
            .sorted(Comparator.comparing(doc -> (String) doc.getProperty(schema, STVocabularyConstants.COLUMN_LABEL)))
            .collect(Collectors.toList());
    }

    @Override
    public List<SelectValueDTO> getCurrentMinisteres() {
        return getSelectValues(
            SSUIServiceLocator.getSSOrganigrammeManagerUIService().getSortedCurrentMinisteres(),
            EntiteNode::getId,
            EntiteNode::getLabel
        );
    }
}
