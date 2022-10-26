package com.sunac.utils;

import com.sunac.Config;
import com.sunac.domain.ColumAttribute;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import lombok.SneakyThrows;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.sunac.domain.ComplexStructureDomain.TABLES;

/**
 * https://gitee.com/fang_wei/fwmagic-flink/blob/master/src/main/java/com/fwmagic/flink/sink/MySqlTwoPhaseNewCommitSink2.java
 *
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.utils
 * @date:2022/9/27
 */
public class MySqlTwoPhaseNewCommitSink extends TwoPhaseCommitSinkFunction<Tuple3<String, String, CommonDomain>, Connection, Void>{
    private Connection connection;

    public MySqlTwoPhaseNewCommitSink() {
        super(new KryoSerializer<>(Connection.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        System.err.println("start open……");
    }

    @Override
    protected void invoke(Connection connection, Tuple3<String, String, CommonDomain> tp, Context context) throws Exception {
        String fullClassName = tp.f0;
        String fld_guid = tp.f1;
        String operation_type = tp.f2.getOperation_type();
        HashMap<String, String> updateInfoMap = tp.f2.getUpdateInfoMap();

        HashMap<String, ColumAttribute> obj = TABLES.get(fullClassName);
        HashMap<String, Object> hm = new HashMap<>();

        /***
         *把需要跟新的字段：包括原始字段和别名拿出来
         */
        for (Map.Entry<String, ColumAttribute> entry : obj.entrySet()) {
            ColumAttribute value = entry.getValue();
            String oldName = entry.getKey();
            String newName = value.getNewName();

            boolean is_update = value.isUpdate_column();
            boolean is_delete = value.isDelete_column();
            boolean is_insert = value.isInsert_column();
            boolean is_table = value.isTable_column();

            // 判断需要更新的字段：三个条件都要满足
            if (is_table && is_update && updateInfoMap.containsKey(oldName) && operation_type.equals(Config.UPDATE)) {
                hm.put(newName, oldName);
            }
            if (is_table && is_delete && operation_type.equals(Config.DELETE)) {
                hm.put(newName, value.getDefaultValue());
            }
            if (is_table && is_insert && operation_type.equals(Config.INSERT)) {
                hm.put(newName, oldName);
            }
        }
        if (hm.size() == 0) {
            return;
        }
        Class<? extends CommonDomain> tClass = tp.f2.getClass();
        StringBuilder sqlBuf = new StringBuilder();
        sqlBuf.append("update all_data_info_2 set ");
        int index = 0;
        int size = hm.size();
        // 拼接sql
        for (Map.Entry<String, Object> entry : hm.entrySet()) {
            String newName = entry.getKey();
            Object oldNameOrValue = entry.getValue();
            // 获取字段类型
            Object value = null;
            if (operation_type.equals(Config.DELETE)) {
                value = entry.getValue();
            } else {
                Class<?> fieldType = TypeUtils.getFieldType(tClass, (String) oldNameOrValue).getType();
                value = TypeUtils.getGetMethod(tp.f2, (String) oldNameOrValue);
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
        sqlBuf.append(" where fld_guid=?");
        PreparedStatement pstmt = connection.prepareStatement(sqlBuf.toString());
        // 宽表的主键：
        pstmt.setString(1, fld_guid);
        pstmt.executeUpdate();
        System.out.println("===================mysql 操作成功===================");
    }

    @Override
    protected Connection beginTransaction() throws Exception {
        this.connection = JdbcUtils.getConnection();
        this.connection.setAutoCommit(false);
        System.err.println("start beginTransaction......." + this.connection);
        return this.connection;
    }

    @Override
    protected void preCommit(Connection connection) throws Exception {
        connection = this.connection;
        System.err.println("start preCommit......." + connection);
    }

    @Override
    protected void commit(Connection connection) {
        connection = this.connection;
        System.err.println("start commit......." + connection);
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void abort(Connection connection) {
        System.err.println("start abort rollback......." + this.connection);
        try {
            connection.rollback();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

