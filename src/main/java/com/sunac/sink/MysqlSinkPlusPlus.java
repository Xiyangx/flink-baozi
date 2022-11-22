package com.sunac.sink;

import com.sunac.utils.HikariUtil;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mu
 * @ClassName MysqlSinkPlus
 * @Description TODO
 * @date 2022/4/14 10:03
 * @Version 1.0
 */
public class MysqlSinkPlusPlus extends TwoPhaseCommitSinkFunction<ArrayList<String>, MysqlSinkPlusPlus.ConnectionState, Void> {
    //定义建立数据库连接的方法
    public static class ConnectionState {
        private final transient Connection connection;
        private final transient Statement statement;

        public ConnectionState(Connection connection, Statement statement) {
            this.connection = connection;
            this.statement = statement;
        }
    }
    private static final Logger log = LoggerFactory.getLogger(MysqlSinkPlusPlus.class);
    private AtomicInteger COUNT = new AtomicInteger(0);
    public MysqlSinkPlusPlus() {
        super(new KryoSerializer<>(ConnectionState.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
    }
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        System.err.println("start open……");
    }
    /**
     * 执行数据库入库操作  task初始化的时候调用
     *
     * @param context
     * @throws Exception
     */
    @Override
    protected void invoke(ConnectionState transaction, ArrayList<String> sqlList, Context context) throws Exception {
        try {
            if (0 == COUNT.get()) {
                transaction.connection.commit();
                COUNT.incrementAndGet();
            }
            for (String sql : sqlList) {
                transaction.statement.addBatch(sql);
                System.err.println("==================================================================添加了sql:==================================================================" + sql);
            }
        } catch (Exception e) {
            System.err.println("==============================" + e.toString());
        }
    }
    /**
     * 获取连接，开启手动提交事物（getConnection方法中）
     *
     * @return
     * @throws Exception
     */
    @Override
    protected ConnectionState beginTransaction() throws Exception {
        System.err.println("===========执行beginTransaction！！！！============");
        Connection connection = HikariUtil.getInstance().getConnection();
        Statement statement = connection.createStatement();
        connection.setAutoCommit(false);
        System.err.println("===========connection的id:============" + connection.hashCode());
        return new ConnectionState(connection, statement);
    }
    /**
     * 预提交，这里预提交的逻辑在invoke方法中
     *
     * @throws Exception
     */
    @Override
    protected void preCommit(ConnectionState connection) throws Exception {
        log.error("start preCommit...");
    }
    /**
     * 如果invoke方法执行正常，则提交事务
     */
    @Override
    protected void commit(ConnectionState transaction) {
        System.err.println("===========执行commit！！！！============");
        try {
            transaction.statement.executeLargeBatch();
            transaction.connection.commit();
            transaction.statement.close();
            transaction.connection.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     * 如果invoke执行异常则回滚事物，下一次的checkpoint操作也不会执行
     */
    @Override
    protected void abort(ConnectionState transaction) {
        System.err.println("===========abort！！！！============");
        try {
            transaction.connection.rollback();
            transaction.statement.close();
            transaction.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


