package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;

/**
 * 钉钉考勤
 */
@DomainDefine(domainClass = DingtalkAttendance.class)
@DomainDefineValid(comment = "钉钉考勤")
public class DingtalkAttendance extends BaseDomain {

    public static final int SOURCE_指纹人脸=1;
    public static final int SOURCE_IBeacon=2;
    public static final int SOURCE_考勤机蓝牙=3;
    public static final int SOURCE_用户打卡=4;
    public static final int SOURCE_老板改签=5;
    public static final int SOURCE_审批系统=6;
    public static final int SOURCE_考勤系统=7;
    public static final int SOURCE_自动打卡=8;

    public static final int RESULT_正常=1;
    public static final int RESULT_早退=2;
    public static final int RESULT_迟到=3;
    public static final int RESULT_严重迟到=4;
    public static final int RESULT_旷工迟到=5;
    public static final int RESULT_未打卡=6;


    public static final int LOCATION_范围内=1;
    public static final int LOCATION_范围外=2;
    public static final int LOCATION_未打卡=3;

    @DomainFieldValid(comment = "成员",  canUpdate = true)
    public int accountId;

    @ForeignKey(domainClass = DingtalkMember.class)
    @DomainFieldValid(comment = "成员", required = true,canUpdate = true)
    public int dingtalkMemberId;

    @DomainFieldValid(comment = "钉钉成员ID", required = true, canUpdate = true)
    public String dingtalkId;

    @DomainFieldValid(comment = "考勤方式", canUpdate = true)
    public int sourceType;

    @DomainFieldValid(comment = "上午考勤基准时间", canUpdate = true)
    public Date amBaseTime;

    @DomainFieldValid(comment = "上午打卡时间", canUpdate = true)
    public Date amUserTime;

    @DomainFieldValid(comment = "上午打卡结果", canUpdate = true)
    public int amTimeResult;

    @DomainFieldValid(comment = "上午打卡范围", canUpdate = true)
    public int amLocation;


    @DomainFieldValid(comment = "下午考勤基准时间", canUpdate = true)
    public Date pmBaseTime;

    @DomainFieldValid(comment = "下午打卡时间", canUpdate = true)
    public Date pmUserTime;

    @DomainFieldValid(comment = "下午考勤结果", canUpdate = true)
    public int pmTimeResult;

    @DomainFieldValid(comment = "下午打卡范围", canUpdate = true)
    public int pmLocation;

    @DomainFieldValid(comment = "工作日", canUpdate = true)
    public Date workDate;

    @DomainFieldValid(comment = "更新时间", canUpdate = true)
    public Date createTime;

    @DomainFieldValid(comment = "更新时间", canUpdate = true)
    public Date updateTime;

    public static class DingtalkAttendanceInfo extends DingtalkAttendance {

        @DomainField(foreignKeyFields="dingtalkMemberId",field="name",persistent=false)
        @DomainFieldValid(comment = "钉钉名称")
        public String dingtalkName;

    }

    //
    //
    @QueryDefine(domainClass = DingtalkAttendanceInfo.class)
    public static class DingtalkAttendanceQuery extends BizQuery {


        //
        public Integer id;

        public Integer accountId;

        public Integer sourceType;

        public String dingtalkId;


        public String workDate;

        @QueryField(foreignKeyFields = "dingtalkMemberId",field = "name")
        public String dingtalkName;


        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        @QueryField(operator = ">=", field = "workDate")
        public Date workDateStart;

        @QueryField(operator = "<=", field = "workDate")
        public Date workDateEnd;

        //in or not in fields

        //inner joins
        //sort
        public int idSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}