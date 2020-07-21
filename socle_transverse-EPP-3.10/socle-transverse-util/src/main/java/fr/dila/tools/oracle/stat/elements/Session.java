package fr.dila.tools.oracle.stat.elements;

public class Session {
	private String	sid;
	private String	serial;
	private String	status;
	private String	process;
	private String	machine;
	private Long	command;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public Long getCommand() {
		return command;
	}

	public void setCommand(Long command) {
		this.command = command;
	}

}
