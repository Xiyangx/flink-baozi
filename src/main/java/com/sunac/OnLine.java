package com.sunac;

import com.sunac.map.*;
import com.sunac.ow.owdomain.*;
import com.sunac.sink.AllDataRichSinkFunction;
import com.sunac.sink.AllDataRichSinkFunctionPlus;
import com.sunac.utils.MakeMd5Utils;
import com.sunac.utils.Util;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 10:19 上午
 * @Version 1.0
 */
public class OnLine {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "hive");
        Configuration configuration = new Configuration();
        configuration.setString(OnLineConfig.EXECUTION_SAVEPOINT_PATH_KEY, OnLineConfig.EXECUTION_SAVEPOINT_PATH_VALUE);
        configuration.setString("dfs.client.use.datanode.hostname", "true");
//        configuration.setInteger("rest.port", 8822);
        StreamExecutionEnvironment env = OnLineConfig.initOnLineEnv(configuration);

        int parallelismRun = env.getParallelism();
        //todo 代码开始
        SingleOutputStreamOperator<AllData> mainTable = Util.getKafkaSource(env, Topic.ES_CHARGE_OWNER_FEE)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<AllData>(Config.AllData))
                .setParallelism(parallelismRun/8)
                .returns(AllData.class);

        mainTable.filter(t -> t.getOperation_type().equals(Config.DELETE)).addSink(new AllDataRichSinkFunction()).setParallelism(1).name("one-sink-delete");

        //todo 维表 esInfoAreaInfo
        SingleOutputStreamOperator<EsInfoAreaInfo> esInfoAreaInfo = Util.getKafkaSource(env, Topic.ES_INFO_AREA_INFO)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsInfoAreaInfo>(Config.EsInfoAreaInfo))
                .setParallelism(parallelismRun/8)
                .returns(EsInfoAreaInfo.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()))
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c1 = OnLineConfig.ConnectUtil(mainTable, esInfoAreaInfo,
                (KeySelector<AllData, String>) allData -> allData.getFld_area_guid(),
                (KeySelector<EsInfoAreaInfo, String>) t -> t.getFld_guid(),
                new EsInfoAreaInfoCoProcessFunction(), Config.ESINFOAREAINFO_UID, parallelismRun/4);

        //todo esInfoObject
        SingleOutputStreamOperator<EsInfoObject> esInfoObject = Util.getKafkaSource(env, Topic.ES_INFO_OBJECT)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsInfoObject>(Config.EsInfoObject))
                .setParallelism(parallelismRun/8)
                .returns(EsInfoObject.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsInfoObject-fld_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_class_guid())).name("EsInfoObject-fld_class_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c2 = OnLineConfig.ConnectUtil(c1, esInfoObject,
                (KeySelector<AllData, String>) allData -> allData.getFld_object_guid(),
                (KeySelector<EsInfoObject, String>) t -> t.getFld_guid(),
                new EsInfoObjectCoProcessFunction(), Config.ESINFOOBJECT_UID, parallelismRun/4);

        //todo esInfoObjectClass
        SingleOutputStreamOperator<EsInfoObjectClass> esInfoObjectClass = Util.getKafkaSource(env, Topic.ES_INFO_OBJECT_CLASS)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsInfoObjectClass>(Config.EsInfoObjectClass))
                .setParallelism(parallelismRun/8)
                .returns(EsInfoObjectClass.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsInfoObjectClass-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c3 = OnLineConfig.ConnectUtil(c2, esInfoObjectClass,
                (KeySelector<AllData, String>) allData -> allData.getO_fld_class_guid(),
                (KeySelector<EsInfoObjectClass, String>) t -> t.getFld_guid(),
                new EsInfoObjectClassCoProcessFunction(), Config.ESINFOOBJECTCLASS_UID, parallelismRun/4);

        //todo esInfoOwner
        SingleOutputStreamOperator<EsInfoOwner> esInfoOwner = Util.getKafkaSource(env, Topic.ES_INFO_OWNER)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsInfoOwner>(Config.EsInfoOwner))
                .setParallelism(parallelismRun/8)
                .returns(EsInfoOwner.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsInfoOwner-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c4 = OnLineConfig.ConnectUtil(c3, esInfoOwner,
                (KeySelector<AllData, String>) allData -> allData.getFld_owner_guid(),
                (KeySelector<EsInfoOwner, String>) t -> t.getFld_guid(),
                new EsInfoOwnerCoProcessFunction(), Config.ESINFOOWNER_UID, parallelismRun/4);

        //todo esChargeProject
        SingleOutputStreamOperator<EsChargeProject> esChargeProject = Util.getKafkaSource(env, Topic.ES_CHARGE_PROJECT)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/4)
                .process(new CommomProcessFunction<EsChargeProject>(Config.EsChargeProject))
                .setParallelism(parallelismRun/4)
                .returns(EsChargeProject.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeProject-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c5 = OnLineConfig.ConnectUtil(c4, esChargeProject,
                (KeySelector<AllData, String>) allData -> allData.getFld_project_guid(),
                (KeySelector<EsChargeProject, String>) t -> t.getFld_guid(),
                new EsChargeProjectCoProcessFunction(), Config.ESCHARGEPROJECT_UID, parallelismRun / 4);

        //todo esChargeRateResult
        SingleOutputStreamOperator<EsChargeRateResult> esChargeRateResult = Util.getKafkaSource(env, Topic.ES_CHARGE_RATE_RESULT)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun)
                .process(new CommomProcessFunction<EsChargeRateResult>(Config.EsChargeRateResult))
                .setParallelism(parallelismRun)
                .returns(EsChargeRateResult.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid())).name("EsChargeRateResult-fld_area_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_project_guid())).name("EsChargeRateResult-fld_project_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeRateResult-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c6 = OnLineConfig.ConnectUtil(c5, esChargeRateResult,
                (KeySelector<AllData, String>) allData -> MakeMd5Utils.makeMd5(allData.getFld_area_guid(), allData.getFld_project_guid()),
                (KeySelector<EsChargeRateResult, String>) t -> t.getPk(),
                new EsChargeRateResultCoProcessFunction(), Config.ESCHARGERATERESULT_UID, parallelismRun/4);

        //todo esInfoObjectAndOwner
        SingleOutputStreamOperator<EsInfoObjectAndOwner> esInfoObjectAndOwner = Util.getKafkaSource(env, Topic.ES_INFO_OBJECT_AND_OWNER)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/2)
                .process(new CommomProcessFunction<EsInfoObjectAndOwner>(Config.EsInfoObjectAndOwner))
                .setParallelism(parallelismRun/2)
                .returns(EsInfoObjectAndOwner.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsInfoObjectAndOwner-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c7 = OnLineConfig.ConnectUtil(c6, esInfoObjectAndOwner,
                (KeySelector<AllData, String>) t -> MakeMd5Utils.makeMd5(t.getO_fld_guid(), t.getW_fld_guid(), t.getFld_area_guid()),
                (KeySelector<EsInfoObjectAndOwner, String>) t -> t.getPk(),
                new EsInfoObjectAndOwnerCoMapFunction(), Config.ESINFOOBJECTANDOWNER_UID, parallelismRun/4);

        //todo esChargeProjectPeriodJoin
        SingleOutputStreamOperator<EsChargeProjectPeriodJoin> esChargeProjectPeriodJoin = Util.getKafkaSource(env, Topic.ES_CHARGE_PROJECT_PERIOD_JOIN)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsChargeProjectPeriodJoin>(Config.EsChargeProjectPeriodJoin))
                .setParallelism(parallelismRun/8)
                .returns(EsChargeProjectPeriodJoin.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_project_guid())).name("EsChargeProjectPeriodJoin-fld_project_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeProjectPeriodJoin-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c8 = OnLineConfig.ConnectUtil(c7, esChargeProjectPeriodJoin,
                (KeySelector<AllData, String>) t -> t.getFld_project_guid(),
                (KeySelector<EsChargeProjectPeriodJoin, String>) t1 -> t1.getFld_project_guid(),
                new EsChargeProjectPeriodJoinCoProcessFunction(), Config.ESCHARGEPROJECTPERIODJOIN_UID, parallelismRun / 8);

        //todo esChargeProjectPeriod
        SingleOutputStreamOperator<EsChargeProjectPeriod> esChargeProjectPeriod = Util.getKafkaSource(env, Topic.ES_CHARGE_PROJECT_PERIOD)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsChargeProjectPeriod>(Config.EsChargeProjectPeriod))
                .setParallelism(parallelismRun/8)
                .returns(EsChargeProjectPeriod.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeProjectPeriod-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c9 = OnLineConfig.ConnectUtil(c8, esChargeProjectPeriod,
                (KeySelector<AllData, String>) t -> t.getPj_fld_period_guid(),
                (KeySelector<EsChargeProjectPeriod, String>) t1 -> t1.getFld_guid(),
                new EsChargeProjectPeriodCoMapFunction(), Config.ESCHARGEPROJECTPERIOD_UID, parallelismRun / 4);

        //todo esChargeSettleAccountsDetail
        SingleOutputStreamOperator<EsChargeSettleAccountsDetail> esChargeSettleAccountsDetail = Util.getKafkaSource(env, Topic.ES_CHARGE_SETTLE_ACCOUNTS_DETAIL)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsChargeSettleAccountsDetail>(Config.EsChargeSettleAccountsDetail))
                .setParallelism(parallelismRun/8)
                .returns(EsChargeSettleAccountsDetail.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid())).name("EsChargeSettleAccountsDetail-fld_area_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeSettleAccountsDetail-fld_guid")
                .setParallelism(parallelismRun/8);
        SingleOutputStreamOperator<EsChargeSettleAccountsDetail> esChargeSettleAccountsDetailOne = esChargeSettleAccountsDetail.process(new ProcessFunction<EsChargeSettleAccountsDetail, EsChargeSettleAccountsDetail>() {
            @Override
            public void processElement(EsChargeSettleAccountsDetail esChargeSettleAccountsDetail, ProcessFunction<EsChargeSettleAccountsDetail, EsChargeSettleAccountsDetail>.Context context, Collector<EsChargeSettleAccountsDetail> collector) throws Exception {
                String fld_area_guid = esChargeSettleAccountsDetail.getFld_area_guid();
                String fld_adjust_guid = esChargeSettleAccountsDetail.getFld_adjust_guid();
                String fld_owner_fee_guid = esChargeSettleAccountsDetail.getFld_owner_fee_guid();
                if (StringUtils.isNotBlank(fld_adjust_guid)) {
                    String pk = MakeMd5Utils.makeMd5(fld_adjust_guid, fld_area_guid);
                    esChargeSettleAccountsDetail.setPk(pk);
                    collector.collect(esChargeSettleAccountsDetail);
                }
                if (StringUtils.isNotBlank(fld_owner_fee_guid)) {
                    String pk = MakeMd5Utils.makeMd5(fld_owner_fee_guid, fld_area_guid);
                    esChargeSettleAccountsDetail.setPk(pk);
                    collector.collect(esChargeSettleAccountsDetail);
                }
            }
        });

        SingleOutputStreamOperator c10 = OnLineConfig.ConnectUtil(c9, esChargeSettleAccountsDetailOne,
                (KeySelector<AllData, String>) t -> MakeMd5Utils.makeMd5(t.getFld_guid(), t.getFld_area_guid()),
                (KeySelector<EsChargeSettleAccountsDetail, String>) t1 -> t1.getPk(),
                new EsChargeSettleAccountsDetailCoMapFunction(), Config.ESCHARGESETTLEACCOUNTSDETAIL_UID, parallelismRun/4);

        //todo esChargeSettleAccountsMain
        SingleOutputStreamOperator<EsChargeSettleAccountsMain> esChargeSettleAccountsMain = Util.getKafkaSource(env, Topic.ES_CHARGE_SETTLE_ACCOUNTS_MAIN)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsChargeSettleAccountsMain>(Config.EsChargeSettleAccountsMain))
                .setParallelism(parallelismRun/8)
                .returns(EsChargeSettleAccountsMain.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid())).name("EsChargeSettleAccountsMain-fld_area_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeSettleAccountsMain-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c11 = OnLineConfig.ConnectUtil(c10, esChargeSettleAccountsMain,
                (KeySelector<AllData, String>) t -> MakeMd5Utils.makeMd5(t.getAd_fld_main_guid(), t.getFld_area_guid()),
                (KeySelector<EsChargeSettleAccountsMain, String>) t1 -> t1.getPk(),
                new EsChargeSettleAccountsMainCoMapFunction(), Config.ESCHARGESETTLEACCOUNTSMAIN_UID, parallelismRun/4);

        //todo esChargeTicketPayDetail
        SingleOutputStreamOperator<EsChargeTicketPayDetail> esChargeTicketPayDetail = Util.getKafkaSource(env, Topic.ES_CHARGE_TICKET_PAY_DETAIL)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsChargeTicketPayDetail>(Config.EsChargeTicketPayDetail))
                .setParallelism(parallelismRun/8)
                .returns(EsChargeTicketPayDetail.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_owner_fee_guid())).name("EsChargeTicketPayDetail-fld_owner_fee_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeTicketPayDetail-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c12 = OnLineConfig.ConnectUtil(c11, esChargeTicketPayDetail,
                (KeySelector<AllData, String>) t -> t.getFld_guid(),
                (KeySelector<EsChargeTicketPayDetail, String>) t1 -> t1.getFld_owner_fee_guid(),
                new EsChargeTicketPayDetailCoProcessFunction(), Config.ESCHARGETICKETPAYDETAIL_UID, parallelismRun);

        //todo esChargeTicketPayOperate
        SingleOutputStreamOperator<EsChargeTicketPayOperate> esChargeTicketPayOperate = Util.getKafkaSource(env, Topic.ES_CHARGE_TICKET_PAY_OPERATE)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsChargeTicketPayOperate>(Config.EsChargeTicketPayOperate))
                .setParallelism(parallelismRun/8)
                .returns(EsChargeTicketPayOperate.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeTicketPayOperate-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c13 = OnLineConfig.ConnectUtil(c12, esChargeTicketPayOperate,
                (KeySelector<AllData, String>) t -> t.getCt_fld_operate_guid(),
                (KeySelector<EsChargeTicketPayOperate, String>) t1 -> t1.getFld_guid(),
                new EsChargeTicketPayOperateCoProcessFunction(), Config.ESCHARGETICKETPAYOPERATE_UID, parallelismRun);

        //todo esCommerceBondFeeObject -------------------------------------------
        SingleOutputStreamOperator<EsCommerceBondFeeObject> esCommerceBondFeeObject = Util.getKafkaSource(env, Topic.ES_COMMERCE_BOND_FEE_OBJECT)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsCommerceBondFeeObject>(Config.EsCommerceBondFeeObject))
                .setParallelism(parallelismRun/8)
                .returns(EsCommerceBondFeeObject.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_object_guid())).name("EsCommerceBondFeeObject-fld_object_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_bond_guid())).name("EsCommerceBondFeeObject-fld_bond_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsCommerceBondFeeObject-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c14 = OnLineConfig.ConnectUtil(c13, esCommerceBondFeeObject,
                (KeySelector<AllData, String>) t -> t.getFld_object_guid(),
                (KeySelector<EsCommerceBondFeeObject, String>) t1 -> t1.getFld_object_guid(),
                new EsCommerceBondFeeObjectCoProcessFunction(), Config.ESCOMMERCEBONDFEEOBJECT_UID, parallelismRun / 4);

        //todo esCommerceBondMain
        SingleOutputStreamOperator<EsCommerceBondMain> esCommerceBondMain = Util.getKafkaSource(env, Topic.ES_COMMERCE_BOND_MAIN)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsCommerceBondMain>(Config.EsCommerceBondMain))
                .setParallelism(parallelismRun/8)
                .returns(EsCommerceBondMain.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsCommerceBondMain-fld_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid())).name("EsCommerceBondMain-fld_area_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_owner_guid())).name("EsCommerceBondMain-fld_owner_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c15 = OnLineConfig.ConnectUtil(c14, esCommerceBondMain,
                (KeySelector<AllData, String>) t -> MakeMd5Utils.makeMd5(t.getCbfo_fld_bond_guid(), t.getFld_area_guid(), t.getFld_owner_guid()),
                (KeySelector<EsCommerceBondMain, String>) t1 -> t1.getPk(),
                new EsCommerceBondMainCoProcessFunction(), Config.ESCOMMERCEBONDMAIN_UID, parallelismRun / 4);

        //todo esChargeIncomingData
        SingleOutputStreamOperator<EsChargeIncomingData> esChargeIncomingData = Util.getKafkaSource(env, Topic.ES_CHARGE_INCOMING_DATA)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun)
                .process(new CommomProcessFunction<EsChargeIncomingData>(Config.EsChargeIncomingData))
                .setParallelism(parallelismRun)
                .returns(EsChargeIncomingData.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeIncomingData-fld_guid")
                .setParallelism(parallelismRun/8)
                .filter(t -> StringUtils.isNotBlank(t.getFld_owner_fee_guid())).name("EsChargeIncomingData-fld_owner_fee_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c16 = OnLineConfig.ConnectUtil(c15, esChargeIncomingData,
                (KeySelector<AllData, String>) t -> t.getFld_guid(),
                (KeySelector<EsChargeIncomingData, String>) t1 -> t1.getFld_owner_fee_guid(),
                new EsChargeIncomingDataCoProcessFunction(), Config.ESCHARGEINCOMINGDATA_UID, parallelismRun + 12);

        //todo esChargeIncomingFee
        SingleOutputStreamOperator<EsChargeIncomingFee> esChargeIncomingFee = Util.getKafkaSource(env, Topic.ES_CHARGE_INCOMING_FEE)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/4)
                .process(new CommomProcessFunction<EsChargeIncomingFee>(Config.EsChargeIncomingFee))
                .setParallelism(parallelismRun/4)
                .returns(EsChargeIncomingFee.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsChargeIncomingFee-fld_guid")
                .setParallelism(parallelismRun/4);

        SingleOutputStreamOperator c17 = OnLineConfig.ConnectUtil(c16, esChargeIncomingFee,
                (KeySelector<AllData, String>) t -> t.getDa_fld_incoming_fee_guid(),
                (KeySelector<EsChargeIncomingFee, String>) t1 -> t1.getFld_guid(),
                new EsChargeIncomingFeeCoProcessFunction(), Config.ESCHARGEINCOMINGFEE_UID, parallelismRun +12);

        //todo esInfoParamInfo-------------------------------------------
        SingleOutputStreamOperator<EsInfoParamInfo> esInfoParamInfo = Util.getKafkaSource(env, Topic.ES_INFO_PARAM_INFO)
                .process(new MyJsonStringProcessFunction())
                .setParallelism(parallelismRun/8)
                .process(new CommomProcessFunction<EsInfoParamInfo>(Config.EsInfoParamInfo))
                .setParallelism(parallelismRun/8)
                .returns(EsInfoParamInfo.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid())).name("EsInfoParamInfo-fld_guid")
                .setParallelism(parallelismRun/8);

        SingleOutputStreamOperator c18 = OnLineConfig.ConnectUtil(c17, esInfoParamInfo,
                (KeySelector<AllData, String>) t -> t.getFld_reason_guid(),
                (KeySelector<EsInfoParamInfo, String>) t1 -> t1.getFld_guid(),
                new EsInfoParamInfoCoProcessFunction(), Config.ESINFOPARAMINFO_UID, parallelismRun / 4);

        DataStream s1 = c1.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s2 = c2.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s3 = c3.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s4 = c4.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s5 = c5.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s6 = c6.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s7 = c7.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s8 = c8.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s9 = c9.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s10 = c10.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s11 = c11.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s12 = c12.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s13 = c13.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s14 = c14.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s15 = c15.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s16 = c16.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s17 = c17.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream s18 = c18.getSideOutput(Constant.SIDE_STREAM_TAG);
        DataStream resultStream = s1.union(s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18);
        resultStream.rebalance().addSink(new AllDataRichSinkFunctionPlus("small-table-sink")).setParallelism(parallelismRun/2).name("small-table-sink");

        c18.addSink(new AllDataRichSinkFunction()).setParallelism(3).name("one-sink-insertAndUpdate");
        env.execute("owner");
    }
}
