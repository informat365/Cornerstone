package cornerstone.biz.domain;

import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class HeartbeatInfo {

	public List<AccountNotification> notificationList;
	
	/**未读通知数量*/
	public int unReadNotificationNum;
	
	/**待我汇报的数量*/
	public int myReportSubmitNum;
	
	/**待我审核的数量*/
	public int myReportAuditNum;
}
