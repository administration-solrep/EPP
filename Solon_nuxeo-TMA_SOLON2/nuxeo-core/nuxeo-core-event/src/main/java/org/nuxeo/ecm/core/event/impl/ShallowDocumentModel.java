/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.core.event.impl;

import java.io.Serializable;
import java.util.Collection;
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
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.model.DocumentPart;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.schema.DocumentType;

/**
 * Light weight {@link DocumentModel} implementation Only holds
 * {@link DocumentRef}, RepositoryName, name and path. Used to reduce memory
 * footprint of {@link Event} stacked in {@link EventBundle}.
 *
 * @author Thierry Delprat
 */
public class ShallowDocumentModel implements DocumentModel {

    private static final long serialVersionUID = 1L;

    private final String id;

    private final String repoName;

    private final String name;

    private final Path path;

    private final String type;

    private final boolean isFolder;

    private final boolean isVersion;

    public ShallowDocumentModel(DocumentModel doc) {
        id = doc.getId();
        repoName = doc.getRepositoryName();
        name = doc.getName();
        path = doc.getPath();
        type = doc.getType();
        isFolder = doc.isFolder();
        isVersion = doc.isVersion();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public DocumentRef getRef() {
        return id == null ? null : new IdRef(id);
    }

    @Override
    public String getRepositoryName() {
        return repoName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public String getPathAsString() {
        if (path != null) {
            return path.toString();
        }
        return null;
    }

    @Override
    public DocumentRef getParentRef() {
        if (path != null) {
            return new PathRef(path.removeLastSegments(1).toString());
        }
        return null;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isFolder() {
        return isFolder;
    }

    @Override
    public boolean isVersion() {
        return isVersion;
    }

    @Override
    public void copyContent(DocumentModel sourceDoc) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyContextData(DocumentModel otherDocument) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean followTransition(String transition) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ACP getACP() throws ClientException {
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
    public Collection<String> getAllowedStateTransitions()
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCacheKey() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScopedMap getContextData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Serializable getContextData(ScopeType scope, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CoreSession getCoreSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCurrentLifeCycleState() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataModel getDataModel(String schema) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataModelMap getDataModels() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<DataModel> getDataModelsCollection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getFacets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getDeclaredFacets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSchemas() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getDeclaredSchemas() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentType getDocumentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getFlags() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLifeCyclePolicy() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLock() {
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
    public Map<String, Serializable> getPrefetch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getProperties(String schemaName)
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Property getProperty(String xpath) throws PropertyException,
            ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(String schemaName, String name)
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Serializable getPropertyValue(String xpath)
            throws PropertyException, ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSourceId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Serializable> T getSystemProp(String systemProperty,
            Class<T> type) throws ClientException, DocumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTitle() throws ClientException {
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
    public boolean hasFacet(String facet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSchema(String schema) {
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
    public boolean isDownloadable() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLifeCycleLoaded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLocked() {
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
    public boolean isVersionable() {
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
    public void prefetchProperty(String id, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putContextData(String key, Serializable value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putContextData(ScopeType scope, String key, Serializable value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh(int refreshFlags, String[] schemas)
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setACP(ACP acp, boolean overwrite) throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLock(String key) throws ClientException {
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
    public void setPathInfo(String parentPath, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperties(String schemaName, Map<String, Object> data)
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(String schemaName, String name, Object value)
            throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPropertyValue(String xpath, Serializable value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() throws ClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocumentModel clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public Serializable getContextData(String key) {
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
    public DocumentRef checkIn(VersioningOption option, String checkinComment)
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
