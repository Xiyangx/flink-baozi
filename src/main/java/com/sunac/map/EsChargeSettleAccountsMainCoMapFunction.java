package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeSettleAccountsMain;
import com.sunac.utils.HikariUtil;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.MakeMd5Utils;
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
 * @Date 2022/10/10 7:29 下午
 * @Version 1.0
 */
public class EsChargeSettleAccountsMainCoMapFunction extends KeyedCoProcessFunction<String, AllData, EsChargeSettleAccountsMain, AllData> {
    MapState<String, EsChargeSettleAccountsMain> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeSettleAccountsMainCoMapFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeSettleAccountsMain.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();

        // TODO PASS事件 ：把主表主键维护
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_charge_settle_accounts_main", fldGuid, MakeMd5Utils.makeMd5(allData.getAd_fld_main_guid() , allData.getFld_area_guid()));
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (allData.getOperation_type().contains("EsChargeSettleAccountsDetail#")) {
            String operationType1 = operationType.split("#")[1];
            EsChargeSettleAccountsMain esChargeSettleAccountsMain = mapState.get(MakeMd5Utils.makeMd5(allData.getAd_fld_main_guid() , allData.getAd_fld_area_guid()));
            if (null != esChargeSettleAccountsMain) {
                esChargeSettleAccountsMain.setOperation_type(operationType1);
                esChargeSettleAccountsMain.setUpdateInfoMap(new HashMap<String, String>());
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeSettleAccountsMain, esChargeSettleAccountsMain));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_charge_settle_accounts_main", MakeMd5Utils.makeMd5(allData.getAd_fld_main_guid() , allData.getAd_fld_area_guid()), sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
            }
            return;
        }

        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsChargeSettleAccountsMain oldData = mapState.get(MakeMd5Utils.makeMd5(allData.getAd_fld_main_guid() , allData.getFld_area_guid()));
            if (oldData != null && oldData.getFld_examine_status() == 4) {
                allData.setAm_fld_guid(oldData.getFld_guid());
                allData.setFld_settle_status(oldData.getFld_settle_status());
                allData.setFld_attribute(oldData.getFld_attribute());
                allData.setFld_settle_bill_no(oldData.getFld_bill_no());
                allData.setFld_main_guid(oldData.getFld_guid());
                allData.setFld_examine_status(oldData.getFld_examine_status());
                allData.setAm_fld_area_guid(oldData.getFld_area_guid());
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeSettleAccountsMain newData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        boolean isSend = false;
        boolean fldExamineStatus = newData.getFld_examine_status() == 4;
        String pk = newData.getPk();

        if (fldExamineStatus) {
            if (operationType.equals(Config.DELETE)) {
                mapState.remove(pk);
                isSend = true;
            } else if (operationType.equals(Config.INSERT) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
                mapState.put(pk, newData);
                isSend = true;
            }
            if (isSend) {
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeSettleAccountsMain, newData));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_charge_settle_accounts_main", pk, sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
            }
        }
    }
}
