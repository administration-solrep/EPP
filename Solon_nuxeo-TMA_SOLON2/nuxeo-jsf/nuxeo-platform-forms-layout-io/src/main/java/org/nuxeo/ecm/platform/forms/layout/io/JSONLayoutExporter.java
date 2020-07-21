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
package org.nuxeo.ecm.platform.forms.layout.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.Base64;
import org.nuxeo.ecm.platform.forms.layout.api.FieldDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.LayoutDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.LayoutRowDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.WidgetDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.WidgetSelectOption;
import org.nuxeo.ecm.platform.forms.layout.api.WidgetSelectOptions;
import org.nuxeo.ecm.platform.forms.layout.api.WidgetTypeConfiguration;
import org.nuxeo.ecm.platform.forms.layout.api.WidgetTypeDefinition;
import org.nuxeo.ecm.platform.forms.layout.api.impl.FieldDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.LayoutDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.LayoutRowDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.WidgetDefinitionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.WidgetSelectOptionImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.WidgetSelectOptionsImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.WidgetTypeConfigurationImpl;
import org.nuxeo.ecm.platform.forms.layout.api.impl.WidgetTypeDefinitionComparator;
import org.nuxeo.ecm.platform.forms.layout.api.impl.WidgetTypeDefinitionImpl;

/**
 * JSON exporter for layout objects
 *
 * @author Anahide Tchertchian
 * @since 5.4.2
 */
public class JSONLayoutExporter {

    private static final Log log = LogFactory.getLog(JSONLayoutExporter.class);

    public static final String ENCODED_VALUES_ENCODING = "UTF-8";

    public static String encode(JSONObject jsonObject)
            throws UnsupportedEncodingException {
        String json = jsonObject.toString();
        String encodedValues = Base64.encodeBytes(json.getBytes(), Base64.GZIP
                | Base64.DONT_BREAK_LINES);
        return URLEncoder.encode(encodedValues, ENCODED_VALUES_ENCODING);
    }

    public static JSONObject decode(String json)
            throws UnsupportedEncodingException {
        String decodedValues = URLDecoder.decode(json, ENCODED_VALUES_ENCODING);
        json = new String(Base64.decode(decodedValues));
        return JSONObject.fromObject(json);
    }

    public static void export(WidgetTypeDefinition def, OutputStream out)
            throws IOException {
        JSONObject res = exportToJson(def);
        out.write(res.toString(2).getBytes(ENCODED_VALUES_ENCODING));
    }

    public static void export(List<WidgetTypeDefinition> defs, OutputStream out)
            throws IOException {
        JSONObject res = new JSONObject();
        if (defs != null) {
            // sort so that order is deterministic
            Collections.sort(defs, new WidgetTypeDefinitionComparator(false));
        }
        for (WidgetTypeDefinition def : defs) {
            res.element(def.getName(), exportToJson(def));
        }
        out.write(res.toString(2).getBytes(ENCODED_VALUES_ENCODING));
    }

    public static JSONObject exportToJson(WidgetTypeDefinition def) {
        JSONObject json = new JSONObject();
        json.element("name", def.getName());
        json.element("handlerClassName", def.getHandlerClassName());
        JSONObject props = exportStringPropsToJson(def.getProperties());
        if (!props.isEmpty()) {
            json.element("properties", props);
        }
        WidgetTypeConfiguration conf = def.getConfiguration();
        if (conf != null) {
            json.element("configuration", exportToJson(conf));
        }
        return json;
    }

    public static WidgetTypeDefinition importWidgetTypeDefinition(
            JSONObject jsonDef) {
        String name = jsonDef.optString("name");
        String handlerClass = jsonDef.optString("handlerClassName");
        Map<String, String> properties = importStringProps(jsonDef.optJSONObject("properties"));
        WidgetTypeConfiguration conf = importWidgetTypeConfiguration(jsonDef.optJSONObject("configuration"));
        return new WidgetTypeDefinitionImpl(name, handlerClass, properties,
                conf);
    }

    public static JSONObject exportToJson(WidgetTypeConfiguration conf) {
        JSONObject json = new JSONObject();
        json.element("title", conf.getTitle());
        json.element("description", conf.getDescription());
        String demoId = conf.getDemoId();
        if (demoId != null) {
            JSONObject demoInfo = new JSONObject();
            demoInfo.element("id", demoId);
            demoInfo.element("previewEnabled", conf.isDemoPreviewEnabled());
            json.element("demo", demoInfo);
        }
        json.element("sinceVersion", conf.getSinceVersion());
        JSONObject confProps = exportPropsToJson(conf.getConfProperties());
        if (!confProps.isEmpty()) {
            json.element("confProperties", confProps);
        }

        JSONArray supportedModes = new JSONArray();
        List<String> confSupportedModes = conf.getSupportedModes();
        if (confSupportedModes != null) {
            supportedModes.addAll(confSupportedModes);
        }
        if (!supportedModes.isEmpty()) {
            json.element("supportedModes", supportedModes);
        }

        json.element("acceptingSubWidgets", conf.isAcceptingSubWidgets());

        JSONObject fields = new JSONObject();
        fields.element("list", conf.isList());
        fields.element("complex", conf.isComplex());

        JSONArray supportedTypes = new JSONArray();
        List<String> confSupportedTypes = conf.getSupportedFieldTypes();
        if (confSupportedTypes != null) {
            supportedTypes.addAll(confSupportedTypes);
        }
        if (!supportedTypes.isEmpty()) {
            fields.element("supportedTypes", supportedTypes);
        }

        JSONArray defaultTypes = new JSONArray();
        List<String> confDefaultTypes = conf.getDefaultFieldTypes();
        if (confDefaultTypes != null) {
            defaultTypes.addAll(confDefaultTypes);
        }
        if (!defaultTypes.isEmpty()) {
            fields.element("defaultTypes", defaultTypes);
        }

        JSONArray defaultFieldDefs = new JSONArray();
        List<FieldDefinition> fieldDefs = conf.getDefaultFieldDefinitions();
        if (fieldDefs != null) {
            for (FieldDefinition fieldDef : fieldDefs) {
                defaultFieldDefs.add(exportToJson(fieldDef));
            }
        }
        if (!defaultFieldDefs.isEmpty()) {
            fields.element("defaultConfiguration", defaultFieldDefs);
        }

        json.element("fields", fields);

        JSONArray cats = new JSONArray();
        List<String> confCats = conf.getCategories();
        if (confCats != null) {
            cats.addAll(confCats);
        }
        if (!cats.isEmpty()) {
            json.element("categories", cats);
        }

        JSONObject props = new JSONObject();
        Map<String, List<LayoutDefinition>> confLayouts = conf.getPropertyLayouts();
        if (confLayouts != null) {
            List<String> modes = new ArrayList<String>(confLayouts.keySet());
            // sort so that order is deterministic
            Collections.sort(modes);
            JSONObject layouts = new JSONObject();
            for (String mode : modes) {
                JSONArray modeLayouts = new JSONArray();
                for (LayoutDefinition layoutDef : confLayouts.get(mode)) {
                    modeLayouts.add(exportToJson(layoutDef));
                }
                layouts.element(mode, modeLayouts);
            }
            if (!layouts.isEmpty()) {
                props.element("layouts", layouts);
            }
        }
        if (!props.isEmpty()) {
            json.element("properties", props);
        }
        return json;
    }

    @SuppressWarnings("unchecked")
    public static WidgetTypeConfiguration importWidgetTypeConfiguration(
            JSONObject conf) {
        if (conf == null) {
            return new WidgetTypeConfigurationImpl(null, null, null, null,
                    false, Collections.EMPTY_MAP, Collections.EMPTY_LIST,
                    false, false, false, Collections.EMPTY_LIST,
                    Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                    Collections.EMPTY_LIST, Collections.EMPTY_MAP);
        }
        String title = conf.getString("title");
        String description = conf.optString("description");
        String sinceVersion = conf.optString("sinceVersion");

        JSONObject demoInfo = conf.optJSONObject("demo");
        String demoId = null;
        boolean demoPreviewEnabled = false;
        if (demoInfo != null && !demoInfo.isNullObject()) {
            demoId = demoInfo.optString("id");
            demoPreviewEnabled = demoInfo.optBoolean("previewEnabled");
        }

        // String demoId = conf.getString(key)

        Map<String, Serializable> confProps = importProps(conf.optJSONObject("confProperties"));

        List<String> confSupportedModes = new ArrayList<String>();
        JSONArray supportedModes = conf.optJSONArray("supportedModes");
        if (supportedModes != null) {
            confSupportedModes.addAll(supportedModes);
        }

        boolean acceptingSubWidgets = conf.optBoolean("acceptingSubWidgets",
                false);

        JSONObject fields = conf.optJSONObject("fields");
        boolean list = false;
        boolean complex = false;
        List<String> confSupportedTypes = new ArrayList<String>();
        List<String> confDefaultTypes = new ArrayList<String>();
        List<FieldDefinition> defaultFieldDefinitions = new ArrayList<FieldDefinition>();
        if (fields != null && !fields.isNullObject()) {
            list = fields.optBoolean("list", false);
            complex = fields.optBoolean("complex", false);
            JSONArray supportedTypes = fields.optJSONArray("supportedTypes");
            if (supportedTypes != null) {
                confSupportedTypes.addAll(supportedTypes);
            }
            JSONArray defaultTypes = fields.optJSONArray("defaultTypes");
            if (defaultTypes != null) {
                confDefaultTypes.addAll(defaultTypes);
            }
            JSONArray jfields = fields.optJSONArray("defaultConfiguration");
            if (jfields != null) {
                for (Object item : jfields) {
                    defaultFieldDefinitions.add(importFieldDefinition((JSONObject) item));
                }
            }
        }

        JSONArray cats = conf.optJSONArray("categories");
        List<String> confCats = new ArrayList<String>();
        if (cats != null) {
            confCats.addAll(cats);
        }

        JSONObject props = conf.optJSONObject("properties");
        Map<String, List<LayoutDefinition>> confLayouts = new HashMap<String, List<LayoutDefinition>>();
        if (props != null && !props.isNullObject()) {
            JSONObject layouts = props.optJSONObject("layouts");
            if (layouts != null && !layouts.isNullObject()) {
                for (Object item : layouts.keySet()) {
                    String mode = (String) item;
                    List<LayoutDefinition> layoutDefs = new ArrayList<LayoutDefinition>();
                    JSONArray modeLayouts = layouts.getJSONArray(mode);
                    if (modeLayouts != null && !mode.isEmpty()) {
                        for (Object subitem : modeLayouts) {
                            layoutDefs.add(importLayoutDefinition((JSONObject) subitem));
                        }
                    }
                    confLayouts.put(mode, layoutDefs);
                }
            }
        }

        WidgetTypeConfiguration res = new WidgetTypeConfigurationImpl(
                sinceVersion, title, description, demoId, demoPreviewEnabled,
                confProps, confSupportedModes, acceptingSubWidgets, list,
                complex, confSupportedTypes, confDefaultTypes,
                defaultFieldDefinitions, confCats, confLayouts);

        return res;
    }

    public static JSONObject exportToJson(LayoutDefinition layoutDef) {
        JSONObject json = new JSONObject();
        json.element("name", layoutDef.getName());

        JSONObject templates = exportStringPropsToJson(layoutDef.getTemplates());
        if (!templates.isEmpty()) {
            json.element("templates", templates);
        }

        JSONObject props = exportPropsByModeToJson(layoutDef.getProperties());
        if (!props.isEmpty()) {
            json.element("properties", props);
        }

        JSONArray rows = new JSONArray();
        LayoutRowDefinition[] defRows = layoutDef.getRows();
        List<String> widgetsToExport = new ArrayList<String>();
        if (defRows != null) {
            for (LayoutRowDefinition layoutRowDef : defRows) {
                rows.add(exportToJson(layoutRowDef));
                String[] widgets = layoutRowDef.getWidgets();
                if (widgets != null) {
                    for (String widget : widgets) {
                        widgetsToExport.add(widget);
                    }
                }
            }
        }
        if (!rows.isEmpty()) {
            json.element("rows", rows);
        }

        JSONArray widgets = new JSONArray();
        for (String widgetName : widgetsToExport) {
            WidgetDefinition widgetDef = layoutDef.getWidgetDefinition(widgetName);
            if (widgetDef == null) {
                log.error(String.format(
                        "No local definition found for widget '%s' in layout '%s' "
                                + "=> cannot export", widgetName,
                        layoutDef.getName()));
                continue;
            }
            widgets.add(exportToJson(widgetDef));
        }
        if (!widgets.isEmpty()) {
            json.element("widgets", widgets);
        }

        return json;
    }

    public static LayoutDefinition importLayoutDefinition(JSONObject layoutDef) {
        String name = layoutDef.optString("name", null);
        Map<String, String> templates = importStringProps(layoutDef.optJSONObject("templates"));
        Map<String, Map<String, Serializable>> properties = importPropsByMode(layoutDef.optJSONObject("properties"));

        List<LayoutRowDefinition> rows = new ArrayList<LayoutRowDefinition>();
        JSONArray jrows = layoutDef.optJSONArray("rows");
        if (jrows != null) {
            for (Object item : jrows) {
                rows.add(importLayoutRowDefinition((JSONObject) item));
            }
        }

        List<WidgetDefinition> widgets = new ArrayList<WidgetDefinition>();
        JSONArray jwidgets = layoutDef.optJSONArray("widgets");
        if (jwidgets != null) {
            for (Object item : jwidgets) {
                widgets.add(importWidgetDefinition((JSONObject) item));
            }
        }

        LayoutDefinition res = new LayoutDefinitionImpl(name, properties,
                templates, rows, widgets);

        return res;
    }

    public static JSONObject exportToJson(LayoutRowDefinition layoutRowDef) {
        JSONObject json = new JSONObject();
        String name = layoutRowDef.getName();
        if (name != null) {
            json.element("name", name);
        }
        JSONObject props = exportPropsByModeToJson(layoutRowDef.getProperties());
        if (!props.isEmpty()) {
            json.element("properties", props);
        }
        JSONArray widgets = new JSONArray();
        String[] defWidgets = layoutRowDef.getWidgets();
        if (defWidgets != null) {
            for (String widget : defWidgets) {
                widgets.add(widget);
            }
        }
        if (!widgets.isEmpty()) {
            json.element("widgets", widgets);
        }
        return json;
    }

    public static LayoutRowDefinition importLayoutRowDefinition(
            JSONObject layoutRowDef) {
        String name = layoutRowDef.optString("name", null);
        Map<String, Map<String, Serializable>> properties = importPropsByMode(layoutRowDef.optJSONObject("properties"));

        List<String> widgets = new ArrayList<String>();
        JSONArray jwidgets = layoutRowDef.optJSONArray("widgets");
        if (jwidgets != null) {
            for (Object item : jwidgets) {
                widgets.add((String) item);
            }
        }

        return new LayoutRowDefinitionImpl(name, properties, widgets, false,
                true);
    }

    public static JSONObject exportToJson(WidgetDefinition widgetDef) {
        JSONObject json = new JSONObject();
        json.element("name", widgetDef.getName());
        json.element("type", widgetDef.getType());
        JSONObject labels = exportStringPropsToJson(widgetDef.getLabels());
        if (!labels.isEmpty()) {
            json.element("labels", labels);
        }
        JSONObject helpLabels = exportStringPropsToJson(widgetDef.getHelpLabels());
        if (!helpLabels.isEmpty()) {
            json.element("helpLabels", helpLabels);
        }
        json.element("translated", widgetDef.isTranslated());
        JSONObject widgetModes = exportStringPropsToJson(widgetDef.getModes());
        if (!widgetModes.isEmpty()) {
            json.element("widgetModes", widgetModes);
        }

        JSONArray fields = new JSONArray();
        FieldDefinition[] fieldDefs = widgetDef.getFieldDefinitions();
        if (fieldDefs != null) {
            for (FieldDefinition fieldDef : fieldDefs) {
                fields.add(exportToJson(fieldDef));
            }
        }
        if (!fields.isEmpty()) {
            json.element("fields", fields);
        }

        JSONArray subWidgets = new JSONArray();
        WidgetDefinition[] subWidgetDefs = widgetDef.getSubWidgetDefinitions();
        if (subWidgetDefs != null) {
            for (WidgetDefinition wDef : subWidgetDefs) {
                subWidgets.add(exportToJson(wDef));
            }
        }
        if (!subWidgets.isEmpty()) {
            json.element("subWidgets", subWidgets);
        }

        JSONObject props = exportPropsByModeToJson(widgetDef.getProperties());
        if (!props.isEmpty()) {
            json.element("properties", props);
        }
        JSONObject widgetModeProps = exportPropsByModeToJson(widgetDef.getWidgetModeProperties());
        if (!widgetModeProps.isEmpty()) {
            json.element("propertiesByWidgetMode", widgetModeProps);
        }

        JSONArray selectOptions = new JSONArray();
        WidgetSelectOption[] selectOptionDefs = widgetDef.getSelectOptions();
        if (selectOptionDefs != null) {
            for (WidgetSelectOption selectOptionDef : selectOptionDefs) {
                selectOptions.add(exportToJson(selectOptionDef));
            }
        }
        if (!selectOptions.isEmpty()) {
            json.element("selectOptions", selectOptions);
        }

        return json;
    }

    public static WidgetDefinition importWidgetDefinition(JSONObject widgetDef) {
        String name = widgetDef.getString("name");
        String type = widgetDef.getString("type");
        Map<String, String> labels = importStringProps(widgetDef.optJSONObject("labels"));
        Map<String, String> helpLabels = importStringProps(widgetDef.optJSONObject("helpLabels"));
        boolean translated = widgetDef.optBoolean("translated", false);
        Map<String, String> modes = importStringProps(widgetDef.optJSONObject("widgetModes"));

        List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
        JSONArray jfields = widgetDef.optJSONArray("fields");
        if (jfields != null) {
            for (Object item : jfields) {
                fieldDefinitions.add(importFieldDefinition((JSONObject) item));
            }
        }

        List<WidgetDefinition> subWidgets = new ArrayList<WidgetDefinition>();
        JSONArray jsubwidgets = widgetDef.optJSONArray("subWidgets");
        if (jsubwidgets != null) {
            for (Object item : jsubwidgets) {
                subWidgets.add(importWidgetDefinition((JSONObject) item));
            }
        }

        Map<String, Map<String, Serializable>> properties = importPropsByMode(widgetDef.optJSONObject("properties"));
        Map<String, Map<String, Serializable>> widgetModeProperties = importPropsByMode(widgetDef.optJSONObject("propertiesByWidgetMode"));

        // select options
        List<WidgetSelectOption> selectOptions = new ArrayList<WidgetSelectOption>();
        JSONArray jselectOptions = widgetDef.optJSONArray("selectOptions");
        if (jselectOptions != null) {
            for (Object item : jselectOptions) {
                selectOptions.add(importWidgetSelectionOption((JSONObject) item));
            }
        }

        WidgetDefinition res = new WidgetDefinitionImpl(name, type, labels,
                helpLabels, translated, modes,
                fieldDefinitions.toArray(new FieldDefinition[] {}), properties,
                widgetModeProperties,
                subWidgets.toArray(new WidgetDefinition[] {}),
                selectOptions.toArray(new WidgetSelectOption[] {}));

        return res;
    }

    public static JSONObject exportToJson(FieldDefinition fieldDef) {
        JSONObject json = new JSONObject();
        json.element("fieldName", fieldDef.getFieldName());
        json.element("schemaName", fieldDef.getSchemaName());
        json.element("propertyName", fieldDef.getPropertyName());
        return json;
    }

    public static FieldDefinition importFieldDefinition(JSONObject fieldDef) {
        // ignore property name: it can be deduced from schema and field name
        FieldDefinition res = new FieldDefinitionImpl(fieldDef.optString(
                "schemaName", null), fieldDef.getString("fieldName"));
        return res;
    }

    public static JSONObject exportToJson(WidgetSelectOption selectOption) {
        JSONObject json = new JSONObject();
        Serializable value = selectOption.getValue();
        boolean isMulti = selectOption instanceof WidgetSelectOptions;
        if (isMulti) {
            json.element("multiple", true);
        } else {
            json.element("multiple", false);
        }
        if (value != null) {
            json.element("value", value);
        }
        String var = selectOption.getVar();
        if (var != null) {
            json.element("var", var);
        }
        String itemLabel = selectOption.getItemLabel();
        if (itemLabel != null) {
            json.element("itemLabel", itemLabel);
        }
        String itemValue = selectOption.getItemValue();
        if (itemValue != null) {
            json.element("itemValue", itemValue);
        }
        Serializable itemDisabled = selectOption.getItemDisabled();
        if (itemDisabled != null) {
            json.element("itemDisabled", itemDisabled);
        }
        Serializable itemRendered = selectOption.getItemRendered();
        if (itemRendered != null) {
            json.element("itemRendered", itemRendered);
        }
        if (isMulti) {
            WidgetSelectOptions selectOptions = (WidgetSelectOptions) selectOption;
            String ordering = selectOptions.getOrdering();
            if (ordering != null) {
                json.element("ordering", ordering);
            }
            Boolean caseSensitive = selectOptions.getCaseSensitive();
            if (caseSensitive != null) {
                json.element("caseSensitive", caseSensitive);
            }
        }
        return json;
    }

    public static WidgetSelectOption importWidgetSelectionOption(
            JSONObject selectOption) {
        boolean isMulti = selectOption.getBoolean("multiple");
        Serializable value = selectOption.optString("value", null);
        String var = selectOption.optString("var", null);
        String itemLabel = selectOption.optString("itemLabel", null);
        String itemValue = selectOption.optString("itemValue", null);
        Serializable itemDisabled = selectOption.optString("itemDisabled", null);
        Serializable itemRendered = selectOption.optString("itemRendered", null);
        if (isMulti) {
            String ordering = selectOption.optString("ordering", null);
            Boolean caseSensitive = null;
            if (selectOption.has("caseSensitive")) {
                caseSensitive = new Boolean(
                        selectOption.getBoolean("caseSensitive"));
            }
            return new WidgetSelectOptionsImpl(value, var, itemLabel,
                    itemValue, itemDisabled, itemRendered, ordering,
                    caseSensitive);
        } else {
            return new WidgetSelectOptionImpl(value, var, itemLabel, itemValue,
                    itemDisabled, itemRendered);
        }
    }

    public static JSONObject exportPropsByModeToJson(
            Map<String, Map<String, Serializable>> propsByMode) {
        JSONObject props = new JSONObject();
        if (propsByMode != null) {
            List<String> defModes = new ArrayList<String>(propsByMode.keySet());
            // sort so that order is deterministic
            Collections.sort(defModes);
            for (String defMode : defModes) {
                props.element(defMode,
                        exportPropsToJson(propsByMode.get(defMode)));
            }
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Serializable>> importPropsByMode(
            JSONObject propsByMode) {
        Map<String, Map<String, Serializable>> props = new HashMap<String, Map<String, Serializable>>();
        if (propsByMode != null && !propsByMode.isNullObject()) {
            List<String> defModes = new ArrayList<String>(propsByMode.keySet());
            // sort so that order is deterministic
            Collections.sort(defModes);
            for (String defMode : defModes) {
                props.put(defMode,
                        importProps(propsByMode.getJSONObject(defMode)));
            }
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject exportPropsToJson(
            Map<String, Serializable> defProps) {
        JSONObject props = new JSONObject();
        if (defProps != null) {
            List<String> defPropNames = new ArrayList<String>(defProps.keySet());
            // sort so that order is deterministic
            Collections.sort(defPropNames);
            for (String defPropName : defPropNames) {
                Serializable value = defProps.get(defPropName);
                if (value instanceof Collection) {
                    JSONArray array = new JSONArray();
                    array.addAll((Collection) value);
                    props.element(defPropName, array);
                } else if (value instanceof Object[]) {
                    JSONArray array = new JSONArray();
                    for (Object item : (Object[]) value) {
                        array.add(item);
                    }
                    props.element(defPropName, array);
                } else {
                    props.element(defPropName, value);
                }
            }
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Serializable> importProps(JSONObject defProps) {
        Map<String, Serializable> props = new HashMap<String, Serializable>();
        if (defProps != null && !defProps.isNullObject()) {
            List<String> defPropNames = new ArrayList<String>(defProps.keySet());
            // sort so that order is deterministic
            Collections.sort(defPropNames);
            for (String defPropName : defPropNames) {
                Object value = defProps.opt(defPropName);
                if (value instanceof JSONArray) {
                    ArrayList<Object> listValue = new ArrayList<Object>();
                    listValue.addAll(((JSONArray) value));
                } else {
                    props.put(defPropName, value.toString());
                }
            }
        }
        return props;
    }

    public static JSONObject exportStringPropsToJson(
            Map<String, String> defProps) {
        JSONObject props = new JSONObject();
        if (defProps != null) {
            List<String> defPropNames = new ArrayList<String>(defProps.keySet());
            // sort so that order is deterministic
            Collections.sort(defPropNames);
            for (String defPropName : defPropNames) {
                props.element(defPropName, defProps.get(defPropName));
            }
        }
        return props;
    }

    public static Map<String, String> importStringProps(JSONObject defProps) {
        Map<String, String> props = new HashMap<String, String>();
        if (defProps != null && !defProps.isNullObject()) {
            for (Object item : defProps.keySet()) {
                String key = (String) item;
                props.put(key, defProps.getString(key));
            }
        }
        return props;
    }

}
