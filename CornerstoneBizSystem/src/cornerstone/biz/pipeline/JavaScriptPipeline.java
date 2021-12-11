/**
 * 
 */
package cornerstone.biz.pipeline;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import cornerstone.biz.BizData;
import cornerstone.biz.ConstDefine;
import cornerstone.biz.CornerstoneBizSystem;
import cornerstone.biz.domain.JavaScriptEngine;
import cornerstone.biz.domain.Machine;
import cornerstone.biz.domain.ParameterValue;
import cornerstone.biz.domain.ProjectArtifact;
import cornerstone.biz.domain.ProjectPipelineRunDetailLog;
import cornerstone.biz.domain.ProjectPipelineRunLog;
import cornerstone.biz.domain.ProjectPipelineRunLog.StepRunInfo;
import cornerstone.biz.pipeline.PipelineDefine.Parameter;
import cornerstone.biz.pipeline.PipelineDefine.Stage;
import cornerstone.biz.ssh.ConnectionInfo;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.SshUtil;
import cornerstone.biz.util.StringUtil;
import cornerstone.biz.websocket.WebEvent;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;
import jdk.nashorn.api.scripting.JSObject;

import javax.script.ScriptException;

/**
 * 1.用于写编译重启脚本
 * @author cs
 *
 */
public class JavaScriptPipeline extends JavaScriptEngine{
	//
	private static Logger logger=LoggerFactory.get(JavaScriptPipeline.class);
	//
	private ProjectPipelineRunLog runLog;
	private PipelineDefine pipelineDefine;
	private JSObject jsPipeline;
	private String currNode;
	private String currWorkspace;
	private String stage;
	private Stage currStage;
	private String step;
	private ConnectionInfo connectionInfo;
	private static boolean isTest;
	private int shDetailLogId;
	//
	public JavaScriptPipeline(String source) {
		this(null,source);
	}
	//
	public JavaScriptPipeline(ProjectPipelineRunLog runLog,String source) {
		super(source);
		this.runLog=runLog;
		pipelineDefine=getPipeline();
	}
	//
	//
	private PipelineDefine getPipeline() {
		jsPipeline=(JSObject)engine.get("pipeline");
		if(jsPipeline==null) {
			throw new AppException("配置数据错误pipeline not found");
		}
		PipelineDefine pipeline=new PipelineDefine();
		pipeline.node=getStringValue(jsPipeline,"node");
		pipeline.workspace=getStringValue(jsPipeline,"workspace");
		pipeline.env=getMapValue(jsPipeline,"env");
		Object stages=jsPipeline.getMember("stages");
		if(stages instanceof JSObject) {
			Set<String> keys=((JSObject)stages).keySet();
			if(keys!=null) {
				for (String key : keys) {
					Object jsStage=((JSObject)stages).getMember(key);
					Stage stage=getStage(key,jsStage);
					if(stage!=null) {
						pipeline.stages.add(stage);
					}
				}
			}
		}
		//
//		if(logger.isInfoEnabled()) {
//			logger.info("getPipelineDomain:{}",DumpUtil.dump(pipeline));
//		}
		//
		return pipeline;
	}
	//
	private Stage getStage(String name,Object jsValue){
		JSObject jsObject=getJSObjectMember(jsValue);
		if(jsObject==null) {
			return null;
		}
		Stage stage=new Stage();
		stage.name=name;
		stage.node=getStringValue(jsObject,"node");
		stage.workspace=getStringValue(jsObject,"workspace");
		stage.env=getMapValue(jsObject,"env");
		stage.steps=getSteps(jsObject);
		stage.parameters=getParameters(jsObject);
		return stage;
	}
		
	private List<String> getSteps(JSObject jsObject){
		List<String> list=new ArrayList<>();
		Object jsSteps=jsObject.getMember("steps");
		if(jsSteps==null|| "undefined".equals(jsSteps.toString())) {
			return list;
		}
		if(!(jsSteps instanceof JSObject)) {
			return list;
		}
		return getFunctionNames((JSObject)jsSteps);
	}
	//
	private List<Parameter> getParameters(JSObject jsObject){
		List<Parameter> parametersList=new ArrayList<>();
		JSObject parameters=getJSObjectMember(jsObject,"parameters");
		if(parameters==null) {
			return parametersList;
		}
		Set<String> keys=parameters.keySet();
		for (String key : keys) {
			JSObject parameter=getJSObjectMember(parameters,key);
			if(parameter==null) {
				continue;
			}
			Parameter p=new Parameter();
			p.name=key;
			p.type=getStringValue(parameter, "type");
			p.message=getStringValue(parameter, "message");
			p.list=getStringListValue(parameter, "list");
			parametersList.add(p);
		}
		return parametersList;
	}
	//
	//
	public void run(String key) {
		if(key==null||!key.equals(ConstDefine.GLOBAL_KEY)) {
			logger.error("run failed.key is not match:{}",key);
			return;
		}
		//确保事务已提交
		int count=0;
		while(true) {
			ProjectPipelineRunLog logFromDB=BizData.pipelineAction.getProjectPipelineRunLogById(runLog.id);
			if(logFromDB!=null) {
				break;
			}
			sleep(500);
			count++;
			if(count>20) {//等10s
				break;
			}
		}
		logger.info("run count:"+count);
		running();
	}
	//
	private int addDetailLog(ProjectPipelineRunDetailLog bean) {
		if(isTest) {
			logger.info("addDetailLog {}",DumpUtil.dump(bean));
			return 0;
		}
		return BizData.pipelineAction.addDetailLog(bean);
	}
	//
	private void updateProjectPipelineRunLog(ProjectPipelineRunLog bean) {
		if(isTest) {
			logger.info("updateProjectPipelineRunLog {}",DumpUtil.dump(bean));
			return;
		}
		BizData.pipelineAction.updateProjectPipelineRunLog(bean);
	}
	//
	private ConnectionInfo getConnectionInfo(Machine machine) {
		return BizUtil.createConnectionInfo(machine);
	}
	//
	private void running() {
		long startTime=System.currentTimeMillis();
		String useTime="";
		try {
			//
			if(CornerstoneBizSystem.webEventServer!=null) {
				WebEvent event=WebEvent.createWebEvent(
						runLog.createAccountId,runLog.companyId, 
						WebEvent.TYPE_Devops开始执行, null, runLog.projectId,null,null,null,null,
						runLog.pipelineId,runLog.id);
				CornerstoneBizSystem.webEventServer.boardcastAsync(event);
			}
			//
			currNode=pipelineDefine.node;
			currWorkspace=pipelineDefine.workspace;
			runLog.node=currNode;
			addDebugDetailLog("开始执行");
			//
			for (Stage stageObj : pipelineDefine.stages) {
				long stateStartTime=System.currentTimeMillis();
				currStage=stageObj;
				if(!StringUtil.isEmpty(stageObj.node)) {
					currNode=stageObj.node;
				}
				if(!StringUtil.isEmpty(stageObj.workspace)) {
					currWorkspace=stageObj.workspace;
				}
				stage=stageObj.name;
				runLog.node=stageObj.node;
				runLog.stage=stageObj.name;
				if(stageObj.parameters!=null&&stageObj.parameters.size()>0) {
					for (Parameter p : stageObj.parameters) {
						runLog.status=ProjectPipelineRunLog.STATUS_Pending;
						runLog.parameter=p.name;
						updateProjectPipelineRunLog(runLog);
						addDebugDetailLog("等待用户输入参数"+p.name);
						waitPending();
					}
				}
				if(stageObj.steps!=null&&stageObj.steps.size()>0) {
					StepRunInfo stepRunInfo=null;
					Date stepStartTime=null;
					for (String s : stageObj.steps) {
						stepStartTime=new Date();
						stepRunInfo=new StepRunInfo();
						stepRunInfo.step=s;
						stepRunInfo.success=true;
						//
						refreshProjectPipelineRunLog();
						if(runLog.status!=ProjectPipelineRunLog.STATUS_正在执行) {
							logger.debug("{} runLog.status{}!=ProjectPipelineRunLog.STATUS_正在执行",
									runLog.id,runLog.status);
							addDetailLog(ProjectPipelineRunDetailLog.TYPE_错误, "执行中断，因为手工停止");
							return;
						}
						step=s;
						runLog.step=step;
						updateProjectPipelineRunLog(runLog);
						String funcName="pipeline.stages."+stage+".steps."+step;
						String remark="";
						try {
							engine.eval(funcName+"()");
						}catch (ScriptException e) {
							stepRunInfo.success=false;
							throw new AppException("ERROR");
						}finally {
							stepRunInfo.time=System.currentTimeMillis()-stepStartTime.getTime();
							runLog.stepInfo.add(stepRunInfo);
							updateProjectPipelineRunLog(runLog);
							//
							ProjectPipelineRunDetailLog detailLog=new ProjectPipelineRunDetailLog();
							detailLog.runLogId=runLog.id;
							detailLog.type=ProjectPipelineRunDetailLog.TYPE_输出;
							detailLog.message="Step【"+step+"】执行完毕 "+BizUtil.getUseTime(stepStartTime.getTime());
							detailLog.node=pipelineDefine.node;
							detailLog.startTime=stepStartTime;
							detailLog.endTime=new Date();
							detailLog.remark=remark;
							addDetailLog(detailLog);
						}
					}
				}//for step
				addDetailLog(ProjectPipelineRunDetailLog.TYPE_输出, "stage【"+stage+"】执行完毕 "+BizUtil.getUseTime(stateStartTime));
			}//for stage
			useTime=BizUtil.getUseTime(startTime);
			runLog.status=ProjectPipelineRunLog.STATUS_执行成功;
			runLog.endTime=new Date();
			runLog.useTime=useTime;
			updateProjectPipelineRunLog(runLog);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			addDetailLog(ProjectPipelineRunDetailLog.TYPE_错误, e.getMessage());
			useTime=BizUtil.getUseTime(startTime);
			runLog.status=ProjectPipelineRunLog.STATUS_执行失败;
			runLog.errorMessage=e.getMessage();
			runLog.endTime=new Date();
			runLog.useTime=useTime;
			updateProjectPipelineRunLog(runLog);
			throw new AppException(e.getMessage());
		}
		//
		addDetailLog(ProjectPipelineRunDetailLog.TYPE_输出, 
				"["+DateUtil.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss")+
				"]总共花费"+useTime);
		//
		//20200131新增
		try {
			if(CornerstoneBizSystem.webEventServer!=null) {
				WebEvent event=WebEvent.createWebEvent(
						runLog.createAccountId,runLog.companyId, 
						WebEvent.TYPE_Devops执行结束, null, runLog.projectId,null,null,null,null,
						runLog.pipelineId,runLog.id);
				CornerstoneBizSystem.webEventServer.boardcastAsync(event);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
		}
		//
	}
	//
	private void waitPending() {
		long waitStartTime=System.currentTimeMillis();
		while(runLog.status==ProjectPipelineRunLog.STATUS_Pending) {
			sleep(500);
			refreshProjectPipelineRunLog();
			logger.info("waitPending");
			if(System.currentTimeMillis()-waitStartTime>1000*60) {//等待超过1分钟
				runLog.status=ProjectPipelineRunLog.STATUS_执行失败;
				runLog.errorMessage="等待输入参数超时，执行失败";
				updateProjectPipelineRunLog(runLog);
				break;
			}
		}
	}
	//
	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	//
	public boolean portTest(String host, int port) {
		Socket socket=null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 1000);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }finally {
        		try {
        				if(socket!=null) {
        					socket.close();
        				}
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
			}
		}
    }
	//
	private void addDebugDetailLog(String message) {
		addDetailLog(ProjectPipelineRunDetailLog.TYPE_调试,message);
	}
	//
	private int addDetailLog(int type,String message) {
		return addDetailLog(currNode, stage, step, type, message);
	}
	//
	private void appendDetailLogMessage(int id,String message) {
		if(isTest) {
			logger.info("appendDetailLogMessage {}",message);
			return;
		}
		BizData.pipelineAction.appendDetailLogMessage(id,message);
	}
	//
	private int addDetailLog(String node,String stage,String step,int type,String message) {
		ProjectPipelineRunDetailLog log=new ProjectPipelineRunDetailLog();
		log.runLogId=runLog.id;
		log.node=node;
		log.stage=stage;
		log.step=step;
		log.type=type;
		log.message=message;
		return addDetailLog(log);
	}
	//
	private void refreshProjectPipelineRunLog() {
		if(isTest) {
			return;
		}
		ProjectPipelineRunLog newRunLog=BizData.pipelineAction.getProjectPipelineRunLog(runLog.id);
		runLog.status=newRunLog.status;
		logger.debug("refreshProjectPipelineRunLog runLog status:{} stage:{} step:{}"
				,runLog.status,stage,step);
	}
	//
	public void print(String message) {
		addDebugDetailLog(message);
	}
	//
	public void printQRCode(String message) {
		addDetailLog(ProjectPipelineRunDetailLog.TYPE_二维码,message);
	}
	//
	public int sh(String cmd){
		return sh(cmd,false);
	}
	//
	private String getEnv(Stage stage) {
		StringBuilder env=new StringBuilder();
		Map<String,String> finalEnvs=new LinkedHashMap<>();
		addEnv(finalEnvs, pipelineDefine.env);
		addEnv(finalEnvs, stage.env);
		if(!finalEnvs.isEmpty()) {
			for (Map.Entry<String, String> entry : finalEnvs.entrySet()) {
				String key=entry.getKey();
				String value=entry.getValue();
				env.append("export "+key+"="+value).append("&&");
			}
		}
		return env.toString();
	}
	//
	private void addEnv(Map<String,String> finalEnvs,Map<String,String> sourceEnvs) {
		if(sourceEnvs==null) {
			return;
		}
		for (Map.Entry<String, String> entry : sourceEnvs.entrySet()) {
			String key=entry.getKey();
			String value=entry.getValue();
			if(StringUtil.isEmptyWithTrim(key)||StringUtil.isEmptyWithTrim(value)) {
				continue;
			}
			finalEnvs.put(key.trim(),value.trim());
		}
	}
	//
	public int sh(String cmd,boolean pty){
		addDebugDetailLog("执行sh命令 "+cmd);
		StringBuilder finalCmd=new StringBuilder();
		if(currStage!=null) {
			if(!StringUtil.isEmptyWithTrim(currWorkspace)){
				finalCmd.append("cd "+currWorkspace).append("&&");//https://blog.csdn.net/stpeace/article/details/51870812//顺序执行各条命令， 只有当前一个执行成功时候， 才执行后面的。	
			}
			String env=getEnv(currStage);
			if(!StringUtil.isEmptyWithTrim(env)) {
				finalCmd.append(env);
			}
		}
		finalCmd.append(cmd);
		cmd=finalCmd.toString();
		logger.info("sh cmd:{} pty:{}",cmd,pty);
		if(!isTest) {
			Machine machine=getMachine(runLog.id, currNode);
			connectionInfo=getConnectionInfo(machine);
		}
		StringBuilder shBuffer=new StringBuilder();
		try {
			int code=SshUtil.execute(connectionInfo.host, connectionInfo.port, connectionInfo.user, 
					connectionInfo.password,connectionInfo.privateKey, cmd, pty,(out,error)->{
						shBuffer.append(out+error);
						if(shBuffer.length()>0) {
							saveShDetailLog(shBuffer.toString());
							shBuffer.delete(0, shBuffer.length());
						}
						
			});
			return code;
		}catch (Exception e) {
			logger.error(cmd+"\n"+e.getMessage(), e);
			throw new AppException("执行命令错误："+e.getMessage());
		}finally {
			if(shBuffer.length()>0) {
				saveShDetailLog(shBuffer.toString());
			}
			shDetailLogId=0;
		}
	}
	//
	//拷贝文件到服务器
	public String artifact(String name,String version,String remoteFile){
		if(StringUtil.isEmpty(name)) {
			throw new AppException("名称不能为空");
		}
		if(StringUtil.isEmpty(version)) {
			throw new AppException("版本号不能为空");
		}
		if(StringUtil.isEmpty(remoteFile)) {
			throw new AppException("文件不能为空");
		}
		if(!remoteFile.startsWith("/")) {
			remoteFile=currStage.workspace+File.separator+remoteFile;
		}
		if(!isTest) {
			Machine machine=getMachine(runLog.id, currNode);
			connectionInfo=getConnectionInfo(machine);
		}
		//
		String fileId=BizUtil.randomUUID();
		File file=BizUtil.getArtifactFile(fileId);
//		addDebugDetailLog("archive name:"+name+",version:"+version+
//				",remoteFile:"+remoteFile+",localFile:"+file.getAbsolutePath());
		SshUtil.scpFrom(connectionInfo.host, connectionInfo.port, connectionInfo.user, 
				connectionInfo.password, remoteFile, file.getAbsolutePath());
		//
		if(file.length()==0) {
			logger.error("文件不存在"+file.getAbsolutePath());
			throw new AppException("artifact失败");
		}
		ProjectArtifact bean=new ProjectArtifact();
		bean.name=name;
		bean.version=version;
		bean.size=file.length();
		bean.uuid=fileId;
		bean.md5=BizUtil.getFileMD5(file);
		if(remoteFile.lastIndexOf("/")!=-1) {
			bean.originalName=(remoteFile.substring(remoteFile.lastIndexOf("/")+1,remoteFile.length()));
		}
		BizData.pipelineAction.addProjectArtifact(runLog.id,bean);
		//
		addDetailLog(ProjectPipelineRunDetailLog.TYPE_ARTIFACT, JSONUtil.toJson(bean));
		//
		return fileId;
	}
	//
	private Machine getMachine(int runLogId,String machineName) {
		Machine machine=BizData.pipelineAction.getMachineByRunLogIdName(runLogId, machineName);
		if(machine==null||machine.isDelete) {
			throw new AppException("找不到名称为【"+machineName+"】的主机");
		}
		return machine;
	}
	//
	private void copy(String fromMachineName,String fromFilePath,String toMachineName,String remoteFilePath) 
	throws IOException {
		File tmpFile=File.createTempFile(UUID.randomUUID().toString(),"copy");
		copyFrom0(fromMachineName, fromFilePath, tmpFile.getAbsolutePath());
		copyTo0(tmpFile.getAbsolutePath(), toMachineName, remoteFilePath);
		tmpFile.delete();
	}
	//
	private void copyFrom0(String machineName,String remoteFilePath,String localFilePath) {
		Machine machine=getMachine(runLog.id, machineName);
		connectionInfo=getConnectionInfo(machine);
		if(!localFilePath.startsWith("/")) {
			localFilePath=currStage.workspace+File.separator+localFilePath;
		}
		SshUtil.scpFrom(connectionInfo.host, connectionInfo.port, connectionInfo.user, 
				connectionInfo.password, remoteFilePath, localFilePath);
	}
	//
	public void copyFrom(String machineName,String remoteFilePath,String localFilePath) throws IOException {
		addDebugDetailLog("拷贝文件 本地路径:"+localFilePath+",节点:"+machineName+
				",远程路径:"+remoteFilePath);
		copy(machineName, remoteFilePath, currNode, localFilePath);
	}
	//
	public void copyTo(String localFilePath,String machineName,String remoteFilePath) throws IOException {
		if(!localFilePath.startsWith("/")) {
			localFilePath=currStage.workspace+File.separator+localFilePath;
		}
		addDebugDetailLog("拷贝文件 本地路径:"+localFilePath+",节点:"+machineName+
				",远程路径:"+remoteFilePath);
		copy(currNode, localFilePath, machineName, remoteFilePath);
	}
	//
	private void copyTo0(String localFilePath,String machineName,String remoteFilePath) {
		Machine machine=getMachine(runLog.id, machineName);
		connectionInfo=getConnectionInfo(machine);
		if(!localFilePath.startsWith("/")) {
			localFilePath=currStage.workspace+File.separator+localFilePath;
		}
		File localFile=new File(localFilePath);
		if(!localFile.exists()) {
			throw new AppException("文件不存在"+localFilePath);
		}
		SshUtil.scpTo(connectionInfo.host, connectionInfo.port, connectionInfo.user, 
				connectionInfo.password,connectionInfo.privateKey,
				localFile.getAbsolutePath(), remoteFilePath, null);
	}
	//
	private void saveShDetailLog(String message) {
		if(StringUtil.isEmpty(message)||message.trim().length()==0) {
			return;
		}
		if(shDetailLogId==0) {
			shDetailLogId=addDetailLog(ProjectPipelineRunDetailLog.TYPE_输出, message);
		}else {
			appendDetailLogMessage(shDetailLogId, message);
		}
	}
	//
	public void parallel(List<Consumer<Void>> functions) {
		addDebugDetailLog("parallel "+functions);
	}
	//
	public void abort(String message) {
		addDebugDetailLog("abort "+DumpUtil.dump(message));
		throw new AppException(message);
	}
	//
	public Object parameter(String parameterName) {
		Parameter  parameter=PipelineDefine.getParameter(pipelineDefine,stage, parameterName);
		if(parameter==null) {
			return null;
		}
		String key=stage+"-"+parameterName;
		ParameterValue value=BizData.pipelineAction.getParamaterValue(runLog.id, key);
		return PipelineDefine.getParameterValue(parameter, value);
	}
	//
	public String stringify(Object obj) {
		return DumpUtil.dump(obj);
	}
	//发送通知(用户名)
	public void sendNotification(String userName,String content) {
		BizData.pipelineAction.sendNotification(runLog.id,userName,content);
		addDebugDetailLog("通知用户【"+userName+"】:"+content);
	}
	//
	/**
	 * @return the pipelineDefine
	 */
	public PipelineDefine getPipelineDefine() {
		return pipelineDefine;
	}
	//
}
