package cornerstone.biz.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cornerstone.biz.domain.ParameterValue;

/**
 * 
 * @author cs
 *
 */
public class PipelineDefine {
	//
	public String node;
	public String workspace;
	public Map<String,String> env;
	public List<Stage> stages;
	//
	public static class Stage{
		public String name;
		public String node;
		public String workspace;
		public Map<String,String> env;	
		public List<String> steps;
		public List<Parameter> parameters;
	}
	//
	public static class Parameter{
		//
		public static final String TYPE_STRING="string";
		public static final String TYPE_BOOL="bool";
		public static final String TYPE_ARRAY="array";
		//
		public String name;
		public String type;
		public String message;
		public List<String> list;
	}
	//
	public PipelineDefine() {
		stages=new ArrayList<>();
	}
	//
	public static Stage getStage(PipelineDefine pipelineDefine,String stage) {
		if(pipelineDefine.stages!=null) {
			for (Stage s : pipelineDefine.stages) {
				if(s.name.equals(stage))
				return s;
			}
		}
		return null;
	}
	//
	public static Parameter getParameter(PipelineDefine pipelineDefine,String stage,String parameterName) {
		Stage s=getStage(pipelineDefine,stage);
		if(s==null) {
			return null;
		}
		if(s.parameters!=null) {
			for (Parameter p : s.parameters) {
				if(p.name.equals(parameterName)) {
					return p;
				}
			}
		}
		return null;
	}
	
	public static Object getParameterValue(Parameter parameter,ParameterValue value) {
		if(value==null) {
			return null;
		}
		if(parameter.type.equals(Parameter.TYPE_STRING)) {
			return value.stringValue;
		}
		if(parameter.type.equals(Parameter.TYPE_BOOL)) {
			return value.booleanValue;
		}
		if(parameter.type.equals(Parameter.TYPE_ARRAY)) {
			return value.listValue;
		}
		return null;
	}
	
}
