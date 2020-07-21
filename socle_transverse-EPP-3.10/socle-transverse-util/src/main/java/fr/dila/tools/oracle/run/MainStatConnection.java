package fr.dila.tools.oracle.run;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.tools.oracle.connection.ConnexionOracle;
import fr.dila.tools.oracle.stat.QueryStatManip;
import fr.dila.tools.oracle.stat.QueryStatSet;
import fr.dila.tools.oracle.stat.elements.Binding;
import fr.dila.tools.oracle.stat.elements.BindingSet;
import fr.dila.tools.oracle.stat.elements.QueryStat;
import fr.dila.tools.oracle.stat.elements.Session;

class MainStatConnection {
	private static final Log	LOG		= LogFactory.getLog(MainStatConnection.class);
	public static byte[]		buffer	= new byte[128];

	public static int read() throws IOException {
		int read = System.in.read();
		while (read == 10 || read == 13) {
			read = System.in.read();
		}
		return read;
	}

	public static String[] readString() throws IOException {
		while (true) {
			int nbread = System.in.read(buffer);
			if (nbread > 0) {
				String read_str = new String(buffer, 0, nbread);
				String[] read_strs = read_str.split(" ");
				int nb = 0;
				for (String str : read_strs) {

					int len = 0;
					if (str.contains("\n")) {
						len++;
					}
					if (str.contains("\r")) {
						len++;
					}
					if (str.length() > len) {
						nb++;
					}

				}
				if (nb > 0) {
					String[] not_empty_str = new String[nb];
					int index = 0;
					for (String str : read_strs) {
						int len = 0;
						if (str.contains("\n")) {
							len++;
						}
						if (str.contains("\r")) {
							len++;
						}
						if (str.length() > len) {

							not_empty_str[index] = str.replace("\r", "").replace("\n", "");
							index++;
						}
					}
					return not_empty_str;
				}
			}
		}
	}

	public static void display(QueryStatSet qsSet1, String orderBy) {
		long exec = 0;
		long elapsed = 0;
		List<QueryStat> qsList = qsSet1.orderBy(orderBy);
		for (QueryStat qs : qsList) {

			LOG.debug(qs.getSqlId());
			LOG.debug(qs.getSqlText());
			long buf_per_exec = qs.getBufferGets() / (qs.getNbExec() <= 0 ? 1 : qs.getNbExec());
			long buf_per_row = qs.getBufferGets() / (qs.getRowsProcessed() <= 0 ? 1 : qs.getRowsProcessed());
			long buf_per_execrow = qs.getBufPerRowExec();
			LOG.debug("Exec=" + qs.getNbExec() + ", buf_gets=" + qs.getBufferGets() + ", rows=" + qs.getRowsProcessed()
					+ ", buf/exec=" + buf_per_exec + ", buf/row=" + buf_per_row + ", buf/execrow=" + buf_per_execrow);
			LOG.debug("Elapsed(us)=" + qs.getElapsedTime());
			LOG.debug("WAIT(us) : appli=" + qs.getApplicationWaitTime() + ", conccurent=" + qs.getConcurrencyWaitTime()
					+ ", cluster=" + qs.getClusterWaitTime() + ", userIO=" + qs.getUserIOWaitTime());
			LOG.debug("ExecTime(us) : plsql=" + qs.getPlsqlExecTime() + ", java=" + qs.getJavaExecTime());
			exec += qs.getNbExec();
			elapsed += qs.getElapsedTime();
			// if("6pcf4quh5gksw".equals(qs.getSqlId())){
			// Map<String,String> bindings = manip.retrieveBindValue(qs.getSqlId());
			// for(String bkey : bindings.keySet()){
			// System.out.println("[" + bkey + "=" + bindings.get(bkey) + "]");
			// }
			// }

			LOG.debug("---------------------------------");
		}
		LOG.debug("NbQueries = " + qsSet1.getMapQueryStat().size());
		LOG.debug("TotalExec = " + exec);
		LOG.debug("TotalElapsed = " + elapsed + " us  / " + (elapsed / 1000.f) + " ms / " + (elapsed / 1000000.f)
				+ " s");
	}

	public static void display(List<Session> sessions) {
		LOG.debug("----------SESSIONS---------------");
		LOG.debug("SID, SERIAL, STATUS, PROCESS, MACHINE, COMMAND");
		for (Session session : sessions) {
			StringBuilder sb = new StringBuilder(String.format("%4d, ", Integer.parseInt(session.getSid())))
					.append(String.format("% 6d, ", Integer.parseInt(session.getSerial()))).append(session.getStatus())
					.append(", ").append(String.format("% 6d, ", Integer.parseInt(session.getProcess())))
					.append(session.getMachine()).append(", ").append(String.format("% 2d, ", session.getCommand()));
			LOG.debug(sb.toString());
		}
		LOG.debug("---------------------------------");
	}

	public static void main(String args[]) throws SQLException, IOException {

		if (args.length != 6) {
			LOG.debug("usage : <host> <port> <SID> <user> <passwd> <user name>");
			System.exit(0);
		}

		String host = args[0];
		String port = args[1];
		String sid = args[2];
		String user = args[3];
		String passwd = args[4];
		String username = args[5];

		String orderBy = QueryStatSet.ORDER_BY_EXEC;

		// trace process start
		Date execDate = new Date();
		LOG.debug("Execution " + execDate);

		ConnexionOracle connexion = new ConnexionOracle();
		connexion.setParam(host, port, sid, user, passwd);

		try {
			connexion.connect();
			LOG.debug("CONNECTED");
			QueryStatManip manip = new QueryStatManip(connexion);

			Long userId = manip.retrieveUserId(username);
			LOG.debug("User id = " + userId + " for [" + username + "]");

			QueryStatSet qsRef = null;
			QueryStatSet qsSet = null;

			boolean quit = false;
			while (!quit) {
				LOG.debug("user input : ");
				String[] re = readString();
				String action = re[0];
				if ("f".equals(action)) {
					LOG.debug("Flush...");
					manip.flush();
				} else if ("r".equals(action)) {
					qsRef = manip.doSnapshot(userId);
				} else if ("s".equals(action)) {
					LOG.debug("Snapshot...");
					qsSet = manip.doSnapshot(userId);
				} else if ("p".equals(action)) {
					LOG.debug("Print snapshot...");
					display(qsSet, orderBy);
				} else if ("d".equals(action)) {
					LOG.debug("Print snapshot (diff)...");
					if (qsSet != null) {
						QueryStatSet qsDiff = manip.diff(qsRef, qsSet);
						display(qsDiff, orderBy);
					}
				} else if ("b".equals(action)) {
					LOG.debug("Binding ....");
					if (re.length < 2) {
						LOG.debug("Missing arg");
					} else {
						String sqlId = re[1];
						BindingSet bindValues = manip.retrieveBindValue(sqlId);
						LOG.debug("Bind values for " + sqlId);
						for (String ca : bindValues.getBindingKeys()) {
							Binding b = bindValues.getBinding(ca);
							LOG.debug("-- ensemble {" + ca + "}");
							for (String k : b.getKeys()) {
								LOG.debug(k + "->" + b.getValue(k));
							}
						}
					}
				} else if ("o".equals(action)) {
					LOG.debug("order by...");
					if (re.length < 2) {
						orderBy = QueryStatSet.ORDER_BY_EXEC;
					} else {
						orderBy = re[1];
					}
					LOG.debug("Order by " + orderBy);
				} else if ("sessions".equals(action)) {
					List<Session> sessions = manip.retrieveSession(username);
					display(sessions);
				} else if ("q".equals(action)) {
					LOG.debug("Quit...");
					quit = true;
				} else {
					LOG.debug("Possible value : q, f, r, s, d, b, o, sessions");
				}
			}

			System.out.println("Done");
		} catch (SQLException e) {
			LOG.debug("Erreur SQL Exception", e);
		} finally {
			connexion.close();
		}

		// trace error
	}
}
