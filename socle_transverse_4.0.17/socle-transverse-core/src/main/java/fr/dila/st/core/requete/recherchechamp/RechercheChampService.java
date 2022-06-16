package fr.dila.st.core.requete.recherchechamp;

import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import java.util.List;

public interface RechercheChampService {
    List<ChampDescriptor> getChamps(String contribName);

    ChampDescriptor getChamp(String contribName, String name);
}
