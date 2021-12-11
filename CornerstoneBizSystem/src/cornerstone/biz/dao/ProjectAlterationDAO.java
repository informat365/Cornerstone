package cornerstone.biz.dao;

import cornerstone.biz.domain.TaskAlterationDefine;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;

import java.util.List;

/**
 * 项目流程Dao类
 */
public class ProjectAlterationDAO extends ITFDAO {


    public List<TaskAlterationDefine.TaskAlterationDefineInfo> getProjectAlterationDefineList(int projectId, int objectType) {
        return getList(TaskAlterationDefine.TaskAlterationDefineInfo.class,
                QueryWhere.create().where("project_id", projectId).where("object_type", objectType));
    }

    public int getProjectAlterationDefineCount(int projectId, int objectType, int type) {
        return getListCount(TaskAlterationDefine.class,QueryWhere.create()
                .where("project_id",projectId).where("object_type",objectType).where("type",type));
    }
}
