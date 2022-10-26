//package com.sunac.test;
//
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.java.functions.KeySelector;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.datastream.*;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.ProcessFunction;
//import org.apache.flink.streaming.api.functions.co.RichCoMapFunction;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.baozi
// * @date:2022/9/1
// */
//public class TestConnect {
//    public static void main(String[] args) throws Exception {
//
//        Configuration configuration = new Configuration();
//        configuration.setInteger("rest.port", 8822);
//        configuration.setString("execution.savepoint.path", "file:///D://charge//test");
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
//        env.setParallelism(8);
//
//
//
//        // 数字字符流
//        DataStreamSource<String> teacher = env.socketTextStream("192.168.200.10", 7777);
//
//
//        SingleOutputStreamOperator<Teacher> teacherStream = teacher.map(new MapFunction<String, Teacher>() {
//            @Override
//            public Teacher map(String s) throws Exception {
//                String[] t = s.split(",");
//                return new Teacher(t[0], t[1], t[2]);
//            }
//        });
//        // 字母字符流
//        DataStreamSource<String> stu = env.socketTextStream("192.168.200.10", 8888);
//
//        SingleOutputStreamOperator<Student> student = stu.map(new MapFunction<String, Student>() {
//            @Override
//            public Student map(String s) throws Exception {
//                String[] t = s.split(",");
//                return new Student(t[0], t[1]);
//            }
//        });
//
//        DataStreamSource<String> Person = env.socketTextStream("192.168.200.10", 9999);
//        SingleOutputStreamOperator<Person> PersonStream = Person.map(new MapFunction<String, Person>() {
//            @Override
//            public Person map(String s) throws Exception {
//                String[] t = s.split(",");
//                return new Person(t[0], t[1], t[2]);
//            }
//        });
//
//        SingleOutputStreamOperator<Teacher> join1 = teacherStream.connect(student).keyBy(t -> t.getId(), s -> s.getId()).map(new RichCoMapFunction<Teacher, Student, Teacher>() {
//
//            MapState<String, Student> mapState;
//            MapState<String, Student> mapState2;
//            MapState<String, Student> mapState3;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, Student>("map_stat", String.class, Student.class));
//            }
//            @Override
//            public Teacher map1(Teacher teacher) throws Exception {
//                // insert 老师
//                Student s = mapState.get(teacher.getId());
//                if (s != null) {
//                    teacher.setName(s.getName());
//                }
//                return teacher;
//            }
//
//            @Override
//            public Teacher map2(Student student) throws Exception {
//                mapState.put(student.getId(), student);
//                System.out.println("===================================更新student===================================");
//                return new Teacher();
//            }
//        }).uid("student");//Caused by: java.lang.RuntimeException: Could not extract key from null
//
//
//        SingleOutputStreamOperator<Teacher> haha = join1.connect(PersonStream).keyBy(t -> (t.getId() + t.getName()), p -> (p.getId() + p.getName())).map(new RichCoMapFunction<Teacher, Person, Teacher>() {
//            MapState<String, Person> mapState;
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, Person>("map_stat2", String.class, Person.class));
//            }
//            @Override
//            public Teacher map1(Teacher teacher) throws Exception {
//                // insert 老师
//                Person s = mapState.get(teacher.getId() + teacher.getName());
//                teacher.setSex(s.getSex());//java.lang.NullPointerException
//                return teacher;
//            }
//            @Override
//            public Teacher map2(Person student) throws Exception {
//                mapState.put(student.getId() + student.getName(), student);
//                System.out.println("===================================更新student===================================");
//                return null;
//            }
//        }).uid("person");
//        haha.print();
//        env.execute();
//
//    }
//}
