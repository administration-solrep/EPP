package fr.dila.st.core.event.batch;

import java.util.Calendar;

import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchLoggerResultModel;
import fr.dila.st.api.event.batch.BatchResultModel;

public class BatchLoggerResultModelImpl implements BatchLoggerResultModel {

	private static final long	serialVersionUID	= 4746590341344319754L;

	private String				name;

	private Calendar			startTime;

	private Calendar			endTime;

	private long				errorCount;

	private String				text;

	private long				executionResult;

	private long				executionTime;

	private String				type;

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
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
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
	public long getExecutionTime() {
		return this.executionTime;
	}

	@Override
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public BatchLoggerResultModelImpl() {

	}

	public BatchLoggerResultModelImpl(final BatchLoggerModel batchLog, final BatchResultModel batchResult) {
		this.endTime = batchLog.getEndTime();
		this.errorCount = batchLog.getErrorCount();
		this.executionResult = batchResult.getExecutionResult();
		this.executionTime = batchResult.getExecutionTime();
		this.name = batchLog.getName();
		this.startTime = batchLog.getStartTime();
		this.text = batchResult.getText();
		this.type = batchLog.getType();
	}

}
