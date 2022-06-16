/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Thomas Roger
 */

package org.nuxeo.wopi;

/**
 * @since 10.3
 */
public class Constants {

    private Constants() {
        // constants class
    }

    public static final String ACCESS_TOKEN_ATTRIBUTE = "accessToken";

    public static final String ACCESS_TOKEN_PARAMETER = "access_token";

    public static final String ACCESS_TOKEN_TTL_ATTRIBUTE = "accessTokenTTL";

    public static final String ACTION_CONVERT = "convert";

    public static final String ACTION_EDIT = "edit";

    public static final String ACTION_VIEW = "view";

    public static final String FILES_ENDPOINT_PATH = "site/wopi/files/";

    public static final String FILE_SCHEMA = "file";

    public static final String FORM_URL = "formURL";

    public static final int JWT_TOKEN_TTL = 60 * 60; // 1h

    public static final String NOTIFICATION_DOCUMENT_ID_CODEC_NAME = "notificationDocId";

    public static final String OPERATION_CHECK_FILE_INFO = "CheckFileInfo";

    public static final String OPERATION_GET_FILE = "GetFile";

    public static final String OPERATION_GET_LOCK = "GetLock";

    public static final String OPERATION_GET_SHARE_URL = "GetShareUrl";

    public static final String OPERATION_LOCK = "Lock";

    public static final String OPERATION_PUT_FILE = "PutFile";

    public static final String OPERATION_PUT_RELATIVE_FILE = "PutRelativeFile";

    public static final String OPERATION_REFRESH_LOCK = "RefreshLock";

    public static final String OPERATION_RENAME_FILE = "RenameFile";

    public static final String OPERATION_UNLOCK = "Unlock";

    public static final String OPERATION_UNLOCK_AND_RELOCK = "UnlockAndRelock";

    public static final String WOPI_BASE_URL_PROPERTY = "nuxeo.wopi.baseURL";

    public static final String WOPI_DISCOVERY_KEY = "wopiDiscovery";

    public static final String WOPI_DISCOVERY_REFRESH_EVENT = "refreshWOPIDiscovery";

    public static final String WOPI_DISCOVERY_URL_PROPERTY = "nuxeo.wopi.discoveryURL";

    public static final String WOPI_JSP = "/wopi.jsp";

    public static final String WOPI_KEY_VALUE_STORE_NAME = "wopi";

    public static final String WOPI_SERVLET_PATH = "wopi";

    public static final String WOPI_SOURCE = "wopi";

    public static final String WOPI_SRC = "WOPISrc";

    // -------- WOPI locks directory ---------------

    public static final String LOCK_DIRECTORY_DOC_ID = "docId";

    public static final String LOCK_DIRECTORY_FILE_ID = "fileId";

    public static final String LOCK_DIRECTORY_LOCK = "lock";

    public static final String LOCK_DIRECTORY_NAME = "wopiLocks";

    public static final String LOCK_DIRECTORY_REPOSITORY = "repository";

    public static final String LOCK_DIRECTORY_SCHEMA_NAME = "wopiLocks"; // NOSONAR

    public static final String LOCK_DIRECTORY_TIMESTAMP = "timestamp";

    public static final String LOCK_EXPIRATION_EVENT = "wopiLocksExpiration";

    public static final long LOCK_TTL = 30L * 60 * 1000; // 30 minutes

    // -------- End WOPI locks directory ---------------

    // -------- CheckFileInfo ---------------

    // -------- Required properties ---------------

    public static final String BASE_FILE_NAME = "BaseFileName";

    public static final String OWNER_ID = "OwnerId";

    public static final String SIZE = "Size";

    public static final String USER_ID = "UserId";

    public static final String VERSION = "Version";

    // -------- Host capabilities properties ---------------

    public static final String SUPPORTS_EXTENDED_LOCK_LENGTH = "SupportsExtendedLockLength";

    public static final String SUPPORTS_GET_LOCK = "SupportsGetLock";

    public static final String SUPPORTS_LOCKS = "SupportsLocks";

    public static final String SUPPORTS_RENAME = "SupportsRename";

    public static final String SUPPORTS_UPDATE = "SupportsUpdate";

    public static final String SUPPORTED_SHARE_URL_TYPES = "SupportedShareUrlTypes";

    // -------- User metadata properties ---------------

    public static final String IS_ANONYMOUS_USER = "IsAnonymousUser";

    public static final String LICENSE_CHECK_FOR_EDIT_IS_ENABLED = "LicenseCheckForEditIsEnabled";

    public static final String USER_FRIENDLY_NAME = "UserFriendlyName";

    // -------- User permissions properties ---------------

    public static final String READ_ONLY = "ReadOnly";

    public static final String USER_CAN_RENAME = "UserCanRename";

    public static final String USER_CAN_WRITE = "UserCanWrite";

    public static final String USER_CAN_NOT_WRITE_RELATIVE = "UserCanNotWriteRelative";

    // -------- File URL properties ---------------

    public static final String CLOSE_URL = "CloseUrl";

    public static final String DOWNLOAD_URL = "DownloadUrl";

    public static final String FILE_VERSION_URL = "FileVersionUrl";

    public static final String HOST_EDIT_URL = "HostEditUrl";

    public static final String HOST_VIEW_URL = "HostViewUrl";

    public static final String SIGNOUT_URL = "SignoutUrl";

    // -------- Breadcrumb properties ---------------

    public static final String BREADCRUMB_BRAND_NAME = "BreadcrumbBrandName";

    public static final String BREADCRUMB_BRAND_URL = "BreadcrumbBrandUrl";

    public static final String BREADCRUMB_FOLDER_NAME = "BreadcrumbFolderName";

    public static final String BREADCRUMB_FOLDER_URL = "BreadcrumbFolderUrl";

    // -------- End CheckFileInfo ---------------

    // -------- Rename and PutRelativeFile ---------------

    public static final String NAME = "Name";

    public static final String URL = "Url";

    // -------- End Rename and PutRelativeFile ---------------

    // -------- GetShareUrl ---------------

    public static final String SHARE_URL = "ShareUrl";

    public static final String SHARE_URL_READ_ONLY = "ReadOnly"; // NOSONAR

    public static final String SHARE_URL_READ_WRITE = "ReadWrite";

    // -------- End GetShareUrl ---------------

}
