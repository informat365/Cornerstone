/**
 *
 */
package cornerstone.biz.util;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import cornerstone.biz.domain.GlobalConfig;
import cornerstone.biz.util.MYSQLJdbcUtil.ColumnInfo;
import cornerstone.biz.util.MYSQLJdbcUtil.ForeignKeyInfo;
import cornerstone.biz.util.MYSQLJdbcUtil.IndexInfo;
import cornerstone.biz.util.MYSQLJdbcUtil.MetaDataInfo;
import cornerstone.biz.util.MYSQLJdbcUtil.Table;
import cornerstone.biz.util.MYSQLJdbcUtil.TableInfo;
import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;

/**
 * @author yama
 *
 */
public class MySQLDatabaseSchema {
    private static Logger logger = LoggerFactory.get(MySQLDatabaseSchema.class);
    //
    String dburl;
    String user;
    String password;
    String database;
    List<String> tableNames;

    //
    public static class DatabaseInfo {
        public String ddl;
        public String dml;
        public int tableCount;
    }

    //
    public MySQLDatabaseSchema(String dburl, String database, String user, String password) {
        super();
        this.database = database;
        this.dburl = dburl;
        this.user = user;
        this.password = password;
    }

    //
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        return MYSQLJdbcUtil.getConnection(GlobalConfig.mysqlDriverClass, dburl, user, password);
    }

    //
    @SuppressWarnings("unchecked")
    public List<Table> loadSchema() throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection()) {
            return (List<Table>) MYSQLJdbcUtil.runWithConnection(conn, this::loadSchema0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    //
    @SuppressWarnings("unchecked")
    public String getDml() {
        Connection conn = null;
        try {
            conn = getConnection();
            return (String) MYSQLJdbcUtil.runWithConnection(conn, this::getDml0);
        } catch (Exception e) {
            logger.error("getDml ERR", e);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    logger.error("getDml ERR", throwables);
                }
            }
        }
        return null;
    }

    //
    private List<Table> loadSchema0(Connection conn) {
        List<TableInfo> tables = MYSQLJdbcUtil.getTables(conn);
        List<Table> ret = new ArrayList<>();
        for (TableInfo table : tables) {
            Table tableInfo = new Table();
            tableInfo.table = table;
            tableInfo.primaryKeys = MYSQLJdbcUtil.getPrimaryKeys(conn, table.name);
            tableInfo.foreignKeys = MYSQLJdbcUtil.getForginKeys(conn, table.name);
            tableInfo.indexs = getIndexs(conn, table.name);
            tableInfo.columns = MYSQLJdbcUtil.getColumns(conn, table.name);
            ret.add(tableInfo);
        }
        return ret;
    }

    //
    public List<IndexInfo> getIndexs(Connection conn, String table) {
        List<IndexInfo> list = new ArrayList<>();
        try {
            MYSQLJdbcUtil.executeQuery(conn,
                    "SELECT * FROM information_schema.statistics WHERE table_schema=? and table_name =?",
                    (rs) -> {
                        int id = 1;
                        while (rs.next()) {
                            IndexInfo ti = new IndexInfo();
                            ti.id = id++;
                            ti.name = BizUtil.null2String(rs.getString("INDEX_NAME")); // 列名
                            ti.type = BizUtil.null2String(rs.getString("INDEX_TYPE"));
                            ti.nonUnique = rs.getBoolean("NON_UNIQUE");
                            ti.columnName = BizUtil.null2String(rs.getString("COLUMN_NAME"));
                            list.add(ti);
                        }
                    }, database, table);
        } catch (Exception e) {
            logger.error("getIndexs ERR", e);
        }
        return list;
    }

    //
    public String getDml0(Connection conn) {
        StringBuilder result = new StringBuilder();
        for (String table : tableNames) {
            String sql = "SELECT * FROM " + table;
            try {
                MYSQLJdbcUtil.executeQuery(conn,
                        sql,
                        (rs) -> {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int columnCount = rsmd.getColumnCount();
                            if (columnCount == 0) {
                                return;
                            }
                            List<MetaDataInfo> metaDataInfos = new ArrayList<>();
                            StringBuilder insertSql = new StringBuilder();
                            insertSql.append("INSERT INTO `").append(table).append("` (");
                            for (int i = 1; i <= columnCount; i++) {
                                MetaDataInfo metaDataInfo = new MetaDataInfo();
                                metaDataInfo.columnName = rsmd.getColumnLabel(i);
                                metaDataInfo.columnType = rsmd.getColumnType(i);
                                metaDataInfo.columnTypeName = rsmd.getColumnTypeName(i);
                                metaDataInfos.add(metaDataInfo);
                                insertSql.append("`").append(metaDataInfo.columnName).append("`,");
                            }
                            insertSql.deleteCharAt(insertSql.length() - 1);
                            insertSql.append(") VALUES (");
                            while (rs.next()) {
                                StringBuilder reallySql = new StringBuilder(insertSql.toString());
                                for (MetaDataInfo e : metaDataInfos) {
                                    String value = rs.getString(e.columnName);
                                    if (value == null) {
                                        reallySql.append("NULL");
                                    } else {
                                        reallySql.append("'").append(value).append("'");
                                    }
                                    reallySql.append(",");
                                }
                                reallySql.deleteCharAt(reallySql.length() - 1);
                                reallySql.append(");\n");
                                result.append(reallySql.toString());
                            }
                        });
            } catch (Exception e) {
                logger.error("getDml0 ERROR", e);
            }
        }
        return result.toString();
    }


    public String getTableSQL(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE `").append(table.table.name).append("`(\n");
        List<String> rows = new ArrayList<>();
        for (ColumnInfo c : table.columns) {
            StringBuilder t = new StringBuilder();
            t.append("`").append(c.name).append("` ").append(c.type);
            if ("decimal".equalsIgnoreCase(c.type)) {
                if (c.dataSize > 0) {
                    t.append("(").append(c.dataSize).append(",").append(c.decimaldigits).append(")");
                }
            } else if ("varchar".equalsIgnoreCase(c.type)) {
                if (c.dataSize > 0) {
                    t.append("(").append(c.dataSize).append(")");
                }
            }
            if (!c.nullable) {
                t.append(" NOT NULL ");
            }
            if (c.columnDef != null) {
                t.append(" DEFAULT ").append(c.columnDef).append(" ");
            }
            if (c.isAutoincrement) {
                t.append(" AUTO_INCREMENT ");
            }
            if (c.remarks != null && !c.remarks.trim().isEmpty()) {
                t.append(" COMMENT '").append(c.remarks).append("'");
            }
            rows.add(t.toString());
        }
        //
        Map<String, List<IndexInfo>> allIndexMap = new LinkedHashMap<>();
        for (IndexInfo idx : table.indexs) {
            if (!allIndexMap.containsKey(idx.name)) {
                allIndexMap.put(idx.name, new ArrayList<>());
            }
            allIndexMap.get(idx.name).add(idx);
        }
        //index and pk
        for (Entry<String, List<IndexInfo>> e : allIndexMap.entrySet()) {
            IndexInfo f = e.getValue().get(0);
            if ("PRIMARY".equals(f.name)) {
                rows.add("PRIMARY KEY (`" + f.columnName + "`)");
                continue;
            }
            StringBuilder t = new StringBuilder();
            if (!f.nonUnique) {
                t.append("UNIQUE ");
            }
            if ("FULLTEXT".equals(f.type)) {
                t.append("FULLTEXT ");
            }
            t.append("KEY ");
            t.append("`").append(e.getKey()).append("` ");
            t.append("(");
            for (int i = 0; i < e.getValue().size(); i++) {
                String q = e.getValue().get(i).columnName;
                t.append("`").append(q).append("`");
                if (i < e.getValue().size() - 1) {
                    t.append(",");
                }
            }
            t.append(")");
            rows.add(t.toString());
        }
        //fk
        for (ForeignKeyInfo fk : table.foreignKeys) {
            rows.add("CONSTRAINT `" + fk.fkName + "` FOREIGN KEY (`" + fk.fkColumnName + "`) REFERENCES `" + fk.pkTableName + "` (`" + fk.pkColumnName + "`)");
        }
        //dump all rows
        for (int i = 0; i < rows.size(); i++) {
            sb.append("\t").append(rows.get(i));
            if (i < rows.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        //
        sb.append(")");
        if (table.table.remarks != null && !table.table.remarks.trim().isEmpty()) {
            sb.append("COMMENT='").append(table.table.remarks).append("'");
        }
        sb.append(";\n");
        //
        return sb.toString();
    }


    public static DatabaseInfo getDatabaseInfo(String dburl, String database, String user, String password, List<String> tableNames) {
        DatabaseInfo info = new DatabaseInfo();
        try {
            MySQLDatabaseSchema schema = new MySQLDatabaseSchema(
                    dburl,
                    database,
                    user,
                    password);//
            List<Table> tables = schema.loadSchema();//这个是domain类
            StringBuilder ddl = new StringBuilder();
            for (Table t : tables) {
                String s = schema.getTableSQL(t);//这是生成的sql
                ddl.append(s);
            }
            info.ddl = ddl.toString();
            info.tableCount = tables.size();
            schema.tableNames = tableNames;
            info.dml = schema.getDml();
            return info;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }
    }

    /**
     *
     * @param dburl
     * @param database
     * @param user
     * @param password
     * @return
     */
    public static String getDatabaseDml(String dburl, String database, String user, String password, List<String> tableNames) {
        if (tableNames == null || tableNames.size() == 0) {
            return null;
        }
        try {
            MySQLDatabaseSchema schema = new MySQLDatabaseSchema(
                    dburl,
                    database,
                    user,
                    password);
            schema.tableNames = tableNames;
            return schema.getDml();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }
    }

    /**
     *
     * @param dburl
     * @param database
     * @param user
     * @param password
     * @return
     */
    public static String getDatabaseDdl(String dburl, String database, String user, String password) {
        try {
            MySQLDatabaseSchema schema = new MySQLDatabaseSchema(
                    dburl,
                    database,
                    user,
                    password);//
            List<Table> tables = schema.loadSchema();//这个是domain类
            StringBuilder ddl = new StringBuilder();
            for (Table t : tables) {
                String s = schema.getTableSQL(t);//这是生成的sql
                ddl.append(s);
            }
            return ddl.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AppException(e.getMessage());
        }
    }
}
