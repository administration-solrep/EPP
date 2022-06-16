package fr.dila.st.core.client;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Couche de transport mapDTO
 *
 */
public abstract class AbstractMapDTO extends HashMap<String, Serializable> {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3436617575736830858L;

    @Override
    public String toString() {
        final StringBuilder display = new StringBuilder("MAP");
        for (Map.Entry<String, Serializable> entry : this.entrySet()) {
            display.append("[").append(entry.getKey()).append(":").append(entry.getValue()).append("]");
        }
        return display.toString();
    }

    protected String getString(String key) {
        return (String) get(key);
    }

    protected Integer getInteger(String key) {
        return (Integer) get(key);
    }

    protected Calendar getCalendar(String key) {
        return (Calendar) get(key);
    }

    protected Date getDate(String key) {
        return (Date) get(key);
    }

    protected Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    protected String[] getStringArray(String key) {
        return (String[]) get(key);
    }

    protected Long getLong(String key) {
        return (Long) get(key);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getListString(String key) {
        return (List<String>) get(key);
    }

    protected void putListString(String key, List<String> list) {
        put(key, (Serializable) list);
    }

    /**
     * Type de l'objet représenté par le DTO
     *
     * @return
     */
    public abstract String getType();

    /**
     * UUID de l'objet selectionnable
     *
     * @return
     */
    public abstract String getDocIdForSelection();

    @SuppressWarnings("unchecked")
    protected List<Calendar> getListCalendar(String key) {
        return (List<Calendar>) get(key);
    }

    protected void putListCalendar(String key, List<Calendar> list) {
        put(key, (Serializable) list);
    }
}
