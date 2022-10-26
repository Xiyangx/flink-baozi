//package com.baozi;
//
//import org.apache.flink.streaming.api.CheckpointingMode;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.table.api.EnvironmentSettings;
//import org.apache.flink.table.api.Table;
//import org.apache.flink.table.api.TableEnvironment;
//import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
//import org.apache.flink.table.catalog.exceptions.DatabaseAlreadyExistException;
//
//import static org.apache.flink.table.api.Expressions.$;
//
//
///**
// * /usr/hdp/3.1.0.0-78/kafka/bin
// *
// * kafka-topics.sh --list --zookeeper 172.17.32.128:2181,172.17.32.129:2181,172.17.32.133:2181
// * kafka-topics.sh --list --zookeeper 172.17.44.36:2181,172.17.44.37:2181,172.17.44.38:2181
// *
// * ./kafka-console-producer.sh --broker-list 172.17.32.128:6667,172.17.32.138:6667,172.17.32.139:6667 --topic test
// *
// * ./kafka-console-producer.sh --broker-list 172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667 --topic testdb
// *
// * ./kafka-console-consumer.sh --bootstrap-server 172.17.32.128:6667,172.17.32.138:6667,172.17.32.139:6667 --topic test
// *
// *./kafka-console-consumer.sh --bootstrap-server 172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667 --topic testdb --from-beginning
// *
// * ./kafka-topics.sh --create --topic es_charge_other_2 --replication-factor 3 --partitions 3 --zookeeper 172.17.44.36:2181,172.17.44.37:2181,172.17.44.38:2181
// * {"id":1,"name":"zs","age":38,"gender":"male"}
// */
//public class Demo1_TableSql {
//
//    public static void main(String[] args) {
//
//        // 把kafka中的一个topic： doit30-2 数据，映射成一张flinkSql表
//        // json :  {"id":1,"name":"zs","age":28,"gender":"male"}
//        // create table_x (id int,name string,age int,gender string)
//        // 172.17.32.128
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//
//        EnvironmentSettings settings = EnvironmentSettings.inStreamingMode();
//        StreamTableEnvironment tenv = StreamTableEnvironment.create(env, settings);
//
//        tenv.executeSql(
//                "create table t_kafka                                  "
//                        + " (                                                   "
//                        + "   id int,                                           "
//                        + "   name string,                                      "
//                        + "   age int,                                          "
//                        + "   gender string                                     "
//                        + " )                                                   "
//                        + " WITH (                                              "
//                        + "  'connector' = 'kafka',                             "
//                        + "  'topic' = 'test',                              "
//                        + "  'properties.bootstrap.servers' = '172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667',"
//                        + "  'properties.group.id' = 'g1',                      "
//                        + "  'scan.startup.mode' = 'earliest-offset',           "
//                        + "  'format' = 'json',                                 "
//                        + "  'json.fail-on-missing-field' = 'false',            "
//                        + "  'json.ignore-parse-errors' = 'true'                "
//                        + " )                                                   "
//        );
//
//
//        /**
//         * 把sql表名， 转成 table对象
//         */
////        Table table = tenv.from("t_kafka");
////        // 利用table api进行查询计算
////        table.groupBy($("gender"))
////                .select($("gender"), $("age").avg())
////                .execute()
////                .print();
//
//        tenv.executeSql("select *  from  t_kafka").print();
//
//
//    }
//}
