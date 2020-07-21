package fr.dila.tools.oracle.stat.process;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.dila.tools.oracle.connection.ProcessResult;
import fr.dila.tools.oracle.stat.QueryStatSet;
import fr.dila.tools.oracle.stat.elements.QueryStat;

public class QueryStatProcessResult implements ProcessResult {

	private QueryStatSet	queryStatSet;

	@Override
	public void process(ResultSet rset) throws SQLException {
		queryStatSet = new QueryStatSet();

		while (rset.next()) {
			QueryStat qs = new QueryStat();
			qs.setSqlId(rset.getString(1));
			qs.setSqlText(rset.getString(2));
			qs.setNbExec(rset.getLong(3));
			qs.setBufferGets(rset.getLong(4));
			qs.setRowsProcessed(rset.getLong(5));
			qs.setElapsedTime(rset.getLong(6));
			qs.setCpuTime(rset.getLong(7));
			qs.setApplicationWaitTime(rset.getLong(8));
			qs.setConcurrencyWaitTime(rset.getLong(9));
			qs.setClusterWaitTime(rset.getLong(10));
			qs.setUserIOWaitTime(rset.getLong(11));
			qs.setPlsqlExecTime(rset.getLong(12));
			qs.setJavaExecTime(rset.getLong(13));

			queryStatSet.add(qs);
		}
	}

	public QueryStatSet getQueryStatSet() {
		return queryStatSet;
	}

}
