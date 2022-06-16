package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import java.util.ArrayList;

import org.nuxeo.ecm.core.query.sql.model.Operand;

/**
 * Strcuture contenant la liste de reférence présente dans un groupby
 *
 */
public class SGroupByList extends ArrayList<Operand> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SGroupByList(Operand operand){
    	super();
        add(operand);
    }
    
}
