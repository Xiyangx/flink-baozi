package com.baozi;

import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.io.IOException;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/9/7
 */
public class TestKafka {
    public static class ConsumerDeserializationSchema<T> implements DeserializationSchema<T> {
        private Class<T> clazz;

        public ConsumerDeserializationSchema(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T deserialize(byte[] bytes) throws IOException {
            //确保 new String(bytes) 是json 格式，如果不是，请自行解析
//            return JSON.parseObject(new String(bytes), clazz);
            return (T) new String(bytes,"UTF-8");

        }

        @Override
        public boolean isEndOfStream(T t) {
            return false;
        }

        @Override
        public TypeInformation<T> getProducedType() {
            return TypeExtractor.getForClass(clazz);
        }
    }

    public static void main(String[] args) throws Exception {
        /**
         * 引入扩展包 ：  flink-connector-kafka
         * 从kafka中读取数据得到数据流
         */
        Configuration conf = new Configuration();
        conf.setInteger("rest.port", 8086);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        KafkaSource kafkaSource = KafkaSource.<String>builder()
                // 设置订阅的目标主题
                .setTopics("testdb")
                // 设置消费者组id
                .setGroupId("g1")
                // 设置kafka服务器地址
                .setBootstrapServers("172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667")
                // 起始消费位移的指定：
                //    OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST) 消费起始位移选择之前所提交的偏移量（如果没有，则重置为LATEST）
                //    OffsetsInitializer.earliest()  消费起始位移直接选择为 “最早”
                //    OffsetsInitializer.latest()  消费起始位移直接选择为 “最新”
                //    OffsetsInitializer.offsets(Map<TopicPartition,Long>)  消费起始位移选择为：方法所传入的每个分区和对应的起始偏移量
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                // 设置value数据的反序列化器://JSONDeserializationSchema
//                .setDeserializer()
                .setValueOnlyDeserializer(new SimpleStringSchema())
                // 开启kafka底层消费者的自动位移提交机制
                //    它会把最新的消费位移提交到kafka的consumer_offsets中
                //    就算把自动位移提交机制开启，KafkaSource依然不依赖自动位移提交机制
                //    （宕机重启时，优先从flink自己的状态中去获取偏移量<更可靠>）
                .setProperty("auto.offset.commit", "true")
//                .setProperty("deserializer.encoding", "utf-8")
                // 把本source算子设置成  BOUNDED属性（有界流）
                //     将来本source去读取数据的时候，读到指定的位置，就停止读取并退出
                //     常用于补数或者重跑某一段历史数据
                // .setBounded(OffsetsInitializer.committedOffsets())
                // 把本source算子设置成  UNBOUNDED属性（无界流）
                //     但是并不会一直读数据，而是达到指定位置就停止读取，但程序不退出
                //     主要应用场景：需要从kafka中读取某一段固定长度的数据，然后拿着这段数据去跟另外一个真正的无界流联合处理
                //.setUnbounded(OffsetsInitializer.latest())
                .build();
        // env.addSource();  //  接收的是  SourceFunction接口的 实现类
        DataStreamSource<String> streamSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kfk-source");//  接收的是 Source 接口的实现类
        streamSource.print();
        env.execute();
    }
}
