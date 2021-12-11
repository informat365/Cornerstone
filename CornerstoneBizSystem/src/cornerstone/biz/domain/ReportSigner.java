package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;
import jazmin.driver.jdbc.smartjdbc.annotations.ForeignKey;

/**
 * 描述:
 * create at 2021/5/7 11:19
 *
 * @author yaop
 */
public class ReportSigner {


    public int companyId;
    //
    @ForeignKey(domainClass=Report.class)
    @DomainFieldValid(comment="汇报",required=true,canUpdate=true)
    public int reportId;

    public int type;

    @ForeignKey(domainClass=Account.class)
    @DomainFieldValid(comment="创建人",required=true,canUpdate=true)
    public int createAccountId;

    @DomainFieldValid(comment="汇报内容",canUpdate=true)
    public String title;

    @DomainFieldValid(comment="内容",required=true,canUpdate=true,needTrim=true)
    public String content;
}
