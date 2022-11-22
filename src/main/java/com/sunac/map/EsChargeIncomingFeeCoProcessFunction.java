package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeIncomingFee;
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
 * @Date 2022/10/11 9:33 上午
 * @Version 1.0
 */
public class EsChargeIncomingFeeCoProcessFunction  extends KeyedCoProcessFunction<String, AllData, EsChargeIncomingFee, AllData> {
    MapState<String, EsChargeIncomingFee> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeIncomingFeeCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        connection = HikariUtil.getInstance().getConnection();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeIncomingFee.class));
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        String daFldIncomingFeeGuid = allData.getDa_fld_incoming_fee_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection, "idx_owe_es_charge_incoming_fee", fldGuid, daFldIncomingFeeGuid);
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (StringUtils.isBlank(allData.getFld_reason_guid())){
            allData.setFld_reason_guid(Config.SINGLE_PARTITION);
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsChargeIncomingFee esChargeIncomingFee = mapState.get(allData.getDa_fld_incoming_fee_guid());
            if (esChargeIncomingFee != null) {
                allData.setFee_fld_cancel_me(esChargeIncomingFee.getFld_cancel_me());
                allData.setCif_fld_guid(esChargeIncomingFee.getFld_guid());
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeIncomingFee esChargeIncomingFee, Context context, Collector<AllData> collector) throws Exception {

        String operationType = esChargeIncomingFee.getOperation_type();
        String fldGuid = esChargeIncomingFee.getFld_guid();
        boolean isSend = false;
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(fldGuid);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            mapState.put(fldGuid, esChargeIncomingFee);
            isSend = true;
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeIncomingFee, esChargeIncomingFee));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection, "idx_owe_es_charge_incoming_fee", fldGuid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }
}

