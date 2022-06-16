package fr.dila.st.ui.bean;

import com.google.common.base.Strings;
import fr.dila.st.core.util.ResourceHelper;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class Breadcrumb {
    public static final int TITLE_ORDER = 1000;
    public static final int SUBTITLE_ORDER = 10000;
    public static final int MAX_TITLE_LENGTH = 30;
    public static final String USER_SESSION_KEY = "navigationContext";
    private String key;
    private String url;
    private Integer order;

    public Breadcrumb(String key, String url, Integer order) {
        super();
        this.key = key;
        this.url = url;
        this.order = order;
    }

    public Breadcrumb(String title, String baseUrl, Integer order, HttpServletRequest request) {
        String url = baseUrl;
        String params = Strings.nullToEmpty(request.getQueryString());

        if (StringUtils.isNotBlank(params) && !url.contains("?")) {
            url = url + "?" + params;
        }

        this.url = url;
        this.key = title;
        this.order = order;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isThisMenu(String menuKey) {
        if (menuKey == null) {
            return false;
        } else {
            return menuKey.equalsIgnoreCase(key);
        }
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;

        if (this == obj) {
            result = true;
        }
        if (obj == null) {
            result = false;
        }
        if (result) {
            if (getClass() != obj.getClass()) {
                result = false;
            }
            Breadcrumb other = (Breadcrumb) obj;
            if (key == null) {
                if (other.key != null) {
                    result = false;
                }
            } else if (!key.equals(other.key)) {
                result = false;
            }
            if (order == null) {
                if (other.order != null) {
                    result = false;
                }
            } else if (!order.equals(other.order)) {
                result = false;
            }
        }
        return result;
    }

    public int getTitleLength() {
        String title = (key == null ? "" : key);

        if (title.contains(".")) {
            title = ResourceHelper.getString(key);
        }

        return title != null ? title.length() : 0;
    }

    public boolean mustBeShortened() {
        return getTitleLength() > MAX_TITLE_LENGTH;
    }
}
