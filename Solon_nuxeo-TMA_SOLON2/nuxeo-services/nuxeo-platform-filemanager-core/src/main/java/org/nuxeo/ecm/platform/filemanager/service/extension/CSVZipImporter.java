/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 * $Id$
 */

package org.nuxeo.ecm.platform.filemanager.service.extension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.schema.TypeConstants;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.primitives.DateType;
import org.nuxeo.ecm.core.schema.types.primitives.IntegerType;
import org.nuxeo.ecm.core.schema.types.primitives.LongType;
import org.nuxeo.ecm.core.schema.types.primitives.StringType;
import org.nuxeo.ecm.platform.types.TypeManager;

import au.com.bytecode.opencsv.CSVReader;

public class CSVZipImporter extends AbstractFileImporter {

    private static final long serialVersionUID = 1L;

    private static final String MARKER = "meta-data.csv";

    private static final Log log = LogFactory.getLog(CSVZipImporter.class);

    public static ZipFile getArchiveFileIfValid(File file) throws IOException {
        ZipFile zip;

        try {
            zip = new ZipFile(file);
        } catch (ZipException e) {
            log.debug("file is not a zipfile ! ", e);
            return null;
        } catch (IOException e) {
            log.debug("can not open zipfile ! ", e);
            return null;
        }

        ZipEntry marker = zip.getEntry(MARKER);

        if (marker == null) {
            zip.close();
            return null;
        } else {
            return zip;
        }
    }

    public DocumentModel create(CoreSession documentManager, Blob content,
            String path, boolean overwrite, String filename, TypeManager typeService)
            throws ClientException, IOException {

        File tmp = File.createTempFile("zipcsv-importer", null);

        content.transferTo(tmp);

        ZipFile zip = getArchiveFileIfValid(tmp);

        if (zip == null) {
            tmp.delete();
            return null;
        }

        DocumentModel container = documentManager.getDocument(new PathRef(path));

        ZipEntry index = zip.getEntry(MARKER);
        InputStream indexStream = zip.getInputStream(index);
        Reader reader = new InputStreamReader(indexStream);
        CSVReader csvReader = new CSVReader(reader);

        @SuppressWarnings("unchecked")
        List<String[]> lines = csvReader.readAll();

        String[] header = lines.get(0);

        for (int idx = 1; idx < lines.size(); idx++) {
            String type = null;
            String id = null;
            Map<String, String> stringValues = new HashMap<String, String>();

            for (int col = 0; col < header.length; col++) {
                String headerValue = header[col];
                String lineValue = (lines.get(idx))[col];

                if ("type".equalsIgnoreCase(headerValue)) {
                    type = lineValue;
                } else if ("id".equalsIgnoreCase(headerValue)) {
                    id = lineValue;
                } else {
                    stringValues.put(headerValue, lineValue);
                }
            }

            boolean updateDoc = false;
            // get doc for update
            DocumentModel targetDoc = null;
            if (id != null) {
                // update ?
                String targetPath = new Path(path).append(id).toString();
                if (documentManager.exists(new PathRef(targetPath))) {
                    targetDoc = documentManager.getDocument(new PathRef(targetPath));
                    updateDoc = true;
                }
            }

            // create doc if needed
            if (targetDoc == null) {
                if (type == null) {
                    log.error("Can not create doc without a type, skipping line");
                    continue;
                }

                if (id == null) {
                    id = IdUtils.generateStringId();
                }
                targetDoc = documentManager.createDocumentModel(path, id, type);
            }

            // update doc properties
            DocumentType docType = targetDoc.getDocumentType();
            for (String fname : stringValues.keySet()) {

                String stringValue = stringValues.get(fname);
                Field field = null;
                boolean usePrefix = false;
                String schemaName = null;
                String fieldName = null;

                if (fname.contains(":")) {
                    if (docType.hasField(fname)) {
                        field = docType.getField(fname);
                        usePrefix = true;
                    }
                } else if (fname.contains(".")) {
                    String[] parts = fname.split("\\.");
                    schemaName = parts[0];
                    fieldName = parts[1];
                    if (docType.hasSchema(schemaName)) {
                        field = docType.getField(fieldName);
                        usePrefix = false;
                    }
                } else {
                    if (docType.hasField(fname)) {
                        field = docType.getField(fname);
                        usePrefix = false;
                        schemaName = field.getDeclaringType().getSchemaName();
                    }
                }

                if (field != null) {
                    Serializable fieldValue = null;
                    if (field.getType().isSimpleType()) {
                        if (field.getType() instanceof StringType) {
                            fieldValue = stringValue;
                        } else if (field.getType() instanceof IntegerType) {
                            fieldValue = Integer.parseInt(stringValue);
                        } else if (field.getType() instanceof LongType) {
                            fieldValue = Long.parseLong(stringValue);
                        } else if (field.getType() instanceof DateType) {
                            try {
                                Date date;
                                if (stringValue.length() == 10) {
                                    date = new SimpleDateFormat("dd/MM/yyyy").parse(stringValue);
                                } else if (stringValue.length() == 8) {
                                    date = new SimpleDateFormat("dd/MM/yy").parse(stringValue);
                                } else {
                                    log.warn("Unknow date format :" + stringValue);
                                    continue;
                                }
                                fieldValue = date;
                            } catch (ParseException e) {
                                log.error("Error during date parsing", e);
                            }
                        }
                    } else if (field.getType().isComplexType()) {
                        if (TypeConstants.CONTENT.equals(field.getName().getLocalName())) {
                            ZipEntry blobIndex = zip.getEntry(stringValue);
                            if (blobIndex != null) {
                                InputStream blobStream = zip.getInputStream(blobIndex);
                                Blob blob = new InputStreamBlob(blobStream);
                                blob.setFilename(stringValue);
                                fieldValue = (Serializable) blob;
                            }
                        }
                    }

                    if (fieldValue != null) {
                        if (usePrefix) {
                            targetDoc.setPropertyValue(fname, fieldValue);
                        } else {
                            targetDoc.setProperty(schemaName, fieldName, fieldValue);
                        }
                    }
                }
            }
            if (updateDoc) {
                documentManager.saveDocument(targetDoc);
            } else {
                documentManager.createDocument(targetDoc);
            }
        }
        tmp.delete();
        return container;
    }

}
