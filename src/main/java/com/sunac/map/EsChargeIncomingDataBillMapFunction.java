package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunac.domain.EsChargeIncomingDataBill;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/9/9
 */
public class EsChargeIncomingDataBillMapFunction implements MapFunction<String, EsChargeIncomingDataBill> {
    @Override
    public EsChargeIncomingDataBill map(String s) throws Exception {
        //fld_bill_guid
        //fld_data_src_guid
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeIncomingDataBill esChargeIncomingDataBill = new EsChargeIncomingDataBill();
        if ("INSERT".equals(optType)) {
            esChargeIncomingDataBill.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeIncomingDataBill.setFld_bill_guid(objArray.getString("fld_bill_guid"));
            esChargeIncomingDataBill.setFld_data_src_guid(objArray.getString("fld_data_src_guid"));
        }
        return esChargeIncomingDataBill;
    }
}
