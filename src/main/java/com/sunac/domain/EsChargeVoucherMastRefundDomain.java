package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_voucher_mast_refund凭证-主表-退款凭证
public class EsChargeVoucherMastRefundDomain extends CommonDomain implements Comparable<EsChargeVoucherMastRefundDomain> {
    private static final long serialVersionUID = 1L;
    private String fld_incoming_back_guid; //退款总表ID
    private String fld_appay_date; //退款日期
    private String fld_date; //退款日期

//105	mr1.fld_date as refund_fld_date_back
//106	mr2.fld_date as refund_fld_date_convert
//107	mr3.fld_date as refund_fld_date_kou

    @SneakyThrows
    @Override
    public int compareTo(EsChargeVoucherMastRefundDomain o) {
        if (null == o) {
            return -1;
        }
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = 0;
        if (sf.parse(this.getFld_appay_date().toString()).getTime() < sf.parse(o.getFld_appay_date().toString()).getTime()) {
            i = 1;
        } else if (sf.parse(this.getFld_appay_date().toString()).getTime() > sf.parse(o.getFld_appay_date().toString()).getTime()) {
            i = -1;
        }
        return i;
    }
}
