package fr.dila.tools.oracle.stat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.dila.tools.oracle.connection.ConnexionOracle;
import fr.dila.tools.oracle.stat.elements.BindingSet;
import fr.dila.tools.oracle.stat.elements.QueryStat;
import fr.dila.tools.oracle.stat.elements.Session;
import fr.dila.tools.oracle.stat.process.BindingProcesssResult;
import fr.dila.tools.oracle.stat.process.QueryStatProcessResult;
import fr.dila.tools.oracle.stat.process.SessionProcessResult;
import fr.dila.tools.oracle.stat.process.SessionUserIdProcessResult;

public class QueryStatManip {
	private ConnexionOracle	connexion;

	public QueryStatManip(ConnexionOracle connexion) {
		this.connexion = connexion;
	}

	public Long retrieveUserId(String username) throws SQLException {
		final String query = "select distinct USER# from v$session WHERE USERNAME = ?";

		SessionUserIdProcessResult sessionUserIdProcessResult = new SessionUserIdProcessResult();
		List<String> params = new ArrayList<String>();
		params.add(username);
		connexion.doQueryWithPreparedStatementString(query, sessionUserIdProcessResult, params);
		return sessionUserIdProcessResult.getUserId();
	}

	public QueryStatSet doSnapshot(Long userId) throws SQLException {
		final String query = "select a.SQL_ID, a.SQL_TEXT, a.EXECUTIONS, a.BUFFER_GETS, a.ROWS_PROCESSED, "
				+ "a.ELAPSED_TIME, a.CPU_TIME, a.APPLICATION_WAIT_TIME, a.CONCURRENCY_WAIT_TIME, "
				+ "a.CLUSTER_WAIT_TIME, a.USER_IO_WAIT_TIME, a.PLSQL_EXEC_TIME, a.JAVA_EXEC_TIME"
				+ " from V$SQLAREA a where parsing_user_id = ?";
		QueryStatProcessResult queryStatProcessResult = new QueryStatProcessResult();
		List<Long> params = new ArrayList<Long>();
		params.add(userId);
		connexion.doQueryWithPreparedStatementLong(query, queryStatProcessResult, params);
		return queryStatProcessResult.getQueryStatSet();
	}

	public void flush() throws SQLException {
		final String query = "alter system flush shared_pool";
		connexion.doQuery(query, null);
	}

	public BindingSet retrieveBindValue(String sqlId) throws SQLException {
		final String query = "select CHILD_ADDRESS, NAME, VALUE_STRING from V$SQL_BIND_CAPTURE WHERE sql_ID= ?";
		BindingProcesssResult bpr = new BindingProcesssResult();
		List<String> params = new ArrayList<String>();
		params.add(sqlId);
		connexion.doQueryWithPreparedStatementString(query, bpr, params);
		return bpr.getBindings();
	}

	public List<Session> retrieveSession(String username) throws SQLException {
		final String query = "select sid, serial#, status, process, machine, command from V$SESSION WHERE username = ?";
		SessionProcessResult spr = new SessionProcessResult();
		List<String> params = new ArrayList<String>();
		params.add(username);
		connexion.doQueryWithPreparedStatementString(query, spr, params);
		return spr.getSessions();
	}

	public QueryStat diff(QueryStat qs1, QueryStat qs2) {

		if (qs1 == null) {
			return new QueryStat(qs2);
		} else {
			if (qs2.getNbExec() == qs1.getNbExec()) {
				return null;
			} else {
				QueryStat qsDiff = new QueryStat(qs2);
				qsDiff.substract(qs1);
				return qsDiff;
			}
		}
	}

	public QueryStatSet diff(QueryStatSet set1, QueryStatSet set2) {
		QueryStatSet qset = new QueryStatSet();
		for (String sqlId : set2.getMapQueryStat().keySet()) {
			QueryStat qs1 = set1 == null ? null : set1.get(sqlId);
			QueryStat qs2 = set2.get(sqlId);
			QueryStat qsDiff = diff(qs1, qs2);
			if (qsDiff != null) {
				qset.add(qsDiff);
			}
		}
		return qset;
	}
}
