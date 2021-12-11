package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 任务序列号
 * 
 * @author 杜展扬 2018-08-01
 *
 */
@DomainDefine(domainClass = TaskSerialNo.class)
@DomainDefineValid(comment ="任务序列号" ,uniqueKeys={@UniqueKey(fields={"projectId","objectType"})})
public class TaskSerialNo extends BaseDomain{
    //
    //
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",required=true,canUpdate=true)
    public int companyId;
    
    public String objectTypeSystemName;
    
    @DomainFieldValid(comment="序列号",required=true,canUpdate=true)
    public int serialNo;
    
    //
    //   
    public static class TaskSerialNoInfo extends TaskSerialNo{
    //

    }
    //
    //   
    @QueryDefine(domainClass=TaskSerialNoInfo.class)
    public static class TaskSerialNoQuery extends BizQuery{
        //
        public Integer id;

        public Integer companyId;
        
        @QueryField(field = "objectTypeSystemName",operator = "=")
        public String eqObjectTypeSystemName;

        public Integer serialNo;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int serialNoSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}