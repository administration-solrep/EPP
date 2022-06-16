package fr.dila.ss.core.birt.datasource;

/**
 * Wrapps a DataSource configuration
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
public class NuxeoDSConfig {
    protected String driverClass;

    protected String url;

    protected String userName;

    public static final String H2_PREFIX = "org.h2";

    public static final String PG_PREFIX = "org.postgresql";

    public static final String ORACLE_PREFIX = "oracle.jdbc";

    public NuxeoDSConfig(String driverClass, String url, String userName) {
        this.driverClass = driverClass;
        this.url = url;
        this.userName = userName;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("username:");
        sb.append(userName);
        sb.append("\ndriver:");
        sb.append(driverClass);
        sb.append("\nurl:");
        sb.append(url);
        return sb.toString();
    }
}
