package cornerstone.biz.util;

import java.util.HashMap;
import java.util.Map;

public class TaskUtil {

    private static final Map<String, String> map = new HashMap<>(32);

    static {
        map.put("releaseName", "release_id");
        map.put("priorityName", "priority");
        map.put("categoryIdList", "category_id_list");
        map.put("createAccountName", "create_account_id");
        map.put("createTime", "create_time");
        map.put("endDays", "end_days");
        map.put("name", "name");
        map.put("subSystemName", "sub_system_id");
        map.put("finishTime", "finish_time");
        map.put("workLoad", "work_load");
        map.put("startDays", "start_days");
        map.put("workTime", "work_time");
        map.put("startDate", "start_date");
//        map.put("versionId", "version_id");
        map.put("endDate", "end_date");
        map.put("updateTime", "update_time");
        map.put("statusName", "status");
        map.put("expectEndDate", "expect_end_date");
        map.put("ownerAccountName", "owner_account_id_list");
        map.put("progress", "progress");
        map.put("iterationName", "iteration_id");
        map.put("stageName", "stage_id");
        map.put("expectWorkTime", "expect_work_time");
        map.put("repositoryName", "repository_id");
    }


    /**
     * stageName -> stage_id
     * 自定义对象类型字段字段转数据库字段
     */
    public static String getColumnName(String defineFieldName) {
        return map.getOrDefault(defineFieldName, defineFieldName);
    }

    /**
     * stageName -> stageId
     */
    public static String getDomainFieldName(String defineFieldName) {
        return getCamlseName(getColumnName(defineFieldName));
    }

    /**
     * stageId->stageName
     * 实体类字段转自定义对象类型字段
     */
    public static String getDefineFieldName(String domainFieldName) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            String cname = getCamlseName(v);
            if (domainFieldName.equals(cname)) {
                return k;
            }
        }
        return null;
    }

    private static String getCamlseName(String name) {
        char[] chars = name.toCharArray();
        StringBuilder sbr = new StringBuilder();
        for (char c : chars) {
            if ('_' != c) {
                if (Character.isUpperCase(c)) {
                    sbr.append(Character.toLowerCase(c));
                } else {
                    sbr.append(c);
                }
            }
        }
        return sbr.toString();
    }
}
