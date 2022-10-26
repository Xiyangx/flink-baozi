package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.domain.EsInfoObjectAndOwnerDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsInfoObjectAndOwner;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.Util;
import org.apache.commons.codec.digest.DigestUtils;
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
 * @Date 2022/10/10 8:59 上午
 * @Version 1.0
 */
public class EsInfoObjectAndOwnerCoMapFunction extends KeyedCoProcessFunction<String, AllData, EsInfoObjectAndOwner, AllData> {
    MapState<String, EsInfoObjectAndOwner> mapState;
    MapState<String, Void> keyMapState;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsInfoObjectAndOwner>("map_stat", String.class, EsInfoObjectAndOwner.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsInfoObjectAndOwner esInfoObjectAndOwner = mapState.get(DigestUtils.md5Hex(allData.getO_fld_guid() + allData.getW_fld_guid() + allData.getFld_area_guid()));
            if (esInfoObjectAndOwner != null) {
                allData.setOao_fld_guid(esInfoObjectAndOwner.getFld_guid());
                allData.setOao_fld_object_guid(esInfoObjectAndOwner.getFld_object_guid());
                allData.setOao_fld_area_guid(esInfoObjectAndOwner.getFld_area_guid());
                allData.setOao_fld_owner_guid(esInfoObjectAndOwner.getFld_owner_guid());
                allData.setFld_is_current(esInfoObjectAndOwner.getFld_is_current());
                allData.setFld_is_charge(esInfoObjectAndOwner.getFld_is_charge());
                allData.setFld_is_owner(esInfoObjectAndOwner.getFld_is_owner());
                allData.setFld_status(esInfoObjectAndOwner.getFld_status());
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsInfoObjectAndOwner newData, Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        String pk = newData.getPk();
        EsInfoObjectAndOwner oldData = mapState.get(pk);
        if (newData.compareTo(oldData) <= 0) {
            if (operation_type.equals(Config.DELETE)) {
                mapState.remove(pk);
                isSend = true;
            } else if (operation_type.equals(Config.INSERT) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
                mapState.put(pk, newData);
                isSend = true;
            }
            if (isSend && isHaveKey) {
                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsInfoObjectAndOwner, newData));
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
