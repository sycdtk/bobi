package com.telinkus.itsm.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;

import com.telinkus.itsm.controller.incident.IncidentProcessDefine;
import com.telinkus.itsm.data.BaseDAO;
import com.telinkus.itsm.data.incident.Incident;
import com.telinkus.itsm.data.person.Person;
import com.telinkus.itsm.data.role.Role;
import com.telinkus.itsm.data.role.RoleDAO;
import com.telinkus.itsm.process.def.Node;
import com.telinkus.itsm.process.def.ProcessDefinition;
import com.telinkus.itsm.process.def.Task;
import com.telinkus.itsm.process.def.Transition;
import com.telinkus.itsm.process.exe.NodeInstance;
import com.telinkus.itsm.process.exe.ProcessInstance;
import com.telinkus.itsm.process.exe.TaskInstance;
import com.telinkus.itsm.process.exe.WithdrawComment;

public class ProcessDAO extends BaseDAO {
	
	public ProcessDAO() {
		
	}
	
	private RoleDAO roleDAO;
	
	public RoleDAO getRoleDAO() {
		return roleDAO;
	}


	public void setRoleDAO(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}


	/*
	 * method to find all definition
	 * not include project process
	 */
	public List findAll() {
		
		List definitions;
		
		definitions = hibernateTemplate.find("select new ProcessDefinition(id,name,description,version) from ProcessDefinition where isProjectCat is null order by name, version desc");
		
		return definitions;
		
	}//findAll()
	
	public List findByName(String name) {
		List definitions;
		definitions = hibernateTemplate.find("select new ProcessDefinition(id,name,description,version) from ProcessDefinition where isProjectCat is null and name='" + name + "' order by name, version desc");
		
		return definitions;
	}
	
	/**
	 * 查询非项目流程的流程数据
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public List findAllPage(final int firstResult, final int maxResults) {
		List definitions = null;
		
		return definitions;
	}
	
	/*
	 * method to find all definition count
	 * not include project process
	 */
	public Long findAllCount() {
		String hql = "select count(distinct pd.name) from com.telinkus.itsm.process.def.ProcessDefinition pd where pd.isProjectCat is null ";
		return (Long) hibernateTemplate.find(hql).get(0);
	}
	
	
	/*
	 * method to find defintion by ID
	 */
	public ProcessDefinition findDefinitionById(Integer id) {
		
		return (ProcessDefinition)hibernateTemplate.get(ProcessDefinition.class, id);
		
	}//findDefinitionById()
	
	
	
	/*
     * method to deploy process definition to DB:
     */
    public void saveProcessDefinition(ProcessDefinition processDefinition) {
    	
    	ProcessDefinition oldDef = null;
    	String paraNames[];
		Object values[];
		List defList = null;
		
		//find latest definition with category and name:
		String hql = "select new ProcessDefinition(id,name,version) from ProcessDefinition where name=:name order by version desc";
		
		paraNames = new String[1];
		paraNames[0] = "name";
		
		values = new Object[1];
		values[0] = processDefinition.getName();
		
		defList = hibernateTemplate.findByNamedParam(hql, paraNames, values);
		
		if(defList.size()>0) {
			//increase version:
			oldDef = (ProcessDefinition)defList.get(0);
			short newVersion = (short)(oldDef.getVersion().shortValue() + 1);
			processDefinition.setVersion(new Short(newVersion));
		} else {
			processDefinition.setVersion(new Short("1"));
		}
		
		hibernateTemplate.save(processDefinition);
		
    }//deployProcessDefinition()
    
    
    /*
     * method to update definition:
     */
    public void updateProcessDefinition(ProcessDefinition  processDefinition) {
    	
    	hibernateTemplate.update(processDefinition);
    }
    
	/*
	 * method to find process definition in DB
	 */
	
	public ProcessDefinition findLatestProcessDefinition(String name) {
		
		ProcessDefinition def = null;
		String paraNames[];
		Object values[];
		List defList;
		
		paraNames = new String[1];
		paraNames[0] = "name";
		
		values = new Object[1];
		values[0] = name;
		
		String hql = "from ProcessDefinition where name=:name order by version desc";
		
		defList = hibernateTemplate.findByNamedParam(hql, paraNames, values);
		if(defList.size()>0) {
			def = (ProcessDefinition)defList.get(0);
			
			//load lazy memebers:
			hibernateTemplate.initialize(def.getNodes());
		}
		else
			def = null;
		
		
		return def;
		
	}//findLatestProcessDefinition()

	/*
	 * method to save process instance to DB:
	 */
	public void saveProcessInstance(ProcessInstance processInstance) {

		hibernateTemplate.saveOrUpdate(processInstance);
		hibernateTemplate.flush();
		
	}//saveProcessInstance()
	
	/*
	 * method to find process instance in DB:
	 */
	public ProcessInstance findProcessInstanceById(Long id) 
										throws HibernateException {
		
		ProcessInstance instance = null;
		
		instance = (ProcessInstance)hibernateTemplate.get(ProcessInstance.class, id);
		
		return instance;
		
	}//findProcessInstanceById()
	
	/*
	 * method to find process instance in DB:
	 */

	/* removed, 2008-2-26

	public List findProcessInstanceByName(ProcessDefinition processDefinition) {
		
		List instanceList = null;
		String paraNames[];
		Object values[];
		String hql;
		
		paraNames = new String[2];
		paraNames[0] = "category";
		paraNames[1] = "name";
		
		values = new Object[2];
		values[0] = processDefinition.getCategory();
		values[1] = processDefinition.getName();
		
		hql = "from ProcessInstance where definition.category=:category and definition.name=:name and definition.version=:version";
	
		instanceList = hibernateTemplate.findByNamedParam(hql, paraNames, values);
		
		return instanceList;
	}

 */

	public void delete(Integer id) {
		ProcessDefinition pd = (ProcessDefinition)hibernateTemplate.get(ProcessDefinition.class, id);
		hibernateTemplate.delete(pd);
	}
	
	public void deleteTaskInstanceById(Long id) {
		TaskInstance ti = (TaskInstance)hibernateTemplate.get(TaskInstance.class, id);
		hibernateTemplate.delete(ti);
	}
	

		
	/*
	 * method to get task by ID
	 */
	public Task findTaskById(Integer id) {
		
		return (Task)hibernateTemplate.get(Task.class, id);
	}
	
	/*
	 * method to get task instance by ID
	 */
	public TaskInstance findTaskInstanceById(Long id) {
		
		return (TaskInstance)hibernateTemplate.get(TaskInstance.class, id);
	}
	
	/*
	 * method to get task instance for update
	 */
	public TaskInstance findTaskInstanceByIdForUpdate(Long id) {
		
		return (TaskInstance)hibernateTemplate.get(TaskInstance.class, id, LockMode.UPGRADE_NOWAIT);
		
	}
	/*
	 * method to update taks instance
	 */
	public void updateTaskInstance(TaskInstance taskInstance) {
		hibernateTemplate.update(taskInstance);
		hibernateTemplate.flush();
	}
	
	/*
	 * method to get node instance by ID
	 */
	public NodeInstance findNodeInstanceById(Long id) {
		
		return (NodeInstance)hibernateTemplate.get(NodeInstance.class, id);
	}
	
	/*
	 * method to get task instance for update
	 */
	public NodeInstance findNodeInstanceByIdForUpdate(Long id) {
		
		return (NodeInstance)hibernateTemplate.get(NodeInstance.class, id, LockMode.UPGRADE_NOWAIT);
		
	}
	/*
	 * method to update task instance
	 */
	public void updateNodeInstance(NodeInstance nodeInstance) {
		hibernateTemplate.update(nodeInstance);
		hibernateTemplate.flush();
	}
	
	/*
	 * method to get node instance by ID
	 */
	public Node findNodeById(Integer id) {
		
		return (Node)hibernateTemplate.get(Node.class, id);
	}
	

	/*
	 * method to find transition by id
	 */
	public Transition findTransitionById(Integer id) {
		
		return (Transition)hibernateTemplate.get(Transition.class, id);
	}

	/*
	 * method to save taks instance
	 */
	public void saveTaskInstance(TaskInstance taskInstance) {
		hibernateTemplate.save(taskInstance);
		hibernateTemplate.flush();
	}
	
	/*
	 * method to save task
	 */
	public void saveTask(Task task) {
		hibernateTemplate.save(task);
		hibernateTemplate.flush();
	}
	
	/*
	 * method to save withdraw message to DB:
	 */
	public void saveWithdraw(WithdrawComment wc) {

		hibernateTemplate.saveOrUpdate(wc);
		hibernateTemplate.flush();
		
	}//saveWithdraw()
	
	public List<NodeInstance> getNodeInstanceByExtId(Long externalId, short cat) {
		
		String hql = "select ni from com.telinkus.itsm.process.exe.NodeInstance ni "
			+ " where ni.externalId = " + externalId + " and ni.category = " + cat
			+ " and ni.node.type=" + Node.TYPE_STEP
			+ " order by ni.id ";
		
		List<NodeInstance> niList = hibernateTemplate.find(hql);
		return niList;
		
	}
	
	public List<HashMap<String, Object>> getNodeInstanceMap(List<NodeInstance> niList) {
		
		ArrayList<HashMap<String, Object>> niMapList = new ArrayList<HashMap<String, Object>>();
		
		for(int i = 0; i < niList.size() - 1; i++) {
			
			HashMap<String, Object> tempNiMap = new HashMap<String, Object>();
			ArrayList<HashMap>  tiMapList = new ArrayList<HashMap>();
			
			NodeInstance ni = niList.get(i);
			
			tempNiMap.put("id", ni.getId());
			tempNiMap.put("name", ni.getNode().getName());
			Integer ownerId = ni.getOwnerId();
			if(ownerId != null) {
				Person p = (Person)hibernateTemplate.get(Person.class, ownerId);
				tempNiMap.put("ownerName", p.getName());
			} else {
				
				Node node = ni.getNode();
				tempNiMap.put("ownerName", node.getOwnerName());
			}
			
			List<TaskInstance> tiList = ni.getTaskInstances();
			for(int k = 0; k < tiList.size(); k ++) {
				
				HashMap<String, Object> tiMap = new HashMap<String, Object>();
				TaskInstance ti = tiList.get(k);
				
				if(StringUtils.isBlank(ti.getName())) {
					continue;
				}
				
				tiMap.put("id", ti.getId());
				tiMap.put("name", ti.getName());
				tiMap.put("description", ti.getDescription());
				Person p1 = (Person)hibernateTemplate.get(Person.class, ti.getActorId());
				
				tiMap.put("ownerName", p1.getName());
				tiMap.put("status", ti.getStatus());
				if(ti.getAttachments().size() > 0) {
					tiMap.put("attactmentFlg", true);
				} else {
					tiMap.put("attactmentFlg", false);
				}
				tiMapList.add(tiMap);
				
			}
			tempNiMap.put("tiMapList",tiMapList);
			
			niMapList.add(tempNiMap);
		}
		
		return niMapList;
	}
	
	
	public List<HashMap<String, Object>> getNodeMapByNodeInstanceId(NodeInstance ni) {
		
		List<HashMap<String, Object>> nodeMapList = new ArrayList<HashMap<String, Object>>();
		
		Node node = ni.getNode();
		
		ProcessDefinition pd = node.getDefinition();
		List<Node> nodes = pd.getNodes();
		
		for(int i = 0; i < nodes.size(); i++) {
			
			Node tempNode = nodes.get(i);
			if(!(tempNode.getId() > node.getId())) {
				continue;
			}
			
			if(Node.TYPE_STEP != tempNode.getType().shortValue()) {
				continue;
			}
			
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			ArrayList<HashMap>  taskMapList = new ArrayList<HashMap>();
			
			tempMap.put("id", tempNode.getId());
			
			
			tempMap.put("name", tempNode.getName());
			
			short type = tempNode.getOwnerType().shortValue();
			
			if(type == Node.OWNER_TYPE_ROLE) {
				
//				Role role = (Role)this.hibernateTemplate.get(Role.class, tempNode.getOwnerId());
//				tempMap.put("ownerName", role.getName());
				
				Role ownerRole = roleDAO.findById(tempNode.getOwnerId());
				
				String ownerPsn = "";
						
				List<Person> persons = roleDAO.findPersons(tempNode.getOwnerId());
				for(Person person : persons) {
					if(ownerPsn.equals("")) {
						ownerPsn = person.getName();
					} else {
						ownerPsn = ownerPsn + "," + person.getName();
					}
				}
				
				tempMap.put("ownerName", ownerRole.getName() + "(" + ownerPsn + ")");
				
			} else if (type == Node.OWNER_TYPE_PERSON) {
				
				Person p = (Person)hibernateTemplate.get(Person.class, tempNode.getOwnerId());
				tempMap.put("ownerName", p.getName());
				
			} else if ( type == Node.OWNER_TYPE_INITIATOR) {
				
				Integer pId = ni.getToken().getProcessInstance().getInitiatorId();
				Person p = (Person)hibernateTemplate.get(Person.class, pId);
				tempMap.put("ownerName", p.getName());
			}
			
			List<Task> taskList = tempNode.getTasks();
			for(int k = 0; k < taskList.size(); k ++) {
				
				HashMap<String, Object> taskMap = new HashMap<String, Object>();
				Task task = taskList.get(k);
				taskMap.put("id", task.getId());
				taskMap.put("name", task.getName());
				taskMap.put("description", task.getDescription());
				
				taskMap.put("ownerName", task.getActorName());
				taskMap.put("attactmentFlg", false);
				taskMapList.add(taskMap);
				
			}
			tempMap.put("taskMapList",taskMapList);
			
			nodeMapList.add(tempMap);
			
		}
		
		return nodeMapList;
	}
	
	public HashMap<String, Object> getCurrentNodeMap(NodeInstance ni, Long tiId) {
		
		
		HashMap<String, Object> tempNiMap = new HashMap<String, Object>();
		ArrayList<HashMap>  tiMapList = new ArrayList<HashMap>();
		
		tempNiMap.put("id", ni.getId());
		tempNiMap.put("name", ni.getNode().getName());
		Integer ownerId = ni.getOwnerId();
		
		tempNiMap.put("ownerId", ownerId);
		
		if(ownerId != null) {
			Person p = (Person)hibernateTemplate.get(Person.class, ownerId);
			tempNiMap.put("ownerName", p.getName());
		} else {
			
			Node node = ni.getNode();
			tempNiMap.put("ownerName", node.getOwnerName());
		}
		
		List<TaskInstance> tiList = ni.getTaskInstances();
		
		for(int k = 0; k < tiList.size(); k ++) {
			
			HashMap<String, Object> tiMap = new HashMap<String, Object>();
			TaskInstance ti = tiList.get(k);
			
			if(StringUtils.isBlank(ti.getName())) {
				continue;
			}
			
			if(ti.getType() != TaskInstance.TYPE_NORMAL 
					&& ti.getType() != TaskInstance.TYPE_SUBTASK) {
				continue;
			}
			tiMap.put("id", ti.getId());
			tiMap.put("name", ti.getName());
			tiMap.put("description", ti.getDescription());
			
			
			short actorType = ti.getActorType();
			if(actorType == Task.ACTOR_TYPE_INITIATOR) {
				
				if(ni !=null && ni.getToken() != null && ni.getToken().getProcessInstance() != null 
						&& ni.getToken().getProcessInstance().getInitiatorId() != null) {
					
					Person p1 = (Person)hibernateTemplate.get(Person.class, ni.getToken().getProcessInstance().getInitiatorId());
					tiMap.put("ownerName", p1.getName());
					
				}
				
				
			} else if(actorType == Task.ACTOR_TYPE_ROLE) {
				
//				Role r1 = (Role)hibernateTemplate.get(Role.class, ti.getActorId());
//				tiMap.put("ownerName", r1.getName());
				
				Role ownerRole = roleDAO.findById(ti.getActorId());
				
				String ownerPsn = "";
						
				List<Person> persons = roleDAO.findPersons(ti.getActorId());
				for(Person person : persons) {
					if(ownerPsn.equals("")) {
						ownerPsn = person.getName();
					} else {
						ownerPsn = ownerPsn + "," + person.getName();
					}
				}
				
				tiMap.put("ownerName", ownerRole.getName() + "(" + ownerPsn + ")");
				
				
			} else if(actorType == Task.ACTOR_TYPE_PERSON) {
				
				Person p1 = (Person)hibernateTemplate.get(Person.class, ti.getActorId());
				tiMap.put("ownerName", p1.getName());
			}
			
			
			tiMap.put("status", ti.getStatus());
			if(ti.getAttachments().size() > 0) {
				tiMap.put("attactmentFlg", true);
			} else {
				tiMap.put("attactmentFlg", false);
			}
			tiMapList.add(tiMap);
			
		}
		
		if(NodeInstance.STATUS_ASSIGNED == ni.getStatus()) {
			
			List<Task> taskList = ni.getNode().getTasks();
			for(int j = 0; j < taskList.size(); j ++) {
				
				HashMap<String, Object> taskMap = new HashMap<String, Object>();
				Task task = taskList.get(j);
				taskMap.put("id", task.getId());
				taskMap.put("name", task.getName());
				taskMap.put("description", task.getDescription());
				
				taskMap.put("ownerName", task.getActorName());
				taskMap.put("attactmentFlg", false);
				tiMapList.add(taskMap);
				
			}
		}
	
		
		tempNiMap.put("tiMapList", tiMapList);
		
		return tempNiMap;
	}
	
	public List<HashMap<String, Object>> getNodeInstanceMapByNiList(List<NodeInstance> niList) {
		
		ArrayList<HashMap<String, Object>> niMapList = new ArrayList<HashMap<String, Object>>();
		
		for(int i = 0; i < niList.size(); i++) {
			
			HashMap<String, Object> tempNiMap = new HashMap<String, Object>();
			ArrayList<HashMap>  tiMapList = new ArrayList<HashMap>();
			
			NodeInstance ni = niList.get(i);
			
			tempNiMap.put("id", ni.getId());
			tempNiMap.put("name", ni.getNode().getName());
			Integer ownerId = ni.getOwnerId();
			
			if(ownerId != null) {
				Person p = (Person)hibernateTemplate.get(Person.class, ownerId);
				tempNiMap.put("ownerName", p.getName());
			} else {
				
				Node node = ni.getNode();
				tempNiMap.put("ownerName", node.getOwnerName());
			}
			
			
			List<TaskInstance> tiList = ni.getTaskInstances();
			for(int k = 0; k < tiList.size(); k ++) {
				
				HashMap<String, Object> tiMap = new HashMap<String, Object>();
				TaskInstance ti = tiList.get(k);
				
				if(StringUtils.isBlank(ti.getName())) {
					continue;
				}
				tiMap.put("id", ti.getId());
				tiMap.put("name", ti.getName());
				tiMap.put("description", ti.getDescription());
				


				Short actorType = ti.getActorType();
				if(actorType != null) {
				
					if(actorType == Task.ACTOR_TYPE_INITIATOR) {
					
						if(ni !=null && ni.getToken() != null && ni.getToken().getProcessInstance() != null 
								&& ni.getToken().getProcessInstance().getInitiatorId() != null) {
							
							Person p1 = (Person)hibernateTemplate.get(Person.class, ni.getToken().getProcessInstance().getInitiatorId());
							tiMap.put("ownerName", p1.getName());
							
						}
						
						
					} else if(actorType == Task.ACTOR_TYPE_ROLE) {
						
	//					Role r1 = (Role)hibernateTemplate.get(Role.class, ti.getActorId());
	//					tiMap.put("ownerName", r1.getName());
						
						Role ownerRole = roleDAO.findById(ti.getActorId());
						
						String ownerPsn = "";
								
						List<Person> persons = roleDAO.findPersons(ti.getActorId());
						for(Person person : persons) {
							if(ownerPsn.equals("")) {
								ownerPsn = person.getName();
							} else {
								ownerPsn = ownerPsn + "," + person.getName();
							}
						}
						
						tiMap.put("ownerName", ownerRole.getName() + "(" + ownerPsn + ")");
						
						
					} else if(actorType == Task.ACTOR_TYPE_PERSON) {
						
						Person p1 = (Person)hibernateTemplate.get(Person.class, ti.getActorId());
						tiMap.put("ownerName", p1.getName());
					}
				}
				
				tiMap.put("status", ti.getStatus());
				if(ti.getAttachments().size() > 0) {
					tiMap.put("attactmentFlg", true);
				} else {
					tiMap.put("attactmentFlg", false);
				}
				tiMapList.add(tiMap);
				
			}
			tempNiMap.put("tiMapList",tiMapList);
			
			niMapList.add(tempNiMap);
		}
		
		return niMapList;
	}
	
	public List getPreProcessInfo(Long extId, short catId, Long piId) {
		
		List nodeList = new ArrayList();
		
		if(piId != null) {
			
			String hql = "select ni from com.telinkus.itsm.process.exe.NodeInstance ni "
				+ " where ni.node.type=" + Node.TYPE_STEP
				+ " and ni.token.processInstance.id=" + piId
				+ " order by ni.id ";
			
			List<NodeInstance> niList = hibernateTemplate.find(hql);
			
			nodeList = this.getNodeInstanceMapByNiList(niList);
			
		}
			
		
		return nodeList;
		
	}
	
	//
	public List getPostProcessInfo(Long extId, short catId, List<Long> postPiIds) {
		
		List nodeList = new ArrayList();
		
			
		if(postPiIds.size() > 0) {
			
			for(int i = 0; i < postPiIds.size(); i ++) {
				
				Long piId = postPiIds.get(i);
				
				String hql = "select ni from com.telinkus.itsm.process.exe.NodeInstance ni "
					+ " where ni.node.type=" + Node.TYPE_STEP
					+ " and ni.token.processInstance.id=" + piId
					+ " order by ni.id ";
				
				List<NodeInstance> niList = hibernateTemplate.find(hql);
				
				List nodeMapList = this.getNodeInstanceMapByNiList(niList);
				
				nodeList.add(nodeMapList);
			}
			
		}
		
		return nodeList;
		
	}
	
	//
	public List<TaskInstance> getTaskInstances(Long extId, short catId) {
		
		String paraNames[] = new String[2];
		paraNames[0] = "extId";
		paraNames[1] = "catId";
		
		Object values[] = new Object[2];
		values[0] = extId;
		values[1] = catId;
		
		String hql = "select distinct t from " + TaskInstance.class.getName() + " t where t.category =:catId and t.externalId =:extId";

		List<TaskInstance> taskInstances = this.findByQuery(hql, paraNames, values);
		
		return taskInstances;
		
	}


	/**
	 * 查询任务实例，状态为：待接管、进行中、队列中
	 * @param nodeInstanceId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TaskInstance> findTaskInstance(Long nodeInstanceId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select t from ");
		sql.append(" com.telinkus.itsm.process.exe.NodeInstance s ");
		sql.append(" join s.taskInstances t ");
		sql.append(" where s.id =:nodeInstanceId and (t.type=:taskInstanceType1 or t.type=:taskInstanceType2)" );
		sql.append(" and (t.status=:status1 or t.status=:status2 or t.status=:status3) ");
		sql.append(" order by index(t)");
		List<TaskInstance> result = hibernateTemplate.findByNamedParam(sql.toString(), new String[]{"nodeInstanceId", "taskInstanceType1", "taskInstanceType2", "status1", "status2", "status3"}, 
				new Object[]{nodeInstanceId, new Short(TaskInstance.TYPE_NORMAL), new Short(TaskInstance.TYPE_SUBTASK), new Short(TaskInstance.STATUS_ASSIGNED), new Short(TaskInstance.STATUS_PROCESSING),
					new Short(TaskInstance.STATUS_QUEUED)});
		return result;
	}


	/**
	 * 根据流程定义id删除与事件和优先级的组合的关联关系
	 * @param id
	 */
	public void deleteIncidentProcessDefRel(Integer id) {
		this.getHibernateTemplate().bulkUpdate("delete from com.telinkus.itsm.controller.incident.IncidentProcessDefine where processDefine.id=" + id);
	}
	
	/**
	 * 根据流程定义id删除与事件和优先级的组合的关联关系
	 * @param catId 类别ID
	 * @param defineType 类型
	 */
	public void deleteIncidentProcessDefRel(Short catId, Short defineType) {
		String hql = "delete from com.telinkus.itsm.controller.incident.IncidentProcessDefine where category.id=" + catId;
		hql += " and defineType=" + defineType;
		this.getHibernateTemplate().bulkUpdate(hql);
	}
	
	/**
	 * 根据流程定义id删除与事件和优先级的组合的关联关系
	 * @param catId 类别ID
	 * @param priority 优先级
	 * @param defineType 类型
	 */
	public void deleteIncidentProcessDefRel(Short catId, Short priority, Short defineType) {
		String hql = "delete from com.telinkus.itsm.controller.incident.IncidentProcessDefine where category.id=" + catId;
		hql += " and priority=" + priority + " and defineType=" + defineType;
		this.getHibernateTemplate().bulkUpdate(hql);
	}
	
	/**
	 * 保存事件和优先级的组合的关联关系
	 * @param obj
	 */
	public void saveIncidentProcessDefRel(IncidentProcessDefine obj) {
		this.getHibernateTemplate().save(obj);
	}
	
}
