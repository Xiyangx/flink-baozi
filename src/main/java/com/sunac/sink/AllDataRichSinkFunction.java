package com.sunac.sink;

import com.sunac.Config;
import com.sunac.ow.owdomain.AllData;
import com.sunac.utils.HikariUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final Logger log = LoggerFactory.getLogger(AllDataRichSinkFunction.class);

    private Connection conn;
    private PreparedStatement ps;

    @Override
    public void open(Configuration parameters) throws Exception {
        conn = HikariUtil.getInstance().getConnection();
    }

    @Override
    public void invoke(AllData value, Context context) throws Exception {
        if (null == conn || (null != conn && conn.isClosed())) {
            conn = HikariUtil.getInstance().getConnection();
        }
        if (Config.INSERT.equals(value.getOperation_type())) {
            insertSql(conn, value);
        }
        if (Config.UPDATE.equals(value.getOperation_type())) {
            updateSql(conn, value);
        }
        if (Config.DELETE.equals(value.getOperation_type())) {
            deleteSql(conn, value);
        }
    }

    @Override
    public void close() throws Exception {
        if (null != conn) {
            conn.close();
        }
        if (null != ps) {
            ps.close();
        }
    }

    public static String getStringValue(String str) {
        if (null == str) {
            return "";
        }
        return str;
    }

    public static String getDateValue(String str) {
        if (null == str) {
            return "1970-01-01";
        }
        return str;
    }

    public static Integer getIntValue99(Integer integer) {
        if (null == integer) {
            return 99;
        }
        return integer;
    }

    public static BigDecimal getBigDecimalValue0(BigDecimal integer) {
        if (null == integer) {
            return new BigDecimal(0);
        }
        return integer;
    }

    public static Integer getIntValue1(Integer integer) {
        if (null == integer) {
            return -1;
        }
        return integer;
    }

    public static void insertSql(Connection conn, AllData value) {
        System.out.println("--------------------插入es_charge_owner_fee_all_data一条：fld_guid=" + getStringValue(value.getFld_guid()) + "--------------------");
        try {
            PreparedStatement ps = conn.prepareStatement(Config.INSERTSQL);
            ps.setString(1, getStringValue(value.getFld_guid()));
            ps.setString(2, value.getFld_create_user());
            ps.setString(3, value.getFld_create_date());
            ps.setString(4, value.getFld_modify_user());
            ps.setString(5, value.getFld_modify_date());
            ps.setString(6, value.getFld_tenancy());
            ps.setString(7, value.getFld_area_guid());
            ps.setString(8, getStringValue(value.getFld_area_name()));
            ps.setString(9, value.getFld_adjust_guid());
            ps.setString(10, value.getFld_object_guid());
            ps.setString(11, getStringValue(value.getFld_object_name()));
            ps.setString(12, value.getFld_owner_guid());
            ps.setString(13, getStringValue(value.getFld_owner_name()));
            ps.setString(14, value.getFld_project_guid());
            ps.setString(15, getStringValue(value.getFld_project_name()));
            ps.setInt(16, getIntValue99(value.getFld_project_type()));
            ps.setBigDecimal(17, value.getFld_total());
            ps.setBigDecimal(18, value.getFld_left_total());
            ps.setBigDecimal(19, value.getFld_amount());
            ps.setBigDecimal(20, value.getFld_rebate());
            ps.setBigDecimal(21, value.getFld_late_total());
            ps.setBigDecimal(22, value.getFld_late_fee());
            ps.setString(23, value.getFld_late_date());
            ps.setInt(24, value.getFld_late_stop());
            ps.setString(25, value.getFld_desc());
            ps.setString(26, value.getFld_owner_date());
            ps.setString(27, value.getFld_allot_date());
            ps.setString(28, value.getFld_finance_date());
            ps.setString(29, value.getFld_point_date());
            ps.setString(30, value.getFld_start_date());
            ps.setString(31, value.getFld_end_date());
            ps.setBigDecimal(32, value.getFld_start_read());
            ps.setBigDecimal(33, value.getFld_end_read());
            ps.setBigDecimal(34, value.getFld_number());
            ps.setInt(35, value.getFld_income());
            ps.setInt(36, value.getFld_income_source());
            ps.setString(37, value.getFld_reason_guid());
            //算法处理
            if (Objects.equals(value.getFld_reason_guid(), Config.SINGLE_PARTITION)) {
                ps.setString(38, "");
            } else {
                ps.setString(38, getStringValue(value.getFld_name()));
            }
            ps.setString(39, value.getFld_reason_remark());
            ps.setString(40, value.getFld_resource());
            ps.setString(41, value.getFld_price());
            ps.setString(42, value.getFld_busi_guid());
            ps.setString(43, getStringValue(value.getFld_dq()));
            ps.setString(44, getStringValue(value.getFld_ywdy()));
            ps.setString(45, getStringValue(value.getFld_xm()));
            ps.setString(46, getStringValue(value.getFld_company()));
            ps.setString(47, getDateValue(value.getFld_confirm_date()));
            ps.setString(48, getStringValue(value.getFld_fee_type()));
            ps.setInt(49, getIntValue99(value.getFld_is_owner()));
            ps.setString(50, getDateValue(value.getFld_owner_fee_date()));
            ps.setString(51, getStringValue(value.getFld_yt()));
            ps.setString(52, getStringValue(value.getFld_object_class_name()));
            ps.setString(53, getStringValue(value.getFld_project_period_name()));
            ps.setInt(54, getIntValue99(value.getFld_settle_status()));
            ps.setInt(55, getIntValue99(value.getFld_attribute()));
            ps.setString(56, getStringValue(value.getFld_settle_bill_no()));
            ps.setString(57, getStringValue(value.getFld_settle_adjust_guid()));
            ps.setString(58, getStringValue(value.getFld_owner_fee_guid()));
            ps.setString(59, getStringValue(value.getFld_main_guid()));
            ps.setInt(60, getIntValue99(value.getFld_examine_status()));
            ps.setString(61, getStringValue(value.getFld_batch()));
            ps.setString(62, getStringValue(value.getFld_building()));
            ps.setString(63, getStringValue(value.getFld_cell()));
            ps.setBigDecimal(64, getBigDecimalValue0(value.getFld_charged_area()));
            ps.setInt(65, getIntValue99(value.getFld_obj_status()));
            ps.setInt(66, getIntValue99(value.getFld_ticket_status()));
            ps.setString(67, getStringValue(value.getFld_co_bill_no()));
            ps.setInt(68, getIntValue99(value.getFld_object_type()));
            ps.setBigDecimal(69, value.getFld_rate());
            //算法计算
//            BigDecimal tmp = value.getEcrr_fld_general_tax();
//            if (value.getEcrr_fld_general_tax() == null) {
//                tmp = BigDecimal.valueOf(-1);
//            }
//            if (tmp.compareTo(new BigDecimal("-1.00"))==0){
//                tmp = new BigDecimal("-1");
//            }
//            if (tmp.equals(BigDecimal.valueOf(-1)) || tmp.equals(BigDecimal.valueOf(-2))) {
//                tmp = BigDecimal.valueOf(0);
//            }
//            BigDecimal add = tmp.add(BigDecimal.valueOf(1));
//
//            BigDecimal divide = value.getFld_amount().divide(add, RoundingMode.CEILING);
//            BigDecimal multiply = divide.multiply(tmp);

            ps.setBigDecimal(70, getBigDecimalValue0(value.getEcrr_fld_general_tax()));
            ps.setString(71, value.getFld_start_fee_date());
            ps.setString(72, getStringValue(value.getFld_phone_number()));
            ps.setString(73, getStringValue(value.getFld_owner_desc()));
            ps.setString(74, getDateValue(value.getBm_stop_date()));
            ps.setString(75, getDateValue(value.getBm_end_date()));
            ps.setString(76, getDateValue(value.getData_fld_create_date()));
            ps.setString(77, getDateValue(value.getData_fld_operate_date()));
            ps.setString(78, getStringValue(value.getData_fld_create_user()));
            ps.setInt(79, getIntValue99(value.getData_fld_busi_type()));
            ps.setString(80, getStringValue(value.getFee_fld_cancel_me()));
            ps.setString(81, getDateValue(value.getFld_examine_date()));
            ps.setInt(82, getIntValue1(value.getObj_fld_order()));
            ps.setBigDecimal(83, getBigDecimalValue0(value.getData_fld_total()));
            ps.setBigDecimal(84, getBigDecimalValue0(value.getData_fld_amount()));
            ps.setBigDecimal(85, getBigDecimalValue0(value.getData_fld_late_fee()));
            ps.setBigDecimal(86, getBigDecimalValue0(value.getData_fld_tax_amount()));
            ps.setBigDecimal(87, getBigDecimalValue0(value.getData_fld_tax()));
            ps.setInt(88, getIntValue99(value.getData_fld_cancel()));
            ps.setBigDecimal(89, getBigDecimalValue0(value.getData_fld_late_amount()));
            ps.setString(90, value.getA_fld_guid());
            ps.setString(91, value.getO_fld_guid());
            ps.setString(92, value.getCif_fld_guid());
            ps.setString(93, value.getW_fld_guid());
            ps.setString(94, value.getP_fld_guid());
            ps.setString(95, value.getC_fld_guid());
            ps.setString(96, value.getEcrr_fld_guid());
            ps.setString(97, value.getPj_fld_guid());
            ps.setString(98, value.getPp_fld_guid());
            ps.setString(99, value.getAd_fld_guid());
            ps.setString(100, value.getAm_fld_guid());
            ps.setString(101, value.getCt_fld_guid());
            ps.setString(102, value.getCo_fld_guid());
            ps.setString(103, value.getDa_fld_guid());
            ps.setString(104, value.getOao_fld_guid());
            ps.setString(105, value.getCbfo_fld_guid());
            ps.setString(106, value.getCbm_fld_guid());
            ps.setBigDecimal(107, value.getEcrr_fld_general_tax());
            ps.setString(108, value.getPj_fld_project_guid());
            ps.setString(109, value.getPj_fld_period_guid());
            ps.setString(110, null);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void updateSql(Connection conn, AllData value) {
        System.out.println("--------------------更改了es_charge_owner_fee_all_data一条：fld_guid=" + getStringValue(value.getFld_guid()) + "--------------------");
        try {
            PreparedStatement ps = conn.prepareStatement(Config.UPDATESQL);
            ps.setString(1, value.getFld_create_user());
            ps.setString(2, value.getFld_create_date());
            ps.setString(3, value.getFld_modify_user());
            ps.setString(4, value.getFld_modify_date());
            ps.setString(5, value.getFld_tenancy());
            ps.setString(6, value.getFld_area_guid());
            ps.setString(7, getStringValue(value.getFld_area_name()));
            ps.setString(8, value.getFld_adjust_guid());
            ps.setString(9, value.getFld_object_guid());
            ps.setString(10, getStringValue(value.getFld_object_name()));
            ps.setString(11, value.getFld_owner_guid());
            ps.setString(12, getStringValue(value.getFld_owner_name()));
            ps.setString(13, value.getFld_project_guid());
            ps.setString(14, getStringValue(value.getFld_project_name()));
            ps.setInt(15, getIntValue99(value.getFld_project_type()));
            ps.setBigDecimal(16, value.getFld_total());
            ps.setBigDecimal(17, value.getFld_left_total());
            ps.setBigDecimal(18, value.getFld_amount());
            ps.setBigDecimal(19, value.getFld_rebate());
            ps.setBigDecimal(20, value.getFld_late_total());
            ps.setBigDecimal(21, value.getFld_late_fee());
            ps.setString(22, value.getFld_late_date());
            ps.setInt(23, value.getFld_late_stop());
            ps.setString(24, value.getFld_desc());
            ps.setString(25, value.getFld_owner_date());
            ps.setString(26, value.getFld_allot_date());
            ps.setString(27, value.getFld_finance_date());
            ps.setString(28, value.getFld_point_date());
            ps.setString(29, value.getFld_start_date());
            ps.setString(30, value.getFld_end_date());
            ps.setBigDecimal(31, value.getFld_start_read());
            ps.setBigDecimal(32, value.getFld_end_read());
            ps.setBigDecimal(33, value.getFld_number());
            ps.setInt(34, value.getFld_income());
            ps.setInt(35, value.getFld_income_source());
            ps.setString(36, value.getFld_reason_guid());
            //算法处理
            if (Objects.equals(value.getFld_reason_guid(), "")) {
                ps.setString(37, "");
            } else {
                ps.setString(37, getStringValue(value.getFld_name()));
            }
            ps.setString(38, value.getFld_reason_remark());
            ps.setString(39, value.getFld_resource());
            ps.setString(40, value.getFld_price());
            ps.setString(41, value.getFld_busi_guid());
            ps.setString(42, getStringValue(value.getFld_dq()));
            ps.setString(43, getStringValue(value.getFld_ywdy()));
            ps.setString(44, getStringValue(value.getFld_xm()));
            ps.setString(45, getStringValue(value.getFld_company()));
            ps.setString(46, getDateValue(value.getFld_confirm_date()));
            ps.setString(47, getStringValue(value.getFld_fee_type()));
            ps.setInt(48, getIntValue99(value.getFld_is_owner()));
            ps.setString(49, getDateValue(value.getFld_owner_fee_date()));
            ps.setString(50, getStringValue(value.getFld_yt()));
            ps.setString(51, getStringValue(value.getFld_object_class_name()));
            ps.setString(52, getStringValue(value.getFld_project_period_name()));
            ps.setInt(53, getIntValue99(value.getFld_settle_status()));
            ps.setInt(54, getIntValue99(value.getFld_attribute()));
            ps.setString(55, getStringValue(value.getFld_settle_bill_no()));
            ps.setString(56, getStringValue(value.getFld_settle_adjust_guid()));
            ps.setString(57, getStringValue(value.getFld_owner_fee_guid()));
            ps.setString(58, getStringValue(value.getFld_main_guid()));
            ps.setInt(59, getIntValue99(value.getFld_examine_status()));
            ps.setString(60, getStringValue(value.getFld_batch()));
            ps.setString(61, getStringValue(value.getFld_building()));
            ps.setString(62, getStringValue(value.getFld_cell()));
            ps.setBigDecimal(63, getBigDecimalValue0(value.getFld_charged_area()));
            ps.setInt(64, getIntValue99(value.getFld_obj_status()));
            ps.setInt(65, getIntValue99(value.getFld_ticket_status()));
            ps.setString(66, getStringValue(value.getFld_co_bill_no()));
            ps.setInt(67, getIntValue99(value.getFld_object_type()));
            ps.setBigDecimal(68, value.getFld_rate());
//            //算法计算
//            BigDecimal tmp = value.getEcrr_fld_general_tax();
//
//            if (value.getEcrr_fld_general_tax() == null) {
//                tmp = BigDecimal.valueOf(-1);
//            }
//            if (tmp.compareTo(new BigDecimal("-1.00"))==0){
//                tmp = new BigDecimal("-1");
//            }
//            if (tmp.equals(BigDecimal.valueOf(-1)) || tmp.equals(BigDecimal.valueOf(-2))) {
//                tmp = BigDecimal.valueOf(0);
//            }
//
//            BigDecimal add = tmp.add(BigDecimal.valueOf(1));
//            BigDecimal divide = value.getFld_amount().divide(add, RoundingMode.CEILING);
//            BigDecimal multiply = divide.multiply(tmp);

            ps.setBigDecimal(69, getBigDecimalValue0(value.getEcrr_fld_general_tax()));
            ps.setString(70, value.getFld_start_fee_date());
            ps.setString(71, value.getFld_phone_number());
            ps.setString(72, getStringValue(value.getFld_owner_desc()));
            ps.setString(73, getDateValue(value.getBm_stop_date()));
            ps.setString(74, getDateValue(value.getBm_end_date()));
            ps.setString(75, getDateValue(value.getData_fld_create_date()));
            ps.setString(76, getDateValue(value.getData_fld_operate_date()));
            ps.setString(77, getStringValue(value.getData_fld_create_user()));
            ps.setInt(78, getIntValue99(value.getData_fld_busi_type()));
            ps.setString(79, getStringValue(value.getFee_fld_cancel_me()));
            ps.setString(80, getDateValue(value.getFld_examine_date()));
            ps.setInt(81, getIntValue1(value.getObj_fld_order()));
            ps.setBigDecimal(82, getBigDecimalValue0(value.getData_fld_total()));
            ps.setBigDecimal(83, getBigDecimalValue0(value.getData_fld_amount()));
            ps.setBigDecimal(84, getBigDecimalValue0(value.getData_fld_late_fee()));
            ps.setBigDecimal(85, getBigDecimalValue0(value.getData_fld_tax_amount()));
            ps.setBigDecimal(86, getBigDecimalValue0(value.getData_fld_tax()));
            ps.setInt(87, getIntValue99(value.getData_fld_cancel()));
            ps.setBigDecimal(88, getBigDecimalValue0(value.getData_fld_late_amount()));
            ps.setString(89, value.getA_fld_guid());
            ps.setString(90, value.getO_fld_guid());
            ps.setString(91, value.getCif_fld_guid());
            ps.setString(92, value.getW_fld_guid());
            ps.setString(93, value.getP_fld_guid());
            ps.setString(94, value.getC_fld_guid());
            ps.setString(95, value.getEcrr_fld_guid());
            ps.setString(96, value.getPj_fld_guid());
            ps.setString(97, value.getPp_fld_guid());
            ps.setString(98, value.getAd_fld_guid());
            ps.setString(99, value.getAm_fld_guid());
            ps.setString(100, value.getCt_fld_guid());
            ps.setString(101, value.getCo_fld_guid());
            ps.setString(102, value.getDa_fld_guid());
            ps.setString(103, value.getOao_fld_guid());
            ps.setString(104, value.getCbfo_fld_guid());
            ps.setString(105, value.getCbm_fld_guid());
            ps.setBigDecimal(106, value.getEcrr_fld_general_tax());
            ps.setString(107, value.getPj_fld_project_guid());
            ps.setString(108, value.getPj_fld_period_guid());
            ps.setString(109, getStringValue(value.getFld_guid()));

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void deleteSql(Connection conn, AllData value){
        System.out.println("--------------------删除了es_charge_owner_fee_all_data一条：fld_guid=" + getStringValue(value.getFld_guid()) + "--------------------");
        try {
            PreparedStatement ps = conn.prepareStatement(Config.DELETESQL);
            ps.setString(1, getStringValue(value.getFld_guid()));

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = HikariUtil.getInstance().getConnection();
        AllData allData = new AllData();
        allData.setFld_guid("1111");
        allData.setFld_late_stop(1);
        allData.setFld_income(1);
        allData.setFld_income_source(1);
        allData.setFld_is_owner(1);
        allData.setFld_settle_status(1);
        allData.setFld_attribute(1);
        allData.setEcrr_fld_general_tax(null);
        allData.setFld_amount(new BigDecimal(2.0));
        insertSql(connection,allData);
    }
}
