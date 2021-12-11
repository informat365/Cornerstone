/**
 * 
 */
package cornerstone.biz.cmdb;

import java.util.List;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.domain.CmdbApplication.CmdbApplicationInfo;
import cornerstone.biz.domain.CmdbInstance.CmdbInstanceInfo;
import cornerstone.biz.domain.CmdbMachine.CmdbMachineInfo;
import cornerstone.biz.domain.CompanyMember.CompanyMemberInfo;
import cornerstone.biz.domain.JavaScriptEngine;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemInfo;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.util.StringUtil;
import cornerstone.biz.util.TripleDESUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.DumpUtil;

/**
 * 用途(1.用于生成jazmin.js这个文件内容)
 * @author cs
 *
 */
public class JavaScriptCmdb extends JavaScriptEngine{
	//
	private static Logger logger=LoggerFactory.get(JavaScriptCmdb.class);
	//
	private BizService service;
	private StringBuilder buff;
	public String apiKey;
	//
	//
	public JavaScriptCmdb(BizService service) {
		super(null);
		this.service=service;
		buff=new StringBuilder();
	}
	//
	public void print(String message) {
		buff.append(message);
	}
	//
	public void println(String message) {
		buff.append(message).append("\n");
	}
	//
	public List<CmdbInstanceInfo> getInstanceList() {
		return service.getCmdbInstanceList(apiKey);
	}
	//
	public List<CmdbApplicationInfo> getApplicationList() {
		return service.getCmdbApplicationList(apiKey);
	}
	//
	public List<CmdbMachineInfo> getMachineList() {
		return service.getCmdbMachineList(apiKey);
	}
	//
	public List<CompanyMemberInfo> getAccountList() {
		return service.getCompanyMemberList(apiKey);
	}
	//
	public List<ProjectMemberInfo> getProjectMemberList() {
		return service.getProjectMemberList(apiKey);
	}
	//
	public List<ProjectSubSystemInfo> getProjectSubSystemList() {
		return service.getProjectSubSystemList(apiKey);
	}
	//
	public String stringify(Object obj) {
		return DumpUtil.dump(obj);
	}
	//
	public String run(String source){
		try {
			engine.eval(source);
		} catch (Exception e) {
			buff.append(e.getMessage());
			logger.error(e.getMessage(),e);
		}
		return buff.toString();
	}
	//
	//查询交付物uuid
	public String getArtifactUuid(String name,String version) {
		return service.getArtifactUuid(name,version);
	}
	//
	public void put(String key,Object value) {
		bindings.put(key, value);
	}
	//
	public String decryptPassword(String password) {
		if(StringUtil.isEmpty(password)) {
			return "";
		}
		return TripleDESUtil.decrypt(password, ConstDefine.GLOBAL_KEY);
	}
	//
}
