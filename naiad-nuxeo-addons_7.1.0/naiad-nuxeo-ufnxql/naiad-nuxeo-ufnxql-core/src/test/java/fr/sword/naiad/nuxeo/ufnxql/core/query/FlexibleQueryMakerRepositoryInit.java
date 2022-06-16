package fr.sword.naiad.nuxeo.ufnxql.core.query;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;

public class FlexibleQueryMakerRepositoryInit implements RepositoryInit {
	@Override
	public void populate(CoreSession session) throws NuxeoException {
		session.removeChildren(new PathRef("/"));
		session.save();
	}
}
