package com.sunac.ow.owdomain;

import com.sunac.domain.CommonDomain;
import lombok.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class EsChargeTicketPayDetail extends CommonDomain implements Comparable<EsChargeTicketPayDetail>{
    private String fld_owner_fee_guid;
    private String fld_operate_guid;
    private String fld_create_date;

    @SneakyThrows
    @Override
    public int compareTo(EsChargeTicketPayDetail o) {
        if (null == o) {
            return -1;
        }
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = 0;
        if (sf.parse(this.getFld_create_date().toString()).getTime() > sf.parse(o.getFld_create_date().toString()).getTime()) {
            i = -1;
        } else if (sf.parse(this.getFld_create_date().toString()).getTime() < sf.parse(o.getFld_create_date().toString()).getTime()) {
            i = 1;
        }
        return i;
    }
}
