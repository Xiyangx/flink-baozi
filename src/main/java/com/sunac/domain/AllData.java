package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: coprivate String baozi.domain
 * @date:2022/8/30
 */
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllData extends CommonDomain {

    private String fld_create_user;    //char(36)	创建人
    private String fld_create_date;    //datetime	创建时间
    private String fld_modify_user;    //char(36)	最后修改人
    private String fld_modify_date;    //datetime	最后修改时间
    private String fld_tenancy;    //archar(100)	租户
    private String fld_incoming_fee_guid = "666";    //char(36)	实收费GUID（es_charge_incoming_fee 表主键id）
    private String fld_owner_fee_guid;//	char(36)	欠费GUID
    private String fld_area_guid;//	char(36)	围合GUID
    private String fld_area_name;// n	varchar(200)	项目名称
    private String fld_object_guid;//	char(36)	资源对象GUID
    private String fld_object_name;//	varchar(50)	资源名称
    private String fld_owner_guid;//	char(36)	收费对象GUID
    private String fld_owner_name;//	varchar(50)	客户名称
    private String fld_project_guid;//char(36)	科目GUID
    private String fld_project_name;//	varchar(50)	科目名称
    private String fld_pay_mode_guid;//	char(36)	付款方式ID
    private String fld_pay_mode_name;//	varchar(50)	付款方式名称
    private String fld_owner_date;//	date	应收日期
    private String fld_allot_date;//	date	分摊日期
    private String fld_finance_date;//	date	财务日期
    private String fld_point_date;//	date	时点时间
    private String fld_desc;//	varchar(500)	费用描述
    private BigDecimal fld_total;//	decimal(18,2)	主费用总实收

    private BigDecimal fld_amount;//	decimal(18,2)	实收金额（含税）
    private BigDecimal fld_late_fee;//	decimal(18,2)	违约金
    private BigDecimal fld_tax_amount;//	decimal(18,2)	税额
    private String fld_tax;//	decimal(18,2)	税率
    private String fld_bill_no;//	varchar(1000)	收据号
    private String fld_bill_type;//	char(36)	收据类型（变更源字段）
    private String fld_bill_no2;//	varchar(1000)	发票号(换票号)
    private String fld_operator_guid;//	char(36)	收费员GUID
    private String fld_operate_date;//	datetime	收费日期
    private String fld_back_fee_guid;//	char(36)	关联此表,非空表示退款(关联实收费科目明细GUID)
    private Integer fld_cancel;//	int	0正常 1返销 2锁定,99 关联后null值替换为99
    private String fld_start_date;//	date	开始日期
    private String fld_end_date;//	date	结束日期
    private String fld_back_guid;//	char(36)	退款单ID
    private Integer fld_busi_type;//	int	来源 -1导入 0前台 1自助 2退款 3银行托收 4划账 5批量打票 6预付款互转 7递延 8空置 9销免 10售免 11地产垫付 12普通返销 13调整返销 14退款转款 15退款扣款,99 关联后null值替换为99
    private String fld_remark;//	varchar(1000)	收费备注
    private String fld_cancel_date;//	datetime	返销日期
    private String fld_cancel_guid;//	char(36)	返销人
    private String fld_number;//	varchar(100)	支付订单号
    private int fld_checkin;//	int	0交款未到账  1已到账  -1待调整,99 关联后null值替换为99
    private String fld_cancel_me;//	char(36)	调整返销对象ID
    private String fld_dq;//	varchar(100)	大区
    private String fld_fee_type;//	varchar(50)	收费方式：酬金制、包干制
    private String fld_confirm_date;//	varchar(20)	确权日期
    private String fld_company;//	varchar(50)	城市公司（账套同步，报表专用）
    private String fld_xm;//	varchar(100)	项目
    private String fld_ywdy;//	varchar(100)	业务单元（公司）
    private Integer fld_is_owner;//	int	居住关系(0业主 1租户 2地产公司 -1其它,99 关联后null值替换为99)
    private Integer fld_status;//	int	状态（0停用 1可用 -1删除,99 关联后null值替换为99）
    private String fld_adjust_guid;//	char(36)	优惠欠费ID（地产垫付：保存复制后的）
    private Integer fld_settle_status;//	int	结算状态(0已作废 1已收费 2部分收费 3未收费 4已返销 5退款中 6已退款 7 剩余作废中,99 关联后null值替换为99)
    private String fld_accounts_detail_bill_no;//	varchar(1000)	地产结算发票号
    private Integer fld_attribute;//	int	属性(1空置 2销免 3售免 4地产垫付,99 关联后null值替换为99)
    private Integer fld_examine_status;//	int	审批状态(1未提交 2审批中 3已打回 4审批通过,99 关联后null值替换为99)
    private String fld_reason_remark;//	varchar(500)	欠费原因备注
    private String fld_price;//	varchar(2000)	计费时的单价
    private BigDecimal fld_rebate;//	decimal(18,2)	优惠总金额
    private String fld_owner_fee_resource;//	varchar(50)	欠费来源（公摊：公摊计费）
    private String fld_owner_fee_desc;//	varchar(500)	费用描述
    private String fld_resource;//	varchar(50)	来源
    private String fld_owner_fee_date;//	date	业主起费日期
    private String fld_building;//	varchar(100)	楼栋名称 es_info_object
    private String fld_cell;//	varchar(100)	单元名称 es_info_object
    private String fld_batch;//	varchar(100)	批次名称 es_info_object
    private BigDecimal fld_charged_area;//	decimal(18,2)	收费面积 es_info_object
    private Integer fld_obj_status;//	int	资源基本状态 (0未售空置 1已售 2已接房 3已入住 4已售空置,99 关联后null值替换为99)
    private String fld_room_type;//	varchar(10)	房间类型(1:房间 2:车位 3:公区)
    private String fld_object_class_name;//  fld_name as fld_object_class_name	varchar(50)	名称
    private String fld_cw_category;//	varchar(50)	车位类别
    private String fld_owner_desc;//	varchar(5000)	客户备注
    private Integer fld_bill_status;//	int	收据状态：0已开票 1已换票 2已作废 3已红冲 4预开待收费 5待开票 6中间状态 ,99 关联后null值替换为99
    private String fld_bill_type_name;//	varchar(50)	收据类型名称
    private String fld_object_type;//	int	计费类型(-1分类 0预付款 1三表类费用 2周期性费用 3一次性费用用,99 关联后null值替换为99)es_charge_project
    private String fld_fee_adjust_guid;//	string	欠费主费用id
    private String fld_yt;//	varchar(50)	围合主业态
    private String fld_bill_type2;//	char(36)	发票类型
    private String fld_bill_type_name2;//	varchar(50)	发票类型名称
    private String fld_bill_code;// 	varchar(1000)	发票代码
    private Integer fld_bill_status2;//	int	发票状态：0已开票 1已换票 2已作废 3已红冲 4预开待收费 5待开票 6中间状态 ,99 关联后null值替换为99
    private String fld_project_period_name;//	varchar(100)	科目归类名称
    private String fld_balance_guid;//	char(36)	对账表ID
    private String fld_hand_in_guid;//	char(36)	交款申请ID
    private Integer fld_hand_in;//	int	交款审批状态 1对账中 2提交失败 3待提交 4未审批 5打回 6审批通过 -1作废，关联后null值替换为99
    private String fld_hand_in_start_date;//	date	交款起始时间
    private String fld_hand_in_end_date;//	date	交款结束时间
    private String fld_incoming_back_guid;//	char(36)	退款凭证对应退款总表ID
    private String fld_data;//	datetime	退款日期
    private String firstTime;//	datetime	聚合每个围合对应收费时间的最小值
    private Integer fld_pre_pay;//	int	0付款方式 1预付款，关联后null值替换为99
    private Integer fld_service_set_type;// as fld_service_set_type	int	1:科目类型 2:业态类型，关联后null值替换为99
    private String two_fld_date;//	datetime	对账表es_charge_two_balance.fld_date，交易日期
    private String two_fld_create_user;//	char(36)	对账表es_charge_two_balance.fld_create_user，对账表创建人
    private Integer obj_fld_order;//	int	资源总表es_info_object.fld_order，排序，未关联取-1
    private String voucher_fld_pay_mode_guid;//	char(36)	凭证-核算科目配置-付款方式表es_charge_voucher_project_pay.fld_pay_mode_guid
    private String pay_fld_attribute;//	int	付款方式表es_charge_pay_mode.fld_attribute 属性（-1:无 0:直连、1:非直连、2:统一支付），关联后null值替换为99
    private String back_fld_incoming_back_guid;//	char(36)	退款总表es_charge_incoming_back.fld_incoming_back_guid，退款对应实收总表ID
    private String back_fld_incoming_convert_guid;//	char(36)	退款总表es_charge_incoming_back.fld_incoming_convert_guid转款对应实收总表ID
    private String back_fld_incoming_kou_guid;//	char(36)	退款总表es_charge_incoming_back.fld_incoming_kou_guid，扣款对应实收总表ID
    private String refund_fld_date_back;//	datetime	凭证-主表-退款凭证es_charge_voucher_mast_refunprivate String fld_date，退款日期
    private String refund_fld_date_convert;//	datetime	凭证-主表-退款凭证es_charge_voucher_mast_refunprivate String fld_date，转款日期
    private String refund_fld_date_kou;//	datetime	凭证-主表-退款凭证es_charge_voucher_mast_refunprivate String fld_date，扣款日期
    // 增加一个fld_bill_guid，用于es_charge_incoming_data_bill加到宽表然后关联es_charge_bill使用
    private String fld_bill_guid;
    // fld_type_guid，用于es_charge_bill加到宽表然后关联es_charge_bill_type使用
    private String fld_type_guid;
    // fld_main_guid:用于es_charge_settle_accounts_detail加到宽表然后关联es_charge_settle_accounts_main使用
    private String fld_main_guid;
    // fld_period_guid:用于es_charge_project_period_join.fld_period_guid加到宽表然后关联es_charge_project_period使用
    private String fld_period_guid;
    // fld_eci_back_guid,fld_eci_convert_guid,fld_eci_kou_guid 取自es_charge_incoming_back  用于关联es_charge_voucher_mast_refund
//    private String fld_eci_back_guid;
//    private String fld_eci_convert_guid;
//    private String fld_eci_kou_guid;
    //
    private String fld_class_guid;
    //
    private Integer t_fld_category;


    private String f_fld_guid;
    private String a_fld_guid;
    private String o_fld_guid;
    private String w_fld_guid;
    private String p_fld_guid;
    private String m_fld_guid;
    private String cof_fld_guid;
    private String op_fld_guid;
    private String oc_fld_guid;
    private String ao_fld_guid;
    private String db_fld_guid;
    private String t_fld_guid;
    private String b_fld_guid;
    private String ad_fld_guid;
    private String am_fld_guid;
    private String pp_fld_guid;
    private String hir_fld_guid;
    private String f1_fld_guid;
    private String ss_fld_guid;
    private String vpp_fld_guid;
//    private String ib_fld_guid;
    private String ib_back_fld_guid;
    private String ib_convert_fld_guid;
    private String ib_kou_fld_guid;
    private String rf_back_fld_guid;
    private String rf_convert_fld_guid;
    private String rf_kou_fld_guid;
}
