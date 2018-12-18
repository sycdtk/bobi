package com.telinkus.itsm.process.exe;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.telinkus.itsm.process.ProcessException;
import com.telinkus.itsm.process.def.Node;
import com.telinkus.itsm.process.def.ProcessDefinition;
import com.telinkus.itsm.process.def.Task;
import com.telinkus.itsm.process.def.Transition;

public class Token {

	public static final short STATUS_RUNNING = 0;
	public static final short STATUS_ENDED = 1;

	private Long id;
	private ProcessInstance processInstance;
	private NodeInstance currentNodeInstance;
	private List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>(0);
	private Short status;
	private Token parent;
	private List<Token> children = new ArrayList<Token>(0);
	private NodeInstance forkNodeInstance;

	static private Logger logger = Logger.getLogger(Token.class);

	public Token() {
	}

	/*
	 * method to move to a new node
	 */
	public void gotoNode(Node node, Transition transition) {

		ProcessInstance processInstance = this.getProcessInstance();

		short node_type = node.getType().shortValue();

		if (node_type == Node.TYPE_BEGIN) {

			NodeInstance nodeInstance = new NodeInstance(node);
			nodeInstance.setToken(this);

			nodeInstance.getArrivingTransitions().add(transition);

			nodeInstance.setCategory(processInstance.getCategory());
			nodeInstance.setExternalId(processInstance.getExternalId());

			nodeInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));

			// set up links:
			NodeInstance currentNodeInstance = this.getCurrentNodeInstance();
			if (currentNodeInstance != null) {

				currentNodeInstance.getNextNodeInstances().add(nodeInstance);
				currentNodeInstance.getLeavingTransitions().add(transition);

				nodeInstance.getPreviousNodeInstances().add(currentNodeInstance);
			}

			// move pointer:
			this.setCurrentNodeInstance(nodeInstance);
			this.getNodeInstances().add(nodeInstance);

			// close this node instance:
			nodeInstance.setStatus(new Short(NodeInstance.STATUS_CLOSED));

			// go to next node directly:
			Transition leavingTransition = node.getLeavingTransitions().get(0);
			Node nextNode = leavingTransition.getToNode();

			this.gotoNode(nextNode, leavingTransition);

		} else if (node_type == Node.TYPE_END) {

			NodeInstance nodeInstance = new NodeInstance(node);
			nodeInstance.setToken(this);

			nodeInstance.getArrivingTransitions().add(transition);
			nodeInstance.setCategory(processInstance.getCategory());
			nodeInstance.setExternalId(processInstance.getExternalId());

			nodeInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));

			// set up links:
			NodeInstance currentNodeInstance = this.getCurrentNodeInstance();
			if (currentNodeInstance != null) {

				currentNodeInstance.getNextNodeInstances().add(nodeInstance);
				currentNodeInstance.getLeavingTransitions().add(transition);

				nodeInstance.getPreviousNodeInstances().add(currentNodeInstance);
			}

			// move pointer:
			this.setCurrentNodeInstance(nodeInstance);
			this.getNodeInstances().add(nodeInstance);

			// close this node instance:
			nodeInstance.setStatus(new Short(NodeInstance.STATUS_CLOSED));

			// end the process:
			this.getProcessInstance().end();
			logger.info("Process instance(id=" + this.getProcessInstance().getId() + ") ended.");

		} else if (node_type == Node.TYPE_STEP) {

			NodeInstance nodeInstance = new NodeInstance(node);
			nodeInstance.setToken(this);

			nodeInstance.getArrivingTransitions().add(transition);
			nodeInstance.setCategory(processInstance.getCategory());
			nodeInstance.setExternalId(processInstance.getExternalId());

			nodeInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));

			// add by yangrq
			ProcessDefinition pd = null;
			// compare to current time
			boolean bStart = false;
			if (processInstance.getDefinition() != null) {
				pd = processInstance.getDefinition();
				if (pd.getIsProjectCat() != null && pd.getIsProjectCat().intValue() == 1) {
					nodeInstance.setPlanStartTime(node.getPlanStartTime());
					nodeInstance.setPlanEndTime(node.getPlanEndTime());
					Date currentDate = new Date(System.currentTimeMillis());
					if (currentDate.after(nodeInstance.getPlanStartTime())) {
						bStart = true;
					}
				}

			}

			// set up links:
			NodeInstance currentNodeInstance = this.getCurrentNodeInstance();
			if (currentNodeInstance != null) {

				currentNodeInstance.getNextNodeInstances().add(nodeInstance);
				currentNodeInstance.getLeavingTransitions().add(transition);

				nodeInstance.getPreviousNodeInstances().add(currentNodeInstance);
			}

			// move pointer:
			this.setCurrentNodeInstance(nodeInstance);
			this.getNodeInstances().add(nodeInstance);

			if (pd != null && pd.getIsProjectCat() != null && !bStart && pd.getIsProjectCat().intValue() == 1) {
				nodeInstance.setStatus(new Short(NodeInstance.STATUS_STANDBY));
			} else {

				if (node.getOwnerType().shortValue() == Node.OWNER_TYPE_ROLE) {

					// set status to assigned:
					nodeInstance.setStatus(new Short(NodeInstance.STATUS_ASSIGNED));

					// create a task instance:

					TaskInstance taskInstance = new TaskInstance();
					taskInstance.setType(new Short(TaskInstance.TYPE_STEP_TAKEOVER)); // waiting
																						// for
																						// taking
																						// over
																						// step

					Timestamp now = new Timestamp(System.currentTimeMillis());
					taskInstance.setCreateTime(now);
					taskInstance.setCategory(this.getProcessInstance().getCategory());
					taskInstance.setExternalId(this.getProcessInstance().getExternalId());

					taskInstance.setStatus(new Short(TaskInstance.STATUS_ASSIGNED));

					taskInstance.setActorType(new Short(Task.ACTOR_TYPE_ROLE));
					taskInstance.setActorId(node.getOwnerId());

					if (node.getFloating() != null && node.getFloating()) {
						// floating node:
						short ctlFldType = node.getControlFieldType().shortValue();
						String ctlFldName = node.getControlFieldName();

						String ctlFldValue = this.getProcessInstance().getGlobalData().get(ctlFldName);
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

					taskInstance.setNodeInstance(nodeInstance);

					nodeInstance.getTaskInstances().add(taskInstance);

					logger.info("Task instance(name=" + taskInstance.getName() + ", type=TYPE_STEP_TAKEOVER) was created for node instance(name="
							+ nodeInstance.getNode().getName() + ").");
				} else if (node.getOwnerType().shortValue() == Node.OWNER_TYPE_PERSON) {
					// owner is person.
					nodeInstance.setOwnerId(node.getOwnerId());

					// begin the node instance:
					nodeInstance.beginStep();
				} else {
					// owner is initiator of process
					nodeInstance.setOwnerId(this.getProcessInstance().getInitiatorId());

					// begin the node instance:
					nodeInstance.beginStep();
				}
			}
		} else if (node_type == Node.TYPE_FORK) {

			NodeInstance nodeInstance = new NodeInstance(node);
			nodeInstance.setToken(this);

			nodeInstance.getArrivingTransitions().add(transition);
			nodeInstance.setCategory(processInstance.getCategory());
			nodeInstance.setExternalId(processInstance.getExternalId());

			nodeInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));

			// set up links:
			NodeInstance currentNodeInstance = this.getCurrentNodeInstance();
			if (currentNodeInstance != null) {

				currentNodeInstance.getNextNodeInstances().add(nodeInstance);
				currentNodeInstance.getLeavingTransitions().add(transition);

				nodeInstance.getPreviousNodeInstances().add(currentNodeInstance);
			}

			// close this fork node instance:
			nodeInstance.setStatus(new Short(NodeInstance.STATUS_CLOSED));

			// move pointer:
			this.setCurrentNodeInstance(nodeInstance);
			this.getNodeInstances().add(nodeInstance);

			// create child token:
			List<Transition> transitionList = node.getLeavingTransitions();
			for (int i = 0; i < transitionList.size(); i++) {

				Transition leavingTransition = transitionList.get(i);
				Node nextNode = leavingTransition.getToNode();

				Token childToken = new Token();

				childToken.setStatus(new Short(Token.STATUS_RUNNING));
				childToken.setProcessInstance(processInstance);

				// set up connections:
				childToken.setParent(this);
				this.getChildren().add(childToken);

				// set fork node instance:
				childToken.setForkNodeInstance(nodeInstance);

				// set current node instance of child(fake, only for setting up
				// links):
				childToken.setCurrentNodeInstance(nodeInstance);

				// let child token go to next node:
				childToken.gotoNode(nextNode, leavingTransition);
			}

		} else if (node_type == Node.TYPE_JOIN) {

			short minVote = node.getMinVote().shortValue();

			Token parentToken = this.getParent();
			List<NodeInstance> nodeInstanceList = parentToken.getNodeInstances();

			// search if the instance of join node has been created already:
			boolean found = false;
			NodeInstance nodeInstance = null;
			Long forkNodeInstanceId = this.getForkNodeInstance().getId();

			for (int i = nodeInstanceList.size() - 1; i >= 0; i--) {

				nodeInstance = nodeInstanceList.get(i);

				if (nodeInstance.getNode().getId().equals(node.getId()) && nodeInstance.getForkNodeInstance().getId().equals(forkNodeInstanceId)) {

					found = true;
					break;
				}
			}

			if (found) {

				short currentVote = nodeInstance.getVote().shortValue();
				short newVote = (short) (currentVote + 1);
				nodeInstance.setVote(new Short(newVote));

				if (nodeInstance.getStatus().shortValue() == NodeInstance.STATUS_WAITING) {

					if (newVote >= minVote) {

						nodeInstance.setStatus(new Short(NodeInstance.STATUS_CLOSED));

						// move parent token:
						Transition leavingTransition = node.getLeavingTransitions().get(0);
						Node nextNode = leavingTransition.getToNode();

						parentToken.gotoNode(nextNode, leavingTransition);
					}
				}

			} else {

				nodeInstance = new NodeInstance(node);

				// set up links:
				nodeInstance.setToken(parentToken);
				parentToken.getNodeInstances().add(nodeInstance);

				nodeInstance.getArrivingTransitions().add(transition);

				nodeInstance.setCategory(processInstance.getCategory());
				nodeInstance.setExternalId(processInstance.getExternalId());

				nodeInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));

				// set fork node instance:
				nodeInstance.setForkNodeInstance(this.getForkNodeInstance());

				nodeInstance.setVote(new Short("1"));

				if (minVote == 1) {
					// close this join node instance:
					nodeInstance.setStatus(new Short(NodeInstance.STATUS_CLOSED));

					// move parent token:
					Transition leavingTransition = node.getLeavingTransitions().get(0);
					Node nextNode = leavingTransition.getToNode();

					parentToken.gotoNode(nextNode, leavingTransition);

				} else {
					// set its status to waiting:
					nodeInstance.setStatus(new Short(NodeInstance.STATUS_WAITING));
				}
			}

			// set up links:
			NodeInstance currentNodeInstance = this.getCurrentNodeInstance();
			if (currentNodeInstance != null) {

				currentNodeInstance.getNextNodeInstances().add(nodeInstance);
				currentNodeInstance.getLeavingTransitions().add(transition);

				nodeInstance.getPreviousNodeInstances().add(currentNodeInstance);
			}

			// end this (child) token:
			this.setStatus(new Short(STATUS_ENDED));

		} else if (node_type == Node.TYPE_SUBPROCESS) {

		} else if (node_type == Node.TYPE_APPROVAL) {
			NodeInstance nodeInstance = new NodeInstance(node);
			nodeInstance.setToken(this);

			nodeInstance.getArrivingTransitions().add(transition);
			nodeInstance.setCategory(processInstance.getCategory());
			nodeInstance.setExternalId(processInstance.getExternalId());

			nodeInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));

			// add by yangrq
			ProcessDefinition pd = null;
			// compare to current time
			boolean bStart = false;
			if (processInstance.getDefinition() != null) {
				pd = processInstance.getDefinition();
				if (pd.getIsProjectCat() != null && pd.getIsProjectCat().intValue() == 1) {
					nodeInstance.setPlanStartTime(node.getPlanStartTime());
					nodeInstance.setPlanEndTime(node.getPlanEndTime());
					Date currentDate = new Date(System.currentTimeMillis());
					if (currentDate.after(nodeInstance.getPlanStartTime())) {
						bStart = true;
					}
				}

			}

			// set up links:
			NodeInstance currentNodeInstance = this.getCurrentNodeInstance();
			if (currentNodeInstance != null) {

				currentNodeInstance.getNextNodeInstances().add(nodeInstance);
				currentNodeInstance.getLeavingTransitions().add(transition);

				nodeInstance.getPreviousNodeInstances().add(currentNodeInstance);
			}

			// move pointer:
			this.setCurrentNodeInstance(nodeInstance);
			this.getNodeInstances().add(nodeInstance);

			if (pd != null && pd.getIsProjectCat() != null && !bStart && pd.getIsProjectCat().intValue() == 1) {
				nodeInstance.setStatus(new Short(NodeInstance.STATUS_STANDBY));
			} else {
				if (node.getOwnerType().shortValue() == Node.OWNER_TYPE_ROLE) {
					// set status to assigned:
					nodeInstance.setStatus(new Short(NodeInstance.STATUS_ASSIGNED));

					// create a task instance:

					TaskInstance taskInstance = new TaskInstance();
					taskInstance.setType(new Short(TaskInstance.TYPE_NORMAL));

					Timestamp now = new Timestamp(System.currentTimeMillis());
					taskInstance.setCreateTime(now);
					taskInstance.setName(nodeInstance.getNode().getName());
					taskInstance.setCategory(this.getProcessInstance().getCategory());
					taskInstance.setExternalId(this.getProcessInstance().getExternalId());

					taskInstance.setStatus(new Short(TaskInstance.STATUS_ASSIGNED));

					taskInstance.setActorType(new Short(Task.ACTOR_TYPE_ROLE));
					taskInstance.setActorId(node.getOwnerId());

					if (node.getFloating() != null && node.getFloating()) {
						// floating node:
						short ctlFldType = node.getControlFieldType().shortValue();
						String ctlFldName = node.getControlFieldName();

						String ctlFldValue = this.getProcessInstance().getGlobalData().get(ctlFldName);
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

					taskInstance.setNodeInstance(nodeInstance);

					nodeInstance.getTaskInstances().add(taskInstance);

					logger.info("Task instance(name=" + taskInstance.getName() + ", type=TYPE_STEP_TAKEOVER) was created for node instance(name="
							+ nodeInstance.getNode().getName() + ").");
				} else if (node.getOwnerType().shortValue() == Node.OWNER_TYPE_PERSON) {
					// owner is person.
					nodeInstance.setOwnerId(node.getOwnerId());

					TaskInstance taskInstance = new TaskInstance();

					taskInstance.setName(nodeInstance.getNode().getName());
					taskInstance.setBeginTime(new Timestamp(System.currentTimeMillis()));
					taskInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));
					taskInstance.setCategory(this.getProcessInstance().getCategory());
					taskInstance.setExternalId(this.getProcessInstance().getExternalId());
					taskInstance.setNodeInstance(nodeInstance);
					taskInstance.setStatus(new Short(TaskInstance.STATUS_PROCESSING));

					taskInstance.setType(TaskInstance.TYPE_NORMAL);

					taskInstance.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
					taskInstance.setActorId(node.getOwnerId());
					
					// add it to task insance List:
					nodeInstance.getTaskInstances().add(taskInstance);

					// begin the node instance:
					nodeInstance.beginStep();
				} else {
					// owner is initiator of process
					nodeInstance.setOwnerId(this.getProcessInstance().getInitiatorId());

					TaskInstance taskInstance = new TaskInstance();

					taskInstance.setName(nodeInstance.getNode().getName());
					taskInstance.setBeginTime(new Timestamp(System.currentTimeMillis()));
					taskInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));
					taskInstance.setCategory(this.getProcessInstance().getCategory());
					taskInstance.setExternalId(this.getProcessInstance().getExternalId());
					taskInstance.setNodeInstance(nodeInstance);
					taskInstance.setStatus(new Short(TaskInstance.STATUS_PROCESSING));

					taskInstance.setType(TaskInstance.TYPE_NORMAL);

					taskInstance.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
					taskInstance.setActorId(this.getProcessInstance().getInitiatorId());
					
					// add it to task insance List:
					nodeInstance.getTaskInstances().add(taskInstance);

					// begin the node instance:
					nodeInstance.beginStep();
				}
			}
		}

	}// gotoNode()

	/*
	 * method to end token
	 */
	public void end() {

		List<Token> childTokenList;
		List<NodeInstance> nodeInstanceList;
		int i, j;

		// end all child token:
		childTokenList = this.getChildren();
		for (i = 0; i < childTokenList.size(); i++) {

			Token childToken = childTokenList.get(i);
			childToken.end();
		}

		// find all non-closed node instance, cancel them and their task
		// instances:
		nodeInstanceList = this.getNodeInstances();
		for (i = 0; i < nodeInstanceList.size(); i++) {

			NodeInstance nodeInstance = nodeInstanceList.get(i);

			short node_inst_status = nodeInstance.getStatus().shortValue();
			if (((node_inst_status == NodeInstance.STATUS_ASSIGNED) || (node_inst_status == NodeInstance.STATUS_RUNNING)
					|| (node_inst_status == NodeInstance.STATUS_STANDBY) || (node_inst_status == NodeInstance.STATUS_WAITING))) {

				nodeInstance.setStatus(new Short(NodeInstance.STATUS_CANCELLED));

				// find all its task instance:
				List<TaskInstance> taskInstanceList = nodeInstance.getTaskInstances();
				for (j = 0; j < taskInstanceList.size(); j++) {

					TaskInstance taskInstance = taskInstanceList.get(j);

					short task_inst_status = taskInstance.getStatus().shortValue();
					if ((task_inst_status == TaskInstance.STATUS_QUEUED) || (task_inst_status == TaskInstance.STATUS_FOR_AUDIT)
							|| (task_inst_status == TaskInstance.STATUS_PROCESSING) || (task_inst_status == TaskInstance.STATUS_ASSIGNED)) {

						taskInstance.setStatus(new Short(TaskInstance.STATUS_CANCELLED));
					}
				}
			}

		}

		this.setStatus(new Short(Token.STATUS_ENDED));

	}// end()

	/*
	 * method to leave one node then enter next node by first(default) one of
	 * the leaving transitions
	 */
	public void signal() throws Exception {

		Transition transition = this.getCurrentNodeInstance().getNode().getDefaultLeavingTransition();
		signal(transition);
	}

	/*
	 * method to leave one node then enter next node by the tranistion given
	 */
	public void signal(String transitionName) throws Exception {

		Transition transition = this.getCurrentNodeInstance().getNode().getLeavingTransitionByName(transitionName);
		signal(transition);
	}

	/*
	 * method to leave one node then enter next node by the tranistion given
	 */
	public void signal(Integer transitionId) throws Exception {

		Transition transition = this.getCurrentNodeInstance().getNode().getLeavingTransitionById(transitionId);
		signal(transition);
	}

	/*
	 * method to leave one node then enter next node by the tranistion given
	 */
	private void signal(Transition transition) {

		if (transition == null) {

			throw new ProcessException("couldn't signal without specifying a leaving transition : transition is null");

		} else {

			Node nextNode = transition.getToNode();

			if (nextNode == null) {
				throw new ProcessException("couldn't signal without a to node :to node is null");
			}

			this.getCurrentNodeInstance().getLeavingTransitions().add(transition);

			// move pointer to next node:
			this.gotoNode(nextNode, transition);
		}
	}

	/*
	 * method to complete task instance
	 */
	public void completeTaskInstance(TaskInstance taskInstance, Transition transition) {

		List<Transition> transitionList;
		NodeInstance currentNodeInstance;

		currentNodeInstance = this.getCurrentNodeInstance();
		transitionList = currentNodeInstance.completeTaskInstance(taskInstance);

		if (taskInstance.getProjectTask() != null) {
			if (taskInstance.getEndTime() != null) {
				taskInstance.getProjectTask().setEndTime(new Timestamp(taskInstance.getEndTime().getTime()));
				taskInstance.getProjectTask().setFinishPercentage(new Short("100"));
			}

			taskInstance.getProjectTask().setTaskStatus(taskInstance.getStatus());
		}

		if (transition != null) {

			this.signal(transition);

		} else {

			if (transitionList != null) {

				if (transitionList.size() == 1) {

					Transition transition1 = transitionList.get(0);
					this.getCurrentNodeInstance().getLeavingTransitions().add(transition1);
					this.signal(transition1);

					logger.info("Token signaled through tranisiton(id=" + transition1.getId() + ").");

				} else { // create a task instance of type branch choosing:

					ProcessInstance processInstance = this.getProcessInstance();

					TaskInstance branchTaskInstance = new TaskInstance();

					branchTaskInstance.setCategory(processInstance.getCategory());
					branchTaskInstance.setExternalId(processInstance.getExternalId());

					branchTaskInstance.setType(new Short(TaskInstance.TYPE_BRANCH));
					branchTaskInstance.setNodeInstance(this.getCurrentNodeInstance());

					branchTaskInstance.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
					branchTaskInstance.setActorId(this.getCurrentNodeInstance().getOwnerId());

					branchTaskInstance.setStatus(new Short(TaskInstance.STATUS_PROCESSING));
					branchTaskInstance.setCreateTime(new Timestamp(System.currentTimeMillis()));

					// add it to current node instance:
					this.getCurrentNodeInstance().getTaskInstances().add(branchTaskInstance);

					logger.info("Task instance(id=" + branchTaskInstance.getId() + ", type=TYPE_BRANCH) was created for node instance(id="
							+ this.getCurrentNodeInstance().getId() + ").");
				}

			}

		}

	}// completeTaskInstance()

	/*
	 * method to close task instance
	 */
	public void closeTaskInstance(TaskInstance taskInstance, Transition transition) {

		List<Transition> transitionList;
		NodeInstance currentNodeInstance;

		currentNodeInstance = this.getCurrentNodeInstance();
		transitionList = currentNodeInstance.closeTaskInstance(taskInstance);

		if (taskInstance.getProjectTask() != null) {
			if (taskInstance.getEndTime() != null) {
				taskInstance.getProjectTask().setEndTime(new Timestamp(taskInstance.getEndTime().getTime()));
			}

			taskInstance.getProjectTask().setTaskStatus(taskInstance.getStatus());
		}

		if (transition != null) {

			this.signal(transition);

		} else {

			// transitionList =
			// currentNodeInstance.getNode().getLeavingTransitions();

			if (transitionList != null) {

				if (transitionList.size() == 1) {

					Transition transition1 = transitionList.get(0);
					this.getCurrentNodeInstance().getLeavingTransitions().add(transition1);
					this.signal(transition1);

					logger.info("Token signaled through tranisiton(id=" + transition1.getId() + ").");

				} else { // create a task instance of type branch choosing:

					TaskInstance branchTaskInstance = new TaskInstance();

					branchTaskInstance.setCategory(this.getProcessInstance().getCategory());
					branchTaskInstance.setExternalId(this.getProcessInstance().getExternalId());

					branchTaskInstance.setType(new Short(TaskInstance.TYPE_BRANCH));
					branchTaskInstance.setNodeInstance(this.getCurrentNodeInstance());

					branchTaskInstance.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
					branchTaskInstance.setActorId(this.getCurrentNodeInstance().getOwnerId());

					branchTaskInstance.setStatus(new Short(TaskInstance.STATUS_PROCESSING));

					Timestamp now = new Timestamp(System.currentTimeMillis());
					branchTaskInstance.setCreateTime(now);

					// add it to current node instance:
					this.getCurrentNodeInstance().getTaskInstances().add(branchTaskInstance);

					logger.info("Task instance(id=" + branchTaskInstance.getId() + ", type=TYPE_BRANCH) was created for node instance(id="
							+ this.getCurrentNodeInstance().getId() + ").");
				}
			}
		}

	}// closeTaskInstance()

	// setters and getters:

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProcessInstance getProcessInstance() {
		return this.processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public NodeInstance getCurrentNodeInstance() {
		return currentNodeInstance;
	}

	public void setCurrentNodeInstance(NodeInstance currentNodeInstance) {
		this.currentNodeInstance = currentNodeInstance;
	}

	public List<NodeInstance> getNodeInstances() {
		return nodeInstances;
	}

	public void setNodeInstances(List<NodeInstance> nodeInstances) {
		this.nodeInstances = nodeInstances;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Token getParent() {
		return parent;
	}

	public void setParent(Token parent) {
		this.parent = parent;
	}

	public List<Token> getChildren() {
		return children;
	}

	public void setChildren(List<Token> children) {
		this.children = children;
	}

	public NodeInstance getForkNodeInstance() {
		return forkNodeInstance;
	}

	public void setForkNodeInstance(NodeInstance forkNodeInstance) {
		this.forkNodeInstance = forkNodeInstance;
	}

}
