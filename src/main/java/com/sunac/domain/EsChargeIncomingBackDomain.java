package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_incoming_back退款总表
public class EsChargeIncomingBackDomain extends CommonDomain implements Comparable<EsChargeIncomingBackDomain> {
    private String fld_incoming_back_guid; // as back_convert_kou_guid 退款对应实收总表ID
    private String fld_incoming_convert_guid; // as back_convert_kou_guid 转款对应实收总表ID
    private String fld_incoming_kou_guid; // as back_convert_kou_guid  扣款对应实收总表ID
    private String fld_submit_time; //取fld_submit_time desc最近的一笔 ,提交时间
    @SneakyThrows
    @Override
    public int compareTo(EsChargeIncomingBackDomain o) {
        if (null == o) {
            return -1;
        }
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = 0;
        if (sf.parse(this.getFld_submit_time().toString()).getTime() < sf.parse(o.getFld_submit_time()).getTime()) {
            i = 1;
        } else if (sf.parse(this.getFld_submit_time().toString()).getTime() > sf.parse(o.getFld_submit_time()).getTime()) {
            i = -1;
        }
        return i;
    }
}
