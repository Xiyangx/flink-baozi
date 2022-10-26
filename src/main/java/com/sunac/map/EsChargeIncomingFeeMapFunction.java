//package com.sunac.map;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sunac.Config;
//import com.sunac.domain.AllData;
//import com.sunac.domain.EsChargeIncomingFee;
//import com.sunac.domain.EsChargeOwnerFee;
//import com.sunac.utils.JdbcUtils;
//import com.sunac.utils.Util;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.common.functions.RichMapFunction;
//import org.apache.flink.configuration.Configuration;
//
//import java.util.ArrayList;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac.map
// * @date:2022/9/8
// */
//public class EsChargeIncomingFeeMapFunction extends RichMapFunction<String, EsChargeIncomingFee> {
//    ArrayList<String> list;
//    ArrayList<String> listValues;
//
//    @Override
//    public void open(Configuration parameters) throws Exception {
//        list = new ArrayList<>();
//        listValues = new ArrayList<>();
//    }
//
//    @Override
//    public EsChargeIncomingFee map(String s) throws Exception {
////                fld_guid
////                fld_busi_type,
////                fld_remark,
////                fld_cancel_date,
////                fld_cancel_guid,
////                fld_number,
////                fld_checkin,
////                fld_cancel_me,
////                fld_modify_date,
//
//        list.clear();
//        listValues.clear();
//
//        JSONObject jsonObject = JSON.parseObject(s);
//        String optType = jsonObject.getString("type");
//        EsChargeIncomingFee esChargeIncomingFee = new EsChargeIncomingFee();
//        JSONObject newArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
//        esChargeIncomingFee = Util.getEsChargeIncomingFeeObj(esChargeIncomingFee, newArray);
//
//
//        if (Config.INSERT.equals(optType)) {
//            esChargeIncomingFee.setOperation_type(Config.INSERT);
//
//            for (String needKey : Config.ES_CHARGE_INCOMING_FEE_LIST) {
//                String newValue = newArray.getString(needKey);
//                list.add(needKey);
//                listValues.add(newValue);
//            }
//            // 记录变更的字段
//            esChargeIncomingFee.setUpdateColumns(list);
//            esChargeIncomingFee.setUpdateColumnVlues(listValues);
//
//        } else if (Config.DELETE.equals(optType)) {
//            esChargeIncomingFee.setOperation_type(Config.DELETE);
//            // TODO:
//        } else if (Config.UPDATE.equals(optType)) {
//            esChargeIncomingFee.setOperation_type(Config.PASS);
//            JSONObject oldArray = (JSONObject) jsonObject.getJSONArray("old").get(0);
//            for (String needKey : Config.ES_CHARGE_INCOMING_FEE_LIST) {
//                String oldVlaue = oldArray.getString(needKey);
//                String newValue = newArray.getString(needKey);
//
//                if (!newValue.equals(oldVlaue)) {
//                    // 如果有变更的关心的字段，就是update事件，否则就是pass事件
//                    esChargeIncomingFee.setOperation_type(Config.UPDATE);
//                    list.add(needKey);
//                    listValues.add(newValue);
//                }
//            }
//            // 记录变更的字段
//            if (list.size() != 0) {
//                esChargeIncomingFee.setUpdateColumns(list);
//                esChargeIncomingFee.setUpdateColumnVlues(listValues);
//            }
//        }
//        return esChargeIncomingFee;
//    }
//}
