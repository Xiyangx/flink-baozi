package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeTicketPayDetail;
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
 * @Date 2022/10/11 10:13 上午
 * @Version 1.0
 */
public class EsChargeTicketPayDetailCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeTicketPayDetail, AllData> {

    MapState<String, EsChargeTicketPayDetail> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeTicketPayDetailCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeTicketPayDetail.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeTicketPayDetail, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_charge_ticket_pay_detail", fldGuid, allData.getFld_guid());
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        // TODO insert事件
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsChargeTicketPayDetail esChargeTicketPayDetail = mapState.get(allData.getFld_guid());
            if (esChargeTicketPayDetail != null) {
                allData.setCt_fld_owner_fee_guid(esChargeTicketPayDetail.getFld_owner_fee_guid());
                allData.setCt_fld_guid(esChargeTicketPayDetail.getFld_guid());
                allData.setCt_fld_operate_guid(esChargeTicketPayDetail.getFld_operate_guid());
            } else {
                allData.setCt_fld_operate_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeTicketPayDetail newData, KeyedCoProcessFunction<String, AllData, EsChargeTicketPayDetail, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }

        String fldOwnerFeeGuid = newData.getFld_owner_fee_guid();
        EsChargeTicketPayDetail oldData = mapState.get(fldOwnerFeeGuid);
        boolean isSend = false;
        if (newData.compareTo(oldData) <= 0) {
            if (operationType.equals(Config.DELETE)) {
                mapState.remove(fldOwnerFeeGuid);
                isSend = true;
            } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
                mapState.put(fldOwnerFeeGuid, newData);
                isSend = true;
            }
            if (isSend) {
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeTicketPayDetail, newData));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_charge_ticket_pay_detail", fldOwnerFeeGuid, sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
                HashMap<String, String> updateInfoMap = newData.getUpdateInfoMap();
                if (operationType.equals(Config.INSERT)) {
                    if (StringUtils.isNotBlank(newData.getFld_operate_guid())){
                        AllData allData = new AllData();
                        allData.setOperation_type("EsChargeTicketPayDetail" + "#" + operationType);
                        allData.setCt_fld_operate_guid(newData.getFld_operate_guid());
                        collector.collect(allData);
                    }
                } else if (operationType.equals(Config.UPDATE) && updateInfoMap.containsKey("Fld_operate_guid")) {
                    String[] vlaueTuple = updateInfoMap.get("fld_operate_guid").split("#");
                    String newValue = vlaueTuple[0];
                    String oldValue = vlaueTuple[1];
                    AllData allData = new AllData();
                    if (StringUtils.isNotBlank(oldValue)){
                        allData.setOperation_type("EsChargeTicketPayDetail" + "#" + Config.DELETE);
                        allData.setCt_fld_operate_guid(oldValue);
                        collector.collect(allData);
                    }
                    if (StringUtils.isNotBlank(newValue)){
                        allData.setOperation_type("EsChargeTicketPayDetail" + "#" + Config.INSERT);
                        allData.setCt_fld_operate_guid(newValue);
                        collector.collect(allData);
                    }
                }
            }
        }
    }
}
