package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.Operand;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;

public class SOrderByExpr extends OrderByExpr {
	/**
	 * Serial UID généré.
	 */
	private static final long serialVersionUID = -3842991882580559129L;

	private final Operand operand;

	public SOrderByExpr(Operand operand, boolean isDescending) {
		super(null, isDescending);
		this.operand = operand;
	}

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof SOrderByExpr) {
            return equalsSOrderByExpr((SOrderByExpr) other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isDescending ? 1231 : 1237);
        result = prime * result 
                + (operand == null ? 0 : operand.hashCode());
        return result;
    }

    private boolean equalsSOrderByExpr(SOrderByExpr other) {
        if (isDescending != other.isDescending) {
            return false;
        }
        if (operand == null) {
            return other.operand == null;
        }
        return operand.equals(other.operand);
    }

    @Override
    public String toString() {
        if (isDescending) {
            return operand.toString() + " DESC";
        } else {
            return operand.toString() + " ASC";
        }
    }

	public Operand getOperand() {
		return operand;
	}
    
    
}
