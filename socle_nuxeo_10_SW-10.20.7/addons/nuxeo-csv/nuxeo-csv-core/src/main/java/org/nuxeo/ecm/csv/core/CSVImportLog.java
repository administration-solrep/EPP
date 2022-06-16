/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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

package org.nuxeo.ecm.csv.core;

import org.nuxeo.common.utils.i18n.I18NUtils;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author <a href="mailto:troger@nuxeo.com">Thomas Roger</a>
 * @since 5.7
 */
public class CSVImportLog implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        SUCCESS, SKIPPED, ERROR
    }

    protected final long line;

    protected final Status status;

    protected final String message;

    protected final String localizedMessage;

    protected final String[] params;

    public CSVImportLog(long line, Status status, String message, String localizedMessage, String... params) {
        this.line = line;
        this.status = status;
        this.message = message;
        this.localizedMessage = localizedMessage;
        this.params = params;
    }

    public long getLine() {
        return line;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }

    public Object[] getLocalizedMessageParams() {
        return params;
    }

    public String getI18nMessage(Locale locale) {
        return I18NUtils.getMessageString("messages", getLocalizedMessage(), getLocalizedMessageParams(), locale);
    }

    public String getI18nMessage() {
        return getI18nMessage(Locale.ENGLISH);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isSkipped() {
        return status == Status.SKIPPED;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

}
