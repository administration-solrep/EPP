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
 *     Alexandre Russel
 */
package fr.dila.cm.operation;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.st.api.caselink.STDossierLink;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
@Operation(
    id = DistributionOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "Distribute a case",
    description = "Distribute a case according to the CaseLink found in the context."
)
public class DistributionOperation {
    public static final String ID = "Case.Management.Distribution";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void distribute() {
        CoreSession session = context.getCoreSession();
        final CaseDistributionService caseDistributionService = ServiceUtil.getRequiredService(
            CaseDistributionService.class
        );
        @SuppressWarnings("unchecked")
        List<STDossierLink> caseLinks = (List<STDossierLink>) context.get(CaseConstants.OPERATION_CASE_LINKS_KEY);
        for (STDossierLink caseLink : caseLinks) {
            caseDistributionService.sendCase(session, caseLink);
        }
    }
}
