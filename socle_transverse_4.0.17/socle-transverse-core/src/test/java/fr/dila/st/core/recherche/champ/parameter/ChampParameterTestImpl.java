package fr.dila.st.core.recherche.champ.parameter;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.core.requete.recherchechamp.ChampParameter;
import java.io.Serializable;
import java.util.Map;

public class ChampParameterTestImpl implements ChampParameter {

    @Override
    public Map<String, Serializable> getAdditionalParameters() {
        return ImmutableMap.of("param1", "val1", "param2", "val2");
    }
}
