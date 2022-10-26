package com.sunac.entity;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/11 2:27 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class EsCommerceBondMain extends CommonDomain {
    private String pk;
    private String fld_area_guid;
    private String fld_owner_guid;
    private String fld_stop_date;
    private String fld_end_date;

}
