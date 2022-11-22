package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsChargeRateResult;
import com.sunac.utils.HikariUtil;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.MakeMd5Utils;
import com.sunac.utils.StringToIntegerUtils;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/9 5:31 下午
 * @Version 1.0
 */
public class EsChargeRateResultCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsChargeRateResult, AllData> {
    MapState<String, EsChargeRateResult> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsChargeRateResultCoProcessFunction.class);
    transient Connection connection = null;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsChargeRateResult.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }
    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsChargeRateResult, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp1 = JdbcUtils.insertTax(connection, "idx_owe_es_charge_rate_result", fldGuid, allData.getFld_amount(), MakeMd5Utils.makeMd5(allData.getFld_area_guid(), allData.getFld_project_guid()));
            connection = tp1.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsChargeRateResult esChargeRateResult = mapState.get(MakeMd5Utils.makeMd5(allData.getFld_area_guid() , allData.getFld_project_guid()));
            if (esChargeRateResult != null) {
                if(StringToIntegerUtils.getInteger(esChargeRateResult.getFld_end_date())<StringToIntegerUtils.getInteger(allData.getFld_finance_date()) || StringToIntegerUtils.getInteger(esChargeRateResult.getFld_start_date()) > StringToIntegerUtils.getInteger(allData.getFld_finance_date())){
                    collector.collect(allData);
                }
                allData.setFld_rate(esChargeRateResult.getFld_general_tax());
                allData.setEcrr_fld_guid(esChargeRateResult.getFld_guid());
                BigDecimal tax = getTax(esChargeRateResult.getFld_general_tax(), allData.getFld_amount());
                allData.setEcrr_fld_general_tax(tax);
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsChargeRateResult esChargeRateResult, KeyedCoProcessFunction<String, AllData, EsChargeRateResult, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = esChargeRateResult.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && esChargeRateResult.getUpdateInfoMap().size() == 0)) {
            return;
        }
        esChargeRateResult.setFld_taxes(new BigDecimal("0"));
        String pk = esChargeRateResult.getPk();
        boolean isSend = false;
        if (operationType.equals(Config.DELETE)) {
            mapState.remove(pk);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            mapState.put(pk, esChargeRateResult);
            isSend = true;
        }
        if (operationType.equals(Config.UPDATE) && esChargeRateResult.getUpdateInfoMap().containsKey("fld_general_tax")){
            String amount = JdbcUtils.queryAmount(esChargeRateResult.getPk());
            BigDecimal tax = getTax(esChargeRateResult.getFld_general_tax(), new BigDecimal(amount));
            esChargeRateResult.setFld_taxes(tax);
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsChargeRateResult, esChargeRateResult));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_charge_rate_result", pk, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }

    private static BigDecimal getTax(BigDecimal fldGeneralTax, BigDecimal fldAmount){
        BigDecimal tmp = fldGeneralTax;
        if (fldGeneralTax == null) {
            tmp = BigDecimal.valueOf(-1);
        }
        if (tmp.compareTo(new BigDecimal("-1.00"))==0){
            tmp = new BigDecimal("-1");
        }
        if (tmp.equals(BigDecimal.valueOf(-1)) || tmp.equals(BigDecimal.valueOf(-2))) {
            tmp = BigDecimal.valueOf(0);
        }
        BigDecimal add = tmp.add(BigDecimal.valueOf(1));

        BigDecimal divide = fldAmount.divide(add, RoundingMode.CEILING);

        return divide.multiply(tmp);
    }
}
