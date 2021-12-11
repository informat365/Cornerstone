package cornerstone.biz.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jazmin.driver.jdbc.ConnectionUtil;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author yama
 */
public class MYSQLJdbcUtil {
    //
    private static Logger logger = LoggerFactory.get(MYSQLJdbcUtil.class);

    public static class DatabaseInfo {
        public int id;
        public String driverName;
        public String databaseProductName;
        public String databaseProductVersion;
    }

    //
    public static class TableInfo {
        public int id;
        public String name;
        public String remarks;
    }

    //
    public static class ColumnInfo {
        public int id;
        public String name;
        public String type;
        public int dataSize;
        public String remarks;
        public String columnDef;
        public boolean isAutoincrement;
        public boolean nullable;
        public String decimaldigits;
    }

    //
    public static class IndexInfo {
        public int id;
        public String name;
        public boolean nonUnique;
        public String indexQualifier;
        public String columnName;
        public String type;
    }

    public static class PrimaryKeyInfo {
        public int id;
        public String name;
        public String columnName;
    }

    //
    public static class ForeignKeyInfo {
        public int id;
        public String pkTableName;
        public String pkColumnName;
        public String fkColumnName;
        public String fkTableName;
        public String pkName;
        public String fkName;
        public short updateRule;
        public short deleteRule;

    }

    public static class Table {
        public TableInfo table;
        public List<ColumnInfo> columns;
        public List<PrimaryKeyInfo> primaryKeys;
        public List<IndexInfo> indexs;
        public List<ForeignKeyInfo> foreignKeys;
    }

    public static class MetaDataInfo {
        public String columnName;
        public int columnType;
        public String columnTypeName;
    }

    public static Connection getConnection(
            String driver,
            String dburl,
            String user,
            String pwd) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(dburl, user, pwd);
        if (logger.isDebugEnabled()) {
            logger.debug("getConnection dburl:{} user:{}", dburl, user);
        }
        return conn;
    }

    public interface ConnectionCallback {
        Object accept(Connection conn) ;
    }

    public static Object runWithConnection(Connection conn, ConnectionCallback callback) {
            try {
                return callback.accept(conn);
            } catch (Exception e) {
                logger.error(e.getMessage(),e);;
            }finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException se) {
                    logger.error("runWithConnection ERR",se);
                }
            }
            return null;
    }

    public static DatabaseInfo getDatabaseInfo(Connection conn){
        DatabaseMetaData metaData = null;
        DatabaseInfo info = new DatabaseInfo();
        try {
            metaData = conn.getMetaData();
            info.driverName = metaData.getDriverName();
            info.databaseProductName = metaData.getDatabaseProductName();
            info.databaseProductVersion = metaData.getDatabaseProductVersion();
        } catch (SQLException throwables) {
            logger.error("getDatabaseInfo ERR",throwables);
        }
        return info;
    }


    public static List<ColumnInfo> getColumns(Connection conn, String table) {
        ResultSet rs = null;
        List<ColumnInfo> r = new ArrayList<MYSQLJdbcUtil.ColumnInfo>();
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getColumns(null, null, table, null);
            int id = 1;
            while (rs.next()) {
                ColumnInfo ti = new ColumnInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(rs.getString("COLUMN_NAME")); // 列名
                ti.type = BizUtil.null2String(rs.getString("TYPE_NAME")); // java.sql.Types类型名称(列类型名称)
                ti.dataSize = rs.getInt("COLUMN_SIZE"); // 列大小
                ti.decimaldigits = BizUtil.null2String(rs.getString("DECIMAL_DIGITS"));
                ti.remarks = BizUtil.null2String(rs.getString("REMARKS")); // 列描述
                ti.columnDef = BizUtil.null2String(rs.getString("COLUMN_DEF")); // 默认值
                ti.nullable = "YES".equalsIgnoreCase(BizUtil.null2String(rs.getString("IS_NULLABLE")));
                ti.isAutoincrement = "YES".equalsIgnoreCase(BizUtil.null2String(rs.getString("IS_AUTOINCREMENT")));
                r.add(ti);
            }
        } catch (SQLException e) {
            logger.error("getColumns ERR", e);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    logger.error("getColumns", throwables);
                }
            }
        }


        return r;
    }

    public static List<ForeignKeyInfo> getForginKeys(Connection conn, String table) {
        List<ForeignKeyInfo> r = new ArrayList<>();
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        try {
            metaData = conn.getMetaData();
            rs = metaData.getImportedKeys(null, null, table);

            int id = 1;
            while (rs.next()) {
                ForeignKeyInfo ti = new ForeignKeyInfo();
                ti.id = id++;
                ti.fkName = BizUtil.null2String(rs.getString("FK_NAME"));
                ti.pkName = BizUtil.null2String(rs.getString("PK_NAME"));
                ti.updateRule = rs.getShort("UPDATE_RULE");
                ti.deleteRule = rs.getShort("DELETE_RULE");

                ti.pkTableName = BizUtil.null2String(rs.getString("PKTABLE_NAME"));
                ti.pkColumnName = BizUtil.null2String(rs.getString("PKCOLUMN_NAME"));
                ti.fkTableName = BizUtil.null2String(rs.getString("FKTABLE_NAME"));
                ti.fkColumnName = BizUtil.null2String(rs.getString("FKCOLUMN_NAME"));
                r.add(ti);
            }
        } catch (SQLException throwables) {
            logger.error("getForginKeys", throwables);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    logger.error("getForginKeys", throwables);
                }
            }
        }

        return r;
    }

    public static List<PrimaryKeyInfo> getPrimaryKeys(Connection conn, String table) {
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        List<PrimaryKeyInfo> r = new ArrayList<MYSQLJdbcUtil.PrimaryKeyInfo>();
        try {
            metaData = conn.getMetaData();
            rs = metaData.getPrimaryKeys(null, null, table);

            int id = 1;
            while (rs.next()) {
                PrimaryKeyInfo ti = new PrimaryKeyInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(rs.getString("PK_NAME")); // 列名
                ti.columnName = BizUtil.null2String(rs.getString("COLUMN_NAME"));
                r.add(ti);
            }
        } catch (SQLException throwables) {
            logger.error("getPrimaryKeys", throwables);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    logger.error("getPrimaryKeys", throwables);
                }
            }
        }
        return r;
    }

    //
    public static List<IndexInfo> getIndexs(Connection conn, String table) {
        DatabaseMetaData metaData = null;
        ResultSet rs = null;
        List<IndexInfo> r = new ArrayList<MYSQLJdbcUtil.IndexInfo>();
        try {
            metaData = conn.getMetaData();
            rs = metaData.getIndexInfo(null, null, table, false, true);
            int id = 1;
            while (rs.next()) {
                IndexInfo ti = new IndexInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(rs.getString("INDEX_NAME")); // 列名
                ti.type = BizUtil.null2String(rs.getString("TYPE"));
                ti.nonUnique = rs.getBoolean("NON_UNIQUE");
                ti.columnName = BizUtil.null2String(rs.getString("COLUMN_NAME"));
                r.add(ti);
            }
        } catch (SQLException throwables) {
            logger.error("getPrimaryKeys", throwables);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    logger.error("getPrimaryKeys", throwables);
                }
            }
        }

        return r;
    }


    public static List<TableInfo> getTables(Connection conn) {
        DatabaseMetaData metaData = null;
        ResultSet tableRet = null;
        List<TableInfo> r = new ArrayList<MYSQLJdbcUtil.TableInfo>();
        try {
            metaData = conn.getMetaData();
            tableRet = metaData.getTables(null, null, null, new String[]{});
            int id = 1;
            while (tableRet.next()) {
                TableInfo ti = new TableInfo();
                ti.id = id++;
                ti.name = BizUtil.null2String(tableRet.getString("TABLE_NAME"));
                ti.remarks = BizUtil.null2String(tableRet.getString("REMARKS"));
                r.add(ti);
            }
        } catch (SQLException throwables) {
            logger.error("getPrimaryKeys", throwables);
        } finally {
            if (null != tableRet) {
                try {
                    tableRet.close();
                } catch (SQLException throwables) {
                    logger.error("getPrimaryKeys", throwables);
                }
            }
        }

        return r;
    }

    public static boolean execute(
            Connection conn,
            String sql,
            Object... args) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            ConnectionUtil.set(stmt, args);
            boolean r = stmt.execute();
            stmt.close();
            return r;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
               logger.error("execute ERR",se2);
            }
        }
    }

    public static int executeUpdate(
            Connection conn,
            String sql,
            Object... args) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            ConnectionUtil.set(stmt, args);
            int r = stmt.executeUpdate();
            stmt.close();
            return r;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
               logger.error("executeUpdate ERR",se2);
            }
        }
    }

    public interface ResultSetCallback {
        void accept(ResultSet rs) throws SQLException;
    }

    public static void executeQuery(
            Connection conn,
            String sql,
            ResultSetCallback callback,
            Object... args) {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            ConnectionUtil.set(stmt, args);
            stmt.setMaxRows(10000);
            rs = stmt.executeQuery();
            callback.accept(rs);
            rs.close();
            stmt.close();
        } catch (Exception e){
            logger.error("executeQuery ERR",e);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se2) {
               logger.error("executeQuery ERR",se2);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                logger.error("executeQuery ERR",se2);
            }
        }
    }
}
