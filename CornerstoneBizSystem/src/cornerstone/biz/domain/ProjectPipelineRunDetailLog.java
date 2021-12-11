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
 * Pipeline运行
 * 
 * @author 杜展扬 2018-08-09
 *
 */
@DomainDefine(domainClass = ProjectPipelineRunDetailLog.class)
@DomainDefineValid(comment ="Pipeline运行详情" )
public class ProjectPipelineRunDetailLog extends BaseDomain{
    //
    public static final int TYPE_输出 = 1;
    public static final int TYPE_错误 = 2;
    public static final int TYPE_调试 = 3;
    public static final int TYPE_ARTIFACT = 4;
    public static final int TYPE_二维码 = 5;
    //
    @ForeignKey(domainClass=ProjectPipelineRunLog.class)
    @DomainFieldValid(comment="项目",required=true,canUpdate=true)
    public int runLogId;
    
    @DomainFieldValid(comment="类型",canUpdate=true,dataDict="ProjectPipelineRunDetailLog.type")
    public int type;
    
    @DomainFieldValid(comment="消息",canUpdate=true)
    public String message;
    
    @DomainFieldValid(comment="节点",canUpdate=true,maxValue=128)
    public String node;
    
    @DomainFieldValid(comment="stage",canUpdate=true,maxValue=128)
    public String stage;
    
    @DomainFieldValid(comment="阶段",canUpdate=true,maxValue=128)
    public String step;
    
    @DomainFieldValid(comment="开始时间",canUpdate=true)
    public Date startTime;
    
    @DomainFieldValid(comment="结束时间",canUpdate=true)
    public Date endTime;
    
    @DomainFieldValid(comment="备注",canUpdate=true,maxValue=512)
    public String remark;
    
    //
    //   
    public static class ProjectPipelineRunDetailLogInfo extends ProjectPipelineRunDetailLog{
    //

    }
    //
    //   
    @QueryDefine(domainClass=ProjectPipelineRunDetailLogInfo.class)
    public static class ProjectPipelineRunDetailLogQuery extends BizQuery{
        //
        public Integer id;

        public Integer runLogId;

        public Integer type;

        public String message;

        public String node;

        public String stage;

        public String step;
        
        @QueryField(operator=">=",field="id")
        public Integer startId;
        
        @QueryField(operator="<=",field="id")
        public Integer endId;

        @QueryField(operator=">=",field="startTime")
        public Date startTimeStart;
        
        @QueryField(operator="<=",field="startTime")
        public Date startTimeEnd;

        @QueryField(operator=">=",field="endTime")
        public Date endTimeStart;
        
        @QueryField(operator="<=",field="endTime")
        public Date endTimeEnd;

        public String remark;

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
        public int runLogIdSort;
        public int typeSort;
        public int messageSort;
        public int nodeSort;
        public int stageSort;
        public int stepSort;
        public int startTimeSort;
        public int endTimeSort;
        public int remarkSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}