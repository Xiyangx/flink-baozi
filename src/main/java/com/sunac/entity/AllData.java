package com.sunac.entity;

import com.sunac.domain.ColumAttribute;
import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/6 9:16 上午
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AllData extends CommonDomain {

    private String fld_create_user;	//char(36)	创建人
    private String  fld_create_date;	//datetime	创建时间
    private String fld_modify_user;	 //char(36)	最后修改人
    private String  fld_modify_date;	//datetime	最后修改时间
    private String fld_tenancy;	//varchar(100)	租户
    private String fld_area_guid;	//char(36)	项目ID
    private String fld_area_name;	//varchar(100)	项目名称
    private String fld_adjust_guid;	//char(36)	所属主费用ID
    private String fld_object_guid;	//char(36)	资源ID
    private String fld_object_name;	//varchar(50)	资源名称
    private String fld_owner_guid;	//char(36)	客户ID
    private String fld_owner_name;	//varchar(50)	客户名称
    private String fld_project_guid;	//char(36)	科目ID
    private String fld_project_name;	//varchar(50)	科目名称
    private Integer fld_project_type;	//int	科目类型(0预付款 1三表类费用 2周期性费用 3一次性费用,99 关联后null值替换为99)
    private BigDecimal fld_total;	//decimal(18,2)	主费总应收(折后饱和)
    private BigDecimal fld_left_total;	//decimal(18,2)	主费剩余未收
    private BigDecimal fld_amount;	//decimal(18,2)	应收金额
    private BigDecimal fld_rebate;	//decimal(18,2)	优惠总金额
    private BigDecimal fld_late_total;	//decimal(18,2)	违约金总应收
    private BigDecimal fld_late_fee;	//decimal(18,2)	违约金剩余未收
    private String fld_late_date;	//date	违约金最后结算日期
    private Integer fld_late_stop;	//int	违约金锁定（1不再向后计算违约金）
    private String fld_desc;	//varchar(500)	费用描述
    private String fld_owner_date;	//date	应收日期 
    private String fld_allot_date;	//date	分摊日期
    private String fld_finance_date;	//date	财务日期
    private String fld_point_date;	//date	时点时间
    private String fld_start_date;	//date	起始时间
    private String fld_end_date;	//date	结束时间
    private BigDecimal fld_start_read;	//decimal(18,2)	三表起数（周期性费用时，保存：0按30天 1按自然月）
    private BigDecimal fld_end_read;	//decimal(18,2)	三表止数
    private BigDecimal fld_number;	//decimal(18,2)	周期性费用：面积/三表：计费数/一次性：1/基本单价：1
    private Integer fld_income;	 //int	-1已取消 0未收 1已收 2挂账 3冻结,99 关联后null值替换为99
    private Integer fld_income_source;	//int	0无 1预开票 2银行托收 3欠费调整 31修改客户 32核销 33挂账/取消挂账 34优惠 4地产结算 5单价变更 6事件返销 7批量收费 8批量划账 81新增合同 82合同终止,99 关联后null值替换为99
    private String fld_reason_guid;	 //char(36)	欠费原因分类
    private String fld_reason_name;	 //varchar(100)	欠费原因分类名称
    private String fld_reason_remark;	 //varchar(500)	欠费原因备注
    private String fld_resource;	//varchar(50)	欠费来源（公摊：公摊计费）
    private String fld_price;	//varchar(200)	计费时的单价
    private String fld_busi_guid;	//char(36)	业务来源ID（合同计划ID、三表抄表记录ID）
    private String fld_dq;	//	varchar(100)	大区
    private String fld_ywdy;	//	varchar(100)	业务单元（公司）
    private String fld_xm;	//	varchar(100)	项目
    private String fld_company;	//	varchar(50)	城市公司（账套同步，报表专用）
    private String fld_confirm_date;	//	varchar(20)	确权日期
    private String fld_fee_type;	//	varchar(50)	收费方式：酬金制、包干制
    private Integer fld_is_owner;	//	int	居住关系(0业主 1租户 2地产公司 -1其它,99 关联后null值替换为99)
    private String fld_owner_fee_date;	//	date	业主起费日期
    private String fld_yt;	//	varchar(50)	围合主业态
    private String fld_object_class_name;	// as fld_object_class_name	varchar(50)	资源业态名称  es_info_object_class
    private String fld_project_period_name;	// as fld_project_period_name	varchar(100)	科目归类名称
    private Integer fld_settle_status;	//	int	结算单状态(0已作废 1已收费 2部分收费 3未收费 4 返销中 5 退款中 6 剩余作废中,99 关联后null值替换为99)
    private Integer fld_attribute;	//	int	属性(1空置 2销免 3售免 4地产垫付,99 关联后null值替换为99)
    private String fld_settle_bill_no;	// as fld_settle_bill_no	varchar(1000)	地产结算发票号
    private String fld_settle_adjust_guid;	// as fld_settle_adjust_guid	char(36)	优惠欠费ID（地产垫付：保存复制后的）
    private String fld_owner_fee_guid;	//	char(36)	主欠费ID（地产垫付：保存原欠费ID）
    private String fld_main_guid;	// as fld_main_guid	char(36)	地产结算主表ID
    private Integer fld_examine_status;	//	int	审批状态(1未提交 2审批中 3已打回 4审批通过,99 关联后null值替换为99)
    private String fld_batch;	//	varchar(100)	批次名称 es_info_object
    private String fld_building;	//	varchar(100)	楼栋名称 es_info_object
    private String fld_cell;	//	varchar(100)	单元名称 es_info_object
    private BigDecimal fld_charged_area;	//	decimal(18,2)	收费面积 es_info_object
    private Integer fld_obj_status;	// as fld_obj_status	int	资源基本状态 (0未售空置 1已售 2已接房 3已入住 4已售空置,99 关联后null值替换为99)es_info_object.fld_status
    private Integer fld_ticket_status;	//	int 	预开票状态(-1:已返销 1:待开票[未开票] 2:待收费[已开票] 3:已作废 4:已收费 5:实收返销,6:待开票[已收费],99 关联后null值替换为99)	es_charge_ticket_pay_operate
    private String fld_co_bill_no;	// as fld_co_bill_no	varchar(50)	预开票票据号  es_charge_ticket_pay_operate.fld_bill_no
    private Integer fld_object_type;	//	int	计费类型(-1分类 0预付款 1三表类费用 2周期性费用 3一次性费用用,99 关联后null值替换为99)es_charge_project
    private BigDecimal fld_rate;	// as fld_rate	decimal(18,2)	税率
    private BigDecimal fld_taxes;	//	decimal(18,2)	税额（下面SQL计算公式返回）
    private String fld_start_fee_date;	//	string	起费日期
    private String fld_phone_number;	//	string	手机号码
    private String fld_owner_desc;	// as fld_owner_desc	string	客户描述
    private String bm_stop_date;	//	date	合同主表-终止日期es_commerce_bond_main.fld_stop-date
    private String bm_end_date;	//	date	合同主表-合同生效止日期es_commerce_bond_main.fld_end-date
    private String data_fld_create_date;	//	datetime //实收明细表es_charge_incoming_data.fld_create_date
    private String data_fld_operate_date;	//	datetime //实收明细表es_charge_incoming_data.fld_operate_date
    private String data_fld_create_user;	//	char(36)	实收明细表es_charge_incoming_data.fld_create_user
    private Integer data_fld_busi_type;	//	int	实收明细表es_charge_incoming_data.fld_busi_type
    private String fee_fld_cancel_me;	//	char(36)	实收总表es_charge_incoming_fee.fld_cancel_me
    private String fld_examine_date;	//	date	地产结算主表es_charge_settle_accounts_main.fld_examine_date
    private Integer obj_fld_order;	//	int	资源总表es_info_object.fld_order，排序，未关联取-1
    private BigDecimal data_fld_total;	//	decimal(18,2)	实收表es_charge_incoming_data.fld_total，主费用总实收
    private BigDecimal data_fld_amount;	//	decimal(18,2)	实收表es_charge_incoming_data.fld_amount，实收金额（含税）
    private BigDecimal data_fld_late_fee;	//	decimal(18,2)	实收表es_charge_incoming_data.fld_late_fee，违约金
    private BigDecimal data_fld_tax_amount;	//	decimal(18,2)	实收表es_charge_incoming_data.fld_tax_amount，税额
    private BigDecimal data_fld_tax;	//	decimal(18,2)	实收表es_charge_incoming_data.fld_tax，税率
    private Integer data_fld_cancel;	//	int	实收表es_charge_incoming_data.fld_cancel，0正常 1返销 2锁定   未匹配取值99
    private BigDecimal data_fld_late_amount;	//	decimal(18,2)	实收表es_charge_incoming_data.fld_late_amount，违约金时点金额

    //关联条件
    private String a_fld_guid; //es_info_area_info a on a.fld_guid=f.fld_area_guid
    private String o_fld_guid; //es_info_object o on o.fld_guid=f.fld_object_guid
    private String o_fld_class_guid;
    private String cif_fld_guid;  //es_charge_incoming_fee cif on cif.fld_guid=da.fld_incoming_fee_guid
    private String w_fld_guid; //es_info_owner w on w.fld_guid=f.fld_owner_guid
    private String p_fld_guid; //es_charge_project p on p.fld_guid=f.fld_project_guid
    private String c_fld_guid; //es_info_object_class c on o.fld_class_guid=c.fld_guid

    //ecrr
    private String ecrr_fld_guid;
    private BigDecimal ecrr_fld_general_tax;

    //pj
    private String pj_fld_guid;
    private String pj_fld_project_guid;
    private String pj_fld_period_guid;

    //pp
    private String pp_fld_guid;

    //ad
    private String ad_fld_guid;
    private Integer ad_fld_status;
    private String ad_fld_area_guid;
    private String ad_fld_main_guid;
    private String ad_fld_create_date;

    //am
    private String am_fld_guid;
    private String am_fld_area_guid;

    //ct
    private String ct_fld_guid;
    private String ct_fld_owner_fee_guid;
    private String ct_fld_operate_guid;

    //co
    private String co_fld_guid;

    //da
    private String da_fld_guid;
    private String da_fld_owner_fee_guid;
    private String da_fld_area_guid;
    private String da_fld_incoming_fee_guid;

    //oao
    private String oao_fld_guid;
    private String oao_fld_object_guid;
    private String oao_fld_owner_guid;
    private String oao_fld_area_guid;
    private Integer fld_is_current;
    private Integer fld_is_charge;
    private Integer fld_status;

    //cbfo
    private String cbfo_fld_guid;
    private String cbfo_fld_bond_guid;
    private String cbfo_fld_object_guid;

    //cbm
    private String cbm_fld_guid;
    private String cbm_fld_area_guid;
    private String cbm_fld_owner_guid;

    private Integer add_status; //1:增加的主流 空:不是增加的主流

    private String fld_name;

}
