package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsInfoOwner;
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
 * @Date 2022/10/9 3:48 下午
 * @Version 1.0
 */
public class EsInfoOwnerCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsInfoOwner, AllData> {

    MapState<String, EsInfoOwner> mapState;
    MapState<String, Void> keyMapState;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsInfoOwner>("map_stat", String.class, EsInfoOwner.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsInfoOwner, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsInfoOwner esInfoOwner = mapState.get(allData.getFld_owner_guid());
            if (esInfoOwner != null) {
                allData.setW_fld_guid(esInfoOwner.getFld_guid());
                allData.setFld_phone_number(esInfoOwner.getFld_phone_number());
                allData.setFld_owner_desc(esInfoOwner.getFld_desc());
            } else {
                allData.setW_fld_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsInfoOwner newData, KeyedCoProcessFunction<String, AllData, EsInfoOwner, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String fld_guid = newData.getFld_guid();
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;

        if (operation_type.equals(Config.DELETE)) {
            mapState.remove(fld_guid);
            isSend = true;
        } else if (operation_type.equals(Config.INSERT) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
            mapState.put(fld_guid, newData);
            isSend = true;
        }
        if (isSend && isHaveKey) {
            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsInfoOwner, newData));
            if (null != sql) {
                while (iterator.hasNext()) {
                    String fld_guid_op = iterator.next();
                    Util.sendData2SlideStream(context, sql, fld_guid_op);
                }
            }
        }
    }
}
