package fr.dila.ss.core.query.sql.model;

import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Reference;

public final class SPredicates {

    public static SPredicate eq(String name) {
        return new SPredicate(new Reference(name), Operator.EQ);
    }

    private SPredicates() {}
}
