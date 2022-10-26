//package com.sunac;
//
//import com.baozi.utils.JdbcUtil;
//import com.sunac.domain.*;
//import com.sunac.utils.Util;
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
//import java.sql.Connection;
//import java.sql.Statement;
//import java.util.Arrays;
//import java.util.Collection;
//
//
//public class es_charge_incoming_data_bill_2 {
//    public static void main(String[] args) throws Exception {
//        writeData();
//    }
//
//    private static void writeData() throws Exception {
//        // 混合环境创建
//        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(8);
//
////        Connection conn = JdbcUtil.getConnection();
////        Statement statement = conn.createStatement();
////
////        String s = "select fld_area_guid from maindb.es_charge_incoming_data_bill group by fld_area_guid ";
////        JdbcGenericParameterValuesProvider paramProviderIncoming = Util.getParameters(statement, s, "fld_area_guid");
////
////        String s2 = "SELECT fld_area_guid from  es_charge_bill GROUP BY fld_area_guid";
////        JdbcGenericParameterValuesProvider paramProviderBill = Util.getParameters(statement, s2, "fld_area_guid");
////
////        JdbcUtil.close(conn, statement);
////
////        MapOperator<Row, EsChargeIncomingDataBill> esChargeIncomingDataBillData = env.createInput(JdbcInputFormat.buildJdbcInputFormat()
////                .setParametersProvider(paramProviderIncoming)
////                .setDrivername("com.mysql.cj.jdbc.Driver")
////                .setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
////                .setUsername("report_readonly_user")
////                .setPassword("TIDB_report_user")
////                .setQuery(SqlConfig.ES_CHARGE_INCOMING_DATA_BILL)
////                .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO,
////                        BasicTypeInfo.STRING_TYPE_INFO))
////                .setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeIncomingDataBill>() {
////            @Override
////            public EsChargeIncomingDataBill map(Row row) throws Exception {
////                return new EsChargeIncomingDataBill(row.getFieldAs(0), row.getFieldAs(1));
////            }
////        });
////
////        MapOperator<Row, EsChargeBill> esChargeBillData = env.createInput(JdbcInputFormat.buildJdbcInputFormat()
////                .setParametersProvider(paramProviderBill).setDrivername("com.mysql.cj.jdbc.Driver")
////                .setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
////                .setUsername("report_readonly_user")
////                .setPassword("TIDB_report_user")
////                .setQuery(SqlConfig.ES_CHARGE__BILL)
////                .setRowTypeInfo(
////                        new RowTypeInfo(
////                                BasicTypeInfo.STRING_TYPE_INFO,
////                                BasicTypeInfo.INT_TYPE_INFO,
////                                BasicTypeInfo.STRING_TYPE_INFO,
////                                BasicTypeInfo.STRING_TYPE_INFO,
////                                BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeBill>() {
////            @Override
////            public EsChargeBill map(Row row) throws Exception {
////                return new EsChargeBill(row.getFieldAs(0), row.getFieldAs(1), row.getFieldAs(2), row.getFieldAs(3), row.getFieldAs(4));
////            }
////        });
////
////        MapOperator<Row, EsChargeBillType> esChargeBillTypeData = env.createInput(JdbcInputFormat.buildJdbcInputFormat()
////                .setDrivername("com.mysql.cj.jdbc.Driver").setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
////                .setUsername("report_readonly_user").setPassword("TIDB_report_user")
////                .setQuery(SqlConfig.ES_CHARGE__BILL_TYPE)
////                .setRowTypeInfo(new RowTypeInfo(
////                        BasicTypeInfo.STRING_TYPE_INFO,
////                        BasicTypeInfo.INT_TYPE_INFO,
////                        BasicTypeInfo.STRING_TYPE_INFO))
////                .setFetchSize(1000).finish())
////                .map(new MapFunction<Row, EsChargeBillType>() {
////            @Override
////            public EsChargeBillType map(Row row) throws Exception {
////                return new EsChargeBillType(row.getFieldAs(0), row.getFieldAs(1), row.getFieldAs(2));
////            }
////        });
//
//        Collection<EsChargeIncomingDataBill> data =
//                Arrays.asList(
//                        new EsChargeIncomingDataBill("1", "1"),
//                        new EsChargeIncomingDataBill("1", "2"),
//                        new EsChargeIncomingDataBill("2", "3"));
//        DataSet<EsChargeIncomingDataBill> esChargeIncomingDataBillData = env.fromCollection(data);
//
////        private String fld_guid;
////        private int fld_status;//发票状态
////        private String fld_bill_code;//发票代码
////        private String fld_type_guid;//票据类型GUID
////        private String fld_operate_date;
//
//        Collection<EsChargeBill> dataBill =
//                Arrays.asList(
//                        new EsChargeBill("1", 100,"发票代码1-1","uuid1","2021-01-01 00:00:00"),
//                        new EsChargeBill("2", 200,"发票代码1-2","uuid2","2021-01-02 00:00:00"),
//                        new EsChargeBill("3", 200,"发票代码2-1","uuid3","2021-01-02 00:00:00"));
//        DataSet<EsChargeBill> esChargeBillData = env.fromCollection(dataBill);
//
//
//
//        BootstrapTransformation<EsChargeIncomingDataBill> transformation = OperatorTransformation
//                .bootstrapWith(esChargeIncomingDataBillData)
//                .keyBy(k -> k.getFld_data_src_guid())
//                .transform(new KeyedStateBootstrapFunction<String, EsChargeIncomingDataBill>() {
//                    MapState<String, EsChargeIncomingDataBill> mapState;
//                    @Override
//                    public void open(Configuration parameters) throws Exception {
//                        RuntimeContext runtimeContext = getRuntimeContext();
//                        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingDataBill>("map_stat", String.class, EsChargeIncomingDataBill.class));
//                    }
//                    @Override
//                    public void processElement(EsChargeIncomingDataBill bill, Context context) throws Exception {
//                        String fld_data_src_guid = bill.getFld_data_src_guid();
//                        String fld_bill_guid = bill.getFld_bill_guid();
//                        String key = fld_data_src_guid + "#" + fld_bill_guid;
//                        System.out.println("======================================" + key);
//                        mapState.put(key, bill);
//                    }
//                });
//        BootstrapTransformation<EsChargeBill> billTransformation = OperatorTransformation
//                .bootstrapWith(esChargeBillData)
//                .keyBy(k -> "666")
//                .transform(new KeyedStateBootstrapFunction<String, EsChargeBill>() {
//                    MapState<String, EsChargeBill> mapState;
//                    @Override
//                    public void open(Configuration parameters) throws Exception {
//                        RuntimeContext runtimeContext = getRuntimeContext();
//                        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeBill>("map_stat", String.class, EsChargeBill.class));
//                    }
//                    @Override
//                    public void processElement(EsChargeBill bill, Context context) throws Exception {
//                        mapState.put(bill.getFld_guid(), bill);
//                    }
//                });
////        BootstrapTransformation<EsChargeBillType> billTpyeTransformation = OperatorTransformation
////                .bootstrapWith(esChargeBillTypeData)
////                .keyBy(k -> k.getFld_guid())
////                .transform(new KeyedStateBootstrapFunction<String, EsChargeBillType>() {
////                    MapState<String, EsChargeBillType> mapState;
////                    @Override
////                    public void open(Configuration parameters) throws Exception {
////                        RuntimeContext runtimeContext = getRuntimeContext();
////                        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeBillType>("map_stat", String.class, EsChargeBillType.class));
////                    }
////                    @Override
////                    public void processElement(EsChargeBillType bill, Context context) throws Exception {
////                        mapState.put(bill.getFld_guid(), bill);
////                    }
////                });
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
//        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
//        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt//bill");
//        Savepoint.create(embeddedRocksDBStateBackend, 128)
//                .withOperator("bill1", transformation)
//                .withOperator("bill2", billTransformation)
////                .withOperator("billTpyeTransformation", billTpyeTransformation)
//                .write("file:///D://charge//bill");
//        env.execute();
//    }
//}
