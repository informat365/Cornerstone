package cornerstone.biz.domain;

import java.util.Date;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.DomainDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryDefine;
import jazmin.driver.jdbc.smartjdbc.annotations.QueryField;
/**
 * 备份
 * 
 * @author 杜展扬 2018-09-06 17:40
 *
 */
@DomainDefine(domainClass = Backup.class)
@DomainDefineValid(comment ="备份" )
public class Backup extends BaseDomain{
    //
    public static final int TYPE_cmdb机器人 = 1;
    public static final int TYPE_cmdbApi = 2;
    public static final int TYPE_pipeline = 3;
    //
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="Backup.type")
    public int type;
    
    @DomainFieldValid(comment="关联ID",required=true,canUpdate=true)
    public int associateId;
    
    @DomainFieldValid(comment="名称",required=true,canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="数据",canUpdate=true)
    public String content;
    
    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;
    
    //
    //   
    public static class BackupInfo extends Backup{
    //

    }
    //
    //   
    @QueryDefine(domainClass=BackupInfo.class)
    public static class BackupQuery extends BizQuery{
        //
        public Integer id;

        public Integer type;

        public Integer associateId;

        public String name;

        public String content;

        public Integer createAccountId;

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
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int typeSort;
        public int associateIdSort;
        public int nameSort;
        public int contentSort;
        public int createAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}