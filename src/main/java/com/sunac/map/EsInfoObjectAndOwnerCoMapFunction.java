package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsInfoObjectAndOwner;
import com.sunac.sink.CommonSink;
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

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/10 8:59 上午
 * @Version 1.0
 */
public class EsInfoObjectAndOwnerCoMapFunction extends KeyedCoProcessFunction<String, AllData, EsInfoObjectAndOwner, AllData> {
    MapState<String, EsInfoObjectAndOwner> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsInfoObjectAndOwnerCoMapFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsInfoObjectAndOwner.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_info_object_and_owner", fldGuid, MakeMd5Utils.makeMd5(allData.getO_fld_guid() , allData.getW_fld_guid() , allData.getFld_area_guid()));
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsInfoObjectAndOwner esInfoObjectAndOwner = mapState.get(MakeMd5Utils.makeMd5(allData.getO_fld_guid() , allData.getW_fld_guid() , allData.getFld_area_guid()));
            if (esInfoObjectAndOwner != null) {
                allData.setOao_fld_guid(esInfoObjectAndOwner.getFld_guid());
                allData.setOao_fld_object_guid(esInfoObjectAndOwner.getFld_object_guid());
                allData.setOao_fld_area_guid(esInfoObjectAndOwner.getFld_area_guid());
                allData.setOao_fld_owner_guid(esInfoObjectAndOwner.getFld_owner_guid());
                allData.setFld_is_current(esInfoObjectAndOwner.getFld_is_current());
                allData.setFld_is_charge(esInfoObjectAndOwner.getFld_is_charge());
                allData.setFld_is_owner(esInfoObjectAndOwner.getFld_is_owner());
                allData.setFld_status(esInfoObjectAndOwner.getFld_status());
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsInfoObjectAndOwner newData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();
        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        boolean isSend = false;
        String pk = newData.getPk();
        EsInfoObjectAndOwner oldData = mapState.get(pk);
        if (newData.compareTo(oldData) <= 0) {
            if (operationType.equals(Config.DELETE)) {
                mapState.remove(pk);
                isSend = true;
            } else if (operationType.equals(Config.INSERT) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
                mapState.put(pk, newData);
                isSend = true;
            }
            if (isSend) {
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsInfoObjectAndOwner, newData));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_info_object_and_owner", pk, sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
            }
        }
    }
}
