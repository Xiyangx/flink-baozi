package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_project_period科目归类表
public class EsChargeProjectPeriodDomain extends CommonDomain{
    private int fld_type; //类型 0业务归类 1报表归类
    private String fld_name; // as fld_project_period_name,归类名称
}
