//package com.sunac;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sunac.comap.*;
//import com.sunac.domain.*;
//import com.sunac.map.CommonMapFunction;
//import com.sunac.utils.MySqlTwoPhaseNewCommitSink;
//import com.sunac.utils.Util;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.typeinfo.TypeHint;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.java.tuple.Tuple3;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
//import org.apache.flink.streaming.api.CheckpointingMode;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
//import org.apache.flink.streaming.api.environment.CheckpointConfig;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.ProcessFunction;
//import org.apache.flink.util.Collector;
//import org.apache.flink.util.OutputTag;
//
//import java.time.Duration;
//
//import static com.sunac.Constant.*;
//import static com.sunac.Constant.ES_CHARGE_VOUCHER_PROJECT_PAY;
//import static com.sunac.Topic.*;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac
// * @date:2022/9/29
// */
//public class Test1030 {
//    public static void main(String[] args) throws Exception {
//        Configuration configuration = new Configuration();
//        configuration.setInteger("rest.port", 8822);
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
//        env.setParallelism(1);
//
//    /*    env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);  // 传入两个最基本ck参数：间隔时长，ck模式
//        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
//        checkpointConfig.setCheckpointStorage("file:///D://flink-baozi//src//main//java//baozi_ck");
//        env.setStateBackend(new HashMapStateBackend());
//        checkpointConfig.setAlignedCheckpointTimeout(Duration.ofMinutes(10000)); // 设置ck对齐的超时时长
//        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);  // 设置ck算法模式
//        checkpointConfig.setCheckpointInterval(2000); // ck的间隔时长
//        //checkpointConfig.setCheckpointIdOfIgnoredInFlightData(5); // 用于非对齐算法模式下，在job恢复时让各个算子自动抛弃掉ck-5中飞行数据
//        checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);  // job cancel调时，保留最后一次ck数据
////*/
//
//        SingleOutputStreamOperator<AllData> join2Done = Util.getKafkaSource(env, LVZHITAO).map(JSON::parseObject).map(new CommonMapFunction<AllData>(Config.AllData)).returns(AllData.class);
//        // 2,es_info_area_info:围合资料表:ON a.fld_guid = d.fld_area_guid
//        SingleOutputStreamOperator<String> processed = Util.getKafkaSource(env, LVZHITAO_ES_CHARGE_OTHER).process(new ProcessFunction<String, String>() {
//            @Override
//            public void processElement(String s, Context ctx, Collector<String> collector) throws Exception {
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = JSON.parseObject(s);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//                String table = jsonObject.getString(Config.TABLE);
//                if (ES_INFO_AREA_INFO.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_INFO_AREA_INFO, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_PROJECT.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_PROJECT, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_PAY_MODE.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_PAY_MODE, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_INCOMING_BACK.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_INCOMING_BACK, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_PROJECT_PERIOD_JOIN.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_PROJECT_PERIOD_JOIN, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_PROJECT_PERIOD.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_PROJECT_PERIOD, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_TWO_BALANCE.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_TWO_BALANCE, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_HAND_IN_RECORD.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_HAND_IN_RECORD, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_VOUCHER_CHECK_SERVICE_SET.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_VOUCHER_CHECK_SERVICE_SET, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_VOUCHER_PROJECT_PAY.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_VOUCHER_PROJECT_PAY, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//            }
//        });
//        SingleOutputStreamOperator<String> processedConnectStream = Util.getKafkaSource(env, LVZHITAO_ES_CHARGE_OTHER_2).process(new ProcessFunction<String, String>() {
//            @Override
//            public void processElement(String s, Context ctx, Collector<String> collector) {
//                JSONObject jsonObject;
//                try {
//                    jsonObject = JSON.parseObject(s);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return;
//                }
//                String table = jsonObject.getString(Config.TABLE);
//                if (ES_INFO_OWNER.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_INFO_OWNER, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_INFO_OBJECT_PARK.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_INFO_OBJECT_PARK, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_INFO_OBJECT_AND_OWNER.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_INFO_OBJECT_AND_OWNER, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_BILL.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_BILL, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_BILL_TYPE.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_BILL_TYPE, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_INFO_OBJECT_CLASS.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_INFO_OBJECT_CLASS, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//
//                if (ES_CHARGE_SETTLE_ACCOUNTS_DETAIL.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_SETTLE_ACCOUNTS_DETAIL, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if (ES_CHARGE_SETTLE_ACCOUNTS_MAIN.equals(table)) {
//                    ctx.output(new OutputTag<>(ES_CHARGE_SETTLE_ACCOUNTS_MAIN, TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//            }
//        });
//        // 7,es_charge_owner_fee:应收欠费明细 topic=es_charge_owner_fee_2
//        SingleOutputStreamOperator<EsChargeOwnerFee> es_charge_owner_fee_2_stream_map = Util.getKafkaSource(env, LVZHITAO_ES_CHARGE_OWNER_FEE_2)
//                .map(JSON::parseObject)
//                .map(new CommonMapFunction<EsChargeOwnerFee>(Config.EsChargeOwnerFee))
//                .returns(EsChargeOwnerFee.class);
//        SingleOutputStreamOperator<AllData> join7Done = join2Done
//                .connect(es_charge_owner_fee_2_stream_map).
//                        keyBy(t1 -> DigestUtils.md5Hex(t1.getFld_owner_fee_guid() + t1.getFld_area_guid()), EsChargeOwnerFee::getPk)
//                .process(new EsChargeOwnerFeeCoMapFunction());
//        join7Done.getSideOutput(new OutputTag<>(Config.SIDE_STREAM,
//                TypeInformation.of(new TypeHint<Tuple3<String, String, CommonDomain>>() {})))
//                .addSink(new MySqlTwoPhaseNewCommitSink());
//
//
//        env.execute();
//    }
//}
