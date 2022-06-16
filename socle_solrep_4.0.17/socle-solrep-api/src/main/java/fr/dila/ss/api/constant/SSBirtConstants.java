package fr.dila.ss.api.constant;

public class SSBirtConstants {

    /**
     * Default constructor
     */
    public SSBirtConstants() {
        // do nothing
    }

    public static final String BIRT_REPORT_ROOT = "birtReports/";

    /**
     * Nom du paramètre (fichier de conf) contenant le chemin vers le fichier de conf log4j2
     */
    public static final String BIRT_APP_LOG_CONFIG_FILE_PROP = "solon.birt.app.logconfigfile";

    /**
     * Paramètre (fichier de conf) contenant le classpath complet de l'application Birt.
     */
    public static final String BIRT_APP_CLASSPATH_PROP = "solon.birt.app.classpath";

    /**
     * Paramètre (fichier de conf) contenant la classe principale de l'application Birt.
     */
    public static final String BIRT_APP_MAIN_CLASS_PROP = "solon.birt.app.mainclass";

    /**
     * Paramètre (fichier de conf) contenant le chemin local vers le fichier de conf générique de l'application Birt.
     */
    public static final String BIRT_APP_CONFIG_FILE_PROP = "solon.birt.app.configfile";

    /**
     * Paramètre (fichier de conf) contenant le chemin local vers le fichier de configuration xml des rapports Birt.
     */
    public static final String BIRT_REPORTS_LIST_FILE_PROP = "solon.birt.reports.list.file";
}
