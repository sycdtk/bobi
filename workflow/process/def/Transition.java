package com.telinkus.itsm.process.def;


public class Transition {
	
	private Integer id;
	private String name;
	private String description;
    private ProcessDefinition definition;
	private Node fromNode;
    private Node toNode;
    private Integer fromX;
    private Integer fromY;
    private Integer toX;
    private Integer toY;
    
	
	public Transition() {
		
	}
	
    public Transition(String name, String description, ProcessDefinition definition, Node fromNode, Node toNode) {
        this.name = name;
        this.description = description;
        this.definition = definition;
        this.fromNode = fromNode;
        this.toNode = toNode;
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
    
    public ProcessDefinition getDefinition() {
        return this.definition;
    }
    
    public void setDefinition(ProcessDefinition definition) {
        this.definition = definition;
    }
	
	public Node getFromNode() {
		return this.fromNode;
	}
	
    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }
    
    public Node getToNode() {
        return this.toNode;
    }
    
    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }

	public Integer getFromX() {
		return fromX;
	}

	public void setFromX(Integer fromX) {
		this.fromX = fromX;
	}

	public Integer getFromY() {
		return fromY;
	}

	public void setFromY(Integer fromY) {
		this.fromY = fromY;
	}

	public Integer getToX() {
		return toX;
	}

	public void setToX(Integer toX) {
		this.toX = toX;
	}

	public Integer getToY() {
		return toY;
	}

	public void setToY(Integer toY) {
		this.toY = toY;
	}
    
}
