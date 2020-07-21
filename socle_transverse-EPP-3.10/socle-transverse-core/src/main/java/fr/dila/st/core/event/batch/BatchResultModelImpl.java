package fr.dila.st.core.event.batch;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.dila.st.api.event.batch.BatchResultModel;

@Entity(name = "BatchResult")
@Table(name = "BATCH_RESULT")
public class BatchResultModelImpl implements BatchResultModel {

	private static final long	serialVersionUID	= 4746590341344319754L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_RESULT", nullable = false, columnDefinition = "integer")
	private long				idResult;

	@Column(name = "TEXT", nullable = false)
	private String				text;

	@Column(name = "ID_LOG", nullable = false, columnDefinition = "integer")
	private long				idLog;

	@Column(name = "EXECUTION_RESULT", columnDefinition = "integer")
	private long				executionResult;

	@Column(name = "EXECUTION_TIME", nullable = false, columnDefinition = "integer")
	private long				executionTime;

	@Override
	public long getIdResult() {
		return this.idResult;
	}

	@Override
	public void setIdResult(long idResult) {
		this.idResult = idResult;
	}

	@Override
	public long getExecutionResult() {
		return this.executionResult;
	}

	@Override
	public void setExecutionResult(long executionResult) {
		this.executionResult = executionResult;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public long getLogId() {
		return this.idLog;
	}

	@Override
	public void setLogId(long logId) {
		this.idLog = logId;
	}

	@Override
	public long getExecutionTime() {
		return this.executionTime;
	}

	@Override
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

}
