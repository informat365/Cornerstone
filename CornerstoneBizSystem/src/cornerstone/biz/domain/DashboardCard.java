package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainField;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 仪表盘卡牌
 * 
 * @author 杜展扬 2019-01-15 18:03
 *
 */
@DomainDefine(domainClass = DashboardCard.class)
@DomainDefineValid(comment ="仪表盘卡牌" )
public class DashboardCard extends BaseDomain{
    //
    public static final int TYPE_数据报表 = 1;
    public static final int TYPE_数字指标 = 2;
    public static final int TYPE_项目列表 = 3;
    public static final int TYPE_日期统计 = 4;
    public static final int TYPE_迭代概览 = 5;//燃尽图
    public static final int TYPE_项目活动图 = 6;
    
    public static final int CHARTID_状态分布图 = 1;
    public static final int CHARTID_责任人分布图 = 2;
    public static final int CHARTID_创建人分布图 = 3;
    public static final int CHARTID_每日累积数量曲线 = 4;
    public static final int CHARTID_每日新增数量曲线 = 5;
    public static final int CHARTID_每日完成数量曲线 = 6;
    public static final int CHARTID_完成度 = 7;//完成数量value 总数量total  name 
    public static final int CHARTID_延期率 = 8;//延期数量value 总数量total  name 
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    @ForeignKey(domainClass=Dashboard.class)
    @DomainFieldValid(comment="仪表盘",required=true,canUpdate=true)
    public int dashboardId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64,needTrim=true)
    public String name;
    
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="DashboardCard.type")
    public int type;
    
    @ForeignKey(domainClass=Project.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int projectId;
    
    @DomainFieldValid(comment="项目ID列表",canUpdate=true)
    public List<Integer> projectIdList;
    
    @ForeignKey(domainClass=ProjectIteration.class)
    public int iterationId;
    
    @ForeignKey(domainClass=ObjectType.class)
    @DomainFieldValid(comment="对象类型",required=true,canUpdate=true)
    public int objectType;
    
    @ForeignKey(domainClass=Filter.class)
    @DomainFieldValid(comment="过滤器",required=true,canUpdate=true)
    public int filterId;
    
    @DomainFieldValid(comment="报表",canUpdate=true,dataDict="DashboardCard.chartId")
    public int chartId;
    
    @DomainFieldValid(comment="统计到日期",canUpdate=true)
    public Date dueDate;
    
    @DomainFieldValid(comment="是否删除",required=true,canUpdate=true)
    public boolean isDelete;
    
    @DomainFieldValid(comment="宽度",required=true,canUpdate=true)
    public int width;
    
    @DomainFieldValid(comment="高度",required=true,canUpdate=true)
    public int height;
    
    @DomainFieldValid(comment="x",required=true,canUpdate=true)
    public int x;
    
    @DomainFieldValid(comment="y",required=true,canUpdate=true)
    public int y;
    
    @DomainFieldValid(comment="index",required=true,canUpdate=true)
    public int index;
    
    @DomainFieldValid(comment = "图表")
    public String cardData;
    
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",required=true,canUpdate=true)
    public int updateAccountId;
    
    //
    //   
    public static class DashboardCardInfo extends DashboardCard{
    //
        @DomainField(foreignKeyFields="companyId",field="name",persistent=false)
        @DomainFieldValid(comment = "企业名称")
        public String companyName;
    
        @DomainField(foreignKeyFields="dashboardId",field="name",persistent=false)
        @DomainFieldValid(comment = "仪表盘名称")
        public String dashboardName;
    
        @DomainField(foreignKeyFields="projectId",field="name",persistent=false)
        @DomainFieldValid(comment = "项目名称")
        public String projectName;
    
        @DomainField(foreignKeyFields="objectType",field="name",persistent=false)
        @DomainFieldValid(comment = "对象类型名称")
        public String objectTypeName;
    
        @DomainField(foreignKeyFields="filterId",field="name",persistent=false)
        @DomainFieldValid(comment = "过滤器名称")
        public String filterName;
    
        @DomainField(foreignKeyFields="iterationId",field="name",persistent=false)
        @DomainFieldValid(comment = "迭代名称")
        public String iterationName;
        
        @DomainField(ignoreWhenSelect=true,persistent=false)
        @DomainFieldValid(comment = "")
        public int updatePassSecond;
    }
    //
    //   
    @QueryDefine(domainClass=DashboardCardInfo.class)
    public static class DashboardCardQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;

        public Integer dashboardId;

        public String name;

        public Integer type;

        public Integer projectId;
        
        public Integer iterationId;

        public Integer objectType;

        public Integer filterId;

        public Integer chartId;

        @QueryField(operator=">=",field="dueDate")
        public Date dueDateStart;
        
        @QueryField(operator="<=",field="dueDate")
        public Date dueDateEnd;

        public Boolean isDelete;

        public Integer width;

        public Integer height;

        public Integer x;

        public Integer y;

        public Integer index;

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
        @QueryField(operator="in",field="type")
        public int[] typeInList;
        
        @QueryField(operator="not in",field="type")
        public int[] typeNotInList;
        
        @QueryField(operator="in",field="chartId")
        public int[] chartIdInList;
        
        @QueryField(operator="not in",field="chartId")
        public int[] chartIdNotInList;
        

        //ForeignQueryFields
        @QueryField(foreignKeyFields="companyId",field="name")
        public String companyName;
        
        @QueryField(foreignKeyFields="dashboardId",field="name")
        public String dashboardName;
        
        @QueryField(foreignKeyFields="projectId",field="name")
        public String projectName;
        
        @QueryField(foreignKeyFields="objectType",field="name")
        public String objectTypeName;
        
        @QueryField(foreignKeyFields="filterId",field="name")
        public String filterName;
        
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int companyNameSort;
        public int dashboardIdSort;
        public int dashboardNameSort;
        public int nameSort;
        public int typeSort;
        public int projectIdSort;
        public int projectNameSort;
        public int objectTypeSort;
        public int objectTypeNameSort;
        public int filterIdSort;
        public int filterNameSort;
        public int chartIdSort;
        public int dueDateSort;
        public int isDeleteSort;
        public int widthSort;
        public int heightSort;
        public int xSort;
        public int ySort;
        public int indexSort;
        public int createAccountIdSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}