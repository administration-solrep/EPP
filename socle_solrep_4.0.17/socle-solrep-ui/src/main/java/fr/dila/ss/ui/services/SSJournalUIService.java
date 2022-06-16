package fr.dila.ss.ui.services;

import static fr.dila.st.api.constant.STEventConstant.CATEGORY_BORDEREAU;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_FDD;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_FEUILLE_ROUTE;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_JOURNAL;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_PARAPHEUR;

import com.google.common.collect.ImmutableSet;
import fr.dila.ss.ui.bean.JournalDossierResultList;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Set;

public interface SSJournalUIService<T extends JournalDossierResultList> {
    T getJournalDTO(SpecificContext context);

    default Set<String> getCategoryList() {
        return ImmutableSet.of(
            CATEGORY_FEUILLE_ROUTE,
            CATEGORY_BORDEREAU,
            CATEGORY_FDD,
            CATEGORY_PARAPHEUR,
            CATEGORY_JOURNAL
        );
    }

    /**
     * Renvoie un SelectValueDTO représentant la catégorie du journal passée en paramètre.
     */
    default SelectValueDTO getCategorySelectValueDTO(String category) {
        return new SelectValueDTO(category, getCategoryLabel(category));
    }

    /**
     * Renvoie le label associé à la catégorie.
     * Format du label par défaut : key = journal.categorie.label.$category$
     */
    default String getCategoryLabel(String category) {
        return ResourceHelper.getString(String.format("journal.categorie.label.%s", category));
    }
}
