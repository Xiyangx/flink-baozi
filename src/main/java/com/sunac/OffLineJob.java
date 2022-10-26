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
//import org.apache.flink.api.java.ExecutionEnvironment;
//import org.apache.flink.api.java.operators.MapOperator;
//import org.apache.flink.api.java.typeutils.RowTypeInfo;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.connector.jdbc.JdbcInputFormat;
//import org.apache.flink.connector.jdbc.split.JdbcGenericParameterValuesProvider;
//import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
//import org.apache.flink.state.api.BootstrapTransformation;
//import org.apache.flink.state.api.NewSavepoint;
//import org.apache.flink.state.api.OperatorTransformation;
//import org.apache.flink.state.api.Savepoint;
//import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
//import org.apache.flink.types.Row;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac
// * @date:2022/9/6
// */
//public class OffLineJob {
//    static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//    static JdbcInputFormat.JdbcInputFormatBuilder jdbcInputFormatBuilder = JdbcInputFormat.buildJdbcInputFormat().setDrivername("com.mysql.cj.jdbc.Driver").setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb").setUsername("report_readonly_user").setPassword("TIDB_report_user");
//    static EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
//    static NewSavepoint newSavepoint = null;
//
//    public static void main(String[] args) throws Exception {
//        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt");
//        newSavepoint = Savepoint.create(embeddedRocksDBStateBackend, 128);
//        env.setParallelism(8);
//        Connection conn = JdbcUtil.getConnection();
//        Statement statement = conn.createStatement();
//        // 1,JOIN ods_fd_es_charge_incoming_fee_af f ON f.fld_guid = d.fld_incoming_fee_guid:
//        //        fld_busi_type	来源 -1导入 0前台
//        //        fld_remark	收费备注
////        main1(conn, statement);
//        // 2,ods_fd_es_info_area_info_af a ON a.fld_guid = d.fld_area_guid
//
////        main2();
//
//        // 3,o.fld_guid = d.fld_object_guid
////        main3(conn, statement);
//        // 4,w.fld_guid = d.fld_owner_guid
////        main4(conn, statement);
//
//        // 5,es_charge_incoming_back退款总表
////        main5(conn, statement);
//
//        // 6,es_charge_voucher_mast_refund:凭证-主表-退款凭证
//        main6();
//        JdbcUtil.close(conn, statement);
//
//    }
//
//
//    private static void main2() throws Exception {
//        MapOperator<Row, EsInfoAreaInfo> mapData = env.
//                createInput(jdbcInputFormatBuilder.setQuery(SqlConfig.ES_INFO_AREA_INFO).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsInfoAreaInfo>() {
//            EsInfoAreaInfo esInfoAreaInfo = new EsInfoAreaInfo();
//
//            @Override
//            public EsInfoAreaInfo map(Row row) throws Exception {
//                esInfoAreaInfo.setFld_guid(row.getFieldAs(0));
//                esInfoAreaInfo.setFld_dq(row.getFieldAs(1));
//                esInfoAreaInfo.setFld_confirm_date(row.getFieldAs(2));
//                esInfoAreaInfo.setFld_company(row.getFieldAs(3));
//                esInfoAreaInfo.setFld_xm(row.getFieldAs(4));
//                esInfoAreaInfo.setFld_ywdy(row.getFieldAs(5));
//                esInfoAreaInfo.setFld_yt(row.getFieldAs(6));
//                esInfoAreaInfo.setFld_name(row.getFieldAs(7));
//                return esInfoAreaInfo;
//            }
//        });
//
//        BootstrapTransformation<EsInfoAreaInfo> transformation = OperatorTransformation.bootstrapWith(mapData).keyBy(k -> k.getFld_guid()).transform(new KeyedStateBootstrapFunction<String, EsInfoAreaInfo>() {
//            MapState<String, EsInfoAreaInfo> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsInfoAreaInfo>("map_stat", String.class, EsInfoAreaInfo.class));
//            }
//
//            @Override
//            public void processElement(EsInfoAreaInfo EsChargeIncomingFee, KeyedStateBootstrapFunction.Context context) throws Exception {
//                mapState.put(EsChargeIncomingFee.getFld_guid(), EsChargeIncomingFee);
//            }
//        });
//        newSavepoint.withOperator("es_info_area_info", transformation).write("file:///D://charge//es_info_area_info");
//        env.execute();
//    }
//
//    private static void main1(Connection conn, Statement statement) throws Exception {
//        String s = "select fld_area_guid from maindb.es_charge_incoming_fee group by fld_area_guid";
//        JdbcGenericParameterValuesProvider paramProviderIncoming = Util.getParameters(statement, s, "fld_area_guid");
//        JdbcUtil.close(conn, statement);
//        MapOperator<Row, EsChargeIncomingFee> mapData = env.
//                createInput(jdbcInputFormatBuilder.setParametersProvider(paramProviderIncoming).setQuery(SqlConfig.ES_CHARGE_INCOMING_FEE1).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeIncomingFee>() {
//            EsChargeIncomingFee esChargeIncomingFee = new EsChargeIncomingFee();
//
//            @Override
//            public EsChargeIncomingFee map(Row row) throws Exception {
//
//                esChargeIncomingFee.setFld_guid(row.getFieldAs(0));
//                esChargeIncomingFee.setFld_busi_type(row.getFieldAs(1));
//                esChargeIncomingFee.setFld_remark(row.getFieldAs(2));
//                return esChargeIncomingFee;
//            }
//        });
//
//        BootstrapTransformation<EsChargeIncomingFee> transformation = OperatorTransformation.bootstrapWith(mapData).keyBy(k -> k.getFld_guid()).transform(new KeyedStateBootstrapFunction<String, EsChargeIncomingFee>() {
//            MapState<String, EsChargeIncomingFee> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingFee>("map_stat", String.class, EsChargeIncomingFee.class));
//            }
//
//            @Override
//            public void processElement(EsChargeIncomingFee EsChargeIncomingFee, KeyedStateBootstrapFunction.Context context) throws Exception {
//                mapState.put(EsChargeIncomingFee.getFld_guid(), EsChargeIncomingFee);
//            }
//        });
//        newSavepoint.withOperator("es_charge_owner_fee", transformation).withOperator("es_charge_owner_fee1", transformation).write("file:///D://charge//es_charge_owner_fee_1");
//        env.execute();
//    }
//
//    private static void main3(Connection conn, Statement statement) throws Exception {
//        String s = "select fld_area_guid from maindb.es_info_object group by fld_area_guid";
//        JdbcGenericParameterValuesProvider paramProviderIncoming = Util.getParameters(statement, s, "fld_area_guid");
//        JdbcUtil.close(conn, statement);
//        MapOperator<Row, EsInfoObject> mapData = env.
//                createInput(jdbcInputFormatBuilder.setParametersProvider(paramProviderIncoming).setQuery(SqlConfig.ES_INFO_OBJECT).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.BIG_DEC_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsInfoObject>() {
//            EsInfoObject esInfoObject = new EsInfoObject();
//
//            @Override
//            public EsInfoObject map(Row row) throws Exception {
//                esInfoObject.setFld_guid(row.getFieldAs(0));
//                esInfoObject.setFld_object_name(row.getFieldAs(1));
//                esInfoObject.setFld_owner_fee_date(row.getFieldAs(2));
//                esInfoObject.setFld_building(row.getFieldAs(3));
//                esInfoObject.setFld_cell(row.getFieldAs(4));
//                esInfoObject.setFld_batch(row.getFieldAs(5));
//                esInfoObject.setFld_charged_area(row.getFieldAs(6));
//                esInfoObject.setFld_obj_status(row.getFieldAs(7));
//                return esInfoObject;
//            }
//        });
//
//        BootstrapTransformation<EsInfoObject> transformation = OperatorTransformation.bootstrapWith(mapData).keyBy(k -> k.getFld_guid()).transform(new KeyedStateBootstrapFunction<String, EsInfoObject>() {
//            MapState<String, EsInfoObject> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsInfoObject>("map_stat", String.class, EsInfoObject.class));
//            }
//
//            @Override
//            public void processElement(EsInfoObject EsChargeIncomingFee, KeyedStateBootstrapFunction.Context context) throws Exception {
//                mapState.put(EsChargeIncomingFee.getFld_guid(), EsChargeIncomingFee);
//            }
//        });
//        newSavepoint.withOperator("es_info_object", transformation).write("file:///D://charge//es_charge_owner_fee_1");
//        env.execute();
//    }
//
//    private static void main4(Connection conn, Statement statement) throws Exception {
//        String s = "select fld_area_guid from maindb.es_info_owner group by fld_area_guid";
//        JdbcGenericParameterValuesProvider paramProviderIncoming = Util.getParameters(statement, s, "fld_area_guid");
//        JdbcUtil.close(conn, statement);
//        MapOperator<Row, EsInfoOwner> mapData = env.
//                createInput(jdbcInputFormatBuilder.setParametersProvider(paramProviderIncoming).setQuery(SqlConfig.ES_INFO_OWNER).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsInfoOwner>() {
//            EsInfoOwner esInfoObject = new EsInfoOwner();
//
//            @Override
//            public EsInfoOwner map(Row row) throws Exception {
//                esInfoObject.setFld_guid(row.getFieldAs(0));
//                esInfoObject.setFld_owner_name(row.getFieldAs(1));
//                esInfoObject.setFld_owner_desc(row.getFieldAs(2));
//                return esInfoObject;
//            }
//        });
//
//        BootstrapTransformation<EsInfoOwner> transformation = OperatorTransformation.bootstrapWith(mapData).keyBy(k -> k.getFld_guid()).transform(new KeyedStateBootstrapFunction<String, EsInfoOwner>() {
//            MapState<String, EsInfoOwner> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsInfoOwner>("map_stat", String.class, EsInfoOwner.class));
//            }
//
//            @Override
//            public void processElement(EsInfoOwner obj, KeyedStateBootstrapFunction.Context context) throws Exception {
//                mapState.put(obj.getFld_guid(), obj);
//            }
//        });
//        newSavepoint.withOperator("es_info_owner", transformation).write("file:///D://charge//es_charge_owner_fee_1");
//        env.execute();
//    }
//
//    private static void main5(Connection conn, Statement statement) throws Exception {
//        String s = "select fld_area_guid from maindb.es_charge_incoming_back group by fld_area_guid";
//        JdbcGenericParameterValuesProvider paramProviderIncoming = Util.getParameters(statement, s, "fld_area_guid");
//        JdbcUtil.close(conn, statement);
//
//        MapOperator<Row, EsChargeIncomingBackDomain> backData = env.
//                createInput(jdbcInputFormatBuilder.setParametersProvider(paramProviderIncoming).setQuery(SqlConfig.ES_CHARGE_INCOMING_BACK).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeIncomingBackDomain>() {
//            EsChargeIncomingBackDomain esInfoObject = new EsChargeIncomingBackDomain();
//
//            @Override
//            public EsChargeIncomingBackDomain map(Row row) throws Exception {
//                esInfoObject.setFld_guid(row.getFieldAs(0));
//                esInfoObject.setFld_incoming_back_guid(row.getFieldAs(1));
////                esInfoObject.setFld_incoming_convert_guid(row.getFieldAs(2));
////                esInfoObject.setFld_incoming_kou_guid(row.getFieldAs(3));
//                esInfoObject.setFld_submit_time(row.getFieldAs(4));
//                return esInfoObject;
//            }
//        });
//
//        MapOperator<Row, EsChargeIncomingBackDomain> convertData = env.
//                createInput(jdbcInputFormatBuilder.setParametersProvider(paramProviderIncoming).setQuery(SqlConfig.ES_CHARGE_INCOMING_BACK).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeIncomingBackDomain>() {
//            EsChargeIncomingBackDomain esInfoObject = new EsChargeIncomingBackDomain();
//
//            @Override
//            public EsChargeIncomingBackDomain map(Row row) throws Exception {
//                esInfoObject.setFld_guid(row.getFieldAs(0));
////                esInfoObject.setFld_incoming_back_guid(row.getFieldAs(1));
//                esInfoObject.setFld_incoming_convert_guid(row.getFieldAs(2));
////                esInfoObject.setFld_incoming_kou_guid(row.getFieldAs(3));
//                esInfoObject.setFld_submit_time(row.getFieldAs(4));
//                return esInfoObject;
//            }
//        });
//
//        MapOperator<Row, EsChargeIncomingBackDomain> kouData = env.
//                createInput(jdbcInputFormatBuilder.setParametersProvider(paramProviderIncoming).setQuery(SqlConfig.ES_CHARGE_INCOMING_BACK).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeIncomingBackDomain>() {
//            EsChargeIncomingBackDomain esInfoObject = new EsChargeIncomingBackDomain();
//
//            @Override
//            public EsChargeIncomingBackDomain map(Row row) throws Exception {
//                esInfoObject.setFld_guid(row.getFieldAs(0));
////                esInfoObject.setFld_incoming_back_guid(row.getFieldAs(1));
////                esInfoObject.setFld_incoming_convert_guid(row.getFieldAs(2));
//                esInfoObject.setFld_incoming_kou_guid(row.getFieldAs(3));
//                esInfoObject.setFld_submit_time(row.getFieldAs(4));
//                return esInfoObject;
//            }
//        });
//
//
//        BootstrapTransformation<EsChargeIncomingBackDomain> transformationBack = OperatorTransformation.bootstrapWith(backData).keyBy(k -> k.getFld_incoming_back_guid()).transform(new KeyedStateBootstrapFunction<String, EsChargeIncomingBackDomain>() {
//            MapState<String, EsChargeIncomingBackDomain> backState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                backState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingBackDomain>("map_stat", String.class, EsChargeIncomingBackDomain.class));
//            }
//
//            @Override
//            public void processElement(EsChargeIncomingBackDomain newData, KeyedStateBootstrapFunction.Context context) throws Exception {
//                String fld_incoming_back_guid = newData.getFld_incoming_back_guid();
//                EsChargeIncomingBackDomain oldData = backState.get(fld_incoming_back_guid);
//                if (null == oldData) {
//                    backState.put(fld_incoming_back_guid, newData);
//                } else {
//                    if (newData.compareTo(oldData) > 0) {
//                        backState.put(fld_incoming_back_guid, newData);
//                    }
//                }
//            }
//        });
//
//        BootstrapTransformation<EsChargeIncomingBackDomain> transformationConvert = OperatorTransformation.bootstrapWith(convertData).keyBy(k -> k.getFld_incoming_convert_guid()).transform(new KeyedStateBootstrapFunction<String, EsChargeIncomingBackDomain>() {
//            MapState<String, EsChargeIncomingBackDomain> convertState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                convertState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingBackDomain>("map_stat", String.class, EsChargeIncomingBackDomain.class));
//            }
//
//            @Override
//            public void processElement(EsChargeIncomingBackDomain newData, KeyedStateBootstrapFunction.Context context) throws Exception {
//                String fld_incoming_convert_guid = newData.getFld_incoming_convert_guid();
//                EsChargeIncomingBackDomain oldData = convertState.get(fld_incoming_convert_guid);
//                if (null == oldData) {
//                    convertState.put(fld_incoming_convert_guid, newData);
//                } else {
//                    if (newData.compareTo(oldData) > 0) {
//                        convertState.put(fld_incoming_convert_guid, newData);
//                    }
//                }
//            }
//        });
//
//
//        BootstrapTransformation<EsChargeIncomingBackDomain> transformationKou = OperatorTransformation.bootstrapWith(kouData).keyBy(k -> k.getFld_incoming_kou_guid()).transform(new KeyedStateBootstrapFunction<String, EsChargeIncomingBackDomain>() {
//            MapState<String, EsChargeIncomingBackDomain> convertKou;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                convertKou = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingBackDomain>("map_stat", String.class, EsChargeIncomingBackDomain.class));
//            }
//
//            @Override
//            public void processElement(EsChargeIncomingBackDomain newData, KeyedStateBootstrapFunction.Context context) throws Exception {
//                String fld_incoming_kou_guid = newData.getFld_incoming_kou_guid();
//                EsChargeIncomingBackDomain oldData = convertKou.get(fld_incoming_kou_guid);
//                if (null == oldData) {
//                    convertKou.put(fld_incoming_kou_guid, newData);
//                } else {
//                    if (newData.compareTo(oldData) > 0) {
//                        convertKou.put(fld_incoming_kou_guid, newData);
//                    }
//                }
//            }
//        });
//        newSavepoint.withOperator("es_info_owner", transformationBack).withOperator("es_info_owner", transformationConvert).withOperator("es_info_owner", transformationKou).write("file:///D://charge//es_charge_owner_fee_1");
//        env.execute();
//    }
//
//    private static void main6() throws Exception {
//        MapOperator<Row, EsChargeVoucherMastRefundDomain> mapData = env.
//                createInput(jdbcInputFormatBuilder.setQuery(SqlConfig.ES_INFO_AREA_INFO).setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish()).map(new MapFunction<Row, EsChargeVoucherMastRefundDomain>() {
//            EsChargeVoucherMastRefundDomain esInfoAreaInfo = new EsChargeVoucherMastRefundDomain();
//
//            @Override
//            public EsChargeVoucherMastRefundDomain map(Row row) throws Exception {
//                esInfoAreaInfo.setFld_guid(row.getFieldAs(0));
//                esInfoAreaInfo.setFld_incoming_back_guid(row.getFieldAs(1));
//                esInfoAreaInfo.setFld_appay_date(row.getFieldAs(2));
//                esInfoAreaInfo.setFld_date(row.getFieldAs(3));
//                return esInfoAreaInfo;
//            }
//        });
//
//        BootstrapTransformation<EsChargeVoucherMastRefundDomain> transformation = OperatorTransformation.bootstrapWith(mapData).keyBy(k -> k.getFld_incoming_back_guid()).transform(new KeyedStateBootstrapFunction<String, EsChargeVoucherMastRefundDomain>() {
//            MapState<String, EsChargeVoucherMastRefundDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeVoucherMastRefundDomain>("map_stat", String.class, EsChargeVoucherMastRefundDomain.class));
//            }
//
//            @Override
//            public void processElement(EsChargeVoucherMastRefundDomain newData, KeyedStateBootstrapFunction.Context context) throws Exception {
//                //  --该语句可能重复，需要分组（根据fld_incoming_back_guid，取fld_appay_date desc最近的一笔
//                String fld_incoming_back_guid = newData.getFld_incoming_back_guid();
//                EsChargeVoucherMastRefundDomain oldNew = mapState.get(fld_incoming_back_guid);
//                if (null == oldNew) {
//                    mapState.put(fld_incoming_back_guid, newData);
//                    return;
//                }
//                if (newData.compareTo(oldNew) > 0) {
//                    mapState.put(fld_incoming_back_guid, newData);
//                }
//            }
//        });
//        newSavepoint.withOperator("es_info_area_info", transformation).write("file:///D://charge//es_info_area_info");
//        env.execute();
//    }
//}
