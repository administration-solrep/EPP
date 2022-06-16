package fr.dila.st.api.service;

import java.util.List;

public interface STDirectoryService {
    List<String> getSuggestions(String keyword, String directory, String column);
}
