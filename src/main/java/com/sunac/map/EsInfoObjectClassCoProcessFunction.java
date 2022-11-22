package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsInfoObjectClass;
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
 * @Date 2022/10/9 7:36 下午
 * @Version 1.0
 */
public class EsInfoObjectClassCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsInfoObjectClass, AllData> {

    MapState<String, EsInfoObjectClass> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsInfoObjectClassCoProcessFunction.class);
    transient Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsInfoObjectClass.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsInfoObjectClass, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();

        if (StringUtils.isNotBlank(fldGuid)) {
            Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_info_object_class", fldGuid, allData.getO_fld_class_guid());
            connection = tp.f0;
            log.info("=====================" + this.getClass() + ":插入fldGuid成功！！！=======================");
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.contains("EsInfoObject#")) {
            EsInfoObjectClass esInfoObjectClass = mapState.get(allData.getO_fld_class_guid());
            if (null != esInfoObjectClass) {
                esInfoObjectClass.setOperation_type(operationType.split("#")[1]);
                esInfoObjectClass.setUpdateInfoMap(new HashMap<String, String>());
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsInfoObjectClass, esInfoObjectClass));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_info_object_class", allData.getO_fld_class_guid(), sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":processElement1:处理来自EsInfoObject因为修改fld_object_class_guid的宽表" + flag + "=======================");
            }
            return;
        }

        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsInfoObjectClass esInfoObjectClass = mapState.get(allData.getO_fld_class_guid());
            if (esInfoObjectClass != null) {
                allData.setC_fld_guid(esInfoObjectClass.getFld_guid());
                allData.setFld_object_class_name(esInfoObjectClass.getFld_name());
            }
            collector.collect(allData);
        }

    }

    @Override
    public void processElement2(EsInfoObjectClass newData, KeyedCoProcessFunction<String, AllData, EsInfoObjectClass, AllData>.Context context, Collector<AllData> collector) throws Exception {
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
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsInfoObjectClass, newData));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_info_object_class", fldGuid, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }
}
