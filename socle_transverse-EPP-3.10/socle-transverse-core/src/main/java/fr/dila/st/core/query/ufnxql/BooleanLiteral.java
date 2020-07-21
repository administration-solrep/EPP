package fr.dila.st.core.query.ufnxql;

import org.nuxeo.ecm.core.query.sql.model.IVisitor;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.QueryMakerException;

/**
 * Boolean literal for internal use in QueryBuilder
 * 
 * @author spesnel
 * 
 */
class BooleanLiteral extends Literal {

	private static final long	serialVersionUID	= 1L;

	public final boolean		value;

	public BooleanLiteral(boolean value) {
		super();
		this.value = value;
	}

	@Override
	public void accept(IVisitor visitor) {
		if (visitor instanceof QueryBuilder) {
			((QueryBuilder) visitor).visitBooleanLiteral(this);
		} else {
			throw new QueryMakerException("BooleanLiteral not supported by visitor");
		}
	}

	@Override
	public String asString() {
		return String.valueOf(value);
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof BooleanLiteral) {
			return value == ((BooleanLiteral) obj).value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Boolean.valueOf(value).hashCode();
	}

}
