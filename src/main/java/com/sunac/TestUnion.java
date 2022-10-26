//package com.sunac;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sunac.domain.AllData;
//import com.sunac.domain.CommonDomain;
//import com.sunac.domain.EsChargeBill;
//import com.sunac.map.CommonMapFunction;
//import com.sunac.utils.Util;
//import org.apache.flink.api.common.functions.RichMapFunction;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.ProcessFunction;
//import org.apache.flink.util.Collector;
//import org.apache.flink.util.OutputTag;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac
// * @date:2022/9/21
// */
//public class TestUnion {
//    public static void main(String[] args) throws Exception {
//        Configuration configuration = new Configuration();
//        configuration.setInteger("rest.port", 8822);
//
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
//        SingleOutputStreamOperator<String> processedConnectStream = Util.getKafkaSource(env, "lvzhitao_es_charge_other_2").process(new ProcessFunction<String, String>() {
//            @Override
//            public void processElement(String s, Context ctx, Collector<String> collector) throws Exception {
//                JSONObject jsonObject = JSON.parseObject(s);
//                String table = jsonObject.getString("table");
//                if ("es_charge_incoming_data_bill".equals(table)) {
//                    ctx.output(new OutputTag<JSONObject>("es_charge_incoming_data_bill", TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if ("es_charge_bill".equals(table)) {
//                    ctx.output(new OutputTag<JSONObject>("es_charge_bill", TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//                if ("es_charge_bill_type".equals(table)) {
//                    ctx.output(new OutputTag<JSONObject>("es_charge_bill_type", TypeInformation.of(JSONObject.class)), jsonObject);
//                }
//            }
//        });
//
//        DataStream<CommonDomain> EsChargeBill = processedConnectStream.getSideOutput(new OutputTag<JSONObject>("es_charge_bill", TypeInformation.of(JSONObject.class))).map(new CommonMapFunction<CommonDomain>(Config.EsChargeBill)).returns(CommonDomain.class);
//
//        DataStream<CommonDomain> EsChargeBillType = processedConnectStream.getSideOutput(new OutputTag<JSONObject>("es_charge_bill_type", TypeInformation.of(JSONObject.class))).map(new CommonMapFunction<CommonDomain>(Config.EsChargeBillType)).returns(CommonDomain.class);
//
//        DataStream<CommonDomain> EsChargeIncomingDataBill = processedConnectStream.getSideOutput(new OutputTag<JSONObject>("es_charge_incoming_data_bill", TypeInformation.of(JSONObject.class))).map(new CommonMapFunction<CommonDomain>(Config.EsChargeIncomingDataBill)).returns(CommonDomain.class);
//
//        SingleOutputStreamOperator<AllData> map = EsChargeIncomingDataBill.union(EsChargeBill, EsChargeBillType).map(new RichMapFunction<CommonDomain, AllData>() {
//            AllData allData;
//            @Override
//            public void open(Configuration parameters) throws Exception {
//                allData = new AllData();
//            }
//            @Override
//            public AllData map(CommonDomain commonDomain) throws Exception {
//                String name = commonDomain.getClass().getName();
//                System.out.println("=-======================" + name);
//                allData.setOperation_type(name);
//                return allData;
//            }
//        });
//        map.print();
//        env.execute();
//    }
//}
