package cornerstone.biz.srv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cornerstone.biz.dao.SurveysDAO;
import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.domain.SurveysDefine;
import cornerstone.biz.domain.SurveysDefine.PermissionRole;
import cornerstone.biz.domain.SurveysDefine.ProjectPermissionRole;
import cornerstone.biz.domain.SurveysFormDefine.SurveysFormField;
import cornerstone.biz.domain.SurveysInstance;
import cornerstone.biz.domain.WorkflowFormDefine.FormField;
import cornerstone.biz.domain.WorkflowFormDefine.FormFieldObjectValue;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import jazmin.core.app.AutoWired;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class SurveysService extends CommService {
	//
	private static Logger logger=LoggerFactory.get(SurveysService.class);
	//
	@AutoWired
	SurveysDAO surveysDAO;
	//
	public boolean havePermission(int accountId,SurveysDefine surveysDefine) {
		return havePermission(accountId,surveysDefine.companyId, surveysDefine.accountList, surveysDefine.companyRoleList,
				surveysDefine.projectRoleList, surveysDefine.departmentList);
	}
	//
	public boolean havePermission(int accountId,int companyId,List<PermissionRole> userList,
			List<PermissionRole> companyRoleList,List<ProjectPermissionRole> projectRoleList,
			List<PermissionRole> departmentList) {
		Set<Integer> ownerId=getAccountIds(companyId,userList, companyRoleList, 
				projectRoleList, departmentList, null);
		if(ownerId.contains(accountId)) {
			return true;
		}
		return false;
	}
	
	public Map<String, Object> getFormFieldValues(SurveysInstance instance) {
		Map<String, Object> ret = new HashMap<>();
		if (instance.formData != null) {
			ret = JSONUtil.fromJson(instance.formData, Map.class);
		}
		if(ret==null) {
			ret=new HashMap<>();
		}
		return ret;
	}
	
	public Object getFieldValue(SurveysInstance instance,String fieldId) {
		Map<String, Object> map=getFormFieldValues(instance);
		return map.get(fieldId);
	}
	
	public String getFormFieldValueForExport(SurveysFormField formField, Object value) {
		if (formField == null || value == null) {
			return "";
		}
		String t = formField.type;
		if (t.equals(FormField.TYPE_TEXT_SINGLE) ||
				t.equals(FormField.TYPE_SYSTEM_VALUE)||
				t.equals(FormField.TYPE_RADIO)||
				t.equals(FormField.TYPE_SELECT)) {// 直接取值
			return value.toString();
		}else if(t.equals(FormField.TYPE_CHECKBOX)) {
			try {
				List<String> values=JSONUtil.fromJsonList(value.toString(),String.class);
				return BizUtil.appendFields(values);
			} catch (Exception e) {
				logger.error("value:"+value);
				logger.error(e.getMessage(),e);
				return value.toString();
			}
		}else if (t.equals(FormField.TYPE_TEXT_AREA)||
				t.equals(FormField.TYPE_TEXT_RICH)) {// 直接取值且htmlclean
			return BizUtil.cleanHtml(value.toString());
		}else if (t.equals(FormField.TYPE_USER_SELECT)||
				t.equals(FormField.TYPE_DEPARTMENT_SELECT)||
				t.equals(FormField.TYPE_ROLE_COMPANY_SELECT)||
				t.equals(FormField.TYPE_ROLE_PROJECT_SELECT)) {
			List<FormFieldObjectValue> values = JSONUtil.fromJsonList(value.toString(),
					FormFieldObjectValue.class);
			return BizUtil.getListSingleField(values, "name");
		}else if (t.equals(FormField.TYPE_DATE)) {
			try {
				return DateUtil.formatDate(DateUtil.parseDateTimeFromExcel(value.toString()));
			} catch (Exception e) {
				logger.warn("parseUTCDate failed."+e.getMessage()+","+value.toString());
				return value.toString();
			}
		}else if (t.equals(FormField.TYPE_ATTACHMENT)) {
			List<FormFieldObjectValue> values = JSONUtil.fromJsonList(value.toString(),FormFieldObjectValue.class);
			if(values==null||values.isEmpty()) {
				return "";
			}
			StringBuilder url=new StringBuilder();
			for (FormFieldObjectValue e : values) {
				url.append(GlobalConfig.webUrl+"p/file/get_file/"+e.id).append(";");
			}
			url.deleteCharAt(url.length()-1);
			return url.toString();
		}else {
			return value.toString();
		}
	}

}
