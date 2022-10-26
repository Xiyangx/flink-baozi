package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeProject;
import com.sunac.entity.EsChargeProjectPeriodJoin;
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
 * @Date 2022/10/10 11:12 上午
 * @Version 1.0
 */
public class EsChargeProjectPeriodJoinCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeProjectPeriodJoin, AllData> {

    MapState<String, EsChargeProjectPeriodJoin> mapState;
    MapState<String, Void> keyMapState;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeProjectPeriodJoin>("map_stat", String.class, EsChargeProjectPeriodJoin.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeProjectPeriodJoin, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsChargeProjectPeriodJoin esChargeProjectPeriodJoin = mapState.get(allData.getFld_project_guid());
            if (esChargeProjectPeriodJoin != null) {
                allData.setPj_fld_project_guid(esChargeProjectPeriodJoin.getFld_project_guid());
                allData.setPj_fld_period_guid(esChargeProjectPeriodJoin.getFld_period_guid());
                allData.setPj_fld_guid(esChargeProjectPeriodJoin.getFld_guid());
            } else {
                allData.setPj_fld_period_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsChargeProjectPeriodJoin newData, KeyedCoProcessFunction<String, AllData, EsChargeProjectPeriodJoin, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }

        String fld_project_guid = newData.getFld_project_guid();
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        if (operation_type.equals(Config.DELETE)) {
            mapState.remove(fld_project_guid);
            isSend = true;
        } else if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            mapState.put(fld_project_guid, newData);
            isSend = true;
            //TODO: allData.setFld_object_class_guid(esInfoObject.getFld_class_guid());--用于关联：ds_fd_es_info_object_class_af oc
            if (newData.getUpdateInfoMap().containsKey("fld_period_guid")) {
                AllData allData = new AllData();
                allData.setOperation_type("EsChargeProjectPeriodJoin" + "#" + "fld_period_guid");
                allData.setPj_fld_project_guid(newData.getFld_project_guid());
                allData.setPj_fld_period_guid(newData.getFld_period_guid());
                allData.setPj_fld_guid(newData.getFld_guid());
                collector.collect(allData);
            }
        }
        if (isSend && isHaveKey) {
            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeProjectPeriodJoin, newData));
            if (null != sql) {
                while (iterator.hasNext()) {
                    String fld_guid_op = iterator.next();
                    Util.sendData2SlideStream(context, sql, fld_guid_op);
                }
            }
        }
    }
}
