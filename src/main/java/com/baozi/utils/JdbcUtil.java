package com.baozi.utils;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;

public class JdbcUtil {
    private static DruidDataSource dataSource;

    static {
        try {
            Properties properties = new Properties();
            //读取jdbc.properties属性配置文件
            InputStream is = JdbcUtil.class.getClassLoader().getResourceAsStream("druid.properties");
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
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = (Connection) dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * 关闭连接，放回数据库连接池
     *
     * @param conn
     */
    public static void close(Connection conn, Statement statement) {
        JdbcUtils.close(statement);
        JdbcUtils.close(conn);
    }

}