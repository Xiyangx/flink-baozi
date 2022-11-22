package com.sunac.map;


import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsInfoAreaInfo;
import com.sunac.sink.CommonSink;
import com.sunac.utils.HikariUtil;
import com.sunac.utils.JdbcUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/9 9:19 上午
 * @Version 1.0
 */
public class EsInfoAreaInfoCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsInfoAreaInfo, AllData>{

    MapState<String, EsInfoAreaInfo> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsInfoAreaInfoCoProcessFunction.class);
    transient Connection connection = null;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsInfoAreaInfo.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }
    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsInfoAreaInfo, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_es_info_area_info", fldGuid, allData.getFld_area_guid());
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsInfoAreaInfo esInfoAreaInfo = mapState.get(allData.getFld_area_guid());
            if (esInfoAreaInfo != null) {
                allData.setA_fld_guid(esInfoAreaInfo.getFld_guid());
                allData.setFld_dq(esInfoAreaInfo.getFld_dq());
                allData.setFld_ywdy(esInfoAreaInfo.getFld_ywdy());
                allData.setFld_xm(esInfoAreaInfo.getFld_xm());
                allData.setFld_company(esInfoAreaInfo.getFld_company());
                allData.setFld_confirm_date(esInfoAreaInfo.getFld_confirm_date());
                allData.setFld_fee_type(esInfoAreaInfo.getFld_fee_type());
                allData.setFld_yt(esInfoAreaInfo.getFld_yt());
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsInfoAreaInfo esInfoAreaInfo, KeyedCoProcessFunction<String, AllData, EsInfoAreaInfo, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = esInfoAreaInfo.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && esInfoAreaInfo.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String fldGuid = esInfoAreaInfo.getFld_guid();
        boolean isSend = false;
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(fldGuid);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            mapState.put(fldGuid, esInfoAreaInfo);
            isSend = true;
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsInfoAreaInfo, esInfoAreaInfo));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_es_info_area_info", fldGuid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }
}