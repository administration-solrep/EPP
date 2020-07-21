/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.platform.contentview.jsf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.platform.ui.web.cache.LRUCachingMap;

/**
 * Cache for content views, handling cache keys set on content views.
 * <p>
 * Each content view instance will be cached if its cache key is not null. Each
 * instance will be cached using the cache key so its state is restored. Also
 * handles refresh of caches when receiving events configured on the content
 * view.
 *
 * @author Anahide Tchertchian
 * @since 5.4
 */
public class ContentViewCache implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Default cache size, set to 5 instances per content view
     */
    public static final Integer DEFAULT_CACHE_SIZE = new Integer(5);

    protected final Map<String, String> namedCacheKeys = new HashMap<String, String>();

    protected final Map<String, ContentView> namedContentViews = new HashMap<String, ContentView>();

    protected final Map<String, Map<String, ContentView>> cacheInstances = new HashMap<String, Map<String, ContentView>>();

    /**
     * Map holding content view names that need their page provider to be
     * refreshed for a given event
     */
    protected final Map<String, Set<String>> refreshEventToContentViewName = new HashMap<String, Set<String>>();

    /**
     * Map holding content view names that need their page provider to be reset
     * for a given event
     */
    protected final Map<String, Set<String>> resetEventToContentViewName = new HashMap<String, Set<String>>();

    /**
     * Add given content view to the cache, resolving its cache key and
     * initializing it with its cache size.
     */
    public void add(ContentView cView) {
        if (cView != null) {
            String cacheKey = cView.getCacheKey();
            if (cacheKey == null) {
                // no cache
                return;
            }
            String name = cView.getName();
            Integer cacheSize = cView.getCacheSize();
            if (cacheSize == null) {
                cacheSize = DEFAULT_CACHE_SIZE;
            }
            if (cacheSize.intValue() <= 0) {
                // no cache
                return;
            }
            Map<String, ContentView> cacheEntry = cacheInstances.get(name);
            if (cacheEntry == null) {
                cacheEntry = new LRUCachingMap<String, ContentView>(
                        cacheSize.intValue());
            }
            cacheEntry.put(cacheKey, cView);
            cacheInstances.put(name, cacheEntry);
            namedCacheKeys.put(name, cacheKey);
            namedContentViews.put(name, cView);
            List<String> events = cView.getRefreshEventNames();
            if (events != null && !events.isEmpty()) {
                for (String event : events) {
                    if (refreshEventToContentViewName.containsKey(event)) {
                        refreshEventToContentViewName.get(event).add(name);
                    } else {
                        Set<String> set = new HashSet<String>();
                        set.add(name);
                        refreshEventToContentViewName.put(event, set);
                    }
                }
            }
            events = cView.getResetEventNames();
            if (events != null && !events.isEmpty()) {
                for (String event : events) {
                    if (resetEventToContentViewName.containsKey(event)) {
                        resetEventToContentViewName.get(event).add(name);
                    } else {
                        Set<String> set = new HashSet<String>();
                        set.add(name);
                        resetEventToContentViewName.put(event, set);
                    }
                }
            }
        }
    }

    /**
     * Returns cached content view with given name, or null if not found.
     */
    public ContentView get(String name) {
        ContentView cView = namedContentViews.get(name);
        if (cView != null) {
            String oldCacheKey = namedCacheKeys.get(name);
            String newCacheKey = cView.getCacheKey();
            if (newCacheKey != null && !newCacheKey.equals(oldCacheKey)) {
                Map<String, ContentView> contentViews = cacheInstances.get(name);
                if (contentViews.containsKey(newCacheKey)) {
                    cView = contentViews.get(newCacheKey);
                    // refresh named caches
                    namedCacheKeys.put(name, newCacheKey);
                    namedContentViews.put(name, cView);
                } else {
                    // cache not here or expired => return null
                    return null;
                }
            }
        }
        return cView;
    }

    /**
     * Refresh page providers for content views in the cache with given
     * name.refreshEventToContentViewName
     * <p>
     * Other contextual information set on the content view and the page
     * provider will be kept.
     */
    public void refresh(String contentViewName, boolean rewind) {
        ContentView cv = namedContentViews.get(contentViewName);
        if (cv != null) {
            if (rewind) {
                cv.refreshAndRewindPageProvider();
            } else {
                cv.refreshPageProvider();
            }
        }
        Map<String, ContentView> instances = cacheInstances.get(contentViewName);
        if (instances != null) {
            for (ContentView cView : instances.values()) {
                if (cView != null) {
                    if (rewind) {
                        cView.refreshAndRewindPageProvider();
                    } else {
                        cView.refreshPageProvider();
                    }
                }
            }
        }
    }

    /**
     * Resets page providers for content views in the cache with given name.
     * <p>
     * Other contextual information set on the content view will be kept.
     */
    public void resetPageProvider(String contentViewName) {
        ContentView cv = namedContentViews.get(contentViewName);
        if (cv != null) {
            cv.resetPageProvider();
        }
        Map<String, ContentView> instances = cacheInstances.get(contentViewName);
        if (instances != null) {
            for (ContentView cView : instances.values()) {
                if (cView != null) {
                    cView.resetPageProvider();
                }
            }
        }
    }

    /**
     * Refresh page providers for content views having declared given event as
     * a refresh event.
     * <p>
     * Other contextual information set on the content view and the page
     * provider will be kept.
     */
    public void refreshOnEvent(String eventName) {
        if (eventName != null) {
            Set<String> contentViewNames = refreshEventToContentViewName.get(eventName);
            if (contentViewNames != null) {
                for (String contentViewName : contentViewNames) {
                    refresh(contentViewName, false);
                }
            }
        }
    }

    /**
     * Resets page providers for content views having declared given event as a
     * reset event.
     * <p>
     * Other contextual information set on the content view will be kept.
     */
    public void resetPageProviderOnEvent(String eventName) {
        if (eventName != null) {
            Set<String> contentViewNames = resetEventToContentViewName.get(eventName);
            if (contentViewNames != null) {
                for (String contentViewName : contentViewNames) {
                    resetPageProvider(contentViewName);
                }
            }
        }
    }

    /**
     * Resets all cached information for given content view.
     */
    public void reset(String contentViewName) {
        namedContentViews.remove(contentViewName);
        namedCacheKeys.remove(contentViewName);
        cacheInstances.remove(contentViewName);
    }

    /**
     * Resets all cached information for all content views
     */
    public void resetAllContent() {
        namedContentViews.clear();
        namedCacheKeys.clear();
        cacheInstances.clear();
    }

    /**
     * Resets all cached information for all content views, as well as
     * configuration caches (refresh and reset events linked to content views).
     */
    public void resetAll() {
        resetAllContent();
        refreshEventToContentViewName.clear();
        resetEventToContentViewName.clear();
    }

}
