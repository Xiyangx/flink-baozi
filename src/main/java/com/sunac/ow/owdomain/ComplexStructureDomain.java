package com.sunac.ow.owdomain;

import com.sunac.Config;
import com.sunac.domain.ColumAttribute;

import java.util.HashMap;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/8 11:08 上午
 * @Version 1.0
 */
public class ComplexStructureDomain {

    public static HashMap<String, ColumAttribute> ALL_DATA_COLUMNS = new HashMap<String, ColumAttribute>() {
        {



            put("fld_guid", new ColumAttribute("fld_guid", true, false, true, true, null));
            put("fld_create_user", new ColumAttribute("fld_create_user", true, true, true, true, ""));
            put("fld_create_date", new ColumAttribute("fld_create_date", true, true, true, true, ""));
            put("fld_modify_user", new ColumAttribute("fld_modify_user", true, true, true, true, ""));
            put("fld_modify_date", new ColumAttribute("fld_modify_date", true, true, true, true, ""));
            put("fld_tenancy", new ColumAttribute("fld_tenancy", true, true, true, true, ""));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", true, true, true, true, null));
            put("fld_area_name", new ColumAttribute("fld_area_name", true, true, true, true, ""));
            put("fld_adjust_guid", new ColumAttribute("fld_adjust_guid", true, true, true, true, ""));
            put("fld_object_guid", new ColumAttribute("fld_object_guid", true, true, true, true, null));
            put("fld_object_name", new ColumAttribute("fld_object_name", true, true, true, true, ""));
            put("fld_owner_guid", new ColumAttribute("fld_owner_guid", true, true, true, true, null));
            put("fld_owner_name", new ColumAttribute("fld_owner_name", true, true, true, true, ""));
            put("fld_project_guid", new ColumAttribute("fld_project_guid", true, true, true, true, null));
            put("fld_project_name", new ColumAttribute("fld_project_name", true, true, true, true, ""));
            put("fld_project_type", new ColumAttribute("fld_project_type", true, true, true, true, 99));
            put("fld_total", new ColumAttribute("fld_total", true, true, true, true, null));
            put("fld_left_total", new ColumAttribute("fld_left_total", true, true, true, true, null));
            put("fld_amount", new ColumAttribute("fld_amount", true, true, true, true, null));
            put("fld_rebate", new ColumAttribute("fld_rebate", true, true, true, true, null));
            put("fld_late_total", new ColumAttribute("fld_late_total", true, true, true, true, null));
            put("fld_late_fee", new ColumAttribute("fld_late_fee", true, true, true, true, null));
            put("fld_late_date", new ColumAttribute("fld_late_date", true, true, true, true, ""));
            put("fld_late_stop", new ColumAttribute("fld_late_stop", true, true, true, true, null));
            put("fld_desc", new ColumAttribute("fld_desc", true, true, true, true, ""));
            put("fld_owner_date", new ColumAttribute("fld_owner_date", true, true, true, true, ""));
            put("fld_allot_date", new ColumAttribute("fld_allot_date", true, true, true, true, ""));
            put("fld_finance_date", new ColumAttribute("fld_finance_date", true, true, true, true, ""));
            put("fld_point_date", new ColumAttribute("fld_point_date", true, true, true, true, ""));
            put("fld_start_date", new ColumAttribute("fld_start_date", true, true, true, true, ""));
            put("fld_end_date", new ColumAttribute("fld_end_date", true, true, true, true, ""));
            put("fld_start_read", new ColumAttribute("fld_start_read", true, true, true, true, null));
            put("fld_end_read", new ColumAttribute("fld_end_read", true, true, true, true, null));
            put("fld_number", new ColumAttribute("fld_number", true, true, true, true, null));
            put("fld_income", new ColumAttribute("fld_income", true, true, true, true, null));
            put("fld_income_source", new ColumAttribute("fld_income_source", true, true, true, true, null));
            put("fld_reason_guid", new ColumAttribute("fld_reason_guid", true, true, true, true, null));
            put("fld_reason_name", new ColumAttribute("fld_reason_name", true, true, true, true, ""));
            put("fld_reason_remark", new ColumAttribute("fld_reason_remark", true, true, true, true, ""));
            put("fld_resource", new ColumAttribute("fld_resource", true, true, true, true, ""));
            put("fld_price", new ColumAttribute("fld_price", true, true, true, true, ""));
            put("fld_busi_guid", new ColumAttribute("fld_busi_guid", true, true, true, true, ""));
            put("fld_dq", new ColumAttribute("fld_dq", true, true, true, true, ""));
            put("fld_ywdy", new ColumAttribute("fld_ywdy", true, true, true, true, ""));
            put("fld_xm", new ColumAttribute("fld_xm", true, true, true, true, ""));
            put("fld_company", new ColumAttribute("fld_company", true, true, true, true, ""));
            put("fld_confirm_date", new ColumAttribute("fld_confirm_date", true, true, true, true, ""));
            put("fld_fee_type", new ColumAttribute("fld_fee_type", true, true, true, true, ""));
            put("fld_is_owner", new ColumAttribute("fld_is_owner", true, true, true, true, 99));
            put("fld_owner_fee_date", new ColumAttribute("fld_owner_fee_date", true, true, true, true, ""));
            put("fld_yt", new ColumAttribute("fld_yt", true, true, true, true, ""));
            put("fld_object_class_name", new ColumAttribute("fld_object_class_name", true, true, true, true, ""));
            put("fld_project_period_name", new ColumAttribute("fld_project_period_name", true, true, true, true, ""));
            put("fld_settle_status", new ColumAttribute("fld_settle_status", true, true, true, true, null));
            put("fld_attribute", new ColumAttribute("fld_attribute", true, true, true, true, null));
            put("fld_settle_bill_no", new ColumAttribute("fld_settle_bill_no", true, true, true, true, ""));
            put("fld_settle_adjust_guid", new ColumAttribute("fld_settle_adjust_guid", true, true, true, true, ""));
            put("fld_owner_fee_guid", new ColumAttribute("fld_owner_fee_guid", true, true, true, true, ""));
            put("fld_main_guid", new ColumAttribute("fld_main_guid", true, true, true, true, ""));
            put("fld_examine_status", new ColumAttribute("fld_examine_status", true, true, true, true, null));
            put("fld_batch", new ColumAttribute("fld_batch", true, true, true, true, ""));
            put("fld_building", new ColumAttribute("fld_building", true, true, true, true, ""));
            put("fld_cell", new ColumAttribute("fld_cell", true, true, true, true, ""));
            put("fld_charged_area", new ColumAttribute("fld_charged_area", true, true, true, true, null));
            put("fld_obj_status", new ColumAttribute("fld_obj_status", true, true, true, true, null));
            put("fld_ticket_status", new ColumAttribute("fld_ticket_status", true, true, true, true, null));
            put("fld_co_bill_no", new ColumAttribute("fld_co_bill_no", true, true, true, true, ""));
            put("fld_object_type", new ColumAttribute("fld_object_type", true, true, true, true, null));
            put("fld_rate", new ColumAttribute("fld_rate", true, true, true, true, null));
            put("fld_taxes", new ColumAttribute("fld_taxes", true, true, true, true, null));
            put("fld_start_fee_date", new ColumAttribute("fld_start_fee_date", true, true, true, true, ""));
            put("fld_phone_number", new ColumAttribute("fld_phone_number", true, true, true, true, ""));
            put("fld_owner_desc", new ColumAttribute("fld_owner_desc", true, true, true, true, ""));
            put("bm_stop_date", new ColumAttribute("bm_stop_date", true, true, true, true, ""));
            put("bm_end_date", new ColumAttribute("bm_end_date", true, true, true, true, ""));
            put("data_fld_create_date", new ColumAttribute("data_fld_create_date", true, true, true, true, ""));
            put("data_fld_operate_date", new ColumAttribute("data_fld_operate_date", true, true, true, true, ""));
            put("data_fld_create_user", new ColumAttribute("data_fld_create_user", true, true, true, true, ""));
            put("data_fld_busi_type", new ColumAttribute("data_fld_busi_type", true, true, true, true, null));
            put("fee_fld_cancel_me", new ColumAttribute("fee_fld_cancel_me", true, true, true, true, ""));
            put("fld_examine_date", new ColumAttribute("fld_examine_date", true, true, true, true, ""));
            put("obj_fld_order", new ColumAttribute("obj_fld_order", true, true, true, true, null));
            put("data_fld_total", new ColumAttribute("data_fld_total", true, true, true, true, null));
            put("data_fld_amount", new ColumAttribute("data_fld_amount", true, true, true, true, null));
            put("data_fld_late_fee", new ColumAttribute("data_fld_late_fee", true, true, true, true, null));
            put("data_fld_tax_amount", new ColumAttribute("data_fld_tax_amount", true, true, true, true, null));
            put("data_fld_tax", new ColumAttribute("data_fld_tax", true, true, true, true, null));
            put("data_fld_cancel", new ColumAttribute("data_fld_cancel", true, true, true, true, null));
            put("data_fld_late_amount", new ColumAttribute("data_fld_late_amount", true, true, true, true, null));
            put("a_fld_guid", new ColumAttribute("a_fld_guid", true, true, true, true, ""));
            put("o_fld_guid", new ColumAttribute("o_fld_guid", true, true, true, true, ""));
            put("o_fld_class_guid", new ColumAttribute("o_fld_class_guid", true, true, true, true, ""));
            put("cif_fld_guid", new ColumAttribute("cif_fld_guid", true, true, true, true, ""));
            put("w_fld_guid", new ColumAttribute("w_fld_guid", true, true, true, true, ""));
            put("p_fld_guid", new ColumAttribute("p_fld_guid", true, true, true, true, ""));
            put("c_fld_guid", new ColumAttribute("c_fld_guid", true, true, true, true, ""));
            put("ecrr_fld_guid", new ColumAttribute("ecrr_fld_guid", true, true, true, true, ""));
            put("ecrr_fld_general_tax", new ColumAttribute("ecrr_fld_general_tax", true, true, true, true, ""));
            put("pj_fld_guid", new ColumAttribute("pj_fld_project_guid", true, true, true, true, ""));
            put("pj_fld_project_guid", new ColumAttribute("pj_fld_project_guid", true, true, true, true, ""));
            put("pj_fld_period_guid", new ColumAttribute("pj_fld_period_guid", true, true, true, true, ""));
            put("pp_fld_guid", new ColumAttribute("pp_fld_guid", true, true, true, true, ""));
            put("ad_fld_guid", new ColumAttribute("ad_fld_guid", true, true, true, true, ""));
            put("ad_fld_create_date", new ColumAttribute("ad_fld_create_date", false, true, true, true, ""));
            put("ad_fld_status", new ColumAttribute("ad_fld_status", false, true, true, true, ""));
            put("ad_fld_area_guid", new ColumAttribute("ad_fld_area_guid", false, true, true, true, ""));
            put("ad_fld_main_guid", new ColumAttribute("ad_fld_main_guid", false, true, true, true, ""));
            put("am_fld_guid", new ColumAttribute("am_fld_guid", true, true, true, true, ""));
            put("am_fld_area_guid", new ColumAttribute("am_fld_area_guid", false, true, true, true, ""));
            put("ct_fld_guid", new ColumAttribute("ct_fld_guid", true, true, true, true, ""));
            put("ct_fld_owner_fee_guid", new ColumAttribute("ct_fld_owner_fee_guid", false, false, true, true, ""));
            put("ct_fld_operate_guid", new ColumAttribute("ct_fld_operate_guid", false, true, true, true, ""));
            put("co_fld_guid", new ColumAttribute("co_fld_guid", true, true, true, true, ""));
            put("da_fld_guid", new ColumAttribute("da_fld_guid", true, true, true, true, ""));
            put("da_fld_owner_fee_guid", new ColumAttribute("da_fld_owner_fee_guid", false, true, true, true, ""));
            put("da_fld_area_guid", new ColumAttribute("da_fld_area_guid", false, true, true, true, ""));
            put("da_fld_incoming_fee_guid", new ColumAttribute("da_fld_incoming_fee_guid", false, true, true, true, ""));

            put("oao_fld_guid", new ColumAttribute("oao_fld_guid", true, true, true, true, ""));
            put("oao_fld_object_guid", new ColumAttribute("oao_fld_object_guid", false, true, true, true, ""));
            put("oao_fld_owner_guid", new ColumAttribute("oao_fld_owner_guid", false, true, true, true, ""));
            put("oao_fld_area_guid", new ColumAttribute("oao_fld_area_guid", false, true, true, true, ""));
            //oao排序字段
            put("fld_is_current", new ColumAttribute("fld_is_current", false, true, true, false, null));
            put("fld_is_charge", new ColumAttribute("fld_is_charge", false, true, true, false, null));
            put("fld_status", new ColumAttribute("fld_status", false, true, true, false, null));
            //cbfo
            put("cbfo_fld_guid", new ColumAttribute("cbfo_fld_guid", true, true, true, false, null));
            put("cbfo_fld_bond_guid", new ColumAttribute("cbfo_fld_bond_guid", false, true, true, false, null));
            put("cbfo_fld_object_guid", new ColumAttribute("cbfo_fld_object_guid", false, true, true, false, null));
            //cbm
            put("cbm_fld_guid", new ColumAttribute("cbm_fld_guid", true, true, true, false, null));
            put("cbm_fld_area_guid", new ColumAttribute("cbm_fld_area_guid", false, true, true, false, null));
            put("cbm_fld_owner_guid", new ColumAttribute("cbm_fld_owner_guid", false, true, true, false, null));

            put("fld_name", new ColumAttribute("fld_name", false, true, true, false, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_INFO_AREA_INFO_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("a_fld_guid", true, false, true, true, null));
            put("fld_dq", new ColumAttribute("fld_dq", true, true, true, true, ""));
            put("fld_ywdy", new ColumAttribute("fld_ywdy", true, true, true, true, ""));
            put("fld_xm", new ColumAttribute("fld_xm", true, true, true, true, ""));
            put("fld_company", new ColumAttribute("fld_company", true, true, true, true, ""));
            put("fld_confirm_date", new ColumAttribute("fld_confirm_date", true, true, true, true, "1970-01-01"));
            put("fld_fee_type", new ColumAttribute("fld_fee_type", true, true, true, true, ""));
            put("fld_yt", new ColumAttribute("fld_yt", true, true, true, true, ""));

        }
    };

    public static HashMap<String, ColumAttribute> ES_INFO_OBJECT_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("o_fld_guid", true, false, true, true, null));
            put("fld_batch", new ColumAttribute("fld_batch", true, true, true, true, ""));
            put("fld_building", new ColumAttribute("fld_building", true, true, true, true, ""));
            put("fld_cell", new ColumAttribute("fld_cell", true, true, true, true, ""));
            put("fld_charged_area", new ColumAttribute("fld_charged_area", true, true, true, true, 0));
            put("fld_status", new ColumAttribute("fld_obj_status", true, true, true, true, 99));
            put("fld_start_fee_date", new ColumAttribute("fld_start_fee_date", true, true, true, true, "是"));
            put("fld_order", new ColumAttribute("obj_fld_order", true, true, true, true, -1));
            put("fld_owner_fee_date", new ColumAttribute("fld_owner_fee_date", true, true, true, true, "1970-01-01"));
            put("fld_class_guid", new ColumAttribute("o_fld_class_guid", false, true, true, true, ""));

        }
    };
    public static HashMap<String, ColumAttribute> ES_INFO_OWNER_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("w_fld_guid", true, false, true, true, null));
            put("fld_phone_number", new ColumAttribute("fld_phone_number", true, true, true, true, "是"));
            put("fld_desc", new ColumAttribute("fld_owner_desc", true, true, true, true, ""));

        }
    };
    public static HashMap<String, ColumAttribute> ES_CHARGE_PROJECT_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("p_fld_guid", true, false, true, true, null));
            put("fld_object_type", new ColumAttribute("fld_object_type", true, true, true, true, 99));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_RATE_RESULT_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ecrr_fld_guid", true, false, true, true, null));
            put("fld_general_tax", new ColumAttribute("fld_rate", true, true, true, true, 0));
            put("fld_taxes", new ColumAttribute("fld_taxes", true, true, true, true, 0));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", false, true, true, true, null));
            put("fld_project_guid", new ColumAttribute("fld_project_guid", false, true, true, true, null));
            put("fld_start_date", new ColumAttribute("fld_start_date", false, true, true, true, null));
            put("fld_end_date", new ColumAttribute("fld_end_date", false, true, true, true, null));

        }
    };

    public static HashMap<String, ColumAttribute> ES_INFO_OBJECT_CLASS_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("c_fld_guid", true, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_object_class_name", true, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_INFO_OBJECT_AND_OWNER_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("oao_fld_guid", true, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("fld_area_guid", false, true, true, true, null));
            put("fld_object_guid", new ColumAttribute("fld_object_guid", false, true, true, true, null));
            put("fld_owner_guid", new ColumAttribute("fld_owner_guid", false, true, true, true, null));
            put("fld_is_owner", new ColumAttribute("fld_is_owner", true, true, true, true, 99));
            // 排序字段
            put("fld_is_current", new ColumAttribute("fld_is_current", false, true, false, false, null));
            put("fld_is_charge", new ColumAttribute("fld_is_charge", false, true, false, false, null));
            put("fld_status", new ColumAttribute("fld_status", false, true, false, false, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_PROJECT_PERIOD_JOIN_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("pj_fld_guid", true, false, true, true, null));
            put("fld_project_guid", new ColumAttribute("pj_fld_project_guid", true, true, true, true, null));
            put("fld_period_guid", new ColumAttribute("pj_fld_period_guid", true, true, true, true, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_PROJECT_PERIOD_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("pp_fld_guid", true, false, true, true, null));
            put("fld_type", new ColumAttribute("pp_fld_type", false, true, true, true, null));
            put("fld_name", new ColumAttribute("fld_project_period_name", true, true, true, true, null));

        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_SETTLE_ACCOUNTS_DETAIL_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ad_fld_guid", true, false, true, true, null));
            put("fld_owner_fee_guid", new ColumAttribute("fld_owner_fee_guid", true, true, true, true, ""));
            put("fld_adjust_guid", new ColumAttribute("fld_settle_adjust_guid", true, true, true, true, ""));
            put("fld_status", new ColumAttribute("ad_fld_status", false, true, true, true, null));
            put("fld_area_guid", new ColumAttribute("ad_fld_area_guid", false, true, true, true, ""));
            put("fld_main_guid", new ColumAttribute("ad_fld_main_guid", false, true, true, true, ""));
            put("fld_create_date", new ColumAttribute("ad_fld_create_date", false, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_SETTLE_ACCOUNTS_MAIN_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("am_fld_guid", true, false, true, true, ""));
            put("fld_bill_no", new ColumAttribute("fld_settle_bill_no", true, true, true, true, ""));
            put("fld_area_guid", new ColumAttribute("am_fld_area_guid", false, true, true, true, ""));
            put("fld_examine_status", new ColumAttribute("fld_examine_status", true, true, true, true, 99));
            put("fld_settle_status", new ColumAttribute("fld_settle_status", true, true, true, true, 99));
            put("fld_attribute", new ColumAttribute("fld_attribute", true, true, true, true, 99));
            put("fld_examine_date", new ColumAttribute("fld_examine_date", true, true, true, true, "1970-01-01"));

        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_TICKET_PAY_DETAIL_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("ct_fld_guid", true, false, true, true, null));
            put("fld_owner_fee_guid", new ColumAttribute("ct_fld_owner_fee_guid", false, true, true, true, null));
            put("fld_operate_guid", new ColumAttribute("ct_fld_operate_guid", false, true, true, true, null));
            put("fld_create_date", new ColumAttribute("ct_fld_create_date", false, true, true, true, null));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_TICKET_PAY_OPERATE_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("co_fld_guid", true, false, true, true, null));
            put("fld_ticket_status", new ColumAttribute("fld_ticket_status", true, true, true, true, 99));
            put("fld_bill_no", new ColumAttribute("fld_co_bill_no", true, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_DATA_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("da_fld_guid", true, false, true, true, null));
            put("fld_owner_fee_guid", new ColumAttribute("da_fld_owner_fee_guid", false, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("da_fld_area_guid", false, true, true, true, ""));
            put("fld_create_date", new ColumAttribute("data_fld_create_date", true, true, true, true, "1970-01-01"));
            put("fld_operate_date", new ColumAttribute("data_fld_operate_date", true, true, true, true, "1970-01-01"));
            put("fld_create_user", new ColumAttribute("data_fld_create_user", true, true, true, true, ""));
            put("fld_busi_type", new ColumAttribute("data_fld_busi_type", true, true, true, true, 99));
            put("fld_total", new ColumAttribute("data_fld_total", true, true, true, true, 0));
            put("fld_amount", new ColumAttribute("data_fld_amount", true, true, true, true, 0));
            put("fld_late_fee", new ColumAttribute("data_fld_late_fee", true, true, true, true, 0));
            put("fld_tax_amount", new ColumAttribute("data_fld_tax_amount", true, true, true, true, 0));
            put("fld_cancel", new ColumAttribute("data_fld_cancel", true, true, true, true, 99));
            put("fld_late_amount", new ColumAttribute("data_fld_late_amount", true, true, true, true, 0));
            put("fld_tax", new ColumAttribute("data_fld_tax", true, true, true, true, 0));
            put("fld_incoming_fee_guid", new ColumAttribute("da_fld_incoming_fee_guid", false, true, true, true, ""));

        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_INCOMING_FEE_COLUMNS = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("cif_fld_guid", true, false, true, true, null));
            put("fld_cancel_me", new ColumAttribute("fee_fld_cancel_me", true, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_COMMERCE_BOND_MAIN = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("cbm_fld_guid", true, false, true, true, null));
            put("fld_area_guid", new ColumAttribute("cbm_fld_area_guid", false, true, true, true, ""));
            put("fld_owner_guid", new ColumAttribute("cbm_fld_owner_guid", false, true, true, true, ""));
            put("fld_stop_date", new ColumAttribute("fld_stop_date", true, true, true, true, ""));
            put("fld_end_date", new ColumAttribute("fld_end_date", true, true, true, true, ""));

        }
    };

    public static HashMap<String, ColumAttribute> ES_COMMERCE_BOND_FEE_OBJECT = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("cbfo_fld_guid", true, false, true, true, null));
            put("fld_object_guid", new ColumAttribute("cbfo_fld_object_guid", false, true, true, true, ""));
            put("fld_bond_guid", new ColumAttribute("cbfo_fld_bond_guid", false, true, true, true, ""));
        }
    };

    public static HashMap<String, ColumAttribute> ES_CHARGE_SETTLE_ACCOUNTS_MAIN_COLUMNS_ADD = new HashMap<String, ColumAttribute>() {
        {
            put("ad_fld_guid", new ColumAttribute("ad_fld_guid", true, false, true, true, null));
            put("fld_owner_fee_guid", new ColumAttribute("fld_owner_fee_guid", true, true, true, true, ""));
            put("fld_settle_adjust_guid", new ColumAttribute("fld_settle_adjust_guid", true, true, true, true, ""));
            put("fld_status", new ColumAttribute("ad_fld_status", false, true, true, true, null));
            put("ad_fld_area_guid", new ColumAttribute("ad_fld_area_guid", false, true, true, true, ""));
            put("fld_main_guid", new ColumAttribute("fld_main_guid", true, true, true, true, ""));
            put("fld_create_date", new ColumAttribute("ad_fld_create_date", false, true, true, true, ""));
            put("am_fld_guid", new ColumAttribute("am_fld_guid", true, false, true, true, ""));
            put("fld_settle_bill_no", new ColumAttribute("fld_settle_bill_no", true, true, true, true, ""));
            put("am_fld_area_guid", new ColumAttribute("am_fld_area_guid", false, true, true, true, ""));
            put("fld_examine_status", new ColumAttribute("fld_examine_status", true, true, true, true, 99));
            put("fld_settle_status", new ColumAttribute("fld_settle_status", true, true, true, true, 99));
            put("fld_attribute", new ColumAttribute("fld_attribute", true, true, true, true, 99));
            put("fld_examine_date", new ColumAttribute("fld_examine_date", true, true, true, true, "1970-01-01"));

        }
    };
    public static HashMap<String, ColumAttribute> ES_INFO_PARAM_INFO = new HashMap<String, ColumAttribute>() {
        {
            put("fld_guid", new ColumAttribute("pi_fld_guid", false, false, true, true, null));
            put("fld_name", new ColumAttribute("fld_reason_name", true, true, true, true, ""));
        }
    };
    public static HashMap<String, HashMap<String, ColumAttribute>> TABLES = new HashMap<String, HashMap<String, ColumAttribute>>() {
        {
            put(Config.AllData, ALL_DATA_COLUMNS);
            put(Config.EsInfoAreaInfo, ES_INFO_AREA_INFO_COLUMNS);
            put(Config.EsInfoObject, ES_INFO_OBJECT_COLUMNS);
            put(Config.EsInfoOwner, ES_INFO_OWNER_COLUMNS);
            put(Config.EsChargeProject, ES_CHARGE_PROJECT_COLUMNS);
            put(Config.EsChargeRateResult, ES_CHARGE_RATE_RESULT_COLUMNS);
            put(Config.EsInfoObjectClass, ES_INFO_OBJECT_CLASS_COLUMNS);
            put(Config.EsInfoObjectAndOwner, ES_INFO_OBJECT_AND_OWNER_COLUMNS);
            put(Config.EsChargeProjectPeriodJoin, ES_CHARGE_PROJECT_PERIOD_JOIN_COLUMNS);
            put(Config.EsChargeProjectPeriod, ES_CHARGE_PROJECT_PERIOD_COLUMNS);
            put(Config.EsChargeSettleAccountsDetail, ES_CHARGE_SETTLE_ACCOUNTS_DETAIL_COLUMNS);
            put(Config.EsChargeSettleAccountsMain, ES_CHARGE_SETTLE_ACCOUNTS_MAIN_COLUMNS);
            put(Config.EsChargeTicketPayDetail, ES_CHARGE_TICKET_PAY_DETAIL_COLUMNS);
            put(Config.EsChargeTicketPayOperate, ES_CHARGE_TICKET_PAY_OPERATE_COLUMNS);
            put(Config.EsChargeIncomingData, ES_CHARGE_INCOMING_DATA_COLUMNS);
            put(Config.EsChargeIncomingFee, ES_CHARGE_INCOMING_FEE_COLUMNS);
            put(Config.EsCommerceBondMain, ES_COMMERCE_BOND_MAIN);
            put(Config.EsCommerceBondFeeObject, ES_COMMERCE_BOND_FEE_OBJECT);
            put(Config.EsInfoParamInfo, ES_INFO_PARAM_INFO);
        }
    };
}
