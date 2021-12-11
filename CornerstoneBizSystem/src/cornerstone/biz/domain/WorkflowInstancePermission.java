package cornerstone.biz.domain;

import java.util.List;

import cornerstone.biz.domain.WorkflowChartDefine.GraphLink;
import cornerstone.biz.domain.WorkflowChartDefine.GraphNode;
import cornerstone.biz.domain.WorkflowChartDefine.NodeFormField;

/**
 * 
 * @author cs
 *
 */
public class WorkflowInstancePermission {

	/**能否提交*/
	public boolean enableSubmit;
	
	/**暂存*/
	public boolean enableSave;
	
	/**打印*/
	public boolean enablePrint;
	
	/**终止*/
	public boolean enableTerminal;
	
	/**撤回*/
	public boolean enableCancel;

	/**回退*/
	public boolean enableBackword;
	
	/**删除*/
	public boolean enableDelete;
	
	/**回退节点列表*/
	public List<GraphNode> backwordNodeList;
	
	/**转交*/
	public boolean enableTransferTo;
	
	/***/
	public List<NodeFormField> formFieldList;
	
	/***/
	public List<GraphNode> graphNodeList;
	
	/***/
	public List<GraphLink> graphLinkList;
	
	public String forwardRule;
	public String submitButtonText;
	public String terminalButtonText;
	
}
