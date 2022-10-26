package com.sunac;

import com.sunac.domain.*;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.*;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac
 * @date:2022/9/13
 */
public class Config {

    enum Topic {
        ES_CHARGE_INCOMING_DATA_BILL("es_charge_incoming_data_bill");
        private String name;

        Topic(String name) {
            this.name = name;
        }

        public String value() {
            return this.name;
        }
    }

    enum Table {

        ES_CHARGE_PROJECT("es_charge_project"), ES_INFO_OBJECT_PARK("es_info_object_park");
        private String name;

        Table(String name) {
            this.name = name;
        }

        public String Name() {
            return this.name;
        }
    }

    public static String PASS = "PASS";

    public static String SIDE_STREAM = "side_stream";
    public static String INSERT = "INSERT";
    public static String DELETE = "DELETE";
    public static String UPDATE = "UPDATE";

    public static String INSERT2 = "INSERT2";
    public static String DELETE2 = "DELETE2";
    public static String UPDATE2 = "UPDATE2";

    public static String INSERT3 = "INSERT3";
    public static String DELETE3 = "DELETE3";
    public static String UPDATE3 = "UPDATE3";

    public static String SINGLE_PARTITION = "666";
    public static String TABLE = "table";

    // TODO:如果是关联键的修改，当作insert事件
    public static String DELETE_SQL = "delete from all_data_info_2 WHERE fld_guid=?";
    public static String INSERT_SQL = "insert into all_data_info_2 (fld_guid,fld_create_user,fld_create_date,fld_modify_user,fld_modify_date," + "fld_tenancy,fld_incoming_fee_guid,fld_owner_fee_guid,fld_area_guid,fld_area_name," + "fld_object_guid,fld_object_name,fld_owner_guid,fld_owner_name,fld_project_guid," + "fld_project_name,fld_pay_mode_guid,fld_pay_mode_name,fld_owner_date,fld_allot_date" + ",fld_finance_date,fld_point_date,fld_desc,fld_total,fld_amount" + ",fld_late_fee,fld_tax_amount,fld_tax,fld_bill_no,fld_bill_type" + ",fld_bill_no2,fld_operator_guid,fld_operate_date,fld_back_fee_guid,fld_cancel" + ",fld_start_date,fld_end_date,fld_back_guid,fld_busi_type,fld_remark" + ",fld_cancel_date,fld_cancel_guid,fld_number,fld_checkin,fld_cancel_me" + ",fld_dq,fld_fee_type,fld_confirm_date,fld_company,fld_xm" + ",fld_ywdy,fld_is_owner,fld_status,fld_adjust_guid,fld_settle_status" + ",fld_accounts_detail_bill_no,fld_attribute,fld_examine_status,fld_reason_remark,fld_price" + ",fld_rebate,fld_owner_fee_resource,fld_owner_fee_desc,fld_resource,fld_owner_fee_date" + ",fld_building,fld_cell,fld_batch,fld_charged_area,fld_obj_status" + ",fld_room_type,fld_object_class_name,fld_cw_category,fld_owner_desc,fld_bill_status" + ",fld_bill_code,fld_bill_type_name,fld_object_type,fld_fee_adjust_guid,fld_yt" + ",fld_bill_type2,fld_bill_type_name2,fld_bill_status2,fld_project_period_name,fld_balance_guid" + ",fld_hand_in_guid,fld_hand_in,fld_hand_in_start_date,fld_hand_in_end_date,fld_incoming_back_guid" + ",fld_data,firstTime,fld_pre_pay,fld_service_set_type,two_fld_date" + ",two_fld_create_user,obj_fld_order,voucher_fld_pay_mode_guid,pay_fld_attribute,back_fld_incoming_back_guid" + ",back_fld_incoming_convert_guid,back_fld_incoming_kou_guid,refund_fld_date_back,refund_fld_date_convert,refund_fld_date_kou)" + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    // TODO:全类名字
    public static String EsChargeIncomingFee = "com.sunac.entity.EsChargeIncomingFee";
    public static String EsChargeIncomingFee2 = "com.sunac.domain.EsChargeIncomingFee2";
    public static String AllData = "com.sunac.entity.AllData";
    public static String EsInfoAreaInfo = "com.sunac.entity.EsInfoAreaInfo";
    public static String EsInfoObject = "com.sunac.entity.EsInfoObject";
    public static String EsInfoOwner = "com.sunac.entity.EsInfoOwner";
    public static String EsChargePayModel = "com.sunac.domain.EsChargePayModel";
    public static String EsChargeOwnerFee = "com.sunac.domain.EsChargeOwnerFee";
    public static String EsInfoObjectPark = "com.sunac.domain.EsInfoObjectPark";
    public static String EsInfoObjectAndOwnerDomain = "com.sunac.domain.EsInfoObjectAndOwnerDomain";
    public static String EsChargeIncomingDataBill = "com.sunac.domain.EsChargeIncomingDataBill";
    public static String EsChargeBill = "com.sunac.domain.EsChargeBill";
    public static String EsChargeBillType = "com.sunac.domain.EsChargeBillType";

    public static String EsChargeIncomingBackDomain = "com.sunac.domain.EsChargeIncomingBackDomain";

    public static String EsChargeIncomingBack = "com.sunac.domain.EsChargeIncomingBack";
    public static String EsChargeIncomingConvert = "com.sunac.domain.EsChargeIncomingConvert";
    public static String EsChargeIncomingKou = "com.sunac.domain.EsChargeIncomingKou";



    public static String EsChargeVoucherMastRefundDomain = "com.sunac.domain.EsChargeVoucherMastRefundDomain";

    public static String EsChargeVoucherMastRefundBack = "com.sunac.domain.EsChargeVoucherMastRefundBack";
    public static String EsChargeVoucherMastRefundConvert = "com.sunac.domain.EsChargeVoucherMastRefundConvert";
    public static String EsChargeVoucherMastRefundKou = "com.sunac.domain.EsChargeVoucherMastRefundKou";


    public static String EsInfoObjectClass = "com.sunac.entity.EsInfoObjectClass";
    public static String EsChargeSettleAccountsDetail = "com.sunac.entity.EsChargeSettleAccountsDetail";
    public static String EsChargeSettleAccountsMain = "com.sunac.entity.EsChargeSettleAccountsMain";
    public static String EsChargeProjectPeriodJoinDomain = "com.sunac.domain.EsChargeProjectPeriodJoinDomain";

    public static String EsChargeProjectPeriodDomain = "com.sunac.domain.EsChargeProjectPeriodDomain";
    public static String EsChargeTwoBalanceDomain = "com.sunac.domain.EsChargeTwoBalanceDomain";
    public static String EsChargeHandInRecordDomain = "com.sunac.domain.EsChargeHandInRecordDomain";

    public static String EsChargeVoucherCheckServiceSetDomain = "com.sunac.domain.EsChargeVoucherCheckServiceSetDomain";
    public static String EsChargeVoucherProjectPayDomain = "com.sunac.domain.EsChargeVoucherProjectPayDomain";

    public static String EsChargeProject = "com.sunac.entity.EsChargeProject";
    public static String EsChargeRateResult = "com.sunac.entity.EsChargeRateResult";
    public static String EsInfoObjectAndOwner = "com.sunac.entity.EsInfoObjectAndOwner";
    public static String EsChargeProjectPeriodJoin = "com.sunac.entity.EsChargeProjectPeriodJoin";
    public static String EsChargeProjectPeriod = "com.sunac.entity.EsChargeProjectPeriod";
    public static String EsChargeTicketPayDetail = "com.sunac.entity.EsChargeTicketPayDetail";
    public static String EsChargeTicketPayOperate = "com.sunac.entity.EsChargeTicketPayOperate";
    public static String EsChargeIncomingData = "com.sunac.entity.EsChargeIncomingData";
    public static String EsCommerceBondMain = "com.sunac.entity.EsCommerceBondMain";
    public static String EsCommerceBondFeeObject = "com.sunac.entity.EsCommerceBondFeeObject";
    public static String EsInfoParamInfo = "com.sunac.entity.EsInfoParamInfo";

    public static String Add1 = "ES_CHARGE_SETTLE_ACCOUNTS_MAIN_COLUMNS_ADD";
}
