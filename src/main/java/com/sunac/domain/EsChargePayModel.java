package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.domain
 * @date:2022/9/2
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsChargePayModel extends CommonDomain {
    private String fld_name;
    private String fld_resource;
    private Integer fld_pre_pay;    // int	0付款方式 1预付款，关联后null值替换为99
    private Integer fld_attribute;
}
