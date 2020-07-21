/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Thomas Roger
 *     Dragos Mihalache
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.api.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.common.collections.ScopeType;
import org.nuxeo.common.collections.ScopedMap;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DataModelMap;
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.model.DocumentPart;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;
import org.nuxeo.ecm.core.api.model.impl.DefaultPropertyFactory;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.schema.DocumentType;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.runtime.api.Framework;

/**
 * A DocumentModel that can have any schema and is not made persistent by
 * itself. A mockup to keep arbitrary schema data.
 */
public class SimpleDocumentModel implements DocumentModel {

    private static final long serialVersionUID = 1L;

    protected final boolean anySchema;

    protected final DataModelMap dataModels = new DataModelMapImpl();

    protected final ScopedMap contextData = new ScopedMap();

    protected Path path;

    protected String type;

    public SimpleDocumentModel(List<String> schemas) {
        for (String schema : schemas) {
            dataModels.put(schema, new SimpleDataModel(schema));
        }
        anySchema = false;
    }

    public SimpleDocumentModel(String... schemas) {
        this(Arrays.asList(schemas));
    }

    public SimpleDocumentModel() {
        anySchema = true;
    }

    /**
     * A data model that is not tied to a particular schema, neither has
     * anything to do with a session (CoreSession).
     */
    public static class SimpleDataModel implements DataModel {

        private static final long serialVersionUID = 1L;

        public final String schema;

        public final Map<String, Object> data = new HashMap<String, Object>();

        public SimpleDataModel(String schema) {
            this.schema = schema;
        }

        @Override
        public void setData(String key, Object value) throws PropertyException {
            data.put(key, value);
        }

        @Override
        public Object getData(String key) throws PropertyException {
            return data.get(key);
        }

        @Override
        public String getSchema() {
            return schema;
        }

        @Override
        public Map<String, Object> getMap() throws PropertyException {
            return data;
        }

        @Override
        public void setMap(Map<String, Object> data) throws PropertyException {
            data = new HashMap<String, Object>(data);
        }

        @Override
        public boolean isDirty() {
            return true;
        }

        @Override
        public boolean isDirty(String name) throws PropertyNotFoundException {
            return true;
        }

        @Override
        public void setDirty(String name) throws PropertyNotFoundException {
        }

        @Override
        public Collection<String> getDirtyFields() {
            return data.keySet();
        }

        @Override
        public Object getValue(String path) throws PropertyException {
            throw new UnsupportedOperationException("getValue");
        }

        @Override
        public Object setValue(String path, Object value)
                throws PropertyException {
            throw new UnsupportedOperationException("setValue");
        }
    }

    protected DataModel getDataModelInternal(String schema) {
        DataModel dm = dataModels.get(schema);
        if (dm == null && anySchema) {
            dm = new SimpleDataModel(schema);
            dataModels.put(schema, dm);
        }
        return dm;
    }

    @Override
    public String[] getSchemas() {
        Set<String> keys = dataModels.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    @Override
    public String[] getDeclaredSchemas() {
        return getSchemas();
    }

    @Override
    public Object getProperty(String schemaName, String name)
            throws ClientException {
        DataModel dm = getDataModelInternal(schemaName);
        return dm != null ? dm.getData(name) : null;
    }

    @Override
    public void setProperty(String schemaName, String name, Object value)
            throws ClientException {
        if (name.contains(":")) {
            name = name.substring(name.indexOf(":"), name.length());
        }
        getDataModelInternal(schemaName).setData(name, value);
    }

    @Override
    public Map<String, Object> getProperties(String schemaName)
            throws ClientException {
        return getDataModelInternal(schemaName).getMap();
    }

    @Override
    public void setProperties(String schemaName, Map<String, Object> data)
            throws ClientException {
        getDataModelInternal(schemaName).setMap(data);
    }

    @Override
    public ScopedMap getContextData() {
        return contextData;
    }

    @Override
    public Serializable getContextData(ScopeType scope, String key) {
        return contextData.getScopedValue(scope, key);
    }

    @Override
    public void putContextData(ScopeType scope, String key, Serializable value) {
        contextData.putScopedValue(scope, key, value);
    }

    @Override
    public Serializable getContextData(String key) {
        return contextData.getScopedValue(key);
    }

    @Override
    public void putContextData(String key, Serializable value) {
        contextData.putScopedValue(key, value);
    }

    @Override
    public void copyContextData(DocumentModel otherDocument) {
        ScopedMap otherMap = otherDocument.getContextData();
        if (otherMap != null) {
            contextData.putAll(otherMap);
        }
    }

    @Override
    public Property getProperty(String xpath) throws PropertyException,
            ClientException {
        Path path = new Path(xpath);
        if (path.segmentCount() == 0) {
            throw new PropertyNotFoundException(xpath, "Schema not specified");
        }
        String segment = path.segment(0);
        int p = segment.indexOf(':');
        if (p == -1) { // support also other schema paths? like schema.property
            // allow also unprefixed schemas -> make a search for the first
            // matching schema having a property with same name as path segment
            // 0
            DocumentPart[] parts = getParts();
            for (DocumentPart part : parts) {
                if (part.getSchema().hasField(segment)) {
                    return part.resolvePath(path.toString());
                }
            }
            // could not find any matching schema
            throw new PropertyNotFoundException(xpath, "Schema not specified");
        }
        String prefix = segment.substring(0, p);
        SchemaManager mgr = Framework.getLocalService(SchemaManager.class);
        Schema schema = mgr.getSchemaFromPrefix(prefix);
        if (schema == null) {
            schema = mgr.getSchema(prefix);
            if (schema == null) {
                throw new PropertyNotFoundException(xpath,
                        "Could not find registered schema with prefix: "
                                + prefix);
            }
        }
        String[] segments = path.segments();
        segments[0] = segments[0].substring(p + 1);
        path = Path.createFromSegments(segments);

        DocumentPart part = DefaultPropertyFactory.newDocumentPart(schema);
        part.init((Serializable) getDataModelInternal(schema.getName()).getMap());
        return part.resolvePath(path.toString());
    }

    @Override
    public Serializable getPropertyValue(String xpath)
            throws PropertyException, ClientException {
        return getProperty(xpath).getValue();
    }

    @Override
    public void setPropertyValue(String xpath, Serializable value)
            throws PropertyException, ClientException {
        getProperty(xpath).setValue(value);
    }

    @Override
    public DocumentType getDocumentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CoreSession getCoreSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentRef getRef() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentRef getParentRef() {
        if (path == null) {
            return null;
        }
        if (!path.isAbsolute()) {
            return null;
        }
        return new PathRef(path.removeLastSegments(1).toString());
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return path == null ? null : path.lastSegment();
    }

    @Override
    public String getPathAsString() {
        return path == null ? null : path.toString();
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public String getTitle() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Set<String> getFacets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getDeclaredFacets() {
        return getFacets();
    }

    @Override
    public Collection<DataModel> getDataModelsCollection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataModelMap getDataModels() {
        return dataModels;
    }

    @Override
    public DataModel getDataModel(String schema) throws ClientException {
        return getDataModelInternal(schema);
    }

    @Override
    public void setPathInfo(String parentPath, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLocked() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLock(String key) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lock setLock() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lock getLockInfo() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lock removeLock() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ACP getACP() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setACP(ACP acp, boolean overwrite) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSchema(String schema) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasFacet(String facet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addFacet(String facet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeFacet(String facet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFolder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVersionable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDownloadable() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isProxy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isImmutable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getAdapter(Class<T> itf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getAdapter(Class<T> itf, boolean refreshCache) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCurrentLifeCycleState() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLifeCyclePolicy() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean followTransition(String transition) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getAllowedStateTransitions()
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyContent(DocumentModel sourceDoc) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRepositoryName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCacheKey() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSourceId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersionLabel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCheckinComment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Serializable> getPrefetch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void prefetchProperty(String id, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void prefetchCurrentLifecycleState(String lifecycle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void prefetchLifeCyclePolicy(String lifeCyclePolicy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLifeCycleLoaded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Serializable> T getSystemProp(String systemProperty,
            Class<T> type) throws ClientException, DocumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentPart getPart(String schema) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentPart[] getParts() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getFlags() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh(int refreshFlags, String[] schemas)
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentModel clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCheckedOut() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkOut() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentRef checkIn(VersioningOption option, String description)
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersionSeriesId() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLatestVersion() throws ClientException {
        return false;
    }

    @Override
    public boolean isMajorVersion() throws ClientException {
        return false;
    }

    @Override
    public boolean isLatestMajorVersion() throws ClientException {
        return false;
    }

    @Override
    public boolean isVersionSeriesCheckedOut() throws ClientException {
        return true;
    }

}
