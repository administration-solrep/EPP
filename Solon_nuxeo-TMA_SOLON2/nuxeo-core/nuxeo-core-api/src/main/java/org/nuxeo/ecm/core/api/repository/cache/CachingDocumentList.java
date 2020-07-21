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
 * $Id$
 */

package org.nuxeo.ecm.core.api.repository.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 *
 * A document list that uses document cached from the document cache.
 * This is not using the children cache. If you need to cache getChildren calls it is better to use
 * the {@link DocumentChildrenList} which is caching children lists
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class CachingDocumentList implements DocumentModelList {

    private static final long serialVersionUID = 6370206124496509919L;

    private final List<DocumentModel> list;
    private final DocumentModelCache cache;

    public CachingDocumentList(DocumentModelCache cache, List<DocumentModel> list) {
        this.list = list;
        this.cache = cache;
    }

    @Override
    public boolean add(DocumentModel o) {
        return list.add(o);
    }

    @Override
    public void add(int index, DocumentModel element) {
        list.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends DocumentModel> c) {
        return list.addAll(c);
    }

    // FIXME: this recurses infinitely.
    @Override
    public boolean addAll(int index, Collection<? extends DocumentModel> c) {
        return addAll(index, c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public DocumentModel get(int index) {
        return cache.cacheDocument(list.get(index));
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<DocumentModel> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<DocumentModel> listIterator() {
        return new CachingIterator(list.listIterator());
    }

    @Override
    public ListIterator<DocumentModel> listIterator(int index) {
        return new CachingIterator(list.listIterator(index));
    }

    @Override
    public DocumentModel remove(int index) {
        return list.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public DocumentModel set(int index, DocumentModel element) {
        return list.set(index, element);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public long totalSize() {
        return size();
    }

    @Override
    public List<DocumentModel> subList(int fromIndex, int toIndex) {
        return new CachingDocumentList(cache, list.subList(fromIndex, toIndex));
    }

    @Override
    public Object[] toArray() {
        Object[] ar = list.toArray();
        for (int i = ar.length - 1; i >= 0; i--) {
            ar[i] = cache.cacheDocument((DocumentModel) ar[i]);
        }
        return ar;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        T[] ar = list.toArray(a);
        for (int i = ar.length - 1; i >= 0; i--) {
            ar[i] = (T) cache.cacheDocument((DocumentModel) ar[i]);
        }
        return ar;
    }

    static class CachingIterator implements ListIterator<DocumentModel> {

        final ListIterator<DocumentModel> it;

        CachingIterator(ListIterator<DocumentModel> it) {
            this.it = it;
        }

        @Override
        public void add(DocumentModel o) {
            it.add(o);
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        @Override
        public DocumentModel next() {
            return it.next();
        }

        @Override
        public int nextIndex() {
            return it.nextIndex();
        }

        @Override
        public DocumentModel previous() {
            return it.previous();
        }

        @Override
        public int previousIndex() {
            return it.previousIndex();
        }

        @Override
        public void remove() {
            it.remove();
        }

        @Override
        public void set(DocumentModel o) {
            it.set(o);
        }
    }

}
