package cornerstone.biz.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cornerstone.biz.domain.GlobalConfig;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.IOUtil;

/**
 * @author yama
 */
public class JdbcUtil {
    //
    public static final int DB_TYPE_MYSQL = 1;
    public static final int DB_TYPE_SQLSERVER = 2;
    //
    private static Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    public static class DatabaseInfo {
        public int id;
        public String driverName;
        public String databaseProductName;
        public String databaseProductVersion;
    }

    //
    public static String getDriverName(int dbType) {
        if (dbType == DB_TYPE_MYSQL) {
            return GlobalConfig.mysqlDriverClass;
        } else if (dbType == DB_TYPE_SQLSERVER) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        throw new AppException("not support dbType:" + dbType);
    }


    /**
     * 获取集合的jdbc占位符
     * @param coll
     * @return
     */
    public static String getCollPlaceholder(Collection coll) {
        if (CollUtil.isEmpty(coll)) {
            throw new AppException("集合不能为空");
        }
        StringBuilder sbu = new StringBuilder();
        for (int i = 0; i < coll.size(); i++) {
            if (0 == i) {
                sbu.append(",");
            }
            sbu.append("?");
        }
        return sbu.toString();
    }

    /**
     * @param <T>
     * @author yama
     * 27 Dec, 2014
     */
    @FunctionalInterface
    public static interface ResultSetHandler<T> {
        T handleRow(ResultSet row) ;
    }

    //
    public static class TableInfo {
        public int id;
        public String name;
        public String remarks;
        public String type;
    }

    //
    public static class ColumnInfo {
        public int id;
        public String name;
        public String tableName;
        public String type;
        public int dataSize;
        public String remarks;
        public String columnDef;
        public String isAutoincrement;
        public String nullable;
    }

    //
    public static class IndexInfo {
        public int id;
        public String name;
        public boolean nonUnique;
        public String indexQualifier;
        public String columnName;
        public String type;
        public short ordinalPosition;
        public int cardinality;
    }

    public static class PrimaryKeyInfo {
        public int id;
        public String name;
        public String columnName;
    }

    //
    public static void loadDriver() {
        try {
            Class.forName(getDriverName(DB_TYPE_MYSQL));
            Class.forName(getDriverName(DB_TYPE_SQLSERVER));
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    //
    public static DatabaseInfo getDatabaseInfo(String dburl,
                                               String user,
                                               String pwd) throws SQLException {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dburl, user, pwd);
            DatabaseMetaData metaData = conn.getMetaData();
            DatabaseInfo info = new DatabaseInfo();
            info.driverName = metaData.getDriverName();
            info.databaseProductName = metaData.getDatabaseProductName();
            info.databaseProductVersion = metaData.getDatabaseProductVersion();
            conn.close();
            return info;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                logger.error(se.getMessage(),se);;
            }
        }
    }

    //
    public static List<ColumnInfo> getColumns(
            int dbType,
            String dburl,
            String user,
            String pwd,
            String table) throws AppException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", pwd);
            props.setProperty("remarks", "true");
            props.setProperty("useInformationSchema", "true");
            conn = DriverManager.getConnection(dburl, props);
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getColumns(null, null, table, null);
            List<ColumnInfo> r = new ArrayList<JdbcUtil.ColumnInfo>();
            int id = 1;
            Set<String> columnSet = new HashSet<>();
            while (rs.next()) {
                ColumnInfo ti = new ColumnInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(rs.getString("COLUMN_NAME")); // 列名
                ti.tableName = rs.getString("TABLE_NAME");
                ti.type = BizUtil.null2String(rs.getString("TYPE_NAME")); // java.sql.Types类型名称(列类型名称)
                ti.dataSize = rs.getInt("COLUMN_SIZE"); // 列大小
                ti.remarks = BizUtil.null2String(rs.getString("REMARKS")); // 列描述
                ti.columnDef = BizUtil.null2String(rs.getString("COLUMN_DEF")); // 默认值
                ti.nullable = BizUtil.null2String(rs.getString("IS_NULLABLE"));
                ti.isAutoincrement = BizUtil.null2String(rs.getString("IS_AUTOINCREMENT"));
                if (dbType == DB_TYPE_SQLSERVER && !ti.tableName.startsWith("T")) {
                    continue;
                }
                String key = ti.tableName + "-" + ti.name;
                if (columnSet.contains(key)) {
                    continue;
                }
                columnSet.add(key);
                r.add(ti);
            }
            conn.close();
            return r;
        } catch (Exception e) {
            throw new AppException(e);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    logger.error("get columns ERR", throwables);
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                logger.error("get columns ERR", se);
            }
        }
    }

    //
    public static List<PrimaryKeyInfo> getPrimaryKeys(
            String dburl,
            String user,
            String pwd,
            String table) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(dburl, user, pwd);
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getPrimaryKeys(null, null, table);
            List<PrimaryKeyInfo> r = new ArrayList<JdbcUtil.PrimaryKeyInfo>();
            int id = 1;
            while (rs.next()) {
                PrimaryKeyInfo ti = new PrimaryKeyInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(rs.getString("PK_NAME")); // 列名
                ti.columnName = BizUtil.null2String(rs.getString("COLUMN_NAME"));
                r.add(ti);
            }
            conn.close();
            return r;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                logger.error("getPrimaryKeys ERR", se);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                logger.error("getPrimaryKeys ERR", se);
            }
        }
    }

    //
    public static Map<String, List<IndexInfo>> getIndexs(
            String dburl,
            String user,
            String pwd,
            List<String> tables) {
        Connection conn = null;
        ResultSet rs = null;
        Map<String, List<IndexInfo>> result = new LinkedHashMap<String, List<IndexInfo>>();
        try {
            conn = DriverManager.getConnection(dburl, user, pwd);
            DatabaseMetaData metaData = conn.getMetaData();
            //
            for (String table : tables) {
                rs= metaData.getIndexInfo(null, null, table, false, true);
                List<IndexInfo> r = new ArrayList<>();
                int id = 1;
                while (rs.next()) {
                    IndexInfo ti = new IndexInfo();
                    ti.id = id++;
                    ti.name = BizUtil.null2String(rs.getString("INDEX_NAME")); // 列名
                    int type = rs.getShort("TYPE");
                    if (type == DatabaseMetaData.tableIndexClustered) {
                        ti.type = "tableIndexClustered";
                    }
                    if (type == DatabaseMetaData.tableIndexStatistic) {
                        ti.type = "tableIndexStatistic";
                    }
                    if (type == DatabaseMetaData.tableIndexHashed) {
                        ti.type = "tableIndexHashed";
                    }
                    if (type == DatabaseMetaData.tableIndexOther) {
                        ti.type = "tableIndexOther";
                    }
                    ti.nonUnique = rs.getBoolean("NON_UNIQUE");
                    ti.columnName = BizUtil.null2String(rs.getString("COLUMN_NAME"));
                    ti.indexQualifier = BizUtil.null2String(rs.getString("INDEX_QUALIFIER"));
                    ti.ordinalPosition = rs.getShort("ORDINAL_POSITION");
                    ti.cardinality = rs.getInt("CARDINALITY");
                    r.add(ti);
                }
                result.put(table, r);
                rs.close();
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                logger.error("getIndexs ERR", se);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                logger.error("getIndexs ERR", se);
            }
        }
    }

    //
    public static List<IndexInfo> getIndexs(
            String dburl,
            String user,
            String pwd,
            String table) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(dburl, user, pwd);
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getIndexInfo(null, null, table, false, true);
            List<IndexInfo> r = new ArrayList<JdbcUtil.IndexInfo>();
            int id = 1;
            while (rs.next()) {
                IndexInfo ti = new IndexInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(rs.getString("INDEX_NAME")); // 列名
                int type = rs.getShort("TYPE");
                if (type == DatabaseMetaData.tableIndexClustered) {
                    ti.type = "tableIndexClustered";
                }
                if (type == DatabaseMetaData.tableIndexStatistic) {
                    ti.type = "tableIndexStatistic";
                }
                if (type == DatabaseMetaData.tableIndexHashed) {
                    ti.type = "tableIndexHashed";
                }
                if (type == DatabaseMetaData.tableIndexOther) {
                    ti.type = "tableIndexOther";
                }
                ti.nonUnique = rs.getBoolean("NON_UNIQUE");
                ti.columnName = BizUtil.null2String(rs.getString("COLUMN_NAME"));
                ti.indexQualifier = BizUtil.null2String(rs.getString("INDEX_QUALIFIER"));
                ti.ordinalPosition = rs.getShort("ORDINAL_POSITION");
                ti.cardinality = rs.getInt("CARDINALITY");
                r.add(ti);
            }
            conn.close();
            return r;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                logger.error("getIndexs ERR", se);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                logger.error("getIndexs ERR", se);
            }
        }
    }

    //
    public static String getDbUrl(int dbType, String dbHost, int port, String dbName) {
        if (dbType == DB_TYPE_MYSQL) {
            return "jdbc:mysql://" + dbHost + ":" + port + "/" + dbName + "?" + GlobalConfig.mysqlDBUrlParams;
        } else if (dbType == DB_TYPE_SQLSERVER) {
            return "jdbc:sqlserver://" + dbHost + ":" + port + ";databaseName=" + dbName + "";
        }
        throw new AppException("not support dbType:" + dbType);
    }

    //
    public static List<TableInfo> getTables(
            String dburl,
            String user,
            String pwd,
            String dbName) throws AppException {
        return getTables(dburl, user, pwd, dbName, "%");
    }

    //
    public static TableInfo getTable(
            String dburl,
            String user,
            String pwd,
            String tableName) throws AppException {
        List<TableInfo> list = getTables(dburl, user, pwd, tableName);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //
    public static List<TableInfo> getTables(
            String dburl,
            String user,
            String pwd,
            String dbName,
            String tableNamePattern) throws AppException {
        Connection conn = null;
        ResultSet tableRet = null;

        try {
            logger.debug("getTables dburl:{} user:{} dbName:{} tableNamePattern:{}",
                    dburl, user, dbName, tableNamePattern);
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", pwd);
            props.setProperty("remarks", "true");
            props.setProperty("useInformationSchema", "true");
            props.setProperty("remarksReporting", "true");
            conn = DriverManager.getConnection(dburl, props);
            DatabaseMetaData metaData = conn.getMetaData();
            tableRet = metaData.getTables(null, dbName, tableNamePattern, new String[]{"TABLE"});
            List<TableInfo> r = new ArrayList<JdbcUtil.TableInfo>();
            int id = 1;
            while (tableRet.next()) {
                TableInfo ti = new TableInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(tableRet.getString("TABLE_NAME"));
                ti.remarks = BizUtil.null2String(tableRet.getString("REMARKS"));
                if ("sys_config".equals(ti.name)) {
                    continue;
                }
                r.add(ti);
            }
            conn.close();
            return r;
        } catch (Exception e) {
            throw new AppException(e);
        } finally {
            try {
                if (tableRet != null) {
                    tableRet.close();
                }
            } catch (SQLException se) {
                logger.error("getIndexs ERR", se);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                logger.error("getIndexs ERR", se);
            }
        }
    }

    /**
     * @param user
     * @param password
     * @param host
     * @param port
     * @param database
     * @return
     */
    public static String dumpDatabase(String user, String password,
                                      String host, int port, String database, String dstFile) {
        long startTime = System.currentTimeMillis();
        String command = String.format("/usr/bin/mysqldump -u%s -p%s -P %d -h %s %s --result-file=%s",
                user, password, port, host, database, dstFile);
        InputStream is = null;
        InputStream error = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            int state = p.waitFor();
            is = p.getInputStream();
            error = p.getErrorStream();
            logger.info("dumpDatabase:{} {}", state, IOUtil.getContent(is));
            logger.info("dumpDatabase error:{}", IOUtil.getContent(error));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        } finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(error);
            logger.info("mysqldump command:{} using {}ms", command, System.currentTimeMillis() - startTime);
        }
        //
        return dstFile;
    }

    /**
     * @param user
     * @param password
     * @param host
     * @param port
     * @param database
     * @param command
     */
    public static void runCommand(String user, String password, String host, int port, String database, String command) {
        long startTime = System.currentTimeMillis();
        String finalCommand = String.format("/usr/bin/mysql -u%s -p%s -P %d -h %s %s %s",
                user, password, port, host, database, command);
        InputStream is = null;
        InputStream error = null;
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", finalCommand);
            Process p = builder.redirectErrorStream(true).start();
            int state = p.waitFor();
            is = p.getInputStream();
            error = p.getErrorStream();
            logger.info("dumpDatabase:{} {}", state, IOUtil.getContent(is));
            logger.info("dumpDatabase error:{}", IOUtil.getContent(error));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        } finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(error);
            logger.info("runCommand {} using:{}ms", finalCommand, System.currentTimeMillis() - startTime);
        }

    }
}
