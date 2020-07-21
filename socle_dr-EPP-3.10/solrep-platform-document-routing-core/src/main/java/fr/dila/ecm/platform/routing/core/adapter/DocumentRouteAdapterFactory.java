/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     arussel
 */
package fr.dila.ecm.platform.routing.core.adapter;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants.ExecutionTypeValues;
import fr.dila.ecm.platform.routing.core.impl.DocumentRouteElementImpl;
import fr.dila.ecm.platform.routing.core.impl.DocumentRouteImpl;
import fr.dila.ecm.platform.routing.core.impl.DocumentRouteStepsContainerImpl;
import fr.dila.ecm.platform.routing.core.impl.ParallelRunner;
import fr.dila.ecm.platform.routing.core.impl.SerialRunner;
import fr.dila.ecm.platform.routing.core.impl.StepElementRunner;

/**
 * Provides {@link DocumentRoute} for a {@link DocumentModel}.
 *
 * @author arussel
 *
 */
public class DocumentRouteAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc,
            @SuppressWarnings("rawtypes") Class itf) {
        String type = doc.getType();
        if (doc.hasFacet(DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_FACET)) {
            ExecutionTypeValues executionType = getExecutionType(doc, type);
            switch (executionType) {
            case serial:
                return new DocumentRouteImpl(doc, new SerialRunner());
            case parallel:
                return new DocumentRouteImpl(doc, new ParallelRunner());
            }
        } else if (doc.hasFacet(DocumentRoutingConstants.ROUTE_STEP_FACET)) {
            return new DocumentRouteElementImpl(doc, new StepElementRunner());
        } else if (DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE.equalsIgnoreCase(type)) {
            ExecutionTypeValues executionType = getExecutionType(doc, type);
            switch (executionType) {
            case serial:
                return new DocumentRouteStepsContainerImpl(doc,
                        new SerialRunner());
            case parallel:
                return new DocumentRouteStepsContainerImpl(doc,
                        new ParallelRunner());
            }
        }
        return null;
    }

    protected ExecutionTypeValues getExecutionType(DocumentModel doc,
            String type) {
        ExecutionTypeValues executionType = ExecutionTypeValues.valueOf((String) getProperty(
                doc, DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME));
        return executionType;
    }

    protected Object getProperty(DocumentModel doc, String xpath) {
        try {
            return doc.getPropertyValue(xpath);
        } catch (ClientException e) {
            throw new ClientRuntimeException(e);
        }
    }

}
