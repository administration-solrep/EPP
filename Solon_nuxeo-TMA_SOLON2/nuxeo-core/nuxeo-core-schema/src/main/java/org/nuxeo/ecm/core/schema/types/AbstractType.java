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

package org.nuxeo.ecm.core.schema.types;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.TypeProvider;
import org.nuxeo.ecm.core.schema.TypeRef;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@SuppressWarnings({ "SuppressionAnnotation" })
public abstract class AbstractType implements Type {

    private static final Log log = LogFactory.getLog(AbstractType.class);

    public static final Type[] EMPTY_SUPERTYPES = new Type[0];

    public static final int F_READONLY = 1;

    public static final int F_NOTNULL = 2;

    private static final long serialVersionUID = -7902736654482518683L;

    protected final String name;

    protected final TypeRef<?> superType;

    protected final String schema;

    protected int flags;

    protected ValueConverter converter;

    protected TypeHelper helper;


    protected AbstractType(TypeRef<? extends Type> superType, String schema, String name) {
        assert schema != null;
        assert name != null;

        this.name = name;
        this.schema = schema;
        this.superType = superType == null ? TypeRef.NULL : superType;
    }


    @Override
    public TypeRef<? extends Type> getRef() {
        return new TypeRef<Type>(schema, name, this);
    }

    @Override
    public TypeHelper getHelper() {
        if (helper == null) {
            try {
                SchemaManager sm = Framework.getService(SchemaManager.class);
                helper = sm.getHelper(schema, name);
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        return helper;
    }

    @Override
    public Type getSuperType() {
        return superType.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSchemaName() {
        return schema;
    }

    @Override
    public Schema getSchema() {
        return Framework.getLocalService(TypeProvider.class).getSchema(schema);
    }

    @Override
    public boolean isSuperTypeOf(Type type) {
        Type t = type;
        do {
            if (this == t) {
                return true;
            }
            t = t.getSuperType();
        } while (t != null);
        return false;
    }

    @SuppressWarnings({ "SameReturnValue" })
    public boolean isAny() {
        return false;
    }

    @Override
    public Type[] getTypeHierarchy() {
        Type type = getSuperType();
        if (type == null) {
            return EMPTY_SUPERTYPES;
        }
        List<Type> types = new ArrayList<Type>();
        while (type != null) {
            types.add(type);
            type = type.getSuperType();
        }
        return types.toArray(new Type[types.size()]);
    }

    @Override
    public boolean isSimpleType() {
        return false;
    }

    @Override
    public boolean isComplexType() {
        return false;
    }

    @Override
    public boolean isListType() {
        return false;
    }

    @Override
    public boolean isAnyType() {
        return false;
    }

    @Override
    public boolean isCompositeType() {
        return false;
    }

    @Override
    public boolean isNotNull() {
        return isFlagSet(F_NOTNULL);
    }

    @Override
    public boolean isReadOnly() {
        return isFlagSet(F_READONLY);
    }

    @Override
    public boolean validate(Object object) throws TypeException {
        return !(object == null && isNotNull());
    }

    public void setNotNull(boolean val) {
        if (val) {
            setFlags(F_NOTNULL);
        } else {
            clearFlags(F_NOTNULL);
        }
    }

    public void setReadOnly(boolean val) {
        if (val) {
            setFlags(F_READONLY);
        } else {
            clearFlags(F_READONLY);
        }
    }

    protected final void setFlags(int flags) {
        this.flags |= flags;
    }

    protected final void clearFlags(int flags) {
        this.flags &= ~flags;
    }

    protected final boolean isFlagSet(int flags) {
        return (this.flags & flags) == flags;
    }

    @Override
    public Object decode(String string) {
        return null;
    }

    @Override
    public String encode(Object object) {
        return null;
    }

    @Override
    public Object newInstance() {
        return null;
    }

}
