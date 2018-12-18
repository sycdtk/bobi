package com.telinkus.itsm.process.exe;

import java.sql.Timestamp;
import java.util.Date;

public class WithdrawComment {
	
	private Long id;
	private Integer personId;
	private Date createTime;
	private String message;
	
	private Long extId;
	
	private Short catId;
	
	private String nodeName;

	public WithdrawComment() {
	}
	
	public WithdrawComment(String message) {
		this.message = message;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Long getExtId() {
		return extId;
	}

	public void setExtId(Long extId) {
		this.extId = extId;
	}

	public Short getCatId() {
		return catId;
	}

	public void setCatId(Short catId) {
		this.catId = catId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

}
