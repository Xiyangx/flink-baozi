package com.baozi;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/8/29
 */
public class Test {
    public static void main(String[] args) throws Exception {
        // 混合环境创建
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE);
//        env.getCheckpointConfig().setCheckpointStorage("file:///d:/ti/checkpoint/haha/hh");
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

//        数据中台只读账号
//        Ip: 10.3.8.208
//        Database: maindb
//        user: readonly_for_sjzt
//        Password:  Sunac@1918
//        tableEnv.executeSql(
//                "create table es_charge_owner_fee                       "
//                        + " (                                              "
//                        + "   fld_guid STRING,                             "
//                        + "   fld_reason_remark STRING,                    "
//                        + "   fld_price STRING,                            "
//                        + "   fld_rebate DECIMAL(18,2),                    "
//                        + "   fld_resource STRING,                         "
//                        + "   fld_desc STRING,                             "
//                        + "   fld_adjust_guid STRING,                      "
//                        + "   fld_area_guid  STRING,                       "
//                        + "   PRIMARY KEY (fld_guid) NOT ENFORCED                        "
//                        + " )                                              "
//                        + " WITH (                                         "
//                        + "  'connector' = 'jdbc',                         "
//                        + "  'url' = 'jdbc:mysql://10.3.8.208:4000/maindb', "
//                        + "  'table-name' = 'es_charge_owner_fee',         "
//                        + "  'username' = 'readonly_for_sjzt',             "
//                        + "  'password' = 'Sunac@1918'                     "
//                        + " )                                               "
//        );
        DataSet<Row> dbData =env.createInput(
                JdbcInputFormat.buildJdbcInputFormat()
                                .setDrivername("com.mysql.cj.jdbc.Driver")
                                .setDBUrl("jdbc:mysql://10.3.8.208:4000/maindb")
                                .setUsername("readonly_for_sjzt")
                                .setPassword("Sunac@1918")
                                .setQuery("SELECT " +
                                        "fld_reason_remark," +
                                        "fld_price," +
                                        "fld_rebate," +
                                        "fld_resource," +
                                        "fld_desc," +
                                        "fld_adjust_guid," +
                                        "fld_guid,fld_area_guid FROM es_charge_owner_fee limit 100")
                                .setRowTypeInfo(new RowTypeInfo(
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        BasicTypeInfo.BIG_DEC_TYPE_INFO,
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        BasicTypeInfo.STRING_TYPE_INFO
                                        )).finish()
                );
        dbData.print();

//        tableEnv.executeSql("select *  from  es_charge_owner_fee limit 100").print();
//        DataStream<Row> dataStream = tableEnv.toChangelogStream(tableEnv.from("es_charge_owner_fee"));
//        dataStream.print();
        env.execute();
    }
}
