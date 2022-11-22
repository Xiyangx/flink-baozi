package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsInfoOwner;
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
 * @Date 2022/10/9 3:48 下午
 * @Version 1.0
 */
public class EsInfoOwnerCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsInfoOwner, AllData> {

    MapState<String, EsInfoOwner> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsInfoOwnerCoProcessFunction.class);
    transient Connection connection = null;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsInfoOwner.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsInfoOwner, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_info_owner", fldGuid, allData.getFld_owner_guid());
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsInfoOwner esInfoOwner = mapState.get(allData.getFld_owner_guid());
            if (esInfoOwner != null) {
                allData.setW_fld_guid(esInfoOwner.getFld_guid());
                allData.setFld_phone_number(esInfoOwner.getFld_phone_number());
                allData.setFld_owner_desc(esInfoOwner.getFld_desc());
            } else {
                allData.setW_fld_guid(Config.SINGLE_PARTITION);
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsInfoOwner newData, KeyedCoProcessFunction<String, AllData, EsInfoOwner, AllData>.Context context, Collector<AllData> collector) throws Exception {
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
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsInfoOwner, newData));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_info_owner", fldGuid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }
}
