package com.sunac.map;

import com.alibaba.fastjson.JSONObject;
import com.sunac.Config;
import com.sunac.ow.owdomain.ComplexStructureDomain;

import com.sunac.utils.JdbcUtils;
import com.sunac.utils.MakeMd5Utils;
import com.sunac.utils.TypeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;

public class CommomProcessFunction<CommonDomain> extends ProcessFunction<JSONObject, CommonDomain> {
String classFullName;

public CommomProcessFunction(String classFullName) {
    this.classFullName = classFullName;
}

@Override
public void processElement(JSONObject jsonObject, ProcessFunction<JSONObject, CommonDomain>.Context context, Collector<CommonDomain> collector) throws Exception {
    HashMap<String, String> updateInfoMap = new HashMap<String, String>();
    
    Class<?> tClass = Class.forName(classFullName);
    CommonDomain object = (CommonDomain) tClass.newInstance();
    if (null == jsonObject) {
        return;
    }
    String optType = jsonObject.getString("type");
    JSONObject newArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
    
    for (String fieldName : ComplexStructureDomain.TABLES.get(classFullName).keySet()) {
        if (!newArray.containsKey(fieldName)) {
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
    if (classFullName.equals(Config.EsChargeRateResult)) {
        TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(MakeMd5Utils.makeMd5(newArray.getString("fld_area_guid") , newArray.getString("fld_project_guid")), String.class));
    }
    if (classFullName.equals(Config.EsInfoObjectAndOwner)) {
        TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(MakeMd5Utils.makeMd5(newArray.getString("fld_object_guid") , newArray.getString("fld_owner_guid") , newArray.getString("fld_area_guid")), String.class));
    }
    if (classFullName.equals(Config.EsChargeSettleAccountsMain)) {
        TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(MakeMd5Utils.makeMd5(newArray.getString("fld_guid") , newArray.getString("fld_area_guid")), String.class));
    }
    if (classFullName.equals(Config.EsCommerceBondMain)) {
        TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(MakeMd5Utils.makeMd5(newArray.getString("fld_area_guid") , newArray.getString("fld_owner_guid")), String.class));
    }

    TypeUtils.setValue(object, tClass, "updateInfoMap", tClass.getField("updateInfoMap").getType(), updateInfoMap);
    
    if (Config.INSERT.equals(optType)) {
        TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.INSERT);
        collector.collect(object);
        return;
    } else if (Config.DELETE.equals(optType)) {
        TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
        collector.collect(object);
        return;
    } else if (Config.UPDATE.equals(optType)) {
        TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.PASS);
        JSONObject oldArray = (JSONObject) jsonObject.getJSONArray("old").get(0);
        for (String needKey : ComplexStructureDomain.TABLES.get(classFullName).keySet()) {
            if (!oldArray.containsKey(needKey) || (!newArray.containsKey(needKey))) {
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
                if (Config.EsInfoObject.equals(classFullName) && needKey.equals("fld_class_guid")) {
                    updateInfoMap.put(needKey, newValue + "#" + oldVlaue);
                } else if (Config.EsChargeSettleAccountsDetail.equals(classFullName) && needKey.equals("fld_main_guid")) {
                    updateInfoMap.put(needKey, newValue + "#" + oldVlaue);
                } else if (Config.EsChargeProjectPeriodJoin.equals(classFullName) && needKey.equals("fld_period_guid")) {
                    updateInfoMap.put(needKey, newValue + "#" + oldVlaue);
                } else if (Config.EsChargeTicketPayDetail.equals(classFullName) && needKey.equals("fld_operate_guid")) {
                    updateInfoMap.put(needKey, newValue + "#" + oldVlaue);
                } else {
                    updateInfoMap.put(needKey, newValue);
                }

                //TODO:有效标识删除，直接del事件！！！！！
                if (classFullName.equals(Config.EsChargeSettleAccountsDetail) && needKey.equals("fld_status") && oldVlaue.equals("1")) {
                    TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
                    TypeUtils.setValue(object, tClass, "fld_status", Integer.class, 1);
                    if (StringUtils.isNotBlank(newArray.getString("fld_area_guid"))) {
                        if (StringUtils.isNotBlank(newArray.getString("fld_adjust_guid"))) {
                            String pk = JdbcUtils.makeMd5(newArray.getString("fld_area_guid"), newArray.getString("fld_adjust_guid"));
                            TypeUtils.setValue(object, tClass, "pk", String.class, pk);
                            collector.collect(object);
                        }
                        if (StringUtils.isNotBlank(newArray.getString("fld_owner_fee_guid"))) {
                            String pk = JdbcUtils.makeMd5(newArray.getString("fld_area_guid"), newArray.getString("fld_owner_fee_guid"));
                            TypeUtils.setValue(object, tClass, "pk", String.class, pk);
                            collector.collect(object);
                        }
                    }
                    return;
                }
                //TODO:有效标识删除，直接del事件！！！！！
                if (classFullName.equals(Config.EsChargeSettleAccountsMain) && needKey.equals("fld_examine_status") && oldVlaue.equals("4")) {
                    TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
                    TypeUtils.setValue(object, tClass, "fld_examine_status", Integer.class, 4);
                    TypeUtils.setValue(object, tClass, "pk", String.class, TypeUtils.stringToTarget(MakeMd5Utils.makeMd5(oldArray.getString("fld_guid") , oldArray.getString("fld_area_guid")), String.class));
                    collector.collect(object);
                    return;
                }
                //TODO:有效标识删除，直接del事件！！！！！
                if (classFullName.equals(Config.EsChargeProjectPeriod) && needKey.equals("fld_type") && oldVlaue.equals("1")) {
                    TypeUtils.setValue(object, tClass, "operation_type", String.class, Config.DELETE);
                    TypeUtils.setValue(object, tClass, "fld_type", Integer.class, 1);
                    TypeUtils.setValue(object, tClass, "fld_guid", String.class, newArray.getString("fld_guid"));
                    collector.collect(object);
                    return;
                }
            }
        }
        TypeUtils.setValue(object, tClass, "updateInfoMap", tClass.getField("updateInfoMap").getType(), updateInfoMap);
    }
    // TODO:这里不是pass，才会发送
    if (null != object && !Config.PASS.equals((String) TypeUtils.getGetMethod(object, "operation_type")) && updateInfoMap.size() > 0) {
        collector.collect(object);
    }
}
}
