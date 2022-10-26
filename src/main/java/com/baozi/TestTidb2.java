package com.baozi;

import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/8/19
 *             "     'username' = 'uat_readonly_for_sjzt',\n" +
 *                 "     'password' = 'Sunac@1918',\n" +
 * 数据中台只读账号
 * Ip: 10.3.8.208
 * Database: maindb
 * user: readonly_for_sjzt
 * Password:  Sunac@1918
 *
 */
public class TestTidb2 {
    public static void main(String[] args) {
        // 10.3.72.83:4000
        //        USER:uat_readonly_for_sjzt
        //        PASSWORD:Sunac@1918
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000, CheckpointingMode.EXACTLY_ONCE).setParallelism(2);
        env.getCheckpointConfig().setCheckpointStorage("file:///e:/checkpoint");
        System.out.println("==========================");
        StreamTableEnvironment tenv = StreamTableEnvironment.create(env);
        // 建表
        tenv.executeSql("CREATE TABLE es_charge_owner_fee (\n" +
                "      fld_guid string,\n" +
                "      fld_create_user string,\n" +
                "      fld_modify_user string,\n" +
                "      PRIMARY KEY(fld_guid) NOT ENFORCED\n" +
                "     ) WITH (\n" +
                "     'connector' = 'tidb-cdc',\n" +
                "     'pd-addresses' = '172.17.44.84:2379',\n" +
                "     'tikv.grpc.timeout_in_ms' = '120000',\n" +
                "     'database-name' = 'maindb',\n" +
                "     'table-name' = 'es_charge_owner_fee'\n" +
                ")");
        // 查询
        tenv.executeSql("select * from es_charge_owner_fee").print();

    }
}
