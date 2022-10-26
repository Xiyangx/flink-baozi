package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeProject;
import com.sunac.entity.EsInfoAreaInfo;
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
 * @Date 2022/10/9 4:02 下午
 * @Version 1.0
 */
public class EsChargeProjectCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeProject, AllData> {

    MapState<String, EsChargeProject> mapState;
    MapState<String, Void> keyMapState;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeProject>("map_stat", String.class, EsChargeProject.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeProject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsChargeProject esChargeProject = mapState.get(allData.getFld_project_guid());
            if (esChargeProject != null) {
                allData.setP_fld_guid(esChargeProject.getFld_guid());
                allData.setFld_object_type(esChargeProject.getFld_object_type());
            }
            collector.collect(allData);
        }


    }

    @Override
    public void processElement2(EsChargeProject esChargeProject, KeyedCoProcessFunction<String, AllData, EsChargeProject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = esChargeProject.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && esChargeProject.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String fld_guid = esChargeProject.getFld_guid();
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        if (operation_type.equals(Config.DELETE)) {
            mapState.remove(fld_guid);
            isSend = true;
        } else if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            mapState.put(fld_guid, esChargeProject);
            isSend = true;
        }
        if (isSend && isHaveKey) {
            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeProject, esChargeProject));
            if (null != sql) {
                while (iterator.hasNext()) {
                    String fld_guid_op = iterator.next();
                    Util.sendData2SlideStream(context, sql, fld_guid_op);
                }
            }
        }
    }
}
