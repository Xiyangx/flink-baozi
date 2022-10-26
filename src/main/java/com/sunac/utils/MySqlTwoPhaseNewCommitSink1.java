package com.sunac.utils;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/12 11:29 上午
 * @Version 1.0
 */
public class MySqlTwoPhaseNewCommitSink1 extends TwoPhaseCommitSinkFunction<Tuple2<String, String>, Connection, Void> {
    private Connection connection;

    public MySqlTwoPhaseNewCommitSink1() {
        super(new KryoSerializer<>(Connection.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        System.err.println("start open……");
    }

    @Override
    protected void invoke(Connection connection, Tuple2<String, String> tp, Context context) throws Exception {
        String executeSql = tp.f0;
        String fld_guid = tp.f1;
        PreparedStatement pstmt = connection.prepareStatement(executeSql);
        // 宽表的主键：
        pstmt.setString(1, fld_guid);
        System.out.println("******************************************************");
        System.out.println(executeSql + fld_guid);
        System.out.println("******************************************************");
        pstmt.executeUpdate();
        System.out.println("===================mysql 操作成功===================");
        pstmt.close();
    }

    @Override
    protected Connection beginTransaction() throws Exception {
        this.connection = JdbcUtils.getConnection();
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

