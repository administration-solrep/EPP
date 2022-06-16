package fr.dila.st.api.constant;

/**
 * Paramètres de l'application.
 *
 * @author jtremeaux
 */
public final class STConfigConstants {
    /**
     * Activation du bulk mode.
     */
    public static final String NUXEO_BULK_MODE = "nuxeo.bulk.mode";

    /**
     * Tag de l'application : numéro de version et date du build.
     */
    public static final String PRODUCT_TAG = "application.product.tag";

    /**
     * Tag du build de l'application : revision git
     */
    public static final String BUILD_TAG = "application.product.build";

    /**
     * URL du serveur d'application.
     */
    public static final String SERVER_URL = "nuxeo.url";

    /**
     * Port du serveur d'application.
     */
    public static final String TOMCAT_PORT = "nuxeo.server.http.port";

    /**
     * Nom du serveur d'application.
     */
    public static final String SERVER_HOST = "org.nuxeo.ecm.instance.host";

    /**
     * Nom de l'utilisateur qui exécute les batch.
     */
    public static final String NUXEO_BATCH_USER = "nuxeo.batch.user";

    /**
     * Nombre de document à partir du duquel on créé un nouveau jeton maître dont le numéro a été incrémenté de 1.
     */
    public static final String WEBSERVICE_JETON_RESULT_SIZE = "jeton.result.size";

    /**
     * URL de légifrance.
     */
    public static final String LEGIFRANCE_JORF_URL = "legifrance.jorf.url";

    /**
     * mail.from
     */
    public static final String MAIL_FROM = "mail.from";

    /**
     * Préfixe des objets des mails envoyés par la plateforme par environnement
     */
    public static final String SOLON_MAIL_PREFIX_OBJECT = "solon.mail.prefix.object";

    /**
     * Préfixe des corps des mails envoyés par la plateforme par environnement
     */
    public static final String SOLON_MAIL_PREFIX_BODY = "solon.mail.prefix.body";

    /**
     * Préfixe de l'adresse pour les mails envoysé par la plateforme par environnement
     */
    public static final String SOLON_MAIL_PREFIX_FROM = "solon.mail.prefix.from";

    /**
     * Lien de l'application utilisé au sein des mails
     */
    public static final String SOLON_MAIL_URL_APPLICATION = "solon.mail.url.application";

    /**
     * FEV521 : Libellé d'identification de la plateforme.
     */
    public static final String SOLON_IDENTIFICATION_PLATEFORME_LIBELLE = "solon.identification.plateforme.libelle";

    //SOLON2NG : Nom de la plateforme
    public static final String SOLON_IDENTIFICATION_PLATEFORME_NAME = "solon.identification.plateforme.name";
    /**
     * FEV521 : Couleur du libellé d'identification de la plateforme.
     */
    public static final String SOLON_IDENTIFICATION_PLATEFORME_COULEUR = "solon.identification.plateforme.couleur";
    /**
     * FEV521 (EPG) : Nom de l'image d'identification de la plateforme (page de login).
     */
    public static final String SOLON_IDENTIFICATION_PLATEFORME_BACKGROUND =
        "solon.identification.plateforme.background";

    /**
     * FEV521 (EPP) : Couleur de fond d'identification de la plateforme (page de login).
     */
    public static final String SOLON_IDENTIFICATION_PLATEFORME_COULEURBG = "solon.identification.plateforme.couleurbg";

    /**
     * FEV552 : limite d'envoi de mails en masses
     */
    public static final String SEND_MAIL_LIMIT = "mail.masse.limit";

    /**
     * FEV552 : délai entre deux mails envoyés en masse
     */
    public static final String SEND_MAIL_DELAY = "mail.masse.delay";

    /**
     * FEV580 : mail notification redémarrage feuille de route
     */
    public static final String SEND_MAIL_REDEMARRAGE_FDR = "reponses.notification.mail.redemarrage.fdr";

    /**
     * Clé du paramètre (fichier .conf) renseignant le chemin complet du répertoire
     * où sont déposés les fichiers temporaires.
     */
    public static final String APP_FOLDER_TMP = "app.folder.tmp";

    /**
     * Clé du paramètre (fichier .conf) renseignant le chemin complet du binary store.
     */
    public static final String REPOSITORY_BINARY_STORE_PATH = "repository.binary.store";

    //SOLON2NG : Répertoire du manuel utilisateur
    public static final String SOLON_MANUEL_FOLDER = "solon.manuel.chemin.repertoire";

    //SOLON2NG : Nom du fichier du manuel utilisateur
    public static final String SOLON_MANUEL_FILE = "solon.manuel.fichier";

    /**
     * Tomcat jvm namespace pour l'application courante
     */
    public static final String SOLON_TOMCAT_NAMESPACE = "solon.tomcat.namespace";

    public static final String ADMIN_SYSTEMP = "solon.default.system.password";

    public static final String NUXEO_HOSTS = "nuxeo.hosts";

    /**
     * utility class
     */
    private STConfigConstants() {
        // do nothing
    }
}
