package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeSettleAccountsMain;
import com.sunac.ow.owdomain.EsChargeTicketPayOperate;
import com.sunac.utils.HikariUtil;
import com.sunac.utils.JdbcUtils;
import org.apache.commons.codec.digest.DigestUtils;
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
 * @Date 2022/10/11 11:18 上午
 * @Version 1.0
 */
public class EsChargeTicketPayOperateCoProcessFunction  extends KeyedCoProcessFunction<String, AllData, EsChargeTicketPayOperate, AllData> {
    MapState<String, EsChargeTicketPayOperate> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeTicketPayOperateCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeTicketPayOperate.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeTicketPayOperate, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_charge_ticket_pay_operate", fldGuid, allData.getCt_fld_operate_guid());
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (allData.getOperation_type().contains("EsChargeTicketPayDetail#")) {
            String operationType1 = operationType.split("#")[1];
            EsChargeTicketPayOperate esChargeTicketPayOperate = mapState.get(allData.getCt_fld_operate_guid());
            if (null != esChargeTicketPayOperate) {
                esChargeTicketPayOperate.setOperation_type(operationType1);
                esChargeTicketPayOperate.setUpdateInfoMap(new HashMap<String, String>());
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeTicketPayOperate, esChargeTicketPayOperate));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_charge_ticket_pay_operate",allData.getCt_fld_operate_guid(), sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
            }
            return;
        }

        // TODO insert事件
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE) ) {
            EsChargeTicketPayOperate esChargeTicketPayOperate = mapState.get(allData.getCt_fld_operate_guid());
            if (esChargeTicketPayOperate != null) {
                allData.setCo_fld_guid(esChargeTicketPayOperate.getFld_guid());
                allData.setFld_ticket_status(esChargeTicketPayOperate.getFld_ticket_status());
                allData.setFld_co_bill_no(esChargeTicketPayOperate.getFld_bill_no());
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeTicketPayOperate newData, KeyedCoProcessFunction<String, AllData, EsChargeTicketPayOperate, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }

        String fld_guid = newData.getFld_guid();
        boolean isSend = false;
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(fld_guid);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            mapState.put(fld_guid, newData);
            isSend = true;
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeTicketPayOperate, newData));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_charge_ticket_pay_operate", fld_guid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }
}
