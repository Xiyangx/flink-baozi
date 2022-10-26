package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_voucher_check_service_set凭证-辅助核算配置-物业服务类型
public class EsChargeVoucherCheckServiceSetDomain extends CommonDomain {
    private String fld_project_class_guid; //科目ID/业态ID
    private int fld_type; // as fld_service_set_type, 1:科目类型 2:业态类型

}
