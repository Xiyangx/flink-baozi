package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunac.Config;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import static com.sunac.Constant.*;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/12 10:46 上午
 * @Version 1.0
 */
public class MySideStreamProcessFunction1 extends ProcessFunction<String, String> {

    @Override
    public void processElement(String s, ProcessFunction<String, String>.Context ctx, Collector<String> collector) throws Exception {
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(s);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String table = jsonObject.getString(Config.TABLE);
        if (ES_INFO_AREA_INFO.equals(table)) {
            ctx.output(ES_INFO_AREA_INFO_TAG, jsonObject);
        } else if (ES_CHARGE_PROJECT.equals(table)) {
            ctx.output(ES_CHARGE_PROJECT_TAG, jsonObject);
        } else if (ES_CHARGE_RATE_RESULT.equals(table)) {
            ctx.output(ES_CHARGE_RATE_RESULT_TAG, jsonObject);
        } else if (ES_CHARGE_PROJECT_PERIOD_JOIN.equals(table)) {
            ctx.output(ES_CHARGE_PROJECT_PERIOD_JOIN_TAG, jsonObject);
        } else if (ES_CHARGE_PROJECT_PERIOD.equals(table)) {
            ctx.output(ES_CHARGE_PROJECT_PERIOD_TAG, jsonObject);
        } else if (ES_COMMERCE_BOND_FEE_OBJECT.equals(table)) {
            ctx.output(ES_COMMERCE_BOND_FEE_OBJECT_TAG, jsonObject);
        } else if (ES_COMMERCE_BOND_MAIN.equals(table)) {
            ctx.output(ES_COMMERCE_BOND_MAIN_TAG, jsonObject);
        } else if (ES_INFO_PARAM_INFO.equals(table)) {
            ctx.output(ES_INFO_PARAM_INFO_TAG, jsonObject);
        }
    }
}
