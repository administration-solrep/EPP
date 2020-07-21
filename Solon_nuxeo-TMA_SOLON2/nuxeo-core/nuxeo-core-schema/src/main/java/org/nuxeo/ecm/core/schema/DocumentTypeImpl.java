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

package org.nuxeo.ecm.core.schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.nuxeo.ecm.core.schema.types.CompositeTypeImpl;
import org.nuxeo.runtime.api.Framework;

/**
 * Implementation of a document type.
 * <p>
 * This class uses lazy loading for schemas and field types.
 * <p>
 * Schemas and fields are cached to improve lookups time.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DocumentTypeImpl extends CompositeTypeImpl implements DocumentType {

    public static final int T_DOCUMENT = 1 << 8;
    public static final int T_FOLDER   = 2 << 8;
    public static final int T_ORDERED  = 4 << 8;

    private static final long serialVersionUID = 4257192861843860742L;

    private static final String[] EMPTY_FACETS = {};

    protected int unstructured;

    protected String[] declaredFacets = EMPTY_FACETS;
    protected Set<String> facets;

    protected String[] subtypes;

    protected PrefetchInfo prefetchInfo;


    public DocumentTypeImpl(DocumentType superType, String name) {
        this(superType == null ? null : superType.getRef(), name, null, null, 0);
    }

    public DocumentTypeImpl(DocumentType superType, String name,
            String[] declaredSchemas) {
        this(superType == null ? null : superType.getRef(), name,
                declaredSchemas, null, 0);
    }

    public DocumentTypeImpl(DocumentType superType, String name,
            String[] declaredSchemas, String[] declaredFacets) {
        this(superType == null ? null : superType.getRef(), name,
                declaredSchemas, declaredFacets, 0);
    }

    public DocumentTypeImpl(TypeRef<DocumentType> superType, String name) {
        this(superType, name, null, null, 0);
    }

    public DocumentTypeImpl(TypeRef<DocumentType> superType, String name,
            String[] declaredSchemas) {
        this (superType, name, declaredSchemas, null, 0);
    }

    public DocumentTypeImpl(TypeRef<DocumentType> superType, String name,
            String[] declaredSchemas, String[] facets) {
        this (superType, name, declaredSchemas, facets, 0);
    }

    public DocumentTypeImpl(TypeRef<DocumentType> superType, String name,
            String[] declaredSchemas, String[] facets, int flags) {
        super(superType, SchemaNames.DOCTYPES, name, declaredSchemas);
        if (flags != 0) {
            setFlags(flags);
        } else {
            DocumentType stype = (DocumentType) this.superType.get();
            if (stype != null) {

                if (stype.isOrdered()) {
                    setFlags(T_ORDERED | T_FOLDER);
                } else if (stype.isFolder()) {
                    setFlags(T_FOLDER);
                } else {
                    setFlags(T_DOCUMENT);
                }
            } else {
                setFlags(T_DOCUMENT);
            }
        }
        declaredFacets = facets == null ? EMPTY_FACETS : facets;
        subtypes = null;
    }

    @Override
    public void setPrefetchInfo(PrefetchInfo prefetchInfo) {
        this.prefetchInfo = prefetchInfo;
    }

    @Override
    public PrefetchInfo getPrefetchInfo() {
        return prefetchInfo;
    }

    @Override
    public boolean isUnstructured() {
        if (unstructured == F_UNSTRUCT_DEFAULT) {
            unstructured = hasSchemas() ? F_UNSTRUCT_FALSE : F_UNSTRUCT_TRUE;
        }
        return unstructured == F_UNSTRUCT_TRUE;
    }

    @Override
    public boolean isFile() {
        return !getFacets().contains(FacetNames.FOLDERISH);
    }

    @Override
    public boolean isFolder() {
        return getFacets().contains(FacetNames.FOLDERISH);
    }

    @Override
    public boolean isOrdered() {
        Set<String> facets = getFacets();
        return facets.contains(FacetNames.ORDERABLE);
        //return isFolder() && isFlagSet(T_ORDERED | T_FOLDER);
    }

    @Override
    public Set<String> getFacets() {
        if (facets == null) {
            facets = buildFacets();
        }
        return facets;
    }

    @Override
    public void addSchemas(String[] schemas) {
        if (schemas != null) {
            for (String schema : schemas) {
                addSchema(schema);
            }
        }
    }

    @Override
    public void setDeclaredFacets(String[] facets) {
        declaredFacets = facets == null ? EMPTY_FACETS : facets;
        this.facets = null;
    }

    protected Set<String> buildFacets() {
        Set<String> facets = new HashSet<String>();
        DocumentTypeImpl stype = (DocumentTypeImpl) superType.get();
        if (stype == null) {
            facets.addAll(Arrays.asList(declaredFacets));
        } else {
            Set<String> inheritedFacets = stype.buildFacets();
            if (declaredFacets.length == 0) {
                facets = inheritedFacets;
            } else {
                for (String facet : inheritedFacets) {
                    facets.add(facet);
                }
                facets.addAll(Arrays.asList(declaredFacets));
            }
        }
        return facets;
    }

    @Override
    public TypeRef<DocumentType> getRef() {
        return new TypeRef<DocumentType>(schema, name, this);
    }

    @Override
    public String[] getChildrenTypes() {
        return subtypes;
    }

    @Override
    public void setChildrenTypes(String[] subTypes) {
        subtypes = subTypes;
    }

    @Override
    public DocumentType[] getResolvedChildrenTypes() {
        SchemaManager mgr = Framework.getLocalService(SchemaManager.class);
        if (subtypes != null) {
            DocumentType[] result = new DocumentType[subtypes.length];
            for (int i = 0; i < subtypes.length; i++) {
                result[i] = mgr.getDocumentType(subtypes[i]);
            }
            return result;
        }
        return null;
    }

    @Override
    public boolean isChildTypeAllowed(String name) {
        if (subtypes != null) {
            for (String subtype : subtypes) {
                if (subtype.equals(name)) {
                    return true;
                }
            }

            // TODO: expand *
            return false;
        }
        return false;
    }

}
