package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsCommerceBondFeeObject;
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
 * @Date 2022/10/11 3:29 下午
 * @Version 1.0
 */
public class EsCommerceBondFeeObjectCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsCommerceBondFeeObject, AllData> {

    MapState<String, EsCommerceBondFeeObject> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsCommerceBondFeeObjectCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsCommerceBondFeeObject.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsCommerceBondFeeObject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        // TODO PASS事件 ：把主表主键维护
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_commerce_bond_fee_object", fldGuid, allData.getFld_object_guid());
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsCommerceBondFeeObject esCommerceBondFeeObject = mapState.get(allData.getFld_object_guid());
            if (null != esCommerceBondFeeObject) {
                allData.setCbfo_fld_object_guid(esCommerceBondFeeObject.getFld_object_guid());
                allData.setCbfo_fld_bond_guid(esCommerceBondFeeObject.getFld_bond_guid());
            }else {
                allData.setCbfo_fld_bond_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsCommerceBondFeeObject newData, KeyedCoProcessFunction<String, AllData, EsCommerceBondFeeObject, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();

        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0) {
            return;
        }
        String fldObjectGuid = newData.getFld_object_guid();
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(fldObjectGuid);
        }
        if (operationType.equals(Config.INSERT)) {
            mapState.put(fldObjectGuid, newData);
            AllData allData = new AllData();
            allData.setOperation_type("EsCommerceBondFeeObject" + "#" + operationType);
            allData.setCbfo_fld_bond_guid(newData.getFld_bond_guid());
            allData.setCbfo_fld_guid(newData.getFld_guid());
            allData.setCbfo_fld_object_guid(newData.getFld_object_guid());
            collector.collect(allData);
        }
        if (operationType.equals(Config.UPDATE)){
            HashMap<String, String> updateInfoMap = newData.getUpdateInfoMap();
            if (updateInfoMap.containsKey("fld_object_guid") && !updateInfoMap.containsKey("fld_bond_guid")){
                String oldFldObjectGuid = newData.getUpdateInfoMap().get("fld_object_guid");
                //todo 维护idx的关联关系
                JdbcUtils.updateSqlByValue(fldObjectGuid,oldFldObjectGuid);
                //todo 把旧的缓存清除
                mapState.remove(oldFldObjectGuid);
                //todo 添加新的缓存
                mapState.put(fldObjectGuid, newData);
                //todo 下游不发生变化，不用传递
                return;
            }
            if (!updateInfoMap.containsKey("fld_object_guid") && updateInfoMap.containsKey("fld_bond_guid")){
                //todo 直接覆盖掉缓存
                mapState.put(fldObjectGuid, newData);
                //todo 下游关联键发生变化，需要发送出去
                AllData allData = new AllData();
                allData.setOperation_type("EsCommerceBondFeeObject" + "#" + operationType);
                allData.setCbfo_fld_bond_guid(newData.getFld_bond_guid());
                allData.setCbfo_fld_guid(newData.getFld_guid());
                allData.setCbfo_fld_object_guid(newData.getFld_object_guid());
                collector.collect(allData);
            }
            if (updateInfoMap.containsKey("fld_object_guid") && updateInfoMap.containsKey("fld_bond_guid")){
                String oldFldObjectGuid = newData.getUpdateInfoMap().get("fld_object_guid");
                //todo 维护idx的关联关系
                JdbcUtils.updateSqlByValue(fldObjectGuid,oldFldObjectGuid);
                //todo 把旧的缓存清除
                mapState.remove(oldFldObjectGuid);
                //todo 添加新的缓存
                mapState.put(fldObjectGuid, newData);
                AllData allData = new AllData();
                allData.setOperation_type("EsCommerceBondFeeObject" + "#" + operationType);
                allData.setCbfo_fld_bond_guid(newData.getFld_bond_guid());
                allData.setCbfo_fld_guid(newData.getFld_guid());
                allData.setCbfo_fld_object_guid(newData.getFld_object_guid());
                collector.collect(allData);
            }
        }
//        if (isSend) {
//            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsCommerceBondFeeObject, newData));
//            boolean isSuccess = JdbcUtils.queryByTabAndKey(context, "idx_owe_es_commerce_bond_fee_object", fldObjectGuid, sql);
//            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + isSuccess);
//            HashMap<String, String> updateInfoMap = newData.getUpdateInfoMap();
//            if (operationType.equals(Config.INSERT)) {
//                AllData allData = new AllData();
//                allData.setOperation_type("EsCommerceBondFeeObject" + "#" + operationType);
//                allData.setCbfo_fld_bond_guid(newData.getFld_bond_guid());
//                allData.setCbfo_fld_guid(newData.getFld_guid());
//                allData.setCbfo_fld_object_guid(newData.getFld_object_guid());
//                collector.collect(allData);
//            } else if (operationType.equals(Config.UPDATE)) {
//                if (updateInfoMap.containsKey("fld_object_guid") && !updateInfoMap.containsKey("fld_bond_guid")){
//
//                }
//                String[] vlaueTuple = updateInfoMap.get("fld_bond_guid").split("#");
//                String newValue = vlaueTuple[0];
//                String oldValue = vlaueTuple[1];
//                AllData allData = new AllData();
//                allData.setOperation_type("EsCommerceBondFeeObject" + "#" + Config.DELETE);
//                allData.setCbfo_fld_bond_guid(oldValue);
//                collector.collect(allData);
//                allData.setOperation_type("EsCommerceBondFeeObject" + "#" + Config.UPDATE);
//                allData.setCbfo_fld_bond_guid(newValue);
//                collector.collect(allData);
//            }
//        }
    }
}
