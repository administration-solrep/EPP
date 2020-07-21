package fr.dila.tools.oracle.connection;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ProcessResult {

	void process(ResultSet rset) throws SQLException;

}
