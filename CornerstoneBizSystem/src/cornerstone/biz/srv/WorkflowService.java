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
	 * ??????????????????
	 * 
	 * @return
	 */
	public WorkflowDefine getValidWorkflowDefine(int workflowDefineId) {
		WorkflowDefine workflowDefine = dao.getExistedById(WorkflowDefine.class, workflowDefineId);
		if (workflowDefine.status != WorkflowDefine.STATUS_??????) {
			throw new AppException("???????????????????????????");
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
			throw new AppException("????????????????????????");
		}
		// 1.??????????????????start
		GraphNode startNode = null;
		Map<String, GraphNodeInfo> nodeMap = new HashMap<>();
		for (GraphNode node : graph.nodes) {
			nodeMap.put(node.id, GraphNodeInfo.create(node));
			if (node.type != null && node.type.equals(GraphNode.TYPE_NODE_START)) {
				if (startNode != null) {
					throw new AppException("?????????1???????????????");
				}
				startNode = node;
			}
		}
		if (startNode == null) {
			throw new AppException("?????????????????????");
		}

		// 2.????????????map
		Map<String, Map<String, GraphLinkInfo>> linkMap = new HashMap<>();
		for (GraphLink link : graph.links) {
			if (link.source == null || link.target == null) {
				throw new AppException("?????????????????????????????????????????????");
			}
			Map<String, GraphLinkInfo> targets = linkMap.get(link.source);
			if (targets == null) {
				targets = new LinkedHashMap<>();
				linkMap.put(link.source, targets);
			}
			targets.put(link.target, GraphLinkInfo.create(link));
		}
		//
		// 3.???????????????
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
					throw new AppException("?????????????????????" + targetNodeId);
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
	// ????????????????????????(????????????????????????????????????????????????????????????)
	public boolean isOwner(WorkflowInstance instance, int accountId) {
		if (instance.currAccountList == null) {
			return false;
		}
		for (WorkflowInstanceOwnerSimpleInfo e : instance.currAccountList) {
			if (e.ownerAccountId == accountId && e.status == WorkflowInstanceOwner.STATUS_?????????) {
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
	
	//????????????
	private void addMembers(Set<Integer> accountSet,List<GraphUser> userList) {
		if (userList != null) {
			for (GraphUser user : userList) {
				accountSet.add(user.id);
			}
		}
	}
	
	//??????????????????
	private void addDepartmentMembers(int companyId,Set<Integer> accountSet,List<GraphDepartment> departmentList) {
		if (departmentList != null) {// ??????????????????
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
	
	//?????????????????????
	private void addDepartmentOwners(int companyId,Set<Integer> accountSet,List<GraphDepartment> departmentOwnerList) {
		if (departmentOwnerList != null) {// ?????????????????????
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
	
	//??????????????????
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
	
	//??????????????????
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
	
	//??????????????????
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
	 * ?????????????????????????????????
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
	 * ????????????????????????????????????????????????,?????????,????????????
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
		List<WorkflowInstanceOwnerSimpleInfo> ccList = getOwnerAccounts(instance, WorkflowInstanceOwner.TYPE_?????????);
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
	// ?????????????????????
	public GraphNodeInfo getNextNode(WorkflowInstance instance, GraphTree tree) {
		String currNodeId = instance.currNode;
		if (StringUtil.isEmpty(currNodeId)) {// ????????????
			currNodeId = tree.startNode.id;
		}
		GraphNodeInfo node = tree.nodeMap.get(currNodeId);
		if (node == null) {
			throw new AppException("???????????????" + currNodeId);
		}
		if (node.targetNodes == null || node.targetNodes.isEmpty()) {
			throw new AppException("????????????????????????");
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
			throw new AppException("???" + node.name + "?????????????????????,????????????");
		}
		if (hitLinks.size() > 2) {
			throw new AppException("???" + node.name + "??????????????????????????????,????????????");
		}
		GraphNodeWithLink hitLink = null;
		if (hitLinks.size() == 2) {// ?????????????????????????????? ?????????????????? ??????????????????????????????
			if (isEmptyExpress(formFieldMap,hitLinks.get(0).link) && isEmptyExpress(formFieldMap,hitLinks.get(1).link)) {// ??????????????????
				throw new AppException("???" + node.name + "??????????????????????????????,????????????");
			}
			if (!isEmptyExpress(formFieldMap,hitLinks.get(0).link) && !isEmptyExpress(formFieldMap,hitLinks.get(1).link)) {// ??????????????????
				throw new AppException("???" + node.name + "??????????????????????????????,????????????");
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
					throw new AppException("??????????????????");
				}
				String value="";
				if(rule.value!=null) {
					value=rule.value.toString();
				}
				if(feild.type.equals(FormField.TYPE_TEXT_NUMBER)) {//???????????????
					express.append(feild.name);
					if("like".equals(rule.op)|| "=".equals(rule.op)){
						express.append("==");
					}else{
						express.append(rule.op);
					}
					express.append(value);
				}else {//?????????
					if("=".equals(rule.op)){//??????
						express.append(feild.name);
						express.append("==");
						express.append("'");
						express.append(value);
						express.append("'");
					}else if("like".equals(rule.op)){//??????string.contains(??????,'???')//???????????????????????????
						express.append("string.contains");
						express.append("(");
							express.append(feild.name);
							express.append(",");
							express.append("'");
							express.append(value);
							express.append("'");
						express.append(")");
					}else {//??????(?????????)
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
			throw new AppException("???????????????" + link.name + "???????????????," + e.getMessage());
		} finally {
			logger.info("AviatorEvaluator.execute({}, {})={} linkName:{}", express, DumpUtil.dump(env), ret, link.name);
		}
		if (!(ret instanceof Boolean)) {
			throw new AppException("???????????????" + link.name + "????????????????????????");
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
	 * ???????????????????????????
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

	// ????????????????????????node??????(id??????????????????)
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
	 * ????????????????????????
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
	 * ????????????????????????
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
	 * ????????????
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
		if (isOwner) {// ??????????????????
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
		// ??????????????????
		if (instance.createAccountId == account.id) {
			permission.enableCancel = true;
		}
		// ??????????????????
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
		// ???????????????
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
		if(instance.status==WorkflowInstance.STATUS_??????) {
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
	 * ??????????????????????????????
	 * 
	 * @param account
	 * @return
	 */
	private boolean isAdmin(Account account) {
		return haveCompanyPermission(account, Permission.ID_??????????????????);
	}

	/**
	 * ??????????????????
	 * 
	 * @param instance
	 * @return
	 */
	public boolean isAllSubmit(WorkflowInstance instance) {
		WorkflowInstanceOwnerQuery query = new WorkflowInstanceOwnerQuery();
		query.workflowInstanceId = instance.id;
		query.eqNodeId = instance.currNode;
		query.status = WorkflowInstanceOwner.STATUS_?????????;
		return dao.getListCount(query) == 0;
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param instance
	 */
	public void setWorkflowInstanceOwnerStatusOtherDone(WorkflowInstance instance) {
		dao.setWorkflowInstanceOwnerStatusOtherDone(instance.id);
	}

	/**
	 * ????????????fields(????????????????????????????????????????????????????????????)
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
	 * ????????????fields(????????????????????????????????????????????????????????????)
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
	 * ??????????????????????????????????????????????????????
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
	 * ??????????????????????????????????????????????????????
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
	 * ??????FormData
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
	 * ??????FormData
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
	 * ??????WorkflowChartDefine?????????
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
		if (type == WorkflowInstanceOwner.TYPE_?????????) {
			owner.status = WorkflowInstanceOwner.STATUS_?????????;
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
