package fr.dila.ss.api.constant;

import static java.lang.String.join;

import fr.dila.st.api.constant.STConstant;

public class ActualiteConstant {

    /**
     * utility class
     */
    private ActualiteConstant() {
        // do nothing
    }

    public static final String ACTUALITE_ROOT_PATH_NAME = "actualite-root";
    public static final String ACTUALITE_ROOT_PATH = join(
        "/",
        STConstant.CASE_MANAGEMENT_PATH,
        ACTUALITE_ROOT_PATH_NAME
    );
    public static final String ACTUALITE_ROOT_DOCUMENT_TYPE = "ActualiteRoot";
    public static final String ACTUALITE_REQUETE_DOCUMENT_TYPE = "ActualiteRequete";
    public static final String ACTUALITE_REQUETE_SCHEMA = "actualiteRequete";
    public static final String ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_DEBUT = "dateEmissionDebut";
    public static final String ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_FIN = "dateEmissionFin";
    public static final String ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_DEBUT = "dateValiditeDebut";
    public static final String ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_FIN = "dateValiditeFin";
    public static final String ACTUALITE_DOCUMENT_TYPE = "Actualite";
    public static final String ACTUALITE_SCHEMA = "actualite";
    public static final String ACTUALITE_SCHEMA_PREFIX = "act";
    public static final String ACTUALITE_PROPERTY_DATE_EMISSION = "dateEmission";
    public static final String ACTUALITE_PROPERTY_DATE_VALIDITE = "dateValidite";
    public static final String ACTUALITE_PROPERTY_DANS_HISTORIQUE = "dansHistorique";
    public static final String ACTUALITE_PROPERTY_OBJET = "objet";
    public static final String ACTUALITE_PROPERTY_CONTENU = "contenu";
    public static final String ACTUALITE_PROPERTY_LECTEURS = "lecteurs";
    public static final String ACTUALITE_PROPERTY_HASPJ = "hasPj";

    public static final String ACTUALITE_REQUETE_XPATH_DATE_EMISSION_DEBUT =
        ACTUALITE_REQUETE_SCHEMA + ":" + ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_DEBUT;
    public static final String ACTUALITE_REQUETE_XPATH_DATE_EMISSION_FIN =
        ACTUALITE_REQUETE_SCHEMA + ":" + ACTUALITE_REQUETE_PROPERTY_DATE_EMISSION_FIN;
    public static final String ACTUALITE_REQUETE_XPATH_DATE_VALIDITE_DEBUT =
        ACTUALITE_REQUETE_SCHEMA + ":" + ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_DEBUT;
    public static final String ACTUALITE_REQUETE_XPATH_DATE_VALIDITE_FIN =
        ACTUALITE_REQUETE_SCHEMA + ":" + ACTUALITE_REQUETE_PROPERTY_DATE_VALIDITE_FIN;
    public static final String ACTUALITE_REQUETE_XPATH_HASPJ =
        ACTUALITE_REQUETE_SCHEMA + ":" + ACTUALITE_PROPERTY_HASPJ;
    public static final String ACTUALITE_REQUETE_XPATH_DANS_HISTORIQUE =
        ACTUALITE_REQUETE_SCHEMA + ":" + ACTUALITE_PROPERTY_DANS_HISTORIQUE;
    public static final String ACTUALITE_REQUETE_XPATH_OBJET =
        ACTUALITE_REQUETE_SCHEMA + ":" + ACTUALITE_PROPERTY_OBJET;

    public static final String ACTUALITE_PREFIX_XPATH_DATE_EMISSION =
        ACTUALITE_SCHEMA_PREFIX + ":" + ACTUALITE_PROPERTY_DATE_EMISSION;
    public static final String ACTUALITE_PREFIX_XPATH_DATE_VALIDITE =
        ACTUALITE_SCHEMA_PREFIX + ":" + ACTUALITE_PROPERTY_DATE_VALIDITE;
    public static final String ACTUALITE_PREFIX_XPATH_DANS_HISTORIQUE =
        ACTUALITE_SCHEMA_PREFIX + ":" + ACTUALITE_PROPERTY_DANS_HISTORIQUE;
    public static final String ACTUALITE_PREFIX_XPATH_OBJET = ACTUALITE_SCHEMA_PREFIX + ":" + ACTUALITE_PROPERTY_OBJET;
    public static final String ACTUALITE_PREFIX_XPATH_CONTENU =
        ACTUALITE_SCHEMA_PREFIX + ":" + ACTUALITE_PROPERTY_CONTENU;
    public static final String ACTUALITE_PREFIX_XPATH_HASPJ = ACTUALITE_SCHEMA_PREFIX + ":" + ACTUALITE_PROPERTY_HASPJ;

    public static final String ACTUALITE_XPATH_DATE_EMISSION =
        ACTUALITE_SCHEMA + ":" + ACTUALITE_PROPERTY_DATE_EMISSION;
    public static final String ACTUALITE_XPATH_DATE_VALIDITE =
        ACTUALITE_SCHEMA + ":" + ACTUALITE_PROPERTY_DATE_VALIDITE;
    public static final String ACTUALITE_XPATH_DANS_HISTORIQUE =
        ACTUALITE_SCHEMA + ":" + ACTUALITE_PROPERTY_DANS_HISTORIQUE;
    public static final String ACTUALITE_XPATH_OBJET = ACTUALITE_SCHEMA + ":" + ACTUALITE_PROPERTY_OBJET;
    public static final String ACTUALITE_XPATH_CONTENU = ACTUALITE_SCHEMA + ":" + ACTUALITE_PROPERTY_CONTENU;
}
