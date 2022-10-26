package com.sunac;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.OutputTag;


/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac
 * @date:2022/9/26
 */
public class Constant {

    public static String ES_INFO_AREA_INFO = "es_info_area_info";
    public static String ES_CHARGE_PROJECT = "es_charge_project";
    public static String ES_CHARGE_PROJECT_PERIOD_JOIN = "es_charge_project_period_join";
    public static String ES_CHARGE_PROJECT_PERIOD = "es_charge_project_period";
    public static String ES_INFO_OWNER = "es_info_owner";
    public static String ES_INFO_OBJECT_AND_OWNER = "es_info_object_and_owner";
    public static String ES_INFO_OBJECT_CLASS = "es_info_object_class";
    public static String ES_CHARGE_SETTLE_ACCOUNTS_DETAIL = "es_charge_settle_accounts_detail";
    public static String ES_CHARGE_SETTLE_ACCOUNTS_MAIN = "es_charge_settle_accounts_main";
    public static String ES_CHARGE_INCOMING_DATA = "es_charge_incoming_data";
    public static String ES_CHARGE_INCOMING_FEE = "es_charge_incoming_fee";
    public static String ES_CHARGE_FEE = "es_charge_fee";
    public static String ES_CHARGE_RATE_RESULT = "es_charge_rate_result";
    public static String ES_CHARGE_TICKET_PAY_DETAIL = "es_charge_ticket_pay_detail";
    public static String ES_CHARGE_TICKET_PAY_OPERATE = "es_charge_ticket_pay_operate";
    public static String ES_COMMERCE_BOND_FEE_OBJECT = "es_commerce_bond_fee_object";
    public static String ES_COMMERCE_BOND_MAIN = "es_commerce_bond_main";
    public static String ES_INFO_PARAM_INFO = "es_info_param_info";
    public static String ES_INFO_OBJECT = "es_info_object";


    public static OutputTag<Tuple2<String, String>> SIDE_STREAM_TAG = new OutputTag<Tuple2<String, String>>(Config.SIDE_STREAM, TypeInformation.of(new TypeHint<Tuple2<String, String>>() {
    }));
    public static OutputTag<Tuple2<String, String>> ALL_DATA_STREAM_TAG = new OutputTag<Tuple2<String, String>>(Config.AllData, TypeInformation.of(new TypeHint<Tuple2<String, String>>() {
    }));
    public static OutputTag<JSONObject> ES_INFO_AREA_INFO_TAG = new OutputTag<JSONObject>(ES_INFO_AREA_INFO) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_PROJECT_TAG = new OutputTag<JSONObject>(ES_CHARGE_PROJECT) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_PROJECT_PERIOD_JOIN_TAG = new OutputTag<JSONObject>(ES_CHARGE_PROJECT_PERIOD_JOIN) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_PROJECT_PERIOD_TAG = new OutputTag<JSONObject>(ES_CHARGE_PROJECT_PERIOD) {
    };
    public static OutputTag<JSONObject> ES_INFO_OWNER_TAG = new OutputTag<JSONObject>(ES_INFO_OWNER) {
    };
    public static OutputTag<JSONObject> ES_INFO_OBJECT_AND_OWNER_TAG = new OutputTag<JSONObject>(ES_INFO_OBJECT_AND_OWNER) {
    };
    public static OutputTag<JSONObject> ES_INFO_OBJECT_CLASS_TAG = new OutputTag<JSONObject>(ES_INFO_OBJECT_CLASS) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_SETTLE_ACCOUNTS_DETAIL_TAG = new OutputTag<JSONObject>(ES_CHARGE_SETTLE_ACCOUNTS_DETAIL) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_SETTLE_ACCOUNTS_MAIN_TAG = new OutputTag<JSONObject>(ES_CHARGE_SETTLE_ACCOUNTS_MAIN) {
    };
    //
    public static OutputTag<JSONObject> ES_CHARGE_INCOMING_DATA_TAG = new OutputTag<JSONObject>(ES_CHARGE_INCOMING_DATA) {
    };
    //
    public static OutputTag<JSONObject> ES_CHARGE_INCOMING_FEE_TAG = new OutputTag<JSONObject>(ES_CHARGE_INCOMING_FEE) {
    };
    //
    public static OutputTag<JSONObject> ES_CHARGE_FEE_TAG = new OutputTag<JSONObject>(ES_CHARGE_FEE) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_RATE_RESULT_TAG = new OutputTag<JSONObject>(ES_CHARGE_RATE_RESULT) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_TICKET_PAY_DETAIL_TAG = new OutputTag<JSONObject>(ES_CHARGE_TICKET_PAY_DETAIL) {
    };
    public static OutputTag<JSONObject> ES_CHARGE_TICKET_PAY_OPERATE_TAG = new OutputTag<JSONObject>(ES_CHARGE_TICKET_PAY_OPERATE) {
    };
    public static OutputTag<JSONObject> ES_COMMERCE_BOND_FEE_OBJECT_TAG = new OutputTag<JSONObject>(ES_COMMERCE_BOND_FEE_OBJECT) {
    };
    public static OutputTag<JSONObject> ES_COMMERCE_BOND_MAIN_TAG = new OutputTag<JSONObject>(ES_COMMERCE_BOND_MAIN) {
    };
    public static OutputTag<JSONObject> ES_INFO_PARAM_INFO_TAG = new OutputTag<JSONObject>(ES_INFO_PARAM_INFO) {
    };
    //
    public static OutputTag<JSONObject> ES_INFO_OBJECT_TAG = new OutputTag<JSONObject>(ES_INFO_OBJECT) {
    };
}
