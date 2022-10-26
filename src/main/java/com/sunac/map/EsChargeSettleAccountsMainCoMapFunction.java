package com.sunac.map;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.entity.EsChargeSettleAccountsMain;
import com.sunac.utils.JdbcUtils;
import com.sunac.utils.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/10 7:29 下午
 * @Version 1.0
 */
public class EsChargeSettleAccountsMainCoMapFunction  extends KeyedCoProcessFunction<String, AllData, EsChargeSettleAccountsMain, AllData> {
    MapState<String, EsChargeSettleAccountsMain> mapState;
    MapState<String, Void> keyMapState;

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        mapState = runtimeContext.getMapState(new MapStateDescriptor<String, EsChargeSettleAccountsMain>("map_stat", String.class, EsChargeSettleAccountsMain.class));
        keyMapState = runtimeContext.getMapState(new MapStateDescriptor<String, Void>("map_stat_2", String.class, Void.class));
    }

    @Override
    public void processElement1(AllData allData, Context context, Collector<AllData> collector) throws Exception {

        if (allData.getOperation_type().equals("EsChargeSettleAccountsDetail" + "#" + "fld_main_guid")) {
            EsChargeSettleAccountsMain esChargeSettleAccountsMain = mapState.get(DigestUtils.md5Hex(allData.getAd_fld_main_guid()+allData.getFld_area_guid()));
            System.out.println("改了detail之后发出来的流的pk："+DigestUtils.md5Hex(allData.getAd_fld_main_guid()+allData.getFld_area_guid()));
            Iterator<String> iterator = keyMapState.keys().iterator();
            boolean isHaveKey = iterator.hasNext();
            if (isHaveKey) {
                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeSettleAccountsMain, esChargeSettleAccountsMain));
                if (null != sql) {
                    while (iterator.hasNext()) {
                        String fld_guid_op = iterator.next();
                        Util.sendData2SlideStream(context, sql, fld_guid_op);
                    }
                }
            }
        }

        // TODO PASS事件 ：把主表主键维护
        String operation_type = allData.getOperation_type();
        String fld_guid = allData.getFld_guid();
        if (!"".equals(fld_guid)) {
            keyMapState.put(fld_guid, null);
        }
        if (operation_type.equals(Config.PASS)) {
            collector.collect(allData);
        }
        if (operation_type.equals(Config.INSERT) || operation_type.equals(Config.UPDATE)) {
            EsChargeSettleAccountsMain oldData = mapState.get(DigestUtils.md5Hex(allData.getAd_fld_main_guid() + allData.getFld_area_guid()));
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

//        // TODO PASS事件 ：把主表主键维护
//        String operation_type = allData.getOperation_type();
//        String fld_guid = allData.getFld_guid();
//        if (null != fld_guid && !"".equals(fld_guid)) {
//            keyMapState.put(fld_guid, null);
//        }
//        if (operation_type.equals(Config.PASS)) {
//            collector.collect(allData);
//        }
//        EsChargeSettleAccountsMain oldData = mapState.get(DigestUtils.md5Hex(allData.getAd_fld_main_guid() + allData.getFld_area_guid()));
//
//        //主流正常的insert
//        if (operation_type.equals(Config.INSERT)) {
//            //能关联上的情况下
//            if (oldData != null && oldData.getFld_examine_status() == 4) {
//                allData.setAm_fld_guid(oldData.getFld_guid());
//                allData.setFld_settle_status(oldData.getFld_settle_status());
//                allData.setFld_attribute(oldData.getFld_attribute());
//                allData.setFld_settle_bill_no(oldData.getFld_bill_no());
//                allData.setFld_main_guid(oldData.getFld_guid());
//                allData.setFld_examine_status(oldData.getFld_examine_status());
//                allData.setAm_fld_area_guid(oldData.getFld_area_guid());
//            }
//        }
//        //上个表修改而来的流
//        //detail表修改的时候。需要一起修改main表的字段
//        if (operation_type.equals(Config.UPDATE3)) {
//            allData.setOperation_type(Config.UPDATE);
//            //能关联上的情况下，两个表的字段一起修改
//            if (oldData != null && oldData.getFld_examine_status() == 4) {
//                System.out.println("我可要改了啊～");
//                allData.setAm_fld_guid(oldData.getFld_guid());
//                allData.setFld_settle_status(oldData.getFld_settle_status());
//                allData.setFld_attribute(oldData.getFld_attribute());
//                allData.setFld_settle_bill_no(oldData.getFld_bill_no());
//                allData.setFld_main_guid(oldData.getFld_guid());
//                allData.setFld_examine_status(oldData.getFld_examine_status());
//                allData.setAm_fld_area_guid(oldData.getFld_area_guid());
//                HashMap<String, String> updateInfoMap = allData.getUpdateInfoMap();
//                updateInfoMap.put("am_fld_guid",oldData.getFld_guid());
//                updateInfoMap.put("fld_settle_bill_no",oldData.getFld_bill_no());
//                updateInfoMap.put("am_fld_area_guid",oldData.getFld_area_guid());
//                updateInfoMap.put("fld_examine_status",oldData.getFld_examine_status().toString());
//                updateInfoMap.put("fld_settle_status",oldData.getFld_settle_status().toString());
//                updateInfoMap.put("fld_attribute",oldData.getFld_attribute().toString());
//                updateInfoMap.put("fld_examine_date",oldData.getFld_examine_status().toString());
//                allData.setUpdateInfoMap(updateInfoMap);
//                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.Add1, allData));
//                Util.sendData2SlideStream(context, sql, allData.getFld_guid());
//                return;
//            }
//            //关联不上的情况下，只写入detail表的字段。不更改main表的字段
//            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.Add1, allData));
//            Util.sendData2SlideStream(context, sql, allData.getFld_guid());
//            return;
//        }
//        //detail表修改的时候。需要一起修改main表的字段
//        if (operation_type.equals(Config.INSERT3)) {
//            allData.setOperation_type(Config.UPDATE);
//            //能关联上的情况下，两个表的字段一起修改
//            if (oldData != null && oldData.getFld_examine_status() == 4) {
//                System.out.println("INSERT3 我可要改了啊～");
//                allData.setAm_fld_guid(oldData.getFld_guid());
//                allData.setFld_settle_status(oldData.getFld_settle_status());
//                allData.setFld_attribute(oldData.getFld_attribute());
//                allData.setFld_settle_bill_no(oldData.getFld_bill_no());
//                allData.setFld_main_guid(oldData.getFld_guid());
//                allData.setFld_examine_status(oldData.getFld_examine_status());
//                allData.setAm_fld_area_guid(oldData.getFld_area_guid());
//                HashMap<String, String> updateInfoMap = allData.getUpdateInfoMap();
//                updateInfoMap.put("am_fld_guid",oldData.getFld_guid());
//                updateInfoMap.put("fld_bill_no",oldData.getFld_bill_no());
//                updateInfoMap.put("am_fld_area_guid",oldData.getFld_area_guid());
//                updateInfoMap.put("fld_examine_status",oldData.getFld_examine_status().toString());
//                updateInfoMap.put("fld_settle_status",oldData.getFld_settle_status().toString());
//                updateInfoMap.put("fld_attribute",oldData.getFld_attribute().toString());
//                allData.setUpdateInfoMap(updateInfoMap);
//                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.Add1, allData));
//                Util.sendData2SlideStream(context, sql, allData.getFld_guid());
//                return;
//            }
//            //关联不上的情况下，只写入detail表的字段。不更改main表的字段
//            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.Add1, allData));
//            Util.sendData2SlideStream(context, sql, allData.getFld_guid());
//            return;
//        }
//
//        //detail表修改的时候。需要一起修改main表的字段
//        if (operation_type.equals(Config.DELETE3)) {
//            allData.setOperation_type(Config.UPDATE);
//            //能关联上的情况下，两个表的字段一起修改
//            if (oldData != null && oldData.getFld_examine_status() == 4) {
//                System.out.println("INSERT3 我可要改了啊～");
//                HashMap<String, String> updateInfoMap = allData.getUpdateInfoMap();
//                updateInfoMap.put("fld_settle_status","");
//                updateInfoMap.put("fld_attribute","");
//                updateInfoMap.put("fld_settle_bill_no","");
//                updateInfoMap.put("fld_main_guid","");
//                updateInfoMap.put("fld_examine_status","");
//                allData.setUpdateInfoMap(updateInfoMap);
//                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.Add1, allData));
//                Util.sendData2SlideStream(context, sql, allData.getFld_guid());
//                return;
//            }
//            //关联不上的情况下，只写入detail表的字段。不更改main表的字段
//
//            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.Add1, allData));
//            Util.sendData2SlideStream(context, sql, allData.getFld_guid());
//            return;
//        }
//
//        collector.collect(allData);
    }

    @Override
    public void processElement2(EsChargeSettleAccountsMain newData, Context context, Collector<AllData> collector) throws Exception {
        String operation_type = newData.getOperation_type();
        if (operation_type.equals(Config.PASS) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() == 0)) {
            return;
        }
        Iterator<String> iterator = keyMapState.keys().iterator();
        boolean isHaveKey = iterator.hasNext();
        boolean isSend = false;
        boolean fld_examine_status = newData.getFld_examine_status() == 4;

        String pk = newData.getPk();
        System.out.println("main的pk:" + pk);
        if (fld_examine_status) {
            if (operation_type.equals(Config.DELETE)) {
                mapState.remove(pk);
                isSend = true;
            } else if (operation_type.equals(Config.INSERT) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
                mapState.put(pk, newData);
                isSend = true;
            }
            if (isSend && isHaveKey) {
                String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeSettleAccountsMain, newData));
                if (null != sql) {
                    while (iterator.hasNext()) {
                        String fld_guid_op = iterator.next();
                        Util.sendData2SlideStream(context, sql, fld_guid_op);
                    }
                }
            }
        }
//        if (operation_type.equals(Config.DELETE)) {
//            mapState.remove(pk);
//            isSend = true;
//        } else if (operation_type.equals(Config.INSERT) || (operation_type.equals(Config.UPDATE) && newData.getUpdateInfoMap().size() > 0)) {
//            mapState.put(pk, newData);
//            isSend = true;
//        }
//        if (isSend && isHaveKey) {
//            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.EsChargeSettleAccountsMain, newData));
//            if (null != sql) {
//                while (iterator.hasNext()) {
//                    String fld_guid_op = iterator.next();
//                    Util.sendData2SlideStream(context, sql, fld_guid_op);
//                }
//            }
//        }

    }
}
