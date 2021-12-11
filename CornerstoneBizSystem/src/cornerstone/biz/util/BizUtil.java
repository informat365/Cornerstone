package cornerstone.biz.util;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

import java.awt.Image;
import java.io.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import com.google.common.base.Joiner;
import cornerstone.biz.domain.*;
import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Sets;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;

import cornerstone.biz.ConstDefine;
import cornerstone.biz.annotations.DomainDefineValid;
import cornerstone.biz.annotations.DomainDefineValid.UniqueKey;
import cornerstone.biz.annotations.DomainFieldValid;
import cornerstone.biz.dao.ITFDAO;
import cornerstone.biz.ssh.ConnectionInfo;
import cornerstone.biz.taskjob.BizTaskJobs;
import cornerstone.biz.util.sensi.SensitiveFilter;
import jazmin.core.app.AppException;
import jazmin.core.job.CronExpression;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.ClassUtils;
import jazmin.util.IOUtil;
import jazmin.util.JSONUtil;
import jazmin.util.MD5Util;
import jazmin.util.RandomUtil;

/**
 * @author cs
 */
public class BizUtil {
    //
    private static Logger logger = LoggerFactory.getLogger(BizUtil.class);

    //
    public static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
    //

    /**
     * 明文密码加密
     *
     * @param password
     * @return
     */
    public static String encryptPassword(String password) {
        return MD5Util.encodeMD5(password);
    }

    public static Field getField(Object bean, String fieldName) {
        try {
            return bean.getClass().getField(fieldName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param bean
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object bean, String fieldName) {
        try {
            return getFieldValue(bean, bean.getClass().getField(fieldName));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取字段注释
     *
     * @return
     */
    public static String getFieldComment(Field filed) {
        DomainFieldValid bean = filed.getAnnotation(DomainFieldValid.class);
        if (bean == null) {
            return null;
        }
        return bean.comment();
    }

    /**
     * @param bean
     * @param f
     * @return
     */
    public static Object getFieldValue(Object bean, Field f) {
        try {
            return f.get(bean);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param bean
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(Object bean, String fieldName, Object value) {
        try {
            Field field = bean.getClass().getField(fieldName);
            if (field != null) {
                field.set(bean, value);
            }
        } catch (Exception e) {
            logger.warn("setFieldValue ERR,Clz:{},field:{},value:{}", bean.getClass().getSimpleName(), fieldName, value);
        }
    }

    /**
     * 返回对象看getByTarget
     *
     * @param array
     * @param fieldName
     * @param target
     * @return
     */
    public static <T> boolean contains(List<T> array, String fieldName, Object target) {
        if (array == null) {
            return false;
        }
        for (Object bean : array) {
            Object v = getFieldValue(bean, fieldName);
            if (v == null) {
                return false;
            }
            if (v.equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param array
     * @param fieldName
     * @param target
     * @return
     */
    public static <T> T getByTarget(List<T> array, String fieldName, Object target) {
        if (array == null) {
            return null;
        }
        for (T bean : array) {
            Object v = getFieldValue(bean, fieldName);
            if (v == null) {
                continue;
            }
            if (v.equals(target)) {
                return bean;
            }
        }
        return null;
    }

    /**
     * 从列表中取出单个字段，逗号分隔[{"name":"111"},{"name":"2222"}=>111,222
     *
     * @param array
     * @param fieldName
     * @return
     */
    public static <T> String getListSingleField(List<T> array, String fieldName) {
        StringBuilder sb = new StringBuilder();
        if (array == null || array.isEmpty()) {
            return sb.toString();
        }
        for (Object bean : array) {
            Object v = getFieldValue(bean, fieldName);
            if (v == null) {
                continue;
            }
            sb.append(v.toString()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * @param myList
     * @param allowList
     * @return
     */
    public static boolean contains(List<Integer> myList, List<Integer> allowList) {
        return contains(convert(myList), convert(allowList));
    }

    /**
     * @param myList
     * @param allowList
     * @return
     */
    public static boolean contains(Set<Integer> myList, Set<Integer> allowList) {
        if (allowList == null || allowList.isEmpty()) {// 如果没有配置允许列表
            return true;
        }
        if (myList == null || myList.isEmpty()) {
            return false;
        }
        for (Integer target : allowList) {
            if (contains(myList, target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param runnable
     */
    public static void excute(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * 判断是否是图片
     *
     * @return
     */
    public static boolean isImage(InputStream is) {
        if (is == null) {
            return false;
        }
        Image img = null;
        try {
            img = ImageIO.read(is);
            return img != null && img.getWidth(null) > 0 && img.getHeight(null) > 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        } finally {
            img = null;
        }
    }

    /**
     * 检查bean数据是否合法
     *
     * @param bean
     */
    public static void checkValid(Object bean) {
        Field[] fields = bean.getClass().getFields();
        for (Field field : fields) {
            try {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                DomainFieldValid define = field.getAnnotation(DomainFieldValid.class);
                if (define == null) {
                    continue;
                }
                Object value = field.get(bean);
                Class<?> fieldType = field.getType();
                String comment = define.comment();
                if (define.required() && value == null) {
                    logger.error("field:{}", field.getName());
                    throw new IllegalArgumentException(define.comment() + "不能为空");
                }
                if (fieldType.equals(String.class)) {
                    String sValue = (String) value;
                    if (sValue != null && define.needTrim()) {//TRIM
                        sValue = sValue.trim();
                        field.set(bean, sValue);
                    }
                    if (define.minValue() > 0) {
                        if (sValue == null || sValue.length() < define.minValue()) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "长度不能小于" + define.minValue() + "个字符");
                        }
                    }
                    if (define.maxValue() > 0) {
                        if (sValue != null && sValue.length() > define.maxValue()) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "长度不能大于" + define.maxValue() + "个字符");
                        }
                    }
                    if (sValue != null && define.needSensiFilter()) {//敏感词过滤
                        sValue = SensitiveFilter.DEFAULT.filter(sValue, '*');
                        field.set(bean, sValue);
                    }
                }
                if (define.minValue() > 0) {
                    int minValue = define.minValue();
                    if (fieldType.equals(short.class)) {
                        if ((((short) value) < minValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能小于" + minValue);
                        }
                    }
                    if (fieldType.equals(int.class)) {
                        if ((((int) value) < minValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能小于" + minValue);
                        }
                    }
                    if (fieldType.equals(long.class)) {
                        if ((((long) value) < minValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能小于" + minValue);
                        }
                    }
                    if (fieldType.equals(float.class)) {
                        if ((((float) value) < minValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能小于" + minValue);
                        }
                    }
                    if (fieldType.equals(double.class)) {
                        if ((((double) value) < minValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能小于" + minValue);
                        }
                    }
                }
                if (define.maxValue() > 0) {
                    int maxValue = define.maxValue();
                    if (fieldType.equals(short.class)) {
                        if ((((short) value) > maxValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能大于" + maxValue);
                        }
                    }
                    if (fieldType.equals(int.class)) {
                        if ((((int) value) > maxValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能大于" + maxValue);
                        }
                    }
                    if (fieldType.equals(long.class)) {
                        if ((((long) value) > maxValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能大于" + maxValue);
                        }
                    }
                    if (fieldType.equals(float.class)) {
                        if ((((float) value) > maxValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能大于" + maxValue);
                        }
                    }
                    if (fieldType.equals(double.class)) {
                        if ((((double) value) > maxValue)) {
                            logger.error("field:{}", field.getName());
                            throw new IllegalArgumentException(comment + "数据错误，不能大于" + maxValue);
                        }
                    }
                }
                if (define.required() && (!StringUtil.isEmpty(define.dataDict()))) {//检查数据字典
                    checkDataDictValueValid(define.dataDict(), (int) value, comment + "数据错误");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    //
    public static void checkDataDictValueValid(String categoryCode, int value, String errorMsg) {
        BizTaskJobs.checkDataDictValueValid(categoryCode, value, errorMsg);
    }

    //
    public static void checkUniqueKeysOnAdd(ITFDAO dao, Object bean) {
        checkUniqueKeysOnAdd(dao, bean, null);
    }

    // 检查唯一索引(新增时)
    public static void checkUniqueKeysOnAdd(ITFDAO dao, Object bean, String errorMsg) {
        DomainDefineValid domainValid = bean.getClass().getAnnotation(DomainDefineValid.class);
        if (domainValid == null) {
            return;
        }
        if (domainValid.uniqueKeys() != null) {
            for (UniqueKey uk : domainValid.uniqueKeys()) {
                DomainUniqueKey key = new DomainUniqueKey();
                key.fields = uk.fields();
                key.values = new Object[key.fields.length];
                for (int i = 0; i < key.fields.length; i++) {
                    key.values[i] = getFieldValue(bean, key.fields[i]);
                }
                Object old = dao.getByUniqueKey(bean.getClass(), key);
                if (old != null) {
                    if (errorMsg == null) {
                        errorMsg = "数据已存在，不能重复添加";
                    }
                    throw new IllegalArgumentException(errorMsg);
                }
            }
        }
    }

    //
    public static void checkUniqueKeysOnUpdate(ITFDAO dao, Object bean, Object old) {
        checkUniqueKeysOnUpdate(dao, bean, old, null);
    }

    // 检查唯一索引(编辑时)
    public static void checkUniqueKeysOnUpdate(ITFDAO dao, Object bean, Object old, String errorMsg) {
        DomainDefineValid domainDefine = bean.getClass().getAnnotation(DomainDefineValid.class);
        if (domainDefine == null) {
            return;
        }
        if (domainDefine.uniqueKeys() != null) {
            for (UniqueKey uk : domainDefine.uniqueKeys()) {
                DomainUniqueKey key = new DomainUniqueKey();
                key.fields = uk.fields();
                key.values = new Object[key.fields.length];
                boolean isChange = false;// 是否有修改唯一索引相关字段
                for (int i = 0; i < key.fields.length; i++) {
                    key.values[i] = getFieldValue(bean, key.fields[i]);
                    if (isChange) {
                        continue;
                    }
                    Object oldValue = getFieldValue(old, key.fields[i]);
                    if (oldValue == null && key.values[i] == null) {
                        continue;
                    }
                    if (oldValue == null || key.values[i] == null) {
                        isChange = true;
                        continue;
                    }
                    if (oldValue.getClass().equals(String.class)) {
                        if (!((String) oldValue).equalsIgnoreCase((String) key.values[i])) {
                            isChange = true;
                        }
                    } else {
                        if (!oldValue.equals(key.values[i])) {
                            isChange = true;
                        }
                    }

                }
                if (isChange) {
                    Object oldObj = dao.getByUniqueKey(bean.getClass(), key);
                    if (oldObj != null) {
                        if (errorMsg == null) {
                            errorMsg = "数据已存在，不能重复添加";
                        }
                        throw new IllegalArgumentException(errorMsg);
                    }
                }
            }
        }
    }

    /**
     * @param list
     * @return
     */
    public static <T> Set<T> convert(List<T> list) {
        if (list == null) {
            return null;
        }
        Set<T> set = new LinkedHashSet<>();
        list.forEach(t -> {
            set.add(t);
        });
        return set;
    }

    public static Set<Integer> convert(int[] list) {
        if (list == null) {
            return null;
        }
        Set<Integer> set = new LinkedHashSet<>();
        for (int t : list) {
            set.add(t);
        }
        return set;
    }

    public static <T> LinkedHashSet<T> convertLinkedHashSet(List<T> list) {
        if (list == null) {
            return null;
        }
        LinkedHashSet<T> set = new LinkedHashSet<>();
        list.forEach(t -> {
            set.add(t);
        });
        return set;
    }

    public static <T> TreeSet<T> convertTreeSet(Set<T> set) {
        if (set == null) {
            return null;
        }
        TreeSet<T> treeSet = new TreeSet<>();
        treeSet.addAll(set);
        return treeSet;
    }

    public static <T> List<T> convert(Set<T> set) {
        if (set == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    /**
     * 去重
     *
     * @param src
     * @return
     */
    public static List<Integer> distinct(List<Integer> src) {
        if (src == null) {
            return null;
        }
        LinkedHashSet<Integer> set = convertLinkedHashSet(src);
        return convert(set);
    }

    /**
     * @param list
     * @return
     */
    public static int[] convertList(List<Integer> list) {
        if (isNullOrEmpty(list)) {
            return null;
        }
        int[] result = new int[list.size()];
        int i = 0;
        for (Integer v : list) {
            if (v == null) {
                v = 0;
            }
            result[i++] = v;
        }
        return result;
    }

    /**
     * @param list
     * @return
     */
    public static int[] convertList(Set<Integer> list) {
        if (isNullOrEmpty(list)) {
            return null;
        }
        int[] result = new int[list.size()];
        int i = 0;
        for (Integer v : list) {
            if (v == null) {
                v = 0;
            }
            result[i++] = v;
        }
        return result;
    }

    /**
     * @param array
     * @param target
     * @return
     */
    public static boolean contains(List<Integer> array, int target) {
        if (array == null) {
            return false;
        }
        for (Integer bean : array) {
            if (bean == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(List<Long> array, long target) {
        if (array == null) {
            return false;
        }
        for (Long bean : array) {
            if (bean == target) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(List<String> array, String target) {
        if (array == null) {
            return false;
        }
        for (String bean : array) {
            if (bean.equals(target)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] array, int target) {
        if (array == null) {
            return false;
        }
        for (Integer bean : array) {
            if (bean == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param array
     * @param target
     * @return
     */
    public static boolean contains(Set<Integer> array, int target) {
        if (array == null) {
            return false;
        }
        for (Integer bean : array) {
            if (bean == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从userName变成UserName
     *
     * @param s
     * @return
     */
    public static String toUpperCaseFirstOne(String s) {
        return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    /**
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    /**
     * @param beforeContent
     * @param afterContent
     * @return
     */
    public static boolean equalString(String beforeContent, String afterContent) {
        if (beforeContent == null && afterContent == null) {
            return true;
        }
        return toString(beforeContent).equals(toString(afterContent));
    }

    /**
     * 会删除<p>标签等其他标签
     *
     * @param htmlContent
     * @return
     */
    public static String cleanHtml(String htmlContent) {
        if (htmlContent == null) {
            return "";
        }
        return new HtmlToPlainText().getPlainText(Jsoup.parse(htmlContent.trim())).trim();
    }

    /**
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 随机生成密码
     *
     * @param len
     * @return
     */
    public static String randomPassword(int len) {
        char[] charr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*.?".toCharArray();
        // //System.out.println("字符数组长度:" + charr.length); //可以看到调用此方法多少次
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int x = 0; x < len; ++x) {
            sb.append(charr[r.nextInt(charr.length)]);
        }
        return sb.toString();
    }

    /**
     * field_123 => 123
     *
     * @param field
     * @return
     */
    public static int getFieldDefineId(String field) {
        return Integer.valueOf(field.substring(6));
    }

    /**
     * @param projectFieldDefineId
     * @return
     */
    public static String getCustomerFieldKey(int projectFieldDefineId) {
        return "field_" + projectFieldDefineId;
    }

    /**
     * 团队成员对象
     *
     * @param projectFieldDefineId
     * @return
     */
    public static String getCustomerFieldKeyForObject(int projectFieldDefineId) {
        return "fieldobject_" + projectFieldDefineId;
    }

    /**
     * 获取复选框的值,逗号分隔
     *
     * @param bean
     * @param fieldDefineId
     * @return
     */
    public static String getCheckBoxValues(Task bean, int fieldDefineId) {
        try {
            String objectKey = getCustomerFieldKey(fieldDefineId);
            Object value = bean.customFields.get(objectKey);
            return getCheckBoxValues(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    /**
     * 获取复选框的值
     *
     * @param value
     * @return
     */
    public static String getCheckBoxValues(Object value) {
        try {
            if (value == null) {
                return "";
            }
            JSONArray array = (JSONArray) value;
            List<String> list = new ArrayList<>();
            for (int j = 0; j < array.size(); j++) {
                String bean = (String) array.get(j);
                list.add(bean);
            }
            return BizUtil.appendFields(list);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    /**
     * 获取自定义字段时间
     *
     * @param value
     * @return
     */
    public static String getCustomeDateString(Object value) {
        try {
            if (value == null) {
                return null;
            }
            return DateUtil.formatDate(getCustomeDate(value), "yyyy-MM-dd");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    public static String getCustomeDateTimeString(Object value) {
        try {
            if (value == null) {
                return null;
            }
            return DateUtil.formatDate(getCustomeDate(value), "yyyy-MM-dd HH:mm");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    /**
     * 获取人员选择列表,逗号分隔
     *
     * @param bean
     * @param fieldDefineId
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getCustomeAccountNames(Task bean, int fieldDefineId) {
        try {
            String objectKey = getCustomerFieldKeyForObject(fieldDefineId);
            Object value = bean.customFields.get(objectKey);
            if (value == null) {
                return "";
            }
            if (value instanceof JSONArray) {
                JSONArray newObjectValue = (JSONArray) value;
                return getAccountNamesFromJsonArray(newObjectValue);
            } else {
                return appendFields((List<AccountSimpleInfo>) value, "name");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    /**
     * 人员选择
     *
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getAccountNamesFromJsonArray(Object value) {
        try {
            if (value == null) {
                return "";
            }
            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                List<AccountSimpleInfo> list = new ArrayList<>();
                for (int j = 0; j < array.size(); j++) {
                    AccountSimpleInfo bean = ((JSONObject) array.get(j)).toJavaObject(AccountSimpleInfo.class);
                    list.add(bean);
                }
                return appendFields(list, "name");
            } else {// List<AccountSimpleInfo>
                return appendFields((List<AccountSimpleInfo>) value, "name");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getIntegerList(Object value) {
        if (value == null) {
            return new ArrayList<>();
        }
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < array.size(); j++) {
                Integer id = (Integer) array.get(j);
                list.add(id);
            }
            return list;
        } else {
            return (List<Integer>) value;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringList(Object value) {
        if (value == null) {
            return new ArrayList<>();
        }
        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            List<String> list = new ArrayList<>();
            for (int j = 0; j < array.size(); j++) {
                String id = (String) array.get(j);
                list.add(id);
            }
            return list;
        } else {
            return (List<String>) value;
        }
    }

    public static List<String> splitList(String val) {
        if (null != val) {
            val = val.trim();
        }
        if (!BizUtil.isNullOrEmpty(val)) {
            val = val.replaceAll("[；，、]", ",");
            return Arrays.asList( val.split(","));
        }
        return Collections.emptyList();
    }

    /**
     * @param value
     * @return
     */
    public static Date getCustomeDate(Object value) {
        try {
            if (value == null) {
                return null;
            }
            if (NumberUtils.isNumber(value.toString())) {
                return new Date((long) value);
            } else {
                Date v = DateUtil.parseDateTimeFromExcel(value.toString());
                if (null != v) {
                    return v;
                }
            }
            return new Date((long) value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    /**
     * 返回 custom_fields->'$."field_11"'
     *
     * @param field         custom_fields
     * @param customerField field_11
     * @return
     */
    public static String getCustomerSqlKey(String field, String customerField) {
        return field + "->'$.\"" + customerField + "\"' ";
    }

    /***
     *
     * @param list
     * @param fieldName
     * @return
     */
    public static <T> String appendFields(List<T> list, String fieldName) {
        if (list == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (T e : list) {
            sb.append(getFieldValue(e, fieldName)).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static <T> String appendFields(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String e : list) {
            sb.append(e).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * @return
     */
    public static String getIndexPath(String key) {
        return GlobalConfig.getValue("lucene.index.path") + File.separator + key;
    }

    /**
     * @param fileId
     * @return
     */
    public static File getArtifactFile(String fileId) {
        String dirPath = GlobalConfig.getValue("pipeline.archive.homeDir");
        return getFile(dirPath, fileId);
    }

    //
    public static File getFile(String fileId) {
        String dirPath = GlobalConfig.fileServiceHomePath;
        return getFile(dirPath, fileId);
    }

    /**
     * @param fileId
     * @return
     */
    private static File getFile(String dirPath, String fileId) {
        File homeDir = new File(dirPath);
        if (homeDir == null || (!homeDir.isDirectory())) {
            logger.error("homeDir:" + homeDir + "," + homeDir.getAbsolutePath());
            throw new AppException("archives存储目录不存在");
        }
        String parent = fileId.charAt(0) + "";
        String child = fileId.charAt(1) + "";
        File file = null;
        try {
            String filePath = parent + File.separator + child + File.separator + fileId;
            file = new File(homeDir, filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            return file;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        }
    }

    /**
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        byte[] buffer = new byte[1024];
        int len;
        try (FileInputStream in = new FileInputStream(file)) {
            digest = MessageDigest.getInstance("MD5");
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static RsaKeyPair genKeyPair() {
        try {
            JSch jsch = new JSch();
            KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            kpair.writePrivateKey(baos);
            String privateKeyString = baos.toString();
            baos = new ByteArrayOutputStream();
            kpair.writePublicKey(baos, "");
            String publicKeyString = baos.toString();
            kpair.dispose();
            //
            RsaKeyPair bean = new RsaKeyPair();
            bean.publicKeyString = publicKeyString;
            bean.privateKeyString = privateKeyString;
            return bean;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }
    }

    public static ConnectionInfo createConnectionInfo(Machine machine) {
        ConnectionInfo info = new ConnectionInfo();
        info.host = machine.host;
        info.name = machine.name;
        if (machine.loginType == CmdbMachine.LOGINTYPE_证书SSH) {
            info.privateKey = machine.privateKey;
        } else {
            info.password = TripleDESUtil.decrypt(machine.password, ConstDefine.GLOBAL_KEY);
        }
        info.port = machine.port;
        info.user = machine.userName;
        info.cmd = machine.cmd;
        return info;
    }

    public static ConnectionInfo createConnectionInfo(CmdbMachine machine) {
        ConnectionInfo info = new ConnectionInfo();
        info.host = machine.outerHost;
        info.name = machine.name;
        if (machine.loginType == CmdbMachine.LOGINTYPE_证书SSH) {
            info.privateKey = machine.privateKey;
        } else {
            info.password = TripleDESUtil.decrypt(machine.password, ConstDefine.GLOBAL_KEY);
        }
        info.port = machine.port;

        info.user = machine.userName;
        return info;
    }

    public static String createSortList(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        List<Field> list = ClassUtils.getFieldList(clazz);
        for (Field field : list) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            sb.append("public int ").append(field.getName()).append("Sort;\n");
        }
        return sb.toString();
    }

    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                IOUtil.closeQuietly(fi);
                IOUtil.closeQuietly(fo);
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    //
    public static void checkValidCustomFieldId(String key) {
        String check = "^field_[0-9]{1,10}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(key);
        if (!matcher.matches()) {
            throw new AppException("自定义字段ID错误 ");
        }
    }

    public static Date nextRunTime(Date lastRunTime, String cron) {
        if (StringUtil.isEmpty(cron)) {
            return null;
        }
        if (lastRunTime == null) {
            lastRunTime = new Date();
        }
        CronExpression ce;
        try {
            ce = new CronExpression(cron);
            return ce.getNextValidTimeAfter(lastRunTime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException("cron格式错误");
        }
    }

    public static List<String> splitString(String value) {
        List<String> valuesList = new ArrayList<>();
        if (StringUtil.isEmpty(value)) {
            return valuesList;
        }
        String[] values = value.split(",");
        valuesList.addAll(Arrays.asList(values));
        return valuesList;
    }

    public static Set<String> splitStringReturnSet(String value) {
        Set<String> valuesList = new LinkedHashSet<>();
        if (StringUtil.isEmpty(value)) {
            return valuesList;
        }
        String[] values = value.split(",");
        valuesList.addAll(Arrays.asList(values));
        return valuesList;
    }

    public static List<Integer> splitInteger(String value, String regex) {
        List<Integer> valuesList = new ArrayList<>();
        if (StringUtil.isEmpty(value)) {
            return valuesList;
        }
        String[] values = value.split(regex);
        for (String v : values) {
            valuesList.add(Integer.valueOf(v));
        }
        return valuesList;
    }

    public static String randomAccessToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(RandomUtil.randomInt(10));
        }
        return sb.toString();
    }

    public static List<String> split(String value, String splitChars) {
        List<String> list = new ArrayList<>();
        if (value == null) {
            return list;
        }
        String[] values = value.split(splitChars);
        list.addAll(Arrays.asList(values));
        return list;
    }

    /**
     * 判断是否有修改
     */
    public static boolean isChange(Object oldValue, Object newValue) {
        if (oldValue == null && newValue == null) {
            return false;
        }
        if (oldValue == null || newValue == null) {
            return true;
        }
        return !oldValue.equals(newValue);
    }

    public static String getUseTime(long startTime) {
        int second = (int) ((System.currentTimeMillis() - startTime) / 1000);
        int hour = 0;
        int min = 0;
        if (second > 3600) {
            hour = second / 3600;
            second = second % 3600;
        }
        if (second > 60) {
            min = second / 60;
            second = second % 60;
        }
        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            sb.append(hour).append("小时");
        }
        if (min > 0) {
            sb.append(min).append("分");
        }
        sb.append(second).append("秒");
        return sb.toString();
    }

    public static String addInCondition(String field, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        return addInCondition(field, convert(ids));
    }

    public static String addInCondition(String field, int[] ids) {
        if (ids == null || ids.length == 0) {
            return "";
        }
        return addInCondition(field, convert(ids));
    }

    public static String addInCondition(String field, Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        StringBuilder inSql = new StringBuilder();
        inSql.append(" and ").append(field).append(" in(\n");
        for (Integer id : ids) {
            inSql.append(id).append(",");
        }
        if (inSql.length() > 0) {
            inSql.deleteCharAt(inSql.length() - 1);
        }
        inSql.append(")\n");
        return inSql.toString();
    }

    public static <T> T clone(Class<T> clazz, T obj) {
        String json = JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect);
        return JSONUtil.fromJson(json, clazz);
    }

    //
    public static String getFileName(String filePath) {
        if (filePath == null) {
            return null;
        }
        if (!filePath.contains("/")) {
            return filePath;
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }
    //

    private static final Set<String> EXCEL_EXT = Sets.newHashSet("xls", "xlt", "et", "xlsx", "xltx", "csv", "xlsm", "xltm");
    private static final Set<String> WORD_EXT = Sets.newHashSet("doc", "dot", "wps", "wpt", "docx", "dotx", "docm", "dotm");
    private static final Set<String> PPT_EXT = Sets.newHashSet("ppt", "pptx", "pptm", "ppsx", "ppsm", "pps", "potx", "potm", "dpt", "dps");

    /**
     * 判断输入内容是否有endsWith传入的结尾结合
     */
    public static boolean endWithAny(String input, Set<String> ends) {
        if (StringUtil.isEmpty(input) || ends == null || ends.isEmpty()) {
            return false;
        }
        return ends.stream().anyMatch(input::endsWith);
    }

    /**
     * 通过文件ID后缀，获取文件结束的类型。
     */
    public static String getWpsRouteEndType(String fileId) {
        StringBuilder builder = new StringBuilder("/");
        if (endWithAny(fileId, WORD_EXT)) {
            builder.append("w/");
        } else if (endWithAny(fileId, EXCEL_EXT)) {
            builder.append("s/");
        } else if (endWithAny(fileId, PPT_EXT)) {
            builder.append("p/");
        } else if (fileId.endsWith("pdf")) {
            builder.append("f/");
        } else {
            builder.append("w/");
        }
        builder.append(MD5Util.encodeMD5(fileId).toLowerCase()).append("?");
        return builder.toString();
    }

    /**
     * 校验wps的签名信息
     */
    public static boolean checkWpsSignature(String reqSignature, Map<String, String> params, String appSecret) {
        if (StringUtil.isEmpty(reqSignature)) {
            return false;
        }
        String signature = getWpsSignature(params, appSecret);
        if (StringUtil.isEmpty(signature)) {
            return false;
        }
        try {
            reqSignature = URLEncoder.encode(reqSignature, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("WPS UnsupportedEncodingException:" + e.getMessage());
            logger.error("WPS encode 获取签名数据有误");
        }
        return signature.equals(reqSignature);
    }

    /**
     * 获取签名数据
     */
    public static String getWpsSignature(Map<String, String> params, String appSecret) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        List<String> keys = new ArrayList<>(params.keySet());
        // 将所有参数按key的升序排序
        keys.sort(String::compareTo);
        // 构造签名的源字符串
        StringBuilder contents = new StringBuilder();
        for (String key : keys) {
            if ("_w_signature".equals(key)) {
                continue;
            }
            contents.append(key).append("=").append(params.get(key));
        }
        contents.append("_w_secretkey=").append(appSecret);

        // 进行hmac sha1 签名
        byte[] bytes = hmacSha1(appSecret.getBytes(), contents.toString().getBytes());
        //字符串经过Base64编码
        String sign = encodeBase64String(bytes);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("WPS UnsupportedEncodingException:" + e.getMessage());
            logger.error("WPS 获取签名数据有误");
        }
        return sign;
    }


    private static byte[] hmacSha1(byte[] key, byte[] data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance(signingKey.getAlgorithm());
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            logger.error("hmacSha1 NoSuchAlgorithmException:" + e.getMessage());
        } catch (InvalidKeyException e) {
            logger.error("hmacSha1 InvalidKeyException:" + e.getMessage());
        }
        return null;
    }

    /**
     * 对象判空
     */
    public static boolean isNullOrEmpty(Object newValue) {
        if (newValue == null) {
            return true;
        }
        String strValue = newValue.toString();
        return StringUtil.isEmptyWithTrim(strValue);
    }


    public static boolean isNullOrEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(int[] array) {
        return null == array || array.length == 0;
    }

    public static Set<Integer> toSet(int[] array) {
        Set<Integer> list = new HashSet<>();
        if (!isNullOrEmpty(array)) {
            for (int i : array) {
                list.add(i);
            }
        }
        return list;
    }

    public static <T> void addIfAbsent(Collection<T> collection, T v, Supplier<T> supplier) {
        if (null != v) {
            collection.add(v);
        } else {
            collection.add(supplier.get());
        }
    }

    public static int[] toIntArray(Collection<Integer> collection) {
        if (!isNullOrEmpty(collection)) {
            int[] array = new int[collection.size()];
            Iterator<Integer> interator = collection.iterator();
            int idx = 0;
            while (interator.hasNext()) {
                array[idx] = interator.next();
                idx++;
            }
            return array;
        }
        return new int[0];
    }

    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "utf8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return url;
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    public static String getColumnFieldName(String fieldName) {
        char[] chars = fieldName.toCharArray();
        StringBuilder sbr = new StringBuilder();
        for (char ch : chars) {
            if (Character.isUpperCase(ch)) {
                sbr.append("_").append(Character.toLowerCase(ch));
            } else {
                sbr.append(ch);
            }
        }
        return sbr.toString();
    }

    public static <T> T filter(List<T> list, String key, Object val) {
        if (isNullOrEmpty(list)) {
            return null;
        }
        for (T t : list) {
            if (Objects.equals(val, getFieldValue(t, key))) {
                return t;
            }
        }
        return null;
    }


    public static int[] deleteElement(int[] objectTypes, int objectType) {
        if (isNullOrEmpty(objectTypes)) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        for (int type : objectTypes) {
            if (type != objectType) {
                list.add(type);
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        return convertList(list);
    }

    public static String null2String(String val) {
        if (null == val) {
            return "";
        }
        return val;
    }

    public static int null2Int(Object val) {
        if (null == val) {
            return 0;
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            logger.error("ERR:{}", e.getMessage());
        }
        return 0;
    }

    public static long null2Long(Object val) {
        if (null == val) {
            return 0;
        }
        try {
            return Long.parseLong(val.toString());
        } catch (Exception e) {
            logger.error("ERR:{}", e.getMessage());
        }
        return 0;
    }


    public static String getDictNameByValue(String key, int value) {
        List<DataDict> dicts = BizTaskJobs.dictMap.get(key);
        if (!isNullOrEmpty(dicts)) {
            for (DataDict dict : dicts) {
                if (dict.value == value) {
                    return dict.name;
                }
            }
        }
        return "";
    }

    public static String getBooleanValue(Boolean express) {
        if (null == express || !express) {
            return "否";
        }
        return "是";
    }

    /**
     * 实体字体转义数据表字段
     *
     * @param beanFieldName
     * @return
     */
    public static String getDatabaseColumn(String beanFieldName) {
        char[] chars = beanFieldName.toCharArray();
        StringBuilder sbr = new StringBuilder();
        for (char aChar : chars) {
            if (Character.isUpperCase(aChar)) {
                sbr.append("_");
            }
            sbr.append(Character.toLowerCase(aChar));
        }
        return sbr.toString();
    }


    public static void asserts(boolean expect, String error) {
        if (!expect) {
            throw new AppException(error);
        }
    }

    public static int getDictValueByName(String categoryCode, String name) {
        if (isNullOrEmpty(categoryCode) || isNullOrEmpty(name)) {
            return 0;
        }
        List<DataDict> dicts = BizTaskJobs.dictMap.get(categoryCode);
        if (!isNullOrEmpty(dicts)) {
            for (DataDict dict : dicts) {
                if (name.equals(dict.name)) {
                    return dict.value;
                }
            }
        }
        return 0;
    }

    public static String filterSpecialChars(String name){
        if(isNullOrEmpty(name)){
            return "";
        }
        name = name.replaceAll("/", "");
        name = name.replaceAll("\\\\", "");
        name = name.replaceAll(".", "");
        name = name.replaceAll("&", "");
        return name;
    }


    public static String readAll(InputStream is) throws IOException {
        BufferedReader reader = createLineRead(is);
        List<String> lines = reader.lines().collect(Collectors.toList());
        return Joiner.on("\n").join(lines);
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static BufferedReader createLineRead(InputStream is) throws IOException {
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
}
