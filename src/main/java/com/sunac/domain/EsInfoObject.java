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

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsInfoObject extends CommonDomain {
    private String fld_name;
    private String fld_owner_fee_date;
    private String fld_building;
    private String fld_cell;
    private String fld_batch;
    private BigDecimal fld_charged_area;
    private Integer fld_status;
    private String fld_class_guid;
    //    资源总表es_info_object.fld_order，排序，未关联取-1
    private Integer fld_order = -1;


}
