package com.sunac.map;

import com.alibaba.fastjson.JSONObject;
import com.sunac.Config;
import com.sunac.entity.ComplexStructureDomain;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.TypeUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.util.HashMap;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/9/15
 */
public class CommonMapFunction<CommonDomain> extends RichMapFunction<JSONObject, CommonDomain> {
    String classFullName;

    public CommonMapFunction(String classFullName) {
        this.classFullName = classFullName;
    }
    @Override
    public CommonDomain map(JSONObject jsonObject) throws Exception {
        HashMap<String, String> updateInfoMap = new HashMap<String, String>();
        Class<?> tClass;
        if (classFullName.equals(Config.EsChargeIncomingBack) || classFullName.equals(Config.EsChargeIncomingConvert) || classFullName.equals(Config.EsChargeIncomingKou)) {
            tClass = Class.forName(Config.EsChargeIncomingBackDomain);
        } else if (classFullName.equals(Config.EsChargeVoucherMastRefundBack) || classFullName.equals(Config.EsChargeVoucherMastRefundConvert) || classFullName.equals(Config.EsChargeVoucherMastRefundKou)) {
            tClass = Class.forName(Config.EsChargeVoucherMastRefundDomain);
        } else {
            tClass = Class.forName(classFullName);
        }
        CommonDomain object = (CommonDomain) tClass.newInstance();
        if (null == jsonObject) {
            return object;
        }
        String optType = jsonObject.getString("type");
        JSONObject newArray = (JSONObject) jsonObject.getJSONArray("data").get(0);

        for (String fieldName : ComplexStructureDomain.TABLES.get(classFullName).keySet()) {
            if (!newArray.containsKey(fieldName)){
                continue;
            }
            String value = newArray.getString(fieldName);
            // 如果是空，就使用默认值
            Class<?> fieldType = TypeUtils.getFieldType(tClass, fieldName).getType();
            if (StringUtils.isBlank(value)) {
                Object defaultValue = ComplexStructureDomain.TABLES.get(classFullName).get(fieldName).getDefaultValue();
                if (null != defaultValue) {
                    value = String.valueOf(defaultValue);
                } else {
                    value = null;
                }
                continue;
            }
            TypeUtils.setValue(object, tClass, fieldName, fieldType, TypeUtils.stringToTarget(value, fieldType));
        }
        // EsChargeRateResult：// fld_owner_fee_guid=9745a545-597c-4588-8617-c579bb202447 +  fld_area_guid=162151971948208
        if (classFullName.equals(Config.EsChargeRateResult)) {
            TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(DigestUtils.md5Hex(newArray.getString("fld_area_guid") + newArray.getString("fld_project_guid")), String.class));
        }
        // EsInfoObjectPark:op.fld_guid = d.fld_object_guid AND op.fld_area_guid = d.fld_area_guid
        if (classFullName.equals(Config.EsInfoObjectPark)) {
            TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(DigestUtils.md5Hex(newArray.getString("fld_guid") + newArray.getString("fld_area_guid")), String.class));
        }
        //       ao.fld_area_guid = d.fld_area_guid
        // * AND ao.fld_object_guid = d.fld_object_guid
        // * AND ao.fld_owner_guid = d.fld_owner_guid
        if (classFullName.equals(Config.EsInfoObjectAndOwner)) {
            TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(DigestUtils.md5Hex(newArray.getString("fld_object_guid") + newArray.getString("fld_owner_guid") + newArray.getString("fld_area_guid")), String.class));
        }

        // EsChargeSettleAccountsMain：
        if (classFullName.equals(Config.EsChargeSettleAccountsMain)) {
            TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(DigestUtils.md5Hex(newArray.getString("fld_guid") + newArray.getString("fld_area_guid")), String.class));
        }
        // EsCommerceBondMain：
        if (classFullName.equals(Config.EsCommerceBondMain)) {
            TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(DigestUtils.md5Hex(newArray.getString("fld_guid") + newArray.getString("fld_area_guid") + newArray.getString("fld_owner_guid")), String.class));
        }

        TypeUtils.setValue(object, tClass, "updateInfoMap", tClass.getField("updateInfoMap").getType(), updateInfoMap);

        if (Config.INSERT.equals(optType)) {
            TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.INSERT);
        } else if (Config.DELETE.equals(optType)) {
            TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
        } else if (Config.UPDATE.equals(optType)) {
            TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.PASS);
            JSONObject oldArray = (JSONObject) jsonObject.getJSONArray("old").get(0);
            for (String needKey : ComplexStructureDomain.TABLES.get(classFullName).keySet()) {
                if (!oldArray.containsKey(needKey) || (!newArray.containsKey(needKey))){
                    continue;
                }
                String oldVlaue = oldArray.getString(needKey);
                String newValue = newArray.getString(needKey);

                if (StringUtils.isBlank(newValue)) {
                    newValue = "";
                }
                if (StringUtils.isBlank(oldVlaue)) {
                    oldVlaue = "";
                }

                if (!newValue.equals(oldVlaue) && ComplexStructureDomain.TABLES.get(classFullName).get(needKey).isUpdate_column()) {
                    TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.UPDATE);
                    updateInfoMap.put(needKey, newValue);

                    //TODO:有效标识删除，直接del事件！！！！！
                    if (classFullName.equals(Config.EsChargeSettleAccountsDetail) && needKey.equals("fld_status") && oldVlaue.equals("1")) {
                        TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
                        TypeUtils.setValue(object, tClass, "fld_status", Integer.class, 1);
                        if (StringUtils.isNotBlank(newArray.getString("fld_area_guid"))) {
                            String pk = "123";
                            if (StringUtils.isNotBlank(newArray.getString("fld_adjust_guid"))) {
                                pk = JdbcUtils.makeMd5(newArray.getString("fld_area_guid"), newArray.getString("fld_adjust_guid"));
                            }
                            if (StringUtils.isNotBlank(newArray.getString("fld_owner_fee_guid"))) {
                                pk = JdbcUtils.makeMd5(newArray.getString("fld_area_guid"), newArray.getString("fld_owner_fee_guid"));
                            }
                            TypeUtils.setValue(object, tClass, "pk", String.class, pk);
                        }
                        return object;
                    }
                    //TODO:有效标识删除，直接del事件！！！！！
                    if (classFullName.equals(Config.EsChargeSettleAccountsMain) && needKey.equals("fld_examine_status") && oldVlaue.equals("4")) {
                        TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
                        TypeUtils.setValue(object, tClass, "fld_examine_status", Integer.class, 4);
                        TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(DigestUtils.md5Hex(oldArray.getString("fld_guid") + oldArray.getString("fld_area_guid")), String.class));
                        return object;
                    }
                    //TODO:有效标识删除，直接del事件！！！！！
                    if (classFullName.equals(Config.EsChargeProjectPeriod) && needKey.equals("fld_type") && oldVlaue.equals("1")) {
                        TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
                        TypeUtils.setValue(object, tClass, "fld_type", Integer.class, 1);
                        TypeUtils.setValue(object, tClass, "fld_guid", String.class, newArray.getString("fld_guid"));
                        return object;
                    }

                }
            }
            TypeUtils.setValue(object, tClass, "updateInfoMap", tClass.getField("updateInfoMap").getType(), updateInfoMap);
        }
        return object;
    }
}
