package fr.dila.solonepp.api.constant;

/**
 * Constantes permettant de construire les ACL / ACE de l'application SOLON EPP.
 *
 * @author jtremeaux
 */
public final class SolonEppAclConstant {
    // *************************************************************
    // ACL
    // *************************************************************
    /**
     * ACL des mesures nominatives.
     */
    public static final String MESURE_NOMINATIVE_ACL = "mesure_nominative";

    /**
     * ACL du rattachement du dossier.
     */
    public static final String RATTACHEMENT_ACL = "rattachement";

    /**
     * ACL indexation du dossier.
     */
    public static final String INDEXATION_ACL = "indexation";

    // *************************************************************
    // ACE des dossiers
    // *************************************************************
    /**
     * Préfixe des ACE permettant de voir les dossiers via la distribution dans les ministères.
     */
    public static final String DOSSIER_DIST_MIN_ACE_PREFIX = "dossier_dist_min-";

    /**
     * Préfixe des ACE permettant de voir les dossiers via la distribution dans les ministères.
     */
    public static final String DOSSIER_DIST_DIR_ACE_PREFIX = "dossier_dist_dir-";

    /**
     * Préfixe des ACE permettant de voir les dossiers via la distribution dans les ministères.
     */
    public static final String DOSSIER_RATTACH_MIN_ACE_PREFIX = "dossier_rattach_min-";

    /**
     * Préfixe des ACE permettant de voir les dossiers via la distribution dans les ministères.
     */
    public static final String DOSSIER_RATTACH_DIR_ACE_PREFIX = "dossier_rattach_dir-";

    /**
     * Préfixe des ACE permettant de voir les dossiers du ministère dans la corbeille "pour indexation"
     */
    public static final String INDEXATION_MIN_ACE_PREFIX = "dossier_index_min-";

    /**
     * Préfixe des ACE permettant de voir les dossiers publié du ministère dans la corbeille "pour indexation"
     */
    public static final String INDEXATION_MIN_PUBLI_ACE_PREFIX = "dossier_index_publi_min-";

    /**
     * Préfixe des ACE permettant de voir les dossiers de la direction dans la corbeille "pour indexation"
     */
    public static final String INDEXATION_DIR_ACE_PREFIX = "dossier_index_dir-";

    /**
     * Préfixe des ACE permettant de voir les dossiers publié de la direction dans la corbeille "pour indexation"
     */
    public static final String INDEXATION_DIR_PUBLI_ACE_PREFIX = "dossier_index_publi_dir-";

    /**
     * utility class
     */
    private SolonEppAclConstant() {
        // do nothing
    }
}
