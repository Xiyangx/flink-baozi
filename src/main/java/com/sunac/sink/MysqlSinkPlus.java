package com.sunac.sink;


import com.sunac.utils.HikariUtil;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.CheckpointListener;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mu
 * @ClassName MysqlSinkPlus
 * @Description TODO
 * @date 2022/4/14 10:03
 * @Version 1.0
 */
public class MysqlSinkPlus extends TwoPhaseCommitSinkFunction<Tuple2<String, String>, MysqlSinkPlus.ConnectionState, Void> implements CheckpointedFunction, CheckpointListener {

    //定义建立数据库连接的方法
    public static class ConnectionState {
        private final transient Connection connection;
        private final transient Statement statement;

        public ConnectionState(Connection connection, Statement statement) {
            this.connection = connection;
            this.statement = statement;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(MysqlSinkPlus.class);
    private AtomicInteger COUNT = new AtomicInteger(0);

    public MysqlSinkPlus() {
        super(new KryoSerializer<>(MysqlSinkPlus.ConnectionState.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
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
    protected void invoke(MysqlSinkPlus.ConnectionState transaction, Tuple2<String, String> tp, Context context) throws Exception {
        try {
            String executeSql = tp.f0;
            String fld_guid = tp.f1;
            if (0 == COUNT.get()) {
                System.out.println("---------------------------------------AtomicInteger:调用！！！！");
                transaction.connection.commit();
                COUNT.incrementAndGet();
            }
            System.out.println("===================================================================执行invoke！！！！！！！！！！！！" + executeSql + "==fld_guid==" + fld_guid);
            Connection connection = transaction.connection;
            Statement statement = transaction.statement;

            statement.executeQuery("");

        } catch (Exception e) {
            System.out.println("==============================" + e.toString());
        }

    }

    /**
     * 获取连接，开启手动提交事物（getConnection方法中）
     *
     * @return
     * @throws Exception
     */
    @Override
    protected MysqlSinkPlus.ConnectionState beginTransaction() throws Exception {
        System.err.println("===========执行beginTransaction！！！！============");
        Connection connection = HikariUtil.getInstance().getConnection();
        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        System.err.println("===========connection的id:============" + connection.hashCode());
        return new ConnectionState(connection, statement);
    }

    /**
     * 预提交，这里预提交的逻辑在invoke方法中
     *
     * @throws Exception
     */
    @Override
    protected void preCommit(MysqlSinkPlus.ConnectionState connection) throws Exception {
        log.error("start preCommit...");
    }

    /**
     * 如果invoke方法执行正常，则提交事务
     */
    @Override
    protected void commit(MysqlSinkPlus.ConnectionState transaction) {
        System.err.println("===========执行commit！！！！============");
        Connection connection = transaction.connection;
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 如果invoke执行异常则回滚事物，下一次的checkpoint操作也不会执行
     */
    @Override
    protected void abort(MysqlSinkPlus.ConnectionState transaction) {
        System.err.println("===========abort！！！！============");
        try {
            transaction.connection.rollback();
            transaction.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


