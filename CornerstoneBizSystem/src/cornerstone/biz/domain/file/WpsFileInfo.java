package cornerstone.biz.domain.file;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * WPS文件元数据类
 * a. 其中user_acl和watermark为非必须返回参数，user_acl用于控制用户权限，不返回则为系统默认值，watermark只用于预览时添加第三方水印.
 * b. 返回的参数类型必须与示例一致,切莫将int与string类型混淆.
 * c. 返回的参数不能为nil,NULL等,时间戳长度需与示例一致.
 * @author ToBy
 */
public class WpsFileInfo {

    public File file;

    public User user;

    public static class  File{
        /**
         * 文件ID
         */
        public String id;

        /**
         * 文件名
         */
        public String name;

        /**
         * 当前版本号，位数小于11
         */
        public int version;

        public long size;

        public String creator;

        @JSONField(name ="create_time")
        public long createTime;

        public String modifier;

//        @JSONField(name ="modify_time")
//        public long modifyTime;

        @JSONField(name ="download_url")
        public String downloadUrl;


//        @JSONField(name ="user_acl")
//        public UserAcl userAcl;
//
//        public static class UserAcl{
//            /**
//             * 重命名权限，1为打开该权限，0为关闭该权限，默认为0
//             */
//            public int rename;
//            /**
//             * 历史版本权限，1为打开该权限，0为关闭该权限,默认为1
//             */
//            public int history;
//        }
//
//        public Watermark watermark;
//
//        public static class Watermark{
//            /**
//             * 水印类型， 0为无水印； 1为文字水印
//             */
//            public int type;
//            /**
//             * 文字水印的文字，当type为1时此字段必选
//             */
//            public String value;
//            /**
//             * 水印的透明度，非必选，有默认值
//             * rgba( 192, 192, 192, 0.6 )
//             */
//            public String fillstyle;
//            /**
//             * 水印的字体，非必选，有默认值
//             * "bold 20px Serif",
//             */
//            public String font;
//            /**
//             * 水印的旋转度，非必选，有默认值
//             *  -0.7853982;
//             */
//            public double rotate;
//            /**
//             *  水印水平间距，非必选，有默认值
//             * 50,
//             */
//            public int horizontal;
//            /**
//             *  水印垂直间距，非必选，有默认值
//             *   100
//             */
//            public int vertical;
//        }
    }

    public static class  User{
        /**
         * 用户id，长度小于40
         */
       public String id;
        /**
         * 用户名称
         */
        public String name;
        /**
         * 用户操作权限，write：可编辑，read：预览
         */
        public String permission;
        /**
         * 用户头像地址
         */
        @JSONField(name ="avatar_url")
        public String avatarUrl;
    }
}
