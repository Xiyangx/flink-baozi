package com.sunac.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;


/***
 * 目前大多数网上能搜索到的关于flink两阶段提交的内容大多雷同，均为直接对连接进行序列化，
 * 并且由单一的直接连接的方式处理。本文章结合网上的部分代码进行优化，解决了使用连接池各种异常的问题，
 * 并且处理好了单纯序列化连接带来的弊端，经过笔者生产在线跑数稳定无异常。
 */
public class HikariUtil {
    private final Logger log = LoggerFactory.getLogger(HikariUtil.class);
    public static HikariDataSource dataSource;
    private static volatile HikariUtil hikariUtilPoll;

    /**
     * 在构造函数初始化的时候获取数据库连接
     */
    private HikariUtil() {
        try {
            if (dataSource == null) {
                InputStream is = HikariUtil.class.getClassLoader().getResourceAsStream("hikaricp.properties");
                Properties props = new Properties();
                props.load(is);
                HikariConfig config = new HikariConfig(props);
                dataSource = new HikariDataSource(config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HikariUtil getInstance() {
        if (hikariUtilPoll == null) {
            synchronized (DBConnectionPool.class) {
                if (hikariUtilPoll == null) {
                    hikariUtilPoll = new HikariUtil();
                }
            }
        }
        return hikariUtilPoll;
    }

    public final synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    protected void finalize() throws Throwable {
        dataSource.close();
        super.finalize();
    }


    public void commit(Connection conn) throws SQLException {
        conn.commit();
    }

    public void rollback(Connection conn) throws SQLException {
        conn.rollback();
    }

    public void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("关闭连接失败,Connection:" + conn);
                e.printStackTrace();
            }
        }
    }
}
