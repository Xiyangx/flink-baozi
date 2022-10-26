package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_two_balance对账信息表
public class EsChargeTwoBalanceDomain extends CommonDomain {
    //    private String fld_guid; // as fld_balance_guid,
    private String fld_date; // as two_fld_date,交易日期
    private String fld_create_user; // as two_fld_create_user, 创建人
    private String fld_hand_in_guid; //交款申请id
}
