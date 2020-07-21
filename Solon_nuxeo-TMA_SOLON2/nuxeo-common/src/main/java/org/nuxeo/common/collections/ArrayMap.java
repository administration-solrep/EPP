/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.common.collections;

import java.util.Map;

/**
 * A mixture of an array list and a map used to store
 * small table of elements using both indices and keys.
 * <p>
 * This map accepts null values.
 * <p>
 * The map is implemented using an array of successive [key, value] pairs.
 *
 * @author  <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@SuppressWarnings({"ClassWithoutToString"})
public class ArrayMap<K, V> {

    // 4 keys, 4 values
    protected static final int DEFAULT_SIZE = 8;
    protected static final int GROW_SIZE = 10;
    protected int count = 0;
    protected Object[] elements;


    public ArrayMap() {
    }

    public ArrayMap(int initialCapacity) {
        elements = new Object[initialCapacity == 0 ? DEFAULT_SIZE : initialCapacity * 2];
    }

    public ArrayMap(Map<K, V> map) {
        this(map.size());
        putAll(map);
    }

    public ArrayMap(ArrayMap<K, V> map) {
        count = map.count;
        elements = new Object[map.elements.length];
        System.arraycopy(map.elements, 0, elements, 0, count * 2);
    }

    public void putAll(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            add(key, value);
        }
    }

    public V remove(K key) {
        if (elements == null || count == 0) {
            return null;
        }
        for (int i = 0; i < elements.length; i += 2) {
            if (elements[i] != null && elements[i].equals(key)) {
                return _remove(i);
            }
        }
        return null;
    }

    public V remove(int index) {
        if (elements == null || count == 0) {
            return null;
        }
        return _remove(index << 1);
    }

    protected final V _remove(int i) {
        V result = (V) elements[i + 1];
        int len = count * 2;
        if (i + 2 == len) {
            elements[i] = null;
            elements[i + 1] = null;
        } else {
            int k = i + 2;
            System.arraycopy(elements, k, elements, i, len - k);
        }
        count--;
        return result;
    }

    public V get(K key) {
        if (elements == null || count == 0) {
            return null;
        }
        for (int i = 0; i < elements.length; i += 2) {
            if (elements[i] != null && elements[i].equals(key)) {
                return (V) elements[i + 1];
            }
        }
        return null;
    }

    public V get(int i) {
        if (elements == null || i >= count) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        return (V) elements[(i << 1) + 1];
    }

    public K getKey(Object value) {
        if (elements == null || count == 0) {
            return null;
        }
        for (int i = 1; i < elements.length; i += 2) {
            if (elements[i] != null && elements[i].equals(value)) {
                return (K) elements[i - 1];
            }
        }
        return null;
    }

    public K getKey(int i) {
        if (elements == null || i >= count) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        return (K) elements[i << 1];
    }

    public V put(K key, V value) {
        // handle the case where we don't have any attributes yet
        if (elements == null) {
            elements = new Object[DEFAULT_SIZE];
        }
        if (count == 0) {
            elements[0] = key;
            elements[1] = value;
            count++;
            return null;
        }

        int insertIndex = count * 2;

        // replace existing value if it exists
        for (int i = 0; i < insertIndex; i += 2) {
            if (elements[i].equals(key)) {
                Object oldValue = elements[i + 1];
                elements[i + 1] = value;
                return (V) oldValue;
            }
        }

        if (elements.length <= insertIndex) {
            grow();
        }
        elements[insertIndex] = key;
        elements[insertIndex + 1] = value;
        count++;

        return null;
    }

    public void add(K key, V value) {
        // handle the case where we don't have any attributes yet
        int insertIndex;
        if (elements == null) {
            elements = new Object[DEFAULT_SIZE];
            insertIndex = 0;
        } else {
            insertIndex = count * 2;
            if (elements.length <= insertIndex) {
                grow();
            }
        }
        elements[insertIndex] = key;
        elements[insertIndex + 1] = value;
        count++;
    }

    public void trimToSize() {
        int len = count * 2;
        if (len < elements.length) {
            Object[] tmp = new Object[len];
            System.arraycopy(elements, 0, tmp, 0, len);
            elements = tmp;
        }
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void clear() {
        elements = null;
        count = 0;
    }

    protected void grow() {
        Object[] expanded = new Object[elements.length + GROW_SIZE];
        System.arraycopy(elements, 0, expanded, 0, elements.length);
        elements = expanded;
    }

    public Object[] getArray() {
        return elements;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ArrayMap) {
            ArrayMap map = (ArrayMap) obj;
            if (count != map.count) {
                return false;
            }
            int len = count << 1;
            for (int i = 0; i < len; i += 2) {
                Object key1 = elements[i];
                Object key2 = map.elements[i];
                if (!key1.equals(key2)) {
                    return false;
                }
                Object val1 = elements[i + 1];
                Object val2 = map.elements[i + 1];
                if (val1 == null) {
                    if (val1 != val2) {
                        return false;
                    }
                } else if (!val1.equals(val2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        for (int i = 0; i < count * 2; i++) {
            result = result * 37 + elements[i].hashCode();
        }
        return result;
    }

}
