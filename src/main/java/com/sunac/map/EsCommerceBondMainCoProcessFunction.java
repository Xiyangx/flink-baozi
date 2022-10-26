package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsCommerceBondMain;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.StringToIntegerUtils;
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
 * @Date 2022/10/11 3:52 下午
 * @Version 1.0
 */
public class EsCommerceBondMainCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsCommerceBondMain, AllData> {

    MapState<String, EsCommerceBondMain> mapState;
    MapState<String, Void> keyMapState;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsCommerceBondMain>("map_stat", String.class, EsCommerceBondMain.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsCommerceBondMain, AllData>.Context context, Collector<AllData> collector) throws Exception {
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
            EsCommerceBondMain esCommerceBondMain = mapState.get(DigestUtils.md5Hex(allData.getCbfo_fld_bond_guid() + allData.getFld_area_guid() + allData.getFld_owner_guid()));
            if (null != esCommerceBondMain) {
                allData.setCbm_fld_area_guid(esCommerceBondMain.getFld_area_guid());
                allData.setCbm_fld_owner_guid(esCommerceBondMain.getFld_owner_guid());
                allData.setBm_end_date(esCommerceBondMain.getFld_end_date());
                allData.setBm_stop_date(esCommerceBondMain.getFld_stop_date());
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsCommerceBondMain newData, KeyedCoProcessFunction<String, AllData, EsCommerceBondMain, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();

        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String pk = newData.getPk();
        EsCommerceBondMain oldData = mapState.get(pk);

        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        if(oldData != null){
            if (StringToIntegerUtils.getInteger(oldData.getFld_end_date()) > StringToIntegerUtils.getInteger(newData.getFld_end_date())){
                newData.setFld_end_date(oldData.getFld_end_date());
            }
            if (StringToIntegerUtils.getInteger(oldData.getFld_stop_date()) > StringToIntegerUtils.getInteger(newData.getFld_stop_date())){
                newData.setFld_stop_date(oldData.getFld_stop_date());
            }
        }
        if (operation_type.equals(Config.DELETE)) {
            mapState.remove(pk);
            isSend = true;
        } else if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            mapState.put(pk, newData);
            isSend = true;
        }
        if (isSend && isHaveKey) {
            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsCommerceBondMain, newData));
            if (null != sql) {
                while (iterator.hasNext()) {
                    String fld_guid_op = iterator.next();
                    Util.sendData2SlideStream(context, sql, fld_guid_op);
                }
            }
        }
    }
}
