/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     <a href="mailto:grenard@nuxeo.com">Guillaume</a>
 */
package org.nuxeo.ecm.platform.sessioninspector.jsf;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @since 5.9.2
 */
public class StateReferenceHelper {

    public static String getIdForNode(Object node) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Field idField = node.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        String result = (String) idField.get(node);
        return result;
    }

    public static Object getStateForPath(List<?> nodes, String[] path, Object[] states) throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        for (int i = 0; i < nodes.size(); i++) {
            if (getIdForNode(nodes.get(i)).equals(path[0])) {
                if (path.length == 1) {
                    return states[i];
                } else {
                    List<?> children = getChildrenForNode(nodes.get(i));
                    if (children != null) {
                        return getStateForPath(children, Arrays.copyOfRange(path, 1, path.length), (Object[]) states[i]);
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static List<?> getChildrenForNode(Object node) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Field idField = node.getClass().getDeclaredField("children");
        idField.setAccessible(true);
        List<?> result = (List<?>) idField.get(node);
        return result;
    }

    public static Object getChildrenState(Object state) {
        Object[] temp = (Object[]) ((Object[]) state)[1];
        return ((Object[]) temp[0])[1];
    }

    public static String getTypeForNode(Object node) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Field idField = node.getClass().getDeclaredField("type");
        idField.setAccessible(true);
        String result = (String) idField.get(node);
        return result;
    }

}
