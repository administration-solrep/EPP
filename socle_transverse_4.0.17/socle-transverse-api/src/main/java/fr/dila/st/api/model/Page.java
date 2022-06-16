package fr.dila.st.api.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Page<T> {
    private final int index;

    private final int size;

    private final List<T> results;

    private final int resultsCount;

    public Page(Integer index, Integer size, List<T> results, int resultsCount) {
        Objects.requireNonNull(results);

        this.results = Collections.unmodifiableList(results);
        this.resultsCount = resultsCount;
        this.index = index != null ? index : 0;
        this.size = size != null ? size : results.size();
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public List<T> getResults() {
        return results;
    }
}
