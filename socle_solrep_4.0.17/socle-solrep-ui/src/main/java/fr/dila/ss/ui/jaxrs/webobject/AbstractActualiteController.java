package fr.dila.ss.ui.jaxrs.webobject;

import fr.dila.ss.ui.bean.actualites.ActualiteRechercheForm;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.function.Supplier;

public abstract class AbstractActualiteController extends SolonWebObject {

    /**
     * Initialise le formulaire de recherche des actualités et le met dans le contexte.
     */

    protected <T extends ActualiteRechercheForm> T putActualiteRechercheFormInContext(
        T actualiteRechercheForm,
        SpecificContext context,
        Supplier<T> supplier
    ) {
        T rechercheForm = ObjectHelper.requireNonNullElseGet(actualiteRechercheForm, supplier);

        context.putInContextData(SSContextDataKey.ACTUALITE_RECHERCHE_FORM, rechercheForm);

        return rechercheForm;
    }
}
