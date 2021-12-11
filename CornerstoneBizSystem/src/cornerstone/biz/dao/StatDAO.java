package cornerstone.biz.dao;

import com.google.common.base.Joiner;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.AccoutBugStat.AccoutBugStatQuery;
import cornerstone.biz.domain.TaskDayData.TaskDayDataInfo;
import cornerstone.biz.domain.TaskDayData.TaskDayDataQuery;
import cornerstone.biz.domain.TaskStatDayData.TaskStatDayDataQuery;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 统计DAO
 *
 * @author cs
 */
public class StatDAO extends ITFDAO {

    /**
     * 统计累积数量(没有objectType)
     *
     * @return
     */
    public List<TaskDayData> getTaskStatDayDataWithNoObjectType(TaskDayDataQuery query) {
        query.objectType = 0;
        return getList(query);
    }

    /**
     * 统计累积数量(按项目)
     *
     * @return
     */
    public List<TaskDayData> getTotalTaskNumGroupByProjectObjectType(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);//30天内登陆过的
        String sql = "\nselect a.company_id,a.project_id,a.object_type,count(1) as total_num from t_task a "
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.is_delete=false and a.create_time<?"
                + "group by a.company_id,a.project_id,a.object_type ";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }
    /**
     * 统计累积工作量(按项目)
     *
     * @return
     */
    public List<TaskDayData> getTotalTaskLoadGroupByProjectObjectType(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);//30天内登陆过的
        String sql = "\nselect a.company_id,a.project_id,a.object_type,sum(work_load) as total_load from t_task a "
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.is_delete=false and a.create_time<?"
                + "group by a.company_id,a.project_id,a.object_type ";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }

    /**
     * 统计累积完成数量(按项目 不按迭代)
     *
     * @param statDate
     * @return
     */
    public List<TaskDayData> getTotalFinishTaskNumGroupByProjectObjectType(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,count(1) as total_finish_num from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.is_delete=false and a.is_finish=true and a.finish_time<?\n"
                + "group by a.company_id,a.project_id,a.object_type \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }
    /**
     * 统计累积完成工作量(按项目 不按迭代)
     *
     * @param statDate
     * @return
     */
    public List<TaskDayData> getTotalFinishTaskLoadGroupByProjectObjectType(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,sum(work_load) as total_finish_load from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.is_delete=false and a.is_finish=true and a.finish_time<?\n"
                + "group by a.company_id,a.project_id,a.object_type \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }

    /**
     * 统计今日完成数量(按项目)
     *
     * @return
     */
    public List<TaskDayData> getTodayFinishTaskNumGroupByProjectObjectType(Date startDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,count(1) as today_finish_num from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.is_delete=false and a.is_finish=true and DATEDIFF(?,a.finish_time)=0 \n"
                + "group by a.company_id,a.project_id,a.object_type \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, startDate);
    }

    /**
     * 统计今日完成工作量(按项目)
     *
     * @return
     */
    public List<TaskDayData> getTodayFinishTaskLoadGroupByProjectObjectType(Date startDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,sum(work_load) as today_finish_load from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.is_delete=false and a.is_finish=true and DATEDIFF(?,a.finish_time)=0 \n"
                + "group by a.company_id,a.project_id,a.object_type \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, startDate);
    }

    /**
     * 统计今天累积未完成数量数量(按项目 不按迭代)
     *
     * @return
     */
    public List<TaskDayData> getTotalUnfinishTaskNumGroupByProjectObjectType() {
        String sql = "\nselect project_id,object_type,count(1) as total_unfinish_num from t_task \n"
                + "where iteration_id=0 and is_delete=false and is_finish=false \n"
                + "group by project_id,object_type \n";
        return queryList(TaskDayData.class, sql);
    }

    /**
     * 统计累积数量(按迭代)
     *
     * @return
     */
    public List<TaskDayData> getTotalTaskNumGroupByProjectObjectTypeIterationId(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);//30天内登陆过的
        String sql = "\nselect a.company_id,a.project_id,a.object_type,a.iteration_id,count(1) as total_num from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.iteration_id>0 and a.is_delete=false and a.create_time<?\n"
                + "group by a.company_id,a.project_id,a.object_type,a.iteration_id \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }

    /**
     * 统计累积工作量(按迭代)
     *
     * @return
     */
    public List<TaskDayData> getTotalTaskLoadGroupByProjectObjectTypeIterationId(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);//30天内登陆过的
        String sql = "\nselect a.company_id,a.project_id,a.object_type,a.iteration_id,sum(work_load) as total_load from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.iteration_id>0 and a.is_delete=false and a.create_time<?\n"
                + "group by a.company_id,a.project_id,a.object_type,a.iteration_id \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }

    /**
     * 统计累积完成数量(按迭代)
     *
     * @return
     */
    public List<TaskDayData> getTotalFinishTaskNumGroupByProjectObjectTypeIterationId(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,a.iteration_id,count(1) as total_finish_num from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.iteration_id>0 and a.is_delete=false and a.is_finish=true and a.finish_time<?\n"
                + "group by a.company_id,a.project_id,a.object_type,a.iteration_id \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }
    /**
     * 统计累积完成工作量(按迭代)
     *
     * @return
     */
    public List<TaskDayData> getTotalFinishTaskLoadGroupByProjectObjectTypeIterationId(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,a.iteration_id,sum(work_load) as total_finish_load from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.iteration_id>0 and a.is_delete=false and a.is_finish=true and a.finish_time<?\n"
                + "group by a.company_id,a.project_id,a.object_type,a.iteration_id \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }

    /**
     * 统计今日完成数量(按迭代)
     *
     * @return
     */
    public List<TaskDayData> getTodayFinishTaskNumGroupByProjectObjectTypeIterationId(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,a.iteration_id,count(1) as today_finish_num from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.iteration_id>0 and a.is_delete=false and a.is_finish=true and dayofyear(?)=dayofyear(a.finish_time) \n"
                + "group by a.company_id,a.project_id,a.object_type,a.iteration_id \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, statDate);
    }
    /**
     * 统计今日完成工作量(按迭代)
     *
     * @return
     */
    public List<TaskDayData> getTodayFinishTaskLoadGroupByProjectObjectTypeIterationId(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "\nselect a.company_id,a.project_id,a.object_type,a.iteration_id,sum(work_load) as today_finish_load from t_task a \n"
                + " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \n"
                + "where a.iteration_id>0 and a.is_delete=false and a.is_finish=true and dayofyear(?)=dayofyear(a.finish_time) \n"
                + "group by a.company_id,a.project_id,a.object_type,a.iteration_id \n";
        return queryList(TaskDayData.class, sql, lastLoginTime, statDate);
    }

    /**
     * 查询每日新增Task数量
     *
     * @param query
     * @return
     */
    public List<TaskStatDayData> getTaskCreateDayDataList(TaskStatDayDataQuery query) {
        StringBuilder sql = new StringBuilder();
        List<Object> values = new ArrayList<>();
        sql.append("select DATE_FORMAT(create_time, '%Y-%m-%d') as stat_date,count(1) as num \n");
        sql.append("from t_task \n");
        sql.append("where true \n");
        sql.append(" and is_delete=false ");//排除删除的
        if (query.companyId != null) {
            sql.append("and company_id=").append(query.companyId).append(" ");
        }
        if (query.projectId != null) {
            sql.append("and project_id=").append(query.projectId).append(" ");
        }
        if (query.objectType != null) {
            sql.append("and object_type=").append(query.objectType).append(" ");
        }
        if (query.iterationId != null) {
            sql.append("and iteration_id=").append(query.iterationId).append(" ");
        }
        if (query.statDateStart != null) {
            sql.append("and create_time>=? ");
            values.add(query.statDateStart);
        }
        if (query.statDateEnd != null) {
            sql.append("and create_time<=? ");
            values.add(query.statDateEnd);
        }
        sql.append(" group by stat_date ");
        sql.append(" order by stat_date asc ");
        List<TaskStatDayData> list = queryList(TaskStatDayData.class, sql.toString(), values.toArray());
        for (Iterator<TaskStatDayData> iterator = list.iterator(); iterator.hasNext(); ) {
            TaskStatDayData e = iterator.next();
            if (e.objectType == Task.OBJECTTYPE_项目清单) {
                iterator.remove();
            }
            e.projectId = query.projectId == null ? 0 : query.projectId;
            e.objectType = query.objectType == null ? 0 : query.objectType;
            e.iterationId = query.iterationId == null ? 0 : query.iterationId;
        }
        return list;
    }

    /**
     * 查询状态分布
     *
     * @param query
     * @return
     */
    public List<TaskStatDayData> getTaskCurrStatusDistributeList(TaskStatDayDataQuery query) {
        StringBuilder sql = new StringBuilder();
        List<Object> values = new ArrayList<>();
        sql.append("select status,count(1) as num \n");
        sql.append("from t_task \n");
        sql.append("where true \n");
        sql.append(" and is_delete=false ");//排除删除的
        sql.append(" and status>0 ");//排除删除的
        if (query.companyId != null) {
            sql.append("and company_id=").append(query.companyId).append(" ");
        }
        if (query.projectId != null) {
            sql.append("and project_id=").append(query.projectId).append(" ");
        }
        if (query.objectType != null) {
            sql.append("and object_type=").append(query.objectType).append(" ");
        }
        if (query.iterationId != null) {
            sql.append("and iteration_id=").append(query.iterationId).append(" ");
        }
        if (query.statDateStart != null) {
            sql.append("and create_time>=? ");
            values.add(query.statDateStart);
        }
        if (query.statDateEnd != null) {
            sql.append("and create_time<=? ");
            values.add(query.statDateEnd);
        }
        sql.append(" group by status ");
        sql.append(" order by status asc ");
        List<TaskStatDayData> list = queryList(TaskStatDayData.class, sql.toString(), values.toArray());
        for (TaskStatDayData e : list) {
            e.projectId = query.projectId == null ? 0 : query.projectId;
            e.objectType = query.objectType == null ? 0 : query.objectType;
            e.iterationId = query.iterationId == null ? 0 : query.iterationId;
        }
        return list;
    }

    /**
     * 创建人分布
     *
     * @param query
     * @return
     */
    public List<TaskStatDayData> getTaskCreateAccountDistributeList(TaskStatDayDataQuery query) {
        StringBuilder sql = new StringBuilder();
        List<Object> values = new ArrayList<>();
        sql.append("select create_account_id,count(1) as num \n");
        sql.append("from t_task \n");
        sql.append("where true \n");
        sql.append(" and is_delete=false ");//排除删除的
        sql.append(" and status>0 ");//排除删除的
        if (query.companyId != null) {
            sql.append("and company_id=").append(query.companyId).append(" ");
        }
        if (query.projectId != null) {
            sql.append("and project_id=").append(query.projectId).append(" ");
        }
        if (query.objectType != null) {
            sql.append("and object_type=").append(query.objectType).append(" ");
        }
        if (query.iterationId != null) {
            sql.append("and iteration_id=").append(query.iterationId).append(" ");
        }
        if (query.statDateStart != null) {
            sql.append("and create_time>=? ");
            values.add(query.statDateStart);
        }
        if (query.statDateEnd != null) {
            sql.append("and create_time<=? ");
            values.add(query.statDateEnd);
        }
        sql.append(" group by create_account_id ");
        sql.append(" order by create_account_id asc ");
        List<TaskStatDayData> list = queryList(TaskStatDayData.class, sql.toString(), values.toArray());
        for (TaskStatDayData e : list) {
            e.projectId = query.projectId == null ? 0 : query.projectId;
            e.objectType = query.objectType == null ? 0 : query.objectType;
            e.iterationId = query.iterationId == null ? 0 : query.iterationId;
        }
        return list;
    }

    //累积代码统计总数（按照迭代）
    public List<TaskDayData> getTotalScmCommitByIteration(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "select i2.company_id,i2.project_id,i2.object_type,i2.iteration_id,sum(IF(a.add_line_num>a.decrease_line_num,a.add_line_num,a.decrease_line_num)) as total_commit_num from t_scm_commit_log a \r\n" +
                "inner join t_task_scm_commit i1 on i1.`scm_commit_log_id`=a.id\r\n" +
                "inner join t_task i2 on i1.`task_id`=i2.id\r\n" +
                "inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \r\n" +
                "where a.create_time<?\r\n" +
                "group by i2.company_id,i2.project_id,i2.object_type,i2.iteration_id;";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }

    //累积代码统计总数（按照项目）
    public List<TaskDayData> getTotalScmCommitByProject(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "select i2.company_id,i2.project_id,i2.object_type,sum(IF(a.add_line_num>a.decrease_line_num,a.add_line_num,a.decrease_line_num)) as total_commit_num from t_scm_commit_log a \r\n" +
                "inner join t_task_scm_commit i1 on i1.`scm_commit_log_id`=a.id\r\n" +
                "inner join t_task i2 on i1.`task_id`=i2.id\r\n" +
                "inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \r\n" +
                "where a.create_time<?\r\n" +
                "group by i2.company_id,i2.project_id,i2.object_type;";
        return queryList(TaskDayData.class, sql, lastLoginTime, DateUtil.getNextDay(statDate, 1));
    }

    //统计今天提交代码总数 （按照迭代）
    public List<TaskDayData> getTodayScmCommitByIteration(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "select i2.company_id,i2.project_id,i2.object_type,i2.iteration_id,sum(IF(a.add_line_num>a.decrease_line_num,a.add_line_num,a.decrease_line_num)) as today_commit_num from t_scm_commit_log a \r\n" +
                "inner join t_task_scm_commit i1 on i1.`scm_commit_log_id`=a.id\r\n" +
                "inner join t_task i2 on i1.`task_id`=i2.id\r\n" +
                " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \r\n" +
                "where DATEDIFF(a.create_time,?)=0 \n" +
                "group by i2.company_id,i2.project_id,i2.object_type,i2.iteration_id;";
        return queryList(TaskDayData.class, sql, lastLoginTime, statDate);
    }

    //统计今天提交代码总数 （按照项目）
    public List<TaskDayData> getTodayScmCommitByProject(Date statDate) {
        Date lastLoginTime = DateUtil.getNextDay(-30);
        String sql = "select i2.company_id,i2.project_id,sum(IF(a.add_line_num>a.decrease_line_num,a.add_line_num,a.decrease_line_num)) as today_commit_num from t_scm_commit_log a \r\n" +
                "inner join t_task_scm_commit i1 on i1.`scm_commit_log_id`=a.id\r\n" +
                "inner join t_task i2 on i1.`task_id`=i2.id\r\n" +
                " inner join t_company t1 on  a.company_id =t1.id and t1.last_login_time > ? \r\n" +
                "where DATEDIFF(a.create_time,?)=0 \n" +
                "group by i2.company_id,i2.project_id,i2.object_type;";
        return queryList(TaskDayData.class, sql, lastLoginTime, statDate);
    }

    /**
     * 统计一个迭代的scm统计（提交人分组）
     *
     * @param iterationId
     * @return
     */
    public List<LongKeyValue> getScmCommitGroupByAuthor(int iterationId) {
        String sql = "select a.author as `key`,sum(IF(a.add_line_num>a.decrease_line_num,a.add_line_num,a.decrease_line_num)) as value from t_scm_commit_log a \r\n" +
                "inner join t_task_scm_commit i1 on i1.`scm_commit_log_id`=a.id\r\n" +
                "inner join t_task i2 on i1.`task_id`=i2.id\r\n" +
                "where  i2.iteration_id=?\r\n" +
                "group by a.author;";
        return queryList(LongKeyValue.class, sql, iterationId);
    }

    /**
     * 统计一个迭代的scm统计（提交人分组）
     *
     * @param projectId
     * @return
     */
    public List<LongKeyValue> getScmCommitGroupByAuthorProjectId(int projectId) {
        String sql = "select a.author as `key`,sum(IF(a.add_line_num>a.decrease_line_num,a.add_line_num,a.decrease_line_num)) as value from t_scm_commit_log a \r\n" +
                "inner join t_task_scm_commit i1 on i1.`scm_commit_log_id`=a.id\r\n" +
                "inner join t_task i2 on i1.`task_id`=i2.id\r\n" +
                "where  i2.project_id=?\r\n" +
                "group by a.author;";
        return queryList(LongKeyValue.class, sql, projectId);
    }

    /**
     * @param projectId
     * @param iterationId
     * @param objectType
     * @param statDate
     * @return
     */
    public TaskDayData getTaskDayData(int companyId, int projectId, int iterationId, int objectType, Date statDate) {
        return getDomain(TaskDayData.class, QueryWhere.create().
                where("company_id", companyId).
                where("project_id", projectId).
                where("iteration_id", iterationId).
                where("object_type", objectType).
                where("stat_date", statDate)
        );
    }

    /**
     * 计算不区分objectType的统计
     *
     * @param statDate
     */
    public void calcNoObjectTypeTaskDayData(Date statDate) {
        String sql = "replace into t_task_day_data(stat_date,project_id,iteration_id,company_id,object_type,total_num,total_finish_num,total_commit_num,today_commit_num,today_finish_num,total_load,total_finish_load,today_finish_load) \n" +
                "select stat_date,project_id,iteration_id,company_id,0 as object_type,"
                + "sum(total_num) as total_num,sum(total_finish_num) as total_finish_num,"
                + "sum(total_commit_num) as total_commit_num,sum(today_commit_num) as today_commit_num,"
                + "sum(today_finish_num) as today_finish_num,sum(total_load) as total_load,sum(total_finish_load) as total_finish_load,sum(today_finish_load) today_finish_load from t_task_day_data \n" +
                "where  stat_date=? and object_type>0 group by stat_date,project_id,iteration_id,company_id;";
        executeUpdate(sql, statDate);
    }

    /**
     * 计算所有不区分objectType的统计
     */
    public void calcAllNoObjectTypeTaskDayData() {
        String sql = "replace into t_task_day_data(stat_date,project_id,iteration_id,company_id,object_type,total_num,total_finish_num,total_commit_num,today_commit_num,today_finish_num) \n" +
                "select stat_date,project_id,iteration_id,company_id,0 as object_type,"
                + "sum(total_num) as total_num,sum(total_finish_num) as total_finish_num,"
                + "sum(total_commit_num) as total_commit_num,sum(today_commit_num) as today_commit_num,"
                + "sum(today_finish_num) as today_finish_num from t_task_day_data \n" +
                "where object_type>0 group by stat_date,project_id,iteration_id,company_id;";
        executeUpdate(sql);
    }

    /**
     * @param iterationId
     * @param startDate
     * @param endDate
     * @param objectTypeList
     * @return
     */
    public List<TaskDayDataInfo> getTaskDayDataInfoByIterationId(int companyId, int iterationId, Date startDate, Date endDate, List<Integer> objectTypeList) {
        String objectTypes = "";
        if (!BizUtil.isNullOrEmpty(objectTypeList)) {
            objectTypes = Joiner.on(",").join(objectTypeList);
        }
        String sql = "select stat_date,iteration_id,company_id,project_id,sum(total_num) as total_num,sum(total_finish_num) as total_finish_num,\n"
                + "sum(today_finish_num) as today_finish_num,sum(total_commit_num) as total_commit_num, \n"
                +" sum(total_load) as total_load,sum(total_finish_load) as total_finish_load,sum(today_finish_load) as today_finish_load, \n"
                + "sum(today_commit_num) as today_commit_num\n"
                + "from t_task_day_data\n"
                + "where company_id=? and iteration_id=? and object_type in("+objectTypes+") and stat_date>=? and stat_date<=?\n"
                + "group by stat_date,iteration_id,company_id,project_id\n"
                + "order by stat_date asc\n";
        return queryList(TaskDayDataInfo.class, sql, companyId, iterationId,  startDate, endDate);
    }

    /**
     * @param query
     * @param countField
     * @return
     */
    public List<AccoutBugStat> getAccoutTaskStatList(AccoutBugStatQuery query, String countField) {
        StringBuilder sql = new StringBuilder();
        sql.append("select account_id,i2.name as account_name,count(1) as " + countField + " from t_task_owner a\n");
        sql.append("inner join t_task i1 on i1.id=a.task_id\n");
        sql.append("inner join t_account i2 on i2.id=a.account_id\n");
        sql.append("where 1=1 and i1.is_delete=false and i1.company_id=" + query.companyId + " ");
        List<Object> values = new ArrayList<>();
        if (query.projectId != null) {
            sql.append(" and i1.project_id= " + query.projectId);
        }

        if (!BizUtil.isNullOrEmpty(query.projectIdList)) {
            sql.append(" and i1.project_id in (\n");
            for (Integer projectId : query.projectIdList) {
                sql.append(projectId).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") ");
        }

        if (query.accountIdList != null && query.accountIdList.size() > 0) {
            sql.append(" and a.account_id in(\n");
            for (Integer accountId : query.accountIdList) {
                sql.append(accountId).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") ");

        }
        if (query.objectType != null) {
            sql.append(" and i1.object_type= ").append(query.objectType);
        }
        if (query.isReOpen != null) {
            sql.append(" and i1.reopen_count>0 ");
        }
        if (query.createTimeStart != null) {
            sql.append(" and i1.create_time>=? ");
            values.add(query.createTimeStart);
        }
        if (query.createTimeEnd != null) {
            sql.append(" and i1.create_time<? ");
            values.add(query.createTimeEnd);
        }
        sql.append(" group by a.account_id,i2.name ");
        return queryList(AccoutBugStat.class, sql.toString(), values.toArray());
    }

    //
    public List<AccoutBugStat> getAccoutBugStatList(AccoutBugStatQuery query, String countField) {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.create_account_id as account_id,i2.name as account_name,count(distinct(a.task_id)) as " + countField + " from t_task_status_change_log a\n");
        sql.append("inner join t_task i1 on i1.id=a.task_id\n");
        sql.append("inner join t_account i2 on i2.id=a.create_account_id\n");
        sql.append("where 1=1 and i1.is_delete=false and i1.company_id= " + query.companyId + " ");
        List<Object> values = new ArrayList<>();
        if (query.projectId != null) {
            sql.append(" and i1.project_id= " + query.projectId);
        }

        if (!BizUtil.isNullOrEmpty(query.projectIdList)) {
            sql.append(" and i1.project_id in (\n");
            for (Integer projectId : query.projectIdList) {
                sql.append(projectId).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") ");
        }

        if (query.accountIdList != null && query.accountIdList.size() > 0) {
            sql.append(" and a.create_account_id in(\n");
            for (Integer accountId : query.accountIdList) {
                sql.append(accountId).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");

        }
        if (query.objectType != null) {
            sql.append(" and i1.object_type= " + query.objectType);
        }
        if (query.isReOpen != null) {
            sql.append(" and i1.reopen_count>0 ");
        }
        if (query.createTimeStart != null) {
            sql.append(" and i1.create_time>=? ");
            values.add(query.createTimeStart);
        }
        if (query.createTimeEnd != null) {
            sql.append(" and i1.create_time<? ");
            values.add(query.createTimeEnd);
        }
        sql.append(" group by a.create_account_id,i2.name ");
        return queryList(AccoutBugStat.class, sql.toString(), values.toArray());
    }
}
