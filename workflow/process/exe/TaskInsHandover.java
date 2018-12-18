package com.telinkus.itsm.process.exe;

import java.util.Date;

public class TaskInsHandover {
	
	private Long id;
	
	private Integer fromer;
	private Integer comer;
	
	private Date createTime;
	
	private TaskInstance taskInstance;
	
	private String message;

	public Integer getComer() {
		return comer;
	}

	public void setComer(Integer comer) {
		this.comer = comer;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getFromer() {
		return fromer;
	}

	public void setFromer(Integer fromer) {
		this.fromer = fromer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TaskInstance getTaskInstance() {
		return taskInstance;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
