/*
 * (C) Copyright 2014-2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Nicolas Chapurlat <nchapurlat@nuxeo.com>
 */

package org.nuxeo.ecm.core.schema.types.constraints;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.i18n.I18NUtils;

/**
 * <p>
 * This constraint ensures some date representation is in an enumeration.
 * </p>
 * <p>
 * This constraint can validate any {@link Date} or {@link Calendar}. This constraint also support {@link Number} types
 * whose long value is recognised as number of milliseconds since January 1, 1970, 00:00:00 GMT.
 * </p>
 *
 * @since 7.1
 */
public abstract class AbstractConstraint implements Constraint {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(AbstractConstraint.class);

    private static final String HARD_CODED_CONTRAINT_ERROR_MESSAGE = "The constraint '%s' failed for value %s";

    @Override
    public final String toString() {
        return getDescription().toString();
    }

    @Override
    public String getErrorMessage(Object invalidValue, Locale locale) {
        // test whether there's a constraint specific translation
        // the expected key is label.schema.constraint.violation.[TheConstraintName]
        // if there's none, replies to a generic message
        // the expected key is label.schema.constraint.violation
        // if there's none, replies to a hard coded message
        List<String> pathTokens = new ArrayList<>();
        pathTokens.add(MESSAGES_KEY);
        pathTokens.add(getDescription().getName());
        String keyConstraint = StringUtils.join(pathTokens, '.');
        String computedInvalidValue = "null";
        if (invalidValue != null) {
            String invalidValueString = invalidValue.toString();
            if (invalidValueString.length() > 20) {
                computedInvalidValue = invalidValueString.substring(0, 15) + "...";
            } else {
                computedInvalidValue = invalidValueString;
            }
        }
        Object[] params = new Object[] { computedInvalidValue };
        Locale computedLocale = locale != null ? locale : Constraint.MESSAGES_DEFAULT_LANG;
        String message = getMessageString(MESSAGES_BUNDLE, keyConstraint, params, computedLocale);

        if (message != null && !message.trim().isEmpty() && !keyConstraint.equals(message)) {
            // use a constraint specific message if there's one
            return message;
        } else {
            params = new Object[] { computedInvalidValue, toString() };
            message = getMessageString(MESSAGES_BUNDLE, MESSAGES_KEY, params, computedLocale);
            if (message != null && !message.trim().isEmpty() && !keyConstraint.equals(message)) {
                // use a generic message if there's one
                return message;
            } else {
                // use a hard coded message
                return String.format(HARD_CODED_CONTRAINT_ERROR_MESSAGE, toString(), computedInvalidValue);
            }
        }
    }

    /**
     * Try to get the message from the given message bundle. If the bundle is not found or the key is not found, return
     * null.
     *
     * @since 7.2
     */
    public static String getMessageString(String bundleName, String key, Object[] params, Locale locale) {
        try {
            return I18NUtils.getMessageString(MESSAGES_BUNDLE, key, params, locale);
        } catch (MissingResourceException e) {
            log.trace("No bundle found", e);
            return null;
        }
    }

}
