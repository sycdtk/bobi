package com.telinkus.itsm.process.exe;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.telinkus.itsm.process.def.Node;
import com.telinkus.itsm.process.def.ProcessDefinition;

public class ProcessInstance {

	public static final short CATEGORY_INCIDENT = 0;
	public static final short CATEGORY_PROBLEM = 1;
	public static final short CATEGORY_CHANGE = 2;
	public static final short CATEGORY_PROJECT = 3;
	public static final short CATEGORY_DEVICE = 4;

	public static final short CATEGORY_RELEASE = 18;

	// add by lxf begin
	public static final short CATEGORY_REQUIREMENT = 19;
	public static final short CATEGORY_REQACCEPT = 20;
	// add by lxf end

	public static final short CATEGORY_AREAVISIT = 21;
	
	public static final short CATEGORY_RELEASE2 = 22;

	// public static final short STATUS_WAITSTART = -1;

	public static final short STATUS_RUNNING = 0;
	public static final short STATUS_ENDED = 1;
	public static final short STATUS_ABORTED = 2;
	public static final short STATUS_SUSPENDED = 3;

	private Long id;
	private Short category;
	private Long externalId;
	private ProcessDefinition definition;
	private Integer initiatorId;
	private Date beginTime;
	private Date endTime;
	private Short status;
	private Token token;

	private Map<String, String> globalData = new HashMap<String, String>(0);

	private String abortComment;

	public ProcessInstance() {

	}

	public ProcessInstance(ProcessDefinition definition) {
		// this.category = definition.getCategory();
		this.definition = definition;
	}

	/*
	 * method to begin execution of a process instance
	 */
	public void begin() throws Exception {

		Timestamp now = new Timestamp(System.currentTimeMillis());
		Node beginNode = this.getDefinition().getBeginNode();

		this.setBeginTime(now);
		this.setStatus(new Short(ProcessInstance.STATUS_RUNNING));

		Token token = new Token();
		token.setProcessInstance(this);
		token.setStatus(new Short(Token.STATUS_RUNNING));
		this.setToken(token);

		// move pointer to first node:
		token.gotoNode(beginNode, null);

	}// begin()

	/*
	 * method to end a process instance
	 */
	public void end() {

		Timestamp now = new Timestamp(System.currentTimeMillis());

		this.setEndTime(now);
		this.setStatus(new Short(ProcessInstance.STATUS_ENDED));

		this.getToken().end();
	}

	/*
	 * method to end a process instance
	 */
	public void abort() {

		Timestamp now = new Timestamp(System.currentTimeMillis());

		this.setEndTime(now);
		this.setStatus(new Short(ProcessInstance.STATUS_ABORTED));

		this.getToken().setStatus(new Short(Token.STATUS_ENDED));
	}

	/*
	 * method to suspend a process instance
	 */
	public void suspend() {

		this.setStatus(new Short(ProcessInstance.STATUS_SUSPENDED));

	}

	/*
	 * method to resume a process instance
	 */
	public void resume() {
		// ......

	}

	// setters and getters:

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Short getCategory() {
		return this.category;
	}

	public void setCategory(Short category) {
		this.category = category;
	}

	public Long getExternalId() {
		return this.externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public ProcessDefinition getDefinition() {
		return this.definition;
	}

	public void setDefinition(ProcessDefinition definition) {
		this.definition = definition;
	}

	public Integer getInitiatorId() {
		return this.initiatorId;
	}

	public void setInitiatorId(Integer initiatorId) {
		this.initiatorId = initiatorId;
	}

	public Date getBeginTime() {
		return this.beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Token getToken() {
		return this.token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Map<String, String> getGlobalData() {
		return globalData;
	}

	public void setGlobalData(Map<String, String> globalData) {
		this.globalData = globalData;
	}

	public String getAbortComment() {
		return abortComment;
	}

	public void setAbortComment(String abortComment) {
		this.abortComment = abortComment;
	}
}