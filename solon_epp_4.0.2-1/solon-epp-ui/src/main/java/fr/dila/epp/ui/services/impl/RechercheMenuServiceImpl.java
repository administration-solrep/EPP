package fr.dila.epp.ui.services.impl;

import fr.dila.epp.ui.services.RechercheMenuService;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.Menu;
import fr.dila.st.ui.services.impl.FragmentServiceImpl;
import fr.dila.st.ui.services.impl.LeftMenuServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public class RechercheMenuServiceImpl extends FragmentServiceImpl implements RechercheMenuService {

    public RechercheMenuServiceImpl() {
        super();
    }

    private Menu[] buildMyMenu(DocumentModelList lstDocs) {
        List<Menu> lstMenus = new ArrayList<>();

        if (lstDocs != null) {
            for (DocumentModel doc : lstDocs) {
                Menu menu = new Menu(
                    "/recherche/" + doc.getId() + "#main_content",
                    (String) doc.getProperty(STVocabularyConstants.VOCABULARY, STVocabularyConstants.COLUMN_LABEL)
                );
                lstMenus.add(menu);
            }
        }

        return lstMenus.toArray(new Menu[0]);
    }

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        DocumentModelList docs = STServiceLocator
            .getVocabularyService()
            .getAllEntry(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_VOCABULARY);
        Map<String, Object> data = new HashMap<>();
        data.put(LeftMenuServiceImpl.MENU_KEY, buildMyMenu(docs));
        return data;
    }
}
