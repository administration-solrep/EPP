package fr.dila.tools.oracle.stat.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import fr.dila.tools.oracle.connection.ProcessResult;
import fr.dila.tools.oracle.stat.elements.Session;

public class SessionProcessResult implements ProcessResult {
	private List<Session>	sessions;

	public List<Session> getSessions() {
		return sessions;
	}

	@Override
	public void process(ResultSet rset) throws SQLException {
		sessions = new LinkedList<Session>();

		while (rset.next()) {

			Session s = new Session();
			s.setSid(rset.getString(1));
			s.setSerial(rset.getString(2));
			s.setStatus(rset.getString(3));
			s.setProcess(rset.getString(4));
			s.setMachine(rset.getString(5));
			s.setCommand(rset.getLong(6));

			sessions.add(s);
		}
	}

}
