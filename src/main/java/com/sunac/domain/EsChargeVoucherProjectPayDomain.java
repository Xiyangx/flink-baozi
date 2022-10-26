package com.sunac.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_voucher_project_pay凭证-核算科目配置-付款方式
public class EsChargeVoucherProjectPayDomain extends CommonDomain {
    private String fld_pay_mode_guid; //付款方式as voucher_fld_pay_mode_guid
}
