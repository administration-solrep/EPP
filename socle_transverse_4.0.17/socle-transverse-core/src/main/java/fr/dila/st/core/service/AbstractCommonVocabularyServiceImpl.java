package fr.dila.st.core.service;

import static fr.dila.st.api.constant.STVocabularyConstants.COLUMN_LABEL;
import static fr.dila.st.core.service.STServiceLocator.getVocabularyService;
import static java.util.stream.Collectors.toList;

import fr.dila.st.api.service.AbstractCommonVocabularyService;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;

public abstract class AbstractCommonVocabularyServiceImpl<T extends Comparable<T>>
    implements AbstractCommonVocabularyService<T> {
    public static final String ID_PROPERTY = "id";
    public static final String LABEL_PROPERTY = "label";

    private static final String ERROR_MESSAGE = "Une erreur s'est produite lors de la récupération des %s";

    private final String vocabularyDirectoryName;
    private final String schemaName;
    private final String entryLabelErrorMessage;

    protected AbstractCommonVocabularyServiceImpl(
        String vocabularyDirectoryName,
        String schemaName,
        String entryLabelErrorMessage
    ) {
        this.vocabularyDirectoryName = vocabularyDirectoryName;
        this.schemaName = schemaName;
        this.entryLabelErrorMessage = entryLabelErrorMessage;
    }

    @Override
    public List<ImmutablePair<T, String>> getEntries() {
        return Optional
            .ofNullable(STServiceLocator.getVocabularyService().getAllEntry(vocabularyDirectoryName))
            .map(this::convertToEntries)
            .orElseThrow(() -> new NuxeoException(String.format(ERROR_MESSAGE, entryLabelErrorMessage)));
    }

    @Override
    public Optional<ImmutablePair<T, String>> getEntry(T id) {
        return Optional
            .ofNullable(id)
            .map(i -> STServiceLocator.getVocabularyService().getEntry(vocabularyDirectoryName, idAsString(i)))
            .map(this::createEntry);
    }

    private List<ImmutablePair<T, String>> convertToEntries(DocumentModelList docs) {
        return convertToEntries(docs.stream());
    }

    protected List<ImmutablePair<T, String>> getFilteredEntries(Predicate<DocumentModel> filter) {
        return Optional
            .ofNullable(STServiceLocator.getVocabularyService().getAllEntry(vocabularyDirectoryName))
            .map(entries -> convertToEntriesWithFilter(entries, filter))
            .orElseThrow(() -> new NuxeoException(String.format(ERROR_MESSAGE, entryLabelErrorMessage)));
    }

    @Override
    public List<String> getFilteredEntries(String labelPrefix) {
        return getVocabularyService().getSuggestions(labelPrefix, vocabularyDirectoryName);
    }

    protected List<String> getFilteredSuggestions(String input, Predicate<DocumentModel> filter) {
        return getFilteredDocumentModels(input)
            .stream()
            .filter(filter)
            .map(doc -> (String) doc.getProperty(schemaName, COLUMN_LABEL))
            .collect(Collectors.toList());
    }

    private List<DocumentModel> getFilteredDocumentModels(String labelPrefix) {
        return getVocabularyService().getListDocumentModelSuggestions(labelPrefix, vocabularyDirectoryName);
    }

    private List<ImmutablePair<T, String>> convertToEntriesWithFilter(
        DocumentModelList docs,
        Predicate<DocumentModel> filter
    ) {
        return convertToEntries(docs.stream().filter(filter));
    }

    protected List<ImmutablePair<T, String>> convertToEntries(Stream<DocumentModel> streamEntries) {
        return streamEntries.map(this::createEntry).sorted(getComparator()).collect(toList());
    }

    protected Comparator<ImmutablePair<T, String>> getComparator() {
        return Comparator.comparing(ImmutablePair::getLeft);
    }

    protected ImmutablePair<T, String> createEntry(DocumentModel doc) {
        return ImmutablePair.of(getId(doc), getLabel(doc));
    }

    protected abstract T getId(DocumentModel doc);

    protected String getDefaultId(DocumentModel doc) {
        return getProperty(doc, ID_PROPERTY);
    }

    protected String getLabel(DocumentModel doc) {
        return getProperty(doc, LABEL_PROPERTY);
    }

    private String getProperty(DocumentModel doc, String propertyName) {
        return (String) doc.getProperty(schemaName, propertyName);
    }
}
