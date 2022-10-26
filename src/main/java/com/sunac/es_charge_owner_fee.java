//package com.sunac;
//
//import com.baozi.utils.JdbcUtil;
//import com.sunac.domain.EsChargeOwnerFee;
//import com.sunac.domain.SqlConfig;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
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
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.Map;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.baozi
// * @date:2022/8/29
// */
//public class es_charge_owner_fee {
//    public static void main(String[] args) throws Exception {
//        writeData();
//    }
//
//    private static void readData() throws Exception {
//        ExecutionEnvironment bEnv = ExecutionEnvironment.getExecutionEnvironment();
//
//        ExistingSavepoint savepoint = Savepoint.load(bEnv, "file:///H://flink-baozi//src//main//java//baozi2", new EmbeddedRocksDBStateBackend(true));
//        DataSource<EsChargeOwnerFee> mapState = savepoint.readKeyedState("uid-test", new KeyedStateReaderFunction<String, EsChargeOwnerFee>() {
//            MapState<String, EsChargeOwnerFee> mapState;
//
//            @Override
//            public void open(Configuration configuration) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeOwnerFee>("map_stat", String.class, EsChargeOwnerFee.class));
//                Iterator<Map.Entry<String, EsChargeOwnerFee>> entryIterator = mapState.iterator(); // 拿到mapstate的entry迭代器
//                for (Iterator<Map.Entry<String, EsChargeOwnerFee>> it = entryIterator; it.hasNext(); ) {
//                    Map.Entry<String, EsChargeOwnerFee> entry = it.next();
//                    System.out.println(String.format("key--: %s, value--: %s", entry.getKey(), entry.getValue()));
//                }
//            }
//
//            @Override
//            public void readKey(String key, Context context, Collector<EsChargeOwnerFee> out) throws Exception {
//                out.collect(mapState.get(key));
//            }
//        });
//        mapState.print();
//    }
//
//    private static void writeData() throws Exception {
//        // 混合环境创建
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(8);
//
//        Connection conn = JdbcUtil.getConnection();
//        String s = "select fld_allot_date from maindb.es_charge_owner_fee group by fld_allot_date ";
//        Statement statement = conn.createStatement();
//        ResultSet res = statement.executeQuery(s);
//        ArrayList<String> set = new ArrayList<>();
//        while (res.next()) {
//           set.add(res.getString("fld_allot_date"));
//        }
//        Serializable[][] queryParameters = new String[set.size()][1];
//        for (int i = 0; i < set.size(); i++) {
//            queryParameters[i] = new String[]{set.get(i)};
//        }
//        JdbcUtil.close(conn, statement);
//
//        JdbcGenericParameterValuesProvider paramProvider = new JdbcGenericParameterValuesProvider(queryParameters);
//
//        DataSet<Row> dbData = env.createInput(JdbcInputFormat.buildJdbcInputFormat()
//                .setParametersProvider(paramProvider)
//                .setDrivername("com.mysql.cj.jdbc.Driver")
//                .setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
//                .setUsername("report_readonly_user").setPassword("TIDB_report_user")
//                .setQuery(SqlConfig.ES_CHARGE_OWNER_FEE)
//                .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO,
//                        BasicTypeInfo.BIG_DEC_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO,
//                        BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish());
//        MapOperator<Row, EsChargeOwnerFee> mapData = dbData.map(new MapFunction<Row, EsChargeOwnerFee>() {
//            @Override
//            public EsChargeOwnerFee map(Row row) throws Exception {
//                System.out.println(row.toString());
//                return new EsChargeOwnerFee(row.getFieldAs(0),
//                        row.getFieldAs(1),
//                        row.getFieldAs(2),
//                        row.getFieldAs(3),
//                        row.getFieldAs(4),
//                        row.getFieldAs(5),
//                        row.getFieldAs(6),
//                        row.getFieldAs(7),
//                        row.getFieldAs(8));
//            }
//        });
//        // .returns(new TypeHint<Tuple2<String, Integer>>() {});   // 通过 TypeHint 传达返回数据类型
//        // .returns(TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {}));  // 更通用的，是传入TypeInformation,上面的TypeHint也是封装了TypeInformation
//        // .returns(Types.TUPLE(Types.STRING, Types.INT));  // 利用工具类Types的各种静态方法，来生成TypeInformation
//
//        BootstrapTransformation<EsChargeOwnerFee> transformation = OperatorTransformation
//                .bootstrapWith(mapData)
//                .keyBy(k -> k.getPk())
//                .transform(new KeyedStateBootstrapFunction<String, EsChargeOwnerFee>() {
//            MapState<String, EsChargeOwnerFee> mapState;
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeOwnerFee>("map_stat", String.class, EsChargeOwnerFee.class));
//            }
//            @Override
//            public void processElement(EsChargeOwnerFee EsChargeOwnerFee, Context context) throws Exception {
//                mapState.put(EsChargeOwnerFee.getPk(), EsChargeOwnerFee);
//            }
//        });
//        BootstrapTransformation<EsChargeOwnerFee> transformation1 = OperatorTransformation
//                .bootstrapWith(mapData)
//                .keyBy(k -> k.getPk())
//                .transform(new KeyedStateBootstrapFunction<String, EsChargeOwnerFee>() {
//                    MapState<String, EsChargeOwnerFee> mapState;
//                    @Override
//                    public void open(Configuration parameters) throws Exception {
//                        RuntimeContext runtimeContext = getRuntimeContext();
//                        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeOwnerFee>("map_stat", String.class, EsChargeOwnerFee.class));
//                    }
//                    @Override
//                    public void processElement(EsChargeOwnerFee EsChargeOwnerFee, Context context) throws Exception {
//                        mapState.put(EsChargeOwnerFee.getPk(), EsChargeOwnerFee);
//                    }
//                });
//        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
//        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt//es_charge_owner_fee");
//        Savepoint.create(embeddedRocksDBStateBackend, 128)
//                .withOperator("es_charge_owner_fee", transformation)
//                .withOperator("es_charge_owner_fee1",transformation1)
//                .write("file:///D://charge//es_charge_owner_fee");
//        env.execute();
//    }
//}
