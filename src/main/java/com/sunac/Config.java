package com.sunac;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac
 * @date:2022/9/13
 */
public class Config {

    public static String PASS = "PASS";

    public static String SIDE_STREAM = "side_stream";
    public static String INSERT = "INSERT";
    public static String DELETE = "DELETE";
    public static String UPDATE = "UPDATE";

    public static String SINGLE_PARTITION = "666";
    public static String TABLE = "table";
    public static String INSERTSQL = "INSERT INTO es_charge_owner_fee_all_data (fld_guid, fld_create_user, fld_create_date, fld_modify_user, fld_modify_date, fld_tenancy, fld_area_guid, fld_area_name, fld_adjust_guid, fld_object_guid, fld_object_name, fld_owner_guid, fld_owner_name, fld_project_guid, fld_project_name, fld_project_type, fld_total, fld_left_total, fld_amount, fld_rebate, fld_late_total, fld_late_fee, fld_late_date, fld_late_stop, fld_desc, fld_owner_date, fld_allot_date, fld_finance_date, fld_point_date, fld_start_date, fld_end_date, fld_start_read, fld_end_read, fld_number, fld_income, fld_income_source, fld_reason_guid, fld_reason_name, fld_reason_remark, fld_resource, fld_price, fld_busi_guid, fld_dq, fld_ywdy, fld_xm, fld_company, fld_confirm_date, fld_fee_type, fld_is_owner, fld_owner_fee_date, fld_yt, fld_object_class_name, fld_project_period_name, fld_settle_status, fld_attribute, fld_settle_bill_no, fld_settle_adjust_guid, fld_owner_fee_guid, fld_main_guid, fld_examine_status, fld_batch, fld_building, fld_cell, fld_charged_area, fld_obj_status, fld_ticket_status, fld_co_bill_no, fld_object_type, fld_rate, fld_taxes, fld_start_fee_date, fld_phone_number, fld_owner_desc, bm_stop_date, bm_end_date, data_fld_create_date, data_fld_operate_date, data_fld_create_user, data_fld_busi_type, fee_fld_cancel_me, fld_examine_date, obj_fld_order, data_fld_total, data_fld_amount, data_fld_late_fee, data_fld_tax_amount, data_fld_tax, data_fld_cancel, data_fld_late_amount, a_fld_guid, o_fld_guid, cif_fld_guid, w_fld_guid, p_fld_guid, c_fld_guid, ecrr_fld_guid, pj_fld_guid, pp_fld_guid, ad_fld_guid, am_fld_guid, ct_fld_guid, co_fld_guid, da_fld_guid, oao_fld_guid, cbfo_fld_guid, cbm_fld_guid, ecrr_fld_general_tax, pj_fld_project_guid, pj_fld_period_guid,update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE fld_guid=VALUES (fld_guid), fld_create_user= VALUES (fld_create_user), fld_create_date= VALUES (fld_create_date), fld_modify_user= VALUES (fld_modify_user), fld_modify_date= VALUES (fld_modify_date), fld_tenancy= VALUES (fld_tenancy), fld_area_guid= VALUES (fld_area_guid), fld_area_name= VALUES (fld_area_name), fld_adjust_guid= VALUES (fld_adjust_guid), fld_object_guid= VALUES (fld_object_guid), fld_object_name= VALUES (fld_object_name), fld_owner_guid= VALUES (fld_owner_guid), fld_owner_name= VALUES (fld_owner_name), fld_project_guid= VALUES (fld_project_guid), fld_project_name= VALUES (fld_project_name), fld_project_type= VALUES (fld_project_type), fld_total= VALUES (fld_total), fld_left_total= VALUES (fld_left_total), fld_amount= VALUES (fld_amount), fld_rebate= VALUES (fld_rebate), fld_late_total= VALUES (fld_late_total), fld_late_fee= VALUES (fld_late_fee), fld_late_date= VALUES (fld_late_date), fld_late_stop= VALUES (fld_late_stop), fld_desc= VALUES (fld_desc), fld_owner_date= VALUES (fld_owner_date), fld_allot_date= VALUES (fld_allot_date), fld_finance_date= VALUES (fld_finance_date), fld_point_date= VALUES (fld_point_date), fld_start_date= VALUES (fld_start_date), fld_end_date= VALUES (fld_end_date), fld_start_read= VALUES (fld_start_read), fld_end_read= VALUES (fld_end_read), fld_number= VALUES (fld_number), fld_income= VALUES (fld_income), fld_income_source= VALUES (fld_income_source), fld_reason_guid= VALUES (fld_reason_guid), fld_reason_name= VALUES (fld_reason_name), fld_reason_remark= VALUES (fld_reason_remark), fld_resource= VALUES (fld_resource), fld_price= VALUES (fld_price), fld_busi_guid= VALUES (fld_busi_guid), fld_dq= VALUES (fld_dq), fld_ywdy= VALUES (fld_ywdy), fld_xm= VALUES (fld_xm), fld_company= VALUES (fld_company), fld_confirm_date= VALUES (fld_confirm_date), fld_fee_type= VALUES (fld_fee_type), fld_is_owner= VALUES (fld_is_owner), fld_owner_fee_date= VALUES (fld_owner_fee_date), fld_yt= VALUES (fld_yt), fld_object_class_name= VALUES (fld_object_class_name), fld_project_period_name= VALUES (fld_project_period_name), fld_settle_status= VALUES (fld_settle_status), fld_attribute= VALUES (fld_attribute), fld_settle_bill_no= VALUES (fld_settle_bill_no), fld_settle_adjust_guid= VALUES (fld_settle_adjust_guid), fld_owner_fee_guid= VALUES (fld_owner_fee_guid), fld_main_guid= VALUES (fld_main_guid), fld_examine_status= VALUES (fld_examine_status), fld_batch= VALUES (fld_batch), fld_building= VALUES (fld_building), fld_cell= VALUES (fld_cell), fld_charged_area= VALUES (fld_charged_area), fld_obj_status= VALUES (fld_obj_status), fld_ticket_status= VALUES (fld_ticket_status), fld_co_bill_no= VALUES (fld_co_bill_no), fld_object_type= VALUES (fld_object_type), fld_rate= VALUES (fld_rate), fld_taxes= VALUES (fld_taxes), fld_start_fee_date= VALUES (fld_start_fee_date), fld_phone_number= VALUES (fld_phone_number), fld_owner_desc= VALUES (fld_owner_desc), bm_stop_date= VALUES (bm_stop_date), bm_end_date= VALUES (bm_end_date), data_fld_create_date= VALUES (data_fld_create_date), data_fld_operate_date= VALUES (data_fld_operate_date), data_fld_create_user= VALUES (data_fld_create_user), data_fld_busi_type= VALUES (data_fld_busi_type), fee_fld_cancel_me= VALUES (fee_fld_cancel_me), fld_examine_date= VALUES (fld_examine_date), obj_fld_order= VALUES (obj_fld_order), data_fld_total= VALUES (data_fld_total), data_fld_amount= VALUES (data_fld_amount), data_fld_late_fee= VALUES (data_fld_late_fee), data_fld_tax_amount= VALUES (data_fld_tax_amount), data_fld_tax= VALUES (data_fld_tax), data_fld_cancel= VALUES (data_fld_cancel), data_fld_late_amount= VALUES (data_fld_late_amount), a_fld_guid= VALUES (a_fld_guid), o_fld_guid= VALUES (o_fld_guid), cif_fld_guid= VALUES (cif_fld_guid), w_fld_guid= VALUES (w_fld_guid), p_fld_guid= VALUES (p_fld_guid), c_fld_guid= VALUES (c_fld_guid), ecrr_fld_guid= VALUES (ecrr_fld_guid), pj_fld_guid= VALUES (pj_fld_guid), pp_fld_guid= VALUES (pp_fld_guid), ad_fld_guid= VALUES (ad_fld_guid), am_fld_guid= VALUES (am_fld_guid), ct_fld_guid= VALUES (ct_fld_guid), co_fld_guid= VALUES (co_fld_guid), da_fld_guid= VALUES (da_fld_guid), oao_fld_guid= VALUES (oao_fld_guid), cbfo_fld_guid= VALUES (cbfo_fld_guid), cbm_fld_guid= VALUES (cbm_fld_guid), ecrr_fld_general_tax= VALUES (ecrr_fld_general_tax), pj_fld_project_guid= VALUES (pj_fld_project_guid), pj_fld_period_guid= VALUES (pj_fld_period_guid), update_time= VALUES (update_time)";
    public static String UPDATESQL = "update es_charge_owner_fee_all_data set fld_create_user=?, fld_create_date=?, fld_modify_user=?, fld_modify_date=?, fld_tenancy=?, fld_area_guid=?, fld_area_name=?, fld_adjust_guid=?, fld_object_guid=?, fld_object_name=?, fld_owner_guid=?, fld_owner_name=?, fld_project_guid=?, fld_project_name=?, fld_project_type=?, fld_total=?, fld_left_total=?, fld_amount=?, fld_rebate=?, fld_late_total=?, fld_late_fee=?, fld_late_date=?, fld_late_stop=?, fld_desc=?, fld_owner_date=?, fld_allot_date=?, fld_finance_date=?, fld_point_date=?, fld_start_date=?, fld_end_date=?, fld_start_read=?, fld_end_read=?, fld_number=?, fld_income=?, fld_income_source=?, fld_reason_guid=?, fld_reason_name=?, fld_reason_remark=?, fld_resource=?, fld_price=?, fld_busi_guid=?, fld_dq=?, fld_ywdy=?, fld_xm=?, fld_company=?, fld_confirm_date=?, fld_fee_type=?, fld_is_owner=?, fld_owner_fee_date=?, fld_yt=?, fld_object_class_name=?, fld_project_period_name=?, fld_settle_status=?, fld_attribute=?, fld_settle_bill_no=?, fld_settle_adjust_guid=?, fld_owner_fee_guid=?, fld_main_guid=?, fld_examine_status=?, fld_batch=?, fld_building=?, fld_cell=?, fld_charged_area=?, fld_obj_status=?, fld_ticket_status=?, fld_co_bill_no=?, fld_object_type=?, fld_rate=?, fld_taxes=?, fld_start_fee_date=?, fld_phone_number=?, fld_owner_desc=?, bm_stop_date=?, bm_end_date=?, data_fld_create_date=?, data_fld_operate_date=?, data_fld_create_user=?, data_fld_busi_type=?, fee_fld_cancel_me=?, fld_examine_date=?, obj_fld_order=?, data_fld_total=?, data_fld_amount=?, data_fld_late_fee=?, data_fld_tax_amount=?, data_fld_tax=?, data_fld_cancel=?, data_fld_late_amount=?, a_fld_guid=?, o_fld_guid=?, cif_fld_guid=?, w_fld_guid=?, p_fld_guid=?, c_fld_guid=?, ecrr_fld_guid=?, pj_fld_guid=?, pp_fld_guid=?, ad_fld_guid=?, am_fld_guid=?, ct_fld_guid=?, co_fld_guid=?, da_fld_guid=?, oao_fld_guid=?, cbfo_fld_guid=?, cbm_fld_guid=?, ecrr_fld_general_tax=?, pj_fld_project_guid=?, pj_fld_period_guid=? WHERE fld_guid=?";
    public static String DELETESQL = "delete from es_charge_owner_fee_all_data where fld_guid = ?";


    // TODO:全类名字
    public static String EsChargeIncomingFee = "com.sunac.ow.owdomain.EsChargeIncomingFee";
    public static String AllData = "com.sunac.ow.owdomain.AllData";
    public static String EsInfoAreaInfo = "com.sunac.ow.owdomain.EsInfoAreaInfo";
    public static String EsInfoObject = "com.sunac.ow.owdomain.EsInfoObject";
    public static String EsInfoOwner = "com.sunac.ow.owdomain.EsInfoOwner";
    public static String EsInfoObjectClass = "com.sunac.ow.owdomain.EsInfoObjectClass";
    public static String EsChargeSettleAccountsDetail = "com.sunac.ow.owdomain.EsChargeSettleAccountsDetail";
    public static String EsChargeSettleAccountsMain = "com.sunac.ow.owdomain.EsChargeSettleAccountsMain";
    public static String EsChargeProject = "com.sunac.ow.owdomain.EsChargeProject";
    public static String EsChargeRateResult = "com.sunac.ow.owdomain.EsChargeRateResult";
    public static String EsInfoObjectAndOwner = "com.sunac.ow.owdomain.EsInfoObjectAndOwner";
    public static String EsChargeProjectPeriodJoin = "com.sunac.ow.owdomain.EsChargeProjectPeriodJoin";
    public static String EsChargeProjectPeriod = "com.sunac.ow.owdomain.EsChargeProjectPeriod";
    public static String EsChargeTicketPayDetail = "com.sunac.ow.owdomain.EsChargeTicketPayDetail";
    public static String EsChargeTicketPayOperate = "com.sunac.ow.owdomain.EsChargeTicketPayOperate";
    public static String EsChargeIncomingData = "com.sunac.ow.owdomain.EsChargeIncomingData";
    public static String EsCommerceBondMain = "com.sunac.ow.owdomain.EsCommerceBondMain";
    public static String EsCommerceBondFeeObject = "com.sunac.ow.owdomain.EsCommerceBondFeeObject";
    public static String EsInfoParamInfo = "com.sunac.ow.owdomain.EsInfoParamInfo";

    public static String ESINFOAREAINFO_UID = "EsInfoAreaInfo";
    public static String ESINFOOBJECT_UID = "EsInfoObject";
    public static String ESINFOOBJECTCLASS_UID = "EsInfoObjectClass";
    public static String ESINFOOWNER_UID = "EsInfoOwner";
    public static String ESCHARGEPROJECT_UID = "EsChargeProject";
    public static String ESCHARGERATERESULT_UID = "EsChargeRateResult";
    public static String ESINFOOBJECTANDOWNER_UID = "EsInfoObjectAndOwner";
    public static String ESCHARGEPROJECTPERIODJOIN_UID = "EsChargeProjectPeriodJoin";
    public static String ESCHARGEPROJECTPERIOD_UID = "EsChargeProjectPeriod";
    public static String ESCHARGESETTLEACCOUNTSDETAIL_UID = "EsChargeSettleAccountsDetail";
    public static String ESCHARGESETTLEACCOUNTSMAIN_UID = "EsChargeSettleAccountsMain";
    public static String ESCHARGETICKETPAYDETAIL_UID = "EsChargeTicketPayDetail";
    public static String ESCHARGETICKETPAYOPERATE_UID = "EsChargeTicketPayOperate";
    public static String ESCOMMERCEBONDFEEOBJECT_UID = "EsCommerceBondFeeObject";
    public static String ESCOMMERCEBONDMAIN_UID = "EsCommerceBondMain";
    public static String ESCHARGEINCOMINGDATA_UID = "EsChargeIncomingData";
    public static String ESCHARGEINCOMINGFEE_UID = "EsChargeIncomingFee";
    public static String ESINFOPARAMINFO_UID = "EsInfoParamInfo";

}
