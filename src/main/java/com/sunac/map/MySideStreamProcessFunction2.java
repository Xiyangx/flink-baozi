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
 * @Date 2022/10/12 10:53 上午
 * @Version 1.0
 */
public class MySideStreamProcessFunction2 extends ProcessFunction<String, String> {
    @Override
    public void processElement(String s, Context ctx, Collector<String> collector) throws Exception {
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(s);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String table = jsonObject.getString(Config.TABLE);
        if (ES_INFO_OWNER.equals(table)) {
            ctx.output(ES_INFO_OWNER_TAG, jsonObject);
        } else if (ES_INFO_OBJECT_CLASS.equals(table)) {
            ctx.output(ES_INFO_OBJECT_CLASS_TAG, jsonObject);
        } else if (ES_INFO_OBJECT_AND_OWNER.equals(table)) {
            ctx.output(ES_INFO_OBJECT_AND_OWNER_TAG, jsonObject);
        } else if (ES_CHARGE_SETTLE_ACCOUNTS_DETAIL.equals(table)) {
            ctx.output(ES_CHARGE_SETTLE_ACCOUNTS_DETAIL_TAG, jsonObject);
        } else if (ES_CHARGE_SETTLE_ACCOUNTS_MAIN.equals(table)) {
            ctx.output(ES_CHARGE_SETTLE_ACCOUNTS_MAIN_TAG, jsonObject);
        } else if (ES_CHARGE_TICKET_PAY_DETAIL.equals(table)) {
            ctx.output(ES_CHARGE_TICKET_PAY_DETAIL_TAG, jsonObject);
        } else if (ES_CHARGE_TICKET_PAY_OPERATE.equals(table)) {
            ctx.output(ES_CHARGE_TICKET_PAY_OPERATE_TAG, jsonObject);
        }
    }
}

