package com.sunac.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接对象
 * Created by yuandl on 2016-12-16.
 */
public class DBConnectionPool {
private static volatile DBConnectionPool dbConnection;
public HikariDataSource dataSource;

/**
 * 在构造函数初始化的时候获取数据库连接
 */
private DBConnectionPool() {
    try {
        // 因为dataSource是全局变量、默认初始化值为null
        if (dataSource == null) {
            // 通过字节输入流 读取 配置文件  hikaricp.properties
            InputStream is = HikariUtil.class.getClassLoader().getResourceAsStream("hikaricp.properties");
            // 因为HikariConfig类不可以加载io，但是可以加载Properties。因此：将输入流is封装到props
            Properties props = new Properties();
            props.load(is);
            HikariConfig config = new HikariConfig(props);
            dataSource = new HikariDataSource(config);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    
}

public static DBConnectionPool getInstance() {
    if (dbConnection == null) {
        synchronized (DBConnectionPool.class) {
            if (dbConnection == null) {
                dbConnection = new DBConnectionPool();
            }
        }
    }
    return dbConnection;
}

/**
 * 获取数据库连接
 *
 * @return 数据库连接
 */
public final synchronized Connection getConnection() throws SQLException {
    return dbConnection.getConnection();
}

/**
 * finalize()方法是在垃圾收集器删除对象之前对这个对象调用的。
 *
 * @throws Throwable
 */
@Override
protected void finalize() throws Throwable {
    dataSource.close();
    super.finalize();
}
}