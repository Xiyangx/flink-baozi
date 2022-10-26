package com.sunac.domain;

import com.sunac.Config;

import java.util.HashMap;

public class ComplexStructureDomain {

    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_FEE_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("f_fld_guid", true, false, true, true, null));
            put("fld_busi_type", new ColumAttribute("fld_busi_type", true, true, true, true, 99));
            put("fld_remark", new ColumAttribute("fld_remark", true, true, true, true, ""));
            put("fld_cancel_date", new ColumAttribute("fld_cancel_date", true, true, true, true, null));
            put("fld_cancel_guid", new ColumAttribute("fld_cancel_guid", true, true, true, true, ""));
            put("fld_number", new ColumAttribute("fld_number", true, true, true, true, ""));
            put("fld_checkin", new ColumAttribute("fld_checkin", true, true, true, true, 99));
            put("fld_cancel_me", new ColumAttribute("fld_cancel_me", true, true, true, true, ""));
            put("fld_modify_date", new ColumAttribute("fld_modify_date", true, true, true, true, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_INFO_OBJECT_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("o_fld_guid", true, false, true, true, null));
            put("fld_class_guid", new ColumAttribute("fld_object_class_guid", true, false, true, true, ""));
            put("fld_name", new ColumAttribute("fld_object_name", true, true, true, true, ""));
            put("fld_owner_fee_date", new ColumAttribute("fld_owner_fee_date", true, true, true, true, "1970-01-01"));
            put("fld_cell", new ColumAttribute("fld_cell", true, true, true, true, ""));
            put("fld_building", new ColumAttribute("fld_building", true, true, true, true, ""));
            put("fld_batch", new ColumAttribute("fld_batch", true, true, true, true, ""));
            put("fld_charged_area", new ColumAttribute("fld_charged_area", true, true, true, true, 0));
            put("fld_status", new ColumAttribute("fld_obj_status", true, true, true, true, 99));
            put("fld_order", new ColumAttribute("obj_fld_order", true, true, true, true, -1));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_DATA_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("fld_guid", true, false, false, false, null));
            put("fld_create_user", new ColumAttribute("fld_create_user", true, true, true, true, null));
            put("fld_create_date", new ColumAttribute("fld_create_date", true, true, true, true, null));
            put("fld_modify_user", new ColumAttribute("fld_modify_user", true, true, true, true, null));
            put("fld_modify_date", new ColumAttribute("fld_modify_date", true, true, true, true, null));
            put("fld_tenancy", new ColumAttribute("fld_tenancy", true, true, true, true, null));
            put("fld_incoming_fee_guid", new ColumAttribute("fld_incoming_fee_guid", true, true, true, true, null));
            put("fld_owner_fee_guid", new ColumAttribute("fld_owner_fee_guid", true, true, true, true, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", true, true, true, true, null));
            put("fld_object_guid", new ColumAttribute("fld_object_guid", true, true, true, true, null));
            put("fld_owner_guid", new ColumAttribute("fld_owner_guid", true, true, true, true, null));
            put("fld_project_guid", new ColumAttribute("fld_project_guid", true, true, true, true, null));
            put("fld_pay_mode_guid", new ColumAttribute("fld_pay_mode_guid", true, true, true, true, null));
            put("fld_owner_date", new ColumAttribute("fld_owner_date", true, true, true, true, null));
            put("fld_allot_date", new ColumAttribute("fld_allot_date", true, true, true, true, null));
            put("fld_finance_date", new ColumAttribute("fld_finance_date", true, true, true, true, null));
            put("fld_point_date", new ColumAttribute("fld_point_date", true, true, true, true, null));
            put("fld_desc", new ColumAttribute("fld_desc", true, true, true, true, null));
            put("fld_total", new ColumAttribute("fld_total", true, true, true, true, null));
            put("fld_amount", new ColumAttribute("fld_amount", true, true, true, true, null));
            put("fld_late_fee", new ColumAttribute("fld_late_fee", true, true, true, true, null));
            put("fld_tax_amount", new ColumAttribute("fld_tax_amount", true, true, true, true, null));
            put("fld_tax", new ColumAttribute("fld_tax", true, true, true, true, null));
            put("fld_bill_no", new ColumAttribute("fld_bill_no", true, true, true, true, null));
            put("fld_bill_no2", new ColumAttribute("fld_bill_no2", true, true, true, true, null));
            put("fld_operator_guid", new ColumAttribute("fld_operator_guid", true, true, true, true, null));
            put("fld_operate_date", new ColumAttribute("fld_operate_date", true, true, true, true, null));
            put("fld_back_fee_guid", new ColumAttribute("fld_back_fee_guid", true, true, true, true, null));
            put("fld_cancel", new ColumAttribute("fld_cancel", true, true, true, true, null));
            put("fld_start_date", new ColumAttribute("fld_start_date", true, true, true, true, null));
            put("fld_end_date", new ColumAttribute("fld_end_date", true, true, true, true, null));
            put("fld_back_guid", new ColumAttribute("fld_back_guid", true, true, true, true, null));
        }
    };


    public static HashMap<String, ColumAttribute> ES_INFO_AREA_INFO_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("a_fld_guid", true, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_area_name", true, true, true, true, ""));
            put("fld_dq", new ColumAttribute("fld_dq", true, true, true, true, ""));
            put("fld_fee_type", new ColumAttribute("fld_fee_type", true, true, true, true, ""));
            put("fld_confirm_date", new ColumAttribute("fld_confirm_date", true, true, true, true, ""));
            put("fld_company", new ColumAttribute("fld_company", true, true, true, true, ""));
            put("fld_xm", new ColumAttribute("fld_xm", true, true, true, true, ""));
            put("fld_ywdy", new ColumAttribute("fld_ywdy", true, true, true, true, ""));
            put("fld_yt", new ColumAttribute("fld_yt", true, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_INFO_OWNER_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("w_fld_guid", true, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_owner_name", true, true, true, true, ""));
            put("fld_desc", new ColumAttribute("fld_owner_desc", true, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_PROJECT_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("p_fld_guid", true, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_project_name", true, true, true, true, ""));
            put("fld_object_type", new ColumAttribute("fld_object_type", true, true, true, true, 99));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_PAY_MODE_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("m_fld_guid", true, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_pay_mode_name", true, true, true, true, ""));
            put("fld_resource", new ColumAttribute("fld_resource", true, true, true, true, ""));
            put("fld_pre_pay", new ColumAttribute("fld_pre_pay", true, true, true, true, 99));
            put("fld_attribute", new ColumAttribute("pay_fld_attribute", true, true, true, true, 99));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_OWNER_FEE_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("cof_fld_guid", true, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", false, false, false, false, null));
            put("fld_reason_remark", new ColumAttribute("fld_reason_remark", true, true, true, true, ""));
            put("fld_price", new ColumAttribute("fld_price", true, true, true, true, ""));
            put("fld_rebate", new ColumAttribute("fld_rebate", true, true, true, true, 0));
            put("fld_resource", new ColumAttribute("fld_owner_fee_resource", true, true, true, true, ""));
            put("fld_desc", new ColumAttribute("fld_owner_fee_desc", true, true, true, true, ""));
            put("fld_adjust_guid", new ColumAttribute("fld_fee_adjust_guid", true, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_INFO_OBJECT_PARK_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("op_fld_guid", true, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", false, false, false, false, null));
            put("fld_cw_category", new ColumAttribute("fld_cw_category", true, true, true, true, ""));
        }
    };


    public static HashMap<String, ColumAttribute> ES_INFO_OBJECT_CLASS_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("oc_fld_guid", true, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_object_class_name", true, true, true, true, ""));
        }
    };
    public static HashMap<String, ColumAttribute> ES_INFO_OBJECT_AND_OWNER_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ao_fld_guid", true, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", true, true, true, true, null));
            put("fld_object_guid", new ColumAttribute("fld_object_guid", true, true, true, true, null));
            put("fld_owner_guid", new ColumAttribute("fld_owner_guid", true, true, true, true, null));
            put("fld_is_owner", new ColumAttribute("fld_is_owner", true, true, true, true, 99));
            put("fld_room_type", new ColumAttribute("fld_room_type", true, true, true, true, ""));
            // 排序字段
            put("fld_is_current", new ColumAttribute("fld_is_current", false, true, false, false, null));
            put("fld_is_charge", new ColumAttribute("fld_is_charge", false, true, false, false, null));
            put("fld_status", new ColumAttribute("fld_status", false, true, false, false, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_BILL_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("b_fld_guid", true, false, true, true, null));
            put("fld_type_guid", new ColumAttribute("fld_type_guid", false, false, false, false, null));
            put("fld_status", new ColumAttribute("fld_bill_status", true, true, true, true, 99));
            put("fld_bill_code", new ColumAttribute("fld_bill_code", true, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_BILL_TYPE_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("fld_bill_type", true, false, true, true, ""));
            put("fld_name", new ColumAttribute("fld_bill_type_name", true, true, true, true, ""));
            put("fld_category", new ColumAttribute("fld_category", true, true, true, true, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_SETTLE_ACCOUNTS_DETAIL_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ad_fld_guid", true, false, true, true, null));
            // 下面三个是关联键
            put("fld_owner_fee_guid", new ColumAttribute("fld_owner_fee_guid", false, false, false, false, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", false, false, false, false, null));
            put("fld_main_guid", new ColumAttribute("fld_main_guid", false, false, false, false, null));

            // 这个是关联键 + 字段
            put("fld_adjust_guid", new ColumAttribute("fld_adjust_guid", true, false, true, true, ""));
            // 这个是条件 + 字段
            put("fld_status", new ColumAttribute("fld_status", true, true, true, true, 99));


            put("fld_settle_status", new ColumAttribute("fld_settle_status", true, true, true, true, 99));
            put("fld_bill_no", new ColumAttribute("fld_accounts_detail_bill_no", true, true, true, true, ""));

        }

    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_SETTLE_ACCOUNTS_MAIN_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("am_fld_guid", true, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", false, false, false, false, null));
            put("fld_attribute", new ColumAttribute("fld_attribute", true, true, true, true, 99));
            put("fld_examine_status", new ColumAttribute("fld_examine_status", true, true, true, true, 99));

        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_DATA_BILL_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("db_fld_guid", true, false, true, true, null));
            put("fld_bill_guid", new ColumAttribute("fld_bill_guid", false, false, false, false, null));
            put("fld_data_src_guid", new ColumAttribute("fld_data_src_guid", false, false, false, false, null));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_FEE2_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("f1_fld_guid", true, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", false, false, false, false, null));
            put("fld_cancel", new ColumAttribute("fld_cancel", false, true, false, false, null));
            put("fld_operate_date", new ColumAttribute("firsttime", true, true, true, true, "1970-01-01"));
            put("fld_create_user", new ColumAttribute("fld_create_user", false, true, false, false, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_HAND_IN_RECORD_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("hir_fld_guid", true, false, true, true, null));
            put("fld_hand_in", new ColumAttribute("fld_hand_in", true, true, true, true, 99));
            put("fld_start_date", new ColumAttribute("fld_hand_in_start_date", true, true, true, true, "1970-01-01"));
            put("fld_end_date", new ColumAttribute("fld_hand_in_end_date", true, true, true, true, "1970-01-01"));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_BACK_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ib_back_fld_guid", true, false, true, true, null));
            // 下面三个是关联键 + 字段
            put("fld_incoming_back_guid", new ColumAttribute("back_fld_incoming_back_guid", true, false, true, true, ""));
            put("fld_submit_time", new ColumAttribute("fld_submit_time", false, true, false, false, null));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_CONVERT_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ib_convert_fld_guid", true, false, true, true, null));
            // 下面三个是关联键 + 字段
            put("fld_incoming_convert_guid", new ColumAttribute("back_fld_incoming_convert_guid", true, false, true, true, ""));
            put("fld_submit_time", new ColumAttribute("fld_submit_time", false, true, false, false, null));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_KOU_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ib_kou_fld_guid", true, false, true, true, null));
            put("fld_incoming_kou_guid", new ColumAttribute("back_fld_incoming_kou_guid", true, false, true, true, ""));
            put("fld_submit_time", new ColumAttribute("fld_submit_time", false, true, false, false, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_VOUCHER_MAST_REFUND_BACK_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("rf_back_fld_guid", true, false, true, true, null));
            put("fld_incoming_back_guid", new ColumAttribute("fld_incoming_back_guid", false, false, false, false, ""));
            put("fld_date", new ColumAttribute("refund_fld_date_back", true, true, true, true, "1970-01-01"));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_VOUCHER_MAST_REFUND_CONVERT_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("rf_convert_fld_guid", true, false, true, true, null));
            put("fld_incoming_back_guid", new ColumAttribute("fld_incoming_back_guid", false, false, false, false, ""));
            put("fld_date", new ColumAttribute("refund_fld_date_convert", true, true, true, true, "1970-01-01"));
        }
    };
    public static HashMap<String, ColumAttribute> ES_CHARGE_VOUCHER_MAST_REFUND_KOU_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("rf_kou_fld_guid", true, false, true, true, null));
            put("fld_incoming_back_guid", new ColumAttribute("fld_incoming_back_guid", false, false, false, false, ""));
            put("fld_date", new ColumAttribute("refund_fld_date_kou", true, true, true, true, "1970-01-01"));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_VOUCHER_CHECK_SERVICE_SET_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ss_fld_guid", true, false, true, true, null));
            put("fld_project_class_guid", new ColumAttribute("fld_project_class_guid", false, false, false, false, null));
            put("fld_type", new ColumAttribute("fld_service_set_type", true, true, true, true, 99));
        }
    };
    public static HashMap<String, ColumAttribute> ES_CHARGE_TWO_BALANCE_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("fld_balance_guid", true, false, true, true, ""));
            put("fld_date", new ColumAttribute("two_fld_date", true, true, true, true, "1970-01-01"));
            put("fld_create_user", new ColumAttribute("two_fld_create_user", true, true, true, true, ""));
            put("fld_hand_in_guid", new ColumAttribute("fld_hand_in_guid", true, true, true, true, ""));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_PROJECT_PERIOD_JOIN_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_project_guid", new ColumAttribute("fld_project_guid", true, true, true, true, null));
            put("fld_period_guid", new ColumAttribute("fld_period_guid", true, true, true, true, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_PROJECT_PERIOD_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("pp_fld_guid", true, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_project_period_name", true, true, true, true, ""));
            put("fld_type", new ColumAttribute("fld_type", true, true, true, true, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_VOUCHER_PROJECT_PAY_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("vpp_fld_guid", true, false, true, true, null));
            put("fld_pay_mode_guid", new ColumAttribute("voucher_fld_pay_mode_guid", true, false, true, true, ""));
        }
    };


    public static HashMap<String, ColumAttribute> ES_CHARGE_BILL_COLUMNS_ADD = new HashMap<String, ColumAttribute>() {
        {
            put("t_fld_category", new ColumAttribute("t_fld_category", false, false, false, false, null));
            put("fld_status", new ColumAttribute("fld_bill_status", true, true, true, true, 99));
            put("fld_bill_code", new ColumAttribute("fld_bill_code", true, true, true, true, ""));
            put("fld_name", new ColumAttribute("fld_bill_type_name", true, true, true, true, ""));
            put("fld_bill_type", new ColumAttribute("fld_bill_type", true, false, false, false, null));
            put("db_fld_guid", new ColumAttribute("db_fld_guid", true, false, true, true, null));
            put("b_fld_guid", new ColumAttribute("b_fld_guid", true, false, true, true, null));
            put("t_fld_guid", new ColumAttribute("t_fld_guid", true, false, true, true, null));
        }
    };


    public static HashMap<String, HashMap<String, ColumAttribute>> TABLES = new HashMap<String, HashMap<String, ColumAttribute>>() {
        {
            put(Config.EsChargeIncomingFee, ES_CHARGE_INCOMING_FEE_COLUMNS);
            put(Config.EsInfoAreaInfo, ES_INFO_AREA_INFO_COLUMNS);
            put(Config.EsInfoObject, ES_INFO_OBJECT_COLUMNS);
            put(Config.EsInfoOwner, ES_INFO_OWNER_COLUMNS);

            put(Config.EsChargeProject, ES_CHARGE_PROJECT_COLUMNS);
            put(Config.EsChargePayModel, ES_CHARGE_PAY_MODE_COLUMNS);
            put(Config.EsChargeOwnerFee, ES_CHARGE_OWNER_FEE_COLUMNS);
            put(Config.EsInfoObjectPark, ES_INFO_OBJECT_PARK_COLUMNS);
            put(Config.EsInfoObjectClass, ES_INFO_OBJECT_CLASS_COLUMNS);
            put(Config.EsInfoObjectAndOwnerDomain, ES_INFO_OBJECT_AND_OWNER_COLUMNS);
            put(Config.EsChargeIncomingDataBill, ES_CHARGE_INCOMING_DATA_BILL_COLUMNS);
            put(Config.EsChargeBill, ES_CHARGE_BILL_COLUMNS);
            put(Config.EsChargeBillType, ES_CHARGE_BILL_TYPE_COLUMNS);
            put(Config.EsChargeSettleAccountsDetail, ES_CHARGE_SETTLE_ACCOUNTS_DETAIL_COLUMNS);
            put(Config.EsChargeSettleAccountsMain, ES_CHARGE_SETTLE_ACCOUNTS_MAIN_COLUMNS);
            put(Config.EsChargeProjectPeriodJoinDomain, ES_CHARGE_PROJECT_PERIOD_JOIN_COLUMNS);
            put(Config.EsChargeProjectPeriodDomain, ES_CHARGE_PROJECT_PERIOD_COLUMNS);
            put(Config.EsChargeTwoBalanceDomain, ES_CHARGE_TWO_BALANCE_COLUMNS);
            put(Config.EsChargeHandInRecordDomain, ES_CHARGE_HAND_IN_RECORD_COLUMNS);
            put(Config.EsChargeIncomingFee2, ES_CHARGE_INCOMING_FEE2_COLUMNS);
            put(Config.EsChargeVoucherCheckServiceSetDomain, ES_CHARGE_VOUCHER_CHECK_SERVICE_SET_COLUMNS);
            put(Config.EsChargeVoucherProjectPayDomain, ES_CHARGE_VOUCHER_PROJECT_PAY_COLUMNS);


            put(Config.EsChargeIncomingBack, ES_CHARGE_INCOMING_BACK_COLUMNS);
            put(Config.EsChargeIncomingConvert, ES_CHARGE_INCOMING_CONVERT_COLUMNS);
            put(Config.EsChargeIncomingKou, ES_CHARGE_INCOMING_KOU_COLUMNS);


            put(Config.EsChargeIncomingDataBill, ES_CHARGE_INCOMING_DATA_COLUMNS);
            put(Config.EsChargeVoucherMastRefundBack, ES_CHARGE_VOUCHER_MAST_REFUND_BACK_COLUMNS);
            put(Config.EsChargeVoucherMastRefundConvert, ES_CHARGE_VOUCHER_MAST_REFUND_CONVERT_COLUMNS);
            put(Config.EsChargeVoucherMastRefundKou, ES_CHARGE_VOUCHER_MAST_REFUND_KOU_COLUMNS);


            put("es_charge_bill_columns_add", ES_CHARGE_BILL_COLUMNS_ADD);
        }
    };


}
