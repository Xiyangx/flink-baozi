package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeSettleAccountsDetail;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/10 3:12 下午
 * @Version 1.0
 */
public class EsChargeSettleAccountsDetailCoMapFunction extends KeyedCoProcessFunction<String, AllData, EsChargeSettleAccountsDetail, AllData> {
    MapState<String, EsChargeSettleAccountsDetail> mapState;
    MapState<String, AllData> keyMapState;
    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeSettleAccountsDetail>("map_stat", String.class, EsChargeSettleAccountsDetail.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, AllData>("map_stat_2", String.class, AllData.class));

    }
    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {
        String operationType = allData.getOperation_type();
        String fldGuid = allData.getFld_guid();
        if (null != fldGuid && !"".equals(fldGuid)) {
            keyMapState.put(fldGuid, null);
        }
        if (operationType.equals(Config.PASS)) {
            collector.collect(allData);
        }
        // TODO insert事件
        if (operationType.equals(Config.INSERT) || operationType.equals(Config.UPDATE)) {
            EsChargeSettleAccountsDetail oldData = mapState.get(DigestUtils.md5Hex(allData.getFld_guid() + allData.getFld_area_guid()));
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
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        System.out.println("isHaveKey = " + isHaveKey);
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
                System.out.println("insert进来了");
                isSend = true;

                if (newData.getUpdateInfoMap().containsKey("fld_main_guid")) {
                    AllData allData = new AllData();
                    allData.setOperation_type("EsChargeSettleAccountsDetail" + "#" + "fld_main_guid");
                    allData.setAd_fld_main_guid(oldData.getFld_main_guid());
                    allData.setAd_fld_area_guid(oldData.getFld_area_guid());
                    allData.setAd_fld_guid(oldData.getFld_guid());
                    allData.setFld_settle_adjust_guid(oldData.getFld_adjust_guid());
                    allData.setFld_owner_fee_guid(oldData.getFld_owner_fee_guid());
                    allData.setFld_area_guid(oldData.getFld_area_guid());
                    collector.collect(allData);
                }
            }
            if (isSend && isHaveKey) {
                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeSettleAccountsDetail, newData));
                if (null != sql) {
                    while (iterator.hasNext()) {
                        String fldGuidOp = iterator.next();
                        Util.sendData2SlideStream(context, sql, fldGuidOp);
                    }
                }
            }
        }
    }
}
