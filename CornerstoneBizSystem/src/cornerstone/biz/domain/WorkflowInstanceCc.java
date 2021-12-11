//package cornerstone.biz.domain;
//
//import java.util.Date;
//
//import cornerstone.biz.annotations.DomainDefineValid;
//import cornerstone.biz.annotations.DomainFieldValid;
//import cornerstone.biz.domain.query.BizQuery;
//import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
//import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
//import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
//import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
//import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
///**
// * 流程实例抄送人
// * 
// * @author 杜展扬 2019-06-24 15:50
// *
// */
//@DomainDefine(domainClass = WorkflowInstanceCc.class)
//@DomainDefineValid(comment ="流程实例抄送人" )
//public class WorkflowInstanceCc extends BaseDomain{
//    //
//	@ForeignKey(domainClass=Company.class)
//    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
//    public int companyId;
//	
//    @ForeignKey(domainClass=WorkflowInstance.class)
//    @DomainFieldValid(comment="流程实例",required=true,canUpdate=true)
//    public int workflowInstanceId;
//    
//    @DomainFieldValid(comment="节点",required=true,canUpdate=true,maxValue=80)
//    public String nodeId;
//    
//    @DomainFieldValid(comment="节点名称",canUpdate=true,maxValue=128)
//    public String nodeName;
//    
//    @ForeignKey(domainClass=Account.class)
//    @DomainFieldValid(comment="责任人",required=true,canUpdate=true)
//    public int ccAccountId;
//    
//    @ForeignKey(domainClass=Account.class)
//    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
//    public int createAccountId;
//    
//    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
//    public int updateAccountId;
//    
//    //
//    //   
//    public static class WorkflowInstanceCcInfo extends WorkflowInstanceCc{
//    //
//        @DomainField(foreignKeyFields="ccAccountId",field="imageId",persistent=false)
//        @DomainFieldValid(comment = "责任人头像")
//        public String ccAccountImageId;
//    
//        @DomainField(foreignKeyFields="ccAccountId",field="name",persistent=false)
//        @DomainFieldValid(comment = "责任人名称")
//        public String ccAccountName;
//    }
//    //
//    public static class WorkflowInstanceCcSimpleInfo {
//		
//		public int ccAccountId;
//
//		public String ccAccountName;
//
//		public String ccAccountImageId;
//	}
//    //
//    //   
//    @QueryDefine(domainClass=WorkflowInstanceCcInfo.class)
//    public static class WorkflowInstanceCcQuery extends BizQuery{
//        //
//        public Integer id;
//        
//        public Integer companyId;
//
//        public Integer workflowInstanceId;
//
//        @QueryField(operator="=",field="nodeId")
//        public String eqNodeId;
//
//        public String nodeName;
//
//        public Integer ccAccountId;
//
//        public Integer createAccountId;
//
//        public Integer updateAccountId;
//
//        @QueryField(operator=">=",field="createTime")
//        public Date createTimeStart;
//        
//        @QueryField(operator="<=",field="createTime")
//        public Date createTimeEnd;
//
//        @QueryField(operator=">=",field="updateTime")
//        public Date updateTimeStart;
//        
//        @QueryField(operator="<=",field="updateTime")
//        public Date updateTimeEnd;
//
//        //in or not in fields
//
//        //ForeignQueryFields
//        @QueryField(foreignKeyFields="ccAccountId",field="imageId")
//        public String ccAccountImageId;
//        
//        @QueryField(foreignKeyFields="ccAccountId",field="name")
//        public String ccAccountName;
//        
//        //inner joins
//        //sort
//        public int idSort;
//        public int workflowInstanceIdSort;
//        public int nodeIdSort;
//        public int nodeNameSort;
//        public int ccAccountIdSort;
//        public int ccAccountImageIdSort;
//        public int ccAccountNameSort;
//        public int createAccountIdSort;
//        public int updateAccountIdSort;
//        public int createTimeSort;
//        public int updateTimeSort;
//    }
//
//}