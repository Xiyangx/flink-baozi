package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunac.domain.EsChargeVoucherMastRefundDomain;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/9/13
 */
public class EsChargeVoucherMastRefundMapFunction implements MapFunction<String, EsChargeVoucherMastRefundDomain> {
    @Override
    public EsChargeVoucherMastRefundDomain map(String s) throws Exception {
        //fld_date
        //fld_incoming_back_guid
        //fld_appay_date desc
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeVoucherMastRefundDomain esChargeVoucherMastRefundDomain = new EsChargeVoucherMastRefundDomain();
        if ("INSERT".equals(optType)) {
            esChargeVoucherMastRefundDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeVoucherMastRefundDomain.setFld_date(objArray.getString("fld_date"));
            esChargeVoucherMastRefundDomain.setFld_incoming_back_guid(objArray.getString("fld_incoming_back_guid"));
            esChargeVoucherMastRefundDomain.setFld_appay_date(objArray.getString("fld_appay_date"));
        }
        return esChargeVoucherMastRefundDomain;
    }
}

