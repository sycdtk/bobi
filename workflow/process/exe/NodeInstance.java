package com.telinkus.itsm.process.exe;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.telinkus.itsm.data.library.Document;
import com.telinkus.itsm.data.project.ProjectTask;
import com.telinkus.itsm.process.def.Node;
import com.telinkus.itsm.process.def.Task;
import com.telinkus.itsm.process.def.Transition;

public class NodeInstance {

	public static final short STATUS_ASSIGNED = 0;
	public static final short STATUS_RUNNING = 1;
	public static final short STATUS_CLOSED = 2;
	public static final short STATUS_CANCELLED = 3;
	public static final short STATUS_WAITING = 4;
	public static final short STATUS_STANDBY = 5;

	public static final short TYPE_APPROVAL_NO = 0;
	public static final short TYPE_APPROVAL_YES = 1;

	public static final String EXECUTEMODE_IMMEDIATE = "immediate";

	private Long id;
	private Node node;
	private Integer ownerId;
	private Short status;
	private Short category;
	private Long externalId;
	private Date createTime;
	private Date beginTime;
	private Date endTime;
	private Token token;

	private Date planStartTime;
	private Date planEndTime;

	private List<TaskInstance> taskInstances = new ArrayList<TaskInstance>(0);
	private List<Transition> arrivingTransitions = new ArrayList<Transition>(0);
	private List<Transition> leavingTransitions = new ArrayList<Transition>(0);
	private List<NodeInstance> previousNodeInstances = new ArrayList<NodeInstance>(0);
	private List<NodeInstance> nextNodeInstances = new ArrayList<NodeInstance>(0);
	private List<Document> attachments = new ArrayList<Document>(0);
	private Short vote;
	private NodeInstance forkNodeInstance;

	private Short approvalType; // 审批类型：0：否决；1：同意；
	private String comment;

	/**
	 * 执行方式<br>
	 * 并发为immediate，串行为空
	 */
	private String executeMode;

	private List<NodeInsHandover> handOvers = new ArrayList<NodeInsHandover>(0);

	static private Logger logger = Logger.getLogger(NodeInstance.class);

	public NodeInstance() {

	}

	public NodeInstance(Node node) {
		this.node = node;
		if (null != node) {
			this.executeMode = node.getExecuteMode();
		}
	}

	/*
	 * method to begin running of a step node instance
	 */
	public void beginStep() {

		ProcessInstance processInstance = this.getToken().getProcessInstance();

		this.setStatus(new Short(STATUS_RUNNING));
		this.setBeginTime(new Timestamp(System.currentTimeMillis()));

		List<Task> taskList = this.getNode().getTasks();

		for (int i = 0; i < taskList.size(); i++) {

			// get task for execution:
			Task task = taskList.get(i);
			TaskInstance taskInstance = new TaskInstance(task);

			taskInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));
			taskInstance.setCategory(this.getToken().getProcessInstance().getCategory());
			taskInstance.setExternalId(this.getToken().getProcessInstance().getExternalId());

			if (task.getProjectTask() != null) {
				taskInstance.setProjectTask(task.getProjectTask());
				task.getProjectTask().setTaskInstance(taskInstance);
				task.getProjectTask().setStartTime(new Timestamp(taskInstance.getCreateTime().getTime()));
			}

			taskInstance.setNodeInstance(this);

			// add it to task insance List:
			this.getTaskInstances().add(taskInstance);

			if (task.getType() == Task.TYPE_USER) {

				taskInstance.setType(TaskInstance.TYPE_NORMAL);

				// set actor:
				short actorType = task.getActorType();
				if (actorType == Task.ACTOR_TYPE_INITIATOR) {

					taskInstance.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
					taskInstance.setActorId(this.getToken().getProcessInstance().getInitiatorId());

				} else if (actorType == Task.ACTOR_TYPE_ROLE) {

					if (task.getHandleWay() != null && task.getHandleWay().intValue() == 1) {
						Integer roleId = task.getActorId();

					}

					// if task actor is same as node owner:
					if (this.getNode().getOwnerType() == Node.OWNER_TYPE_ROLE && this.getNode().getOwnerId().equals(task.getActorId())) {

						// set actor of task to owner of the node instance
						taskInstance.setActorType(Task.ACTOR_TYPE_PERSON);
						taskInstance.setActorId(this.getOwnerId());

					} else {

						taskInstance.setActorType(Task.ACTOR_TYPE_ROLE);
						taskInstance.setActorId(task.getActorId());

						if (task.getFloating() != null && task.getFloating()) {
							// floating task:
							short ctlFldType = task.getControlFieldType();
							String ctlFldName = task.getControlFieldName();

							String ctlFldValue = this.getToken().getProcessInstance().getGlobalData().get(ctlFldName);
							if (ctlFldValue != null) {

								int idx = ctlFldValue.indexOf(":");

								if (idx > 0) {

									if (ctlFldType == Task.CONTROL_TYPE_ORGANIZATION) {

										taskInstance.setOrganizationId(new Integer(ctlFldValue.substring(0, idx)));

									} else {

										taskInstance.setLocationId(new Integer(ctlFldValue.substring(0, idx)));
									}
								}
							}

						}

					}

				} else {
					// person:
					taskInstance.setActorType(Task.ACTOR_TYPE_PERSON);
					taskInstance.setActorId(task.getActorId());
				}

				// set auditor:
				if ((task.getNeedAudit() != null) && (task.getNeedAudit().booleanValue() == true)) {

					taskInstance.setAuditorId(this.getOwnerId());
				}

				// set status:
				if (i == 0 || NodeInstance.EXECUTEMODE_IMMEDIATE.equals(this.getNode().getExecuteMode())) {
					// first task instance:
					if (taskInstance.getActorType() == Task.ACTOR_TYPE_ROLE) {

						taskInstance.setStatus(new Short(TaskInstance.STATUS_ASSIGNED));

					} else {

						taskInstance.setBeginTime(new Timestamp(System.currentTimeMillis()));
						taskInstance.setStatus(TaskInstance.STATUS_PROCESSING);
					}
				} else {
					taskInstance.setStatus(TaskInstance.STATUS_QUEUED);
				}
				if (task.getProjectTask() != null) {
					task.getProjectTask().setTaskStatus(taskInstance.getStatus());
				}

			} else {
				// auto task:
				taskInstance.setType(TaskInstance.TYPE_AUTO);

				// set auditor:
				if ((task.getNeedAudit() != null) && task.getNeedAudit()) {

					taskInstance.setAuditorId(this.getOwnerId());
				}

				// set status:
				// TODO
				/*
				 * 暂时没有用到自动任务，所以先不考虑此处的并发情况，并且如果要做需要考虑: 1)判断是否是最后一个任务，确认是否需要关闭节点
				 * 2)自动任务是此节点的最后一个任务，此节点以后有分支，还需要判断是否需要选择分支
				 */
				if (i == 0) {
					// first task instance, execute it at once:
					taskInstance.setBeginTime(new Timestamp(System.currentTimeMillis()));

					String className = task.getClassName();
					String methodName = task.getMethodName();

					try {

						Class<?> c = Class.forName(className);
						Class[] argTypes = new Class[] { Long.class, Map.class };

						Method method = c.getDeclaredMethod(methodName, argTypes);

						// set actor id to global data:
						processInstance.getGlobalData().put("actorId", this.getOwnerId().toString());
						Map newGlobalData = (Map) method.invoke(null, processInstance.getId(), processInstance.getGlobalData());

						// set return global data, make it effect:
						if (newGlobalData != null && newGlobalData.size() > 0)
							processInstance.getGlobalData().putAll(newGlobalData);

						taskInstance.setEndTime(new Timestamp(System.currentTimeMillis()));
						taskInstance.setSuccess(true);

					} catch (Exception e) {

						e.printStackTrace();

						if (taskInstance.getAuditorId() != null) {
							taskInstance.setStatus(TaskInstance.STATUS_FOR_AUDIT);
						} else {
							taskInstance.setStatus(TaskInstance.STATUS_CLOSED);
						}

						taskInstance.setSuccess(false);
						taskInstance.setErrorMessage(e.getLocalizedMessage());

					}

					// call token to close this task instance:
					this.getToken().completeTaskInstance(taskInstance, null);

				} else {
					taskInstance.setStatus(new Short(TaskInstance.STATUS_QUEUED));
				}
			}

		}

		logger.info("Step Node(id=" + this.getId() + ") was started.");

	}// beginStep()

	/*
	 * method to complete one task and then open next task in the list
	 */
	public List<Transition> completeTaskInstance(TaskInstance taskInstance) {

		// check if auditing is needed:
		if (taskInstance.getAuditorId() != null) {

			taskInstance.setStatus(new Short(TaskInstance.STATUS_FOR_AUDIT));
			return null;

		} else {

			return closeTaskInstance(taskInstance);
		}

	}// completeTaskInstance()

	/*
	 * method to close task instance:
	 */
	public List<Transition> closeTaskInstance(TaskInstance taskInstance) {

		int i;
		List<Transition> transitionList = null;

		ProcessInstance processInstance = this.getToken().getProcessInstance();

		// close this task:
		taskInstance.setEndTime(new Timestamp(System.currentTimeMillis()));
		taskInstance.setStatus(new Short(TaskInstance.STATUS_CLOSED));
		logger.info("Task instance(id=" + taskInstance.getId() + ") was completed.");

		i = this.getTaskInstances().indexOf(taskInstance);

		if (NodeInstance.EXECUTEMODE_IMMEDIATE.equals(this.getExecuteMode())) {// 并行
			if (NodeInstance.isAllTaskClose(this)) {
				// reach last task instance of this node instance, close this
				// node instance:
				this.setStatus(new Short(NodeInstance.STATUS_CLOSED));
				this.setEndTime(new Timestamp(System.currentTimeMillis()));

				// return transitions to leave current node:
				transitionList = this.getNode().getLeavingTransitions();

			}
		} else {// 串行
			if (i == this.getTaskInstances().size() - 1) {
				// reach last task instance of this node instance, close this
				// node instance:
				this.setStatus(new Short(NodeInstance.STATUS_CLOSED));
				this.setEndTime(new Timestamp(System.currentTimeMillis()));

				// return transitions to leave current node:
				transitionList = this.getNode().getLeavingTransitions();

			} else {
				// open next task instance for execution:
				TaskInstance nextTaskInstance = this.getTaskInstances().get(i + 1);

				if (nextTaskInstance.getType() == TaskInstance.TYPE_AUTO) {

					Task task = nextTaskInstance.getTask();
					String className = task.getClassName();
					String methodName = task.getMethodName();

					try {

						Class<?> c = Class.forName(className);
						Class[] argTypes = new Class[] { Long.class, Map.class };

						Method method = c.getDeclaredMethod(methodName, argTypes);

						// set actor id to global data:
						processInstance.getGlobalData().put("actorId", this.getOwnerId().toString());
						Map newGlobalData = (Map) method.invoke(null, processInstance.getId(), processInstance.getGlobalData());

						// add return map to global data, make it effect:
						if (newGlobalData != null && newGlobalData.size() > 0)
							processInstance.getGlobalData().putAll(newGlobalData);

						taskInstance.setEndTime(new Timestamp(System.currentTimeMillis()));
						nextTaskInstance.setSuccess(true);

					} catch (Exception e) {

						e.printStackTrace();

						if (nextTaskInstance.getAuditorId() != null) {
							nextTaskInstance.setStatus(TaskInstance.STATUS_FOR_AUDIT);
						} else {
							nextTaskInstance.setStatus(TaskInstance.STATUS_CLOSED);
						}

						nextTaskInstance.setSuccess(false);
						nextTaskInstance.setErrorMessage(e.getLocalizedMessage());

						return null;

					}

					// close next task instance(for audit):
					return closeTaskInstance(nextTaskInstance);

				} else {

					if (nextTaskInstance.getActorType().shortValue() == Task.ACTOR_TYPE_ROLE)
						nextTaskInstance.setStatus(new Short(TaskInstance.STATUS_ASSIGNED));
					else {
						nextTaskInstance.setStatus(new Short(TaskInstance.STATUS_PROCESSING));
						nextTaskInstance.setBeginTime(new Timestamp(System.currentTimeMillis()));
						Task tempTask = nextTaskInstance.getTask();
						if (tempTask != null) {
							ProjectTask tempPJTask = tempTask.getProjectTask();
							if (tempPJTask != null) {
								tempPJTask.setStartTime(new Timestamp(System.currentTimeMillis()));
							}
						}
					}
				}
			}
		}

		return transitionList;

	}// closeTaskInstance()

	/**
	 * 检查一个节点实例的任务是否全部完成
	 * 
	 * @param nodeInstance
	 * @return
	 */
	public static boolean isAllTaskClose(NodeInstance nodeInstance) {
		List<TaskInstance> list = nodeInstance.getTaskInstances();
		TaskInstance tempTaskInstance;
		for (int i = 0; i < list.size(); i++) {
			tempTaskInstance = list.get(i);
			if (TaskInstance.STATUS_CLOSED != tempTaskInstance.getStatus()) {
				return false;
			}
		}
		return true;
	}

	/*
	 * method to execute auto task
	 */
	public TaskInstance retryAutoTask(TaskInstance taskInstance) {

		ProcessInstance processInstance = this.getToken().getProcessInstance();
		Task task = taskInstance.getTask();

		// execute it:
		String className = task.getClassName();
		String methodName = task.getMethodName();

		try {

			Class<?> c = Class.forName(className);
			Class[] argTypes = new Class[] { Long.class, Map.class };

			Method method = c.getDeclaredMethod(methodName, argTypes);

			// set actor id to global data:
			processInstance.getGlobalData().put("actorId", this.getOwnerId().toString());
			Map newGlobalData = (Map) method.invoke(null, processInstance.getId(), processInstance.getGlobalData());

			// set return global data, make it effect:
			if (newGlobalData != null && newGlobalData.size() > 0)
				processInstance.getGlobalData().putAll(newGlobalData);

			taskInstance.setEndTime(new Timestamp(System.currentTimeMillis()));
			taskInstance.setSuccess(true);
			taskInstance.setErrorMessage(null);

			// call token to close this task instance:
			this.getToken().closeTaskInstance(taskInstance, null);

		} catch (Exception e) {

			e.printStackTrace();

			taskInstance.setSuccess(false);
			taskInstance.setErrorMessage(e.getLocalizedMessage());

			// set status to "for audit":
			taskInstance.setStatus(TaskInstance.STATUS_FOR_AUDIT);

		}

		return taskInstance;

	}// retryAutoTask()

	// setters and getters:

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Short getCategory() {
		return category;
	}

	public void setCategory(Short category) {
		this.category = category;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public List<TaskInstance> getTaskInstances() {
		return taskInstances;
	}

	public void setTaskInstances(List<TaskInstance> taskInstances) {
		this.taskInstances = taskInstances;
	}

	public List<Transition> getArrivingTransitions() {
		return arrivingTransitions;
	}

	public void setArrivingTransitions(List<Transition> arrivingTransitions) {
		this.arrivingTransitions = arrivingTransitions;
	}

	public List<Transition> getLeavingTransitions() {
		return leavingTransitions;
	}

	public void setLeavingTransitions(List<Transition> leavingTransitions) {
		this.leavingTransitions = leavingTransitions;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<NodeInstance> getNextNodeInstances() {
		return nextNodeInstances;
	}

	public void setNextNodeInstances(List<NodeInstance> nextNodeInstances) {
		this.nextNodeInstances = nextNodeInstances;
	}

	public List<NodeInstance> getPreviousNodeInstances() {
		return previousNodeInstances;
	}

	public void setPreviousNodeInstances(List<NodeInstance> previousNodeInstances) {
		this.previousNodeInstances = previousNodeInstances;
	}

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

	public Short getVote() {
		return vote;
	}

	public void setVote(Short vote) {
		this.vote = vote;
	}

	public NodeInstance getForkNodeInstance() {
		return forkNodeInstance;
	}

	public void setForkNodeInstance(NodeInstance forkNodeInstance) {
		this.forkNodeInstance = forkNodeInstance;
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

	public List<NodeInsHandover> getHandOvers() {
		return handOvers;
	}

	public void setHandOvers(List<NodeInsHandover> handOvers) {
		this.handOvers = handOvers;
	}

	public String getExecuteMode() {
		return executeMode;
	}

	public void setExecuteMode(String executeMode) {
		this.executeMode = executeMode;
	}

	public Short getApprovalType() {
		return approvalType;
	}

	public void setApprovalType(Short approvalType) {
		this.approvalType = approvalType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}