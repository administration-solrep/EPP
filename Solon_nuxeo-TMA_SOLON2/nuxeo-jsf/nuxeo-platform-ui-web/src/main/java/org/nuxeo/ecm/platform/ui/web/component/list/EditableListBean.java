/*
 * (C) Copyright 2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id: EditableListBean.java 25566 2007-10-01 14:01:21Z atchertchian $
 */

package org.nuxeo.ecm.platform.ui.web.component.list;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.ui.web.model.EditableModel;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;

/**
 * Bean used to interact with {@link UIEditableList} component.
 * <p>
 * Used to add/remove items from a list.
 * <p>
 * Optionally used to work around some unwanted behaviour in data tables.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public class EditableListBean {

    private static final Log log = LogFactory.getLog(EditableListBean.class);

    public static final String FOR_PARAMETER_NAME = "for";

    public static final String INDEX_PARAMETER_NAME = "index";

    public static final String TYPE_PARAMETER_NAME = "type";

    public static final String NUMBER_PARAMETER_NAME = "number";

    protected UIComponent binding;

    // dont make it static so that jsf can call it
    public UIComponent getBinding() {
        return binding;
    }

    // dont make it static so that jsf can call it
    public void setBinding(UIComponent binding) {
        this.binding = binding;
    }

    // don't make it static so that jsf can call it
    public void performAction(String listComponentId, String index, String type) {
        if (binding == null) {
            log.error("Component binding not set, cannot perform action");
            return;
        }
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(FOR_PARAMETER_NAME, listComponentId);
        requestMap.put(INDEX_PARAMETER_NAME, index);
        requestMap.put(TYPE_PARAMETER_NAME, type);
        performAction(binding, requestMap);
    }

    // don't make it static so that jsf can call it
    public void performAction(ActionEvent event) {
        UIComponent component = event.getComponent();
        if (component == null) {
            return;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext eContext = context.getExternalContext();
        Map<String, String> requestMap = eContext.getRequestParameterMap();
        performAction(component, requestMap);
    }

    /**
     * Resets all {@link UIEditableList} components cached model in first
     * container found thanks to given event
     *
     * @since 5.3.1
     */
    public void resetAllListsCachedModels(ActionEvent event) {
        UIComponent component = event.getComponent();
        if (component == null) {
            return;
        }
        // take first anchor and force flush on every list component
        UIComponent anchor = ComponentUtils.getBase(component);
        resetListCachedModels(anchor);
    }

    protected void resetListCachedModels(UIComponent parent) {
        if (parent == null) {
            return;
        }
        if (parent instanceof UIEditableList) {
            ((UIEditableList) parent).resetCachedModel();
        }
        List<UIComponent> children = parent.getChildren();
        if (children != null && !children.isEmpty()) {
            for (UIComponent child : children) {
                resetListCachedModels(child);
            }
        }
    }

    /**
     * Returns a new template, unreferenced from the given one
     */
    private static Object getUnreferencedTemplate(Object template) {
        if (!(template instanceof Serializable)) {
            return template;
        }
        try {
            // serialize
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(template);
            oos.close();
            // deserialize to make sure it is not the same instance
            byte[] pickled = out.toByteArray();
            InputStream in = new ByteArrayInputStream(pickled);
            ObjectInputStream ois = new ObjectInputStream(in);
            Object newTemplate = ois.readObject();
            return newTemplate;
        } catch (IOException e) {
            return template;
        } catch (ClassNotFoundException e) {
            return template;
        }
    }

    protected static void performAction(UIComponent binding,
            Map<String, String> requestMap) {
        UIEditableList editableComp = getEditableListComponent(binding,
                requestMap);
        if (editableComp == null) {
            return;
        }
        EditableListModificationType type = getModificationType(requestMap);
        if (type == null) {
            return;
        }
        Integer index;
        Integer number;

        EditableModel model = editableComp.getEditableModel();
        Object template = editableComp.getTemplate();
        switch (type) {
        case ADD:
            number = getNumber(requestMap);
            if (number == null) {
                // perform add only once
                model.addValue(template);
            } else {
                for (int i = 0; i < number; i++) {
                    model.addValue(getUnreferencedTemplate(template));
                }
            }
            break;
        case INSERT:
            index = getIndex(requestMap);
            if (index == null) {
                return;
            }
            number = getNumber(requestMap);
            if (number == null) {
                // perform insert only once
                model.insertValue(index, template);
            } else {
                for (int i = 0; i < number; i++) {
                    model.insertValue(index, getUnreferencedTemplate(template));
                }
            }
            break;
        case REMOVE:
            index = getIndex(requestMap);
            if (index == null) {
                return;
            }
            model.removeValue(index);
            break;
        }
    }

    protected static String getParameterValue(Map<String, String> requestMap,
            String parameterName) {
        String string = requestMap.get(parameterName);
        if (string == null || string.length() == 0) {
            return null;
        } else {
            return string;
        }
    }

    protected static UIEditableList getEditableListComponent(
            UIComponent component, Map<String, String> requestMap) {
        UIEditableList listComponent = null;
        String forString = getParameterValue(requestMap, FOR_PARAMETER_NAME);
        if (forString == null) {
            log.error(String.format(
                    "Could not find '%s' parameter in the request map",
                    FOR_PARAMETER_NAME));
        } else {
            try {
                UIComponent forComponent = component.findComponent(forString);
                if (forComponent == null) {
                    log.error("Could not find component with id: " + forString);
                } else if (!(forComponent instanceof UIEditableList)) {
                    log.error(String.format(
                            "Invalid component with id %s: %s, expected a "
                                    + "component with class %s", forString,
                            forComponent, UIEditableList.class));
                } else {
                    listComponent = (UIEditableList) forComponent;
                }
            } catch (Exception e) {
                log.error("Caught exception while looking for component "
                        + "with id: " + forString);
            }
        }
        return listComponent;
    }

    protected static EditableListModificationType getModificationType(
            Map<String, String> requestMap) {
        EditableListModificationType type = null;
        String typeString = getParameterValue(requestMap, TYPE_PARAMETER_NAME);
        if (typeString == null) {
            log.error(String.format(
                    "Could not find '%s' parameter in the request map",
                    TYPE_PARAMETER_NAME));
        } else {
            try {
                type = EditableListModificationType.valueOfString(typeString);
            } catch (IllegalArgumentException err) {
                log.error(String.format(
                        "Illegal value for '%s' attribute: %s, "
                                + "should be one of %s", TYPE_PARAMETER_NAME,
                        typeString, EditableListModificationType.values()));
            }
        }
        return type;
    }

    protected static Integer getIndex(Map<String, String> requestMap) {
        Integer index = null;
        String indexString = getParameterValue(requestMap, INDEX_PARAMETER_NAME);
        if (indexString == null) {
            log.error(String.format(
                    "Could not find '%s' parameter in the request map",
                    INDEX_PARAMETER_NAME));
        } else {
            try {
                index = Integer.valueOf(indexString);
            } catch (Exception e) {
                log.error(String.format(
                        "Illegal value for '%s' attribute: %s, "
                                + "should be integer", INDEX_PARAMETER_NAME,
                        indexString));
            }
        }
        return index;
    }

    protected static Integer getNumber(Map<String, String> requestMap) {
        Integer number = null;
        String numberString = getParameterValue(requestMap,
                NUMBER_PARAMETER_NAME);
        if (numberString != null) {
            try {
                number = Integer.valueOf(numberString);
            } catch (Exception e) {
                log.error(String.format(
                        "Illegal value for '%s' attribute: %s, "
                                + "should be integer", NUMBER_PARAMETER_NAME,
                        numberString));
            }
        }
        return number;
    }

    /**
     * Dummy list of one item, used to wrap a table within another table.
     * <p>
     * A table resets its saved state when decoding, which is a problem when
     * saving a file temporarily: as it will not be submitted again in the
     * request, the new value will be lost. The table is not reset when embedded
     * in another table, so we can use this list as value of the embedding table
     * as a work around.
     *
     * @return dummy list of one item
     */
    // don't make it static so that jsf can call it
    public List<Object> getDummyList() {
        List<Object> dummy = new ArrayList<Object>(1);
        dummy.add("dummy");
        return dummy;
    }

}
