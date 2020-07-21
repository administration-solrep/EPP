package fr.dila.tools.oracle.stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.dila.tools.oracle.stat.elements.QueryStat;

public class QueryStatSet {

	public static final String		ORDER_BY_EXEC			= "Exec";
	public static final String		ORDER_BY_ROW			= "Row";
	public static final String		ORDER_BY_BUF_EXEC_ROW	= "BufExecRow";
	public static final String		ORDER_BY_ELAPSED_TIME	= "ElapsedTime";

	private Map<String, QueryStat>	mapQueryStat;

	public static final class ExecComp implements Comparator<QueryStat> {

		/**
		 * default constructor
		 */
		public ExecComp() {
			// do nothing
		}

		@Override
		public int compare(QueryStat qs1, QueryStat qs2) {
			if (qs1.getNbExec() == qs2.getNbExec()) {
				return 0;
			} else {
				return (qs1.getNbExec() > qs2.getNbExec()) ? 1 : -1;
			}
		}

	}

	public static final class RowComp implements Comparator<QueryStat> {

		/**
		 * default constructor
		 */
		public RowComp() {
			// do nothing
		}

		@Override
		public int compare(QueryStat qs1, QueryStat qs2) {
			if (qs1.getRowsProcessed() == qs2.getRowsProcessed()) {
				return 0;
			} else {
				return (qs1.getRowsProcessed() > qs2.getRowsProcessed()) ? 1 : -1;
			}
		}

	}

	public static final class BufRowExecComp implements Comparator<QueryStat> {

		/**
		 * Default constructor
		 */
		public BufRowExecComp() {
			// do nothing
		}

		@Override
		public int compare(QueryStat qs1, QueryStat qs2) {
			if (qs1.getBufPerRowExec() == qs2.getBufPerRowExec()) {
				return 0;
			} else {
				return (qs1.getBufPerRowExec() > qs2.getBufPerRowExec()) ? 1 : -1;
			}
		}

	}

	public static final class ElapsedTimeComp implements Comparator<QueryStat> {

		/**
		 * default constructor
		 */
		public ElapsedTimeComp() {
			// do nothing
		}

		@Override
		public int compare(QueryStat qs1, QueryStat qs2) {
			if (qs1.getElapsedTime() == qs2.getElapsedTime()) {
				return 0;
			} else {
				return (qs1.getElapsedTime() > qs2.getElapsedTime()) ? 1 : -1;
			}
		}

	}

	public QueryStatSet() {
		mapQueryStat = new HashMap<String, QueryStat>();
	}

	public void add(QueryStat qs) {
		mapQueryStat.put(qs.getSqlId(), qs);
	}

	public QueryStat get(String sqlId) {
		return mapQueryStat.get(sqlId);
	}

	public Map<String, QueryStat> getMapQueryStat() {
		return mapQueryStat;
	}

	public List<QueryStat> orderBy(String type) {
		List<QueryStat> qsList = new ArrayList<QueryStat>();
		for (String str : mapQueryStat.keySet()) {
			qsList.add(mapQueryStat.get(str));
		}

		Comparator<QueryStat> comp;
		if (ORDER_BY_ROW.equals(type)) {
			comp = new RowComp();
		} else if (ORDER_BY_BUF_EXEC_ROW.equals(type)) {
			comp = new BufRowExecComp();
		} else if (ORDER_BY_ELAPSED_TIME.equals(type)) {
			comp = new ElapsedTimeComp();
		} else { // default : exec
			comp = new ExecComp();
		}
		Collections.sort(qsList, comp);
		return qsList;
	}

}
