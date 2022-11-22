package com.sunac.utils;

import com.sunac.Config;
import com.sunac.domain.ColumAttribute;
import com.sunac.domain.CommonDomain;
import com.sunac.ow.owdomain.ComplexStructureDomain;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sunac.ow.owdomain.ComplexStructureDomain.TABLES;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class JdbcUtils {
    private static StringBuilder sb = new StringBuilder();
    private static final Logger log = LoggerFactory.getLogger(JdbcUtils.class);

    public static String makeMd5(String... s) {
        StringBuilder sb = new StringBuilder();
        for (String e : s) {
            sb.append(e);
        }
        return MakeMd5Utils.makeMd5(sb.toString());
    }

    public static String makeSQL(Tuple2<String, CommonDomain> tp) throws Exception {
        String fullClassName = tp.f0;
        String operation_type = tp.f1.getOperation_type();
        HashMap<String, String> updateInfoMap = tp.f1.getUpdateInfoMap();

        HashMap<String, ColumAttribute> obj = TABLES.get(fullClassName);
        HashMap<String, Object> hm = new HashMap<>();

        HashMap<String, Object> es_info_object_fld_object_class_guid_es_info_object_class = new HashMap<>();
        /***
         * EsInfoObject表的fld_class_guid（fld_object_class_guid）的删除会影响EsInfoObjectClass表的，所以也要删除
         */
        if (fullClassName.equals(Config.EsInfoObject) && operation_type.equals(Config.DELETE)) {
            HashMap<String, ColumAttribute> esInfoObjectClass = TABLES.get(Config.EsInfoObjectClass);
            for (Map.Entry<String, ColumAttribute> entry2 : esInfoObjectClass.entrySet()) {
                ColumAttribute value = entry2.getValue();
                String newName = value.getNewName();
                boolean is_delete = value.isDelete_column();
                boolean is_table = value.isTable_column();
                if (is_table && is_delete) {
                    es_info_object_fld_object_class_guid_es_info_object_class.put(newName, value.getDefaultValue());
                }
            }
        }
        HashMap<String, Object> fldMainGuid = new HashMap<>();
        /***
         * es_charge_settle_accounts_detail表的fld_main_guid的删除会影响Es_charge_settle_accounts_main表的，所以也要删除
         */
        if (fullClassName.equals(Config.EsChargeSettleAccountsDetail) && operation_type.equals(Config.DELETE)) {
            HashMap<String, ColumAttribute> mainGuidMap = ComplexStructureDomain.TABLES.get(Config.EsChargeSettleAccountsMain);
            for (Map.Entry<String, ColumAttribute> e : mainGuidMap.entrySet()) {
                ColumAttribute value = e.getValue();
                String newName = value.getNewName();
                boolean is_delete = value.isDelete_column();
                boolean is_table = value.isTable_column();
                if (is_table && is_delete) {
                    fldMainGuid.put(newName, value.getDefaultValue());
                }
            }
        }

        /***
         * es_charge_project_period_join表的fld_period_guid(fld_period_guid_join)的删除会影响es_charge_project_period表的，
         * 所以也要删除
         */
        HashMap<String, Object> fldPeriodJoinGuidMap = new HashMap<>();
        if (fullClassName.equals(Config.EsChargeProjectPeriodJoin) && operation_type.equals(Config.DELETE)) {
            HashMap<String, ColumAttribute> m = TABLES.get(Config.EsChargeProjectPeriod);
            for (Map.Entry<String, ColumAttribute> e : m.entrySet()) {
                ColumAttribute value = e.getValue();
                String newName = value.getNewName();
                boolean is_delete = value.isDelete_column();
                boolean is_table = value.isTable_column();
                if (is_table && is_delete) {
                    fldPeriodJoinGuidMap.put(newName, value.getDefaultValue());
                }
            }
        }



        /***
         *把需要跟新的字段：包括原始字段和别名拿出来
         */
        int t_fld_category = 0;
        for (Map.Entry<String, ColumAttribute> entry : obj.entrySet()) {
            ColumAttribute value = entry.getValue();
            String oldName = entry.getKey();
            String newName = value.getNewName();

            boolean isUpdate = value.isUpdate_column();
            boolean isDelete = value.isDelete_column();
            boolean isInsert = value.isInsert_column();
            boolean isTable = value.isTable_column();

            // 判断需要更新的字段：三个条件都要满足
            if (isTable && isUpdate && updateInfoMap.containsKey(oldName) && operation_type.equals(Config.UPDATE)) {
                hm.put(newName, oldName);
            }
            if (isTable && isDelete && operation_type.equals(Config.DELETE)) {
                hm.put(newName, value.getDefaultValue());
            }
            if (isTable && isInsert && operation_type.equals(Config.INSERT)) {
                hm.put(newName, oldName);
            }
        }
        if (hm.size() == 0) {
            return null;
        }
        Class<? extends CommonDomain> tClass = tp.f1.getClass();
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("update es_charge_owner_fee_all_data set ");

        int index = 0;
        int size = hm.size();

        // 拼接sql
        //
        for (Map.Entry<String, Object> entry : hm.entrySet()) {
            String newName = entry.getKey();
            Object oldNameOrValue = entry.getValue();
            // 获取字段类型
            Object value = null;
            if (operation_type.equals(Config.DELETE)) {
                value = entry.getValue();
            } else {
                Class<?> fieldType = TypeUtils.getFieldType(tClass, (String) oldNameOrValue).getType();
                value = TypeUtils.getGetMethod(tp.f1, (String) oldNameOrValue);
                if (value != null) {
                    value = TypeUtils.stringToTarget(String.valueOf(value), fieldType);
                }
            }
            if (value != null && value.getClass().equals(String.class)) {
                value = "'" + value + "'";
            }
            sqlBuf.append(newName).append("=").append(value);
            if (index != size - 1) {
                sqlBuf.append(",");
            }
            index++;
        }

        if (fullClassName.equals(Config.EsInfoObject) && operation_type.equals(Config.DELETE)) {
            sqlBuf.append(",");
            int index_class = 0;
            int size_class = es_info_object_fld_object_class_guid_es_info_object_class.size();

            for (Map.Entry<String, Object> entry : es_info_object_fld_object_class_guid_es_info_object_class.entrySet()) {
                String newName = entry.getKey();
                Object value = entry.getValue();
                if (value != null && value.getClass().equals(String.class)) {
                    value = "'" + value + "'";
                }
                sqlBuf.append(newName).append("=").append(value);
                if (index_class != size_class - 1) {
                    sqlBuf.append(",");
                }
                index_class++;
            }
        }
        if (fullClassName.equals(Config.EsChargeSettleAccountsDetail) && operation_type.equals(Config.DELETE)) {
            sqlBuf.append(",");
            int index_class = 0;
            int size_class = fldMainGuid.size();
            for (Map.Entry<String, Object> entry : fldMainGuid.entrySet()) {
                String newName = entry.getKey();
                Object value = entry.getValue();
                if (value != null && value.getClass().equals(String.class)) {
                    value = "'" + value + "'";
                }
                sqlBuf.append(newName).append("=").append(value);
                if (index_class != size_class - 1) {
                    sqlBuf.append(",");
                }
                index_class++;
            }
        }
        if (fullClassName.equals(Config.EsChargeProjectPeriodJoin) && operation_type.equals(Config.DELETE)) {
            sqlBuf.append(",");
            int index_class = 0;
            int size_class = fldPeriodJoinGuidMap.size();
            for (Map.Entry<String, Object> entry : fldPeriodJoinGuidMap.entrySet()) {
                String newName = entry.getKey();
                Object value = entry.getValue();
                if (value != null && value.getClass().equals(String.class)) {
                    value = "'" + value + "'";
                }
                sqlBuf.append(newName).append("=").append(value);
                if (index_class != size_class - 1) {
                    sqlBuf.append(",");
                }
                index_class++;
            }
        }

        sqlBuf.append(" where fld_guid in ");
        return sqlBuf.toString();
    }

    public static void insertOrIgnore(String tabName, String fld_guid, String key) throws SQLException, IOException {
        sb.delete(0, sb.length());
        sb.append("INSERT INTO ");
        sb.append(tabName);
        sb.append(" (fld_guid,fld_value) VALUES ('");
        sb.append(fld_guid);
        sb.append("','");
        sb.append(key + "') ON DUPLICATE KEY UPDATE fld_value= VALUES (fld_value)");
        HikariUtil hikariUtil = HikariUtil.getInstance();
        Connection connection = hikariUtil.getConnection();
        connection.setAutoCommit(false);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sb.toString());
            hikariUtil.commit(connection);
        } catch (SQLException e) {
            try {
                hikariUtil.rollback(connection);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            hikariUtil.close(connection);
        }
    }

    public static void updateSqlByValue(String new_value, String old_value) throws SQLException {
        sb.delete(0, sb.length());
        //UPDATE bj_sjzt_db.idx_owe_es_commerce_bond_main
        //SET fld_value='', create_time='', update_time=CURRENT_TIMESTAMP
        //WHERE fld_guid='';
        sb.append("UPDATE idx_owe_es_commerce_bond_fee_object SET fld_value='" + new_value + "' where fld_value = '");
        sb.append(old_value);
        sb.append("'");
        System.out.println("idx_owe_es_commerce_bond_fee_object 的修改呀-----：" + sb.toString());
        HikariUtil hikariUtil = HikariUtil.getInstance();
        Connection connection = hikariUtil.getConnection();
        connection.setAutoCommit(false);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sb.toString());
            hikariUtil.commit(connection);
        } catch (SQLException e) {
            try {
                hikariUtil.rollback(connection);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            hikariUtil.close(connection);
        }
    }
    public static Tuple2<Connection, Boolean> insertOrIgnore2(Connection connection, String tabName, String fld_guid, String key) throws SQLException {
        Tuple2<Connection, Boolean> tp2 = new Tuple2<Connection, Boolean>();
        Boolean isSuccessExe = true;
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tabName);
        sb.append(" (fld_guid,fld_value) VALUES ('");
        sb.append(fld_guid);
        sb.append("','");
        sb.append(key + "') ON DUPLICATE KEY UPDATE fld_value= VALUES (fld_value)");
        if (!connectionIsAlive(connection)) {
            connection = HikariUtil.getInstance().getConnection();
        }
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.execute(sb.toString());
            connection.commit();
        } catch (SQLException e) {
            isSuccessExe = false;
            try {
                connection.rollback();
            } catch (Exception ex) {
            }
            log.error("======================================insertOrIgnore2执行SQL报错！SQL===================================" + sb.toString());
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }
        tp2.f0 = connection;
        tp2.f1 = isSuccessExe;
        return tp2;
    }
    public static Tuple2<Connection, Boolean> insertTax(Connection connection, String tabName, String fld_guid, BigDecimal amont, String key) throws SQLException {
        Tuple2<Connection, Boolean> tp2 = new Tuple2<Connection, Boolean>();
        Boolean isSuccessExe = true;
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tabName);
        sb.append(" (fld_guid,fld_amount,fld_value) VALUES ('");
        sb.append(fld_guid);
        sb.append("',");
        sb.append(amont);
        sb.append(",'");
        sb.append(key + "') ON DUPLICATE KEY UPDATE fld_value= VALUES (fld_value),fld_amount= VALUES (fld_amount)");
        if (!connectionIsAlive(connection)) {
            connection = HikariUtil.getInstance().getConnection();
        }
        Statement statement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.execute(sb.toString());
            connection.commit();
        } catch (SQLException e) {
            isSuccessExe = false;
            try {
                connection.rollback();
            } catch (Exception ex) {
            }
            log.error("======================================insertOrIgnore2执行SQL报错！SQL===================================" + sb.toString());
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }
        tp2.f0 = connection;
        tp2.f1 = isSuccessExe;
        return tp2;
    }
    public static Boolean connectionIsAlive(Connection connection) {
        try {
            if (null == connection || (null != connection && connection.isClosed())) {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    public static Tuple2<Connection, Boolean> queryByTabAndKey2(KeyedCoProcessFunction.Context context, Connection connection, String tabName, String key, String exeSQL) throws SQLException, IOException {
        Tuple2<Connection, Boolean> tp2 = new Tuple2<Connection, Boolean>(connection, true);
        if (null == exeSQL) {
            return tp2;
        }
        boolean isExeSuccess = true;
        String sql = "SELECT fld_guid FROM " + tabName + " WHERE fld_value='" + key + "'";
        if (!connectionIsAlive(connection)) {
            connection = HikariUtil.getInstance().getConnection();
        }
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<String> sqlContainer = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(exeSQL);
        sb.append("(");
        try {
            if (!connectionIsAlive(connection)) {
                connection = HikariUtil.getInstance().getConnection();
                statement = connection.createStatement();
                statement.setFetchSize(5000);
            } else {
                statement = connection.createStatement();
                statement.setFetchSize(5000);
            }
            int rowCount = 0;
            boolean isSend = false;
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                isSend = true;
                ++rowCount;
                sb.append("'" + resultSet.getString(1) + "',");
                if (rowCount % 200 == 0) {
                    sb.append(")");
                    sqlContainer.add(sb.toString().replace(",)", ")"));
                    // 清空sb,下面使用
                    sb.delete(0, sb.length());
                    sb.append(exeSQL);
                    sb.append("(");
                }
            }
            if (isSend && sb.length() > 0) {
                sb.append(")");
                sqlContainer.add(sb.toString().replace(",)", ")"));
            }
            if (isSend) {
                ArrayList<String> sendSqlList = new ArrayList<>();
                for (int i = 0; i < sqlContainer.size(); i++) {
                    sendSqlList.add(sqlContainer.get(i));
                    if ((i + 1) % 200 == 0) {
                        Util.sendData2SlideStreamAllSQL(context, sendSqlList);
                        sendSqlList.clear();
                    }
                }
                if (sendSqlList.size() > 0) {
                    Util.sendData2SlideStreamAllSQL(context, sendSqlList);
                }
            }
        } catch (SQLException e) {
            log.error("======================================queryByTabAndKey2执行SQL报错！SQL===================================" + sb.toString());
            isExeSuccess = false;
            e.printStackTrace();
        } finally {
            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }
        tp2.f0 = connection;
        tp2.f1 = isExeSuccess;
        return tp2;
    }
    public static boolean queryByTabAndKey(KeyedCoProcessFunction.Context context, String tabName, String key, String exeSQL) throws SQLException, IOException {
        boolean isExeSuccess = true;
        String sql = "SELECT fld_guid FROM " + tabName + " WHERE fld_value='" + key + "'";
        String countSQL = "SELECT COUNT(*) as rec FROM " + tabName + " WHERE fld_value='" + key + "'";
        HikariUtil hikariUtil = HikariUtil.getInstance();
        Connection connection = hikariUtil.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<String> sqlContainer = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try {
            statement = connection.createStatement();
            statement.setFetchSize(100);
        ResultSet rs = statement.executeQuery(countSQL);
            int rowCount = 0;
        while (rs.next()) {
            rowCount = rs.getInt("rec");
        }
        if (rowCount != 0){
            resultSet = statement.executeQuery(sql);
            sb.append(exeSQL);
            sb.append("(");

            while (resultSet.next()) {
                ++rowCount;
//            System.out.println("========================================发送fld_guid_op=" + fld_guid_op);
//            Util.sendData2SlideStream(context, exeSQL, fld_guid_op);
                sb.append("'" + resultSet.getString(1) + "',");
                if (rowCount % 200 == 0) {
                    sb.append(")");
                    sqlContainer.add(sb.toString().replace(",)", ")"));
                    // 清空sb,下面使用
                    sb.delete(0, sb.length());
                    sb.append(exeSQL);
                    sb.append("(");
                }
            }
            if (sb.length() > 0) {
                sb.append(")");
                sqlContainer.add(sb.toString().replace(",)", ")"));
                // 清空sb,下面使用
                sb.delete(0, sb.length());
            }
            System.out.println("========================================准备发射！！！！" + sqlContainer.size() + "========================================");
            Util.sendData2SlideStreamAllSQL(context, sqlContainer);
        }
//        Util.sendData2SlideStream2(context, exeSQL, resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            hikariUtil.close(connection);
        }
        return isExeSuccess;
    }

    public static String queryAmount(String fld_value) throws SQLException, IOException {
        String tax = "0";
        String sql = "SELECT fld_amount FROM idx_owe_es_charge_rate_result WHERE fld_value='" + fld_value + "' limit 1";
        HikariUtil hikariUtil = HikariUtil.getInstance();
        Connection connection = hikariUtil.getConnection();
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();
            statement.setFetchSize(100);
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                tax = rs.getString("fld_amount");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            hikariUtil.close(connection);
        }
        return tax;
    }

    public static ArrayList<String> queryFldGuid(String fld_value) throws SQLException, IOException {
        ArrayList<String> arr = new ArrayList<>();
        String sql = "SELECT fld_guid FROM idx_owe_es_commerce_bond_fee_object WHERE fld_value='" + fld_value + "'";
        HikariUtil hikariUtil = HikariUtil.getInstance();
        Connection connection = hikariUtil.getConnection();
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();
            statement.setFetchSize(100);
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                String fld_object = rs.getString("fld_guid");
                arr.add(fld_object);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != rs) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            hikariUtil.close(connection);
        }
        return arr;
    }

    public static boolean querySizeByTabAndKey(String tabName, String key) throws SQLException, IOException {
        boolean isExeSuccess = false;
        String sql = "SELECT count(*) FROM " + tabName + " WHERE fld_value='" + key + "'";
        HikariUtil hikariUtil = HikariUtil.getInstance();
        Connection connection = hikariUtil.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            statement.setFetchSize(100);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Integer size = resultSet.getInt(1);
                if (size > 0) {
                    isExeSuccess = true;
                    break;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != resultSet) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            hikariUtil.close(connection);
        }
        return isExeSuccess;
    }

}
