package fr.dila.st.api.recherche;

/**
 * Effectue des opérations sur l'objet requête.
 *
 * @author jgomez
 *
 */
public interface RequeteTraitement<T> {
    /**
     * Mise en place des opérations d'initialisation de la requête
     *
     * @param requete
     */
    void init(T requete);

    /**
     * Mise des opérations à effectuer avant la recherche
     *
     * @param requete
     */
    void doBeforeQuery(T requete);

    /**
     * Mise des opérations à effectuer après la recherche
     *
     * @param requete
     */
    void doAfterQuery(T requete);
}
