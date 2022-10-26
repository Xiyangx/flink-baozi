//package com.sunac.map;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sunac.Config;
//import com.sunac.domain.EsInfoAreaInfo;
//import com.sunac.domain.EsInfoObject;
//import com.sunac.utils.Util;
//import org.apache.flink.api.common.functions.RichMapFunction;
//import org.apache.flink.configuration.Configuration;
//
//import java.util.ArrayList;
//
///**
// * @author: create by Lantian
// * @version: v1.0
// * @description: com.sunac.map
// * @date:2022/9/15
// */
//public class EsInfoAreaMapFunction extends RichMapFunction<JSONObject, EsInfoAreaInfo> {
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
//    public EsInfoAreaInfo map(JSONObject jsonObject) throws Exception {
////                fld_guid
////                fld_name AS fld_area_name,
////                fld_dq,
////                fld_fee_type,
////                fld_confirm_date,
////                fld_company,
////                fld_xm,
////                fld_ywdy,
////                fld_yt,
//
//        list.clear();
//        listValues.clear();
//
//        String optType = jsonObject.getString("type");
//        EsInfoAreaInfo esInfoAreaInfo = new EsInfoAreaInfo();
//        JSONObject newArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
//
//        if (Config.INSERT.equals(optType)) {
//            esInfoAreaInfo = Util.getEsInfoAreaInfoObj(esChargeIncomingFee, newArray);
//
//            esInfoAreaInfo.setOperation_type("INSERT");
//            esInfoAreaInfo.setFld_guid(objArray.getString("fld_guid"));
//            esInfoAreaInfo.setFld_area_name(objArray.getString("fld_name"));
//            esInfoAreaInfo.setFld_dq(objArray.getString("fld_dq"));
//            esInfoAreaInfo.setFld_fee_type(objArray.getString("fld_fee_type"));
//            esInfoAreaInfo.setFld_confirm_date(objArray.getString("fld_confirm_date"));
//            esInfoAreaInfo.setFld_company(objArray.getString("fld_company"));
//            esInfoAreaInfo.setFld_xm(objArray.getString("fld_xm"));
//            esInfoAreaInfo.setFld_ywdy(objArray.getString("fld_ywdy"));
//            esInfoAreaInfo.setFld_yt(objArray.getString("fld_yt"));
//
//        }
//        return esInfoAreaInfo;
//        return null;
//    }
//}
