package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/10/6
 */
public class MyJsonStringProcessFunction extends ProcessFunction<String, JSONObject> {
    private static final Logger log = LoggerFactory.getLogger(MyJsonStringProcessFunction.class);

    @Override
    public void processElement(String s, Context context, Collector<JSONObject> collector) throws Exception {
        JSONObject jsonObject = null;
        boolean isFire = false;
        try {
            jsonObject = JSON.parseObject(s);
            String table = jsonObject.getString("table");
            JSONObject newArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            // 大于2022-11-16  20:00:00，用的是fld_modify_date
            int i = 0;
            try {
                i = newArray.getString("fld_modify_date").compareTo("2022-11-16  20:00:00");
            } catch (Exception e) {
                log.error("==============================Table:" + table + "newArray.getString(\"fld_modify_date\")报错！！！=================================");
                e.printStackTrace();
                log.error("==============================Table:" + table + "newArray.getString(\"fld_modify_date\")报错！！！=================================");
                return;
            }
            if (i < 1) {
                return;
            }
            switch (table) {
                case "es_charge_owner_fee":
                    isFire = isSuccessFire(
                            newArray.getString("fld_guid"),
                            newArray.getString("fld_area_guid"),
                            newArray.getString("fld_object_guid"),
                            newArray.getString("fld_owner_guid"),
                            newArray.getString("fld_project_guid")
                    );
                    break;
                case "es_info_owner":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_info_param_info":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_info_area_info":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_charge_project":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_charge_rate_result":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_area_guid"), newArray.getString("fld_project_guid"));
                    break;
                case "es_info_object_class":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_info_object_and_owner":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_object_guid"), newArray.getString("fld_owner_guid"), newArray.getString("fld_area_guid"));
                    break;
                case "es_charge_project_period_join":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_project_guid"));
                    break;
                case "es_charge_project_period":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_charge_settle_accounts_detail":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_area_guid"));
                    break;
                case "es_charge_settle_accounts_main":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_area_guid"));
                    break;
                case "es_charge_ticket_pay_detail":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_owner_fee_guid"));
                    break;
                case "es_charge_ticket_pay_operate":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_commerce_bond_main":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_area_guid"), newArray.getString("fld_owner_guid"));
                    break;
                case "es_commerce_bond_fee_object":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_bond_guid"), newArray.getString("fld_object_guid"));
                    break;
                case "es_charge_incoming_fee":
                    isFire = isSuccessFire(newArray.getString("fld_guid"));
                    break;
                case "es_charge_incoming_data":
                    isFire = isSuccessFire(
                            newArray.getString("fld_guid"),
                            newArray.getString("fld_incoming_fee_guid"),
                            newArray.getString("fld_owner_guid"));
                    break;
                case "es_info_object":
                    isFire = isSuccessFire(newArray.getString("fld_guid"), newArray.getString("fld_class_guid"));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.toString());
            return;
        }
        if (isFire) {
            collector.collect(jsonObject);
        }
    }

    public static boolean isSuccessFire(String... s) {
        for (String val : s) {
            if (StringUtils.isBlank(val)) {
                return false;
            }
        }
        return true;
    }
}
