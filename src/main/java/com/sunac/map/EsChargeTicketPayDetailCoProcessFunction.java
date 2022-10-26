package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeTicketPayDetail;
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
 * @Date 2022/10/11 10:13 上午
 * @Version 1.0
 */
public class EsChargeTicketPayDetailCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeTicketPayDetail, AllData> {

    MapState<String, EsChargeTicketPayDetail> mapState;
    MapState<String, Void> keyMapState;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeTicketPayDetail>("map_stat", String.class, EsChargeTicketPayDetail.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeTicketPayDetail, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        // TODO insert事件
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsChargeTicketPayDetail esChargeTicketPayDetail = mapState.get(allData.getFld_guid());
            if (esChargeTicketPayDetail != null) {
                allData.setCt_fld_owner_fee_guid(esChargeTicketPayDetail.getFld_owner_fee_guid());
                allData.setCt_fld_guid(esChargeTicketPayDetail.getFld_guid());
                allData.setCt_fld_operate_guid(esChargeTicketPayDetail.getFld_operate_guid());
            } else {
                allData.setCt_fld_operate_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsChargeTicketPayDetail newData, KeyedCoProcessFunction<String, AllData, EsChargeTicketPayDetail, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }

        String fld_guid = newData.getFld_guid();
        Iterator<String> iterator = keyMapState.keys().iterator();
        String fld_owner_fee_guid = newData.getFld_owner_fee_guid();
        EsChargeTicketPayDetail oldData = mapState.get(fld_owner_fee_guid);
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        if (newData.compareTo(oldData) <= 0) {
            if (operation_type.equals(Config.DELETE)) {
                mapState.remove(fld_owner_fee_guid);
                isSend = true;
            } else if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
                mapState.put(fld_owner_fee_guid, newData);
                isSend = true;
            }
            if (isSend && isHaveKey) {
                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeTicketPayDetail, newData));
                if (null != sql) {
                    while (iterator.hasNext()) {
                        String fld_guid_op = iterator.next();
                        Util.sendData2SlideStream(context, sql, fld_guid_op);
                    }
                }
            }
        }
    }
}
