package com.telinkus.itsm.process.def;

//import java.sql.Date;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.telinkus.itsm.data.project.ProjectTask;

public class Task {
	
	public static final short TYPE_USER = 0;
	public static final short TYPE_AUTO = 1;
	
	public static final short ACTOR_TYPE_PERSON=0;
	public static final short ACTOR_TYPE_ROLE=1;
	public static final short ACTOR_TYPE_INITIATOR=2;
	
	public static final short CONTROL_TYPE_ORGANIZATION = 0;
	public static final short CONTROL_TYPE_LOCATION = 1;
	
	private Integer id;
	private Short type;
	private String name;
	private String description;
    
	//private Date earliestTime;
	//private Date latestTime;
	
	//for user task:
	private Short actorType;
	private Integer actorId;
	private String actorName;
	private Boolean floating;
	private Short controlFieldType;
	private String controlFieldName;
	private Boolean needAudit;
	private List<TaskCopier> copiers = new ArrayList<TaskCopier>(0);
	
	private Boolean internal;
    private String form;
    
	//for external task:
	private Boolean integrative;
	private String url;
    
	//for auto task:
	private String className;
	private String methodName;
	
    private ProcessDefinition definition;
    private Node node;
	
    private Date planStartTime;
    private Date planEndTime;
    
    private Float planWorkAmount;
    private Short planWorkTimeUnit;
    private Short handleWay;
    
    private ProjectTask projectTask;
    
    private String attachmentDefinition;
    
	public Task() {
    }

    public Task(String name, String description, Short actorType, Integer actorId,
    				ProcessDefinition definition, Node node) {
    	
        this.name = name;
        this.description = description;
        this.actorType = actorType;
        this.actorId = actorId;
        this.definition = definition;
        this.node = node;
	}
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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

    /*
    public Date getEarliestTime() {
        return this.earliestTime;
    }
    
    public void setEarliestTime(Date dueTime) {
        this.earliestTime = dueTime;
    }

	public Date getLatestTime() {
		return latestTime;
	}

	public void setLatestTime(Date latestTime) {
		this.latestTime = latestTime;
	}
*/
    
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
    
    public List<TaskCopier> getCopiers() {
        return this.copiers;
    }
    
    public void setCopiers(List<TaskCopier> copiers) {
        this.copiers = copiers;
    }
        
    public String getForm() {
    	return this.form;
    }
    
    public void setForm(String form) {
    	this.form = form;
    }
    
    public ProcessDefinition getDefinition() {
        return this.definition;
    }
    
    public void setDefinition(ProcessDefinition definition) {
        this.definition = definition;
    }
    
    public Node getNode() {
        return this.node;
    }
    
    public void setNode(Node node) {
        this.node = node;
    }

	public Boolean getNeedAudit() {
		return this.needAudit;
	}

	public void setNeedAudit(Boolean needAudit) {
		this.needAudit = needAudit;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
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

	public Boolean isFloating() {
		return floating;
	}

	public void setFloating(Boolean floating) {
		this.floating = floating;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Boolean getFloating() {
		return floating;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Boolean getIntegrative() {
		return integrative;
	}

	public void setIntegrative(Boolean integrative) {
		this.integrative = integrative;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

	public void setPlanEndTime(Date planEndTime) {
		this.planEndTime = planEndTime;
	}

	public void setPlanStartTime(Date planStartTime) {
		this.planStartTime = planStartTime;
	}

	public Date getPlanEndTime() {
		return planEndTime;
	}

	public Date getPlanStartTime() {
		return planStartTime;
	}

	public Short getHandleWay() {
		return handleWay;
	}

	public void setHandleWay(Short handleWay) {
		this.handleWay = handleWay;
	}

	public Float getPlanWorkAmount() {
		return planWorkAmount;
	}

	public void setPlanWorkAmount(Float planWorkAmount) {
		this.planWorkAmount = planWorkAmount;
	}

	public Short getPlanWorkTimeUnit() {
		return planWorkTimeUnit;
	}

	public void setPlanWorkTimeUnit(Short planWorkTimeUnit) {
		this.planWorkTimeUnit = planWorkTimeUnit;
	}

	public ProjectTask getProjectTask() {
		return projectTask;
	}

	public void setProjectTask(ProjectTask projectTask) {
		this.projectTask = projectTask;
	}

	public String getAttachmentDefinition() {
		return attachmentDefinition;
	}

	public void setAttachmentDefinition(String attachmentDefinition) {
		this.attachmentDefinition = attachmentDefinition;
	}

}
