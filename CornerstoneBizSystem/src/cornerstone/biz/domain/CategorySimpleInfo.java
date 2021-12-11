package cornerstone.biz.domain;


/**
 * @author cs
 */
public class CategorySimpleInfo {
    public int id;
    public String name;
    public String color;
    public int sortWeight;

    //
    public static CategorySimpleInfo createCategorySimpleinfo(Category bean) {
        CategorySimpleInfo info = new CategorySimpleInfo();
        info.id = bean.id;
        info.name = bean.name;
        info.color = bean.color;
        info.sortWeight = bean.sortWeight;
        return info;
    }
}
