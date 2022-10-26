//package com.sunac.test;
//
//import com.sunac.Config;
//import org.apache.flink.api.common.functions.RichMapFunction;
//import org.apache.flink.api.common.state.BroadcastState;
//import org.apache.flink.api.common.state.ListStateDescriptor;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
//import org.apache.flink.api.common.typeinfo.TypeHint;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.java.operators.DataSource;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.datastream.*;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
//import org.apache.flink.util.Collector;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac.test
// * @date:2022/9/14
// */
//public class BroadCastTest {
//    public static void main(String[] args) throws Exception {
//        Configuration configuration = new Configuration();
//        configuration.setInteger("rest.port", 8822);
//
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
//
//        DataStreamSource<Tuple2<String, Set<String>>> hh = env.fromCollection(list);
//
//
//        MapStateDescriptor<String, Tuple2<String, Set<String>>> userInfoStateDesc1 = new MapStateDescriptor<>("userInfoStateDesc", TypeInformation.of(String.class), TypeInformation.of(new TypeHint<Tuple2<String, Set<String>>>() {
//        }));
//
//        BroadcastStream<Tuple2<String, Set<String>>> broadcast = hh.broadcast(userInfoStateDesc1);
//        DataStreamSource<String> stream1 = env.socketTextStream("192.168.200.10", 8888);
//
//        SingleOutputStreamOperator<String> process = stream1.connect(broadcast).process(new BroadcastProcessFunction<String, Tuple2<String, Set<String>>, String>() {
//            MapStateDescriptor<String, Tuple2<String, Set<String>>> userInfoStateDesc1;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                userInfoStateDesc1 = new MapStateDescriptor<>("userInfoStateDesc", TypeInformation.of(String.class), TypeInformation.of(new TypeHint<Tuple2<String, Set<String>>>() {
//                }));
//            }
//
//            @Override
//            public void processElement(String s, ReadOnlyContext context, Collector<String> collector) throws Exception {
//                ReadOnlyBroadcastState<String, Tuple2<String, Set<String>>> broadcastState = context.getBroadcastState(userInfoStateDesc1);
//                Tuple2<String, Set<String>> es_charge_incoming_data = broadcastState.get("es_charge_incoming_data");
//                Set<String> sets = es_charge_incoming_data.f1;
//                for (String ss:sets){
//                    System.out.println("====================================" + ss);
//                    collector.collect(ss);
//                }
//            }
//
//            @Override
//            public void processBroadcastElement(Tuple2<String, Set<String>> tp, Context context, Collector<String> collector) throws Exception {
//                BroadcastState<String, Tuple2<String, Set<String>>> broadcastState = context.getBroadcastState(userInfoStateDesc1);
//                broadcastState.put(tp.f0, tp);
//            }
//        });
//        process.print();
//        env.execute();
//    }
//}
