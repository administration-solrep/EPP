package fr.dila.tools.oracle.stat.process;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.dila.tools.oracle.connection.ProcessResult;

public class SessionUserIdProcessResult implements ProcessResult {
	private Long	userId;

	public Long getUserId() {
		return userId;
	}

	@Override
	public void process(ResultSet rset) throws SQLException {
		userId = null;

		while (rset.next()) {
			userId = rset.getLong(1);
		}
	}

}
