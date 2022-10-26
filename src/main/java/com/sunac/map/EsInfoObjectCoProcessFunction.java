package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsInfoAreaInfo;
import com.sunac.entity.EsInfoObject;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.Util;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Iterator;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/9 3:27 下午
 * @Version 1.0
 */
public class EsInfoObjectCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsInfoObject, AllData> {

    MapState<String, EsInfoObject> mapState;
    MapState<String, Void> keyMapState;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsInfoObject>("map_stat", String.class, EsInfoObject.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsInfoObject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsInfoObject esInfoObject = mapState.get(allData.getFld_object_guid());
            if (esInfoObject != null) {
                allData.setO_fld_guid(esInfoObject.getFld_guid());
                allData.setFld_batch(esInfoObject.getFld_batch());
                allData.setFld_building(esInfoObject.getFld_building());
                allData.setFld_cell(esInfoObject.getFld_cell());
                allData.setFld_charged_area(esInfoObject.getFld_charged_area());
                allData.setFld_obj_status(esInfoObject.getFld_status());
                allData.setFld_start_fee_date(esInfoObject.getFld_start_fee_date());
                allData.setObj_fld_order(esInfoObject.getFld_order());
                allData.setFld_owner_fee_date(esInfoObject.getFld_owner_fee_date());
                allData.setO_fld_class_guid(esInfoObject.getFld_class_guid());
            } else {
                allData.setO_fld_guid(Config.SINGLE_PARTITION);
                allData.setO_fld_class_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsInfoObject newData, KeyedCoProcessFunction<String, AllData, EsInfoObject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String fld_guid = newData.getFld_guid();
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        EsInfoObject esInfoObject = mapState.get(fld_guid);
        if (operation_type.equals(Config.DELETE)) {
            mapState.remove(fld_guid);
            isSend = true;

        } else if (operation_type.equals(Config.INSERT) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
            mapState.put(fld_guid, newData);
            isSend = true;
            if (newData.getUpdateInfoMap().containsKey("fld_class_guid")) {
                AllData allData = new AllData();
                allData.setOperation_type("EsInfoObject" + "#" + "fld_class_guid");
                allData.setO_fld_guid(esInfoObject.getFld_guid());
                allData.setFld_batch(esInfoObject.getFld_batch());
                allData.setFld_building(esInfoObject.getFld_building());
                allData.setFld_cell(esInfoObject.getFld_cell());
                allData.setFld_charged_area(esInfoObject.getFld_charged_area());
                allData.setFld_obj_status(esInfoObject.getFld_status());
                allData.setFld_start_fee_date(esInfoObject.getFld_start_fee_date());
                allData.setObj_fld_order(esInfoObject.getFld_order());
                allData.setFld_owner_fee_date(esInfoObject.getFld_owner_fee_date());
                allData.setO_fld_class_guid(esInfoObject.getFld_class_guid());
                collector.collect(allData);
            }
        }
        if (isSend && isHaveKey) {
            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsInfoObject, newData));
            if (null != sql) {
                while (iterator.hasNext()) {
                    String fld_guid_op = iterator.next();
                    Util.sendData2SlideStream(context, sql, fld_guid_op);
                }
            }
        }
    }
}
