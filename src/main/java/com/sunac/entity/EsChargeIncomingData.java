package com.sunac.entity;

import com.sunac.domain.CommonDomain;
import lombok.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 12:04 下午
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsChargeIncomingData extends CommonDomain implements Comparable<EsChargeIncomingData>{
    private String fld_owner_fee_guid;
    private String fld_incoming_fee_guid;
    private String fld_area_guid;

    private String fld_create_date;
    private String fld_operate_date;
    private String fld_create_user;
    private Integer fld_busi_type;
    private BigDecimal fld_total;
    private BigDecimal fld_amount;
    private BigDecimal fld_late_fee;
    private BigDecimal fld_tax_amount;
    private BigDecimal fld_tax;
    private Integer fld_cancel;
    private BigDecimal fld_late_amount;

    @SneakyThrows
    @Override
    public int compareTo(EsChargeIncomingData o) {
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
