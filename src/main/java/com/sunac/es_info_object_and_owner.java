//package com.sunac;
//
//import com.baozi.utils.JdbcUtil;
//import com.sunac.domain.EsInfoObjectAndOwnerDomain;
//import com.sunac.domain.SqlConfig;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
//import org.apache.flink.api.java.DataSet;
//import org.apache.flink.api.java.ExecutionEnvironment;
//import org.apache.flink.api.java.operators.MapOperator;
//import org.apache.flink.api.java.typeutils.RowTypeInfo;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.connector.jdbc.JdbcInputFormat;
//import org.apache.flink.connector.jdbc.split.JdbcGenericParameterValuesProvider;
//import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
//import org.apache.flink.state.api.BootstrapTransformation;
//import org.apache.flink.state.api.OperatorTransformation;
//import org.apache.flink.state.api.Savepoint;
//import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
//import org.apache.flink.types.Row;
//
//import java.io.Serializable;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac.domain
// * @date:2022/9/2
// */
//
////ods_fd_es_info_object_and_owner_af ao
//// 人房关系表  ---3549573条
////        -- 该语句可能重复，需要去重（
////        分组：
////        fld_area_guid、
////        fld_object_guid、
////        fld_owner_guid
//
////        排序：
//
////        fld_is_current desc,是否当前(0否 1是)
////        fld_is_charge desc, 是否计费(0否 1是)
////        fld_status desc    数据状态(1可用 2不可用 6迁出7作废)
//
////
////        ）
////        ON  ao.fld_area_guid  = d.fld_area_guid  管理区GUID
//
////        AND ao.fld_object_guid = d.fld_object_guid 资源GUID
//
////        AND ao.fld_owner_guid = d.fld_owner_guid 客户GUID
////          fld_guid
////    71	ao.fld_room_type	varchar(10)	房间类型(1:房间 2:车位 3:公区)
////    52	ao.fld_is_owner	    int	居住关系(0业主 1租户 2地产公司 -1其它,99     关联后null值替换为99)
//
//public class es_info_object_and_owner {
//    public static void main(String[] args) throws Exception {
//
//        Connection conn = JdbcUtil.getConnection();
//        String s = "select fld_area_guid from es_info_object_and_owner group by fld_area_guid";
//        Statement statement = conn.createStatement();
//        ResultSet res = statement.executeQuery(s);
//        ArrayList<String> set = new ArrayList<>();
//        while (res.next()) {
//            set.add(res.getString("fld_area_guid"));
//        }
//        Serializable[][] queryParameters = new String[set.size()][1];
//        for (int i = 0; i < set.size(); i++) {
//            queryParameters[i] = new String[]{set.get(i)};
//        }
//        JdbcUtil.close(conn, statement);
//
//
//        JdbcGenericParameterValuesProvider paramProvider = new JdbcGenericParameterValuesProvider(queryParameters);
//        // 混合环境创建
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(8);
//
//
//        JdbcInputFormat finish = JdbcInputFormat.buildJdbcInputFormat().setDrivername("com.mysql.cj.jdbc.Driver").setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb").setUsername("report_readonly_user").setPassword("TIDB_report_user").setQuery(SqlConfig.ES_INFO_OBJECT_AND_OWNER).setParametersProvider(paramProvider).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish();
//        DataSet<Row> dbData = env.createInput(finish);
//        MapOperator<Row, EsInfoObjectAndOwnerDomain> mapData = dbData.map(new MapFunction<Row, EsInfoObjectAndOwnerDomain>() {
//            @Override
//            public EsInfoObjectAndOwnerDomain map(Row row) {
//                return new EsInfoObjectAndOwnerDomain(row.getFieldAs(0),
//                        row.getFieldAs(1), row.getFieldAs(2),
//                        row.getFieldAs(3), row.getFieldAs(4),
//                        row.getFieldAs(5), row.getFieldAs(6),
//                        row.getFieldAs(7), row.getFieldAs(8),
//                        row.getFieldAs(9)
//                );
//            }
//        });
//        // .returns(new TypeHint<Tuple2<String, Integer>>() {});   // 通过 TypeHint 传达返回数据类型
//        // .returns(TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {}));  // 更通用的，是传入TypeInformation,上面的TypeHint也是封装了TypeInformation
//        // .returns(Types.TUPLE(Types.STRING, Types.INT));  // 利用工具类Types的各种静态方法，来生成TypeInformation
//
//        BootstrapTransformation<EsInfoObjectAndOwnerDomain> transformation = OperatorTransformation.bootstrapWith(mapData).keyBy(k -> k.getPk()).transform(new KeyedStateBootstrapFunction<String, EsInfoObjectAndOwnerDomain>() {
//            MapState<String, EsInfoObjectAndOwnerDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsInfoObjectAndOwnerDomain>("map_stat", String.class, EsInfoObjectAndOwnerDomain.class));
//            }
//
//            @Override
//            public void processElement(EsInfoObjectAndOwnerDomain newEsInfoObjectAndOwnerDomain, KeyedStateBootstrapFunction.Context context) throws Exception {
//                String pk = newEsInfoObjectAndOwnerDomain.getPk();
//                EsInfoObjectAndOwnerDomain old = mapState.get(pk);
//                if (null == old) {
//                    mapState.put(newEsInfoObjectAndOwnerDomain.getPk(), newEsInfoObjectAndOwnerDomain);
//                } else {
//                        //        fld_is_current desc,是否当前(0否 1是)
//                        //        fld_is_charge desc, 是否计费(0否 1是)
//                        //        fld_status desc    数据状态(1可用 2不可用 6迁出7作废)
//                    /**这里写排序逻辑**/
//                }
//
//
//            }
//        });
//        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
//        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt");
//        Savepoint.create(embeddedRocksDBStateBackend, 128).withOperator("es_info_object_and_owner", transformation).write("file:///D://charge");
//        env.execute();
//    }
//}
