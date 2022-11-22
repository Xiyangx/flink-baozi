package com.sunac.map;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.ow.owdomain.EsCommerceBondMain;
import com.sunac.utils.HikariUtil;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.MakeMd5Utils;
import com.sunac.utils.StringToIntegerUtils;
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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/11 3:52 下午
 * @Version 1.0
 */
public class EsCommerceBondMainCoProcessFunction extends KeyedCoProcessFunction<String, AllData, EsCommerceBondMain, AllData> {

    MapState<String, EsCommerceBondMain> mapState;
    private static final Logger log = LoggerFactory.getLogger(EsCommerceBondMainCoProcessFunction.class);
    transient Connection connection = null;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<>("map_stat", String.class, EsCommerceBondMain.class));
        connection = HikariUtil.getInstance().getConnection();
        log.info("=====================" + this.getClass() + ":创建HikariUtil成功！！！=======================");
    }

    @Override
    public void processElement1(AllData allData, KeyedCoProcessFunction<String, AllData, EsCommerceBondMain, AllData>.Context context, Collector<AllData> collector) throws Exception {

        String operationType = allData.getOperation_type();

        //todo 判断是上个流当中的1方法还是2方法来的数据
        //todo 这里只处理2方法来的数据
        if (allData.getOperation_type().contains("EsCommerceBondFeeObject#")){
            String operationType1 = allData.getOperation_type().split("#")[1];
            ArrayList<String> arr = JdbcUtils.queryFldGuid(allData.getCbfo_fld_object_guid());
            //todo 维护idx
            for (int i = 0; i < arr.size(); i++) {
                Tuple2<Connection, Boolean> tp = JdbcUtils.insertOrIgnore2(connection,"idx_owe_es_commerce_bond_main", arr.get(i), MakeMd5Utils.makeMd5(allData.getCbfo_fld_bond_guid() , allData.getFld_area_guid() , allData.getFld_owner_guid()));
                connection = tp.f0;
                log.info("=====================" + this.getClass() + ":插入fld_guid成功！！！=======================");
            }
            EsCommerceBondMain esCommerceBondMain = mapState.get(MakeMd5Utils.makeMd5(allData.getCbfo_fld_bond_guid() , allData.getFld_area_guid() , allData.getFld_owner_guid()));
            if (null != esCommerceBondMain){
                esCommerceBondMain.setOperation_type(operationType1);
                esCommerceBondMain.setUpdateInfoMap(new HashMap<String, String>());
                String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsCommerceBondMain, esCommerceBondMain));
                Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_commerce_bond_main", MakeMd5Utils.makeMd5(allData.getCbfo_fld_bond_guid() , allData.getFld_area_guid() , allData.getFld_owner_guid()), sql);
                connection = tp2.f0;
                String flag = (tp2.f1 ? "成功" : "失败");
                log.info("=====================" + this.getClass() + ":processElement1:处理来自EsCommerceBondMain因为修改关联键的宽表" + flag + "=======================");
            }
            return;
        }
        if (operationType.equals(Config.PASS)) {
            return;
        }
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsCommerceBondMain esCommerceBondMain = mapState.get(MakeMd5Utils.makeMd5(allData.getCbfo_fld_bond_guid() , allData.getFld_area_guid() , allData.getFld_owner_guid()));
            if (null != esCommerceBondMain) {
                allData.setCbm_fld_guid(esCommerceBondMain.getFld_guid());
                allData.setCbm_fld_area_guid(esCommerceBondMain.getFld_area_guid());
                allData.setCbm_fld_owner_guid(esCommerceBondMain.getFld_owner_guid());
                allData.setBm_end_date(esCommerceBondMain.getFld_end_date());
                allData.setBm_stop_date(esCommerceBondMain.getFld_stop_date());
            }
            collector.collect(allData);
        }
    }

    @Override
    public void processElement2(EsCommerceBondMain newData, KeyedCoProcessFunction<String, AllData, EsCommerceBondMain, AllData>.Context context, Collector<AllData> collector) throws Exception {
        String operationType = newData.getOperation_type();

        if (operationType.equals(Config.PASS) || (operationType.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        boolean isSend = false;
        String pk = newData.getPk();

        if (operationType.equals(Config.DELETE)) {
            mapState.remove(pk);
            isSend = true;
        } else if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsCommerceBondMain oldData = mapState.get(pk);
            if(oldData != null){
                if (StringToIntegerUtils.getInteger(oldData.getFld_end_date()) > StringToIntegerUtils.getInteger(newData.getFld_end_date())){
                    newData.setFld_end_date(oldData.getFld_end_date());
                }
                if (StringToIntegerUtils.getInteger(oldData.getFld_stop_date()) > StringToIntegerUtils.getInteger(newData.getFld_stop_date())){
                    newData.setFld_stop_date(oldData.getFld_stop_date());
                }
            }
            mapState.put(pk, newData);
            isSend = true;
        }
        if (isSend) {
            String sql = JdbcUtils.makeSQL(new Tuple2<>(Config.EsCommerceBondMain, newData));
            Tuple2<Connection, Boolean> tp2 = JdbcUtils.queryByTabAndKey2(context, connection,"idx_owe_es_commerce_bond_main", pk, sql);
            connection = tp2.f0;
            String flag = (tp2.f1 ? "成功" : "失败");
            log.info("=====================" + this.getClass() + ":更改宽表：=======================" + flag);
        }
    }
}
