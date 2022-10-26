package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunac.entity.EsChargeOwnerFee;
import org.apache.flink.api.common.functions.MapFunction;

import java.math.BigDecimal;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/8 10:19 上午
 * @Version 1.0
 */
public class AllDataMapFunction1 implements MapFunction<String,EsChargeOwnerFee> {
    @Override
    public EsChargeOwnerFee map(String s) throws Exception {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeOwnerFee esChargeOwnerFee = new EsChargeOwnerFee();
        if ("INSERT".equals(optType)) {
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeOwnerFee.setFld_guid(objArray.getString("fld_guid"));
            esChargeOwnerFee.setFld_create_user(objArray.getString("fld_create_user"));
            esChargeOwnerFee.setFld_create_date(objArray.getString("fld_create_date"));
            esChargeOwnerFee.setFld_modify_user(objArray.getString("fld_modify_user"));
            esChargeOwnerFee.setFld_modify_date(objArray.getString("fld_modify_date"));
            esChargeOwnerFee.setFld_tenancy(objArray.getString("fld_tenancy"));
            esChargeOwnerFee.setFld_area_guid(objArray.getString("fld_area_guid"));
            esChargeOwnerFee.setFld_area_name(objArray.getString("fld_area_name"));
            esChargeOwnerFee.setFld_adjust_guid(objArray.getString("fld_adjust_guid"));
            esChargeOwnerFee.setFld_object_guid(objArray.getString("fld_object_gui"));
            esChargeOwnerFee.setFld_object_name(objArray.getString("fld_object_name"));
            esChargeOwnerFee.setFld_owner_guid(objArray.getString("fld_owner_guid"));
            esChargeOwnerFee.setFld_owner_name(objArray.getString("fld_owner_name"));
            esChargeOwnerFee.setFld_project_guid(objArray.getString("fld_project_guid"));
            esChargeOwnerFee.setFld_project_name(objArray.getString("fld_project_name"));
            esChargeOwnerFee.setFld_project_type(objArray.getInteger("fld_project_type"));
            esChargeOwnerFee.setFld_total(new BigDecimal(objArray.getString("fld_total")));
            esChargeOwnerFee.setFld_left_total(new BigDecimal(objArray.getString("fld_left_total")));
            esChargeOwnerFee.setFld_amount(new BigDecimal(objArray.getString("fld_amount")));
            esChargeOwnerFee.setFld_rebate(new BigDecimal(objArray.getString("fld_rebate")));
            esChargeOwnerFee.setFld_late_total(new BigDecimal(objArray.getString("fld_late_total")));
            esChargeOwnerFee.setFld_late_fee(new BigDecimal(objArray.getString("fld_late_fee")));
            esChargeOwnerFee.setFld_desc(objArray.getString("fld_desc"));
            esChargeOwnerFee.setFld_late_stop(objArray.getInteger("fld_late_stop"));
            esChargeOwnerFee.setFld_desc(objArray.getString("fld_desc"));
            esChargeOwnerFee.setFld_owner_date(objArray.getString("fld_owner_date"));
            esChargeOwnerFee.setFld_allot_date(objArray.getString("fld_allot_date"));
            esChargeOwnerFee.setFld_finance_date(objArray.getString("fld_finance_date"));
            esChargeOwnerFee.setFld_point_date(objArray.getString("fld_point_date"));
            esChargeOwnerFee.setFld_start_date(objArray.getString("fld_start_date"));
            esChargeOwnerFee.setFld_end_date(objArray.getString("fld_end_date"));
            esChargeOwnerFee.setFld_start_read(new BigDecimal(objArray.getString("fld_start_read")));
            esChargeOwnerFee.setFld_end_read(new BigDecimal(objArray.getString("fld_end_read")));
            esChargeOwnerFee.setFld_number(new BigDecimal(objArray.getString("fld_number")));
            esChargeOwnerFee.setFld_income(objArray.getInteger("fld_income"));
            esChargeOwnerFee.setFld_income_source(objArray.getInteger("income_source"));
            esChargeOwnerFee.setFld_reason_guid(objArray.getString("reason_guid"));
            esChargeOwnerFee.setFld_reason_name(objArray.getString("fld_reason_name"));
            esChargeOwnerFee.setFld_reason_remark(objArray.getString("fld_reason_remark"));
            esChargeOwnerFee.setFld_resource(objArray.getString("fld_resource"));
            esChargeOwnerFee.setFld_price(objArray.getString("fld_price"));
            esChargeOwnerFee.setFld_busi_guid(objArray.getString("fld_busi_guid"));
        }

        return esChargeOwnerFee;

    }
}
