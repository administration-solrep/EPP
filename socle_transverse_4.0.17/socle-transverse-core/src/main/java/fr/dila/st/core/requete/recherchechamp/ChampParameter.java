package fr.dila.st.core.requete.recherchechamp;

import java.io.Serializable;
import java.util.Map;

public interface ChampParameter {
    Map<String, Serializable> getAdditionalParameters();
}
