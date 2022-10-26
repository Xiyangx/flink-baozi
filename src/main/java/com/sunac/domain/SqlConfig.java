package com.sunac.domain;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.domain
 * @date:2022/9/2
 */
public class SqlConfig {

    //        fld_is_current desc,是否当前(0否 1是)
//        fld_is_charge desc, 是否计费(0否 1是)
//        fld_status desc    数据状态(1可用 2不可用 6迁出7作废)

    public static String ES_CHARGE_INCOMING_FEE1 = "SELECT " + "fld_guid," + "fld_busi_type," + "fld_remark" + " FROM es_charge_incoming_fee where " + "fld_area_guid=?";
    public static String ES_INFO_AREA_INFO = "SELECT fld_guid, fld_dq,date_format(fld_confirm_date,'%Y-%m-%d %H:%i:%S') fld_confirm_date,fld_company,fld_xm,fld_ywdy,fld_yt,fld_name FROM es_info_area_info";
    public static String ES_INFO_OBJECT = "SELECT "
            + "fld_guid,"
            + "fld_name AS fld_object_name,"
            + "date_format( fld_owner_fee_date, '%Y-%m-%d %H:%i:%S' ) fld_owner_fee_date,"
            + "fld_building,"
            + "fld_cell,"
            + "fld_batch,"
            + "fld_charged_area,"
            + "fld_status AS fld_obj_status"
            + " FROM es_info_object where fld_area_guid=?";
    public static String ES_CHARGE_OWNER_FEE = "SELECT md5(CONCAT(fld_guid,fld_area_guid)) pk," +
            "fld_reason_remark," +
            "fld_price," +
            "fld_rebate," +
            "fld_resource,"
            + "fld_desc,fld_adjust_guid,fld_guid,fld_area_guid " +
            "FROM " + "es_charge_owner_fee"
            + " where fld_allot_date=?";

    public static String ES_INFO_OWNER = "SELECT fld_guid,fld_name AS fld_owner_name,fld_desc AS fld_owner_desc FROM es_info_owner where fld_area_guid=?";

    public static String ES_INFO_OBJECT_AND_OWNER = "SELECT " + "md5(CONCAT(fld_area_guid,fld_object_guid,fld_owner_guid)) pk," + "fld_guid," + "fld_room_type," + "fld_is_owner," + "fld_area_guid," + "fld_object_guid," + "fld_owner_guid," + "fld_is_current," + "fld_is_charge," + "fld_status" + " FROM es_info_object_and_owner where fld_area_guid=?";

    public static String ES_CHARGE_INCOMING_BACK = "SELECT " +
            "fld_guid," +
            "fld_incoming_back_guid," +
            "fld_incoming_convert_guid," +
            "fld_incoming_kou_guid," +
            "date_format( fld_submit_time, '%Y-%m-%d %H:%i:%S' ) fld_submit_time " +
            "FROM es_charge_incoming_back where fld_area_guid=?";

    public static String ES_CHARGE_VOUCHER_MAST_REFUND = "select " +
            "fld_guid," +
            "fld_incoming_back_guid," +
            "date_format( fld_appay_date, '%Y-%m-%d %H:%i:%S' ) fld_appay_date," +
            "date_format( fld_date, '%Y-%m-%d %H:%i:%S' ) fld_date " +
            "FROM es_charge_voucher_mast_refund";





    public static String ES_CHARGE_INCOMING_DATA_BILL = "SELECT " + "fld_data_src_guid," + "fld_bill_guid" + " FROM es_charge_incoming_data_bill where fld_area_guid=? limit 1";


    public static String ES_CHARGE_INCOMING_FEE = "SELECT " + "fld_guid," + "fld_area_guid," + "date_format(fld_operate_date,'%Y-%m-%d %H:%i:%S') fld_operate_date" + " FROM es_charge_incoming_fee where " + "fld_area_guid=? and fld_cancel != 1 and fld_create_user != 'importuser' and fld_create_user != 'outuser' limit 10";


    public static String ES_CHARGE__BILL = "SELECT " + "fld_guid," + "fld_status," + "fld_bill_code," + "fld_type_guid," + "date_format(fld_operate_date,'%Y-%m-%d %H:%i:%S') fld_operate_date" + " FROM es_charge_bill where fld_area_guid=? limit 1";

    public static String ES_CHARGE__BILL_TYPE = "SELECT " + "fld_guid," + "fld_category," + "fld_name" + " FROM es_charge_bill_type where fld_category=0";
}
