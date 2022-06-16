/*
 * (C) Copyright 2006-2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Nuxeo - Thierry Delprat <tdelprat@nuxeo.com> - Mock implementation
 *     Nicolas Chapurlat <nchapurlat@nuxeo.com>
 */

package org.nuxeo.ecm.core.schema.types.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * This constraint ensures some object's String representation is in an enumeration.
 * </p>
 *
 * @since 7.1
 */
public class EnumConstraint extends AbstractConstraint {

    private static final long serialVersionUID = 1L;

    private static final String NAME = "EnumConstraint";

    private static final String PNAME_VALUES = "Values";

    protected final Set<String> possibleValues;

    /**
     * Supports any objects, use their String representation.
     */
    public EnumConstraint(List<?> possibleValues) {
        this.possibleValues = new HashSet<>();
        for (Object possibleValue : possibleValues) {
            this.possibleValues.add(possibleValue.toString());
        }
    }

    public EnumConstraint(Object... possibleValues) {
        this.possibleValues = new HashSet<>();
        for (Object possibleValue : possibleValues) {
            this.possibleValues.add(possibleValue.toString());
        }
    }

    @Override
    public boolean validate(Object object) {
        if (object == null) {
            return true;
        }
        return possibleValues.contains(object.toString());
    }

    /**
     * Here, value is : <br>
     * name = {@value #NAME} <br>
     * parameter =
     * <ul>
     * <li>{@value #PNAME_VALUES} : List[value1, value2, value3]</li>
     * </ul>
     */
    @Override
    public Description getDescription() {
        Map<String, Serializable> params = new HashMap<>();
        params.put(EnumConstraint.PNAME_VALUES, new ArrayList<>(possibleValues));
        return new Description(EnumConstraint.NAME, params);
    }

    public Set<String> getPossibleValues() {
        return Collections.unmodifiableSet(possibleValues);
    }

    @Override
    public String getErrorMessage(Object invalidValue, Locale locale) {
        // test whether there's a custom translation for this field constraint specific translation
        // the expected key is label.schema.constraint.violation.[ConstraintName]
        // follow the AbstractConstraint behavior otherwise
        List<String> pathTokens = new ArrayList<>();
        pathTokens.add(MESSAGES_KEY);
        pathTokens.add(EnumConstraint.NAME);
        String key = StringUtils.join(pathTokens, '.');
        Object[] params = new Object[] { StringUtils.join(getPossibleValues(), ", ") };
        Locale computedLocale = locale != null ? locale : Constraint.MESSAGES_DEFAULT_LANG;
        String message = getMessageString(MESSAGES_BUNDLE, key, params, computedLocale);
        if (message != null && !message.trim().isEmpty() && !key.equals(message)) {
            // use a custom constraint message if there's one
            return message;
        } else {
            // follow AbstractConstraint behavior otherwise
            return super.getErrorMessage(invalidValue, computedLocale);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((possibleValues == null) ? 0 : possibleValues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EnumConstraint other = (EnumConstraint) obj;
        if (possibleValues == null) {
            if (other.possibleValues != null) {
                return false;
            }
        } else if (!possibleValues.equals(other.possibleValues)) {
            return false;
        }
        return true;
    }

}
