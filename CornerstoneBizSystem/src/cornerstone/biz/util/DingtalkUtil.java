package cornerstone.biz.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import cornerstone.biz.domain.DingtalkAttendance;
import cornerstone.biz.domain.DingtalkMember;
import cornerstone.biz.domain.GlobalConfig;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 钉钉考勤接入工具类
 *
 * @author yaop
 */
public class DingtalkUtil {

    private static Logger logger = LoggerFactory.getLogger(DingtalkUtil.class);

    private static String accessToken;

    private static Date expireTime;

    private static final String SPLITOR = "_";

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String tokenUrl ="%s/gettoken?appkey=%s&appsecret=%s";

    /**
     * 查询全部部门ID
     */
    private static String url = "%s/topapi/v2/department/listsubid?access_token=%s";
    /**
     * 根据部门ID查询成员id列表
     */
    private static String listidUrl = "%s/topapi/user/listid?access_token=%s";
    /**
     * 根据成员ID查询详情
     */
    private static String userDetailUrl = "%s/topapi/v2/user/get?access_token=%s";
    /**
     * 考勤数据
     */
    private static String attendanceUrl = "%s/attendance/list?access_token=%s";

    private static String default_host = "https://oapi.dingtalk.com";
    private static Map<String, Integer> cmap = new HashMap<>(16);

    static {
        cmap.put("ATM", 1);
        cmap.put("BEACON", 2);
        cmap.put("DING_ATM", 3);
        cmap.put("USER", 4);
        cmap.put("BOSS", 5);
        cmap.put("APPROVE", 6);
        cmap.put("SYSTEM", 7);
        cmap.put("AUTO_CHECK", 8);

        cmap.put("Normal", 1);
        cmap.put("Outside", 2);
        cmap.put("NotSigned", 3);

        cmap.put("Normal", 1);
        cmap.put("Early", 2);
        cmap.put("Late", 3);
        cmap.put("SeriousLate", 4);
        cmap.put("Absenteeism", 5);
//        cmap.put("NotSigned", 6);

        cmap.put("OnDuty", 1);
        cmap.put("OffDuty", 2);
    }

    private DingtalkUtil() {
    }

    public static class DingtalkResp {
        public int errcode;
        public String errmsg;
        public String access_token;
        public JSONObject result;
        public JSONArray recordresult;
        public boolean hasMore;
    }

    public static class DingtalkUserResp {
        /**
         * 钉钉ID
         */
        public String userid;
        /**
         * 姓名
         */
        public String name;
        /**
         * 手机号
         */
        public String mobile;
        /**
         * 工号
         */
        public String job_number;
        /**
         * 职位
         */
        public String title;

    }

    public static class DingtalkAttendanceResp {
        /**
         * 数据来源
         * ATM：考勤机打卡（指纹/人脸打卡）
         * BEACON：IBeacon
         * DING_ATM：钉钉考勤机（考勤机蓝牙打卡）
         * USER：用户打卡
         * BOSS：老板改签
         * APPROVE：审批系统
         * SYSTEM：考勤系统
         * AUTO_CHECK：自动打卡
         */
        public String sourceType;
        /**
         * 打卡基准时间
         */
        public Date baseCheckTime;
        /**
         * 实际打卡时间
         */
        public Date userCheckTime;
        /**
         * 位置结果
         * Normal：范围内
         * Outside：范围外
         * NotSigned：未打卡
         */
        public String locationResult;
        /**
         * 打卡结果
         * Normal：正常
         * Early：早退
         * Late：迟到
         * SeriousLate：严重迟到
         * Absenteeism：旷工迟到
         * NotSigned：未打卡
         */
        public String timeResult;
        /**
         * 考勤类型
         * OnDuty：上班
         * OffDuty：下班
         */
        public String checkType;
        /**
         * 打卡人ID
         */
        public String userId;
        /**
         * 工作日
         */
        public Date workDate;
    }


    /**
     * 查询部门下的全部成员
     */
    public static List<DingtalkMember> getAllDepartmentUserList() {
        List<DingtalkMember> list = new ArrayList<>();
        refreshAccessToken();

        Map<String, Integer> query = ImmutableMap.of("dept_id", 1);
        String deptResp = URLUtil.httpPostWithBody(String.format(url,GlobalConfig.getValue("dingtalk.host",default_host), accessToken), JSONUtil.toJson(query));
        DingtalkResp deptQueryResp = JSONUtil.fromJson(deptResp, DingtalkResp.class);
        checkResult(deptQueryResp);
        JSONArray array = deptQueryResp.result.getJSONArray("dept_id_list");
        if (BizUtil.isNullOrEmpty(array)) {
            logger.info("query for empty departments...");
            return list;
        }

        for (Object o : array) {
            Map<String, Object> listIdQuery = ImmutableMap.of("dept_id", o);
            String deptUserIdRespJson = URLUtil.httpPostWithBody(String.format(listidUrl,GlobalConfig.getValue("dingtalk.host",default_host), accessToken), JSONUtil.toJson(listIdQuery));
            DingtalkResp deptUserIdResp = JSONUtil.fromJson(deptUserIdRespJson, DingtalkResp.class);
            checkResult(deptUserIdResp);
            JSONArray userIdArray = deptUserIdResp.result.getJSONArray("userid_list");
            //System.out.println(JSONUtil.toJson(userIdArray));
            if (!BizUtil.isNullOrEmpty(userIdArray)) {
                //查询成员详情
                for (Object userId : userIdArray) {
                    Map<String, Object> useDetailQuery = ImmutableMap.of("userid", userId);
                    String userDetailRespJson = URLUtil.httpPostWithBody(String.format(userDetailUrl,GlobalConfig.getValue("dingtalk.host",default_host), accessToken), JSONUtil.toJson(useDetailQuery));
                    DingtalkResp userDetailResp = JSONUtil.fromJson(userDetailRespJson, DingtalkResp.class);
                    checkResult(userDetailResp);
                    DingtalkUserResp user = JSONUtil.fromJson(JSONUtil.toJson(userDetailResp.result), DingtalkUserResp.class);
                    DingtalkMember member = new DingtalkMember();
                    member.dingtalkId = user.userid;
                    member.jobNumber = user.job_number;
                    member.name = user.name;
                    member.mobileNo = user.mobile;
                    member.title = user.title;
                    list.add(member);
                }
            }
        }
        return list;
    }


    /**
     * 查询上个月的考勤数据
     */
    public static List<DingtalkAttendance> getAttendanceList(List<DingtalkMember> memberList,Date date) {
        List<String> userIdList = new ArrayList<>();
        Map<String,DingtalkMember> dingtalkAccountMap = new HashMap<>(32);
        for (DingtalkMember member : memberList) {
            userIdList.add(member.dingtalkId);
            dingtalkAccountMap.put(member.dingtalkId,member);
        }
        refreshAccessToken();
        List<DingtalkAttendance> list = new ArrayList<>();
        //钉钉一次最多查询50个人，跨度7天，每次返回50条
        date= DateUtil.getBeginOfMonth(date);
        Date lastMonthEnd = new Date(date.getTime()-1);
        Date lastMonthBegin = DateUtil.getBeginOfMonth(lastMonthEnd);
//        Date lastMonthBegin = DateUtil.getBeginOfMonth(-1);
//        Date lastMonthEnd = new Date(DateUtil.getBeginOfMonth().getTime() - 1);
        boolean hasMore = true;
        Date startDate = lastMonthBegin;
        Date endDate = DateUtil.getNextDay(startDate, 7);
        endDate = new Date(endDate.getTime()-1);
        int offset = 0;
        while (hasMore) {
            Map<String, Object> query = new HashMap<>();
            query.put("workDateFrom", fullFormat.format(startDate));
            query.put("workDateTo", fullFormat.format(endDate));
            query.put("userIdList", userIdList);
            query.put("offset", offset);
            query.put("limit", 50);
            String attendanceJson = URLUtil.httpPostWithBody(String.format(attendanceUrl,GlobalConfig.getValue("dingtalk.host",default_host), accessToken), JSONUtil.toJson(query));
            DingtalkResp attendanceResp = JSONUtil.fromJson(attendanceJson, DingtalkResp.class);
            checkResult(attendanceResp);
            JSONArray records = attendanceResp.recordresult;
            for (Object record : records) {
                DingtalkAttendanceResp attendanceRes = JSONUtil.fromJson(JSONUtil.toJson(record), DingtalkAttendanceResp.class);
                DingtalkAttendance attendance = new DingtalkAttendance();
                attendance.accountId = dingtalkAccountMap.get(attendanceRes.userId).accountId;
                attendance.dingtalkMemberId = dingtalkAccountMap.get(attendanceRes.userId).id;
                attendance.dingtalkId = attendanceRes.userId;
                attendance.workDate = attendanceRes.workDate;
                attendance.sourceType = cmap.get(attendanceRes.sourceType);
                if ("OnDuty".equals(attendanceRes.checkType)) {
                    attendance.amBaseTime = attendanceRes.baseCheckTime;
                    attendance.amUserTime = attendanceRes.userCheckTime;
                    attendance.amTimeResult = cmap.get(attendanceRes.timeResult);
                    attendance.amLocation = cmap.get(attendanceRes.locationResult);
                    if ("NotSigned".equals(attendanceRes.timeResult)) {
                        attendance.amTimeResult = 6;
                    }
                } else if ("OffDuty".equals(attendanceRes.checkType)) {
                    attendance.pmBaseTime = attendanceRes.baseCheckTime;
                    attendance.pmUserTime = attendanceRes.userCheckTime;
                    attendance.pmTimeResult = cmap.get(attendanceRes.timeResult);
                    attendance.pmLocation = cmap.get(attendanceRes.locationResult);
                    if ("NotSigned".equals(attendanceRes.timeResult)) {
                        attendance.pmTimeResult = 6;
                    }
                }

                list.add(attendance);
            }
            if (attendanceResp.hasMore) {
                offset += 50;
            } else {
                if (DateUtil.getDayDiff(endDate, lastMonthEnd) <= 0) {
                    hasMore = false;
                } else {
                    startDate = DateUtil.getNextDay(startDate, 7);
                    startDate = DateUtil.min(startDate, lastMonthEnd);
                    endDate = DateUtil.getNextDay(endDate, 7);
                    endDate = new Date(endDate.getTime()-1);
                    endDate = DateUtil.min(endDate, lastMonthEnd);
                    offset = 0;
                }
            }
        }

        List<DingtalkAttendance> attendanceList = new ArrayList<>();
        //将早晚考勤数据合并到一条记录
        if (!BizUtil.isNullOrEmpty(list)) {
            //key-> userId_workday
            Map<String, List<DingtalkAttendance>> map = list.stream().collect(Collectors.groupingBy(k -> k.dingtalkId + SPLITOR + formatter.format(k.workDate)));
            map.forEach((mixKey, attendances) -> {
                //合并考勤时间
                DingtalkAttendance dance = attendances.get(0);
                if (attendances.size() > 1) {
                    DingtalkAttendance another = attendances.get(1);
                    if (dance.amTimeResult == 0) {
                        dance.amTimeResult = another.amTimeResult;
                        dance.amBaseTime = another.amBaseTime;
                        dance.amUserTime = another.amUserTime;
                        dance.amLocation = another.amLocation;
                    } else if (dance.pmTimeResult == 0) {
                        dance.pmTimeResult = another.pmTimeResult;
                        dance.pmBaseTime = another.pmBaseTime;
                        dance.pmUserTime = another.pmUserTime;
                        dance.pmLocation = another.pmLocation;
                    }
                }
                attendanceList.add(dance);
            });
        }
        return attendanceList;
    }


    private static void refreshAccessToken() {
        if (BizUtil.isNullOrEmpty(accessToken) || (null != expireTime && expireTime.before(new Date()))) {
            String json = URLUtil.httpGet(String.format(tokenUrl,
                    GlobalConfig.getValue("dingtalk.host",default_host),
                    GlobalConfig.getValue("dingtalk.attendance.appKey", "ding0uozsgaffqf82r4m"),
                    GlobalConfig.getValue("dingtalk.attendance.appSecret", "cMAjWagWC7HTzkMWQ6xxIhop1WcWYoJ-h8VYImTHXpdKGZT5_7wzegmxf-Ychbp9")));
            DingtalkResp resp = JSONUtil.fromJson(json, DingtalkResp.class);
            checkResult(resp);
            accessToken = resp.access_token;
            //有效期内重复获取自动续期
            expireTime = DateUtil.getNextSecond(7000);
            logger.info("accessToken:{} accessTokenExpireTime:{}", accessToken, expireTime);
        }
    }

    private static void checkResult(DingtalkResp resp) {
        int errcode = resp.errcode;
        String errmsg = resp.errmsg;
        if (errcode != 0) {
            logger.error("checkResult :{}", errmsg);
            throw new AppException(errmsg);
        }
    }


}
