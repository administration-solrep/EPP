package fr.dila.tools.oracle.run;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.tools.oracle.connection.ConnexionOracle;

class MainInitDB {
	private static final Log	LOG	= LogFactory.getLog(MainInitDB.class);

	private MainInitDB() {
		// Default constructor
	}

	public static void main(String args[]) throws SQLException {

		if (args.length != 6) {
			LOG.debug("usage : <host> <port> <SID> <admin user> <admin passwd> <test user>");
			System.exit(0);
		}

		String host = args[0];
		String port = args[1];
		String sid = args[2];
		String adminuser = args[3];
		String adminpasswd = args[4];
		String testuser = args[5];
		String testpasswd = testuser;

		// trace process start
		Date execDate = new Date();
		LOG.debug("Start Execution " + execDate);

		ConnexionOracle connexion = new ConnexionOracle();
		connexion.setParam(host, port, sid, adminuser, adminpasswd);

		int ret = 1;

		try {
			LOG.debug("Connect...");
			connexion.connect();
			LOG.debug("Connected.");
			LOG.debug("Drop user [" + testuser + "]...");

			try {
				connexion.dropUser(testuser);
				LOG.debug("User dropped");
			} catch (SQLSyntaxErrorException e) {
				// ignore erreur sur utilisateur inexistant
				if (e.getMessage().startsWith("ORA-01918")) {
					LOG.debug("User not dropped : inexisting.");
				} else {
					throw e;
				}
			}

			LOG.debug("Create user ...");
			connexion.createUser(testuser, testpasswd);
			LOG.debug("User created.");

			LOG.debug("Grant access ...");
			connexion.grantAccess(testuser);
			LOG.debug("Access granted.");

			LOG.debug("Reset done.");

			ret = 0;

		} catch (SQLException e) {
			LOG.debug("SQL exception", e);
		} finally {
			connexion.close();
		}
		// trace error

		System.exit(ret);

	}
}
