package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeProjectPeriodJoin;
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
import java.util.HashMap;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/10 11:12 上午
 * @Version 1.0
 */
public class EsChargeProjectPeriodJoinCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeProjectPeriodJoin, AllData> {

    MapState<String, EsChargeProjectPeriodJoin> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeProjectPeriodJoinCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeProjectPeriodJoin.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeProjectPeriodJoin, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        String fldProjectGuid = allData.getFld_project_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_charge_project_period_join", fldGuid, fldProjectGuid);
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
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
        String operationType = newData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }

        String fldProjectGuid = newData.getFld_project_guid();
        boolean isSend = false;
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(fldProjectGuid);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            mapState.put(fldProjectGuid, newData);
            isSend = true;
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeProjectPeriodJoin, newData));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_charge_project_period_join", fldProjectGuid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
            HashMap<String, String> updateInfoMap = newData.getUpdateInfoMap();
            if (operationType.equals(Config.INSERT)) {
                if (StringUtils.isNotBlank(newData.getFld_period_guid())){
                    AllData allData = new AllData();
                    allData.setOperation_type("EsChargeProjectPeriodJoin" + "#" + operationType);
                    allData.setPj_fld_period_guid(newData.getFld_period_guid());
                    collector.collect(allData);
                }
            } else if (operationType.equals(Config.UPDATE) && updateInfoMap.containsKey("fld_period_guid")) {
                String[] vlaueTuple = updateInfoMap.get("fld_main_guid").split("#");
                String newValue = vlaueTuple[0];
                String oldValue = vlaueTuple[1];
                AllData allData = new AllData();
                if (StringUtils.isNotBlank(oldValue)){
                    allData.setOperation_type("EsChargeProjectPeriodJoin" + "#" + Config.DELETE);
                    allData.setPj_fld_period_guid(oldValue);
                    collector.collect(allData);
                }
                if (StringUtils.isNotBlank(newValue)){
                    allData.setOperation_type("EsChargeProjectPeriodJoin" + "#" + Config.INSERT);
                    allData.setPj_fld_period_guid(newValue);
                    collector.collect(allData);
                }
            }
        }
    }
}
