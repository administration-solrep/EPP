package fr.dila.tools.oracle.run;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.tools.oracle.connection.ConnexionOracle;
import fr.dila.tools.oracle.connection.ProcessResult;

public final class MainTestConnection {
	private static final Log	LOG	= LogFactory.getLog(MainTestConnection.class);

	public static class ExtractDocumentCount implements ProcessResult {
		private long	value;

		public ExtractDocumentCount() {
			// do nothing
		}

		@Override
		public void process(ResultSet rset) throws SQLException {
			if (rset.next()) {
				value = rset.getLong(1);
			}
		}

		public long getValue() {
			return value;
		}

	}

	/**
	 * utility class
	 */
	private MainTestConnection() {
		// do nothing
	}

	public static void main(String args[]) {

		if (args.length != 5) {
			LOG.debug("usage : <host> <port> <SID> <user> <passwd>");
			System.exit(0);
		}

		String host = args[0];
		String port = args[1];
		String sid = args[2];
		String user = args[3];
		String passwd = args[4];

		// trace process start
		Date execDate = new Date();
		LOG.debug("Execution " + execDate);

		ConnexionOracle connexion = new ConnexionOracle();
		connexion.setParam(host, port, sid, user, passwd);

		try {
			connexion.connect();

			Long connectTime = connexion.getConnectDuration();
			Thread.sleep(1000);
			final String query1 = "select count(*) from hierarchy";
			// final String query2 = "select count(*) from dublincore";
			// final String query3 = "select count(*) from question";
			final String queries[] = { query1 }; // , query2, query3};

			final int NB = 2;

			LOG.debug("START QUERY");

			long queryTime = 0;
			int i = 0;
			while (i < NB) {
				ExtractDocumentCount edc = new ExtractDocumentCount();
				for (int q = 0; q < queries.length; ++q) {
					connexion.doQuery(queries[q], edc);

					Long qt = connexion.getLastQueryTime();
					LOG.debug("qt[" + q + "]=" + qt);
					queryTime += qt;

					Thread.sleep(10000);
				}
				i++;
			}

			// trace processing time
			LOG.debug("[" + execDate + "] connect= " + connectTime + "ms" + " query= " + queryTime + "ms");

			connexion.close();

		} catch (SQLException e) {
			LOG.warn(e);
		} catch (InterruptedException e) {
			LOG.warn(e);
		}

		// trace error
	}
}
