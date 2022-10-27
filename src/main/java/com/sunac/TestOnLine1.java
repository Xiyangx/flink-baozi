package com.sunac;

import com.sunac.entity.*;
import com.sunac.map.*;
import com.sunac.sink.AllDataRichSinkFunction;
import com.sunac.utils.JdbcUtils;
import com.sunac.sink.MySqlCommitSink;
import com.sunac.utils.Util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import static com.sunac.Constant.*;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 10:19 上午
 * @Version 1.0
 */
public class TestOnLine1 {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setInteger("rest.port", 8822);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
        env.setParallelism(1);

        SingleOutputStreamOperator<AllData> mainTable = Util.getKafkaSource(env, Topic.ES_CHARGE_INCOMING_FEE_TOPIC)
                .process(new MyJsonStringProcessFunction()).map(new CommonMapFunction<AllData>(Config.AllData))
                .returns(AllData.class);
        mainTable.filter(t -> t.getOperation_type()==Config.DELETE).addSink(new AllDataRichSinkFunction());
        SingleOutputStreamOperator<String> mySideTable1 = Util.getKafkaSource(env, Topic.SIDE_TOPIC_1).process(new MySideStreamProcessFunction1());
        SingleOutputStreamOperator<String> mySideTable2 = Util.getKafkaSource(env, Topic.SIDE_TOPIC_2).process(new MySideStreamProcessFunction2());

        //维表 esInfoAreaInfo
        SingleOutputStreamOperator<EsInfoAreaInfo> esInfoAreaInfo = mySideTable1.getSideOutput(ES_INFO_AREA_INFO_TAG)
                .map(new CommonMapFunction<EsInfoAreaInfo>(Config.EsInfoAreaInfo))
                .returns(EsInfoAreaInfo.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        SingleOutputStreamOperator<AllData> join01 = mainTable.connect(esInfoAreaInfo)
                .keyBy(t->t.getFld_area_guid(), t1->t1.getFld_guid())
                .process(new EsInfoAreaInfoCoProcessFunction());
        join01.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        // esInfoObject
        SingleOutputStreamOperator<EsInfoObject> esInfoObject = Util.getKafkaSource(env, Topic.ES_INFO_OBJECT_TOPIC)
                .process(new MyJsonStringProcessFunction()).map(new CommonMapFunction<EsInfoObject>(Config.EsInfoObject))
                .returns(EsInfoObject.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        SingleOutputStreamOperator<AllData> join02 = join01.connect(esInfoObject)
                .keyBy(t -> t.getFld_object_guid(), t1 -> t1.getFld_guid())
                .process(new EsInfoObjectCoProcessFunction());
        join02.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        esInfoObject.print("esInfoObject");
        join02.print("join02");

        //esInfoObjectClass
        SingleOutputStreamOperator<EsInfoObjectClass> esInfoObjectClass = mySideTable2.getSideOutput(ES_INFO_OBJECT_CLASS_TAG)
                .map(new CommonMapFunction<EsInfoObjectClass>(Config.EsInfoObjectClass))
                .returns(EsInfoObjectClass.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        SingleOutputStreamOperator<AllData> join03 = join02.connect(esInfoObjectClass)
                .keyBy(t -> t.getO_fld_class_guid(), t1 -> t1.getFld_guid())
                .process(new EsInfoObjectClassCoProcessFunction());
        join03.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());
        esInfoObjectClass.print("esInfoObjectClass");
        join03.print("join03");

        //esInfoOwner
        SingleOutputStreamOperator<EsInfoOwner> esInfoOwner = mySideTable2.getSideOutput(ES_INFO_OWNER_TAG)
                .map(new CommonMapFunction<EsInfoOwner>(Config.EsInfoOwner))
                .returns(EsInfoOwner.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        SingleOutputStreamOperator<AllData> join04 = join03.connect(esInfoOwner)
                .keyBy(t -> t.getFld_owner_guid(), t1 -> t1.getFld_guid())
                .process(new EsInfoOwnerCoProcessFunction());
        join04.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());


        //esChargeProject
        SingleOutputStreamOperator<EsChargeProject> esChargeProject = mySideTable1.getSideOutput(ES_CHARGE_PROJECT_TAG)
                .map(new CommonMapFunction<EsChargeProject>(Config.EsChargeProject))
                .returns(EsChargeProject.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        SingleOutputStreamOperator<AllData> join05 = join04.connect(esChargeProject)
                .keyBy(t -> t.getFld_project_guid(), t1 -> t1.getFld_guid())
                .process(new EsChargeProjectCoProcessFunction());
        join05.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esChargeRateResult
        SingleOutputStreamOperator<EsChargeRateResult> esChargeRateResult = mySideTable1.getSideOutput(ES_CHARGE_RATE_RESULT_TAG)
                .map(new CommonMapFunction<EsChargeRateResult>(Config.EsChargeRateResult))
                .returns(EsChargeRateResult.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid()))
                .filter(t -> StringUtils.isNotBlank(t.getFld_project_guid()));
        SingleOutputStreamOperator<AllData> join06 = join05.connect(esChargeRateResult)
                .keyBy(t -> DigestUtils.md5Hex(t.getFld_area_guid() + t.getFld_project_guid()), t1 -> t1.getPk())
                .process(new EsChargeRateResultCoProcessFunction());
        join06.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esInfoObjectAndOwner
        SingleOutputStreamOperator<EsInfoObjectAndOwner> esInfoObjectAndOwner = mySideTable2.getSideOutput(ES_INFO_OBJECT_AND_OWNER_TAG)
                .map(new CommonMapFunction<EsInfoObjectAndOwner>(Config.EsInfoObjectAndOwner))
                .returns(EsInfoObjectAndOwner.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_object_guid()))
                .filter(t -> StringUtils.isNotBlank(t.getFld_owner_guid()))
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid()));
        SingleOutputStreamOperator<AllData> join07 = join06.connect(esInfoObjectAndOwner)
                .keyBy(t -> DigestUtils.md5Hex(t.getO_fld_guid() + t.getW_fld_guid() + t.getFld_area_guid()), t1 -> t1.getPk())
                .process(new EsInfoObjectAndOwnerCoMapFunction());
        join07.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esChargeProjectPeriodJoin
        SingleOutputStreamOperator<EsChargeProjectPeriodJoin> esChargeProjectPeriodJoin = mySideTable1.getSideOutput(ES_CHARGE_PROJECT_PERIOD_JOIN_TAG)
                .map(new CommonMapFunction<EsChargeProjectPeriodJoin>(Config.EsChargeProjectPeriodJoin))
                .returns(EsChargeProjectPeriodJoin.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_project_guid()));
        SingleOutputStreamOperator<AllData> join08 = join07.connect(esChargeProjectPeriodJoin)
                .keyBy(t -> t.getFld_project_guid(), t1 -> t1.getFld_project_guid())
                .process(new EsChargeProjectPeriodJoinCoProcessFunction());
        join08.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esChargeProjectPeriod
        SingleOutputStreamOperator<EsChargeProjectPeriod> esChargeProjectPeriod = mySideTable1.getSideOutput(ES_CHARGE_PROJECT_PERIOD_TAG)
                .map(new CommonMapFunction<EsChargeProjectPeriod>(Config.EsChargeProjectPeriod))
                .returns(EsChargeProjectPeriod.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        SingleOutputStreamOperator<AllData> join09 = join08.connect(esChargeProjectPeriod)
                .keyBy(t -> t.getPj_fld_period_guid(), t1 -> t1.getFld_guid())
                .process(new EsChargeProjectPerionCoMapFunction());
        join09.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esChargeIncomingData
        SingleOutputStreamOperator<EsChargeIncomingData> esChargeIncomingData = Util.getKafkaSource(env, Topic.ES_CHARGE_INCOMING_DATA_TOPIC)
                .process(new MyJsonStringProcessFunction()).map(new CommonMapFunction<EsChargeIncomingData>(Config.EsChargeIncomingData))
                .returns(EsChargeIncomingData.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_owner_fee_guid()) && t.getFld_cancel() != 1);
        SingleOutputStreamOperator<AllData> join10 = join09.connect(esChargeIncomingData)
                .keyBy(t -> t.getFld_guid(), t1 -> t1.getFld_owner_fee_guid())
                .process(new EsChargeIncomingDataCoProcessFunction());
        join10.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esChargeSettleAccountsDetail
        SingleOutputStreamOperator<EsChargeSettleAccountsDetail> esChargeSettleAccountsDetail = mySideTable2.getSideOutput(ES_CHARGE_SETTLE_ACCOUNTS_DETAIL_TAG)
                .map(new CommonMapFunction<EsChargeSettleAccountsDetail>(Config.EsChargeSettleAccountsDetail))
                .returns(EsChargeSettleAccountsDetail.class)
                .filter(t -> (t.getFld_status() == 1) && StringUtils.isNotBlank(t.getFld_area_guid()));
        SingleOutputStreamOperator<EsChargeSettleAccountsDetail> esChargeSettleAccountsDetailOne = esChargeSettleAccountsDetail.process(new ProcessFunction<EsChargeSettleAccountsDetail, EsChargeSettleAccountsDetail>() {
            @Override
            public void processElement(EsChargeSettleAccountsDetail esChargeSettleAccountsDetail, ProcessFunction<EsChargeSettleAccountsDetail, EsChargeSettleAccountsDetail>.Context context, Collector<EsChargeSettleAccountsDetail> collector) throws Exception {
                String fld_area_guid = esChargeSettleAccountsDetail.getFld_area_guid();
                String fld_adjust_guid = esChargeSettleAccountsDetail.getFld_adjust_guid();
                String fld_owner_fee_guid = esChargeSettleAccountsDetail.getFld_owner_fee_guid();
                if (StringUtils.isNotBlank(fld_adjust_guid)) {
                    String pk = JdbcUtils.makeMd5(fld_adjust_guid, fld_area_guid);
                    esChargeSettleAccountsDetail.setPk(pk);
                    collector.collect(esChargeSettleAccountsDetail);
                }
                if (StringUtils.isNotBlank(fld_owner_fee_guid)) {
                    String pk = JdbcUtils.makeMd5(fld_owner_fee_guid, fld_area_guid);
                    esChargeSettleAccountsDetail.setPk(pk);
                    collector.collect(esChargeSettleAccountsDetail);
                }
            }
        });
        SingleOutputStreamOperator<AllData> join11 = join10.connect(esChargeSettleAccountsDetailOne)
                .keyBy(t -> DigestUtils.md5Hex(t.getFld_guid() + t.getFld_area_guid()), t1 -> t1.getPk())
                .process(new EsChargeSettleAccountsDetailCoMapFunction());
        esChargeSettleAccountsDetailOne.print("esChargeSettleAccountsDetail");
        join11.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        join11.print("join11");


        //esChargeSettleAccountsMain
        SingleOutputStreamOperator<EsChargeSettleAccountsMain> esChargeSettleAccountsMain = mySideTable2.getSideOutput(ES_CHARGE_SETTLE_ACCOUNTS_MAIN_TAG)
                .map(new CommonMapFunction<EsChargeSettleAccountsMain>(Config.EsChargeSettleAccountsMain))
                .returns(EsChargeSettleAccountsMain.class)
                .filter(t -> t.getFld_examine_status() == 4)
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid()));
        SingleOutputStreamOperator<AllData> join12 = join11.connect(esChargeSettleAccountsMain)
                .keyBy(t -> DigestUtils.md5Hex(t.getAd_fld_main_guid() + t.getFld_area_guid()), t1 -> t1.getPk())
                .process(new EsChargeSettleAccountsMainCoMapFunction());
        join12.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());
        join12.print("join12");


        //esChargeIncomingFee
        SingleOutputStreamOperator<EsChargeIncomingFee> esChargeIncomingFee = Util.getKafkaSource(env, Topic.ES_CHARGE_INCOMING_FEE)
                .process(new MyJsonStringProcessFunction()).map(new CommonMapFunction<EsChargeIncomingFee>(Config.EsChargeIncomingFee))
                .returns(EsChargeIncomingFee.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        esChargeIncomingFee.print();
        SingleOutputStreamOperator<AllData> join13 = join12.connect(esChargeIncomingFee)
                .keyBy(t -> t.getDa_fld_incoming_fee_guid(), t1 -> t1.getFld_guid())
                .process(new EsChargeIncomingFeeCoProcessFunction());
        join13.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esChargeTicketPayDetail
        SingleOutputStreamOperator<EsChargeTicketPayDetail> esChargeTicketPayDetail = mySideTable2.getSideOutput(ES_CHARGE_TICKET_PAY_DETAIL_TAG)
                .map(new CommonMapFunction<EsChargeTicketPayDetail>(Config.EsChargeTicketPayDetail))
                .returns(EsChargeTicketPayDetail.class);
        SingleOutputStreamOperator<AllData> join14 = join13.connect(esChargeTicketPayDetail)
                .keyBy(t -> t.getFld_guid(), t1 -> t1.getFld_owner_fee_guid())
                .process(new EsChargeTicketPayDetailCoProcessFunction());
        join14.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esChargeTicketPayOperate
        SingleOutputStreamOperator<EsChargeTicketPayOperate> esChargeTicketPayOperate = mySideTable2.getSideOutput(ES_CHARGE_TICKET_PAY_OPERATE_TAG)
                .map(new CommonMapFunction<EsChargeTicketPayOperate>(Config.EsChargeTicketPayOperate))
                .returns(EsChargeTicketPayOperate.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        SingleOutputStreamOperator<AllData> join15 = join14.connect(esChargeTicketPayOperate)
                .keyBy(t -> t.getCt_fld_operate_guid(), t1 -> t1.getFld_guid())
                .process(new EsChargeTicketPayOperateCoProcessFunction());
        join15.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esCommerceBondFeeObject
        SingleOutputStreamOperator<EsCommerceBondFeeObject> esCommerceBondFeeObject = mySideTable1.getSideOutput(ES_COMMERCE_BOND_FEE_OBJECT_TAG)
                .map(new CommonMapFunction<EsCommerceBondFeeObject>(Config.EsCommerceBondFeeObject))
                .returns(EsCommerceBondFeeObject.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_object_guid()));
        SingleOutputStreamOperator<AllData> join16 = join15.connect(esCommerceBondFeeObject)
                .keyBy(t -> t.getFld_object_guid(), t1 -> t1.getFld_object_guid())
                .process(new EsCommerceBondFeeObjectCoProcessFunction());
        join16.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esCommerceBondMain
        SingleOutputStreamOperator<EsCommerceBondMain> esCommerceBondMain = mySideTable1.getSideOutput(ES_COMMERCE_BOND_MAIN_TAG)
                .map(new CommonMapFunction<EsCommerceBondMain>(Config.EsCommerceBondMain))
                .returns(EsCommerceBondMain.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()))
                .filter(t -> StringUtils.isNotBlank(t.getFld_area_guid()))
                .filter(t -> StringUtils.isNotBlank(t.getFld_owner_guid()));
        SingleOutputStreamOperator<AllData> join17 = join16.connect(esCommerceBondMain)
                .keyBy(t -> DigestUtils.md5Hex(t.getCbfo_fld_bond_guid() + t.getFld_area_guid() + t.getFld_owner_guid()), t1 -> t1.getPk())
                .process(new EsCommerceBondMainCoProcessFunction());
        join17.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());

        //esInfoParamInfo
        SingleOutputStreamOperator<EsInfoParamInfo> esInfoParamInfo = mySideTable1.getSideOutput(ES_INFO_PARAM_INFO_TAG)
                .map(new CommonMapFunction<EsInfoParamInfo>(Config.EsInfoParamInfo))
                .returns(EsInfoParamInfo.class)
                .filter(t -> StringUtils.isNotBlank(t.getFld_guid()));
        esInfoParamInfo.print("esInfoParamInfo");
        SingleOutputStreamOperator<AllData> join18 = join17.connect(esInfoParamInfo)
                .keyBy(t -> t.getFld_reason_guid(), t1 -> t1.getFld_guid())
                .process(new EsInfoParamInfoCoProcessFunction());
        join18.print("join18");
        join18.getSideOutput(SIDE_STREAM_TAG).addSink(new MySqlCommitSink());
        join18.addSink(new AllDataRichSinkFunction());
        env.execute();
    }
}
