package com.telinkus.itsm.process.exe;

import java.sql.Timestamp;
import java.util.Date;

public class Comment {
	
	private Long id;
	private Integer authorId;
	private Date createTime;
	private String message;

	private TaskInstance taskInstance;

	public Comment() {
	}
	
	public Comment(String message) {
		this.message = message;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public Comment(Integer authorId, Date createTime, String message,	TaskInstance taskInstance) {
		this.authorId = authorId;
		this.createTime = createTime;
		this.message = message;
		this.taskInstance = taskInstance;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAuthorId() {
		return this.authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TaskInstance getTaskInstance() {
		return this.taskInstance;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

}
