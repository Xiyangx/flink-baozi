package com.sunac.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.domain
 * @date:2022/9/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsInfoAreaInfo extends CommonDomain {
    private String fld_dq;
    private String fld_confirm_date;
    private String fld_company;
    private String fld_xm;
    private String fld_ywdy;
    private String fld_yt;
    private String fld_name;//fld_name AS fld_area_name,
    // 收费方式：酬金制、包干制
    private String fld_fee_type;

}
