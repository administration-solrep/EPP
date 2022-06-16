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
 *
 * Contributors:
 *      Nelson Silva
 */

package org.nuxeo.ecm.media.publishing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.work.api.Work;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.media.publishing.adapter.PublishableMedia;
import org.nuxeo.ecm.media.publishing.upload.MediaPublishingUploadWork;
import org.nuxeo.ecm.webapp.action.ActionContextProvider;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 7.3
 */
@Name("mediaPublishing")
@Scope(ScopeType.EVENT)
public class MediaPublishingActions implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String MEDIA_PUBLISHING_OPTIONS_CATEGORY = "MEDIA_PUBLISHING_OPTIONS_CATEGORY";

    private static final Log log = LogFactory.getLog(MediaPublishingActions.class);

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected transient ActionContextProvider actionContextProvider;

    @In(create = true, required = false)
    protected transient WebActions webActions;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    private static String selectedAccount;

    private Map<String, Object> providersStats;

    private Map<String, String> providersURL;

    private Map<String, String> providersEmbedCode;

    private Map<String, Boolean> publishedProviders;

    Map<String, String> options;

    private DocumentModel currentDoc;

    public MediaPublishingActions() {
        providersStats = new HashMap<>();
        providersEmbedCode = new HashMap<>();
        providersURL = new HashMap<>();
        options = new HashMap<>();
        publishedProviders = new HashMap<>();
    }

    public String[] getAvailableServices(DocumentModel doc) {
        return getMediaPublishingService().getAvailableProviders(doc);
    }

    public UploadStatus getUploadStatus(DocumentModel doc, String uploadServiceName) {
        WorkManager workManager = Framework.getService(WorkManager.class);

        String workId = MediaPublishingUploadWork.getIdFor(doc.getRepositoryName(), doc.getId(), uploadServiceName);
        Work.State state = workManager.getWorkState(workId);

        if (state == null) {
            return null;
        } else {
            switch (state) {
                case SCHEDULED:
                    return new UploadStatus(UploadStatus.STATUS_UPLOAD_QUEUED, new Work.Progress(0));
                case RUNNING:
                    return new UploadStatus(UploadStatus.STATUS_UPLOAD_PENDING, new Work.Progress(0));
                default:
                    return null;
            }
        }
    }

    public boolean isPublished(DocumentModel doc, String provider) {
        if (!publishedProviders.containsKey(provider)) {
            PublishableMedia media = doc.getAdapter(PublishableMedia.class);
            boolean isPublished = media != null && media.getId(provider) != null && media.isPublishedByProvider(provider);
            publishedProviders.put(provider, isPublished);
        }
        return publishedProviders.get(provider);
    }

    public String getPublishedURL(DocumentModel doc, String provider) {
        String url = providersURL.get(provider);
        if (url == null) {
            PublishableMedia media = doc.getAdapter(PublishableMedia.class);
            url = media.getUrl(provider);
            providersURL.put(provider, url);
        }
        return url;
    }

    public void publish(String provider) {
        DocumentModel doc = navigationContext.getCurrentDocument();
        if (selectedAccount == null || selectedAccount.length() == 0) {
            return;
        }
        getMediaPublishingService().publish(doc, provider, selectedAccount, options);
        selectedAccount = null;
    }

    public void unpublish(String provider) {
        DocumentModel doc = navigationContext.getCurrentDocument();
        getMediaPublishingService().unpublish(doc, provider);
        publishedProviders.remove(provider);
    }

    public String getEmbedCode(DocumentModel doc, String provider) {
        String embedCode = providersEmbedCode.get(provider);
        if (embedCode == null) {
            PublishableMedia media = doc.getAdapter(PublishableMedia.class);
            embedCode = media.getEmbedCode(provider);
            providersEmbedCode.put(provider, embedCode);
        }
        return embedCode;
    }

    public Map<String, String> getStats(DocumentModel doc, String provider) {
        Map<String, String> stats = (Map<String, String>) providersStats.get(provider);
        if (stats == null) {
            PublishableMedia media = doc.getAdapter(PublishableMedia.class);
            stats = media.getStats(provider);
            providersStats.put(provider, stats);
        }
        return stats;
    }

    private MediaPublishingService getMediaPublishingService() {
        return Framework.getService(MediaPublishingService.class);
    }

    public String getStatusMessageFor(UploadStatus status) {
        if (status == null) {
            return "";
        }
        String i18nMessageTemplate = resourcesAccessor.getMessages().get(status.getMessage());
        if (i18nMessageTemplate == null) {
            return "";
        } else {
            return Interpolator.instance().interpolate(i18nMessageTemplate,
                    status.positionInQueue, status.queueSize, status.progress.getCurrent());
        }
    }

    public String getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(String selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public DocumentModel getCurrentDoc() {
        if (currentDoc == null) {
            currentDoc = navigationContext.getCurrentDocument();
            currentDoc.refresh();
        }
        return currentDoc;
    }

    /**
     * Helper to retrieve Options widgets for a given Publishing provider
     */
    public List<Action> getProviderOptionsWidgets(String provider) {
        ActionContext ctx = actionContextProvider.createActionContext();
        ctx.putLocalVariable("provider", provider);
        return webActions.getActionsList(MEDIA_PUBLISHING_OPTIONS_CATEGORY, ctx);
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    /**
     * Data transfer object to report on the state of a video upload.
     */
    public class UploadStatus {
        public static final String STATUS_UPLOAD_QUEUED = "status.video.uploadQueued";

        public static final String STATUS_UPLOAD_PENDING = "status.video.uploadPending";

        public final String message;

        public final int positionInQueue;

        public final int queueSize;

        public final Work.Progress progress;

        public UploadStatus(String message, Work.Progress progress) {
            this(message, progress, 0, 0);
        }

        public UploadStatus(String message, Work.Progress progress, int positionInQueue, int queueSize) {
            this.message = message;
            this.progress = progress;
            this.positionInQueue = positionInQueue;
            this.queueSize = queueSize;
        }

        public String getMessage() {
            return message;
        }

        public Work.Progress getProgress() {
            return progress;
        }

        public int getPositionInQueue() {
            return positionInQueue;
        }

        public int getQueueSize() {
            return queueSize;
        }
    }

    public boolean canPublish(String provider) {
        return getMediaPublishingService().getProvider(provider).isAvailable();
    }

    public boolean isMediaAvailable(DocumentModel doc, String provider) {
        PublishableMedia media = doc.getAdapter(PublishableMedia.class);
        return getMediaPublishingService().getProvider(provider).isMediaAvailable(media);
    }
}
