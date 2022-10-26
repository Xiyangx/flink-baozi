package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_settle_accounts_main地产结算主表
public class EsChargeSettleAccountsMain extends CommonDomain{
    private String pk; //主键
    private String fld_area_guid; //项目ID
    private int fld_attribute; //属性(1空置 2销免 3售免 4地产垫付)
    private int fld_examine_status; //审批状态(1未提交 2审批中 3已打回 4审批通过)

}
