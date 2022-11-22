package com.sunac;

import com.sunac.ow.owdomain.AllData;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;

public class OnLineConfig {

public static String EXECUTION_SAVEPOINT_PATH_KEY = "execution.savepoint.path";
public static String EXECUTION_SAVEPOINT_PATH_VALUE = "hdfs://HDPCluster:8020/checkpoint2";

public static String CheckpointStorage = "hdfs://HDPCluster:8020/owner";

public static Integer KafkaPartitionNum = 3;

    public static StreamExecutionEnvironment initOnLineEnv(Configuration configuration) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        env.setMaxParallelism(1280);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointInterval(30 * 1000);
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        checkpointConfig.setCheckpointStorage(OnLineConfig.CheckpointStorage);
        checkpointConfig.setTolerableCheckpointFailureNumber(30);
        checkpointConfig.setMinPauseBetweenCheckpoints(2000);
        checkpointConfig.setCheckpointTimeout(1000 * 60 * 60);
        checkpointConfig.setFailOnCheckpointingErrors(false);
        checkpointConfig.setMaxConcurrentCheckpoints(1);
        checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        checkpointConfig.setForceUnalignedCheckpoints(false);
        EmbeddedRocksDBStateBackend embe = new EmbeddedRocksDBStateBackend(true);
        env.setStateBackend(embe);
        RestartStrategies.RestartStrategyConfiguration restartStrategy = null;
        restartStrategy = RestartStrategies.fixedDelayRestart(5, 2000);
        env.setRestartStrategy(restartStrategy);
        return env;
    }

    public static SingleOutputStreamOperator<AllData> ConnectUtil(SingleOutputStreamOperator<AllData> s1,
                                                                  SingleOutputStreamOperator s2,
                                                                  KeySelector key1,
                                                                  KeySelector key2,
                                                                  KeyedCoProcessFunction keyedCoProcessFunction,
                                                                  String uid,
                                                                  Integer parallelism) {
        SingleOutputStreamOperator name = s1.connect(s2)
                .keyBy(key1, key2)
                .process(keyedCoProcessFunction)
                .setParallelism(parallelism)
                .uid(uid)
                .name(uid + " keyBy");
        return name;
    }
}
