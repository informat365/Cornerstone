package cornerstone.biz.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import cornerstone.biz.domain.GlobalConfig;
import jazmin.core.app.AppException;

import java.util.HashMap;
import java.util.Map;

public class HttpRollback {

    /**
     * url
     */
    public String url;

    /**
     * 成功状态
     */
    public Integer successStatus;

    /**
     * 映射
     */
    public Map<String, String> mappings;

    /**
     * 请求头集
     */
    public Map<String, String> headers;

    /**
     * 回掉信息类型
     */
    public String rollbackInfoType;

    /**
     * 结果类型
     * @param args
     */
    public String resultType;

    /**
     * 获取回掉信息
     * @param context
     * @param rollbackInfo
     * @param <T>
     * @return
     */
    public static <T> T callback(String context, String rollbackInfo) {
        int successStatus = 200;
        String infoName = "info";
        HttpRollback httpRollback = JSON.parseObject(context, HttpRollback.class);

        // info信息默认为string
        String rollbackInfoType = StrUtil.blankToDefault(httpRollback.rollbackInfoType, "String");

        Map<String, String> mappings = httpRollback.mappings;

        // 成功状态码 (默认)
        if (null != httpRollback.successStatus) {
            successStatus = httpRollback.successStatus;
        }
        String url = httpRollback.url;

        String reqInfoName = mappings.get(infoName);

        Method method = Method.valueOf(StrUtil.blankToDefault(mappings.get("method"), "post").toUpperCase());

        HttpRequest request = HttpUtil.createRequest(method, url);
        if (CollUtil.isNotEmpty(httpRollback.headers)) {
            request.addHeaders(httpRollback.headers);
        }

        // 非get请求时 将对应
        if (StrUtil.isNotBlank(rollbackInfo)) {
            if (Method.GET.equals(method)) {
                int questionMarkIdx = url.indexOf("?");
                if (questionMarkIdx > -1) {
                    String params = url.substring(questionMarkIdx + 1);
                    int andMarkIdx = params.lastIndexOf("&");
                    if (andMarkIdx > -1 && andMarkIdx != params.length() - 1) {
                        url += "&";
                    }
                } else {
                    url += "?";
                }
                StringBuilder sbu = new StringBuilder();
                // 如果是map类型
                if ("Map".equalsIgnoreCase(rollbackInfoType)) {
                    Map<String, Object> map = JSON.parseObject(rollbackInfo, HashMap.class);
                    map.forEach((k, v) -> {
                        if (0 != sbu.length()) {
                            sbu.append("&");
                        }
                        sbu.append(k).append("=").append(v);
                    });
                } else {
                    sbu.append(StrUtil.isBlank(reqInfoName) ? "info": reqInfoName)
                            .append("=").append(rollbackInfo);
                }
                url += sbu;
                request.setUrl(url);
            } else {
                String rollbackPlaceholder = "String".equalsIgnoreCase(rollbackInfoType) ? "\"" : "";
                String body = StrUtil.isBlank(reqInfoName) ? rollbackInfo :
                        new StringBuilder("{\"").append(reqInfoName).append("\":")
                                .append(rollbackPlaceholder)
                                .append(rollbackInfo)
                                .append(rollbackPlaceholder)
                                .append("}")
                                .toString();
                request.body(body);
            }
        }
        HttpResponse resp = request.execute();

        int status = resp.getStatus();
        if (status != successStatus) {
            throw new AppException("状态码错误，期望状态码: " + successStatus + "，实际状态码: " + status);
        }
        String resultType = StrUtil.blankToDefault(httpRollback.resultType, "Object");

        String body = resp.body();
        if (StrUtil.isBlank(body)) {
            return null;
        }
        if (!"Object".equalsIgnoreCase(resultType)) {
            return (T) body;
        }
        Map<String, Object> result = JSON.parseObject(body, Map.class);
        String successCodeKey = "successCodeKey";
        successCodeKey = mappings.get(successCodeKey);

        String successCode = "successCode";
        successCode = mappings.get(successCode);

        // 如果有指定成功状态 那么校验返回状态是否成功
        if (StrUtil.isNotBlank(successCodeKey) && StrUtil.isNotBlank(successCode)) {
            Object o = result.get(successCodeKey);
            if (!successCode.equals(o)) {
                throw new AppException("调用失败，获取状态码与期望状态码不一致，状态码：" + o + ", 期望状态码: "+ successCode);
            }
        }
        String resultKey = "resultKey";
        resultKey = StrUtil.blankToDefault(mappings.get(resultKey), resultKey);
        return (T) result.get(resultKey);
    }

    
    
}
