package com.telinkus.itsm.process.def;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProcessDefinition {
	
	private Integer id;
    private String name;
    private String description;
    private Short version;
    private String xml;
    private Node beginNode;
    private List<Node> nodes = new ArrayList<Node>(0);
    
    private Short isProjectCat;
    private Short usable;
    
    private Set<com.telinkus.itsm.data.codes.incident.Category> incidentCategories = new HashSet<com.telinkus.itsm.data.codes.incident.Category>(0);
    private Set<com.telinkus.itsm.data.codes.problem.Category> problemCategories = new HashSet<com.telinkus.itsm.data.codes.problem.Category>(0);
    private Set<com.telinkus.itsm.data.codes.change.Category> changeCategories = new HashSet<com.telinkus.itsm.data.codes.change.Category>(0);
    private Set<com.telinkus.itsm.data.codes.device.operation.Category> operationCategories = new HashSet<com.telinkus.itsm.data.codes.device.operation.Category>(0);
    private Set<com.telinkus.itsm.data.areavisit.Area> areas = new HashSet<com.telinkus.itsm.data.areavisit.Area>(0);

	private Set<com.telinkus.itsm.data.codes.incident.Category> postIncidentCategories = new HashSet<com.telinkus.itsm.data.codes.incident.Category>(0);
	private Set<com.telinkus.itsm.data.codes.problem.Category> postProblemCategories = new HashSet<com.telinkus.itsm.data.codes.problem.Category>(0);
	private Set<com.telinkus.itsm.data.codes.change.Category> postChangeCategories = new HashSet<com.telinkus.itsm.data.codes.change.Category>(0);

	//add by lxf begin
	private Set<com.telinkus.itsm.data.codes.reqaccept.Category> reqacceptCategories = new HashSet<com.telinkus.itsm.data.codes.reqaccept.Category>(0);    
	private Set<com.telinkus.itsm.data.codes.reqaccept.Category> postReqacceptCategories = new HashSet<com.telinkus.itsm.data.codes.reqaccept.Category>(0);
	//add by lxf end
	
	//add by qizhe in 
	private Set<com.telinkus.itsm.data.release2.Category> release2Categories = new HashSet<com.telinkus.itsm.data.release2.Category>(0);
	private Set<com.telinkus.itsm.data.release2.Category> postRelease2Categories = new HashSet<com.telinkus.itsm.data.release2.Category>(0);
	
	//end by qizhe 
	
	public ProcessDefinition() {
		
	}
	
	public ProcessDefinition(Integer id, String name, Short version) {
		this.id = id;
		this.name = name;
		this.version = version;
	}
	
	public ProcessDefinition(Integer id, String name, String description, Short version) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.version = version;
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
    
    public Short getVersion() {
        return this.version;
    }
    
    public void setVersion(Short version) {
        this.version = version;
    }
    public String getXml() {
        return this.xml;
    }
    
    public void setXml(String xml) {
        this.xml = xml;
    }
    
    public Node getBeginNode() {
        return this.beginNode;
    }
    
    public void setBeginNode(Node beginNode) {
        this.beginNode = beginNode;
    }

    public List<Node> getNodes() {
        return this.nodes;
    }
    
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
        
	public Set<com.telinkus.itsm.data.codes.change.Category> getChangeCategories() {
		return changeCategories;
	}

	public void setChangeCategories(
			Set<com.telinkus.itsm.data.codes.change.Category> changeCategories) {
		this.changeCategories = changeCategories;
	}

	public Set<com.telinkus.itsm.data.codes.incident.Category> getIncidentCategories() {
		return incidentCategories;
	}

	public void setIncidentCategories(
			Set<com.telinkus.itsm.data.codes.incident.Category> incidentCategories) {
		this.incidentCategories = incidentCategories;
	}

	public Set<com.telinkus.itsm.data.codes.device.operation.Category> getOperationCategories() {
		return operationCategories;
	}

	public void setOperationCategories(
			Set<com.telinkus.itsm.data.codes.device.operation.Category> operationCategories) {
		this.operationCategories = operationCategories;
	}

	public Set<com.telinkus.itsm.data.codes.problem.Category> getProblemCategories() {
		return problemCategories;
	}

	public void setProblemCategories(
			Set<com.telinkus.itsm.data.codes.problem.Category> problemCategories) {
		this.problemCategories = problemCategories;
	}

	/*
	 * 
	 */
	public static ProcessDefinition parseXmlString(String xml) throws Exception {

		
		Document doc;
		ProcessDefinition def = null;
		Element root;
		int i, j, k;
		String description;
		
		SAXReader xmlReader = new SAXReader();
		
		doc = xmlReader.read(new StringReader(xml));
		
		root = doc.getRootElement();
		
		def = new ProcessDefinition();
		def.setName(root.attributeValue("name"));
		def.setDescription(root.attributeValue("description"));
		def.setXml(xml);
		
		for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
            Element element = it.next();
         
            if(element.getName().equals("begin")) {
            	
            	Node beginNode = new Node();
            	beginNode.setName("begin");
            	beginNode.setType(new Short(Node.TYPE_BEGIN));
            	
            	beginNode.setDivId(element.attributeValue("divId")); //div=divID
            	
            	beginNode.setX(new Integer(element.attributeValue("x")));
            	beginNode.setY(new Integer(element.attributeValue("y")));
            	
            	//get transition leaving from begin node:
            	Element transElement = element.element("transition");
            	Transition transition = new Transition();
            	
            	transition.setName(transElement.attributeValue("name"));
            	
            	description = transElement.attributeValue("description");
            	if((description!= null) && (description.length() > 0))
            		transition.setDescription(description);
            	
            	transition.setFromX(new Integer(transElement.attributeValue("fromX")));
            	transition.setFromY(new Integer(transElement.attributeValue("fromY")));
            	transition.setToX(new Integer(transElement.attributeValue("toX")));
            	transition.setToY(new Integer(transElement.attributeValue("toY")));
            	
            	transition.setDefinition(def);
            	transition.setFromNode(beginNode);
            	
            	//set temporary to node:
            	Node toNode = new Node();
            	toNode.setDivId(transElement.attributeValue("to"));
            	transition.setToNode(toNode);

            	beginNode.getLeavingTransitions().add(transition);
            	
            	//connect definition with start node:
            	beginNode.setDefinition(def);
            	def.setBeginNode(beginNode);
            	def.getNodes().add(beginNode);
             	
            } else if(element.getName().equals("end")) {
            	
            	Node endNode = new Node();
            	endNode.setName("end");
            	endNode.setType(new Short(Node.TYPE_END));
            	
            	endNode.setDivId(element.attributeValue("divId")); //div=divID
            	
            	endNode.setX(new Integer(element.attributeValue("x")));
            	endNode.setY(new Integer(element.attributeValue("y")));
            	
            	//connect definition with end node:
            	endNode.setDefinition(def);
            	def.getNodes().add(endNode);
            	
            } else if(element.getName().equals("step-node")) {
            	//step-node
            	Node node = new Node();
            	
        		node.setType(new Short(Node.TYPE_STEP));
            	node.setName(element.attributeValue("name"));
            	
            	description = element.attributeValue("description");
            	if((description!= null) && (description.length() > 0))
            		node.setDescription(description);
            	
            	// executeMode,串行为空
            	String executeModeStr = element.attributeValue("executeMode");
            	if("immediate".equals(executeModeStr)) {//并行
            		node.setExecuteMode("immediate");
            	}
            	
            	node.setX(new Integer(element.attributeValue("x")));
            	node.setY(new Integer(element.attributeValue("y")));
            	
            	node.setDivId(element.attributeValue("divId")); //div=divID

            	//connect definition with node:
    			node.setDefinition(def);
            	def.getNodes().add(node);
            	
            	//set owner:
            	Element ownerElement = element.element("owner");
            	String tOwnerTypeString = ownerElement.elementText("type");

                if(tOwnerTypeString.equals("role")) {
                	
                	node.setOwnerType(new Short(Node.OWNER_TYPE_ROLE));
                	
                	node.setOwnerId(new Integer(ownerElement.element("id").getText()));
                	node.setOwnerName(ownerElement.element("name").getText());
                	
                	String ownerFloatingStr = ownerElement.elementText("floating");
	                if(ownerFloatingStr != null) {
	                	
	                	if(ownerFloatingStr.equals("true")) {
	                		
	                		node.setFloating(true);
	                	
	                		Element ctlElement = ownerElement.element("control-field");
	                		
	                		if(ctlElement.elementText("type").equals("organization"))
	                			node.setControlFieldType(Node.CONTROL_TYPE_ORGANIZATION);
	                		else
	                			node.setControlFieldType(Node.CONTROL_TYPE_LOCATION);
	                		
	                		node.setControlFieldName(ctlElement.elementText("name"));
	                	
	                	} else {
	                		
	                		node.setFloating(false);
	                	}
	                	
	                }
                	
                } else if(tOwnerTypeString.equals("person")) {
                	
                	node.setOwnerType(new Short(Node.OWNER_TYPE_PERSON));
                	
                	node.setOwnerId(new Integer(ownerElement.element("id").getText()));
                	node.setOwnerName(ownerElement.element("name").getText());
                
                } else {
                	
                	node.setOwnerType(new Short(Node.OWNER_TYPE_INITIATOR));
                }

                //find all tasks attached to the node:
            	for(Iterator taskIt=element.elementIterator("task"); taskIt.hasNext(); ) {
            		Element taskElement = (Element)taskIt.next();
            		
            		Task task = new Task();
            		
            		//attributes:
            		task.setName(taskElement.attributeValue("name"));
            		
            		description = taskElement.attributeValue("description");
            		if((description!= null) && (description.length() > 0))
            			task.setDescription(description);
            		
            		//need audit:
            		String needAudit = taskElement.attributeValue("needAudit");
            		if(needAudit != null)
            			task.setNeedAudit(new Boolean(needAudit));
	           		
            		String type = taskElement.attributeValue("type");
            		if(type.equals("user")) {
            			//user task:
            			task.setType(Task.TYPE_USER);
            		
	            		//actor:
	            		Element actorElement = taskElement.element("actor");
	            		
	            		String tActorTypeString = actorElement.elementText("type");
	                		
	            		if(tActorTypeString.equals("role")) {
	                			
                			task.setActorType(new Short(Task.ACTOR_TYPE_ROLE));
                			
                			task.setActorId(new Integer(actorElement.elementText("id")));
                			task.setActorName(actorElement.elementText("name"));
                			
                			String ownerFloatingStr = actorElement.elementText("floating");
                			if(ownerFloatingStr != null) {
	                	
			                	if(ownerFloatingStr.equals("true")) {
			                		
			                		task.setFloating(true);
			                	
			                		Element ctlElement = actorElement.element("control-field");
			                		
			                		if(ctlElement.elementText("type").equals("organization"))
			                			task.setControlFieldType(Task.CONTROL_TYPE_ORGANIZATION);
			                		else
			                			task.setControlFieldType(Task.CONTROL_TYPE_LOCATION);
			                		
			                		task.setControlFieldName(ctlElement.elementText("name"));
			                	
			                	} else {
			                		
			                		task.setFloating(false);
			                	}
			                	
			                }
		                
	                	} else if(tActorTypeString.equals("person")) {
	                		
                			task.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
                			
                			task.setActorId(new Integer(actorElement.elementText("id")));
                			task.setActorName(actorElement.elementText("name"));

	            		} else {
	            			task.setActorType(new Short(Task.ACTOR_TYPE_INITIATOR));
	            		}
	            		
	            		
	            		
	            		//if(actorElement.element("initiator") != null) {
	            			
	            			//task.setActorType(new Short(Task.ACTOR_TYPE_INITIATOR));
	            			
	            		//} //else if(actorElement.element("roleId") != null) {
	            		
	            			//Element idElement = actorElement.element("roleId");
	            			//task.setActorType(new Short(Task.ACTOR_TYPE_ROLE));
	            			//task.setActorId(new Integer(idElement.getText()));
	            			
	            		//} else {
	            			
	            			//Element idElement = actorElement.element("personId");
	            			//task.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
	            			//task.setActorId(new Integer(idElement.getText()));
	            		//}
	            		
	            		//copy:
	            		Element copyElement = taskElement.element("copy");
	            		if(copyElement != null) {
	            			
	            			if(copyElement.element("actorOfFollowingTasks") != null) {
	            				
	            				TaskCopier taskCopier = new TaskCopier();
	            				taskCopier.setType(new Short(TaskCopier.TYPE_ACTOR_OF_FOLLOWING_TASKS));
	
	            				task.getCopiers().add(taskCopier);
	            			}
	            			
	            			if(copyElement.element("initiator") != null) {
	            				
	            				TaskCopier taskCopier = new TaskCopier();
	            				taskCopier.setType(new Short(TaskCopier.TYPE_INITIATOR));
	
	            				task.getCopiers().add(taskCopier);
	            			}
	            			
	            			for(Iterator copierIt = copyElement.elementIterator("copier"); copierIt.hasNext();) {
	            				Element copierElement = (Element)copierIt.next();
	            				
	            				TaskCopier taskCopier = new TaskCopier();
	            				
	            				if(copierElement.elementText("type").equals("role")) {
	            					taskCopier.setType(new Short(TaskCopier.TYPE_ROLE));
	            				}else {
	            					taskCopier.setType(new Short(TaskCopier.TYPE_PERSON));
	            				}
	            				
	            				taskCopier.setCopierId(new Integer(copierElement.elementText("id")));
	            				taskCopier.setCopierName(copierElement.elementText("name"));
	            				
	            				task.getCopiers().add(taskCopier);
	            			}
	            			
	            			/*
	            			for(Iterator roleIt=copyElement.elementIterator("roleId"); roleIt.hasNext(); ) {
	            				
	    	            		Element roleElement = (Element)roleIt.next();
	    	            		
	    	            		TaskCopier taskCopier = new TaskCopier();
	    	            		taskCopier.setType(new Short(TaskCopier.TYPE_ROLE));
	    	            		taskCopier.setCopierId(new Integer(roleElement.getText()));
	    	            		
	    	            		task.getCopiers().add(taskCopier);
	            			}
	            			
	            			for(Iterator personIt=copyElement.elementIterator("personId"); personIt.hasNext(); ) {
	            				
	    	            		Element personElement = (Element)personIt.next();
	    	            		
	    	            		TaskCopier taskCopier = new TaskCopier();
	    	            		taskCopier.setType(new Short(TaskCopier.TYPE_PERSON));
	    	            		taskCopier.setCopierId(new Integer(personElement.getText()));
	    	            		
	    	            		task.getCopiers().add(taskCopier);
	            			}
	            			*/
	            		}
	            		
	            		Boolean internal = new Boolean(taskElement.elementText("internal"));
	            		task.setInternal(internal);
	            		
	            		if(internal) {
	            		
		            		//form:
		            		Element formElement = taskElement.element("form");
		            		if(formElement != null) {
		            			
		            			task.setForm(formElement.asXML());
		            			
		            		}
		            		
		            		//attachment:
		            		Element attachmentElement = taskElement.element("attachments");
		            		if(attachmentElement != null) {
		            			task.setAttachmentDefinition(attachmentElement.asXML());
		            		}
	            		
	            		} else {
	            			//external link:
	            			task.setIntegrative(new Boolean(taskElement.elementText("integrative")));
	            			task.setUrl(taskElement.elementText("url"));
	            		}
            		
            		} else if(type.equals("auto")) {
            			//auto task:
            			task.setType(Task.TYPE_AUTO);
            			
            			task.setClassName(taskElement.elementText("class"));
            			task.setMethodName(taskElement.elementText("method"));
            			
            		}
            		
            		//set up the connections:
            		task.setDefinition(def);
            		task.setNode(node);
            		node.getTasks().add(task);
            	}
            	
            	//find all transitions leaving the node:
            	for(Iterator transIt=element.elementIterator("transition"); transIt.hasNext();) {
            		Element transElement = (Element)transIt.next();
            		
            		Transition transition = new Transition();
            		transition.setName(transElement.attributeValue("name"));
            		
            		description = transElement.attributeValue("description");
            		if((description!= null) && (description.length() > 0))
            			transition.setDescription(description);
            		
            		transition.setFromX(new Integer(transElement.attributeValue("fromX")));
                	transition.setFromY(new Integer(transElement.attributeValue("fromY")));
                	transition.setToX(new Integer(transElement.attributeValue("toX")));
                	transition.setToY(new Integer(transElement.attributeValue("toY")));
            		
            		//set temporary "to node":
            		Node toNode = new Node();
            		toNode.setDivId(transElement.attributeValue("to"));
            		transition.setToNode(toNode);
            		
            		//set up the connections:
            		transition.setDefinition(def);
            		transition.setFromNode(node);
            		node.getLeavingTransitions().add(transition);
            	}
            	

            } else if(element.getName().equals("approval-node")) {
            	//step-node
            	Node node = new Node();
            	
        		node.setType(new Short(Node.TYPE_APPROVAL));
            	node.setName(element.attributeValue("name"));
            	
            	description = element.attributeValue("description");
            	if((description!= null) && (description.length() > 0))
            		node.setDescription(description);
            	
            	// executeMode,串行为空
            	String executeModeStr = element.attributeValue("executeMode");
            	if("immediate".equals(executeModeStr)) {//并行
            		node.setExecuteMode("immediate");
            	}
            	
            	node.setX(new Integer(element.attributeValue("x")));
            	node.setY(new Integer(element.attributeValue("y")));
            	
            	node.setDivId(element.attributeValue("divId")); //div=divID

            	//connect definition with node:
    			node.setDefinition(def);
            	def.getNodes().add(node);
            	
            	//set owner:
            	Element ownerElement = element.element("owner");
            	String tOwnerTypeString = ownerElement.elementText("type");

                if(tOwnerTypeString.equals("role")) {
                	
                	node.setOwnerType(new Short(Node.OWNER_TYPE_ROLE));
                	
                	node.setOwnerId(new Integer(ownerElement.element("id").getText()));
                	node.setOwnerName(ownerElement.element("name").getText());
                	
                	String ownerFloatingStr = ownerElement.elementText("floating");
	                if(ownerFloatingStr != null) {
	                	
	                	if(ownerFloatingStr.equals("true")) {
	                		
	                		node.setFloating(true);
	                	
	                		Element ctlElement = ownerElement.element("control-field");
	                		
	                		if(ctlElement.elementText("type").equals("organization"))
	                			node.setControlFieldType(Node.CONTROL_TYPE_ORGANIZATION);
	                		else
	                			node.setControlFieldType(Node.CONTROL_TYPE_LOCATION);
	                		
	                		node.setControlFieldName(ctlElement.elementText("name"));
	                	
	                	} else {
	                		
	                		node.setFloating(false);
	                	}
	                	
	                }
                	
                } else if(tOwnerTypeString.equals("person")) {
                	
                	node.setOwnerType(new Short(Node.OWNER_TYPE_PERSON));
                	
                	node.setOwnerId(new Integer(ownerElement.element("id").getText()));
                	node.setOwnerName(ownerElement.element("name").getText());
                
                } else {
                	
                	node.setOwnerType(new Short(Node.OWNER_TYPE_INITIATOR));
                }

            	//find all transitions leaving the node:
            	for(Iterator transIt=element.elementIterator("transition"); transIt.hasNext();) {
            		Element transElement = (Element)transIt.next();
            		
            		Transition transition = new Transition();
            		transition.setName(transElement.attributeValue("name"));
            		
            		description = transElement.attributeValue("description");
            		if((description!= null) && (description.length() > 0))
            			transition.setDescription(description);
            		
            		transition.setFromX(new Integer(transElement.attributeValue("fromX")));
                	transition.setFromY(new Integer(transElement.attributeValue("fromY")));
                	transition.setToX(new Integer(transElement.attributeValue("toX")));
                	transition.setToY(new Integer(transElement.attributeValue("toY")));
            		
            		//set temporary "to node":
            		Node toNode = new Node();
            		toNode.setDivId(transElement.attributeValue("to"));
            		transition.setToNode(toNode);
            		
            		//set up the connections:
            		transition.setDefinition(def);
            		transition.setFromNode(node);
            		node.getLeavingTransitions().add(transition);
            	}
            	

            } else if(element.getName().equals("fork-node")) {

            	Node node = new Node();
            	
        		node.setType(new Short(Node.TYPE_FORK));
            	node.setName(element.attributeValue("name"));
            	node.setDescription(element.attributeValue("description"));
            	
            	node.setX(new Integer(element.attributeValue("x")));
            	node.setY(new Integer(element.attributeValue("y")));
            	
            	node.setDivId(element.attributeValue("divId")); //div=divID

            	//connect definition with node:
    			node.setDefinition(def);
            	def.getNodes().add(node);
            	
            	//find all transitions leaving the node:
            	for(Iterator transIt=element.elementIterator("transition"); transIt.hasNext();) {
            		Element transElement = (Element)transIt.next();
            		
            		Transition transition = new Transition();
            		transition.setName(transElement.attributeValue("name"));
            		
            		description = transElement.attributeValue("description");
            		if((description!= null) && (description.length() > 0))
            			transition.setDescription(description);
            		
            		transition.setFromX(new Integer(transElement.attributeValue("fromX")));
                	transition.setFromY(new Integer(transElement.attributeValue("fromY")));
                	transition.setToX(new Integer(transElement.attributeValue("toX")));
                	transition.setToY(new Integer(transElement.attributeValue("toY")));
            		
            		//set temporary "to node":
            		Node toNode = new Node();
            		toNode.setDivId(transElement.attributeValue("to"));
            		transition.setToNode(toNode);
            		
            		//set up the connections:
            		transition.setDefinition(def);
            		transition.setFromNode(node);
            		node.getLeavingTransitions().add(transition);
            	}
            	
            } else if(element.getName().equals("join-node")) {

            	Node node = new Node();
            	
        		node.setType(new Short(Node.TYPE_JOIN));
            	node.setName(element.attributeValue("name"));
            	
            	description = element.attributeValue("description");
            	if((description!= null) && (description.length() > 0))
            		node.setDescription(description);
            	
            	node.setX(new Integer(element.attributeValue("x")));
            	node.setY(new Integer(element.attributeValue("y")));
            	
            	node.setDivId(element.attributeValue("divId")); //div=divID
            	
        		//get join-type:
        		String joinType = element.elementText("join-type");
        		if(joinType.equals("or")) {
        			
        			node.setMinVote(new Short("1"));
        			
        		} else if(joinType.equals("and")){
        			//temporary set 0:
        			node.setMinVote(new Short("0"));
        			
        		} else {
        			//vote:
        			node.setMinVote(new Short(element.elementText("minVote")));
        			
        		}

            	//connect definition with node:
    			node.setDefinition(def);
            	def.getNodes().add(node);
            	
            	//find all transitions leaving the node:
            	for(Iterator transIt=element.elementIterator("transition"); transIt.hasNext();) {
            		Element transElement = (Element)transIt.next();
            		
            		Transition transition = new Transition();
            		transition.setName(transElement.attributeValue("name"));
            		
            		description = transElement.attributeValue("description");
            		if((description!= null) && (description.length() > 0))
            			transition.setDescription(description);
            		
            		transition.setFromX(new Integer(transElement.attributeValue("fromX")));
                	transition.setFromY(new Integer(transElement.attributeValue("fromY")));
                	transition.setToX(new Integer(transElement.attributeValue("toX")));
                	transition.setToY(new Integer(transElement.attributeValue("toY")));
                	
            		//set temporary "to node":
            		Node toNode = new Node();
            		toNode.setDivId(transElement.attributeValue("to"));
            		transition.setToNode(toNode);
            		
            		//set up the connections:
            		transition.setDefinition(def);
            		transition.setFromNode(node);
            		node.getLeavingTransitions().add(transition);
            	}
            	
            } else {
            	//sub-process
            	
            }
        }
	
		//set correct "to node":
		List<Node> nodeList = def.getNodes();
		
		for(i=0;i<nodeList.size();i++) {
			
			Node node = nodeList.get(i);
			
			//connect transition with node:
			List<Transition> transList = node.getLeavingTransitions();
			for(j=0;j<transList.size();j++) {
				
				Transition transition = transList.get(j);
				//String toNodeName = transition.getToNode().getName();
				String toNodeDiv = transition.getToNode().getDivId();
				
				for(k=0;k<nodeList.size();k++) {
					Node node1 = (Node)nodeList.get(k);
					if(toNodeDiv.equals(node1.getDivId())) {
						
						transition.setToNode(node1);
						node1.getArrivingTransitions().add(transition);
						break;
					}
				}
			}
		}
		
		//set correct "minVote" of join node:
		for(i=0;i<nodeList.size();i++) {
			
			Node node = (Node)nodeList.get(i);
			if(node.getType().shortValue() == Node.TYPE_JOIN) {
					
				if(node.getMinVote().intValue() == 0)
					node.setMinVote(new Short(Integer.toString(node.getArrivingTransitions().size())));
			}
		}
			
		return def;
	}

	public Short getIsProjectCat() {
		return isProjectCat;
	}

	public void setIsProjectCat(Short isProjectCat) {
		this.isProjectCat = isProjectCat;
	}

	public Short getUsable() {
		return usable;
	}

	public void setUsable(Short usable) {
		this.usable = usable;
	}

	public Set<com.telinkus.itsm.data.codes.incident.Category> getPostIncidentCategories() {
		return postIncidentCategories;
	}

	public void setPostIncidentCategories(
			Set<com.telinkus.itsm.data.codes.incident.Category> postIncidentCategories) {
		this.postIncidentCategories = postIncidentCategories;
	}

	public Set<com.telinkus.itsm.data.codes.problem.Category> getPostProblemCategories() {
		return postProblemCategories;
	}

	public void setPostProblemCategories(
			Set<com.telinkus.itsm.data.codes.problem.Category> postProblemCategories) {
		this.postProblemCategories = postProblemCategories;
	}

	public Set<com.telinkus.itsm.data.codes.change.Category> getPostChangeCategories() {
		return postChangeCategories;
	}

	public void setPostChangeCategories(
			Set<com.telinkus.itsm.data.codes.change.Category> postChangeCategories) {
		this.postChangeCategories = postChangeCategories;
	}
	
	//add by lxf begin
	public Set<com.telinkus.itsm.data.codes.reqaccept.Category> getReqacceptCategories() {
		return reqacceptCategories;
	}

	public void setReqacceptCategories(
			Set<com.telinkus.itsm.data.codes.reqaccept.Category> reqacceptCategories) {
		this.reqacceptCategories = reqacceptCategories;
	}
	
	public Set<com.telinkus.itsm.data.codes.reqaccept.Category> getPostReqacceptCategories() {
		return postReqacceptCategories;
	}

	public void setPostReqacceptCategories(
			Set<com.telinkus.itsm.data.codes.reqaccept.Category> postReqacceptCategories) {
		this.postReqacceptCategories = postReqacceptCategories;
	}
	//add by lxf end

	public Set<com.telinkus.itsm.data.areavisit.Area> getAreas() {
		return areas;
	}

	public void setAreas(Set<com.telinkus.itsm.data.areavisit.Area> areas) {
		this.areas = areas;
	}

	public Set<com.telinkus.itsm.data.release2.Category> getRelease2Categories() {
		return release2Categories;
	}

	public void setRelease2Categories(
			Set<com.telinkus.itsm.data.release2.Category> release2Categories) {
		this.release2Categories = release2Categories;
	}

	public Set<com.telinkus.itsm.data.release2.Category> getPostRelease2Categories() {
		return postRelease2Categories;
	}

	public void setPostRelease2Categories(
			Set<com.telinkus.itsm.data.release2.Category> postRelease2Categories) {
		this.postRelease2Categories = postRelease2Categories;
	}

	
}
