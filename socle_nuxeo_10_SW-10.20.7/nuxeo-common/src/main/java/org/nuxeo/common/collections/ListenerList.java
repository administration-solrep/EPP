/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     IBM Corporation - initial API and implementation
 */
package org.nuxeo.common.collections;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Internal class to maintain a list of listeners. This class is a thread-safe list that is optimized for frequent reads
 * and infrequent writes. Copy on write is used to ensure readers can access the list without synchronization overhead.
 * Readers are given access to the underlying array data structure for reading, with the trust that they will not modify
 * the underlying array.
 * <p>
 * Contains code from Eclipse org.eclipse.core.runtime.ListenerList class
 *
 * @see http://www.eclipse.org
 */
public class ListenerList {

    /**
     * Mode constant (value 0) indicating that listeners should be compared using equality.
     */
    public static final int EQUALITY = 0;

    /**
     * Mode constant (value 1) indicating that listeners should be compared using identity.
     */
    public static final int IDENTITY = 1;

    /**
     * The empty array singleton instance.
     */
    private static final Object[] EMPTY_ARRAY = new Object[0];

    /**
     * Indicates the comparison mode used to determine if two listeners are equivalent.
     */
    private final int compareMode;

    /**
     * The list of listeners. Initially <code>null</code> but initialized to an array of size capacity the first time a
     * listener is added. Maintains invariant: <code>listeners != null</code>.
     */
    private volatile Object[] listeners = EMPTY_ARRAY;

    /**
     * Used to order listeners
     */
    private final Comparator<?> comparator;

    /**
     * Creates a listener list.
     */
    public ListenerList() {
        this(EQUALITY, null);
    }

    public ListenerList(Comparator<?> comparator) {
        this(EQUALITY, comparator);
    }

    /**
     * Creates a listener list using the provided comparison mode.
     */
    public ListenerList(int mode, Comparator<?> comparator) {
        compareMode = mode;
        this.comparator = comparator;
    }

    /**
     * Adds the given listener to this list. Has no effect if an equal listener is already registered.
     * <p>
     * This method is synchronized to protect against multiple threads adding or removing listeners concurrently. This
     * does not block concurrent readers.
     *
     * @param listener the listener to add
     */
    public synchronized void add(Object listener) {
        // check for duplicates
        final int oldSize = listeners.length;
        for (int i = 0; i < oldSize; ++i) {
            if (same(listener, listeners[i])) {
                return;
            }
        }
        // Thread safety: create new array to avoid affecting concurrent readers
        Object[] newListeners = new Object[oldSize + 1];
        System.arraycopy(listeners, 0, newListeners, 0, oldSize);
        newListeners[oldSize] = listener;
        if (comparator != null) {
            Arrays.sort(newListeners, (Comparator<Object>) comparator);
        }
        // atomic assignment
        listeners = newListeners;
    }

    /**
     * Returns an array containing all the registered listeners. The resulting array is unaffected by subsequent adds or
     * removes. If there are no listeners registered, the result is an empty array singleton instance (no garbage is
     * created). Use this method when notifying listeners, so that any modifications to the listener list during the
     * notification will have no effect on the notification itself.
     * <p>
     * Note: callers must not modify the returned array.
     *
     * @return the list of registered listeners
     */
    public Object[] getListeners() {
        return listeners;
    }

    public synchronized Object[] getListenersCopy() {
        Object[] tmp = new Object[listeners.length];
        System.arraycopy(listeners, 0, tmp, 0, listeners.length);
        return tmp;
    }

    /**
     * Returns whether this listener list is empty.
     *
     * @return <code>true</code> if there are no registered listeners, and <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return listeners.length == 0;
    }

    /**
     * Removes the given listener from this list. Has no effect if an identical listener was not already registered.
     * <p>
     * This method is synchronized to protect against multiple threads adding or removing listeners concurrently. This
     * does not block concurrent readers.
     *
     * @param listener the listener
     */
    public synchronized void remove(Object listener) {
        int oldSize = listeners.length;
        for (int i = 0; i < oldSize; ++i) {
            if (same(listener, listeners[i])) {
                if (oldSize == 1) {
                    listeners = EMPTY_ARRAY;
                } else {
                    // Thread safety: create new array to avoid affecting concurrent readers
                    Object[] newListeners = new Object[oldSize - 1];
                    System.arraycopy(listeners, 0, newListeners, 0, i);
                    System.arraycopy(listeners, i + 1, newListeners, i, oldSize - i - 1);
                    // atomic assignment to field
                    listeners = newListeners;
                }
                return;
            }
        }
    }

    /**
     * Returns <code>true</code> if the two listeners are the same based on the specified comparison mode, and
     * <code>false</code> otherwise.
     */
    private boolean same(Object listener1, Object listener2) {
        return compareMode == IDENTITY ? listener1 == listener2 : listener1.equals(listener2);
    }

    /**
     * Returns the number of registered listeners.
     *
     * @return the number of registered listeners
     */
    public int size() {
        return listeners.length;
    }

}
