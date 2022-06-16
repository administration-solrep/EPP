package fr.dila.st.api.requeteur;

import java.util.Map;
import java.util.regex.Pattern;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Une interface pour permettre la résolution de 'fonctions', directement dans le requeteur. La substitution est
 * appliqué au niveau du requêteur, sur la requête complête. En pratique, un mot-clé avec des arguments, par exemple
 * ufnxql_date:(NOW-15J) est transformé en portion de SQL valide.
 *
 * @author jgomez
 *
 */
public interface RequeteurFunctionSolver {
    /**
     * Retourne l'expression sur lequel travaille la fonction Exemple (NOW-15J) pour la fonction date
     *
     * @return une chaîne correspondant à l'expression
     */
    String getExpr();

    /**
     * Met à jour l'expression
     *
     * @param expr l'expression
     */
    void setExpr(String expr);

    /**
     * Donne l'environnement de la fonction, c'est à dire une map qui contient les constantes utiles à la fonction.
     *
     * @return la map d'environnement
     */
    Map<String, Object> getEnv();

    /**
     * Donne un environnement
     *
     * @param env l'environnement de la fonction
     */
    void setEnv(Map<String, Object> env);

    /**
     * Retourne la substitution à reporter dans la requête complête.
     *
     * @return une portion de requête
     */
    String solve(final CoreSession session);

    /**
     * Retourne une expression régulière permettant de matcher le contenu de la fonction dans la requête complête.
     *
     * @return une expression régulière.
     */
    Pattern getPattern();

    /**
     * Pour un certain contenu, retourne la fonction qui correspond.
     *
     * @param groupStr
     * @return la fonction
     *
     */
    String getExpressionToBeMatched(String groupStr);
}
