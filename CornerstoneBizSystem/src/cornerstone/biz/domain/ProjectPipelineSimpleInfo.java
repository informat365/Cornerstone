package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class ProjectPipelineSimpleInfo {

	public String name;
	//
	public static ProjectPipelineSimpleInfo create(ProjectPipeline bean) {
		ProjectPipelineSimpleInfo info=new ProjectPipelineSimpleInfo();
		info.name=bean.name;
		return info;
	}
}
