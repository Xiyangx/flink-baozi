package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunac.domain.EsInfoObject;
import com.sunac.domain.EsInfoObject;
import org.apache.flink.api.common.functions.MapFunction;

import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/9/8
 */
public class EsInfoObjectMapFunction implements MapFunction<String, EsInfoObject> {
    @Override
    public EsInfoObject map(String s) throws Exception {
//        fld_guid		es_info_object
//        fld_class_guid		es_info_object
//        fld_name AS fld_object_name,		es_info_object
//        fld_owner_fee_date,		es_info_object
//        fld_building,		es_info_object
//        fld_cell,		es_info_object
//        fld_batch,		es_info_object
//        fld_charged_area,		es_info_object
//        fld_status AS fld_obj_status,		es_info_object
//        fld_order AS obj_fld_order,		es_info_object


        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsInfoObject esInfoObject = new EsInfoObject();
        if ("INSERT".equals(optType)) {
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esInfoObject.setFld_guid(objArray.getString("fld_guid"));
            esInfoObject.setFld_class_guid(objArray.getString("fld_class_guid"));
            esInfoObject.setFld_name(objArray.getString("fld_name"));
            esInfoObject.setFld_owner_fee_date(objArray.getString("fld_owner_fee_date"));
            esInfoObject.setFld_building(objArray.getString("fld_building"));
            esInfoObject.setFld_cell(objArray.getString("fld_cell"));
            esInfoObject.setFld_batch(objArray.getString("fld_batch"));
            esInfoObject.setFld_charged_area(new BigDecimal(objArray.getString("fld_charged_area")));
            esInfoObject.setFld_status(Integer.parseInt(objArray.getString("fld_status")));
            esInfoObject.setFld_order(Integer.parseInt(objArray.getString("fld_order")));
        }
        return esInfoObject;
    }
}
