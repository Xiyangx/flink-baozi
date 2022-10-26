package com.baozi;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.BootstrapTransformation;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.state.api.OperatorTransformation;
import org.apache.flink.state.api.Savepoint;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/8/30
 */
public class TestStateInit {
    public static void main(String[] args) throws Exception {

        ExecutionEnvironment bEnv = ExecutionEnvironment.getExecutionEnvironment();
        bEnv.setParallelism(1);
        ExistingSavepoint savepoint = Savepoint.load(bEnv, "file:///H://flink-baozi//src//main//java//baozi_ck//b25e3fb81ac6906c1f6443773c26315c//chk-28", new EmbeddedRocksDBStateBackend(true));
        DataSource<String> dd = savepoint.readKeyedState("hh", new KeyedStateReaderFunction<String, String>() {
            private ValueState<String> state;

            @Override
            public void open(Configuration configuration) throws Exception {
                ValueStateDescriptor<String> stateDescriptor = new ValueStateDescriptor("state", Types.STRING);
                state = getRuntimeContext().getState(stateDescriptor);
            }

            @Override
            public void readKey(String s, Context context, Collector<String> collector) throws Exception {
                state.update(state.value() + s + "骚啦");
                collector.collect(state.value());
            }
        });
        BootstrapTransformation<String> transformation = OperatorTransformation.bootstrapWith(dd).keyBy(k -> "666").transform(new WriterFunction());
        Savepoint.create(new EmbeddedRocksDBStateBackend(true), 8)
                .withOperator("uid-test", transformation).write("file:///H://flink-baozi//src//main//java//baozi_ck_saola");
        bEnv.execute();
    }

    public static class WriterFunction extends KeyedStateBootstrapFunction<String, String> {
        ValueState<String> state;
        @Override
        public void open(Configuration parameters) throws Exception {

            ValueStateDescriptor<String> stateDescriptor = new ValueStateDescriptor<>("state", Types.STRING);
            state = getRuntimeContext().getState(stateDescriptor);
        }
        @Override
        public void processElement(String value, KeyedStateBootstrapFunction<String, String>.Context ctx) throws Exception {
            state.update(value);
        }
    }
}
