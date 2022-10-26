package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunac.domain.EsChargeOwnerFee;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.flink.api.common.functions.MapFunction;

import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/9/8
 */
public class EsChargeOwnerFeeMapFunction implements MapFunction<String, EsChargeOwnerFee> {
    @Override
    public EsChargeOwnerFee map(String s) throws Exception {
//        fld_guid
//        fld_area_guid
//        fld_reason_remark,
//        fld_price,
//        fld_rebate,
//        fld_resource AS fld_owner_fee_resource,
//        fld_desc AS fld_owner_fee_desc,
//        fld_adjust_guid as fld_fee_adjust_guid,
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeOwnerFee esChargeOwnerFee = new EsChargeOwnerFee();
        if ("INSERT".equals(optType)) {
            esChargeOwnerFee.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            String fld_guid = objArray.getString("fld_guid");
            String fld_area_guid = objArray.getString("fld_area_guid");
            String pk = DigestUtils.md5Hex(fld_guid + fld_area_guid);
            esChargeOwnerFee.setPk(pk);
            esChargeOwnerFee.setFld_guid(fld_guid);
            esChargeOwnerFee.setFld_area_guid(fld_area_guid);
            esChargeOwnerFee.setFld_reason_remark(objArray.getString("fld_reason_remark"));
            esChargeOwnerFee.setFld_price(objArray.getString("fld_price"));
            esChargeOwnerFee.setFld_rebate(new BigDecimal(objArray.getString("fld_rebate")));
            esChargeOwnerFee.setFld_resource(objArray.getString("fld_resource"));
            esChargeOwnerFee.setFld_desc(objArray.getString("fld_desc"));
            esChargeOwnerFee.setFld_adjust_guid(objArray.getString("fld_adjust_guid"));
        }
        return esChargeOwnerFee;
    }
}
