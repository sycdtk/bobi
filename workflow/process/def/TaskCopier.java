package com.telinkus.itsm.process.def;

public class TaskCopier {
	
	public static final short TYPE_ACTOR_OF_FOLLOWING_TASKS = 0;
	public static final short TYPE_INITIATOR = 1;
	public static final short TYPE_PERSON = 2;
	public static final short TYPE_ROLE = 3;

	private Integer id;
    private Short type;
    private Integer copierId;
    private String copierName;
    
    
	public String getCopierName() {
		return copierName;
	}

	public void setCopierName(String copierName) {
		this.copierName = copierName;
	}

	public Integer getCopierId() {
		return copierId;
	}
	
	public void setCopierId(Integer copierId) {
		this.copierId = copierId;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Short getType() {
		return type;
	}
	
	public void setType(Short type) {
		this.type = type;
	}
    
    
}
