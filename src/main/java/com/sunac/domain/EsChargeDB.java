package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsChargeDB extends CommonDomain implements Serializable, Comparable<EsChargeDB> {
    private static final long serialVersionUID = 1L;
    private String fld_data_src_guid;
    private int fld_status;//发票状态
    private String fld_bill_code;//发票代码
    private String fld_name;//票据类型GUID
    private String fld_type_guid;//关联t表使用
    private String fld_operate_date;

    @SneakyThrows
    @Override
    public int compareTo(EsChargeDB o) {
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = 0;
        if (sf.parse(this.getFld_operate_date().toString()).getTime() < sf.parse(o.fld_operate_date.toString()).getTime()) {
            i = -1;
        } else if (sf.parse(this.getFld_operate_date().toString()).getTime() > sf.parse(o.fld_operate_date.toString()).getTime()) {
            i = 1;
        }
        return i;
    }
}
