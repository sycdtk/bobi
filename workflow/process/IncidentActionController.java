package com.telinkus.itsm.controller.incident;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import com.telinkus.itsm.util.PinYinUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContext;
import org.xml.sax.InputSource;

import com.telinkus.itsm.data.assignment.Assignment;
import com.telinkus.itsm.data.assignment.AssignmentDAO;
import com.telinkus.itsm.data.change.Change;
import com.telinkus.itsm.data.change.ChangeDAO;
import com.telinkus.itsm.data.codes.general.CodeTypeDAO;
import com.telinkus.itsm.data.codes.general.TreeTypeDAO;
import com.telinkus.itsm.data.codes.incident.BackgroundColor;
import com.telinkus.itsm.data.codes.incident.BackgroundColorDAO;
import com.telinkus.itsm.data.codes.incident.Category;
import com.telinkus.itsm.data.codes.incident.CategoryDAO;
import com.telinkus.itsm.data.codes.incident.CategoryWithLevel;
import com.telinkus.itsm.data.codes.incident.CategoryWithLevelDAO;
import com.telinkus.itsm.data.codes.incident.CustomSourceType;
import com.telinkus.itsm.data.codes.incident.CustomSourceTypeDao;
import com.telinkus.itsm.data.codes.incident.Effect;
import com.telinkus.itsm.data.codes.incident.EffectDAO;
import com.telinkus.itsm.data.codes.incident.IncidentCause;
import com.telinkus.itsm.data.codes.incident.IncidentCauseDAO;
import com.telinkus.itsm.data.codes.incident.IncidentResolveTimeLimitDAO;
import com.telinkus.itsm.data.codes.incident.IncidentSecondaryCategory;
import com.telinkus.itsm.data.codes.incident.IncidentSecondaryCategoryDAO;
import com.telinkus.itsm.data.codes.incident.ProcessingStatus;
import com.telinkus.itsm.data.codes.incident.ProcessingStatusDAO;
import com.telinkus.itsm.data.codes.incident.Reason;
import com.telinkus.itsm.data.codes.incident.ReasonDAO;
import com.telinkus.itsm.data.codes.incident.Severity;
import com.telinkus.itsm.data.codes.incident.SeverityDAO;
import com.telinkus.itsm.data.codes.incident.Urgency;
import com.telinkus.itsm.data.codes.incident.UrgencyDAO;
import com.telinkus.itsm.data.codes.kb.ReflectChannel;
import com.telinkus.itsm.data.codes.view.ViewInfo;
import com.telinkus.itsm.data.codes.view.ViewInfoDAO;
import com.telinkus.itsm.data.codes.view.ViewKeyValue;
import com.telinkus.itsm.data.configuration.ConfigurationItem;
import com.telinkus.itsm.data.configuration.ConfigurationItemDAO;
import com.telinkus.itsm.data.duty.DutyTaskDao;
import com.telinkus.itsm.data.export.CustomExport;
import com.telinkus.itsm.data.export.CustomExportDao;
import com.telinkus.itsm.data.holiday.AdjustDayDao;
import com.telinkus.itsm.data.holiday.HolidayDao;
import com.telinkus.itsm.data.incident.CustomInfo;
import com.telinkus.itsm.data.incident.Incident;
import com.telinkus.itsm.data.incident.IncidentActivity;
import com.telinkus.itsm.data.incident.IncidentDAO;
import com.telinkus.itsm.data.incident.IncidentEmail;
import com.telinkus.itsm.data.incident.IncidentHastenLog;
import com.telinkus.itsm.data.incident.IncidentTicket;
import com.telinkus.itsm.data.incident.IncidentWorkLog;
import com.telinkus.itsm.data.incident.NoticeRecord;
import com.telinkus.itsm.data.job.JobInstance;
import com.telinkus.itsm.data.job.JobInstanceDAO;
import com.telinkus.itsm.data.job.RequestByJob;
import com.telinkus.itsm.data.job.RequestByJobDAO;
import com.telinkus.itsm.data.kb.Knowledge;
import com.telinkus.itsm.data.kb.KnowledgeActivity;
import com.telinkus.itsm.data.kb.KnowledgeBaseDAO;
import com.telinkus.itsm.data.kb.KnowledgeComment;
import com.telinkus.itsm.data.kb.Solution;
import com.telinkus.itsm.data.library.Directory;
import com.telinkus.itsm.data.library.Document;
import com.telinkus.itsm.data.library.LibraryDAO;
import com.telinkus.itsm.data.location.Location;
import com.telinkus.itsm.data.location.LocationDAO;
import com.telinkus.itsm.data.message.AfterNotification;
import com.telinkus.itsm.data.message.AfterNotificationDAO;
import com.telinkus.itsm.data.message.MailFormat;
import com.telinkus.itsm.data.message.MailServer;
import com.telinkus.itsm.data.message.MessageDAO;
import com.telinkus.itsm.data.message.MessageThreadUtil;
import com.telinkus.itsm.data.message.MessageUtils;
import com.telinkus.itsm.data.message.Mode;
import com.telinkus.itsm.data.message.NotificationJsonDAO;
import com.telinkus.itsm.data.message.NotificationJsonData;
import com.telinkus.itsm.data.message.SmsApi;
import com.telinkus.itsm.data.message.SmsApiDAO;
import com.telinkus.itsm.data.message.SmsFormat;
import com.telinkus.itsm.data.notice.Notice;
import com.telinkus.itsm.data.notice.NoticeDAO;
import com.telinkus.itsm.data.notice.NoticeReceiver;
import com.telinkus.itsm.data.notice.NoticeWorking;
import com.telinkus.itsm.data.notice.NoticeWorkingDAO;
import com.telinkus.itsm.data.organization.Organization;
import com.telinkus.itsm.data.organization.OrganizationDAO;
import com.telinkus.itsm.data.permission.PermissionItem;
import com.telinkus.itsm.data.person.Person;
import com.telinkus.itsm.data.person.PersonDAO;
import com.telinkus.itsm.data.plan.PlanUtil;
import com.telinkus.itsm.data.problem.Problem;
import com.telinkus.itsm.data.problem.ProblemDAO;
import com.telinkus.itsm.data.requestno.RequestNoGeneration;
import com.telinkus.itsm.data.requestno.RequestNoGenerationDao;
import com.telinkus.itsm.data.requirement.Requirement;
import com.telinkus.itsm.data.requirement.RequirementDAO;
import com.telinkus.itsm.data.role.Role;
import com.telinkus.itsm.data.role.RoleDAO;
import com.telinkus.itsm.data.servicedesk.CallRecord;
import com.telinkus.itsm.data.servicedesk.CallRecordDao;
import com.telinkus.itsm.data.servicedesk.Customer;
import com.telinkus.itsm.data.servicedesk.CustomerDao;
import com.telinkus.itsm.data.sla.Agreement;
import com.telinkus.itsm.data.sla.AgreementDAO;
import com.telinkus.itsm.data.sla.Target;
import com.telinkus.itsm.data.utils.ThreadNo;
import com.telinkus.itsm.data.utils.ThreadNoDAO;
import com.telinkus.itsm.data.visitapplication.VisitApplication;
import com.telinkus.itsm.data.workgroup.LocationDomain;
import com.telinkus.itsm.data.workgroup.Workgroup;
import com.telinkus.itsm.data.workgroup.WorkgroupDAO;
import com.telinkus.itsm.process.ProcessDAO;
import com.telinkus.itsm.process.ProcessException;
import com.telinkus.itsm.process.def.Node;
import com.telinkus.itsm.process.def.ProcessDefinition;
import com.telinkus.itsm.process.def.Task;
import com.telinkus.itsm.process.def.Transition;
import com.telinkus.itsm.process.exe.Comment;
import com.telinkus.itsm.process.exe.NodeInstance;
import com.telinkus.itsm.process.exe.ProcessInstance;
import com.telinkus.itsm.process.exe.TaskInstance;
import com.telinkus.itsm.process.exe.Token;
import com.telinkus.itsm.util.AutocompleteUtils;
import com.telinkus.itsm.util.DateUtils;
import com.telinkus.itsm.util.EntityUtils;
import com.telinkus.itsm.util.HolidayCalc;
import com.telinkus.itsm.util.ItsmConstants;
import com.telinkus.itsm.util.PinYinUtils;
import com.telinkus.itsm.util.Utils;
import com.telinkus.itsm.util.excel.ExportUtils;

import com.telinkus.itsm.util.levelcount.incident.CountIncidentLevel;
import freemarker.ext.dom.NodeModel;

/**
 * Incident Action Controller
 * 
 * @author xingshy
 *
 */
public class IncidentActionController extends MultiActionController{
	
	private Resource resource;
	private static Properties configProperties = null;
	
	static private Logger logger = Logger.getLogger(IncidentActionController.class);
	
	public static final short LEVEL_1 = 1;
	public static final short LEVEL_2 = 2;
	public static final short LEVEL_3 = 3;
	public static final short LEVEL_4 = 4;
	public static final short LEVEL_5 = 5;
	
	private static int threadNo = 1; // 工单号生成标识
	private static String tagStr = "";
	
	private String newFormView;
	private String newTicketFormView;
	private String searchFormView;
	private String detailView;
	private String detailTicketView;
	private String editView;
	private String editAgainView;
	private String abandonView;
	private String listView;
	private String listConcernView;
	private String listMyDraftView;
	private String listViewPage;
	private String listCatLevelView;
	private String listOverTimeNotClose;
	private String editTaskView;
	private String editStepView;
	private String successView;
	private String errorView;
	private String handleView;
	private String handleTicketView;
	private String newProblemView;
	private String interveneView;
	private String activityView;
	private String chooseFormView;
	private String multiChooseFormView;
	private String chooseView;
	private String multiChooseView;
	private String chooseReferFormView;
	private String chooseReferView;
	private String chooseViewInfoView;
	private String showDataView;
	private String batchMergeView;
	private String batchRejectView;
	private String countFormView;
	private String countView;
	private String applicationHistoryView;
	private String toCheckPageView;
	private String toUpdateIncidentInfoPageView;
	private String manageIncidentView;
	private String sendEmailFormView;
	private String callBackEmailFormView;

	private AdjustDayDao adjustDayDao; // add by xingshy
	private AgreementDAO agreementDAO;
	private AssignmentDAO assignmentDAO; // add by xingshy
	private AfterNotificationDAO afterNotificationDAO;
	private BackgroundColorDAO backgroundColorDao;
	private CallRecordDao callRecordDao;
	private CategoryDAO catDAO;
	private ChangeDAO changeDAO;
	private CodeTypeDAO codeTypeDAO;
	private ConfigurationItemDAO ciDAO;
	private CustomerDao customerDao;
	private CustomExportDao customExportDao;
	private CustomSourceTypeDao customSourceTypeDao;
	private DutyTaskDao dutyTaskDao;
	private EffectDAO effectDAO;
	private HolidayDao holidayDao; // add by xingshy
	private IncidentDAO incidentDAO;
	private IncidentCauseDAO causeDAO; // 事件原因
	private IncidentSecondaryCategoryDAO secondaryCatDAO; // 事件分类（一）
	private IncidentResolveTimeLimitDAO incidentResolveTimeLimitDAO; // add by xingshy
	private JobInstanceDAO jobInstanceDAO;
	private KnowledgeBaseDAO kbDAO;
	private LibraryDAO libraryDAO;
	private LocationDAO locationDAO;
	private MessageDAO messageDAO;
	private NoticeDAO noticeDAO;
	private NotificationJsonDAO notificationJsonDAO;
	private OrganizationDAO organizationDAO;
	private PersonDAO personDAO;
	private ProcessDAO processDAO;
	private ProcessingStatusDAO processingStatusDAO;
	private ReasonDAO reasonDAO;
	private RequestNoGenerationDao requestNoGenerationDao;
	private RequestByJobDAO requestByJobDAO;
	private RequirementDAO requirementDAO;
	private RoleDAO roleDAO;
	private SeverityDAO severityDAO;
	private SmsApiDAO smsApiDAO;
	private TreeTypeDAO treeTypeDAO;
	private UrgencyDAO urgencyDAO;
	private ViewInfoDAO infoDAO;
	private WorkgroupDAO workgroupDao;
	private CategoryWithLevelDAO catWithLevelDAO;
	private static ThreadNoDAO threadNoDAO;
	private ProblemDAO problemDAO;
	private NoticeWorkingDAO noticeWorkingDAO;
	
	private static Target targetUtils = new Target(); // add 
	
	private String CUSTOMER_TABLE_FORMAT_ROOT ="table";
	private static final  String CUSTOMER_TABLE_FORMAT_FIELD ="field";
	private static final String CUSTOMER_TABLE_FORMAT_FIELD_LABEL_KEY ="label";
	private static final String CUSTOMER_TABLE_FORMAT_FIELD_COMPONET_KEY ="componet";
	private static final String CUSTOMER_TABLE_FORMAT_FIELD_LABEL_FIRSTLABELVALUE ="firstLabelValue";	
	private static final String CUSTOMER_TABLE_FORMAT_FIELD_LABEL_LASTLABELVALUE ="lastLabelValue";
	private static final String CUSTOMER_TABLE_FORMAT_TD ="td";
	private static final String CUSTOMER_TABLE_FORMAT_STYLENAME ="styleName";
	private static final String CUSTOMER_TABLE_FORMAT_TABLETITLE_KEY ="tableTitle";
	private static final String CUSTOMER_TABLE_FORMAT_COLUMNTITLE_KEY ="columnTitle";
	private static final String CUSTOMER_TABLE_FORMAT_ROWTITLE_KEY ="rowTitle";
	private static final String CUSTOMER_TABLE_FORMAT_TABLEVALUE_KEY ="tableValue";
	private static final String CUSTOMER_TABLE_FORMAT_COLUMNVALUE_KEY ="columnValue";
	private static final String CUSTOMER_TABLE_FORMAT_ROWVALUE_KEY ="rowValue";
	
	/**
	 * 在界面上应该用到的freemarker宏字符串
	 */
	public static final String CUSTOMER_TABLE_FORMAT_FTL_GEN_STRING = "<#if (status == Incident_STATUS_CHECKING || status == Incident_STATUS_PROCESSING || status == Incident_STATUS_DRAFT || status == Incident_STATUS_ASSESSING) && (readonly!'0') != '1'>"+
	"	<#list form.field as field>"+
	"		<#if field.name=='%field'>"+
	"			<#if status == Incident_STATUS_PROCESSING>"+
	"				<@gen_edit_field fl=field catId=categoryId customInfo=customInfo/>"+
	"			<#elseif customInfo?has_content>"+
	"               <@gen_edit_field fl=field catId=categoryId customInfo=customInfo event='create'/>"+
	"			<#else>"+
	"				<@gen_new_field fl=field catId=categoryId/>"+
	"			</#if>"+
	"		</#if>"+
	"	</#list>"+
	"<#else>"+
	"	<#list form.field as fl>"+
	"		<#if fl.name=='%field'>"+
	"			<@showCustomInfoValue fl=fl customInfo=customInfo/>"+
	"		</#if>"+
	"	</#list>"+
	"</#if>";
	
	public static final String CUSTOMER_TABLE_FORMAT_FTL_GEN_STRING_SPLIT = "%field";

	public IncidentActionController() {

	}
	
	public ModelAndView manageIncident(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		HttpSession session = req.getSession();
		Integer personId = (Integer)session.getAttribute("personId");
		Person person = personDAO.findById(personId);
		Integer locationId = person.getLocation().getId();
		Integer organizationId = person.getOrganization().getId();
		
		Integer categoryId = null ;
		Category category = null;
		
		String categoryIdStr = req.getParameter("categoryId");
		
		if (org.apache.commons.lang.StringUtils.isNotBlank(categoryIdStr)
				&& org.apache.commons.lang.StringUtils.isNumeric(categoryIdStr)) {
			categoryId = Integer.valueOf(categoryIdStr);
			category = catDAO.findById(Short.valueOf(categoryIdStr));
			map.put("categoryIdURL", "categoryId=" + categoryId);
		}
		
		List<Role> roleList = new ArrayList<Role>();
		List<Integer> roleIdList = new ArrayList<Integer>();
		
		String roleIds = (String)req.getSession().getAttribute("roleId");
		for(String roleId : roleIds.split("#")){
			roleIdList.add(Integer.valueOf(roleId));
			roleList.add(roleDAO.findById(Integer.valueOf(roleId)));
		}
		
		String range = req.getParameter("default");
		
		map.put("New_Incident", Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_NEW));
		map.put("My_Draft", Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_NEW));
		map.put("My_Submission", Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_NEW));
		
		map.put("Assess_Process_My_Running_Step",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_ASSESS_HANDLE));
		map.put("Assess_Process_My_Open_Task",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_ASSESS_HANDLE));
		map.put("Assess_Process_My_Audit_Task",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_ASSESS_HANDLE));
		
		map.put("my_order",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_HANDLE));
		map.put("Search_incident",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_QUERY));
		map.put("Statistic_chart",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_QUERY));
		//map.put("view_info",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_QUERY)); 原来的视图权限
		map.put("view_info",Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId, PermissionItem.OPERATION_NULL));
		map.put("assign_rule", Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId,PermissionItem.OPERATION_ASSIGN_RULE));
		
		List<Incident> templates = this.incidentDAO.findAllTemplate();
		ArrayList incidentList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < templates.size(); i++) {

			Map<String, Object> incidentMap = this.getIncidentMap(templates.get(i));
			incidentList.add(incidentMap);
		}

		map.put("incidentList", incidentList);
		map.put("assign_rule", Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, categoryId,PermissionItem.OPERATION_ASSIGN_RULE));
		map.put("default", range);
		
		long countMyWork = FindWorkController.countMyWorkToTake(req, incidentDAO, category, personId, roleIds.replace("#", ","));
		if(countMyWork > 0){
			map.put("countMyWork", countMyWork);
		}
		long countMyTicketWork = FindTicketWorkController.countMyWorkByTypeToTake(incidentDAO, personId, range);
		if(countMyTicketWork > 0){
			map.put("countMyTicketWork", countMyTicketWork);
		}
		long countOpenTask = countOpenTask(req, processDAO, category, personId, organizationId, locationId, roleIdList);
		if(countOpenTask > 0){
			map.put("countOpenTask", countOpenTask);
		}
		
//		List<HashMap<String, Object>> catList = catDAO.findAllForReport();
//		map.put("catList", catList);
		
		return new ModelAndView(this.getManageIncidentView(),map);
	}
	
	/*
	 * method to count open task
	 */
	private long countOpenTask(HttpServletRequest req, ProcessDAO processDAO, Category category,
										Integer personId, Integer organizationId,
										Integer locationId, List<Integer> roleIdList) {
		
		String paraNames[];
		Object values[];
		String hql = "";
		long rowCount;
		String criteriaStr = null;
		
		
		//find task instance with personId or role ID:
		paraNames = new String[6 + roleIdList.size()];
		paraNames[0] = "category";
		paraNames[1] = "status2";
		paraNames[2] = "locationId";
		paraNames[3] = "organizationId";
		paraNames[4] = "actorType2";

		values = new Object[6 + roleIdList.size()];
		values[0] = new Short(ProcessInstance.CATEGORY_INCIDENT);
		values[1] = new Short(TaskInstance.STATUS_ASSIGNED);
		values[2] = locationId;
		values[3] = organizationId;
		values[4] = new Short(Task.ACTOR_TYPE_ROLE);
		
		for(int i=0;i<roleIdList.size();i++) {
			paraNames[i+5] = "roleId" + i;
			values[i+5] = roleIdList.get(i);
			
			if(i==0) {
				criteriaStr = "ti.category=:category and " + 
					"((((ti.organizationId is null and ti.locationId is null) or ti.locationId=:locationId or ti.organizationId=:organizationId) and " +
						"ti.status=:status2 and ti.actorType=:actorType2 and (ti.actorId =:roleId" + i;
			} else {
				criteriaStr += " or ti.actorId = :roleId" + i;
			}
				
			if(i==roleIdList.size() -1) {
				criteriaStr += ")))";
			}
		}
		
		String range = req.getParameter("default");
		List<Integer> reservedInteger6 = new ArrayList<Integer>();
		if("incident".equals(range)){
			reservedInteger6.add(Incident.TYPE_INCIDENT_FORM);
		}else if("query".equals(range)){
			reservedInteger6.add(Incident.TYPE_QUERY_FORM);
		}else if("all".equals(range)){
			reservedInteger6.add(Incident.TYPE_INCIDENT_FORM);
			reservedInteger6.add(Incident.TYPE_QUERY_FORM);
		}
		paraNames[5 + roleIdList.size()] = "reservedInteger6";
		values[5 + roleIdList.size()] = reservedInteger6.toArray(new Integer[0]);
		if(category == null) {
		
			/*
			 hql = "select count(*) from com.telinkus.itsm.process.exe.TaskInstance ti, com.telinkus.itsm.data.incident.Incident incident"
					+ "  where " + criteriaStr
					+ " and ti.externalId=incident.id";
			*/
			hql = "select count(*) from com.telinkus.itsm.process.exe.TaskInstance ti, com.telinkus.itsm.data.incident.Incident incident"
					+ " where " + criteriaStr
					+ " and ti.externalId=incident.id and incident.reservedInteger6 in (:reservedInteger6)";
		} else {
			
			hql = "select count(*) from com.telinkus.itsm.process.exe.TaskInstance ti, com.telinkus.itsm.data.incident.Incident incident"
					+ "  where " + criteriaStr
					+ " and ti.externalId=incident.id"
					+ " and incident.category.code like '" + category.getCode() + "%' and incident.reservedInteger6 in (:reservedInteger6)";
		}
		String subject = req.getParameter("subject");
		if(subject != null && subject.length() > 0){
			hql += " and incident.subject like '%" + subject + "%' ";
		}
		rowCount = ((Long)processDAO.findByQuery(hql, paraNames, values, 0, 1).get(0)).longValue();		
		
		return rowCount;
		
	}//countOpentTask()
	
	
	public ModelAndView addTicket(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap;
		String token[];
		Incident incident; 
		IncidentTicket ticket;
		
		HttpSession session = req.getSession();
		Integer personId = (Integer)session.getAttribute("personId");
		Person creator = personDAO.findById(personId);
		
		// get parameters from http request:
		String ticketIdStr = req.getParameter("id");
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		String opinion = req.getParameter("opinion");
		String incidentIdStr = req.getParameter("incidentId");
		String handlerIdListStr = req.getParameter("handlerPersonId");
		
		ticket = new IncidentTicket();

		ticket.setCreateTime(new Timestamp(System.currentTimeMillis()));
		ticket.setCreator(creator);
		ticket.setSubject(subject);
		ticket.setContent(content);
		ticket.setReservedString2(opinion);
		
		//set status to assigned:
		ticket.setStatus(new Short(IncidentTicket.STATUS_ASSIGNED));
		
		if(handlerIdListStr.length() > 0) {
			
			token = handlerIdListStr.split(",");
			for(int i=0;i<token.length;i++) {
				
				Person handler = personDAO.findById(new Integer(token[i]));
				ticket.getHandlers().add(handler);
				
			}
		}
	
		incident = incidentDAO.findById(new Long(incidentIdStr));
		ticket.setIncident(incident);
	    incident.getTickets().add(ticket);
	    
	    //子工单处理人加入事件工单参与人
	    incident.getParticipators().addAll(ticket.getHandlers());
		
	    incidentDAO.insertTicket(ticket);
	    incidentDAO.update(incident);

	    //set map for return:
		paraMap = new HashMap<String, Object>();
		paraMap.put("windowLocation", "incident_action.do?operation=detailTicket&id=" + ticket.getId());

		//if(ticketIdStr != null) {
			//draft -> create, reload parent frame
			paraMap.put("parentReload", true);
		//}
			
		//增加“创建子工单”动作的消息通知
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_CREATE_TICKET, incident, req, ticket);
		} catch (Exception e) {
			logger.error("finish menthod sendNotice error : "+Utils.getStackTrace(e));
		}

		return new ModelAndView(getSuccessView(), paraMap);
		
	}//addTicket()
	
	

	public ModelAndView detailTicket(HttpServletRequest req, HttpServletResponse res) {
	
		HashMap<String,Object> paraMap;
		boolean canDeassign = false;
		
		//get parameters from http request:
		Long ticketId = new Long(req.getParameter("id"));
		
		IncidentTicket ticket = incidentDAO.findTicketById(ticketId);
		
		HttpSession session = req.getSession();
		Person person = getCurrentPerson(session);
		
		paraMap = new HashMap<String,Object>();//this.getMap(ticket);
		getIncidentSimpleDataMap(ticket.getIncident(), paraMap);
		
		paraMap.put("createTime", ticket.getCreateTime());
		paraMap.put("startTime", ticket.getStartTime());
		paraMap.put("finishTime", ticket.getFinishTime());
		paraMap.put("status", ticket.getStatus());

		paraMap.put("subject", ticket.getSubject());
		paraMap.put("content", ticket.getContent());
		paraMap.put("opinion", ticket.getReservedString2());
		paraMap.put("solution", ticket.getReservedString1());
		paraMap.put("remark", ticket.getRemark());
		paraMap.put("id", ticketId);
		
		Set<Person> handlers = ticket.getHandlers();
		Iterator<Person> iter = handlers.iterator();
		String handlerList = "";
		while(iter.hasNext()){
			Person handler = iter.next();
			handlerList += "," + handler.getName();
		}
		handlerList = handlerList.replaceFirst(",", "");
		
		paraMap.put("handlers", handlerList);
		
		if(ticket.getStatus() == IncidentTicket.STATUS_ASSIGNED || ticket.getStatus() == IncidentTicket.STATUS_PROCESSING){
			if(ticket.getIncident().getHandlers().contains(person)){
				canDeassign = true;
			}
		}	
		
		paraMap.put("canDeassign", canDeassign);
		paraMap.put("roleList", 
				roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));
		
		return new ModelAndView(getDetailTicketView(), paraMap);
		
	}//detailTicket()


	public ModelAndView newTicketForm(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		/*
		String categoryId = req.getParameter("categoryId");
		
		if(categoryId!=null&&!"".equals(categoryId)){
			paraMap.put("categoryIdURL", "categoryId="+categoryId);
		}
		
		paraMap.put("now", new Timestamp(System.currentTimeMillis()));
		*/
		//paraMap.put("roleList", roleDAO.findAll());
		
		String incidentId = req.getParameter("incidentId");
		Incident incident = incidentDAO.findById(Long.parseLong(incidentId));
		paraMap = this.getMap(incident);
		
		paraMap.put("incidentId", incidentId);
		
		paraMap.put("roleList", 
					roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));
		
		//paraMap.put("applicantPersonId", req.getParameter("applicantPersonId"));
		//String applicantPersonName = req.getParameter("applicantPersonName");
		//if(applicantPersonName != null)
		//	paraMap.put("applicantPersonName", new String(applicantPersonName.getBytes("ISO-8859-1"), "UTF-8"));

		
		return new ModelAndView(this.getNewTicketFormView(), paraMap);
		
	}//newTicket()
	
	/*
	 * method to show form of new incident
	 */
	public ModelAndView newForm(HttpServletRequest req, HttpServletResponse res) {
		
		SimpleDateFormat datetimeFormat = new SimpleDateFormat(ItsmConstants.DATETIME_FORMAT);
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		String categoryId = req.getParameter("categoryId");
		
		if(categoryId!=null&&!"".equals(categoryId)){
			paraMap.put("categoryIdURL", "categoryId="+categoryId);
		}
		String jobInstanceId = req.getParameter("jobInstanceId");
		if(EntityUtils.isNotEmpty(jobInstanceId)) {
			JobInstance jobInstance = this.jobInstanceDAO.get(Long.valueOf(jobInstanceId));
			paraMap.put("jobInstanceId", jobInstanceId);
			paraMap.put("jobInstanceName", jobInstance.getName());
			paraMap.put("jobInstanceExecutionNote", jobInstance.getExecutionNote());
			paraMap.put("jobInstanceDescription", jobInstance.getDescription());
			paraMap.put("jobInstanceBegintime", datetimeFormat.format(jobInstance.getBegintime().getTime()));
		}

		String callRecordId = req.getParameter("callRecordId");
		if(StringUtils.isNotBlank(callRecordId)){
		
			CallRecord callRecord = callRecordDao.findCallRecordById(Long.valueOf(callRecordId));
			paraMap.put("callRecordId", callRecord.getId());
			paraMap.put("subject", callRecord.getSubject());
			paraMap.put("content", callRecord.getContent());
			paraMap.put("callRecordStateType", callRecord.getStateType());
			
			Customer customer = callRecord.getCustomer();
			if(customer != null) {
				paraMap.put("customerId", customer.getId());
				paraMap.put("customerName", customer.getName());
			}
		}

//		List<HashMap<String, Object>> catList = catDAO.findAllForReport();
		List<HashMap<String, Object>> catList = catDAO.findAllForReportOptimize();
		
		paraMap.put("now", new Timestamp(System.currentTimeMillis()));
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		paraMap.put("customSourceTypeList", customSourceTypeDao.findAll());
		paraMap.put("catList", catList);
		
		//关联系统
		if(configProperties == null) {
			configProperties = new Properties();
			try {
				configProperties.load(new FileInputStream(resource.getFile()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//影响系统
		/*String influenceSystemParentID = configProperties.getProperty("incident_influence_system_parent_id");
		List<HashMap<String, Object>> influenceSystemList = catDAO.findInfluenceSystem(influenceSystemParentID);
		paraMap.put("influenceSystemList", influenceSystemList);*/
		String influenceSystemParentID = configProperties.getProperty("incident_ci_parent_id");
		List<HashMap<String, Object>> influenceSystemList = ciDAO.findInfluenceSystem(influenceSystemParentID);
		paraMap.put("influenceSystemList", influenceSystemList);
		paraMap.put("catType", 2);

		//paraMap.put("roleList", roleDAO.findAll());
		
		paraMap.put("roleList", roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));
		
		return new ModelAndView(this.getNewFormView(), paraMap);
		
	}//newForm()
	
	
	/*
	 * method to show form of search incident
	 */
	public ModelAndView searchForm(HttpServletRequest req, HttpServletResponse res) throws FileNotFoundException, IOException {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		int pageSize;
		String pageSizeStr;
		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		pageSizeStr = req.getParameter("pageSize");
		if (pageSizeStr != null) {
			pageSize = Integer.parseInt(pageSizeStr);
		} else {
			pageSize = Integer.parseInt(configProperties.getProperty("pageSize"));
		}
		
		String categoryId = req.getParameter("categoryId");
		
		//if category ID was passed, pass it on
		if(StringUtils.isNotEmpty(categoryId)){
			paraMap.put("categoryIdURL", "categoryId="+ new Integer(categoryId));
		}
		
		String range = req.getParameter("default");
		paraMap.put("default", range);
		
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		paraMap.put("customSourceTypeList", customSourceTypeDao.findAll());
		paraMap.put("pageSizeNum", pageSize);
		
//		List<HashMap<String, Object>> catList = catDAO.findAllForReport();
		List<HashMap<String, Object>> catList = catDAO.findAllForReportOptimize();
		paraMap.put("catList", catList);
		
		return new ModelAndView(this.getSearchFormView(), paraMap);
	
	}//searchForm()
	
	/*
	 * method to add an Incident
	 * 
	 * 
	 */
	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		ProcessInstance processInstance = null;
		ProcessDefinition processDefinition = null;
		String token[];
		ConfigurationItem ci;
		String solution;
		Incident incident;
		Person applicant = null;
		Customer customer = null;
		boolean isworking = true;
		
		//get parameters from http request:
		String incidentIdStr = req.getParameter("id");
		
		String applicantPersonIdStr = req.getParameter("applicantPersonId");
		if(StringUtils.isNotBlank(applicantPersonIdStr)) {
			Integer applPsnId = new Integer(req.getParameter("applicantPersonId"));
			applicant = personDAO.findById(applPsnId);
		}
		
		String customerId = req.getParameter("customerId");
		if(StringUtils.isNotBlank(customerId)){
			customer = customerDao.findCustomerById(Integer.valueOf(customerId));
		}
		
		String callRecordId = req.getParameter("callRecordId");
		
		Timestamp applicationTime = Timestamp.valueOf(req.getParameter("applicationTime"));
		
		Integer creatorPsnId = new Integer(req.getParameter("creatorPersonId"));
		Person creator = personDAO.findById(creatorPsnId);
		
		Short sourceType = new Short(req.getParameter("sourceType"));
		String otherSourceType = req.getParameter("otherSourceType");
		String customSourceTypeId = req.getParameter("customSourceTypeId");
		
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		
		Short severityId = new Short(req.getParameter("severityId"));
		Severity severity = severityDAO.findById(severityId);
		
		Effect effect = null;
		String effectIdStr = req.getParameter("effectId");
		if (StringUtils.isNotBlank(effectIdStr)) {
			Short effectId = new Short(effectIdStr);
			effect = effectDAO.findById(effectId);
		}else{
			effect = effectDAO.find();
		}
		
		Urgency urgency = null;
		String urgencyIdStr = req.getParameter("urgencyId");
		if (StringUtils.isNotBlank(urgencyIdStr)) {
			Short urgencyId = new Short(urgencyIdStr);
			urgency = urgencyDAO.findById(urgencyId);
		}else{
			urgency = urgencyDAO.find();
		}
		
		Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		
		Boolean sendMessage = new Boolean(req.getParameter("sendMessage"));
		
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		String ciIdListStr = req.getParameter("ciIdList");
		//String fileIndexList = req.getParameter("fileIndexList");
		
		//SLA id:
		String agreementIdStr = req.getParameter("agreementId");
		//获取复核角色信息
		String checkRoleIdStr = req.getParameter("checkRoleId");
		String template = req.getParameter("template");
		
		//create incident:
		if(StringUtils.isBlank(incidentIdStr)) {
			incident = new Incident();
		} else if(StringUtils.isNotBlank(template)){
			incident = new Incident();
		}else{
			incident = incidentDAO.findById(new Long(incidentIdStr));
		}
		
		// set setReservedField s : private method
		this.setReservedField(incident, req);
		
		Role checkRole = null;
		if(null != checkRoleIdStr && !"".equals(checkRoleIdStr)) {
			checkRole = roleDAO.findById(new Integer(checkRoleIdStr));
		}
		incident.setCheckRole(checkRole);
		
		// 处理人
		String handlerIdListStr = req.getParameter("handlerPersonId");
		
		if(StringUtils.isNotBlank(handlerIdListStr)){
			this.setHandlers(incident, handlerIdListStr);
		}else {
			List<Assignment> assignments = this.assignmentDAO.findByIncidentCatId(categoryId);
			for (Assignment assignment : assignments) {
				if (assignment.getAssignType().equals(Assignment.ASSIGN_TYPE_CREATOR)) {
					incident.getHandlers().add(creator);
					break;
				}
			}
		}
		
		// 参与人
		String participatorPersonId = req.getParameter("participatorPersonId");
        
        String assignType = req.getParameter("assignType");
        
        if(assignType.equals("choose")){
        	//手动分配
    		if(StringUtils.isNotBlank(participatorPersonId)) {
    			String[] participatorPersonIds = participatorPersonId.split(",");
    			for(String ppId : participatorPersonIds) {
    				Person participator = personDAO.findById(new Integer(ppId));
    				incident.getParticipators().add(participator);
    			}
    		}
        	
    		// 新处理人的领导添加到参与人中
        	String[] handlerIds = handlerIdListStr.split(",");
			for(String handlerId : handlerIds) {
				incident.getParticipators().addAll(this.roleDAO.findLeadersByMemberId(new Integer(handlerId)));
			}
        }else{
        	//自动分配
        	this.setParticipators(incident, categoryId, participatorPersonId);
        }
        //事件类型的工单，创建人也加入参与人
        int type = Integer.valueOf(req.getParameter("reservedInteger6"));
        if(type==Incident.TYPE_INCIDENT_FORM){
        	incident.getParticipators().add(creator);
        }
        
        if(applicant != null) {
        	
        	incident.setApplicant(applicant);
        	incident.setLocation(applicant.getLocation());
        	
		} else if(customer != null) {
			
        	incident.setCustomer(customer);
        	//use location of current person:
        	Integer locationId = (Integer)req.getSession().getAttribute("locationId");
        	incident.setLocation(locationDAO.findById(locationId));
        }
        
        if(StringUtils.isNotBlank(callRecordId)){
		
        	CallRecord callRecord = callRecordDao.findCallRecordById(Long.valueOf(callRecordId));
        	//add by gushigao
        	callRecord.setSubject(subject);
        	callRecord.setContent(content);
        	incident.getCallRecords().add(callRecord);
        }
	
		incident.setApplicationTime(applicationTime);
		incident.setSourceType(sourceType);
		incident.setCreator(creator);
		incident.setCreateTime(new Timestamp(System.currentTimeMillis()));
		
		
		if(sourceType == Incident.SOURCETYPE_CUSTOM) {
			
			CustomSourceType customSourceType = customSourceTypeDao.findById(new Short(customSourceTypeId));
			incident.setCustomSourceType(customSourceType);
			
		} else if((sourceType == Incident.SOURCETYPE_OTHER) && StringUtils.isNotBlank(otherSourceType)) {
			incident.setOtherSourceType(otherSourceType);
		}
		
		incident.setCategory(category);
		
		incident.setSeverity(severity);
		if (effect != null) {
			incident.setEffect(effect);
		}
		
		if(urgency != null){
			incident.setUrgency(urgency);
		}
		
		String priority = req.getParameter("priority");
		if(EntityUtils.isNotEmpty(priority)) {
			incident.setPriority(new Short(priority));
		} else {
			incident.setPriority(null);
		}
		
		//计算承诺开始时间（响应时间：处于工作时间及直接计算，处于非工作时间，到下一个工作时间开始直接计算）
		Timestamp appTime = incident.getCreateTime();//incident.getApplicationTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(appTime.getTime());
		String calStr = HolidayCalc.calendarToStr(cal);
		if(!HolidayCalc.isWorking(calStr)){
			isworking = false;
			calStr = HolidayCalc.getNextWorkday(calStr);
		}
		
		cal = HolidayCalc.strToCalendar(calStr);
		
		//极高、重大半个小时；高级、中级一个小时；低级两个小时
		if(incident.getPriority()==1){
			cal.add(Calendar.MINUTE, 120);
		}else if(incident.getPriority()==2||incident.getPriority()==3){
			cal.add(Calendar.MINUTE, 60);
		}else if(incident.getPriority()==4||incident.getPriority()==5){
			cal.add(Calendar.MINUTE, 30);
		}
		
		incident.setPromiseStartTime(new Timestamp(cal.getTimeInMillis()));
		
		incident.setExpectFinishTime(expectFinishTime);
		incident.setSubject(subject);
		incident.setContent(content);
		
		incident.setSendMessage(sendMessage);
		
		String catType = req.getParameter("catType");
		if(StringUtils.isNotBlank(catType)){
			if(Integer.valueOf(catType) == 1){
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<Category> mainInfluenceSysList = catDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainInfluenceSystem().clear();
						incident.getMainInfluenceSystem().addAll(new HashSet<Category>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<Category> relaInfluenceSysList = catDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationInfluenceSystem().clear();
						incident.getRelationInfluenceSystem().addAll(new HashSet<Category>(relaInfluenceSysList));
					}
				}
			}else{
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<ConfigurationItem> mainInfluenceSysList = ciDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainCi().clear();
						incident.getMainCi().addAll(new HashSet<ConfigurationItem>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<ConfigurationItem> relaInfluenceSysList = ciDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationCi().clear();
						incident.getRelationCi().addAll(new HashSet<ConfigurationItem>(relaInfluenceSysList));
					}
				}
			}
		}
		
		if (ciIdListStr.length() > 0) {
			
			token = ciIdListStr.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().add(ci);
				} else {
					logger.error("CI(id=" + token[i] + ") not found!");
				}
			}
		}
		
		String kbIds = "";
		HashSet<Knowledge> kbSet = new HashSet<Knowledge>();
		kbIds = req.getParameter("kbId");
		if (kbIds!=null&&kbIds.length() > 0) {
			for(String kbId : kbIds.split(",")){
				Knowledge knowledge = kbDAO.findKnowledgeById(Long.valueOf(kbId));
				if(knowledge != null){
					kbSet.add(knowledge);
				}
			}
		}
		incident.setKnowledges(kbSet);
		
		String inciIds = "";
		HashSet<Incident> inciSet = new HashSet<Incident>();
		inciIds = req.getParameter("inciId");
		if (inciIds!= null && inciIds.length() > 0) {
			for(String inciId : inciIds.split(",")){
				Incident inci = incidentDAO.findById(Long.valueOf(inciId));
				if(inci != null){
					inciSet.add(inci);
				}
			}
		}
		incident.setIncidents(inciSet);
		
		String problemIds = "";
		HashSet<Problem> problemSet = new HashSet<Problem>();
		problemIds = req.getParameter("problemId");
		if (problemIds!= null && problemIds.length() > 0) {
			for(String problemId : problemIds.split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		String relatedProblemIds = req.getParameter("relatedProblemIds");
		if (relatedProblemIds!= null && relatedProblemIds.length() > 0) {
			for(String problemId : relatedProblemIds.replaceAll("#", "").split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		incident.setProblems(problemSet);
		
		//create activity: private method
		IncidentActivity activity = this.createIncidentActivity(creator,IncidentActivity.TYPE_CREATE, "");
		incident.getActivities().add(activity);
		
		//create customInfo: private method
		CustomInfo customInfo = this.getCustomInfoFromRequest(req);
		incident.setCustomInfo(customInfo);
		
		// SLA:
		if(StringUtils.isBlank(agreementIdStr)){
			if(configProperties == null) {
				configProperties = new Properties();
				configProperties.load(new FileInputStream(resource.getFile()));
			}
			
			if(incident.getReservedInteger6().equals(Incident.TYPE_INCIDENT_FORM)){
				agreementIdStr = configProperties.getProperty("incident_sla_id");
			}else{
				agreementIdStr = configProperties.getProperty("request_sla_id");
			}
		}
		
		if(StringUtils.isNotBlank(agreementIdStr)) {
			Agreement agreement = agreementDAO.findById(new Integer(agreementIdStr));
			incident.setSla(agreement);
		}
		incident.setPlannedOutage(StringUtils.isNotBlank(req.getParameter("plannedOutage"))? true:false);
		
		// set status:
		if(checkRole != null) {
			incident.setStatus(Incident.STATUS_CHECKING);//需要复核人复核信息
		} else {
			incident.setStatus(Incident.STATUS_UNASSIGNED);
		}
		
		// save to DB:
		if(StringUtils.isBlank(incidentIdStr) || StringUtils.isNotBlank(template) ) {
			incidentDAO.insert(incident);
		}

		// set request no: private method
		//String requestNo = this.getRequestNo(req, incident);
		String requestNo = this.buildRequestNo2(incident);
	    incident.setRequestNo(requestNo);
		
	    // upload attachment：private method
		this.uploadAttachment(req, incident, creator);
		
		//add references:
		String docIdListStr = req.getParameter("docIdList");
		incident.getReferences().clear();
		if(docIdListStr.length() > 0) {
			token = docIdListStr.split(",");
			for(int i=0;i<token.length;i++) {
				Document doc = libraryDAO.findDocumentById(new Long(token[i]));
				incident.getReferences().add(doc);
			}
		}
		
		if(checkRole == null) {
			if(req.getParameter("fastSolution") != null) {
				//fast solution: just finish the request.
				
				solution = req.getParameter("solution");
				
				incident.setSolution(solution);
				
				//************ add to knowledge base:
				String addToKnowledgeBase = req.getParameter("addToKnowledgeBase");
				if(addToKnowledgeBase != null) {
					
					solution = req.getParameter("solution");
					
					Solution kbSolution = new Solution();
					
					kbSolution.setType(new Short(Solution.TYPE_INCIDENT));
					kbSolution.setCategoryId(incident.getCategory().getId());
					
					kbSolution.setSubject(incident.getSubject());
					kbSolution.setSymptom(incident.getContent());
					kbSolution.setContent(solution);
					
					kbSolution.setCreateTime(new Timestamp(System.currentTimeMillis()));
					//get person ID and role IDs from Session:
					HttpSession session = req.getSession();
					
					Person handler = getCurrentPerson(session);
					kbSolution.setCreator(handler);
					
					kbSolution.setOpen(false);
					kbSolution.setStatus(new Short(Solution.STATUS_ASSESSING));
					kbSolution.setIncident(incident);
					
					kbDAO.insertSolution(kbSolution);
				}		
				
				//add by czk begin
				//check if post process exists:
	//			ProcessDefinition postProcessDefinition = incident.getCategory().getPostProcessDefinition();
				ProcessDefinition postProcessDefinition = null;
				IncidentProcessDefine ipdPost = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_POST);
				if(ipdPost != null) {
					postProcessDefinition = ipdPost.getProcessDefine();
				}
				if(postProcessDefinition != null) {
					
					ProcessInstance postProcessInstance = new ProcessInstance(postProcessDefinition);
					
					postProcessInstance.setInitiatorId(creatorPsnId);
					
					postProcessInstance.setCategory(new Short(ProcessInstance.CATEGORY_INCIDENT));
					postProcessInstance.setExternalId(incident.getId());
					
					//add location and organization of applicant to global data of process instance:
					if(applicant != null){
						Location location = applicant.getLocation();
						postProcessInstance.getGlobalData().put("applicantLocation", location.getId() + ":" + location.getPath());
							
						Organization organization = applicant.getOrganization();
						postProcessInstance.getGlobalData().put("applicantOrganization", organization.getId()+ ":" + organization.getPath());
						
						//add location and organization of creator to global data of process instance:
						if(incident.getCreator() != null){
							location = incident.getCreator().getLocation();
							postProcessInstance.getGlobalData().put("creatorLocation", location.getId() + ":" + location.getPath());
								
							organization = incident.getCreator().getOrganization();
							postProcessInstance.getGlobalData().put("creatorOrganization", organization.getId()+ ":" + organization.getPath());
						}
					}
					//start process instance:
					postProcessInstance.begin();
					
					processDAO.saveProcessInstance(postProcessInstance);
					logger.info("Process instance(id=" + postProcessInstance.getId() + ") was started.");
					
				    //store id of process instance to incident object:
					incident.getPostProcessInstanceIds().add(postProcessInstance.getId());
					incident.setStatus(new Short(Incident.STATUS_ASSESSING));
					incident.setFinishTime(new Timestamp(System.currentTimeMillis()));
					
					//add activity:
					IncidentActivity activityFinish = new IncidentActivity();
					
					activityFinish.setType(IncidentActivity.TYPE_ASSESS);
					activityFinish.setPerson(creator);
					activityFinish.setTime(new Timestamp(System.currentTimeMillis()));
					
					incident.getActivities().add(activityFinish);
					
				} else {
					//close the incident:
					incident.setStatus(new Short(Incident.STATUS_CLOSED));
					incident.setCloseTime(new Timestamp(System.currentTimeMillis()));
					incident.setCloseType(new Short(Incident.CLOSETYPE_SOLVED));
					
					//add activity:
					IncidentActivity activityClose = new IncidentActivity();
					
					activityClose.setType(IncidentActivity.TYPE_CLOSE);
					activityClose.setPerson(creator);
					activityClose.setTime(new Timestamp(System.currentTimeMillis()));
					
					incident.getActivities().add(activityClose);
				}
				
				incident.setFinishPercentage((short)100);
				
				//add by czk end
				
			} else {
			
				//find assessing process:
	//			processDefinition = category.getProcessDefinition();
				IncidentProcessDefine ipd = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_PRE);
				if(ipd != null) {
					processDefinition = ipd.getProcessDefine();
				}
				
				if(processDefinition != null) {
				
					processInstance = new ProcessInstance(processDefinition);
			
					processInstance.setInitiatorId(creatorPsnId);
					
					processInstance.setCategory(new Short(ProcessInstance.CATEGORY_INCIDENT));
					processInstance.setExternalId(incident.getId());
			
					//add location and organization of applicant to global data of process instance:
					if(applicant != null){
						Location location = applicant.getLocation();
						processInstance.getGlobalData().put("applicantLocation", location.getId() + ":" + location.getPath());
						
						Organization organization = applicant.getOrganization();
						processInstance.getGlobalData().put("applicantOrganization", organization.getId()+ ":" + organization.getPath());
						
						//add location and organization of creator to global data of process instance:
						if(incident.getCreator() != null){
							location = creator.getLocation();
							processInstance.getGlobalData().put("creatorLocation", location.getId() + ":" + location.getPath());
							
							organization = creator.getOrganization();
							processInstance.getGlobalData().put("creatorOrganization", organization.getId()+ ":" + organization.getPath());
						}
					}
	
					//start process instance:
					processInstance.begin();
					
					//store the process instance to DB:
					processDAO.saveProcessInstance(processInstance);
					logger.info("Process instance(id=" + processInstance.getId() + ") was started.");
			
					String incidentEditTaskUrl = configProperties.getProperty("incidentEditTaskURL");
					String incidentViewUrl = configProperties.getProperty("incidentViewURL");
					
					NodeInstance nodeInstance = processInstance.getToken().getCurrentNodeInstance();
					
					short node_type = nodeInstance.getNode().getType().shortValue();
					if(node_type == Node.TYPE_STEP || node_type == Node.TYPE_APPROVAL) {
						
						TaskInstance firstTaskInstance = (TaskInstance)nodeInstance.getTaskInstances().get(0);
						
						subject = configProperties.getProperty("newTaskSubject");
						String message = configProperties.getProperty("newTaskMessage");
						
						String editLink = incidentEditTaskUrl +
										"&id=" + incident.getId() +
										"&taskInstanceId=" + firstTaskInstance.getId();
						
						String viewLink = incidentViewUrl +
										"&id=" + incident.getId();
				
						Notice notice = Notice.newNoticeTemplate(processInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
											null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
						
						noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
					
					} else {
						//TYPE_FORK:
						
						subject = configProperties.getProperty("newTaskSubject");
						String message = configProperties.getProperty("newTaskMessage");
			
						List<Token> childTokenList = processInstance.getToken().getChildren();
						for(int i=0;i<childTokenList.size();i++) {
							
							Token childToken = childTokenList.get(i);
							
							//we assume only step nodes here:
							TaskInstance firstTaskInstance = (TaskInstance)childToken.getCurrentNodeInstance().getTaskInstances().get(0);
							
							String editLink = incidentEditTaskUrl +
											"&id=" + incident.getId() +
											"&taskInstanceId=" + firstTaskInstance.getId();
							String viewLink = incidentViewUrl +
											"&id=" + incident.getId();
					
							Notice notice = Notice.newNoticeTemplate(processInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
												null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
							
							noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
						}
						
					}
					
					//set process instance ID:
					incident.setProcessInstanceId(processInstance.getId());
					
					incident.setStatus(new Short(Incident.STATUS_ASSESSING));
					
					//add activity:
					activity = new IncidentActivity();
					activity.setType(IncidentActivity.TYPE_ASSESS);
					activity.setTime(new Timestamp(System.currentTimeMillis()));
					activity.setPerson(creator);
					
					incident.getActivities().add(activity);
					
					//return info of process instance:
					Map<String,Object> processInstanceMap = new HashMap<String,Object>();
					
					processInstanceMap.put("id", processInstance.getId());
					processInstanceMap.put("beginTime", processInstance.getBeginTime());
					processInstanceMap.put("status", processInstance.getStatus());
					processInstanceMap.put("initiatorId", creatorPsnId);
					processInstanceMap.put("initiatorName", creator.getName());
					
					paraMap.put("processInstance", processInstanceMap);
	
				} else {
					//no process defined for this category.
					//just waiting for handlers to take over:
					if(incident.getHandlers().size() > 0) {
						incident.setStatus(Incident.STATUS_ASSIGNED);
						incident.setAssignTime(new Timestamp(System.currentTimeMillis()));
				
					}
						
					else
						incident.setStatus(Incident.STATUS_UNASSIGNED);
				}
				
			}
		}
		
		/* send message 			*/
		if(incident.getSendMessage()) {
			incident.setSendSuccess(sendMessage(incident));
		}
		
		//update incident:
		incidentDAO.update(incident);
		logger.info("Incident(id=" + incident.getId() + "') was created.");
       
		//return map:
		paraMap.put("windowLocation", "incident_action.do?operation=detail&id=" + incident.getId());
		
		if(incidentIdStr != null) {
			//draft -> create, refresh parent window frame page:
			paraMap.put("parentReload", true);
		}
		
		//for trigger:
		paraMap.put("id", incident.getId());
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_CREATE, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		// private method 
		this.setProcessInstance(req, processInstance, incident);
		
		String jobInstanceId = req.getParameter("jobInstanceId");// 是否为来自计划作业发起的工单
		if(EntityUtils.isNotEmpty(jobInstanceId)) {
			JobInstance jobInstance = jobInstanceDAO.get(new Long(jobInstanceId));
			if(jobInstance != null) {
				RequestByJob requestByJob = new RequestByJob();
				requestByJob.setJobInstance(jobInstance);
				requestByJob.setRequestNo(requestNo);
				requestByJob.setRequestType(RequestByJob.REQUEST_TYPE_INCIDENT);
				requestByJobDAO.save(requestByJob);
			}
		}
		
		//非工作时间创建，加入工作时间提醒
		if(!isworking){
			NoticeWorking noticeWorking = new NoticeWorking();
			noticeWorking.setType(0);
			noticeWorking.setIncidentId(incident.getId());
			noticeWorkingDAO.save(noticeWorking);
		}
		
		return new ModelAndView(getSuccessView(), paraMap);
		
	}//add()

	/**
	 * 活动记录
	 * @param comment 
	 * 
	 * @param creator
	 * @return
	 */
	private IncidentActivity createIncidentActivity(Person person,short type, String comment) {
		
		IncidentActivity activity = new IncidentActivity();
		activity.setType(type);
		activity.setPerson(person);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		return activity;
	}

	/**
	 * 工单号生产算法
	 * 
	 * @param req
	 * @param incident
	 * @return
	 * @throws Exception
	 */
	private String getRequestNo(HttpServletRequest req, Incident incident) throws Exception {
		
		String requestNo = "";
		Session currentSession = incidentDAO.getHibernateTemplate().getSessionFactory().getCurrentSession();
	    
	    RequestNoGeneration requestNoGeneration = requestNoGenerationDao.findByModule(ItsmConstants.MODULE_INCIDENT);
	    
	    // 事件单工单号生产方法
	    if(requestNoGeneration != null){
	    	
	    	String className = requestNoGeneration.getClassName();
	    	String methodName = requestNoGeneration.getMethodName();
			
			//invoke the method:
			try {			
				Class<?> c = Class.forName(className);
				Method m = c.getDeclaredMethod(methodName, HttpServletRequest.class, Session.class, Long.class);
				requestNo = m.invoke(null, req, currentSession, incident.getId()).toString();
				
			} catch(ClassNotFoundException e) {				
				logger.error("Class " + className + " not found!");

				requestNo = buildRequestNo(incident);
				
			} catch(NoSuchMethodException e) {				
				logger.error("Method " + methodName + " not found in class " + className + " !");

				requestNo = buildRequestNo(incident);
				
			} catch(Exception e) {				
				logger.error(Utils.getStackTrace(e));
				throw e;
			}
			
	    }else{
	    	
	    	requestNo = buildRequestNo(incident);
	    }
		return requestNo;
	}

	/**
	 * 流程实例
	 * 
	 * @param req
	 * @param processInstance
	 * @param incident
	 */
	private void setProcessInstance(HttpServletRequest req,
			ProcessInstance processInstance, Incident incident) {
		try {
			if(processInstance != null) {
				// 获取当前步骤节点
				NodeInstance nodeInstance = processInstance.getToken().getCurrentNodeInstance();
				// 获取节点类型
				short nodeType = nodeInstance.getNode().getType();
				if(nodeType == Node.TYPE_STEP) {// 如果是普通步骤节点
					if(nodeInstance.getStatus() == NodeInstance.STATUS_STANDBY) {// 如果步骤为待启动状态
						//todo
					} else if(nodeInstance.getStatus() == NodeInstance.STATUS_ASSIGNED) {//如果步骤状态为待受理(负责人为角色)
						// 发送步骤启动通知
						processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, incident, processInstance, nodeInstance, null, req);
					} else if(nodeInstance.getStatus() == NodeInstance.STATUS_RUNNING) {//如果步骤状态为执行中(负责人为人员)
						// 发送步骤启动通知
						processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, incident, processInstance, nodeInstance, null, req);
						
						if(nodeInstance.getExecuteMode() != null && nodeInstance.getExecuteMode().equals(NodeInstance.EXECUTEMODE_IMMEDIATE)) {// 如果步骤下任务执行方式为并发
							// 遍历节点下任务，并发送任务启动通知
							List<TaskInstance> taskInstances = nodeInstance.getTaskInstances();
							for(int index = 0; index < taskInstances.size(); index++) {
								TaskInstance taskInstance = taskInstances.get(index);
								if(taskInstance.getType() != TaskInstance.TYPE_AUTO && taskInstance.getType() != TaskInstance.TYPE_STEP_TAKEOVER && taskInstance.getType() != TaskInstance.TYPE_BRANCH) {// 自动任务不发送通知
									processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_START, incident, processInstance, nodeInstance, taskInstance, req);
								}
							}
						} else {//步骤下任务串行执行
							List<TaskInstance> taskInstances = nodeInstance.getTaskInstances();
							// 遍历节点下任务，找到第一个不为自动任务的任务实例，并发送任务启动通知
							for(int index = 0; index < taskInstances.size(); index++) {
								TaskInstance taskInstance = taskInstances.get(index);
								if(taskInstance.getType() != TaskInstance.TYPE_AUTO && taskInstance.getType() != TaskInstance.TYPE_STEP_TAKEOVER && taskInstance.getType() != TaskInstance.TYPE_BRANCH) {// 自动任务不发送通知
									processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_START, incident, processInstance, nodeInstance, taskInstance, req);
									break;
								}
							}
						}
					}
				} else if(nodeType == Node.TYPE_FORK) {// 并发开始节点
					// 遍历所有节点
					List<Token> childTokenList = processInstance.getToken().getChildren();
					for(int i=0;i<childTokenList.size();i++) {
						Token childToken = childTokenList.get(i);
						NodeInstance tempNodeInst = childToken.getCurrentNodeInstance();
						if(tempNodeInst.getStatus() == NodeInstance.STATUS_ASSIGNED) {//如果步骤状态为待受理(负责人为角色)
							// 发送步骤启动通知
							processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, incident, processInstance, tempNodeInst, null, req);
						} else if(tempNodeInst.getStatus() == NodeInstance.STATUS_RUNNING) {//如果步骤状态为执行中(负责人为人员)
							if(tempNodeInst.getTaskInstances().size() == 0) {//如果节点下没有任务，目前忽略
								
							} else {
								// 发送步骤启动通知
								processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, incident, processInstance, tempNodeInst, null, req);
								
								if(tempNodeInst.getExecuteMode() != null && tempNodeInst.getExecuteMode().equals(NodeInstance.EXECUTEMODE_IMMEDIATE)) {// 如果步骤下任务执行方式为并发
									// 遍历节点下任务，并发送任务启动通知
									List<TaskInstance> taskInstances = tempNodeInst.getTaskInstances();
									for(int index = 0; index < taskInstances.size(); index++) {
										TaskInstance taskInstance = taskInstances.get(index);
										if(taskInstance.getType() != TaskInstance.TYPE_AUTO && taskInstance.getType() != TaskInstance.TYPE_STEP_TAKEOVER && taskInstance.getType() != TaskInstance.TYPE_BRANCH) {// 自动任务不发送通知
											processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_START, incident, processInstance, nodeInstance, taskInstance, req);
										}
									}
								} else {//步骤下任务串行执行
									List<TaskInstance> taskInstances = tempNodeInst.getTaskInstances();
									// 遍历节点下任务，找到第一个不为自动任务的任务实例，并发送任务启动通知
									for(int index = 0; index < taskInstances.size(); index++) {
										TaskInstance taskInstance = taskInstances.get(index);
										if(taskInstance.getType() != TaskInstance.TYPE_AUTO && taskInstance.getType() != TaskInstance.TYPE_STEP_TAKEOVER && taskInstance.getType() != TaskInstance.TYPE_BRANCH) {// 自动任务不发送通知
											processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_START, incident, processInstance, nodeInstance, taskInstance, req);
											break;
										}
									}
								}
							}
						}
						
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
	}

	/**
	 * 新建事件时上传附件
	 * @param req
	 * @param incident
	 * @param creator
	 * @param fileIndexList
	 * @param multipartRequest
	 * @param removedAttachmentIdListStr
	 * @throws IOException
	 */
	private void uploadAttachment(HttpServletRequest req, Incident incident,Person creator) throws IOException {
		
		String fileIndexList = req.getParameter("fileIndexList");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
		String removedAttachmentIdListStr = req.getParameter("removedAttachmentIdList");
			
		String[] token;
		String path;
		String path1;
		String path2;
		String path3;
		MultipartFile multipartFile;
		if((removedAttachmentIdListStr != null) && (removedAttachmentIdListStr.length() > 0)) {
			
			String attachmentIdStr[] = removedAttachmentIdListStr.split(",");
			
			for(int i=0;i<attachmentIdStr.length;i++) {
				
				Long attachmentId = new Long(attachmentIdStr[i]);
				Document attachment = libraryDAO.findDocumentById(attachmentId);
				
				path = attachment.getPath1();
				if(attachment.getPath2() != null) {
					path += attachment.getPath2();
				
					if(attachment.getPath3() != null)
						path += attachment.getPath3();
				}
				
				File file = new File(path + System.getProperty("file.separator") + attachment.getFileName()); 
				file.delete();
				
				incident.getAttachments().remove(attachment);
				
				libraryDAO.deleteDocument(attachmentId);
				logger.info("Attachment(id=" + attachment.getId() + ", name=" + attachment.getFileName() +
								", originalName=" + attachment.getOriginalFileName() + ") was deleted.");
			}
		}

	    //add attachments:
		String attachmentPath = configProperties.getProperty("attachmentPath");
		
		path = attachmentPath + System.getProperty("file.separator") +
					"incident" + System.getProperty("file.separator") +
					"incident" + incident.getId() + System.getProperty("file.separator");

		path1 = path2 = path3 = null;
		
		int max_len = Document.MAX_PATH_LENGTH;
		if(path.length() > max_len) {
			String tmp = path;
			
			path1 = tmp.substring(0, max_len - 1);
			
			tmp = tmp.substring(max_len);
			if(tmp.length() > max_len) {
				
				path2 = tmp.substring(0, max_len - 1);
				
				tmp = tmp.substring(max_len);
				if(tmp.length() > max_len) {
					
					throw new ProcessException("Path_of_attachment_too_long");
					
				} else {
					
					path3 = tmp;
				}
				
			} else {
				
				path2 = tmp;
			}
			
		} else {
			
			path1 = path;
			
		}
		
		//get each file:
		if(fileIndexList.length() > 0) {
			
			token = fileIndexList.split(",");
			for (int i = 0; i < token.length; i++) {
				
				//get file from http request. covert it to MultipartHttpServletRequest first:
				multipartFile = multipartRequest.getFile("fileName" + token[i]);

				String docSubject = req.getParameter("subject" + token[i]);
				String author = req.getParameter("author" + token[i]);
				String number = req.getParameter("number" + token[i]);
				String directoryIdStr = req.getParameter("directoryId" + token[i]);
				String keywords = req.getParameter("keywords" + token[i]);
				String docDescription = req.getParameter("fileDescription" + token[i]);
	
				String fileName = multipartFile.getOriginalFilename();
				
				//create attachment object:
				Document attachment = new Document();
				attachment.setCreateTime(new Date(System.currentTimeMillis()));
				attachment.setOriginalFileName(fileName);
				attachment.setCreator(creator);
				
				//set type to incident:
				attachment.setType(Document.TYPE_INCIDENT);
				
				//set dummy to false, so it will be found in library
				attachment.setDummy(false);
				
				//use current time as new file name:
//				String newFileName = (new Long(System.currentTimeMillis())).toString();
				String newFileName = (new Long(System.currentTimeMillis())).toString() + "_" + i;
				attachment.setFileName(newFileName);
	
				if(docSubject.length() > 0)
					attachment.setSubject(docSubject);
				else
					attachment.setSubject(fileName);
				
				if(author.length() > 0)
					attachment.setAuthor(author);
				
				if(number.length() > 0)
					attachment.setNumber(number);
				
				if(keywords.length() > 0)
					attachment.setKeywords(keywords);
				
				if(docDescription.length() > 0)
					attachment.setDescription(docDescription);
				
				if(directoryIdStr.length() > 0) {
					
					Directory dir = libraryDAO.findDirectoryById(new Integer(directoryIdStr));
					attachment.setDirectory(dir);
					
				}
				
				//set path:
				attachment.setPath1(path1);
				attachment.setPath2(path2);
				attachment.setPath3(path3);
				
				//add it to task instance's attachments:
				incident.getAttachments().add(attachment);
				
				//upload the file to the server:
				try {
					
					File dir = new File(path);
					
					if(!dir.exists()) {
						
						dir.mkdirs();
					}
					
					File destinationFile = new File(path + System.getProperty("file.separator") + newFileName);
						
					multipartFile.transferTo(destinationFile);
					
				} catch(IOException e) {
					
					logger.error(e.getLocalizedMessage());
					
					throw e;
				}
			}
		
		}
	}

	/**
	 * 设置事件处理人
	 * 
	 * @param incident
	 * @param handlerIdListStr
	 */
	private void setHandlers(Incident incident, String handlerIdListStr) {
		String[] token;
		if(StringUtils.isNotBlank(handlerIdListStr)) {
			
			token = handlerIdListStr.split(",");
			for(int i=0;i<token.length;i++) {
				
				Person handler = personDAO.findById(new Integer(token[i]));
				incident.getHandlers().add(handler);
			}
		}
	}

	/**
	 * 设置事件参与人
	 * 
	 * @param incident
	 * @param categoryId
	 * @param participatorPersonId
	 */
	private void setParticipators(Incident incident, Short categoryId,
			String participatorPersonId) {
		if(StringUtils.isNotBlank(participatorPersonId)) {
			String[] token = participatorPersonId.split(",");
			for(int i=0;i<token.length;i++) {
				Person participator = personDAO.findById(new Integer(token[i]));
				incident.getParticipators().add(participator);
			}
		} else {
			// ///////// 读取分配规则的参与人添加给Incident /////////////
			Set<Integer> participantIds = new HashSet<Integer>(0);
			List<Assignment> assignments = this.assignmentDAO
					.findByIncidentCatId(categoryId);
			for (Assignment assignment : assignments) {
				participantIds.addAll(assignment.getParticipantIds());
				Integer participantRoleId = assignment.getParticipantRoleId();
				if (participantRoleId != null) {
					incident.getParticipators().addAll(
							roleDAO.findPersons(participantRoleId));
				}
			}
			if (!participantIds.isEmpty()) {
				for (Integer pid : participantIds) {
					Person participator = personDAO.findById(pid);
					incident.getParticipators().add(participator);
				}
			}
			// ///////////////////
		}
	}
	
	
	/**
	 * 事件信息复核
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkIncidentInfo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		ProcessInstance processInstance = null;
		ProcessDefinition processDefinition = null;
		String token[];
		ConfigurationItem ci;
		String path, path1, path2, path3;
		MultipartFile multipartFile;
		String solution;
		//Short reasonId;
		//Reason reason;
		Incident incident;
		Person applicant = null;
		Customer customer = null;
		
		Integer personId = (Integer) req.getSession().getAttribute("personId");
		Person currentPerson = personDAO.findById(personId);
		
		//get parameters from http request:
		String incidentIdStr = req.getParameter("id");
		String saveFlagStr = req.getParameter("saveFlag");
		boolean saveFlag = EntityUtils.isNotEmpty(saveFlagStr);//是否为保存操作
		
		String applicantPersonIdStr = req.getParameter("applicantPersonId");
		if(StringUtils.isNotBlank(applicantPersonIdStr)) {
			Integer applPsnId = new Integer(req.getParameter("applicantPersonId"));
			applicant = personDAO.findById(applPsnId);
		}
		
		String customerId = req.getParameter("customerId");
		if(StringUtils.isNotBlank(customerId)){
			customer = customerDao.findCustomerById(Integer.valueOf(customerId));
		}
		
		Timestamp applicationTime = Timestamp.valueOf(req.getParameter("applicationTime"));
		
		Short sourceType = new Short(req.getParameter("sourceType"));
		String otherSourceType = req.getParameter("otherSourceType");
		String customSourceTypeId = req.getParameter("customSourceTypeId");
		
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		
		Short severityId = new Short(req.getParameter("severityId"));
		Severity severity = severityDAO.findById(severityId);
		
		Short effectId = new Short(req.getParameter("effectId"));
		Effect effect = effectDAO.findById(effectId);
		
		Short urgencyId = new Short(req.getParameter("urgencyId"));
		Urgency urgency = urgencyDAO.findById(urgencyId);
		
		Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		
		String ciIdListStr = req.getParameter("ciIdList");
		String fileIndexList = req.getParameter("fileIndexList");
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
		String removedAttachmentIdListStr = req.getParameter("removedAttachmentIdList");
		
		
		//get references:
		String docIdListStr = req.getParameter("docIdList");
		
		CustomInfo customInfo = this.getCustomInfoFromRequest(req);
		
		//SLA id:
		String agreementIdStr = req.getParameter("agreementId");
		
		incident = incidentDAO.findByIdForUpdate(new Long(incidentIdStr));
		if(incident.getStatus() != Incident.STATUS_CHECKING) {
			paraMap.put("error", "request_has_already_been_checked_by_other_person");
			
			return new ModelAndView(getErrorView(), paraMap);
		}
		
		String participatorPersonId = req.getParameter("participatorPersonId");
		String handlerIdListStr = req.getParameter("handlerPersonId");
		
		incident.getHandlers().clear();
		setHandlers(incident, handlerIdListStr);
		
		incident.getParticipators().clear();
		if(participatorPersonId.length() > 0) {
			token = participatorPersonId.split(",");
			for(int i=0;i<token.length;i++) {
				Person handler = personDAO.findById(new Integer(token[i]));
				incident.getParticipators().add(handler);
			}
		}
		
		if(applicant != null) {
			
			incident.setApplicant(applicant);
			incident.setLocation(applicant.getLocation());
			
		} else if(customer != null) {
			
			incident.setCustomer(customer);
			
			//use location of current person:
			Integer locationId = (Integer)req.getSession().getAttribute("locationId");
			incident.setLocation(locationDAO.findById(locationId));
		}
		
		
		incident.setApplicationTime(applicationTime);
		incident.setSourceType(sourceType);
		
		if(sourceType == Incident.SOURCETYPE_CUSTOM) {
			
			CustomSourceType customSourceType = customSourceTypeDao.findById(new Short(customSourceTypeId));
			incident.setCustomSourceType(customSourceType);
			
		} else if((sourceType == Incident.SOURCETYPE_OTHER) && StringUtils.isNotBlank(otherSourceType)) {
			incident.setOtherSourceType(otherSourceType);
		}
		
		incident.setCategory(category);
		
		incident.setSeverity(severity);
		incident.setEffect(effect);
		incident.setUrgency(urgency);
		
//		short priority = (short)(effect.getValue().shortValue() * urgency.getValue().shortValue());
		String priority = req.getParameter("priority");
		if(EntityUtils.isNotEmpty(priority)) {
			incident.setPriority(new Short(priority));
		} else {
			incident.setPriority(null);
		}
		
		incident.setExpectFinishTime(expectFinishTime);
		incident.setSubject(subject);
		incident.setContent(content);
		
		incident.getCIs().clear();
		if (ciIdListStr.length() > 0) {
			
			token = ciIdListStr.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().add(ci);
				} else {
					logger.error("CI(id=" + token[i] + ") not found!");
				}
			}
		}
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_CHECK));
		activity.setPerson(currentPerson);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		
		incident.getActivities().add(activity);
		
		incident.setCustomInfo(customInfo);
		
		// SLA:
		if(agreementIdStr != null && agreementIdStr.length() > 0) {
			
			Agreement agreement = agreementDAO.findById(new Integer(agreementIdStr));
			incident.setSla(agreement);
		}
		
		// set status to UNASSINGED at this moment:
		if(!saveFlag) {
			incident.setStatus(Incident.STATUS_UNASSIGNED);//需要是复核提交操作，则修改事件状态
		}
		
		// upload attachment：private method
		this.uploadAttachment(req, incident, currentPerson);
		
		// add references:
		incident.getReferences().clear();
		if(docIdListStr.length() > 0) {
			
			token = docIdListStr.split(",");
			for(int i=0;i<token.length;i++) {
				
				Document doc = libraryDAO.findDocumentById(new Long(token[i]));
				
				incident.getReferences().add(doc);
			}
		}
		if(!saveFlag) {
			if(req.getParameter("fastSolution") != null) {
				//fast solution: just finish the request.
				
				solution = req.getParameter("solution");
				
				//removed, 2010-6-17
				/*
				reasonId = new Short(req.getParameter("reasonId"));
				reason = reasonDAO.findById(reasonId);
				 */
				
				incident.setSolution(solution);
				
				
				/*//************2010.10.18  椹姞 add to knowledge base:
				String addToKnowledgeBase = req.getParameter("addToKnowledgeBase");
				if(addToKnowledgeBase != null) {
					
					solution = req.getParameter("solution");
					
					Solution kbSolution = new Solution();
					
					kbSolution.setType(new Short(Solution.TYPE_INCIDENT));
					kbSolution.setCategoryId(incident.getCategory().getId());
					
					kbSolution.setSubject(incident.getSubject());
					kbSolution.setSymptom(incident.getContent());
					kbSolution.setContent(solution);
					
					kbSolution.setCreateTime(new Timestamp(System.currentTimeMillis()));
					kbSolution.setCreator(currentPerson);
					
					kbSolution.setOpen(false);
					kbSolution.setStatus(new Short(Solution.STATUS_ASSESSING));
					kbSolution.setIncident(incident);
					
					kbDAO.insertSolution(kbSolution);
				}		*/
				
				//add by czk begin
				//check if post process exists:
				//			ProcessDefinition postProcessDefinition = incident.getCategory().getPostProcessDefinition();
				ProcessDefinition postProcessDefinition = null;
				IncidentProcessDefine ipdPost = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_POST);
				if(ipdPost != null) {
					postProcessDefinition = ipdPost.getProcessDefine();
				}
				if(postProcessDefinition != null) {
					
					ProcessInstance postProcessInstance = new ProcessInstance(postProcessDefinition);
					
					postProcessInstance.setInitiatorId(incident.getCreator().getId());
					
					postProcessInstance.setCategory(new Short(ProcessInstance.CATEGORY_INCIDENT));
					postProcessInstance.setExternalId(incident.getId());
					
					//add location and organization of applicant to global data of process instance:
					if(applicant != null){
						Location location = applicant.getLocation();
						postProcessInstance.getGlobalData().put("applicantLocation", location.getId() + ":" + location.getPath());
						
						Organization organization = applicant.getOrganization();
						postProcessInstance.getGlobalData().put("applicantOrganization", organization.getId()+ ":" + organization.getPath());
						
						//add location and organization of creator to global data of process instance:
						if(incident.getCreator() != null){
							location = incident.getCreator().getLocation();
							postProcessInstance.getGlobalData().put("creatorLocation", location.getId() + ":" + location.getPath());
							
							organization = incident.getCreator().getOrganization();
							postProcessInstance.getGlobalData().put("creatorOrganization", organization.getId()+ ":" + organization.getPath());
						}
					}
					//start process instance:
					postProcessInstance.begin();
					
					processDAO.saveProcessInstance(postProcessInstance);
					logger.info("Process instance(id=" + postProcessInstance.getId() + ") was started.");
					
					//store id of process instance to incident object:
					incident.getPostProcessInstanceIds().add(postProcessInstance.getId());
					incident.setStatus(new Short(Incident.STATUS_ASSESSING));
					incident.setFinishTime(new Timestamp(System.currentTimeMillis()));
					
					//add activity:
					IncidentActivity activityFinish = new IncidentActivity();
					
					activityFinish.setType(IncidentActivity.TYPE_ASSESS);
					activityFinish.setPerson(incident.getCreator());
					activityFinish.setTime(new Timestamp(System.currentTimeMillis()));
					
					incident.getActivities().add(activityFinish);
					
				} else {
					//close the incident:
					incident.setStatus(new Short(Incident.STATUS_CLOSED));
					incident.setCloseTime(new Timestamp(System.currentTimeMillis()));
					incident.setCloseType(new Short(Incident.CLOSETYPE_SOLVED));
					
					//add activity:
					IncidentActivity activityClose = new IncidentActivity();
					
					activityClose.setType(IncidentActivity.TYPE_CLOSE);
					activityClose.setPerson(incident.getCreator());
					activityClose.setTime(new Timestamp(System.currentTimeMillis()));
					
					incident.getActivities().add(activityClose);
				}
				
				incident.setFinishPercentage((short)100);
				
				//add by czk end
				
			} else {
				
				//find assessing process:
				//			processDefinition = category.getProcessDefinition();
				IncidentProcessDefine ipd = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_PRE);
				if(ipd != null) {
					processDefinition = ipd.getProcessDefine();
				}
				
				if(processDefinition != null) {
					
					processInstance = new ProcessInstance(processDefinition);
					
					processInstance.setInitiatorId(incident.getCreator().getId());
					
					processInstance.setCategory(new Short(ProcessInstance.CATEGORY_INCIDENT));
					processInstance.setExternalId(incident.getId());
					
					//add location and organization of applicant to global data of process instance:
					if(applicant != null){
						Location location = applicant.getLocation();
						processInstance.getGlobalData().put("applicantLocation", location.getId() + ":" + location.getPath());
						
						Organization organization = applicant.getOrganization();
						processInstance.getGlobalData().put("applicantOrganization", organization.getId()+ ":" + organization.getPath());
						
						//add location and organization of creator to global data of process instance:
						if(incident.getCreator() != null){
							location = incident.getCreator().getLocation();
							processInstance.getGlobalData().put("creatorLocation", location.getId() + ":" + location.getPath());
							
							organization = incident.getCreator().getOrganization();
							processInstance.getGlobalData().put("creatorOrganization", organization.getId()+ ":" + organization.getPath());
						}
					}
					
					//start process instance:
					processInstance.begin();
					
					//store the process instance to DB:
					processDAO.saveProcessInstance(processInstance);
					logger.info("Process instance(id=" + processInstance.getId() + ") was started.");
					
					String incidentEditTaskUrl = configProperties.getProperty("incidentEditTaskURL");
					String incidentViewUrl = configProperties.getProperty("incidentViewURL");
					
					NodeInstance nodeInstance = processInstance.getToken().getCurrentNodeInstance();
					
					short node_type = nodeInstance.getNode().getType().shortValue();
					if(node_type == Node.TYPE_STEP || node_type == Node.TYPE_APPROVAL) {
						
						TaskInstance firstTaskInstance = (TaskInstance)nodeInstance.getTaskInstances().get(0);
						
						subject = configProperties.getProperty("newTaskSubject");
						String message = configProperties.getProperty("newTaskMessage");
						
						String editLink = incidentEditTaskUrl +
						"&id=" + incident.getId() +
						"&taskInstanceId=" + firstTaskInstance.getId();
						
						String viewLink = incidentViewUrl +
						"&id=" + incident.getId();
						
						Notice notice = Notice.newNoticeTemplate(processInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
								null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
						
						noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
						
					} else {
						//TYPE_FORK:
						
						subject = configProperties.getProperty("newTaskSubject");
						String message = configProperties.getProperty("newTaskMessage");
						
						List<Token> childTokenList = processInstance.getToken().getChildren();
						for(int i=0;i<childTokenList.size();i++) {
							
							Token childToken = childTokenList.get(i);
							
							//we assume only step nodes here:
							TaskInstance firstTaskInstance = (TaskInstance)childToken.getCurrentNodeInstance().getTaskInstances().get(0);
							
							String editLink = incidentEditTaskUrl +
							"&id=" + incident.getId() +
							"&taskInstanceId=" + firstTaskInstance.getId();
							String viewLink = incidentViewUrl +
							"&id=" + incident.getId();
							
							Notice notice = Notice.newNoticeTemplate(processInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
									null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
							
							noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
						}
						
					}
					
					//set process instance ID:
					incident.setProcessInstanceId(processInstance.getId());
					
					incident.setStatus(new Short(Incident.STATUS_ASSESSING));
					
					//add activity:
					activity = new IncidentActivity();
					activity.setType(IncidentActivity.TYPE_ASSESS);
					activity.setTime(new Timestamp(System.currentTimeMillis()));
					activity.setPerson(incident.getCreator());
					
					incident.getActivities().add(activity);
					
					//return info of process instance:
					Map<String,Object> processInstanceMap = new HashMap<String,Object>();
					
					processInstanceMap.put("id", processInstance.getId());
					processInstanceMap.put("beginTime", processInstance.getBeginTime());
					processInstanceMap.put("status", processInstance.getStatus());
					
					processInstanceMap.put("initiatorId", incident.getCreator().getId());
					processInstanceMap.put("initiatorName", incident.getCreator().getName());
					
					paraMap.put("processInstance", processInstanceMap);
					
				} else {
					//no process defined for this category.
					//just waiting for handlers to take over:
					if(incident.getHandlers().size() > 0) {
						
						incident.setStatus(Incident.STATUS_ASSIGNED);
						incident.setAssignTime(new Timestamp(System.currentTimeMillis()));
						
						//					Agreement agreement = incident.getSla();
						//					if(agreement != null) {
						//					
						//						List<Target> targets = agreement.getTargets();
						//						Timestamp now = new Timestamp(System.currentTimeMillis());
						
						//						for(int i=0;i<targets.size();i++) {
						//							
						//							Target target = targets.get(i);
						//							if((priority >= target.getCeiling()) && (priority < target.getFloor()) ) {
						//								
						//								/*if(target.inBusinessHour(now)) {
						//									
						//									
						//									
						//								}*/
						//								
						//								break;
						//							}
						//					
						//						}
						
						//					}
						
					}
					
					else
						incident.setStatus(Incident.STATUS_UNASSIGNED);
				}
				
			}
		}
		
		/* send message 			*/
		if(incident.getSendMessage()) {
			
			incident.setSendSuccess(sendMessage(incident));
			
		}
		
		//update incident:
		incidentDAO.update(incident);
		logger.info("Incident(id=" + incident.getId() + "') was created.");
		
		//return map:
		paraMap.put("windowLocation", "incident_action.do?operation=detail&id=" + incident.getId());
		
		if(incidentIdStr != null) {
			//draft -> create, refresh parent window frame page:
			paraMap.put("parentReload", true);
		}
		
		//for trigger:
		paraMap.put("id", incident.getId());
		
		
		return new ModelAndView(getSuccessView(), paraMap);
		
	}
	
	/**
	 * 更新事件信息
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView updateIncidentInfo(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String,Object> paraMap = new HashMap<String,Object>();
		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		String token[];
		MultipartFile multipartFile;
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
		String path, path1, path2, path3;
		
		Integer personId = (Integer) req.getSession().getAttribute("personId");
		Person currentPerson = personDAO.findById(personId);
		String incidentIdStr = req.getParameter("incidentId");
		Incident incident;
		incident = incidentDAO.findByIdForUpdate(new Long(incidentIdStr));
		
		//来源类型
		Short sourceType = new Short(req.getParameter("sourceType"));
		String otherSourceType = req.getParameter("otherSourceType");
		String customSourceTypeId = req.getParameter("customSourceTypeId");
		incident.setSourceType(sourceType);
		if(sourceType == Incident.SOURCETYPE_CUSTOM) {
			
			CustomSourceType customSourceType = customSourceTypeDao.findById(new Short(customSourceTypeId));
			incident.setCustomSourceType(customSourceType);
			
		} else if((sourceType == Incident.SOURCETYPE_OTHER) && StringUtils.isNotBlank(otherSourceType)) {
			incident.setOtherSourceType(otherSourceType);
		}
		//期望完成时间
		Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		incident.setExpectFinishTime(expectFinishTime);
		//分类一、分类二
		String reservedInteger5 = req.getParameter("reservedInteger5");
		String reservedInteger4 = req.getParameter("reservedInteger4");
		if(null != reservedInteger5 && !reservedInteger5.equals("")){
			incident.setReservedInteger5(Integer.parseInt(reservedInteger5));
		}else{
			incident.setReservedInteger5(null);
		}
		incident.setReservedInteger4(Integer.parseInt(reservedInteger4));
		//参与人
		String oldParIds = ",";
		for(Person person : incident.getParticipators()){
			oldParIds += person.getId() + ",";
		}
		String participatorsIds = req.getParameter("partIds");
		if(StringUtils.isNotBlank(participatorsIds) && participatorsIds.length() > 0){
			String[] newParIds = participatorsIds.split(",");
			for(int n = 0; n < newParIds.length; n++){
				
				if(oldParIds.indexOf("," + newParIds[n] + ",") == -1){
					
					incident.getParticipators().add(personDAO.findById(new Integer(newParIds[n])));
				}
			}
		}
		//主题
		String commonSubject = req.getParameter("commonSubject");
		incident.setSubject(commonSubject);
		//内容
		String commonContent = req.getParameter("commonContent");
		incident.setContent(commonContent);
		//详细信息
		CustomInfo customInfo = this.getCustomInfoFromRequest(req);
		incident.setCustomInfo(customInfo);
		//配置项
		String ciIdListStr = req.getParameter("ciIdList");
		incident.getCIs().clear();
		ConfigurationItem ci = null;
		if (ciIdListStr.length() > 0) {
			token = ciIdListStr.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().add(ci);
				} else {
					logger.error("CI(id=" + token[i] + ") not found!");
				}
			}
		}
		// 添加附件
		this.uploadAttachment(req, incident, currentPerson);
		
		//参考文档
		String docIdListStr = req.getParameter("docIdList");
		incident.getReferences().clear();
		if(docIdListStr.length() > 0) {
			
			token = docIdListStr.split(",");
			for(int i=0;i<token.length;i++) {
				
				Document doc = libraryDAO.findDocumentById(new Long(token[i]));
				
				incident.getReferences().add(doc);
			}
		}
		
		//参考的变更
		String changeIdListStr = req.getParameter("changeIdList");
		String removedChangeIdListStr = req.getParameter("removedChangeIdList");
		if(StringUtils.isNotBlank(removedChangeIdListStr)) {
			
			for(String changeIdStr : removedChangeIdListStr.split(",")) {
			
				for(Change change : incident.getReferencedChanges()) {
				
					if(change.getId().longValue() == Long.parseLong(changeIdStr)) {
						
						incident.getReferencedChanges().remove(change);
						break;
					}
				}
			
			}
		}
		
		
		if(StringUtils.isNotBlank(changeIdListStr)) {
			
			for(String changeIdStr : changeIdListStr.split(",")) {
				
				Change change = changeDAO.findById(new Long(changeIdStr));
				incident.getReferencedChanges().add(change);
			}
		}
		
		//参考的需求单
		String requirementIdListStr = req.getParameter("requirementIdList");	//newly added requirements
		String removedRequirementIdListStr = req.getParameter("removedRequirementIdList");	//removed requirements
		
		//remove reference requirements:
		if(StringUtils.isNotBlank(removedRequirementIdListStr)) {
			
			for(String requirementIdStr : removedRequirementIdListStr.split(",")) {
			
				for(Requirement requirement : incident.getReferencedRequirements()) {
				
					if(requirement.getId().longValue() == Long.parseLong(requirementIdStr)) {
						
						incident.getReferencedRequirements().remove(requirement);
						break;
					}
				}
			
			}
		}
		
		if(StringUtils.isNotBlank(requirementIdListStr)) {
			
			for(String requirementIdStr : requirementIdListStr.split(",")) {
				
				Requirement requirement = requirementDAO.findById(new Long(requirementIdStr));
				incident.getReferencedRequirements().add(requirement);
			}
		}
		
		//工作日志
		int countWorkLog = Integer.parseInt(req.getParameter("countWorkLog"));
		for(int m = 1; m <= countWorkLog; m++){
			IncidentWorkLog workLog = new IncidentWorkLog();
			workLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
			workLog.setCreator(currentPerson);
			workLog.setContent(req.getParameter("content" + m));
			
			incident.getWorkLogs().add(workLog);
		}
		
		//记录活动记录
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		IncidentActivity incidentAct = new IncidentActivity();
		incidentAct.setType(IncidentActivity.TYPE_UPDATE);
		incidentAct.setTime(currentTime);
		incidentAct.setPerson(currentPerson);
		incident.getActivities().add(incidentAct);
		
		this.setReservedField(incident, req);
		//刷新父页面
		paraMap.put("parentReload", "true");
		
		return new ModelAndView(getSuccessView(), paraMap);
	}
	
	
	/*
	 * method to save draft of incident
	 */
	public ModelAndView draft(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String token[];
		ConfigurationItem ci;
		String path, path1, path2, path3;
		MultipartFile multipartFile;
		String solution;
		//Short reasonId;
		//Reason reason;
		Incident incident;
		Person applicant = null;
		Customer customer = null;
		
		
		//get parameters from http request:
		String incidentIdStr = req.getParameter("id");
		
		String applicantPersonIdStr = req.getParameter("applicantPersonId");
		if(StringUtils.isNotBlank(applicantPersonIdStr)) {
			Integer applPsnId = new Integer(req.getParameter("applicantPersonId"));
			applicant = personDAO.findById(applPsnId);
		}
		
		String customerId = req.getParameter("customerId");
		if(StringUtils.isNotBlank(customerId)){
			customer = customerDao.findCustomerById(Integer.valueOf(customerId));
		}
		
		String callRecordId = req.getParameter("callRecordId");
		
		Timestamp applicationTime = Timestamp.valueOf(req.getParameter("applicationTime"));
		
		Integer creatorPsnId = new Integer(req.getParameter("creatorPersonId"));
		Person creator = personDAO.findById(creatorPsnId);
		
		Short sourceType = new Short(req.getParameter("sourceType"));
		String otherSourceType = req.getParameter("otherSourceType");
		String customSourceTypeId = req.getParameter("customSourceTypeId");
		
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		
		Short severityId = new Short(req.getParameter("severityId"));
		Severity severity = severityDAO.findById(severityId);
		
		///////////////////////////////////////////////////
		Effect effect = null;
		String effectIdStr = req.getParameter("effectId");
		if (StringUtils.isNotBlank(effectIdStr)) {
			Short effectId = new Short(effectIdStr);
			effect = effectDAO.findById(effectId);
		}else{
			effect = effectDAO.find();
		}
		
		Urgency urgency = null;
		String urgencyIdStr = req.getParameter("urgencyId");
		if (StringUtils.isNotBlank(urgencyIdStr)) {
			Short urgencyId = new Short(urgencyIdStr);
			urgency = urgencyDAO.findById(urgencyId);
		}else{
			urgency = urgencyDAO.find();
		}
		////////////////////////////////////////////////
		
		Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		
		Boolean sendMessage = new Boolean(req.getParameter("sendMessage"));

		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		
		String ciIdListStr = req.getParameter("ciIdList");
		String fileIndexList = req.getParameter("fileIndexList");
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
		String removedAttachmentIdListStr = req.getParameter("removedAttachmentIdList");
		
		String docIdListStr = req.getParameter("docIdList");
		
		CustomInfo customInfo = this.getCustomInfoFromRequest(req);
		
		if(StringUtils.isBlank(incidentIdStr)) {
			//create incident:
			incident = new Incident();
		} else {
			
			incident = incidentDAO.findById(new Long(incidentIdStr));
		}
		
		//获取复核角色信息
		String checkRoleIdStr = req.getParameter("checkRoleId");
		Role checkRole = null;
		if(null != checkRoleIdStr && !"".equals(checkRoleIdStr)) {
			checkRole = roleDAO.findById(new Integer(checkRoleIdStr));
		}
		incident.setCheckRole(checkRole);
		
		// set setReservedField s : private method
		this.setReservedField(incident, req);
		
		incident.setPlannedOutage(StringUtils.isNotBlank(req.getParameter("plannedOutage"))? true:false);
		
		String handlerIdListStr = req.getParameter("handlerPersonId");
		setHandlers(incident, handlerIdListStr);
		
		if(applicant != null) {
        	
        	incident.setApplicant(applicant);
        	incident.setLocation(applicant.getLocation());
        	
		} else if(customer != null) {
			
        	incident.setCustomer(customer);
        	
        	//use location of current person:
        	Integer locationId = (Integer)req.getSession().getAttribute("locationId");
        	incident.setLocation(locationDAO.findById(locationId));
        }
        
        if(StringUtils.isNotBlank(callRecordId)){
		
        	CallRecord callRecord = callRecordDao.findCallRecordById(Long.valueOf(callRecordId));
        	incident.getCallRecords().add(callRecord);
        }
        
		incident.setApplicationTime(applicationTime);
		
		incident.setCreator(creator);
		incident.setCreateTime(new Timestamp(System.currentTimeMillis()));
		
		//incident.setSource(source);
		incident.setSourceType(sourceType);
		if(sourceType == Incident.SOURCETYPE_CUSTOM) {
			
			CustomSourceType customSourceType = customSourceTypeDao.findById(new Short(customSourceTypeId));
			incident.setCustomSourceType(customSourceType);
			
		} else if((sourceType == Incident.SOURCETYPE_OTHER) && StringUtils.isNotBlank(otherSourceType)) {
			incident.setOtherSourceType(otherSourceType);
		}
		
		incident.setCategory(category);
		
		incident.setSeverity(severity);
		incident.setEffect(effect);
		incident.setUrgency(urgency);
//		incident.setPriority(new Short((short)(effect.getValue().shortValue() * urgency.getValue().shortValue())));
		String priority = req.getParameter("priority");
		if(EntityUtils.isNotEmpty(priority)) {
			incident.setPriority(new Short(priority));
		} else {
			incident.setPriority(null);
		}
		incident.setSendMessage(sendMessage);
		
		incident.setExpectFinishTime(expectFinishTime);
		incident.setSubject(subject);
		incident.setContent(content);
		
		String catType = req.getParameter("catType");
		if(StringUtils.isNotBlank(catType)){
			if(Integer.valueOf(catType) == 1){
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<Category> mainInfluenceSysList = catDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainInfluenceSystem().clear();
						incident.getMainInfluenceSystem().addAll(new HashSet<Category>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<Category> relaInfluenceSysList = catDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationInfluenceSystem().clear();
						incident.getRelationInfluenceSystem().addAll(new HashSet<Category>(relaInfluenceSysList));
					}
				}
			}else{
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<ConfigurationItem> mainInfluenceSysList = ciDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainCi().clear();
						incident.getMainCi().addAll(new HashSet<ConfigurationItem>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<ConfigurationItem> relaInfluenceSysList = ciDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationCi().clear();
						incident.getRelationCi().addAll(new HashSet<ConfigurationItem>(relaInfluenceSysList));
					}
				}
			}
		}
		
		String problemIds = "";
		Set<Problem> problemSet = new HashSet<Problem>();
		problemIds = req.getParameter("problemId");
		if (problemIds!= null && problemIds.length() > 0) {
			for(String problemId : problemIds.split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		String relatedProblemIds = req.getParameter("relatedProblemIds");
		if (relatedProblemIds!= null && relatedProblemIds.length() > 0) {
			for(String problemId : relatedProblemIds.replaceAll("#", "").split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		incident.setProblems(problemSet);
		
		if (ciIdListStr.length() > 0) {
			
			token = ciIdListStr.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().add(ci);
				}
				else
					logger.error("CI(id=" + token[i] + ") not found!");
			}
		}

		incident.setCustomInfo(customInfo);

		if(req.getParameter("fastSolution") != null) {
			//fast solution: just finish the request.
			
			solution = req.getParameter("solution");
			//reasonId = new Short(req.getParameter("reasonId"));
			
			//reason = reasonDAO.findById(reasonId);
			
			incident.setSolution(solution);
			//incident.setReason(reason);

		}

		String template = req.getParameter("template");
		if (StringUtils.isNotBlank(template)) {
			incident.setStatus(Incident.STATUS_TEMPLATE); // 模板
		}else{
			incident.setStatus(Incident.STATUS_DRAFT); // 草稿
		}
		
		//save to DB:
		if(StringUtils.isBlank(incidentIdStr)) {
			
			incidentDAO.insert(incident);
			
		} else {
			
			incidentDAO.update(incident);
		}
		
		//set request no:
		String requestNo = "";
	    
	    Session currentSession = incidentDAO.getHibernateTemplate().getSessionFactory().getCurrentSession();
	    
	    RequestNoGeneration requestNoGeneration = requestNoGenerationDao.findByModule(ItsmConstants.MODULE_INCIDENT);
	    
	    if(requestNoGeneration != null){
	    	
	    	String className = requestNoGeneration.getClassName();
	    	String methodName = requestNoGeneration.getMethodName();
			
			//invoke the method:
			try {			
				Class<?> c = Class.forName(className);
				Method m = c.getDeclaredMethod(methodName, HttpServletRequest.class, Session.class, Long.class);
				requestNo = m.invoke(null, req, currentSession, incident.getId()).toString();
				
			} catch(ClassNotFoundException e) {				
				logger.error("Class " + className + " not found!");

				requestNo = buildRequestNo(incident);
				
			} catch(NoSuchMethodException e) {				
				logger.error("Method " + methodName + " not found in class " + className + " !");

				requestNo = buildRequestNo(incident);
				
			} catch(Exception e) {				
				logger.error(Utils.getStackTrace(e));
				throw e;
			}
			
	    }else{
	    	
	    	requestNo = buildRequestNo(incident);
	    }
	    
	    incident.setRequestNo(requestNo);

	    //remove deleted attachments:
		if((removedAttachmentIdListStr != null) && (removedAttachmentIdListStr.length() > 0)) {
			
			String attachmentIdStr[] = removedAttachmentIdListStr.split(",");
			
			for(int i=0;i<attachmentIdStr.length;i++) {
				
				Long attachmentId = new Long(attachmentIdStr[i]);
				Document attachment = libraryDAO.findDocumentById(attachmentId);
				
				path = attachment.getPath1();
				if(attachment.getPath2() != null) {
					path += attachment.getPath2();
				
					if(attachment.getPath3() != null)
						path += attachment.getPath3();
				}
				
				File file = new File(path + System.getProperty("file.separator") + attachment.getFileName()); 
				file.delete();
				
				incident.getAttachments().remove(attachment);
				
				libraryDAO.deleteDocument(attachmentId);
				logger.info("Attachment(id=" + attachment.getId() + ", name=" + attachment.getFileName() +
								", originalName=" + attachment.getOriginalFileName() + ") was deleted.");
			}
		}
		
		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		 //add attachments:
		String attachmentPath = configProperties.getProperty("attachmentPath");
		
		path = attachmentPath + System.getProperty("file.separator") +
					"incident" + System.getProperty("file.separator") +
					"incident" + incident.getId() + System.getProperty("file.separator");

		path1 = path2 = path3 = null;
		
		int max_len = Document.MAX_PATH_LENGTH;
		if(path.length() > max_len) {
			String tmp = path;
			
			path1 = tmp.substring(0, max_len - 1);
			
			tmp = tmp.substring(max_len);
			if(tmp.length() > max_len) {
				
				path2 = tmp.substring(0, max_len - 1);
				
				tmp = tmp.substring(max_len);
				if(tmp.length() > max_len) {
					
					throw new ProcessException("Path_of_attachment_too_long");
					
				} else {
					
					path3 = tmp;
				}
				
			} else {
				
				path2 = tmp;
			}
			
		} else {
			
			path1 = path;
			
		}
		
		//get each file:
		if(fileIndexList.length() > 0) {
			
			token = fileIndexList.split(",");
			for (int i = 0; i < token.length; i++) {
				
				//get file from http request. covert it to MultipartHttpServletRequest first:
				multipartFile = multipartRequest.getFile("fileName" + token[i]);

				String docSubject = req.getParameter("subject" + token[i]);
				String author = req.getParameter("author" + token[i]);
				String number = req.getParameter("number" + token[i]);
				String directoryIdStr = req.getParameter("directoryId" + token[i]);
				String keywords = req.getParameter("keywords" + token[i]);
				String docDescription = req.getParameter("fileDescription" + token[i]);
	
				String fileName = multipartFile.getOriginalFilename();
				
				//create attachment object:
				Document attachment = new Document();
				attachment.setCreateTime(new Date(System.currentTimeMillis()));
				attachment.setOriginalFileName(fileName);
				attachment.setCreator(creator);
				
				//set type to incident:
				attachment.setType(Document.TYPE_INCIDENT);
				
				//set dummy to false, so it will be found in library
				attachment.setDummy(false);
				
				//use current time as new file name:
//				String newFileName = (new Long(System.currentTimeMillis())).toString();
				String newFileName = (new Long(System.currentTimeMillis())).toString() + "_" + i;
				attachment.setFileName(newFileName);
	
				if(docSubject.length() > 0)
					attachment.setSubject(docSubject);
				else
					attachment.setSubject(fileName);
				
				if(author.length() > 0)
					attachment.setAuthor(author);
				
				if(number.length() > 0)
					attachment.setNumber(number);
				
				if(keywords.length() > 0)
					attachment.setKeywords(keywords);
				
				if(docDescription.length() > 0)
					attachment.setDescription(docDescription);
				
				if(directoryIdStr.length() > 0) {
					
					Directory dir = libraryDAO.findDirectoryById(new Integer(directoryIdStr));
					attachment.setDirectory(dir);
					
				}
				
				//set path:
				attachment.setPath1(path1);
				attachment.setPath2(path2);
				attachment.setPath3(path3);
				
				//add it to task instance's attachments:
				incident.getAttachments().add(attachment);
				
				//upload the file to the server:
				try {
					
					File dir = new File(path);
					
					if(!dir.exists()) {
						
						dir.mkdirs();
					}
					
					File destinationFile = new File(path + System.getProperty("file.separator") + newFileName);
						
					multipartFile.transferTo(destinationFile);
					
				} catch(IOException e) {
					
					logger.error(e.getLocalizedMessage());
					
					throw e;
				}
		
			}
		
		}

		//add references:
		incident.getReferences().clear();
		if(docIdListStr.length() > 0) {
			
			token = docIdListStr.split(",");
			for(int i=0;i<token.length;i++) {
				
				Document doc = libraryDAO.findDocumentById(new Long(token[i]));
				
				incident.getReferences().add(doc);
			}
		}

		//update incident:
		incidentDAO.update(incident);
		
		if(incidentIdStr == null)
			logger.info("Incident(id=" + incident.getId() + ") was created.");
		else
			logger.info("Incident(id=" + incident.getId() + ") was updated.");
		
		//set return map:
		paraMap.put("windowLocation", "incident_action.do?operation=edit&id=" + incident.getId());
		
		String jobInstanceId = req.getParameter("jobInstanceId");// 是否为来自计划作业发起的工单
		if(EntityUtils.isNotEmpty(jobInstanceId)) {
			JobInstance jobInstance = jobInstanceDAO.get(new Long(jobInstanceId));
			if(jobInstance != null) {
				RequestByJob requestByJob = new RequestByJob();
				requestByJob.setJobInstance(jobInstance);
				requestByJob.setRequestNo(requestNo);
				requestByJob.setRequestType(RequestByJob.REQUEST_TYPE_INCIDENT);
				requestByJobDAO.save(requestByJob);
			}
		}
		
		return new ModelAndView(getSuccessView(), paraMap);
		
	}//draft()
	
	/*
	 * method to save template of incident
	 */
	public ModelAndView template(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String token[];
		ConfigurationItem ci;
		String path, path1, path2, path3;
		MultipartFile multipartFile;
		String solution;
		Incident incident;
		Person applicant = null;
		Customer customer = null;
		
		//get parameters from http request:
		String incidentIdStr = req.getParameter("id");
		
		String applicantPersonIdStr = req.getParameter("applicantPersonId");
		if(StringUtils.isNotBlank(applicantPersonIdStr)) {
			Integer applPsnId = new Integer(req.getParameter("applicantPersonId"));
			applicant = personDAO.findById(applPsnId);
		}
		
		String customerId = req.getParameter("customerId");
		if(StringUtils.isNotBlank(customerId)){
			customer = customerDao.findCustomerById(Integer.valueOf(customerId));
		}
		
		String callRecordId = req.getParameter("callRecordId");
		
		Timestamp applicationTime = Timestamp.valueOf(req.getParameter("applicationTime"));
		
		Integer creatorPsnId = new Integer(req.getParameter("creatorPersonId"));
		Person creator = personDAO.findById(creatorPsnId);
		
		Short sourceType = new Short(req.getParameter("sourceType"));
		String otherSourceType = req.getParameter("otherSourceType");
		String customSourceTypeId = req.getParameter("customSourceTypeId");
		
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		
		Short severityId = new Short(req.getParameter("severityId"));
		Severity severity = severityDAO.findById(severityId);
		
		///////////////////////////////////////////////////
		Effect effect = null;
		String effectIdStr = req.getParameter("effectId");
		if (StringUtils.isNotBlank(effectIdStr)) {
			Short effectId = new Short(effectIdStr);
			effect = effectDAO.findById(effectId);
		}else{
			effect = effectDAO.find();
		}
		
		Urgency urgency = null;
		String urgencyIdStr = req.getParameter("urgencyId");
		if (StringUtils.isNotBlank(urgencyIdStr)) {
			Short urgencyId = new Short(urgencyIdStr);
			urgency = urgencyDAO.findById(urgencyId);
		}else{
			urgency = urgencyDAO.find();
		}
		////////////////////////////////////////////////
		
		Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		
		Boolean sendMessage = new Boolean(req.getParameter("sendMessage"));

		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		
		String ciIdListStr = req.getParameter("ciIdList");
		String fileIndexList = req.getParameter("fileIndexList");
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
		String removedAttachmentIdListStr = req.getParameter("removedAttachmentIdList");
		
		String docIdListStr = req.getParameter("docIdList");
		
		CustomInfo customInfo = this.getCustomInfoFromRequest(req);
		
		if(incidentIdStr == null) {
			//create incident:
			incident = new Incident();
		} else {
			
			incident = incidentDAO.findById(new Long(incidentIdStr));
		}
		
		//获取复核角色信息
		String checkRoleIdStr = req.getParameter("checkRoleId");
		Role checkRole = null;
		if(null != checkRoleIdStr && !"".equals(checkRoleIdStr)) {
			checkRole = roleDAO.findById(new Integer(checkRoleIdStr));
		}
		incident.setCheckRole(checkRole);
		
		String handlerIdListStr = req.getParameter("handlerPersonId");
		setHandlers(incident, handlerIdListStr);
		
		if(applicant != null) {
        	
        	incident.setApplicant(applicant);
        	incident.setLocation(applicant.getLocation());
        	
		} else if(customer != null) {
			
        	incident.setCustomer(customer);
        	
        	//use location of current person:
        	Integer locationId = (Integer)req.getSession().getAttribute("locationId");
        	incident.setLocation(locationDAO.findById(locationId));
        }
        
        if(StringUtils.isNotBlank(callRecordId)){
		
        	CallRecord callRecord = callRecordDao.findCallRecordById(Long.valueOf(callRecordId));
        	incident.getCallRecords().add(callRecord);
        }
        
		incident.setApplicationTime(applicationTime);
		
		incident.setCreator(creator);
		incident.setCreateTime(new Timestamp(System.currentTimeMillis()));
		
		//incident.setSource(source);
		incident.setSourceType(sourceType);
		if(sourceType == Incident.SOURCETYPE_CUSTOM) {
			
			CustomSourceType customSourceType = customSourceTypeDao.findById(new Short(customSourceTypeId));
			incident.setCustomSourceType(customSourceType);
			
		} else if((sourceType == Incident.SOURCETYPE_OTHER) && StringUtils.isNotBlank(otherSourceType)) {
			incident.setOtherSourceType(otherSourceType);
		}
		
		incident.setCategory(category);
		
		incident.setSeverity(severity);
		incident.setEffect(effect);
		incident.setUrgency(urgency);
		// incident.setPriority(new Short((short)(effect.getValue().shortValue() * urgency.getValue().shortValue())));
		String priority = req.getParameter("priority");
		if(EntityUtils.isNotEmpty(priority)) {
			incident.setPriority(new Short(priority));
		} else {
			incident.setPriority(null);
		}
		incident.setSendMessage(sendMessage);
		
		incident.setExpectFinishTime(expectFinishTime);
		incident.setSubject(subject);
		incident.setContent(content);
		
		//主影响系统
		String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
		if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
			List<Category> mainInfluenceSysList = catDAO.findCatListByIDs(mainInfluenceSystemStr);
			if(mainInfluenceSysList.size()>0){
				incident.getMainInfluenceSystem().addAll(new HashSet<Category>(mainInfluenceSysList));
			}
		}
		//关联影响系统
		String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
		if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
			List<Category> relaInfluenceSysList = catDAO.findCatListByIDs(relaInfluenceSystemStr);
			if(relaInfluenceSysList.size()>0){
				incident.getRelationInfluenceSystem().addAll(new HashSet<Category>(relaInfluenceSysList));
			}
		}
		
		if (ciIdListStr.length() > 0) {
			
			token = ciIdListStr.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().add(ci);
				}
				else
					logger.error("CI(id=" + token[i] + ") not found!");
			}
		}

		incident.setCustomInfo(customInfo);

		if(req.getParameter("fastSolution") != null) {
			solution = req.getParameter("solution");
			incident.setSolution(solution);
		}

		incident.setStatus(new Short(Incident.STATUS_DRAFT));
		
		//save to DB:
		if(incidentIdStr == null) {
			incidentDAO.insert(incident);
		} else {
			incidentDAO.update(incident);
		}
		
		//set request no:
		String requestNo = "";
	    
	    Session currentSession = incidentDAO.getHibernateTemplate().getSessionFactory().getCurrentSession();
	    
	    RequestNoGeneration requestNoGeneration = requestNoGenerationDao.findByModule(ItsmConstants.MODULE_INCIDENT);
	    
	    if(requestNoGeneration != null){
	    	
	    	String className = requestNoGeneration.getClassName();
	    	String methodName = requestNoGeneration.getMethodName();
			
			//invoke the method:
			try {			
				Class<?> c = Class.forName(className);
				Method m = c.getDeclaredMethod(methodName, HttpServletRequest.class, Session.class, Long.class);
				requestNo = m.invoke(null, req, currentSession, incident.getId()).toString();
				
			} catch(ClassNotFoundException e) {				
				logger.error("Class " + className + " not found!");

				requestNo = buildRequestNo(incident);
				
			} catch(NoSuchMethodException e) {				
				logger.error("Method " + methodName + " not found in class " + className + " !");

				requestNo = buildRequestNo(incident);
				
			} catch(Exception e) {				
				logger.error(Utils.getStackTrace(e));
				throw e;
			}
			
	    }else{
	    	
	    	requestNo = buildRequestNo(incident);
	    }
	    
	    incident.setRequestNo(requestNo);

	    //remove deleted attachments:
		if((removedAttachmentIdListStr != null) && (removedAttachmentIdListStr.length() > 0)) {
			
			String attachmentIdStr[] = removedAttachmentIdListStr.split(",");
			
			for(int i=0;i<attachmentIdStr.length;i++) {
				
				Long attachmentId = new Long(attachmentIdStr[i]);
				Document attachment = libraryDAO.findDocumentById(attachmentId);
				
				path = attachment.getPath1();
				if(attachment.getPath2() != null) {
					path += attachment.getPath2();
				
					if(attachment.getPath3() != null)
						path += attachment.getPath3();
				}
				
				File file = new File(path + System.getProperty("file.separator") + attachment.getFileName()); 
				file.delete();
				
				incident.getAttachments().remove(attachment);
				
				libraryDAO.deleteDocument(attachmentId);
				logger.info("Attachment(id=" + attachment.getId() + ", name=" + attachment.getFileName() +
								", originalName=" + attachment.getOriginalFileName() + ") was deleted.");
			}
		}
		
		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		 //add attachments:
		String attachmentPath = configProperties.getProperty("attachmentPath");
		
		path = attachmentPath + System.getProperty("file.separator") +
					"incident" + System.getProperty("file.separator") +
					"incident" + incident.getId() + System.getProperty("file.separator");

		path1 = path2 = path3 = null;
		
		int max_len = Document.MAX_PATH_LENGTH;
		if(path.length() > max_len) {
			String tmp = path;
			
			path1 = tmp.substring(0, max_len - 1);
			
			tmp = tmp.substring(max_len);
			if(tmp.length() > max_len) {
				
				path2 = tmp.substring(0, max_len - 1);
				
				tmp = tmp.substring(max_len);
				if(tmp.length() > max_len) {
					
					throw new ProcessException("Path_of_attachment_too_long");
					
				} else {
					
					path3 = tmp;
				}
				
			} else {
				
				path2 = tmp;
			}
			
		} else {
			
			path1 = path;
			
		}
		
		//get each file:
		if(fileIndexList.length() > 0) {
			
			token = fileIndexList.split(",");
			for (int i = 0; i < token.length; i++) {
				
				//get file from http request. covert it to MultipartHttpServletRequest first:
				multipartFile = multipartRequest.getFile("fileName" + token[i]);

				String docSubject = req.getParameter("subject" + token[i]);
				String author = req.getParameter("author" + token[i]);
				String number = req.getParameter("number" + token[i]);
				String directoryIdStr = req.getParameter("directoryId" + token[i]);
				String keywords = req.getParameter("keywords" + token[i]);
				String docDescription = req.getParameter("fileDescription" + token[i]);
	
				String fileName = multipartFile.getOriginalFilename();
				
				//create attachment object:
				Document attachment = new Document();
				attachment.setCreateTime(new Date(System.currentTimeMillis()));
				attachment.setOriginalFileName(fileName);
				attachment.setCreator(creator);
				
				//set type to incident:
				attachment.setType(Document.TYPE_INCIDENT);
				
				//set dummy to false, so it will be found in library
				attachment.setDummy(false);
				
				//use current time as new file name:
//				String newFileName = (new Long(System.currentTimeMillis())).toString();
				String newFileName = (new Long(System.currentTimeMillis())).toString() + "_" + i;
				attachment.setFileName(newFileName);
	
				if(docSubject.length() > 0)
					attachment.setSubject(docSubject);
				else
					attachment.setSubject(fileName);
				
				if(author.length() > 0)
					attachment.setAuthor(author);
				
				if(number.length() > 0)
					attachment.setNumber(number);
				
				if(keywords.length() > 0)
					attachment.setKeywords(keywords);
				
				if(docDescription.length() > 0)
					attachment.setDescription(docDescription);
				
				if(directoryIdStr.length() > 0) {
					
					Directory dir = libraryDAO.findDirectoryById(new Integer(directoryIdStr));
					attachment.setDirectory(dir);
					
				}
				
				//set path:
				attachment.setPath1(path1);
				attachment.setPath2(path2);
				attachment.setPath3(path3);
				
				//add it to task instance's attachments:
				incident.getAttachments().add(attachment);
				
				//upload the file to the server:
				try {
					
					File dir = new File(path);
					
					if(!dir.exists()) {
						
						dir.mkdirs();
					}
					
					File destinationFile = new File(path + System.getProperty("file.separator") + newFileName);
						
					multipartFile.transferTo(destinationFile);
					
				} catch(IOException e) {
					
					logger.error(e.getLocalizedMessage());
					
					throw e;
				}
		
			}
		
		}

		//add references:
		incident.getReferences().clear();
		if(docIdListStr.length() > 0) {
			
			token = docIdListStr.split(",");
			for(int i=0;i<token.length;i++) {
				
				Document doc = libraryDAO.findDocumentById(new Long(token[i]));
				
				incident.getReferences().add(doc);
			}
		}

		//update incident:
		incidentDAO.update(incident);
		
		if(incidentIdStr == null){
			logger.info("Incident(id=" + incident.getId() + ") was created.");
		}else{
			logger.info("Incident(id=" + incident.getId() + ") was updated.");
		}
		
		//set return map:
		paraMap.put("windowLocation", "incident_action.do?operation=edit&id=" + incident.getId());
		
		String jobInstanceId = req.getParameter("jobInstanceId");// 是否为来自计划作业发起的工单
		if(EntityUtils.isNotEmpty(jobInstanceId)) {
			JobInstance jobInstance = jobInstanceDAO.get(new Long(jobInstanceId));
			if(jobInstance != null) {
				RequestByJob requestByJob = new RequestByJob();
				requestByJob.setJobInstance(jobInstance);
				requestByJob.setRequestNo(requestNo);
				requestByJob.setRequestType(RequestByJob.REQUEST_TYPE_INCIDENT);
				requestByJobDAO.save(requestByJob);
			}
		}
		
		return new ModelAndView(getSuccessView(), paraMap);
		
	}//template()
	
	/*
	 * method to abandon creation of change
	 */
	
	public ModelAndView abandon(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		
		//get parameters from http request:
		Long id = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(id);
		
		if(incident.getStatus().shortValue() == Incident.STATUS_DRAFT || incident.getStatus().shortValue() == Incident.STATUS_TEMPLATE) {
			
			List<Document> attachmentList = incident.getAttachments();
			for(int i=0;i<attachmentList.size();i++) {
				
				Document attachment = attachmentList.get(i);
				
				if(attachment != null) {
					String path = attachment.getPath1();
					if(attachment.getPath2() != null) {
						path += attachment.getPath2();
					
						if(attachment.getPath3() != null)
							path += attachment.getPath3();
					}
					
					File file = new File(path + System.getProperty("file.separator") + attachment.getFileName()); 
					file.delete();
					
					logger.info("Attachment(id=" + attachment.getId() + ", name=" + attachment.getFileName() +
									", originalName=" + attachment.getOriginalFileName() + ") was deleted.");
				}
			}

			//delete incident:
			incidentDAO.delete(id);
			logger.info("Incident(id=" + id + ") was abandoned.");
		}
		
		return new ModelAndView(getAbandonView(), paraMap);
		
	}//abandon()
	
	
	/*
	 * method to edit draft change
	 */
	public ModelAndView edit(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		paraMap = this.getMap(incident);
		
		//extra parameers:
		paraMap.put("now", new Timestamp(System.currentTimeMillis()));
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		String isTemplate = req.getParameter("isTemplate");
		if (StringUtils.isNotBlank(isTemplate)) {
			paraMap.put("isTemplate", isTemplate);
			
		}
		
		paraMap.put("customSourceTypeList", customSourceTypeDao.findAll());
		
//		List<HashMap<String, Object>> catList = catDAO.findAllForReport();
		List<HashMap<String, Object>> catList = catDAO.findAllForReportOptimize();
		paraMap.put("catList", catList);
		
		//影响系统
		if((incident.getMainInfluenceSystem() != null && incident.getMainInfluenceSystem().size() > 0)
				|| (incident.getRelationInfluenceSystem() != null && incident.getRelationInfluenceSystem().size() > 0)){
			String influenceSystemParentID = configProperties.getProperty("incident_influence_system_parent_id");
			List<HashMap<String, Object>> influenceSystemList = catDAO.findInfluenceSystem(influenceSystemParentID);
			paraMap.put("influenceSystemList", influenceSystemList);
			paraMap.put("catType", 1);
		}else{
			String influenceSystemParentID = configProperties.getProperty("incident_ci_parent_id");
			List<HashMap<String, Object>> influenceSystemList = ciDAO.findInfluenceSystem(influenceSystemParentID);
			paraMap.put("influenceSystemList", influenceSystemList);
			paraMap.put("catType", 2);
		}
		paraMap.put("roleList", 
			roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));

		
		return new ModelAndView(this.getEditView(), paraMap);
		
	}//edit()
	
	/*
	 * method to edit again change
	 */
	public ModelAndView editAgainForm(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		paraMap = this.getMap(incident);
		
		//extra parameers:
		paraMap.put("now", new Timestamp(System.currentTimeMillis()));
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		String isTemplate = req.getParameter("isTemplate");
		if (StringUtils.isNotBlank(isTemplate)) {
			paraMap.put("isTemplate", isTemplate);
			
		}
		
		paraMap.put("customSourceTypeList", customSourceTypeDao.findAll());
		
//		List<HashMap<String, Object>> catList = catDAO.findAllForReport();
		List<HashMap<String, Object>> catList = catDAO.findAllForReportOptimize();
		
		paraMap.put("catList", catList);
		
		
		//影响系统
		if((incident.getMainInfluenceSystem() != null && incident.getMainInfluenceSystem().size() > 0)
				|| (incident.getRelationInfluenceSystem() != null && incident.getRelationInfluenceSystem().size() > 0)){
			String influenceSystemParentID = configProperties.getProperty("incident_influence_system_parent_id");
			List<HashMap<String, Object>> influenceSystemList = catDAO.findInfluenceSystem(influenceSystemParentID);
			paraMap.put("influenceSystemList", influenceSystemList);
			paraMap.put("catType", 1);
		}else{
			String influenceSystemParentID = configProperties.getProperty("incident_ci_parent_id");
			List<HashMap<String, Object>> influenceSystemList = ciDAO.findInfluenceSystem(influenceSystemParentID);
			paraMap.put("influenceSystemList", influenceSystemList);
			paraMap.put("catType", 2);
		}
		
		paraMap.put("roleList", 
			roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));

		
		return new ModelAndView(this.getEditAgainView(), paraMap);
		
	}//editAgainForm()
	
	/*
	 * method to finish the incident
	 */
	public ModelAndView editAgain(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();

		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person handler = this.getCurrentPerson(session);
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		
		//类型、来源、申请时间、分类一、分类二、发生时间、影响系统、事件定性、计划停机、主题、内容、等
		
		// Short closeType = new Short(req.getParameter("closeType"));
		String solution = req.getParameter("solution");
		// String addToKnowledgeBase = req.getParameter("addToKnowledgeBase");
		 Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		 String severityIdStr = req.getParameter("severityId");
		 String effectIdStr = req.getParameter("effectId");
		 String urgencyIdStr = req.getParameter("urgencyId");
		 String priority = req.getParameter("priority");
		
		Short sourceType = new Short(req.getParameter("sourceType"));
		String otherSourceType = req.getParameter("otherSourceType");
		String customSourceTypeId = req.getParameter("customSourceTypeId");
		
		Timestamp applicationTime = Timestamp.valueOf(req.getParameter("applicationTime"));
		 
		//String commonSubject = req.getParameter("commonSubject");
		//String participatorsIds = req.getParameter("partIds");
		String conclusion = req.getParameter("conclusion");//结论
		
		//find from DB:
		Incident incident = incidentDAO.findById(incidentId);
		
		//更新类型时，超时通知时间变化
		Integer type = Integer.valueOf(req.getParameter("reservedInteger6"));
		if(!incident.getReservedInteger6().equals(type)){
			//事件类型值有修改，则更新sla
			if(configProperties == null) {
				configProperties = new Properties();
				configProperties.load(new FileInputStream(resource.getFile()));
			}
			String slaId = null;
			if(type.equals(Incident.TYPE_INCIDENT_FORM)){
				slaId = (String) configProperties.get("incident_sla_id");
			}else{
				slaId = (String) configProperties.get("request_sla_id");
			}
			
			Agreement agreement = agreementDAO.findById(Integer.valueOf(slaId));
			
			if(agreement != null) {
				incident.setSla(agreement);
			}
		}
		
		this.setReservedField(incident, req);
		
		//update custom info:
		CustomInfo customInfo = incident.getCustomInfo();
		customInfo = this.updateCustomInfoFromRequest(req, customInfo);
		incident.setCustomInfo(customInfo);
		
		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}

		//get and add attachments:
		//this.uploadAttachment(req, handler, incident);
		this.uploadAttachment(req, incident, handler);
		
		incident.setSubject(subject);
		incident.setContent(content);
		incident.setSolution(solution);
		incident.setConclusion(conclusion);
		//级别相关
		Effect effect = effectDAO.findById(new Short(effectIdStr));
		incident.setEffect(effect);
		
		Urgency urgency = urgencyDAO.findById(new Short(urgencyIdStr));
		incident.setUrgency(urgency);
		
		Severity severity = severityDAO.findById(new Short(severityIdStr));
		incident.setSeverity(severity);
		
		if(EntityUtils.isNotEmpty(priority)) {
			incident.setPriority(new Short(priority));
		} else {
			incident.setPriority(null);
		}
		
		incident.setExpectFinishTime(expectFinishTime);
		
		//来源
		incident.setSourceType(sourceType);
		if(sourceType == Incident.SOURCETYPE_CUSTOM) {	
			CustomSourceType customSourceType = customSourceTypeDao.findById(new Short(customSourceTypeId));
			incident.setCustomSourceType(customSourceType);
		} else if((sourceType == Incident.SOURCETYPE_OTHER) && StringUtils.isNotBlank(otherSourceType)) {
			incident.setOtherSourceType(otherSourceType);
		}
		
		//类别
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		incident.setCategory(category);
		
		//申请时间
		incident.setApplicationTime(applicationTime);
		
		String catType = req.getParameter("catType");
		if(StringUtils.isNotBlank(catType)){
			if(Integer.valueOf(catType) == 1){
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<Category> mainInfluenceSysList = catDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainInfluenceSystem().clear();
						incident.getMainInfluenceSystem().addAll(new HashSet<Category>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<Category> relaInfluenceSysList = catDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationInfluenceSystem().clear();
						incident.getRelationInfluenceSystem().addAll(new HashSet<Category>(relaInfluenceSysList));
					}
				}
			}else{
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<ConfigurationItem> mainInfluenceSysList = ciDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainCi().clear();
						incident.getMainCi().addAll(new HashSet<ConfigurationItem>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<ConfigurationItem> relaInfluenceSysList = ciDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationCi().clear();
						incident.getRelationCi().addAll(new HashSet<ConfigurationItem>(relaInfluenceSysList));
					}
				}
			}
		}
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		activity.setType(new Short(IncidentActivity.TYPE_EDIT_AGAIN));
		activity.setPerson(handler);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		incident.getActivities().add(activity);		
		
		incidentDAO.update(incident);
		
		logger.info("Incident(id=" + incidentId + ") was edit again by " + handler.getName());
		
		paraMap.put("parentReload", true);
		
		//for trigger
		paraMap.put("id", incidentId);
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_FINISH, incident, req);
		} catch (Exception e) {
			logger.error("finish menthod sendNotice error : "+Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
	
	}//editAgain()
	
	/*
	 * method to check incident info page
	 */
	
	public ModelAndView toCheckPage(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		paraMap = this.getMap(incident);
		
		//extra parameers:
		paraMap.put("now", new Timestamp(System.currentTimeMillis()));
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		paraMap.put("customSourceTypeList", customSourceTypeDao.findAll());
		
		paraMap.put("roleList", 
				roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));
		
		
		return new ModelAndView(this.getToCheckPageView(), paraMap);
		
	}
	
	
	/*
	 * method to get detail of incident
	 */
	public ModelAndView detail(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String, Object> paraMap;
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		paraMap = this.getMap(incident);
		paraMap.put("isParticipator", this.isParticipatorForIncident(req,incident));
		paraMap.put("readonly", req.getParameter("readonly"));
		
		String ss = "";
		//Set<TaskInstance> tis = incident.getTaskInstances();
		
		List<TaskInstance> tis = this.processDAO.getTaskInstances(incidentId, PermissionItem.RESOURCE_INCIDENT);
		
		short tag = 0;
		for (TaskInstance ti : tis ) {
			if (ti.getType().equals(tag)) {
				ss = (String)ti.getData().get("备注");
			}
		}
		
		//pi.get
		
		//check if custom export program configured:
		CustomExport customExport = customExportDao.findByModule(ItsmConstants.MODULE_INCIDENT);
		if(customExport != null) {
			paraMap.put("canCustomExport", true);
		}
		paraMap.put("taskInstanceContent", ss);
		return new ModelAndView(this.getDetailView(), paraMap);
		
	}//detail()
	
	/**
	 * 濞ｈ濮濩omment閹板繗顬�1锟�7.
	 * @param req
	 * @param res
	 * @return
	 */
	public ModelAndView addComment(HttpServletRequest req, HttpServletResponse res) {
		
		Long incidentId = new Long(req.getParameter("id"));
		Incident incident = incidentDAO.findById(incidentId);
		Person person = this.getPersonDAO().findById(Utils.getSessionPersion(req));
		com.telinkus.itsm.data.incident.Comment comment = new com.telinkus.itsm.data.incident.Comment();
		comment.setCotent(com.telinkus.itsm.util.StringUtils.getParameterByKey(req, "content"));
		comment.setTime(DateUtils.getNowTime());
		comment.setPerson(person);
		comment.setIncident(incident);
		incident.getComments().add(comment);
        incidentDAO.update(incident);
        String write = comment.getPerson().getName() + "&" + comment.getCotent() + "&" + DateUtils.dateToStr(comment.getTime());
        try {
        	res.setCharacterEncoding("utf-8"); 
			res.getWriter().print(write);
		} catch (IOException e) {
			return PlanUtil.sendMessage(e);
		}
		return null;
		
	}
	
	

	/**
	 * 閸掋倖鏌囪ぐ鎾冲閻劍鍩涢弰顖欑瑝閺勵垰鐫樻禍宥猲cident娑擃厾娈戦崣鍌欑瑢娴滅瘻articipator.
	 * @param req
	 * @param incident
	 * @return
	 */
	public Integer isParticipatorForIncident(HttpServletRequest req,Incident incident){
		Integer sessionPersonId = Utils.getSessionPersion(req);
		Set<Person> set = incident.getParticipators();
		Iterator<Person> itr = set.iterator();
		while(itr.hasNext()){
			if(itr.next().getId().equals(sessionPersonId)){
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * 找到组员的超时未关闭的工作列表
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listOverTimeNotClose(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		int i, pageSize, pageNo;
		String pageNoStr, pageSizeStr;
		long rowCount;
		
		List<Incident> incidents;
		List<Map<String, Object>> incidentList;
		
		HttpSession session = req.getSession();
		
		Integer personId = (Integer)session.getAttribute("personId");
		
		//get parameters fron config file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		//get parameters for multipages:
		pageNoStr = req.getParameter("pageNo");
		if (StringUtils.isBlank(pageNoStr)) {
			pageNo = 1;
		} else {
			pageNo = Integer.parseInt(pageNoStr);
		}

		pageSizeStr = req.getParameter("pageSize");
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		} else {
			pageSize = Integer.parseInt(configProperties.getProperty("pageSize"));
		}

		List<Integer> membersId = this.roleDAO.findAllMembersIdByLeaderId(personId);
		
		String range = req.getParameter("default");
		
		rowCount = this.incidentDAO.findOvertimeNoCloseCount(membersId, range);
		
		incidentList = new ArrayList<Map<String, Object>>();

		//initialize the return list:
		if (rowCount > 0) {
			//call DAO to search:
			incidents = incidentDAO.findOvertimeNoClose(membersId, range, pageSize*(pageNo-1), pageSize);
			for (i = 0; i < incidents.size(); i++) {
				Map<String, Object> incidentMap = this.getIncidentMap(incidents.get(i));
				incidentList.add(incidentMap);
			}
			
		}
		
		paraMap.put("incidentList", incidentList);
		
		//set extra parameters:
		paraMap.put("pageNo", new Integer(pageNo));
		paraMap.put("pageSize", new Integer(pageSize));
		paraMap.put("rowCount", new Long(rowCount));
		
		paraMap.put("operation", "listOverTimeNotClose");
		paraMap.put("default", range);
		
		return new ModelAndView(listOverTimeNotClose, paraMap);
		
	}//listOvertimeNoClose()
	
	/**
	 * 找到组员的工作列表
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listOfStaffWork(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		int i, pageSize, pageNo;
		String pageNoStr, pageSizeStr;
		long rowCount;
		
		List<Incident> incidents;
		List<Map<String, Object>> incidentList;
		
		HttpSession session = req.getSession();
		
		Integer personId = (Integer)session.getAttribute("personId");
		
		// String roleIdListStr = (String)session.getAttribute("roleId");
		// String[] token = roleIdListStr.split("#");
		// List<Role> roleList = new ArrayList<Role>();
		// for(i=0;i<token.length;i++) {
		// Role role = roleDAO.findById(new Integer(token[i]));
		// roleList.add(role);
		// }

		//get parameters fron config file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		//get parameters for multipages:
		pageNoStr = req.getParameter("pageNo");
		if (StringUtils.isBlank(pageNoStr)) {
			pageNo = 1;
		} else {
			pageNo = Integer.parseInt(pageNoStr);
		}

		pageSizeStr = req.getParameter("pageSize");
		if (StringUtils.isNotBlank(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		} else {
			pageSize = Integer.parseInt(configProperties.getProperty("pageSize"));
		}

		List<Integer> membersId = new ArrayList<Integer>();
		String memberId = req.getParameter("memberId");
		membersId.add(Integer.valueOf(memberId));
		
		List<Short> status = new ArrayList<Short>();
		
		String statusStr = req.getParameter("status");
		
		String[] token = statusStr.split(",");
		for (i = 0; i < token.length; i++) {
			status.add(new Short(token[i]));
		}
		
		rowCount = this.incidentDAO.findStaffIncidentCount(status, membersId);
		
		incidentList = new ArrayList<Map<String, Object>>();

		//initialize the return list:
		if (rowCount > 0) {
			//call DAO to search:
			incidents = incidentDAO.findStaffIncident(status, membersId, pageSize*(pageNo-1), pageSize);
			
			for (i = 0; i < incidents.size(); i++) {
				
				Map<String, Object> incidentMap = this.getIncidentMap(incidents.get(i));
				incidentList.add(incidentMap);
			}
			
		}
		
		paraMap.put("incidentList", incidentList);
		
		//set extra parameters:
		paraMap.put("pageNo", new Integer(pageNo));
		paraMap.put("pageSize", new Integer(pageSize));
		paraMap.put("rowCount", new Long(rowCount));
		paraMap.put("memberId", memberId);
		paraMap.put("status", statusStr);
		
		paraMap.put("operationValue", req.getParameter("operationValue"));
		
		return new ModelAndView(this.getListViewPage(), paraMap);
		
	}//listOfStaffWork()


	/*
	 * method to find templates TODO
	 */
	public ModelAndView listTemplate(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		List<Incident> templates = this.incidentDAO.findAllTemplate();
		
		paraMap.put("dataList", templates);
		
		return new ModelAndView(this.getListView(), paraMap);
		
	}//listTemplate()
	
	/*
	 * method to find My Draft
	 */
	public ModelAndView listMyDraft(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap;
		
		paraMap = search(req);
		
		return new ModelAndView(this.getListMyDraftView(), paraMap);
		
	}//find()
	
	/*
	 * method to find certain set of incidents
	 */
	public ModelAndView list(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap;
		paraMap = search(req);
		
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		int timeout = Integer.parseInt(configProperties.getProperty("timeout"));
		paraMap.put("timeout", timeout);
		String concern = req.getParameter("concern");
		if(null != concern && concern.equals("concern")){
			return new ModelAndView(this.getListConcernView(), paraMap);
		}
		return new ModelAndView(this.getListView(), paraMap);
		
	}//find()
	
	
	/*
	 * method to find certain set of incidents
	 */
	private Map<String,Object> search(HttpServletRequest req) throws Exception {

		Map<String,Object> paraMap = new HashMap<String,Object>();
		int i, pageSize, pageNo;
		String pageNoStr, rowCountStr, pageSizeStr;
		long rowCount = 0;
		HashMap<String,Object> fldMap;
		String paraNames[];
		Object values[];
		List<Incident> incidents = new ArrayList<Incident>();
		List<Map<String, Object>> incidentList = null;
		String roleIdListStr = "";
		String token[];
		List<Role> roleList;
		Integer personId;
		Set<Integer> interveneLcIdSet;
		StringBuffer countHql, searchHql;
		boolean isTrackList = false, isTrackAllList = false;
		
		interveneLcIdSet = new HashSet<Integer>();
			
		//get parameters from session:
		HttpSession session = req.getSession();
		roleIdListStr = (String)session.getAttribute("roleId");
		token = roleIdListStr.split("#");
		
		personId = (Integer)session.getAttribute("personId");
		Person person = personDAO.findById(personId);
		Location personLocation = person.getLocation();
		
		roleList = new ArrayList<Role>();
		for(i=0;i<token.length;i++) {
			
			Role role = roleDAO.findById(new Integer(token[i]));
			roleList.add(role);
		}

		//get parameters fron config file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		//get parameters for multipages:
		pageNoStr = req.getParameter("pageNo");
		if (pageNoStr == null) {
			pageNo = 1;
		} else {
			pageNo = Integer.parseInt(pageNoStr);
		}

		pageSizeStr = req.getParameter("pageSize");
		if (pageSizeStr != null) {
			pageSize = Integer.parseInt(pageSizeStr);
		} else {
			pageSize = Integer.parseInt(configProperties.getProperty("pageSize"));
		}
		
		String order = req.getParameter("order");
		String[] orderBy = req.getParameterValues("orderBy");
		
		//閹垫儳鍩岄幍锟介張澶庡厴楠炴煡顣╅惃鍕勾閸╃噦绱�1锟�7
		for(Workgroup workgroup : person.getWorkgroups()) {
			
			for(LocationDomain ld : workgroup.getLocationDomains()) {
				
				if(ld.getInterveneIncident()!= null && ld.getInterveneIncident()) {
					interveneLcIdSet.add(ld.getLocationId());
				}
			}
			
		}
			
		// make criteria:
		countHql = new StringBuffer();
		searchHql = new StringBuffer();
		fldMap = new HashMap<String, Object>();

		// private method
		this.makeSearchHql(req, countHql, searchHql, fldMap);
		
		//prepare parameters for query:
		Object flds[] = fldMap.keySet().toArray();
		paraNames = new String[flds.length];
		for (i = 0; i < flds.length; i++) {
			String fld = (String) flds[i];
			paraNames[i] = fld;
		}

		values = fldMap.values().toArray();
		
		/*//count first:
		rowCountStr = req.getParameter("rowCount");
		if (rowCountStr == null) {

			rowCount = ((Long) incidentDAO.findByNamedParam(countHql.toString(), 1, paraNames, values).get(0)).longValue();
			logger.debug("rowCount=" + rowCount);
			
		}else {
			rowCount = Long.parseLong(rowCountStr);
		}*/
		if(req.getParameter("trackList") != null && !req.getParameter("trackList").equals("")){
			isTrackList = true;
			paraMap.put("trackList", "true");
		}
		if(req.getParameter("trackAllList") != null && !req.getParameter("trackAllList").equals("")){
			isTrackAllList = true;
			paraMap.put("trackAllList", "true");
		}
		String para = req.getParameter("default");
		
		String sql = " FROM " +
				"	INCIDENT I, " +
				"	INCIDENT_HANDLER IH, " +
				"	INCIDENT_COMMENT IC, " +
				"	INCIDENT_WORKLOG IW " +
				" WHERE " +
				" I.STATUS not in ( " + Incident.STATUS_CLOSED + ", " + Incident.STATUS_REJECTED + " )" +
				"	 ANd I.INCIDENT_ID = IH.INCIDENT_ID " +
				" AND ( " +
				"	I.INCIDENT_ID = IC.INCIDENT_ID " +
				"	AND ( " +
				"		SELECT " +
				"			SYSDATE - 2 " +
				"		FROM " +
				"			DUAL " +
				"	) > ( " +
				"		SELECT " +
				"			MAX (ICC.COMMENT_TIME) " +
				"		FROM " +
				"			INCIDENT_COMMENT ICC WHERE ICC.INCIDENT_ID=I.INCIDENT_ID " +
				"	) " +
				" OR I.INCIDENT_ID = IW.INCIDENT_ID AND ( " +
				"		SELECT " +
				"			SYSDATE - 2 " +
				"		FROM " +
				"			DUAL " +
				"	) > ( " +
				"		SELECT " +
				"			MAX (IW.CREAT_TIME) " +
				"		FROM " +
				"			INCIDENT_WORKLOG IWW WHERE IWW.INCIDENT_ID=I.INCIDENT_ID " +
				"	) " +
				" ) ";
		if(isTrackList || isTrackAllList){
			String countSql =" SELECT " +
					"	COUNT(DISTINCT(i.INCIDENT_ID)) " + sql;
			if(isTrackList){
				countSql += " AND IH.PSN_ID = " + personId ;
			}
			if(StringUtils.isNotBlank(para)) {
				if(para.equals("incident")){
					countSql += " AND I.RSV_INTEGER6=" + Incident.TYPE_INCIDENT_FORM;
				}else if(para.equals("query")){
					countSql += " AND I.RSV_INTEGER6=" + Incident.TYPE_QUERY_FORM;
				}else if(para.equals("all")){
					
				}
			}
			rowCount = ((BigDecimal)incidentDAO.excuteSql(countSql, 0, 1).get(0)).longValue();
		}else{
			rowCount = ((Long) incidentDAO.findByNamedParam(countHql.toString(), 1, paraNames, values).get(0)).longValue();
		}
		logger.debug("rowCount=" + rowCount);
		
		incidentList = new ArrayList<Map<String, Object>>();

		//initialize the return list:
		if (rowCount > 0) {
			
			if(order != null && order.equals("desc")) {
				order = "desc";
			}else{
				order = " ";
			}
			
			//set order by:
			if(orderBy != null && orderBy.length > 0) {
				
				for(i=0;i<orderBy.length;i++) {
					
					if(i==0) {
						searchHql.append(" order by incident." + orderBy[i] + " " + order);
					} else {
						searchHql.append(", incident." + orderBy[i] + " " + order);
					}
				}
				
			} else {
				
				searchHql.append(" order by incident.applicationTime " + order);
			}
			
			//call DAO to search:
			if(isTrackList || isTrackAllList){
				String searchSql =" SELECT " +
						"	DISTINCT(I.INCIDENT_ID) " + sql;
				if(isTrackList){
					searchSql += " AND IH.PSN_ID = " + personId ;
				}
				if(StringUtils.isNotBlank(para)) {
					if(para.equals("incident")){
						searchSql += " AND I.RSV_INTEGER6=" + Incident.TYPE_INCIDENT_FORM;
					}else if(para.equals("query")){
						searchSql += " AND I.RSV_INTEGER6=" + Incident.TYPE_QUERY_FORM;
					}else if(para.equals("all")){
						
					}
				}
				//set order by:
				if(orderBy != null && orderBy.length > 0) {
					
					for(i=0;i<orderBy.length;i++) {
						
						if(i==0) {
							searchHql.append(" order by incident." + orderBy[i] + " " + order);
						} else {
							searchHql.append(", incident." + orderBy[i] + " " + order);
						}
					}
					
				} else {
					
					searchHql.append(" order by incident.applicationTime " + order);
				}
				List list = incidentDAO.excuteSql(searchSql, pageSize*(pageNo-1), pageSize);
				int incidentId = -1;
				for (i = 0; i < list.size(); i++) {
					if(pageNo == 1){
						incidentId = ((Number)list.get(i)).intValue();
					}else if(pageNo > 1){
						Object[] o = (Object[])list.get(i);
						incidentId = ((Number)o[0]).intValue();
					}
					incidents.add(incidentDAO.findById(Long.parseLong("" + incidentId)));
				}
			}else{
				incidents = incidentDAO.findByQuery(searchHql.toString(), paraNames, values, pageSize*(pageNo-1), pageSize);
			}
			for (i = 0; i < incidents.size(); i++) {
				
				Map<String,Object> incidentMap = new HashMap<String,Object>();
				Incident incident = (Incident)incidents.get(i);
				
				incidentMap.put("id", incident.getId());
				
				Location location = incident.getLocation();
				if(location != null)
					incidentMap.put("location", location.getName());
				
				incidentMap.put("requestNo", incident.getRequestNo());
				if(incident.getCategory().getParent() != null 
						&& incident.getCategory().getParent().getName().indexOf("应用软件") != -1){
					incidentMap.put("category", incident.getCategory().getName());
				}else{
					incidentMap.put("category", incident.getCategory().getPath());
				}
				incidentMap.put("subject", incident.getSubject());
				
				incidentMap.put("sourceType", incident.getSourceType());
				
				Person applicant = incident.getApplicant();
				Customer customer = incident.getCustomer();
				if(null != applicant){
					incidentMap.put("applicant", applicant.getName());
				} else if(null != customer) {
					incidentMap.put("applicant", customer.getName());
				}
				
				incidentMap.put("applicationTime", incident.getApplicationTime());
				if(incident.getCustomInfo()!=null){
					incidentMap.put("datetime6", incident.getCustomInfo().getDatetime6());
				}
				

				incidentMap.put("status", incident.getStatus());
				incidentMap.put("reservedInteger7", incident.getReservedInteger7());

				Timestamp promiseFinishTime = incident.getPromiseFinishTime();
				Timestamp expectFinishTime = incident.getExpectFinishTime();
				Timestamp finishTime = incident.getFinishTime();
				
				incidentMap.put("promiseFinishTime", promiseFinishTime);
				incidentMap.put("expectFinishTime", expectFinishTime);
				incidentMap.put("finishTime", finishTime);
				
				Set<Person> handlers = incident.getHandlers();
				List<Map<String,Object>> handlerList = new ArrayList<Map<String,Object>>();
				
				Iterator<Person> it = handlers.iterator();
				while(it.hasNext()) {
				
					Person handler = it.next();
					
					Map<String,Object> handlerMap = new HashMap<String,Object>();
					handlerMap.put("name", handler.getName());
					
					handlerList.add(handlerMap);
				}
				
				incidentMap.put("handlerList", handlerList);
				
				Set<Person> participators = incident.getParticipators();
				List<Map<String,Object>> participatorsList = new ArrayList<Map<String,Object>>();
				
				Iterator<Person> itp = participators.iterator();
				while(itp.hasNext()) {
				
					Person participator = itp.next();
					
					Map<String,Object> participatorMap = new HashMap<String,Object>();
					participatorMap.put("name", participator.getName());
					
					participatorsList.add(participatorMap);
				}
				
				incidentMap.put("participatorsList", participatorsList);
				
				//check if it can be intervened by current person:
				if(location != null) {
					//婵″倹鐏夐弰顖氱秼閸撳秳姹夐幍锟介崷銊ユ勾閸╃噦绱濋幋鏍拷鍛Ц瑜版挸澧犳禍鐑樺鐏炵偛浼愭担婊呯矋閼宠棄鍏辨０鍕畱閸︽澘鐓欓敍宀冿拷灞肩瑬娴犳牕褰叉禒銉ュ瀻闁板秵顒濈猾璇插焼閿涘苯鍨崣顖欎簰楠炴煡顣�1锟�7
					if((location.getId().equals(personLocation.getId()) ||
							interveneLcIdSet.contains(location.getId())) &&
							Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT, 
									new Integer(incident.getCategory().getId()), PermissionItem.OPERATION_ASSIGN)) {
						
						incidentMap.put("canIntervene", true);
					
					} else {
					
						incidentMap.put("canIntervene", false);
					}
					
					if(incident.getHandlers().contains(person) || incident.getParticipators().contains(person)){
						
						incidentMap.put("canHandle", true);
						
					} else {
						
						incidentMap.put("canHandle", false);
					}
					
				}else{
					incidentMap.put("canIntervene", false);
					if(incident.getHandlers().contains(person) || incident.getParticipators().contains(person)){
						incidentMap.put("canHandle", true);
					} else {
						incidentMap.put("canHandle", false);
					}
				}
				
				// 是否拥有重新编辑事件单的权限
				boolean canEdit = Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(incident.getCategory().getId()), PermissionItem.OPERATION_EDIT);
				incidentMap.put("canEdit", canEdit);
				
				//backgroundColor
				List<BackgroundColor> bgColorList = backgroundColorDao.findAllObject();
				
				if(bgColorList != null && bgColorList.size() > 0){
					
					int type = bgColorList.get(0).getFieldType();
					
					if(type == BackgroundColor.BG_COLOR_STATUS_TYPE){
						
						if(incident.getStatus() != null){
							short status = incident.getStatus();
							
							for(BackgroundColor bgColor : bgColorList){
								if(status == bgColor.getIncidentStatus()){
									incidentMap.put("color", bgColor.getColor());
									break;
								}
							}
						}	
						
					}else if(type == BackgroundColor.BG_COLOR_SEVERITY_TYPE){
						
						if(incident.getSeverity() != null){
							short severityId = incident.getSeverity().getId();
							
							for(BackgroundColor bgColor : bgColorList){
								if(severityId == bgColor.getFieldId()){
									incidentMap.put("color", bgColor.getColor());
									break;
								}
							}
						}	
						
					}else if(type == BackgroundColor.BG_COLOR_URGENCY_TYPE){
						
						short urgencyId = incident.getUrgency().getId();
						
						for(BackgroundColor bgColor : bgColorList){
							if(urgencyId == bgColor.getFieldId()){
								incidentMap.put("color", bgColor.getColor());
								break;
							}
						}
						
					}else if(type == BackgroundColor.BG_COLOR_EFFECT_TYPE){
						
						short effectId = incident.getEffect().getId();
						
						for(BackgroundColor bgColor : bgColorList){
							if(effectId == bgColor.getFieldId()){
								incidentMap.put("color", bgColor.getColor());
								break;
							}
						}
						
					}else if(type == BackgroundColor.BG_COLOR_PRIORITY_TYPE){
						
						short priorityValue = incident.getPriority();
						
						for(BackgroundColor bgColor : bgColorList){
							if(priorityValue >= bgColor.getPriorityFloor() && priorityValue <= bgColor.getPriorityCeiling()){
								incidentMap.put("color", bgColor.getColor());
								break;
							}
						}
						
					}
					
				}
				//当前用户是否可以进行复核操作
				if(incident.getCheckRole() != null && roleList.contains(incident.getCheckRole())) {
					incidentMap.put("canCheck", true);
				} else {
					incidentMap.put("canCheck", false);
				}
				
				incidentMap.put("reservedString1", incident.getReservedString1());
				incidentMap.put("reservedString2", incident.getReservedString2());
				incidentMap.put("reservedString3", incident.getReservedString3());
				incidentMap.put("reservedString4", incident.getReservedString4());
				incidentMap.put("reservedString5", incident.getReservedString5());
				incidentMap.put("reservedString6", incident.getReservedString6());
				incidentMap.put("reservedString7", incident.getReservedString7());
				incidentMap.put("reservedString8", incident.getReservedString8());
				incidentMap.put("reservedString9", incident.getReservedString9());
				incidentMap.put("reservedString10", incident.getReservedString10());
				
				incidentList.add(incidentMap);
			}
		}
		
		paraMap.put("incidentList", incidentList);
		
		//set if the user has permission of intervene(assign, reject):
		/* removed, 2008-11-1
		if(Utils.hasPermission(roleList, new Short(PermissionItem.RESOURCE_INCIDENT), PermissionItem.OPERATION_ASSIGN) ||
				Utils.hasPermission(roleList, new Short(PermissionItem.RESOURCE_INCIDENT), PermissionItem.OPERATION_REJECT)) {
			
			paraMap.put("canIntervene", new Boolean(true));
			
		} else {
			
			paraMap.put("canIntervene", new Boolean(false));
		}
		*/
		
		//copy request parameter:
		
		paraMap.put("default", req.getParameter("default"));
		paraMap.put("draft", new Boolean(req.getParameter("draft")));
		
		paraMap.put("requestNo", req.getParameter("requestNo"));
		paraMap.put("sourceType", req.getParameter("sourceType"));
		paraMap.put("applicantPersonId", req.getParameter("applicantPersonId"));
		paraMap.put("organizationId", req.getParameter("organizationId"));
		paraMap.put("creatorPersonId", req.getParameter("creatorPersonId"));
		paraMap.put("handlerPersonId", req.getParameter("handlerPersonId"));
		paraMap.put("participatorPersonId", req.getParameter("participatorPersonId"));
		//paraMap.put("solution", req.getParameter("solution"));
		paraMap.put("beginApplicationTime", req.getParameter("beginApplicationTime"));
		paraMap.put("endApplicationTime", req.getParameter("endApplicationTime"));
		paraMap.put("beginFinishTime", req.getParameter("beginFinishTime"));
		paraMap.put("endFinishTime", req.getParameter("endFinishTime"));
		paraMap.put("beginPFTime", req.getParameter("beginPFTime"));
		paraMap.put("endPFTime", req.getParameter("endPFTime"));
		paraMap.put("plannedOutage", req.getParameter("plannedOutage"));
		paraMap.put("reservedString9", req.getParameter("reservedString9"));
		paraMap.put("feedbackerPersonId", req.getParameter("feedbackerPersonId"));
		paraMap.put("priority", req.getParameter("priority"));
		
		paraMap.put("customerId", req.getParameter("customerId"));
		
		paraMap.put("locationCode", req.getParameter("locationCode"));
		if (null != req.getParameter("subLocationIncluded")) {
			paraMap.put("subLocationIncluded", true);
		}

		paraMap.put("categoryCode", req.getParameter("categoryCode"));
		if(null != req.getParameter("subCatIncluded")) {
			paraMap.put("subCatIncluded", true);
		}
		
		paraMap.put("subject", req.getParameter("subject"));
		paraMap.put("content", req.getParameter("content"));
		
		paraMap.put("severityId", req.getParameter("severityId"));
		paraMap.put("effectId", req.getParameter("effectId"));
		paraMap.put("urgencyId", req.getParameter("urgencyId"));
		paraMap.put("reasonId", req.getParameter("reasonId"));
		paraMap.put("closeType", req.getParameter("closeType"));
		paraMap.put("status", req.getParameter("status"));
		
		String finishStatus = req.getParameter("finishStatus");
		if((finishStatus != null) && (finishStatus.length() > 0)) {
			paraMap.put("finishStatus", "useForFinish");
		}
		
		paraMap.put("integer1", req.getParameter("integer1"));
		paraMap.put("interger1_op", req.getParameter("integer1_op"));
		paraMap.put("integer2", req.getParameter("integer2"));
		paraMap.put("interger2_op", req.getParameter("integer2_op"));
		paraMap.put("integer3", req.getParameter("integer3"));
		paraMap.put("interger3_op", req.getParameter("integer3_op"));
		paraMap.put("integer4", req.getParameter("integer4"));
		paraMap.put("interger4_op", req.getParameter("integer4_op"));
		paraMap.put("integer5", req.getParameter("integer5"));
		paraMap.put("interger5_op", req.getParameter("integer5_op"));
		paraMap.put("integer6", req.getParameter("integer6"));
		paraMap.put("interger6_op", req.getParameter("integer6_op"));
		paraMap.put("integer7", req.getParameter("integer7"));
		paraMap.put("interger7_op", req.getParameter("integer7_op"));
		paraMap.put("integer8", req.getParameter("integer8"));
		paraMap.put("interger8_op", req.getParameter("integer8_op"));
		paraMap.put("integer9", req.getParameter("integer9"));
		paraMap.put("interger9_op", req.getParameter("integer9_op"));
		paraMap.put("integer10", req.getParameter("integer10"));
		paraMap.put("interger10_op", req.getParameter("integer10_op"));

		paraMap.put("double1", req.getParameter("double1"));
		paraMap.put("double1_op", req.getParameter("double1_op"));
		paraMap.put("double2", req.getParameter("double2"));
		paraMap.put("double2_op", req.getParameter("double2_op"));
		paraMap.put("double3", req.getParameter("double3"));
		paraMap.put("double3_op", req.getParameter("double3_op"));
		paraMap.put("double4", req.getParameter("double4"));
		paraMap.put("double4_op", req.getParameter("double4_op"));
		paraMap.put("double5", req.getParameter("double5"));
		paraMap.put("double5_op", req.getParameter("double5_op"));
		paraMap.put("double6", req.getParameter("double6"));
		paraMap.put("double6_op", req.getParameter("double6_op"));
		paraMap.put("double7", req.getParameter("double7"));
		paraMap.put("double7_op", req.getParameter("double7_op"));
		paraMap.put("double8", req.getParameter("double8"));
		paraMap.put("double8_op", req.getParameter("double8_op"));
		paraMap.put("double9", req.getParameter("double9"));
		paraMap.put("double9_op", req.getParameter("double9_op"));
		paraMap.put("double10", req.getParameter("double10"));
		paraMap.put("double10_op", req.getParameter("double10_op"));

		paraMap.put("boolean1", req.getParameter("boolean1"));
		paraMap.put("boolean2", req.getParameter("boolean2"));
		paraMap.put("boolean3", req.getParameter("boolean3"));
		paraMap.put("boolean4", req.getParameter("boolean4"));
		paraMap.put("boolean5", req.getParameter("boolean5"));
		paraMap.put("boolean6", req.getParameter("boolean6"));
		paraMap.put("boolean7", req.getParameter("boolean7"));
		paraMap.put("boolean8", req.getParameter("boolean8"));
		paraMap.put("boolean9", req.getParameter("boolean9"));
		paraMap.put("boolean10", req.getParameter("boolean10"));
		
		paraMap.put("datetime1", req.getParameter("datetime1"));
		paraMap.put("datetime1_op", req.getParameter("datetime1_op"));
		paraMap.put("datetime2", req.getParameter("datetime2"));
		paraMap.put("datetime2_op", req.getParameter("datetime2_op"));
		paraMap.put("datetime3", req.getParameter("datetime3"));
		paraMap.put("datetime3_op", req.getParameter("datetime3_op"));
		paraMap.put("datetime4", req.getParameter("datetime4"));
		paraMap.put("datetime4_op", req.getParameter("datetime4_op"));
		paraMap.put("datetime5", req.getParameter("datetime5"));
		paraMap.put("datetime5_op", req.getParameter("datetime5_op"));
		paraMap.put("datetime6", req.getParameter("datetime6"));
		paraMap.put("datetime6_op", req.getParameter("datetime6_op"));
		paraMap.put("datetime7", req.getParameter("datetime7"));
		paraMap.put("datetime7_op", req.getParameter("datetime7_op"));
		paraMap.put("datetime8", req.getParameter("datetime8"));
		paraMap.put("datetime8_op", req.getParameter("datetime8_op"));
		paraMap.put("datetime9", req.getParameter("datetime9"));
		paraMap.put("datetime9_op", req.getParameter("datetime9_op"));
		paraMap.put("datetime10", req.getParameter("datetime10"));
		paraMap.put("datetime10_op", req.getParameter("datetime10_op"));
		
		paraMap.put("date1", req.getParameter("date1"));
		paraMap.put("date1_op", req.getParameter("date1_op"));
		paraMap.put("date2", req.getParameter("date2"));
		paraMap.put("date2_op", req.getParameter("date2_op"));
		paraMap.put("date3", req.getParameter("date3"));
		paraMap.put("date3_op", req.getParameter("date3_op"));
		paraMap.put("date4", req.getParameter("date4"));
		paraMap.put("date4_op", req.getParameter("date4_op"));
		paraMap.put("date5", req.getParameter("date5"));
		paraMap.put("date5_op", req.getParameter("date5_op"));
		paraMap.put("date6", req.getParameter("date6"));
		paraMap.put("date6_op", req.getParameter("date6_op"));
		paraMap.put("date7", req.getParameter("date7"));
		paraMap.put("date7_op", req.getParameter("date7_op"));
		paraMap.put("date8", req.getParameter("date8"));
		paraMap.put("date8_op", req.getParameter("date8_op"));
		paraMap.put("date9", req.getParameter("date9"));
		paraMap.put("date9_op", req.getParameter("date9_op"));
		paraMap.put("date10", req.getParameter("date10"));
		paraMap.put("date10_op", req.getParameter("date10_op"));
		
		paraMap.put("shortstring1", req.getParameter("shortstring1"));
		paraMap.put("shortstring2", req.getParameter("shortstring2"));
		paraMap.put("shortstring3", req.getParameter("shortstring3"));
		paraMap.put("shortstring4", req.getParameter("shortstring4"));
		paraMap.put("shortstring5", req.getParameter("shortstring5"));
		paraMap.put("shortstring6", req.getParameter("shortstring6"));
		paraMap.put("shortstring7", req.getParameter("shortstring7"));
		paraMap.put("shortstring8", req.getParameter("shortstring8"));
		paraMap.put("shortstring9", req.getParameter("shortstring9"));
		paraMap.put("shortstring10", req.getParameter("shortstring10"));
		paraMap.put("shortstring11", req.getParameter("shortstring11"));
		paraMap.put("shortstring12", req.getParameter("shortstring12"));
		paraMap.put("shortstring13", req.getParameter("shortstring13"));
		paraMap.put("shortstring14", req.getParameter("shortstring14"));
		paraMap.put("shortstring15", req.getParameter("shortstring15"));
		paraMap.put("shortstring16", req.getParameter("shortstring16"));
		paraMap.put("shortstring17", req.getParameter("shortstring17"));
		paraMap.put("shortstring18", req.getParameter("shortstring18"));
		paraMap.put("shortstring19", req.getParameter("shortstring19"));
		paraMap.put("shortstring20", req.getParameter("shortstring20"));

		paraMap.put("mediumstring1", req.getParameter("mediumstring1"));
		paraMap.put("mediumstring2", req.getParameter("mediumstring2"));
		paraMap.put("mediumstring3", req.getParameter("mediumstring3"));
		paraMap.put("mediumstring4", req.getParameter("mediumstring4"));
		paraMap.put("mediumstring5", req.getParameter("mediumstring5"));
		paraMap.put("mediumstring6", req.getParameter("mediumstring6"));
		paraMap.put("mediumstring7", req.getParameter("mediumstring7"));
		paraMap.put("mediumstring8", req.getParameter("mediumstring8"));
		paraMap.put("mediumstring9", req.getParameter("mediumstring9"));
		paraMap.put("mediumstring10", req.getParameter("mediumstring10"));
		
		paraMap.put("longstring1", req.getParameter("longstring1"));
		paraMap.put("longstring2", req.getParameter("longstring2"));
		paraMap.put("longstring3", req.getParameter("longstring3"));
		paraMap.put("longstring4", req.getParameter("longstring4"));
		paraMap.put("longstring5", req.getParameter("longstring5"));

		paraMap.put("treevalue1", req.getParameter("treevalue1"));
		paraMap.put("treevalue2", req.getParameter("treevalue2"));
		paraMap.put("treevalue3", req.getParameter("treevalue3"));
		paraMap.put("treevalue4", req.getParameter("treevalue4"));
		paraMap.put("treevalue5", req.getParameter("treevalue5"));
		paraMap.put("treevalue6", req.getParameter("treevalue6"));
		paraMap.put("treevalue7", req.getParameter("treevalue7"));
		paraMap.put("treevalue8", req.getParameter("treevalue8"));
		paraMap.put("treevalue9", req.getParameter("treevalue9"));
		paraMap.put("treevalue10", req.getParameter("treevalue10"));
		
		paraMap.put("codevalue1", req.getParameter("codevalue1"));
		paraMap.put("codevalue2", req.getParameter("codevalue2"));
		paraMap.put("codevalue3", req.getParameter("codevalue3"));
		paraMap.put("codevalue4", req.getParameter("codevalue4"));
		paraMap.put("codevalue5", req.getParameter("codevalue5"));
		paraMap.put("codevalue6", req.getParameter("codevalue6"));
		paraMap.put("codevalue7", req.getParameter("codevalue7"));
		paraMap.put("codevalue8", req.getParameter("codevalue8"));
		paraMap.put("codevalue9", req.getParameter("codevalue9"));
		paraMap.put("codevalue10", req.getParameter("codevalue10"));
		paraMap.put("codevalue11", req.getParameter("codevalue11"));
		paraMap.put("codevalue12", req.getParameter("codevalue12"));
		paraMap.put("codevalue13", req.getParameter("codevalue13"));
		paraMap.put("codevalue14", req.getParameter("codevalue14"));
		paraMap.put("codevalue15", req.getParameter("codevalue15"));
		paraMap.put("codevalue16", req.getParameter("codevalue16"));
		paraMap.put("codevalue17", req.getParameter("codevalue17"));
		paraMap.put("codevalue18", req.getParameter("codevalue18"));
		paraMap.put("codevalue19", req.getParameter("codevalue19"));
		paraMap.put("codevalue20", req.getParameter("codevalue20"));
		
		paraMap.put("location1Id", req.getParameter("location1Id"));
		paraMap.put("location2Id", req.getParameter("location2Id"));
		paraMap.put("location3Id", req.getParameter("location3Id"));
		paraMap.put("location4Id", req.getParameter("location4Id"));
		paraMap.put("location5Id", req.getParameter("location5Id"));
		
		paraMap.put("organization1Id", req.getParameter("organization1Id"));
		paraMap.put("organization2Id", req.getParameter("organization2Id"));
		paraMap.put("organization3Id", req.getParameter("organization3Id"));
		paraMap.put("organization4Id", req.getParameter("organization4Id"));
		paraMap.put("organization5Id", req.getParameter("organization5Id"));
		
		paraMap.put("person1Id", req.getParameter("person1Id"));
		paraMap.put("person2Id", req.getParameter("person2Id"));
		paraMap.put("person3Id", req.getParameter("person3Id"));
		paraMap.put("person4Id", req.getParameter("person4Id"));
		paraMap.put("person5Id", req.getParameter("person5Id"));
		
		paraMap.put("reservedString1", req.getParameter("reservedString1"));
		paraMap.put("reservedString2", req.getParameter("reservedString2"));
		paraMap.put("reservedString3", req.getParameter("reservedString3"));
		paraMap.put("reservedString4", req.getParameter("reservedString4"));
		paraMap.put("reservedString5", req.getParameter("reservedString5"));
		paraMap.put("reservedString6", req.getParameter("reservedString6"));
		paraMap.put("reservedString7", req.getParameter("reservedString7"));
		paraMap.put("reservedString8", req.getParameter("reservedString8"));
		paraMap.put("reservedString9", req.getParameter("reservedString9"));
		paraMap.put("reservedString10", req.getParameter("reservedString10"));
		
		paraMap.put("reservedInteger1", req.getParameter("reservedInteger1"));
		paraMap.put("reservedInteger2", req.getParameter("reservedInteger2"));
		paraMap.put("reservedInteger3", req.getParameter("reservedInteger3"));
		paraMap.put("reservedInteger4", req.getParameter("reservedInteger4"));
		paraMap.put("reservedInteger5", req.getParameter("reservedInteger5"));
		paraMap.put("reservedInteger6", req.getParameter("reservedInteger6"));
		paraMap.put("reservedInteger7", req.getParameter("reservedInteger7"));
		paraMap.put("reservedInteger8", req.getParameter("reservedInteger8"));
		paraMap.put("reservedInteger9", req.getParameter("reservedInteger9"));
		paraMap.put("reservedInteger10", req.getParameter("reservedInteger10"));
		
		//order by:
		paraMap.put("order", order);
		
		if(orderBy != null && orderBy.length > 0) {
			String orderByStr = "";
			for(i=0;i<orderBy.length;i++) {
				
				if(i == 0)
					orderByStr = orderBy[i];
				else
					orderByStr += "," + orderBy[i];
			}
				
			paraMap.put("orderBy", orderByStr);
		}
		
		//set extra parameters:
		paraMap.put("pageNo", new Integer(pageNo));
		paraMap.put("pageSize", new Integer(pageSize));
		paraMap.put("rowCount", new Long(rowCount));
		
		paraMap.put("categoryId", req.getParameter("categoryId"));
		paraMap.put("operationValue", req.getParameter("operationValue"));
		paraMap.put("concern", req.getParameter("concern"));
		
		return paraMap;
		
	}//search()
	
	

	/*
	 * method to make criteria string by HTTP request
	 */
	private void makeSearchHql(HttpServletRequest req, StringBuffer countHql,
								StringBuffer searchHql, HashMap<String, Object> fldMap) {
		
		String criteriaStr;
		String roleIdListStr = "";
		String token[];
		List<Role> roleList;
		boolean subCatIncluded, subLocationIncluded;
		String para;
		Boolean draft;
		Integer personId;
		int i;
		
		//get parameters from session:
		HttpSession session = req.getSession();
		roleIdListStr = (String)session.getAttribute("roleId");
		token = roleIdListStr.split("#");
		
		personId = (Integer)session.getAttribute("personId");
		Person person = personDAO.findById(personId);
		Location personLocation = person.getLocation();
		
		roleList = new ArrayList<Role>();
		for(i=0;i<token.length;i++) {
			
			Role role = roleDAO.findById(new Integer(token[i]));
			roleList.add(role);
		}
		
		//get parameters from HTTP request:
		String locationCode = req.getParameter("locationCode");
		if(null != req.getParameter("subLocationIncluded")) {
			subLocationIncluded = true;
		} else {
			subLocationIncluded = false;
		}
		
		String requestNo = req.getParameter("requestNo");
		String sourceTypeStr = req.getParameter("sourceType");
		String customSourceTypeIdStr = req.getParameter("customSourceTypeId");
		String applicantPersonIdStr = req.getParameter("applicantPersonId");
		String organizationIdStr = req.getParameter("organizationId");
		String creatorPersonIdStr = req.getParameter("creatorPersonId");
		String handlerPersonIdStr = req.getParameter("handlerPersonId");
		String participatorPersonIdStr = req.getParameter("participatorPersonId");
		//String solution = req.getParameter("solution");
		String beginApplicationTimeStr = req.getParameter("beginApplicationTime");
		String endApplicationTimeStr = req.getParameter("endApplicationTime");
		String beginFinishTimeStr = req.getParameter("beginFinishTime");
		String endFinishTimeStr = req.getParameter("endFinishTime");
		String beginPFTimeStr = req.getParameter("beginPFTime");
		String endPFTimeStr = req.getParameter("endPFTime");
		
		String customerIdStr = req.getParameter("customerId");
		
		String categoryId = req.getParameter("categoryId"); 
		String operationValue = req.getParameter("operationValue");
		
		String severityIdStr = req.getParameter("severityId");
		String effectIdStr = req.getParameter("effectId");
		String urgencyIdStr = req.getParameter("urgencyId");
		String reasonIdStr = req.getParameter("reasonId");
		String closeTypeStr = req.getParameter("closeType");
		String statusStr = req.getParameter("status");
		
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		String relatedTicketHandler = req.getParameter("relatedTicketHandler");
		//add by yangrq 
		String finishStatus = req.getParameter("finishStatus");
		
		draft = new Boolean(req.getParameter("draft"));

		String categoryCode = req.getParameter("categoryCode");
		if (null != req.getParameter("subCatIncluded")) {
			subCatIncluded = true;
		} else {
			subCatIncluded = false;
		}
		
		//回访人
		String feedbackerPersonIdStr = req.getParameter("feedbackerPersonId");
		
		//create criteria using parameters got:
		criteriaStr = "";
		
		if(draft.booleanValue()) {
			
			criteriaStr += " (incident.status = :statusDraft and incident.creator.id = :personId) or incident.status = :statusTemplate";
			fldMap.put("statusDraft", new Short(Incident.STATUS_DRAFT));
			fldMap.put("statusTemplate", new Short(Incident.STATUS_TEMPLATE));
			fldMap.put("personId", personId);
			para = req.getParameter("default");
			if(StringUtils.isNotBlank(para)) {
				if(para.equals("incident")){
					fldMap.put("reservedInteger6", Incident.TYPE_INCIDENT_FORM);
					criteriaStr += " and incident.reservedInteger6 =:reservedInteger6";
				}else if(para.equals("query")){
					fldMap.put("reservedInteger6", Incident.TYPE_QUERY_FORM);
					criteriaStr += " and incident.reservedInteger6 =:reservedInteger6";
				}else if(para.equals("all")){
					
				}
			}
			para= req.getParameter("concern");
			if(StringUtils.isNotBlank(para)) {
				String incidentIds = "";
				for(Incident inci : person.getIncidents()){
					incidentIds += "," + inci.getId();
				}
				incidentIds = incidentIds.replaceFirst(",", "");
				if(incidentIds.length() == 0){
					incidentIds = "-1";
				}
				criteriaStr += " and incident.id in (" + incidentIds + ") ";
			}
			
		} else {
			
			fldMap.put("personLocationId", personLocation.getId());
			
			if(StringUtils.isBlank(handlerPersonIdStr) && StringUtils.isBlank(participatorPersonIdStr)){
				criteriaStr += "(";
			}
			
			criteriaStr += "(incident.location.id = :personLocationId";
			
			//domain:
			criteriaStr += " or incident.location.id in (" 
							+ " select distinct ld.locationId from com.telinkus.itsm.data.workgroup.LocationDomain ld"
							+ " join ld.workgroup.members members where " + personId + " in members.id"
							+ " and (ld.viewIncident = true or ld.interveneIncident = true))";
			
			criteriaStr += ")";
			
			if(StringUtils.isBlank(handlerPersonIdStr) && StringUtils.isBlank(participatorPersonIdStr)){
				
				fldMap.put("handlerPersonId", new Integer(req.getSession().getAttribute("personId").toString()));
				fldMap.put("participatorPersonId", new Integer(req.getSession().getAttribute("personId").toString()));
				
				criteriaStr += " or (:handlerPersonId in hls.id or :participatorPersonId in pts.id))";
			}
			
			//making criteria:
			/*criteriaStr += " and (incident.status != :statusDraft)";
			fldMap.put("statusDraft", new Short(Incident.STATUS_DRAFT));*/
			criteriaStr += " and (incident.status != :statusDraft and incident.status != :statusTemplate)";
			fldMap.put("statusDraft", new Short(Incident.STATUS_DRAFT));
			fldMap.put("statusTemplate", new Short(Incident.STATUS_TEMPLATE));
		
			/*	閼惧嘲绶辫ぐ鎾冲娴滃搫鎲筹拷1锟�7锟斤拷1锟�7锟界皑娴犺埖婀侀弻銉嚄閺夊啴妾洪惃鍕閸掔嵒D閻ㄥ嫬鐡欓弻銉嚄	*/
			if(StringUtils.isNotEmpty(operationValue)) {
				if(!(session.getAttribute("personId") + ",").equals(handlerPersonIdStr)){
					criteriaStr += " and incident.category.id in ("
									+ "select categoryId from com.telinkus.itsm.data.role.RoleCategoryRelation"
									+ " where moduleId =  " + ItsmConstants.MODULE_INCIDENT
									+ " and operation=" + new Integer(operationValue)
									+ " and role.id in (" + roleIdListStr.replace('#', ',')
									+ "))";
				}
			}
			
			if(StringUtils.isNotEmpty(categoryCode)) {
			
				if(subCatIncluded) {
						
					fldMap.put("categoryCode", categoryCode + "%");
				
					criteriaStr += " and incident.category.code like :categoryCode";
				
				} else {
					
					fldMap.put("categoryCode", categoryCode);
					
					criteriaStr += " and incident.category.code = :categoryCode";
				}
			

			} else {

				if(StringUtils.isNotEmpty(categoryId)) {
					
					//鏉╂瑩鍣烽弰顖涙箒缁鍩嗛梽鎰煑閿涘奔绲鹃弰顖滄暏閹村嘲婀弻銉嚄閻ㄥ嫭妞傞崐娆愮梾閺堝锟藉瀚ㄧ猾璇插焼娴ｆ粈璐熼弶鈥叉鏉╂稖顢戦弻銉嚄
					//閺嶈宓佺猾璇插焼Id閼惧嘲褰囪ぐ鎾冲缁鍩嗛惃鍕椽閻拷
					Category category = catDAO.findById(new Short(categoryId));
					
					fldMap.put("categoryCode", category.getCode() + "%");

					criteriaStr += " and incident.category.code like :categoryCode";
				
				}
			}

			//beginning of conditions from page:
			if((locationCode != null) && (locationCode.length() > 0)) {
				
				if(subLocationIncluded) {
					
					fldMap.put("locationCode", locationCode + "%");
					criteriaStr += " and incident.location.code like :locationCode";
				
				} else {
					
					fldMap.put("locationCode", locationCode);
					criteriaStr += " and incident.location.code = :locationCode";
				}
			}
			
			if((requestNo != null) && (requestNo.length() > 0)) {
				fldMap.put("requestNo", "%" + requestNo + "%");
				
				criteriaStr += " and incident.requestNo like :requestNo";
			}
			
			if((sourceTypeStr != null) && (sourceTypeStr.length() > 0)) {
				fldMap.put("sourceType", new Short(sourceTypeStr));
				
				criteriaStr += " and incident.sourceType = :sourceType";
				
				if(StringUtils.isNotEmpty(customSourceTypeIdStr)) {
					fldMap.put("customSourceTypeId", new Short(customSourceTypeIdStr));
					criteriaStr += " and incident.customSourceType.id = :customSourceTypeId";
				}
			}
			
			if(StringUtils.isNotBlank(subject)) {
				fldMap.put("subject", "%" + subject + "%");
				criteriaStr += " and incident.subject like :subject";
			} 
			
			if(StringUtils.isNotBlank(content)) {
				fldMap.put("content", "%" + content + "%");
				criteriaStr += " and incident.content like :content";
			}
			
			if((organizationIdStr != null) && (organizationIdStr.length() > 0)) {
				criteriaStr += " and (";
				String applicantPersonCon = "";
				List<Person> findByOrgId = personDAO.findByOrgId(Integer.valueOf(organizationIdStr));
				for(int j = 0; j < findByOrgId.size(); j++){
					fldMap.put("applicantPersonId" + j, findByOrgId.get(j).getId());
					applicantPersonCon += " or incident.applicant.id = :applicantPersonId" + j;
				}
				applicantPersonCon = applicantPersonCon.replaceFirst("or", " ") + ")";
				criteriaStr += applicantPersonCon;
			}else{
				if((applicantPersonIdStr != null) && (applicantPersonIdStr.length() > 0)) {
					criteriaStr += " and (";
					String[] applicantPersonIdStrArr = applicantPersonIdStr.split(",");
					String applicantPersonCon = "";
					for(int j = 0; j < applicantPersonIdStrArr.length; j++){
						fldMap.put("applicantPersonId" + j, new Integer(applicantPersonIdStrArr[j]));
						applicantPersonCon += " or incident.applicant.id = :applicantPersonId" + j;
					}
					applicantPersonCon = applicantPersonCon.replaceFirst("or", " ") + ")";
					criteriaStr += applicantPersonCon;
					/*fldMap.put("applicantPersonId", new Integer(applicantPersonIdStr));
					
					criteriaStr += " and incident.applicant.id = :applicantPersonId";*/
				}
			}
			
			if(StringUtils.isNotBlank(customerIdStr)) {
				fldMap.put("customerId", new Integer(customerIdStr));
				
				criteriaStr += " and incident.customer.id = :customerId";
			}
			
			if((creatorPersonIdStr != null) && (creatorPersonIdStr.length() > 0)) {
				fldMap.put("creatorPersonId", new Integer(creatorPersonIdStr));
				
				criteriaStr += " and incident.creator.id = :creatorPersonId";
			}
			
			if((handlerPersonIdStr != null) && (handlerPersonIdStr.length() > 0)) {
				if(!(session.getAttribute("personId") + ",").equals(handlerPersonIdStr)){
					criteriaStr += " and (";
					String[] handlerPersonIdStrArr = handlerPersonIdStr.split(",");
					String handlerPersonCon = "";
					for(int j = 0; j < handlerPersonIdStrArr.length; j++){
						fldMap.put("handlerPersonId" + j, new Integer(handlerPersonIdStrArr[j]));
						handlerPersonCon += " or :handlerPersonId" + j + " in hls.id";
					}
					handlerPersonCon = handlerPersonCon.replaceFirst("or", " ") + ")";
					criteriaStr += handlerPersonCon;
					/*fldMap.put("handlerPersonId", new Integer(handlerPersonIdStr));
					
					criteriaStr += " and :handlerPersonId in hls.id";*/
				}else{
					fldMap.put("handlerId", new Integer(req.getSession().getAttribute("personId").toString()));
					fldMap.put("handoverId", new Integer(req.getSession().getAttribute("personId").toString()));
					criteriaStr += " and (";
					criteriaStr += "((:handlerId in hls.id) or (:handoverId in activitys.person.id and activitys.type=5))";
					criteriaStr += " ) ";
				}
			} 
			
			if(StringUtils.isNotEmpty(participatorPersonIdStr)) {
				criteriaStr += " and (";
				String[] participatorPersonIdStrArr = participatorPersonIdStr.split(",");
				String participatorPersonCon = "";
				for(int j = 0; j < participatorPersonIdStrArr.length; j++){
					fldMap.put("participatorPersonId" + j, new Integer(participatorPersonIdStrArr[j]));
					participatorPersonCon += " or :participatorPersonId" + j + " in pts.id";
				}
				participatorPersonCon = participatorPersonCon.replaceFirst("or", " ") + ")";
				criteriaStr += participatorPersonCon;
				/*fldMap.put("participatorPersonId", new Integer(participatorPersonIdStr));
				
				criteriaStr += " and :participatorPersonId in pts.id";*/
			}
			
			/*
			if((solution != null) && (solution.length() > 0)) {
				fldMap.put("solution", "%" + solution + "%");
				
				criteriaStr += " and incident.solution like :solution";
			}
			*/
			
			if((beginApplicationTimeStr != null) && (beginApplicationTimeStr.length() > 0)) {
				fldMap.put("beginApplicationTime", java.sql.Date.valueOf(beginApplicationTimeStr));
				
				criteriaStr += " and incident.applicationTime >= :beginApplicationTime";
			}
			
			if((endApplicationTimeStr != null) && (endApplicationTimeStr.length() > 0)) {
				fldMap.put("endApplicationTime", java.sql.Timestamp.valueOf(endApplicationTimeStr + " 23:59:59"));
				
				criteriaStr += " and incident.applicationTime <= :endApplicationTime";
			}
			
			if((beginFinishTimeStr != null) && (beginFinishTimeStr.length() > 0)) {
				fldMap.put("beginFinishTime", java.sql.Date.valueOf(beginFinishTimeStr));
				
				criteriaStr += " and incident.finishTime >= :beginFinishTime";
			}
			
			if((endFinishTimeStr != null) && (endFinishTimeStr.length() > 0)) {
				fldMap.put("endFinishTime", java.sql.Date.valueOf(endFinishTimeStr));
				
				criteriaStr += " and incident.finishTime <= :endFinishTime";
			}
			
			if(req.getParameter("plannedOutage") != null && !req.getParameter("plannedOutage").equals("")){
				criteriaStr += " and incident.plannedOutage = true";
			}
			
			if(req.getParameter("reservedString9") != null && !req.getParameter("reservedString9").equals("")){
				criteriaStr += " and (";
				String[] inciQuali = req.getParameter("reservedString9").split(",");
				String con = "";
				for(int j=0; j<inciQuali.length; j++){
					con += " or incident.reservedString9 like '%" + inciQuali[j] + "%'";
				}
				criteriaStr += con.replaceFirst("or", "") + ")";
			}
			
			if((beginPFTimeStr != null) && (beginPFTimeStr.length() > 0)) {
				fldMap.put("beginPFTime", java.sql.Date.valueOf(beginPFTimeStr));
				
				criteriaStr += " and incident.customInfo.datetime6 >= :beginPFTime";
			}
			
			if((endPFTimeStr != null) && (endPFTimeStr.length() > 0)) {
				fldMap.put("endPFTime", java.sql.Timestamp.valueOf(endPFTimeStr + " 23:59:59"));
				
				criteriaStr += " and incident.customInfo.datetime6 <= :endPFTime";
			}
			
			if((severityIdStr != null) && (severityIdStr.length() > 0)) {
				
				/*fldMap.put("severityId", new Short(severityIdStr));
				criteriaStr += " and incident.severity.id = :severityId";*/
				
				criteriaStr += " and incident.severity.id in (" + severityIdStr + ")";
			}
			
			if((effectIdStr != null) && (effectIdStr.length() > 0)) {
				fldMap.put("effectId", new Short(effectIdStr));
				
				criteriaStr += " and incident.effect.id = :effectId";
			}
			
			if((urgencyIdStr != null) && (urgencyIdStr.length() > 0)) {
				fldMap.put("urgencyId", new Short(urgencyIdStr));
				
				criteriaStr += " and incident.urgency.id = :urgencyId";
			}
			
			if((reasonIdStr != null) && (reasonIdStr.length() > 0)) {
				fldMap.put("reasonId", new Short(reasonIdStr));
				
				criteriaStr += " and incident.reason.id = :reasonId";
			}
			
			if((closeTypeStr != null) && (closeTypeStr.length() > 0)) {
				fldMap.put("closeType", new Short(closeTypeStr));
				
				criteriaStr += " and incident.closeType = :closeType";
			}
			
			if((statusStr != null) && (statusStr.length() > 0)) {
				
				/*fldMap.put("status", new Short(statusStr));
				criteriaStr += " and incident.status = :status";*/
				
				String[] statusArr = statusStr.split(",");
				
				boolean status_processing = false;
				boolean status_processing_testing = false;
				boolean status_processing_tested = false;
				
				for(String s : statusArr){
					if(s.equals(String.valueOf(Incident.STATUS_PROCESSING))){
						status_processing = true;
						break;
					}
					if(s.equals(String.valueOf(Incident.STATUS_PROCESSING_TESTING))){
						status_processing_testing = true;
					}
					if(s.equals(String.valueOf(Incident.STATUS_PROCESSING_TESTED))){
						status_processing_tested = true;
					}
				}
				//
				if(!status_processing){
					if(status_processing_testing||status_processing_tested){
						criteriaStr += "and (incident.status in (" + statusStr +") or incident.reservedInteger7 in (" + statusStr +")) ";
					}else{
						criteriaStr += " and incident.status in (" + statusStr +") ";
					}
				}else{
					criteriaStr += " and incident.status in (" + statusStr +") ";
				}
			}
			
			if((finishStatus != null) && (finishStatus.length() > 0)) {
				criteriaStr += " and incident.status in (4,6,7) ";
			}
			
			//是否关联子工单
			if((relatedTicketHandler != null) && (relatedTicketHandler.length() > 0)) {
				if(relatedTicketHandler.equals("related")){
					criteriaStr += " and size(incident.tickets) > 0";
				}else if(relatedTicketHandler.equals("norelated")){
					criteriaStr += " and size(incident.tickets) = 0";
				}
			}
			
			//custom info:
			para = req.getParameter("integer1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer1", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer1 " +  req.getParameter("integer1_op") + " :integer1";
			}
			
			para = req.getParameter("integer2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer2", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer2 " +  req.getParameter("integer2_op") + " :integer2";
			}
			
			para = req.getParameter("integer3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer3", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer3 " +  req.getParameter("integer3_op") + " :integer3";
			}
			
			para = req.getParameter("integer4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer4", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer4 " +  req.getParameter("integer4_op") + " :integer4";
			}
			
			para = req.getParameter("integer5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer5", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer5 " +  req.getParameter("integer5_op") + " :integer5";
			}
			
			para = req.getParameter("integer6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer6", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer6 " +  req.getParameter("integer6_op") + " :integer6";
			}
			
			para = req.getParameter("integer7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer7", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer7 " +  req.getParameter("integer7_op") + " :integer7";
			}
			
			para = req.getParameter("integer8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer8", new Integer(para));
				
				criteriaStr += " and incident.customInfointeger8 " +  req.getParameter("integer8_op") + " :integer8";
			}
			
			para = req.getParameter("integer9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer9", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer9 " +  req.getParameter("integer9_op") + " :integer9";
			}
			
			para = req.getParameter("integer10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("integer10", new Integer(para));
				
				criteriaStr += " and incident.customInfo.integer10 " +  req.getParameter("integer10_op") + " :integer10";
			}
	
			para = req.getParameter("double1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double1", new Double(para));
				
				criteriaStr += " and incident.customInfo.double1 " +  req.getParameter("double1_op") + " :double1";
			
			}
	
			para = req.getParameter("double2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double2", new Double(para));
				
				criteriaStr += " and incident.customInfo.double2 " +  req.getParameter("double2_op") + " :double2";
			
			}
			
			para = req.getParameter("double3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double3", new Double(para));
				
				criteriaStr += " and incident.customInfo.double3 " +  req.getParameter("double3_op") + " :double3";
			
			}
			
			para = req.getParameter("double4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double4", new Double(para));
				
				criteriaStr += " and incident.customInfo.double4 " +  req.getParameter("double4_op") + " :double4";
			
			}
			
			para = req.getParameter("double5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double5", new Double(para));
				
				criteriaStr += " and incident.customInfo.double5 " +  req.getParameter("double5_op") + " :double5";
			
			}
			
			para = req.getParameter("double6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double6", new Double(para));
				
				criteriaStr += " and incident.customInfo.double6 " +  req.getParameter("double6_op") + " :double6";
			
			}
			
			para = req.getParameter("double7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double7", new Double(para));
				
				criteriaStr += " and incident.customInfo.double7 " +  req.getParameter("double7_op") + " :double7";
			
			}
			
			para = req.getParameter("double8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double8", new Double(para));
				
				criteriaStr += " and incident.customInfo.double8 " +  req.getParameter("double8_op") + " :double8";
			
			}
			
			para = req.getParameter("double9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double9", new Double(para));
				
				criteriaStr += " and incident.customInfo.double9 " +  req.getParameter("double9_op") + " :double9";
			
			}
			
			para = req.getParameter("double10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("double10", new Double(para));
				
				criteriaStr += " and incident.customInfo.double10 " +  req.getParameter("double10_op") + " :double10";
			
			}
			
			para = req.getParameter("boolean1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean1", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean1 = :boolean1";
			}
			
			para = req.getParameter("boolean2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean2", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean2 = :boolean2";
			}
			
			para = req.getParameter("boolean3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean3", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean3 = :boolean3";
			}
			
			para = req.getParameter("boolean4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean4", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean4 = :boolean4";
			}
			
			para = req.getParameter("boolean5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean5", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean5 = :boolean5";
			}
			
			para = req.getParameter("boolean6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean6", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean6 = :boolean6";
			}
			
			para = req.getParameter("boolean7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean7", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean7 = :boolean7";
			}
			
			para = req.getParameter("boolean8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean8", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean8 = :boolean8";
			}
			
			para = req.getParameter("boolean9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean9", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean9 = :boolean9";
			}
			
			para = req.getParameter("boolean10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("boolean10", new Boolean(para));
				
				criteriaStr += " and incident.customInfo.boolean10 = :boolean10";
			}
			
			para = req.getParameter("datetime1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime1", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime1 " + req.getParameter("datetime1_op") + " :datetime1";
			}
			
			para = req.getParameter("datetime2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime2", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime2 " + req.getParameter("datetime2_op") + " :datetime2";
			}
			
			para = req.getParameter("datetime3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime3", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime3 " + req.getParameter("datetime3_op") + " :datetime3";
			}
			
			para = req.getParameter("datetime4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime4", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime4 " + req.getParameter("datetime4_op") + " :datetime4";
			}
			
			para = req.getParameter("datetime5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime5", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime5 " + req.getParameter("datetime5_op") + " :datetime5";
			}
			
			para = req.getParameter("datetime6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime6", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime6 " + req.getParameter("datetime6_op") + " :datetime6";
			}
			
			para = req.getParameter("datetime7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime7", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime7 " + req.getParameter("datetime7_op") + " :datetime7";
			}
			
			para = req.getParameter("datetime8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime8", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime8 " + req.getParameter("datetime8_op") + " :datetime8";
			}
			
			para = req.getParameter("datetime9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime9", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime9 " + req.getParameter("datetime9_op") + " :datetime9";
			}
			
			para = req.getParameter("datetime10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("datetime10", Timestamp.valueOf(para));
				
				criteriaStr += " and incident.customInfo.datetime10 " + req.getParameter("datetime10_op") + " :datetime10";
			}
			
			para = req.getParameter("date1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date1", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date1 " + req.getParameter("date1_op") + " :date1";
			}
			
			para = req.getParameter("date2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date2", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date2 " + req.getParameter("date2_op") + " :date2";
			}
			
			para = req.getParameter("date3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date3", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date3 " + req.getParameter("date3_op") + " :date3";
			}
			
			para = req.getParameter("date4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date4", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date4 " + req.getParameter("date4_op") + " :date4";
			}
			
			para = req.getParameter("date5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date5", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date5 " + req.getParameter("date5_op") + " :date5";
			}
			
			para = req.getParameter("date6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date6", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date6 " + req.getParameter("date6_op") + " :date6";
			}
			
			para = req.getParameter("date7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date7", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date7 " + req.getParameter("date7_op") + " :date7";
			}
			
			para = req.getParameter("date8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date8", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date8 " + req.getParameter("date8_op") + " :date8";
			}
			
			para = req.getParameter("date9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date9", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date9 " + req.getParameter("date9_op") + " :date9";
			}
			
			para = req.getParameter("date10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("date10", Date.valueOf(para));
				
				criteriaStr += " and incident.customInfo.date10 " + req.getParameter("date10_op") + " :date10";
			}
			
			para = req.getParameter("shortstring1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring1", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring1 like :shortstring1";
			}
			
			para = req.getParameter("shortstring2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring2", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring2 like :shortstring2";
			}
			
			para = req.getParameter("shortstring3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring3", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring3 like :shortstring3";
			}
			
			para = req.getParameter("shortstring4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring4", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring4 like :shortstring4";
			}
			
			para = req.getParameter("shortstring5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring5", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring5 like :shortstring5";
			}
			
			para = req.getParameter("shortstring6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring6", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring6 like :shortstring6";
			}
			
			para = req.getParameter("shortstring7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring7", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring7 like :shortstring7";
			}
			
			para = req.getParameter("shortstring8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring8", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring8 like :shortstring81";
			}
			
			para = req.getParameter("shortstring9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring9", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring9 like :shortstring9";
			}
			
			para = req.getParameter("shortstring10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring10", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring10 like :shortstring10";
			}
			
			para = req.getParameter("shortstring11");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring11", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring11 like :shortstring11";
			}
			
			para = req.getParameter("shortstring12");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring12", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring12 like :shortstring12";
			}
			
			para = req.getParameter("shortstring13");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring13", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring13 like :shortstring13";
			}
			
			para = req.getParameter("shortstring14");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring14", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring14 like :shortstring14";
			}
			
			para = req.getParameter("shortstring15");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring15", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring15 like :shortstring15";
			}
			
			para = req.getParameter("shortstring16");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring16", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring16 like :shortstring16";
			}
			
			para = req.getParameter("shortstring17");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring17", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring17 like :shortstring17";
			}
			
			para = req.getParameter("shortstring18");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring18", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring18 like :shortstring18";
			}
			
			para = req.getParameter("shortstring19");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring19", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring19 like :shortstring19";
			}
			
			para = req.getParameter("shortstring20");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("shortstring20", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.shortstring20 like :shortstring20";
			}
			para = req.getParameter("mediumstring1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring1", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring1 like :mediumstring1";
			}
	
			para = req.getParameter("mediumstring2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring2", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring2 like :mediumstring2";
			}
			
			para = req.getParameter("mediumstring3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring3", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring3 like :mediumstring3";
			}
			
			para = req.getParameter("mediumstring4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring4", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring4 like :mediumstring4";
			}
			
			para = req.getParameter("mediumstring5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring5", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring5 like :mediumstring5";
			}
			
			para = req.getParameter("mediumstring6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring6", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring6 like :mediumstring6";
			}
			
			para = req.getParameter("mediumstring7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring7", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring7 like :mediumstring7";
			}
			
			para = req.getParameter("mediumstring8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring8", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring8 like :mediumstring8";
			}
			
			para = req.getParameter("mediumstring9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring9", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring9 like :mediumstring9";
			}
			
			para = req.getParameter("mediumstring10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("mediumstring10", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.mediumstring10 like :mediumstring10";
			}
			
			para = req.getParameter("longstring1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("longstring1", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.longstring1 like :longstring1";
			}
			
			para = req.getParameter("longstring2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("longstring2", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.longstring2 like :longstring2";
			}
			
			para = req.getParameter("longstring3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("longstring3", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.longstring3 like :longstring3";
			}
			
			para = req.getParameter("longstring4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("longstring4", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.longstring4 like :longstring4";
			}
			
			para = req.getParameter("longstring5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("longstring5", "%" + para + "%");
				
				criteriaStr += " and incident.customInfo.longstring5 like :longstring5";
			}
			para = req.getParameter("treevalue1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue1", para);
				
				criteriaStr += " and incident.customInfo.treevalue1 = :treevalue1";
			}
			
			para = req.getParameter("treevalue2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue2", para);
				
				criteriaStr += " and incident.customInfo.treevalue2 = :treevalue2";
			}
			
			para = req.getParameter("treevalue3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue3", para);
				
				criteriaStr += " and incident.customInfo.treevalue3 = :treevalue3";
			}
			
			para = req.getParameter("treevalue4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue4", para);
				
				criteriaStr += " and incident.customInfo.treevalue4 = :treevalue4";
			}
			
			para = req.getParameter("treevalue5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue5", para);
				
				criteriaStr += " and incident.customInfo.treevalue5 = :treevalue5";
			}
			
			para = req.getParameter("treevalue6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue6", para);
				
				criteriaStr += " and incident.customInfo.treevalue6 = :treevalue6";
			}
			
			para = req.getParameter("treevalue7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue7", para);
				
				criteriaStr += " and incident.customInfo.treevalue7 = :treevalue7";
			}
			
			para = req.getParameter("treevalue8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue8", para);
				
				criteriaStr += " and incident.customInfo.treevalue8 = :treevalue8";
			}
			
			para = req.getParameter("treevalue9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue9", para);
				
				criteriaStr += " and incident.customInfo.treevalue9 = :treevalue9";
			}
			
			para = req.getParameter("treevalue10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("treevalue10", para);
				
				criteriaStr += " and incident.customInfo.treevalue10 = :treevalue10";
			}
			
			para = req.getParameter("codevalue1");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue1", para);
				
				criteriaStr += " and incident.customInfo.codevalue1 = :codevalue1";
			}
			
			para = req.getParameter("codevalue2");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue2", para);
				
				criteriaStr += " and incident.customInfo.codevalue2 = :codevalue2";
			}
			
			para = req.getParameter("codevalue3");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue3", para);
				
				criteriaStr += " and incident.customInfo.codevalue3 = :codevalue3";
			}
			
			para = req.getParameter("codevalue4");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue4", para);
				
				criteriaStr += " and incident.customInfo.codevalue4 = :codevalue4";
			}
			
			para = req.getParameter("codevalue5");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue5", para);
				
				criteriaStr += " and incident.customInfo.codevalue5 = :codevalue5";
			}
			
			para = req.getParameter("codevalue6");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue6", para);
				
				criteriaStr += " and incident.customInfo.codevalue6 = :codevalue6";
			}
			
			para = req.getParameter("codevalue7");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue7", para);
				
				criteriaStr += " and incident.customInfo.codevalue7 = :codevalue7";
			}
			
			para = req.getParameter("codevalue8");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue8", para);
				
				criteriaStr += " and incident.customInfo.codevalue8 = :codevalue8";
			}
			
			para = req.getParameter("codevalue9");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue9", para);
				
				criteriaStr += " and incident.customInfo.codevalue9 = :codevalue9";
			}
			
			para = req.getParameter("codevalue10");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue10", para);
				
				criteriaStr += " and incident.customInfo.codevalue10 = :codevalue10";
			}
			
			para = req.getParameter("codevalue11");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue11", para);
				
				criteriaStr += " and incident.customInfo.codevalue11 = :codevalue11";
			}
			
			para = req.getParameter("codevalue12");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue12", para);
				
				criteriaStr += " and incident.customInfo.codevalue12 = :codevalue12";
			}
			
			para = req.getParameter("codevalue13");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue13", para);
				
				criteriaStr += " and incident.customInfo.codevalue13 = :codevalue13";
			}
			
			para = req.getParameter("codevalue14");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue14", para);
				
				criteriaStr += " and incident.customInfo.codevalue14 = :codevalue14";
			}
			
			para = req.getParameter("codevalue15");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue15", para);
				
				criteriaStr += " and incident.customInfo.codevalue15 = :codevalue15";
			}
			
			para = req.getParameter("codevalue16");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue16", para);
				
				criteriaStr += " and incident.customInfo.codevalue16 = :codevalue16";
			}
			
			para = req.getParameter("codevalue17");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue17", para);
				
				criteriaStr += " and incident.customInfo.codevalue17 = :codevalue17";
			}
			
			para = req.getParameter("codevalue18");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue18", para);
				
				criteriaStr += " and incident.customInfo.codevalue18 = :codevalue18";
			}
			
			para = req.getParameter("codevalue19");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue19", para);
				
				criteriaStr += " and incident.customInfo.codevalue19 = :codevalue19";
			}
			
			para = req.getParameter("codevalue20");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("codevalue20", para);
				
				criteriaStr += " and incident.customInfo.codevalue20 = :codevalue20";
			}
		
			para = req.getParameter("location1Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("location1Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.location1.id = :location1Id";
				else
					criteriaStr += "incident.customInfo.location1.id = :location1Id";
			}
			
			para = req.getParameter("location2Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("location2Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.location2.id = :location2Id";
				else
					criteriaStr += "incident.customInfo.location2.id = :location2Id";
			}
			
			para = req.getParameter("location3Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("location3Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.location3.id = :location3Id";
				else
					criteriaStr += "incident.customInfo.location3.id = :location3Id";
			}
			
			para = req.getParameter("location4Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("location4Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.location4.id = :location4Id";
				else
					criteriaStr += "incident.customInfo.location4.id = :location4Id";
			}
			
			para = req.getParameter("location5Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("location5Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.location5.id = :location5Id";
				else
					criteriaStr += "incident.customInfo.location5.id = :location5Id";
			}
			
			para = req.getParameter("organization1Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("organization1Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.organization1.id = :organization1Id";
				else
					criteriaStr += "incident.customInfo.organization1.id = :organization1Id";
			}
			
			para = req.getParameter("organization2Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("organization2Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.organization2.id = :organization2Id";
				else
					criteriaStr += "incident.customInfo.organization2.id = :organization2Id";
			}
			
			para = req.getParameter("organization3Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("organization3Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.organization3.id = :organization3Id";
				else
					criteriaStr += "incident.customInfo.organization3.id = :organization3Id";
			}
			
			para = req.getParameter("organization4Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("organization4Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.organization4.id = :organization4Id";
				else
					criteriaStr += "incident.customInfo.organization4.id = :organization4Id";
			}
			
			para = req.getParameter("organization5Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("organization5Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.organization5.id = :organization5Id";
				else
					criteriaStr += "incident.customInfo.organization5.id = :organization5Id";
			}
			
			para = req.getParameter("person1Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("person1Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.person1.id = :person1Id";
				else
					criteriaStr += "incident.customInfo.person1.id = :person1Id";
			}
			
			para = req.getParameter("person2Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("person2Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.person2.id = :person2Id";
				else
					criteriaStr += "incident.customInfo.person2.id = :person2Id";
			}
			
			para = req.getParameter("person3Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("person3Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.person3.id = :person3Id";
				else
					criteriaStr += "incident.customInfo.person3.id = :person3Id";
			}
			
			para = req.getParameter("person4Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("person4Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.person4.id = :person4Id";
				else
					criteriaStr += "incident.customInfo.person4.id = :person4Id";
			}
			
			para = req.getParameter("person5Id");
			if((para != null) && (para.length() >0)) {
				
				fldMap.put("person5Id", new Integer(para));
				
				if (criteriaStr.length() > 0)
					criteriaStr += " and incident.customInfo.person5.id = :person5Id";
				else
					criteriaStr += "incident.customInfo.person5.id = :person5Id";
			}
			
			para = req.getParameter("reservedString1"); 
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString1", "%" + para + "%");
				criteriaStr += " and incident.reservedString1 like :reservedString1";
			}
			
			para = req.getParameter("reservedString2"); 
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString2", "%" + para + "%");
				criteriaStr += " and incident.reservedString2 like :reservedString2";
			}
			
			para = req.getParameter("reservedString3");
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString3", "%" + para + "%");
				criteriaStr += " and incident.reservedString3 like :reservedString3";
			}
			
			para = req.getParameter("reservedString4"); 
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString4", "%" + para + "%");
				criteriaStr += " and incident.reservedString4 like :reservedString4";
			}
			
			para = req.getParameter("reservedString5");
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString5", "%" + para + "%");
				criteriaStr += " and incident.reservedString5 like :reservedString5";
			}
			
			para = req.getParameter("reservedString6");
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString6", "%" + para + "%");
				criteriaStr += " and incident.reservedString6 like :reservedString6";
			}
			
			para = req.getParameter("reservedString7");
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString7", "%" + para + "%");
				criteriaStr += " and incident.reservedString7 like :reservedString7";
			}
			
			para = req.getParameter("reservedString8");
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString8", "%" + para + "%");
				criteriaStr += " and incident.reservedString8 like :reservedString8";
			}
			
			para = req.getParameter("reservedString9");
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString9", "%" + para + "%");
				criteriaStr += " and incident.reservedString9 like :reservedString9";
			}
			
			para= req.getParameter("reservedString10");
			if(StringUtils.isNotBlank(para)) {
				fldMap.put("reservedString10", "%" + para + "%");
				criteriaStr += " and incident.reservedString10 like :reservedString10";
			}
			
			////////////////// reservedInteger start //////////////////
			para= req.getParameter("reservedInteger1");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger1 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger1", reservedInteger1);
				criteriaStr += " and incident.reservedInteger1 =:reservedInteger1";
			}
			
			para= req.getParameter("reservedInteger2");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger2 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger2", reservedInteger2);
				criteriaStr += " and incident.reservedInteger2 =:reservedInteger2";
			}
			
			para= req.getParameter("reservedInteger3");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger3 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger3", reservedInteger3);
				criteriaStr += " and incident.reservedInteger3 =:reservedInteger3";
			}
			
			para= req.getParameter("reservedInteger4");
			if(StringUtils.isNotBlank(para)) {
				/*Integer reservedInteger4 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger4", reservedInteger4);
				criteriaStr += " and incident.reservedInteger4 =:reservedInteger4";*/
				
				criteriaStr += " and incident.reservedInteger4 in (" + para + ")";
			}
			
			para= req.getParameter("reservedInteger5");
			if(StringUtils.isNotBlank(para)) {
				/*Integer reservedInteger5 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger5", reservedInteger5);
				criteriaStr += " and incident.reservedInteger5 =:reservedInteger5";*/
				
				criteriaStr += " and incident.reservedInteger5 in (" + para + ")";
			}
			
			para= req.getParameter("reservedInteger6");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger6 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger6", reservedInteger6);
				criteriaStr += " and incident.reservedInteger6 =:reservedInteger6";
			}
			
			para= req.getParameter("reservedInteger7");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger7 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger7", reservedInteger7);
				criteriaStr += " and incident.reservedInteger7 =:reservedInteger7";
			}
			
			para= req.getParameter("reservedInteger8");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger8 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger8", reservedInteger8);
				criteriaStr += " and incident.reservedInteger8 =:reservedInteger8";
			}
			
			para= req.getParameter("reservedInteger9");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger9 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger9", reservedInteger9);
				criteriaStr += " and incident.reservedInteger9 =:reservedInteger9";
			}
			
			para= req.getParameter("reservedInteger10");
			if(StringUtils.isNotBlank(para)) {
				Integer reservedInteger10 = Utils.parseString2Integer(para);
				fldMap.put("reservedInteger10", reservedInteger10);
				criteriaStr += " and incident.reservedInteger10 =:reservedInteger10";
			}
			
			para= req.getParameter("default");
			if(StringUtils.isNotBlank(para)) {
				if(para.equals("incident")){
					fldMap.put("reservedInteger6", Incident.TYPE_INCIDENT_FORM);
					criteriaStr += " and incident.reservedInteger6 =:reservedInteger6";
				}else if(para.equals("query")){
					fldMap.put("reservedInteger6", Incident.TYPE_QUERY_FORM);
					criteriaStr += " and incident.reservedInteger6 =:reservedInteger6";
				}else if(para.equals("all")){
					
				}
			}
			////////////////// reservedInteger start end//////////////////
			para= req.getParameter("concern");
			if(StringUtils.isNotBlank(para)) {
				String incidentIds = "";
				for(Incident inci : person.getIncidents()){
					incidentIds += "," + inci.getId();
				}
				incidentIds = incidentIds.replaceFirst(",", "");
				if(incidentIds.length() == 0){
					incidentIds = "-1";
				}
				criteriaStr += " and incident.id in (" + incidentIds + ") ";
			}
			
			//回访人
			if(StringUtils.isNotEmpty(feedbackerPersonIdStr)){
				fldMap.put("feedbackerPersonId", new Integer(feedbackerPersonIdStr));
				criteriaStr += " and (activitys.type in ("+IncidentActivity.TYPE_PROCESS_NODE_TAKEOVER+","+IncidentActivity.TYPE_PROCESS_TASK_FINISH+") and :feedbackerPersonId = activitys.person.id  )";
			}
			
			//优先级
			String priority = req.getParameter("priority");
			if(StringUtils.isNotEmpty(priority)){
				fldMap.put("priority", new Short(priority));
				criteriaStr += " and incident.priority = :priority ";
			}
			
		}

		logger.debug("Criteria of finding incidents: " + criteriaStr);
		
		//make count HQL:
		countHql.append("select count(distinct incident) from com.telinkus.itsm.data.incident.Incident incident");
		
		if(StringUtils.isNotEmpty(handlerPersonIdStr)) {
			countHql.append(" join incident.handlers hls");
		} 
		
		if(StringUtils.isNotEmpty(participatorPersonIdStr)) {
			countHql.append(" join incident.participators pts");
		}
		
		if(!draft && ((session.getAttribute("personId") + ",").equals(handlerPersonIdStr) || (StringUtils.isBlank(handlerPersonIdStr) && StringUtils.isBlank(participatorPersonIdStr)))){
			countHql.append(" left join incident.handlers hls");
			countHql.append(" left join incident.participators pts");
		}
		
		if((session.getAttribute("personId") + ",").equals(handlerPersonIdStr) || StringUtils.isNotEmpty(feedbackerPersonIdStr)) {
			countHql.append(" join incident.activities activitys ");
		}
		
		countHql.append(" where " + criteriaStr);
		
		//make search HQL:
		searchHql.append("select distinct incident from com.telinkus.itsm.data.incident.Incident incident");
		
		if(StringUtils.isNotEmpty(handlerPersonIdStr)) {
			searchHql.append(" join incident.handlers hls");
		} 
		
		if(StringUtils.isNotEmpty(participatorPersonIdStr)) {
			searchHql.append(" join incident.participators pts");
		}
		
		if(!draft && ((session.getAttribute("personId") + ",").equals(handlerPersonIdStr) || (StringUtils.isBlank(handlerPersonIdStr) && StringUtils.isBlank(participatorPersonIdStr)))){
			searchHql.append(" left join incident.handlers hls");
			searchHql.append(" left join incident.participators pts");
		}
		
		if((session.getAttribute("personId") + ",").equals(handlerPersonIdStr) || StringUtils.isNotEmpty(feedbackerPersonIdStr)) {
			searchHql.append(" join incident.activities activitys ");
		}
		
		searchHql.append(" where " + criteriaStr);
		
		return;
	
	}//makeSearchHql()
	
	
	
	/*
	 * method to edit task instance
	 */
	public ModelAndView editTask(HttpServletRequest req, HttpServletResponse res) {
		
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		TaskInstance taskInstance;
		Integer personId = null;
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		Long taskInstanceId = new Long(req.getParameter("taskInstanceId"));
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		personId = (Integer)session.getAttribute("personId");
		
		//get current open taskInstance:
		taskInstance = processDAO.findTaskInstanceById(taskInstanceId);
		
		NodeInstance nodeInstance = taskInstance.getNodeInstance();
		ProcessInstance processInstance = nodeInstance.getToken().getProcessInstance();
		
		short status = taskInstance.getStatus().shortValue();
		if((status == TaskInstance.STATUS_CLOSED)
				||(status == TaskInstance.STATUS_CANCELLED) 
				|| (status == TaskInstance.STATUS_SUSPENDED) ) {
			
			paraMap.put("error", "Task_already_closed");
			return new ModelAndView(this.getErrorView(), paraMap);
		}
		
		if((status == TaskInstance.STATUS_FOR_AUDIT) && !taskInstance.getAuditorId().equals(personId)) {
			
			paraMap.put("error", "Task_is_not_for_you_to_audit");
			return new ModelAndView(this.getErrorView(), paraMap);
		}
		
		if((status == TaskInstance.STATUS_PROCESSING) && !taskInstance.getActorId().equals(personId)) {
			
			paraMap.put("error", "Task_is_not_for_you_to_handle");
			return new ModelAndView(this.getErrorView(), paraMap);
		}
		
		NodeInstance previousNodeInstance = (NodeInstance)nodeInstance.getPreviousNodeInstances().get(0);
		Node previousNode = previousNodeInstance.getNode();
		
		if(previousNode.getType().shortValue() == Node.TYPE_STEP) {
			
			paraMap.put("canWithdraw", "yes");
		}
		
		Incident incident = incidentDAO.findById(incidentId);
		
		paraMap = this.getMap(incident);
		
//		List<HashMap<String, Object>> catList = catDAO.findAllForReport();
		List<HashMap<String, Object>> catList = catDAO.findAllForReportOptimize();
		paraMap.put("catList", catList);
		
		//set data of task instance:
		this.getTaskInstanceMap(paraMap, taskInstance);
		
		//set node instance:
		this.getNodeInstanceMap(paraMap, nodeInstance);
		
		//set data of process instance:
		this.getProcessInstanceMap(paraMap, processInstance);
		
		//check if this node is approval node(pre or post)
		if(nodeInstance.getNode().getType() == Node.TYPE_APPROVAL){
			IncidentProcessDefine ipdPre = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_PRE);
			IncidentProcessDefine ipdPost = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_POST);
			if(ipdPre != null && ipdPre.getProcessDefine() == processInstance.getDefinition()){
				
				paraMap.put("idPreApprovalNode", true);
				paraMap.put("operat", "abort");
				
			}else if(ipdPost != null && ipdPost.getProcessDefine() == processInstance.getDefinition()){
				
				paraMap.put("isPosApprovalNode", true);
				paraMap.put("operat", "disapprove");
			}
			
		}
		
		paraMap.put("severityList", severityDAO.findAll());
		
		return new ModelAndView(getEditTaskView(), paraMap);
		
	}//editTask()

	
	/*
	 * method to take over task instance
	 */
	public ModelAndView takeoverTask(HttpServletRequest req, HttpServletResponse res) {
		
		TaskInstance taskInstance = null;
		Integer personId;
		
		
		//get parameters from http request:
		Long taskInstanceId = new Long(req.getParameter("taskInstanceId"));
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		personId = (Integer)session.getAttribute("personId");

		try {
			
			taskInstance = processDAO.findTaskInstanceByIdForUpdate(taskInstanceId);
			
		} catch(Exception e) {
			
			Map<String,Object> paraMap = new HashMap<String,Object>();
			paraMap.put("error", "Cannot_take_over_the_task");
			
			return new ModelAndView(getErrorView(), paraMap);
		}
		
		if(taskInstance.getActorType().shortValue() != Task.ACTOR_TYPE_ROLE) {
			
			Map<String,Object> paraMap = new HashMap<String,Object>();
			paraMap.put("error", "it_has_already_been_taken_over_by_other_person");
			
			return new ModelAndView(getErrorView(), paraMap);
		}
		
		//change status to assigned:
		taskInstance.setStatus(new Short(TaskInstance.STATUS_PROCESSING));
		
		taskInstance.setActorType(new Short(Task.ACTOR_TYPE_PERSON));
		taskInstance.setActorId(personId);
		
		taskInstance.setBeginTime(new Timestamp(System.currentTimeMillis()));
	
		processDAO.updateTaskInstance(taskInstance);
		logger.info("Task instance(id=" + taskInstanceId + ") was taken over by person(id=" + personId + ").");
		
		//check if this node is approval node
		NodeInstance inst = taskInstance.getNodeInstance();
		if(inst.getNode().getType() == Node.TYPE_APPROVAL){
			inst.setOwnerId(personId);
			inst.beginStep();
		}
		
		//send process notice
		try {
			Token token = taskInstance.getNodeInstance().getToken();
			ProcessInstance processInstance = token.getProcessInstance();
			NodeInstance nodeInstance = token.getCurrentNodeInstance();
			Incident incident = incidentDAO.findById(processInstance.getExternalId());
			processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_TAKEOVER, incident, processInstance, nodeInstance, taskInstance, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		//call editTask to complete the request:
		return editTask(req, res);
		
	}//takeoverTask()

	
	/*
	 * method to handback task instance
	 */
	public ModelAndView handbackTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		Long taskInstanceId = new Long(req.getParameter("taskInstanceId"));
		TaskInstance taskInstance = processDAO.findTaskInstanceByIdForUpdate(taskInstanceId);
		
		incident.getTaskInstances().remove(taskInstance);
		
		processDAO.deleteTaskInstanceById(taskInstanceId);
		
		incident.setStatus(Incident.STATUS_PROCESSING);
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		Person person = this.getCurrentPerson(session);
		
		// create activity : private method
		String comment = req.getParameter("comment");
		IncidentActivity activity = this.createIncidentActivity(person, IncidentActivity.TYPE_HANDBACK_TASK, comment);
		incident.getActivities().add(activity);
		
		incidentDAO.update(incident);
		
		//增加“退回”动作的消息通知
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_HANDBACK, incident, req);
		} catch (Exception e) {
			logger.error("finish menthod sendNotice error : "+Utils.getStackTrace(e));
		}
		
		return this.detail(req, res);
		
	}//handbackTask()
	
	/*
	 * method to take over a step
	 */
	public ModelAndView takeoverStep(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		NodeInstance nodeInstance;
		String editTaskUrl;
		String viewUrl;
		String editLink;
		String viewLink;
		Incident incident;
		String subject;
		String message;
		String requestNo;
		Notice notice;
		String returnView = "";

		
		//get person ID from Session:
		HttpSession session = req.getSession();
		Integer personId = (Integer)session.getAttribute("personId");
		Person person = this.personDAO.findById(personId);

		//get parameters from http request:
		Long taskInstanceId = new Long(req.getParameter("taskInstanceId"));
		
		//get current open taskInstance:
		TaskInstance taskInstance = processDAO.findTaskInstanceById(taskInstanceId);
		if(incidentDAO.findById(taskInstance.getExternalId()).getStatus() 
				!= Incident.STATUS_TOREVIEW){
			
			paraMap.put("error", "Cannot_take_over_step");
			return new ModelAndView(getErrorView(), paraMap);
		}
		Long nodeInstanceId = taskInstance.getNodeInstance().getId();
		
		try {
			
			nodeInstance = processDAO.findNodeInstanceByIdForUpdate(nodeInstanceId);
			
		}catch(Exception e) {
			
			paraMap.put("error", "Cannot_take_over_step");
			
			return new ModelAndView(getErrorView(), paraMap);
		}
		
		if(nodeInstance.getStatus() == NodeInstance.STATUS_RUNNING) {
			
			paraMap.put("error", "it_has_already_been_taken_over_by_other_person");
			
			return new ModelAndView(getErrorView(), paraMap);
		}
		
		ProcessInstance processInstance = nodeInstance.getToken().getProcessInstance();
		
		//set owner of node instance:
		nodeInstance.setOwnerId(personId);
		
		//begin running of the node:
		nodeInstance.beginStep();
		
		//close the task instance:
		taskInstance.setStatus(new Short(TaskInstance.STATUS_CLOSED));
		taskInstance.setEndTime(new Timestamp(System.currentTimeMillis()));
		
		//save to DB:
		//processDAO.updateNodeInstance(nodeInstance);
		processDAO.saveProcessInstance(processInstance);
		
		logger.info("Step node instance(id=" + nodeInstance.getId() + ") was taken over by person(id=" + personId + ")");
		
		//check if process has ended:
		if(processInstance.getStatus().shortValue() == ProcessInstance.STATUS_ENDED) {
		
			//update status of incident to assigned(with handlers) or unassinged(withour handlers)
			incident = incidentDAO.findById(processInstance.getExternalId());
			if(incident.getHandlers().size() > 0) {
						
				incident.setStatus(Incident.STATUS_ASSIGNED);
				logger.info("Incident(id=" + incident.getId() + ")'status was set to assigned.");
						
			} else {
						
				incident.setStatus(Incident.STATUS_UNASSIGNED);
				logger.info("Incident(id=" + incident.getId() + ")'s status was set to unassigned.");
			}
					
			incidentDAO.update(incident);
			
		} else {
			
			if(nodeInstance.getStatus() == NodeInstance.STATUS_RUNNING) {
				
				//find the first task:
				List<TaskInstance> taskInstanceList = nodeInstance.getTaskInstances();
				TaskInstance firstTaskInstance = null;
				for(int i=0;i<taskInstanceList.size();i++) {
			
					TaskInstance ti = taskInstanceList.get(i);
					
					if(((ti.getType().shortValue() == TaskInstance.TYPE_NORMAL) ||
							(ti.getType().shortValue() == TaskInstance.TYPE_SUBTASK)) &&
							(ti.getStatus() == TaskInstance.STATUS_PROCESSING)) {
						
						firstTaskInstance = ti;
						break;
					
					} else if(ti.getType().shortValue() == TaskInstance.TYPE_AUTO &&
								ti.getStatus().shortValue() == TaskInstance.STATUS_FOR_AUDIT) {
						
						firstTaskInstance = ti;
						break;
					}
				}
		
				if(firstTaskInstance != null) {//send notice
				
					incident = incidentDAO.findById(processInstance.getExternalId());
					requestNo = incident.getRequestNo();
							
					if(firstTaskInstance.getStatus().shortValue() == TaskInstance.STATUS_FOR_AUDIT) {
						
						paraMap = this.getMap(incident);
						
						//set data of task instance:
						this.getTaskInstanceMap(paraMap, firstTaskInstance);
					
						//set data of node instance:
						this.getNodeInstanceMap(paraMap, nodeInstance);
						
						//set data of process instance:
						this.getProcessInstanceMap(paraMap, processInstance);
						
						returnView = "EditTaskView";
						//return new ModelAndView(getEditTaskView(), paraMap);
					
					} else {
						
						if(firstTaskInstance.getActorId().equals(personId)) {
							//open task form for edit directly:
							paraMap = this.getMap(incident);
						
							//set data of task instance:
							this.getTaskInstanceMap(paraMap, firstTaskInstance);
						
							//set data of node instance:
							this.getNodeInstanceMap(paraMap, nodeInstance);
							
							//set data of process instance:
							this.getProcessInstanceMap(paraMap, processInstance);
							
							returnView = "EditTaskView";
							//return new ModelAndView(getEditTaskView(), paraMap);
							
						} else {
							//send notice to the actor of first task:
							
							//get properties from configuration file:
							if(configProperties == null) {
								configProperties = new Properties();
								configProperties.load(new FileInputStream(resource.getFile()));
							}
							
							editTaskUrl = configProperties.getProperty("incidentEditTaskURL");
							viewUrl = configProperties.getProperty("incidentViewURL");
							
							subject = configProperties.getProperty("newTaskSubject");
							message = configProperties.getProperty("newTaskMessage");
							
							editLink = editTaskUrl +
											"&id=" + processInstance.getExternalId() +
											"&taskInstanceId=" + firstTaskInstance.getId();
							
							viewLink = viewUrl + "&id=" + processInstance.getExternalId();
					
							notice = Notice.newNoticeTemplate(nodeInstance, firstTaskInstance, null, null, 
																subject, message, requestNo, editLink, viewLink);
							
							noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
						}
					}
					
				}
			}
			
		}
		
		//send process notice
		Incident inci = incidentDAO.findById(processInstance.getExternalId());
		//接管时incident状态改为回访中
		inci.setStatus(Incident.STATUS_REVIEWING);
		
		// 获取当前时间
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		IncidentActivity incidnetAct = new IncidentActivity();
		incidnetAct.setType(IncidentActivity.TYPE_PROCESS_NODE_TAKEOVER);
		incidnetAct.setTime(currentTime);
		incidnetAct.setPerson(person);
		inci.getActivities().add(incidnetAct);
		incidentDAO.update(inci);
		paraMap.put("frameReload", new Boolean(true));
		
		
		try{
			processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_TAKEOVER, inci, processInstance, nodeInstance, null, req);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		//发送邮件
//		try {
//			String addresseeStr = inci.getReservedString5();
//			if(addresseeStr != null && addresseeStr.length() > 0){
//				if(addresseeStr.indexOf("@") == -1){
//					addresseeStr += "@bankofbeijing.com.cn";
//				}
//				IncidentEmail incidentEmail = new IncidentEmail();
//				incidentEmail.setAddressor(person);
//				incidentEmail.setSendTime(new Timestamp(new java.util.Date().getTime()));
//				incidentEmail.setAddresseeAdd(addresseeStr);
//				incidentEmail.setSubject("查询单回访（" + inci.getRequestNo() + "，" + inci.getSubject() + "）");
//				StringBuffer sb = new StringBuffer("发件人：" + person.getEmail() + "<br/><br/>");
//				sb.append("尊敬的");
//				if(inci.getReservedString3() != null && inci.getReservedString3().length() > 0){
//					sb.append(inci.getReservedString3()).append("支行");
//				}
//				sb.append(inci.getReservedString5()).append("，你好！<br/>");
//				sb.append("您的查询单（").append(inci.getRequestNo() + "，" + inci.getSubject() + "）")
//				.append("已按需求处理完毕，处理结果已由处理人反馈您。<br/>");
//				sb.append("现请您回复：处理结果是否满足需求？现是否可以结单？如有任何问题请及时回复反馈。<br/>");
//				sb.append("<br/><br/>");
//				sb.append("请回复此邮件。<br/>");
//				sb.append("谢谢！");
//				sb.append("<br/><br/>");
//				sb.append("附查询单内容：").append(inci.getContent());
//				incidentEmail.setContent(sb.toString());
//				inci.getEmails().add(incidentEmail);
//				
//				if (configProperties == null) {
//					configProperties = new Properties();
//					configProperties.load(new FileInputStream(resource.getFile()));
//				}
//				
//				String host = configProperties.getProperty("mail.host");
//				String protocol = configProperties.getProperty("mail.transport.protocol");
//				String auth = configProperties.getProperty("mail.smtp.auth");
//				String mailUsername = configProperties.getProperty("mailUsername");
//				String mailPassword = configProperties.getProperty("mailPassword");
//				
//				Properties prop = new Properties();
//				prop.setProperty("mail.host", host);
//				prop.setProperty("mail.transport.protocol", protocol);
//				prop.setProperty("mail.smtp.auth", auth);
//				
//				javax.mail.Session mailSession = javax.mail.Session.getInstance(prop);
//				Transport transport = mailSession.getTransport();
//				transport.connect(host, mailUsername, mailPassword);
//				
//				Message msg = createMail(mailSession, null, incidentEmail);
//				transport.sendMessage(msg, msg.getAllRecipients());
//				transport.close();
//				
//				incidentDAO.update(inci);
//			}
//		} catch (Exception e) {
//			logger.error(e.getLocalizedMessage());
//			e.printStackTrace();
//		}
		
		//步骤接管后有可能直接进入下一个步骤
		try {
			if(processInstance != null) {
				Token currentToken = nodeInstance.getToken();// 获取当前操作节点所在token
				NodeInstance currentTokenNodeInstance = currentToken.getCurrentNodeInstance();
				short currentTokenNodeType = currentTokenNodeInstance.getNode().getType();
				Token globalToken = processInstance.getToken();// 获取全局token
				NodeInstance globalNodeInstance = globalToken.getCurrentNodeInstance();//全局流程当前节点
				short globalNodeType = globalNodeInstance.getNode().getType();//获取节点类型
				if(globalNodeType == Node.TYPE_STEP || (globalNodeType == Node.TYPE_FORK && currentToken.getForkNodeInstance() != null && currentToken.getForkNodeInstance().getId() != globalNodeInstance.getId())) {
					//全局流程当前节点是普通节点||全局流程当前节点是并发开始节点但是与当前操作节点所属的并发开始节点不同，那么当前节点已经不在并发中
					if(currentTokenNodeType == Node.TYPE_STEP) {// 如果是普通步骤节点
						if(currentTokenNodeInstance.getStatus() == NodeInstance.STATUS_STANDBY) {// 如果步骤为待启动状态
							//todo
						} else if(currentTokenNodeInstance.getStatus() == NodeInstance.STATUS_ASSIGNED) {//如果步骤状态为待受理(负责人为角色)
							// 发送步骤启动通知
							if(currentTokenNodeInstance.getId() != nodeInstance.getId()) {//如果当前步骤不为被接管步骤
								processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, inci, processInstance, currentTokenNodeInstance, null, req);
							}
						} else if(currentTokenNodeInstance.getStatus() == NodeInstance.STATUS_RUNNING) {//如果步骤状态为执行中(负责人为人员)
							// 发送步骤启动通知
							if(currentTokenNodeInstance.getId() != nodeInstance.getId()) {//如果当前步骤不为被接管步骤
								processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, inci, processInstance, currentTokenNodeInstance, null, req);
							}
							
							List<TaskInstance> taskInstances = currentTokenNodeInstance.getTaskInstances();
							// 遍历节点下任务，找到第一个不为自动任务的任务实例，并发送任务启动通知
							for(int index = 0; index < taskInstances.size(); index++) {
								TaskInstance tempTask = taskInstances.get(index);
								if(tempTask.getType() != TaskInstance.TYPE_AUTO && tempTask.getType() != TaskInstance.TYPE_STEP_TAKEOVER && tempTask.getType() != TaskInstance.TYPE_BRANCH) {// 自动任务不发送通知
									processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_START, inci, processInstance, currentTokenNodeInstance, tempTask, req);
									break;
								}
							}
						}
					} else if(currentTokenNodeType == Node.TYPE_FORK) {// 并发开始节点,这里不需要判断当前步骤是否为被接管步骤，因为并发节点不可能需要接管
						// 遍历所有节点
						List<Token> childTokenList = processInstance.getToken().getChildren();
						for(int i=0;i<childTokenList.size();i++) {
							Token childToken = childTokenList.get(i);
							NodeInstance tempNodeInst = childToken.getCurrentNodeInstance();
							if(tempNodeInst.getStatus() == NodeInstance.STATUS_ASSIGNED) {//如果步骤状态为待受理(负责人为角色)
								// 发送步骤启动通知
								processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, inci, processInstance, tempNodeInst, null, req);
							} else if(currentTokenNodeInstance.getStatus() == NodeInstance.STATUS_RUNNING) {//如果步骤状态为执行中(负责人为人员)
								if(tempNodeInst.getTaskInstances().size() == 0) {//如果节点下没有任务，目前忽略
									
								} else {
									// 发送步骤启动通知
									processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, inci, processInstance, tempNodeInst, null, req);
									
									List<TaskInstance> taskInstances = tempNodeInst.getTaskInstances();
									// 遍历节点下任务，找到第一个不为自动任务的任务实例，并发送任务启动通知
									for(int index = 0; index < taskInstances.size(); index++) {
										TaskInstance tempTask = taskInstances.get(index);
										if(tempTask.getType() != TaskInstance.TYPE_AUTO && tempTask.getType() != TaskInstance.TYPE_STEP_TAKEOVER && tempTask.getType() != TaskInstance.TYPE_BRANCH) {// 自动任务不发送通知
											processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_START, inci, processInstance, currentTokenNodeInstance, tempTask, req);
											break;
										}
									}
								}
							}
							
						}
					}
				} else {
					//在并发步骤中,currentTokenNodeInstance的类型一定为普通节点
					if(currentTokenNodeInstance.getStatus() == NodeInstance.STATUS_STANDBY) {// 如果步骤为待启动状态
						//todo
					} else if(currentTokenNodeInstance.getStatus() == NodeInstance.STATUS_ASSIGNED) {//如果步骤状态为待受理(负责人为角色)
						// 发送步骤启动通知
						if(currentTokenNodeInstance.getId() != nodeInstance.getId()) {//如果当前步骤不为被当前操作步骤
							processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, inci, processInstance, currentTokenNodeInstance, null, req);
						}
					} else if(currentTokenNodeInstance.getStatus() == NodeInstance.STATUS_RUNNING) {//如果步骤状态为执行中(负责人为人员)
						// 发送步骤启动通知
						if(currentTokenNodeInstance.getId() != nodeInstance.getId()) {//如果当前步骤不为当前操作步骤
							processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_NODE_START, inci, processInstance, currentTokenNodeInstance, null, req);
						}
						
						List<TaskInstance> taskInstances = currentTokenNodeInstance.getTaskInstances();
						// 遍历节点下任务，找到第一个不为自动任务的任务实例，并发送任务启动通知
						for(int index = 0; index < taskInstances.size(); index++) {
							TaskInstance tempTask = taskInstances.get(index);
							if(tempTask.getType() != TaskInstance.TYPE_AUTO && tempTask.getType() != TaskInstance.TYPE_STEP_TAKEOVER && tempTask.getType() != TaskInstance.TYPE_BRANCH) {// 自动任务不发送通知
								processSendNotification(ItsmConstants.PROCESS_COMMON, IncidentActivity.TYPE_PROCESS_TASK_START, inci, processInstance, currentTokenNodeInstance, tempTask, req);
								break;
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		paraMap.put("parentReload", true);
		
		if("EditTaskView".equals(returnView)) {
//			List<HashMap<String, Object>> catList = catDAO.findAllForReport();
			List<HashMap<String, Object>> catList = catDAO.findAllForReportOptimize();
			paraMap.put("catList", catList);
			paraMap.put("severityList", severityDAO.findAll());
			return new ModelAndView(getEditTaskView(), paraMap);
		} else {
			return new ModelAndView(getSuccessView(), paraMap);
		}
		
	}//takeoverStep()
	
	
	/*
	 * method to edit step node instance
	 */
	public ModelAndView editStep(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		Map<String,Object> nodeInstanceMap;
		NodeInstance nodeInstance;
		Integer personId = null;
		List<Map<String,Object>> attachmentList;
		Map<String,Object> processInstanceMap, attachmentMap;
		ProcessInstance processInstance;
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		Long nodeInstanceId = new Long(req.getParameter("nodeInstanceId"));

		//get person ID from Session:
		HttpSession session = req.getSession();
		personId = (Integer)session.getAttribute("personId");
		
		//get current open nodeInstance:
		nodeInstance = processDAO.findNodeInstanceById(nodeInstanceId);

		short status = nodeInstance.getStatus().shortValue();
		if(status == NodeInstance.STATUS_CLOSED || status == NodeInstance.STATUS_CANCELLED) {
			
			paraMap.put("error", "Step_already_closed");
			return new ModelAndView(this.getErrorView(), paraMap);
		}
		
		if(status == NodeInstance.STATUS_RUNNING && !nodeInstance.getOwnerId().equals(personId)) {
			
			paraMap.put("error", "Step_is_not_for_you_to_handle");
			return new ModelAndView(this.getErrorView(), paraMap);
		}
		
		Incident incident = incidentDAO.findById(incidentId);
		
		paraMap = this.getMap(incident);
		
		//add by czk begin
		//only can be withdrawed to step node :
		NodeInstance previousNodeInstance = (NodeInstance)nodeInstance.getPreviousNodeInstances().get(0);
		Node previousNode = previousNodeInstance.getNode();
		
		if(previousNode.getType().shortValue() == Node.TYPE_STEP) {
			
			paraMap.put("canWithdraw", "yes");
		}
		//add by czk end
		
		//return info of process instance:
		//processInstance = processDAO.findProcessInstanceById(incident.getProcessInstanceId());
		processInstance = nodeInstance.getToken().getProcessInstance();
		processInstanceMap = new HashMap<String,Object>();
		
		processInstanceMap.put("id", processInstance.getId());
		processInstanceMap.put("beginTime", processInstance.getBeginTime());
		processInstanceMap.put("status", processInstance.getStatus());
		
		Person initiator = personDAO.findById(processInstance.getInitiatorId());
		processInstanceMap.put("initiatorName", initiator.getName());
		
		paraMap.put("processInstance", processInstanceMap);
		
		Node node = nodeInstance.getNode();
		
		nodeInstanceMap =  new HashMap<String,Object>();
		
		nodeInstanceMap.put("id", nodeInstance.getId());
		nodeInstanceMap.put("status", nodeInstance.getStatus());
		nodeInstanceMap.put("name", nodeInstance.getNode().getName());
		nodeInstanceMap.put("description", node.getDescription());
		
		nodeInstanceMap.put("createTime", nodeInstance.getCreateTime());
		nodeInstanceMap.put("beginTime", nodeInstance.getBeginTime());
		
		Person owner = personDAO.findById(nodeInstance.getOwnerId());
		nodeInstanceMap.put("ownerName", owner.getName());
		
		//tasks:
		List<TaskInstance> taskInstances = nodeInstance.getTaskInstances();
		
		List<Map<String,Object>> taskInstanceList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<taskInstances.size();i++) {
			
			TaskInstance taskInstance = (TaskInstance)taskInstances.get(i);
			
			short taskInstanceType = taskInstance.getType().shortValue();
			if(( taskInstanceType!= TaskInstance.TYPE_NORMAL) && (taskInstanceType != TaskInstance.TYPE_SUBTASK))
				continue;
			
			Map<String,Object> taskInstanceMap = new HashMap<String,Object>();
			
			taskInstanceMap.put("id", taskInstance.getId());
			taskInstanceMap.put("name", taskInstance.getName());
			taskInstanceMap.put("description", taskInstance.getDescription());
			taskInstanceMap.put("status", taskInstance.getStatus());
			
			taskInstanceMap.put("actorType", taskInstance.getActorType());
			taskInstanceMap.put("actorId", taskInstance.getActorId());
			
			if(taskInstance.getActorType().shortValue() == Task.ACTOR_TYPE_PERSON) {
				
				Person person = personDAO.findById(taskInstance.getActorId());
				taskInstanceMap.put("actorName", person.getName());
				
			} else {
				//assigned to role:
				Role role = roleDAO.findById(taskInstance.getActorId());
				taskInstanceMap.put("actorName", role.getName());
			}
			
			taskInstanceList.add(taskInstanceMap);
		}
		
		nodeInstanceMap.put("taskInstanceList", taskInstanceList);
		
		//attachments:
		List<Document> attachments = nodeInstance.getAttachments();
		
		attachmentList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<attachments.size();i++) {
			
			Document attachment = (Document)attachments.get(i);
			
			attachmentMap = new HashMap<String,Object>();
			
			attachmentMap.put("id", attachment.getId());
			attachmentMap.put("subject", attachment.getSubject());
			attachmentMap.put("author", attachment.getAuthor());
			attachmentMap.put("number", attachment.getNumber());
			
			attachmentMap.put("keywords", attachment.getKeywords());
			attachmentMap.put("description", attachment.getDescription());
			
			attachmentMap.put("fileName", attachment.getOriginalFileName());
			attachmentMap.put("directoryId", attachment.getDirectory().getId());
			attachmentMap.put("directoryName", attachment.getDirectory().getPath());
			
			attachmentMap.put("createTime", attachment.getCreateTime());
			attachmentMap.put("creator", attachment.getCreator().getName());
			
			attachmentList.add(attachmentMap);
		}
		
		nodeInstanceMap.put("attachmentList", attachmentList);
		
		paraMap.put("nodeInstance", nodeInstanceMap);
		
		//check if this node is approval node
		if(nodeInstance.getNode().getType() == Node.TYPE_APPROVAL){
			paraMap.put("isApprovalNode", true);
		}
		
		return new ModelAndView(this.getEditStepView(), paraMap);
		
	}//editStep()
	
	
	/*
	 * method to handle incident
	 */
	public ModelAndView handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String roleIdListStr = "";
		String token[];
		List<Role> roleList;
		
		if(configProperties == null) {
			configProperties = new Properties();
			try {
				configProperties.load(new FileInputStream(resource.getFile()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//事件分类一必填角色id
		String catOneRoleId = configProperties.getProperty("incident_cat_one_close_role_id").trim();
		//是否必填分类一
		boolean catOneRequired = false;
		
		//get parameters from session:
		HttpSession session = req.getSession();
		roleIdListStr = (String)session.getAttribute("roleId");
		token = roleIdListStr.split("#");
		
		roleList = new ArrayList<Role>();
		for(int i=0;i<token.length;i++) {
			if(token[i].equals(catOneRoleId)){
				catOneRequired = true;
			}
			Role role = roleDAO.findById(new Integer(token[i]));
			roleList.add(role);
		}
		
		Person person = getCurrentPerson(session);
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		if(!(incident.getHandlers().contains(person) || person.equals(incident.getFeedbacker()))) {
			//myself is only participator
			return detail(req, res);
		}
		
		Short categoryId = incident.getCategory().getId();
		
		paraMap = this.getMap(incident);
		
		paraMap.put("catOneRequired", catOneRequired);

//		List<HashMap<String, Object>> catList = catDAO.findAllForReport();
		List<HashMap<String, Object>> catList = catDAO.findAllForReportOptimize();
		paraMap.put("catList", catList);

		//check permissions:
		paraMap.put("canHandle", 
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDLE))));

		paraMap.put("canHandback", 
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDBACK))));
		
		
		
		
		if(incident.getReservedInteger6()==Incident.TYPE_QUERY_FORM&&incident.getCustomInfo().getDatetime6()!=null){
			paraMap.put("canHandover",Boolean.FALSE);
		}else{
			paraMap.put("canHandover",
					new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDOVER))));
		}
		
		paraMap.put("canReject",
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_REJECT))));

		
		paraMap.put("reasonList", reasonDAO.findAll());
		paraMap.put("roleList", 
					roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));

		//processing status:
		paraMap.put("processingStatusList", getProcessingStatusDAO().findAll());
		
		paraMap.put("severityList", getSeverityDAO().findAll());
		
		paraMap.put("isParticipator", this.isParticipatorForIncident(req,incident));
		
		paraMap.put("effectList", this.getEffectDAO().findAll());
		
		paraMap.put("urgencyList", this.getUrgencyDAO().findAll());
		
		//关联系统
		if(configProperties == null) {
			configProperties = new Properties();
			try {
				configProperties.load(new FileInputStream(resource.getFile()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//影响系统
		if((incident.getMainInfluenceSystem() != null && incident.getMainInfluenceSystem().size() > 0)
				|| (incident.getRelationInfluenceSystem() != null && incident.getRelationInfluenceSystem().size() > 0)){
			String influenceSystemParentID = configProperties.getProperty("incident_influence_system_parent_id");
			List<HashMap<String, Object>> influenceSystemList = catDAO.findInfluenceSystem(influenceSystemParentID);
			paraMap.put("influenceSystemList", influenceSystemList);
			paraMap.put("catType", 1);
		}else{
			String influenceSystemParentID = configProperties.getProperty("incident_ci_parent_id");
			List<HashMap<String, Object>> influenceSystemList = ciDAO.findInfluenceSystem(influenceSystemParentID);
			paraMap.put("influenceSystemList", influenceSystemList);
			paraMap.put("catType", 2);
		}
		
		
		
		//查询事件是否绑定了解决方案
		Solution solution = kbDAO.findSolutionByIncident(incident);
		paraMap.put("solutionId", solution == null ? null : solution.getId());
		
		return new ModelAndView(this.getHandleView(), paraMap);
		
	}//handle()
	
	/*
	 * method to update incident info page
	 */
	public ModelAndView toUpdateIncidentInfoPage(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String roleIdListStr = "";
		String token[];
		List<Role> roleList;
		
		
		
		//get parameters from session:
		HttpSession session = req.getSession();
		roleIdListStr = (String)session.getAttribute("roleId");
		token = roleIdListStr.split("#");
		
		roleList = new ArrayList<Role>();
		for(int i=0;i<token.length;i++) {
			
			Role role = roleDAO.findById(new Integer(token[i]));
			roleList.add(role);
		}
		
		Person person = getCurrentPerson(session);
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		Short categoryId = incident.getCategory().getId();
		
		paraMap = this.getMap(incident);
		
		//check permissions:
		paraMap.put("canHandle", 
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDLE))));
		
		paraMap.put("canHandback", 
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDBACK))));
		
		paraMap.put("canHandover",
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDOVER))));
		
		paraMap.put("canReject",
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_REJECT))));
		
		
		paraMap.put("reasonList", reasonDAO.findAll());
		paraMap.put("roleList", 
				roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));
		
		//processing status:
		paraMap.put("processingStatusList", getProcessingStatusDAO().findAll());
		
		paraMap.put("severityList", getSeverityDAO().findAll());
		
		paraMap.put("isParticipator", this.isParticipatorForIncident(req,incident));
		
		paraMap.put("effectList", this.getEffectDAO().findAll());
		
		paraMap.put("urgencyList", this.getUrgencyDAO().findAll());
		
		//查询事件是否绑定了解决方案
		Solution solution = kbDAO.findSolutionByIncident(incident);
		paraMap.put("solutionId", solution == null ? null : solution.getId());
		return new ModelAndView(this.getToUpdateIncidentInfoPageView(), paraMap);
		
	}//handle()
	
	/**
	 * 获取事件基本信息
	 * @param incident
	 * @param paraMap
	 */
	public void getIncidentSimpleDataMap(Incident incident, Map<String,Object> paraMap) {
		if(incident == null) {
			return;
		}
		
		paraMap.put("incidentId", incident.getId());
		paraMap.put("incidentSubject", incident.getSubject());
		paraMap.put("incidentReqNo", incident.getRequestNo());
		paraMap.put("incidentCatName", incident.getCategory().getName());
		paraMap.put("incidentStatus", incident.getStatus());
	}
	
	
	public ModelAndView showCatLevelList(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map<String,Object> paraMap = new HashMap<String,Object>();
		List<CategoryWithLevel> list = this.catWithLevelDAO.findAll();
		List<Map<String, Object>> dataMapList = new ArrayList<Map<String,Object>>();
		if(list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++) {
				CategoryWithLevel data = list.get(i);
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("id", data.getId());
				dataMap.put("catId", data.getCategory().getId());
				dataMap.put("catName", data.getCategory().getName());
				dataMap.put("serviceLevel", data.getSeverity().getName());
				dataMapList.add(dataMap);
			}
		}
		
		paraMap.put("dataMapList", dataMapList);
		
		return new ModelAndView(this.getListCatLevelView(), paraMap);
	}
	
	/*
	 * method to handle incident
	 */
	public ModelAndView handleTicket(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String roleIdListStr = "";
		String token[];
		List<Role> roleList;
		boolean canDeassign = false;
		
		//get parameters from session:
		HttpSession session = req.getSession();
		roleIdListStr = (String)session.getAttribute("roleId");
		token = roleIdListStr.split("#");
		
		roleList = new ArrayList<Role>();
		for(int i=0;i<token.length;i++) {
			
			Role role = roleDAO.findById(new Integer(token[i]));
			roleList.add(role);
		}
		
		Person person = getCurrentPerson(session);
		
		//get parameters from http request:
		Long ticketId = new Long(req.getParameter("id"));
		
		IncidentTicket ticket = incidentDAO.findTicketById(ticketId);
		
		getIncidentSimpleDataMap(ticket.getIncident(), paraMap);
		
		if(!(ticket.getHandlers().contains(person) )) {
			//myself is only participator
			return detailTicket(req, res);
		}
		
		Short categoryId = ticket.getIncident().getCategory().getId();
		
		//paraMap = this.getMap(incidentTicket);
		paraMap.put("id", ticket.getId());
		paraMap.put("createTime", ticket.getCreateTime());
		paraMap.put("status", ticket.getStatus());
		paraMap.put("startTime", ticket.getStartTime());
		paraMap.put("finishTime", ticket.getFinishTime());
		
		paraMap.put("subject", ticket.getSubject());
		paraMap.put("content", ticket.getContent());
		paraMap.put("opinion", ticket.getReservedString2());
		paraMap.put("finishPercentage", ticket.getFinishPercentage());
		paraMap.put("remark", ticket.getRemark());
		
		Set<Person> handlers = ticket.getHandlers();
		Iterator<Person> iter = handlers.iterator();
		String handlerList = "";
		while(iter.hasNext()){
			Person handler = iter.next();
			handlerList += "," + handler.getName();
		}
		handlerList = handlerList.replaceFirst(",", "");
		
		paraMap.put("handlers", handlerList);
		
		if(ticket.getStatus() == IncidentTicket.STATUS_ASSIGNED || ticket.getStatus() == IncidentTicket.STATUS_PROCESSING){
			if(ticket.getIncident().getHandlers().contains(person)){
				canDeassign = true;
			}
		}	
		paraMap.put("canDeassign", canDeassign);
		
		//check permissions:
		
		paraMap.put("canHandle", 
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDLE))));

		paraMap.put("canHandback", 
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDBACK))));
		
		paraMap.put("canHandover",
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_HANDOVER))));
		
		paraMap.put("canReject",
				new Boolean(Utils.hasPermission(roleList, ItsmConstants.MODULE_INCIDENT,new Integer(categoryId), new Short(PermissionItem.OPERATION_REJECT))));

		
		paraMap.put("reasonList", reasonDAO.findAll());
		paraMap.put("roleList", 
					roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));

		return new ModelAndView(this.getHandleTicketView(), paraMap);
		
	}//handleTicket()
		
	
	/*
	 * method to save incident during processing
	 */
	public ModelAndView save(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String path, path1, path2, path3;
		MultipartFile multipartFile;
		String token[];
		ConfigurationItem ci;
		
		
		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person handler = getCurrentPerson(session);
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String finishPercentageStr = req.getParameter("finishPercentage");
		String processingStatusIdStr = req.getParameter("processingStatusId");
		String severityIdStr = req.getParameter("severityId");
		Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		String reservedInteger5 = req.getParameter("reservedInteger5");
		String reservedInteger4 = req.getParameter("reservedInteger4");
		String effectIdStr = req.getParameter("effectId");
		String urgencyIdStr = req.getParameter("urgencyId");
		String commonSubject = req.getParameter("commonSubject");
		String commonContent = req.getParameter("commonContent");
		String participatorsIds = req.getParameter("partIds");
		
		CustomInfo customInfo = this.getCustomInfoFromRequest(req);
		
		//get attachments:
		String fileIndexList = req.getParameter("fileIndexList");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
		
		//referenced changes:
		String changeIdListStr = req.getParameter("changeIdList");	//newly added changes
		String removedChangeIdListStr = req.getParameter("removedChangeIdList");	//removed changes
		
		String processingTestStatusIdStr = req.getParameter("processingTestStatusId");
		
		Incident incident = incidentDAO.findById(incidentId);
		
		Timestamp datetime6Old = incident.getCustomInfo().getDatetime6();
		
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		incident.setCategory(category);
		
		//更新类型时，超时通知时间变化
		Integer type = Integer.valueOf(req.getParameter("reservedInteger6"));
		if(!incident.getReservedInteger6().equals(type)){
			//事件类型值有修改，则更新sla
			if(configProperties == null) {
				configProperties = new Properties();
				configProperties.load(new FileInputStream(resource.getFile()));
			}
			String slaId = null;
			if(type.equals(Incident.TYPE_INCIDENT_FORM)){
				slaId = (String) configProperties.get("incident_sla_id");
			}else{
				slaId = (String) configProperties.get("request_sla_id");
			}
			
			Agreement agreement = agreementDAO.findById(Integer.valueOf(slaId));
			
			if(agreement != null) {
				incident.setSla(agreement);
			}
		}
 
		this.setReservedField(incident, req);
		
		if(StringUtils.isNotBlank(finishPercentageStr)) {
			
			incident.setFinishPercentage(new Short(finishPercentageStr));
			
		}
		
		if(StringUtils.isNotBlank(processingStatusIdStr)) {
			
			ProcessingStatus processingStatus = this.getProcessingStatusDAO().findById(new Short(processingStatusIdStr));
			incident.setProcessingStatus(processingStatus);
		}
		
		//增加处理中附加状态  测试中、测试完成
		if(StringUtils.isNotBlank(processingTestStatusIdStr)) {
			
			if(Short.valueOf(processingTestStatusIdStr)==Incident.STATUS_PROCESSING_TESTING||Short.valueOf(processingTestStatusIdStr)==Incident.STATUS_PROCESSING_TESTED){
				incident.setReservedInteger7(Integer.valueOf(processingTestStatusIdStr));
			}else{
				incident.setReservedInteger7(null);
			}
		}
		
		Severity severity = severityDAO.findById(new Short(severityIdStr));
		incident.setSeverity(severity);
		
		incident.setCustomInfo(customInfo);
		incident.setExpectFinishTime(expectFinishTime);
		if(null != reservedInteger5 && !reservedInteger5.equals("")){
			incident.setReservedInteger5(Integer.parseInt(reservedInteger5));
		}else{
			incident.setReservedInteger5(null);
		}
		if(reservedInteger4!=null && !reservedInteger4.equals("")){
			incident.setReservedInteger4(Integer.parseInt(reservedInteger4));
		}else{
			incident.setReservedInteger4(null);
		}
		
		
		Effect effect = effectDAO.findById(new Short(effectIdStr));
		incident.setEffect(effect);
		
		Urgency urgency = urgencyDAO.findById(new Short(urgencyIdStr));
		incident.setUrgency(urgency);
		
//		int priority = effect.getValue() * urgency.getValue();
//		incident.setPriority((short)priority);
		String priority = req.getParameter("priority");
		if(EntityUtils.isNotEmpty(priority)) {
			incident.setPriority(new Short(priority));
		} else {
			incident.setPriority(null);
		}
		
		incident.setSubject(commonSubject);
		incident.setContent(commonContent);
		
		String oldParIds = ",";
		for(Person person : incident.getParticipators()){
			oldParIds += person.getId() + ",";
		}
		
		if(StringUtils.isNotBlank(participatorsIds) && participatorsIds.length() > 0){
			
			String[] newParIds = participatorsIds.split(",");
			for(int n = 0; n < newParIds.length; n++){
				
				if(oldParIds.indexOf("," + newParIds[n] + ",") == -1){
					
					incident.getParticipators().add(personDAO.findById(new Integer(newParIds[n])));
				}
			}
		}	
		
		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		//主影响系统
		String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
		if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
			List<Category> mainInfluenceSysList = catDAO.findCatListByIDs(mainInfluenceSystemStr);
			if(mainInfluenceSysList.size()>0){
				incident.getMainInfluenceSystem().clear();
				incident.getMainInfluenceSystem().addAll(new HashSet<Category>(mainInfluenceSysList));
			}
		}
		//关联影响系统
		String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
		if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
			List<Category> relaInfluenceSysList = catDAO.findCatListByIDs(relaInfluenceSystemStr);
			if(relaInfluenceSysList.size()>0){
				incident.getRelationInfluenceSystem().clear();
				incident.getRelationInfluenceSystem().addAll(new HashSet<Category>(relaInfluenceSysList));
			}
		}

	    //add attachments:
		String attachmentPath = configProperties.getProperty("attachmentPath");
		
		path = attachmentPath + System.getProperty("file.separator") +
					"incident" + System.getProperty("file.separator") +
					"incident" + incident.getId() + System.getProperty("file.separator");

		path1 = path2 = path3 = null;
		
		int max_len = Document.MAX_PATH_LENGTH;
		if(path.length() > max_len) {
			String tmp = path;
			
			path1 = tmp.substring(0, max_len - 1);
			
			tmp = tmp.substring(max_len);
			if(tmp.length() > max_len) {
				
				path2 = tmp.substring(0, max_len - 1);
				
				tmp = tmp.substring(max_len);
				if(tmp.length() > max_len) {
					
					throw new ProcessException("Path_of_attachment_too_long");
					
				} else {
					
					path3 = tmp;
				}
				
			} else {
				
				path2 = tmp;
			}
			
		} else {
			
			path1 = path;
			
		}
		
		//get each file:
		if(fileIndexList.length() > 0) {
			
			token = fileIndexList.split(",");
			for (int i = 0; i < token.length; i++) {
				
				//get file from http request. covert it to MultipartHttpServletRequest first:
				multipartFile = multipartRequest.getFile("fileName" + token[i]);

				String docSubject = req.getParameter("subject" + token[i]);
				String author = req.getParameter("author" + token[i]);
				String number = req.getParameter("number" + token[i]);
				String directoryIdStr = req.getParameter("directoryId" + token[i]);
				String keywords = req.getParameter("keywords" + token[i]);
				String docDescription = req.getParameter("fileDescription" + token[i]);
	
				String fileName = multipartFile.getOriginalFilename();
				
				//create attachment object:
				Document attachment = new Document();
				attachment.setCreateTime(new Date(System.currentTimeMillis()));
				attachment.setOriginalFileName(fileName);
				attachment.setCreator(handler);
				
				//set type to incident:
				attachment.setType(Document.TYPE_INCIDENT);
				
				//set dummy to false, so it will be found in library
				attachment.setDummy(false);

				//use current time as new file name:
//				String newFileName = (new Long(System.currentTimeMillis())).toString();
				String newFileName = (new Long(System.currentTimeMillis())).toString() + "_" + i;
				attachment.setFileName(newFileName);
	
				if(docSubject.length() > 0)
					attachment.setSubject(docSubject);
				else
					attachment.setSubject(fileName);
				
				if(author.length() > 0)
					attachment.setAuthor(author);
				
				if(number.length() > 0)
					attachment.setNumber(number);
				
				if(keywords.length() > 0)
					attachment.setKeywords(keywords);
				
				if(docDescription.length() > 0)
					attachment.setDescription(docDescription);
				
				if(directoryIdStr.length() > 0) {
					
					Directory dir = libraryDAO.findDirectoryById(new Integer(directoryIdStr));
					attachment.setDirectory(dir);
					
				}
				
				//set path:
				attachment.setPath1(path1);
				attachment.setPath2(path2);
				attachment.setPath3(path3);
				
				//add it to task instance's attachments:
				incident.getAttachments().add(attachment);
				
				//upload the file to the server:
				try {
					
					File dir = new File(path);
					
					if(!dir.exists()) {
						
						dir.mkdirs();
					}
					
					File destinationFile = new File(path + System.getProperty("file.separator") + newFileName);
						
					multipartFile.transferTo(destinationFile);
					
				} catch(IOException e) {
					
					logger.error(e.getLocalizedMessage());
					
					throw e;
				}
				
			}
		
		}
		
		//remove reference changes:
		if(StringUtils.isNotBlank(removedChangeIdListStr)) {
			
			for(String changeIdStr : removedChangeIdListStr.split(",")) {
			
				for(Change change : incident.getReferencedChanges()) {
				
					if(change.getId().longValue() == Long.parseLong(changeIdStr)) {
						
						incident.getReferencedChanges().remove(change);
						break;
					}
				}
			
			}
		}
		
		//add reference changes:
		if(StringUtils.isNotBlank(changeIdListStr)) {
			
			for(String changeIdStr : changeIdListStr.split(",")) {
				
				Change change = changeDAO.findById(new Long(changeIdStr));
				incident.getReferencedChanges().add(change);
			}
		}
		
		//关联知识
		String kbIds = "";
		kbIds = req.getParameter("kbId");
		if (StringUtils.isNotBlank(kbIds)) {
			for(String kbId : kbIds.split(",")){
				Knowledge knowledge = kbDAO.findKnowledgeById(Long.valueOf(kbId));
				if(knowledge != null){
					incident.getKnowledges().add(knowledge);
				}
			}
		}
		//关联问题
		String problemIds = "";
		HashSet<Problem> problemSet = new HashSet<Problem>();
		problemIds = req.getParameter("problemId");
		if (problemIds!= null && problemIds.length() > 0) {
			for(String problemId : problemIds.split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		String relatedProblemIds = req.getParameter("relatedProblemIds");
		if (relatedProblemIds!= null && relatedProblemIds.length() > 0) {
			for(String problemId : relatedProblemIds.replaceAll("#", "").split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		incident.setProblems(problemSet);
		
		//add by czk begin
		//referenced requirements:
		String requirementIdListStr = req.getParameter("requirementIdList");	//newly added requirements
		String removedRequirementIdListStr = req.getParameter("removedRequirementIdList");	//removed requirements
		
		//remove reference requirements:
		if(StringUtils.isNotBlank(removedRequirementIdListStr)) {
			
			for(String requirementIdStr : removedRequirementIdListStr.split(",")) {
			
				for(Requirement requirement : incident.getReferencedRequirements()) {
				
					if(requirement.getId().longValue() == Long.parseLong(requirementIdStr)) {
						
						incident.getReferencedRequirements().remove(requirement);
						break;
					}
				}
			
			}
		}
		
		//add reference requirements:
		if(StringUtils.isNotBlank(requirementIdListStr)) {
			
			for(String requirementIdStr : requirementIdListStr.split(",")) {
				
				Requirement requirement = requirementDAO.findById(new Long(requirementIdStr));
				incident.getReferencedRequirements().add(requirement);
			}
		}
		
		int countWorkLog = Integer.parseInt(req.getParameter("countWorkLog"));
		for(int m = 1; m <= countWorkLog; m++){
			IncidentWorkLog workLog = new IncidentWorkLog();
			workLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
			workLog.setCreator(handler);
			workLog.setContent(req.getParameter("content" + m));
			
			incident.getWorkLogs().add(workLog);
		}
		//add by czk end
		
		String removedCIIdList = req.getParameter("removedCIIdList");
		if (removedCIIdList.length() > 0) {
			
			token = removedCIIdList.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().remove(ci);
				} else {
					logger.error("CI(id=" + token[i] + ") not found!");
				}
			}
		}
		String ciIdListStr = req.getParameter("ciIdList");
		if (ciIdListStr.length() > 0) {
			
			token = ciIdListStr.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().add(ci);
				} else {
					logger.error("CI(id=" + token[i] + ") not found!");
				}
			}
		}
		
		incident.setPlannedOutage(StringUtils.isNotBlank(req.getParameter("plannedOutage"))? true:false);
		
		String para = req.getParameter("datetime6");
		if(datetime6Old==null && para!=null && para.length() >0){
			
			IncidentActivity activity = new IncidentActivity();
			
			activity.setType(new Short(IncidentActivity.TYPE_UPDATE));
			activity.setPerson(handler);
			activity.setTime(new Timestamp(System.currentTimeMillis()));
			activity.setComment("更新承诺完成时间："+para);
			
			incident.getActivities().add(activity);
		}else{
			
			IncidentActivity activity = new IncidentActivity();
			
			activity.setType(new Short(IncidentActivity.TYPE_UPDATE));
			activity.setPerson(handler);
			activity.setTime(new Timestamp(System.currentTimeMillis()));
			
			incident.getActivities().add(activity);
			
		}
		
		incidentDAO.update(incident);
		logger.info("incident(id=" + incidentId + ") was updated.");
		
		//set return map:
		paraMap.put("windowLocation", "incident_action.do?operation=handle&id=" + incidentId);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//save()


	
	
	/*
	 * method to take over incident
	 */
	public ModelAndView takeover(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		Incident incident = null;
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		/* removed, 2010-07-30
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		*/

		String comment = req.getParameter("comment");
		
		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person handler = getCurrentPerson(session);
		
		try {
		
			incident = incidentDAO.findByIdForUpdate(incidentId);
		
		}catch(Exception e) {
			
			paraMap.put("error", "it_has_already_been_taken_over_by_other_person");
			
			return new ModelAndView(getErrorView(), paraMap);
		}
		
		if (incident.getStatus() != Incident.STATUS_ASSIGNED) {
			
			paraMap.put("error", "it_has_already_been_taken_over_by_other_person");
			
			return new ModelAndView(this.getErrorView(), paraMap);
		}
		
		/* removed, 2010-07-30
		if(!incident.getCategory().getId().equals(categoryId))
			incident.setCategory(category);
		*/
		
		incident.getHandlers().clear();
		incident.getHandlers().add(handler);
		
		incident.setStatus(new Short(Incident.STATUS_PROCESSING));
		if(incident.getStartTime() == null)	//set only if it has not been taken over before!!!!
			incident.setStartTime(new Timestamp(System.currentTimeMillis()));
		//更新事件类型
		if(null != req.getParameter("reservedInteger6")){
			incident.setReservedInteger6(Integer.parseInt(req.getParameter("reservedInteger6")));
		}
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_TAKEOVER));
		activity.setPerson(handler);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		incident.getActivities().add(activity);
		
		if(null == incident.getResponseDelay()){
			incident.setResponseDelay(false);
		}
		
		incidentDAO.update(incident);
		
		logger.info("Incident(id=" + incidentId + ") was taken over by " + handler.getName());
		
		//set return map:
		paraMap.put("parentReload", true);
		paraMap.put("windowLocation", "incident_action.do?operation=handle&id=" + incidentId);
		
		//for trigger:
		paraMap.put("id", incident.getId());
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_TAKEOVER, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//takeover()
	
	/*
	 * method to take over incident
	 */
	public ModelAndView takeoverTicket(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		IncidentTicket ticket = null;
		
		
		//get parameters from http request:
		Long ticketId = new Long(req.getParameter("id"));
		
		/* removed, 2010-07-30
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		*/

		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person handler = getCurrentPerson(session);
		
		try {
		
			ticket = incidentDAO.findTicketByIdForUpdate(ticketId);
		
		}catch(Exception e) {
			
			paraMap.put("error", "it_has_already_been_taken_over_by_other_person");
			
			return new ModelAndView(getErrorView(), paraMap);
		}
		
		if (ticket.getStatus() != IncidentTicket.STATUS_ASSIGNED) {
			
			paraMap.put("error", "it_has_already_been_taken_over_by_other_person");
			
			return new ModelAndView(this.getErrorView(), paraMap);
		}
		
		/* removed, 2010-07-30
		if(!incident.getCategory().getId().equals(categoryId))
			incident.setCategory(category);
		*/
		
		ticket.getHandlers().clear();
		ticket.getHandlers().add(handler);
		
		ticket.setStatus(new Short(IncidentTicket.STATUS_PROCESSING));
		if(ticket.getStartTime() == null)	//set only if it has not been taken over before!!!!
			ticket.setStartTime(new Timestamp(System.currentTimeMillis()));
		
		incidentDAO.updateTicket(ticket);
		
		logger.info("Ticket(id=" + ticketId + ") was taken over by " + handler.getName());
		
		//set return map:
		paraMap.put("parentReload", true);
		paraMap.put("windowLocation", "incident_action.do?operation=handleTicket&id=" + ticketId);
		
		//for trigger:
		paraMap.put("id", ticket.getId());
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//takeoverTicket()

	/*
	 * method to finish the incident
	 */
	public ModelAndView finishTicket(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		String incidentIdStr = req.getParameter("incidentId");
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String token[];
		
		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person handler = getCurrentPerson(session);
		
		//get parameters from http request:
		Long ticketId = new Long(req.getParameter("id"));
		//Short finishPercentage = new Short(req.getParameter("finishPercentage"));

		//find from DB:
		IncidentTicket ticket = incidentDAO.findTicketById(ticketId);
		
		//ticket.setStatus("0");
		ticket.setFinishTime(new Timestamp(System.currentTimeMillis()));
		//ticket.setFinishPercentage(finishPercentage);
		ticket.setFinishPercentage((short)100);
		ticket.setRemark(req.getParameter("remark"));
		ticket.setReservedString1(req.getParameter("solution"));
		
		ticket.setStatus(IncidentTicket.STATUS_CLOSED);
		incidentDAO.updateTicket(ticket);
		
		logger.info("Ticket(id=" + ticketId + ") was finished by " + handler.getName());
		paraMap.put("parentReload", true);
		
		//for trigger
		paraMap.put("id", ticketId);
		
		Incident incident = incidentDAO.findById(new Long(incidentIdStr));
		
		//增加“完成子工单”动作的消息通知
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_FINISH_TICKET, incident, req, ticket);
		} catch (Exception e) {
			logger.error("finish menthod sendNotice error : "+Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
	
	}//finishTicket()
	
	public void ifAllRelatedChangeClosed(HttpServletRequest req, HttpServletResponse res) throws IOException{

		String responseText = "";
		Long incidentId = new Long(req.getParameter("id"));
		Incident incident = incidentDAO.findById(incidentId);
		
		for(Change change : incident.getChanges()){
			if(change.getStatus() != Change.STATUS_CLOSED 
					&& change.getStatus() != Change.STATUS_REJECTED 
					&& change.getStatus() != Change.STATUS_ANNULLED){
				responseText = "请先关闭该事件关联的变更！";
				break;
			}
		}
		res.setContentType("text/html;charset=utf-8");
		PrintWriter writer = res.getWriter();
		writer.print(responseText);
		writer.flush();
		writer.close();
	}
	
	/*
	 * method to finish the incident
	 */
	public ModelAndView finish(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();

		String token[];
		ConfigurationItem ci;
		
		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person handler = this.getCurrentPerson(session);
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		Short closeType = new Short(req.getParameter("closeType"));
		String subject = req.getParameter("subject");
		String solution = req.getParameter("solution");
		String addToKnowledgeBase = req.getParameter("addToKnowledgeBase");
		Timestamp expectFinishTime = Timestamp.valueOf(req.getParameter("expectFinishTime"));
		String reservedInteger5 = req.getParameter("reservedInteger5");
		String reservedInteger4 = req.getParameter("reservedInteger4");
		String severityIdStr = req.getParameter("severityId");
		String effectIdStr = req.getParameter("effectId");
		String urgencyIdStr = req.getParameter("urgencyId");
		String priority = req.getParameter("priority");
		String commonSubject = req.getParameter("commonSubject");
		String commonContent = req.getParameter("commonContent");
		String participatorsIds = req.getParameter("partIds");
		String conclusion = req.getParameter("conclusion");//结论
		
		//find from DB:
		Incident incident = incidentDAO.findById(incidentId);
		
		this.setReservedField(incident, req);
		
		//get custom info:
		CustomInfo customInfo = this.getCustomInfoFromRequest(req);
		incident.setCustomInfo(customInfo);
		
		//referenced changes:
		String changeIdListStr = req.getParameter("changeIdList");	//newly added changes
		String removedChangeIdListStr = req.getParameter("removedChangeIdList");	//removed changes

		//get properties from configuration file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}

		//get and add attachments:
		this.uploadAttachment(req, handler, incident);
		
		incident.setCloseType(closeType);
		incident.setSolution(solution);
		incident.setSubject(subject);
		incident.setFinishTime(new Timestamp(System.currentTimeMillis()));
		incident.setConclusion(conclusion);
		
		//clear processing status and set finish percentage to 100%, 2010-01-13
		incident.setProcessingStatus(null);
		incident.setFinishPercentage((short)100);
		
		Severity severity = severityDAO.findById(new Short(severityIdStr));
		incident.setSeverity(severity);
		
		incident.setExpectFinishTime(expectFinishTime);
		
		if(!reservedInteger5.equals("")){
			incident.setReservedInteger5(Integer.parseInt(reservedInteger5));
		}
		
		if(!reservedInteger4.equals("")){
			incident.setReservedInteger4(Integer.parseInt(reservedInteger4));	
		}else{
			incident.setReservedInteger4(null);
		}
		
		Effect effect = effectDAO.findById(new Short(effectIdStr));
		incident.setEffect(effect);
		
		Urgency urgency = urgencyDAO.findById(new Short(urgencyIdStr));
		incident.setUrgency(urgency);
		
		if(EntityUtils.isNotEmpty(priority)) {
			incident.setPriority(new Short(priority));
		} else {
			incident.setPriority(null);
		}
		
		incident.setSubject(commonSubject);
		incident.setContent(commonContent);
		
		//事件类别
		Short categoryId = new Short(req.getParameter("categoryId"));
		Category category = catDAO.findById(categoryId);
		incident.setCategory(category);
				
		String catType = req.getParameter("catType");
		if(StringUtils.isNotBlank(catType)){
			if(Integer.valueOf(catType) == 1){
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<Category> mainInfluenceSysList = catDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainInfluenceSystem().clear();
						incident.getMainInfluenceSystem().addAll(new HashSet<Category>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<Category> relaInfluenceSysList = catDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationInfluenceSystem().clear();
						incident.getRelationInfluenceSystem().addAll(new HashSet<Category>(relaInfluenceSysList));
					}
				}
			}else{
				//主影响系统
				String mainInfluenceSystemStr = req.getParameter("mainInfluenceSystem");
				if(StringUtils.isNotBlank(mainInfluenceSystemStr)){
					List<ConfigurationItem> mainInfluenceSysList = ciDAO.findCatListByIDs(mainInfluenceSystemStr);
					if(mainInfluenceSysList.size()>0){
						incident.getMainCi().clear();
						incident.getMainCi().addAll(new HashSet<ConfigurationItem>(mainInfluenceSysList));
					}
				}
				//关联影响系统
				String relaInfluenceSystemStr = req.getParameter("relaInfluenceSystem");
				if(StringUtils.isNotBlank(relaInfluenceSystemStr)){
					List<ConfigurationItem> relaInfluenceSysList = ciDAO.findCatListByIDs(relaInfluenceSystemStr);
					if(relaInfluenceSysList.size()>0){
						incident.getRelationCi().clear();
						incident.getRelationCi().addAll(new HashSet<ConfigurationItem>(relaInfluenceSysList));
					}
				}
			}
		}
		
		
		String oldParIds = ",";
		for(Person person : incident.getParticipators()){
			oldParIds += person.getId() + ",";
		}
		if(StringUtils.isNotBlank(participatorsIds) && participatorsIds.length() > 0){
			String[] newParIds = participatorsIds.split(",");
			for(int n = 0; n < newParIds.length; n++){
				if(oldParIds.indexOf("," + newParIds[n] + ",") == -1){
					incident.getParticipators().add(personDAO.findById(new Integer(newParIds[n])));
				}
			}
		}	
		
		//remove reference changes:
		if(StringUtils.isNotBlank(removedChangeIdListStr)) {
			
			for(String changeIdStr : removedChangeIdListStr.split(",")) {
			
				for(Change change : incident.getReferencedChanges()) {
				
					if(change.getId().longValue() == Long.parseLong(changeIdStr)) {
						
						incident.getReferencedChanges().remove(change);
						break;
					}
				}
			
			}
		}
		
		//add reference changes:
		if(StringUtils.isNotBlank(changeIdListStr)) {
			
			for(String changeIdStr : changeIdListStr.split(",")) {
				
				Change change = changeDAO.findById(new Long(changeIdStr));
				incident.getReferencedChanges().add(change);
			}
		}
		
		//关联知识
		String kbIds = "";
		kbIds = req.getParameter("kbId");
		if (StringUtils.isNotBlank(kbIds)) {
			for(String kbId : kbIds.split(",")){
				Knowledge knowledge = kbDAO.findKnowledgeById(Long.valueOf(kbId));
				if(knowledge != null){
					incident.getKnowledges().add(knowledge);
				}
			}
		}
		
		//关联问题
		String problemIds = "";
		HashSet<Problem> problemSet = new HashSet<Problem>();
		problemIds = req.getParameter("problemId");
		if (problemIds!= null && problemIds.length() > 0) {
			for(String problemId : problemIds.split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		String relatedProblemIds = req.getParameter("relatedProblemIds");
		if (relatedProblemIds!= null && relatedProblemIds.length() > 0) {
			for(String problemId : relatedProblemIds.replaceAll("#", "").split(",")){
				Problem problem = problemDAO.findById(Long.valueOf(problemId));
				if(problem != null){
					problemSet.add(problem);
				}
			}
		}
		incident.setProblems(problemSet);
		
		//add by czk begin
		//referenced requirements:
		String requirementIdListStr = req.getParameter("requirementIdList");	//newly added requirements
		String removedRequirementIdListStr = req.getParameter("removedRequirementIdList");	//removed requirements
		
		//remove reference requirements:
		if(StringUtils.isNotBlank(removedRequirementIdListStr)) {
			
			for(String requirementIdStr : removedRequirementIdListStr.split(",")) {
			
				for(Requirement requirement : incident.getReferencedRequirements()) {
				
					if(requirement.getId().longValue() == Long.parseLong(requirementIdStr)) {
						
						incident.getReferencedRequirements().remove(requirement);
						break;
					}
				}
			
			}
		}
		
		//add reference requirements:
		if(StringUtils.isNotBlank(requirementIdListStr)) {
			
			for(String requirementIdStr : requirementIdListStr.split(",")) {
				
				Requirement requirement = requirementDAO.findById(new Long(requirementIdStr));
				incident.getReferencedRequirements().add(requirement);
			}
		}
		
		int countWorkLog = Integer.parseInt(req.getParameter("countWorkLog"));
		for(int m = 1; m <= countWorkLog; m++){
			IncidentWorkLog workLog = new IncidentWorkLog();
			workLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
			workLog.setCreator(handler);
			workLog.setContent(req.getParameter("content" + m));
			incident.getWorkLogs().add(workLog);
		}
		//add by czk end
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		activity.setType(new Short(IncidentActivity.TYPE_FINISH));
		activity.setPerson(handler);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		incident.getActivities().add(activity);		
		
		//check if it needs review:
		if(incident.getCategory().getNeedFeedback() && (incident.getCreator() != null)) {
			incident.setStatus(new Short(Incident.STATUS_FEEDBACKING));
			//for creator to review:
			incident.setFeedbacker(incident.getCreator());
				
		} else {
			ProcessDefinition postProcessDefinition = null;
			IncidentProcessDefine ipdPost = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_POST);
			if(ipdPost != null) {
				postProcessDefinition = ipdPost.getProcessDefine();
			}
			if(postProcessDefinition != null) {
				synchronized(IncidentActionController.class){
					if(incident.getStatus() == Incident.STATUS_PROCESSING){
						ProcessInstance postProcessInstance = new ProcessInstance(postProcessDefinition);
						
						postProcessInstance.setInitiatorId(handler.getId());
						
						postProcessInstance.setCategory(new Short(ProcessInstance.CATEGORY_INCIDENT));
						postProcessInstance.setExternalId(incident.getId());
						
						//add location and organization of applicant to global data of process instance:
						Person applicant = incident.getApplicant();
						if(applicant != null){
							Location location = applicant.getLocation();
							postProcessInstance.getGlobalData().put("applicantLocation", location.getId() + ":" + location.getPath());
							
							Organization organization = applicant.getOrganization();
							postProcessInstance.getGlobalData().put("applicantOrganization", organization.getId()+ ":" + organization.getPath());
							
							//add location and organization of creator to global data of process instance:
							if(incident.getCreator() != null){
								location = incident.getCreator().getLocation();
								postProcessInstance.getGlobalData().put("creatorLocation", location.getId() + ":" + location.getPath());
								
								organization = incident.getCreator().getOrganization();
								postProcessInstance.getGlobalData().put("creatorOrganization", organization.getId()+ ":" + organization.getPath());
							}
						}
						
						//start process instance:
						postProcessInstance.begin();
						
						//store the process instance to DB:
						processDAO.saveProcessInstance(postProcessInstance);
						logger.info("Process instance(id=" + postProcessInstance.getId() + ") was started.");
						
						//create notice for the first task:
						//去除系统自动消息通知
						//get urls from configuration file:
	//				String incidentEditTaskUrl = configProperties.getProperty("incidentEditTaskURL");
	//				String incidentViewUrl = configProperties.getProperty("incidentViewURL");
	//				
	//				NodeInstance nodeInstance = postProcessInstance.getToken().getCurrentNodeInstance();
	//				
	//				short node_type = nodeInstance.getNode().getType().shortValue();
	//				if(node_type == Node.TYPE_STEP || node_type == Node.TYPE_APPROVAL) {
	//					
	//					TaskInstance firstTaskInstance = (TaskInstance)nodeInstance.getTaskInstances().get(0);
	//					
	//					//String subject = configProperties.getProperty("newTaskSubject");
	//					String message = configProperties.getProperty("newTaskMessage");
	//					
	//					String editLink = incidentEditTaskUrl +
	//									"&id=" + incident.getId() +
	//									"&taskInstanceId=" + firstTaskInstance.getId();
	//					String viewLink = incidentViewUrl +
	//									"&id=" + incident.getId();
	//			
	//					Notice notice = Notice.newNoticeTemplate(postProcessInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
	//										null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
	//					
	//					noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
	//				
	//				} else {
	//					//TYPE_FORK:
	//					
	//					//String subject = configProperties.getProperty("newTaskSubject");
	//					String message = configProperties.getProperty("newTaskMessage");
	//		
	//					List<Token> childTokenList = postProcessInstance.getToken().getChildren();
	//					for(int i=0;i<childTokenList.size();i++) {
	//						
	//						Token childToken = childTokenList.get(i);
	//						
	//						//we assume only step nodes here:
	//						TaskInstance firstTaskInstance = (TaskInstance)childToken.getCurrentNodeInstance().getTaskInstances().get(0);
	//						
	//						String editLink = incidentEditTaskUrl +
	//										"&id=" + incident.getId() +
	//										"&taskInstanceId=" + firstTaskInstance.getId();
	//						String viewLink = incidentViewUrl +
	//										"&id=" + incident.getId();
	//				
	//						Notice notice = Notice.newNoticeTemplate(postProcessInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
	//											null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
	//						
	//						noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
	//					}
	//					
	//				}
						
						//store id of process instance to incident object:
						incident.getPostProcessInstanceIds().add(postProcessInstance.getId());
						//incident.setStatus(new Short(Incident.STATUS_ASSESSING));
						incident.setStatus(new Short(Incident.STATUS_TOREVIEW));
						
						activity = new IncidentActivity();
						activity.setType(IncidentActivity.TYPE_ASSESS);
						activity.setTime(new Timestamp(System.currentTimeMillis()));
						activity.setPerson(handler);
						
						incident.getActivities().add(activity);
					}
				}
				
			} else {
				//close the incident:
				incident.setStatus(new Short(Incident.STATUS_CLOSED));
				incident.setCloseTime(new Timestamp(System.currentTimeMillis()));

			}

		}	
		
		incident.setPlannedOutage(StringUtils.isNotBlank(req.getParameter("plannedOutage"))? true:false);
		
		incidentDAO.update(incident);
		
		logger.info("Incident(id=" + incidentId + ") was finished by " + handler.getName());
		
		//add to knowledge base:
		String createSolutionMode = req.getParameter("createSolutionMode");
		if(addToKnowledgeBase != null) {
			//查询事件是否绑定了解决方案
			Solution kbSolution = kbDAO.findSolutionByIncident(incident);
			if(kbSolution == null || "new".equals(createSolutionMode)) {
				kbSolution = new Solution();
			}
			
			kbSolution.setType(new Short(Solution.TYPE_INCIDENT));
			kbSolution.setCategoryId(incident.getCategory().getId());
			
			kbSolution.setSubject(subject);
			kbSolution.setSymptom(incident.getContent());
			kbSolution.setContent(solution);
			
			kbSolution.setCreateTime(new Timestamp(System.currentTimeMillis()));
			kbSolution.setCreator(handler);
			
			kbSolution.setOpen(false);
			kbSolution.setStatus(new Short(Solution.STATUS_ASSESSING));
			kbSolution.setIncident(incident);
			kbDAO.insertSolution(kbSolution);
		}
		
		paraMap.put("parentReload", true);
		
		String removedCIIdList = req.getParameter("removedCIIdList");
		if (removedCIIdList.length() > 0) {
			
			token = removedCIIdList.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().remove(ci);
				} else {
					logger.error("CI(id=" + token[i] + ") not found!");
				}
			}
		}
		String ciIdListStr = req.getParameter("ciIdList");
		if (ciIdListStr.length() > 0) {
			
			token = ciIdListStr.split(",");
			for (int i = 0; i < token.length; i++) {
				ci = ciDAO.findById(new Long(token[i]));
				if (null != ci) {
					incident.getCIs().add(ci);
				} else {
					logger.error("CI(id=" + token[i] + ") not found!");
				}
			}
		}
		
		//for trigger
		paraMap.put("id", incidentId);
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_FINISH, incident, req);
		} catch (Exception e) {
			logger.error("finish menthod sendNotice error : "+Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
	
	}//finish()

	/**
	 * 上传附件
	 * 
	 * @param req
	 * @param handler
	 * @param incident
	 * @throws IOException
	 */
	private void uploadAttachment(HttpServletRequest req, Person handler,Incident incident) throws IOException {
		String path;
		String path1;
		String path2;
		String path3;
		MultipartFile multipartFile;
		String[] token;
		String fileIndexList = req.getParameter("fileIndexList");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)req;
		
		String attachmentPath = configProperties.getProperty("attachmentPath");
		
		path = attachmentPath + System.getProperty("file.separator") +
					"incident" + System.getProperty("file.separator") +
					"incident" + incident.getId() + System.getProperty("file.separator");

		path1 = path2 = path3 = null;
		
		int max_len = Document.MAX_PATH_LENGTH;
		if(path.length() > max_len) {
			String tmp = path;
			
			path1 = tmp.substring(0, max_len - 1);
			
			tmp = tmp.substring(max_len);
			if(tmp.length() > max_len) {
				
				path2 = tmp.substring(0, max_len - 1);
				
				tmp = tmp.substring(max_len);
				if(tmp.length() > max_len) {
					
					throw new ProcessException("Path_of_attachment_too_long");
					
				} else {
					
					path3 = tmp;
				}
				
			} else {
				
				path2 = tmp;
			}
			
		} else {
			
			path1 = path;
			
		}
		
		//get each file:
		if(fileIndexList.length() > 0) {
			
			token = fileIndexList.split(",");
			for (int i = 0; i < token.length; i++) {
				
				//get file from http request. covert it to MultipartHttpServletRequest first:
				multipartFile = multipartRequest.getFile("fileName" + token[i]);

				String docSubject = req.getParameter("subject" + token[i]);
				String author = req.getParameter("author" + token[i]);
				String number = req.getParameter("number" + token[i]);
				String directoryIdStr = req.getParameter("directoryId" + token[i]);
				String keywords = req.getParameter("keywords" + token[i]);
				String docDescription = req.getParameter("fileDescription" + token[i]);
	
				String fileName = multipartFile.getOriginalFilename();
				
				//create attachment object:
				Document attachment = new Document();
				attachment.setCreateTime(new Date(System.currentTimeMillis()));
				attachment.setOriginalFileName(fileName);
				attachment.setCreator(handler);
				
				//set type to incident:
				attachment.setType(Document.TYPE_INCIDENT);
				
				//set dummy to false, so it will be found in library
				attachment.setDummy(false);

				//use current time as new file name:
//				String newFileName = (new Long(System.currentTimeMillis())).toString();
				String newFileName = (new Long(System.currentTimeMillis())).toString() + "_" + i;
				attachment.setFileName(newFileName);
	
				if(docSubject.length() > 0)
					attachment.setSubject(docSubject);
				else
					attachment.setSubject(fileName);
				
				if(author.length() > 0)
					attachment.setAuthor(author);
				
				if(number.length() > 0)
					attachment.setNumber(number);
				
				if(keywords.length() > 0)
					attachment.setKeywords(keywords);
				
				if(docDescription.length() > 0)
					attachment.setDescription(docDescription);
				
				if(directoryIdStr.length() > 0) {
					
					Directory dir = libraryDAO.findDirectoryById(new Integer(directoryIdStr));
					attachment.setDirectory(dir);
					
				}
				
				//set path:
				attachment.setPath1(path1);
				attachment.setPath2(path2);
				attachment.setPath3(path3);
				
				//add it to task instance's attachments:
				incident.getAttachments().add(attachment);
				
				//upload the file to the server:
				try {
					
					File dir = new File(path);
					
					if(!dir.exists()) {
						
						dir.mkdirs();
					}
					
					File destinationFile = new File(path + System.getProperty("file.separator") + newFileName);
						
					multipartFile.transferTo(destinationFile);
					
				} catch(IOException e) {
					
					logger.error(e.getLocalizedMessage());
					
					throw e;
				}
				
			}
		
		}
	}
	
	private void setReservedField(Incident incident, HttpServletRequest req) {
		
		// reservedInteger
		String reservedInteger1 = req.getParameter("reservedInteger1");
		String reservedInteger2 = req.getParameter("reservedInteger2");
		String reservedInteger3 = req.getParameter("reservedInteger3"); // 事件原因(IncidentCause)id(完成和保存时添加)
		String reservedInteger4 = req.getParameter("reservedInteger4"); // 分类二的标识
		String reservedInteger5 = req.getParameter("reservedInteger5"); // 分类一 id （创建时添加）
		String reservedInteger6 = req.getParameter("reservedInteger6");
		String reservedInteger7 = req.getParameter("reservedInteger7");
		String reservedInteger8 = req.getParameter("reservedInteger8");
		String reservedInteger9 = req.getParameter("reservedInteger9");
		String reservedInteger10 = req.getParameter("reservedInteger10");
		
		if (StringUtils.isNotBlank(reservedInteger1)) {
			incident.setReservedInteger1(new Integer(reservedInteger1));
		}
		
		if (StringUtils.isNotBlank(reservedInteger2)) {
			incident.setReservedInteger2(new Integer(reservedInteger2));
		}
		
		if (StringUtils.isNotBlank(reservedInteger3)) {
			incident.setReservedInteger3(new Integer(reservedInteger3));
		}
		
		if (StringUtils.isNotBlank(reservedInteger4)) {
			incident.setReservedInteger4(new Integer(reservedInteger4));
		}
		
		if (StringUtils.isNotBlank(reservedInteger5)) {
			incident.setReservedInteger5(new Integer(reservedInteger5));
		}
		
		if (StringUtils.isNotBlank(reservedInteger6)) {
			incident.setReservedInteger6(new Integer(reservedInteger6));
		}
		
		if (StringUtils.isNotBlank(reservedInteger7)) {
			incident.setReservedInteger7(new Integer(reservedInteger7));
		}
		
		if (StringUtils.isNotBlank(reservedInteger8)) {
			incident.setReservedInteger8(new Integer(reservedInteger8));
		}
		
		if (StringUtils.isNotBlank(reservedInteger9)) {
			incident.setReservedInteger9(new Integer(reservedInteger9));
		}
		
		if (StringUtils.isNotBlank(reservedInteger10)) {
			incident.setReservedInteger10(new Integer(reservedInteger10));
		}
		
		// reservedString
		String reservedString1 = req.getParameter("reservedString1"); // 姓名
		String reservedString2 = req.getParameter("reservedString2"); // 工号
		String reservedString3 = req.getParameter("reservedString3"); // 单位
		String reservedString4 = req.getParameter("reservedString4"); // 联系电话
		String reservedString5 = req.getParameter("reservedString5"); // 全拼
		String reservedString6 = req.getParameter("reservedString6"); // 申请单位
		String reservedString7 = req.getParameter("reservedString7"); // 业务负责人
		String reservedString8 = req.getParameter("reservedString8");
		String reservedString9 = req.getParameter("reservedString9");  //事件定性
		String reservedString10 = req.getParameter("reservedString10"); //模板名称
		
		AutocompleteUtils.regData(AutocompleteUtils.APPL_PSN, reservedString1);
		AutocompleteUtils.regData(AutocompleteUtils.WORK_NUM, reservedString2);
		AutocompleteUtils.regData(AutocompleteUtils.WORK_UNIT, reservedString3);
		AutocompleteUtils.regData(AutocompleteUtils.PHONE_NUM, reservedString4);
		AutocompleteUtils.regData(AutocompleteUtils.DEPARTMENT, reservedString6);
		
		incident.setReservedString1(reservedString1);
		incident.setReservedString2(reservedString2);
		incident.setReservedString3(reservedString3);
		incident.setReservedString4(reservedString4);
		if(reservedString5!=null){
			incident.setReservedString5(reservedString5);
		}else{
			incident.setReservedString5(PinYinUtils.getPinYin(reservedString1));
		}
		
		incident.setReservedString6(reservedString6);
		incident.setReservedString7(reservedString7);
		incident.setReservedString8(reservedString8);
		incident.setReservedString9(reservedString9);
		incident.setReservedString10(reservedString10);
	}

	/*
	 * method to hand over the incident to another person
	 */
	public ModelAndView handover(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String token[];
		String handlerNameList = "";
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String comment = req.getParameter("comment");
		Boolean sendMessage = new Boolean(req.getParameter("sendMessage"));
		
		Incident incident = incidentDAO.findById(incidentId);

		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person currentHandler = getCurrentPerson(session);
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_HANDOVER));
		activity.setPerson(currentHandler);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		//set participators:
		String participatorPersonIdListStr = req.getParameter("participatorPersonId");
		token = participatorPersonIdListStr.split(",");
		
		//clear participator:
		//incident.getParticipators().clear();
		
		if (participatorPersonIdListStr.length() > 0) {
		
			for (int i=0; i<token.length; i++) {
			
				Person participator = personDAO.findById(new Integer(token[i]));
			
				incident.getParticipators().add(participator);
			
			}
		}
		
		// 旧的处理人的领导在参与人中去除
		for (Person handler:incident.getHandlers()) {
			incident.getParticipators().removeAll((this.roleDAO.findLeadersByMemberId(handler.getId())));
		}
		
		//将当前的处理人加到“参与人”中去
		for(Person handler : incident.getHandlers()){
			incident.getParticipators().add(handler);
		}
		
		String handlerPersonIdListStr = req.getParameter("handlerPersonId");
		token = handlerPersonIdListStr.split(",");
		
		//clear the handler
		incident.getHandlers().clear();

		for(int i=0;i<token.length;i++) {
		
			Integer handlerPersonId = new Integer(token[i]);
			
			// 新处理人的领导添加到参与人中
			incident.getParticipators().addAll(this.roleDAO.findLeadersByMemberId(handlerPersonId));
			
			Person handler = personDAO.findById(handlerPersonId);
			
			incident.getHandlers().add(handler);
			activity.getToPersons().add(handler);
			
			if(handlerNameList.length() == 0)
				handlerNameList = handler.getName();
			else
				handlerNameList += ", " + handler.getName();
			
		}
		
		//set status to assigned
		incident.setStatus(new Short(Incident.STATUS_ASSIGNED));
		
		//set sendMessage:
		incident.setSendMessage(sendMessage);
		
		if(sendMessage) {
		
			incident.setSendSuccess(sendMessage(incident));
		}
		
		//add activity:
		incident.getActivities().add(activity);
		
		//转交后工单不在对接管超时进行提醒
		if(null == incident.getResponseDelay()){
			incident.setResponseDelay(false);
		}
		
		//call DAO to update:
		incidentDAO.update(incident);
		logger.info("Incident(id=" + incidentId + ") was handed over to " +  handlerNameList + " by " + currentHandler.getName());
		
		paraMap.put("parentReload", true);
		
		//for trigger:
		paraMap.put("id", incidentId);
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_HANDOVER, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);	
		
	}//handover()
	
	//**********2010.9.28  椹姞   鎶婃祦绋嬮噸鍋�1锟�7**********************************
	public ModelAndView rebound(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String comment = req.getParameter("comment");
		
		Incident incident = incidentDAO.findById(incidentId);

		Person feedbacker = incident.getFeedbacker();

		//create activity:
		IncidentActivity activity = new IncidentActivity();
	
		activity.setType(new Short(IncidentActivity.TYPE_REBOUND));
		activity.setPerson(feedbacker);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		incident.getActivities().add(activity);
		
		//set status to closed
		incident.setStatus(new Short(Incident.STATUS_PROCESSING));
		incident.setCloseTime(new Timestamp(System.currentTimeMillis()));
		
		incidentDAO.update(incident);
		
		logger.info("Incident(id=" + incidentId + ") was rebounded by " +  feedbacker.getName());
		
		
		paraMap.put("parentReload", true);
		
		//for trigger:
		paraMap.put("id", incidentId);
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_REBOUND, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//rebound()	
		
	/*
	 * method to close incident
	 */
	public ModelAndView close(HttpServletRequest req, HttpServletResponse res)throws Exception {
		
		if (configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		Map<String,Object> paraMap = new HashMap<String,Object>();

		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person handler = getCurrentPerson(session);
		
		//Short reasonId = new Short(req.getParameter("reasonId"));
		String subject = req.getParameter("subject");		
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String comment = req.getParameter("comment");
		
		Incident incident = incidentDAO.findById(incidentId);

		Person feedbacker = incident.getFeedbacker();

		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_CLOSE));
		activity.setPerson(feedbacker);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		incident.getActivities().add(activity);
		
        //************2010.9.28  椹姞  鍔犲叆鍚庣疆瀹℃牳娴佺▼鏂规硶		
		//check if post process exists:
//		ProcessDefinition postProcessDefinition = incident.getCategory().getPostProcessDefinition();
		ProcessDefinition postProcessDefinition = null;
		IncidentProcessDefine ipdPost = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_POST);
		if(ipdPost != null) {
			postProcessDefinition = ipdPost.getProcessDefine();
		}
		if(postProcessDefinition != null) {
			
			ProcessInstance postProcessInstance = new ProcessInstance(postProcessDefinition);
			
			postProcessInstance.setInitiatorId(handler.getId());
			
			postProcessInstance.setCategory(new Short(ProcessInstance.CATEGORY_INCIDENT));
			postProcessInstance.setExternalId(incident.getId());
			
			//add location and organization of applicant to global data of process instance:
			Person applicant = incident.getApplicant();
			if(applicant != null){
				Location location = applicant.getLocation();
				postProcessInstance.getGlobalData().put("applicantLocation", location.getId() + ":" + location.getPath());
					
				Organization organization = applicant.getOrganization();
				postProcessInstance.getGlobalData().put("applicantOrganization", organization.getId()+ ":" + organization.getPath());
	
				//add location and organization of creator to global data of process instance:
				if(incident.getCreator() != null){
					location = incident.getCreator().getLocation();
					postProcessInstance.getGlobalData().put("creatorLocation", location.getId() + ":" + location.getPath());
						
					organization = incident.getCreator().getOrganization();
					postProcessInstance.getGlobalData().put("creatorOrganization", organization.getId()+ ":" + organization.getPath());
				}
			}
			//start process instance:
			postProcessInstance.begin();
			
			//store the process instance to DB:
			processDAO.saveProcessInstance(postProcessInstance);
			logger.info("Process instance(id=" + postProcessInstance.getId() + ") was started.");
			
			//create notice for the first task:
			
			//get urls from configuration file:
			String incidentEditTaskUrl = configProperties.getProperty("incidentEditTaskURL");
			String incidentViewUrl = configProperties.getProperty("incidentViewURL");
			
			NodeInstance nodeInstance = postProcessInstance.getToken().getCurrentNodeInstance();
			
			short node_type = nodeInstance.getNode().getType().shortValue();
			if(node_type == Node.TYPE_STEP) {
				
				TaskInstance firstTaskInstance = (TaskInstance)nodeInstance.getTaskInstances().get(0);
				
				//String subject = configProperties.getProperty("newTaskSubject");
				String message = configProperties.getProperty("newTaskMessage");
				
				String editLink = incidentEditTaskUrl +
								"&id=" + incident.getId() +
								"&taskInstanceId=" + firstTaskInstance.getId();
				String viewLink = incidentViewUrl +
								"&id=" + incident.getId();
		
				Notice notice = Notice.newNoticeTemplate(postProcessInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
									null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
				
				noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
			
			} else {
				//TYPE_FORK:
				
				//String subject = configProperties.getProperty("newTaskSubject");
				String message = configProperties.getProperty("newTaskMessage");
	
				List<Token> childTokenList = postProcessInstance.getToken().getChildren();
				for(int i=0;i<childTokenList.size();i++) {
					
					Token childToken = childTokenList.get(i);
					
					//we assume only step nodes here:
					TaskInstance firstTaskInstance = (TaskInstance)childToken.getCurrentNodeInstance().getTaskInstances().get(0);
					
					String editLink = incidentEditTaskUrl +
									"&id=" + incident.getId() +
									"&taskInstanceId=" + firstTaskInstance.getId();
					String viewLink = incidentViewUrl +
									"&id=" + incident.getId();
			
					Notice notice = Notice.newNoticeTemplate(postProcessInstance.getToken().getCurrentNodeInstance(), firstTaskInstance, 
										null, null, subject, message, incident.getRequestNo(), editLink, viewLink);
					
					noticeDAO.insert(notice, firstTaskInstance.getOrganizationId(), firstTaskInstance.getLocationId());
				}
				
			}
	
		    //store id of process instance to incident object:
			incident.getPostProcessInstanceIds().add(postProcessInstance.getId());
			incident.setStatus(new Short(Incident.STATUS_ASSESSING));
			
			activity = new IncidentActivity();
			activity.setType(IncidentActivity.TYPE_ASSESS);
			activity.setTime(new Timestamp(System.currentTimeMillis()));
			activity.setPerson(handler);
			
			incident.getActivities().add(activity);
			
		} else {
			//close the incident:
			incident.setStatus(new Short(Incident.STATUS_CLOSED));
			incident.setCloseTime(new Timestamp(System.currentTimeMillis()));

		}	

		Short closeCode = Short.valueOf(req.getParameter("closeCode"));
		incident.setCloseCode(closeCode);
		
		incidentDAO.update(incident);
		
		logger.info("Incident(id=" + incidentId + ") was closed by " +  feedbacker.getName());
		
		
		paraMap.put("parentReload", true);
		
		//for trigger:
		paraMap.put("id", incidentId);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//close()
	
	
	
	/*
	 * method to intervene incident
	 */
	public ModelAndView intervene(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		
		Incident incident = incidentDAO.findById(incidentId);
		
		paraMap = this.getMap(incident);
		
		paraMap.put("reasonList", reasonDAO.findAll());
		paraMap.put("roleList", 
						roleDAO.findRolesByModuleOperation(ItsmConstants.MODULE_INCIDENT, PermissionItem.OPERATION_HANDLE));
	
		paraMap.put("readonly", req.getParameter("readonly"));
		
		return new ModelAndView(this.getInterveneView(), paraMap);
		
	}//intervene()
	
	
	/*
	 * 分配
	 * 
	 * method to assign incident request compulsively
	 */
	public ModelAndView assign(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String token[];
		String handlerNameList = "";
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String comment = req.getParameter("comment");
		Boolean sendMessage = new Boolean(req.getParameter("sendMessage"));
		
		Incident incident = incidentDAO.findById(incidentId);

		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person intervener = getCurrentPerson(session);
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_ASSIGN));
		activity.setPerson(intervener);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		String handlerPersonIdListStr = req.getParameter("handlerPersonId");
		token = handlerPersonIdListStr.split(",");
		
		//////////////////////////////////////////////
		Set<Person> oldHandlers = incident.getHandlers();

		List<Person> leaders = new ArrayList<Person>();
		for (Person psn : oldHandlers) {
			leaders.addAll(this.roleDAO.findLeadersByMemberId(psn.getId()));

		}
		//////////////////////////////////////////////
		
		//clear the handler
		incident.getHandlers().clear();

		for(int i=0;i<token.length;i++) {
		
			Integer handlerPersonId = new Integer(token[i]);
			
			Person handler = personDAO.findById(handlerPersonId);
			
			incident.getHandlers().add(handler);
			activity.getToPersons().add(handler);
			
			if(handlerNameList.length() == 0)
				handlerNameList = handler.getName();
			else
				handlerNameList += ", " + handler.getName();
			
		}
		
		//////////////////////////////////////////////
		Set<Person> newHandlers = incident.getHandlers();

		List<Person> newLeaders = new ArrayList<Person>();
		for (Person psn : newHandlers) {
			newLeaders.addAll(this.roleDAO.findLeadersByMemberId(psn.getId()));

		}
		
		//////////////////////////////////////////////
		
		//set participators:
		String participatorPersonIdListStr = req.getParameter("participatorPersonId");
		token = participatorPersonIdListStr.split(",");
		
		//clear participator:
		//incident.getParticipators().clear();
		if (participatorPersonIdListStr.length() > 0) {
		
			Set<Person> participators = new HashSet<Person>();
			for1:for (int i=0; i<token.length; i++) {
			
				Person participator = personDAO.findById(new Integer(token[i]));
			
				for (Person psn : incident.getParticipators()) {
					if (psn.getId().equals(participator.getId())) {
						continue for1;
					}
				}
				participators.add(participator);
			}
			incident.getParticipators().addAll(participators);
			incident.getParticipators().removeAll(leaders);
		}else{
			incident.getParticipators().clear();
		}
		incident.getParticipators().addAll(newLeaders);
		//set status to assigned
		incident.setStatus(new Short(Incident.STATUS_ASSIGNED));
		incident.setAssignTime(new Timestamp(System.currentTimeMillis()));
		
		//set sendMessage:
		incident.setSendMessage(sendMessage);
		
		if(sendMessage) {
		
			incident.setSendSuccess(sendMessage(incident));
			
		}
		
		//add activity:
		incident.getActivities().add(activity);
		
		//call DAO to update:
		incidentDAO.update(incident);
		logger.info("Incident(id=" + incidentId + ") was assigned to " +  handlerNameList + " by " + intervener.getName());
	
		paraMap.put("parentReload", true);
		
		//for trigger:
		paraMap.put("id", incidentId);
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_ASSIGN, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//assign()
	
	
	
	/*
	 * method to reject incident request
	 */
	public ModelAndView reject(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String comment = req.getParameter("comment");
		
		Incident incident = incidentDAO.findById(incidentId);
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person intervener = getCurrentPerson(session);
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_REJECT));
		activity.setPerson(intervener);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		incident.getActivities().add(activity);
		
		incident.setStatus(new Short(Incident.STATUS_REJECTED));
		
		incidentDAO.update(incident);
		
		logger.info("Incident(id=" + incidentId + ") was rejected by " +  intervener.getName());
		
		paraMap.put("parentReload", true);
		
		//for trigger:
		paraMap.put("id", incident.getId());
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_REJECT, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//reject()
	
	
	/*
	 * method to hand back request
	 */
	public ModelAndView handback(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
	
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		String comment = req.getParameter("comment");
		
		Incident incident = incidentDAO.findById(incidentId);
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person person = getCurrentPerson(session);
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_HANDBACK));
		activity.setPerson(person);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		incident.getActivities().add(activity);
		
		incident.getHandlers().clear();
		incident.setStatus(new Short(Incident.STATUS_UNASSIGNED));
		
		incidentDAO.update(incident);
		
		logger.info("Incident(id=" + incidentId + ") was handed back by " +  person.getName());

		paraMap.put("parentReload", true);
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_HANDBACK, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//handback()
	
	
	
	/*
	 * method to show form of search incident
	 */
	public ModelAndView chooseForm(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		paraMap.put("form", req.getParameter("form"));
		paraMap.put("idField", req.getParameter("idField"));
		paraMap.put("requestNoField", req.getParameter("requestNoField"));
		paraMap.put("linkName", req.getParameter("linkName"));
		
		paraMap.put("customSourceTypeList", customSourceTypeDao.findAll());
		
		return new ModelAndView(this.getChooseFormView(), paraMap);
	
	}//chooseForm()
	
	public ModelAndView multiChooseForm(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		paraMap.put("form", req.getParameter("form"));
		paraMap.put("idField", req.getParameter("idField"));
		paraMap.put("requestNoField", req.getParameter("requestNoField"));
		paraMap.put("linkName", req.getParameter("linkName"));
		
		paraMap.put("customSourceTypeList", customSourceTypeDao.findAll());
		
		return new ModelAndView(this.getMultiChooseFormView(), paraMap);
		
	}//chooseForm()
	
	
	/*
	 * method to show form of search incident
	 */
	public ModelAndView chooseFormForRefer(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		paraMap.put("form", req.getParameter("form"));
		paraMap.put("idField", req.getParameter("idField"));
		paraMap.put("requestNoField", req.getParameter("requestNoField"));
		paraMap.put("linkName", req.getParameter("linkName"));
		
		return new ModelAndView(this.getChooseReferFormView(), paraMap);
	
	}//chooseFormForRefer()

	
	
	/*
	 * method to choose incident
	 */
	public ModelAndView choose(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		paraMap = this.search(req);
		
		paraMap.put("form", req.getParameter("form"));
		paraMap.put("idField", req.getParameter("idField"));
		paraMap.put("requestNoField", req.getParameter("requestNoField"));
		
		return new ModelAndView(this.getChooseView(), paraMap);
		
		
	}//choose()
	
	public ModelAndView multiChoose(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		paraMap = this.search(req);
		
		paraMap.put("form", req.getParameter("form"));
		paraMap.put("idField", req.getParameter("idField"));
		paraMap.put("requestNoField", req.getParameter("requestNoField"));
		
		return new ModelAndView(this.getMultiChooseView(), paraMap);
		
		
	}//choose()
	
	/*
	 * method to choose incident
	 */
	public ModelAndView chooseRefer(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		paraMap = this.search(req);
		
		paraMap.put("form", req.getParameter("form"));
		paraMap.put("idField", req.getParameter("idField"));
		paraMap.put("requestNoField", req.getParameter("requestNoField"));
		paraMap.put("linkName", req.getParameter("linkName"));
		
		return new ModelAndView(this.getChooseReferView(), paraMap);
		
		
	}//choose()
	
	
	
	/*
	 * method to merge incident with another
	 */
	public ModelAndView merge(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get parameters from http request:
		Long incidentId = new Long(req.getParameter("id"));
		Long mainIncidentId = new Long(req.getParameter("mainIncidentId"));
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person person = getCurrentPerson(session);
		
		Incident incident = incidentDAO.findById(incidentId);
		Incident mainIncident = incidentDAO.findById(mainIncidentId);
		
		incident.setStatus(Incident.STATUS_CLOSED);
		incident.setMainIncident(mainIncident);
		
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_MERGE));
		activity.setPerson(person);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		
		incident.getActivities().add(activity);
		
		incidentDAO.update(incident);
		logger.info("Incident(id=" + incidentId + ") was merged to incident(id=" + mainIncidentId + ").");
		
		paraMap.put("parentReload", true);
		
		//notification after activity
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_MERGE, incident, req);
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//merge()
	
	
	
	/*
	 * method to publish incident
	 */
	public ModelAndView publish(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get parameters from http request:
		Long id = new Long(req.getParameter("id"));
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person person = getCurrentPerson(session);
		
		Incident incident = incidentDAO.findById(id);
		
		incident.setPublished(true);
		incidentDAO.update(incident);
		logger.info("Incident(id=" + id + ") was pubished by " + person.getName());
		
		paraMap.put("id", id);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//publish()
	
	
	
	
	/*
	 * method to get detail of activity
	 */
	public ModelAndView getActivity(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get parameters from http request:
		Long activityId = new Long(req.getParameter("id"));
		
		IncidentActivity activity = incidentDAO.findActivityById(activityId);
		
		paraMap.put("id", activityId);
		paraMap.put("type", activity.getType());
		paraMap.put("time", activity.getTime());
		
		if(activity.getPerson() != null)
			paraMap.put("person", activity.getPerson().getName());
		
		if(activity.getToPersons().size() > 0) {
			
			List<Map<String,Object>> toPersonList = new ArrayList<Map<String,Object>>();
			
			Iterator<Person> it = activity.getToPersons().iterator();
			while(it.hasNext()) {
				
				Person toPerson = it.next();
				Map<String,Object> toPersonMap = new HashMap<String,Object>();
				
				toPersonMap.put("name", toPerson.getName());
				
				toPersonList.add(toPersonMap);
			}
			
			paraMap.put("toPersonList", toPersonList);
		}
		
		paraMap.put("comment", activity.getComment());
		
		return new ModelAndView(this.getActivityView(), paraMap);
		
	}//getActivity()
	
	
	/*
	 * method to show batch merge form
	 */
	public ModelAndView batchMergeForm(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		paraMap.put("idList", req.getParameter("idList"));
		
		return new ModelAndView(this.getBatchMergeView(), paraMap);
		
	}//batchMergeForm()
	
	
	
	/*
	 * method to batch merge
	 */
	public ModelAndView batchMerge(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person person = getCurrentPerson(session);
		
		//get parameters from http request:
		String mainIncidentIdStr = req.getParameter("mainIncidentId");
		String idListStr = req.getParameter("idList");
		
		//call DAO to get the incident:
		Incident mainIncident = this.getIncidentDAO().findById(new Long(mainIncidentIdStr));
		
		String[] idStr = idListStr.split(",");
		for (int i=0;i<idStr.length;i++) {
			
			//避免合并自身
			if(mainIncidentIdStr.equals(idStr[i])){
				continue;
			}
			
			Incident incident = this.getIncidentDAO().findById(new Long(idStr[i]));
			
			Timestamp time = new Timestamp(System.currentTimeMillis());
			incident.setCloseTime(time);
			incident.setStatus(Incident.STATUS_CLOSED);
			incident.setMainIncident(mainIncident);
		
			IncidentActivity activity = new IncidentActivity();
		
			activity.setType(new Short(IncidentActivity.TYPE_MERGE));
			activity.setPerson(person);
			activity.setTime(time);
		
			incident.getActivities().add(activity);
		
			this.getIncidentDAO().update(incident);
			logger.info("Incident(id=" + incident.getId() + ") was merged to incident(id=" + mainIncidentIdStr + ").");
			
		}
		
		paraMap.put("parentReload", true);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
		
	}//batchMerge()
	
	
	/**
	 * 工单催办
	 * @param req
	 * @param res
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public ModelAndView hastenIncidentProcessor(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		Person person = getCurrentPerson(session);

		String idListStr = req.getParameter("idList");
		String msgStr = new String(req.getParameter("msg").getBytes("ISO-8859-1"), "UTF-8");
		
		List<Incident> hastenIncidentList = this.getIncidentDAO().findIncidentHandler(idListStr);
		
		String reqNo = "";
		Set<Person> handlers = null;
		String message = "";
		
		for(Incident incident : hastenIncidentList){
			reqNo = incident.getRequestNo();
			handlers = incident.getHandlers();
			message = msgStr.replaceAll("<工单号>", reqNo);
			sendSMSAndNotice(handlers,message,incident.getId(),reqNo);
			
			//催办记录
			IncidentHastenLog incidentHastenLog = new IncidentHastenLog();
			incidentHastenLog.setAddressor(person);
			incidentHastenLog.setSendTime(new Timestamp(System.currentTimeMillis()));
			incidentHastenLog.getRecipients().addAll(handlers);
			incidentHastenLog.setContent(message);
			
			incident.getHastenLogs().add(incidentHastenLog);
			
			this.incidentDAO.update(incident);
		}
		
		paraMap.put("parentReload", true);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
		
	}
	
	private void sendSMSAndNotice(Set<Person> receiverSet,String msgStr,Long incidentId,String reqNo){
		
		Set<Person> recipients = new HashSet<Person>();
		Set<Workgroup> workgroupSet = new HashSet<Workgroup>();
		Set<Role> roleSet = new HashSet<Role>();
		
		if(configProperties == null) {
			configProperties = new Properties();
			try {
				configProperties.load(new FileInputStream(resource.getFile()));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		//系统通知
		List<Integer> receiverIdList = new ArrayList<Integer>();
		for(Person person : receiverSet){
			receiverIdList.add(person.getId());
		}
		
		recipients.addAll(receiverSet);
		
		Notice notice = new Notice(Notice.CATEGORY_INCIDENT, 
				receiverIdList, null,
				"事件工单催办",
				msgStr,
				reqNo, 
				null,
				configProperties.getProperty("incidentViewURL") + "&id=" + incidentId);
		noticeDAO.addNoticeAndPersonNotice(notice);
		
		//邮件通知
//		List<Map<String,String>> recipientList = null;
//		MailServer mailServer = messageDAO.findMailServer();
//		if(mailServer != null) {
//			recipientList = MessageUtils.makeMailRecipients(recipients, workgroupSet, roleSet);
//		}
		
		//邮件通知
		String mobilePhones = null;
		SmsApi smsApi = smsApiDAO.findSMSApi();
		if(smsApi != null) {
			mobilePhones = MessageUtils.makeMobilePhoneStr(recipients, workgroupSet, roleSet);
		}
		 
		MessageThreadUtil messageThreadUtil = new MessageThreadUtil(null, null, null, "事件工单催办", msgStr, smsApi, mobilePhones, msgStr, logger);
		messageThreadUtil.start();
		
	}
	
	/*
	 * method to show batch merge form
	 */
	public ModelAndView batchRejectForm(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		paraMap.put("idList", req.getParameter("idList"));
		
		return new ModelAndView(this.getBatchRejectView(), paraMap);
		
	}//batchMergeForm()
	
	
	/*
	 * method to batch reject
	 */
	public ModelAndView batchReject(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person person = getCurrentPerson(session);
		
		//get parameters from http request:
		String comment = req.getParameter("comment");
		String idListStr = req.getParameter("idList");
		
		String[] idStr = idListStr.split(",");
		for (int i=0;i<idStr.length;i++) {
			
			Incident incident = this.getIncidentDAO().findById(new Long(idStr[i]));
			
			//create activity:
			IncidentActivity activity = new IncidentActivity();
			
			activity.setType(new Short(IncidentActivity.TYPE_REJECT));
			activity.setPerson(person);
			activity.setTime(new Timestamp(System.currentTimeMillis()));
			activity.setComment(comment);
			
			incident.getActivities().add(activity);
			
			incident.setStatus(new Short(Incident.STATUS_REJECTED));
			
			incidentDAO.update(incident);
			
			logger.info("Incident(id=" + incident.getId() + ") was rejected by " +  person.getName());
				
		}
		
		paraMap.put("parentReload", true);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//batchReject()
	
	
	/*
	 * method to show count form
	 */
	public ModelAndView countForm(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		paraMap.put("effectList", effectDAO.findAll());
		paraMap.put("severityList", severityDAO.findAll());
		paraMap.put("urgencyList", urgencyDAO.findAll());
		paraMap.put("reasonList", reasonDAO.findAll());
		
		return new ModelAndView(this.getCountFormView(), paraMap);
		
	}//countForm()
	
	
	/*
	 * method to count
	 */
	public ModelAndView count(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String criteriaStr;
		HashMap<String,Object> fldMap;
		String hql;
		String paraNames[];
		Object values[];
		List<Object> incidents;
		List<Map<String, Object>> incidentList;
		String roleIdListStr = "";
		String token[];
		List<Role> roleList;
		boolean subCatIncluded, subLocationIncluded;
		String para;
		Integer personId;
		String viewLcIdSet;
		String groupBy;
		int i;
		
		
		viewLcIdSet = "";

		//get parameters from session:
		HttpSession session = req.getSession();
		roleIdListStr = (String)session.getAttribute("roleId");
		token = roleIdListStr.split("#");
		
		personId = (Integer)session.getAttribute("personId");
		Person person = personDAO.findById(personId);
		Location personLocation = person.getLocation();
		
		roleList = new ArrayList<Role>();
		for(i=0;i<token.length;i++) {
			
			Role role = roleDAO.findById(new Integer(token[i]));
			roleList.add(role);
		}

		//get parameters from HTTP request:
		String locationCode = req.getParameter("locationCode");
		if(null != req.getParameter("subLocationIncluded")) {
			subLocationIncluded = true;
		} else {
			subLocationIncluded = false;
		}
		
		String requestNo = req.getParameter("requestNo");
		String sourceTypeStr = req.getParameter("sourceType");
		String applicantPersonIdStr = req.getParameter("applicantPersonId");
		String creatorPersonIdStr = req.getParameter("creatorPersonId");
		String handlerPersonIdStr =req.getParameter("handlerPersonId");
		String solution = req.getParameter("solution");
		String beginApplicationTimeStr = req.getParameter("beginApplicationTime");
		String endApplicationTimeStr = req.getParameter("endApplicationTime");
		String beginFinishTimeStr = req.getParameter("beginFinishTime");
		String endFinishTimeStr = req.getParameter("endFinishTime");
		
		String categoryCode = req.getParameter("categoryCode");
		if(null != req.getParameter("subCatIncluded")) {
			subCatIncluded = true;
		} else {
			subCatIncluded = false;
		}
		
		String severityIdStr = req.getParameter("severityId");
		String effectIdStr = req.getParameter("effectId");
		String urgencyIdStr = req.getParameter("urgencyId");
		String reasonIdStr = req.getParameter("reasonId");
		String closeTypeStr = req.getParameter("closeType");
		String statusStr = req.getParameter("status");
		
		groupBy = req.getParameter("groupBy");
		
		//create criteria using parameters got:
		fldMap = new HashMap<String,Object>();
		criteriaStr = "";
		
		fldMap.put("personLocationId", personLocation.getId());
		criteriaStr += "(incident.location.id = :personLocationId";
		
		//domain:
		viewLcIdSet = Utils.getViewIncidentLocationIdList(person);
		
		if(viewLcIdSet != "") {
			criteriaStr += " or incident.location.id in (" + viewLcIdSet + ")";
		}
		
		criteriaStr += ")";
		
		//making criteria:
		criteriaStr += " and incident.status != :status1";
		fldMap.put("status1", new Short(Incident.STATUS_DRAFT));
	
		if((locationCode != null) && (locationCode.length() > 0)) {
			
			if(subLocationIncluded) {
				
				fldMap.put("locationCode", locationCode + "%");
				criteriaStr += " and incident.location.code like :locationCode";
			
			} else {
				
				fldMap.put("locationCode", locationCode);
				criteriaStr += " and incident.location.code = :locationCode";
			}
		}
		
		if((requestNo != null) && (requestNo.length() > 0)) {
			fldMap.put("requestNo", requestNo);
			
			criteriaStr += " and incident.requestNo = :requestNo";
		}
		
		if((sourceTypeStr != null) && (sourceTypeStr.length() > 0)) {
			fldMap.put("sourceType", new Short(sourceTypeStr));
			
			criteriaStr += " and incident.sourceType = :sourceType";
		}
		
		if((applicantPersonIdStr != null) && (applicantPersonIdStr.length() > 0)) {
			fldMap.put("applicantPersonId", new Integer(applicantPersonIdStr));
			
			criteriaStr += " and incident.applicant.id = :applicantPersonId";
		}
		
		if((creatorPersonIdStr != null) && (creatorPersonIdStr.length() > 0)) {
			fldMap.put("creatorPersonId", new Integer(creatorPersonIdStr));
			
			criteriaStr += " and incident.creator.id = :creatorPersonId";
		}
		
		if((handlerPersonIdStr != null) && (handlerPersonIdStr.length() > 0)) {
			
			fldMap.put("handlerPersonId", new Integer(handlerPersonIdStr));
			
			criteriaStr += " and :handlerPersonId in hls.id";
		}
		
		if((solution != null) && (solution.length() > 0)) {
			fldMap.put("solution", "%" + solution + "%");
			
			criteriaStr += " and incident.solution like :solution";
		}
		
		if((beginApplicationTimeStr != null) && (beginApplicationTimeStr.length() > 0)) {
			fldMap.put("beginApplicationTime", java.sql.Date.valueOf(beginApplicationTimeStr));
			
			criteriaStr += " and incident.applicationTime >= :beginApplicationTime";
		}
		
		if((endApplicationTimeStr != null) && (endApplicationTimeStr.length() > 0)) {
			fldMap.put("endApplicationTime", java.sql.Timestamp.valueOf(endApplicationTimeStr + " 23:59:59"));
			
			criteriaStr += " and incident.applicationTime <= :endApplicationTime";
		}
		
		if((beginFinishTimeStr != null) && (beginFinishTimeStr.length() > 0)) {
			fldMap.put("beginFinishTime", java.sql.Date.valueOf(beginFinishTimeStr));
			
			criteriaStr += " and incident.finishTime >= :beginFinishTime";
		}
		
		if((endFinishTimeStr != null) && (endFinishTimeStr.length() > 0)) {
			fldMap.put("endFinishTime", java.sql.Date.valueOf(endFinishTimeStr));
			
			criteriaStr += " and incident.finishTime <= :endFinishTime";
		}
		
		if((categoryCode != null) && (categoryCode.length() > 0)) {
			
			if(subCatIncluded) {
				
				fldMap.put("categoryCode", categoryCode + "%");
			
				criteriaStr += " and incident.category.code like :categoryCode";
			
			} else {
				
				fldMap.put("categoryCode", categoryCode);
				
				criteriaStr += " and incident.category.code = :categoryCode";
			}
		}
		
		if((severityIdStr != null) && (severityIdStr.length() > 0)) {
			fldMap.put("severityId", new Short(severityIdStr));
			
			criteriaStr += " and incident.severity.id = :severityId";
		}
		
		if((effectIdStr != null) && (effectIdStr.length() > 0)) {
			fldMap.put("effectId", new Short(effectIdStr));
			
			criteriaStr += " and incident.effect.id = :effectId";
		}
		
		if((urgencyIdStr != null) && (urgencyIdStr.length() > 0)) {
			fldMap.put("urgencyId", new Short(urgencyIdStr));
			
			criteriaStr += " and incident.urgency.id = :urgencyId";
		}
		
		if((reasonIdStr != null) && (reasonIdStr.length() > 0)) {
			fldMap.put("reasonId", new Short(reasonIdStr));
			
			criteriaStr += " and incident.reason.id = :reasonId";
		}
		
		if((closeTypeStr != null) && (closeTypeStr.length() > 0)) {
			fldMap.put("closeType", new Short(closeTypeStr));
			
			criteriaStr += " and incident.closeType = :closeType";
		}
		
		if((statusStr != null) && (statusStr.length() > 0)) {
			fldMap.put("status", new Short(statusStr));
			
			criteriaStr += " and incident.status = :status";
		}
		
		//custom info:
		para = req.getParameter("integer1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer1", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer1 " +  req.getParameter("integer1_op") + " :integer1";
		}
		
		para = req.getParameter("integer2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer2", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer2 " +  req.getParameter("integer2_op") + " :integer2";
		}
		
		para = req.getParameter("integer3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer3", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer3 " +  req.getParameter("integer3_op") + " :integer3";
		}
		
		para = req.getParameter("integer4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer4", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer4 " +  req.getParameter("integer4_op") + " :integer4";
		}
		
		para = req.getParameter("integer5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer5", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer5 " +  req.getParameter("integer5_op") + " :integer5";
		}
		
		para = req.getParameter("integer6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer6", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer6 " +  req.getParameter("integer6_op") + " :integer6";
		}
		
		para = req.getParameter("integer7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer7", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer7 " +  req.getParameter("integer7_op") + " :integer7";
		}
		
		para = req.getParameter("integer8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer8", new Integer(para));
			
			criteriaStr += " and incident.customInfointeger8 " +  req.getParameter("integer8_op") + " :integer8";
		}
		
		para = req.getParameter("integer9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer9", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer9 " +  req.getParameter("integer9_op") + " :integer9";
		}
		
		para = req.getParameter("integer10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("integer10", new Integer(para));
			
			criteriaStr += " and incident.customInfo.integer10 " +  req.getParameter("integer10_op") + " :integer10";
		}

		para = req.getParameter("double1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double1", new Double(para));
			
			criteriaStr += " and incident.customInfo.double1 " +  req.getParameter("double1_op") + " :double1";
		
		}

		para = req.getParameter("double2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double2", new Double(para));
			
			criteriaStr += " and incident.customInfo.double2 " +  req.getParameter("double2_op") + " :double2";
		
		}
		
		para = req.getParameter("double3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double3", new Double(para));
			
			criteriaStr += " and incident.customInfo.double3 " +  req.getParameter("double3_op") + " :double3";
		
		}
		
		para = req.getParameter("double4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double4", new Double(para));
			
			criteriaStr += " and incident.customInfo.double4 " +  req.getParameter("double4_op") + " :double4";
		
		}
		
		para = req.getParameter("double5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double5", new Double(para));
			
			criteriaStr += " and incident.customInfo.double5 " +  req.getParameter("double5_op") + " :double5";
		
		}
		
		para = req.getParameter("double6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double6", new Double(para));
			
			criteriaStr += " and incident.customInfo.double6 " +  req.getParameter("double6_op") + " :double6";
		
		}
		
		para = req.getParameter("double7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double7", new Double(para));
			
			criteriaStr += " and incident.customInfo.double7 " +  req.getParameter("double7_op") + " :double7";
		
		}
		
		para = req.getParameter("double8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double8", new Double(para));
			
			criteriaStr += " and incident.customInfo.double8 " +  req.getParameter("double8_op") + " :double8";
		
		}
		
		para = req.getParameter("double9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double9", new Double(para));
			
			criteriaStr += " and incident.customInfo.double9 " +  req.getParameter("double9_op") + " :double9";
		
		}
		
		para = req.getParameter("double10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("double10", new Double(para));
			
			criteriaStr += " and incident.customInfo.double10 " +  req.getParameter("double10_op") + " :double10";
		
		}
		
		para = req.getParameter("boolean1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean1", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean1 = :boolean1";
		}
		
		para = req.getParameter("boolean2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean2", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean2 = :boolean2";
		}
		
		para = req.getParameter("boolean3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean3", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean3 = :boolean3";
		}
		
		para = req.getParameter("boolean4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean4", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean4 = :boolean4";
		}
		
		para = req.getParameter("boolean5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean5", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean5 = :boolean5";
		}
		
		para = req.getParameter("boolean6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean6", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean6 = :boolean6";
		}
		
		para = req.getParameter("boolean7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean7", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean7 = :boolean7";
		}
		
		para = req.getParameter("boolean8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean8", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean8 = :boolean8";
		}
		
		para = req.getParameter("boolean9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean9", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean9 = :boolean9";
		}
		
		para = req.getParameter("boolean10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("boolean10", new Boolean(para));
			
			criteriaStr += " and incident.customInfo.boolean10 = :boolean10";
		}
		
		para = req.getParameter("datetime1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime1", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime1 " + req.getParameter("datetime1_op") + " :datetime1";
		}
		
		para = req.getParameter("datetime2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime2", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime2 " + req.getParameter("datetime2_op") + " :datetime2";
		}
		
		para = req.getParameter("datetime3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime3", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime3 " + req.getParameter("datetime3_op") + " :datetime3";
		}
		
		para = req.getParameter("datetime4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime4", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime4 " + req.getParameter("datetime4_op") + " :datetime4";
		}
		
		para = req.getParameter("datetime5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime5", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime5 " + req.getParameter("datetime5_op") + " :datetime5";
		}
		
		para = req.getParameter("datetime6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime6", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime6 " + req.getParameter("datetime6_op") + " :datetime6";
		}
		
		para = req.getParameter("datetime7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime7", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime7 " + req.getParameter("datetime7_op") + " :datetime7";
		}
		
		para = req.getParameter("datetime8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime8", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime8 " + req.getParameter("datetime8_op") + " :datetime8";
		}
		
		para = req.getParameter("datetime9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime9", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime9 " + req.getParameter("datetime9_op") + " :datetime9";
		}
		
		para = req.getParameter("datetime10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("datetime10", Timestamp.valueOf(para));
			
			criteriaStr += " and incident.customInfo.datetime10 " + req.getParameter("datetime10_op") + " :datetime10";
		}
		
		para = req.getParameter("date1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date1", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date1 " + req.getParameter("date1_op") + " :date1";
		}
		
		para = req.getParameter("date2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date2", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date2 " + req.getParameter("date2_op") + " :date2";
		}
		
		para = req.getParameter("date3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date3", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date3 " + req.getParameter("date3_op") + " :date3";
		}
		
		para = req.getParameter("date4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date4", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date4 " + req.getParameter("date4_op") + " :date4";
		}
		
		para = req.getParameter("date5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date5", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date5 " + req.getParameter("date5_op") + " :date5";
		}
		
		para = req.getParameter("date6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date6", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date6 " + req.getParameter("date6_op") + " :date6";
		}
		
		para = req.getParameter("date7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date7", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date7 " + req.getParameter("date7_op") + " :date7";
		}
		
		para = req.getParameter("date8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date8", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date8 " + req.getParameter("date8_op") + " :date8";
		}
		
		para = req.getParameter("date9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date9", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date9 " + req.getParameter("date9_op") + " :date9";
		}
		
		para = req.getParameter("date10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("date10", Date.valueOf(para));
			
			criteriaStr += " and incident.customInfo.date10 " + req.getParameter("date10_op") + " :date10";
		}
		
		para = req.getParameter("shortstring1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring1", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring1 like :shortstring1";
		}
		
		para = req.getParameter("shortstring2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring2", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring2 like :shortstring2";
		}
		
		para = req.getParameter("shortstring3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring3", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring3 like :shortstring3";
		}
		
		para = req.getParameter("shortstring4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring4", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring4 like :shortstring4";
		}
		
		para = req.getParameter("shortstring5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring5", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring5 like :shortstring5";
		}
		
		para = req.getParameter("shortstring6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring6", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring6 like :shortstring6";
		}
		
		para = req.getParameter("shortstring7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring7", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring7 like :shortstring7";
		}
		
		para = req.getParameter("shortstring8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring8", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring8 like :shortstring81";
		}
		
		para = req.getParameter("shortstring9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring9", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring9 like :shortstring9";
		}
		
		para = req.getParameter("shortstring10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring10", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring10 like :shortstring10";
		}
		
		para = req.getParameter("shortstring11");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring11", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring11 like :shortstring11";
		}
		
		para = req.getParameter("shortstring12");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring12", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring12 like :shortstring12";
		}
		
		para = req.getParameter("shortstring13");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring13", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring13 like :shortstring13";
		}
		
		para = req.getParameter("shortstring14");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring14", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring14 like :shortstring14";
		}
		
		para = req.getParameter("shortstring15");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring15", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring15 like :shortstring15";
		}
		
		para = req.getParameter("shortstring16");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring16", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring16 like :shortstring16";
		}
		
		para = req.getParameter("shortstring17");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring17", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring17 like :shortstring17";
		}
		
		para = req.getParameter("shortstring18");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring18", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring18 like :shortstring18";
		}
		
		para = req.getParameter("shortstring19");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring19", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring19 like :shortstring19";
		}
		
		para = req.getParameter("shortstring20");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("shortstring20", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.shortstring20 like :shortstring20";
		}
		para = req.getParameter("mediumstring1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring1", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring1 like :mediumstring1";
		}

		para = req.getParameter("mediumstring2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring2", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring2 like :mediumstring2";
		}
		
		para = req.getParameter("mediumstring3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring3", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring3 like :mediumstring3";
		}
		
		para = req.getParameter("mediumstring4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring4", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring4 like :mediumstring4";
		}
		
		para = req.getParameter("mediumstring5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring5", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring5 like :mediumstring5";
		}
		
		para = req.getParameter("mediumstring6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring6", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring6 like :mediumstring6";
		}
		
		para = req.getParameter("mediumstring7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring7", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring7 like :mediumstring7";
		}
		
		para = req.getParameter("mediumstring8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring8", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring8 like :mediumstring8";
		}
		
		para = req.getParameter("mediumstring9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring9", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring9 like :mediumstring9";
		}
		
		para = req.getParameter("mediumstring10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("mediumstring10", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.mediumstring10 like :mediumstring10";
		}
		
		para = req.getParameter("longstring1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("longstring1", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.longstring1 like :longstring1";
		}
		
		para = req.getParameter("longstring2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("longstring2", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.longstring2 like :longstring2";
		}
		
		para = req.getParameter("longstring3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("longstring3", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.longstring3 like :longstring3";
		}
		
		para = req.getParameter("longstring4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("longstring4", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.longstring4 like :longstring4";
		}
		
		para = req.getParameter("longstring5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("longstring5", "%" + para + "%");
			
			criteriaStr += " and incident.customInfo.longstring5 like :longstring5";
		}
		para = req.getParameter("treevalue1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue1", para);
			
			criteriaStr += " and incident.customInfo.treevalue1 = :treevalue1";
		}
		
		para = req.getParameter("treevalue2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue2", para);
			
			criteriaStr += " and incident.customInfo.treevalue2 = :treevalue2";
		}
		
		para = req.getParameter("treevalue3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue3", para);
			
			criteriaStr += " and incident.customInfo.treevalue3 = :treevalue3";
		}
		
		para = req.getParameter("treevalue4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue4", para);
			
			criteriaStr += " and incident.customInfo.treevalue4 = :treevalue4";
		}
		
		para = req.getParameter("treevalue5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue5", para);
			
			criteriaStr += " and incident.customInfo.treevalue5 = :treevalue5";
		}
		
		para = req.getParameter("treevalue6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue6", para);
			
			criteriaStr += " and incident.customInfo.treevalue6 = :treevalue6";
		}
		
		para = req.getParameter("treevalue7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue7", para);
			
			criteriaStr += " and incident.customInfo.treevalue7 = :treevalue7";
		}
		
		para = req.getParameter("treevalue8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue8", para);
			
			criteriaStr += " and incident.customInfo.treevalue8 = :treevalue8";
		}
		
		para = req.getParameter("treevalue9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue9", para);
			
			criteriaStr += " and incident.customInfo.treevalue9 = :treevalue9";
		}
		
		para = req.getParameter("treevalue10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("treevalue10", para);
			
			criteriaStr += " and incident.customInfo.treevalue10 = :treevalue10";
		}
		
		para = req.getParameter("codevalue1");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue1", para);
			
			criteriaStr += " and incident.customInfo.codevalue1 = :codevalue1";
		}
		
		para = req.getParameter("codevalue2");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue2", para);
			
			criteriaStr += " and incident.customInfo.codevalue2 = :codevalue2";
		}
		
		para = req.getParameter("codevalue3");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue3", para);
			
			criteriaStr += " and incident.customInfo.codevalue3 = :codevalue3";
		}
		
		para = req.getParameter("codevalue4");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue4", para);
			
			criteriaStr += " and incident.customInfo.codevalue4 = :codevalue4";
		}
		
		para = req.getParameter("codevalue5");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue5", para);
			
			criteriaStr += " and incident.customInfo.codevalue5 = :codevalue5";
		}
		
		para = req.getParameter("codevalue6");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue6", para);
			
			criteriaStr += " and incident.customInfo.codevalue6 = :codevalue6";
		}
		
		para = req.getParameter("codevalue7");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue7", para);
			
			criteriaStr += " and incident.customInfo.codevalue7 = :codevalue7";
		}
		
		para = req.getParameter("codevalue8");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue8", para);
			
			criteriaStr += " and incident.customInfo.codevalue8 = :codevalue8";
		}
		
		para = req.getParameter("codevalue9");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue9", para);
			
			criteriaStr += " and incident.customInfo.codevalue9 = :codevalue9";
		}
		
		para = req.getParameter("codevalue10");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue10", para);
			
			criteriaStr += " and incident.customInfo.codevalue10 = :codevalue10";
		}
		
		para = req.getParameter("codevalue11");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue11", para);
			
			criteriaStr += " and incident.customInfo.codevalue11 = :codevalue11";
		}
		
		para = req.getParameter("codevalue12");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue12", para);
			
			criteriaStr += " and incident.customInfo.codevalue12 = :codevalue12";
		}
		
		para = req.getParameter("codevalue13");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue13", para);
			
			criteriaStr += " and incident.customInfo.codevalue13 = :codevalue13";
		}
		
		para = req.getParameter("codevalue14");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue14", para);
			
			criteriaStr += " and incident.customInfo.codevalue14 = :codevalue14";
		}
		
		para = req.getParameter("codevalue15");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue15", para);
			
			criteriaStr += " and incident.customInfo.codevalue15 = :codevalue15";
		}
		
		para = req.getParameter("codevalue16");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue16", para);
			
			criteriaStr += " and incident.customInfo.codevalue16 = :codevalue16";
		}
		
		para = req.getParameter("codevalue17");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue17", para);
			
			criteriaStr += " and incident.customInfo.codevalue17 = :codevalue17";
		}
		
		para = req.getParameter("codevalue18");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue18", para);
			
			criteriaStr += " and incident.customInfo.codevalue18 = :codevalue18";
		}
		
		para = req.getParameter("codevalue19");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue19", para);
			
			criteriaStr += " and incident.customInfo.codevalue19 = :codevalue19";
		}
		
		para = req.getParameter("codevalue20");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("codevalue20", para);
			
			criteriaStr += " and incident.customInfo.codevalue20 = :codevalue20";
		}
	
		para = req.getParameter("location1Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("location1Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.location1.id = :location1Id";
		}
		
		para = req.getParameter("location2Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("location2Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.location2.id = :location2Id";
		}
		
		para = req.getParameter("location3Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("location3Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.location3.id = :location3Id";
		}
		
		para = req.getParameter("location4Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("location4Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.location4.id = :location4Id";
		}
		
		para = req.getParameter("location5Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("location5Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.location5.id = :location5Id";
		}
		
		para = req.getParameter("organization1Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("organization1Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.organization1.id = :organization1Id";
		}
		
		para = req.getParameter("organization2Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("organization2Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.organization2.id = :organization2Id";
		}
		
		para = req.getParameter("organization3Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("organization3Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.organization3.id = :organization3Id";
		}
		
		para = req.getParameter("organization4Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("organization4Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.organization4.id = :organization4Id";
		}
		
		para = req.getParameter("organization5Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("organization5Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.organization5.id = :organization5Id";
		}
		
		para = req.getParameter("person1Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("person1Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.person1.id = :person1Id";
		}
		
		para = req.getParameter("person2Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("person2Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.person2.id = :person2Id";
		}
		
		para = req.getParameter("person3Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("person3Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.person3.id = :person3Id";
		}
		
		para = req.getParameter("person4Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("person4Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.person4.id = :person4Id";
		}
		
		para = req.getParameter("person5Id");
		if((para != null) && (para.length() >0)) {
			
			fldMap.put("person5Id", new Integer(para));
			
			criteriaStr += " and incident.customInfo.person5.id = :person5Id";
		}

		logger.debug("Criteria of count incidents: " + criteriaStr);
		
		//prepare parameters for query:
		Object flds[] = fldMap.keySet().toArray();
		paraNames = new String[flds.length];
		for (i = 0; i < flds.length; i++) {
			String fld = (String) flds[i];
			paraNames[i] = fld;
		}

		values = fldMap.values().toArray();
		
		//count all:
		if((handlerPersonIdStr != null) && (handlerPersonIdStr.length() > 0)) {
			hql = "select count(*) from com.telinkus.itsm.data.incident.Incident incident join incident.handlers hls where " + criteriaStr;
		}else {
			hql = "select count(*) from com.telinkus.itsm.data.incident.Incident incident where " + criteriaStr;
		}

		Long rowCount = ((Long) incidentDAO.findByNamedParam(hql, 1, paraNames, values).get(0)).longValue();
		logger.debug("rowCount=" + rowCount);
		
		//count group by:
		if((handlerPersonIdStr != null) && (handlerPersonIdStr.length() > 0)) {
			hql = "select " + groupBy + ".id"
					+ ",count(*) from com.telinkus.itsm.data.incident.Incident incident join incident.handlers hls where " + criteriaStr
					+ " group by " + groupBy + ".id";
		}else {
			hql = "select " + groupBy + ".id"
					+ ",count(*) from com.telinkus.itsm.data.incident.Incident incident where " + criteriaStr
					+ " group by " + groupBy + ".id";
		}
		
		logger.debug("hql=" + hql);

		incidentList = new ArrayList<Map<String, Object>>();

		//call DAO to search:
		incidents = incidentDAO.findByNamedParam(hql, 0, paraNames, values);
		
		for (i = 0; i < incidents.size(); i++) {
			
			Map<String,Object> incidentMap = new HashMap<String,Object>();
			
			Object [] row = (Object[])incidents.get(i);
			
			if (groupBy.equals("category")) {
				
				Short catId = (Short)row[0];
				Category cat = this.getCatDAO().findById(catId);
				
				incidentMap.put("categoryName", cat.getName());
				incidentMap.put("categoryCode", cat.getCode());
				
			} else if (groupBy.equals("location")) {
				
				Integer locationId = (Integer)row[0];
				Location location = this.getLocationDAO().findById(locationId);
				
				incidentMap.put("locationName", location.getName());
				incidentMap.put("locationCode", location.getCode());
			}
			
			incidentMap.put("count", (Long)row[1]);
			
			incidentList.add(incidentMap);
		}
		
		paraMap.put("incidentList", incidentList);
		
		paraMap.put("groupBy", groupBy);
		paraMap.put("rowCount", rowCount);
		
		return new ModelAndView(this.getCountView(), paraMap);
		
	}//count()
	
	
	/*
	 * method to find application history of certain person
	 * 		-- 閺囨寧宕查崢鐔告降娴ｈ法鏁ら惃鍒瞚st閺傝纭堕敍灞借嫙娑撴梻绮ㄩ弸婊冨灙鐞涖劋绗夋担璺ㄦ暏column list閹貉傛
	 */
	public ModelAndView applicationHistory(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String,Object> paraMap;
		
		
		paraMap = search(req);
		
		return new ModelAndView(getApplicationHistoryView(), paraMap);
		
	}//applicationHistory()
	
	
	/*
	 * method to export using customized program
	 */
	public void customExport(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		CustomExport customExport;
		String className;
		String methodName;
		Class<?> c;
		Method m;
		Session session;
		Long id;
		
		//get parameters from http request:
		id = new Long(req.getParameter("id"));
		
		//get custom export:
		customExport = customExportDao.findByModule(ItsmConstants.MODULE_INCIDENT);
		if(customExport != null) {
			
			//get hibernate session from DAO: 
			session = customExportDao.getHibernateTemplate().getSessionFactory().getCurrentSession();
		
			className = customExport.getClassName();
			methodName = customExport.getMethodName();
			
			logger.debug("className=" + className + ", methodName=" + methodName);
			
			//invoke the method:
			try {
			
				c = Class.forName(className);
				m = c.getDeclaredMethod(methodName, HttpServletResponse.class, Session.class, Long.class);
				m.invoke(null, res, session, id);
				
			} catch(ClassNotFoundException e) {
				
				logger.error("Class " + className + " not found!");
				
				RequestContext requestContext = new RequestContext(req);
				
				res.setContentType("text/html;charset=utf-8");
				PrintWriter out = res.getWriter();
				out.print("<script>" +
						"alert('" + requestContext.getMessage("configuration", "Configuration") + requestContext.getMessage("of", "Of") +
						requestContext.getMessage("class_name", "Class Name") + requestContext.getMessage("error", "Error") + "!');" +
						"window.location = 'incident_action.do?operation=detail&id=" + req.getParameter("id") + "&readonly=1';" +
						"</script>"
				);
				
			} catch(NoSuchMethodException e) {
				
				logger.error("Method " + methodName + " not found in class " + className + " !");
				
				RequestContext requestContext = new RequestContext(req);
				
				res.setContentType("text/html;charset=utf-8");
				PrintWriter out = res.getWriter();
				out.print("<script>" +
						"alert(" + requestContext.getMessage("configuration", "Configuration") + requestContext.getMessage("of", "Of") +
						requestContext.getMessage("method_name", "Method Name") + requestContext.getMessage("error", "Error") + "!);" +
						"window.location = 'incident_action.do?operation=detail&id=" + req.getParameter("id") + "&readonly=1';" +
						"</script>"
				);
				
			} catch(Exception e) {
				
				logger.error(Utils.getStackTrace(e));
				throw e;
			}
		
		}
		
	}//customExport()
	
	
	
	/*
	 * method to export Incident to Excel
	 */
	public void exportSingle(HttpServletRequest req, HttpServletResponse res)  throws Exception {
		
		Incident incident;
		List<String> headerList;
		List<String> dataList;
		RequestContext requestContext;
		SimpleDateFormat datetimeFormat;
		
		
		datetimeFormat = new SimpleDateFormat(ItsmConstants.DATETIME_FORMAT);
		
		
		//get parameters from http request:
		Long id = new Long(req.getParameter("id"));
		
		incident = incidentDAO.findById(id);
		
		requestContext = new RequestContext(req);
		
		headerList = new ArrayList<String>();
		dataList = new ArrayList<String>();
		
		headerList.add(requestContext.getMessage("location", "location"));
		dataList.add(incident.getLocation().getName());
		
		//category:
		Category cat = incident.getCategory();
		
		headerList.add(requestContext.getMessage("category", "category"));
		dataList.add(cat.getName());

		headerList.add(requestContext.getMessage("request_no", "Request No"));
		dataList.add(incident.getRequestNo());
		
		headerList.add(requestContext.getMessage("source_type", "Source Type"));
		switch (incident.getSourceType()) {

			case Incident.SOURCETYPE_PHONE:
				
				dataList.add(requestContext.getMessage("phone", "Phone"));
				break;
	
			case Incident.SOURCETYPE_FAX:
				
				dataList.add(requestContext.getMessage("fax", "Fax"));
				break;
	
			case Incident.SOURCETYPE_EMAIL:
				
				dataList.add(requestContext.getMessage("email", "EMail"));
				break;
	
			case Incident.SOURCETYPE_MONITORSYSTEM:
				
				dataList.add(requestContext.getMessage("monitor_system", "Monitor System"));
				break;
	
			case Incident.SOURCETYPE_SELFSERVICE:
				
				dataList.add(requestContext.getMessage("client_self_service", "Client Self Service"));
				break;
	
			case Incident.SOURCETYPE_CUSTOM:
				
				dataList.add(incident.getCustomSourceType().getName());
				break;
	
	
			case Incident.SOURCETYPE_OTHER:
				
				dataList.add(requestContext.getMessage("other", "other") + incident.getOtherSourceType());
				break;

		}
		
		headerList.add(requestContext.getMessage("effect", "effect"));
		dataList.add(incident.getEffect().getName());
		
		headerList.add(requestContext.getMessage("urgency", "urgency"));
		dataList.add(incident.getUrgency().getName());
		
		headerList.add(requestContext.getMessage("priority", "priority"));
		dataList.add(incident.getPriority().toString());
		
		headerList.add(requestContext.getMessage("severity", "severity"));
		if(incident.getSeverity() != null) {
			dataList.add(incident.getSeverity().getName());
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("applicant", "applicant"));
		
		if(incident.getApplicant() != null) {
			dataList.add(incident.getApplicant().getName());
		} else if(incident.getCustomer() != null){
			dataList.add(incident.getCustomer().getName());
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("creator", "creator"));
		if(incident.getCreator() != null) {
			dataList.add(incident.getCreator().getName());
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("status", "Status"));
		dataList.add(getStatusLabel(requestContext, incident.getStatus()));
		
		headerList.add(requestContext.getMessage("create_time", "createTime"));
		dataList.add(datetimeFormat.format(incident.getCreateTime()));
		
		headerList.add(requestContext.getMessage("application", "Application") +
						requestContext.getMessage("time", "Time"));
		dataList.add(datetimeFormat.format(incident.getApplicationTime()));
		
		headerList.add(requestContext.getMessage("expect_finish_time", "Expect Finish Time"));
		if(incident.getExpectFinishTime() != null) {
			dataList.add(datetimeFormat.format(incident.getExpectFinishTime()));
		} else {
			dataList.add("");
		}
		
		/*
		headerList.add(requestContext.getMessage("promise_start_time", "Promise Start Time"));
		if(incident.getPromiseStartTime() != null) {
			dataList.add(datetimeFormat.format(incident.getPromiseStartTime()));
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("response_delay", "Response Delay"));
		if(incident.getResponseDelay() != null) {
			dataList.add(incident.getResponseDelay().toString());
		} else {
			dataList.add("");
		}

		headerList.add(requestContext.getMessage("promise_finish_time", "Promise Finish Time"));
		if(incident.getPromiseFinishTime() != null) {
			dataList.add(datetimeFormat.format(incident.getPromiseFinishTime()));
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("finish_delay", "Finish Delay"));
		if(incident.getFinishDelay() != null) {
			dataList.add(incident.getFinishDelay().toString());
		} else {
			dataList.add("");
		}
		*/
		
		headerList.add(requestContext.getMessage("assign", "Assign") +
						requestContext.getMessage("time", "Time") );
		if(incident.getAssignTime() != null) {
			dataList.add(datetimeFormat.format(incident.getAssignTime()));
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("start", "start") +
						requestContext.getMessage("time", "Time"));
		if(incident.getStartTime() != null) {
			dataList.add(datetimeFormat.format(incident.getStartTime()));
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("finish", "Finish") + 
					requestContext.getMessage("time", "Time"));
		if(incident.getFinishTime() != null) {
			dataList.add(datetimeFormat.format(incident.getFinishTime()));
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("close", "Close") +
						requestContext.getMessage("time", "Time"));
		if(incident.getCloseTime() != null) {
			dataList.add(datetimeFormat.format(incident.getCloseTime()));
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("processing_status", "Processing Status"));
		if(incident.getProcessingStatus() != null) {
			dataList.add(incident.getProcessingStatus().getName());
		} else {
			dataList.add("");
		}
		
		headerList.add(requestContext.getMessage("finish", "Finish") +
						requestContext.getMessage("percentage", "Percentage"));
		if(incident.getFinishPercentage() != null) {
			dataList.add(incident.getFinishPercentage().toString() + "%");
		} else {
			dataList.add("");
		}
		
		//custom fields:
		CustomInfo customInfo = incident.getCustomInfo();
		if(customInfo != null) {
			
			// get form of category
			org.dom4j.Document formDoc = cat.getFullFormDocument();
			Element root = formDoc.getRootElement();
			
			String fieldType;
			String fieldName;
			String fieldDisplay;
		
			for(int i=0; i< root.elements().size(); i++) {
				
				Element element = (Element)root.elements().get(i);
				
				fieldType = element.elementText(ItsmConstants.XML_TAG_TYPE);
				fieldName = element.elementText(ItsmConstants.XML_TAG_NAME);
				fieldDisplay = element.elementText(ItsmConstants.XML_TAG_DISPLAY);
				
				headerList.add(fieldDisplay);

				dataList.add(getCustomInfoFieldValue(customInfo, requestContext, fieldType, fieldName));
				
			}
		
		}

		String handlers = "";
		for(Person handler : incident.getHandlers()) {
			if(handlers.length() == 0) {
				handlers = handler.getName();
			} else {
				handlers += "," + handler.getName();
			}
		}
		
		headerList.add(requestContext.getMessage("handler", "handler"));
		dataList.add(handlers);
		
		String participators = "";
		for(Person participator : incident.getParticipators()) {
			if(participators.length() == 0) {
				participators = participator.getName();
			} else {
				participators += "," + participator.getName();
			}
		}
		
		headerList.add(requestContext.getMessage("participators", "participators"));
		dataList.add(participators);

		headerList.add(requestContext.getMessage("subject", "subject"));
		dataList.add(incident.getSubject());

		headerList.add(requestContext.getMessage("content", "content"));
		dataList.add(incident.getContent());

		headerList.add(requestContext.getMessage("solution", "solution"));
		if(incident.getSolution() != null) {
			dataList.add(incident.getSolution());
		} else {
			dataList.add("");
		}

//		headerList.add(requestContext.getMessage("reason", "reason"));
//		if(incident.getReason() != null) {
//			dataList.add(incident.getReason().getName());
//		} else {
//			dataList.add("");
//		}
		
		String fileName = incident.getRequestNo() + ".xls";

		res.reset();
		res.setContentType("application/x-msdownload");
		res.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		
		//export to excel:
		OutputStream out = res.getOutputStream();
		ExportUtils.exportSingleEntity(requestContext.getMessage("incident", "incident"), out, headerList, dataList);
		
		// close file
		out.close();

		// flush the response:
		res.flushBuffer();
		
	}//exportSingle()
	
	
	/*
	 * method to export search result
	 */
	public void exportSearchResult(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		StringBuffer countHql, searchHql;
		String paraNames[];
		Object values[];
		HashMap<String, Object> fldMap;
		String catName;
		Vector<String> headers;
		Vector<Vector<String>> rowSet;
		Vector<Vector<Vector<String>>> rowSetVector;
		Vector<String> categoryNameVector;
		Vector<Vector<String>> headersVector;
		RequestContext requestContext;
		List<Incident> incidents;
		List<Element> eleList = null;
		SimpleDateFormat datetimeFormat;
		String[] priority = {"低", "中", "高", "极高", "重大"};
		String[] inciQuali = {"可用性 ", "连续性", "容量", "信息安全", "不涉及"};
		
		datetimeFormat = new SimpleDateFormat(ItsmConstants.DATETIME_FORMAT);
		requestContext = new RequestContext(req);
		
		// make HQL:
		countHql = new StringBuffer();
		searchHql = new StringBuffer();
		fldMap = new HashMap<String, Object>();
		makeSearchHql(req, countHql, searchHql, fldMap);

		// prepare parameters for query:
		Object flds[] = fldMap.keySet().toArray();
		paraNames = new String[flds.length];
		for (int i = 0; i < flds.length; i++) {
			String fld = (String) flds[i];
			paraNames[i] = fld;
		}

		values = fldMap.values().toArray();

		// append order by to hql:
		String order = req.getParameter("order");
		
		searchHql.append(" order by incident.category");
		searchHql.append(", incident.location");

		searchHql.append(", incident.applicationTime");
		if(order != null && order.equals("desc")) {
			searchHql.append(" desc");
		}

		logger.debug("hql=" + searchHql);
		
		// call DAO to search:
		incidents = incidentDAO.findByNamedParam(searchHql.toString(), 0, paraNames, values);
		
		catName = null;
		headersVector = new Vector<Vector<String>>();
		categoryNameVector = new Vector<String>();
		rowSet = null;
		rowSetVector = new Vector<Vector<Vector<String>>>();
		
		for(Incident incident : incidents) {
			
			Category cat = incident.getCategory();
			
			if((null == catName) || !catName.equals(cat.getName()) ) {
				
				// a new category starts:
				catName = cat.getName();
				
				headers = new Vector<String>();
				
				//add common fields header:
				headers.add(requestContext.getMessage("location", "Location"));
				headers.add(requestContext.getMessage("request_no", "Request No"));
				headers.add(requestContext.getMessage("category", "Category"));
				headers.add(requestContext.getMessage("subject", "Subject"));
				headers.add(requestContext.getMessage("content", "Content"));
				headers.add(requestContext.getMessage("applicant", "Applicant"));
				headers.add(requestContext.getMessage("handlers"));
				headers.add(requestContext.getMessage("visitapplication_applicant"));//申请人
				headers.add(requestContext.getMessage("unit"));//单位
				headers.add(requestContext.getMessage("person_phone_number"));//联系电话
				headers.add(requestContext.getMessage("assess") + requestContext.getMessage("department"));//审批部门
				headers.add(requestContext.getMessage("vice_cause_analysis","Cause Analysis"));
				headers.add(requestContext.getMessage("solution","Solution"));
				headers.add(requestContext.getMessage("result"));
				headers.add(requestContext.getMessage("status", "status"));
				headers.add(requestContext.getMessage("priority", "priority"));
				headers.add(requestContext.getMessage("application", "Application") + requestContext.getMessage("time", "Time"));
				headers.add(requestContext.getMessage("happen","happen") + requestContext.getMessage("time","Time"));
				headers.add(requestContext.getMessage("response_time","response_time"));
				headers.add(requestContext.getMessage("solve","solve") + requestContext.getMessage("time","Time"));
				headers.add(requestContext.getMessage("service_desk", "service_desk") + requestContext.getMessage("finish", "Finish") + requestContext.getMessage("time", "Time"));
				headers.add(requestContext.getMessage("close", "close") + requestContext.getMessage("time", "Time"));
				headers.add(requestContext.getMessage("incident_qualitative", "incident_qualitative"));
				headers.add(requestContext.getMessage("planned_outage", "planned_outage"));
				headers.add(requestContext.getMessage("promise_finish_time", "promise_finish_time"));
				headers.add(requestContext.getMessage("finish", "finish")+requestContext.getMessage("time", "time"));
				headers.add(requestContext.getMessage("feedbacker", "feedbacker"));
				// get form of category:
				org.dom4j.Document formDoc = cat.getFullFormDocument();
				if(formDoc != null) {
				
					Element root = formDoc.getRootElement();
					eleList = root.elements(ItsmConstants.XML_TAG_FIELD);
					
					//add each custom field to header:
					for (int i = 0; i < eleList.size(); i++) {
						
						Element fieldElement = (Element) eleList.get(i);
						headers.add(fieldElement.element(ItsmConstants.XML_TAG_DISPLAY).getText());
	
					}
				}

				// add it to vector:
				headersVector.add(headers);
				categoryNameVector.add(catName);

				if (null != rowSet) {
					rowSetVector.add(rowSet);
				}

				rowSet = new Vector<Vector<String>>();
				
			}
			
			// write to excel file:
			Vector<String> cellSet = new Vector<String>();
			
			cellSet.add(incident.getLocation().getName());
			cellSet.add(incident.getRequestNo());
			cellSet.add(incident.getCategory().getName());
			cellSet.add(incident.getSubject());
			cellSet.add(incident.getContent());
			
			Person applicant = incident.getApplicant();
			if(applicant != null) {
				cellSet.add(applicant.getName());
			} else {
				
				Customer customer = incident.getCustomer();
				if(customer != null) {
					cellSet.add(customer.getName());
				} else {
					cellSet.add("");
				}
			}
			
			String handlerListStr = "";
			for(Person handler : incident.getHandlers()) {
				
				if(handlerListStr.length() == 0) {
					handlerListStr = handler.getName();
				} else {
					handlerListStr += "," + handler.getName();
				}
			}
			
			cellSet.add(handlerListStr);
			
			cellSet.add(incident.getReservedString1());//申请人
			cellSet.add(incident.getReservedString3());//单位
			cellSet.add(incident.getReservedString4());//联系电话
			cellSet.add(incident.getReservedString6());//审批部门
			
			//原因分析
			if (incident.getCustomInfo()!=null) {
				if (incident.getCustomInfo().getLongstring2() != null) {
					cellSet.add(incident.getCustomInfo().getLongstring2());
				} else {
					cellSet.add("");
				}
			} else {
				cellSet.add("");
			}
			
			cellSet.add(incident.getSolution());
			cellSet.add(incident.getConclusion());
			cellSet.add(getStatusLabel(requestContext, incident.getStatus()));
			
			if(incident.getPriority() != null){
				cellSet.add(priority[incident.getPriority()-1]);
			}else{
				cellSet.add("");
			}
			
			// 申请时间
			cellSet.add(datetimeFormat.format(incident.getApplicationTime()));
			
			CustomInfo customInfo = incident.getCustomInfo();
			
			// 发生时间
			if (null != customInfo) {
				if (customInfo.getDatetime1() != null) {
					cellSet.add(datetimeFormat.format(customInfo.getDatetime1()));
				} else {
					cellSet.add("");
				}
			} else {
				cellSet.add("");
			}
			//第一次响应时间
			if(incident.getStartTime()!=null){
				cellSet.add(datetimeFormat.format(incident.getStartTime()));
			}else{
				cellSet.add("");
			}
			
			
			// 解决时间
			if (null != customInfo) {
				if (customInfo.getDatetime2() != null) {
					cellSet.add(datetimeFormat.format(customInfo.getDatetime2()));
				} else {
					cellSet.add("");
				}
			} else {
				cellSet.add("");
			}
			
			// 服务台完成时间
			if (null != customInfo) {
				if (customInfo.getDatetime3() != null) {
					cellSet.add(datetimeFormat.format(customInfo.getDatetime3()));
				} else {
					cellSet.add("");
				}
			} else {
				cellSet.add("");
			}
			
			// 关闭时间
			Timestamp finishTime = incident.getCloseTime();//incident.getFinishTime();
			if(finishTime != null) {
				cellSet.add(datetimeFormat.format(finishTime));
			} else {
				cellSet.add("");
			}
			//事件定性
			if(incident.getReservedString9() != null){
				cellSet.add(incident.getReservedString9().replace("1", inciQuali[0])
						.replace("2", inciQuali[1])
						.replace("3", inciQuali[2])
						.replace("4", inciQuali[3])
						.replace("5", inciQuali[4])
						);
			}else {
				cellSet.add("");
			}
			//计划内停机
			if(incident.isPlannedOutage()){
				cellSet.add("计划内停机");
			}else {
				cellSet.add("");
			}
			//承诺完成时间
			if(incident.getCustomInfo() != null && incident.getCustomInfo().getDatetime6() != null){
				cellSet.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
						format(new java.util.Date(incident.getCustomInfo().getDatetime6().getTime())));
			}else{
				cellSet.add("");
			}
			
			// 完成时间
			Timestamp fTime = incident.getFinishTime();
			if(fTime != null) {
				cellSet.add(datetimeFormat.format(fTime));
			} else {
				cellSet.add("");
			}
			
			//从活动记录获取回访人，仅取最后一次回访记录的人员作为回访人
			String fdPsnName="";
			Timestamp fdTime = null;
			
			for(IncidentActivity ia : incident.getActivities()){
				if(ia.getType().equals(IncidentActivity.TYPE_PROCESS_TASK_FINISH)||ia.getType().equals(IncidentActivity.TYPE_PROCESS_NODE_TAKEOVER)){
					if(fdTime!=null){
						if(ia.getTime().getTime()>fdTime.getTime()){
							fdPsnName = ia.getPerson().getName();
							fdTime = ia.getTime();
						}
					}else{
						fdPsnName = ia.getPerson().getName();
						fdTime = ia.getTime();
					}
				}
			}
			
			cellSet.add(fdPsnName);//回访人

			// export custom info
			//CustomInfo customInfo = incident.getCustomInfo();
			if (null != customInfo) {
				this.exportCustomInfo(requestContext, cat, cellSet, customInfo);

			}
			rowSet.add(cellSet);

			if (incidents.indexOf(incident) == incidents.size() - 1) {
				// the last one, add it to result set vector:
				rowSetVector.add(rowSet);
			}
			
		}// for()
		
		// export:
		String fileName = URLEncoder.encode(requestContext.getMessage(
				"list_of_incident", "List of Incident")
				+ "_" + new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) + ".xls", "UTF-8");

		// set format of response:
		res.reset();
		res.setContentType("application/x-msdownload");
		res.addHeader("Content-Disposition", "attachment; filename=\"" + fileName
				+ "\"");

		OutputStream out = res.getOutputStream();

		ExportUtils.exportMultipleCategoryEntities2(categoryNameVector,
				headersVector, rowSetVector, out);

		// close file
		out.close();

		// flush the response:
		res.flushBuffer();
		
	}//exportSearchResult()

	/**
	 * export custom info
	 * 
	 * @param requestContext
	 * @param cat
	 * @param cellSet
	 * @param customInfo
	 * @throws Exception
	 */
	private void exportCustomInfo(RequestContext requestContext, Category cat,
			Vector<String> cellSet, CustomInfo customInfo) throws Exception {
		
		org.dom4j.Document formDoc = cat.getFullFormDocument();
		
		if (formDoc == null) {
			return;
		}
		Element root = formDoc.getRootElement();

		String fieldType;
		String fieldName;

		for (int i = 0; i < root.elements().size(); i++) {

			Element element = (Element) root.elements().get(i);

			fieldType = element.elementText(ItsmConstants.XML_TAG_TYPE);
			fieldName = element.elementText(ItsmConstants.XML_TAG_NAME);

			cellSet.add(getCustomInfoFieldValue(customInfo, requestContext, fieldType, fieldName));

		}
	}
	
	
	/*
	 * method to get value of custom field
	 */
	private static String getCustomInfoFieldValue(CustomInfo customInfo, RequestContext requestContext,
											String fieldType, String fieldName) throws Exception {
		

		String value;
		Field field;
		SimpleDateFormat datetimeFormat;
		SimpleDateFormat dateFormat;
		
		
		datetimeFormat = new SimpleDateFormat(ItsmConstants.DATETIME_FORMAT);
		dateFormat = new SimpleDateFormat(ItsmConstants.DATE_FORMAT);
		
		value = "";
		
		field = customInfo.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		
		if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_INTEGER)) {
			
			Integer integerValue = (Integer)field.get(customInfo);
			if(integerValue != null) {
				value = integerValue.toString();
			} else {
				value = "";
			}
			
		} else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_DOUBLE))	{
			
			Double doubleValue = (Double)field.get(customInfo);
			if(doubleValue != null) {
				value = doubleValue.toString();
			} else {
				value = "";
			}
		
		} else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_BOOLEAN)) {
			
			Boolean booleanValue = (Boolean)field.get(customInfo);
			if(booleanValue != null) {
				
				if(booleanValue) {
					value = requestContext.getMessage("yes", "yes");
				} else {
					value =  requestContext.getMessage("no", "no");
				}
			} else {
				value = "";
			}
			
		} else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_DATETIME)) {
			
			Timestamp datetimeValue = (Timestamp)field.get(customInfo);
			if(datetimeValue != null) {
				value = datetimeFormat.format(datetimeValue);
			} else {
				value = "";
			}
			
		} else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_DATE)) {
			
			Date dateValue = (Date)field.get(customInfo);
			if(dateValue != null) {
				value = dateFormat.format(dateValue);
			} else {
				value = "";
			}
			
		}else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_SHORTSTRING) ||
				fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_MEDIUMSTRING) ||
				fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_LONGSTRING)   ||
				fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_VERYLONGSTRING)   ||
				fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_CODEVALUE)   ||
				fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_TREEVALUE)) {
			
			String stringValue = (String)field.get(customInfo);
			if(stringValue != null) {
				value = stringValue;
			} else {
				value = "";
			}
			
		}else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_LOCATION)) {
			
			Location locationValue = (Location)field.get(customInfo);
			if(locationValue != null) {
				value = locationValue.getName();
			} else {
				value = "";
			}
			
		}else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_ORGANIZATION)) {
			
			Organization organizationValue = (Organization)field.get(customInfo);
			if(organizationValue != null) {
				value = organizationValue.getName();
			} else {
				value = "";
			}
			
		}else if(fieldType.equals(ItsmConstants.CUSTOMINFO_TYPE_PERSON)) {
			
			Person personValue = (Person)field.get(customInfo);
			if(personValue != null) {
				value = personValue.getName();
			} else {
				value = "";
			}
		}
		
		return value;
		
	}//getCustomInfoFieldValue()
	
	
	/*
	 * method to get status text
	 */
	private static String getStatusLabel(RequestContext requestContext, short status) {
		
		String label = "";
		
		switch (status) {

			case Incident.STATUS_ASSESSING:
				label = requestContext.getMessage("assessing", "Assessing");
				break;
	
			case Incident.STATUS_ASSIGNED:
				label = requestContext.getMessage("assigned", "Assigned");
				break;
	
			case Incident.STATUS_PROCESSING:
				label = requestContext.getMessage("processing", "Processing");
				break;
	
			case Incident.STATUS_FEEDBACKING:
				label = requestContext.getMessage("feedbacking", "Feedbacking");
				break;
	
			case Incident.STATUS_CLOSED:
				label = requestContext.getMessage("closed", "Closed");
				break;
	
			case Incident.STATUS_UNASSIGNED:
				label = requestContext.getMessage("unassigned", "Unassigned");
				break;
	
			case Incident.STATUS_ANNULLED:
				label = requestContext.getMessage("annulled", "Annulled");
				break;
	
			case Incident.STATUS_REJECTED:
				label = requestContext.getMessage("rejected", "Rejected");
				break;
			case Incident.STATUS_TOREVIEW:
				label = requestContext.getMessage("toview", "ToView");
				break;
			case Incident.STATUS_REVIEWING:
				label = requestContext.getMessage("viewing", "Viewing");
				break;

		}
		
		return label;
		
	}//getStatusLabel()
	
	private String buildTableFormat(String tableXML) throws Exception{
		if(EntityUtils.isEmpty(tableXML)) {
			return "";
		}
		
		String result = tableXML;
		
		result = tableXML.replaceAll("#@", "\"");
		
		result = result.replaceAll("&nbsp;", "#@#");
		
		SAXReader xmlReader = new SAXReader();
			
		if(result==null||result.trim().equals("")){
				
			return result;
				
		}
		org.dom4j.Document doc = xmlReader.read(new StringReader(result));
		Element root = doc.getRootElement();
		
		if(root.getName().equals("table")){
			
			root.addAttribute("width", "98%");
			root.addAttribute("class", "dataTable");
			root.addAttribute("cellspacing", "0");
			root.addAttribute("cellpadding", "0");
		}
			
		findAndSetField(root);
		
		
		result = doc.asXML(); 
		
		result = result.replaceAll( "#@#","&nbsp;");

		result=result.replaceAll("&lt;", "<");
		
		result=result.replaceAll("&gt;", ">");
		
		result=result.replaceAll("##", "&nbsp;");
		
		result=result.replaceAll("&amp;&amp;", "&&");
		
		
		return result;
	}
	
	/**
	 * 将前台flash做好的xml内field都替换成freemarker标签
	 * @return
	 */
	private void findAndSetField(Element root){
		
		if(root.elements()!=null){
			
			for(Object o : root.elements()){
				
				Element e = (Element)o;
				
				if(e.getName().equals(CUSTOMER_TABLE_FORMAT_FIELD)&&e.attributeValue("type").equals(CUSTOMER_TABLE_FORMAT_FIELD_COMPONET_KEY)){
					
					String fieldId = e.attributeValue("id");
					
					e.getParent().addText(CUSTOMER_TABLE_FORMAT_FTL_GEN_STRING.replaceAll(CUSTOMER_TABLE_FORMAT_FTL_GEN_STRING_SPLIT, fieldId));
					
					e.getParent().remove(e);
					
					continue;
				}else if(e.getName().equals(CUSTOMER_TABLE_FORMAT_FIELD)&&e.attributeValue("type").equals(CUSTOMER_TABLE_FORMAT_FIELD_LABEL_KEY)){
					
					String value = "";
					
					if(e.attribute(CUSTOMER_TABLE_FORMAT_FIELD_LABEL_FIRSTLABELVALUE)!=null)
						value = e.attributeValue(CUSTOMER_TABLE_FORMAT_FIELD_LABEL_FIRSTLABELVALUE);
					
					if(e.attribute(CUSTOMER_TABLE_FORMAT_FIELD_LABEL_LASTLABELVALUE)!=null)
						value = e.attributeValue(CUSTOMER_TABLE_FORMAT_FIELD_LABEL_LASTLABELVALUE);
					
					e.getParent().addText(value);
					e.getParent().remove(e);
										
				}else if(e.getName().equals(CUSTOMER_TABLE_FORMAT_COLUMNTITLE_KEY)){
					
					String value = e.attributeValue(CUSTOMER_TABLE_FORMAT_COLUMNVALUE_KEY);
					
					e.getParent().addText(value);
					e.getParent().addAttribute("class", "tdCenter");
					e.getParent().remove(e);
					
					
										
				}else if(e.getName().equals(CUSTOMER_TABLE_FORMAT_TABLETITLE_KEY)){
					
					String value = e.attributeValue(CUSTOMER_TABLE_FORMAT_TABLEVALUE_KEY);					
					e.getParent().addText(value);
					e.getParent().addAttribute("style", "font-weight: bold;background:#D7F2FB;");
					e.getParent().remove(e);
					
										
				}else if(e.getName().equals(CUSTOMER_TABLE_FORMAT_ROWTITLE_KEY)){
					
					String value = e.attributeValue(CUSTOMER_TABLE_FORMAT_ROWVALUE_KEY);					
					e.getParent().addText(value);
					e.getParent().addAttribute("class", "Tdbg");
					e.getParent().remove(e);
					
				}				
				
				if(!e.elements().isEmpty()){
					findAndSetField(e);
				}
			}
		}
		
	}
	
	/**
	 * 获取自定义格式
	 * @param category
	 * @return
	 */
	public String getFullCustomTableFormat(Category category) {
		if(null == category) {
			return "";
		}
		String customTableFormat = category.getCustomTableFormat();
		if(customTableFormat != null && !"".equals(customTableFormat)) {
			return customTableFormat;
		} else {
			// 如果未设计格式
			if(category.getInheritParentForm()) {//继承父类别表单和格式
				return getFullCustomTableFormat(category.getParent());
			} else {
				return "";
			}
		}
	}
	
	/*
	 * method to get map of incident
	 */
	private HashMap<String,Object> getMap(Incident incident) {
		
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		Map<String,Object> processInstanceMap, attachmentMap, ciMap, workLogsMap;
		List<Map<String,Object>> attachmentList, ciList, workLogsList;
		ProcessInstance processInstance;
		RequestByJob requestByJob = null;		
		
		paraMap.put("id", incident.getId());
		paraMap.put("requestNo", incident.getRequestNo());
		
		paraMap.put("published", incident.getPublished());
		
		Location location = incident.getLocation();
		if(location != null)
			paraMap.put("location", location.getName());
		
		paraMap.put("sourceType", incident.getSourceType());
		paraMap.put("otherSourceType", incident.getOtherSourceType());
		
		//获取复核角色信息
		Role checkRole = incident.getCheckRole();
		if(checkRole != null) {
			paraMap.put("checkRoleId", checkRole.getId());
			paraMap.put("checkRoleName", checkRole.getName());
		}
		
		if(incident.getSourceType() == Incident.SOURCETYPE_CUSTOM) {
			
			CustomSourceType customSourceType = incident.getCustomSourceType();
			paraMap.put("customSourceTypeId", customSourceType.getId());
			paraMap.put("customSourceTypeName", customSourceType.getName());
		}
		
		paraMap.put("externalId", incident.getExternalId());
		paraMap.put("serviceType", incident.getServiceType());
		
		Category cat = incident.getCategory();
		paraMap.put("category", cat.getPath());
		
		paraMap.put("categoryId", cat.getId());
		paraMap.put("categoryName", cat.getName());
		paraMap.put("categoryPath", cat.getPath());
		paraMap.put("categoryDescription", cat.getDescription());
		
		Directory directory = cat.getDirectory();
		if(directory != null) {
			
			paraMap.put("categoryDirectoryId", directory.getId());
			paraMap.put("categoryDirectoryName", directory.getPath());
		}
		
		try {
		
			org.dom4j.Document formDoc = Utils.replaceGeneralType(cat.getFullFormDocument(), codeTypeDAO, treeTypeDAO);

			if (formDoc != null) {
					
				paraMap.put("formDoc", freemarker.ext.dom.NodeModel.parse(new InputSource(new StringReader(formDoc.asXML()))));
	
			}
			
		} catch (Exception e) {

			logger.error("Error:" + e.getLocalizedMessage());
		}

		String tableFormatXML="";
		try{
			tableFormatXML =  buildTableFormat(getFullCustomTableFormat(cat));
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		if(!"".equals(tableFormatXML))
			paraMap.put("tableFormatXML", tableFormatXML);
		
		//custom info:
		CustomInfo customInfo = incident.getCustomInfo();
		if(customInfo != null){
			paraMap.put("customInfo", this.getCustomInfoMap(customInfo));
		}
		
		if(null != incident.getSeverity()){
			paraMap.put("severityId", incident.getSeverity().getId());
			paraMap.put("severity", incident.getSeverity().getName());
		}
		
		Effect effect = incident.getEffect();
		if(effect != null) {
			paraMap.put("effectId", effect.getId());
			paraMap.put("effect", effect.getName());
		}
		
		Urgency urgency = incident.getUrgency();
		if(urgency != null) {
			paraMap.put("urgencyId", urgency.getId());
			paraMap.put("urgency", urgency.getName());
		}
		
		/*if(effect != null && urgency != null)
			paraMap.put("priority", effect.getValue() + "x" + urgency.getValue() +  "=" + incident.getPriority());*/
		paraMap.put("priority", incident.getPriority());
			
		Person applicant = incident.getApplicant();
		if(applicant != null) {
		
			paraMap.put("applicantPersonId", applicant.getId());
			paraMap.put("applicantPersonName", applicant.getName());
			paraMap.put("applicant", applicant.getName());
		}
		
		Customer customer = incident.getCustomer();
		if(customer != null) {
			
			paraMap.put("customerId", customer.getId());
			paraMap.put("customerName", customer.getName());
		}
		
		if(null != incident.getCreator()) {
			paraMap.put("creator", incident.getCreator().getName());
			paraMap.put("creatorId", incident.getCreator().getId());
		}
		
		paraMap.put("createTime", incident.getCreateTime());
		paraMap.put("applicationTime", incident.getApplicationTime());
		paraMap.put("expectFinishTime", incident.getExpectFinishTime());
		
		paraMap.put("promiseStartTime", incident.getPromiseStartTime());
		paraMap.put("responseDelay", incident.getResponseDelay());
		paraMap.put("promiseFinishTime", incident.getPromiseFinishTime());
		paraMap.put("finishDelay", incident.getFinishDelay());
		
		paraMap.put("assignTime", incident.getAssignTime());
		paraMap.put("startTime", incident.getStartTime());
		paraMap.put("finishTime", incident.getFinishTime());
		paraMap.put("closeTime", incident.getCloseTime());
		paraMap.put("conclusion", incident.getConclusion());
		
		ProcessingStatus processingStatus = incident.getProcessingStatus();
		if(processingStatus != null)
			paraMap.put("processingStatus", processingStatus.getName());
		
		paraMap.put("finishPercentage", incident.getFinishPercentage());
		
		paraMap.put("subject", incident.getSubject());
		paraMap.put("content", incident.getContent());
		paraMap.put("solution", incident.getSolution());
		
		//主影响系统
		Set<Category> mainInfluenceSystemSet = incident.getMainInfluenceSystem();
		Set<ConfigurationItem> mainCiSet = incident.getMainCi();
		if(mainCiSet!=null&&mainCiSet.size()>0){
			String mainCiStr = "";
			List<Map<String,Object>> mainCiList = new ArrayList<Map<String,Object>>(0);
			
			Iterator<ConfigurationItem> it = mainCiSet.iterator();
			while(it.hasNext()) {
				
				ConfigurationItem catTmp = it.next();
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", catTmp.getId());
				map.put("name", catTmp.getName());
				
				mainCiStr+=catTmp.getName()+",";
				
				mainCiList.add(map);
			}
			
			paraMap.put("mainInfluenceSystemList", mainCiList);
			mainCiStr = mainCiStr.endsWith(",")?mainCiStr.substring(0, mainCiStr.length()-1):mainCiStr;
			paraMap.put("mainInfluenceSystemStr", mainCiStr);
		}else if(mainInfluenceSystemSet!=null&&mainInfluenceSystemSet.size()>0){
			String mainInfluenceSystemStr = "";
			List<Map<String,Object>> mainInfluenceSystemList = new ArrayList<Map<String,Object>>(0);
			
			Iterator<Category> it = mainInfluenceSystemSet.iterator();
			while(it.hasNext()) {
				
				Category catTmp = it.next();
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", catTmp.getId());
				map.put("name", catTmp.getName());
				
				mainInfluenceSystemStr+=catTmp.getName()+",";
				
				mainInfluenceSystemList.add(map);
			}
			
			paraMap.put("mainInfluenceSystemList", mainInfluenceSystemList);
			mainInfluenceSystemStr = mainInfluenceSystemStr.endsWith(",")?mainInfluenceSystemStr.substring(0, mainInfluenceSystemStr.length()-1):mainInfluenceSystemStr;
			paraMap.put("mainInfluenceSystemStr", mainInfluenceSystemStr);
		}
		
		//关联影响系统
		Set<Category> relaInfluenceSystemSet = incident.getRelationInfluenceSystem();
		Set<ConfigurationItem> relaCiSet = incident.getRelationCi();
		if(relaCiSet!=null&&relaCiSet.size()>0){
			String relaCiStr = "";
			List<Map<String,Object>> relaCiList = new ArrayList<Map<String,Object>>(0);
			
			Iterator<ConfigurationItem> it = relaCiSet.iterator();
			while(it.hasNext()) {
				
				ConfigurationItem catTmp = it.next();
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", catTmp.getId());
				map.put("name", catTmp.getName());
				
				relaCiStr+=catTmp.getName()+",";
				
				relaCiList.add(map);
			}
			
			paraMap.put("relaInfluenceSystemList", relaCiList);
			relaCiStr = relaCiStr.endsWith(",")?relaCiStr.substring(0, relaCiStr.length()-1):relaCiStr;
			paraMap.put("relaInfluenceSystemStr", relaCiStr);
		}else if(relaInfluenceSystemSet!=null&&relaInfluenceSystemSet.size()>0){
			String relaInfluenceSystemStr = "";
			List<Map<String,Object>> relaInfluenceSystemList = new ArrayList<Map<String,Object>>(0);
			
			Iterator<Category> it = relaInfluenceSystemSet.iterator();
			while(it.hasNext()) {
				
				Category catTmp = it.next();
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", catTmp.getId());
				map.put("name", catTmp.getName());
				
				relaInfluenceSystemStr+=catTmp.getName()+",";
				
				relaInfluenceSystemList.add(map);
			}
			
			paraMap.put("relaInfluenceSystemList", relaInfluenceSystemList);
			relaInfluenceSystemStr = relaInfluenceSystemStr.endsWith(",")?relaInfluenceSystemStr.substring(0, relaInfluenceSystemStr.length()-1):relaInfluenceSystemStr;
			paraMap.put("relaInfluenceSystemStr", relaInfluenceSystemStr);
		}
		

		Reason reason = incident.getReason();
		if(reason != null) {
			paraMap.put("reason", reason.getName());
		}
		
		paraMap.put("status", incident.getStatus());
		paraMap.put("closeType", incident.getCloseType());
		paraMap.put("closeCode", incident.getCloseCode());
		
		paraMap.put("sendMessage", incident.getSendMessage());
		paraMap.put("sendSuccess", incident.getSendSuccess());
		
		Incident mainIncident = incident.getMainIncident();
		if(mainIncident != null) {
			
			HashMap<String,Object> mainIncidentMap = new HashMap<String,Object>();
			
			mainIncidentMap.put("id", mainIncident.getId());
			mainIncidentMap.put("requestNo", mainIncident.getRequestNo());
			mainIncidentMap.put("category", mainIncident.getCategory().getName());
			
			paraMap.put("mainIncident", mainIncidentMap);
		}
		
		//get merged incidents:
		Set<Incident> mergedIncidents = incident.getMergedIncidents();
		if(mergedIncidents.size() > 0) {
			
			List<Map<String,Object>> miList = new ArrayList<Map<String,Object>>(0);
			
			Iterator<Incident> miIt = mergedIncidents.iterator();
			while(miIt.hasNext()) {
				
				Incident mi = miIt.next();
				
				Map<String,Object> miMap = new HashMap<String,Object>();
				miMap.put("id", mi.getId());
				miMap.put("requestNo", mi.getRequestNo());
				miMap.put("category", mi.getCategory().getName());
				
				miList.add(miMap);
			}
			
			paraMap.put("mergedIncidentList", miList);
		}

		//get handlers:
		Set<Person> handlerSet = incident.getHandlers();
		if(handlerSet.size() > 0) {
			
			List<Map<String,Object>> handlerList = new ArrayList<Map<String,Object>>(0);
			
			Iterator<Person> handlerIt = handlerSet.iterator();
			while(handlerIt.hasNext()) {
				
				Person handler = handlerIt.next();
				
				Map<String,Object> handlerMap = new HashMap<String,Object>();
				handlerMap.put("id", handler.getId());
				handlerMap.put("name", handler.getName());
				
				handlerList.add(handlerMap);
			}
			
			paraMap.put("handlerList", handlerList);
		}
		
		//get participators:
		Set<Person> participatorSet = incident.getParticipators();
		
		String participatorIds = "";
		
		if(participatorSet.size() > 0) {
			
			List<Map<String,Object>> participatorList = new ArrayList<Map<String,Object>>(0);
			
			Iterator<Person> participatorItr = participatorSet.iterator();
			while(participatorItr.hasNext()) {
				
				Person participator = participatorItr.next();
				
				Map<String,Object> handlerMap = new HashMap<String,Object>();
				handlerMap.put("id", participator.getId());
				handlerMap.put("name", participator.getName());
				
				participatorList.add(handlerMap);
				
				participatorIds += "," + participator.getId();
			}
			
			paraMap.put("participatorList", participatorList);
		}
		
		if(participatorIds.length() > 0){
			participatorIds = participatorIds.replaceFirst(",", "");
			paraMap.put("participatorIds", participatorIds);
		}
		
		//return info of cis:
		ciList = new ArrayList<Map<String,Object>>(0);
		
		Set<ConfigurationItem> cis = incident.getCIs();
		Iterator<ConfigurationItem> ciIt = cis.iterator();
		while (ciIt.hasNext()) {
			
			ConfigurationItem ci = ciIt.next();
			
			ciMap = new HashMap<String,Object>();
			ciMap.put("id", ci.getId());
			ciMap.put("name", ci.getName());
			
			ciMap.put("category", ci.getCategory().getName());
			ciMap.put("categoryName", ci.getCategory().getName());
			ciMap.put("categoryId", ci.getCategory().getId());
			
			ciList.add(ciMap);
		}
		
		paraMap.put("ciList", ciList);
		
		//return info of attachments:
		attachmentList = new ArrayList<Map<String,Object>>(0);
		
		List<Document> attachments = incident.getAttachments();
		for(int i=0;i<attachments.size();i++) {
			
			Document attachment = attachments.get(i);
			if(attachment != null) {
				attachmentMap = new HashMap<String,Object>();
				attachmentMap.put("id", attachment.getId());
				attachmentMap.put("subject", attachment.getSubject());
				attachmentMap.put("author", attachment.getAuthor());
				attachmentMap.put("number", attachment.getNumber());
				
				attachmentMap.put("keywords", attachment.getKeywords());
				attachmentMap.put("description", attachment.getDescription());
				
				attachmentMap.put("fileName", attachment.getOriginalFileName());
				
				directory = attachment.getDirectory();
				if(directory != null) {
				
					attachmentMap.put("directoryId", directory.getId());
					attachmentMap.put("directoryName", directory.getPath());
				}
				
				attachmentMap.put("createTime", attachment.getCreateTime());
				attachmentMap.put("creator", attachment.getCreator().getName());
				
				attachmentList.add(attachmentMap);
			}
		}
		
		paraMap.put("attachmentList", attachmentList);

		//references:
		Iterator<Document> refIt = incident.getReferences().iterator();
		List<Map<String,Object>> referenceList = new ArrayList<Map<String,Object>>(0);
		
		while(refIt.hasNext()) {
			
			Document ref = refIt.next();
			
			Map<String,Object> refMap = new HashMap<String,Object>();
			
			refMap.put("id", ref.getId());
			refMap.put("subject", ref.getSubject());
			refMap.put("author", ref.getAuthor());
			refMap.put("number", ref.getNumber());
			
			refMap.put("keywords", ref.getKeywords());
			refMap.put("description", ref.getDescription());
			
			refMap.put("fileName", ref.getOriginalFileName());
			refMap.put("directoryId", ref.getDirectory().getId());
			refMap.put("directoryName", ref.getDirectory().getPath());
			
			refMap.put("createTime", ref.getCreateTime());
			refMap.put("creator", ref.getCreator().getName());
			
			referenceList.add(refMap);
			
		}
		
		paraMap.put("referenceList", referenceList);

		//return info of process instance:
//		ProcessDefinition processDefinition = cat.getProcessDefinition();
		ProcessDefinition processDefinition = null;
		IncidentProcessDefine ipd = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_PRE);
		if(ipd != null) {
			processDefinition = ipd.getProcessDefine();
		}
		
		if(processDefinition != null) {
			
			Map<String,Object> processDefinitionMap = new HashMap<String,Object>();
			
			processDefinitionMap.put("id", processDefinition.getId());
			processDefinitionMap.put("name", processDefinition.getName());
			
			paraMap.put("processDefinition", processDefinitionMap);
		}
		
		Long processInstanceId = incident.getProcessInstanceId();
		if(processInstanceId != null) {
			
			processInstance = processDAO.findProcessInstanceById(processInstanceId);
			processInstanceMap = new HashMap<String,Object>();
			
			processInstanceMap.put("id", processInstance.getId());
			processInstanceMap.put("beginTime", processInstance.getBeginTime());
			processInstanceMap.put("status", processInstance.getStatus());
			
			Person initiator = personDAO.findById(processInstance.getInitiatorId());
			processInstanceMap.put("initiatorName", initiator.getName());
			
			paraMap.put("processInstance", processInstanceMap);

		}

		//post process:
//		processDefinition = cat.getPostProcessDefinition();
		IncidentProcessDefine ipdPost = incidentDAO.findIncidentProcessDefine(incident.getCategory().getId(), incident.getPriority(), IncidentProcessDefine.DEFINE_TYPE_POST);
		if(ipdPost != null) {
			processDefinition = ipdPost.getProcessDefine();
		}
		if(processDefinition != null) {
			
			Map<String,Object> processDefinitionMap = new HashMap<String,Object>();
			
			processDefinitionMap.put("id", processDefinition.getId());
			processDefinitionMap.put("name", processDefinition.getName());
			
			paraMap.put("postProcessDefinition", processDefinitionMap);
		}

		List<Long> postProcessInstanceIdList = incident.getPostProcessInstanceIds();
		if(postProcessInstanceIdList.size() > 0) {
			
			List<Map<String,Object>> postProcessInstanceList = new ArrayList<Map<String,Object>>();
		
			for(int i=0;i<postProcessInstanceIdList.size();i++) {
			
				ProcessInstance postProcessInstance = processDAO.findProcessInstanceById(postProcessInstanceIdList.get(i));
				
				processInstanceMap = new HashMap<String,Object>();
				processInstanceMap.put("id", postProcessInstance.getId());
				processInstanceMap.put("beginTime", postProcessInstance.getBeginTime());
				processInstanceMap.put("endTime", postProcessInstance.getEndTime());
				processInstanceMap.put("status", postProcessInstance.getStatus());
				processInstanceMap.put("abortComment", postProcessInstance.getAbortComment());
				
				Integer initiatorId = postProcessInstance.getInitiatorId();
				Person initiator = personDAO.findById(initiatorId);
				
				processInstanceMap.put("initiatorName", initiator.getName());
				
				postProcessInstanceList.add(processInstanceMap);
			}

			paraMap.put("postProcessInstanceList", postProcessInstanceList);
		
		}

		//activities:
		List<Map<String,Object>> activityList = new ArrayList<Map<String,Object>>(0);
		
		List<IncidentActivity> activities = incident.getActivities();
		for(int i=0;i<activities.size();i++) {
			
			IncidentActivity activity = activities.get(i);
			
			Map<String,Object> activityMap = new HashMap<String,Object>();
		
			activityMap.put("id", activity.getId());
			activityMap.put("type", activity.getType());
			activityMap.put("time", activity.getTime());
			activityMap.put("comment", activity.getComment());
			
			Person person = activity.getPerson();
			if(null != person){
				activityMap.put("person", person.getName());
			}
			
			List<Map<String,Object>> toPersonList = new ArrayList<Map<String,Object>>(0);
			
			if(activity.getToPersons().size() > 0) {
				
				Iterator<Person> psnIt = activity.getToPersons().iterator();
				while(psnIt.hasNext()) {
					
					Person toPerson = psnIt.next();
					Map<String,Object> toPersonMap = new HashMap<String,Object>();
					
					toPersonMap.put("name", toPerson.getName());
					
					toPersonList.add(toPersonMap);
					
				}
				
				activityMap.put("toPersonList", toPersonList);
			}
		
			activityList.add(activityMap);
		}
		
		paraMap.put("activityList", activityList);

		//incidentEmails:
		List<Map<String,Object>> incidentEmailList = new ArrayList<Map<String,Object>>(0);
		
		List<IncidentEmail> incidentEmails = incident.getEmails();
		for(int i=0;i<incidentEmails.size();i++) {
			
			IncidentEmail incidentEmail = incidentEmails.get(i);
			
			Map<String,Object> incidentEmailMap = new HashMap<String,Object>();
		
			incidentEmailMap.put("id", incidentEmail.getId());
			incidentEmailMap.put("addressor", incidentEmail.getAddressor()==null? "":incidentEmail.getAddressor().getName());
			incidentEmailMap.put("sendTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(incidentEmail.getSendTime().getTime())));
			incidentEmailMap.put("subject", incidentEmail.getSubject());
			incidentEmailMap.put("content", incidentEmail.getContent());
			
			Set<Person> addressees = incidentEmail.getAddressee();
			Iterator<Person> addresseesIter = addressees.iterator();
			String addresseesList = "";
			while(addresseesIter.hasNext()){
				Person addressee = addresseesIter.next();
				addresseesList += "," + addressee.getName();
			}
			if(incidentEmail.getAddresseeAdd() != null){
				String addresseeAdd = incidentEmail.getAddresseeAdd();
				addresseeAdd = addresseeAdd.replace(";", ",");
				addresseesList = addresseesList + "," + addresseeAdd;
			}
			addresseesList = addresseesList.replaceFirst(",", "");
			incidentEmailMap.put("addressees", addresseesList);
			
			Set<Person> ccs = incidentEmail.getCc();
			Iterator<Person> ccsIter = ccs.iterator();
			String ccsList = "";
			while(ccsIter.hasNext()){
				Person cc = ccsIter.next();
				ccsList += "," + cc.getName();
			}
			if(incidentEmail.getCcAdd() != null){
				String ccAdd = incidentEmail.getCcAdd();
				ccAdd = ccAdd.replace(";", ",");
				if(ccsList.length() > 0){
					ccsList = ccsList + "," + ccAdd;
				}else{
					ccsList = ccsList + ccAdd;
				}
			}
			ccsList = ccsList.replaceFirst(",", "");
			incidentEmailMap.put("ccs", ccsList);
			
			incidentEmailList.add(incidentEmailMap);
		}
		
		paraMap.put("incidentEmailList", incidentEmailList);
		
		//noticeRecord
		List<Map<String,Object>> noticeRecordsList = new ArrayList<Map<String,Object>>(0);
		List<NoticeRecord> noticeRecords = incident.getNoticeRecords();
		
		for(int i=0; i<noticeRecords.size(); i++){
			NoticeRecord noticeRecord = noticeRecords.get(i);
			Map<String,Object> noticeRecordMap = new HashMap<String,Object>();
			
			noticeRecordMap.put("id", noticeRecord.getId());
			noticeRecordMap.put("type", noticeRecord.getType());
			noticeRecordMap.put("noticeTime", noticeRecord.getNoticeTime());
			String notifier = "";
			for(Person p : noticeRecord.getNotifier()){
				notifier += "," + p.getName();
			}
			noticeRecordMap.put("notifier", notifier.replaceFirst(",", ""));
			noticeRecordsList.add(noticeRecordMap);
		}
		paraMap.put("noticeRecordsList", noticeRecordsList);
		
		List<Map<String,Object>> incidentHastenLogList = new ArrayList<Map<String,Object>>(0);
		
		List<IncidentHastenLog> incidentHastenLogs = incident.getHastenLogs();
		for(IncidentHastenLog hastenLog : incidentHastenLogs){
			Map<String,Object> hastenLogMap = new HashMap<String,Object>();
			hastenLogMap.put("id", hastenLog.getId());
			hastenLogMap.put("addressor", hastenLog.getAddressor()==null?"":hastenLog.getAddressor().getName());
			hastenLogMap.put("sendTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(hastenLog.getSendTime().getTime())));
			hastenLogMap.put("content", hastenLog.getContent());
			String recipientStr = "";
			for(Person p : hastenLog.getRecipients()){
				recipientStr+=p.getName()+",";
			}
			recipientStr = recipientStr.length()>1?recipientStr.substring(0, recipientStr.length()-1):recipientStr;
			hastenLogMap.put("recipients", recipientStr);
			incidentHastenLogList.add(hastenLogMap);
		}
				
		paraMap.put("incidentHastenLogList", incidentHastenLogList);
		
		//***********2010.10.24  椹姞  ***********tickets:
		List<Map<String,Object>> ticketList = new ArrayList<Map<String,Object>>(0);
		
		List<IncidentTicket> tickets = incident.getTickets();
		for(int i=0;i<tickets.size();i++) {
			
			IncidentTicket ticket = tickets.get(i);
			
			Map<String,Object> ticketMap = new HashMap<String,Object>();
			ticketMap.put("id", ticket.getId());
			ticketMap.put("subject", ticket.getSubject());
			
			ticketMap.put("createTime", ticket.getCreateTime());
			ticketMap.put("startTime", ticket.getStartTime());
			ticketMap.put("finishTime", ticket.getFinishTime());
			ticketMap.put("status", ticket.getStatus());
			
			Set<Person> ticketHandlers = ticket.getHandlers();
			Iterator<Person> iter = ticketHandlers.iterator();
			String ticketHandlerList = "";
			while(iter.hasNext()){
				Person handler = iter.next();
				ticketHandlerList += "," + handler.getName();
			}
			ticketHandlerList = ticketHandlerList.replaceFirst(",", "");
			
			ticketMap.put("handler", ticketHandlerList);
			
			ticketList.add(ticketMap);
		}
		
		paraMap.put("ticketList", ticketList);
		
		//incidents
		List<Map<String,Object>> inciList = new ArrayList<Map<String,Object>>(0);
		
		Iterator<Incident> incidentIt = incident.getIncidents().iterator();
		while(incidentIt.hasNext()) {
			Incident inci = incidentIt.next();
			Map<String,Object> inciMap = new HashMap<String,Object>();
			inciMap = getMap(inci);
			inciList.add(inciMap);
		}
		paraMap.put("inciList", inciList);
		
		//changes:
		List<Map<String,Object>> changeList = new ArrayList<Map<String,Object>>(0);
		
		Iterator<Change> chgIt = incident.getChanges().iterator();
		while(chgIt.hasNext()) {
			
			Change change = chgIt.next();
			
			Map<String,Object> changeMap = new HashMap<String,Object>();
			changeMap.put("id", change.getId());
			changeMap.put("requestNo", change.getRequestNo());
			if(change.getCategory() != null){
				changeMap.put("category", change.getCategory().getName());
			}
			
			Person changeApplicant = change.getApplicant();
			if(changeApplicant != null)
				changeMap.put("applicant", changeApplicant.getName());
			
			changeMap.put("applicationTime", change.getApplicationTime());
			changeMap.put("status", change.getStatus());
			
			changeList.add(changeMap);
		}
		
		paraMap.put("changeList", changeList);
		
		//problems:
		List<Map<String,Object>> problemList = new ArrayList<Map<String,Object>>();
		String relatedProblemIds = "";
		
		Iterator<Problem> prbIt = incident.getProblems().iterator();
		while(prbIt.hasNext()) {
			
			Problem problem = prbIt.next();
			
			Map<String,Object> problemMap = new HashMap<String,Object>();
			problemMap.put("id", problem.getId());
			problemMap.put("requestNo", problem.getRequestNo());
			problemMap.put("category", problem.getCategory().getName());
			problemMap.put("applicant", problem.getApplicant().getName());
			problemMap.put("applicationTime", problem.getApplicationTime());
			problemMap.put("status", problem.getStatus());
			relatedProblemIds += "#" + problem.getId() + ",";
			
			problemList.add(problemMap);
		}
		
		paraMap.put("problemList", problemList);
		paraMap.put("relatedProblemIds", relatedProblemIds);
		
		//knowledges:
		List<Map<String,Object>> knowledgeList = new ArrayList<Map<String,Object>>();
		
		Iterator<Knowledge> kbIt = incident.getKnowledges().iterator();
		while(kbIt.hasNext()) {
			
			Knowledge knowledge = kbIt.next();
			
			Map<String,Object> knowledgeMap = new HashMap<String,Object>();
			knowledgeMap = getKnowledgeMap(knowledge);
			
			knowledgeList.add(knowledgeMap);
		}
		
		paraMap.put("knowledgeList", knowledgeList);
		
		//referenced changes:
		List<Map<String,Object>> referencedChangeList = new ArrayList<Map<String,Object>>(0);
		
		for (Change change : incident.getReferencedChanges()) {
			
			Map<String,Object> changeMap = new HashMap<String,Object>();
			
			changeMap.put("id", change.getId());
			changeMap.put("location", change.getLocation().getName());
			changeMap.put("requestNo", change.getRequestNo());
			if(change.getCategory() != null){
				changeMap.put("category", change.getCategory().getName());
			}
			
			changeMap.put("subject", change.getSubject());
			
			Person changeApplicant = change.getApplicant();
			if(changeApplicant != null) {
				changeMap.put("applicant", changeApplicant.getName());
			}
			
			changeMap.put("applicationTime", change.getApplicationTime());
			changeMap.put("status", change.getStatus());
			
			referencedChangeList.add(changeMap);
		}
		
		paraMap.put("referencedChangeList", referencedChangeList);
		
		//add by czk begin
		//referenced requirements:
		List<Map<String,Object>> referencedRequirementList = new ArrayList<Map<String,Object>>(0);
		
		for (Requirement requirement : incident.getReferencedRequirements()) {
			
			Map<String,Object> requirementMap = new HashMap<String,Object>();
			
			requirementMap.put("id", requirement.getId());
			requirementMap.put("location", requirement.getLocation().getName());
			requirementMap.put("requestNo", requirement.getRequestNo());
			requirementMap.put("category", requirement.getCategory().getName());
			
			requirementMap.put("subject", requirement.getSubject());
			
			Person requirementApplicant = requirement.getApplicant();
			if(requirementApplicant != null) {
				requirementMap.put("applicant", requirementApplicant.getName());
			}
			
			requirementMap.put("applicationTime", requirement.getApplicationTime());
			requirementMap.put("status", requirement.getStatus());
			
			referencedRequirementList.add(requirementMap);
		}
		
		paraMap.put("referencedRequirementList", referencedRequirementList);
		
		//requirements:
		List<Map<String,Object>> requirementList = new ArrayList<Map<String,Object>>(0);
		
		Iterator<Requirement> inciIt = incident.getRequirements().iterator();
		while(inciIt.hasNext()) {
			
			Requirement requirement = inciIt.next();
			
			Map<String,Object> requirementMap = new HashMap<String,Object>();
			
			requirementMap.put("id", requirement.getId());
			requirementMap.put("location", requirement.getLocation().getName());
			requirementMap.put("requestNo", requirement.getRequestNo());
			requirementMap.put("category", requirement.getCategory().getName());
			
			requirementMap.put("subject", requirement.getSubject());
			
			Person requirementApplicant = requirement.getApplicant();
			if(requirementApplicant != null) {
				requirementMap.put("applicant", requirementApplicant.getName());
			}
			
			requirementMap.put("applicationTime", requirement.getApplicationTime());
			requirementMap.put("status", requirement.getStatus());
			
			requirementList.add(requirementMap);
		}
		
		paraMap.put("requirementList", requirementList);
		
		//add by czk end
		
		//SLA:
		Agreement agreement = incident.getSla();
		if(agreement != null) {
			
			Map<String,Object> agreementMap = new HashMap<String,Object>();
			
			agreementMap.put("id", agreement.getId());
			agreementMap.put("subject", agreement.getSubject());
			agreementMap.put("number", agreement.getNumber());
			
			paraMap.put("sla", agreementMap);
		}
		
		paraMap.put("plannedOutage", incident.isPlannedOutage());
		
		//comment
		Set<com.telinkus.itsm.data.incident.Comment> comment = incident.getComments() ;
		if(comment.size() > 0) {
			
			List<Map<String,Object>> commentList = new ArrayList<Map<String,Object>>();
			
			Iterator<com.telinkus.itsm.data.incident.Comment> commentIt = comment.iterator();
			while(commentIt.hasNext()) {
				
				com.telinkus.itsm.data.incident.Comment icomment = (com.telinkus.itsm.data.incident.Comment)commentIt.next();
				
				Map<String,Object> commentMap = new HashMap<String,Object>();
				commentMap.put("name", icomment.getPerson().getName());
				commentMap.put("time", icomment.getTime());
				commentMap.put("content", icomment.getCotent());
				commentList.add(commentMap);
			}
			
			paraMap.put("commentList", commentList);
		}
		
		//related call records:
		//List<CallRecord> callRecords = incident.getCallRecords();
		
		List<CallRecord> callRecords = callRecordDao.findCallRecordByIncidentId(incident.getId());
		List<Map<String, Object>> callRecordList = new ArrayList<Map<String, Object>>();
		
		for(CallRecord callRecord : callRecords) {
			
			Map<String, Object> callRecordMap = new HashMap<String, Object>();
			callRecordMap.put("stateType", callRecord.getStateType());
			callRecordMap.put("createTime", callRecord.getCreateTime());
			callRecordMap.put("customer", callRecord.getCustomer() == null ? "" : callRecord.getCustomer().getName());
			callRecordMap.put("id", callRecord.getId());
			callRecordMap.put("subject", callRecord.getSubject());
			
			callRecordList.add(callRecordMap);
		}
		
		paraMap.put("callRecordList", callRecordList);
		
		//workLogs
		workLogsList = new ArrayList<Map<String,Object>>(0);
		
		List<IncidentWorkLog> workLogs = incident.getWorkLogs();
		for(int i = 0; i < workLogs.size(); i++) {
			
			IncidentWorkLog workLog = workLogs.get(i);

			workLogsMap = new HashMap<String,Object>();
			workLogsMap.put("id", workLog.getId());
			workLogsMap.put("createTime", workLog.getCreateTime());
			workLogsMap.put("content", workLog.getContent());
			workLogsMap.put("creator", workLog.getCreator().getName());
			
			workLogsList.add(workLogsMap);
		}
		
		paraMap.put("workLogsList", workLogsList);
		
		//referenced jobInstance
		requestByJob = requestByJobDAO.findByTypeAndReqNo(RequestByJob.REQUEST_TYPE_INCIDENT, incident.getRequestNo());
		if(requestByJob != null){
			paraMap.put("jobInstanceId", requestByJob.getJobInstance().getId());
			paraMap.put("jobInstanceName", requestByJob.getJobInstance().getName());
		}
		
		//关联的工作任务单
		Set<VisitApplication> visitAppSet = incident.getVisitApplications();
		if(visitAppSet != null && visitAppSet.size() > 0) {
			Iterator<VisitApplication> visitAppIt = visitAppSet.iterator();
			VisitApplication visitApp = null;
			Map<String, Object> visitAppDataMap = null;
			List<Map<String, Object>> visitAppList = new ArrayList<Map<String,Object>>();
			while(visitAppIt.hasNext()) {
				visitApp = visitAppIt.next();
				visitAppDataMap = new HashMap<String, Object>();
				visitAppDataMap.put("id", visitApp.getId());
				visitAppDataMap.put("reqNo", visitApp.getReqNo());
				visitAppDataMap.put("createTime", visitApp.getCreateTime());
				visitAppDataMap.put("type", visitApp.getType());
				visitAppDataMap.put("content", visitApp.getContent());
				visitAppList.add(visitAppDataMap);
			}
			paraMap.put("visitAppList", visitAppList);
		}
		
		paraMap.put("reservedInteger1", incident.getReservedInteger1());
		paraMap.put("reservedInteger2", incident.getReservedInteger2());
		// edit by xingshy :reservedInteger3事件原因的id
		if (incident.getReservedInteger3() != null) {
			IncidentCause cause = this.causeDAO.findById(incident.getReservedInteger3());
			if (cause != null) {
				paraMap.put("reservedInteger3", cause.getId());
				paraMap.put("reservedInteger3Name", this.getCauseNamePath(cause));
			}
		}
		// edit by xingshy :reservedInteger3事件原因的id end
		paraMap.put("reservedInteger4", incident.getReservedInteger4());
		
		// edit by xingshy :reservedInteger5事件分类的id
		if (incident.getReservedInteger5() != null) {
			IncidentSecondaryCategory secondaryCat = this.secondaryCatDAO.findById(incident.getReservedInteger5());
			if (secondaryCat != null) {
				paraMap.put("reservedInteger5", secondaryCat.getId());
				paraMap.put("reservedInteger5Name", this.getSecondaryCatNamePath(secondaryCat));
			}
		}
		// edit by xingshy :reservedInteger3事件分类的id end
		
		paraMap.put("reservedInteger6", incident.getReservedInteger6());
		paraMap.put("reservedInteger7", incident.getReservedInteger7());
		paraMap.put("reservedInteger8", incident.getReservedInteger8());
		paraMap.put("reservedInteger9", incident.getReservedInteger9());
		paraMap.put("reservedInteger10", incident.getReservedInteger10());

		paraMap.put("reservedString1", incident.getReservedString1());
		paraMap.put("reservedString2", incident.getReservedString2());
		paraMap.put("reservedString3", incident.getReservedString3());
		paraMap.put("reservedString4", incident.getReservedString4());
		paraMap.put("reservedString5", incident.getReservedString5());
		paraMap.put("reservedString6", incident.getReservedString6());
		paraMap.put("reservedString7", incident.getReservedString7());
		paraMap.put("reservedString8", incident.getReservedString8());
		paraMap.put("reservedString9", incident.getReservedString9());
		paraMap.put("reservedString10", incident.getReservedString10());
		
		return paraMap;
		
	}//getMap()
	
	/*
	 * method to get map of knowledge
	 */
	private Map<String,Object> getKnowledgeMap(Knowledge knowledge) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		
		paraMap.put("id", knowledge.getId());
		paraMap.put("status", knowledge.getStatus());
		
		com.telinkus.itsm.data.codes.kb.Category category = knowledge.getCategory();
		
		paraMap.put("categoryId", category.getId());
		paraMap.put("saveCategoryId", category.getId());
		paraMap.put("categoryName", category.getName());
		paraMap.put("open", category.getOpen());
		
		paraMap.put("subject", knowledge.getSubject());
		paraMap.put("reflectChannel", new ArrayList<ReflectChannel>(knowledge.getReflectChannel()));
		paraMap.put("supportStaff", knowledge.getSupportStaff());
		paraMap.put("transactionCode", knowledge.getTransactionCode());
		paraMap.put("transactionName", knowledge.getTransactionName());
		paraMap.put("returnCode", knowledge.getReturnCode());
		paraMap.put("returnInfo", knowledge.getReturnInfo());
		paraMap.put("involveSystem", knowledge.getInvolveSystem());
		
		paraMap.put("content", knowledge.getContent());
		
		paraMap.put("creator",  knowledge.getCreator().getName());
		paraMap.put("createTime", knowledge.getCreateTime());
		
		Directory directory = category.getDirectory();
		if(directory != null) {
			
			paraMap.put("categoryDirectoryId", directory.getId());
			paraMap.put("categoryDirectoryName", directory.getPath());
		}
		
		Person lastUpdator = knowledge.getLastUpdator();
		if(lastUpdator != null) {
			
			paraMap.put("lastUpdator", lastUpdator.getName());
			paraMap.put("lastUpdateTime", knowledge.getLastUpdateTime());
		}
		
		Person approver = knowledge.getApprover();
		if(approver != null) {
		
			paraMap.put("approveTime", knowledge.getApproveTime());
			paraMap.put("approver", approver.getName());
			
		}
		
		//attachments:
		List<Map<String,Object>> attachmentList = new ArrayList<Map<String,Object>>(0);
		
		List<Document> attachments = knowledge.getAttachments();
		for(int i=0;i<attachments.size();i++) {
			
			Document attachment = (Document)attachments.get(i);

			Map<String,Object> attachmentMap = new HashMap<String,Object>();
			attachmentMap.put("id", attachment.getId());
			attachmentMap.put("subject", attachment.getSubject());
			attachmentMap.put("author", attachment.getAuthor());
			attachmentMap.put("number", attachment.getNumber());
			
			attachmentMap.put("keywords", attachment.getKeywords());
			attachmentMap.put("description", attachment.getDescription());
			
			attachmentMap.put("fileName", attachment.getOriginalFileName());
			attachmentMap.put("directoryId", attachment.getDirectory().getId());
			attachmentMap.put("directoryName", attachment.getDirectory().getPath());
			
			attachmentMap.put("createTime", attachment.getCreateTime());
			attachmentMap.put("creator", attachment.getCreator().getName());
			
			attachmentList.add(attachmentMap);
			
		}
		
		paraMap.put("attachmentList", attachmentList);
		
		//activities:
		List<Map<String,Object>> activityList = new ArrayList<Map<String,Object>>(0);
		for(KnowledgeActivity activity : knowledge.getActivities()) {
			
			Map<String,Object> activityMap = new HashMap<String,Object>();
		
			activityMap.put("id", activity.getId());
			activityMap.put("type", activity.getType());
			activityMap.put("time", activity.getTime());
			
			Person person = activity.getPerson();
			if(null != person){
				activityMap.put("person", person.getName());
			}
			
			activityMap.put("comment", activity.getComment());
			
			activityList.add(activityMap);
		}
		
		paraMap.put("activityList", activityList);
		
		paraMap.put("hits", knowledge.getHits()!=null?knowledge.getHits():0);
		paraMap.put("commentSize", knowledge.getComments().size());
		
		return paraMap;
		
	}//getKnowledgeMap()
	
	/**
	 * 根据原因的叶子节点找到路径上的所有节点的name
	 * 
	 * add by xingshy
	 * 
	 * @param cause
	 * @return
	 */
	private String getCauseNamePath(IncidentCause cause) {
		String path = "";
		if (cause.getParent() != null) {
			path = path + getCauseNamePath(cause.getParent());
		}
		path = path + "/" + cause.getName();
		return path;
	}
	
	/**
	 * 根据叶子节点找到路径上的所有节点的name
	 * 
	 * add by xingshy
	 * 
	 * @param data
	 * @return
	 */
	private String getSecondaryCatNamePath(IncidentSecondaryCategory secondaryCat) {
		String path = "";
		if (secondaryCat.getParent() != null) {
			path = path + getSecondaryCatNamePath(secondaryCat.getParent());
		}
		path = path + "/" + secondaryCat.getName();
		return path;
	}

	/*
	 * get map of custom info:
	 */
	public Map<String,Object> getCustomInfoMap(CustomInfo customInfo) {
		
		Map<String,Object> ciMap;
		
		ciMap = new HashMap<String,Object>();
		
		
		ciMap.put("integer1", customInfo.getInteger1());
		ciMap.put("integer2", customInfo.getInteger2());
		ciMap.put("integer3", customInfo.getInteger3());
		ciMap.put("integer4", customInfo.getInteger4());
		ciMap.put("integer5", customInfo.getInteger5());
		ciMap.put("integer6", customInfo.getInteger6());
		ciMap.put("integer7", customInfo.getInteger7());
		ciMap.put("integer8", customInfo.getInteger8());
		ciMap.put("integer9", customInfo.getInteger9());
		ciMap.put("integer10", customInfo.getInteger10());
		
		ciMap.put("double1", customInfo.getDouble1());
		ciMap.put("double2", customInfo.getDouble2());
		ciMap.put("double3", customInfo.getDouble3());
		ciMap.put("double4", customInfo.getDouble4());
		ciMap.put("double5", customInfo.getDouble5());
		ciMap.put("double6", customInfo.getDouble6());
		ciMap.put("double7", customInfo.getDouble7());
		ciMap.put("double8", customInfo.getDouble8());
		ciMap.put("double9", customInfo.getDouble9());
		ciMap.put("double10", customInfo.getDouble10());
		
		ciMap.put("boolean1", customInfo.getBoolean1());
		ciMap.put("boolean2", customInfo.getBoolean2());
		ciMap.put("boolean3", customInfo.getBoolean3());
		ciMap.put("boolean4", customInfo.getBoolean4());
		ciMap.put("boolean5", customInfo.getBoolean5());
		ciMap.put("boolean6", customInfo.getBoolean6());
		ciMap.put("boolean7", customInfo.getBoolean7());
		ciMap.put("boolean8", customInfo.getBoolean8());
		ciMap.put("boolean9", customInfo.getBoolean9());
		ciMap.put("boolean10", customInfo.getBoolean10());
		
		ciMap.put("datetime1", customInfo.getDatetime1());
		ciMap.put("datetime2", customInfo.getDatetime2());
		ciMap.put("datetime3", customInfo.getDatetime3());
		ciMap.put("datetime4", customInfo.getDatetime4());
		ciMap.put("datetime5", customInfo.getDatetime5());
		ciMap.put("datetime6", customInfo.getDatetime6());
		ciMap.put("datetime7", customInfo.getDatetime7());
		ciMap.put("datetime8", customInfo.getDatetime8());
		ciMap.put("datetime9", customInfo.getDatetime9());
		ciMap.put("datetime10", customInfo.getDatetime10());
		
		ciMap.put("date1", customInfo.getDate1());
		ciMap.put("date2", customInfo.getDate2());
		ciMap.put("date3", customInfo.getDate3());
		ciMap.put("date4", customInfo.getDate4());
		ciMap.put("date5", customInfo.getDate5());
		ciMap.put("date6", customInfo.getDate6());
		ciMap.put("date7", customInfo.getDate7());
		ciMap.put("date8", customInfo.getDate8());
		ciMap.put("date9", customInfo.getDate9());
		ciMap.put("date10", customInfo.getDate10());
		
		ciMap.put("shortstring1", customInfo.getShortstring1());
		ciMap.put("shortstring2", customInfo.getShortstring2());
		ciMap.put("shortstring3", customInfo.getShortstring3());
		ciMap.put("shortstring4", customInfo.getShortstring4());
		ciMap.put("shortstring5", customInfo.getShortstring5());
		ciMap.put("shortstring6", customInfo.getShortstring6());
		ciMap.put("shortstring7", customInfo.getShortstring7());
		ciMap.put("shortstring8", customInfo.getShortstring8());
		ciMap.put("shortstring9", customInfo.getShortstring9());
		ciMap.put("shortstring10", customInfo.getShortstring10());
		ciMap.put("shortstring11", customInfo.getShortstring11());
		ciMap.put("shortstring12", customInfo.getShortstring12());
		ciMap.put("shortstring13", customInfo.getShortstring13());
		ciMap.put("shortstring14", customInfo.getShortstring14());
		ciMap.put("shortstring15", customInfo.getShortstring15());
		ciMap.put("shortstring16", customInfo.getShortstring16());
		ciMap.put("shortstring17", customInfo.getShortstring17());
		ciMap.put("shortstring18", customInfo.getShortstring18());
		ciMap.put("shortstring19", customInfo.getShortstring19());
		ciMap.put("shortstring20", customInfo.getShortstring20());
		
		ciMap.put("mediumstring1", customInfo.getMediumstring1());
		ciMap.put("mediumstring2", customInfo.getMediumstring2());
		ciMap.put("mediumstring3", customInfo.getMediumstring3());
		ciMap.put("mediumstring4", customInfo.getMediumstring4());
		ciMap.put("mediumstring5", customInfo.getMediumstring5());
		ciMap.put("mediumstring6", customInfo.getMediumstring6());
		ciMap.put("mediumstring7", customInfo.getMediumstring7());
		ciMap.put("mediumstring8", customInfo.getMediumstring8());
		ciMap.put("mediumstring9", customInfo.getMediumstring9());
		ciMap.put("mediumstring10", customInfo.getMediumstring10());
		
		ciMap.put("longstring1", customInfo.getLongstring1());
		ciMap.put("longstring2", customInfo.getLongstring2());
		ciMap.put("longstring3", customInfo.getLongstring3());
		ciMap.put("longstring4", customInfo.getLongstring4());
		ciMap.put("longstring5", customInfo.getLongstring5());
		
		ciMap.put("verylongstring1", customInfo.getVerylongstring1());
		ciMap.put("verylongstring2", customInfo.getVerylongstring2());
		
		ciMap.put("treevalue1", customInfo.getTreevalue1());
		ciMap.put("treevalue2", customInfo.getTreevalue2());
		ciMap.put("treevalue3", customInfo.getTreevalue3());
		ciMap.put("treevalue4", customInfo.getTreevalue4());
		ciMap.put("treevalue5", customInfo.getTreevalue5());
		ciMap.put("treevalue6", customInfo.getTreevalue6());
		ciMap.put("treevalue7", customInfo.getTreevalue7());
		ciMap.put("treevalue8", customInfo.getTreevalue8());
		ciMap.put("treevalue9", customInfo.getTreevalue9());
		ciMap.put("treevalue10", customInfo.getTreevalue10());
		
		ciMap.put("codevalue1", customInfo.getCodevalue1());
		ciMap.put("codevalue2", customInfo.getCodevalue2());
		ciMap.put("codevalue3", customInfo.getCodevalue3());
		ciMap.put("codevalue4", customInfo.getCodevalue4());
		ciMap.put("codevalue5", customInfo.getCodevalue5());
		ciMap.put("codevalue6", customInfo.getCodevalue6());
		ciMap.put("codevalue7", customInfo.getCodevalue7());
		ciMap.put("codevalue8", customInfo.getCodevalue8());
		ciMap.put("codevalue9", customInfo.getCodevalue9());
		ciMap.put("codevalue10", customInfo.getCodevalue10());
		ciMap.put("codevalue11", customInfo.getCodevalue11());
		ciMap.put("codevalue12", customInfo.getCodevalue12());
		ciMap.put("codevalue13", customInfo.getCodevalue13());
		ciMap.put("codevalue14", customInfo.getCodevalue14());
		ciMap.put("codevalue15", customInfo.getCodevalue15());
		ciMap.put("codevalue16", customInfo.getCodevalue16());
		ciMap.put("codevalue17", customInfo.getCodevalue17());
		ciMap.put("codevalue18", customInfo.getCodevalue18());
		ciMap.put("codevalue19", customInfo.getCodevalue19());
		ciMap.put("codevalue20", customInfo.getCodevalue20());

		Organization org = customInfo.getOrganization1();
		if(org != null) 
		{
			ciMap.put("organization1Id", org.getId());
			ciMap.put("organization1", org.getPath());
		}
		
		org = customInfo.getOrganization2();
		if(org != null) 
		{
			ciMap.put("organization2Id", org.getId());
			ciMap.put("organization2", org.getPath());
		}
		
		org = customInfo.getOrganization3();
		if(org != null) 
		{
			ciMap.put("organization3Id", org.getId());
			ciMap.put("organization3", org.getPath());
		}
		
		org = customInfo.getOrganization4();
		if(org != null) 
		{
			ciMap.put("organization4Id", org.getId());
			ciMap.put("organization4", org.getPath());
		}
		
		org = customInfo.getOrganization5();
		if(org != null) 
		{
			ciMap.put("organization5Id", org.getId());
			ciMap.put("organization5", org.getPath());
		}
		
		Location lc = customInfo.getLocation1();
		if(lc != null) {
			ciMap.put("location1Id", lc.getId());
			ciMap.put("location1", lc.getPath());
		}
		
		lc = customInfo.getLocation2();
		if(lc != null) {
			ciMap.put("location2Id", lc.getId());
			ciMap.put("location2", lc.getPath());
		}
		
		lc = customInfo.getLocation3();
		if(lc != null) {
			ciMap.put("location3Id", lc.getId());
			ciMap.put("location3", lc.getPath());
		}
		
		lc = customInfo.getLocation4();
		if(lc != null) {
			ciMap.put("location4Id", lc.getId());
			ciMap.put("location4", lc.getPath());
		}
		
		lc = customInfo.getLocation5();
		if(lc != null) {
			ciMap.put("location5Id", lc.getId());
			ciMap.put("location5", lc.getPath());
		}
		
		Person psn = customInfo.getPerson1();
		if(psn != null) {
			ciMap.put("person1Id", psn.getId());
			ciMap.put("person1", psn.getName());
		}
		
		psn = customInfo.getPerson2();
		if(psn != null) {
			ciMap.put("person2Id", psn.getId());
			ciMap.put("person2", psn.getName());
		}
		
		psn = customInfo.getPerson3();
		if(psn != null) {
			ciMap.put("person3Id", psn.getId());
			ciMap.put("person3", psn.getName());
		}
		
		psn = customInfo.getPerson4();
		if(psn != null) {
			ciMap.put("person4Id", psn.getId());
			ciMap.put("person4", psn.getName());
		}
		
		psn = customInfo.getPerson5();
		if(psn != null) {
			ciMap.put("person5Id", psn.getId());
			ciMap.put("person5", psn.getName());
		}

		return ciMap;
		
	}//getCustomInfoMap()
	
	
	/*
	 * method to get CustomInfo object from HTTP request
	 */
	private CustomInfo getCustomInfoFromRequest(HttpServletRequest req){
		
		CustomInfo customInfo;
		String para;
		Location location;
		Organization org;
		Person person;
		
		customInfo = new CustomInfo();
		
		para = req.getParameter("integer1");
		if((para != null) && (para.length() >0))
			customInfo.setInteger1(new Integer(para));
		
		para = req.getParameter("integer2");
		if((para != null) && (para.length() >0))
			customInfo.setInteger2(new Integer(para));
		
		para = req.getParameter("integer3");
		if((para != null) && (para.length() >0))
			customInfo.setInteger3(new Integer(para));
		
		para = req.getParameter("integer4");
		if((para != null) && (para.length() >0))
			customInfo.setInteger4(new Integer(para));
		
		para = req.getParameter("integer5");
		if((para != null) && (para.length() >0))
			customInfo.setInteger5(new Integer(para));
		
		para = req.getParameter("integer6");
		if((para != null) && (para.length() >0))
			customInfo.setInteger6(new Integer(para));
		
		para = req.getParameter("integer7");
		if((para != null) && (para.length() >0))
			customInfo.setInteger7(new Integer(para));
		
		para = req.getParameter("integer8");
		if((para != null) && (para.length() >0))
			customInfo.setInteger8(new Integer(para));
		
		para = req.getParameter("integer9");
		if((para != null) && (para.length() >0))
			customInfo.setInteger9(new Integer(para));
		
		para = req.getParameter("integer10");
		if((para != null) && (para.length() >0))
			customInfo.setInteger10(new Integer(para));

		para = req.getParameter("double1");
		if((para != null) && (para.length() >0))
			customInfo.setDouble1(new Double(para));
		
		para = req.getParameter("double2");
		if((para != null) && (para.length() >0))
			customInfo.setDouble2(new Double(para));
		
		para = req.getParameter("double3");
		if((para != null) && (para.length() >0))
			customInfo.setDouble3(new Double(para));
		
		para = req.getParameter("double4");
		if((para != null) && (para.length() >0))
			customInfo.setDouble4(new Double(para));
		
		para = req.getParameter("double5");
		if((para != null) && (para.length() >0))
			customInfo.setDouble5(new Double(para));
		
		para = req.getParameter("double6");
		if((para != null) && (para.length() >0))
			customInfo.setDouble6(new Double(para));
		
		para = req.getParameter("double7");
		if((para != null) && (para.length() >0))
			customInfo.setDouble7(new Double(para));
		
		para = req.getParameter("double8");
		if((para != null) && (para.length() >0))
			customInfo.setDouble8(new Double(para));
		
		para = req.getParameter("double9");
		if((para != null) && (para.length() >0))
			customInfo.setDouble9(new Double(para));
		
		para = req.getParameter("double10");
		if((para != null) && (para.length() >0))
			customInfo.setDouble10(new Double(para));

		para = req.getParameter("boolean1");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean1(new Boolean(para));
		
		para = req.getParameter("boolean2");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean2(new Boolean(para));
		
		para = req.getParameter("boolean3");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean3(new Boolean(para));
		
		para = req.getParameter("boolean4");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean4(new Boolean(para));
		
		para = req.getParameter("boolean5");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean5(new Boolean(para));
		
		para = req.getParameter("boolean6");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean6(new Boolean(para));
		
		para = req.getParameter("boolean7");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean7(new Boolean(para));
		
		para = req.getParameter("boolean8");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean8(new Boolean(para));
		
		para = req.getParameter("boolean9");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean9(new Boolean(para));
		
		para = req.getParameter("boolean10");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean10(new Boolean(para));
		
		para = req.getParameter("datetime1");
		if((para != null) && (para.length() >0)){
			customInfo.setDatetime1(Timestamp.valueOf(para));
		}
		
		para = req.getParameter("datetime2");
		if((para != null) && (para.length() >0)){
			customInfo.setDatetime2(Timestamp.valueOf(para));
		}
		
		para = req.getParameter("datetime3");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime3(Timestamp.valueOf(para));

		para = req.getParameter("datetime4");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime4(Timestamp.valueOf(para));

		para = req.getParameter("datetime5");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime5(Timestamp.valueOf(para));

		para = req.getParameter("datetime6");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime6(Timestamp.valueOf(para));

		para = req.getParameter("datetime7");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime7(Timestamp.valueOf(para));

		para = req.getParameter("datetime8");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime8(Timestamp.valueOf(para));

		para = req.getParameter("datetime9");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime9(Timestamp.valueOf(para));

		para = req.getParameter("datetime10");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime10(Timestamp.valueOf(para));

		para = req.getParameter("date1");
		if((para != null) && (para.length() >0))
			customInfo.setDate1(Date.valueOf(para));

		para = req.getParameter("date2");
		if((para != null) && (para.length() >0))
			customInfo.setDate2(Date.valueOf(para));

		para = req.getParameter("date3");
		if((para != null) && (para.length() >0))
			customInfo.setDate3(Date.valueOf(para));

		para = req.getParameter("date4");
		if((para != null) && (para.length() >0))
			customInfo.setDate4(Date.valueOf(para));

		para = req.getParameter("date5");
		if((para != null) && (para.length() >0))
			customInfo.setDate5(Date.valueOf(para));

		para = req.getParameter("date6");
		if((para != null) && (para.length() >0))
			customInfo.setDate6(Date.valueOf(para));

		para = req.getParameter("date7");
		if((para != null) && (para.length() >0))
			customInfo.setDate7(Date.valueOf(para));

		para = req.getParameter("date8");
		if((para != null) && (para.length() >0))
			customInfo.setDate8(Date.valueOf(para));

		para = req.getParameter("date9");
		if((para != null) && (para.length() >0))
			customInfo.setDate9(Date.valueOf(para));

		para = req.getParameter("date10");
		if((para != null) && (para.length() >0))
			customInfo.setDate10(Date.valueOf(para));

		para = req.getParameter("shortstring1");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring1(para);
		
		para = req.getParameter("shortstring2");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring2(para);
		
		para = req.getParameter("shortstring3");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring3(para);
		
		para = req.getParameter("shortstring4");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring4(para);
		
		para = req.getParameter("shortstring5");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring5(para);
		
		para = req.getParameter("shortstring6");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring6(para);
		
		para = req.getParameter("shortstring7");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring7(para);
		
		para = req.getParameter("shortstring8");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring8(para);
		
		para = req.getParameter("shortstring9");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring9(para);
		
		para = req.getParameter("shortstring10");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring10(para);
		
		para = req.getParameter("shortstring11");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring11(para);
		
		para = req.getParameter("shortstring12");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring12(para);
		
		para = req.getParameter("shortstring13");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring13(para);
		
		para = req.getParameter("shortstring14");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring14(para);
		
		para = req.getParameter("shortstring15");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring15(para);
		
		para = req.getParameter("shortstring16");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring16(para);
		
		para = req.getParameter("shortstring17");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring17(para);
		
		para = req.getParameter("shortstring18");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring18(para);

		para = req.getParameter("shortstring19");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring19(para);
		
		para = req.getParameter("shortstring20");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring20(para);
		
		para = req.getParameter("mediumstring1");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring1(para);
		
		para = req.getParameter("mediumstring2");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring2(para);
		
		para = req.getParameter("mediumstring3");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring3(para);
		
		para = req.getParameter("mediumstring4");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring4(para);
		
		para = req.getParameter("mediumstring5");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring5(para);
		
		para = req.getParameter("mediumstring6");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring6(para);
		
		para = req.getParameter("mediumstring7");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring7(para);
		
		para = req.getParameter("mediumstring8");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring8(para);
		
		para = req.getParameter("mediumstring9");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring9(para);
		
		para = req.getParameter("mediumstring10");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring10(para);
		
		para = req.getParameter("longstring1");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring1(para);
		
		para = req.getParameter("longstring2");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring2(para);
		
		para = req.getParameter("longstring3");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring3(para);
		
		para = req.getParameter("longstring4");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring4(para);
		
		para = req.getParameter("longstring5");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring5(para);

		para = req.getParameter("verylongstring1");
		if((para != null) && (para.length() >0))
			customInfo.setVerylongstring1(para);
		
		para = req.getParameter("verylongstring2");
		if((para != null) && (para.length() >0))
			customInfo.setVerylongstring2(para);
		
		para = req.getParameter("treevalue1");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue1(para);
		
		para = req.getParameter("treevalue2");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue2(para);
		
		para = req.getParameter("treevalue3");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue3(para);

		para = req.getParameter("treevalue4");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue4(para);
		
		para = req.getParameter("treevalue5");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue5(para);
		
		para = req.getParameter("treevalue6");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue6(para);
		
		para = req.getParameter("treevalue7");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue7(para);
		
		para = req.getParameter("treevalue8");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue8(para);
		
		para = req.getParameter("treevalue9");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue9(para);
		
		para = req.getParameter("treevalue10");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue10(para);
		
		/*
		para = req.getParameter("codevalue1");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue1(para);
		
		para = req.getParameter("codevalue2");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue2(para);
		
		para = req.getParameter("codevalue3");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue3(para);
		
		para = req.getParameter("codevalue4");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue4(para);
		
		para = req.getParameter("codevalue5");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue5(para);
		
		para = req.getParameter("codevalue6");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue6(para);
		
		para = req.getParameter("codevalue7");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue7(para);

		para = req.getParameter("codevalue8");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue8(para);

		para = req.getParameter("codevalue9");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue9(para);
		
		para = req.getParameter("codevalue10");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue10(para);

		para = req.getParameter("codevalue11");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue11(para);
		
		para = req.getParameter("codevalue12");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue12(para);
		
		para = req.getParameter("codevalue13");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue13(para);
		
		para = req.getParameter("codevalue14");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue14(para);
		
		para = req.getParameter("codevalue15");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue15(para);
		
		para = req.getParameter("codevalue16");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue16(para);
		
		para = req.getParameter("codevalue17");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue17(para);
		
		para = req.getParameter("codevalue18");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue18(para);
		
		para = req.getParameter("codevalue19");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue19(para);
		
		para = req.getParameter("codevalue20");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue20(para);
		*/
		
		String codevalue[] = req.getParameterValues("codevalue1");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue1(value);
		}
		
		codevalue = req.getParameterValues("codevalue2");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue2(value);
		}
		
		codevalue = req.getParameterValues("codevalue3");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue3(value);
		}
		
		codevalue = req.getParameterValues("codevalue4");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue4(value);
		}
		
		codevalue = req.getParameterValues("codevalue5");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue5(value);
		}
		
		codevalue = req.getParameterValues("codevalue6");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue6(value);
		}
		
		codevalue = req.getParameterValues("codevalue7");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue7(value);
		}
		
		codevalue = req.getParameterValues("codevalue8");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue8(value);
		}
		
		codevalue = req.getParameterValues("codevalue9");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue9(value);
		}
		
		codevalue = req.getParameterValues("codevalue10");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue10(value);
		}
		
		codevalue = req.getParameterValues("codevalue11");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue11(value);
		}
		
		codevalue = req.getParameterValues("codevalue12");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue12(value);
		}
		
		codevalue = req.getParameterValues("codevalue13");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue13(value);
		}
		
		codevalue = req.getParameterValues("codevalue14");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue14(value);
		}
		
		codevalue = req.getParameterValues("codevalue15");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue15(value);
		}
		
		codevalue = req.getParameterValues("codevalue16");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue16(value);
		}
		
		codevalue = req.getParameterValues("codevalue17");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue17(value);
		}
		
		codevalue = req.getParameterValues("codevalue18");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue18(value);
		}
		
		codevalue = req.getParameterValues("codevalue19");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue19(value);
		}
		
		codevalue = req.getParameterValues("codevalue20");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
				
			customInfo.setCodevalue20(value);
		}
		
		para = req.getParameter("location1Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation1(location);
		}
		
		para = req.getParameter("location2Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation2(location);
		}
		
		para = req.getParameter("location3Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation3(location);
		}
		
		para = req.getParameter("location4Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation4(location);
		}
		
		para = req.getParameter("location5Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation5(location);
		}
		
		para = req.getParameter("organization1Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization1(org);
		}
		
		para = req.getParameter("organization2Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization2(org);
		}
		
		para = req.getParameter("organization3Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization3(org);
		}
		
		para = req.getParameter("organization4Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization4(org);
		}
		
		para = req.getParameter("organization5Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization5(org);
		}
		
		para = req.getParameter("person1Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson1(person);
		}
		
		para = req.getParameter("person2Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson2(person);
		}
		
		para = req.getParameter("person3Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson3(person);
		}
		
		para = req.getParameter("person4Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson4(person);
		}
		
		para = req.getParameter("person5Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson5(person);
		}

		
		return customInfo;
		
	}//
	
	/*
	 * method to update CustomInfo object from HTTP request
	 */
	private CustomInfo updateCustomInfoFromRequest(HttpServletRequest req, CustomInfo customInfo){
		
		String para;
		Location location;
		Organization org;
		Person person;
		
		para = req.getParameter("integer1");
		if((para != null) && (para.length() >0))
			customInfo.setInteger1(new Integer(para));
		
		para = req.getParameter("integer2");
		if((para != null) && (para.length() >0))
			customInfo.setInteger2(new Integer(para));
		
		para = req.getParameter("integer3");
		if((para != null) && (para.length() >0))
			customInfo.setInteger3(new Integer(para));
		
		para = req.getParameter("integer4");
		if((para != null) && (para.length() >0))
			customInfo.setInteger4(new Integer(para));
		
		para = req.getParameter("integer5");
		if((para != null) && (para.length() >0))
			customInfo.setInteger5(new Integer(para));
		
		para = req.getParameter("integer6");
		if((para != null) && (para.length() >0))
			customInfo.setInteger6(new Integer(para));
		
		para = req.getParameter("integer7");
		if((para != null) && (para.length() >0))
			customInfo.setInteger7(new Integer(para));
		
		para = req.getParameter("integer8");
		if((para != null) && (para.length() >0))
			customInfo.setInteger8(new Integer(para));
		
		para = req.getParameter("integer9");
		if((para != null) && (para.length() >0))
			customInfo.setInteger9(new Integer(para));
		
		para = req.getParameter("integer10");
		if((para != null) && (para.length() >0))
			customInfo.setInteger10(new Integer(para));
		
		para = req.getParameter("double1");
		if((para != null) && (para.length() >0))
			customInfo.setDouble1(new Double(para));
		
		para = req.getParameter("double2");
		if((para != null) && (para.length() >0))
			customInfo.setDouble2(new Double(para));
		
		para = req.getParameter("double3");
		if((para != null) && (para.length() >0))
			customInfo.setDouble3(new Double(para));
		
		para = req.getParameter("double4");
		if((para != null) && (para.length() >0))
			customInfo.setDouble4(new Double(para));
		
		para = req.getParameter("double5");
		if((para != null) && (para.length() >0))
			customInfo.setDouble5(new Double(para));
		
		para = req.getParameter("double6");
		if((para != null) && (para.length() >0))
			customInfo.setDouble6(new Double(para));
		
		para = req.getParameter("double7");
		if((para != null) && (para.length() >0))
			customInfo.setDouble7(new Double(para));
		
		para = req.getParameter("double8");
		if((para != null) && (para.length() >0))
			customInfo.setDouble8(new Double(para));
		
		para = req.getParameter("double9");
		if((para != null) && (para.length() >0))
			customInfo.setDouble9(new Double(para));
		
		para = req.getParameter("double10");
		if((para != null) && (para.length() >0))
			customInfo.setDouble10(new Double(para));
		
		para = req.getParameter("boolean1");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean1(new Boolean(para));
		
		para = req.getParameter("boolean2");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean2(new Boolean(para));
		
		para = req.getParameter("boolean3");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean3(new Boolean(para));
		
		para = req.getParameter("boolean4");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean4(new Boolean(para));
		
		para = req.getParameter("boolean5");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean5(new Boolean(para));
		
		para = req.getParameter("boolean6");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean6(new Boolean(para));
		
		para = req.getParameter("boolean7");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean7(new Boolean(para));
		
		para = req.getParameter("boolean8");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean8(new Boolean(para));
		
		para = req.getParameter("boolean9");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean9(new Boolean(para));
		
		para = req.getParameter("boolean10");
		if((para != null) && (para.length() >0))
			customInfo.setBoolean10(new Boolean(para));
		
		para = req.getParameter("datetime1");
		if((para != null) && (para.length() >0)){
			customInfo.setDatetime1(Timestamp.valueOf(para));
		}
		
		para = req.getParameter("datetime2");
		if((para != null) && (para.length() >0)){
			customInfo.setDatetime2(Timestamp.valueOf(para));
		}
		
		para = req.getParameter("datetime3");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime3(Timestamp.valueOf(para));
		
		para = req.getParameter("datetime4");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime4(Timestamp.valueOf(para));
		
		para = req.getParameter("datetime5");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime5(Timestamp.valueOf(para));
		
		para = req.getParameter("datetime6");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime6(Timestamp.valueOf(para));
		
		para = req.getParameter("datetime7");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime7(Timestamp.valueOf(para));
		
		para = req.getParameter("datetime8");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime8(Timestamp.valueOf(para));
		
		para = req.getParameter("datetime9");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime9(Timestamp.valueOf(para));
		
		para = req.getParameter("datetime10");
		if((para != null) && (para.length() >0))
			customInfo.setDatetime10(Timestamp.valueOf(para));
		
		para = req.getParameter("date1");
		if((para != null) && (para.length() >0))
			customInfo.setDate1(Date.valueOf(para));
		
		para = req.getParameter("date2");
		if((para != null) && (para.length() >0))
			customInfo.setDate2(Date.valueOf(para));
		
		para = req.getParameter("date3");
		if((para != null) && (para.length() >0))
			customInfo.setDate3(Date.valueOf(para));
		
		para = req.getParameter("date4");
		if((para != null) && (para.length() >0))
			customInfo.setDate4(Date.valueOf(para));
		
		para = req.getParameter("date5");
		if((para != null) && (para.length() >0))
			customInfo.setDate5(Date.valueOf(para));
		
		para = req.getParameter("date6");
		if((para != null) && (para.length() >0))
			customInfo.setDate6(Date.valueOf(para));
		
		para = req.getParameter("date7");
		if((para != null) && (para.length() >0))
			customInfo.setDate7(Date.valueOf(para));
		
		para = req.getParameter("date8");
		if((para != null) && (para.length() >0))
			customInfo.setDate8(Date.valueOf(para));
		
		para = req.getParameter("date9");
		if((para != null) && (para.length() >0))
			customInfo.setDate9(Date.valueOf(para));
		
		para = req.getParameter("date10");
		if((para != null) && (para.length() >0))
			customInfo.setDate10(Date.valueOf(para));
		
		para = req.getParameter("shortstring1");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring1(para);
		
		para = req.getParameter("shortstring2");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring2(para);
		
		para = req.getParameter("shortstring3");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring3(para);
		
		para = req.getParameter("shortstring4");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring4(para);
		
		para = req.getParameter("shortstring5");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring5(para);
		
		para = req.getParameter("shortstring6");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring6(para);
		
		para = req.getParameter("shortstring7");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring7(para);
		
		para = req.getParameter("shortstring8");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring8(para);
		
		para = req.getParameter("shortstring9");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring9(para);
		
		para = req.getParameter("shortstring10");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring10(para);
		
		para = req.getParameter("shortstring11");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring11(para);
		
		para = req.getParameter("shortstring12");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring12(para);
		
		para = req.getParameter("shortstring13");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring13(para);
		
		para = req.getParameter("shortstring14");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring14(para);
		
		para = req.getParameter("shortstring15");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring15(para);
		
		para = req.getParameter("shortstring16");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring16(para);
		
		para = req.getParameter("shortstring17");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring17(para);
		
		para = req.getParameter("shortstring18");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring18(para);
		
		para = req.getParameter("shortstring19");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring19(para);
		
		para = req.getParameter("shortstring20");
		if((para != null) && (para.length() >0))
			customInfo.setShortstring20(para);
		
		para = req.getParameter("mediumstring1");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring1(para);
		
		para = req.getParameter("mediumstring2");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring2(para);
		
		para = req.getParameter("mediumstring3");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring3(para);
		
		para = req.getParameter("mediumstring4");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring4(para);
		
		para = req.getParameter("mediumstring5");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring5(para);
		
		para = req.getParameter("mediumstring6");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring6(para);
		
		para = req.getParameter("mediumstring7");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring7(para);
		
		para = req.getParameter("mediumstring8");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring8(para);
		
		para = req.getParameter("mediumstring9");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring9(para);
		
		para = req.getParameter("mediumstring10");
		if((para != null) && (para.length() >0))
			customInfo.setMediumstring10(para);
		
		para = req.getParameter("longstring1");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring1(para);
		
		para = req.getParameter("longstring2");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring2(para);
		
		para = req.getParameter("longstring3");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring3(para);
		
		para = req.getParameter("longstring4");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring4(para);
		
		para = req.getParameter("longstring5");
		if((para != null) && (para.length() >0))
			customInfo.setLongstring5(para);
		
		para = req.getParameter("verylongstring1");
		if((para != null) && (para.length() >0))
			customInfo.setVerylongstring1(para);
		
		para = req.getParameter("verylongstring2");
		if((para != null) && (para.length() >0))
			customInfo.setVerylongstring2(para);
		
		para = req.getParameter("treevalue1");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue1(para);
		
		para = req.getParameter("treevalue2");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue2(para);
		
		para = req.getParameter("treevalue3");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue3(para);
		
		para = req.getParameter("treevalue4");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue4(para);
		
		para = req.getParameter("treevalue5");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue5(para);
		
		para = req.getParameter("treevalue6");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue6(para);
		
		para = req.getParameter("treevalue7");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue7(para);
		
		para = req.getParameter("treevalue8");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue8(para);
		
		para = req.getParameter("treevalue9");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue9(para);
		
		para = req.getParameter("treevalue10");
		if((para != null) && (para.length() >0))
			customInfo.setTreevalue10(para);
		
		/*
		para = req.getParameter("codevalue1");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue1(para);
		
		para = req.getParameter("codevalue2");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue2(para);
		
		para = req.getParameter("codevalue3");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue3(para);
		
		para = req.getParameter("codevalue4");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue4(para);
		
		para = req.getParameter("codevalue5");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue5(para);
		
		para = req.getParameter("codevalue6");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue6(para);
		
		para = req.getParameter("codevalue7");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue7(para);

		para = req.getParameter("codevalue8");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue8(para);

		para = req.getParameter("codevalue9");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue9(para);
		
		para = req.getParameter("codevalue10");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue10(para);

		para = req.getParameter("codevalue11");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue11(para);
		
		para = req.getParameter("codevalue12");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue12(para);
		
		para = req.getParameter("codevalue13");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue13(para);
		
		para = req.getParameter("codevalue14");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue14(para);
		
		para = req.getParameter("codevalue15");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue15(para);
		
		para = req.getParameter("codevalue16");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue16(para);
		
		para = req.getParameter("codevalue17");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue17(para);
		
		para = req.getParameter("codevalue18");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue18(para);
		
		para = req.getParameter("codevalue19");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue19(para);
		
		para = req.getParameter("codevalue20");
		if((para != null) && (para.length() >0))
			customInfo.setCodevalue20(para);
		 */
		
		String codevalue[] = req.getParameterValues("codevalue1");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue1(value);
		}
		
		codevalue = req.getParameterValues("codevalue2");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue2(value);
		}
		
		codevalue = req.getParameterValues("codevalue3");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue3(value);
		}
		
		codevalue = req.getParameterValues("codevalue4");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue4(value);
		}
		
		codevalue = req.getParameterValues("codevalue5");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue5(value);
		}
		
		codevalue = req.getParameterValues("codevalue6");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue6(value);
		}
		
		codevalue = req.getParameterValues("codevalue7");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue7(value);
		}
		
		codevalue = req.getParameterValues("codevalue8");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue8(value);
		}
		
		codevalue = req.getParameterValues("codevalue9");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue9(value);
		}
		
		codevalue = req.getParameterValues("codevalue10");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue10(value);
		}
		
		codevalue = req.getParameterValues("codevalue11");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue11(value);
		}
		
		codevalue = req.getParameterValues("codevalue12");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue12(value);
		}
		
		codevalue = req.getParameterValues("codevalue13");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue13(value);
		}
		
		codevalue = req.getParameterValues("codevalue14");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue14(value);
		}
		
		codevalue = req.getParameterValues("codevalue15");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue15(value);
		}
		
		codevalue = req.getParameterValues("codevalue16");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue16(value);
		}
		
		codevalue = req.getParameterValues("codevalue17");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue17(value);
		}
		
		codevalue = req.getParameterValues("codevalue18");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue18(value);
		}
		
		codevalue = req.getParameterValues("codevalue19");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue19(value);
		}
		
		codevalue = req.getParameterValues("codevalue20");
		if(codevalue != null) {
			
			String value = "";
			
			for(int i=0;i<codevalue.length;i++) {
				
				if(value == "")
					value = codevalue[i];
				else
					value += "," + codevalue[i];
			}
			
			customInfo.setCodevalue20(value);
		}
		
		para = req.getParameter("location1Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation1(location);
		}
		
		para = req.getParameter("location2Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation2(location);
		}
		
		para = req.getParameter("location3Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation3(location);
		}
		
		para = req.getParameter("location4Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation4(location);
		}
		
		para = req.getParameter("location5Id");
		if((para != null) && (para.length() >0)) {
			
			location = locationDAO.findById(new Integer(para));
			customInfo.setLocation5(location);
		}
		
		para = req.getParameter("organization1Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization1(org);
		}
		
		para = req.getParameter("organization2Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization2(org);
		}
		
		para = req.getParameter("organization3Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization3(org);
		}
		
		para = req.getParameter("organization4Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization4(org);
		}
		
		para = req.getParameter("organization5Id");
		if((para != null) && (para.length() >0)) {
			
			org = organizationDAO.findById(new Integer(para));
			customInfo.setOrganization5(org);
		}
		
		para = req.getParameter("person1Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson1(person);
		}
		
		para = req.getParameter("person2Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson2(person);
		}
		
		para = req.getParameter("person3Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson3(person);
		}
		
		para = req.getParameter("person4Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson4(person);
		}
		
		para = req.getParameter("person5Id");
		if((para != null) && (para.length() >0)) {
			
			person = personDAO.findById(new Integer(para));
			customInfo.setPerson5(person);
		}
		
		
		return customInfo;
		
	}//
	
	/*
	 * method to get map of task instance
	 */
	private void getTaskInstanceMap(HashMap<String,Object> paraMap, TaskInstance taskInstance) {
		
		Map<String,Object> taskInstanceMap;
				
		taskInstanceMap =  new HashMap<String,Object>();
		
		taskInstanceMap.put("id", taskInstance.getId());
		taskInstanceMap.put("name", taskInstance.getName());
		taskInstanceMap.put("type", taskInstance.getType());
		taskInstanceMap.put("createTime", taskInstance.getCreateTime());
		taskInstanceMap.put("actorType", taskInstance.getActorType());
		taskInstanceMap.put("actorId", taskInstance.getActorId());
		taskInstanceMap.put("auditorId", taskInstance.getAuditorId());
		taskInstanceMap.put("status", taskInstance.getStatus());
		
		taskInstanceMap.put("description", taskInstance.getDescription());
		
		if(taskInstance.getActorType().shortValue() == Task.ACTOR_TYPE_PERSON) {
			
			Person person = personDAO.findById(taskInstance.getActorId());
			taskInstanceMap.put("actorName", person.getName());
			
			if((taskInstance.getType().shortValue() == TaskInstance.TYPE_NORMAL) ||
					(taskInstance.getType().shortValue() == TaskInstance.TYPE_SUBTASK)) {
				
				List<Comment> comments = taskInstance.getComments();
				if(comments.size() > 0) {
					
					List<Map<String,Object>> commentList = new ArrayList<Map<String,Object>>();
					for(int i=0;i<comments.size();i++) {
						
						Comment comment = (Comment)comments.get(i);
						Map<String,Object> commentMap = new HashMap<String,Object>();
						
						Person author = personDAO.findById(comment.getAuthorId());
						commentMap.put("authorName", author.getName());
						
						commentMap.put("message", comment.getMessage());
						commentMap.put("createTime", comment.getCreateTime());
						
						commentList.add(commentMap);
					}
					
					taskInstanceMap.put("commentList", commentList);
					
				}
				
				if(taskInstance.getStatus().shortValue() == TaskInstance.STATUS_PROCESSING) {
					
					Task task = taskInstance.getTask();
					
					if(task != null) {
						
						taskInstanceMap.put("taskId", task.getId());

						String form = task.getForm();
						if(form != null) {
							try {
								taskInstanceMap.put("formDoc", NodeModel.parse(new InputSource(new StringReader(form))));
							} catch(Exception e) {
								logger.error("Error:" + e.getLocalizedMessage());
							}
							
							if(taskInstance.getData().size() > 0) {
								//alread saved before. so restore saved values:
								taskInstanceMap.put("data", taskInstance.getData());
							}
						}
					}
					
				} else if(taskInstance.getStatus().shortValue() == TaskInstance.STATUS_FOR_AUDIT) {
					
					Task task = taskInstance.getTask();
					
					if(task != null) {
						
						taskInstanceMap.put("taskId", task.getId());

						String form = task.getForm();
						if(form != null) {
							try {
								taskInstanceMap.put("formDoc", NodeModel.parse(new InputSource(new StringReader(form))));
							} catch(Exception e) {
								logger.error("Error:" + e.getLocalizedMessage());
							}
					
							taskInstanceMap.put("data", taskInstance.getData());
						}
					}
				}
				
				//attachments:
				List<Document> attachments = taskInstance.getAttachments();
				
				List<Map<String,Object>> attachmentList = new ArrayList<Map<String,Object>>();
				for(int i=0;i<attachments.size();i++) {
					
					Document attachment = attachments.get(i);
					
					Map<String,Object> attachmentMap = new HashMap<String,Object>();
					
					attachmentMap.put("id", attachment.getId());
					attachmentMap.put("subject", attachment.getSubject());
					attachmentMap.put("author", attachment.getAuthor());
					attachmentMap.put("number", attachment.getNumber());
					
					attachmentMap.put("keywords", attachment.getKeywords());
					attachmentMap.put("description", attachment.getDescription());
					
					attachmentMap.put("fileName", attachment.getOriginalFileName());
					attachmentMap.put("directoryId", attachment.getDirectory().getId());
					attachmentMap.put("directoryName", attachment.getDirectory().getPath());
					
					attachmentMap.put("createTime", attachment.getCreateTime());
					attachmentMap.put("creator", attachment.getCreator().getName());
					
					attachmentList.add(attachmentMap);
				}
				
				taskInstanceMap.put("attachmentList", attachmentList);
				
				//if task has attachment definition, pass it to view:
				if(taskInstance.getStatus().shortValue() == TaskInstance.STATUS_PROCESSING) {
					
					Task task = taskInstance.getTask();
					
					if(task != null) {
						
						String attachmentDefinition = task.getAttachmentDefinition();
						if(attachmentDefinition != null) {
							try {
								taskInstanceMap.put("attachmentDefinitionDoc", NodeModel.parse(new InputSource(new StringReader(attachmentDefinition))));
							} catch(Exception e) {
								logger.error("Error:" + e.getLocalizedMessage());
							}
							
						}
					}
					
				}
				
				//if the actor of the task instance is same to the owner of the node instance,
				//and this is the last task instance, and there are more than two braches leaving this node,
				//then add transition list for choose in the meantime:
				NodeInstance nodeInstance = taskInstance.getNodeInstance();
				Node node = nodeInstance.getNode();
				
				List<TaskInstance> taskInstanceList = nodeInstance.getTaskInstances();
				int i = taskInstanceList.indexOf(taskInstance);
				List<Transition> transitions = node.getLeavingTransitions();
				
				if(taskInstance.getActorId().equals(nodeInstance.getOwnerId()) &&
					(i == taskInstanceList.size() - 1) &&
					(transitions.size() > 1)) {
					
					if((taskInstance.getType() == TaskInstance.TYPE_NORMAL || taskInstance.getType() == TaskInstance.TYPE_SUBTASK) && taskInstance.getAuditorId() != null) {
						// 分支选择需要在审核任务的时候执行
					} else {
						List<Map<String,Object>> transitionList = new ArrayList<Map<String,Object>>();
						for(int j=0;j<transitions.size();j++) {
						
							Transition transition = transitions.get(j);
							
							Map<String,Object> transitionMap = new HashMap<String,Object>();
							transitionMap.put("id", transition.getId());
							transitionMap.put("name", transition.getName());
							transitionMap.put("descritpion", transition.getDescription());
						
							transitionList.add(transitionMap);
						}
					
						taskInstanceMap.put("transitionList", transitionList);
					}	
				}
				
			} else if(taskInstance.getType().shortValue() == TaskInstance.TYPE_BRANCH) {
			
				Node node = taskInstance.getNodeInstance().getNode();
				List<Transition> transitions = node.getLeavingTransitions();
				
				List<Map<String,Object>> transitionList = new ArrayList<Map<String,Object>>();
				for(int i=0;i<transitions.size();i++) {
					
					Transition transition = (Transition)transitions.get(i);
					
					Map<String,Object> transitionMap = new HashMap<String,Object>();
					transitionMap.put("id", transition.getId());
					transitionMap.put("name", transition.getName());
					transitionMap.put("descritpion", transition.getDescription());
					
					transitionList.add(transitionMap);
				}
				
				taskInstanceMap.put("transitionList", transitionList);
				
			}
			
		} else {
			//assigned to role:
			Role role = roleDAO.findById(taskInstance.getActorId());
			taskInstanceMap.put("actorName", role.getName());
		}
		
		paraMap.put("taskInstance", taskInstanceMap);
		

		
	}//getTaskInstanceMap()
	
	
	
	/*
	 * method to get map of node instance
	 */
	private void getNodeInstanceMap(HashMap<String,Object> paraMap, NodeInstance nodeInstance) {
		
		
		
		Node node = nodeInstance.getNode();
		
		Map<String,Object> nodeInstanceMap = new HashMap<String,Object>();
		nodeInstanceMap.put("id", nodeInstance.getId());
		nodeInstanceMap.put("name", node.getName());
		nodeInstanceMap.put("description", node.getDescription());
		
		if(nodeInstance.getStatus().shortValue() == NodeInstance.STATUS_ASSIGNED) {
		
			nodeInstanceMap.put("ownerType", node.getOwnerType());
			
			if (node.getOwnerType() == Node.OWNER_TYPE_ROLE){
				String ownerName = roleDAO.findById(node.getOwnerId()).getName();
				nodeInstanceMap.put("ownerName", ownerName);
			} else if (node.getOwnerType() == Node.OWNER_TYPE_PERSON){
				String ownerName = personDAO.findById(node.getOwnerId()).getName();
				nodeInstanceMap.put("ownerName", ownerName);
			}
			
//			nodeInstanceMap.put("ownerName", node.getOwnerName());

		
		} else {
			
			nodeInstanceMap.put("ownerType", Node.OWNER_TYPE_PERSON);
			
			Person owner = personDAO.findById(nodeInstance.getOwnerId());
			nodeInstanceMap.put("ownerName", owner.getName());
			
		}
		
		nodeInstanceMap.put("status", nodeInstance.getStatus());
		nodeInstanceMap.put("createTime", nodeInstance.getCreateTime());
		nodeInstanceMap.put("beginTime", nodeInstance.getBeginTime());
		
		paraMap.put("nodeInstance", nodeInstanceMap);

		
	}//getNodeInstanceMap()
	
	
	/*
	 * method to get map of process instance
	 */
	private void getProcessInstanceMap(HashMap<String,Object> paraMap, ProcessInstance processInstance) {
		
		
		Map<String,Object> processInstanceMap = new HashMap<String,Object>();
		
		processInstanceMap.put("id", processInstance.getId());
		processInstanceMap.put("beginTime", processInstance.getBeginTime());
		processInstanceMap.put("endTime", processInstance.getEndTime());
		processInstanceMap.put("status", processInstance.getStatus());
			
		Integer initiatorId = processInstance.getInitiatorId();
		Person initiator = personDAO.findById(initiatorId);
			
		processInstanceMap.put("initiatorName", initiator.getName());
		
		//global data:
		processInstanceMap.put("globalData", processInstance.getGlobalData());
		
		paraMap.put("processInstance", processInstanceMap);

		
	}//getProcessInstanceMap()
	
	public ModelAndView showViewInfo(HttpServletRequest req, HttpServletResponse res) {
		
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		List viewInfoList = this.infoDAO.findAll(new Short("0"));
		paraMap.put("viewInfoList", viewInfoList);
		
		return new ModelAndView(this.getChooseViewInfoView(),paraMap);
	}
	
	public ModelAndView showViewData(HttpServletRequest req, HttpServletResponse res) throws FileNotFoundException, IOException {
		
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		Integer viewId = new Integer(req.getParameter("viewId"));
		
		paraMap.put("viewId", viewId);
		ViewInfo vi = infoDAO.findById(viewId);
		String selectSql = vi.getSelectSql();
		
		int i, pageSize, pageNo;
		String pageNoStr, rowCountStr, pageSizeStr;
		long rowCount;
		HashMap<String,Object> fldMap;
		String paraNames[];
		Object values[];
		List<Incident> incidents;
		List<Map<String, Object>> incidentList;
		String roleIdListStr = "";
		String token[];
		List<Role> roleList;
		Integer personId;
		String viewLcIdSet, interveneLcIdSet;
		
		
		viewLcIdSet = "";
		interveneLcIdSet = "";

		//get parameters from session:
		HttpSession session = req.getSession();
		roleIdListStr = (String)session.getAttribute("roleId");
		token = roleIdListStr.split("#");
		
		personId = (Integer)session.getAttribute("personId");
		Person person = personDAO.findById(personId);
		Location personLocation = person.getLocation();
		
		roleList = new ArrayList<Role>();
		for(i=0;i<token.length;i++) {
			
			Role role = roleDAO.findById(new Integer(token[i]));
			roleList.add(role);
		}

		//get parameters fron config file:
		if(configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		//get parameters for multipages:
		pageNoStr = req.getParameter("pageNo");
		if (pageNoStr == null)
			pageNo = 1;
		else
			pageNo = Integer.parseInt(pageNoStr);

		pageSizeStr = req.getParameter("pageSize");
		if (pageSizeStr != null)
			pageSize = Integer.parseInt(pageSizeStr);
		else 
			pageSize = Integer.parseInt(configProperties.getProperty("pageSize"));
		
		//create criteria using parameters got:
		fldMap = new HashMap<String,Object>();
		String criteriaStr = "";
		String hql = "";
		
		fldMap.put("personLocationId", personLocation.getId());
		criteriaStr += "(incident.location.id = :personLocationId";
		
		//domain:
		Set<Workgroup> workgroups = person.getWorkgroups();
		StringBuffer sbWg = new StringBuffer();
		Iterator<Workgroup> it = workgroups.iterator();
		while(it.hasNext()) {
			
			Workgroup wg = (Workgroup)it.next();
			
			sbWg.append(wg.getId().toString() + ",");
			
			Set<LocationDomain> locationDomains = wg.getLocationDomains();
			if(locationDomains.size() > 0) {
				
				Iterator<LocationDomain> ldIt = locationDomains.iterator();
				while(ldIt.hasNext()) {
					
					LocationDomain ld = ldIt.next();
					
					if(ld.getViewIncident()!= null && ld.getViewIncident()) {
						
						if(viewLcIdSet == "")
							viewLcIdSet = ld.getLocationId().toString();
						else
							viewLcIdSet += "," + ld.getLocationId();
					}
					
					if(ld.getInterveneIncident()!= null && ld.getInterveneIncident()) {
						
						if(interveneLcIdSet == "")
							interveneLcIdSet = ld.getLocationId().toString();
						else
							interveneLcIdSet += "," + ld.getLocationId();
					}
				}
				
			}
			
		}
		String strWg = sbWg.toString();
		
		strWg = strWg.substring(0,strWg.length()-1);
		
		if(viewLcIdSet != "")
			criteriaStr += " or incident.location.id in (" + viewLcIdSet + ")";
		
		criteriaStr += ")";
			
		
		Set<ViewKeyValue> vkvs = vi.getViewKeyValues();
		Iterator<ViewKeyValue> vkvIt = vkvs.iterator();
		
		
		if(selectSql != null) {
			if(selectSql.indexOf("like :" + ViewKeyValue.property_location) > 0){
				fldMap.put(ViewKeyValue.property_location, personLocation.getCode() + "%");
			} else if(selectSql.indexOf(ViewKeyValue.property_location) > 0){
				fldMap.put(ViewKeyValue.property_location, personLocation.getCode());
			}
		}
		
		if(selectSql != null && selectSql.indexOf(ViewKeyValue.property_login) > 0) {
			fldMap.put(ViewKeyValue.property_login, personId);
		}
		
		if(selectSql != null){
			if(selectSql.indexOf("like :" + ViewKeyValue.property_workgroup) > 0){
				
				String subSelectSql = "in ( select person.id from com.telinkus.itsm.data.person.Person person " +
									  "join person.workgroups wg where wg.id = :workgroupId)";
				selectSql = selectSql.replace("like :" + ViewKeyValue.property_workgroup, subSelectSql);
				
			}else if(selectSql.indexOf("in :" + ViewKeyValue.property_workgroup) > 0){
				
				String subSelectSql = "( select person.id from com.telinkus.itsm.data.person.Person person " +
									  "join person.workgroups wg where wg.id in (" + strWg + "))";
				selectSql = selectSql.replace(":" + ViewKeyValue.property_workgroup, subSelectSql);
			}
		}
		
		while (vkvIt.hasNext()) {
			ViewKeyValue vkv = vkvIt.next();
			if(1 == vkv.getValueType().intValue()){
				fldMap.put(vkv.getKeyString(), new Integer(vkv.getValueString()));
			} else if(2 == vkv.getValueType().intValue()){
				fldMap.put(vkv.getKeyString(), new Short(vkv.getValueString()));
			} else if(3 == vkv.getValueType().intValue()){
				fldMap.put(vkv.getKeyString(), java.sql.Date.valueOf(vkv.getValueString()));
			} else if(5 == vkv.getValueType().intValue()){
				fldMap.put(vkv.getKeyString(), java.sql.Timestamp.valueOf(vkv.getValueString()));
			} else if(4 == vkv.getValueType().intValue()){
				fldMap.put(vkv.getKeyString(), vkv.getValueString());
			}
			
		}
		
		//replace placeholder to operationDate
		if(selectSql != null) {
			if(selectSql.indexOf("tempApplyMonth") > 0){
				selectSql = selectSql.replace("tempApplyMonth", DateUtils.computeMonth(Integer.parseInt(fldMap.get("apply").toString())));
				fldMap.remove("apply");
			}
			if(selectSql.indexOf("tempFinishMonth") > 0){
				selectSql = selectSql.replace("tempFinishMonth", DateUtils.computeMonth(Integer.parseInt(fldMap.get("finish").toString())));
				fldMap.remove("finish");
			}
			
			if(selectSql.indexOf("tempApplyDate") > 0){
				selectSql = selectSql.replace("tempApplyDate", DateUtils.computeDate(Integer.parseInt(fldMap.get("apply").toString())));
				fldMap.remove("apply");
			}
			if(selectSql.indexOf("tempFinishDate") > 0){
				selectSql = selectSql.replace("tempFinishDate", DateUtils.computeDate(Integer.parseInt(fldMap.get("finish").toString())));
				fldMap.remove("finish");
			}
		}
		
		//prepare parameters for query:
		Object flds[] = fldMap.keySet().toArray();
		paraNames = new String[flds.length];
		for (i = 0; i < flds.length; i++) {
			String fld = (String) flds[i];
			paraNames[i] = fld;
		}

		values = fldMap.values().toArray();
		
		//count first:
		rowCountStr = req.getParameter("rowCount");
		if (rowCountStr == null) {

			if (criteriaStr.length() > 0) {
//				hql = "select count(*) from com.telinkus.itsm.data.incident.Incident where " + criteriaStr;
				hql = "select count(distinct incident) " + selectSql + " and " + criteriaStr;
			} else {
//				hql = "select count(*) from com.telinkus.itsm.data.incident.Incident";
				hql = "select count(distinct incident) " + selectSql;
			}

			rowCount = ((Long) incidentDAO.findByNamedParam(hql, 1, paraNames, values).get(0)).longValue();
			logger.debug("rowCount=" + rowCount);
			
		}else {
			rowCount = Long.parseLong(rowCountStr);
		}
		

		incidentList = new ArrayList<Map<String, Object>>();

		//initialize the return list:
		if (rowCount > 0) {
			
//			hql = "from com.telinkus.itsm.data.incident.Incident";
			hql = selectSql;
			
			hql = "select distinct incident " + hql + " and " + criteriaStr;
			logger.debug("hql=" + hql);

			//call DAO to search:
			incidents = incidentDAO.findByQuery(hql, paraNames, values, pageSize*(pageNo-1), pageSize);
			
			for (i = 0; i < incidents.size(); i++) {
				Map<String,Object> incidentMap = new HashMap<String,Object>();
				
				Incident incident = (Incident)incidents.get(i);

				incidentMap.put("id", incident.getId());
				
				Location location = incident.getLocation();
				if(location != null)
					incidentMap.put("location", location.getName());
				
				incidentMap.put("requestNo", incident.getRequestNo());
				//incidentMap.put("category", incident.getCategory().getName());
				incidentMap.put("category", incident.getCategory().getPath());
				incidentMap.put("subject", incident.getSubject());
				
				incidentMap.put("sourceType", incident.getSourceType());
				
				Customer customer = incident.getCustomer();
				
				if(null != incident.getApplicant()){
					incidentMap.put("applicant", incident.getApplicant().getName());
				} else if(null != customer) {
					
					incidentMap.put("applicant", customer.getName());
				}
				incidentMap.put("applicationTime", incident.getApplicationTime());

				incidentMap.put("status", incident.getStatus());

				Timestamp promiseFinishTime = incident.getPromiseFinishTime();
				incidentMap.put("promiseFinishTime", promiseFinishTime);
				incidentMap.put("finishTime", incident.getFinishTime());
				
				Set<Person> handlers = incident.getHandlers();
				List<Map<String,Object>> handlerList = new ArrayList<Map<String,Object>>();
				
				Iterator<Person> it1 = handlers.iterator();
				while(it1.hasNext()) {
				
					Person handler = it1.next();
					
					Map<String,Object> handlerMap = new HashMap<String,Object>();
					handlerMap.put("name", handler.getName());
					
					handlerList.add(handlerMap);
				}
				
				incidentMap.put("handlerList", handlerList);
				
				//check if it can be intervened by current person:
				if(location != null) {
				
					if(interveneLcIdSet.indexOf(location.getId().toString()) >=0 )
						incidentMap.put("canIntervene", true);
					else
						incidentMap.put("canIntervene", false);
					
				}
				
				incidentList.add(incidentMap);
			}
			
		}
		
		paraMap.put("incidentList", incidentList);
		
		
		
		//set extra parameters:
		paraMap.put("pageNo", new Integer(pageNo));
		paraMap.put("pageSize", new Integer(pageSize));
		paraMap.put("rowCount", new Long(rowCount));
		
		return new ModelAndView(this.getShowDataView(),paraMap);
		
	}
	
	
	/*
	 * method to send mail(or/and sms) to handlers, cc participators
	 */
	private boolean sendMessage(Incident incident) {
		
		boolean success = false;
		
		
		Mode mode = messageDAO.findModeByModule(ItsmConstants.MODULE_INCIDENT);
			
		if(mode != null && mode.getMail()) {
		
			MailServer mailServer = messageDAO.findMailServer();
		
			if(mailServer != null) {
			
				List<Map<String,String>> toRecipientList = MessageUtils.makeMailRecipients(incident.getHandlers());
				List<Map<String,String>> ccRecipientList = MessageUtils.makeMailRecipients(incident.getParticipators());
				
				if(toRecipientList.size() > 0) {
				
					String messageSubject;
					String messageContent;
					
					MailFormat mailFormat = messageDAO.findMailFormatByModule(ItsmConstants.MODULE_INCIDENT);
					
					if(mailFormat != null) {
						
						messageSubject = MessageUtils.processTemplate(mailFormat.getSubject(),
																		getMap(incident));
						messageContent = MessageUtils.processTemplate(mailFormat.getContent(),
																		getMap(incident));
						
					} else {
						messageSubject = incident.getCreateTime() + ":" + incident.getSubject();
						messageContent = incident.getContent();
					}
					
					success = MessageUtils.sendMail(mailServer,  toRecipientList, ccRecipientList,
														messageSubject, messageContent, logger); 
				}
			
			} else {
				//set flag to false:
				success = false;
			}
		}
		
		if(mode != null && mode.getSms()) {
			
			SmsApi smsApi = messageDAO.findSmsApi();
				
			if(smsApi != null) {
				
				List<String> toRecipientList = MessageUtils.makeSmsRecipients(incident.getHandlers());
				List<String> ccRecipientList = MessageUtils.makeSmsRecipients(incident.getParticipators());
				
				if(toRecipientList.size() > 0) {
					
					String message;
					
					SmsFormat smsFormat = messageDAO.findSmsFormatByModule(ItsmConstants.MODULE_INCIDENT);
					
					if(smsFormat != null) {
						
						message = MessageUtils.processTemplate(smsFormat.getMessage(),
																		getMap(incident));
						
					} else {
						
						message = incident.getCreateTime() + ":" + incident.getSubject();
					}
					
					toRecipientList.addAll(ccRecipientList);
					
					success = MessageUtils.sendSMS(smsApi, toRecipientList, message, logger);
					
				}
			
			}
		}
		
			
		return success;
		
	}//sendMessage()
	
	
	/*
	 * total count of incident for my work
	 */
	public static long totalCountOfMyWork(HttpServletRequest req, IncidentDAO incidentDAO, Integer personId, ProcessDAO processDAO,
							Integer organizationId, List<Integer> roleIdList, Integer locationId){
		
		long rowCount = 0;
		String roleIdStr = "";
		for(int i = 0; i < roleIdList.size(); i++) {
			roleIdStr += "," + roleIdList.get(i);
		}
		
		rowCount = rowCount + com.telinkus.itsm.controller.incident.FindWorkController.countMyWork(req, incidentDAO, null, personId, roleIdStr.replaceFirst(",", ""))
				 + com.telinkus.itsm.controller.incident.FindTicketWorkController.countMyWork(incidentDAO, personId)
				 + com.telinkus.itsm.controller.incident.FindOpenTaskController.countOpenTask(req, processDAO, null, personId, organizationId, locationId, roleIdList)
				 + com.telinkus.itsm.controller.incident.FindAuditTaskController.countAuditTask(req, processDAO, null, personId);
		
		return rowCount;
		
	}//end totalCountOfMyWork
	
	
	/*
	 * count of Incident_STATUS_ASSIGNED
	 */
	public static long countOfAssigned(IncidentDAO incidentDAO, Integer personId){
		
		String paraNames[] = new String[2];
		paraNames[0] = "personId";
		paraNames[1] = "status";
			
		Object values[] = new Object[2];
		values[0] = personId;
		values[1] = new Short(Incident.STATUS_ASSIGNED);
		
		String hql = "select distinct incident from com.telinkus.itsm.data.incident.Incident incident left join incident.handlers h left join incident.participators p"
				+ " where (:personId in h.id or :personId in p.id) and (incident.status = :status)";
		
		List<Incident> incidents = incidentDAO.findByNamedParam(hql, 0, paraNames, values);
		
		long count = incidents.size();
		
		return count;
		
	}
	
	
	/*
	 * count of Incident_STATUS_PROCESSING
	 */
	public static long countOfProcessing(IncidentDAO incidentDAO, Integer personId){
		
		String paraNames[] = new String[2];
		paraNames[0] = "personId";
		paraNames[1] = "status";
			
		Object values[] = new Object[2];
		values[0] = personId;
		values[1] = new Short(Incident.STATUS_PROCESSING);
		
		String hql = "select distinct incident from com.telinkus.itsm.data.incident.Incident incident left join incident.handlers h left join incident.participators p"
				+ " where (:personId in h.id or :personId in p.id) and (incident.status = :status)";
		
		List<Incident> incidents = incidentDAO.findByNamedParam(hql, 0, paraNames, values);
		
		long count = incidents.size();
		
		return count;
		
	}
	
	
	/*
	 * count of Incident_STATUS_FEEDBACKING
	 */
	public static long countOfFeedbacking(IncidentDAO incidentDAO, Integer personId){
		
		String paraNames[] = new String[2];
		paraNames[0] = "personId";
		paraNames[1] = "status";
			
		Object values[] = new Object[2];
		values[0] = personId;
		values[1] = new Short(Incident.STATUS_FEEDBACKING);
		
		String hql = "select distinct incident from com.telinkus.itsm.data.incident.Incident incident left join incident.handlers h left join incident.participators p"
			+ " where (:personId = incident.feedbacker.id and incident.status = :status)";
		
		List<Incident> incidents = incidentDAO.findByNamedParam(hql, 0, paraNames, values);
		
		long count = incidents.size();
		
		return count;
		
	}
	
	
	/*
	 * methot to close all ticket
	 */
	public ModelAndView closeAllTicket(HttpServletRequest req, HttpServletResponse res){
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		Long id = new Long(req.getParameter("id"));
		
		Incident incident = this.incidentDAO.findById(id);
		
		for(int i = 0; i < incident.getTickets().size(); i++){
			
			IncidentTicket ticket = incident.getTickets().get(i);
			
			if(ticket.getStatus() != IncidentTicket.STATUS_CLOSED && ticket.getStatus() != IncidentTicket.STATUS_FORCE_CLOSED){
				
				ticket.setFinishPercentage((short)100);
				ticket.setStatus(IncidentTicket.STATUS_FORCE_CLOSED);
				ticket.setFinishTime(new Timestamp(System.currentTimeMillis()));
				this.incidentDAO.updateTicket(ticket);
			}
		}
		
		paraMap.put("windowLocation", "incident_action.do?operation=handle&id=" + id);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
	}
	
	
	/*
	 * method to deassign ticket
	 */
	public ModelAndView assignTicket(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		String token[];
		
		String incidentIdStr = req.getParameter("incidentId");
		
		Long ticketId = new Long(req.getParameter("id"));
		IncidentTicket ticket = incidentDAO.findTicketByIdForUpdate(ticketId);
		
		String handlerPersonIdListStr = req.getParameter("handlerPersonId");
		token = handlerPersonIdListStr.split(",");
		
		//clear the handler
		ticket.getHandlers().clear();

		for(int i=0;i<token.length;i++) {
			Integer handlerPersonId = new Integer(token[i]);
			Person handler = personDAO.findById(handlerPersonId);
			ticket.getHandlers().add(handler);	
		}
		
		ticket.setStatus(new Short(IncidentTicket.STATUS_ASSIGNED));
		ticket.setStartTime(null);
		
		//call DAO to update:
		incidentDAO.updateTicket(ticket);
	
		paraMap.put("parentReload", true);
		
		Incident incident = incidentDAO.findById(new Long(incidentIdStr));
		
		//增加“强制分配子工单”动作的消息通知
		try {
			sendNotice(ItsmConstants.MODULE_INCIDENT, IncidentActivity.TYPE_ASSIGN_TICKET, incident, req, ticket);
		} catch (Exception e) {
			logger.error("finish menthod sendNotice error : "+Utils.getStackTrace(e));
		}
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//assignTicket()
	
	
	/*
	 * method to use default method to build requestNo
	 */
	public String buildRequestNo(Incident incident){
		
		String requestNo = incident.getId().toString();
    	
	    int req_no_len = Integer.parseInt(configProperties.getProperty("requestNoLength"));
	    
	    if(requestNo.length() < req_no_len) {
	    	
	    	for(int i = requestNo.length(); i < req_no_len; i++) {
	    		requestNo = "0" + requestNo;
	    	}
	    }
	    
	    requestNo = configProperties.getProperty("incidentRequestPrefix") + requestNo;
	    
	    return requestNo;
	}
	
	
	/*
	 * method to use default method to build new requestNo
	 */
	public static synchronized String buildRequestNo2(Incident incident) {

		java.util.Date date = new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String formattedDate = formatter.format(date);

		ThreadNo tn = threadNoDAO.find();
		if (tn == null) {
			tn = new ThreadNo(1, formattedDate);
			threadNoDAO.insert(tn);
		}

		int threadNo = tn.getThreadNo();
		String tagStr = tn.getTagStr();

		logger.debug("threadNo is :" + threadNo);
		logger.debug("tagStr is :" + tagStr);

		String requestNo = "";

		String prefix = Incident.REQ_NO_PREFIX_SJ;
		if (incident.getReservedInteger6()!=null && incident.getReservedInteger6().equals(Incident.TYPE_QUERY_FORM)) {
			prefix = Incident.REQ_NO_PREFIX_CX;
		}

		if (!tagStr.equals(formattedDate)) {
			threadNo = 1;
			tagStr = formattedDate;
			tn.setThreadNo(threadNo);
			tn.setTagStr(formattedDate);
		}

		String suffix = "";

		if (threadNo < 10) {
			suffix = "000" + threadNo;
		} else if (10 <= threadNo && threadNo < 100) {
			suffix = "00" + threadNo;
		} else if (100 <= threadNo && threadNo < 1000) {
			suffix = "0" + threadNo;
		} else if (1000 <= threadNo) {
			suffix = "" + threadNo;
		}
		
		requestNo = prefix + formattedDate + suffix;
		threadNo++;
		
		tn.setThreadNo(threadNo);
		threadNoDAO.update(tn);

		return requestNo;
	}
	
	
	public boolean sendNotice(Short moduleId, Short activityType, Incident incident, HttpServletRequest req) throws JSONException, FileNotFoundException, IOException {
		return sendNotice( moduleId, activityType, incident, req, null);
	}
	
	/*
	 * method to send mail(or/and sms) to target
	 */
	public boolean sendNotice(Short moduleId, Short activityType, Incident incident, HttpServletRequest req, IncidentTicket ticket) throws JSONException, FileNotFoundException, IOException {
		
		HttpSession session = req.getSession();
		Person currentPerson = getCurrentPerson(session);
		
		RequestContext requestContext = new RequestContext(req);
		List<Incident> incidents;
		boolean success = false;
		Map<String,String> officeGroupMap = new HashMap<String,String>();
		Map<String,String> wgOrg = new HashMap<String,String>();
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(resource.getFile()));
			String slaOfficeGroup = properties.getProperty("sla_office_group");
			String[] offices = slaOfficeGroup.split(";");
			for(String o : offices){
				String[] office = o.split(":");
				String[] groups = office[1].split(",");
				for(String g : groups){
					officeGroupMap.put(g, office[0]);
				}
			}
			
			String slaOrgWg = properties.getProperty("sla_org_wg");
			String[] orgWgs = slaOrgWg.split(";");
			for(String orgWg : orgWgs){
				String[] wg = orgWg.split(":");
				String[] orgs = wg[1].split(",");
				for(String org : orgs){
					wgOrg.put(org, wg[0]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<AfterNotification> afterNotices = afterNotificationDAO.getDataByModuleAndActType(moduleId, activityType);
		
		if(afterNotices.size() > 0){
			
			NotificationJsonData notificationJsonData = notificationJsonDAO.getDataByModule(ItsmConstants.MODULE_INCIDENT, NotificationJsonData.TYPE_AFTER);
			JSONObject jsonObject = new JSONObject(notificationJsonData.getJsonString());
			
			for(int i = 0; i < afterNotices.size(); i++){
				AfterNotification afterNotice = afterNotices.get(i);
				
				incidents = getList(afterNotice.getCondition(), jsonObject, activityType, i, incident);
				
				if(incidents.size() > 0){

					String emailTitle = afterNotice.getEmailSubject();
					String emailContent = afterNotice.getEmailContent();
					String smsContent = afterNotice.getSmsContent();
					String noticeSubject = afterNotice.getNoticeSubject();
					String noticeContent = afterNotice.getNoticeContent();
					
					emailTitle = replaceIncidentProperty(emailTitle, incident, requestContext);
					emailContent = replaceIncidentProperty(emailContent, incident, requestContext);
					smsContent = replaceIncidentProperty(smsContent, incident, requestContext);
					noticeSubject = replaceIncidentProperty(noticeSubject, incident, requestContext);
					noticeContent = replaceIncidentProperty(noticeContent, incident, requestContext);
					
					JSONObject targetJsonObject = new JSONObject(afterNotice.getTarget());
					
					Set<Person> recipients = new HashSet<Person>();
					Set<Workgroup> workgroupSet = new HashSet<Workgroup>();
					Set<Role> roleSet = new HashSet<Role>();
					
					if(targetJsonObject.has("applicant") && targetJsonObject.getBoolean("applicant")){
						recipients.add(incident.getApplicant());
					}
					
					if(targetJsonObject.has("creator") && targetJsonObject.getBoolean("creator")){
						recipients.add(incident.getCreator());
					}
					
					if(targetJsonObject.has("participators") && targetJsonObject.getBoolean("participators")){
						Iterator<Person> iter = incident.getParticipators().iterator();
						while(iter.hasNext()){
							recipients.add(iter.next());
						}
					}
					//增加子工单处理人
					if(targetJsonObject.has("ticketHandler") && targetJsonObject.getBoolean("ticketHandler")){
						Iterator<Person> iter = ticket.getHandlers().iterator();
						while(iter.hasNext()){
							recipients.add(iter.next());
						}
					}
					
					if(targetJsonObject.has("groupLeader") && targetJsonObject.getBoolean("groupLeader")){
						Iterator<Person> handler = incident.getHandlers().iterator();
						Person p = null;
						while(handler.hasNext()){
							p = handler.next();
							Set<Person> leaderSet = p.getOrganization().getLeaders();
							Iterator<Person> leaderIterator = leaderSet.iterator();
							while(leaderIterator.hasNext()){
								recipients.add(leaderIterator.next());
							}
						}
					}
					
					if(targetJsonObject.has("officeManager") && targetJsonObject.getBoolean("officeManager")){
						Iterator<Person> handlers = incident.getHandlers().iterator();
						Person per = null;
						while(handlers.hasNext()){
							per = handlers.next();
							
							Set<Person> leaderSet = new HashSet<Person>();
							String wgId = wgOrg.get(per.getOrganization().getId().toString());
							if(wgId != null){
								Workgroup workgroup = workgroupDao.findById(Integer.valueOf(wgId));
								leaderSet = workgroup.getMembers();
							}else{
								String officeId = officeGroupMap.get(per.getOrganization().getId().toString());
								Organization org = this.organizationDAO.findById(Integer.valueOf(officeId));
								leaderSet = org.getLeaders();
							}
							Iterator<Person> leaderIterator = leaderSet.iterator();
							while(leaderIterator.hasNext()){
								recipients.add(leaderIterator.next());
							}
						}
					}
					
					if(targetJsonObject.has("currWatchstander") && targetJsonObject.getBoolean("currWatchstander")){
						Set<Person> dutyTaskPersons = dutyTaskDao.findDutyTaskPersons(new java.util.Date());
						Iterator<Person> iter = dutyTaskPersons.iterator();
						while(iter.hasNext()){
							recipients.add(iter.next());
						}
					}
					
					if(targetJsonObject.has("handler") && targetJsonObject.getBoolean("handler")){
						Iterator<Person> iter = incident.getHandlers().iterator();
						while(iter.hasNext()){
							recipients.add(iter.next());
						}
					}
					
					if(targetJsonObject.has("personList")){
						String[] personList = targetJsonObject.getString("personList").split(",");
						for(String personId : personList){
							recipients.add(personDAO.findById(Integer.parseInt(personId)));
						}
					}

					if(targetJsonObject.has("workgroup") && targetJsonObject.getBoolean("workgroup")) {
						String workgroupList = targetJsonObject.getString("workgroupList");
						String[] workgroupArray = workgroupList.split(",");
						for(int j = 0; j < workgroupArray.length; j++) {
							workgroupSet.add(workgroupDao.findById(new Integer(workgroupArray[j])));
						}
					}
					if(targetJsonObject.has("role") && targetJsonObject.getBoolean("role")) {
						String rolesList  = targetJsonObject.getString("rolesList");
						String[] roleArray = rolesList.split(",");
						for(int j = 0; j < roleArray.length; j++) {
							roleSet.add(roleDAO.findById(new Integer(roleArray[j])));
						}
					}
					
					List<Map<String,String>> recipientList = null;
					MailServer mailServer = messageDAO.findMailServer();
					if(mailServer != null) {
						recipientList = MessageUtils.makeMailRecipients(recipients, workgroupSet, roleSet);
					}
					
					String mobilePhones = null;
					SmsApi smsApi = smsApiDAO.findSMSApi();
					if(smsApi != null) {
						mobilePhones = MessageUtils.makeMobilePhoneStr(recipients, workgroupSet, roleSet);
					}
					 
					MessageThreadUtil messageThreadUtil = new MessageThreadUtil(mailServer, recipientList, null, emailTitle, emailContent, smsApi, mobilePhones, smsContent, logger);
					messageThreadUtil.start();
					
					// 生成系统通知
					if (EntityUtils.isNotEmpty(noticeSubject)) {
						if (configProperties == null) {
							configProperties = new Properties();
							configProperties.load(new FileInputStream(resource.getFile()));
						}
						String incidentViewURL = configProperties.getProperty("incidentViewURL");
						String viewLink = incidentViewURL + "&id=" + incident.getId();
						
						Notice notice = new Notice();
						notice.setCategory(Notice.CATEGORY_INCIDENT);
						notice.setPriority(new Short(Notice.PRIORITY_NORMAL));
						notice.setSendTime(new Timestamp(System.currentTimeMillis()));
						notice.setSenderId(currentPerson.getId());
						List<Person> persons = MessageUtils.receiversForList(recipients, workgroupSet, roleSet);
						for (Person toPerson : persons) {
							NoticeReceiver nr = new NoticeReceiver();
							nr.setType(NoticeReceiver.TYPE_PERSON);
							nr.setReceiverId(toPerson.getId());
							notice.getReceivers().add(nr);
						}
						notice.setSubject(noticeSubject);
						notice.setMessage(noticeContent);
						notice.setRequestNo(incident.getRequestNo());
						notice.setViewLink(viewLink);
						
						noticeDAO.insert(notice, null, null);
					}
				}
			}
		}
			
		return success;
		
	}//sendNotice()
	
	
	/*
	 * method to replace String to incident property
	 */
	public String replaceIncidentProperty(String target, Incident incident, RequestContext requestContext){
		
		if(target == null){
			return "";
		}
		
		Map<String,String> propertyMap = new HashMap<String, String>();
		propertyMap.put("<" + requestContext.getMessage("request_no", "Request No") + ">", incident.getRequestNo());
		propertyMap.put("<" + requestContext.getMessage("location", "Location") + ">", incident.getLocation() == null ?"" :incident.getLocation().getName());
		propertyMap.put("<" + requestContext.getMessage("category", "Category") + ">", incident.getCategory().getName());
		propertyMap.put("<" + requestContext.getMessage("source", "Source") + requestContext.getMessage("type", "Type") + ">", getSourceTypeDesc(incident.getSourceType(),incident,requestContext));
		propertyMap.put("<" + requestContext.getMessage("applicant", "Applicant") + ">", incident.getApplicant() == null ?"" : incident.getApplicant().getName());
		propertyMap.put("<" + requestContext.getMessage("application", "Application") + requestContext.getMessage("time", "Time") + ">", incident.getApplicationTime() == null ?"": incident.getApplicationTime().toString());
		propertyMap.put("<" + requestContext.getMessage("expect_finish_time", "Expect Finish Time") + ">", incident.getExpectFinishTime() == null ?"" : incident.getExpectFinishTime().toString());
		propertyMap.put("<" + requestContext.getMessage("promise_finish_time", "Promise Finish Time") + ">", incident.getPromiseFinishTime() == null ?"" : incident.getPromiseFinishTime().toString());
		propertyMap.put("<" + requestContext.getMessage("promise", "Promise") + requestContext.getMessage("start", "Start") + requestContext.getMessage("time", "Time") + ">", 
				incident.getPromiseStartTime() == null ?"" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(incident.getPromiseStartTime()));
		propertyMap.put("<" + requestContext.getMessage("severity", "Severity") + ">", incident.getSeverity() == null ?"": incident.getSeverity().getName());
		propertyMap.put("<" + requestContext.getMessage("effect", "Effect") + ">", incident.getEffect() == null ?"": incident.getEffect().getName());
		propertyMap.put("<" + requestContext.getMessage("urgency", "Urgency") + ">", incident.getUrgency() == null ?"": incident.getUrgency().getName());
		propertyMap.put("<" + requestContext.getMessage("priority", "Priority") + ">", incident.getPriority() == null ?"": incident.getPriority().toString());
		propertyMap.put("<" + requestContext.getMessage("handlers", "Handlers") + ">", incident.getHandlers() == null ?"" : getPersonNames(incident.getHandlers()));
		propertyMap.put("<" + requestContext.getMessage("participators", "Participators") + ">", incident.getParticipators() == null ?"" : getPersonNames(incident.getParticipators()));
		propertyMap.put("<" + requestContext.getMessage("creator", "Creator") + ">", incident.getCreator() == null ?"": incident.getCreator().getName());
		propertyMap.put("<" + requestContext.getMessage("create_time", "Create Time") + ">", incident.getCreateTime() == null ?"": incident.getCreateTime().toString());
		propertyMap.put("<" + requestContext.getMessage("processing_status", "Processing Status") + ">", incident.getProcessingStatus() == null ?"" : incident.getProcessingStatus().getName());
		propertyMap.put("<" + requestContext.getMessage("finish", "finish") + requestContext.getMessage("percentage", "percentage") + ">", incident.getFinishPercentage() == null ?"" : incident.getFinishPercentage() + "%");
		propertyMap.put("<" + requestContext.getMessage("status", "Status") + ">", incident.getStatus() == null ?"" : getStatusDesc(incident.getStatus(),requestContext));
		propertyMap.put("<" + requestContext.getMessage("subject", "Subject") + ">", incident.getSubject() == null ?"": incident.getSubject());
		propertyMap.put("<" + requestContext.getMessage("content", "Content") + ">", incident.getContent() == null ?"": incident.getContent());
		
		Iterator<String> iter = propertyMap.keySet().iterator();
		String temp = "";
		if(target.length() > 0){
			while(iter.hasNext()){
				temp = iter.next();
				if(target.indexOf(temp) != -1){
					target = target.replaceAll(temp, propertyMap.get(temp));
				}
			}
		}	
		
		return target;
		
	}
	
	
	/*
	 * method to get source type name
	 */
	public String getSourceTypeDesc(Short sourceType, Incident incident, RequestContext requestContext) {
		String result = "";
		switch (sourceType) {
		case Incident.SOURCETYPE_PHONE:
			result = requestContext.getMessage("phone", "Phone");
			break;
		case Incident.SOURCETYPE_FAX:
			result = requestContext.getMessage("fax", "Fax");
			break;
		case Incident.SOURCETYPE_EMAIL:
			result = requestContext.getMessage("EMail", "EMail");
			break;
		case Incident.SOURCETYPE_CUSTOM:
			result = incident.getCustomSourceType().getName();
			break;
		case Incident.SOURCETYPE_OTHER:
			result = incident.getOtherSourceType();
			break;

		default:
			break;
		}
		return result;
		
	}
	
	
	/*
	 * method to get status name
	 */
	public String getStatusDesc(Short status, RequestContext requestContext){
		
		String result = "";
		switch (status) {
		case Incident.STATUS_ASSESSING:
			result = requestContext.getMessage("assessing", "Assessing");
			break;
		case Incident.STATUS_ASSIGNED:
			result = requestContext.getMessage("assigned", "Assigned");
			break;
		case Incident.STATUS_PROCESSING:
			result = requestContext.getMessage("processing", "Processing");
			break;
		case Incident.STATUS_FEEDBACKING:
			result = requestContext.getMessage("feedbacking", "Feedbacking");
			break;
		case Incident.STATUS_CLOSED:
			result = requestContext.getMessage("closed", "Closed");
			break;
		case Incident.STATUS_UNASSIGNED:
			result = requestContext.getMessage("unassigned", "Unassigned");
			break;
		case Incident.STATUS_ANNULLED:
			result = requestContext.getMessage("annulled", "Annulled");
			break;
		case Incident.STATUS_REJECTED:
			result = requestContext.getMessage("rejected", "Rejected");
			break;

		default:
			break;
		}
		return result;
	}
	
	
	/*
	 *method to get person names 
	 */
	public String getPersonNames(Set<Person> personSet){
		
		StringBuffer personNames = new StringBuffer();
		String returnStr = "";
		
		Iterator<Person> iter = personSet.iterator();
		while(iter.hasNext()){
			Person person = iter.next();
			String name = person.getName();
			//String mobilePhone = person.getMobilePhone();
			//personNames.append(name+"("+ mobilePhone +")" + ",");
			personNames.append(name+",");
		}
		if(personNames.length() > 0){
			returnStr = personNames.toString().substring(0, personNames.toString().length() - 1);
		}
		
		return returnStr;
	}
	
	/**
	 * 发送系统通知
	 * @param moduleId
	 * @param activityType
	 * @param incident 
	 * @param processInstance 
	 * @param taskInstance 
	 * @param nodeInstance 
	 * @param req 
	 * @throws JSONException 
	 */
	public Boolean processSendNotification(Short moduleId, Short activityType, Incident incident, ProcessInstance processInstance, NodeInstance nodeInstance, TaskInstance taskInstance, HttpServletRequest req) throws JSONException {
		RequestContext requestContext = new RequestContext(req);
		Boolean success = false;
		
		// 查询流程事后通知
		List<AfterNotification> list = afterNotificationDAO.getDataByModuleAndActType(moduleId, activityType);
		for(int i = 0; i < list.size(); i++) {
			AfterNotification afterNotification = list.get(i);
			if(afterNotification.getTarget() != null) {

				if(incident != null) {

					Set<Person> personSet = new HashSet<Person>();
					Set<Workgroup> workgroupSet = new HashSet<Workgroup>();
					Set<Role> roleSet = new HashSet<Role>();
					
					
					// 生成通知对象json对象
					JSONObject targetJsonObject = new JSONObject(afterNotification.getTarget());
					String emailTitle = afterNotification.getEmailSubject();//邮件主题
					String emailContent = afterNotification.getEmailContent();//邮件内容
					String smsContent = afterNotification.getSmsContent();//手机短信内容
					emailTitle = replaceIncidentInfoToStr(emailTitle, incident, requestContext);
					emailTitle = replaceNodeInfoToStr(emailTitle, nodeInstance, requestContext, activityType);
					emailTitle = replaceTaskInfoToStr(emailTitle, taskInstance, requestContext);
					emailContent = replaceIncidentInfoToStr(emailContent, incident, requestContext);
					emailContent = replaceNodeInfoToStr(emailContent, nodeInstance, requestContext, activityType);
					emailContent = replaceTaskInfoToStr(emailContent, taskInstance, requestContext);
					smsContent = replaceIncidentInfoToStr(smsContent, incident, requestContext);
					smsContent = replaceNodeInfoToStr(smsContent, nodeInstance, requestContext, activityType);
					smsContent = replaceTaskInfoToStr(smsContent, taskInstance, requestContext);
					
					if(targetJsonObject.has("creator") && targetJsonObject.getBoolean("creator")) {//是否给创建者发送
						Person creator = incident.getCreator();	
						if(creator != null) {
							personSet.add(creator);
						}
					}
					if(targetJsonObject.has("applicant") && targetJsonObject.getBoolean("applicant")){
						Person applicant = incident.getApplicant();	
						if(applicant != null) {
							personSet.add(applicant);
						}
					}
					if(targetJsonObject.has("participators") && targetJsonObject.getBoolean("participators")){
						Iterator<Person> iter = incident.getParticipators().iterator();
						while(iter.hasNext()){
							personSet.add(iter.next());
						}
					}
					
					if(targetJsonObject.has("handler") && targetJsonObject.getBoolean("handler")){
						Iterator<Person> iter = incident.getHandlers().iterator();
						while(iter.hasNext()){
							personSet.add(iter.next());
						}
					}
					if(targetJsonObject.has("flowInitiator") && targetJsonObject.getBoolean("flowInitiator")) {//是否给流程发起人发送
						Integer initiatorId = processInstance.getInitiatorId();
						Person initiator = personDAO.findById(initiatorId);	
						if(initiator != null) {
							personSet.add(initiator);
						}
					}
					if(targetJsonObject.has("nodeOwner") && targetJsonObject.getBoolean("nodeOwner")) {//是否给步骤负责人发送
						
						Integer ownerId = nodeInstance.getOwnerId();
						if(ownerId != null){//此时步骤负责人为人员
							Person owner = personDAO.findById(ownerId);
							if(owner != null) {
								personSet.add(owner);
							}
						}else{//角色
							List persons = roleDAO.findPersons(nodeInstance.getNode().getOwnerId());
							for(int index = 0; index < persons.size(); index++) {
								personSet.add((Person) persons.get(index));
							}
						}
					}
					if(targetJsonObject.has("taskOwner") && targetJsonObject.getBoolean("taskOwner") && taskInstance != null) {//是否给任务负责人发送
						Short ownerType = taskInstance.getActorType();
						Integer ownerId = taskInstance.getActorId();
						if(ownerType == Node.OWNER_TYPE_ROLE) {// 任务负责人为角色
							List persons = roleDAO.findPersons(ownerId);
							for(int index = 0; index < persons.size(); index++) {
								personSet.add((Person) persons.get(index));
							}
						} else if(ownerType == Node.OWNER_TYPE_PERSON) {// 任务负责人为个人
							Person owner = personDAO.findById(ownerId);
							if(owner != null) {
								personSet.add(owner);
							}
						} else if(ownerType == Node.OWNER_TYPE_INITIATOR) {// 任务负责人为发起人
							Integer initiatorId = processInstance.getInitiatorId();
							Person initiator = personDAO.findById(initiatorId);	
							if(initiator != null) {
								personSet.add(initiator);
							}
						}
					}
					if(targetJsonObject.has("person") && targetJsonObject.getBoolean("person")) {//是否给人员发送
						String personList = targetJsonObject.getString("personList");
						String[] personArray = personList.split(",");
						for(int j = 0; j < personArray.length; j++) {
							personSet.add(personDAO.findById(new Integer(personArray[j])));
						}
						
					}
					if(targetJsonObject.has("workgroup") && targetJsonObject.getBoolean("workgroup")) {//是否给工作组发送
						String workgroupList = targetJsonObject.getString("workgroupList");
						String[] workgroupArray = workgroupList.split(",");
						for(int j = 0; j < workgroupArray.length; j++) {
							workgroupSet.add(workgroupDao.findById(new Integer(workgroupArray[j])));
						}
					}
					if(targetJsonObject.has("role") && targetJsonObject.getBoolean("role")) {//是否给角色发送
						String rolesList  = targetJsonObject.getString("rolesList");
						String[] roleArray = rolesList.split(",");
						for(int j = 0; j < roleArray.length; j++) {
							roleSet.add(roleDAO.findById(new Integer(roleArray[j])));
						}
					}
					
					
					MailServer mailServer = messageDAO.findMailServer();
					List<Map<String,String>> recipientList = null;
					if(mailServer != null) {
						// 获取邮件发送人集合
						recipientList = MessageUtils.makeMailRecipients(personSet, workgroupSet, roleSet);
					}
					
					SmsApi smsApi = smsApiDAO.findSMSApi();
					String mobilePhones = null;
					if(smsApi != null) {
						// 获取要发送短信的手机号集合
						mobilePhones = MessageUtils.makeMobilePhoneStr(personSet, workgroupSet, roleSet);
					}
					MessageThreadUtil messageThreadUtil = new MessageThreadUtil(mailServer, recipientList, null, emailTitle, emailContent, smsApi, mobilePhones, smsContent, logger);
					messageThreadUtil.start();
					
				}
			}
		}
		
		return success;
	}
	
	/**
	 * 将字符串中的项目属性标记替换为实际项目属性的内容
	 * @param str
	 * @param incident
	 * @param requestContext
	 * @return
	 */
	public String replaceIncidentInfoToStr(String str, Incident incident, RequestContext requestContext) {
		if(str == null) {
			return "";
		}
		str = str.replaceAll("<" + requestContext.getMessage("request_no", "request_no") + ">", incident.getRequestNo() == null ? "" : incident.getRequestNo());
		str = str.replaceAll("<" + requestContext.getMessage("subject", "subject") + ">", incident.getSubject() == null ? "" : incident.getSubject());
		str = str.replaceAll("<" + requestContext.getMessage("category", "category") + ">", incident.getCategory() == null ? "" : incident.getCategory().getName());
		str = str.replaceAll("<" + requestContext.getMessage("creator", "creator") + ">", incident.getCreator() == null ? "" : incident.getCreator().getName());
		str = str.replaceAll("<" + requestContext.getMessage("status", "status") + ">", incident.getStatus() == null ? "" : getStatusDesc(incident.getStatus(), requestContext));
		return str;
	}
	
	/**
	 * 将字符串中的步骤属性标记替换为实际步骤属性的内容
	 * @param str
	 * @param requestContext
	 * @return
	 */
	public String replaceNodeInfoToStr(String str, NodeInstance nodeInstance, RequestContext requestContext, Short activityType) {
		if(str == null) {
			return "";
		}
		if(nodeInstance == null) {
			str = str.replaceAll("<" + requestContext.getMessage("step", "step") + requestContext.getMessage("name", "name") + ">", "");
			str = str.replaceAll("<" + requestContext.getMessage("step", "step") + requestContext.getMessage("owner", "owner") + ">", "");
			return str;
		}
		String stempOwnerName = "";
		Short ownerType = nodeInstance.getNode().getOwnerType();
		if(activityType == IncidentActivity.TYPE_PROCESS_NODE_TAKEOVER) {//如果活动为接管，那么负责人类型为个人
			ownerType = Node.OWNER_TYPE_PERSON;
		}
		
		if(ownerType == Node.OWNER_TYPE_PERSON || ownerType == Node.OWNER_TYPE_INITIATOR) {//负责人类型为个人
			Person person = personDAO.findById(nodeInstance.getOwnerId());
			stempOwnerName = person.getName();
		} else if(ownerType == Node.OWNER_TYPE_ROLE) {// 负责人类型为角色
			Role role = roleDAO.findById(nodeInstance.getNode().getOwnerId());
			if(null != role) {
				stempOwnerName = role.getName();
			}
		}
		str = str.replaceAll("<" + requestContext.getMessage("step", "step") + requestContext.getMessage("name", "name") + ">", nodeInstance.getNode().getName());
		str = str.replaceAll("<" + requestContext.getMessage("step", "step") + requestContext.getMessage("owner", "owner") + ">", stempOwnerName);
		return str;
	}
	
	/**
	 * 将字符串中的任务属性标记替换为实际步骤属性的内容
	 * @param str
	 * @param taskInstance
	 * @param requestContext
	 * @return
	 */
	public String replaceTaskInfoToStr(String str, TaskInstance taskInstance, RequestContext requestContext) {
		if(str == null) {
			return "";
		}
		if(taskInstance != null) {
			String ownerName = "";
			// 根据负责人类型获取负责人名称
			if(taskInstance.getActorType() == Task.ACTOR_TYPE_ROLE) {
				Role role = roleDAO.findById(taskInstance.getActorId());
				if(null != role) {
					ownerName = role.getName();
				}
			} else if(taskInstance.getActorType() == Task.ACTOR_TYPE_PERSON || taskInstance.getActorType() == Task.ACTOR_TYPE_INITIATOR) {
				Person person = personDAO.findById(taskInstance.getActorId());
				if(null != person) {
					ownerName = person.getName();
				}
			}
			str = str.replaceAll("<" + requestContext.getMessage("task", "task") + requestContext.getMessage("name", "name") + ">", taskInstance.getName());
			str = str.replaceAll("<" + requestContext.getMessage("task", "task") + requestContext.getMessage("owner", "owner") + ">", ownerName);
		} else {
			str = str.replaceAll("<" + requestContext.getMessage("task", "task") + requestContext.getMessage("name", "name") + ">", "");
			str = str.replaceAll("<" + requestContext.getMessage("task", "task") + requestContext.getMessage("owner", "owner") + ">", "");
		}
		return str;
	}
	
	
	public List<Incident> getList(String afterNoticeCondition, JSONObject jsonObject, Short activityType, int i, Incident incident){
		
		HashMap<String,Object> paraMap = new HashMap<String,Object>();
		List<Incident> incidents;
		String activityName = "";
		String paraNames[];
		Object values[];
		
		String hql = "select incident from com.telinkus.itsm.data.incident.Incident incident where incident.id = " + incident.getId();
		
		if(afterNoticeCondition != null){
			hql += afterNoticeCondition;
		}
		
		switch(activityType){
		
			case IncidentActivity.TYPE_CREATE:
				activityName = "create";
				break;
				
			case IncidentActivity.TYPE_TAKEOVER:
				activityName = "takeover";
				break;
				
			case IncidentActivity.TYPE_FINISH:
				activityName = "complete";
				break;
				
			case IncidentActivity.TYPE_HANDOVER:
				activityName = "handover";
				break;
				
			case IncidentActivity.TYPE_ASSIGN:
				activityName = "assign";
				break;
				
			case IncidentActivity.TYPE_REJECT:
				activityName = "reject";
				break;
				
			case IncidentActivity.TYPE_HANDBACK:
				activityName = "handback";
				break;
				
			case IncidentActivity.TYPE_MERGE:
				activityName = "merge";
				break;
				
			case IncidentActivity.TYPE_REBOUND:
				activityName = "rebound";
				break;
			case IncidentActivity.TYPE_CREATE_TICKET:
				activityName = "createTicket";
				break;
				
			case IncidentActivity.TYPE_ASSIGN_TICKET:
				activityName = "assignTicket";
				break;
				
			case IncidentActivity.TYPE_FINISH_TICKET:
				activityName = "finishTicket";
				break;
				
			case IncidentActivity.TYPE_TAKEOVER_OVERTIME:
				activityName = "takeoverOvertime";
				break;
				
			case IncidentActivity.TYPE_COMPLETE_OVERTIME:
				activityName = "completeOvertime";
				break;
		}
		
		try {
			JSONObject oneRowData = jsonObject.getJSONArray(activityName).getJSONObject(i);
			
			if(oneRowData.has("expectFinishTime")){
				Boolean expectFinishTime = (Boolean) oneRowData.get("expectFinishTime");
				if(expectFinishTime){
					String expectFinishDay = oneRowData.getString("expectFinishDay");
					if(expectFinishDay != null && expectFinishDay.length() > 0){
						paraMap.put("expectFinishTime", java.sql.Timestamp.valueOf(getCurrentTime(expectFinishDay, "datetime")));
					}
				}
			}
			
			if(oneRowData.has("promiseBeginTime")){
				Boolean promiseBeginTime = (Boolean) oneRowData.get("promiseBeginTime");
				if(promiseBeginTime){
					String promiseBeginDay = oneRowData.getString("promiseBeginDay");
					if(promiseBeginDay != null && promiseBeginDay.length() > 0){
						paraMap.put("promiseStartTime", java.sql.Timestamp.valueOf(getCurrentTime(promiseBeginDay, "datetime")));
					}
				}
			}
			
			if(oneRowData.has("promiseFinishTime")){
				Boolean promiseFinishTime = (Boolean) oneRowData.get("promiseFinishTime");
				if(promiseFinishTime){
					String promiseFinishDay = oneRowData.getString("promiseFinishDay");
					if(promiseFinishDay != null && promiseFinishDay.length() > 0){
						paraMap.put("promiseFinishTime", java.sql.Timestamp.valueOf(getCurrentTime(promiseFinishDay, "datetime")));
					}
				}
			}
			
			if(oneRowData.has("customField")){
				Boolean customField = (Boolean) oneRowData.get("customField");
				if(customField) {
					String elementNamesStr = (String) oneRowData.get("elementNamesStr");
					if(!",".equals(elementNamesStr)) {
						//datetime
						for(int m = 1; m <= Utils.DATETIME_CUST_NUM; m++) {
							String datetimeCheck = "datetime" + m;
							String datetimeValue = "datetime" + m + "Value";
							if(elementNamesStr.indexOf("," + datetimeCheck + ",") != -1) {
								Boolean check = (Boolean) oneRowData.get(datetimeCheck);
								String value = oneRowData.get(datetimeValue).toString();
								if(check && value.length() > 0) {
									paraMap.put("datetimeValue" + m, java.sql.Timestamp.valueOf(getCurrentTime(value, "datetime")));
								}
							}
						}
						//date
						for(int n = 1; n <= Utils.DATE_CUST_NUM; n++) {
							String dateCheck = "date" + n;
							String dateValue = "date" + n + "Value";
							if(elementNamesStr.indexOf("," + dateCheck + ",") != -1) {
								Boolean check = (Boolean) oneRowData.get(dateCheck);
								String value = oneRowData.get(dateValue).toString();
								if(check && value.length() > 0) {
									paraMap.put("dateValue" + n, java.sql.Date.valueOf(getCurrentTime(value, "date")));
								}
							}
						}
					}
				}
			}	
			
			if(oneRowData.has("form_type_1")||oneRowData.has("form_type_2")){
				Boolean form_type_1 = (Boolean) oneRowData.get("form_type_1");
				hql += " and (" ;
				if(form_type_1){
					hql += " incident.reservedInteger6="+Incident.TYPE_INCIDENT_FORM;
				}
				Boolean form_type_2 = (Boolean) oneRowData.get("form_type_2");
				if(form_type_1&&form_type_2){
					hql += " or ";
				}
				if(form_type_2){
					hql += " incident.reservedInteger6="+Incident.TYPE_QUERY_FORM;
				}
				hql += " )";
			}
			
			
		} catch (JSONException e) {
			logger.error(Utils.getStackTrace(e));
		}
		
		//prepare parameters for query:
		Object flds[] = paraMap.keySet().toArray();
		paraNames = new String[flds.length];
		for (i = 0; i < flds.length; i++) {
			String fld = (String) flds[i];
			paraNames[i] = fld;
		}
		values = paraMap.values().toArray();
		
		incidents = incidentDAO.findByNamedParam(hql, 0, paraNames, values);
		
		return incidents;
	}
	
	public String getCurrentTime(String days, String type){
		
		java.util.Date now = new java.util.Date(); 
		java.util.Date newDate = Utils.dateOperate(now, null, 0, null, 0, "+", Integer.parseInt(days));
		
		String date;
		
		if(type.equals("datetime")){
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate);
		}else{
			date = new SimpleDateFormat("yyyy-MM-dd").format(newDate);
		}

        return date;
	}
	
	
	/*
	 * withdraw to draft
	 */
	public ModelAndView withdrawToDraft(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String, Object> paraMap = new HashMap<String, Object>(0);
		NodeInstance nodeInstance;
		ProcessInstance processInstance;
		
		//get parameters from http request:
		Long id = new Long(req.getParameter("id"));
		String comment = req.getParameter("comment");
		String taskInstanceIdStr = req.getParameter("taskInstanceId");
		//String nodeInstanceIdStr = req.getParameter("nodeInstanceId");
		
		//get person ID and role IDs from Session:
		HttpSession session = req.getSession();
		
		Person assessor = getCurrentPerson(session);
		
		Incident incident = incidentDAO.findById(id);
		
		incident.setProcessInstanceId(null);
		incident.setStatus(Incident.STATUS_DRAFT);
		
		//create activity:
		IncidentActivity activity = new IncidentActivity();
		
		activity.setType(new Short(IncidentActivity.TYPE_REJECT));
		activity.setPerson(assessor);
		activity.setTime(new Timestamp(System.currentTimeMillis()));
		activity.setComment(comment);
		
		incidentDAO.update(incident);
		logger.info("incident(id=" + id + ") was withdrawed to draft by" + assessor.getName());
		
		TaskInstance taskInstance = processDAO.findTaskInstanceById(new Long(taskInstanceIdStr));
		nodeInstance = taskInstance.getNodeInstance();
		processInstance = nodeInstance.getToken().getProcessInstance();
		
		//cancel all task instances of current node instance:
		List<TaskInstance> taskInstanceList = nodeInstance.getTaskInstances();
		for(int i=0;i<taskInstanceList.size();i++) {
			
			TaskInstance ti = taskInstanceList.get(i);
			
			if(ti.getStatus() != TaskInstance.STATUS_CLOSED) {
				
				ti.setStatus(new Short(TaskInstance.STATUS_CANCELLED));
				logger.info("Task instance(id=" + ti.getId() + ") was cancelled.");
		
			}
		}
		
		//cancel this node instance:
		nodeInstance.setStatus(new Short(NodeInstance.STATUS_CANCELLED));
				
		//abort the process instance:
		processInstance.abort();
		processInstance.setAbortComment(comment);
		
		//save to DB:
		processDAO.saveProcessInstance(processInstance);
		logger.info("Process insance(id=" + processInstance.getId() + ") was aborted by person");
				
		//set return map:
		paraMap.put("parentReload", true);
		
		return new ModelAndView(getSuccessView(), paraMap);
		
	}//withdrawToDraft()
	
	public ModelAndView sendEmailForm(HttpServletRequest req, HttpServletResponse res){
		
		Map<String, Object> paraMap = new HashMap<String, Object>(0);
		
		HttpSession session = req.getSession();
		Person addressor = getCurrentPerson(session);
		paraMap.put("addressorId", addressor.getId());
		paraMap.put("addressorName", addressor.getName());
		
		String incidentId = req.getParameter("incidentId");
		Incident incident = incidentDAO.findById(Long.parseLong(incidentId));
		paraMap = this.getMap(incident);
		
		return new ModelAndView(getSendEmailFormView(), paraMap);
	}
	
	public ModelAndView sendEmail(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String, Object> paraMap = new HashMap<String, Object>(0);
		String addresseeIds[], ccIds[];
		
		IncidentEmail incidentEmail = new IncidentEmail();
		
		HttpSession session = req.getSession();
		Person person = getCurrentPerson(session);
		incidentEmail.setAddressor(person);
		incidentEmail.setSendTime(new Timestamp(new java.util.Date().getTime()));
		
		String addresseeIdListStr = req.getParameter("addresseeIds");
		addresseeIds = addresseeIdListStr.split(",");
		
		if (addresseeIdListStr.length() > 0) {
		
			Set<Person> addressees = new HashSet<Person>();
			for (int i=0; i<addresseeIds.length; i++) {
			
				Person addressee = personDAO.findById(new Integer(addresseeIds[i]));
				addressees.add(addressee);
			}
			incidentEmail.getAddressee().addAll(addressees);
		}else{
			incidentEmail.getAddressee().clear();
		}
		incidentEmail.setAddresseeAdd(req.getParameter("addresseeAdd"));
		
		String ccIdListStr = req.getParameter("ccIds");
		ccIds = ccIdListStr.split(",");
		
		if (ccIdListStr.length() > 0) {
		
			Set<Person> ccs = new HashSet<Person>();
			for (int i=0; i<ccIds.length; i++) {
			
				Person cc = personDAO.findById(new Integer(ccIds[i]));
				ccs.add(cc);
			}
			incidentEmail.getCc().addAll(ccs);
		}else{
			incidentEmail.getCc().clear();
		}
		incidentEmail.setCcAdd(req.getParameter("ccAdd"));
		
		String subject = req.getParameter("subject");
		incidentEmail.setSubject(subject);
		
		String content = req.getParameter("content");
		String opinion = req.getParameter("opinion");
		StringBuffer sb = new StringBuffer("发件人：");
		sb.append(incidentEmail.getAddressor().getEmail());
		sb.append("<br/>内容：<br/>&nbsp;&nbsp;&nbsp;&nbsp;").append(content);
		sb.append("<br/>意见：<br/>&nbsp;&nbsp;&nbsp;&nbsp;").append(opinion);
		incidentEmail.setContent(sb.toString());
		
		Incident incident = incidentDAO.findById(Long.parseLong(req.getParameter("id")));

		incident.getEmails().add(incidentEmail);
		
		if (configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		String host = configProperties.getProperty("mail.host");
		String protocol = configProperties.getProperty("mail.transport.protocol");
		String auth = configProperties.getProperty("mail.smtp.auth");
		String mailUsername = configProperties.getProperty("mailUsername");
		String mailPassword = configProperties.getProperty("mailPassword");
		
		Properties prop = new Properties();
		prop.setProperty("mail.host", host);
		prop.setProperty("mail.transport.protocol", protocol);
		prop.setProperty("mail.smtp.auth", auth);
		
		javax.mail.Session mailSession = javax.mail.Session.getInstance(prop);
		Transport transport = mailSession.getTransport();
		transport.connect(host, mailUsername, mailPassword);
		
		Message message = createMail(mailSession, incident.getAttachments(), incidentEmail);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

		incidentDAO.update(incident);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
	}
	
	public ModelAndView callBackEmailForm(HttpServletRequest req, HttpServletResponse res){
		
		Map<String, Object> paraMap = new HashMap<String, Object>(0);
		
		HttpSession session = req.getSession();
		Person addressor = getCurrentPerson(session);
		paraMap.put("addressorId", addressor.getId());
		paraMap.put("addressorName", addressor.getName());
		
		String incidentId = req.getParameter("incidentId");
		Incident incident = incidentDAO.findById(Long.parseLong(incidentId));
		paraMap.put("id", incident.getId());
		paraMap.put("subject", "查询单回访（" + incident.getRequestNo() + "，" + incident.getSubject() + "）");
		StringBuffer sb = new StringBuffer("发件人：" + addressor.getEmail() + "\r\n\r\n");
		sb.append("尊敬的XXX支行XXX，你好！\r\n");
		sb.append("您的查询单（").append(incident.getRequestNo() + "，" + incident.getSubject() + "）")
		.append("已按需求处理完毕，处理结果已由处理人反馈您。\r\n");
		sb.append("现请您回复：处理结果是否满足需求？现是否可以结单？如有任何问题请及时回复反馈。");
		sb.append("\r\n\r\n");
		sb.append("请回复此邮件。\r\n");
		sb.append("谢谢！");
		sb.append("\r\n\r\n");
		sb.append("附查询单内容：").append(incident.getContent());
		paraMap.put("content", sb.toString());
		
		return new ModelAndView(getCallBackEmailFormView(), paraMap);
	}
	
	public ModelAndView callBackEmail(HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		Map<String, Object> paraMap = new HashMap<String, Object>(0);
		String addresseeIds[], ccIds[];
		
		IncidentEmail incidentEmail = new IncidentEmail();
		
		HttpSession session = req.getSession();
		Person person = getCurrentPerson(session);
		incidentEmail.setAddressor(person);
		incidentEmail.setSendTime(new Timestamp(new java.util.Date().getTime()));
		
		String addresseeIdListStr = req.getParameter("addresseeIds");
		addresseeIds = addresseeIdListStr.split(",");
		
		if (addresseeIdListStr.length() > 0) {
		
			Set<Person> addressees = new HashSet<Person>();
			for (int i=0; i<addresseeIds.length; i++) {
			
				Person addressee = personDAO.findById(new Integer(addresseeIds[i]));
				addressees.add(addressee);
			}
			incidentEmail.getAddressee().addAll(addressees);
		}else{
			incidentEmail.getAddressee().clear();
		}
		incidentEmail.setAddresseeAdd(req.getParameter("addresseeAdd"));
		
		String ccIdListStr = req.getParameter("ccIds");
		ccIds = ccIdListStr.split(",");
		
		if (ccIdListStr.length() > 0) {
		
			Set<Person> ccs = new HashSet<Person>();
			for (int i=0; i<ccIds.length; i++) {
			
				Person cc = personDAO.findById(new Integer(ccIds[i]));
				ccs.add(cc);
			}
			incidentEmail.getCc().addAll(ccs);
		}else{
			incidentEmail.getCc().clear();
		}
		incidentEmail.setCcAdd(req.getParameter("ccAdd"));
		
		String subject = req.getParameter("subject");
		incidentEmail.setSubject(subject);
		
		String content = req.getParameter("content");
		incidentEmail.setContent(content);
		
		Incident incident = incidentDAO.findById(Long.parseLong(req.getParameter("id")));

		incident.getEmails().add(incidentEmail);
		
		if (configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		
		String host = configProperties.getProperty("mail.host");
		String protocol = configProperties.getProperty("mail.transport.protocol");
		String auth = configProperties.getProperty("mail.smtp.auth");
		String mailUsername = configProperties.getProperty("mailUsername");
		String mailPassword = configProperties.getProperty("mailPassword");
		
		Properties prop = new Properties();
		prop.setProperty("mail.host", host);
		prop.setProperty("mail.transport.protocol", protocol);
		prop.setProperty("mail.smtp.auth", auth);
		
		javax.mail.Session mailSession = javax.mail.Session.getInstance(prop);
		Transport transport = mailSession.getTransport();
		transport.connect(host, mailUsername, mailPassword);
		
		Message message = createMail(mailSession, incident.getAttachments(), incidentEmail);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

		incidentDAO.update(incident);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
	}
	
	private Message createMail(javax.mail.Session mailSession, List<Document> attachments, IncidentEmail incidentEmail) throws Exception {
		
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(incidentEmail.getSubject());
		if (configProperties == null) {
			configProperties = new Properties();
			configProperties.load(new FileInputStream(resource.getFile()));
		}
		message.setFrom(configProperties.getProperty("mailUsername"));
		
		Person[] addresseeArr = new Person[incidentEmail.getAddressee().size()];
		incidentEmail.getAddressee().toArray(addresseeArr);
		String[] addresseeAddArr = new String[0];
		String addresseeAddStr = incidentEmail.getAddresseeAdd();
		if(addresseeAddStr != null && addresseeAddStr.length() > 0){
			
			addresseeAddArr = addresseeAddStr.split(";");
		}
		InternetAddress[] addressee = new InternetAddress[incidentEmail.getAddressee().size() + addresseeAddArr.length];
		for( int i = 0; i < incidentEmail.getAddressee().size(); i++){
			addressee[i] = new InternetAddress(addresseeArr[i].getEmail());
		}
		for(int i = 0; i < addresseeAddArr.length; i++){
			addressee[incidentEmail.getAddressee().size() + i] = new InternetAddress(addresseeAddArr[i]);
		}
		message.setRecipients(Message.RecipientType.TO, addressee);
		
		Person[] ccArr = new Person[incidentEmail.getCc().size()];
		incidentEmail.getCc().toArray(ccArr);
		String[] ccAddArr = new String[0];
		String ccAddStr = incidentEmail.getCcAdd();
		if(ccAddStr != null && ccAddStr.length() > 0){
			
			ccAddArr = ccAddStr.split(";");
		}
		InternetAddress[] cc = new InternetAddress[incidentEmail.getCc().size() + ccAddArr.length];
		for( int i = 0; i < incidentEmail.getCc().size(); i++){
			cc[i] = new InternetAddress(ccArr[i].getEmail());
		}
		for(int i = 0; i < ccAddArr.length; i++){
			cc[incidentEmail.getCc().size() + i] = new InternetAddress(ccAddArr[i]);
		}
		message.setRecipients(Message.RecipientType.CC, cc);
		
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(incidentEmail.getContent(), "text/html;charset=UTF-8");
		
		MimeMultipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		MimeBodyPart mimeBodyPart;
		if(attachments != null){
			for(int i = 0; i < attachments.size(); i++){
				Document doc = attachments.get(i);
				String fileName = doc.getFileName();
				if(!new File(doc.getPath1() + File.separator + fileName).exists()){
					fileName = doc.getOriginalFileName();
				}
				String fileSource = doc.getPath1() + File.separator + fileName;
				mimeBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(fileSource);
				mimeBodyPart.setDataHandler(new DataHandler(source));
				mimeBodyPart.setFileName(fileName);
				multipart.addBodyPart(mimeBodyPart);
			}
		}
		message.setContent(multipart);
		message.saveChanges();
		
		return message;
	}

	public void checkAddressee(HttpServletRequest req, HttpServletResponse res) throws IOException{

		String addresseeIdListStr = req.getParameter("addresseeIds");
		String ccIdListStr = req.getParameter("ccIds");
		String[] addresseeIds = addresseeIdListStr.split(",");
		String[] ccIds = ccIdListStr.split(",");
		StringBuffer addresseeList = new StringBuffer();
		String responseText = "";
		if (addresseeIdListStr != null && addresseeIdListStr.length() > 0) {
		
			for (int i=0; i<addresseeIds.length; i++) {
			
				Person addressee = personDAO.findById(new Integer(addresseeIds[i]));
				if(addressee.getEmail() == null){
					addresseeList.append("、" + addressee.getName());
				}
			}
		}
		if (ccIdListStr != null && ccIdListStr.length() > 0) {
			
			for (int i=0; i<ccIds.length; i++) {
				
				Person cc = personDAO.findById(new Integer(ccIds[i]));
				if(cc.getEmail() == null){
					addresseeList.append("、" + cc.getName());
				}
			}
		}
		
		if(addresseeList.length() > 0){
			responseText = "收件人" + addresseeList.toString().replaceFirst("、", "") + "邮箱为空。";
		}
		res.setContentType("text/html;charset=utf-8");
		PrintWriter writer = res.getWriter();
		writer.print(responseText);
		writer.flush();
		writer.close();
	}
	
	/**
	 * 
	 */
	public ModelAndView concern(HttpServletRequest req, HttpServletResponse res){
		
		Map<String, Object> paraMap = new HashMap<String, Object>(0);
		
		HttpSession session = req.getSession();
		Person person = getCurrentPerson(session);
		String incidentId = req.getParameter("incidentId");
		Incident incident = incidentDAO.findById(Long.parseLong(incidentId));
	
		person.getIncidents().add(incident);
		personDAO.update(person);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
	}
	
	
	/*
	 * method to cancle Concern
	 */
	public ModelAndView cancleConcern(HttpServletRequest req, HttpServletResponse res) {
		
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		//get person ID from Session:
		HttpSession session = req.getSession();
		
		Person person = getCurrentPerson(session);
		
		//get parameters from http request:
		String idListStr = req.getParameter("idList");
		
		String[] idStr = idListStr.split(",");
		for (int i=0;i<idStr.length;i++) {
			
			Incident incident = this.getIncidentDAO().findById(new Long(idStr[i]));
			person.getIncidents().remove(incident);
		}
		
		personDAO.update(person);
		
		paraMap.put("parentReload", true);
		
		return new ModelAndView(this.getSuccessView(), paraMap);
		
	}//cancleConcern()
	
	/**
	 * 获取类别等级
	 * 
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	public void getCategoryLevel(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setCharacterEncoding("UTF-8");
		String categoryId = req.getParameter("categoryId");// 类别id
		String result = null;
		CategoryWithLevel catWithLevel = this.catWithLevelDAO.findByCategory(new Short(categoryId));
		if (catWithLevel != null && catWithLevel.getSeverity()!=null) {
			result = catWithLevel.getSeverity().getId().toString() + ","
					+ catWithLevel.getSeverity().getName();
		} else {
			result = "";
		}

		res.getWriter().write(result);
	}
	
	/**
	 * 计算事件优先级 和 期望完成时间
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	public void getPriorityValue(HttpServletRequest req, HttpServletResponse res) throws IOException{
		
		res.setCharacterEncoding("UTF-8");
		
		try {
			String effectId = req.getParameter("effectId");//影响程度
			String urgencyId = req.getParameter("urgencyId");//紧急程度
			String severityId = req.getParameter("severityId");//影响范围
			String type = req.getParameter("type");//事件类型
			
			Short effectValue = null;
			Short urgencyValue = null;
			Short severityValue = null;
			
			if(StringUtils.isNotBlank(effectId)) {
				Effect effect = effectDAO.findById(new Short(effectId));
				if(effect != null) {
					effectValue = effect.getValue();
				}
			}
			if(StringUtils.isNotBlank(urgencyId)) {
				Urgency urgency = urgencyDAO.findById(new Short(urgencyId));
				if(urgency != null) {
					urgencyValue = urgency.getValue();
				}
			}
			if(StringUtils.isNotBlank(severityId)) {
				Severity severity = severityDAO.findById(new Short(severityId));
				if(severity != null) {
					severityValue = severity.getValue();
				}
			}
			Short priority = null;
			if (effectValue!=null && urgencyValue!=null && severityValue!=null) {
				
				priority = CountIncidentLevel.getPriority(severityValue, effectValue, urgencyValue);
			}else if(severityValue != null){
				priority = CountIncidentLevel.getPriority(severityValue, null, null);
			}
			String applicationTimeStr = req.getParameter("applicationTime");// 申请时间
			SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			Timestamp aTime = Timestamp.valueOf(applicationTimeStr);
			
			//触发开始时间,若申请时间为工作时间直接进行计时，若申请时间为非工作时间，则等到工作时间开始计时
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(aTime.getTime());
			String calStr = HolidayCalc.calendarToStr(cal);
			
			if(!HolidayCalc.isWorking(calStr)){
				calStr = HolidayCalc.getNextWorkday(calStr);
			}
			
			aTime = Timestamp.valueOf(calStr);
			String expectFinishTimeStr = null;
			if(type!=null&&!type.equals("undefined")&&priority!=null){
				expectFinishTimeStr = this.getExpectFinishTime(aTime,priority,Integer.valueOf(type)).toString();
				expectFinishTimeStr = expectFinishTimeStr.substring(0, expectFinishTimeStr.length() - 2);
			}else{
				priority=null;
			}

			res.getWriter().write(priority == null ? "" : priority.toString() + "#" + expectFinishTimeStr );
		} catch (Exception e) {
			logger.error(Utils.getStackTrace(e));
			res.getWriter().write("");
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * 期望完成时间计算：
	 * 
	 * 一级：申请时间  + 2880分钟 （工作时间范围内）
	 * 二级：申请时间  + 720分钟 （工作时间范围内）
	 * 三级：申请时间  + 240分钟 （工作时间范围内）
	 * 四级：申请时间  + 30分钟 （工作时间范围内）
	 * 五级：申请时间  + 30分钟 （工作时间范围内）
	 * 
	 * @param applicationTime
	 */
	public Timestamp getExpectFinishTime(Timestamp appTime,Short priority,Integer type) {
		
		try {
			
			if(configProperties == null) {
				configProperties = new Properties();
				configProperties.load(new FileInputStream(resource.getFile()));
			}
			
			Integer slaId = null; 
			String slaIdStr = null;
			
			if(type.equals(Incident.TYPE_INCIDENT_FORM)){
				slaIdStr = configProperties.getProperty("incident_sla_id");
			}else{
				slaIdStr = configProperties.getProperty("request_sla_id");
			}
			
			Agreement agreement = null;
			if (StringUtils.isNotBlank(slaIdStr)) {
				slaId = Integer.parseInt(slaIdStr);
				agreement = this.agreementDAO.findById(slaId);
			}
			
			if (agreement == null) {
				return null;
			}

			// Timestamp appTime = incident.getApplicationTime();
			List<Target> targetList = agreement.getTargets();
			int targetSize = targetList.size();

			int handoverLimit;
			short handoverLimitUnit;

			Target target = null;
			for (int j = 0; j < targetSize; j++) {
				Target targets = targetList.get(j);
				// check priority
				if ((targets.getFloor() <= priority)
						&& (priority < targets.getCeiling())) {
					target = targets;
					break;
				}
			}

			if (null == target) {
				return null;
			}

			Map busHourMap = agreement.getBusinessHour();
			// check in during business hour 是否在营业时间范围
			boolean isDuringBusHour = targetUtils.checkDuringBusHour(appTime,
					busHourMap);

			if (isDuringBusHour) {
				// during business hour

				handoverLimit = target.getNormalAutoHandoverLimit();
				handoverLimitUnit = target.getNormalAutoHandoverLimitUnit();

			} else {
				// during non business hour 顺延至下一个营业时间
				if (target.getPostpone()) {
					// check postpone next during biz hour
					appTime = targetUtils.postponeNextDuringBizHour2(appTime,
							busHourMap);

					handoverLimit = target.getNormalAutoHandoverLimit();
					handoverLimitUnit = target.getNormalAutoHandoverLimitUnit();

				} else {
					handoverLimit = target.getOtherAutoHandoverLimit();
					handoverLimitUnit = target.getOtherAutoHandoverLimitUnit();
				}
			}// end if(isDuringBusHour)
			//不过滤休息时间，均为自然日
			return targetUtils.checkAlarmTime2(handoverLimitUnit,handoverLimit, appTime);
			
		} catch (Exception e) {
			logger.error("getExpectFinishTime error :" + Utils.getStackTrace(e));
		}
		return null;

	}

	/////////////////////////////////////////////////////////////////
	
	/**
	 * 获取事件流程定义信息
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	public void getIncidentProcessDefineInfo(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setCharacterEncoding("UTF-8");
		String categoryId = req.getParameter("categoryId");//类别id
		String priority = req.getParameter("priority");//优先级
		String prePDId = "";
		String prePDName = "";
		String postPDId = "";
		String postPDName = "";
		if(categoryId == null || priority == null) {
			res.getWriter().write("");
		}
		//查询前置流程
		IncidentProcessDefine preProcessDefine = incidentDAO.findIncidentProcessDefine(new Short(categoryId), new Short(priority), IncidentProcessDefine.DEFINE_TYPE_PRE);
		if(preProcessDefine != null) {
			prePDId = preProcessDefine.getProcessDefine().getId().toString();
			prePDName = preProcessDefine.getProcessDefine().getName();
		}
		//查询后置流程
		IncidentProcessDefine postProcessDefine = incidentDAO.findIncidentProcessDefine(new Short(categoryId), new Short(priority), IncidentProcessDefine.DEFINE_TYPE_POST);
		if(postProcessDefine != null) {
			postPDId = postProcessDefine.getProcessDefine().getId().toString();
			postPDName = postProcessDefine.getProcessDefine().getName();
		}
		String result = "";
		if(preProcessDefine != null || postProcessDefine != null) {
			result = "success" + "\1" + prePDId + "\1" + prePDName + "\1" + postPDId + "\1" + postPDName;
		} 
		
		res.getWriter().write(result);
	}
	
	
	/////////////////////////////////////////// private //////////////////////////////////////////////
	
	/**
	 * Incident Map for list
	 * 
	 * @param incident
	 * @return
	 */
	private Map<String, Object> getIncidentMap(Incident incident) {
		Map<String,Object> incidentMap = new HashMap<String,Object>();
		
		incidentMap.put("id", incident.getId());
		
		Location location = incident.getLocation();
		if(location != null){
			incidentMap.put("location", location.getName());
		}
		
		incidentMap.put("requestNo", incident.getRequestNo());
		//incidentMap.put("category", incident.getCategory().getName());
		incidentMap.put("category", incident.getCategory().getPath());
		incidentMap.put("subject", incident.getSubject());
		
		incidentMap.put("sourceType", incident.getSourceType());
		
		Person applicant = incident.getApplicant();
		Customer customer = incident.getCustomer();
		if(null != applicant){
			incidentMap.put("applicant", applicant.getName());
		} else if(null != customer) {
			incidentMap.put("applicant", customer.getName());
		}
		
		incidentMap.put("applicationTime", incident.getApplicationTime());

		incidentMap.put("status", incident.getStatus());

		Timestamp promiseFinishTime = incident.getPromiseFinishTime();
		Timestamp expectFinishTime = incident.getExpectFinishTime();
		incidentMap.put("promiseFinishTime", promiseFinishTime);
		incidentMap.put("expectFinishTime", expectFinishTime);
		incidentMap.put("finishTime", incident.getFinishTime());
		
		Set<Person> handlers = incident.getHandlers();
		List<Map<String,Object>> handlerList = new ArrayList<Map<String,Object>>();
		
		Iterator<Person> it = handlers.iterator();
		while(it.hasNext()) {
			Person handler = it.next();
			Map<String,Object> handlerMap = new HashMap<String,Object>();
			handlerMap.put("name", handler.getName());
			handlerList.add(handlerMap);
		}
		incidentMap.put("handlerList", handlerList);
		
		Set<Person> participators = incident.getParticipators();
		List<Map<String,Object>> participatorsList = new ArrayList<Map<String,Object>>();
		Iterator<Person> itp = participators.iterator();
		while(itp.hasNext()) {
			Person participator = itp.next();
			Map<String,Object> participatorMap = new HashMap<String,Object>();
			participatorMap.put("name", participator.getName());
			participatorsList.add(participatorMap);
		}
		
		incidentMap.put("participatorsList", participatorsList);
		
		//backgroundColor
		List<BackgroundColor> bgColorList = backgroundColorDao.findAllObject();
		
		if(bgColorList != null && bgColorList.size() > 0){
			
			int type = bgColorList.get(0).getFieldType();
			
			if(type == BackgroundColor.BG_COLOR_STATUS_TYPE){
				
				if(incident.getStatus() != null){
					short incidentStatus = incident.getStatus();
					
					for(BackgroundColor bgColor : bgColorList){
						if(incidentStatus == bgColor.getIncidentStatus()){
							incidentMap.put("color", bgColor.getColor());
							break;
						}
					}
				}	
				
			}else if(type == BackgroundColor.BG_COLOR_SEVERITY_TYPE){
				
				if(incident.getSeverity() != null){
					short severityId = incident.getSeverity().getId();
					
					for(BackgroundColor bgColor : bgColorList){
						if(severityId == bgColor.getFieldId()){
							incidentMap.put("color", bgColor.getColor());
							break;
						}
					}
				}	
				
			}else if(type == BackgroundColor.BG_COLOR_URGENCY_TYPE){
				
				short urgencyId = incident.getUrgency().getId();
				
				for(BackgroundColor bgColor : bgColorList){
					if(urgencyId == bgColor.getFieldId()){
						incidentMap.put("color", bgColor.getColor());
						break;
					}
				}
				
			}else if(type == BackgroundColor.BG_COLOR_EFFECT_TYPE){
				
				short effectId = incident.getEffect().getId();
				
				for(BackgroundColor bgColor : bgColorList){
					if(effectId == bgColor.getFieldId()){
						incidentMap.put("color", bgColor.getColor());
						break;
					}
				}
				
			}else if(type == BackgroundColor.BG_COLOR_PRIORITY_TYPE){
				
				short priorityValue = incident.getPriority();
				
				for(BackgroundColor bgColor : bgColorList){
					if(priorityValue >= bgColor.getPriorityFloor() && priorityValue <= bgColor.getPriorityCeiling()){
						incidentMap.put("color", bgColor.getColor());
						break;
					}
				}
				
			}
			
		}
		
		incidentMap.put("reservedString1", incident.getReservedString1());
		incidentMap.put("reservedString2", incident.getReservedString2());
		incidentMap.put("reservedString3", incident.getReservedString3());
		incidentMap.put("reservedString4", incident.getReservedString4());
		incidentMap.put("reservedString5", incident.getReservedString5());
		incidentMap.put("reservedString6", incident.getReservedString6());
		incidentMap.put("reservedString7", incident.getReservedString7());
		incidentMap.put("reservedString8", incident.getReservedString8());
		incidentMap.put("reservedString9", incident.getReservedString9());
		incidentMap.put("reservedString10", incident.getReservedString10());
		
		return incidentMap;
	}
	
	private Person getCurrentPerson(HttpSession session) {
		Integer personId = (Integer)session.getAttribute("personId");
		Person handler = personDAO.findById(personId);
		return handler;
	}
	
	/**
	 * 自动补全提示
	 * @param req
	 * @param res
	 * @throws Exception
	 */
	public void acSearch(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String setName = req.getParameter("setName");
		String searchStr =new String(req.getParameter("q").getBytes("ISO8859-1"),"UTF-8"); 
		String result  = AutocompleteUtils.searchData(setName, searchStr);
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(result);
		
	}
	
	/////////////////////////////////////////// getters and setters: /////////////////////////////////////////////////
	public String getAbandonView() {
		return abandonView;
	}

	public void setAbandonView(String abandonView) {
		this.abandonView = abandonView;
	}

	public ConfigurationItemDAO getCiDAO() {
		return ciDAO;
	}

	public void setCiDAO(ConfigurationItemDAO ciDAO) {
		this.ciDAO = ciDAO;
	}

	public String getDetailTicketView() {
		return detailTicketView;
	}

	public void setDetailTicketView(String detailTicketView) {
		this.detailTicketView = detailTicketView;
	}
	
	public String getDetailView() {
		return detailView;
	}

	public void setDetailView(String detailView) {
		this.detailView = detailView;
	}

	public String getEditStepView() {
		return editStepView;
	}

	public void setEditStepView(String editStepView) {
		this.editStepView = editStepView;
	}

	public String getEditTaskView() {
		return editTaskView;
	}

	public void setEditTaskView(String editTaskView) {
		this.editTaskView = editTaskView;
	}

	public String getEditView() {
		return editView;
	}

	public void setEditView(String editView) {
		this.editView = editView;
	}

	public String getEditAgainView() {
		return editAgainView;
	}

	public void setEditAgainView(String editAgainView) {
		this.editAgainView = editAgainView;
	}

	public String getErrorView() {
		return errorView;
	}

	public void setErrorView(String errorView) {
		this.errorView = errorView;
	}

	public String getListView() {
		return listView;
	}

	public void setListView(String listView) {
		this.listView = listView;
	}

	public String getListConcernView() {
		return listConcernView;
	}

	public void setListConcernView(String listConcernView) {
		this.listConcernView = listConcernView;
	}

	public NoticeDAO getNoticeDAO() {
		return noticeDAO;
	}

	public void setNoticeDAO(NoticeDAO noticeDAO) {
		this.noticeDAO = noticeDAO;
	}

	public PersonDAO getPersonDAO() {
		return personDAO;
	}

	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}

	public ProcessDAO getProcessDAO() {
		return processDAO;
	}

	public void setProcessDAO(ProcessDAO processDAO) {
		this.processDAO = processDAO;
	}

	public RoleDAO getRoleDAO() {
		return roleDAO;
	}

	public void setRoleDAO(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}

	public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public IncidentDAO getIncidentDAO() {
		return incidentDAO;
	}

	public void setIncidentDAO(IncidentDAO incidentDAO) {
		this.incidentDAO = incidentDAO;
	}

	public CategoryDAO getCatDAO() {
		return catDAO;
	}

	public void setCatDAO(CategoryDAO catDAO) {
		this.catDAO = catDAO;
	}

	public EffectDAO getEffectDAO() {
		return effectDAO;
	}

	public void setEffectDAO(EffectDAO effectDAO) {
		this.effectDAO = effectDAO;
	}

	public SeverityDAO getSeverityDAO() {
		return severityDAO;
	}

	public void setSeverityDAO(SeverityDAO severityDAO) {
		this.severityDAO = severityDAO;
	}

	public UrgencyDAO getUrgencyDAO() {
		return urgencyDAO;
	}

	public void setUrgencyDAO(UrgencyDAO urgencyDAO) {
		this.urgencyDAO = urgencyDAO;
	}

	public void setReasonDAO(ReasonDAO reasonDAO) {
		this.reasonDAO = reasonDAO;
	}

	public String getHandleView() {
		return handleView;
	}

	public void setHandleView(String handleView) {
		this.handleView = handleView;
	}

	public String getHandleTicketView() {
		return handleTicketView;
	}

	public void setHandleTicketView(String handleTicketView) {
		this.handleTicketView = handleTicketView;
	}
	
	public String getNewProblemView() {
		return newProblemView;
	}

	public void setNewProblemView(String newProblemView) {
		this.newProblemView = newProblemView;
	}

	public String getInterveneView() {
		return interveneView;
	}

	public void setInterveneView(String interveneView) {
		this.interveneView = interveneView;
	}

	public String getActivityView() {
		return activityView;
	}

	public void setActivityView(String activityView) {
		this.activityView = activityView;
	}

	public LibraryDAO getLibraryDAO() {
		return libraryDAO;
	}

	public void setLibraryDAO(LibraryDAO libraryDAO) {
		this.libraryDAO = libraryDAO;
	}

	public ReasonDAO getReasonDAO() {
		return reasonDAO;
	}

	public void setNewFormView(String newFormView) {
		this.newFormView = newFormView;
	}

	public void setNewTicketFormView(String newTicketFormView) {
		this.newTicketFormView = newTicketFormView;
	}
	
	public String getSearchFormView() {
		return searchFormView;
	}

	public void setSearchFormView(String searchFormView) {
		this.searchFormView = searchFormView;
	}

	public KnowledgeBaseDAO getKbDAO() {
		return kbDAO;
	}

	public void setKbDAO(KnowledgeBaseDAO kbDAO) {
		this.kbDAO = kbDAO;
	}

	public AgreementDAO getAgreementDAO() {
		return agreementDAO;
	}

	public void setAgreementDAO(AgreementDAO agreementDAO) {
		this.agreementDAO = agreementDAO;
	}

	public String getNewFormView() {
		return newFormView;
	}

	public String getNewTicketFormView() {
		return newTicketFormView;
	}
	
	public LocationDAO getLocationDAO() {
		return locationDAO;
	}

	public void setLocationDAO(LocationDAO locationDAO) {
		this.locationDAO = locationDAO;
	}

	public OrganizationDAO getOrganizationDAO() {
		return organizationDAO;
	}

	public void setOrganizationDAO(OrganizationDAO organizationDAO) {
		this.organizationDAO = organizationDAO;
	}

	public String getChooseFormView() {
		return chooseFormView;
	}

	public void setChooseFormView(String chooseFormView) {
		this.chooseFormView = chooseFormView;
	}

	public String getMultiChooseFormView() {
		return multiChooseFormView;
	}

	public void setMultiChooseFormView(String multiChooseFormView) {
		this.multiChooseFormView = multiChooseFormView;
	}

	public String getChooseView() {
		return chooseView;
	}

	public void setChooseView(String chooseView) {
		this.chooseView = chooseView;
	}

	public String getMultiChooseView() {
		return multiChooseView;
	}

	public void setMultiChooseView(String multiChooseView) {
		this.multiChooseView = multiChooseView;
	}

	public CodeTypeDAO getCodeTypeDAO() {
		return codeTypeDAO;
	}

	public void setCodeTypeDAO(CodeTypeDAO codeTypeDAO) {
		this.codeTypeDAO = codeTypeDAO;
	}

	public TreeTypeDAO getTreeTypeDAO() {
		return treeTypeDAO;
	}

	public void setTreeTypeDAO(TreeTypeDAO treeTypeDAO) {
		this.treeTypeDAO = treeTypeDAO;
	}

	public String getChooseReferFormView() {
		return chooseReferFormView;
	}

	public void setChooseReferFormView(String chooseReferFormView) {
		this.chooseReferFormView = chooseReferFormView;
	}

	public String getChooseReferView() {
		return chooseReferView;
	}

	public void setChooseReferView(String chooseReferView) {
		this.chooseReferView = chooseReferView;
	}

	public ViewInfoDAO getInfoDAO() {
		return infoDAO;
	}

	public void setInfoDAO(ViewInfoDAO infoDAO) {
		this.infoDAO = infoDAO;
	}

	public String getChooseViewInfoView() {
		return chooseViewInfoView;
	}

	public void setChooseViewInfoView(String chooseViewInfoView) {
		this.chooseViewInfoView = chooseViewInfoView;
	}

	public String getShowDataView() {
		return showDataView;
	}

	public void setShowDataView(String showDataView) {
		this.showDataView = showDataView;
	}

	public ProcessingStatusDAO getProcessingStatusDAO() {
		return processingStatusDAO;
	}

	public void setProcessingStatusDAO(ProcessingStatusDAO processingStatusDAO) {
		this.processingStatusDAO = processingStatusDAO;
	}

	public String getBatchMergeView() {
		return batchMergeView;
	}

	public void setBatchMergeView(String batchMergeView) {
		this.batchMergeView = batchMergeView;
	}

	public String getBatchRejectView() {
		return batchRejectView;
	}

	public void setBatchRejectView(String batchRejectView) {
		this.batchRejectView = batchRejectView;
	}

	public String getCountFormView() {
		return countFormView;
	}

	public void setCountFormView(String countFormView) {
		this.countFormView = countFormView;
	}

	public String getCountView() {
		return countView;
	}

	public void setCountView(String countView) {
		this.countView = countView;
	}
	
	public String getManageIncidentView() {
		return manageIncidentView;
	}

	public void setManageIncidentView(String manageIncidentView) {
		this.manageIncidentView = manageIncidentView;
	}

	public String getApplicationHistoryView() {
		return applicationHistoryView;
	}

	public void setApplicationHistoryView(String applicationHistoryView) {
		this.applicationHistoryView = applicationHistoryView;
	}

	public String getListViewPage() {
		return listViewPage;
	}

	public void setListViewPage(String listViewPage) {
		this.listViewPage = listViewPage;
	}

	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public CallRecordDao getCallRecordDao() {
		return callRecordDao;
	}

	public void setCallRecordDao(CallRecordDao callRecordDao) {
		this.callRecordDao = callRecordDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public MessageDAO getMessageDAO() {
		return messageDAO;
	}

	public void setMessageDAO(MessageDAO messageDAO) {
		this.messageDAO = messageDAO;
	}

	public CustomExportDao getCustomExportDao() {
		return customExportDao;
	}

	public void setCustomExportDao(CustomExportDao customExportDao) {
		this.customExportDao = customExportDao;
	}

	public static Properties getConfigProperties() {
		return configProperties;
	}

	public static void setConfigProperties(Properties configProperties) {
		IncidentActionController.configProperties = configProperties;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		IncidentActionController.logger = logger;
	}

	public CustomSourceTypeDao getCustomSourceTypeDao() {
		return customSourceTypeDao;
	}

	public void setCustomSourceTypeDao(CustomSourceTypeDao customSourceTypeDao) {
		this.customSourceTypeDao = customSourceTypeDao;
	}

	public ChangeDAO getChangeDAO() {
		return changeDAO;
	}

	public void setChangeDAO(ChangeDAO changeDAO) {
		this.changeDAO = changeDAO;
	}

	//add by czk begin
	public RequirementDAO getRequirementDAO() {
		return requirementDAO;
	}

	public void setRequirementDAO(RequirementDAO requirementDAO) {
		this.requirementDAO = requirementDAO;
	}

	public RequestNoGenerationDao getRequestNoGenerationDao() {
		return requestNoGenerationDao;
	}

	public void setRequestNoGenerationDao(
			RequestNoGenerationDao requestNoGenerationDao) {
		this.requestNoGenerationDao = requestNoGenerationDao;
	}

	public BackgroundColorDAO getBackgroundColorDao() {
		return backgroundColorDao;
	}

	public void setBackgroundColorDao(BackgroundColorDAO backgroundColorDao) {
		this.backgroundColorDao = backgroundColorDao;
	}

	public AfterNotificationDAO getAfterNotificationDAO() {
		return afterNotificationDAO;
	}

	public void setAfterNotificationDAO(AfterNotificationDAO afterNotificationDAO) {
		this.afterNotificationDAO = afterNotificationDAO;
	}

	public WorkgroupDAO getWorkgroupDao() {
		return workgroupDao;
	}

	public void setWorkgroupDao(WorkgroupDAO workgroupDao) {
		this.workgroupDao = workgroupDao;
	}

	public SmsApiDAO getSmsApiDAO() {
		return smsApiDAO;
	}

	public void setSmsApiDAO(SmsApiDAO smsApiDAO) {
		this.smsApiDAO = smsApiDAO;
	}

	public NotificationJsonDAO getNotificationJsonDAO() {
		return notificationJsonDAO;
	}

	public void setNotificationJsonDAO(NotificationJsonDAO notificationJsonDAO) {
		this.notificationJsonDAO = notificationJsonDAO;
	}
	//add by czk end
	public RequestByJobDAO getRequestByJobDAO() {
		return requestByJobDAO;
	}

	public void setRequestByJobDAO(RequestByJobDAO requestByJobDAO) {
		this.requestByJobDAO = requestByJobDAO;
	}

	public JobInstanceDAO getJobInstanceDAO() {
		return jobInstanceDAO;
	}

	public void setJobInstanceDAO(JobInstanceDAO jobInstanceDAO) {
		this.jobInstanceDAO = jobInstanceDAO;
	}

	public String getToCheckPageView() {
		return toCheckPageView;
	}

	public void setToCheckPageView(String toCheckPageView) {
		this.toCheckPageView = toCheckPageView;
	}

	public String getToUpdateIncidentInfoPageView() {
		return toUpdateIncidentInfoPageView;
	}

	public void setToUpdateIncidentInfoPageView(String toUpdateIncidentInfoPageView) {
		this.toUpdateIncidentInfoPageView = toUpdateIncidentInfoPageView;
	}

	public AssignmentDAO getAssignmentDAO() {
		return assignmentDAO;
	}

	public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	public HolidayDao getHolidayDao() {
		return holidayDao;
	}

	public void setHolidayDao(HolidayDao holidayDao) {
		this.holidayDao = holidayDao;
	}

	public AdjustDayDao getAdjustDayDao() {
		return adjustDayDao;
	}

	public void setAdjustDayDao(AdjustDayDao adjustDayDao) {
		this.adjustDayDao = adjustDayDao;
	}

	public IncidentResolveTimeLimitDAO getIncidentResolveTimeLimitDAO() {
		return incidentResolveTimeLimitDAO;
	}

	public void setIncidentResolveTimeLimitDAO(
			IncidentResolveTimeLimitDAO incidentResolveTimeLimitDAO) {
		this.incidentResolveTimeLimitDAO = incidentResolveTimeLimitDAO;
	}

	public IncidentCauseDAO getCauseDAO() {
		return causeDAO;
	}

	public void setCauseDAO(IncidentCauseDAO causeDAO) {
		this.causeDAO = causeDAO;
	}

	public CategoryWithLevelDAO getCatWithLevelDAO() {
		return catWithLevelDAO;
	}

	public void setCatWithLevelDAO(CategoryWithLevelDAO catWithLevelDAO) {
		this.catWithLevelDAO = catWithLevelDAO;
	}

	public String getListCatLevelView() {
		return listCatLevelView;
	}

	public void setListCatLevelView(String listCatLevelView) {
		this.listCatLevelView = listCatLevelView;
	}

	public String getListMyDraftView() {
		return listMyDraftView;
	}

	public void setListMyDraftView(String listMyDraftView) {
		this.listMyDraftView = listMyDraftView;
	}

	public String getSendEmailFormView() {
		return sendEmailFormView;
	}

	public void setSendEmailFormView(String sendEmailFormView) {
		this.sendEmailFormView = sendEmailFormView;
	}

	public String getCallBackEmailFormView() {
		return callBackEmailFormView;
	}

	public void setCallBackEmailFormView(String callBackEmailFormView) {
		this.callBackEmailFormView = callBackEmailFormView;
	}

	public IncidentSecondaryCategoryDAO getSecondaryCatDAO() {
		return secondaryCatDAO;
	}

	public void setSecondaryCatDAO(IncidentSecondaryCategoryDAO secondaryCatDAO) {
		this.secondaryCatDAO = secondaryCatDAO;
	}

	public ThreadNoDAO getThreadNoDAO() {
		return threadNoDAO;
	}

	public void setThreadNoDAO(ThreadNoDAO threadNoDAO) {
		this.threadNoDAO = threadNoDAO;
	}

	public ProblemDAO getProblemDAO() {
		return problemDAO;
	}

	public void setProblemDAO(ProblemDAO problemDAO) {
		this.problemDAO = problemDAO;
	}

	public NoticeWorkingDAO getNoticeWorkingDAO() {
		return noticeWorkingDAO;
	}

	public void setNoticeWorkingDAO(NoticeWorkingDAO noticeWorkingDAO) {
		this.noticeWorkingDAO = noticeWorkingDAO;
	}

	public String getListOverTimeNotClose() {
		return listOverTimeNotClose;
	}

	public void setListOverTimeNotClose(String listOverTimeNotClose) {
		this.listOverTimeNotClose = listOverTimeNotClose;
	}

	public DutyTaskDao getDutyTaskDao() {
		return dutyTaskDao;
	}

	public void setDutyTaskDao(DutyTaskDao dutyTaskDao) {
		this.dutyTaskDao = dutyTaskDao;
	}

}
