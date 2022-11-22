package com.sunac.utils;

import com.sunac.Config;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import java.util.ArrayList;

import static com.sunac.OnLineConfig.KafkaPartitionNum;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.utils
 * @date:2022/9/5
 */
public class Util {


    public static void sendData2SlideStreamAllSQL(KeyedCoProcessFunction.Context context, ArrayList<String> sql) {
        context.output(new OutputTag<>(Config.SIDE_STREAM, TypeInformation.of(new TypeHint<ArrayList<String>>() {})), sql);
    }

    private static KafkaSourceBuilder<String> buildKafkaSource(String topic, String clientIdPrefix) {
        return KafkaSource.<String>builder()
                // 设置订阅的目标主题
                .setTopics(topic)
                // 设置kafka服务器地址
                .setBootstrapServers("172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667")
                // 起始消费位移的指定：
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                // 设置value数据的反序列化器://JSONDeserializationSchema
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setGroupId("Aixleft")
                .setProperty("max.poll.interval.ms", "100")
//                .setProperty("isolation.level", "read_committed")
                .setClientIdPrefix(clientIdPrefix)
                .setProperty("auto.offset.commit", "false");
    }
    public static DataStreamSource<String> getKafkaSource(StreamExecutionEnvironment env, String topic) {
        return env.fromSource(Util.buildKafkaSource(topic,topic + "-clientIdPrefix").
                build(), WatermarkStrategy.noWatermarks(), topic)
                .setParallelism(KafkaPartitionNum);
    }

}