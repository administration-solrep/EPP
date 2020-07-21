package fr.dila.st.core.event.batch;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fr.dila.st.api.event.batch.BatchLoggerModel;

@Entity(name = "BatchLogger")
@Table(name = "BATCH_LOG")
public class BatchLoggerModelImpl implements BatchLoggerModel {

	private static final long	serialVersionUID	= 4746590341344319754L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_LOG", nullable = false, columnDefinition = "integer")
	private long				idLog;

	@Column(name = "NAME")
	private String				name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_TIME")
	private Calendar			startTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_TIME")
	private Calendar			endTime;

	@Column(name = "ERROR_COUNT", columnDefinition = "integer")
	private long				errorCount;

	@Column(name = "PARENT_ID", columnDefinition = "integer")
	private long				parentId;

	@Column(name = "SERVER")
	private String				server;

	@Column(name = "TYPE")
	private String				type;

	@Column(name = "TOMCAT", columnDefinition = "integer")
	private long				tomcat;

	@Override
	public long getIdLog() {
		return idLog;
	}

	@Override
	public void setIdLog(long idLog) {
		this.idLog = idLog;
	}

	@Override
	public Calendar getStartTime() {
		return startTime;
	}

	@Override
	public void setStartTime(Calendar startTime) {
		this.startTime = (Calendar) startTime.clone();
	}

	@Override
	public Calendar getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(Calendar endTime) {
		this.endTime = (Calendar) endTime.clone();
	}

	@Override
	public long getErrorCount() {
		return errorCount;
	}

	@Override
	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public long getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	@Override
	public String getServer() {
		return server;
	}

	@Override
	public void setServer(String server) {
		this.server = server;
	}

	@Override
	public long getTomcat() {
		return tomcat;
	}

	@Override
	public void setTomcat(long tomcat) {
		this.tomcat = tomcat;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

}
