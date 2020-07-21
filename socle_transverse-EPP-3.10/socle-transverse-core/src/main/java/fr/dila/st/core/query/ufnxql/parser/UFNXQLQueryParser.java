package fr.dila.st.core.query.ufnxql.parser;

import java.io.Reader;
import java.io.StringReader;

import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

public final class UFNXQLQueryParser {

	// utility class
	private UFNXQLQueryParser() {

	}

	public static SQLQuery parse(Reader reader) {
		try {
			Scanner scanner = new Scanner(reader);
			Parser parser = new Parser(scanner);
			return (SQLQuery) parser.parse().value;
		} catch (Exception e) {
			throw new QueryParseException(e);
		}
	}

	public static SQLQuery parse(String string) throws QueryParseException {
		try {
			SQLQuery query = parse(new StringReader(string));
			query.setQueryString(string);
			return query;
		} catch (QueryParseException e) {
			throw new QueryParseException(e.getMessage() + " in query: " + string, e);
		}
	}

}
