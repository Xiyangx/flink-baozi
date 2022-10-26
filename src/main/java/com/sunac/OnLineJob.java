//package com.sunac;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sunac.comap.*;
//import com.sunac.domain.*;
//import com.sunac.map.*;
//import com.sunac.utils.Util;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.flink.api.common.ExecutionConfig;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.functions.RuntimeContext;
//import org.apache.flink.api.common.state.MapState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.java.operators.DataSource;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.datastream.*;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.ProcessFunction;
//import org.apache.flink.streaming.api.functions.co.RichCoMapFunction;
//import org.apache.flink.util.Collector;
//import org.apache.flink.util.OutputTag;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.baozi
// * @date:2022/9/1 ods_fd_es_charge_incoming_data_af d
// * JOIN ods_fd_es_charge_incoming_fee_af f ON f.fld_guid = d.fld_incoming_fee_guid
// * JOIN ods_fd_es_info_area_info_af a ON a.fld_guid = d.fld_area_guid
// * JOIN ods_fd_es_info_object_af o ON o.fld_guid = d.fld_object_guid
// * JOIN ods_fd_es_info_owner_af w ON w.fld_guid = d.fld_owner_guid
// * <p>
// * <p>
// * JOIN ods_fd_es_charge_project_af p ON p.fld_guid = d.fld_project_guid
// * JOIN ods_fd_es_charge_pay_mode_af m ON m.fld_guid = d.fld_pay_mode_guid
// * JOIN ods_fd_es_charge_owner_fee_af cof ON cof.fld_guid = d.fld_owner_fee_guid
// * AND cof.fld_area_guid = d.fld_area_guid
// * <p>
// * JOIN ods_fd_es_info_object_park_af op ON op.fld_guid = d.fld_object_guid
// * AND op.fld_area_guid = d.fld_area_guid
// * JOIN ods_fd_es_info_object_class_af oc ON oc.fld_guid = o.fld_class_guid
// * <p>
// * JOIN ods_fd_es_info_object_and_owner_af ao ON d.fld_area_guid = ao.fld_area_guid   -- 该语句可能重复，需要去重（分组：fld_area_guid、fld_object_guid、fld_owner_guid 排序：fld_is_current desc,.fld_is_charge desc,fld_status desc）
// * AND ao.fld_object_guid = d.fld_object_guid
// * AND ao.fld_owner_guid = d.fld_owner_guid
// * <p>
// * ./kafka-console-consumer.sh --bootstrap-server 172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667 --topic testdb --from-beginning
// */
//public class OnLineJob {
//    public static void main(String[] args) throws Exception {
//        Configuration configuration = new Configuration();
//        configuration.setInteger("rest.port", 8822);
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
//        BroadcastStream<Tuple2<String, Set<String>>> broadcastStateStream = env.fromCollection(Util.initBrocastState()).broadcast(Config.userInfoStateDesc);
//
//        env.setParallelism(10);
//        /**1,主表：es_charge_incoming_data**/
//        BroadcastConnectedStream<String, Tuple2<String, Set<String>>> es_charge_incoming_data_stream = Util.getKafkaSource(env, "es_charge_incoming_data").connect(broadcastStateStream);
//        SingleOutputStreamOperator<AllData> mainTable = es_charge_incoming_data_stream.process(new EsChargeIncomingDataMapFunction("es_charge_incoming_data"));
//
//        /***===========================================================================================================================*/
//        //1,实收费总表:f.fld_guid = d.fld_incoming_fee_guid
//        SingleOutputStreamOperator<EsChargeIncomingFee> es_charge_incoming_fee_stream_map = Util.getKafkaSource(env, "es_charge_incoming_fee").map(new EsChargeIncomingFeeMapFunction());
//        ConnectedStreams<AllData, EsChargeIncomingFee> join1 = mainTable.connect(es_charge_incoming_fee_stream_map);
//        SingleOutputStreamOperator<AllData> join1Done = join1.keyBy(t1 -> t1.getFld_incoming_fee_guid(), t2 -> t2.getFld_guid()).map(new EsChargeIncomingFeeCoMapFunction());
//        // 2,es_info_area_info:围合资料表:ON a.fld_guid = d.fld_area_guid
//        SingleOutputStreamOperator<String> processed = Util.getKafkaSource(env, "es_charge_other").process(new ProcessFunction<String, String>() {
//            @Override
//            public void processElement(String s, Context ctx, Collector<String> collector) throws Exception {
//                JSONObject jsonObject = JSON.parseObject(s);
//                String table = jsonObject.getString("table");
//                if ("es_info_area_info".equals(table)) {
//                    ctx.output(new OutputTag<JSONObject>("es_info_area_info", TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if ("es_charge_project".equals(table)) {
//                    EsChargeProject esChargeProject = Util.parseEsChargeProjectJson(s);
//                    ctx.output(new OutputTag<EsChargeProject>("es_charge_project", TypeInformation.of(EsChargeProject.class)), esChargeProject);
//                    return;
//                }
//                if ("es_charge_pay_mode".equals(table)) {
//                    EsChargePayModel esChargePayModel = Util.parseEsChargePayModelJson(s);
//                    ctx.output(new OutputTag<EsChargePayModel>("es_charge_pay_mode", TypeInformation.of(EsChargePayModel.class)), esChargePayModel);
//                    return;
//                }
//                // es_charge_project_period_join
//                if ("es_charge_project_period_join".equals(table)) {
//                    EsChargeProjectPeriodJoinDomain esChargeProjectPeriodJoinDomain = Util.parseEsChargeProjectPeriodJoinDomainJson(s);
//                    ctx.output(new OutputTag<EsChargeProjectPeriodJoinDomain>("es_charge_project_period_join", TypeInformation.of(EsChargeProjectPeriodJoinDomain.class)), esChargeProjectPeriodJoinDomain);
//                }
//                // es_charge_project_period
//                if ("es_charge_project_period".equals(table)) {
//                    EsChargeProjectPeriodDomain esChargeProjectPeriodDomain = Util.parseEsChargeProjectPeriodDomainJson(s);
//                    ctx.output(new OutputTag<EsChargeProjectPeriodDomain>("es_charge_project_period", TypeInformation.of(EsChargeProjectPeriodDomain.class)), esChargeProjectPeriodDomain);
//                }
//                // es_charge_two_balance
//                if ("es_charge_two_balance".equals(table)) {
//                    EsChargeTwoBalanceDomain esChargeTwoBalanceDomain = Util.parseEsChargeTwoBalanceDomainJson(s);
//                    ctx.output(new OutputTag<EsChargeTwoBalanceDomain>("es_charge_two_balance", TypeInformation.of(EsChargeTwoBalanceDomain.class)), esChargeTwoBalanceDomain);
//                }
//                // es_charge_hand_in_record
//                if ("es_charge_hand_in_record".equals(table)) {
//                    EsChargeHandInRecordDomain esChargeHandInRecordDomain = Util.parseEsChargeHandInRecordDomainJson(s);
//                    ctx.output(new OutputTag<EsChargeHandInRecordDomain>("es_charge_hand_in_record", TypeInformation.of(EsChargeHandInRecordDomain.class)), esChargeHandInRecordDomain);
//                }
//                // es_charge_voucher_check_service_set
//                if ("es_charge_voucher_check_service_set".equals(table)) {
//                    EsChargeVoucherCheckServiceSetDomain esChargeVoucherCheckServiceSetDomain = Util.parseEsChargeVoucherCheckServiceSetDomainJson(s);
//                    ctx.output(new OutputTag<EsChargeVoucherCheckServiceSetDomain>("es_charge_voucher_check_service_set", TypeInformation.of(EsChargeVoucherCheckServiceSetDomain.class)), esChargeVoucherCheckServiceSetDomain);
//                }
//                // es_charge_voucher_project_pay
//                if ("es_charge_voucher_project_pay".equals(table)) {
//                    EsChargeVoucherProjectPayDomain esChargeVoucherProjectPayDomain = Util.parseEsChargeVoucherProjectPayDomainJson(s);
//                    ctx.output(new OutputTag<EsChargeVoucherProjectPayDomain>("es_charge_voucher_project_pay", TypeInformation.of(EsChargeVoucherProjectPayDomain.class)), esChargeVoucherProjectPayDomain);
//                }
//                // es_charge_incoming_back
//                if ("es_charge_incoming_back".equals(table)) {
//                    EsChargeIncomingBackDomain esChargeIncomingBackDomain = Util.parseEsChargeIncomingBackDomainJson(s);
//                    ctx.output(new OutputTag<EsChargeIncomingBackDomain>("es_charge_incoming_back", TypeInformation.of(EsChargeIncomingBackDomain.class)), esChargeIncomingBackDomain);
//                }
//
//            }
//        });
//        /**获取围合资料表的测流**/
//        DataStream<JSONObject> EsInfoAreaInfoStream = processed.getSideOutput(new OutputTag<JSONObject>("es_info_area_info", TypeInformation.of(JSONObject.class))).map(new EsInfoAreaMapFunction())
//
//
//        ConnectedStreams<AllData, JSONObject> join2 = join1Done.connect(EsInfoAreaInfoStream);
//        SingleOutputStreamOperator<AllData> join2Done = join2.keyBy(t -> t.getFld_area_guid(), t2 -> t2.getFld_guid()).map(new EsInfoAreaCoMapFunction());
//        // 3,es_info_object:资源总表:ON o.fld_guid = d.fld_object_guid
//        SingleOutputStreamOperator<EsInfoObject> es_info_object_stream_map = Util.getKafkaSource(env, "es_info_object").map(new EsInfoObjectMapFunction());
//        ConnectedStreams<AllData, EsInfoObject> join3 = join2Done.connect(es_info_object_stream_map);
//        SingleOutputStreamOperator<AllData> join3Done = join3.keyBy(t1 -> t1.getFld_object_guid(), t2 -> t2.getFld_guid()).map(new EsInfoObjectCoMapFunction());
//
//
//        // 4,es_info_owner	客户总表:2509145 w ON w.fld_guid = d.fld_owner_guid:测流
//        /**TODO:创建测流集合**/
//        SingleOutputStreamOperator<String> processedConnectStream = Util.getKafkaSource(env, "es_charge_other_2").process(new ProcessFunction<String, String>() {
//            @Override
//            public void processElement(String s, Context ctx, Collector<String> collector) throws Exception {
//                JSONObject jsonObject = JSON.parseObject(s);
//                String table = jsonObject.getString("table");
//                if ("es_info_owner".equals(table)) {
//                    EsInfoOwner esInfoOwner = Util.parseEsInfoOwnerJson(s);
//                    ctx.output(new OutputTag<EsInfoOwner>("es_info_owner", TypeInformation.of(EsInfoOwner.class)), esInfoOwner);
//                }
//                if ("es_info_object_park".equals(table)) {
//                    EsInfoObjectPark esInfoObjectPark = Util.parseEsInfoObjectParkJson(s);
//                    ctx.output(new OutputTag<EsInfoObjectPark>("es_info_object_park", TypeInformation.of(EsInfoObjectPark.class)), esInfoObjectPark);
//                }
//                // es_info_object_class
//                if ("es_info_object_class".equals(table)) {
//                    EsInfoObjectClass esInfoObjectClass = Util.parseEsInfoObjectClassJson(s);
//                    ctx.output(new OutputTag<EsInfoObjectClass>("es_info_object_park", TypeInformation.of(EsInfoObjectClass.class)), esInfoObjectClass);
//                }
//                // es_info_object_and_owner
//                if ("es_info_object_and_owner".equals(table)) {
//                    EsInfoObjectAndOwnerDomain esInfoObjectAndOwnerDomain = Util.parseEsInfoObjectAndOwnerJson(s);
//                    ctx.output(new OutputTag<EsInfoObjectAndOwnerDomain>("es_info_object_and_owner", TypeInformation.of(EsInfoObjectAndOwnerDomain.class)), esInfoObjectAndOwnerDomain);
//                }
//                // es_charge_bill
//
//                if ("es_charge_bill".equals(table)) {
//                    EsChargeBill esChargeBill = Util.parseEsChargeBillJson(s);
//                    ctx.output(new OutputTag<EsChargeBill>("es_charge_bill", TypeInformation.of(EsChargeBill.class)), esChargeBill);
//                }
//                // es_charge_bill_type
//                if ("es_charge_bill_type".equals(table)) {
//                    EsChargeBillType esChargeBillType = Util.parseEsChargeBillTypeJson(s);
//                    ctx.output(new OutputTag<EsChargeBillType>("es_charge_bill_type", TypeInformation.of(EsChargeBillType.class)), esChargeBillType);
//                }
//                // es_charge_settle_accounts_detail
//                if ("es_charge_settle_accounts_detail".equals(table)) {
//                    EsChargeSettleAccountsDetail esChargeSettleAccountsDetail = Util.parseEsChargeSettleAccountsDetailJson(s);
//                    ctx.output(new OutputTag<EsChargeSettleAccountsDetail>("es_charge_settle_accounts_detail", TypeInformation.of(EsChargeSettleAccountsDetail.class)), esChargeSettleAccountsDetail);
//                }
//                // es_charge_settle_accounts_main
//                if ("es_charge_settle_accounts_main".equals(table)) {
//                    EsChargeSettleAccountsMain esChargeSettleAccountsMain = Util.parseEsChargeSettleAccountsMainJson(s);
//                    ctx.output(new OutputTag<EsChargeSettleAccountsMain>("es_charge_settle_accounts_main", TypeInformation.of(EsChargeSettleAccountsMain.class)), esChargeSettleAccountsMain);
//                }
//            }
//        });
//        DataStream<EsInfoOwner> esInfoOwnerStream = processedConnectStream.getSideOutput(new OutputTag<EsInfoOwner>("es_info_owner", TypeInformation.of(EsInfoOwner.class)));
//        ConnectedStreams<AllData, EsInfoOwner> join4 = join3Done.connect(esInfoOwnerStream);
//        SingleOutputStreamOperator<AllData> joni4Done = join4.keyBy(t1 -> t1.getFld_owner_guid(), t2 -> t2.getFld_guid()).map(new EsInfoOwnerCoMapFunction());
//        // 5,es_charge_project	计费科目表:测流 p ON p.fld_guid = d.fld_project_guid
//
////        fld_guid		es_charge_project	ON p.fld_guid = d.fld_project_guid
////        fld_name AS fld_project_name,		es_charge_project
////        fld_object_type ,		es_charge_project
//        DataStream<EsChargeProject> esChargeProjectStream = processed.getSideOutput(new OutputTag<EsChargeProject>("es_charge_project", TypeInformation.of(EsChargeProject.class)));
//        ConnectedStreams<AllData, EsChargeProject> join5 = joni4Done.connect(esChargeProjectStream);
//        SingleOutputStreamOperator<AllData> join5Done = join5.keyBy(t1 -> t1.getFld_project_guid(), t2 -> t2.getFld_guid()).map(new EsChargeProjectCoMapFunction());
//
//        /**获取计费科目表表的测流**/
        // 6,es_charge_pay_mode	付款方式/预付款类型:测流 m ON m.fld_guid = d.fld_pay_mode_guid
/*        fld_guid		es_charge_pay_mode	ON m.fld_guid = d.fld_pay_mode_guid
          fld_name AS fld_pay_mode_name,		es_charge_pay_mode
          fld_resource,		es_charge_pay_mode
          fld_pre_pay,		es_charge_pay_mode*/
//        DataStream<EsChargePayModel> esChargePayModelStream = processed.getSideOutput(new OutputTag<EsChargePayModel>("es_charge_pay_mode", TypeInformation.of(EsChargePayModel.class)));
//        ConnectedStreams<AllData, EsChargePayModel> join6 = join5Done.connect(esChargePayModelStream);
//        SingleOutputStreamOperator<AllData> join6Done = join6.keyBy(t1 -> t1.getFld_pay_mode_guid(), t2 -> t2.getFld_guid()).map(new EsChargePayModelCoMapFunction());
//        // 7,es_charge_owner_fee:应收欠费明细 topic=es_charge_owner_fee_2
//        // JOIN ods_fd_es_charge_owner_fee_af cof ON cof.fld_guid = d.fld_owner_fee_guid AND cof.fld_area_guid = d.fld_area_guid
//
//        SingleOutputStreamOperator<EsChargeOwnerFee> es_charge_owner_fee_2_stream_map = Util.getKafkaSource(env, "es_charge_owner_fee_2").map(new EsChargeOwnerFeeMapFunction());
//
//        ConnectedStreams<AllData, EsChargeOwnerFee> join7 = join6Done.connect(es_charge_owner_fee_2_stream_map);
//        SingleOutputStreamOperator<AllData> join7Done = join7.keyBy(t1 -> DigestUtils.md5Hex(t1.getFld_owner_fee_guid() + t1.getFld_area_guid()), t2 -> t2.getPk()).map(new EsChargeOwnerFeeCoMapFunction());
//
//        // 8,es_info_object_park	车位资料 op.fld_guid = d.fld_object_guid AND op.fld_area_guid = d.fld_area_guid
////                fld_guid
////                fld_area_guid
////                fld_cw_category,
//        DataStream<EsInfoObjectPark> esInfoObjectParkStream = processedConnectStream.getSideOutput(new OutputTag<EsInfoObjectPark>("es_info_object_park", TypeInformation.of(EsInfoObjectPark.class)));
//        ConnectedStreams<AllData, EsInfoObjectPark> join8 = join7Done.connect(esInfoObjectParkStream);
//        SingleOutputStreamOperator<AllData> join8Done = join8.keyBy(t1 -> DigestUtils.md5Hex(t1.getFld_object_guid() + t1.getFld_area_guid()), t2 -> t2.getPk()).map(new EsInfoObjectParkCoMapFunction());
//
//        // 9,es_info_object_class	资源业态:oc ON oc.fld_guid = o.fld_class_guid
////                fld_guid,
////                fld_name fld_object_class_name,
////        DataStream<EsInfoObjectClass> esInfoObjectClassStream = processedConnectStream.getSideOutput(new OutputTag<EsInfoObjectClass>("es_info_object_class", TypeInformation.of(EsInfoObjectClass.class)));
////        ConnectedStreams<AllData, EsInfoObjectClass> join9 = join8Done.connect(esInfoObjectClassStream);
////        SingleOutputStreamOperator<AllData> join9Done = join8.keyBy(t1 -> t1.getFld_c, t2 -> t2.getPk()).map(new EsInfoObjectParkCoMapFunction());
//
//        // 10,ods_fd_es_info_object_and_owner_af:人房关系表 ao
//        // -- 该语句可能重复，需要去重（
//        // 分组：fld_area_guid、fld_object_guid、fld_owner_guid
//        // 排序：fld_is_current desc,.fld_is_charge desc,fld_status desc）
//        //   ON d.fld_area_guid = ao.fld_area_guid
//        // * AND ao.fld_object_guid = d.fld_object_guid
//        // * AND ao.fld_owner_guid = d.fld_owner_guid
//        DataStream<EsInfoObjectAndOwnerDomain> esInfoObjectAndOwnerDomain = processedConnectStream.getSideOutput(new OutputTag<EsInfoObjectAndOwnerDomain>("es_info_object_and_owner", TypeInformation.of(EsInfoObjectAndOwnerDomain.class)));
//        ConnectedStreams<AllData, EsInfoObjectAndOwnerDomain> join9 = join8Done.connect(esInfoObjectAndOwnerDomain);
//        SingleOutputStreamOperator<AllData> join9Done = join9.keyBy(t1 -> DigestUtils.md5Hex(t1.getFld_area_guid() + t1.getFld_object_guid() + t1.getFld_owner_guid()), t2 -> t2.getPk()).map(new EsInfoObjectAndOwnerCoMapFunction());
//
//        // 11,) db ON:es_charge_incoming_data_bill  实收明细使用票据记录   es_charge_bill:票据号使用记录表(测流) es_charge_bill_type	票据类型设置表 (测流)
////        JOIN (
////                -- 取收据：该语句可能重复，需要去重（分组：fld_data_src_guid 排序：b.fld_operate_date desc）
////                SELECT
////                data_bill.fld_data_src_guid,
////                b.fld_status,
////                b.fld_bill_code,
////                t.fld_name,
////                t.fld_guid
////                FROM
////                ods_fd_es_charge_incoming_data_bill_af data_bill
////                JOIN ods_fd_es_charge_bill_af b ON data_bill.fld_bill_guid = b.fld_guid
////                JOIN ods_fd_es_charge_bill_type_af t ON t.fld_guid = b.fld_type_guid and t.fld_category=0
////        ) db ON db.fld_data_src_guid = d.fld_guid
//        SingleOutputStreamOperator<EsChargeIncomingDataBill> es_charge_incoming_data_bill_map_stream = Util.getKafkaSource(env, "es_charge_incoming_data_bill").map(new EsChargeIncomingDataBillMapFunction());
//        ConnectedStreams<AllData, EsChargeIncomingDataBill> join10 = join9Done.connect(es_charge_incoming_data_bill_map_stream);
//        SingleOutputStreamOperator<ArrayList<AllData>> join10Done = join10.keyBy(t1 -> t1.getFld_guid(), t2 -> t2.getFld_data_src_guid()).map(new EsChargeIncomingDataBillRichCoMapFunction());
//        // ods_fd_es_charge_bill_af b ON data_bill.fld_bill_guid = b.fld_guid
//        DataStream<EsChargeBill> es_charge_bill_map_stream = processedConnectStream.getSideOutput(new OutputTag<EsChargeBill>("es_charge_bill", TypeInformation.of(EsChargeBill.class)));
//        // TODO:这里要到同一个partition中去
//        ConnectedStreams<ArrayList<AllData>, EsChargeBill> join11 = join10Done.connect(es_charge_bill_map_stream).keyBy(t -> "666", t2 -> "666");
//        SingleOutputStreamOperator<AllData> join11Done = join11.map(new RichCoMapFunction<ArrayList<AllData>, EsChargeBill, AllData>() {
//            MapState<String, EsChargeBill> mapState;
//            ArrayList<EsChargeBill> list = null;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                list = new ArrayList<>();
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeBill>("map_stat", String.class, EsChargeBill.class));
//            }
//
//            @Override
//            public AllData map1(ArrayList<AllData> allDatas) throws Exception {
//                list.clear();
//                for (AllData bill : allDatas) {
//                    String fld_bill_guid = bill.getFld_bill_guid();
//                    EsChargeBill esChargeBill = mapState.get(fld_bill_guid);
//                    list.add(esChargeBill);
//                }
//                EsChargeBill ret = null;
//                int indexMax = 0;
//                for (int i = 0; i < list.size(); i++) {
//                    if (ret == null) {
//                        ret = list.get(i);
//                        continue;
//                    }
//                    if (list.get(i).compareTo(ret) > 0) {
//                        // 取新来的值最大的
//                        ret = list.get(i);
//                        indexMax = i;
//                        continue;
//                    }
//                }
//                AllData allData = allDatas.get(indexMax);
//                allData.setFld_bill_status(ret.getFld_status());
//                allData.setFld_bill_code(ret.getFld_bill_code());
//                // 增加一个fld_type_guid用于关联后面的表
//                allData.setFld_type_guid(ret.getFld_type_guid());
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeBill esChargeBill) throws Exception {
//                //TODO:跟新state
//                return null;
//            }
//        });
//
//        //fd_es_charge_bill_type_af t ON t.fld_guid = b.fld_type_guid and t.fld_category=0
//        DataStream<EsChargeBillType> es_charge_bill_type_map_stream = processedConnectStream.getSideOutput(new OutputTag<EsChargeBillType>("es_charge_bill_type", TypeInformation.of(EsChargeBillType.class)));
//        ConnectedStreams<AllData, EsChargeBillType> join12 = join11Done.connect(es_charge_bill_type_map_stream);
//        SingleOutputStreamOperator<AllData> join12Done = join12.keyBy(t -> t.getFld_type_guid(), t2 -> t2.getFld_guid()).map(new RichCoMapFunction<AllData, EsChargeBillType, AllData>() {
//            MapState<String, EsChargeBillType> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeBillType>("map_stat", String.class, EsChargeBillType.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
////                fld_type_guid
//                String fld_type_guid = allData.getFld_type_guid();
//                EsChargeBillType esChargeBillType = mapState.get(fld_type_guid);
//                if (esChargeBillType != null) {
//                    int fld_category = esChargeBillType.getFld_category();
//                    if (1 == fld_category) {
//                        allData.setFld_bill_type2(esChargeBillType.getFld_guid());
//                        allData.setFld_bill_type_name2(esChargeBillType.getFld_name());
//                        allData.setFld_bill_status2(allData.getFld_bill_status());
//                        allData.setFld_bill_code2(allData.getFld_bill_code());
//                        // 除此之外，要将status  bill_code 弄回null
//                        allData.setFld_bill_status(null);
//                        allData.setFld_bill_code(null);
//                    } else {
//                        allData.setFld_bill_type(esChargeBillType.getFld_guid());
//                        allData.setFld_bill_type_name(esChargeBillType.getFld_name());
//                    }
//                }
//                return allData;
//
//            }
//
//            @Override
//            public AllData map2(EsChargeBillType esChargeBillType) throws Exception {
//                return null;
//            }
//        });
//        // 12,es_charge_settle_accounts_detail	地产结算明细表  --- 测流
//
//        DataStream<EsChargeSettleAccountsDetail> es_charge_settle_accounts_detail_map_stream = processedConnectStream.getSideOutput(new OutputTag<EsChargeSettleAccountsDetail>("es_charge_settle_accounts_detail", TypeInformation.of(EsChargeSettleAccountsDetail.class)));
//        ConnectedStreams<AllData, EsChargeSettleAccountsDetail> join13 = join12Done.connect(es_charge_settle_accounts_detail_map_stream);
//        ////        ON ( ad.fld_owner_fee_guid = d.fld_owner_fee_guid
//        ////          OR ad.fld_adjust_guid    = d.fld_owner_fee_guid
//        ////        AND ad.fld_area_guid = d.fld_area_guid
//        ////        AND ad.fld_status = 1
//
//        SingleOutputStreamOperator<AllData> join13_2 = join13.keyBy(t1 -> DigestUtils.md5Hex(t1.getFld_owner_fee_guid() + t1.getFld_area_guid()), t2 -> t2.getPk1()).map(new RichCoMapFunction<AllData, EsChargeSettleAccountsDetail, AllData>() {
//            MapState<String, EsChargeSettleAccountsDetail> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeSettleAccountsDetail>("map_stat", String.class, EsChargeSettleAccountsDetail.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeSettleAccountsDetail oldData = mapState.get(DigestUtils.md5Hex(allData.getFld_owner_fee_guid() + allData.getFld_area_guid()));
//                //          -- 该语句可能重复，需要去重（
//                //          分组：fld_area_guid、
//                //               fld_owner_fee_guid、
//                //               fld_adjust_guid
//                //          排序：fld_create_date desc）
//
//                if (oldData != null && oldData.getFld_status() == 1) {
//                    //        fld_owner_fee_guid
//                    //        fld_area_guid
//                    //        fld_main_guid
//
//                    //        fld_adjust_guid,
//                    //        fld_status,
//                    //        fld_settle_status,
//                    //        fld_bill_no AS fld_accounts_detail_bill_no,
//                    allData.setFld_main_guid(oldData.getFld_main_guid());
//                    allData.setFld_adjust_guid(oldData.getFld_adjust_guid());
//                    allData.setFld_status(oldData.getFld_status());
//                    allData.setFld_settle_status(oldData.getFld_settle_status());
//                    allData.setFld_bill_no(oldData.getFld_bill_no());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeSettleAccountsDetail esChargeSettleAccountsDetail) throws Exception {
//                return null;
//            }
//        });
//        ConnectedStreams<AllData, EsChargeSettleAccountsDetail> join13_3 = join13_2.connect(es_charge_settle_accounts_detail_map_stream);
//
//        SingleOutputStreamOperator<AllData> join13Done = join13_3.keyBy(t1 -> DigestUtils.md5Hex(t1.getFld_owner_fee_guid() + t1.getFld_area_guid()), t2 -> t2.getPk2()).map(new RichCoMapFunction<AllData, EsChargeSettleAccountsDetail, AllData>() {
//            MapState<String, EsChargeSettleAccountsDetail> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeSettleAccountsDetail>("map_stat", String.class, EsChargeSettleAccountsDetail.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeSettleAccountsDetail oldData = mapState.get(DigestUtils.md5Hex(allData.getFld_owner_fee_guid() + allData.getFld_area_guid()));
//                //          -- 该语句可能重复，需要去重（
//                //          分组：fld_area_guid、
//                //               fld_owner_fee_guid、
//                //               fld_adjust_guid
//                //          排序：fld_create_date desc）
//
//                if (oldData != null && oldData.getFld_status() == 1) {
//                    //        fld_owner_fee_guid
//                    //        fld_area_guid
//                    //        fld_main_guid
//
//                    //        fld_adjust_guid,
//                    //        fld_status,
//                    //        fld_settle_status,
//                    //        fld_bill_no AS fld_accounts_detail_bill_no,
//                    allData.setFld_adjust_guid(oldData.getFld_adjust_guid());
//                    allData.setFld_status(oldData.getFld_status());
//                    allData.setFld_settle_status(oldData.getFld_settle_status());
//                    allData.setFld_bill_no(oldData.getFld_bill_no());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeSettleAccountsDetail esChargeSettleAccountsDetail) throws Exception {
//                //todo 这里还得处理一次，因为要用到state
//                return null;
//            }
//        });
//        // 13,es_charge_settle_accounts_main	地产结算主表 --- 测流
//
////        JOIN ods_fd_es_charge_settle_accounts_main_af am ON
////            ad.fld_main_guid = am.fld_guid
////        AND am.fld_area_guid = d.fld_area_guid
////        AND am.fld_examine_status = 4
//
//        DataStream<EsChargeSettleAccountsMain> es_charge_settle_accounts_main_map_stream = processedConnectStream.getSideOutput(new OutputTag<EsChargeSettleAccountsMain>("es_charge_settle_accounts_main", TypeInformation.of(EsChargeSettleAccountsMain.class)));
//        ConnectedStreams<AllData, EsChargeSettleAccountsMain> join14 = join13Done.connect(es_charge_settle_accounts_main_map_stream);
//        SingleOutputStreamOperator<AllData> join14Done = join14.keyBy(t1 -> DigestUtils.md5Hex(t1.getFld_main_guid() + t1.getFld_guid()), t2 -> t2.getPk()).map(new RichCoMapFunction<AllData, EsChargeSettleAccountsMain, AllData>() {
//            MapState<String, EsChargeSettleAccountsMain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeSettleAccountsMain>("map_stat", String.class, EsChargeSettleAccountsMain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
////                fld_guid
////                fld_area_guid
////                fld_attribute,
////                fld_examine_status,
//                EsChargeSettleAccountsMain oldData = mapState.get(DigestUtils.md5Hex(allData.getFld_main_guid() + allData.getFld_guid()));
//                if (null != oldData) {
//                    allData.setFld_attribute(oldData.getFld_attribute());
//                    allData.setFld_examine_status(oldData.getFld_examine_status());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeSettleAccountsMain esChargeSettleAccountsMain) throws Exception {
//                return null;
//            }
//        });
//
//        // 14,es_charge_project_period_join	科目归类关联科目  --- 测流:on pj.fld_project_guid=d.fld_project_guid
//        DataStream<EsChargeProjectPeriodJoinDomain> esInfoAreaInfoEsChargeProjectPeriodJoinDomainStream = processed.getSideOutput(new OutputTag<EsChargeProjectPeriodJoinDomain>("es_charge_project_period_join", TypeInformation.of(EsChargeProjectPeriodJoinDomain.class)));
//        ConnectedStreams<AllData, EsChargeProjectPeriodJoinDomain> join15 = join14Done.connect(esInfoAreaInfoEsChargeProjectPeriodJoinDomainStream);
//        SingleOutputStreamOperator<AllData> join15Done = join15.keyBy(t1 -> t1.getFld_project_guid(), t2 -> t2.getFld_project_guid()).map(new RichCoMapFunction<AllData, EsChargeProjectPeriodJoinDomain, AllData>() {
//
//            MapState<String, EsChargeProjectPeriodJoinDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeProjectPeriodJoinDomain>("map_stat", String.class, EsChargeProjectPeriodJoinDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeProjectPeriodJoinDomain oldData = mapState.get(allData.getFld_project_guid());
//                if (null != oldData) {
//                    allData.setFld_period_guid(oldData.getFld_period_guid());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeProjectPeriodJoinDomain esChargeProjectPeriodJoinDomain) throws Exception {
//                return null;
//            }
//        });
//
//        // 15,es_charge_project_period	科目归类表 --- 测流
//        // fld_guid
//        // fld_type
//        // fld_name as fld_project_period_name,
//
//        DataStream<EsChargeProjectPeriodDomain> esChargeProjectPeriodDomainStream = processed.getSideOutput(new OutputTag<EsChargeProjectPeriodDomain>("es_charge_project_period", TypeInformation.of(EsChargeProjectPeriodDomain.class)));
//        ConnectedStreams<AllData, EsChargeProjectPeriodDomain> join16 = join15Done.connect(esChargeProjectPeriodDomainStream);
//        SingleOutputStreamOperator<AllData> join16Done = join16.keyBy(t1 -> t1.getFld_period_guid(), t2 -> t2.getFld_guid()).map(new RichCoMapFunction<AllData, EsChargeProjectPeriodDomain, AllData>() {
//
//            MapState<String, EsChargeProjectPeriodDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeProjectPeriodDomain>("map_stat", String.class, EsChargeProjectPeriodDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeProjectPeriodDomain oldData = mapState.get(allData.getFld_project_guid());
//                if (null != oldData) {
//                    allData.setFld_project_period_name(oldData.getFld_name());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeProjectPeriodDomain esChargeProjectPeriodDomain) throws Exception {
//                // pp.fld_type=1
//                return null;
//            }
//        });
//
//        // 16,es_charge_two_balance	对账信息表 --- 测流
//
//
//        DataStream<EsChargeTwoBalanceDomain> esChargeTwoBalanceDomainStream = processed.getSideOutput(new OutputTag<EsChargeTwoBalanceDomain>("es_charge_two_balance", TypeInformation.of(EsChargeTwoBalanceDomain.class)));
//        ConnectedStreams<AllData, EsChargeTwoBalanceDomain> join17 = join16Done.connect(esChargeTwoBalanceDomainStream);
//        SingleOutputStreamOperator<AllData> join17Done = join17.keyBy(t1 -> t1.getFld_incoming_fee_guid(), t2 -> t2.getFld_guid()).map(new RichCoMapFunction<AllData, EsChargeTwoBalanceDomain, AllData>() {
//
//            MapState<String, EsChargeTwoBalanceDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeTwoBalanceDomain>("map_stat", String.class, EsChargeTwoBalanceDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeTwoBalanceDomain oldData = mapState.get(allData.getFld_incoming_fee_guid());
//                if (null != oldData) {
//                    //        fld_guid as fld_balance_guid,
//                    //        fld_date as two_fld_date,
//                    //        fld_create_user as two_fld_create_user,
//                    //        fld_hand_in_guid,
//                    allData.setFld_balance_guid(oldData.getFld_guid());
//                    allData.setTwo_fld_date(oldData.getFld_date());
//                    allData.setTwo_fld_create_user(oldData.getFld_create_user());
//                    allData.setFld_hand_in_guid(oldData.getFld_hand_in_guid());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeTwoBalanceDomain esChargeTwoBalanceDomain) throws Exception {
//                // pp.fld_type=1
//                return null;
//            }
//        });
//
//        // 17,es_charge_hand_in_record	实收交款申请表 --- 测流:JOIN es_charge_hand_in_record hir on hir.fld_guid=two.fld_hand_in_guid
//
//        DataStream<EsChargeHandInRecordDomain> EsChargeHandInRecordDomainStream = processed.getSideOutput(new OutputTag<EsChargeHandInRecordDomain>("es_charge_hand_in_record", TypeInformation.of(EsChargeHandInRecordDomain.class)));
//        ConnectedStreams<AllData, EsChargeHandInRecordDomain> join18 = join17Done.connect(EsChargeHandInRecordDomainStream);
//        SingleOutputStreamOperator<AllData> join18Done = join18.keyBy(t1 -> t1.getFld_hand_in_guid(), t2 -> t2.getFld_guid()).map(new RichCoMapFunction<AllData, EsChargeHandInRecordDomain, AllData>() {
//
//            MapState<String, EsChargeHandInRecordDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeHandInRecordDomain>("map_stat", String.class, EsChargeHandInRecordDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeHandInRecordDomain oldData = mapState.get(allData.getFld_hand_in_guid());
//                if (null != oldData) {
//                    //        fld_guid
//                    //        fld_hand_in,
//                    //        fld_start_date as fld_hand_in_start_date,
//                    //        fld_end_date as fld_hand_in_end_date,
//                    allData.setFld_hand_in_start_date(oldData.getFld_start_date());
//                    allData.setFld_hand_in_end_date(oldData.getFld_end_date());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeHandInRecordDomain EsChargeHandInRecordDomain) throws Exception {
//                // pp.fld_type=1
//                return null;
//            }
//        });
//
//        // 18,es_charge_voucher_check_service_set	凭证-辅助核算配置-物业服务类型  -- 测流:ss.fld_project_class_guid=d.fld_project_guid
//
//        DataStream<EsChargeVoucherCheckServiceSetDomain> EsChargeVoucherCheckServiceSetDomainStream = processed.getSideOutput(new OutputTag<EsChargeVoucherCheckServiceSetDomain>("es_charge_voucher_check_service_set", TypeInformation.of(EsChargeVoucherCheckServiceSetDomain.class)));
//        ConnectedStreams<AllData, EsChargeVoucherCheckServiceSetDomain> join19 = join18Done.connect(EsChargeVoucherCheckServiceSetDomainStream);
//        SingleOutputStreamOperator<AllData> join19Done = join19.keyBy(t1 -> t1.getFld_project_guid(), t2 -> t2.getFld_project_class_guid()).map(new RichCoMapFunction<AllData, EsChargeVoucherCheckServiceSetDomain, AllData>() {
//            MapState<String, EsChargeVoucherCheckServiceSetDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeVoucherCheckServiceSetDomain>("map_stat", String.class, EsChargeVoucherCheckServiceSetDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeVoucherCheckServiceSetDomain oldData = mapState.get(allData.getFld_hand_in_guid());
//                if (null != oldData) {
////                    fld_project_class_guid
////                    fld_type as fld_service_set_type
//                    allData.setFld_service_set_type(oldData.getFld_type());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeVoucherCheckServiceSetDomain EsChargeVoucherCheckServiceSetDomain) throws Exception {
//                // pp.fld_type=1
//                return null;
//            }
//        });
//        // 19.es_charge_voucher_project_pay:凭证-核算科目配置-付款方式:vpp on vpp.fld_pay_mode_guid=d.fld_pay_mode_guid
//        DataStream<EsChargeVoucherProjectPayDomain> EsChargeVoucherProjectPayDomainStream = processed.getSideOutput(new OutputTag<EsChargeVoucherProjectPayDomain>("es_charge_voucher_project_pay", TypeInformation.of(EsChargeVoucherProjectPayDomain.class)));
//        ConnectedStreams<AllData, EsChargeVoucherProjectPayDomain> join20 = join19Done.connect(EsChargeVoucherProjectPayDomainStream);
//        SingleOutputStreamOperator<AllData> join20Done = join20.keyBy(t1 -> t1.getFld_pay_mode_guid(), t2 -> t2.getFld_pay_mode_guid()).map(new RichCoMapFunction<AllData, EsChargeVoucherProjectPayDomain, AllData>() {
//            MapState<String, EsChargeVoucherProjectPayDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeVoucherProjectPayDomain>("map_stat", String.class, EsChargeVoucherProjectPayDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeVoucherProjectPayDomain oldData = mapState.get(allData.getFld_pay_mode_guid());
//                if (null != oldData) {
//                    // fld_pay_mode_guid as voucher_fld_pay_mode_guid
//                    allData.setVoucher_fld_pay_mode_guid(oldData.getFld_pay_mode_guid());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeVoucherProjectPayDomain EsChargeVoucherProjectPayDomain) throws Exception {
//                // pp.fld_type=1
//                return null;
//            }
//        });
//
//        // 20,es_charge_incoming_back	退款凭证对应退款总表
//        DataStream<EsChargeIncomingBackDomain> esChargeIncomingBackDomain = processed.getSideOutput(new OutputTag<EsChargeIncomingBackDomain>("es_charge_incoming_back", TypeInformation.of(EsChargeIncomingBackDomain.class)));
//
//        ConnectedStreams<AllData, EsChargeIncomingBackDomain> join21 = join20Done.connect(esChargeIncomingBackDomain);
//        SingleOutputStreamOperator<AllData> join21Done = join21.keyBy(t1 -> t1.getFld_incoming_fee_guid(), t2 -> t2.getFld_incoming_back_guid()).map(new RichCoMapFunction<AllData, EsChargeIncomingBackDomain, AllData>() {
//            MapState<String, EsChargeIncomingBackDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingBackDomain>("map_stat", String.class, EsChargeIncomingBackDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeIncomingBackDomain oldData = mapState.get(allData.getFld_incoming_fee_guid());
//                if (null != oldData) {
//                    // fld_pay_mode_guid as voucher_fld_pay_mode_guid
//                    allData.setBack_fld_incoming_back_guid(oldData.getFld_incoming_back_guid());
//                    allData.setFld_eci_back_guid(oldData.getFld_guid());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeIncomingBackDomain EsChargeIncomingBackDomain) throws Exception {
//                // --该语句可能重复，需要分组(根据fld_incoming_back_guid分组，取fld_submit_time desc最近的一笔)
//                return null;
//            }
//        });
////
//
//        ConnectedStreams<AllData, EsChargeIncomingBackDomain> join22 = join21Done.connect(esChargeIncomingBackDomain);
//        SingleOutputStreamOperator<AllData> join22Done = join22.keyBy(t1 -> t1.getFld_incoming_fee_guid(), t2 -> t2.getFld_incoming_convert_guid()).map(new RichCoMapFunction<AllData, EsChargeIncomingBackDomain, AllData>() {
//            MapState<String, EsChargeIncomingBackDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingBackDomain>("map_stat", String.class, EsChargeIncomingBackDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeIncomingBackDomain oldData = mapState.get(allData.getFld_incoming_fee_guid());
//                if (null != oldData) {
//                    // fld_pay_mode_guid as voucher_fld_pay_mode_guid
//                    allData.setBack_fld_incoming_convert_guid(oldData.getFld_incoming_convert_guid());
//                    allData.setFld_eci_back_guid(oldData.getFld_guid());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeIncomingBackDomain EsChargeIncomingBackDomain) throws Exception {
//                // --该语句可能重复，需要分组(根据fld_incoming_back_guid分组，取fld_submit_time desc最近的一笔)
//                return null;
//            }
//        });
//
//        ConnectedStreams<AllData, EsChargeIncomingBackDomain> join23 = join22Done.connect(esChargeIncomingBackDomain);
//        SingleOutputStreamOperator<AllData> join24Done = join23.keyBy(t1 -> t1.getFld_incoming_fee_guid(), t2 -> t2.getFld_incoming_kou_guid()).map(new RichCoMapFunction<AllData, EsChargeIncomingBackDomain, AllData>() {
//            MapState<String, EsChargeIncomingBackDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingBackDomain>("map_stat", String.class, EsChargeIncomingBackDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeIncomingBackDomain oldData = mapState.get(allData.getFld_incoming_fee_guid());
//                if (null != oldData) {
//                    // fld_pay_mode_guid as voucher_fld_pay_mode_guid
//                    allData.setBack_fld_incoming_kou_guid(oldData.getFld_incoming_kou_guid());
//                    allData.setFld_eci_kou_guid(oldData.getFld_guid());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeIncomingBackDomain EsChargeIncomingBackDomain) throws Exception {
//                // --该语句可能重复，需要分组(根据fld_incoming_back_guid分组，取fld_submit_time desc最近的一笔)
//                return null;
//            }
//        });
//        // es_charge_voucher_mast_refund
//        SingleOutputStreamOperator<EsChargeVoucherMastRefundDomain> es_charge_voucher_mast_refund_stream_map = Util.getKafkaSource(env, "es_charge_voucher_mast_refund").map(new EsChargeVoucherMastRefundMapFunction());
//
//        ConnectedStreams<AllData, EsChargeVoucherMastRefundDomain> join25 = join24Done.connect(es_charge_voucher_mast_refund_stream_map);
//        SingleOutputStreamOperator<AllData> join25Done = join25.keyBy(t1 -> t1.getFld_eci_back_guid(), t2 -> t2.getFld_incoming_back_guid()).map(new RichCoMapFunction<AllData, EsChargeVoucherMastRefundDomain, AllData>() {
//            MapState<String, EsChargeVoucherMastRefundDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeVoucherMastRefundDomain>("map_stat", String.class, EsChargeVoucherMastRefundDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeVoucherMastRefundDomain oldData = mapState.get(allData.getFld_eci_back_guid());
//                if (oldData != null) {
//                    allData.setRefund_fld_date_back(oldData.getFld_date());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeVoucherMastRefundDomain esChargeVoucherMastRefundDomain) throws Exception {
//                // --该语句可能重复，需要分组（根据fld_incoming_back_guid，取fld_appay_date desc最近的一笔
//                return null;
//            }
//        });
//        //
//        ConnectedStreams<AllData, EsChargeVoucherMastRefundDomain> join26 = join25Done.connect(es_charge_voucher_mast_refund_stream_map);
//        SingleOutputStreamOperator<AllData> join26Done = join26.keyBy(t1 -> t1.getFld_eci_convert_guid(), t2 -> t2.getFld_incoming_back_guid()).map(new RichCoMapFunction<AllData, EsChargeVoucherMastRefundDomain, AllData>() {
//            MapState<String, EsChargeVoucherMastRefundDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeVoucherMastRefundDomain>("map_stat", String.class, EsChargeVoucherMastRefundDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeVoucherMastRefundDomain oldData = mapState.get(allData.getFld_eci_convert_guid());
//                if (oldData != null) {
//                    allData.setRefund_fld_date_convert(oldData.getFld_date());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeVoucherMastRefundDomain esChargeVoucherMastRefundDomain) throws Exception {
//                // --该语句可能重复，需要分组（根据fld_incoming_back_guid，取fld_appay_date desc最近的一笔
//                return null;
//            }
//        });
//
////
//
//        ConnectedStreams<AllData, EsChargeVoucherMastRefundDomain> join27 = join26Done.connect(es_charge_voucher_mast_refund_stream_map);
//        SingleOutputStreamOperator<AllData> join27Done = join27.keyBy(t1 -> t1.getFld_eci_kou_guid(), t2 -> t2.getFld_incoming_back_guid()).map(new RichCoMapFunction<AllData, EsChargeVoucherMastRefundDomain, AllData>() {
//            MapState<String, EsChargeVoucherMastRefundDomain> mapState;
//
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                RuntimeContext runtimeContext = getRuntimeContext();
//                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeVoucherMastRefundDomain>("map_stat", String.class, EsChargeVoucherMastRefundDomain.class));
//            }
//
//            @Override
//            public AllData map1(AllData allData) throws Exception {
//                EsChargeVoucherMastRefundDomain oldData = mapState.get(allData.getFld_eci_kou_guid());
//                if (oldData != null) {
//                    allData.setRefund_fld_date_kou(oldData.getFld_date());
//                }
//                return allData;
//            }
//
//            @Override
//            public AllData map2(EsChargeVoucherMastRefundDomain esChargeVoucherMastRefundDomain) throws Exception {
//                // --该语句可能重复，需要分组（根据fld_incoming_back_guid，取fld_appay_date desc最近的一笔
//                return null;
//            }
//        });
//
//    }
//}
