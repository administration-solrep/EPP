package fr.dila.st.core.ufnxql.functions;

import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.doQuery;
import static fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils.ufnxqlToFnxqlQuery;

import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

@RunWith(FeaturesRunner.class)
@Features({ TransactionalFeature.class, CoreFeature.class })
@Deploy("fr.sword.naiad.nuxeo.ufnxql.core")
@Deploy("fr.dila.st.core:OSGI-INF/st-fnxqlconfig-contrib.xml")
public class RegexpFunctionIT {
    @Inject
    private CoreSession session;

    @Test
    public void testRegexpFunction() {
        DocumentModel doc = session.createDocumentModel("/", "myDoc", "File");
        DublincorePropertyUtil.setTitle(doc, "Toto123");
        doc = session.createDocument(doc);

        session.save();

        DocumentModelList dml = doQuery(
            session,
            ufnxqlToFnxqlQuery("SELECT f.ecm:uuid AS id FROM File AS f WHERE regexpLike(f.dc:title, '^Tot[0-9]$') = 1"),
            -1,
            0
        );
        Assertions.assertThat(dml).hasSize(1);
    }
}
