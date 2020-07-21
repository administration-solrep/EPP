/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 *
 * $Id: ExportedDocumentImpl.java 29029 2008-01-14 18:38:14Z ldoguin $
 */

package org.nuxeo.ecm.core.io.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.nuxeo.common.collections.PrimitiveArrays;
import org.nuxeo.common.utils.Base64;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DocumentLocation;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentLocationImpl;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.io.ExportConstants;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.schema.Namespace;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.TypeConstants;
import org.nuxeo.ecm.core.schema.types.ComplexType;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.ListType;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.core.schema.types.Type;
import org.nuxeo.runtime.api.Framework;

/**
 * A representation for an exported document.
 * <p>
 * It contains all the information needed to restore document data and state.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@SuppressWarnings("unchecked")
public class ExportedDocumentImpl implements ExportedDocument {

    private static final Log log = LogFactory.getLog(ExportedDocumentImpl.class);

    private static final Random random = new Random();

    private DocumentLocation srcLocation;

    // document unique ID
    private String id;

    // document path
    private Path path;

    // the main document
    private Document document;

    // the external blobs if any
    private final Map<String, Blob> blobs = new HashMap<String, Blob>(4);

    // the optional attached documents
    private final Map<String, Document> documents = new HashMap<String, Document>(
            4);

    public ExportedDocumentImpl() {
    }

    /**
     * @param doc
     * @param path the path to use for this document this is used to remove full
     *            paths
     */
    public ExportedDocumentImpl(DocumentModel doc, Path path,
            boolean inlineBlobs) throws IOException {
        id = doc.getId();
        if (path == null) {
            this.path = new Path("");
        } else {
            this.path = path.makeRelative();
        }
        try {
            readDocument(doc, inlineBlobs);
        } catch (ClientException e) {
            throw new ClientRuntimeException(e);
        }

        srcLocation = new DocumentLocationImpl(doc);
    }

    public ExportedDocumentImpl(DocumentModel doc) throws IOException {
        this(doc, false);
    }

    public ExportedDocumentImpl(DocumentModel doc, boolean inlineBlobs)
            throws IOException {
        this(doc, doc.getPath(), inlineBlobs);
    }

    /**
     * @return the source DocumentLocation
     */
    @Override
    public DocumentLocation getSourceLocation() {
        return srcLocation;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return document.getRootElement().element(ExportConstants.SYSTEM_TAG).elementText(
                "type");
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
        id = document.getRootElement().attributeValue(ExportConstants.ID_ATTR);
        String repName = document.getRootElement().attributeValue(
                ExportConstants.REP_NAME);
        srcLocation = new DocumentLocationImpl(repName, new IdRef(id));
    }

    @Override
    public Map<String, Blob> getBlobs() {
        return blobs;
    }

    @Override
    public void putBlob(String id, Blob blob) {
        blobs.put(id, blob);
    }

    @Override
    public Blob removeBlob(String id) {
        return blobs.remove(id);
    }

    @Override
    public Blob getBlob(String id) {
        return blobs.get(id);
    }

    @Override
    public boolean hasExternalBlobs() {
        return !blobs.isEmpty();
    }

    @Override
    public Map<String, Document> getDocuments() {
        return documents;
    }

    @Override
    public Document getDocument(String id) {
        return documents.get(id);
    }

    @Override
    public void putDocument(String id, Document doc) {
        documents.put(id, doc);
    }

    @Override
    public Document removeDocument(String id) {
        return documents.remove(id);
    }

    /**
     * @return the number of files describing the document.
     */
    @Override
    public int getFilesCount() {
        return 1 + documents.size() + blobs.size();
    }

    private void readDocument(DocumentModel doc, boolean inlineBlobs)
            throws IOException, ClientException {
        document = DocumentFactory.getInstance().createDocument();
        document.setName(doc.getName());
        Element rootElement = document.addElement(ExportConstants.DOCUMENT_TAG);
        rootElement.addAttribute(ExportConstants.REP_NAME,
                doc.getRepositoryName());
        rootElement.addAttribute(ExportConstants.ID_ATTR,
                doc.getRef().toString());
        Element systemElement = rootElement.addElement(ExportConstants.SYSTEM_TAG);
        systemElement.addElement(ExportConstants.TYPE_TAG).addText(
                doc.getType());
        systemElement.addElement(ExportConstants.PATH_TAG).addText(
                path.toString());
        // lifecycle
        try {
            String lifeCycleState = doc.getCurrentLifeCycleState();
            if (lifeCycleState != null && lifeCycleState.length() > 0) {
                systemElement.addElement(ExportConstants.LIFECYCLE_STATE_TAG).addText(
                        lifeCycleState);
            }
            String lifeCyclePolicy = doc.getLifeCyclePolicy();
            if (lifeCyclePolicy != null && lifeCyclePolicy.length() > 0) {
                systemElement.addElement(ExportConstants.LIFECYCLE_POLICY_TAG).addText(
                        lifeCyclePolicy);
            }
        } catch (Exception e) {
            log.error(e, e);
        } // end of lifecycle

        // write security
        Element acpElement = systemElement.addElement(ExportConstants.ACCESS_CONTROL_TAG);
        ACP acp = doc.getACP();
        if (acp != null) {
            readACP(acpElement, acp);
        }

        // write schemas
        SchemaManager schemaManager = Framework.getLocalService(SchemaManager.class);
        String[] schemaNames = doc.getSchemas();
        for (String schemaName : schemaNames) {
            Element schemaElement = rootElement.addElement(
                    ExportConstants.SCHEMA_TAG).addAttribute("name", schemaName);
            Schema schema = schemaManager.getSchema(schemaName);
            Namespace targetNs = schema.getNamespace();
            schemaElement.addNamespace(targetNs.prefix, targetNs.uri);
            DataModel dataModel = doc.getDataModel(schemaName);
            for (Field field : schema.getFields()) {
                Object value = dataModel.getData(field.getName().getLocalName());
                readProperty(schemaElement, targetNs, field, value, inlineBlobs);
            }
        }
    }

    private void readProperty(Element parent, Namespace targetNs, Field field,
            Object value, boolean inlineBlobs) throws IOException {
        Type type = field.getType();
        QName name = QName.get(field.getName().getLocalName(), targetNs.prefix,
                targetNs.uri);
        Element element = parent.addElement(name);
        if (value == null) {
            return; // have no content
        }

        // extract the element content
        if (type.isSimpleType()) {
            element.addText(type.encode(value));
        } else if (type.isComplexType()) {
            ComplexType ctype = (ComplexType) type;
            if (TypeConstants.isContentType(ctype)) {
                readBlob(element, ctype, (Blob) value, inlineBlobs);
            } else {
                readComplex(element, ctype, (Map) value, inlineBlobs);
            }
        } else if (type.isListType()) {
            if (value instanceof List) {
                readList(element, (ListType) type, (List) value, inlineBlobs);
            } else if (value.getClass().getComponentType() != null) {
                readList(element, (ListType) type,
                        PrimitiveArrays.toList(value), inlineBlobs);
            } else {
                throw new IllegalArgumentException(
                        "A value of list type is neither list neither array: "
                                + value);
            }
        }
    }

    private void readBlob(Element element, ComplexType ctype, Blob blob,
            boolean inlineBlobs) throws IOException {
        String blobPath = Integer.toHexString(random.nextInt()) + ".blob";
        element.addElement(ExportConstants.BLOB_ENCODING).addText(
                blob.getEncoding() != null ? blob.getEncoding() : "");
        element.addElement(ExportConstants.BLOB_MIME_TYPE).addText(
                blob.getMimeType() != null ? blob.getMimeType() : "");
        element.addElement(ExportConstants.BLOB_FILENAME).addText(
                blob.getFilename() != null ? blob.getFilename() : "");
        Element data = element.addElement(ExportConstants.BLOB_DATA);
        if (inlineBlobs) {
            String content = Base64.encodeBytes(blob.getByteArray());
            data.setText(content);
        } else {
            data.setText(blobPath);
            blobs.put(blobPath, blob);
        }
    }

    private void readComplex(Element element, ComplexType ctype, Map map,
            boolean inlineBlobs) throws IOException {
        Iterator<Map.Entry> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            readProperty(element, ctype.getNamespace(),
                    ctype.getField(entry.getKey().toString()),
                    entry.getValue(), inlineBlobs);
        }
    }

    private void readList(Element element, ListType ltype, List list,
            boolean inlineBlobs) throws IOException {
        Field field = ltype.getField();
        for (Object obj : list) {
            readProperty(element, Namespace.DEFAULT_NS, field, obj, inlineBlobs);
        }
    }

    private static void readACP(Element element, ACP acp) {
        ACL[] acls = acp.getACLs();
        for (ACL acl : acls) {
            Element aclElement = element.addElement(ExportConstants.ACL_TAG);
            aclElement.addAttribute(ExportConstants.NAME_ATTR, acl.getName());
            ACE[] aces = acl.getACEs();
            for (ACE ace : aces) {
                Element aceElement = aclElement.addElement(ExportConstants.ACE_TAG);
                aceElement.addAttribute(ExportConstants.PRINCIPAL_ATTR,
                        ace.getUsername());
                aceElement.addAttribute(ExportConstants.PERMISSION_ATTR,
                        ace.getPermission());
                aceElement.addAttribute(ExportConstants.GRANT_ATTR,
                        String.valueOf(ace.isGranted()));
            }
        }
    }

}
