package com.sunac.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baozi.utils.JdbcUtil;
import com.sunac.Config;
import com.sunac.domain.ColumAttribute;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.ComplexStructureDomain;
import com.sunac.entity.EsChargeIncomingData;
import com.sunac.entity.EsChargeSettleAccountsDetail;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import static com.sunac.entity.ComplexStructureDomain.TABLES;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


public class JdbcUtils {
    private static DruidDataSource dataSource;


    static {
        try {
            Properties properties = new Properties();
            //读取jdbc.properties属性配置文件
            InputStream is = JdbcUtil.class.getClassLoader().getResourceAsStream("druid_2.properties");
            //从流中记载数据
            properties.load(is);
            //创建数据库连接池
            dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接池中的连接
     *
     * @return 如果返回null, 说明连接失败, 有值就是获取连接成功
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static String makeMd5(String... s) {
        StringBuilder sb = new StringBuilder();
        for (String e : s) {
            sb.append(e);
        }
        return DigestUtils.md5Hex(sb.toString());
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

        sqlBuf.append(" where fld_guid=?");
        return sqlBuf.toString();
    }

    public static JdbcInputFormat.JdbcInputFormatBuilder getJdbcInputFormat() {
        return JdbcInputFormat.buildJdbcInputFormat().setDrivername("com.mysql.cj.jdbc.Driver")
                .setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
//                .setDBUrl("jdbc:mysql://10.3.72.83:4000/sjzt")
                .setUsername("report_readonly_user").setPassword("TIDB_report_user");
//                .setUsername("uat_sjzt").setPassword("UAT_sjzt");
    }

}
