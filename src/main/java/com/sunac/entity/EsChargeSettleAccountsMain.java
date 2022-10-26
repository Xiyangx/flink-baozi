package com.sunac.entity;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 12:03 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class EsChargeSettleAccountsMain extends CommonDomain {
    private String pk;
    private String fld_area_guid;
    private Integer fld_examine_status;
    private Integer fld_settle_status;
    private Integer fld_attribute;
    private String fld_bill_no;
    private String fld_examine_date;
}
