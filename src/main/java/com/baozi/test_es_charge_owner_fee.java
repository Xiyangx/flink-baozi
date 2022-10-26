package com.baozi;

import com.baozi.domain.ChargeOwnerFee;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.contrib.streaming.state.PredefinedOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/9/2
 */
public class test_es_charge_owner_fee {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setInteger("rest.port", 8822);
        configuration.setString("execution.savepoint.path", "file:///D://charge//es_charge_owner_fee");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        env.setParallelism(6);
        env.setMaxParallelism(128);
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);


        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt//es_charge_owner_fee");
        embeddedRocksDBStateBackend.setPredefinedOptions(PredefinedOptions.SPINNING_DISK_OPTIMIZED_HIGH_MEM);


        env.enableCheckpointing(TimeUnit.MINUTES.toMillis(1), CheckpointingMode.EXACTLY_ONCE);  // 传入两个最基本ck参数：间隔时长，ck模式
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointStorage("file:///D://flink-baozi//src//main//java//baozi_ck");
        checkpointConfig.setAlignedCheckpointTimeout(Duration.ofMinutes(10000)); // 设置ck对齐的超时时长
        //checkpointConfig.setCheckpointIdOfIgnoredInFlightData(5); // 用于非对齐算法模式下，在job恢复时让各个算子自动抛弃掉ck-5中飞行数据
        checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);  // job cancel调时，保留最后一次ck数据
        checkpointConfig.setForceUnalignedCheckpoints(false);  // 是否强制使用  非对齐的checkpoint模式
        checkpointConfig.setMaxConcurrentCheckpoints(5); // 允许在系统中同时存在的飞行中（未完成的）的ck数
//        checkpointConfig.setMinPauseBetweenCheckpoints(2000); //  设置两次ck之间的最小时间间隔，用于防止checkpoint过多地占用算子的处理时间
        checkpointConfig.setCheckpointTimeout(TimeUnit.MINUTES.toMillis(10)); // 一个算子在一次checkpoint执行过程中的总耗费时长超时上限
//        checkpointConfig.setTolerableCheckpointFailureNumber(1); // 允许的checkpoint失败最大次数
        // 设置指定的重启策略
        RestartStrategies.RestartStrategyConfiguration restartStrategy = RestartStrategies.fixedDelayRestart(5, 2000);
        env.setRestartStrategy(restartStrategy);


        DataStreamSource<String> stream1 = env.socketTextStream("192.168.200.10", 8888);


        SingleOutputStreamOperator<ChargeOwnerFee> maps = stream1.map(new MapFunction<String, ChargeOwnerFee>() {
            @Override
            public ChargeOwnerFee map(String s) throws Exception {

                ChargeOwnerFee chargeOwnerFee = new ChargeOwnerFee();
                chargeOwnerFee.setPk(s);
                return chargeOwnerFee;
            }
        }).setParallelism(2);
        maps.keyBy(t -> t.getPk()).map(new RichMapFunction<ChargeOwnerFee, String>() {
            MapState<String, ChargeOwnerFee> mapState;

            @Override
            public void open(Configuration parameters) throws Exception {
                RuntimeContext runtimeContext = getRuntimeContext();
                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, ChargeOwnerFee>("map_stat", String.class, ChargeOwnerFee.class));
            }

            @Override
            public String map(ChargeOwnerFee s) throws Exception {
                ChargeOwnerFee chargeOwnerFee = mapState.get("b7cec59d82884b91e70d817c6001a5bc");
                if (null != chargeOwnerFee){
                    System.out.println("8888888888888888888888888888888888888" + chargeOwnerFee.toString());
                }
                Iterator<Map.Entry<String, ChargeOwnerFee>> entryIterator = mapState.iterator(); // 拿到mapstate的entry迭代器
                for (Iterator<Map.Entry<String, ChargeOwnerFee>> it = entryIterator; it.hasNext(); ) {
                    Map.Entry<String, ChargeOwnerFee> entry = it.next();
                    System.out.println(String.format("key--: %s, value--: %s", entry.getKey(), entry.getValue()));
                    break;
                }
                return "===========================================";
            }
        }).uid("es_charge_owner_fee");

        maps.keyBy(t -> t.getPk()).map(new RichMapFunction<ChargeOwnerFee, String>() {
            MapState<String, ChargeOwnerFee> mapState;

            @Override
            public void open(Configuration parameters) throws Exception {
                RuntimeContext runtimeContext = getRuntimeContext();
                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, ChargeOwnerFee>("map_stat", String.class, ChargeOwnerFee.class));
            }

            @Override
            public String map(ChargeOwnerFee s) throws Exception {
                ChargeOwnerFee chargeOwnerFee = mapState.get("b7cec59d82884b91e70d817c6001a5bc");
                if (null != chargeOwnerFee){
                    System.out.println("8888888888888888888888888888888888888" + chargeOwnerFee.toString());
                }
                Iterator<Map.Entry<String, ChargeOwnerFee>> entryIterator = mapState.iterator(); // 拿到mapstate的entry迭代器
                for (Iterator<Map.Entry<String, ChargeOwnerFee>> it = entryIterator; it.hasNext(); ) {
                    Map.Entry<String, ChargeOwnerFee> entry = it.next();
                    System.out.println(String.format("key--: %s, value--: %s", entry.getKey(), entry.getValue()));
                    break;
                }
                return "===========================================";
            }
        }).uid("es_charge_owner_fee1");
        env.execute();
    }
}
