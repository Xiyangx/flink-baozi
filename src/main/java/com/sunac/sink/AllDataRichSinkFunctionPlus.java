package com.sunac.sink;

import com.sunac.utils.HikariUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AllDataRichSinkFunctionPlus extends RichSinkFunction<ArrayList<String>> {
    private static final Logger log = LoggerFactory.getLogger(AllDataRichSinkFunctionPlus.class);
    private String uid;
    transient Connection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = HikariUtil.getInstance().getConnection();
    }

    @Override
    public void close() throws Exception {
        if (null != connection) {
            connection.close();
        }
    }

    public AllDataRichSinkFunctionPlus(String uid) {
        this.uid = uid;
    }

    @Override
    public void invoke(ArrayList<String> sqlList, Context context) {
        Statement statement = null;
        log.info("===================================================================" + uid + "开始执行sink invoke！！！===================================================================");
        try {
            if (null == connection || (null != connection && connection.isClosed())) {
                connection = HikariUtil.getInstance().getConnection();
            }
            connection.setAutoCommit(false);
            for (String sql : sqlList) {
                statement = connection.createStatement();
                statement.addBatch(sql);
            }
            statement.executeLargeBatch();
            connection.commit();
        } catch (SQLException e) {
            log.error("===================================================================" + uid + "执行sink invoke失败！！！===================================================================");
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
