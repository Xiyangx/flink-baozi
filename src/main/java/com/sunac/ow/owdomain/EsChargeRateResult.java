package com.sunac.ow.owdomain;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 12:00 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class EsChargeRateResult extends CommonDomain {
    private String pk;
    private BigDecimal fld_general_tax;  // as fld_rate	decimal(18,2)	税率
    private String fld_area_guid;
    private String fld_project_guid;
    private String fld_start_date;
    private String fld_end_date;
    private BigDecimal fld_taxes;
}
