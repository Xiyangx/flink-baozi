package com.sunac.domain;

import lombok.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class EsChargeIncomingFee2 extends CommonDomain implements Comparable<EsChargeIncomingFee2> {
    private String fld_area_guid;
    private String fld_operate_date;
    private Integer fld_cancel;
    private String fld_create_user;



    @SneakyThrows
    @Override
    public int compareTo(EsChargeIncomingFee2 o) {
        if (null == o) {
            return 1;
        }
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = 0;
        if (sf.parse(this.getFld_operate_date().toString()).getTime() > sf.parse(o.fld_operate_date.toString()).getTime()) {
            i = 1;
        } else if (sf.parse(this.getFld_operate_date().toString()).getTime() < sf.parse(o.fld_operate_date.toString()).getTime()) {
            i = -1;
        }
        return i;
    }
}
