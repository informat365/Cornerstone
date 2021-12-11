package cornerstone.biz.srv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.aviator.AviatorEvaluator;

import cornerstone.biz.domain.Account;
import cornerstone.biz.domain.Permission;
import cornerstone.biz.domain.WorkflowChartDefine;
import cornerstone.biz.domain.WorkflowChartDefine.Graph;
import cornerstone.biz.domain.WorkflowChartDefine.GraphAccounts;
import cornerstone.biz.domain.WorkflowChartDefine.GraphCompanyRole;
import cornerstone.biz.domain.WorkflowChartDefine.GraphDepartment;
import cornerstone.biz.domain.WorkflowChartDefine.GraphLink;
import cornerstone.biz.domain.WorkflowChartDefine.GraphLinkInfo;
import cornerstone.biz.domain.WorkflowChartDefine.GraphNode;
import cornerstone.biz.domain.WorkflowChartDefine.GraphNodeInfo;
import cornerstone.biz.domain.WorkflowChartDefine.GraphNodeWithLink;
import cornerstone.biz.domain.WorkflowChartDefine.GraphProjectRole;
import cornerstone.biz.domain.WorkflowChartDefine.GraphProperty;
import cornerstone.biz.domain.WorkflowChartDefine.GraphRule;
import cornerstone.biz.domain.WorkflowChartDefine.GraphTree;
import cornerstone.biz.domain.WorkflowChartDefine.GraphUser;
import cornerstone.biz.domain.WorkflowChartDefine.NodeFormField;
import cornerstone.biz.domain.WorkflowChartDefine.WorkflowChartDefineInfo;
import cornerstone.biz.domain.WorkflowChartDefine.WorkflowChartDefineQuery;
import cornerstone.biz.domain.WorkflowDefine;
import cornerstone.biz.domain.WorkflowDefine.WorkflowDefineInfo;
import cornerstone.biz.domain.WorkflowFormDefine;
import cornerstone.biz.domain.WorkflowFormDefine.FormField;
import cornerstone.biz.domain.WorkflowInstance;
import cornerstone.biz.domain.WorkflowInstance.WorkflowInstanceInfo;
import cornerstone.biz.domain.WorkflowInstanceChangeLog;
import cornerstone.biz.domain.WorkflowInstanceNodeChange;
import cornerstone.biz.domain.WorkflowInstanceNodeChange.WorkflowInstanceNodeChangeQuery;
import cornerstone.biz.domain.WorkflowInstanceOwner;
import cornerstone.biz.domain.WorkflowInstanceOwner.WorkflowInstanceOwnerInfo;
import cornerstone.biz.domain.WorkflowInstanceOwner.WorkflowInstanceOwnerQuery;
import cornerstone.biz.domain.WorkflowInstanceOwner.WorkflowInstanceOwnerSimpleInfo;
import cornerstone.biz.domain.WorkflowInstancePermission;
import cornerstone.biz.util.BeanUtil;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DumpUtil;
import cornerstone.biz.util.StringUtil;
import jazmin.core.app.AppException;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class WorkflowService extends CommService {
	//
	private static Logger logger = LoggerFactory.get(WorkflowService.class);

	//
	/**
	 * 查询有效流程
	 * 
	 * @return
	 */
	public WorkflowDefine getValidWorkflowDefine(int workflowDefineId) {
		WorkflowDefine workflowDefine = dao.getExistedById(WorkflowDefine.class, workflowDefineId);
		if (workflowDefine.status != WorkflowDefine.STATUS_有效) {
			throw new AppException("流程不存在或已无效");
		}
		return workflowDefine;
	}
	//
	public Map<String, GraphProperty> parseProperty(String json) {
		return JSONUtil.fromJsonMap(json, String.class, GraphProperty.class);
	}

	//
	public GraphTree parseGraph(String graphJson, String propsJson) {
		Map<String, GraphProperty> graphProperty = parseProperty(propsJson);
		if (graphProperty == null) {
			graphProperty = new HashMap<>();
		}
		Graph graph = JSONUtil.fromJson(graphJson, Graph.class);
		if (graph == null || graph.nodes == null || graph.nodes.isEmpty()) {
			throw new AppException("流转数据不能为空");
		}
		// 1.确保只有一个start
		GraphNode startNode = null;
		Map<String, GraphNodeInfo> nodeMap = new HashMap<>();
		for (GraphNode node : graph.nodes) {
			nodeMap.put(node.id, GraphNodeInfo.create(node));
			if (node.type != null && node.type.equals(GraphNode.TYPE_NODE_START)) {
				if (startNode != null) {
					throw new AppException("只能有1个开始节点");
				}
				startNode = node;
			}
		}
		if (startNode == null) {
			throw new AppException("未找到开始节点");
		}

		// 2.生成跳转map
		Map<String, Map<String, GraphLinkInfo>> linkMap = new HashMap<>();
		for (GraphLink link : graph.links) {
			if (link.source == null || link.target == null) {
				throw new AppException("跳转源节点或目标节点都不能为空");
			}
			Map<String, GraphLinkInfo> targets = linkMap.get(link.source);
			if (targets == null) {
				targets = new LinkedHashMap<>();
				linkMap.put(link.source, targets);
			}
			targets.put(link.target, GraphLinkInfo.create(link));
		}
		//
		// 3.创建节点树
		GraphTree tree = new GraphTree();
		tree.startNode = GraphNodeInfo.create(startNode);
		tree.nodes = GraphNodeInfo.createList(graph.nodes);
		tree.links = GraphLinkInfo.createList(graph.links);
		makeNodeTree(tree, tree.startNode, nodeMap, linkMap, graphProperty);
		//
		return tree;
	}

	//
	private void makeNodeTree(GraphTree tree, GraphNodeInfo node, Map<String, GraphNodeInfo> nodeMap,
			Map<String, Map<String, GraphLinkInfo>> linkMap, Map<String, GraphProperty> graphProperty) {
		node.nodeProperty = graphProperty.get(node.id);
		tree.nodeMap.put(node.id, node);
		Map<String, GraphLinkInfo> targets = linkMap.get(node.id);
		if (targets != null) {
			for (Map.Entry<String, GraphLinkInfo> entry : targets.entrySet()) {
				String targetNodeId = entry.getKey();
				GraphLinkInfo link = entry.getValue();
				link.linkProperty = graphProperty.get(link.id);
				GraphNodeInfo targetNode = nodeMap.get(targetNodeId);
				if (targetNode == null) {
					throw new AppException("目标节点找不到" + targetNodeId);
				}
				GraphNodeWithLink targetNodeWithLink = new GraphNodeWithLink();
				targetNodeWithLink.node = targetNode;
				targetNodeWithLink.link = link;
				node.targetNodes.add(targetNodeWithLink);
				makeNodeTree(tree, targetNode, nodeMap, linkMap, graphProperty);
			}
		}
	}

	//
	// 判断是否是责任人(只有待处理状态才算是责任人，否则很多问题)
	public boolean isOwner(WorkflowInstance instance, int accountId) {
		if (instance.currAccountList == null) {
			return false;
		}
		for (WorkflowInstanceOwnerSimpleInfo e : instance.currAccountList) {
			if (e.ownerAccountId == accountId && e.status == WorkflowInstanceOwner.STATUS_待处理) {
				return true;
			}
		}
		return false;
	}

	//
	public Set<Integer> getOwner(WorkflowInstance instance, GraphNodeInfo node) {
		return getOwnerOrCC(instance, node, true);
	}

	public Set<Integer> getCc(WorkflowInstance instance, GraphNodeInfo node) {
		return getOwnerOrCC(instance, node, false);
	}

	/**
	 * 
	 * @param instance
	 * @param node
	 * @param isOwner
	 *            owner or cc
	 * @return
	 */
	private Set<Integer> getOwnerOrCC(WorkflowInstance instance, GraphNodeInfo node, boolean isOwner) {
		Set<Integer> accountSet = new HashSet<>();
		int companyId = instance.companyId;
		GraphProperty property = node.nodeProperty;
		if (node == null || property == null) {
			return accountSet;
		}
		GraphAccounts accounts = null;
		if (isOwner) {
			accounts = property.owner;
		} else {
			accounts = property.cc;
		}
		if (accounts == null) {
			return accountSet;
		}
		accountSet = getAccountIds(companyId,instance, accounts.userList, accounts.companyRoleList, accounts.projectRoleList,
				accounts.departmentList, accounts.departmentOwnerList,accounts.formItemList);
		if (accounts.submitter) {
			accountSet.add(instance.createAccountId);
		}
		return accountSet;
	}
	
	//成员选择
	private void addMembers(Set<Integer> accountSet,List<GraphUser> userList) {
		if (userList != null) {
			for (GraphUser user : userList) {
				accountSet.add(user.id);
			}
		}
	}
	
	//部门成员列表
	private void addDepartmentMembers(int companyId,Set<Integer> accountSet,List<GraphDepartment> departmentList) {
		if (departmentList != null) {// 部门成员列表
			Set<Integer> departmentIdSet = new LinkedHashSet<>();
			for (GraphDepartment department : departmentList) {
				int[] departmentIds = getChildDepartmentIds(department.id);
				for (int departmentId : departmentIds) {
					departmentIdSet.add(departmentId);
				}
			}
			if (!departmentIdSet.isEmpty()) {
				List<Integer> accountIdList = dao.getAccountIdsByDepartmentIds(companyId, departmentIdSet);
				for (Integer accountId : accountIdList) {
					accountSet.add(accountId);
				}
			}
		}
	}
	
	//部门负责人列表
	private void addDepartmentOwners(int companyId,Set<Integer> accountSet,List<GraphDepartment> departmentOwnerList) {
		if (departmentOwnerList != null) {// 部门负责人列表
			Set<Integer> departmentIdSet = new LinkedHashSet<>();
			for (GraphDepartment department : departmentOwnerList) {
				departmentIdSet.add(department.id);
			}
			List<Integer> accountIdList = dao.getOwnerAccountIdsByDepartmentIds(companyId, departmentIdSet);
			for (Integer accountId : accountIdList) {
				accountSet.add(accountId);
			}
		}
	}
	
	//公司角色列表
	private void addCompanyRoleMembers(int companyId,Set<Integer> accountSet, List<GraphCompanyRole> companyRoleList) {
		if (companyRoleList != null) {
			Set<Integer> roleSet = new LinkedHashSet<>();
			for (GraphCompanyRole role : companyRoleList) {
				roleSet.add(role.id);
			}
			List<Integer> accountIdList = dao.getAccountIdsByCompanyRoles(companyId, roleSet);
			for (Integer accountId : accountIdList) {
				accountSet.add(accountId);
			}
		}
	}
	
	//项目角色列表
	private void addProjectRoleMembers(int companyId,Set<Integer> accountSet, List<GraphProjectRole> projectRoleList) {
		if (projectRoleList != null) {
			Set<String> roleSet = new LinkedHashSet<>();
			for (GraphProjectRole role : projectRoleList) {
				roleSet.add(role.id);
			}
			List<Integer> accountIdList = dao.getAccountIdsByProjectRoles(companyId, roleSet);
			for (Integer accountId : accountIdList) {
				accountSet.add(accountId);
			}
		}
	}
	
	//表单成员列表
	private void addFormItemList(int companyId,WorkflowInstance instance,Set<Integer> accountSet,List<String> formItemList) {
		if(formItemList!=null) {
			Map<String, FormField> formFields=getFormFieldDefines(instance);
			for (String id : formItemList) {
				Object value=getFieldValue(instance, id);
				if(value==null) {
					continue;
				}
				FormField ff=formFields.get(id);
				String t=ff.type;
				//
				if(t.equals(FormField.TYPE_USER_SELECT)){
					List<GraphUser> userList=JSONUtil.fromJsonList(value.toString(),GraphUser.class);
					addMembers(accountSet, userList);
				}
				//
				if(t.equals(FormField.TYPE_DEPARTMENT_SELECT)){
					List<GraphDepartment> departmentList=JSONUtil.fromJsonList(value.toString(),GraphDepartment.class);
					addDepartmentMembers(companyId, accountSet, departmentList);
				}
				//
				if(t.equals(FormField.TYPE_ROLE_COMPANY_SELECT)){
					List<GraphCompanyRole> companyRoleList=JSONUtil.fromJsonList(value.toString(),GraphCompanyRole.class);
					addCompanyRoleMembers(companyId, accountSet, companyRoleList);
				}
				//
				if(t.equals(FormField.TYPE_ROLE_PROJECT_SELECT)){
					List<GraphProjectRole> projectRoleList=JSONUtil.fromJsonList(value.toString(),GraphProjectRole.class);
					addProjectRoleMembers(companyId, accountSet, projectRoleList);
				}
			}
		}
	}

	/**
	 * 获取所有满足的成员列表
	 * 
	 * @param companyRoleList
	 * @param projectRoleList
	 * @param departmentList
	 * @return
	 */
	public Set<Integer> getAccountIds(int companyId,WorkflowInstance instance, List<GraphUser> userList, List<GraphCompanyRole> companyRoleList,
			List<GraphProjectRole> projectRoleList, List<GraphDepartment> departmentList,
			List<GraphDepartment> departmentOwnerList,List<String> formItemList) {
		Set<Integer> accountSet = new HashSet<>();
		addMembers(accountSet, userList);
		addDepartmentMembers(companyId, accountSet, departmentList);
		addDepartmentOwners(companyId, accountSet, departmentOwnerList);
		addCompanyRoleMembers(companyId, accountSet, companyRoleList);
		addProjectRoleMembers(companyId, accountSet, projectRoleList);
		addFormItemList(companyId,instance,accountSet, formItemList);
		return accountSet;
	}

	/**
	 * 找出流程实例当前节点下的（责任人,抄送人,创建人）
	 * 
	 * @param instance
	 * @return
	 */
	public Set<Integer> getWorkflowInstanceJoinMembers(WorkflowInstance instance) {
		Set<Integer> accountIdSet = new LinkedHashSet<>();
		accountIdSet.add(instance.createAccountId);
		for (WorkflowInstanceOwnerSimpleInfo e : instance.currAccountList) {
			accountIdSet.add(e.ownerAccountId);
		}
		List<WorkflowInstanceOwnerSimpleInfo> ccList = getOwnerAccounts(instance, WorkflowInstanceOwner.TYPE_抄送人);
		for (WorkflowInstanceOwnerSimpleInfo e : ccList) {
			accountIdSet.add(e.ownerAccountId);
		}
		return accountIdSet;
	}

	//
	public GraphTree parse(WorkflowChartDefine chart) {
		GraphTree tree = parseGraph(chart.graph, chart.props);
		return tree;
	}

	public GraphTree parse(WorkflowInstance instance) {
		WorkflowChartDefine chartDefine = dao.getExistedById(WorkflowChartDefine.class, instance.workflowChartDefineId);
		return parse(chartDefine);
	}

	//
	// 获取下一个节点
	public GraphNodeInfo getNextNode(WorkflowInstance instance, GraphTree tree) {
		String currNodeId = instance.currNode;
		if (StringUtil.isEmpty(currNodeId)) {// 开始节点
			currNodeId = tree.startNode.id;
		}
		GraphNodeInfo node = tree.nodeMap.get(currNodeId);
		if (node == null) {
			throw new AppException("节点不存在" + currNodeId);
		}
		if (node.targetNodes == null || node.targetNodes.isEmpty()) {
			throw new AppException("找不到下一个节点");
		}
		WorkflowFormDefine form = dao.getExistedById(WorkflowFormDefine.class, instance.workflowFormDefineId);
		Map<String, FormField> formFieldMap=getFormFieldDefines(form);
		List<GraphNodeWithLink> hitLinks = new ArrayList<>();
		for (GraphNodeWithLink e : node.targetNodes) {
			GraphLinkInfo link = e.link;
			if (executeExpress(instance, form, link)) {
				hitLinks.add(e);
			}
		}
		if (hitLinks.size() == 0) {
			throw new AppException("【" + node.name + "】没有条件满足,无法执行");
		}
		if (hitLinks.size() > 2) {
			throw new AppException("【" + node.name + "】有多个条件符合跳转,无法执行");
		}
		GraphNodeWithLink hitLink = null;
		if (hitLinks.size() == 2) {// 如果一个设置了表达式 一个没有设置 则选择设置了表达式的
			if (isEmptyExpress(formFieldMap,hitLinks.get(0).link) && isEmptyExpress(formFieldMap,hitLinks.get(1).link)) {// 两个都是空的
				throw new AppException("【" + node.name + "】有多个条件符合跳转,无法执行");
			}
			if (!isEmptyExpress(formFieldMap,hitLinks.get(0).link) && !isEmptyExpress(formFieldMap,hitLinks.get(1).link)) {// 两个都是空的
				throw new AppException("【" + node.name + "】有多个条件符合跳转,无法执行");
			}
			hitLink = isEmptyExpress(formFieldMap,hitLinks.get(0).link) ? hitLinks.get(1) : hitLinks.get(0);
		} else {
			hitLink = hitLinks.get(0);
		}
		return hitLink.node;
	}

	private String computeExpress(Map<String,FormField> formMap,GraphLinkInfo link) {
		GraphProperty property = link.linkProperty;
		if (property == null) {
			return null;
		}
		if (!StringUtil.isEmpty(property.express)) {
			return property.express;
		}
		int index=0;
		if (!StringUtil.isEmpty(property.ruleType)&&property.ruleList!=null&&property.ruleList.size()>0) {
			StringBuilder express=new StringBuilder();
			express.append("(");
			for (GraphRule rule : property.ruleList) {
				FormField feild=formMap.get(rule.field);
				if(feild==null) {
					logger.error("feild is null {}",rule.field);
					throw new AppException("规则配置错误");
				}
				String value="";
				if(rule.value!=null) {
					value=rule.value.toString();
				}
				if(feild.type.equals(FormField.TYPE_TEXT_NUMBER)) {//如果是数字
					express.append(feild.name);
					if("like".equals(rule.op)|| "=".equals(rule.op)){
						express.append("==");
					}else{
						express.append(rule.op);
					}
					express.append(value);
				}else {//字符串
					if("=".equals(rule.op)){//等于
						express.append(feild.name);
						express.append("==");
						express.append("'");
						express.append(value);
						express.append("'");
					}else if("like".equals(rule.op)){//包含string.contains(姓名,'张')//表示姓名里包含张的
						express.append("string.contains");
						express.append("(");
							express.append(feild.name);
							express.append(",");
							express.append("'");
							express.append(value);
							express.append("'");
						express.append(")");
					}else {//其他(不等于)
						express.append(feild.name);
						express.append(rule.op);
						express.append("'");
						express.append(value);
						express.append("'");
					}
					
				}
				if(index<property.ruleList.size()-1) {
					if("and".equals(property.ruleType)) {
						express.append("&&");
					}else {
						express.append("||");
					}
				}
				index++;
			}
			express.append(")");
			if(logger.isDebugEnabled()) {
				logger.debug("computeExpress \nformMap:{} \nproperty:{} \nexpress:{}",
						DumpUtil.dump(formMap),DumpUtil.dump(property),express.toString());
			}
			return express.toString();
		}
		return null;
	}
	
	private boolean isEmptyExpress(Map<String, FormField> formFieldMap,GraphLinkInfo link) {
		String express = computeExpress(formFieldMap,link);
		return StringUtil.isEmpty(express);
	}

	private boolean executeExpress(WorkflowInstance instance, WorkflowFormDefine form, GraphLinkInfo link) {
		GraphProperty property = link.linkProperty;
		if (property == null) {
			return true;
		}
		String express = computeExpress(getFormFieldDefines(form),link);
		if (StringUtil.isEmpty(express)) {
			return true;
		}
		Map<String, Object> env = new HashMap<String, Object>();
		Map<String, Object> formData = JSONUtil.fromJson(instance.formData, Map.class);
		if(formData==null) {
			formData=new HashMap<>();
		}
		List<FormField> fields = JSONUtil.fromJsonList(form.fieldList, FormField.class);
		for (FormField f : fields) {
			if (formData.containsKey(f.id)) {
				env.put(f.name, formData.get(f.id));
			}
		}
		Object ret = null;
		try {
			ret = AviatorEvaluator.execute(express, env);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppException("判断条件【" + link.name + "】执行错误," + e.getMessage());
		} finally {
			logger.info("AviatorEvaluator.execute({}, {})={} linkName:{}", express, DumpUtil.dump(env), ret, link.name);
		}
		if (!(ret instanceof Boolean)) {
			throw new AppException("判断条件【" + link.name + "】没有返回逻辑值");
		}
		//
		return (Boolean) ret;
	}

	public Map<String, FormField> getFormFieldDefines(WorkflowInstance instance) {
		WorkflowFormDefine form = dao.getExistedById(WorkflowFormDefine.class, instance.workflowFormDefineId);
		return getFormFieldDefines(form);
	}
	
	public Map<String, FormField> getFormFieldDefines(WorkflowFormDefine form) {
		Map<String, FormField> ret = new HashMap<>();
		List<FormField> fields = JSONUtil.fromJsonList(form.fieldList, FormField.class);
		if (fields != null) {
			for (FormField e : fields) {
				ret.put(e.id, e);
			}
		}
		return ret;
	}

	public Map<String, Object> getFormFieldValues(WorkflowInstance instance) {
		Map<String, Object> ret = new HashMap<>();
		if (instance.formData != null) {
			ret = JSONUtil.fromJson(instance.formData, Map.class);
		}
		if(ret==null) {
			ret=new HashMap<>();
		}
		return ret;
	}
	
	public Object getFieldValue(WorkflowInstance instance,String fieldId) {
		Map<String, Object> map=getFormFieldValues(instance);
		return map.get(fieldId);
	}

	/**
	 * 
	 * @param instance
	 * @return
	 */
	public GraphNodeInfo getCurrNode(WorkflowInstance instance) {
		GraphTree tree = parse(instance);
		return getCurrNode(instance, tree);
	}

	public GraphNodeInfo getCurrNode(WorkflowInstance instance, GraphTree tree) {
		GraphNodeInfo node=tree.nodeMap.get(instance.currNode);
		return node;
	}

	/**
	 * 
	 * @param instance
	 * @param nodeId
	 * @return
	 */
	public GraphNodeInfo getNodeById(WorkflowInstance instance, String nodeId) {
		GraphTree tree = parse(instance);
		return tree.nodeMap.get(nodeId);
	}

	/**
	 * 不区分是否处理状态
	 * 
	 * @return
	 */
	public List<WorkflowInstanceOwnerSimpleInfo> getOwnerAccounts(WorkflowInstance instance, int type) {
		WorkflowInstanceOwnerQuery query = new WorkflowInstanceOwnerQuery();
		query.workflowInstanceId = instance.id;
		query.eqNodeId = instance.currNode;
		query.type = type;
		query.pageSize = Integer.MAX_VALUE;
		List<WorkflowInstanceOwnerInfo> list = dao.getList(query);
		List<WorkflowInstanceOwnerSimpleInfo> ret = new ArrayList<>();
		for (WorkflowInstanceOwnerInfo e : list) {
			ret.add(BeanUtil.copyTo(WorkflowInstanceOwnerSimpleInfo.class, e));
		}
		return ret;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public WorkflowInstanceInfo getWorkflowInstanceByUuid(String uuid) {
		return dao.getExistedByUuid(WorkflowInstanceInfo.class, uuid);
	}

	//
	public WorkflowInstance getWorkflowInstanceByUuidForUpdate(String uuid) {
		return dao.getExistedByUuidForUpdate(WorkflowInstance.class, uuid);
	}

	// 查询实例经历过的node列表(id从小到大排序)
	public List<WorkflowInstanceNodeChange> getNodeChangeList(int instanceId) {
		WorkflowInstanceNodeChangeQuery query = new WorkflowInstanceNodeChangeQuery();
		query.pageSize = Integer.MAX_VALUE;
		query.workflowInstanceId = instanceId;
		query.orderType = WorkflowInstanceNodeChangeQuery.ORDER_TYPE_ID_ASC;
		return dao.getList(query);
	}

	//
	public void addWorkflowInstanceChangeLog(WorkflowInstance instance, int type, int ownerAccountId, String detail,
			String remark, String submitButtonText, String terminalButtonText) {
		addWorkflowInstanceChangeLog(instance.companyId, instance.id, type, instance.beforeNodeId,
				instance.beforeNodeName, instance.currNode, instance.currNodeName, ownerAccountId, detail, remark,
				submitButtonText, terminalButtonText);
	}

	//
	public void addWorkflowInstanceChangeLog(int companyId, int workflowInstanceId, int type, String beforeNodeId,
			String beforeNodeName, String nodeId, String nodeName, int ownerAccountId) {
		addWorkflowInstanceChangeLog(companyId, workflowInstanceId, type, beforeNodeId, beforeNodeName, nodeId,
				nodeName, ownerAccountId, null, null, null, null);
	}

	//
	public void addWorkflowInstanceChangeLog(int companyId, int workflowInstanceId, int type, String beforeNodeId,
			String beforeNodeName, String nodeId, String nodeName, int ownerAccountId, String detail, String remark,
			String submitButtonText, String terminalButtonText) {
		WorkflowInstanceChangeLog changeLog = new WorkflowInstanceChangeLog();
		changeLog.companyId = companyId;
		changeLog.workflowInstanceId = workflowInstanceId;
		changeLog.type = type;
		changeLog.beforeNodeId = beforeNodeId;
		changeLog.beforeNodeName = beforeNodeName;
		changeLog.nodeId = nodeId;
		changeLog.nodeName = nodeName;
		changeLog.ownerAccountId = ownerAccountId;
		changeLog.detail = detail;
		changeLog.remark = remark;
		changeLog.submitButtonText = submitButtonText;
		changeLog.terminalButtonText = terminalButtonText;
		dao.add(changeLog);
	}

	/**
	 * 获取权限用于操作
	 * 
	 * @param account
	 * @param instance
	 * @return
	 */
	public WorkflowInstancePermission getPermissionForOperation(Account account, WorkflowInstance instance) {
		WorkflowDefineInfo define = dao.getExistedById(WorkflowDefineInfo.class, instance.workflowDefineId);
		WorkflowChartDefineInfo chart = dao.getExistedById(WorkflowChartDefineInfo.class,
				instance.workflowChartDefineId);
		GraphTree tree = parseGraph(chart.graph, chart.props);
		return getPermission(account, instance, define, tree, isAdmin(account));
	}

	/**
	 * 获取权限用于显示
	 * 
	 * @param account
	 * @param instance
	 * @param isAdmin
	 * @return
	 */
	public WorkflowInstancePermission getPermissionForView(Account account, WorkflowInstance instance,
			boolean isAdmin) {
		WorkflowDefineInfo define = dao.getExistedById(WorkflowDefineInfo.class, instance.workflowDefineId);
		WorkflowChartDefineInfo chart = dao.getExistedById(WorkflowChartDefineInfo.class,
				instance.workflowChartDefineId);
		GraphTree tree = parseGraph(chart.graph, chart.props);
		return getPermission(account, instance, define, tree, isAdmin);
	}

	/**
	 * 获取权限
	 * 
	 * @param account
	 * @param instance
	 * @param define
	 * @return
	 */
	private WorkflowInstancePermission getPermission(Account account, WorkflowInstance instance, WorkflowDefine define,
			GraphTree tree, boolean isAdmin) {
		WorkflowInstancePermission permission = new WorkflowInstancePermission();
		permission.graphLinkList = BeanUtil.copyToList(GraphLink.class, tree.links);
		permission.graphNodeList = BeanUtil.copyToList(GraphNode.class, tree.nodes);
		GraphNodeInfo currNode = tree.nodeMap.get(instance.currNode);
		GraphProperty nodeProp = currNode.nodeProperty;
		if (nodeProp != null) {
			permission.forwardRule = nodeProp.forwardRule;
		}
		// formFieldList
		List<NodeFormField> fields = getVisiableFieldIds(account.id,isAdmin,instance, tree);
		Set<String> editableFieldIds = getEditableFieldIds(instance, tree);
		for (NodeFormField f : fields) {
			if (editableFieldIds.contains(f.id)) {
				f.editable = true;
			} else {
				f.editable = false;
			}
		}
		permission.formFieldList = BeanUtil.copyToList(NodeFormField.class, fields);
		//
		boolean isOwner = isOwner(instance, account.id);
		permission.enablePrint = define.enablePrint;
		if (isOwner) {// 如果是责任人
			permission.enableSubmit = true;
			if (nodeProp != null) {
				permission.enableSave = nodeProp.enableSave;
				permission.enableTerminal = nodeProp.enableTerminal;
				permission.enableBackword = nodeProp.enableBackword;
				permission.backwordNodeList = new ArrayList<>();
				if (nodeProp.backwordNodeList != null) {
					for (String nodeId : nodeProp.backwordNodeList) {
						permission.backwordNodeList.add(tree.nodeMap.get(nodeId));
					}
				}
				permission.enableTransferTo = nodeProp.enableTransferTo;
				permission.submitButtonText = nodeProp.submitButtonText;
				permission.terminalButtonText = nodeProp.terminalButtonText;
			}
		} else {
			permission.enableSubmit = false;
			if (permission.formFieldList != null) {
				for (NodeFormField field : permission.formFieldList) {
					field.editable = false;
				}
			}
		}
		// 如果是发起人
		if (instance.createAccountId == account.id) {
			permission.enableCancel = true;
		}
		// 如果是管理员
		if (isAdmin) {
			permission.enableCancel = true;
			permission.enableSubmit = true;
			permission.enableSave = true;
			permission.enableTerminal = true;
			permission.enableCancel = true;
			permission.enableBackword = true;
			permission.enableTransferTo = true;
			permission.enableDelete = true;
		}
		// 如果是结束
		if (instance.isFinished) {
			permission.enableCancel = false;
			permission.enableSubmit = false;
			permission.enableSave = false;
			permission.enableTerminal = false;
			permission.enableCancel = false;
			permission.enableBackword = false;
			permission.enableTransferTo = false;
		}
		//
		if(instance.status==WorkflowInstance.STATUS_草稿) {
			permission.enablePrint=false;
			permission.enableCancel=false;
			permission.enableBackword=false;
			permission.enableTransferTo=false;
			permission.enableDelete=false;
		}
		//
		return permission;
	}

	/**
	 * 是否是流程数据管理员
	 * 
	 * @param account
	 * @return
	 */
	private boolean isAdmin(Account account) {
		return haveCompanyPermission(account, Permission.ID_管理流程数据);
	}

	/**
	 * 是否都同意了
	 * 
	 * @param instance
	 * @return
	 */
	public boolean isAllSubmit(WorkflowInstance instance) {
		WorkflowInstanceOwnerQuery query = new WorkflowInstanceOwnerQuery();
		query.workflowInstanceId = instance.id;
		query.eqNodeId = instance.currNode;
		query.status = WorkflowInstanceOwner.STATUS_待处理;
		return dao.getListCount(query) == 0;
	}

	/**
	 * 把未处理都改为他人处理
	 * 
	 * @param instance
	 */
	public void setWorkflowInstanceOwnerStatusOtherDone(WorkflowInstance instance) {
		dao.setWorkflowInstanceOwnerStatusOtherDone(instance.id);
	}

	/**
	 * 获取可见fields(不只是这个节点，包含以前经历过的所有节点)
	 * 
	 * @param accountId
	 * @param instance
	 * @return
	 */
	public List<NodeFormField> getVisiableFieldIds(int accountId,boolean isAdmin, WorkflowInstance instance) {
		GraphTree tree = parse(instance);
		return getVisiableFieldIds(accountId, isAdmin,instance, tree);
	}

	/**
	 * 获取可见fields(不只是这个节点，包含以前经历过的所有节点)
	 * 
	 * @param accountId
	 * @param instance
	 * @param tree
	 * @return
	 */
	public List<NodeFormField> getVisiableFieldIds(int accountId,boolean isAdmin,WorkflowInstance instance, GraphTree tree) {
		List<WorkflowInstanceNodeChange> nodeChanges = getNodeChangeList(instance.id);
		List<NodeFormField> ret = new ArrayList<>();
		Set<String> visiableFieldIds = new LinkedHashSet<>();
		for (WorkflowInstanceNodeChange e : nodeChanges) {
			if (!BizUtil.contains(e.ownerAccountList, "ownerAccountId", accountId)
					&& !BizUtil.contains(e.ccAccountList, "ownerAccountId", accountId)
					&& !isAdmin) {
				continue;
			}
			GraphNodeInfo node = tree.nodeMap.get(e.nodeId);
			if(node==null||node.nodeProperty==null||node.nodeProperty.fieldList==null) {
				continue;
			}
			for (NodeFormField field : node.nodeProperty.fieldList) {
				if (field.visible||isAdmin) {
					if (visiableFieldIds.contains(field.id)) {
						continue;
					}
					field.visible=true;
					visiableFieldIds.add(field.id);
					ret.add(field);
				}
			}
		}
		return ret;
	}

	/**
	 * 获取这个节点责任人能够编辑的字段列表
	 * 
	 * @param instance
	 * @param tree
	 * @return
	 */
	public Set<String> getEditableFieldIds(WorkflowInstance instance, GraphTree tree) {
		GraphNodeInfo currNode = getCurrNode(instance, tree);
		return getEditableFieldIds(instance, currNode);
	}

	/**
	 * 获取这个节点责任人能够编辑的字段列表
	 * 
	 * @param instance
	 * @param node
	 * @return
	 */
	public Set<String> getEditableFieldIds(WorkflowInstance instance, GraphNodeInfo node) {
		Set<String> fieldIds = new HashSet<>();
		if (node.nodeProperty == null || node.nodeProperty.fieldList == null) {
			return fieldIds;
		}
		for (NodeFormField field : node.nodeProperty.fieldList) {
			if (field.editable) {
				fieldIds.add(field.id);
			}
		}
		return fieldIds;

	}

	/**
	 * 过滤FormData
	 * 
	 * @param totalFormData
	 * @return
	 */
	private String filterFormData(String totalFormData, List<NodeFormField> visiableFields) {
		Map<String, Object> beforeMap = JSONUtil.fromJson(totalFormData, Map.class);
		if (beforeMap == null) {
			return null;
		}
		Map<String, Object> result = new LinkedHashMap<>();
		for (NodeFormField field : visiableFields) {
			Object fieldValue = beforeMap.get(field.id);
			result.put(field.id, fieldValue);
		}
		return JSONUtil.toJson(result);
	}

	/**
	 * 
	 * @param accountId
	 * @param instance
	 * @return
	 */
	public String filterFormData(int accountId, WorkflowInstance instance,boolean isAdmin) {
		List<NodeFormField> fields = getVisiableFieldIds(accountId,isAdmin,instance);
		return filterFormData(instance.formData, fields);
	}

	/**
	 * 合并FormData
	 * 
	 * @param beforeFormData
	 * @param afterFormData
	 * @return
	 */
	public String mergeFormData(String beforeFormData, String afterFormData) {
		Map<String, Object> beforeMap = JSONUtil.fromJson(beforeFormData, Map.class);
		Map<String, Object> afterMap = JSONUtil.fromJson(afterFormData, Map.class);
		if (beforeMap == null) {
			beforeMap = afterMap;
		}
		if (afterMap != null) {
			for (Map.Entry<String, Object> entry : afterMap.entrySet()) {
				beforeMap.put(entry.getKey(), entry.getValue());
			}
		}
		return JSONUtil.toJson(beforeMap);
	}

	public List<WorkflowChartDefine> getAllWorkflowChartDefines(int workflowDefineId) {
		WorkflowChartDefineQuery query = new WorkflowChartDefineQuery();
		query.workflowDefineId = workflowDefineId;
		return dao.getAll(query);
	}

	/**
	 * 检查WorkflowChartDefine合法性
	 * 
	 * @param old
	 */
	public void checkWorkflowChartDefineValid(WorkflowChartDefine old) {
		// TODO Auto-generated method stub

	}

	public void addWorkflowInstanceOwner(int accountId, WorkflowInstance instance, int ownerAccountId, int type) {
		WorkflowInstanceOwner owner = new WorkflowInstanceOwner();
		owner.ownerAccountId = ownerAccountId;
		owner.companyId = instance.companyId;
		owner.workflowInstanceId = instance.id;
		if (type == WorkflowInstanceOwner.TYPE_责任人) {
			owner.status = WorkflowInstanceOwner.STATUS_待处理;
		}
		owner.type = type;
		owner.nodeId = instance.currNode;
		owner.nodeName = instance.currNodeName;
		owner.createAccountId = accountId;
		owner.enterTime = new Date();
		BizUtil.checkValid(owner);
		dao.add(owner);
	}

	/**
	 * 
	 * @param id
	 */
	public void deleteWorkflowInstance(int id) {
		dao.deleteById(WorkflowInstance.class, id);
		dao.delete(WorkflowInstanceNodeChange.class, QueryWhere.create().where("workflow_instance_id", id));
		dao.delete(WorkflowInstanceChangeLog.class, QueryWhere.create().where("workflow_instance_id", id));
		dao.delete(WorkflowInstanceOwner.class, QueryWhere.create().where("workflow_instance_id", id));
	}
}
