package com.sunac.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunac.Config;
import com.sunac.domain.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.jdbc.split.JdbcGenericParameterValuesProvider;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.utils
 * @date:2022/9/5
 */
public class Util {
    public static void sendData2SlideStream(KeyedCoProcessFunction.Context context, String sql, String fld_guid_op) {

        //        Tuple3<String, String, CommonDomain> tp = new Tuple3<>(fullClassName, fld_guid_op, data);
        //        TypeInformation.of(new TypeHint<Tuple3<String, String, CommonDomain>>() {})

        Tuple2<String, String> tp = new Tuple2<>(sql, fld_guid_op);
        context.output(new OutputTag<>(Config.SIDE_STREAM, TypeInformation.of(new TypeHint<Tuple2<String, String>>() {
        })), tp);
    }

    public static JdbcGenericParameterValuesProvider getParameters(Statement stat, String querySql, String segmentCol) throws SQLException {
        ResultSet res = stat.executeQuery(querySql);
        ArrayList<String> list = new ArrayList<>();
        while (res.next()) {
            list.add(res.getString(segmentCol));
        }
        Serializable[][] queryParameters = new String[list.size()][1];
        for (int i = 0; i < list.size(); i++) {
            queryParameters[i] = new String[]{list.get(i)};
        }
        return new JdbcGenericParameterValuesProvider(queryParameters);
    }

    public static AllData getAllDataObj(AllData allData, JSONObject objArray) {
        allData.setFld_guid(objArray.getString("fld_guid"));
        allData.setFld_create_user(objArray.getString("fld_create_user"));
        allData.setFld_create_date(objArray.getString("fld_create_date"));
        allData.setFld_modify_user(objArray.getString("fld_modify_user"));
        allData.setFld_modify_date(objArray.getString("fld_modify_date"));
        allData.setFld_tenancy(objArray.getString("fld_tenancy"));
        allData.setFld_incoming_fee_guid(objArray.getString("fld_incoming_fee_guid"));
        allData.setFld_owner_fee_guid(objArray.getString("fld_owner_fee_guid"));
        allData.setFld_area_guid(objArray.getString("fld_area_guid"));
        allData.setFld_object_guid(objArray.getString("fld_object_guid"));
        allData.setFld_owner_guid(objArray.getString("fld_owner_guid"));
        allData.setFld_project_guid(objArray.getString("fld_project_guid"));
        allData.setFld_pay_mode_guid(objArray.getString("fld_pay_mode_guid"));
        allData.setFld_owner_date(objArray.getString("fld_owner_date"));
        allData.setFld_allot_date(objArray.getString("fld_allot_date"));
        allData.setFld_finance_date(objArray.getString("fld_finance_date"));
        allData.setFld_point_date(objArray.getString("fld_point_date"));
        allData.setFld_desc(objArray.getString("fld_desc"));
        allData.setFld_total(new BigDecimal(objArray.getString("fld_total")));
        allData.setFld_amount(new BigDecimal(objArray.getString("fld_amount")));
        allData.setFld_late_fee(new BigDecimal(objArray.getString("fld_late_fee")));
        allData.setFld_tax_amount(new BigDecimal(objArray.getString("fld_tax_amount")));
        allData.setFld_tax(objArray.getString("fld_tax"));
        allData.setFld_bill_no(objArray.getString("fld_bill_no"));
        allData.setFld_bill_type(objArray.getString("fld_bill_type"));
        allData.setFld_bill_no2(objArray.getString("fld_bill_no2"));
        allData.setFld_operator_guid(objArray.getString("fld_operator_guid"));
        allData.setFld_operate_date(objArray.getString("fld_operate_date"));
        allData.setFld_back_fee_guid(objArray.getString("fld_back_fee_guid"));
        allData.setFld_cancel(Integer.parseInt(objArray.getString("fld_cancel")));
        allData.setFld_start_date(objArray.getString("fld_start_date"));
        allData.setFld_end_date(objArray.getString("fld_end_date"));
        allData.setFld_back_guid(objArray.getString("fld_back_guid"));
        return allData;
    }

    public static EsChargeIncomingFee getEsChargeIncomingFeeObj(EsChargeIncomingFee esChargeIncomingFee, JSONObject objArray) {
        esChargeIncomingFee.setFld_guid(objArray.getString("fld_guid"));
        esChargeIncomingFee.setFld_busi_type(Integer.parseInt(objArray.getString("fld_busi_type")));
        esChargeIncomingFee.setFld_remark(objArray.getString("fld_remark"));
        esChargeIncomingFee.setFld_cancel_date(objArray.getString("fld_cancel_date"));
        esChargeIncomingFee.setFld_cancel_guid(objArray.getString("fld_cancel_guid"));
        esChargeIncomingFee.setFld_number(objArray.getString("fld_number"));
        esChargeIncomingFee.setFld_checkin(Integer.parseInt(objArray.getString("fld_checkin")));
        esChargeIncomingFee.setFld_cancel_me(objArray.getString("fld_cancel_me"));
        esChargeIncomingFee.setFld_modify_date(objArray.getString("fld_modify_date"));
        return esChargeIncomingFee;
    }

    private static KafkaSourceBuilder<String> buildKafkaSource(String topic) {
        return KafkaSource.<String>builder()
                // 设置订阅的目标主题
                .setTopics(topic)
                // 设置消费者组id
                .setGroupId("g1")
                // 设置kafka服务器地址
                .setBootstrapServers("172.17.44.27:6667,172.17.44.28:6667,172.17.44.29:6667")
                // 起始消费位移的指定：
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                // 设置value数据的反序列化器://JSONDeserializationSchema
                .setValueOnlyDeserializer(new SimpleStringSchema()).setProperty("auto.offset.commit", "true");
    }

    public static DataStreamSource<String> getKafkaSource(StreamExecutionEnvironment env, String topic) {
        return env.fromSource(Util.buildKafkaSource(topic).build(), WatermarkStrategy.noWatermarks(), topic);
    }

    public static AllData parseJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        AllData allData = new AllData();

        if ("INSERT".equals(optType)) {
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            allData.setOperation_type("INSERT");
            allData.setFld_guid(objArray.getString("fld_guid"));
            allData.setFld_create_user(objArray.getString("fld_create_user"));
            allData.setFld_create_date(objArray.getString("fld_create_date"));
            allData.setFld_modify_user(objArray.getString("fld_modify_user"));
            allData.setFld_modify_date(objArray.getString("fld_modify_date"));
            allData.setFld_tenancy(objArray.getString("fld_tenancy"));
            allData.setFld_incoming_fee_guid(objArray.getString("fld_incoming_fee_guid"));
            allData.setFld_owner_fee_guid(objArray.getString("fld_owner_fee_guid"));
            allData.setFld_area_guid(objArray.getString("fld_area_guid"));
            allData.setFld_object_guid(objArray.getString("fld_object_guid"));
            allData.setFld_owner_guid(objArray.getString("fld_owner_guid"));
            allData.setFld_project_guid(objArray.getString("fld_project_guid"));
            allData.setFld_pay_mode_guid(objArray.getString("fld_pay_mode_guid"));
            allData.setFld_owner_date(objArray.getString("fld_owner_date"));
            allData.setFld_allot_date(objArray.getString("fld_allot_date"));
            allData.setFld_finance_date(objArray.getString("fld_finance_date"));
            allData.setFld_point_date(objArray.getString("fld_point_date"));
            allData.setFld_desc(objArray.getString("fld_desc"));
            allData.setFld_total(new BigDecimal(objArray.getString("fld_total")));
            allData.setFld_amount(new BigDecimal(objArray.getString("fld_amount")));
            allData.setFld_late_fee(new BigDecimal(objArray.getString("fld_late_fee")));
            allData.setFld_tax_amount(new BigDecimal(objArray.getString("fld_tax_amount")));
            allData.setFld_tax(objArray.getString("fld_tax"));
            allData.setFld_bill_no(objArray.getString("fld_bill_no"));
            allData.setFld_bill_type(objArray.getString("fld_bill_type"));
            allData.setFld_bill_no2(objArray.getString("fld_bill_no2"));
            allData.setFld_operator_guid(objArray.getString("fld_operator_guid"));
            allData.setFld_operate_date(objArray.getString("fld_operate_date"));
            allData.setFld_back_fee_guid(objArray.getString("fld_back_fee_guid"));
            allData.setFld_cancel(Integer.parseInt(objArray.getString("fld_cancel")));
            allData.setFld_start_date(objArray.getString("fld_start_date"));
            allData.setFld_end_date(objArray.getString("fld_end_date"));
            allData.setFld_back_guid(objArray.getString("fld_back_guid"));

        } else if ("DELETE".equals(optType)) {

        }
        return allData;
    }

    public static EsChargeOwnerFee parseEsChargeOwnerFeeJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeOwnerFee esChargeOwnerFee = new EsChargeOwnerFee();
        if ("INSERT".equals(optType)) {
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
//                fld_area_guid
//                fld_reason_remark,
//                fld_price,
//                fld_rebate,
//                fld_resource AS fld_owner_fee_resource,
//                fld_desc AS fld_owner_fee_desc,
//                fld_adjust_guid as fld_fee_adjust_guid,
            String fld_guid = objArray.getString("fld_guid");
            String fld_area_guid = objArray.getString("fld_area_guid");
            String fld_reason_remark = objArray.getString("fld_reason_remark");
            String fld_rebate = objArray.getString("fld_rebate");
            String fld_owner_fee_resource = objArray.getString("fld_resource");
            String fld_fee_adjust_guid = objArray.getString("fld_adjust_guid");
            String fld_desc = objArray.getString("fld_desc");
            //  md5(CONCAT(fld_guid,fld_area_guid)) pk,
            String pk = DigestUtils.md5Hex(fld_guid + fld_area_guid);
            esChargeOwnerFee.setPk(pk);
            esChargeOwnerFee.setFld_guid(fld_guid);
            esChargeOwnerFee.setFld_area_guid(fld_area_guid);
            esChargeOwnerFee.setFld_reason_remark(fld_reason_remark);
            esChargeOwnerFee.setFld_rebate(new BigDecimal(fld_rebate));
            esChargeOwnerFee.setFld_resource(fld_owner_fee_resource);
            esChargeOwnerFee.setFld_adjust_guid(fld_fee_adjust_guid);
            esChargeOwnerFee.setFld_desc(fld_desc);
        }

        return esChargeOwnerFee;
    }

    public static EsInfoOwner parseEsInfoOwnerJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsInfoOwner esInfoOwner = new EsInfoOwner();

//        fld_guid,
//        fld_name AS fld_owner_name,
//        fld_desc AS fld_owner_desc,

        if ("INSERT".equals(optType)) {
            esInfoOwner.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esInfoOwner.setFld_guid(objArray.getString("fld_guid"));
            esInfoOwner.setFld_name(objArray.getString("fld_name"));
            esInfoOwner.setFld_desc(objArray.getString("fld_desc"));
        }
        return esInfoOwner;
    }

    public static EsChargeProject parseEsChargeProjectJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeProject esChargeProject = new EsChargeProject();
        //        fld_guid		es_charge_project	ON p.fld_guid = d.fld_project_guid
        //        fld_name AS fld_project_name,		es_charge_project
        //        fld_object_type ,		es_charge_project
        if ("INSERT".equals(optType)) {
            esChargeProject.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeProject.setFld_guid(objArray.getString("fld_guid"));
            esChargeProject.setFld_name(objArray.getString("fld_name"));
            esChargeProject.setFld_object_type(objArray.getString("fld_object_type"));
        }
        return esChargeProject;
    }

    public static EsChargePayModel parseEsChargePayModelJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargePayModel esChargePayModel = new EsChargePayModel();
  /*      fld_guid		es_charge_pay_mode	ON m.fld_guid = d.fld_pay_mode_guid
          fld_name AS fld_pay_mode_name,		es_charge_pay_mode
          fld_resource,		es_charge_pay_mode
          fld_pre_pay,		es_charge_pay_mode*/
        if ("INSERT".equals(optType)) {
            esChargePayModel.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargePayModel.setFld_guid(objArray.getString("fld_guid"));
            esChargePayModel.setFld_name(objArray.getString("fld_name"));
            esChargePayModel.setFld_resource(objArray.getString("fld_nafld_resourceme"));
            esChargePayModel.setFld_pre_pay(Integer.parseInt(objArray.getString("fld_pre_pay")));
        }
        return esChargePayModel;
    }

    public static EsInfoObjectPark parseEsInfoObjectParkJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsInfoObjectPark esInfoObjectPark = new EsInfoObjectPark();
//                fld_guid
//                fld_area_guid
//                fld_cw_category,
        if ("INSERT".equals(optType)) {
            esInfoObjectPark.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            String fld_guid = objArray.getString("fld_guid");
            String fld_area_guid = objArray.getString("fld_area_guid");
            String pk = DigestUtils.md5Hex(fld_guid + fld_area_guid);
            esInfoObjectPark.setPk(pk);
            esInfoObjectPark.setFld_guid(fld_guid);
            esInfoObjectPark.setFld_area_guid(fld_area_guid);
            esInfoObjectPark.setFld_cw_category(objArray.getString("fld_cw_category"));
        }
        return esInfoObjectPark;
    }

    public static EsInfoObjectClass parseEsInfoObjectClassJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsInfoObjectClass esInfoObjectClass = new EsInfoObjectClass();
//                fld_guid,
//                fld_name fld_object_class_name,
        if ("INSERT".equals(optType)) {
            esInfoObjectClass.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esInfoObjectClass.setFld_guid(objArray.getString("fld_guid"));
            esInfoObjectClass.setFld_name(objArray.getString("fld_name"));
        }
        return esInfoObjectClass;
    }

    public static EsInfoObjectAndOwnerDomain parseEsInfoObjectAndOwnerJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsInfoObjectAndOwnerDomain esInfoObjectAndOwnerDomain = new EsInfoObjectAndOwnerDomain();
//                fld_area_guid
//                fld_object_guid
//                fld_owner_guid
//                fld_is_owner,
//                fld_room_type,
        if ("INSERT".equals(optType)) {
            esInfoObjectAndOwnerDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            //   ON d.fld_area_guid = ao.fld_area_guid
            // * AND ao.fld_object_guid = d.fld_object_guid
            // * AND ao.fld_owner_guid = d.fld_owner_guid
            String fld_guid = objArray.getString("fld_guid");
            String fld_area_guid = objArray.getString("fld_area_guid");
            String fld_object_guid = objArray.getString("fld_object_guid");
            String fld_owner_guid = objArray.getString("fld_owner_guid");
            String pk = DigestUtils.md5Hex(fld_area_guid + fld_object_guid + fld_owner_guid);
            esInfoObjectAndOwnerDomain.setPk(pk);
            esInfoObjectAndOwnerDomain.setFld_guid(fld_guid);
            esInfoObjectAndOwnerDomain.setFld_area_guid(fld_area_guid);
            esInfoObjectAndOwnerDomain.setFld_object_guid(fld_object_guid);
            esInfoObjectAndOwnerDomain.setFld_owner_guid(fld_owner_guid);
            esInfoObjectAndOwnerDomain.setFld_is_owner(Integer.parseInt(objArray.getString("fld_is_owner")));
            esInfoObjectAndOwnerDomain.setFld_room_type(objArray.getString("fld_room_type"));
        }
        return esInfoObjectAndOwnerDomain;
    }

    public static EsChargeBill parseEsChargeBillJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeBill esChargeBill = new EsChargeBill();
        if ("INSERT".equals(optType)) {
            esChargeBill.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            //        fld_guid
            //        fld_type_guid
            //        fld_status AS fld_bill_status,
            //        fld_bill_code,
            esChargeBill.setFld_guid(objArray.getString("fld_guid"));
            esChargeBill.setFld_type_guid(objArray.getString("fld_type_guid"));
            esChargeBill.setFld_status(Integer.parseInt(objArray.getString("fld_status")));
            esChargeBill.setFld_bill_code(objArray.getString("fld_bill_code"));

        }
        return esChargeBill;
    }

    public static EsChargeBillType parseEsChargeBillTypeJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeBillType esChargeBillType = new EsChargeBillType();
        if ("INSERT".equals(optType)) {
            esChargeBillType.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
//                    t.fld_name,
//                    t.fld_guid
            esChargeBillType.setFld_guid(objArray.getString("fld_guid"));
            esChargeBillType.setFld_category(Integer.parseInt(objArray.getString("fld_category")));
            esChargeBillType.setFld_name(objArray.getString("fld_name"));
        }
        return esChargeBillType;
    }

    public static EsChargeSettleAccountsDetail parseEsChargeSettleAccountsDetailJson(String s) {
//        ON ( ad.fld_owner_fee_guid = d.fld_owner_fee_guid
//          OR ad.fld_adjust_guid    = d.fld_owner_fee_guid
//        AND ad.fld_area_guid = d.fld_area_guid
//        AND ad.fld_status = 1

//          )
//
//          -- 该语句可能重复，需要去重（
//          分组：fld_area_guid、
//               fld_owner_fee_guid、
//               fld_adjust_guid
//          排序：fld_create_date desc）
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeSettleAccountsDetail esChargeSettleAccountsDetail = new EsChargeSettleAccountsDetail();
        if ("INSERT".equals(optType)) {
            esChargeSettleAccountsDetail.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            String fld_owner_fee_guid = objArray.getString("fld_owner_fee_guid");
            String fld_adjust_guid = objArray.getString("fld_adjust_guid");
            String fld_area_guid = objArray.getString("fld_area_guid");
            String pk1 = DigestUtils.md5Hex(fld_owner_fee_guid + fld_area_guid);
            String pk2 = DigestUtils.md5Hex(fld_adjust_guid + fld_area_guid);
            esChargeSettleAccountsDetail.setPk1(pk1);
            esChargeSettleAccountsDetail.setPk1(pk2);
//        fld_owner_fee_guid
//        fld_adjust_guid,
//        fld_area_guid
//        fld_main_guid
//        fld_status,
//        fld_settle_status,
//        fld_bill_no AS fld_accounts_detail_bill_no,
            esChargeSettleAccountsDetail.setFld_owner_fee_guid(objArray.getString("fld_owner_fee_guid"));
            esChargeSettleAccountsDetail.setFld_adjust_guid(objArray.getString("fld_adjust_guid"));
            esChargeSettleAccountsDetail.setFld_area_guid(objArray.getString("fld_area_guid"));
            esChargeSettleAccountsDetail.setFld_main_guid(objArray.getString("fld_main_guid"));
            esChargeSettleAccountsDetail.setFld_status(Integer.parseInt(objArray.getString("fld_status")));
            esChargeSettleAccountsDetail.setFld_settle_status(Integer.parseInt(objArray.getString("fld_settle_status")));
            esChargeSettleAccountsDetail.setFld_bill_no(objArray.getString("fld_bill_no"));
        }
        return esChargeSettleAccountsDetail;
    }

    public static EsChargeSettleAccountsMain parseEsChargeSettleAccountsMainJson(String s) {
//        fld_guid
//        fld_area_guid
//        fld_attribute,
//        fld_examine_status,

        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeSettleAccountsMain esChargeSettleAccountsMain = new EsChargeSettleAccountsMain();
        if ("INSERT".equals(optType)) {
            esChargeSettleAccountsMain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            String fld_guid = objArray.getString("fld_guid");
            String fld_area_guid = objArray.getString("fld_area_guid");
            esChargeSettleAccountsMain.setPk(DigestUtils.md5Hex(fld_guid + fld_area_guid));
            esChargeSettleAccountsMain.setFld_area_guid(fld_area_guid);
            esChargeSettleAccountsMain.setFld_guid(fld_guid);
            esChargeSettleAccountsMain.setFld_attribute(Integer.parseInt(objArray.getString("fld_attribute")));
            esChargeSettleAccountsMain.setFld_examine_status(Integer.parseInt(objArray.getString("fld_examine_status")));
        }
        return esChargeSettleAccountsMain;
    }

    public static EsChargeProjectPeriodJoinDomain parseEsChargeProjectPeriodJoinDomainJson(String s) {
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeProjectPeriodJoinDomain esChargeProjectPeriodJoinDomain = new EsChargeProjectPeriodJoinDomain();
        if ("INSERT".equals(optType)) {
            esChargeProjectPeriodJoinDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
//            fld_project_guid
//            fld_period_guid
            esChargeProjectPeriodJoinDomain.setFld_project_guid(objArray.getString("fld_project_guid"));
            esChargeProjectPeriodJoinDomain.setFld_period_guid(objArray.getString("fld_period_guid"));

        }
        return esChargeProjectPeriodJoinDomain;
    }

    public static EsChargeProjectPeriodDomain parseEsChargeProjectPeriodDomainJson(String s) {
//        fld_type
//        fld_name as fld_project_period_name,

        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeProjectPeriodDomain esChargeProjectPeriodDomain = new EsChargeProjectPeriodDomain();
        if ("INSERT".equals(optType)) {
            esChargeProjectPeriodDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            // fld_guid
            // fld_type
            // fld_name as fld_project_period_name,
            esChargeProjectPeriodDomain.setFld_guid(objArray.getString("fld_guid"));
            esChargeProjectPeriodDomain.setFld_type(Integer.parseInt(objArray.getString("fld_type")));
            esChargeProjectPeriodDomain.setFld_name(objArray.getString("fld_name"));

        }
        return esChargeProjectPeriodDomain;
    }

    public static EsChargeTwoBalanceDomain parseEsChargeTwoBalanceDomainJson(String s) {
//        fld_guid as fld_balance_guid,
//        fld_date as two_fld_date,
//        fld_create_user as two_fld_create_user,
//        fld_hand_in_guid,
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeTwoBalanceDomain esChargeTwoBalanceDomain = new EsChargeTwoBalanceDomain();
        if ("INSERT".equals(optType)) {
            esChargeTwoBalanceDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeTwoBalanceDomain.setFld_guid(objArray.getString("fld_guid"));
            esChargeTwoBalanceDomain.setFld_date(objArray.getString("fld_date"));
            esChargeTwoBalanceDomain.setFld_create_user(objArray.getString("fld_create_user"));
            esChargeTwoBalanceDomain.setFld_hand_in_guid(objArray.getString("fld_hand_in_guid"));
        }
        return esChargeTwoBalanceDomain;
    }

    public static EsChargeHandInRecordDomain parseEsChargeHandInRecordDomainJson(String s) {
//        fld_guid
//        fld_hand_in,
//        fld_start_date as fld_hand_in_start_date,
//        fld_end_date as fld_hand_in_end_date,
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeHandInRecordDomain esChargeHandInRecordDomain = new EsChargeHandInRecordDomain();
        if ("INSERT".equals(optType)) {
            esChargeHandInRecordDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeHandInRecordDomain.setFld_guid(objArray.getString("fld_guid"));
            esChargeHandInRecordDomain.setFld_hand_in(Integer.parseInt(objArray.getString("fld_guid")));
            esChargeHandInRecordDomain.setFld_start_date(objArray.getString("fld_start_date"));
            esChargeHandInRecordDomain.setFld_end_date(objArray.getString("fld_end_date"));
        }
        return esChargeHandInRecordDomain;
    }

    public static EsChargeVoucherCheckServiceSetDomain parseEsChargeVoucherCheckServiceSetDomainJson(String s) {
//        fld_project_class_guid
//        fld_type as fld_service_set_type

        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeVoucherCheckServiceSetDomain esChargeVoucherCheckServiceSetDomain = new EsChargeVoucherCheckServiceSetDomain();
        if ("INSERT".equals(optType)) {
            esChargeVoucherCheckServiceSetDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeVoucherCheckServiceSetDomain.setFld_guid(objArray.getString("fld_guid"));
            esChargeVoucherCheckServiceSetDomain.setFld_project_class_guid(objArray.getString("fld_project_class_guid"));
            esChargeVoucherCheckServiceSetDomain.setFld_type(Integer.parseInt(objArray.getString("fld_type")));
        }
        return esChargeVoucherCheckServiceSetDomain;
    }

    public static EsChargeVoucherProjectPayDomain parseEsChargeVoucherProjectPayDomainJson(String s) {
        // fld_pay_mode_guid as voucher_fld_pay_mode_guid
        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeVoucherProjectPayDomain esChargeVoucherProjectPayDomain = new EsChargeVoucherProjectPayDomain();
        if ("INSERT".equals(optType)) {
            esChargeVoucherProjectPayDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeVoucherProjectPayDomain.setFld_guid(objArray.getString("fld_guid"));
            esChargeVoucherProjectPayDomain.setFld_pay_mode_guid(objArray.getString("fld_pay_mode_guid"));
        }
        return esChargeVoucherProjectPayDomain;
    }

    public static EsChargeIncomingBackDomain parseEsChargeIncomingBackDomainJson(String s) {

//        fld_guid as fld_back_guid,
//        fld_incoming_back_guid as back_convert_kou_guid
//        fld_incoming_convert_guid as back_convert_kou_guid
//        fld_incoming_kou_guid as back_convert_kou_guid
//        fld_submit_time

        JSONObject jsonObject = JSON.parseObject(s);
        String optType = jsonObject.getString("type");
        EsChargeIncomingBackDomain esChargeIncomingBackDomain = new EsChargeIncomingBackDomain();
        if ("INSERT".equals(optType)) {
            esChargeIncomingBackDomain.setOperation_type("INSERT");
            JSONObject objArray = (JSONObject) jsonObject.getJSONArray("data").get(0);
            esChargeIncomingBackDomain.setFld_guid(objArray.getString("fld_guid"));
            esChargeIncomingBackDomain.setFld_incoming_back_guid(objArray.getString("fld_incoming_back_guid"));
            esChargeIncomingBackDomain.setFld_incoming_convert_guid(objArray.getString("fld_incoming_convert_guid"));
            esChargeIncomingBackDomain.setFld_incoming_kou_guid(objArray.getString("fld_incoming_kou_guid"));
            esChargeIncomingBackDomain.setFld_submit_time(objArray.getString("fld_submit_time"));
        }
        return esChargeIncomingBackDomain;

    }
}