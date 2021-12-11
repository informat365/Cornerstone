package cornerstone.biz.srv;

import cornerstone.biz.dao.StatDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.Category.CategoryQuery;
import cornerstone.biz.domain.ChangeLog.ChangeLogInfo;
import cornerstone.biz.domain.ChangeLog.ChangeLogQuery;
import cornerstone.biz.domain.DashboardCard.DashboardCardInfo;
import cornerstone.biz.domain.Project.ProjectInfo;
import cornerstone.biz.domain.Project.ProjectQuery;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.Task.TaskQuery;
import cornerstone.biz.domain.TaskDayData.TaskDayDataInfo;
import cornerstone.biz.domain.TaskDayData.TaskDayDataQuery;
import cornerstone.biz.domain.TaskStat.TaskNumStat;
import cornerstone.biz.domain.TaskStat.TaskNumTreeStat;
import cornerstone.biz.domain.TaskStatDayData.TaskStatDayDataQuery;
import cornerstone.biz.domain.card.*;
import cornerstone.biz.domain.card.ChartBar.ChartData;
import cornerstone.biz.util.BizUtil;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.StatUtil;
import cornerstone.biz.util.StatUtil.StatResult;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.smartjdbc.Query;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 统计相关Service
 *
 * @author cs
 */
public class StatService extends CommService {

    //
    private static Logger logger = LoggerFactory.get(StatService.class);
    //
    @AutoWired
    StatDAO statDAO;

    /**
     * @param iteration
     * @return
     */
    public List<TaskDayDataInfo> getTaskDayDataInfoListByIteration(ProjectIteration iteration) {
        TaskDayDataQuery query = new TaskDayDataQuery();
        query.companyId = iteration.companyId;
        query.iterationId = iteration.id;
        query.objectType = 0;// 不区分objectType
        query.statDateStart = iteration.startDate;
        query.statDateEnd = iteration.endDate;
        query.pageSize = Integer.MAX_VALUE;
        query.statDateSort = Query.SORT_TYPE_ASC;
        return statDAO.getList(query);
    }

    public List<TaskDayDataInfo> getTaskDayDataInfoListByProject(Project project) {
        TaskDayDataQuery query = new TaskDayDataQuery();
        query.companyId = project.companyId;
        query.objectType = 0;// 不区分objectType
        query.projectId = project.id;
        if (null != project.startDate) {
            query.statDateStart = project.startDate;
        }
        if (null != project.endDate) {
            query.statDateEnd = project.endDate;
        }
        query.pageSize = Integer.MAX_VALUE;
        query.statDateSort = Query.SORT_TYPE_ASC;
        return statDAO.getList(query);
    }

    /**
     * 根据迭代查询代码提交统计
     */
    public ScmCommitStatInfo getScmCommitStatInfo(Account account, int iterationId) {
        ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, iterationId);
        checkPermissionForProjectAccess(account, iteration.projectId);
        //
        ScmCommitStatInfo info = new ScmCommitStatInfo();
        List<TaskDayDataInfo> dataList = getTaskDayDataInfoListByIteration(iteration);
        for (TaskDayDataInfo e : dataList) {
            String item = DateUtil.formatDate(e.statDate, "yyyy-MM-dd");
            info.totalList.add(createDayStatResult(item, (long) e.totalCommitNum));
            info.dailyList.add(createDayStatResult(item, (long) e.todayCommitNum));
        }
        //
        List<LongKeyValue> keyValues = statDAO.getScmCommitGroupByAuthor(iterationId);
        for (LongKeyValue e : keyValues) {
            info.authorList.add(createDayStatResult(e.key, e.value));
        }
        //
        return info;
    }

    /**
     * 根据项目代码提交统计
     */
    public ScmCommitStatInfo getScmCommitStatInfoByProjectId(Account account, int projectId) {
        Project project = dao.getExistedById(Project.class, projectId);
        checkPermissionForProjectAccess(account, project);
        //
        ScmCommitStatInfo info = new ScmCommitStatInfo();
        List<TaskDayDataInfo> dataList = getTaskDayDataInfoListByProject(project);

        if (!BizUtil.isNullOrEmpty(dataList)) {
            dataList.sort((a, b) -> {
                if (a.statDate.getTime() < b.statDate.getTime()) {
                    return -1;
                }
                return 1;
            });
            TreeMap<String, List<TaskDayDataInfo>> datamap = new TreeMap<>();
            dataList.forEach(data -> {
                String key = DateUtil.formatDate(data.statDate, "yyyy-MM-dd");
                datamap.computeIfAbsent(key, v -> new ArrayList<>()).add(data);
            });
//            dataList.stream().collect(Collectors.groupingBy(k->DateUtil.formatDate(k.statDate, "yyyy-MM-dd")));
            datamap.forEach((item, list) -> {
                info.totalList.add(createDayStatResult(item, list.stream().mapToLong(k -> k.totalCommitNum).sum()));
                info.dailyList.add(createDayStatResult(item, list.stream().mapToLong(k -> k.totalCommitNum).sum()));
            });
        }
//        for (TaskDayDataInfo e : dataList) {
//            String item = DateUtil.formatDate(e.statDate, "yyyy-MM-dd");
//            info.totalList.add(createDayStatResult(item,e.totalCommitNum));
//            info.dailyList.add(createDayStatResult(item,  e.todayCommitNum));
//        }
        //
        List<LongKeyValue> keyValues = statDAO.getScmCommitGroupByAuthorProjectId(projectId);
        for (LongKeyValue e : keyValues) {
            info.authorList.add(createDayStatResult(e.key, e.value));
        }
        //
        return info;
    }

    //
    public StatResult createDayStatResult(String item, long value) {
        StatResult bean = new StatResult();
        bean.item = item;
        bean.type = StatResult.TYPE_按日统计;
        bean.values.add((long) value);
        return bean;
    }

    //

    private void setTaskStatDayDataQueryForIteration(TaskStatDayDataQuery query) {
        if (query.statDateStart == null || query.statDateEnd == null) {
            if (query.iterationId != null && query.iterationId > 0) {
                ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, query.iterationId);
                if (query.statDateStart == null) {
                    query.statDateStart = iteration.startDate;
                }
                if (query.statDateEnd == null) {
                    query.statDateEnd = iteration.endDate;
                }
            }
        }
    }

    /**
     * @param account
     * @param query
     * @param field
     * @return
     */
    public List<TaskStatDayData> getTaskDayDataList(Account account, TaskStatDayDataQuery query, String field,
                                                    boolean bu0) {
        setTaskStatDayDataQueryForIteration(query);
        TaskDayDataQuery dataQuery = new TaskDayDataQuery();
        dataQuery.companyId = account.companyId;
        dataQuery.projectId = query.projectId;
        dataQuery.iterationId = query.iterationId;
        dataQuery.objectType = query.objectType;
        dataQuery.statDateStart = query.statDateStart;
        dataQuery.statDateEnd = query.statDateEnd;
        dataQuery.pageSize = Integer.MAX_VALUE;
        List<TaskDayData> list = null;
        if (dataQuery.objectType != null) {
            list = dao.getList(dataQuery);
        } else {
            list = statDAO.getTaskStatDayDataWithNoObjectType(dataQuery);
        }
        //
        Date startDate = query.statDateStart;
        Date endDate = query.statDateEnd;
        if (startDate == null && endDate == null) {
            startDate = DateUtil.getBeginOfDay(DateUtil.getNextDay(-30));
            endDate = DateUtil.getNextDay(DateUtil.getToday(), -1);
        }
        List<TaskStatDayData> result = new ArrayList<>();
        for (TaskDayData e : list) {
            if (startDate == null || startDate.after(e.statDate)) {
                startDate = e.statDate;
            }
            if (endDate == null || endDate.before(e.statDate)) {
                endDate = e.statDate;
            }
            TaskStatDayData data = new TaskStatDayData();
            data.statDate = e.statDate;
            data.num = (Long) BizUtil.getFieldValue(e, field);
            result.add(data);
        }
        return StatUtil.buDays(TaskStatDayData.class,
                startDate, endDate, result, "statDate", bu0, "num");
    }

    //
    public void makeDashboardCardInfo(Account account, DashboardCard bean) {
        Object cardData = null;
        //
        TaskQuery taskQuery = new TaskQuery();
        taskQuery.pageSize = Integer.MAX_VALUE;
        setupQuery(account, taskQuery);
        if (bean.projectId > 0) {
            taskQuery.projectId = bean.projectId;
        }
        if (bean.objectType > 0) {
            taskQuery.objectType = bean.objectType;
        }
        if (bean.iterationId > 0) {
            taskQuery.iterationId = bean.iterationId;
        }
        if (bean.filterId > 0) {
            taskQuery.filterId = bean.filterId;
        }
        TaskStatDayDataQuery query = new TaskStatDayDataQuery();
        query.pageSize = Integer.MAX_VALUE;
        setupQuery(account, query);
        if (bean.projectId > 0) {
            query.projectId = bean.projectId;
        }
        if (bean.objectType > 0) {
            query.objectType = bean.objectType;
        }
        if (bean.iterationId > 0) {
            query.iterationId = bean.iterationId;
        }
        //
        if (bean.type == DashboardCardInfo.TYPE_数据报表) {
            if (bean.chartId == DashboardCardInfo.CHARTID_状态分布图) {
                Map<Integer, ProjectStatusDefine> statusDefineMap = getProjectStatusDefineMap(bean.projectId,
                        bean.objectType);
                List<TaskStatDayData> list = statDAO.getTaskCurrStatusDistributeList(query);
                ChartBar chartBar = new ChartBar();
                for (TaskStatDayData e : list) {
                    ProjectStatusDefine define = statusDefineMap.get(e.status);
                    if (define == null) {
                        continue;
                    }
                    e.statusName = define.name;
                    ChartData data = new ChartData();
                    data.name = e.statusName;
                    data.value = e.num;
                    chartBar.dataList.add(data);
                }
                cardData = chartBar;
            }
            //
            if (bean.chartId == DashboardCardInfo.CHARTID_责任人分布图) {
                List<AccountTaskNum> taskInfos = dao.getAccountTaskNum(account, taskQuery, null);
                ChartBar chartBar = new ChartBar();
                for (AccountTaskNum e : taskInfos) {
                    ChartData data = new ChartData();
                    data.name = e.accountName;
                    data.value = e.num;
                    chartBar.dataList.add(data);
                }
                cardData = chartBar;
            }
            //
            if (bean.chartId == DashboardCardInfo.CHARTID_创建人分布图) {
                List<TaskStatDayData> list = statDAO.getTaskCreateAccountDistributeList(query);
                ChartBar chartBar = new ChartBar();
                for (TaskStatDayData e : list) {
                    Account a = dao.getExistedById(Account.class, e.createAccountId);
                    ChartData data = new ChartData();
                    data.name = a.name + "(" + a.userName + ")";
                    data.value = e.num;
                    chartBar.dataList.add(data);
                }
                cardData = chartBar;
            }
            if (bean.chartId == DashboardCardInfo.CHARTID_每日累积数量曲线) {//累积曲线会上下波动 是因为删除的任务不算进去
                ChartBar chartBar = new ChartBar();
                List<TaskStatDayData> list = getTaskDayDataList(account, query, "totalNum", false);
                for (TaskStatDayData e : list) {
                    ChartData data = new ChartData();
                    data.name = DateUtil.formatDate(e.statDate, "yyyy-MM-dd");
                    data.value = e.num;
                    chartBar.dataList.add(data);
                }
                cardData = chartBar;
            }
            if (bean.chartId == DashboardCardInfo.CHARTID_每日新增数量曲线) {
                ChartBar chartBar = new ChartBar();
                List<TaskStatDayData> list = statDAO.getTaskCreateDayDataList(query);
                for (TaskStatDayData e : list) {
                    ChartData data = new ChartData();
                    data.name = DateUtil.formatDate(e.statDate, "yyyy-MM-dd");
                    data.value = e.num;
                    chartBar.dataList.add(data);
                }
                cardData = chartBar;
            }
            if (bean.chartId == DashboardCardInfo.CHARTID_每日完成数量曲线) {
                ChartBar chartBar = new ChartBar();
                List<TaskStatDayData> list = getTaskDayDataList(account, query, "todayFinishNum", true);
                for (TaskStatDayData e : list) {
                    ChartData data = new ChartData();
                    data.name = DateUtil.formatDate(e.statDate, "yyyy-MM-dd");
                    data.value = e.num;
                    chartBar.dataList.add(data);
                }
                cardData = chartBar;
            }
            if (bean.chartId == DashboardCardInfo.CHARTID_完成度 || bean.chartId == DashboardCardInfo.CHARTID_延期率) {
                TaskStat taskStat = getTaskStat0(account, taskQuery);
                ChartPie pie = new ChartPie();
                pie.total = taskStat.totalNum;
                if (bean.chartId == DashboardCardInfo.CHARTID_完成度) {
                    pie.name = "完成度";
                    pie.value = taskStat.finishNum;
                }
                if (bean.chartId == DashboardCardInfo.CHARTID_延期率) {
                    pie.value = taskStat.delayNum;
                    pie.name = "延期率";
                }
                cardData = pie;
            }
        }
        if (bean.type == DashboardCardInfo.TYPE_数字指标) {
            Map<String, Object> result = getTaskInfoList0(account, taskQuery, null, false, true, false);
            int count = (Integer) result.get("count");
            ChartNumber number = new ChartNumber();
            number.value = count;
            cardData = number;
        }
        if (bean.type == DashboardCardInfo.TYPE_项目活动图) {
            ChangeLogQuery changeLogQuery = new ChangeLogQuery();
            if (bean.projectId > 0) {
                changeLogQuery.projectId = bean.projectId;
            }
            List<ChangeLogInfo> dataList = getChangeLogList(account, changeLogQuery);
            ChartActivity activity = new ChartActivity();
            activity.dataList = dataList;
            cardData = activity;
        }
        if (bean.type == DashboardCardInfo.TYPE_项目列表) {
            if (bean.projectIdList != null && bean.projectIdList.size() > 0) {
                ChartProjectList list = new ChartProjectList();
                ProjectQuery projectQuery = new ProjectQuery();
                projectQuery.idInList = BizUtil.convertList(bean.projectIdList);
                projectQuery.pageSize = Integer.MAX_VALUE;
                setupQuery(account, projectQuery);
                List<ProjectInfo> projectInfos = dao.getList(projectQuery);
                list.dataList = projectInfos;
                cardData = list;
            }
        }
        if (bean.type == DashboardCardInfo.TYPE_迭代概览) {
            List<IterationBurnDownData> dataList = getIterationBurnDownChart0(account, bean.iterationId, IterationBurnDownData.TYPE_任务数, 0);
            ChartIteration chartIteration = new ChartIteration();
            chartIteration.dataList = dataList;
            cardData = chartIteration;
        }
        if (cardData != null) {
            bean.cardData = JSONUtil.toJson(cardData);
        }
    }

    //
    public Map<Integer, ProjectStatusDefine> getProjectStatusDefineMap(int projectId, int objectType) {
        List<ProjectStatusDefine> statusDefines = dao.getProjectStatusDefineList(projectId, objectType);
        Map<Integer, ProjectStatusDefine> statusDefineMap = new HashMap<>();
        for (ProjectStatusDefine e : statusDefines) {
            statusDefineMap.put(e.id, e);
        }
        return statusDefineMap;
    }

    //
    // 燃尽图
    public List<IterationBurnDownData> getIterationBurnDownChart0(Account account, int iterationId, int type, int objectType) {
        ProjectIteration iteration = dao.getExistedById(ProjectIteration.class, iterationId);
        checkPermissionForProjectAccess(account, iteration.projectId);
        //
        List<Integer> objectTypeList = new ArrayList<>();
        if (objectType > 0) {
            objectTypeList.add(objectType);
        } else {
            objectTypeList = getStatusBasedObjectTypelist(iteration.projectId);
        }
        if (BizUtil.isNullOrEmpty(objectTypeList)) {
            return new ArrayList<>();
        }
        // 总数
        // int
        // totalNum=bizService.getTotalStatusBaseTaskNumByIteration(iteration,objectTypeList);
        List<TaskDayDataInfo> list = statDAO.getTaskDayDataInfoByIterationId(account.companyId, iterationId,
                iteration.startDate, iteration.endDate, objectTypeList);
        double totalNum = 0;
        Map<Date, TaskDayDataInfo> map = new HashMap<>();
        for (TaskDayDataInfo e : list) {
            map.put(e.statDate, e);
            totalNum = Math.max(totalNum,type==IterationBurnDownData.TYPE_任务数?e.totalNum:e.totalLoad);
        }
        //
        List<IterationBurnDownData> result = makeIterationBurnDownDatas(totalNum, iteration.startDate,
                iteration.endDate, map,type);
        if (logger.isDebugEnabled()) {
            logger.debug("IterationBurnDownData {}", cornerstone.biz.util.DumpUtil.dump(result));
        }
        return result;
    }

    //
    private List<IterationBurnDownData> makeIterationBurnDownDatas(double totalNum, Date startDate, Date endDate,
                                                                   Map<Date, TaskDayDataInfo> map,int type) {
        List<IterationBurnDownData> result = new ArrayList<>();
        int day = DateUtil.differentDays(startDate, endDate);
        double rate = 0;
        if (day > 0) {
            rate = totalNum / (day );
        }
        logger.info("makeIterationBurnDownDatas totalNum:{} startDate:{} endDate:{} day:{} rate:{}", totalNum,
                startDate, endDate, day, rate);
        double unfinishNum = totalNum;
        Date now = new Date();
        for (int i = 0; i <= day; i++) {
            IterationBurnDownData data = new IterationBurnDownData();
            data.statDate = DateUtil.getNextDay(startDate, i);
            data.expectUnfinishNum = totalNum - Math.round(rate * i);
            TaskDayDataInfo dayData = map.get(data.statDate);
            if (dayData != null) {
                unfinishNum = totalNum - (type==IterationBurnDownData.TYPE_任务数? dayData.totalFinishNum:dayData.totalFinishLoad);// 实际未完成数量=总数-已完成数量
                unfinishNum = Math.max(0,unfinishNum);
            }
            if (data.statDate.after(now)) {
                data.unfinishNum = -1;
            } else {
                data.unfinishNum = unfinishNum;
            }
            result.add(data);
        }
        return result;
    }


    private void recursiveCategoryIds(Set<Integer> categoryIds, int categoryId, Map<Integer, Set<Integer>> catemap) {
        Set<Integer> subCategoryIds = catemap.get(categoryId);
        if (!BizUtil.isNullOrEmpty(subCategoryIds)) {
            categoryIds.addAll(subCategoryIds);
            for (Integer subCategoryId : subCategoryIds) {
                recursiveCategoryIds(categoryIds, subCategoryId, catemap);
            }
        }
    }

    //

    private void recursiveDepartmentIds(Set<Integer> depIds, int depId, Map<Integer, Set<Integer>> depmap) {
        Set<Integer> subdepIds = depmap.get(depId);
        if (!BizUtil.isNullOrEmpty(subdepIds)) {
            depIds.addAll(subdepIds);
            for (Integer subdepId : subdepIds) {
                recursiveDepartmentIds(depIds, subdepId, depmap);
            }
        }
    }

    /**
     * 按照项目查询统计
     */
    public TaskStat getTaskStat0(Account account, TaskQuery query) {
        if (query.projectId == null) {
            throw new AppException("参数错误");
        }
        if (query.objectType == null) {
            throw new AppException("参数错误");
        }
        int departmentLevel = null == query.departmentLevel ? 2 : query.departmentLevel;
        query.departmentLevel = null;
        setupQuery(account, query);
        //切换视图会导致分页参数异常
        query.pageIndex = 1;
        query.pageSize = Integer.MAX_VALUE;
        Set<String> includeField = new HashSet<>();
        includeField.add("id");
        includeField.add("status");
        includeField.add("statusName");
        includeField.add("isFinish");
        includeField.add("endDate");
        includeField.add("ownerAccountList");
        includeField.add("createTime");
        includeField.add("categoryIdList");
        Map<String, Object> map = getTaskInfoList0(account, query, includeField, false);
        @SuppressWarnings("unchecked")
        List<TaskInfo> list = (List<TaskInfo>) map.get("list");

        //查询的部门集合
        List<Integer> departmentIds = new ArrayList<>();
        //部门过滤
        Department.DepartmentQuery departmentQuery = new Department.DepartmentQuery();
        departmentQuery.companyId = query.companyId;
        departmentQuery.pageSize = Integer.MAX_VALUE;
        List<Department> departmentList = dao.getAll(departmentQuery);
        //depId->subDepIds
        Map<Integer, Set<Integer>> departmentLevelMap = new TreeMap<>();
        Map<Integer, Department> departmentmap = new HashMap<>();
        departmentList.stream().filter(dep -> dep.type == Department.TYPE_组织架构).sorted(Comparator.comparingInt(dep -> dep.level)).forEach(d -> {
            if (d.id != d.parentId) {
                departmentLevelMap.computeIfAbsent(d.parentId, k -> new HashSet<>()).add(d.id);
            }
            if (d.level == departmentLevel) {
                departmentIds.add(d.id);
            }
            departmentmap.put(d.id, d);
        });
        int maxDepartmentLevel = departmentList.stream().filter(dep -> dep.type == Department.TYPE_组织架构).mapToInt(k -> k.level).max().getAsInt();
        //depId->accountIds
        Map<Integer, Set<Integer>> departmentAccountMap = new HashMap<>();
        departmentList.stream().filter(dep -> dep.type == Department.TYPE_人员).forEach(d -> departmentAccountMap.computeIfAbsent(d.parentId, k -> new HashSet<>()).add(d.accountId));
        Map<Integer, Set<Integer>> finalDepartmentAccountMap = new HashMap<>();
        for (int departmentId : departmentIds) {
            Set<Integer> accountIds = new HashSet<>();
            Set<Integer> allDepartmentIds = new HashSet<>();
            allDepartmentIds.add(departmentId);
            recursiveDepartmentIds(allDepartmentIds, departmentId, departmentLevelMap);
            allDepartmentIds.forEach(depId -> {
                Set<Integer> accountIdSet = departmentAccountMap.get(depId);
                if (!BizUtil.isNullOrEmpty(accountIdSet)) {
                    accountIds.addAll(accountIdSet);
                }
            });
            finalDepartmentAccountMap.put(departmentId, accountIds);
        }


        TaskStat bean = new TaskStat();
        bean.maxDepartmentLevel = maxDepartmentLevel;
        bean.totalNum = list.size();
        ProjectFieldDefine define = dao.getProjectFieldDefineByProjectIdObjectTypeField(query.projectId, query.objectType, "endDate");
        if (define == null) {//如果没有截止时间
            bean.delayNum = -1;
        }
        //
        CategoryQuery categoryQuery = new CategoryQuery();
        categoryQuery.projectId = query.projectId;
        categoryQuery.objectType = query.objectType;
        List<Category> categories = dao.getAll(categoryQuery);
        Map<Integer, String> categoriesMap = new TreeMap<>();
        Map<Integer, Category> cmap = new TreeMap<>();
        Map<Integer, Set<Integer>> categoryLevelMap = new TreeMap<>();
        categories.sort(Comparator.comparingInt(cate -> cate.level));
        categories.sort(Comparator.comparingInt(cate -> cate.id));
        for (Category e : categories) {
            categoriesMap.put(e.id, e.name);
            if (e.id != e.parentId) {
                categoryLevelMap.computeIfAbsent(e.parentId, k -> new HashSet<>()).add(e.id);
            }
            cmap.put(e.id, e);
        }
        Set<Integer> categoryIdList = new HashSet<>();
        if (!BizUtil.isNullOrEmpty(query.categoryIdList)) {
            for (int cateId : query.categoryIdList) {
                categoryIdList.add(cateId);
                recursiveCategoryIds(categoryIdList, cateId, categoryLevelMap);
            }
        } else {
            categoryIdList = categories.stream().map(k -> k.id).collect(Collectors.toSet());
        }
        Map<Integer, List<Category>> categorygroup = categories.stream().collect(Collectors.groupingBy(s -> s.level));
        //
        Map<Integer, TaskNumStat> statusStatMap = new HashMap<>();
        Map<Integer, TaskNumStat> categoryStatMap = new HashMap<>();
        Map<Integer, TaskNumStat> accountStatMap = new HashMap<>();//key accountId

        Map<Integer, Integer> repeatCategory = new HashMap<>();
        Map<Integer, TaskNumTreeStat> categoryChartSumMap = new TreeMap<>();
        //
        for (TaskInfo e : list) {
            //状态分布
            TaskNumStat stat = statusStatMap.get(e.status);
            if (stat == null) {
                stat = new TaskNumStat();
                stat.name = e.statusName;
                statusStatMap.put(e.status, stat);
            }
            //
            //分类分布
            List<TaskNumStat> categoryStats = new ArrayList<>();
            if (e.categoryIdList != null) {
                for (Integer categoryId : e.categoryIdList) {
                    TaskNumStat categoryStat = categoryStatMap.get(categoryId);
                    if (categoryStat == null) {
                        categoryStat = new TaskNumStat();
                        categoryStat.name = categoriesMap.get(categoryId);
                        categoryStatMap.put(categoryId, categoryStat);
                    }
                    categoryStats.add(categoryStat);
                }

                for (Integer categoryId : e.categoryIdList) {
                    if (BizUtil.isNullOrEmpty(query.categoryIdList) || categoryIdList.contains(categoryId)) {
                        categoryChartSumMap.compute(categoryId, (k, v) -> v == null ? new TaskNumTreeStat(categoriesMap.get(categoryId), 1, new ArrayList<>()) : v.increament());
                    }
                }

                //同时存在多个分类且存在父子关系
                if (e.categoryIdList.size() > 1) {
                    List<Category> cs = new ArrayList<>();
                    e.categoryIdList.forEach(cid -> {
                        if (null != cmap.get(cid)) {
                            cs.add(cmap.get(cid));
                        }
                    });
                    //等级倒排
                    if (!BizUtil.isNullOrEmpty(cs)) {
                        cs.sort((a, b) -> b.level - a.level);
                    }
                    for (Category c : cs) {
                        if (!categoryIdList.contains(c.id)) {
                            continue;
                        }
                        boolean loop = true;
                        //判断是否存在父子关系
                        int parentId = c.parentId;
                        while (loop) {
                            if (e.categoryIdList.contains(parentId) && categoryIdList.contains(parentId)) {
                                repeatCategory.compute(parentId, (k, v) -> null == v ? 1 : v + 1);
                            }

                            if (null != cmap.get(parentId)) {
                                if (cmap.get(parentId).level == 1 || parentId == 0) {
                                    loop = false;
                                }
                                parentId = cmap.get(parentId).parentId;
                            } else {
                                loop = false;
                            }
                        }
                    }
                }
            }

            //

            //个人
            List<TaskNumStat> accountStats = new ArrayList<>();
            if (e.ownerAccountList != null && e.ownerAccountList.size() > 0) {
                for (AccountSimpleInfo as : e.ownerAccountList) {
                    TaskNumStat accountstat = accountStatMap.get(as.id);
                    if (accountstat == null) {
                        accountstat = new TaskNumStat();
                        accountstat.name = as.name + "(" + as.userName + ")";
                        accountStatMap.put(as.id, accountstat);
                    }
                    accountStats.add(accountstat);
                }
            } else {//待认领
                TaskNumStat accountstat = accountStatMap.get(0);
                if (accountstat == null) {
                    accountstat = new TaskNumStat();
                    accountstat.name = "待认领";
                    accountStatMap.put(0, accountstat);
                }
                accountStats.add(accountstat);
            }
            stat.totalNum++;
            for (TaskNumStat ts : accountStats) {
                ts.totalNum++;
            }
            for (TaskNumStat ts : categoryStats) {
                ts.totalNum++;
            }
            if (e.isFinish) {
                bean.finishNum++;
                stat.finishNum++;
                for (TaskNumStat ts : accountStats) {
                    ts.finishNum++;
                }
                for (TaskNumStat ts : categoryStats) {
                    ts.finishNum++;
                }
            } else {
                if (e.endDate != null && define != null) {
                    if (DateUtil.differentDays(e.endDate, new Date()) >= 1) {//延期
                        bean.delayNum++;
                        stat.delayNum++;
                        for (TaskNumStat ts : accountStats) {
                            ts.delayNum++;
                        }
                        for (TaskNumStat ts : categoryStats) {
                            ts.delayNum++;
                        }
                    }
                }
            }
        }
        //
        bean.statusStatList = new ArrayList<>(statusStatMap.values());
        bean.accountStatList = new ArrayList<>(accountStatMap.values());
        bean.categoryStatList = new ArrayList<>(categoryStatMap.values());

        List<TaskNumTreeStat> categoryStatSunList = new ArrayList<>();
        List<Integer> decreamentCategoryId = new ArrayList<>();
        //组装数据结构,填充children并去重
        categories.sort((a, b) -> b.level - a.level);
        for (Category category : categories) {
            int parentId = category.parentId;
            TaskNumTreeStat cs = categoryChartSumMap.get(parentId);
            if (null != cs) {
                if (BizUtil.isNullOrEmpty(cs.children)) {
                    cs.children = new ArrayList<>();
                }
                TaskNumTreeStat cus = categoryChartSumMap.get(category.id);
                if (null != cus) {
                    cs.children.add(cus);
                    cs.value += cus.value;
                    Integer count = repeatCategory.get(parentId);
                    if (null != count && count > 0) {
                        //防止重复去重
                        if (!decreamentCategoryId.contains((parentId))) {
                            cs.value -= count;
                            decreamentCategoryId.add(parentId);
                        }
                    }
                }
            }
        }
        for (Map.Entry<Integer, List<Category>> entry : categorygroup.entrySet()) {
            List<Category> cs = entry.getValue();
            if (BizUtil.isNullOrEmpty(categoryStatSunList)) {
                if (BizUtil.isNullOrEmpty(query.categoryIdList)) {
                    cs.forEach(c -> {
                        TaskNumTreeStat chartSun = categoryChartSumMap.get(c.id);
                        if (null != chartSun) {
                            categoryStatSunList.add(chartSun);
                        } else {
                            categoryStatSunList.add(new TaskNumTreeStat(categoriesMap.get(c.id), 0));
                        }
                    });
                } else {
                    cs.forEach(c -> {
                        if (BizUtil.contains(query.categoryIdList, c.id)) {
                            TaskNumTreeStat chartSun = categoryChartSumMap.get(c.id);
                            if (null != chartSun) {
                                categoryStatSunList.add(chartSun);
                            } else {
                                categoryStatSunList.add(new TaskNumTreeStat(categoriesMap.get(c.id), 0));
                            }
                        }
                    });
                }
            }
        }
        bean.categoryStatSunList = categoryStatSunList;

        List<TaskNumStat> departmentStatList = new ArrayList<>();
        //部门分组聚合统计
        departmentIds.forEach(departmentId -> {
            TaskNumStat stat = new TaskNumStat();
            stat.name = departmentmap.get(departmentId).name;
            Set<Integer> accountIdSet = finalDepartmentAccountMap.get(departmentId);
            if (!BizUtil.isNullOrEmpty(accountIdSet)) {
                for (Integer accountId : accountIdSet) {
                    TaskNumStat accountStat = accountStatMap.get(accountId);
                    if (null != accountStat) {
                        stat.totalNum += accountStat.totalNum;
                        stat.delayNum += accountStat.delayNum;
                        stat.finishNum += accountStat.finishNum;
                    }
                }
            }
            departmentStatList.add(stat);
        });
        bean.departmentStatList = departmentStatList;
        //
        return bean;
    }

    //
    public List<ChangeLogInfo> getChangeLogList(Account account, ChangeLogQuery query) {
        setupQuery(account, query);
        if (query.taskId == null && query.projectId == null && query.associatedId == null) {
            throw new AppException("参数错误");
        }
        if (query.taskId != null) {
            checkPermissionForTask(account, query.taskId);
        }
        if (query.projectId != null) {
            checkPermissionForProjectAccess(account, query.projectId);
            query.createTimeStart = DateUtil.getNextDay(-15);//15天前
            query.pageSize = 1000;
        }
        List<ChangeLogInfo> list = dao.getList(query);
        if (query.taskId == null && list.size() > 15) {//取项目变更记录时，忽略掉items
            for (int i = 15; i < list.size(); i++) {
                ChangeLogInfo log = list.get(i);
                log.itemId = null;
                log.items = null;
                log.remark = null;
                log.createAccountImageId = null;
            }
        }
        return list;
    }

    public List<StageBurnDownData> getProjectStageBurnDownChart(Account account, int projectId) {
        List<StageBurnDownData> list = new ArrayList<>();
        List<Stage.StageInfo> stages = dao.getProjectStageList(account.companyId, projectId);
        if (BizUtil.isNullOrEmpty(stages)) {
            return Collections.emptyList();
        }
        Date start = null;
        Date end = null;
        for (Stage.StageInfo stage : stages) {
            if (null == start || stage.startDate.before(start)) {
                start = stage.startDate;
            }
            if (null == end || stage.endDate.after(end)) {
                end = stage.endDate;
            }
        }
        if (null == start || null == end) {
            return Collections.emptyList();
        }
        List<Task> stageTaskList = dao.getStageTaskListByProjectId(projectId);

        if (BizUtil.isNullOrEmpty(stageTaskList)) {
//            Date startDate = new Date();
            LocalDate sd = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate ed = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()).toLocalDate();
            while (sd.isBefore(ed)) {
                StageBurnDownData data = new StageBurnDownData();
                data.statDate = Date.from(sd.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                data.unfinishNum = 0;
                data.expectUnfinishNum = 0;
                list.add(data);
                sd = sd.plusDays(1);
            }
        } else {
//            stageTaskList.sort((obj1, obj2) -> {
//                if (obj1.createTime.before(obj2.createTime)) {
//                    return -1;
//                } else if (obj1.createTime.after(obj2.createTime)) {
//                    return 1;
//                } else {
//                    return 0;
//                }
//            });
            //完成日期分组
            Map<Date, AtomicInteger> endDateMap = new HashMap<>();
            Map<Date, AtomicInteger> finishDateMap = new HashMap<>();
            Map<Date, AtomicInteger> dateMap = new HashMap<>();
            int mapcount = 0;
            int totalcount = stageTaskList.size();
            for (Task task : stageTaskList) {
                if (null != task.endDate) {
                    endDateMap.computeIfAbsent(task.endDate, k -> new AtomicInteger()).incrementAndGet();
                }
                if (null != task.finishTime) {
                    finishDateMap.computeIfAbsent(DateUtil.getBeginOfDay(task.finishTime), k -> new AtomicInteger()).incrementAndGet();
                }
                if (null != task.endDate || null != task.finishTime) {
                    mapcount++;
                }
                if (null != task.endDate && null != task.finishTime) {
                    if (task.endDate.equals(DateUtil.getBeginOfDay(task.finishTime))) {
                        dateMap.computeIfAbsent(task.endDate, k -> new AtomicInteger()).incrementAndGet();
                    }
                }
            }
            //既没有截止时间又没有完成时间的作为初始待完成
            int totalFinishNum = 0;
            int totalEndNum = 0;
            LocalDate sd = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).toLocalDate();
            LocalDate ed = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault()).toLocalDate();
            while (sd.isBefore(ed) || sd.equals(ed)) {
                StageBurnDownData data = new StageBurnDownData();
                Date today = Date.from(sd.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                data.statDate = today;

                //截至当日预期未完成的
                totalEndNum += endDateMap.getOrDefault(today, new AtomicInteger(0)).get();
                data.expectUnfinishNum = totalcount - totalEndNum;
                //截止当日实际未完成的
                totalFinishNum += finishDateMap.getOrDefault(today, new AtomicInteger(0)).get();
                data.unfinishNum = totalcount - totalFinishNum;
                list.add(data);
                sd = sd.plusDays(1);
            }
        }

        return list;
    }

    public List<StageFinishDelayRate> getProjectStageFinishDelayRate0(Account account, int projectId) {
        List<StageFinishDelayRate> list = new ArrayList<>();
        List<Task> stageTaskList = dao.getStageTaskListByProjectIdObjectType(projectId);
        if (BizUtil.isNullOrEmpty(stageTaskList)) {
            return Collections.emptyList();
        }
        Map<Integer, List<Task>> taskObjectMap = stageTaskList.stream().collect(Collectors.groupingBy(k -> k.objectType));
        taskObjectMap.forEach((objectType, tasks) -> {
            StageFinishDelayRate rate = new StageFinishDelayRate();
            rate.objectType = objectType;
            rate.totalNum = tasks.size();
            rate.finishNum = tasks.stream().filter(k -> k.isFinish).count();
            rate.delayNum = rate.totalNum - rate.finishNum;
            list.add(rate);
        });
        return list;
    }
    //
}
