package fr.dila.ss.core.query.sql.model;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SParamLiteral;
import org.nuxeo.ecm.core.query.sql.model.Operand;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Predicate;

public class SPredicate extends Predicate {
    private static final long serialVersionUID = 1016295164500506454L;

    public SPredicate(Operand lvalue, Operator operator) {
        super(lvalue, operator, new SParamLiteral());
    }
}
