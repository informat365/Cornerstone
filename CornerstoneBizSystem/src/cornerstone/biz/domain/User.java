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
 * boss后台用户
 * 
 * @author 杜展扬 2019-09-21 14:14
 *
 */
@DomainDefine(domainClass = User.class)
@DomainDefineValid(comment ="boss后台用户" ,uniqueKeys={@UniqueKey(fields={"userName"})})
public class User extends BaseDomain{
    //
	public static final int STATUS_有效=1;
	public static final int STATUS_无效=2;
    //
    @DomainFieldValid(comment="用户名",required=true,canUpdate=true,maxValue=64)
    public String userName;
    
    @DomainFieldValid(comment="密码",canUpdate=true,maxValue=32)
    public String password;
    
    @DomainFieldValid(comment="姓名",canUpdate=true,maxValue=32)
    public String name;
    
    @DomainFieldValid(comment="type",canUpdate=true)
    public int type;
    
    @DomainFieldValid(comment="手机号",canUpdate=true,maxValue=32)
    public String mobileNo;
    
    @DomainFieldValid(comment="邮箱",canUpdate=true,maxValue=64)
    public String email;
    
    @DomainFieldValid(comment="头像",canUpdate=true,maxValue=64)
    public String imageId;
    
    @DomainFieldValid(comment="性别",canUpdate=true,maxValue=8)
    public String gender;
    
    @DomainFieldValid(comment="状态",required=true,canUpdate=true)
    public int status;
    
    @DomainFieldValid(comment="部门id",canUpdate=true)
    public int departmentId;
    
    @DomainFieldValid(comment="部门名称",canUpdate=true,maxValue=64)
    public String departmentName;
    
    @DomainFieldValid(comment="职位",canUpdate=true,maxValue=64)
    public String position;
    
    @DomainFieldValid(comment="最后登录时间",canUpdate=true)
    public Date lastLoginTime;
    
    @DomainFieldValid(comment="最后登录ip",canUpdate=true,maxValue=64)
    public String lastLoginIp;
    
    @DomainFieldValid(comment="职员id",canUpdate=true)
    public int staffId;
    
    @ForeignKey(domainClass=User.class)
    @DomainFieldValid(comment="创建人id")
    public int createUserId;
    
    @DomainFieldValid(comment="更新人id")
    public int updateUserId;
    
    //
    //   
    public static class UserInfo extends User{
    //

    }
    //
    //   
    @QueryDefine(domainClass=UserInfo.class)
    public static class UserQuery extends BizQuery{
        //
        public Integer id;

        public String userName;

        public String password;

        public String name;

        public Integer type;

        public String mobileNo;

        public String email;

        public String imageId;

        public String gender;

        public Integer status;

        public Integer departmentId;

        public String departmentName;

        public String position;

        @QueryField(operator=">=",field="lastLoginTime")
        public Date lastLoginTimeStart;
        
        @QueryField(operator="<=",field="lastLoginTime")
        public Date lastLoginTimeEnd;

        public String lastLoginIp;

        public Integer staffId;

        public Integer createUserId;

        public Integer updateUserId;

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
        public int userNameSort;
        public int passwordSort;
        public int nameSort;
        public int typeSort;
        public int mobileNoSort;
        public int emailSort;
        public int imageIdSort;
        public int genderSort;
        public int statusSort;
        public int departmentIdSort;
        public int departmentNameSort;
        public int positionSort;
        public int lastLoginTimeSort;
        public int lastLoginIpSort;
        public int staffIdSort;
        public int createUserIdSort;
        public int updateUserIdSort;
        public int createTimeSort;
        public int updateTimeSort;
    }

}