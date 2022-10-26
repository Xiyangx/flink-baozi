//package com.sunac;
//
//import com.sunac.domain.EsChargeBill;
//import com.sunac.domain.EsChargeDB;
//import com.sunac.domain.EsChargeIncomingDataBill;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.functions.RichMapFunction;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.restartstrategy.RestartStrategies;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.typeinfo.TypeHint;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.api.java.tuple.Tuple3;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
//import org.apache.flink.streaming.api.CheckpointingMode;
//import org.apache.flink.streaming.api.datastream.*;
//import org.apache.flink.streaming.api.environment.CheckpointConfig;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
//import org.apache.flink.streaming.api.functions.co.RichCoMapFunction;
//import org.apache.flink.util.Collector;
//
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.baozi
// * @date:2022/9/1
// */
//public class es_charge_incoming_data_bill_2_test {
//    public static void main(String[] args) throws Exception {
//
//        Configuration configuration = new Configuration();
//        configuration.setInteger("rest.port", 8822);
//        configuration.setString("execution.savepoint.path", "file:///D://charge//bill");
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
//        env.setParallelism(8);
//        env.setMaxParallelism(128);
//
//
//        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
//        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt//bill");
//
//
//        DataStreamSource<String> stream1 = env.socketTextStream("192.168.200.10", 7777);
//        DataStreamSource<String> stream2 = env.socketTextStream("192.168.200.10", 8888);
//        DataStreamSource<String> stream3 = env.socketTextStream("192.168.200.10", 9999);
//
//
////        new EsChargeIncomingDataBill("1", "1"),
////                new EsChargeIncomingDataBill("1", "2"),
////                new EsChargeIncomingDataBill("2", "3"));
//
//
////        Arrays.asList(
////                new EsChargeBill("1", 100,"发票代码1-1","uuid1","2021-01-01"),
////                new EsChargeBill("2", 200,"发票代码1-2","uuid2","2021-01-02"),
////                new EsChargeBill("3", 200,"发票代码2-1","uuid3","2021-01-02"));
////        DataSet<EsChargeBill> esChargeBillData = env.fromCollection(dataBill);
//
//
//        SingleOutputStreamOperator<EsChargeIncomingDataBill> bill1 = stream2.map(new MapFunction<String, EsChargeIncomingDataBill>() {
//            @Override
//            public EsChargeIncomingDataBill map(String s) throws Exception {
//                String[] split = s.split(",");
//                return new EsChargeIncomingDataBill(split[0], split[1]);
//            }
//        });
//
//        SingleOutputStreamOperator<EsChargeBill> bill2 = stream3.map(new MapFunction<String, EsChargeBill>() {
//            @Override
//            public EsChargeBill map(String s) throws Exception {
//                String[] split = s.split(",");
//                String s1 = split[0];
//                String s2 = split[1];
//                String s3 = split[2];
//                String s4 = split[3];
//                String s5 = split[4];
//                return new EsChargeBill(s1, Integer.parseInt(s2), s3, s4, s5);
//            }
//        });
////        bill1.keyBy(t->t.getFld_data_src_guid()).map(new RichMapFunction<EsChargeIncomingDataBill, String>() {
////            MapState<String, EsChargeIncomingDataBill> mapState;
////            @Override
////            public void open(Configuration parameters) throws Exception {
////                RuntimeContext runtimeContext = getRuntimeContext();
////                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingDataBill>("map_stat", String.class, EsChargeIncomingDataBill.class));
////                Iterator<Map.Entry<String, EsChargeIncomingDataBill>> iterator = mapState.iterator();
////
////                while (iterator.hasNext()) {
////                    System.out.println("=============================开始遍历EsChargeIncomingDataBill的state=============================");
////                    Map.Entry<String, EsChargeIncomingDataBill> map = iterator.next();
////                    String key1 = map.getKey();
////                    EsChargeIncomingDataBill value = map.getValue();
////                    String mapKey = key1.split("#")[0];
////                    System.out.println("=============================mapKey=" + mapKey + "=============================");
////
////                }
////            }
////
////            @Override
////            public String map(EsChargeIncomingDataBill esChargeIncomingDataBill) throws Exception {
////                return null;
////            }
////        }).uid("bill1");
//
//        ConnectedStreams<String, EsChargeIncomingDataBill> connect1 = stream1.connect(bill1);
//
//        SingleOutputStreamOperator<ArrayList<EsChargeIncomingDataBill>> join1 = connect1
//                .keyBy(s -> s, t -> t.getFld_data_src_guid())
//                .map(new RichCoMapFunction<String, EsChargeIncomingDataBill, ArrayList<EsChargeIncomingDataBill>>() {
//            MapState<String, EsChargeIncomingDataBill> mapState;
//            ArrayList<EsChargeIncomingDataBill> esChargeIncomingDataBillsList;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                esChargeIncomingDataBillsList = new ArrayList<>();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingDataBill>("map_stat", String.class, EsChargeIncomingDataBill.class));
//                System.out.println("============================回复了EsChargeIncomingDataBill的state！！！！=============================");
//            }
//            @Override
//            public ArrayList<EsChargeIncomingDataBill> map1(String key) throws Exception {
//                System.out.println("=============================主流来了：key=" + key);
//                Iterator<Map.Entry<String, EsChargeIncomingDataBill>> iterator = mapState.iterator();
//                esChargeIncomingDataBillsList.clear();
//                while (iterator.hasNext()) {
//                    System.out.println("=============================开始遍历EsChargeIncomingDataBill的state=============================");
//                    Map.Entry<String, EsChargeIncomingDataBill> map = iterator.next();
//                    String key1 = map.getKey();
//                    EsChargeIncomingDataBill value = map.getValue();
//                    String mapKey = key1.split("#")[0];
//                    if (mapKey.equals(key)) {
//                        esChargeIncomingDataBillsList.add(value);
//                    }
//                }
//                return esChargeIncomingDataBillsList;
//            }
//
//            @Override
//            public ArrayList<EsChargeIncomingDataBill> map2(EsChargeIncomingDataBill esChargeIncomingDataBill) throws Exception {
//                return null;
//            }
//        }).uid("bill1");
//
//
//        SingleOutputStreamOperator<EsChargeBill> uid = join1.connect(bill2).keyBy(t -> "666", t2 -> "666").map(new RichCoMapFunction<ArrayList<EsChargeIncomingDataBill>, EsChargeBill, EsChargeBill>() {
//            MapState<String, EsChargeBill> mapState;
//            ArrayList<EsChargeBill> list = null;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                list = new ArrayList<>();
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeBill>("map_stat", String.class, EsChargeBill.class));
//            }
//
//            @Override
//            public EsChargeBill map1(ArrayList<EsChargeIncomingDataBill> esChargeIncomingDataBills) throws Exception {
//                list.clear();
//                for (EsChargeIncomingDataBill bill : esChargeIncomingDataBills) {
//                    String fld_bill_guid = bill.getFld_bill_guid();
//                    EsChargeBill esChargeBill = mapState.get(fld_bill_guid);
//                    list.add(esChargeBill);
//                }
//                EsChargeBill ret = null;
//                for (EsChargeBill b2 : list) {
//                    if (ret == null) {
//                        ret = b2;
//                        continue;
//                    }
//                    if (b2.compareTo(ret) > 0) {
//                        // 取新来的值最大的
//                        ret = b2;
//                        continue;
//                    }
//                }
//                return ret;
//            }
//            @Override
//            public EsChargeBill map2(EsChargeBill esChargeBill) throws Exception {
//                return null;
//            }
//        }).uid("bill2");
//        uid.print();
//        env.execute();
//
//    }
//}
