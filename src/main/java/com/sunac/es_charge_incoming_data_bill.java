//package com.sunac;
//
//import com.baozi.domain.ChargeOwnerFee;
//import com.baozi.utils.JdbcUtil;
//import com.sunac.domain.*;
//import com.sunac.utils.Util;
//import org.apache.flink.api.common.RuntimeExecutionMode;
//import org.apache.flink.api.common.functions.*;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.state.ValueState;
//import org.apache.flink.api.common.state.ValueStateDescriptor;
//import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
//import org.apache.flink.api.java.DataSet;
//import org.apache.flink.api.java.ExecutionEnvironment;
//import org.apache.flink.api.java.operators.*;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.api.java.typeutils.RowTypeInfo;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.connector.jdbc.JdbcInputFormat;
//import org.apache.flink.connector.jdbc.split.JdbcGenericParameterValuesProvider;
//import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
//import org.apache.flink.state.api.*;
//import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
//import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
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
//
//public class es_charge_incoming_data_bill {
//    public static void main(String[] args) throws Exception {
//        writeData();
//    }
//
//    private static void writeData() throws Exception {
//        // 混合环境创建
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(8);
////        StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
////        streamEnv.setParallelism(8);
//
//        // 按批计算模式去执行
////        streamEnv.setRuntimeMode(RuntimeExecutionMode.BATCH);
//
//
//        Connection conn = JdbcUtil.getConnection();
//        Statement statement = conn.createStatement();
//
//        String s = "select fld_area_guid from maindb.es_charge_incoming_data_bill group by fld_area_guid ";
//        JdbcGenericParameterValuesProvider paramProviderIncoming = Util.getParameters(statement, s, "fld_area_guid");
//
//        String s2 = "SELECT fld_area_guid from  es_charge_bill GROUP BY fld_area_guid";
//        JdbcGenericParameterValuesProvider paramProviderBill = Util.getParameters(statement, s2, "fld_area_guid");
//
//        JdbcUtil.close(conn, statement);
//
//        MapOperator<Row, EsChargeIncomingDataBill> EsChargeIncomingDataBillData = env.createInput(JdbcInputFormat.buildJdbcInputFormat()
//                .setParametersProvider(paramProviderIncoming)
//                .setDrivername("com.mysql.cj.jdbc.Driver")
//                .setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
//                .setUsername("report_readonly_user")
//                .setPassword("TIDB_report_user")
//                .setQuery(SqlConfig.ES_CHARGE_INCOMING_DATA_BILL)
//                .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO,
//                        BasicTypeInfo.STRING_TYPE_INFO))
//                .setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeIncomingDataBill>() {
//            @Override
//            public EsChargeIncomingDataBill map(Row row) throws Exception {
//                return new EsChargeIncomingDataBill(row.getFieldAs(0), row.getFieldAs(1));
//            }
//        });
//
//        MapOperator<Row, EsChargeBill> EsChargeBillData = env.createInput(JdbcInputFormat.buildJdbcInputFormat()
//                .setParametersProvider(paramProviderBill).setDrivername("com.mysql.cj.jdbc.Driver")
//                .setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
//                .setUsername("report_readonly_user")
//                .setPassword("TIDB_report_user")
//                .setQuery(SqlConfig.ES_CHARGE__BILL)
//                .setRowTypeInfo(
//                        new RowTypeInfo(
//                                BasicTypeInfo.STRING_TYPE_INFO,
//                                BasicTypeInfo.INT_TYPE_INFO,
//                                BasicTypeInfo.STRING_TYPE_INFO,
//                                BasicTypeInfo.STRING_TYPE_INFO,
//                                BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeBill>() {
//            @Override
//            public EsChargeBill map(Row row) throws Exception {
//                return new EsChargeBill(row.getFieldAs(0), row.getFieldAs(1), row.getFieldAs(2), row.getFieldAs(3), row.getFieldAs(4));
//            }
//        });
//
//
//        MapOperator<Row, EsChargeBillType> EsChargeBillTypeData = env.createInput(JdbcInputFormat.buildJdbcInputFormat().setDrivername("com.mysql.cj.jdbc.Driver").setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb").setUsername("report_readonly_user").setPassword("TIDB_report_user").setQuery(SqlConfig.ES_CHARGE__BILL_TYPE).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeBillType>() {
//            @Override
//            public EsChargeBillType map(Row row) throws Exception {
//                return new EsChargeBillType(row.getFieldAs(0), row.getFieldAs(1), row.getFieldAs(2));
//            }
//        });
//
//        JoinOperator.EquiJoin<EsChargeIncomingDataBill, EsChargeBill, EsChargeIncomingDataBill> pp1 = EsChargeIncomingDataBillData.join(EsChargeBillData).where(t -> t.getFld_data_src_guid()).equalTo(b -> b.getFld_guid()).with(new JoinFunction<EsChargeIncomingDataBill, EsChargeBill, EsChargeIncomingDataBill>() {
//            @Override
//            public EsChargeIncomingDataBill join(EsChargeIncomingDataBill esChargeIncomingDataBill, EsChargeBill esChargeBill) throws Exception {
//                return null;
//            }
//        });
//
//
///**
// JOIN (
// -- 取收据：该语句可能重复，需要去重（分组：fld_data_src_guid 排序：b.fld_operate_date desc）
// SELECT
// data_bill.fld_data_src_guid,
// b.fld_status,
// b.fld_bill_code,
// t.fld_name,
// t.fld_guid
// FROM
// ods_fd_es_charge_incoming_data_bill_af data_bill
// JOIN ods_fd_es_charge_bill_af b
//                                                                        ON data_bill.fld_bill_guid = b.fld_guid
// JOIN ods_fd_es_charge_bill_type_af t (and t.fld_category=0) ON         t.fld_guid = b.fld_type_guid
// ) db ON db.fld_data_src_guid = d.fld_guid
// *
// */
//        CoGroupOperator<EsChargeIncomingDataBill, EsChargeBill, EsChargeDB> baozi1 = EsChargeIncomingDataBillData
//                .coGroup(EsChargeBillData).where(t -> t.getFld_bill_guid()).equalTo(b -> b.getFld_guid())
//                .with(new RichCoGroupFunction<EsChargeIncomingDataBill, EsChargeBill, EsChargeDB>() {
//            MapState<String, EsChargeDB> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeDB>("map_stat", String.class, EsChargeDB.class));
//            }
//
//            @Override
//            public void coGroup(Iterable<EsChargeIncomingDataBill> d1, Iterable<EsChargeBill> d2, Collector<EsChargeDB> collector) throws Exception {
//                for (EsChargeIncomingDataBill bill : d1) {
//                    for (EsChargeBill bi : d2) {
//
//                        String fld_data_src_guid = bill.getFld_data_src_guid();
//                        EsChargeDB oldEsChargeDB = mapState.get(fld_data_src_guid);
//                        EsChargeDB newEsChargeDB = new EsChargeDB();
//
//                        newEsChargeDB.setFld_data_src_guid(bill.getFld_data_src_guid());
//                        newEsChargeDB.setFld_status(bi.getFld_status());
//                        newEsChargeDB.setFld_bill_code(bi.getFld_bill_code());
//                        newEsChargeDB.setFld_type_guid(bi.getFld_type_guid());
//                        newEsChargeDB.setFld_operate_date(bi.getFld_operate_date());
//
//                        if (null != oldEsChargeDB) {
//                            // 比较两个obj，将大的放到map里面
//                            mapState.put(fld_data_src_guid, newEsChargeDB.compareTo(oldEsChargeDB) > 0 ? newEsChargeDB : oldEsChargeDB);
//                            collector.collect(newEsChargeDB.compareTo(oldEsChargeDB) > 0 ? newEsChargeDB : oldEsChargeDB);
//                        } else {
//                            mapState.put(fld_data_src_guid, newEsChargeDB);
//                            collector.collect(newEsChargeDB);
//                        }
//
//                    }
//                }
//            }
//        }).name("baozi1");
//
//        CoGroupOperator<EsChargeDB, EsChargeBillType, EsChargeDB> baozi2 = baozi1.coGroup(EsChargeBillTypeData).where(t -> t.getFld_type_guid()).equalTo(t2 -> t2.getFld_guid()).with(new RichCoGroupFunction<EsChargeDB, EsChargeBillType, EsChargeDB>() {
//            //                    ValueState<EsChargeDB> valueState;
////                    @Override
////                    public void open(Configuration parameters) throws Exception {
////                        RuntimeContext runtimeContext = getRuntimeContext();
////                        valueState = runtimeContext.getState(new ValueStateDescriptor<EsChargeDB>("vstate",EsChargeDB.class));
////                    }
//            @Override
//            public void coGroup(Iterable<EsChargeDB> d1, Iterable<EsChargeBillType> d2, Collector<EsChargeDB> collector) throws Exception {
//                for (EsChargeDB data1 : d1) {
//                    for (EsChargeBillType data2 : d2) {
//                        data1.setFld_name(data2.getFld_name());
//                        data1.setFld_guid(data2.getFld_guid());
//                        collector.collect(data1);
//                    }
//                }
//            }
//        });
//        baozi2.print();
//
///*      db.fld_name as fld_bill_type_name	varchar(50)	收据类型名称
//        db.fld_status as fld_bill_status	int	收据状态：0已开票 1已换票 2已作废 3已红冲 4预开待收费 5待开票 6中间状态 ,99 关联后null值替换为99
//        db.fld_guid as fld_bill_type	char(36)	收据类型（变更源字段）
//
//        fld_data_src_guid
//        db.fld_status AS fld_bill_status,
//        db.fld_bill_code,
//        db.fld_name AS fld_bill_type_name,*/
//
//
//        // .returns(new TypeHint<Tuple2<String, Integer>>() {});   // 通过 TypeHint 传达返回数据类型
//        // .returns(TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {}));  // 更通用的，是传入TypeInformation,上面的TypeHint也是封装了TypeInformation
//        // .returns(Types.TUPLE(Types.STRING, Types.INT));  // 利用工具类Types的各种静态方法，来生成TypeInformation
//
//        OperatorTransformation.bootstrapWith(baozi2).keyBy(k -> k.getPk()).transform(new KeyedStateBootstrapFunction<String, ChargeOwnerFee>() {
//
//        BootstrapTransformation<EsChargeDB> transform = OperatorTransformation.bootstrapWith(baozi2).keyBy(k -> k.getFld_data_src_guid()).transform(new KeyedStateBootstrapFunction<String, EsChargeDB>() {
//            @Override
//            public void processElement(EsChargeDB esChargeDB, Context context) throws Exception {
//
//            }
//        });
//
////
////        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
////        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt//es_charge_owner_fee");
////        Savepoint.create(embeddedRocksDBStateBackend, 128).withOperator("es_charge_owner_fee", transform).write("file:///D://charge//es_charge_owner_fee");
//        env.execute();
//    }
//}
