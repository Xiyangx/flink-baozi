package com.sunac.ow.owdomain;

import com.sunac.domain.CommonDomain;
import lombok.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 12:02 下午
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsChargeSettleAccountsDetail extends CommonDomain implements Comparable<EsChargeSettleAccountsDetail>{
    private String pk;
    private String fld_owner_fee_guid;
    private String fld_adjust_guid;
    private Integer fld_status;
    private String fld_area_guid;
    private String fld_main_guid;
    private String fld_create_date;


    @SneakyThrows
    @Override
    public int compareTo(EsChargeSettleAccountsDetail o) {
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
