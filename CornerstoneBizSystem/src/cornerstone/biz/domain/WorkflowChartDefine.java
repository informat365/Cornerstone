package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import cornerstone.biz.util.BeanUtil;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 流程流转
 * 
 * @author 杜展扬 2019-06-21 17:05
 *
 */
@DomainDefine(domainClass = WorkflowChartDefine.class)
@DomainDefineValid(comment ="流程流转" )
public class WorkflowChartDefine extends BaseDomain{
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=WorkflowDefine.class)
    @DomainFieldValid(comment="流程",required=true,canUpdate=true)
    public int workflowDefineId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="版本",required=true)
    public int version;
    
    @DomainFieldValid(comment="流转图",canUpdate=true)
    public String graph;
    
    @DomainFieldValid(comment="属性",canUpdate=true)
    public String props;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    public static class Graph{
		public List<GraphNode> nodes;
    		public List<GraphLink> links;
    }
    public static class GraphTree{
		public GraphNodeInfo startNode;
		public Map<String,GraphNodeInfo> nodeMap;//所有节点
		public List<GraphNodeInfo> nodes;
		public List<GraphLinkInfo> links;
		//
		public GraphTree() {
			nodeMap=new HashMap<>();
		}
    }
    //
    public static class GraphNode{
    		//
    		public static final String TYPE_NODE_START="nodeStart";
    		public static final String TYPE_NODE_END="nodeEnd";
    		public static final String TYPE_NODE_EVENT="nodeEvent";
    		public static final String TYPE_NODE_AUTO="nodeAuto";
    		//
    		public String id;//":"59232ee4-184c-2eba-0b5c-4e0b5466381c",
    		public String name;//":"开始",
    		public String type;//":"nodeStart"
    }
    //
    public static class GraphNodeInfo extends GraphNode{//加工后的
		public GraphProperty nodeProperty;//节点属性可能是空 计算出来
		public List<GraphNodeWithLink> targetNodes;//流转节点列表 计算出来
		//
		public GraphNodeInfo() {
			nodeProperty=new GraphProperty();
			targetNodes=new ArrayList<>();
		}
		//
		public static GraphNodeInfo create(GraphNode node) {
			return BeanUtil.copyTo(GraphNodeInfo.class, node);
		}
		//
		public static List<GraphNodeInfo> createList(List<GraphNode> nodes) {
			List<GraphNodeInfo> list=new ArrayList<>();
			for (GraphNode e : nodes) {
				list.add(create(e));
			}
			return list;
		}
    	}
    //
    public static class GraphNodeWithLink{
    		public GraphNodeInfo node;
    		public GraphLinkInfo link;
    }
    //
    public static class GraphLink{
	    	public String id;//":"87669679-d139-5b47-2ef2-45e79d87d8ea",
	    	public String name;//":"流转",
	    	public String source;//":"8d8ba1d8-8e70-3ca5-2369-6e2cd7bf13d1",
	    	public String target;//":"98a27731-fe60-a2f4-9438-217e480b6b03"
    }
    //
    public static class GraphLinkInfo extends GraphLink{
    		public GraphProperty linkProperty;//节点属性可能是空 计算出来 
    		//
    		public static GraphLinkInfo create(GraphLink link) {
    			return BeanUtil.copyTo(GraphLinkInfo.class, link);
    		}
    		//
    		public static List<GraphLinkInfo> createList(List<GraphLink> links) {
    			List<GraphLinkInfo> list=new ArrayList<>();
    			for (GraphLink e : links) {
    				list.add(create(e));
    			}
    			return list;
    		}
    }
    //
    public static class NodeFormField{
		public String id;//":"e7c1e7ef-7d6d-4693-abdd-de45d7e86d23",
		public String name;//":"单行文本",
		public Boolean visible;//":true,
		public Boolean editable;//":true
    }
    //
    public static class GraphUser{
    	public int id;
    	public String name;
    }
    //
    public static class GraphDepartment{
    		public int id;
		public String name;
    }
    //
    public static class GraphCompanyRole{
    		public int id;
		public String name;
    }
    //
    public static class GraphProjectRole{
		public String id;//projectid-roleid
		public String name;
    }
    //
    public static class GraphAccounts{
    		public List<GraphUser> userList;////指定人
		public List<GraphDepartment> departmentList;//部门下所有成员
		public List<GraphDepartment> departmentOwnerList;//部门负责人
		public List<GraphCompanyRole> companyRoleList;//公司角色
		public List<GraphProjectRole> projectRoleList;//项目角色
		public List<String> formItemList;//
		public boolean submitter;//":false,
		//
		public GraphAccounts() {
			userList=new ArrayList<>();
			departmentList=new ArrayList<>();
			departmentOwnerList=new ArrayList<>();
			companyRoleList=new ArrayList<>();
			projectRoleList=new ArrayList<>();
			formItemList=new ArrayList<>();
		}
    }
    //
    public static class GraphProperty{
    		//
    		public static final String FORWARD_RULE_ANY="any";
    		public static final String FORWARD_RULE_ALL="all";
    		public static final String FORWARD_RULE_NEXT="next";
    		public static final String FORWARD_RULE_WAIT="wait";
    		//
    		public static final String ACTION_EMAIL="email";
    		public static final String ACTION_WEBAPI="webApi";
    		//
    		public String id;//":"8d8ba1d8-8e70-3ca5-2369-6e2cd7bf13d1",
    		public List<NodeFormField> fieldList;//":[],
    		public boolean enableBackword;//":false,
    		public List<String> backwordNodeList;//":[],
    		public boolean enableSave;//":false,
    		public boolean enableTerminal;//":false,
    		public String forwardRule;//":"any","all"
    		public GraphAccounts owner;//":[],
    		public GraphAccounts cc;//":[],
    		public boolean enableTransferTo;//
    		public String submitButtonText;
    		public String terminalButtonText;
    		//表达式
    		public String express;
    		public String ruleType;//or and
    		public List<GraphRule> ruleList;
    		//
    		public String action;//email webApi
    		public EmailSetting emailSetting;//TODO
    		public WebApiSetting webApiSetting;//TODO
    }
    //
    public static class EmailSetting{
    		public List<String> title;
    		public List<String> to;
    		public List<String> cc;
    		public List<String> content;
    }
    //
    public static class WebApiSetting{
    		public String url;
    }
    //
    public static class GraphRule{
    		public String field;//":"4cb73f4c-7091-4fdb-997d-04180a8c7ffc",
    		public String op;//":"<=",
    		public Object value;//":"3"
    }
    //   
    public static class WorkflowChartDefineInfo extends WorkflowChartDefine{
    //
        @DomainField(foreignKeyFields="createAccountId",field="imageId",persistent=false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;
    
        @DomainField(foreignKeyFields="createAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;
    

    }
    //
    //   
    @QueryDefine(domainClass=WorkflowChartDefineInfo.class)
    public static class WorkflowChartDefineQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;
        
        public Integer workflowDefineId;

        public String name;

        public String graph;

        public String props;

        public Integer createAccountId;

        public Integer updateAccountId;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="createAccountId",field="imageId")
        public String createAccountImageId;
        
        @QueryField(foreignKeyFields="createAccountId",field="name")
        public String createAccountName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int workflowDefineIdSort;
        public int nameSort;
        public int graphSort;
        public int propsSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}