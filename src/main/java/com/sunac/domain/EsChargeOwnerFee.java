package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi.domain
 * @date:2022/8/30
 */
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsChargeOwnerFee extends CommonDomain {
    private String pk;
    private String fld_reason_remark;
    private String fld_price;
    private BigDecimal fld_rebate;
    private String fld_resource;
    private String fld_desc;
    private String fld_adjust_guid;
    private String fld_area_guid;
}
