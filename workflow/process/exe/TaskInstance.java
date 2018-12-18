package com.telinkus.itsm.process.exe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.telinkus.itsm.data.change.Change;
import com.telinkus.itsm.data.configuration.ConfigurationItem;
import com.telinkus.itsm.data.incident.Incident;
import com.telinkus.itsm.data.library.Document;
import com.telinkus.itsm.data.problem.Problem;
import com.telinkus.itsm.data.project.Project;
import com.telinkus.itsm.data.project.ProjectTask;
import com.telinkus.itsm.process.def.Task;

public class TaskInstance {
	public static final short TYPE_NORMAL = 0; // normal task
	public static final short TYPE_STEP_TAKEOVER = 1; // task of taking over
														// step
	public static final short TYPE_BRANCH = 2; // task of choosing branches
	public static final short TYPE_SUBTASK = 3; // sub task
	public static final short TYPE_AUTO = 9; // auto task

	public static final short TYPE_CHILD_INSTANCE = 4; // 多人接管时产生的子实例

	public static final short STATUS_QUEUED = 0; // queued
	public static final short STATUS_ASSIGNED = 1; // assigned to a role,
													// waiting for a person to
													// take over
	public static final short STATUS_PROCESSING = 2; // assigned to a person and
														// is under execution
	public static final short STATUS_FOR_AUDIT = 3; // executed, waiting for
													// auditing
	public static final short STATUS_CLOSED = 4; // executed (and audited if
													// necessary)
	public static final short STATUS_SUSPENDED = 5; // suspended for later
													// execution
	public static final short STATUS_CANCELLED = 6; // cancelled

	public static final short STATUS_ASSIGNING = 7; // assigning to more than
													// one person

	private Long id;
	private String name;
	private Short category;
	private Long externalId;
	private Short type;
	private String description;

	private Boolean success;
	private String errorMessage;

	private Integer locationId;
	private Integer organizationId;

	private Short actorType;
	private Integer actorId;
	private String actorName;
	private Integer auditorId;

	private Date createTime;
	private Date beginTime;
	private Date endTime;
	private Short status;

	private String form;

	private Map<String, Object> data = new HashMap<String, Object>(0);

	private Task task;
	private NodeInstance nodeInstance;

	private List<Comment> comments = new ArrayList<Comment>(0);
	private List<Document> attachments = new ArrayList<Document>(0);

	private Set<Incident> incidents = new HashSet<Incident>(0);
	private Set<Problem> problems = new HashSet<Problem>(0);
	private Set<Change> changes = new HashSet<Change>(0);
	private Set<ConfigurationItem> CIs = new HashSet<ConfigurationItem>(0);

	private Set<Project> projects = new HashSet<Project>(0);

	private ProjectTask projectTask;

	private Date planStartTime;
	private Date planEndTime;

	private List<TaskInsHandover> handOvers = new ArrayList<TaskInsHandover>(0);

	private Set<TaskInstance> subTaskInstances = new HashSet<TaskInstance>(0);

	private TaskInstance parentTaskInstance;

	private String workResult;

	public TaskInstance() {
	}

	public TaskInstance(Task task) {
		this.task = task;
		this.name = task.getName();
		this.description = task.getDescription();
	}

	/*
	 * method to add comment
	 */
	public void addComment(String message) {

		this.getComments().add(new Comment(message));
	}

	/*
	 * method to show if task instance has benn closed.
	 */
	public boolean isClosed() {

		return this.getStatus().equals(new Short(TaskInstance.STATUS_CLOSED));
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

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Short getActorType() {
		return this.actorType;
	}

	public void setActorType(Short actorType) {
		this.actorType = actorType;
	}

	public Integer getActorId() {
		return this.actorId;
	}

	public void setActorId(Integer actorId) {
		this.actorId = actorId;
	}

	public Integer getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(Integer auditorId) {
		this.auditorId = auditorId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public List<Comment> getComments() {
		return this.comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public NodeInstance getNodeInstance() {
		return nodeInstance;
	}

	public void setNodeInstance(NodeInstance nodeInstance) {
		this.nodeInstance = nodeInstance;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Set<Incident> getIncidents() {
		return incidents;
	}

	public void setIncidents(Set<Incident> incidents) {
		this.incidents = incidents;
	}

	public Set<Problem> getProblems() {
		return problems;
	}

	public void setProblems(Set<Problem> problems) {
		this.problems = problems;
	}

	public Set<Change> getChanges() {
		return changes;
	}

	public void setChanges(Set<Change> changes) {
		this.changes = changes;
	}

	public Set<ConfigurationItem> getCIs() {
		return CIs;
	}

	public void setCIs(Set<ConfigurationItem> is) {
		CIs = is;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	public Date getPlanEndTime() {
		return planEndTime;
	}

	public void setPlanEndTime(Date planEndTime) {
		this.planEndTime = planEndTime;
	}

	public Date getPlanStartTime() {
		return planStartTime;
	}

	public void setPlanStartTime(Date planStartTime) {
		this.planStartTime = planStartTime;
	}

	public ProjectTask getProjectTask() {
		return projectTask;
	}

	public void setProjectTask(ProjectTask projectTask) {
		this.projectTask = projectTask;
	}

	public List<TaskInsHandover> getHandOvers() {
		return handOvers;
	}

	public void setHandOvers(List<TaskInsHandover> handOvers) {
		this.handOvers = handOvers;
	}

	public TaskInstance getParentTaskInstance() {
		return parentTaskInstance;
	}

	public void setParentTaskInstance(TaskInstance parentTaskInstance) {
		this.parentTaskInstance = parentTaskInstance;
	}

	public Set<TaskInstance> getSubTaskInstances() {
		return subTaskInstances;
	}

	public void setSubTaskInstances(Set<TaskInstance> subTaskInstances) {
		this.subTaskInstances = subTaskInstances;
	}

	public String getWorkResult() {
		return workResult;
	}

	public void setWorkResult(String workResult) {
		this.workResult = workResult;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
}