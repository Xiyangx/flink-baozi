package com.baozi.utils;

import com.alibaba.druid.util.JdbcUtils;
import com.baozi.es_charge_owner_fee;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi.utils
 * @date:2022/8/31
 */

public class Test {
    public static void main(String[] args) {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = JdbcUtil.getConnection();
            String s = "select fld_allot_date from maindb.es_charge_owner_fee GROUP BY fld_allot_date";
            ResultSet res = conn.createStatement().executeQuery(s);
            while (res.next()) {
                String dat = res.getString("fld_allot_date");
                System.out.println("==========" + dat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.close(conn, statement);
        }
    }
}
