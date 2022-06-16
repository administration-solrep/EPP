package fr.dila.ss.ui.jaxrs.webobject.page.journal;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "JournalTechnique")
public class SsJournalTechnique extends AbstractSsJournal {

    /**
     * @return Object
     * on set les catégories et on délègue vers le controller ajax pour la recherche
     */
    @Path("technique")
    public Object getJournalTechnique() {
        template.setName(getTemplateName());
        Map<String, Object> map = new HashMap<>();
        map.put("categories", getCategories());
        template.setData(map);
        template.setContext(context);
        return newObject(getAjax(), context, template);
    }

    protected String getTemplateName() {
        return "pages/admin/journal/journalTechnique";
    }

    protected String getAjax() {
        return "JournalTechniqueAjax";
    }
}
