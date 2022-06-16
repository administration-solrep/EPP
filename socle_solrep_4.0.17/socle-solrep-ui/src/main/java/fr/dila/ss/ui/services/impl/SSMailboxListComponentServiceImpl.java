package fr.dila.ss.ui.services.impl;

import static fr.dila.st.core.service.STServiceLocator.getSTPostesService;
import static fr.dila.st.core.service.STServiceLocator.getSTUserService;

import fr.dila.ss.ui.services.SSMailboxListComponentService;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.services.impl.FragmentServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;

public class SSMailboxListComponentServiceImpl extends FragmentServiceImpl implements SSMailboxListComponentService {
    public static final String REFRESH_CORBEILLE_KEY = "refreshCorbeille";
    public static final String MODE_TRI_KEY = "modeTriVal";
    public static final String SELECTION_VALIDEE_KEY = "selectionValideeBool";
    public static final String POSTE_KEY = "selectionPosteVal";
    public static final String SELECTED_POSTE_KEY = "selectionPoste";
    public static final String USER_KEY = "selectionUserVal";
    public static final String SELECTED_USER_KEY = "selectionUser";
    public static final String POSTE_LABEL_KEY = "selectionPosteLabel";
    public static final String USER_LABEL_KEY = "selectionUserLabel";
    public static final String MASQUER_CORBEILLES_KEY = "masquerCorbeillesBool";
    public static final String SELECTED_KEY = "mailboxListSelected";
    public static final String ACTIVE_KEY = "mailboxListActivated";
    public static final String DISPLAY_CORBEILLE_SELECTION_KEY = "displayCorbeilleSelection";

    protected static final String SESSION_KEY = "mailboxList";
    protected static final String JS_ACTION = "onClickMailboxItem";
    protected static final String LINK_URL_MIN = "/travail/listeMin";
    protected static final String LINK_URL_POSTE = "/travail/listePoste";
    protected static final String LINK_URL_SIGNAL = "/travail/listeSignal";

    protected static final String LINK_URL_TYPE_ETAPE = "/travail/listeTypeEtape";
    protected static final String LINK_URL_TYPE_ACTE = "/travail/listeTypeActe";

    public TreeElementDTO searchInTreeElement(List<? extends TreeElementDTO> liste, String searchKey) {
        TreeElementDTO returnElement = null;
        // Recherche récursive d'un item dans l'arbre via la clé
        for (TreeElementDTO element : liste) {
            if (searchKey.equals(element.getCompleteKey())) {
                return element;
            } else {
                returnElement = searchInTreeElement(element.getChilds(), searchKey);
                if (returnElement != null) {
                    break;
                }
            }
        }
        return returnElement;
    }

    protected String getPosteLabel(String posteId) {
        return getSTPostesService().getPosteLabel(posteId);
    }

    protected String getUserLabel(String username) {
        return getSTUserService().getUserFullNameOrEmpty(username);
    }

    // must be ovverrided
    @Override
    public Map<String, Object> getData(SpecificContext context) {
        return null;
    }
}
