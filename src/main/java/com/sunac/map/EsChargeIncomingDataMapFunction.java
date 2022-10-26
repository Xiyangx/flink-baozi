//package com.sunac.map;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sunac.Config;
//import com.sunac.domain.AllData;
//import com.sunac.utils.JdbcUtils;
//import com.sunac.utils.Util;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.state.BroadcastState;
//import org.apache.flink.api.common.state.MapStateDescriptor;
//import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
//import org.apache.flink.util.Collector;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Set;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac.map
// * @date:2022/9/13
// */
//public class EsChargeIncomingDataMapFunction extends BroadcastProcessFunction<String, Tuple2<String, Set<String>>, AllData> {
//    MapStateDescriptor<String, Tuple2<String, Set<String>>> userInfoStateDesc;
//    public String tableName;
//
//    public EsChargeIncomingDataMapFunction(String tableName) {
//        this.tableName = tableName;
//    }
//
//    @Override
//    public void open(Configuration parameters) throws Exception {
//        super.open(parameters);
//        userInfoStateDesc = Config.userInfoStateDesc;
//    }
//
//    @Override
//    public void processElement(String s, ReadOnlyContext context, Collector<AllData> collector) throws Exception {
//        // 获取关心的字段set
//        Tuple2<String, Set<String>> es_charge_incoming_data = context.getBroadcastState(Config.userInfoStateDesc).get(this.tableName);
//        Set<String> needColSets = es_charge_incoming_data.f1;
//        // 解析json字符串
//        JSONObject jsonObject = JSON.parseObject(s);
//        String optType = jsonObject.getString("type");
//        // 传递下去的allData
//        AllData allData = new AllData();
//        allData.setOperation_type(Config.PASS);
//        JSONObject newArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
//
//        if (Config.INSERT.equals(optType)) {
//            allData.setOperation_type(Config.INSERT);
//            allData = Util.getAllDataObj(allData, newArray);
//            JdbcUtils.insertAllData(allData);
//        } else if (Config.DELETE.equals(optType)) {
//            // TODO:按照主键直接删除宽表就行了
//            String fld_guid = newArray.getString("fld_guid");
//            allData.setFld_guid(fld_guid);
//            JdbcUtils.deleteAllData(allData);
//        } else if (Config.UPDATE.equals(optType)) {
//            JSONObject oldArray = (JSONObject) jsonObject.getJSONArray("old").get(0);
//            for (String needKey : needColSets) {
//                String oldVlaue = oldArray.getString(needKey);
//                String newValue = newArray.getString(needKey);
//                if (!oldVlaue.equals(newValue)) {
//                    // TODO:变化的值：
//                    allData.setOperation_type(Config.INSERT);
//                    allData = Util.getAllDataObj(allData, newArray);
//                    JdbcUtils.insertAllData(allData);
//                    break;
//                }
//            }
//        }
//        collector.collect(allData);
//    }
//
//    @Override
//    public void processBroadcastElement(Tuple2<String, Set<String>> tp, Context context, Collector<AllData> collector) throws Exception {
//        BroadcastState<String, Tuple2<String, Set<String>>> broadcastState = context.getBroadcastState(userInfoStateDesc);
//        broadcastState.put(tp.f0, tp);
//    }
//}
