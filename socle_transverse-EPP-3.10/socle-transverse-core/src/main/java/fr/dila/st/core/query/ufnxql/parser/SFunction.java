package fr.dila.st.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.OperandList;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class SFunction extends Function {

	private static final long	serialVersionUID	= -6107133982072616209L;

	public final SQLQuery		query;

	public SFunction(String name) {
		this(name, null, null);
	}

	public SFunction(String name, OperandList args) {
		this(name, null, args);
	}

	public SFunction(String name, SQLQuery query) {
		this(name, query, null);
	}

	public SFunction(String name, SQLQuery query, OperandList args) {
		super(name, args);
		this.query = query;
	}

	@Override
	public String toString() {
		return args == null ? name + "()" : name + '(' + args + ')';
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			if (obj instanceof SFunction) {
				SFunction sfunc = (SFunction) obj;
				if (query == null) {
					return sfunc.query == null;
				}
				return query.equals(sfunc.query);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (args != null ? args.hashCode() : 0);
		return result;
	}

}
