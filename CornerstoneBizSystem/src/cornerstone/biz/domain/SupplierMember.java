package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 供应商人员
 */
@DomainDefine(domainClass = SupplierMember.class)
@DomainDefineValid(comment = "供应商人员")
public class SupplierMember extends BaseDomain {

    public static final int LEVEL_初级=1;
    public static final int LEVEL_中级=2;
    public static final int LEVEL_高级=3;
    public static final int LEVEL_助理级=4;
    public static final int LEVEL_实习=5;
    public static final int LEVEL_临时协助=6;


    public static final int STATUS_在职=1;
    public static final int STATUS_离场=2;
    public static final int STATUS_暂时离场=3;
    public static final int STATUS_偶尔支持=4;

    public static final int POSITION_开发=1;
    public static final int POSITION_测试=2;

    //
    //
    @DomainFieldValid(comment = "成员", required = true, canUpdate = true)
    public int accountId;
    //
    @ForeignKey(domainClass = DingtalkMember.class)
    @DomainFieldValid(comment = "钉钉账号", canUpdate = true)
    public int dingtalkMemberId;

    @DomainFieldValid(comment = "企业", canUpdate = true)
    public int companyId;

    @ForeignKey(domainClass = Supplier.class)
    @DomainFieldValid(comment = "供应商", required = true, canUpdate = true)
    public int supplierId;

    @DomainFieldValid(comment = "科室",  canUpdate = true)
    public int departmentId;

    @DomainFieldValid(comment = "小组",  canUpdate = true)
    public int teamId;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "项目经理",  canUpdate = true)
    public int leaderAccountId;

    @DomainFieldValid(comment = "姓名", required = true, canUpdate = true,maxValue = 32)
    public String name;

    @DomainFieldValid(comment = "岗位",  canUpdate = true,maxValue = 32)
    public int position;

    @DomainFieldValid(comment = "工号", canUpdate = true, maxValue = 32)
    public String code;

    @DomainFieldValid(comment = "工作地点", canUpdate = true, maxValue = 128)
    public String baseStation;

    @DomainFieldValid(comment = "工位号", canUpdate = true, maxValue = 32)
    public String baseStationCode;

    @DomainFieldValid(comment = "手机号", canUpdate = true, maxValue = 16)
    public String mobile;

    @DomainFieldValid(comment = "邮箱", canUpdate = true, maxValue = 32)
    public String email;

    @DomainFieldValid(comment = "身份证", canUpdate = true, maxValue = 32)
    public String idCard;

    @DomainFieldValid(comment = "产品库(关联版本库)", canUpdate = true, maxValue = 256)
    public List<Integer> productIds;

    @DomainFieldValid(comment = "门禁卡", canUpdate = true, maxValue = 64)
    public boolean entryCard;

    @DomainFieldValid(comment = "承诺书", canUpdate = true, maxValue = 512)
    public boolean promiseDesc;

    @DomainFieldValid(comment = "状态", canUpdate = true)
    public int status;

    @DomainFieldValid(comment = "工作状态", canUpdate = true)
    public int workStatus;

    @DomainFieldValid(comment = "级别", canUpdate = true)
    public int level;

    @DomainFieldValid(comment = "入场时间", canUpdate = true)
    public Date entryTime;

    @DomainFieldValid(comment = "离场时间", canUpdate = true)
    public Date leaveTime;

    @DomainFieldValid(comment = "籍贯", canUpdate = true, maxValue = 32)
    public String nativePlace;

    @DomainFieldValid(comment = "学历", canUpdate = true)
    public String education;

    @DomainFieldValid(comment = "毕业院校", canUpdate = true, maxValue = 64)
    public String graduation;

    @DomainFieldValid(comment = "创建人",  canUpdate = true)
    public int createUserId;

    @DomainFieldValid(comment = "更新人",  canUpdate = true)
    public int updateUserId;


    @DomainFieldValid(comment = "创建时间", canUpdate = true)
    public Date createTime;

    @DomainFieldValid(comment = "更新时间", canUpdate = true)
    public Date updateTime;

    public static class SupplierMemberInfo extends SupplierMember {

        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<Project.ProjectInfo> projectList;

        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<DepartmentSimpleInfo> departmentList;

        @DomainField(foreignKeyFields="supplierId",field="name",persistent=false)
        @DomainFieldValid(comment = "供应商名称")
        public String supplierName;

        @DomainField(foreignKeyFields="leaderAccountId",field="name",persistent=false)
        @DomainFieldValid(comment = "项目经理姓名")
        public String leaderAccountName;

        @DomainField(foreignKeyFields="dingtalkMemberId",field="name",persistent=false)
        @DomainFieldValid(comment = "钉钉账号")
        public String dingtalkMemberName;

        @DomainField(foreignKeyFields="dingtalkMemberId",field="dingtalkId",persistent=false)
        @DomainFieldValid(comment = "钉钉账号")
        public String dingtalkId;

        @DomainField(foreignKeyFields="dingtalkMemberId",field="mobileNo",persistent=false)
        @DomainFieldValid(comment = "钉钉账号")
        public String dingtalkMobileNo;

        @DomainField(foreignKeyFields="dingtalkMemberId",field="jobNumber",persistent=false)
        @DomainFieldValid(comment = "钉钉账号")
        public String dingtalkJobNumber;

        @DomainField(ignoreWhenSelect=true,persistent=false)
        public List<CompanyVersionRepository> repositoryList;

    }

    //
    //   
    @QueryDefine(domainClass = SupplierMemberInfo.class)
    public static class SupplierMemberQuery extends BizQuery {


        @InnerJoin(table1Field="accountId",table2=Department.class,table2Field="accountId",
                table1Fields={"companyId"},table2Fields= {"companyId"})
        @QueryField(field="parentId")
        public Integer departmentId;
        //
        public Integer id;

        public Integer accountId;
        public Integer supplierId;
        public Integer teamId;
        public Integer leaderAccountId;
        public Integer position;
        public Integer status;
        public Integer workStatus;

        public String name;
        public String code;
        public String baseStation;
        public String baseStattionCode;
        public String mobile;
        public String email;
        public String idCard;
        public Boolean entryCard;
        public String graduation;
        public String nativePlace;
        public Boolean promiseDesc;

        public Integer level;
        public Integer education;


        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        @QueryField(operator = ">=", field = "entryTime")
        public Date entryTimeStart;

        @QueryField(operator = "<=", field = "entryTime")
        public Date entryTimeEnd;

        @QueryField(operator = ">=", field = "leaveTime")
        public Date leaveTimeStart;

        @QueryField(operator = "<=", field = "leaveTime")
        public Date leaveTimeEnd;

        //in or not in fields

        //inner joins
        //sort
        public int idSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}