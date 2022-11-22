package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeSettleAccountsDetail;
import com.sunac.utils.HikariUtil;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.MakeMd5Utils;
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
 * @Date 2022/10/10 3:12 下午
 * @Version 1.0
 */
public class EsChargeSettleAccountsDetailCoMapFunction extends KeyedCoProcessFunction<String, AllData, EsChargeSettleAccountsDetail, AllData> {
    MapState<String, EsChargeSettleAccountsDetail> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeSettleAccountsDetailCoMapFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeSettleAccountsDetail.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_charge_settle_accounts_detail", fldGuid, MakeMd5Utils.makeMd5(allData.getFld_guid() , allData.getFld_area_guid()));
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        // TODO insert事件
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsChargeSettleAccountsDetail oldData = mapState.get(MakeMd5Utils.makeMd5(allData.getFld_guid() , allData.getFld_area_guid()));
            if (oldData != null && oldData.getFld_status() == 1) {
                allData.setFld_settle_adjust_guid(oldData.getFld_adjust_guid());
                allData.setFld_owner_fee_guid(oldData.getFld_owner_fee_guid());
                allData.setAd_fld_guid(oldData.getFld_guid());
                allData.setAd_fld_status(oldData.getFld_status());
                allData.setAd_fld_area_guid(oldData.getFld_area_guid());
                allData.setAd_fld_main_guid(oldData.getFld_main_guid());
                allData.setAd_fld_create_date(oldData.getFld_create_date());
                allData.setFld_area_guid(oldData.getFld_area_guid());
            } else {
                allData.setAd_fld_main_guid(Config.SINGLE_PARTITION);
                //todo 还不知道能不能关联上呢
                allData.setFld_area_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeSettleAccountsDetail newData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        boolean isSend = false;
        Integer fldStatus = newData.getFld_status();
        String pk = newData.getPk();
        EsChargeSettleAccountsDetail oldData = mapState.get(pk);
        if (fldStatus == 1 && newData.compareTo(oldData) <= 0) {
            if (operationType.equals(Config.DELETE)) {
                mapState.remove(pk);
                isSend = true;
                //TODO:fld_main_guid:用于es_charge_settle_accounts_detail加到宽表然后关联es_charge_settle_accounts_main使用
            } else if (operationType.equals(Config.INSERT) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
                mapState.put(pk, newData);
                isSend = true;
            }
            if (isSend) {
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeSettleAccountsDetail, newData));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context,connection, "idx_owe_es_charge_settle_accounts_detail", pk, sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
                HashMap<String, String> updateInfoMap = newData.getUpdateInfoMap();
                if (operationType.equals(Config.INSERT)) {
                    if (StringUtils.isNotBlank(oldData.getFld_main_guid())){
                        AllData allData = new AllData();
                        allData.setOperation_type("EsChargeSettleAccountsDetail" + "#" + operationType);
                        allData.setAd_fld_main_guid(oldData.getFld_main_guid());
                        allData.setAd_fld_area_guid(oldData.getFld_area_guid());
                        allData.setAd_fld_guid(oldData.getFld_guid());
                        allData.setFld_settle_adjust_guid(oldData.getFld_adjust_guid());
                        allData.setFld_owner_fee_guid(oldData.getFld_owner_fee_guid());

                        // todo 还不知道能不能关联上呢
                        allData.setFld_area_guid(oldData.getFld_area_guid());
                        collector.collect(allData);
                    }
                } else if (operationType.equals(Config.UPDATE) && updateInfoMap.containsKey("fld_main_guid")) {
                    String[] vlaueTuple = updateInfoMap.get("fld_main_guid").split("#");
                    String newValue = vlaueTuple[0];
                    String oldValue = vlaueTuple[1];
                    AllData allData = new AllData();
                    allData.setAd_fld_area_guid(oldData.getFld_area_guid());
                    // todo 还不知道能不能关联上呢
                    allData.setFld_area_guid(oldData.getFld_area_guid());
                    if (StringUtils.isNotBlank(oldValue)){
                        allData.setOperation_type("EsChargeSettleAccountsDetail" + "#" + Config.DELETE);
                        allData.setAd_fld_main_guid(oldValue);
                        collector.collect(allData);
                    }
                    if (StringUtils.isNotBlank(newValue)){
                        allData.setOperation_type("EsChargeSettleAccountsDetail" + "#" + Config.INSERT);
                        allData.setAd_fld_main_guid(newValue);
                        collector.collect(allData);
                    }
                }
            }
        }
    }
}
