package com.telinkus.itsm.process.def;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class Node {

	public static final short TYPE_BEGIN = 0;
	public static final short TYPE_END = 1;
	public static final short TYPE_STEP = 2;
	public static final short TYPE_FORK = 3;
	public static final short TYPE_JOIN = 4;
	public static final short TYPE_SUBPROCESS = 5;
	public static final short TYPE_APPROVAL = 6;

	public static final short OWNER_TYPE_PERSON = 0;
	public static final short OWNER_TYPE_ROLE = 1;
	public static final short OWNER_TYPE_INITIATOR = 2;

	public static final short CONTROL_TYPE_ORGANIZATION = 0;
	public static final short CONTROL_TYPE_LOCATION = 1;

	private Integer id;
	private Short type;
	private String name;
	private String description;
	private Short ownerType;
	private Integer ownerId;
	private String ownerName;
	private Boolean floating;
	private Short controlFieldType;
	private String controlFieldName;
	private ProcessDefinition definition;
	private List<Task> tasks = new ArrayList<Task>(0);
	private List<Transition> leavingTransitions = new ArrayList<Transition>(0);
	private Set<Transition> arrivingTransitions = new HashSet<Transition>(0);
	private Short minVote;
	private Integer x;
	private Integer y;

	private String divId;

	private Date planStartTime;
	private Date planEndTime;
	private Float planRelativeTime;
	private Boolean absoluteOrRelative;
	private Short planRelativeTimeUnit;
	private Float planWorkAmount;
	private Short planWorkAmountUnit;

	/**
	 * 执行方式<br>
	 * 并发为immediate，串行为空
	 */
	private String executeMode;

	public Node() {
	}

	public Node(Short type, String name, String description, ProcessDefinition definition, List<Task> tasks, List<Transition> leavingTransitions,
			Set<Transition> arrivingTransitions) {

		this.type = type;
		this.name = name;
		this.description = description;
		this.definition = definition;
		this.tasks = tasks;
		this.leavingTransitions = leavingTransitions;
		this.arrivingTransitions = arrivingTransitions;
	}

	/**
	 * Get the default leaving transition.
	 */
	public Transition getDefaultLeavingTransition() {
		Transition defaultTransition = null;

		if (leavingTransitions.size() > 0) {
			defaultTransition = leavingTransitions.get(0);
		}

		return defaultTransition;
	}

	/**
	 * retrieves a leaving transition by name.
	 */
	public Transition getLeavingTransitionByName(String transitionName) {

		Transition transition = null;

		for (int i = 0; i < this.leavingTransitions.size(); i++) {

			transition = this.leavingTransitions.get(i);
			if (transition.getName().equals(transitionName)) {
				break;
			}
		}

		return transition;
	}

	/**
	 * retrieves a leaving transition by name.
	 */
	public Transition getLeavingTransitionById(Integer transitionId) {

		Transition transition = null;

		for (int i = 0; i < this.leavingTransitions.size(); i++) {

			transition = this.leavingTransitions.get(i);
			if (transition.getId().equals(transitionId)) {
				break;
			}
		}

		return transition;
	}

	/*
	 * method to get task with given name
	 */
	public Task getTask(String taskName) {

		Task task = null;

		for (int i = 0; i < this.tasks.size(); i++) {

			Task task1 = this.tasks.get(i);
			if (task1.getName().equals(taskName)) {

				task = task1;
				break;
			}
		}

		return task;
	}

	// setters and getters:

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Short getType() {
		return this.type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProcessDefinition getDefinition() {
		return this.definition;
	}

	public void setDefinition(ProcessDefinition definition) {
		this.definition = definition;
	}

	public List<Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Transition> getLeavingTransitions() {
		return this.leavingTransitions;
	}

	public void setLeavingTransitions(List<Transition> leavingTransitions) {
		this.leavingTransitions = leavingTransitions;
	}

	public Set<Transition> getArrivingTransitions() {
		return this.arrivingTransitions;
	}

	public void setArrivingTransitions(Set<Transition> arrivingTransitions) {
		this.arrivingTransitions = arrivingTransitions;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Short getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(Short ownerType) {
		this.ownerType = ownerType;
	}

	public Short getMinVote() {
		return minVote;
	}

	public void setMinVote(Short minVote) {
		this.minVote = minVote;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getDivId() {
		return divId;
	}

	public void setDivId(String divId) {
		this.divId = divId;
	}

	public Short getControlFieldType() {
		return controlFieldType;
	}

	public void setControlFieldType(Short controlFieldType) {
		this.controlFieldType = controlFieldType;
	}

	public String getControlFieldName() {
		return controlFieldName;
	}

	public void setControlFieldName(String controlFieldName) {
		this.controlFieldName = controlFieldName;
	}

	public Boolean getFloating() {
		return floating;
	}

	public void setFloating(Boolean floating) {
		this.floating = floating;
	}

	public Boolean getAbsoluteOrRelative() {
		return absoluteOrRelative;
	}

	public void setAbsoluteOrRelative(Boolean absoluteOrRelative) {
		this.absoluteOrRelative = absoluteOrRelative;
	}

	public Date getPlanEndTime() {
		return planEndTime;
	}

	public void setPlanEndTime(Date planEndTime) {
		this.planEndTime = planEndTime;
	}

	public Float getPlanRelativeTime() {
		return planRelativeTime;
	}

	public void setPlanRelativeTime(Float planRelativeTime) {
		this.planRelativeTime = planRelativeTime;
	}

	public Short getPlanRelativeTimeUnit() {
		return planRelativeTimeUnit;
	}

	public void setPlanRelativeTimeUnit(Short planRelativeTimeUnit) {
		this.planRelativeTimeUnit = planRelativeTimeUnit;
	}

	public Date getPlanStartTime() {
		return planStartTime;
	}

	public void setPlanStartTime(Date planStartTime) {
		this.planStartTime = planStartTime;
	}

	public Float getPlanWorkAmount() {
		return planWorkAmount;
	}

	public void setPlanWorkAmount(Float planWorkAmount) {
		this.planWorkAmount = planWorkAmount;
	}

	public Short getPlanWorkAmountUnit() {
		return planWorkAmountUnit;
	}

	public void setPlanWorkAmountUnit(Short planWorkAmountUnit) {
		this.planWorkAmountUnit = planWorkAmountUnit;
	}

	public String getExecuteMode() {
		return executeMode;
	}

	public void setExecuteMode(String executeMode) {
		this.executeMode = executeMode;
	}
}