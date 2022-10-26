package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeProject;
import com.sunac.entity.EsChargeRateResult;
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
 * @Date 2022/10/9 5:31 下午
 * @Version 1.0
 */
public class EsChargeRateResultCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeRateResult, AllData> {
    MapState<String, EsChargeRateResult> mapState;
    MapState<String, Void> keyMapState;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeRateResult>("map_stat", String.class, EsChargeRateResult.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }
    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeRateResult, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (null != fld_guid && !"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsChargeRateResult esChargeRateResult = mapState.get(DigestUtils.md5Hex(allData.getFld_area_guid() + allData.getFld_project_guid()));
            if (esChargeRateResult != null) {
                //and f.fld_finance_date <= ecrr.fld_end_date
                //and f.fld_finance_date >= ecrr.fld_start_date
                if(StringToIntegerUtils.getInteger(esChargeRateResult.getFld_end_date())<StringToIntegerUtils.getInteger(allData.getFld_finance_date()) || StringToIntegerUtils.getInteger(esChargeRateResult.getFld_start_date()) > StringToIntegerUtils.getInteger(allData.getFld_finance_date())){
                    collector.collect(allData);
                }
                allData.setFld_rate(esChargeRateResult.getFld_general_tax());
                allData.setEcrr_fld_guid(esChargeRateResult.getFld_guid());
                allData.setEcrr_fld_general_tax(esChargeRateResult.getFld_general_tax());
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsChargeRateResult esChargeRateResult, KeyedCoProcessFunction<String, AllData, EsChargeRateResult, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operation_type = esChargeRateResult.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && esChargeRateResult.getUpdateInfoMap().size() == 0)) {
            return;
        }

        String pk = esChargeRateResult.getPk();
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        if (operation_type.equals(Config.DELETE)) {
            mapState.remove(pk);
            isSend = true;
        } else if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            mapState.put(pk, esChargeRateResult);
            isSend = true;
        }
        if (isSend && isHaveKey) {
            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeRateResult, esChargeRateResult));
            if (null != sql) {
                while (iterator.hasNext()) {
                    String fld_guid_op = iterator.next();
                    Util.sendData2SlideStream(context, sql, fld_guid_op);
                }
            }
        }
    }
}
