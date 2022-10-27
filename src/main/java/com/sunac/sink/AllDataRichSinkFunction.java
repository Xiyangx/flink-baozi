package com.sunac.sink;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.entity.AllData;
import com.sunac.utils.JdbcUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/24 6:31 下午
 * @Version 1.0
 */
public class AllDataRichSinkFunction extends RichSinkFunction<AllData> {
    private Connection conn;
    private PreparedStatement ps;
    @Override
    public void open(Configuration parameters) throws Exception {
        conn = JdbcUtils.getConnection();
    }

    @Override
    public void invoke(AllData value, Context context) throws Exception {
        if (Config.INSERT.equals(value.getOperation_type())){
            ps = conn.prepareStatement("insert into es_charge_owner_fee_all_data values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            insertSql(ps, value);
//            String sql = JdbcUtils.makeSQL(new Tuple2<String, CommonDomain>(Config.AllData, value));
//            System.out.println("sql = " + sql);
//            ps = conn.prepareStatement(sql);
//            ps.execute();
        }
        if (Config.UPDATE.equals(value.getOperation_type())){
            String sql = "UPDATE es_charge_owner_fee_all_data SET fld_create_user=?, fld_create_date=?, fld_modify_user=?, fld_modify_date=?, fld_tenancy=?, fld_area_guid=?, fld_area_name=?, fld_adjust_guid=?, fld_object_guid=?, fld_object_name=?, fld_owner_guid=?, fld_owner_name=?, fld_project_guid=?, fld_project_name=?, fld_project_type=?, fld_total=?, fld_left_total=?, fld_amount=?, fld_rebate=?, fld_late_total=?, fld_late_fee=?, fld_late_date=?, fld_late_stop=?, fld_desc=?, fld_owner_date=?, fld_allot_date=?, fld_finance_date=?, fld_point_date=?, fld_start_date=?, fld_end_date=?, fld_start_read=?, fld_end_read=?, fld_number=?, fld_income=?, fld_income_source=?, fld_reason_guid=?, fld_reason_name=?, fld_reason_remark=?, fld_resource=?, fld_price=?, fld_busi_guid=?, fld_dq=?, fld_ywdy=?, fld_xm=?, fld_company=?, fld_confirm_date=?, fld_fee_type=?, fld_is_owner=?, fld_owner_fee_date=?, fld_yt=?, fld_object_class_name=?, fld_project_period_name=?, fld_settle_status=?, fld_attribute=?, fld_settle_bill_no=?, fld_settle_adjust_guid=?, fld_owner_fee_guid=?, fld_main_guid=?, fld_examine_status=?, fld_batch=?, fld_building=?, fld_cell=?, fld_charged_area=?, fld_obj_status=?, fld_ticket_status=?, fld_co_bill_no=?, fld_object_type=?, fld_rate=?, fld_taxes=?, fld_start_fee_date=?, fld_phone_number=?, fld_owner_desc=?, bm_stop_date=?, bm_end_date=?, data_fld_create_date=?, data_fld_operate_date=?, data_fld_create_user=?, data_fld_busi_type=?, fee_fld_cancel_me=?, fld_examine_date=?, obj_fld_order=?, data_fld_total=?, data_fld_amount=?, data_fld_late_fee=?, data_fld_tax_amount=?, data_fld_tax=?, data_fld_cancel=?, data_fld_late_amount=?, a_fld_guid=?, o_fld_guid=?, cif_fld_guid=?, w_fld_guid=?, p_fld_guid=?, c_fld_guid=?, ecrr_fld_guid=?, pj_fld_guid=?, pp_fld_guid=?, ad_fld_guid=?, am_fld_guid=?, ct_fld_guid=?, co_fld_guid=?, da_fld_guid=?, oao_fld_guid=?, cbfo_fld_guid=?, cbm_fld_guid=?, ecrr_fld_general_tax=?, pj_fld_project_guid=?, pj_fld_period_guid=? WHERE fld_guid=?";
            ps = conn.prepareStatement(sql);
            updateSql(ps,value);
        }
        if (Config.DELETE.equals(value.getOperation_type())){
            ps = conn.prepareStatement("delete from es_charge_owner_fee_all_data where fld_guid = ?");
            deleteSql(ps, value);
        }

    }

    @Override
    public void close() throws Exception {
        ps.close();
        conn.close();
    }
    public static String getStringValue(String str){
        if (str == null){
            return "";
        }
        return str;
    }
    public static String getDateValue(String str){
        if (str == null){
            return "1970-01-01";
        }
        return str;
    }
    public static Integer getIntValue99(Integer integer){
        if (integer == null){
            return 99;
        }
        return integer;
    }
    public static BigDecimal getBigDecimalValue0(BigDecimal integer){
        if (integer == null){
            return new BigDecimal(0);
        }
        return integer;
    }
    public static Integer getIntValue1(Integer integer){
        if (integer == null){
            return -1;
        }
        return integer;
    }
    public static void insertSql(PreparedStatement ps, AllData value) throws SQLException {
        System.out.println("value = " + value);
        ps.setString(1,getStringValue(value.getFld_guid()));
        ps.setString(2,value.getFld_create_user());
        ps.setString(3,value.getFld_create_date());
        ps.setString(4,value.getFld_modify_user());
        ps.setString(5,value.getFld_modify_date());
        ps.setString(6,value.getFld_tenancy());
        ps.setString(7,value.getFld_area_guid());
        ps.setString(8,getStringValue(value.getFld_area_name()));
        ps.setString(9,value.getFld_adjust_guid());
        ps.setString(10,value.getFld_object_guid());
        ps.setString(11,getStringValue(value.getFld_object_name()));
        ps.setString(12,value.getFld_owner_guid());
        ps.setString(13,getStringValue(value.getFld_owner_name()));
        ps.setString(14,value.getFld_project_guid());
        ps.setString(15,getStringValue(value.getFld_project_name()));
        ps.setInt(16,getIntValue99(value.getFld_project_type()));
        ps.setBigDecimal(17,value.getFld_total());
        ps.setBigDecimal(18,value.getFld_left_total());
        ps.setBigDecimal(19,value.getFld_amount());
        ps.setBigDecimal(20,value.getFld_rebate());
        ps.setBigDecimal(21,value.getFld_late_total());
        ps.setBigDecimal(22,value.getFld_late_fee());
        ps.setString(23,value.getFld_late_date());
        ps.setInt(24,value.getFld_late_stop());
        ps.setString(25,value.getFld_desc());
        ps.setString(26,value.getFld_owner_date());
        ps.setString(27,value.getFld_allot_date());
        ps.setString(28,value.getFld_finance_date());
        ps.setString(29,value.getFld_point_date());
        ps.setString(30,value.getFld_start_date());
        ps.setString(31,value.getFld_end_date());
        ps.setBigDecimal(32,value.getFld_start_read());
        ps.setBigDecimal(33,value.getFld_end_read());
        ps.setBigDecimal(34,value.getFld_number());
        ps.setInt(35,value.getFld_income());
        ps.setInt(36,value.getFld_income_source());
        ps.setString(37,value.getFld_reason_guid());
        //算法处理
        if (Objects.equals(value.getFld_reason_guid(), "")){
            ps.setString(38,"");
        } else {
            ps.setString(38,getStringValue(value.getFld_name()));
        }
        ps.setString(39,value.getFld_reason_remark());
        ps.setString(40,value.getFld_resource());
        ps.setString(41,value.getFld_price());
        ps.setString(42,value.getFld_busi_guid());
        ps.setString(43,getStringValue(value.getFld_dq()));
        ps.setString(44,getStringValue(value.getFld_ywdy()));
        ps.setString(45,getStringValue(value.getFld_xm()));
        ps.setString(46,getStringValue(value.getFld_company()));
        ps.setString(47,getDateValue(value.getFld_confirm_date()));
        ps.setString(48,getStringValue(value.getFld_fee_type()));
        ps.setInt(49,getIntValue99(value.getFld_is_owner()));
        ps.setString(50,getDateValue(value.getFld_owner_fee_date()));
        ps.setString(51,getStringValue(value.getFld_yt()));
        ps.setString(52,getStringValue(value.getFld_object_class_name()));
        ps.setString(53,getStringValue(value.getFld_project_period_name()));
        ps.setInt(54,getIntValue99(value.getFld_settle_status()));
        ps.setInt(55,getIntValue99(value.getFld_attribute()));
        ps.setString(56,getStringValue(value.getFld_settle_bill_no()));
        ps.setString(57,getStringValue(value.getFld_settle_adjust_guid()));
        ps.setString(58,getStringValue(value.getFld_owner_fee_guid()));
        ps.setString(59,getStringValue(value.getFld_main_guid()));
        ps.setInt(60,getIntValue99(value.getFld_examine_status()));
        ps.setString(61,getStringValue(value.getFld_batch()));
        ps.setString(62,getStringValue(value.getFld_building()));
        ps.setString(63,getStringValue(value.getFld_cell()));
        ps.setBigDecimal(64,getBigDecimalValue0(value.getFld_charged_area()));
        ps.setInt(65,getIntValue99(value.getFld_obj_status()));
        ps.setInt(66,getIntValue99(value.getFld_ticket_status()));
        ps.setString(67,getStringValue(value.getFld_co_bill_no()));
        ps.setInt(68,getIntValue99(value.getFld_object_type()));
        ps.setBigDecimal(69,value.getFld_rate());
        //算法计算
        BigDecimal tmp = value.getEcrr_fld_general_tax();
        if(value.getEcrr_fld_general_tax()==null){
            tmp = BigDecimal.valueOf(-1);
        }
        if (tmp.equals(BigDecimal.valueOf(-1)) || tmp.equals(BigDecimal.valueOf(-2))){
            tmp = BigDecimal.valueOf(0);
        }
        BigDecimal add = tmp.add(BigDecimal.valueOf(1));
        BigDecimal divide = value.getFld_amount().divide(add,BigDecimal.ROUND_CEILING);
        BigDecimal multiply = divide.multiply(tmp);

        ps.setBigDecimal(70,getBigDecimalValue0(multiply));
        ps.setString(71,value.getFld_start_fee_date());
        ps.setString(72,getStringValue(value.getFld_phone_number()));
        ps.setString(73,getStringValue(value.getFld_owner_desc()));
        ps.setString(74,getDateValue(value.getBm_stop_date()));
        ps.setString(75,getDateValue(value.getBm_end_date()));
        ps.setString(76,getDateValue(value.getData_fld_create_date()));
        ps.setString(77,getDateValue(value.getData_fld_operate_date()));
        ps.setString(78,getStringValue(value.getData_fld_create_user()));
        ps.setInt(79,getIntValue99(value.getData_fld_busi_type()));
        ps.setString(80,getStringValue(value.getFee_fld_cancel_me()));
        ps.setString(81,getDateValue(value.getFld_examine_date()));
        ps.setInt(82,getIntValue1(value.getObj_fld_order()));
        ps.setBigDecimal(83,getBigDecimalValue0(value.getData_fld_total()));
        ps.setBigDecimal(84,getBigDecimalValue0(value.getData_fld_amount()));
        ps.setBigDecimal(85,getBigDecimalValue0(value.getData_fld_late_fee()));
        ps.setBigDecimal(86,getBigDecimalValue0(value.getData_fld_tax_amount()));
        ps.setBigDecimal(87,getBigDecimalValue0(value.getData_fld_tax()));
        ps.setInt(88,getIntValue99(value.getData_fld_cancel()));
        ps.setBigDecimal(89,getBigDecimalValue0(value.getData_fld_late_amount()));
        ps.setString(90,value.getA_fld_guid());
        ps.setString(91,value.getO_fld_guid());
        ps.setString(92,value.getCif_fld_guid());
        ps.setString(93,value.getW_fld_guid());
        ps.setString(94,value.getP_fld_guid());
        ps.setString(95,value.getC_fld_guid());
        ps.setString(96,value.getEcrr_fld_guid());
        ps.setString(97,value.getPj_fld_guid());
        ps.setString(98,value.getPp_fld_guid());
        ps.setString(99,value.getAd_fld_guid());
        ps.setString(100,value.getAm_fld_guid());
        ps.setString(101,value.getCt_fld_guid());
        ps.setString(102,value.getCo_fld_guid());
        ps.setString(103,value.getDa_fld_guid());
        ps.setString(104,value.getOao_fld_guid());
        ps.setString(105,value.getCbfo_fld_guid());
        ps.setString(106,value.getCbm_fld_guid());
        ps.setBigDecimal(107,value.getEcrr_fld_general_tax());
        ps.setString(108,value.getPj_fld_project_guid());
        ps.setString(109,value.getPj_fld_period_guid());
        ps.execute();
    }
    public static void updateSql(PreparedStatement ps, AllData value) throws SQLException {
        ps.setString(1,value.getFld_create_user());
        ps.setString(2,value.getFld_create_date());
        ps.setString(3,value.getFld_modify_user());
        ps.setString(4,value.getFld_modify_date());
        ps.setString(5,value.getFld_tenancy());
        ps.setString(6,value.getFld_area_guid());
        ps.setString(7,getStringValue(value.getFld_area_name()));
        ps.setString(8,value.getFld_adjust_guid());
        ps.setString(9,value.getFld_object_guid());
        ps.setString(10,getStringValue(value.getFld_object_name()));
        ps.setString(11,value.getFld_owner_guid());
        ps.setString(12,getStringValue(value.getFld_owner_name()));
        ps.setString(13,value.getFld_project_guid());
        ps.setString(14,getStringValue(value.getFld_project_name()));
        ps.setInt(15,getIntValue99(value.getFld_project_type()));
        ps.setBigDecimal(16,value.getFld_total());
        ps.setBigDecimal(17,value.getFld_left_total());
        ps.setBigDecimal(18,value.getFld_amount());
        ps.setBigDecimal(19,value.getFld_rebate());
        ps.setBigDecimal(20,value.getFld_late_total());
        ps.setBigDecimal(21,value.getFld_late_fee());
        ps.setString(22,value.getFld_late_date());
        ps.setInt(23,value.getFld_late_stop());
        ps.setString(24,value.getFld_desc());
        ps.setString(25,value.getFld_owner_date());
        ps.setString(26,value.getFld_allot_date());
        ps.setString(27,value.getFld_finance_date());
        ps.setString(28,value.getFld_point_date());
        ps.setString(29,value.getFld_start_date());
        ps.setString(30,value.getFld_end_date());
        ps.setBigDecimal(31,value.getFld_start_read());
        ps.setBigDecimal(32,value.getFld_end_read());
        ps.setBigDecimal(33,value.getFld_number());
        ps.setInt(34,value.getFld_income());
        ps.setInt(35,value.getFld_income_source());
        ps.setString(36,value.getFld_reason_guid());
        //算法处理
        if (Objects.equals(value.getFld_reason_guid(), "")){
            ps.setString(37,"");
        } else {
            ps.setString(37,getStringValue(value.getFld_name()));
        }
        ps.setString(38,value.getFld_reason_remark());
        ps.setString(39,value.getFld_resource());
        ps.setString(40,value.getFld_price());
        ps.setString(41,value.getFld_busi_guid());
        ps.setString(42,getStringValue(value.getFld_dq()));
        ps.setString(43,getStringValue(value.getFld_ywdy()));
        ps.setString(44,getStringValue(value.getFld_xm()));
        ps.setString(45,getStringValue(value.getFld_company()));
        ps.setString(46,getDateValue(value.getFld_confirm_date()));
        ps.setString(47,getStringValue(value.getFld_fee_type()));
        ps.setInt(48,getIntValue99(value.getFld_is_owner()));
        ps.setString(49,getDateValue(value.getFld_owner_fee_date()));
        ps.setString(50,getStringValue(value.getFld_yt()));
        ps.setString(51,getStringValue(value.getFld_object_class_name()));
        ps.setString(52,getStringValue(value.getFld_project_period_name()));
        ps.setInt(53,getIntValue99(value.getFld_settle_status()));
        ps.setInt(54,getIntValue99(value.getFld_attribute()));
        ps.setString(55,getStringValue(value.getFld_settle_bill_no()));
        ps.setString(56,getStringValue(value.getFld_settle_adjust_guid()));
        ps.setString(57,getStringValue(value.getFld_owner_fee_guid()));
        ps.setString(58,getStringValue(value.getFld_main_guid()));
        ps.setInt(59,getIntValue99(value.getFld_examine_status()));
        ps.setString(60,getStringValue(value.getFld_batch()));
        ps.setString(61,getStringValue(value.getFld_building()));
        ps.setString(62,getStringValue(value.getFld_cell()));
        ps.setBigDecimal(63,getBigDecimalValue0(value.getFld_charged_area()));
        ps.setInt(64,getIntValue99(value.getFld_obj_status()));
        ps.setInt(65,getIntValue99(value.getFld_ticket_status()));
        ps.setString(66,getStringValue(value.getFld_co_bill_no()));
        ps.setInt(67,getIntValue99(value.getFld_object_type()));
        ps.setBigDecimal(68,value.getFld_rate());
        //算法计算
        BigDecimal tmp = value.getEcrr_fld_general_tax();
        if(value.getEcrr_fld_general_tax()==null){
            tmp = BigDecimal.valueOf(-1);
        }
        if (tmp.equals(BigDecimal.valueOf(-1)) || tmp.equals(BigDecimal.valueOf(-2))){
            tmp = BigDecimal.valueOf(0);
        }
        BigDecimal add = tmp.add(BigDecimal.valueOf(1));
        BigDecimal divide = value.getFld_amount().divide(add,BigDecimal.ROUND_CEILING);
        BigDecimal multiply = divide.multiply(tmp);

        ps.setBigDecimal(69,getBigDecimalValue0(multiply));
        ps.setString(70,value.getFld_start_fee_date());
        ps.setString(71,value.getFld_phone_number());
        ps.setString(72,getStringValue(value.getFld_owner_desc()));
        ps.setString(73,getDateValue(value.getBm_stop_date()));
        ps.setString(74,getDateValue(value.getBm_end_date()));
        ps.setString(75,getDateValue(value.getData_fld_create_date()));
        ps.setString(76,getDateValue(value.getData_fld_operate_date()));
        ps.setString(77,getStringValue(value.getData_fld_create_user()));
        ps.setInt(78,getIntValue99(value.getData_fld_busi_type()));
        ps.setString(79,getStringValue(value.getFee_fld_cancel_me()));
        ps.setString(80,getDateValue(value.getFld_examine_date()));
        ps.setInt(81,getIntValue1(value.getObj_fld_order()));
        ps.setBigDecimal(82,getBigDecimalValue0(value.getData_fld_total()));
        ps.setBigDecimal(83,getBigDecimalValue0(value.getData_fld_amount()));
        ps.setBigDecimal(84,getBigDecimalValue0(value.getData_fld_late_fee()));
        ps.setBigDecimal(85,getBigDecimalValue0(value.getData_fld_tax_amount()));
        ps.setBigDecimal(86,getBigDecimalValue0(value.getData_fld_tax()));
        ps.setInt(87,getIntValue99(value.getData_fld_cancel()));
        ps.setBigDecimal(88,getBigDecimalValue0(value.getData_fld_late_amount()));
        ps.setString(89,value.getA_fld_guid());
        ps.setString(90,value.getO_fld_guid());
        ps.setString(91,value.getCif_fld_guid());
        ps.setString(92,value.getW_fld_guid());
        ps.setString(93,value.getP_fld_guid());
        ps.setString(94,value.getC_fld_guid());
        ps.setString(95,value.getEcrr_fld_guid());
        ps.setString(96,value.getPj_fld_guid());
        ps.setString(97,value.getPp_fld_guid());
        ps.setString(98,value.getAd_fld_guid());
        ps.setString(99,value.getAm_fld_guid());
        ps.setString(100,value.getCt_fld_guid());
        ps.setString(101,value.getCo_fld_guid());
        ps.setString(102,value.getDa_fld_guid());
        ps.setString(103,value.getOao_fld_guid());
        ps.setString(104,value.getCbfo_fld_guid());
        ps.setString(105,value.getCbm_fld_guid());
        ps.setBigDecimal(106,value.getEcrr_fld_general_tax());
        ps.setString(107,value.getPj_fld_project_guid());
        ps.setString(108,value.getPj_fld_period_guid());
        ps.setString(109,getStringValue(value.getFld_guid()));

        ps.execute();
    }
    public static void deleteSql(PreparedStatement ps, AllData value) throws SQLException {
        ps.setString(1,getStringValue(value.getFld_guid()));
        ps.execute();
    }
}
