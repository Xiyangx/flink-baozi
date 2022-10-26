package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.domain.EsChargeProjectPeriodDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeProjectPeriod;
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
 * @Date 2022/10/10 12:14 下午
 * @Version 1.0
 */
public class EsChargeProjectPerionCoMapFunction extends KeyedCoProcessFunction<String, AllData, EsChargeProjectPeriod, AllData> {
    MapState<String, EsChargeProjectPeriod> mapState;
    MapState<String, Void> keyMapState;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeProjectPeriod>("map_stat", String.class, EsChargeProjectPeriod.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {
        if (allData.getOperation_type().equals("EsChargeProjectPeriodJoin" + "#" + "fld_period_guid")) {
            EsChargeProjectPeriod esInfoObjectClass = mapState.get(allData.getPj_fld_period_guid());
            Iterator<String> iterator = keyMapState.keys().iterator();
            boolean isHaveKey = iterator.hasNext();
            if (isHaveKey) {
                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeProjectPeriod, esInfoObjectClass));
                if (null != sql) {
                    while (iterator.hasNext()) {
                        String fld_guid_op = iterator.next();
                        Util.sendData2SlideStream(context, sql, fld_guid_op);
                    }
                }
            }
        }
        // TODO PASS事件 ：把主表主键维护
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsChargeProjectPeriod oldData = mapState.get(allData.getPj_fld_period_guid());
            if (null != oldData && oldData.getFld_type() == 1) {
                allData.setPp_fld_guid(oldData.getFld_guid());
                allData.setFld_project_period_name(oldData.getFld_name());

            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsChargeProjectPeriod newData, Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();

        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }


        String fld_guid = newData.getFld_guid();
        boolean fld_type = newData.getFld_type() == 1;
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        if (fld_type) {
            if (operation_type.equals(Config.DELETE)) {
                mapState.remove(fld_guid);
                isSend = true;
            } else if (operation_type.equals(Config.INSERT) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
                mapState.put(fld_guid, newData);
                isSend = true;
            }
            if (isSend && isHaveKey) {
                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeProjectPeriod, newData));
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
