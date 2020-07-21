package fr.dila.st.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.IVisitor;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.QueryMakerException;

/**
 * represent marker for parameter in query
 * 
 * @author spesnel
 */
public class SParamLiteral extends Literal {

	/**
     * 
     */
	private static final long	serialVersionUID	= -8173054899922275377L;

	public static final String	PARAM_MARKER		= "?";

	public SParamLiteral() {
	}

	@Override
	public String toString() {
		return PARAM_MARKER;
	}

	@Override
	public void accept(IVisitor visitor) {
		if (visitor instanceof UFNXQLVisitor) {
			((UFNXQLVisitor) visitor).visitSParamLiteral();
		} else {
			throw new QueryMakerException("visitor does not support SParamLiteral");
		}
	}

	@Override
	public String asString() {
		return PARAM_MARKER;
	}

}
