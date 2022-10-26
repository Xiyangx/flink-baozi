package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_project_period_join科目归类关联科目
public class EsChargeProjectPeriodJoinDomain extends CommonDomain {
    private String fld_project_guid; //科目ID
    private String fld_period_guid; //归类ID
}
