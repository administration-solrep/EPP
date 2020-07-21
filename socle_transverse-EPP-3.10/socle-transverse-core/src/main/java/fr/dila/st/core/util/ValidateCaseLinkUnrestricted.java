/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Laurent Doguin
 */
package fr.dila.st.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLink;

/**
 * Fetch and validate due {@link CaseLink}.
 * 
 * @author <a href="mailto:ldoguin@nuxeo.com">Laurent Doguin</a>
 */
public class ValidateCaseLinkUnrestricted extends UnrestrictedSessionRunner {

	/**
	 * Logger.
	 */
	private static final Log	LOGGER	= LogFactory.getLog(ValidateCaseLinkUnrestricted.class);

	private final CaseLink		caseLink;

	public ValidateCaseLinkUnrestricted(CoreSession session, CaseLink caseLink) {
		super(session);
		this.caseLink = caseLink;
	}

	@Override
	public void run() throws ClientException {
		ActionableCaseLink acl = caseLink.getDocument().getAdapter(ActionableCaseLink.class);
		if (acl.isActionnable() && acl.isTodo()) {
			acl.validate(session);
		} else {
			LOGGER.info("[REPARATION CL] - Case Link présent dans un état autre que 'todo' ; validation esquivée pour éviter 'Unable to follow transition' - caseLinkId : "
					+ acl.getId());
		}
	}

}
