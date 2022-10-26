package com.baozi;

import org.apache.commons.codec.digest.DigestUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.baozi.domain.ChargeOwnerFee;
import com.baozi.utils.JdbcUtil;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import org.apache.flink.contrib.streaming.state.ConfigurableRocksDBOptionsFactory;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.contrib.streaming.state.PredefinedOptions;
import org.apache.flink.contrib.streaming.state.RocksDBOptionsFactory;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.hadoop.yarn.util.Times;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.alibaba.druid.util.JdbcUtils.printResultSet;

/**
 * sql.SQLException: GC overhead limit exceeded
 * https://www.jianshu.com/p/88e6d066bf6a
 * 15:46:58.042 INFO  org.apache.flink.runtime.jobmaster.JobMaster
 * {} - Using job/cluster config to configure application-defined state backend: EmbeddedRocksDBStateBackend{, localRocksDbDirectories=null, enableIncrementalCheckpointing=TRUE, numberOfTransferThreads=-1, writeBatchSize=-1}
 *
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/8/31
 */
public class es_charge_owner_fee {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
//        conf.setString("execution.savepoint.path", "hdfs://172.17.44.25:8020/tmp/data/es_charge_owner_fee");
        Configuration configuration = new Configuration();
        configuration.setInteger("rest.port", 8081);
        configuration.setString("taskmanager.memory.managed.size", "2048m");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        env.setParallelism(6);

        env.setMaxParallelism(128);

        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://charge");
        embeddedRocksDBStateBackend.setPredefinedOptions(PredefinedOptions.SPINNING_DISK_OPTIMIZED_HIGH_MEM);


        env.setStateBackend(embeddedRocksDBStateBackend);
        /* *
         * checkpoint相关配置
         */
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
        // 数据处理：
        DataStreamSource<ChargeOwnerFee> chargeOwnerFeeDataStreamSource = env.addSource(new MyRichParallelSourceFunction()).setParallelism(2);

        chargeOwnerFeeDataStreamSource.keyBy(t -> t.getPk()).map(new RichMapFunction<ChargeOwnerFee, String>() {
            MapState<String, ChargeOwnerFee> mapState;

            @Override
            public void open(Configuration parameters) throws Exception {
                RuntimeContext runtimeContext = getRuntimeContext();
                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, ChargeOwnerFee>("map_stat", String.class, ChargeOwnerFee.class));
            }

            @Override
            public String map(ChargeOwnerFee s) throws Exception {
                mapState.put(s.getPk(), s);
                return s.getPk();
            }
        }).uid("map-es_charge_owner_fee").print();
        env.execute();
    }

    public static class MyOptionsFactory implements ConfigurableRocksDBOptionsFactory {
        public final ConfigOption<Integer> BLOCK_RESTART_INTERVAL = ConfigOptions.key("my.custom.rocksdb.block.restart-interval").intType().defaultValue(16).withDescription(" Block restart interval. RocksDB has default block restart interval as 16. ");

        private int blockRestartInterval = BLOCK_RESTART_INTERVAL.defaultValue();

        @Override
        public DBOptions createDBOptions(DBOptions currentOptions, Collection<AutoCloseable> handlesToClose) {
            return currentOptions.setIncreaseParallelism(4).setUseFsync(false);
        }

        @Override
        public ColumnFamilyOptions createColumnOptions(ColumnFamilyOptions currentOptions, Collection<AutoCloseable> handlesToClose) {
            return currentOptions.setTableFormatConfig(new BlockBasedTableConfig().setBlockRestartInterval(blockRestartInterval));
        }

        @Override
        public RocksDBOptionsFactory configure(ReadableConfig configuration) {
            this.blockRestartInterval = configuration.get(BLOCK_RESTART_INTERVAL);
            return this;
        }
    }
}
