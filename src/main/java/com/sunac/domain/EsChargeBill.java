package com.sunac.domain;

import lombok.*;

import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.domain
 * @date:2022/9/2
 */
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsChargeBill extends CommonDomain implements Comparable<EsChargeBill> {
    private Integer fld_status;//发票状态
    private String fld_bill_code;//发票代码
    private String fld_type_guid;//票据类型GUID
    private String fld_operate_date;

    @SneakyThrows
    @Override
    public int compareTo(EsChargeBill o) {
        if (null == o) {
            return -1;
        }
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = 0;
        if (sf.parse(this.getFld_operate_date().toString()).getTime() > sf.parse(o.fld_operate_date.toString()).getTime()) {
            i = -1;
        } else if (sf.parse(this.getFld_operate_date().toString()).getTime() < sf.parse(o.fld_operate_date.toString()).getTime()) {
            i = 1;
        }
        return i;
    }

    //  `fld_status` int(11) NOT NULL DEFAULT '5' COMMENT '0已开票 1已换票 2已作废 3已红冲 4预开待收费 5待开票 6中间状态 ',
    //   `fld_bill_code` varchar(1000) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票代码',
    //   `fld_type_guid` char(36) COLLATE utf8mb4_general_ci NOT NULL COMMENT '票据类型GUID',

}
