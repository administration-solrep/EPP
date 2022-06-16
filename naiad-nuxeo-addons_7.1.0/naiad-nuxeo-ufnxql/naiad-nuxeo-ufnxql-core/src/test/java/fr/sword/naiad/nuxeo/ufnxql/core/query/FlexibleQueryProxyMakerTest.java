package fr.sword.naiad.nuxeo.ufnxql.core.query;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;

@RunWith(FeaturesRunner.class)
@RepositoryConfig(init = FlexibleQueryMakerRepositoryInit.class, cleanup = Granularity.METHOD)
@Features(FlexibleQueryMakerFeature.class)
public class FlexibleQueryProxyMakerTest {

    @Inject
    protected CoreSession session;

    public static final String DOC_TYPE = "File";

    @Test
    public void testProxy() throws NuxeoException {
        final int MAX = 5;
        final List<String> expectedNoteIds = new ArrayList<String>();
        final List<String> expectedProxyNoteIds = new ArrayList<String>();

        for (int i = 0; i < MAX; ++i) {
            final String name = String.format("name-%d", i);

            final String title = name + "-1";
            DocumentModel note = session.createDocumentModel("/", title, "MaNote");
            DublincorePropertyUtil.setTitle(note, title);
            note = session.createDocument(note);

            expectedNoteIds.add(note.getId());

            final DocumentModel noteProxy = session.createProxy(note.getRef(), new PathRef("/"));

            expectedProxyNoteIds.add(noteProxy.getId());

        }

        session.save();

        // default : no proxy
        {
            final String query = "select d.ecm:uuid AS id From MaNote as d order by d.ecm:name ASC";
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            Assert.assertEquals(expectedNoteIds.size(), ids.size());
            for (int i = 0; i < ids.size(); i++) {
                Assert.assertEquals(expectedNoteIds.get(i), ids.get(i));
            }

        }

        // only proxy
        {
            final String query = "select p.ecm:uuid AS id From Proxy as p, MaNote as d where p.ecm:proxyTargetId = d.ecm:uuid order by d.ecm:name ASC";
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            Assert.assertEquals(expectedProxyNoteIds.size(), ids.size());
            for (int i = 0; i < ids.size(); i++) {
                Assert.assertEquals(expectedProxyNoteIds.get(i), ids.get(i));
            }
        }

        // all
        {
            final String query = "select a.id AS id, a.na AS na FROM ((select d.ecm:uuid AS id, d.ecm:name AS na From MaNote as d ) "
                    + "UNION (select p.ecm:uuid AS id, d.ecm:name AS na From Proxy as p, Note as d where p.ecm:proxyTargetId = d.ecm:uuid )) "
                    + "AS a ORDER BY a.na asc";
            final List<String> ids = QueryUtils.doUFNXQLQueryForIdsList(session, query, null);
            Assert.assertEquals(expectedNoteIds.size() + expectedProxyNoteIds.size(), ids.size());
            for (int i = 0; i < ids.size(); i++) {
                Assert.assertTrue(expectedNoteIds.contains(ids.get(i)) || expectedProxyNoteIds.contains(ids.get(i)));
            }
        }

        session.query("Select * from MaNote");
    }

}
