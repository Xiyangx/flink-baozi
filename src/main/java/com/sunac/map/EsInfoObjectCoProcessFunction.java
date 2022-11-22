package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsInfoObject;
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
 * @Date 2022/10/9 3:27 下午
 * @Version 1.0
 */
public class EsInfoObjectCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsInfoObject, AllData> {

    MapState<String, EsInfoObject> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsInfoObjectCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsInfoObject.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsInfoObject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_info_object", fldGuid, allData.getFld_object_guid());
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsInfoObject esInfoObject = mapState.get(allData.getFld_object_guid());
            if (esInfoObject != null) {
                allData.setO_fld_guid(esInfoObject.getFld_guid());
                allData.setFld_batch(esInfoObject.getFld_batch());
                allData.setFld_building(esInfoObject.getFld_building());
                allData.setFld_cell(esInfoObject.getFld_cell());
                allData.setFld_charged_area(esInfoObject.getFld_charged_area());
                allData.setFld_obj_status(esInfoObject.getFld_status());
                allData.setFld_start_fee_date(esInfoObject.getFld_start_fee_date());
                allData.setObj_fld_order(esInfoObject.getFld_order());
                allData.setFld_owner_fee_date(esInfoObject.getFld_owner_fee_date());
                allData.setO_fld_class_guid(esInfoObject.getFld_class_guid());
            } else {
                allData.setO_fld_class_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsInfoObject newData, KeyedCoProcessFunction<String, AllData, EsInfoObject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String fldGuid = newData.getFld_guid();
        boolean isSend = false;
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(fldGuid);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
            mapState.put(fldGuid, newData);
            isSend = true;
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsInfoObject, newData));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_info_object", fldGuid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
            HashMap<String, String> updateInfoMap = newData.getUpdateInfoMap();
            if (operationType.equals(Config.INSERT)) {
                if (StringUtils.isNotBlank(newData.getFld_class_guid())){
                    AllData allData = new AllData();
                    allData.setOperation_type("EsInfoObject" + "#" + Config.INSERT);
                    allData.setO_fld_class_guid(newData.getFld_class_guid());
                    collector.collect(allData);
                }
            } else if (operationType.equals(Config.UPDATE) && updateInfoMap.containsKey("fld_class_guid")) {
                String[] vlaueTuple = updateInfoMap.get("fld_class_guid").split("#");
                String newValue = vlaueTuple[0];
                String oldValue = vlaueTuple[1];
                AllData allData = new AllData();
                if (StringUtils.isNotBlank(oldValue)){
                    allData.setOperation_type("EsInfoObject" + "#" + Config.DELETE);
                    allData.setO_fld_class_guid(oldValue);
                    collector.collect(allData);
                }
                if (StringUtils.isNotBlank(newValue)){
                    allData.setOperation_type("EsInfoObject" + "#" + Config.INSERT);
                    allData.setO_fld_class_guid(newValue);
                    collector.collect(allData);
                }
            }
        }
    }
}