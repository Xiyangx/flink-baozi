//package com.sunac.test;
//
//import com.baozi.utils.JdbcUtil;
//import com.sunac.domain.SqlConfig;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.common.typeinfo.Types;
//import org.apache.flink.api.java.DataSet;
//import org.apache.flink.api.java.ExecutionEnvironment;
//import org.apache.flink.api.java.operators.DataSource;
//import org.apache.flink.api.java.operators.MapOperator;
//import org.apache.flink.api.java.typeutils.RowTypeInfo;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.connector.jdbc.JdbcInputFormat;
//import org.apache.flink.connector.jdbc.split.JdbcGenericParameterValuesProvider;
//import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
//import org.apache.flink.state.api.BootstrapTransformation;
//import org.apache.flink.state.api.ExistingSavepoint;
//import org.apache.flink.state.api.OperatorTransformation;
//import org.apache.flink.state.api.Savepoint;
//import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
//import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
//import org.apache.flink.types.Row;
//import org.apache.flink.util.Collector;
//
//import java.io.Serializable;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.*;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.baozi
// * @date:2022/8/29
// */
//public class Job {
//    public static void main(String[] args) throws Exception {
//        writeData();
//    }
//
//    private static void writeData() throws Exception {
//        // 混合环境创建
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(8);
//        final Collection<Student> data = Arrays.asList(new Student("1", "baozi1"), new Student("2", "baozi2"), new Student("3", "baozi3"));
//        DataSet<Student> student = env.fromCollection(data);
//
//        final Collection<Person> data2 = Arrays.asList(new Person("1", "baozi1", "男"), new Person("2", "baozi2", "男"), new Person("3", "baozi3", "男"));
//        DataSet<Person> person = env.fromCollection(data2);
//
//        BootstrapTransformation<Student> transformation = OperatorTransformation.bootstrapWith(student).keyBy(k -> k.getId()).transform(new KeyedStateBootstrapFunction<String, Student>() {
//            MapState<String, Student> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, Student>("map_stat", String.class, Student.class));
//            }
//
//            @Override
//            public void processElement(Student student, Context context) throws Exception {
//                mapState.put(student.getId(), student);
//            }
//        });
//
//        BootstrapTransformation<Person> transformation2 = OperatorTransformation.bootstrapWith(person).keyBy(k -> (k.getId() + k.getName())).transform(new KeyedStateBootstrapFunction<String, Person>() {
//            MapState<String, Person> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, Person>("map_stat2", String.class, Person.class));
//            }
//
//            @Override
//            public void processElement(Person p, Context context) throws Exception {
//                mapState.put((p.getId() + p.getName()), p);
//            }
//        });
//
//
//        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
//        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt//test");
//        Savepoint.create(embeddedRocksDBStateBackend, 128)
//                .withOperator("student", transformation)
//                .withOperator("person", transformation2)
//                .write("file:///D://charge//test");
//        env.execute();
//    }
//}
