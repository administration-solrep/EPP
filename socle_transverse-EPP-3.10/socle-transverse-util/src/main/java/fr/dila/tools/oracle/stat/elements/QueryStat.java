package fr.dila.tools.oracle.stat.elements;

public class QueryStat {
	/** SQL identifier of the parent cursor in the library cache */
	private String	sqlId;
	/** First thousand characters of the SQL text for the current cursor */
	private String	sqlText;
	/** Total number of executions, totalled over all the child cursors */
	private long	nbExec;
	/** Sum of buffer gets over all child cursors */
	private long	bufferGets;
	/** Total number of rows processed on behalf of this SQL statement */
	private long	rowsProcessed;
	/** Elapsed time (in microseconds) used by this cursor for parsing, executing, and fetching */
	private long	elapsedTime;
	/** CPU time (in microseconds) used by this cursor for parsing, executing, and fetching */
	private long	cpuTime;
	/** Application wait time (in microseconds) */
	private long	applicationWaitTime;
	/** Concurrency wait time (in microseconds) */
	private long	concurrencyWaitTime;
	/** Cluster wait time (in microseconds) */
	private long	clusterWaitTime;
	/** User I/O Wait Time (in microseconds) */
	private long	userIOWaitTime;
	/** PL/SQL execution time (in microseconds) */
	private long	plsqlExecTime;
	/** Java execution time (in microseconds) */
	private long	javaExecTime;

	public QueryStat() {

	}

	public QueryStat(QueryStat toCopy) {
		this.sqlId = toCopy.sqlId;
		this.sqlText = toCopy.sqlText;
		this.nbExec = toCopy.nbExec;
		this.bufferGets = toCopy.bufferGets;
		this.rowsProcessed = toCopy.rowsProcessed;
		this.elapsedTime = toCopy.elapsedTime;
		this.cpuTime = toCopy.cpuTime;
		this.applicationWaitTime = toCopy.applicationWaitTime;
		this.concurrencyWaitTime = toCopy.concurrencyWaitTime;
		this.clusterWaitTime = toCopy.clusterWaitTime;
		this.userIOWaitTime = toCopy.userIOWaitTime;
		this.plsqlExecTime = toCopy.plsqlExecTime;
		this.javaExecTime = toCopy.javaExecTime;
	}

	/**
	 * substract value in q1 to this
	 * 
	 * @param qs1
	 */
	public void substract(QueryStat qs1) {
		this.nbExec = this.nbExec - qs1.nbExec;
		this.bufferGets = this.bufferGets - qs1.bufferGets;
		this.rowsProcessed = this.rowsProcessed - qs1.rowsProcessed;
		this.elapsedTime = this.elapsedTime - qs1.elapsedTime;
		this.cpuTime = this.cpuTime - qs1.cpuTime;
		this.applicationWaitTime = this.applicationWaitTime - qs1.applicationWaitTime;
		this.concurrencyWaitTime = this.concurrencyWaitTime - qs1.concurrencyWaitTime;
		this.clusterWaitTime = this.clusterWaitTime - qs1.clusterWaitTime;
		this.userIOWaitTime = this.userIOWaitTime - qs1.userIOWaitTime;
		this.plsqlExecTime = this.plsqlExecTime - qs1.plsqlExecTime;
		this.javaExecTime = this.javaExecTime - qs1.javaExecTime;
	}

	public long getBufPerRowExec() {
		long max = Math.max(getNbExec(), getRowsProcessed());
		if (max == 0) {
			max = 1;
		}
		return getBufferGets() / max;
	}

	// getters and setters

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public long getNbExec() {
		return nbExec;
	}

	public void setNbExec(long nbExec) {
		this.nbExec = nbExec;
	}

	public long getBufferGets() {
		return bufferGets;
	}

	public void setBufferGets(long bufferGets) {
		this.bufferGets = bufferGets;
	}

	public long getRowsProcessed() {
		return rowsProcessed;
	}

	public void setRowsProcessed(long rowsProcessed) {
		this.rowsProcessed = rowsProcessed;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public long getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(long cpuTime) {
		this.cpuTime = cpuTime;
	}

	public long getApplicationWaitTime() {
		return applicationWaitTime;
	}

	public void setApplicationWaitTime(long applicationWaitTime) {
		this.applicationWaitTime = applicationWaitTime;
	}

	public long getConcurrencyWaitTime() {
		return concurrencyWaitTime;
	}

	public void setConcurrencyWaitTime(long concurrencyWaitTime) {
		this.concurrencyWaitTime = concurrencyWaitTime;
	}

	public long getClusterWaitTime() {
		return clusterWaitTime;
	}

	public void setClusterWaitTime(long clusterWaitTime) {
		this.clusterWaitTime = clusterWaitTime;
	}

	public long getUserIOWaitTime() {
		return userIOWaitTime;
	}

	public void setUserIOWaitTime(long userIOWaitTime) {
		this.userIOWaitTime = userIOWaitTime;
	}

	public long getPlsqlExecTime() {
		return plsqlExecTime;
	}

	public void setPlsqlExecTime(long plsqlExecTime) {
		this.plsqlExecTime = plsqlExecTime;
	}

	public long getJavaExecTime() {
		return javaExecTime;
	}

	public void setJavaExecTime(long javaExecTime) {
		this.javaExecTime = javaExecTime;
	}

}
