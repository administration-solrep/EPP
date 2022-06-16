package fr.dila.st.core.descriptor;

import fr.dila.st.api.requeteur.RequeteurFunctionSolver;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur de d'objet qui aide à la résolution de mot-clés dans les requêtes
 *
 * @author jgomez
 */
@XObject("solver")
public class RequeteurFunctionSolverDescriptor {
    @XNode("@class")
    protected Class<RequeteurFunctionSolver> klass;

    /**
     * Default constructor
     */
    public RequeteurFunctionSolverDescriptor() {
        // do nothing
    }

    public Class<RequeteurFunctionSolver> getKlass() {
        return klass;
    }
}
