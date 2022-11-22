package com.sunac.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MyRunnable extends Thread  {

    private Connection connection = HikariUtil.getInstance().getConnection();
    private ArrayList<String> sqlList;
    private String uid;


    @Override
    public void interrupt() {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("=============================" + uid + ":关闭sink线程失败!!!===================================");
            }
        }

    }

    public MyRunnable(ArrayList<String> sqlList, String uid) throws SQLException {
        this.sqlList = sqlList;
        this.uid = uid;
    }

    @Override
    public void run() {
        Statement statement = null;
        try {
            if (null == connection || (null != connection && connection.isClosed())) {
                connection = HikariUtil.getInstance().getConnection();
            }
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            for (String sql : sqlList) {
                statement.addBatch(sql);
            }
            statement.executeLargeBatch();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("===================================================================" + uid + "执行sink invoke失败！！！===================================================================");
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (null != statement) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }

    }
}
