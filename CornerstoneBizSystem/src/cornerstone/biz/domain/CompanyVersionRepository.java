package cornerstone.biz.domain;

import java.util.Date;
import java.util.List;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

/**
 * 版本库
 *
 * @author 杜展扬 2020-04-16 13:42
 */
@DomainDefine(domainClass = CompanyVersionRepository.class)
@DomainDefineValid(comment = "版本库")
public class CompanyVersionRepository extends BaseDomain {

    public static final int STATUS_未开始 = 1;
    public static final int STATUS_运行中 = 2;
    public static final int STATUS_已关闭 = 3;

    public static final int ARCH_开放银行 = 1;
    public static final int ARCH_操作工厂 = 2;
    public static final int ARCH_管理驾驶舱 = 3;
    public static final int ARCH_工具 = 4;
    public static final int ARCH_其他 = 5;
    //
    //
    @ForeignKey(domainClass = Company.class)
    @DomainFieldValid(comment = "企业", required = true)
    public int companyId;

    @DomainFieldValid(comment = "名称", required = true, maxValue = 128)
    public String name;

    @DomainFieldValid(comment = "详细描述")
    public String description;

    @DomainFieldValid(comment = "是否删除", required = true)
    public boolean isDelete;

    @ForeignKey(domainClass = Account.class)
    @DomainFieldValid(comment = "创建人", required = true)
    public int createAccountId;

    @DomainFieldValid(comment = "更新人", required = true)
    public int updateAccountId;


    //region  通商银行新增字段 2021年7月16日17点16分
    @DomainFieldValid(comment = "最新版本")
    public String latest;

    @DomainFieldValid(comment = "系统经理")
    public List<Integer> ownerAccountIdList;

    @DomainFieldValid(comment = "系统经理")
    public List<AccountSimpleInfo> ownerAccountList;

    @DomainFieldValid(comment = "业务经理")
    public String businessLeader;

/*
    @ForeignKey(domainClass = Department.class)
    @DomainFieldValid(comment = "开发科室")
    public int ownerDepartmentId;
*/


    @DomainFieldValid(comment = "开发科室")
    public List<Integer> ownerDepartmentIdList;

    @DomainFieldValid(comment = "开发科室列表")
    public List<DepartmentSimpleInfo> ownerDepartmentList;

    @DomainFieldValid(comment = "系统主管部门")
    public String department;

    @DomainFieldValid(comment = "发布时间")
    public Date releaseDate;

    @DomainFieldValid(comment = "系统状态")
    public int status;

    @DomainFieldValid(comment = "是否arch332架构体系")
    public boolean isArch332n;

    @DomainFieldValid(comment = "架构")
    public int arch;

    //end region

    //
    //   
    public static class CompanyVersionRepositoryInfo extends CompanyVersionRepository {
        //
        @DomainField(foreignKeyFields = "createAccountId", field = "imageId", persistent = false)
        @DomainFieldValid(comment = "创建人头像")
        public String createAccountImageId;

        @DomainField(foreignKeyFields = "createAccountId", field = "name", persistent = false)
        @DomainFieldValid(comment = "创建人名称")
        public String createAccountName;

//        @DomainField(foreignKeyFields = "ownerAccountId", field = "name", persistent = false)
//        @DomainFieldValid(comment = "系统经理")
//        public String ownerAccountName;

      /*  @DomainField(foreignKeyFields = "ownerDepartmentId", field = "name", persistent = false)
        @DomainFieldValid(comment = "开发科室")
        public String ownerDepartmentName;*/

        @NonPersistent
        @DomainField(ignoreWhenSelect = true, comment = "版本列表")
        public List<CompanyVersion.CompanyVersionInfo> versionList;

    }

    //
    //   
    @QueryDefine(domainClass = CompanyVersionRepositoryInfo.class)
    public static class CompanyVersionRepositoryQuery extends BizQuery {

        public String latest;

        @QueryField(whereSql="json_contains(a.owner_account_id_list,'${ownerAccountId}')")
        public Integer ownerAccountId;

        public String businessLeader;

        public Integer ownerDepartmentId;

        public String department;

        public Date releaseDate;

        public Integer status;
        //
        public Integer id;

        public Integer arch;

        public Integer companyId;

        public String name;

        public String description;

        public Boolean isDelete;

        public Boolean isArch332n;

        public Integer createAccountId;

        public Integer updateAccountId;

        @QueryField(operator = ">=", field = "createTime")
        public Date createTimeStart;

        @QueryField(operator = "<=", field = "createTime")
        public Date createTimeEnd;

        @QueryField(operator = ">=", field = "updateTime")
        public Date updateTimeStart;

        @QueryField(operator = "<=", field = "updateTime")
        public Date updateTimeEnd;

        @QueryField(operator = ">=", field = "releaseDate")
        public Date releaseDateStart;

        @QueryField(operator = "<=", field = "releaseDate")
        public Date releaseDateEnd;

        //in or not in fields
//        @QueryField(operator = "in",field = "ownerDepartmentId")
        public int[] ownerDepartmentIds;

//        @QueryField(whereSql="json_contains(a.owner_account_id_list,'${ownerAccountId}')")
        public int[] ownerAccountIds;

        //ForeignQueryFields
        @QueryField(foreignKeyFields = "createAccountId", field = "imageId")
        public String createAccountImageId;

        @QueryField(foreignKeyFields = "createAccountId", field = "name")
        public String createAccountName;

        //inner joins
        //sort
        public int idSort;
        public int companyIdSort;
        public int nameSort;
        public int descriptionSort;
        public int isDeleteSort;
        public int createAccountIdSort;
        public int createAccountImageIdSort;
        public int createAccountNameSort;
        public int updateAccountIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}