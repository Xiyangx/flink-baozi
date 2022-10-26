package com.sunac.domain;

import lombok.*;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

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
public class EsChargeIncomingFee extends CommonDomain {
    private Integer fld_busi_type;
    private String fld_remark;
    private String fld_cancel_date;
    private String fld_cancel_guid;
    private String fld_number;
    private Integer fld_checkin;
    private String fld_cancel_me;
    private String fld_modify_date;
}
