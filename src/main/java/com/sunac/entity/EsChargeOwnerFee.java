package com.sunac.entity;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsChargeOwnerFee extends CommonDomain {
    String fld_create_user;	//char(36)	创建人
    String  fld_create_date;	//datetime	创建时间
    String fld_modify_user;	 //char(36)	最后修改人
    String  fld_modify_date;	//datetime	最后修改时间
    String fld_tenancy;	//varchar(100)	租户
    String fld_area_guid;	//char(36)	项目ID
    String fld_area_name;	//varchar(100)	项目名称
    String fld_adjust_guid;	//char(36)	所属主费用ID
    String fld_object_guid;	//char(36)	资源ID
    String fld_object_name;	//varchar(50)	资源名称
    String fld_owner_guid;	//char(36)	客户ID
    String fld_owner_name;	//varchar(50)	客户名称
    String fld_project_guid;	//char(36)	科目ID
    String fld_project_name;	//varchar(50)	科目名称
    Integer fld_project_type;	//int	科目类型(0预付款 1三表类费用 2周期性费用 3一次性费用,99 关联后null值替换为99)
    BigDecimal fld_total;	//decimal(18,2)	主费总应收(折后饱和)
    BigDecimal fld_left_total;	//decimal(18,2)	主费剩余未收
    BigDecimal fld_amount;	//decimal(18,2)	应收金额
    BigDecimal fld_rebate;	//decimal(18,2)	优惠总金额
    BigDecimal fld_late_total;	//decimal(18,2)	违约金总应收
    BigDecimal fld_late_fee;	//decimal(18,2)	违约金剩余未收
    String fld_late_date;	//date	违约金最后结算日期
    Integer fld_late_stop;	//int	违约金锁定（1不再向后计算违约金）
    String fld_desc;	//varchar(500)	费用描述
    String fld_owner_date;	//date	应收日期 
    String fld_allot_date;	//date	分摊日期
    String fld_finance_date;	//date	财务日期
    String fld_point_date;	//date	时点时间
    String fld_start_date;	//date	起始时间
    String fld_end_date;	//date	结束时间
    BigDecimal fld_start_read;	//decimal(18,2)	三表起数（周期性费用时，保存：0按30天 1按自然月）
    BigDecimal fld_end_read;	//decimal(18,2)	三表止数
    BigDecimal fld_number;	//decimal(18,2)	周期性费用：面积/三表：计费数/一次性：1/基本单价：1
    Integer fld_income;	 //int	-1已取消 0未收 1已收 2挂账 3冻结,99 关联后null值替换为99
    Integer fld_income_source;	//int	0无 1预开票 2银行托收 3欠费调整 31修改客户 32核销 33挂账/取消挂账 34优惠 4地产结算 5单价变更 6事件返销 7批量收费 8批量划账 81新增合同 82合同终止,99 关联后null值替换为99
    String fld_reason_guid;	 //char(36)	欠费原因分类
    String fld_reason_name;	 //varchar(100)	欠费原因分类名称
    String fld_reason_remark;	 //varchar(500)	欠费原因备注
    String fld_resource;	//varchar(50)	欠费来源（公摊：公摊计费）
    String fld_price;	//varchar(200)	计费时的单价
    String fld_busi_guid;	//char(36)	业务来源ID（合同计划ID、三表抄表记录ID）
    // BigDecimal fld_taxes; //decimal(18,2)	税额（下面SQL计算公式返回）

}
