package com.baozi;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.RichCoMapFunction;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/9/1
 *
 * ./kafka-console-producer.sh --broker-list 172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667 --topic testdb
 */
public class TestConnect {
    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        configuration.setInteger("rest.port", 8822);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        env.setParallelism(10);

        // 数字字符流
        DataStreamSource<String> stream1 = env.socketTextStream("192.168.200.10", 8888);
        // 字母字符流
        DataStreamSource<String> stream2 = env.socketTextStream("192.168.200.10", 9999);
        SingleOutputStreamOperator<Integer> map3 = stream2.map(t -> Integer.parseInt(t));

        ConnectedStreams<String, Integer> connect = stream1.connect(map3);

        SingleOutputStreamOperator<String> s1 = connect.keyBy(t -> t, h -> h).map(new RichCoMapFunction<String, Integer, String>() {
            @Override
            public String map1(String s) throws Exception {
                return null;
            }

            @Override
            public String map2(Integer integer) throws Exception {
                return null;
            }
        });





        DataStreamSource<String> stream3 = env.socketTextStream("192.168.200.10", 7777);


        DataStream<String> union = stream1.union(stream2, stream3);


        ConnectedStreams<String, String> connectedStreams = stream1.connect(stream2);



        SingleOutputStreamOperator<String> hh = connectedStreams.map(new RichCoMapFunction<String, String, String>() {

            @Override
            public String map1(String s) throws Exception {
                return s + "骚丁宝1";
            }

            @Override
            public String map2(String s) throws Exception {
                return s + "骚丁宝2";
            }
        });
        KeyedStream<String, Integer> stringIntegerKeyedStream = hh.keyBy(new KeySelector<String, Integer>() {
            @Override
            public Integer getKey(String s) throws Exception {
                return 0;

            }
        });

        KeyedStream<String, String> stream33 = stream3.keyBy(new KeySelector<String, String>() {
            @Override
            public String getKey(String s) throws Exception {
                return s;
            }
        });

        ConnectedStreams<String, String> connect3 = hh.connect(stream33);


        connect3.map(new RichCoMapFunction<String, String, String>() {

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
            }


            @Override
            public String map1(String s) throws Exception {
                return s;
            }
            @Override
            public String map2(String s) throws Exception {
                return "骚丁宝3";
            }
        }).print();

        env.execute();

    }
}
