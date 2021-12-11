package cornerstone.biz.domain;

import cornerstone.biz.annotations.DomainFieldValid;

/**
 * 
 * @author cs
 *
 */
public class AttachmentSimpleInfo {

	@DomainFieldValid(comment="附件uuid")
    public String uuid;
    
    @DomainFieldValid(comment="附件名")
    public String name;
}
