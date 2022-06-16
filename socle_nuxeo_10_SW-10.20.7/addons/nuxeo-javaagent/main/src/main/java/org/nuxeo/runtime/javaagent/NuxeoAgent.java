/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nuxeo.runtime.javaagent;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.IdentityHashMap;
import java.util.Map;

public class NuxeoAgent {

    protected static NuxeoAgent agent = new NuxeoAgent();

    protected Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        agent.instrumentation = inst;
    }

    public static void agentmain(String args, Instrumentation inst) {
        agent.instrumentation = inst;
    }

    public long sizeOf(final Object o) {
        return instrumentation.getObjectSize(o);
    }

    public long deepSizeOf(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }

        return AccessController.doPrivileged(new PrivilegedAction<Long>() {
            @Override
            public Long run() {
                return new GraphSizeProfiler().visit(o);
            }
        });
    }

    protected class GraphSizeProfiler {

        protected final Map<Object, Object> visited = new IdentityHashMap<Object, Object>();

        protected long visit(Object each) {
            if (each == null) {
                return 0;
            }
            if (visited.containsKey(each)) {
                return 0;
            }
            visited.put(each, each);
            long size = instrumentation.getObjectSize(each);
            Class<?> eachType = each.getClass();
            if (eachType.isArray()) {
                if (eachType.getComponentType().isPrimitive()) {
                    return 0;
                }
                for (int i = 0; i < Array.getLength(each); i++) {
                    size += visit(Array.get(each, i));
                }
            } else {
                size += visit(each, eachType);
            }
            return size;
        }

        protected long visit(Object each, Class<?> eachType) {
            if (eachType.equals(Object.class)) {
                return 0;
            }
            long size = 0;
            for (Field eachField : eachType.getDeclaredFields()) {
                size += visit(each, eachField);
            }
            return size + visit(each, eachType.getSuperclass());
        }

        protected long visit(Object each, Field eachField) {
            if ((eachField.getModifiers() & Modifier.STATIC) != 0) {
                return 0;
            }
            if (eachField.getType().isPrimitive()) {
                return 0;
            }
            boolean oldAccessible = eachField.isAccessible();
            eachField.setAccessible(true);
            try {
                return visit(eachField.get(each));
            } catch (Exception e) {
                throw new RuntimeException("Exception trying to access field " + eachField, e);
            } finally {
                eachField.setAccessible(oldAccessible);
            }
        }
    }

}
