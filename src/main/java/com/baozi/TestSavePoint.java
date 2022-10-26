package com.baozi;

import com.baozi.domain.ChargeOwnerFee;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/9/1
 */
public class TestSavePoint {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "hive");
        // 显式声明为本地运行环境，且带webUI
        Configuration configuration = new Configuration();
        configuration.setInteger("rest.port", 8081);
//        configuration.setString("execution.savepoint.path", "file:///H://flink-baozi//src//main//java//baozi_ck//6b46004df0d690d8b706b7110b8df634//chk-8");
        configuration.setString("execution.savepoint.path", "file:///D://charge");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        /**
         * 本地运行模式时，程序的默认并行度为 ，你的cpu的逻辑核数
         */
        env.setParallelism(5);  // 默认并行度可以通过env人为指定
        // 设置任务的最大并行度 也就是keyGroup的个数
        env.setMaxParallelism(128);


//1255582eb31e362b62988d0baed32fa9

        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt");
        env.setStateBackend(embeddedRocksDBStateBackend);
        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);  // 传入两个最基本ck参数：间隔时长，ck模式
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointStorage("file:///H://flink-baozi//src//main//java//baozi_ck");
        //hdfs://172.17.44.25:8020/tmp/data/es_charge_owner_fee
        checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);  // job cancel调时，保留最后一次ck数据
        SingleOutputStreamOperator<String> source = env.socketTextStream("192.168.200.10", 8888).setParallelism(1);
        SingleOutputStreamOperator<ChargeOwnerFee> map = source.map(new MapFunction<String, ChargeOwnerFee>() {
            @Override
            public ChargeOwnerFee map(String s) throws Exception {
                return new ChargeOwnerFee(s, s, s, new BigDecimal(100), s, s, s, s, s);
            }
        });
        KeyedStream<ChargeOwnerFee, String> keyed = map.keyBy(k -> k.getPk());

        keyed.process(new KeyedProcessFunction<String, ChargeOwnerFee, String>() {
            MapState<String, ChargeOwnerFee> mapState;

            @Override
            public void open(Configuration parameters) throws Exception {
                RuntimeContext runtimeContext = getRuntimeContext();
                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, ChargeOwnerFee>("map_stat", String.class, ChargeOwnerFee.class));
            }

            @Override
            public void processElement(ChargeOwnerFee chargeOwnerFee, Context context, Collector<String> collector) throws Exception {
                ChargeOwnerFee chargeOwnerFee1 = mapState.get(chargeOwnerFee.getPk());
                System.out.println(chargeOwnerFee1.toString());
                collector.collect(chargeOwnerFee1.getPk());
            }
        }).uid("es_charge_owner_fee").print();
        // 触发程序的提交运行
        env.execute();
    }
}
