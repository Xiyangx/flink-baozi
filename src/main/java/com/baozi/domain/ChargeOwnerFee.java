package com.baozi.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi.domain
 * @date:2022/8/30
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeOwnerFee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String pk;
    private String fld_reason_remark;
    private String fld_price;
    private BigDecimal fld_rebate;
    private String fld_resource;
    private String fld_desc;
    private String fld_adjust_guid;
    private String fld_guid;
    private String fld_area_guid;

    @Override
    public String toString() {
        return "ChargeOwnerFee{" + "pk='" + pk + '\'' + ", fld_reason_remark='" + fld_reason_remark + '\'' + ", fld_price='" + fld_price + '\'' + ", fld_rebate=" + fld_rebate + ", fld_resource='" + fld_resource + '\'' + ", fld_desc='" + fld_desc + '\'' + ", fld_adjust_guid='" + fld_adjust_guid + '\'' + ", fld_guid='" + fld_guid + '\'' + ", fld_area_guid='" + fld_area_guid + '\'' + '}';
    }
}
