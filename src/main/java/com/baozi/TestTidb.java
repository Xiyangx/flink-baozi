package com.baozi;

import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/8/19
 *             "     'username' = 'uat_readonly_for_sjzt',\n" +
 *                 "     'password' = 'Sunac@1918',\n" +
 */
public class TestTidb {
    private static final Logger log = LoggerFactory.getLogger(TestTidb.class);

    public static void main(String[] args) {

        log.info("-----------------> start");

        // 10.3.72.83:4000
        //        USER:uat_readonly_for_sjzt
        //        PASSWORD:Sunac@1918
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();;
        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("file:///d:/ti/checkpoint/haha/hh");
        StreamTableEnvironment tenv = StreamTableEnvironment.create(env);
        System.out.println("=========================================");
        // 建表
        tenv.executeSql("CREATE TABLE testdb (\n" +
                "      id INT,\n" +
                "      name STRING,\n" +
                "      age INT,\n" +
                "      PRIMARY KEY(id) NOT ENFORCED\n" +
                "     ) WITH (\n" +
                "     'connector' = 'tidb-cdc',\n" +
                "     'pd-addresses' = '172.17.44.84:2379',\n" +
                "     'tikv.grpc.timeout_in_ms' = '20000',\n" +
                "     'scan.startup.mode' = 'latest-offset',\n" +
                "     'database-name' = 'test',\n" +
                "     'table-name' = 'testdb'\n" +
                ")");
        // 查询
        tenv.executeSql("select * from testdb").print();
    }
}
