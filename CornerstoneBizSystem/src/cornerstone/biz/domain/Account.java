package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.domain.Role.RoleInfo;
import cornerstone.biz.domain.query.BizQuery;
import jazmin.driver.jdbc.smartjdbc.annotations.*;

import java.util.Date;
import java.util.List;
/**
 * 账号
 * 
 * @author 杜展扬 2018-07-28
 *
 */
@DomainDefine(domainClass = Account.class)
@DomainDefineValid(comment ="账号" ,uniqueKeys=
{@UniqueKey(fields={"mobileNo"}),@UniqueKey(fields={"userName"})})
public class Account extends BaseDomain{
    //
    public static final int STATUS_有效 = 1;
    public static final int STATUS_无效 = 2;

    public static final int BOSS_企业_读写 =1;
    public static final int BOSS_企业_只读 =2;
    public static final int BOSS_部门_读写 =3;
    public static final int BOSS_部门_只读 =4;
    //
    @DomainFieldValid(comment="UUID",canUpdate=true,maxValue=64)
    public String uuid;
    
    public String calUuid;
    
    @DomainFieldValid(comment="是否激活",canUpdate=true)
    public boolean isActivated;
    
    @ForeignKey(domainClass=Company.class)
    @DomainFieldValid(comment="企业",canUpdate=true)
    public int companyId;

    @ForeignKey(domainClass = Supplier.class)
    @DomainFieldValid(comment="供应商",canUpdate=true)
    public int supplierId;

    @DomainFieldValid(comment="名称",canUpdate=true,maxValue=64)
    public String name;
    
    @DomainFieldValid(comment="用户名",required=true,canUpdate=true,maxValue=64)
    public String userName;
    

    @DomainFieldValid(comment="域账号用户名",canUpdate=true,maxValue=64)
    public String adName;

//    @ForeignKey(domainClass = DingtalkMember.class)
    @DomainFieldValid(comment="钉钉用户ID",canUpdate=true,maxValue=64)
    public int dingtalkMemberId;

    @DomainFieldValid(comment="拼音名称",canUpdate=true,maxValue=128)
    public String pinyinName;
    
    @DomainFieldValid(comment="手机号",canUpdate=true,maxValue=32)
    public String mobileNo;
    
    @DomainFieldValid(comment="邮箱",canUpdate=true,maxValue=128)
    public String email;
    
    @DomainFieldValid(comment="密码",canUpdate=true,maxValue=128)
    public String password;
    
    @DomainFieldValid(comment="3DES加密密码",canUpdate=true,maxValue=128)
    public String encryptPassword;
    
    @DomainFieldValid(comment="登录凭证",canUpdate=true,maxValue=64)
    public String accessToken;
    
    @DomainFieldValid(comment="微信openId",canUpdate=true,maxValue=64)
    public String wxOpenId;
    
    @DomainFieldValid(comment="unionId",canUpdate=true,maxValue=128)
    public String wxUnionId;
    
    @DomainFieldValid(comment="昵称",canUpdate=true,maxValue=64)
    public String wxNickname;
    
    @DomainFieldValid(comment="小程序openid",canUpdate=true,maxValue=64)
    public String xcxOpenId;
    
    @DomainFieldValid(comment="sessionKey",canUpdate=true,maxValue=64)
    public String xcxSessionKey;
    
    @DomainFieldValid(comment="性别",canUpdate=true)
    public int sex;
    
    @DomainFieldValid(comment="微信头像",canUpdate=true,maxValue=256)
    public String headimgurl;
    
    @DomainFieldValid(comment="省份",canUpdate=true,maxValue=255)
    public String province;
    
    @DomainFieldValid(comment="城市",canUpdate=true,maxValue=255)
    public String city;
    
    @DomainFieldValid(comment="头像",canUpdate=true,maxValue=64)
    public String imageId;
    
    @DomainFieldValid(comment="状态",canUpdate=true,dataDict="Account.status")
    public int status;
    
    @DomainFieldValid(comment="最后登录时间",canUpdate=true)
    public Date lastLoginTime;
    
    @DomainFieldValid(comment="创建人",canUpdate=true)
    public int createAccountId;
    
    @DomainFieldValid(comment="更新人",canUpdate=true)
    public int updateAccountId;
    
    @DomainFieldValid(comment="是否需要强制更新密码",canUpdate=true)
    public boolean needUpdatePassword;
    
    @DomainFieldValid(comment="每日登陆失败次数",canUpdate=true)
    public int dailyLoginFailCount;
    
    @DomainFieldValid(comment="飞书openId",canUpdate=true)
    public String larkOpenId;
    
    @DomainFieldValid(comment="飞书TenantKey",canUpdate=true)
    public String larkTenantKey;
    
    @DomainFieldValid(comment="解锁时间",canUpdate=true)
    public Date disableEndTime;
    
    @DomainFieldValid(comment="手机验证码输错次数",canUpdate=true)
    public int kaptchaErrorCount;
    
    @DomainFieldValid(comment="企业微信UserId",canUpdate=true)
    public String qywxUserId;

    @DomainFieldValid(comment="钉钉UserId",canUpdate=true)
    public String dingtalkUserId;

    @DomainFieldValid(comment="注册ip地址",canUpdate=true)
    public String registerIp;

    @DomainFieldValid(comment="超级boss权限",canUpdate=true)
    public int superBoss;

    @DomainFieldValid(comment="密码",canUpdate=true,maxValue=128)
    public String lastPassword;

    @DomainFieldValid(comment="密码",canUpdate=true,maxValue=128)
    public String beforeLastPassword;

    //
    //   
    public static class AccountInfo extends Account{
    		//
    	 	@DomainField(ignoreWhenSelect=true,persistent=false)
    		public List<RoleInfo> roles;
    }
    //
    //   
    @QueryDefine(domainClass=AccountInfo.class)
    public static class AccountQuery extends BizQuery{
        //
        public Integer id;

        public String uuid;

        public Boolean isActivated;

        public Integer companyId;

        public Integer supplierId;

        @QueryField(field="name")
        public String supplierName;

        public Integer dingtalkMemberId;

        public String name;
        
        @QueryField(operator="=",field="name")
        public String eqName;
        
        public String pinyinName;

        public String mobileNo;

        public String email;

        public String password;

        public String wxOpenId;

        public String wxUnionId;

        public String wxNickname;

        public String xcxOpenId;

        public String xcxSessionKey;

        public Integer sex;

        public String headimgurl;

        public String province;

        public String city;

        public String imageId;

        public Integer status;
        
        public String larkOpenId;
        
        public String larkTenantKey;

        @QueryField(operator=">=",field="lastLoginTime")
        public Date lastLoginTimeStart;
        
        @QueryField(operator="<=",field="lastLoginTime")
        public Date lastLoginTimeEnd;

        @QueryField(operator=">=",field="createTime")
        public Date createTimeStart;
        
        @QueryField(operator="<=",field="createTime")
        public Date createTimeEnd;

        @QueryField(operator=">=",field="updateTime")
        public Date updateTimeStart;
        
        @QueryField(operator="<=",field="updateTime")
        public Date updateTimeEnd;

        //in or not in fields
        @QueryField(operator="in",field="companyId")
        public int[] companyIdInList;
        
        @QueryField(operator="in",field="status")
        public int[] statusInList;
        
        @QueryField(operator="not in",field="status")
        public int[] statusNotInList;
        

        //ForeignQueryFields
        //inner joins
        //sort
        public int idSort;
        public int uuidSort;
        public int isActivatedSort;
        public int companyIdSort;
        public int nameSort;
        public int pinyinNameSort;
        public int mobileNoSort;
        public int emailSort;
        public int passwordSort;
        public int wxOpenIdSort;
        public int wxUnionIdSort;
        public int wxNicknameSort;
        public int xcxOpenIdSort;
        public int xcxSessionKeySort;
        public int sexSort;
        public int headimgurlSort;
        public int provinceSort;
        public int citySort;
        public int imageIdSort;
        public int statusSort;
        public int lastLoginTimeSort;
        public int createTimeSort;
        public int updateTimeSort;
        public int larkOpenIdSort;
        //
        @QueryField(ingore=true)
        public Integer departmentId;//查询此组织架构下的所有用户
        
        @InnerJoin(table2=Department.class,table2Field="accountId")
        @QueryField(field="parentId")
        public int[] departmentIds;
    }

}