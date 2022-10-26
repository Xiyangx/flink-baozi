package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeIncomingData;
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
 * @Date 2022/10/10 3:53 下午
 * @Version 1.0
 */
public class EsChargeIncomingDataCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeIncomingData, AllData> {
    MapState<String, EsChargeIncomingData> mapState;
    MapState<String, Void> keyMapState;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeIncomingData>("map_stat", String.class, EsChargeIncomingData.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeIncomingData, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsChargeIncomingData esChargeIncomingData = mapState.get(allData.getFld_guid());
            if (esChargeIncomingData != null && esChargeIncomingData.getFld_cancel()!=1) {
                allData.setDa_fld_guid(esChargeIncomingData.getFld_guid());
                allData.setData_fld_create_date(esChargeIncomingData.getFld_create_date());
                allData.setData_fld_operate_date(esChargeIncomingData.getFld_operate_date());
                allData.setData_fld_create_user(esChargeIncomingData.getFld_create_user());
                allData.setData_fld_busi_type(esChargeIncomingData.getFld_busi_type());
                allData.setData_fld_total(esChargeIncomingData.getFld_total());
                allData.setData_fld_amount(esChargeIncomingData.getFld_amount());
                allData.setData_fld_late_fee(esChargeIncomingData.getFld_late_fee());
                allData.setData_fld_tax_amount(esChargeIncomingData.getFld_tax_amount());
                allData.setData_fld_tax(esChargeIncomingData.getFld_tax());
                allData.setData_fld_cancel(esChargeIncomingData.getFld_cancel());
                allData.setData_fld_late_amount(esChargeIncomingData.getFld_late_amount());
                allData.setDa_fld_owner_fee_guid(esChargeIncomingData.getFld_owner_fee_guid());
                allData.setDa_fld_area_guid(esChargeIncomingData.getFld_area_guid());
                allData.setDa_fld_incoming_fee_guid(esChargeIncomingData.getFld_incoming_fee_guid());
            } else {
                allData.setDa_fld_incoming_fee_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeIncomingData esChargeIncomingData, KeyedCoProcessFunction<String, AllData, EsChargeIncomingData, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = esChargeIncomingData.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && esChargeIncomingData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String fld_owner_fee_guid = esChargeIncomingData.getFld_owner_fee_guid();
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        if (operation_type.equals(Config.DELETE)) {
            mapState.remove(fld_owner_fee_guid);
            isSend = true;
        } else if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            mapState.put(fld_owner_fee_guid, esChargeIncomingData);
            isSend = true;
        }
        if (isSend && isHaveKey) {
            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeIncomingData, esChargeIncomingData));
            if (null != sql) {
                while (iterator.hasNext()) {
                    String fld_guid_op = iterator.next();
                    Util.sendData2SlideStream(context, sql, fld_guid_op);
                }
            }
        }
    }
}
