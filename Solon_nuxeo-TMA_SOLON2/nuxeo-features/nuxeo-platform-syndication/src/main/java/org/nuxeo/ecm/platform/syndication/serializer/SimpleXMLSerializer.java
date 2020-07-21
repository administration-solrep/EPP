/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.nuxeo.ecm.platform.syndication.serializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.QName;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMDocumentFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.syndication.translate.TranslationHelper;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Response;
import org.w3c.dom.Element;

public class SimpleXMLSerializer extends AbstractDocumentModelSerializer
        {

    private final Log log = LogFactory.getLog(SimpleXMLSerializer.class);

    private static final String rootNodeName = "results";

    private static final String docNodeName = "document";

    private static final String rootTaskNodeName = "tasks";

    private static final String taskNodeName = "task";

    private static final String taskCategoryNodeName = "category";

    private static final String translationNodeName = "translation";

    private static final String taskNS = "http://www.nuxeo.org/tasks";

    private static final String taskNSPrefix = "nxt";

    private static final DateFormat DATE_PARSER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    @Override
    public String serialize(ResultSummary summary, DocumentModelList docList,
            List<String> columnsDefinition, HttpServletRequest req)
            throws ClientException {
        if (docList == null) {
            return EMPTY_LIST;
        }

        DOMDocumentFactory domfactory = new DOMDocumentFactory();
        DOMDocument result = (DOMDocument) domfactory.createDocument();

        Element current = result.createElement(rootNodeName);
        result.setRootElement((org.dom4j.Element) current);

        Element pagesElement = result.createElement("pages");
        pagesElement.setAttribute("pages", Integer.toString(summary.getPages()));
        pagesElement.setAttribute("pageNumber",
                Integer.toString(summary.getPageNumber()));
        current.appendChild(pagesElement);

        for (DocumentModel doc : docList) {
            Element el = result.createElement(docNodeName);
            el.setAttribute("id", doc.getId());

            for (String colDef : columnsDefinition) {
                ResultField res = getDocumentProperty(doc, colDef);
                el.setAttribute(res.getName(), res.getValue());
            }
            current.appendChild(el);
        }

        return result.asXML();
    }


    public static Map<String, String> getTranslationsForWorkflow(String lang) {
        Map<String, String> result = new HashMap<String, String>();

        String validation = "workflowDirectiveValidation";
        result.put(validation, TranslationHelper.getLabel(validation, lang));

        String opinion = "workflowDirectiveOpinion";
        result.put(opinion, TranslationHelper.getLabel(opinion, lang));

        String diffusion = "workflowDirectiveDiffusion";
        result.put(diffusion, TranslationHelper.getLabel(diffusion, lang));

        String check = "workflowDirectiveCheck";
        result.put(check, TranslationHelper.getLabel(check, lang));

        String verification = "workflowDirectiveVerification";
        result.put(verification, TranslationHelper.getLabel(verification, lang));

        String dueDate = "label.workflow.task.duedate";
        result.put(dueDate, TranslationHelper.getLabel(dueDate, lang));

        String startDate = "label.workflow.task.startdate";
        result.put(startDate, TranslationHelper.getLabel(startDate, lang));

        String name = "label.workflow.task.name";
        result.put(name, TranslationHelper.getLabel(name, lang));

        return result;
    }
}
