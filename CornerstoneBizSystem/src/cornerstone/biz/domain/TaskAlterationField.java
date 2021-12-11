package cornerstone.biz.domain;


/**
 * 项目变更字段
 */
public class TaskAlterationField {

    //名称
    public String name;

    //字段名
    public String field;

    //字段类型，参考ProjectFieldDefine.type
    //    public static final int TYPE_单行文本框 = 1;
    //    public static final int TYPE_复选框 = 3;//List<String>
    //    public static final int TYPE_单选框 = 4;
    //    public static final int TYPE_团队成员选择 = 6;//List<AccountSimpleInfo>  这个比较特殊 会有两个列表fieldobject_2255,field_2255
    //    public static final int TYPE_日期 = 7;//long型  时间戳格式 只有年月日
    //    public static final int TYPE_数值 = 8;
    public int type;

    //字段旧值
    public Object oldValue;

    //字段新值
    public Object newValue;

    //页面展示的字段旧值
    public Object oldShowValue;

    //页面展示的字段新值
    public Object newShowValue;


}