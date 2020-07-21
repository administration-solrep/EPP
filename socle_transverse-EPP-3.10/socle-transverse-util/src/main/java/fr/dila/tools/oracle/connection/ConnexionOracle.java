package fr.dila.tools.oracle.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

public class ConnexionOracle {

	private String				url;
	private String				user;
	private String				password;

	public boolean				isInitialized;
	private OracleDataSource	ods;
	private Connection			connection;

	private Long				connectDuration;
	private Long				lastQueryTime;

	public ConnexionOracle() {
		isInitialized = false;
	}

	public void setParam(String host, String port, String sid, String user, String password) {
		this.url = "jdbc:oracle:thin:@" + host + ":" + port + "/" + sid;
		this.user = user;
		this.password = password;

	}

	public void connect() throws SQLException {
		if (isInitialized) {
			throw new IllegalStateException("already initialized");
		}
		if (url == null) {
			throw new IllegalStateException("no url defined");
		}

		Date tstart = new Date();

		ods = new OracleDataSource();
		ods.setURL(url);
		ods.setUser(user);
		ods.setPassword(password);

		connection = ods.getConnection();

		isInitialized = true;

		Date tend = new Date();

		this.connectDuration = tend.getTime() - tstart.getTime();
	}

	public void close() throws SQLException {
		if (connection != null) {
			connection.close();
		}
		isInitialized = false;
	}

	/**
	 * Pour des requêtes avec paramètres - utilisation de prepareStatement pour éviter les injections SQL - utilisation
	 * avec des paramètres de type string
	 * 
	 * @param query
	 * @param delegate
	 * @throws SQLException
	 */
	public void doQueryWithPreparedStatementString(String query, ProcessResult delegate, List<String> params)
			throws SQLException {
		PreparedStatement ps = null;
		ResultSet rset = null;
		try {
			Date tstart = new Date();
			ps = connection.prepareStatement(query);
			int compteurParams = 0;
			for (String param : params) {
				ps.setString(compteurParams, param);
				param = param + 1;
			}
			rset = ps.executeQuery();
			Date tend = new Date();
			lastQueryTime = tend.getTime() - tstart.getTime();
			if (delegate != null) {
				delegate.process(rset);
			}
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * Pour des requêtes avec paramètres - utilisation de prepareStatement pour éviter les injections SQL - utilisation
	 * avec des paramètres de type long
	 * 
	 * @param query
	 * @param delegate
	 * @throws SQLException
	 */
	public void doQueryWithPreparedStatementLong(String query, ProcessResult delegate, List<Long> params)
			throws SQLException {
		PreparedStatement ps = null;
		ResultSet rset = null;
		try {
			Date tstart = new Date();
			ps = connection.prepareStatement(query);
			int compteurParams = 0;
			for (Long param : params) {
				ps.setLong(compteurParams, param);
				param = param + 1;
			}
			rset = ps.executeQuery();
			Date tend = new Date();
			lastQueryTime = tend.getTime() - tstart.getTime();
			if (delegate != null) {
				delegate.process(rset);
			}
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	public void dropUser(String username) throws SQLException {
		final String query = "drop user ? cascade";
		List<String> params = new ArrayList<String>();
		params.add(username);
		doQueryWithPreparedStatementString(query, null, params);
	}

	public void createUser(String username, String passwd) throws SQLException {
		final String query = "create user ? identified by ?";
		List<String> params = new ArrayList<String>();
		params.add(username);
		params.add(password);
		doQueryWithPreparedStatementString(query, null, params);

	}

	public void grantAccess(String username) throws SQLException {
		final String grantConnect = "GRANT CONNECT TO  ?";
		List<String> params = new ArrayList<String>();
		params.add(username);
		doQueryWithPreparedStatementString(grantConnect, null, params);
		final String grantResource = "GRANT RESOURCE TO ?";
		doQueryWithPreparedStatementString(grantResource, null, params);
		final String grantExecute = "GRANT EXECUTE ON SYS.DBMS_CRYPTO TO ?";
		doQueryWithPreparedStatementString(grantExecute, null, params);
		final String grantSelect = "GRANT SELECT ON SYS.V_$SESSION TO ?";
		doQueryWithPreparedStatementString(grantSelect, null, params);
		final String grantExecuteCtxSys = "GRANT EXECUTE ON CTXSYS.CTX_DDL TO ?";
		doQueryWithPreparedStatementString(grantExecuteCtxSys, null, params);
	}

	public void doQuery(String query, ProcessResult delegate) throws SQLException {

		Statement stmt = null;
		ResultSet rset = null;

		try {
			Date tstart = new Date();
			stmt = connection.createStatement();
			// Date tstmt = new Date();
			rset = stmt.executeQuery(query);
			Date tend = new Date();
			lastQueryTime = tend.getTime() - tstart.getTime();
			// long lastStmt = tstmt.getTime() - tstart.getTime();
			// long exec = tend.getTime() - tstmt.getTime();
			// System.out.println("doQuery : " + lastQueryTime + " ["+lastStmt+"/"+exec+"] ");
			if (delegate != null) {
				delegate.process(rset);
			}
		} finally {
			if (rset != null) {
				rset.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public Long getConnectDuration() {
		return connectDuration;
	}

	public Long getLastQueryTime() {
		return lastQueryTime;
	}

}
