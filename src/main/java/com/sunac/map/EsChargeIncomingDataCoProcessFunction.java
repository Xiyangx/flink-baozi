package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeIncomingData;
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
 * @Date 2022/10/10 3:53 下午
 * @Version 1.0
 */
public class EsChargeIncomingDataCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeIncomingData, AllData> {
    MapState<String, EsChargeIncomingData> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeIncomingDataCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        connection = HikariUtil.getInstance().getConnection();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeIncomingData.class));
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeIncomingData, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection, "idx_owe_es_charge_incoming_data", fldGuid, fldGuid);
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsChargeIncomingData esChargeIncomingData = mapState.get(allData.getFld_guid());
            if (esChargeIncomingData != null && esChargeIncomingData.getFld_cancel()!=1) {
                allData.setDa_fld_guid(esChargeIncomingData.getFld_guid());
                allData.setData_fld_create_date(esChargeIncomingData.getFld_create_date());
                allData.setData_fld_operate_date(esChargeIncomingData.getFld_operate_date());
                allData.setData_fld_create_user(esChargeIncomingData.getFld_create_user());
                allData.setData_fld_busi_type(esChargeIncomingData.getFld_busi_type());
                allData.setData_fld_total(esChargeIncomingData.getFld_total());
                allData.setData_fld_amount(esChargeIncomingData.getFld_amount());
                allData.setData_fld_late_fee(esChargeIncomingData.getFld_late_fee());
                allData.setData_fld_tax_amount(esChargeIncomingData.getFld_tax_amount());
                allData.setData_fld_tax(esChargeIncomingData.getFld_tax());
                allData.setData_fld_cancel(esChargeIncomingData.getFld_cancel());
                allData.setData_fld_late_amount(esChargeIncomingData.getFld_late_amount());
                allData.setDa_fld_owner_fee_guid(esChargeIncomingData.getFld_owner_fee_guid());
                allData.setDa_fld_area_guid(esChargeIncomingData.getFld_area_guid());
                allData.setDa_fld_incoming_fee_guid(esChargeIncomingData.getFld_incoming_fee_guid());
            } else {
                allData.setDa_fld_incoming_fee_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeIncomingData esChargeIncomingData, KeyedCoProcessFunction<String, AllData, EsChargeIncomingData, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = esChargeIncomingData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && esChargeIncomingData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        String fldOwnerFeeGuid = esChargeIncomingData.getFld_owner_fee_guid();
        boolean isSend = false;
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(fldOwnerFeeGuid);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            mapState.put(fldOwnerFeeGuid, esChargeIncomingData);
            isSend = true;
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeIncomingData, esChargeIncomingData));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection, "idx_owe_es_charge_incoming_data", fldOwnerFeeGuid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }
}
