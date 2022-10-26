package com.sunac.domain;

import lombok.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_settle_accounts_detail地产结算明细表
public class EsChargeSettleAccountsDetail extends CommonDomain implements Comparable<EsChargeSettleAccountsDetail> {
    private static final long serialVersionUID = 1L;
    private String pk1;
    private String pk2;
    private String fld_owner_fee_guid; //主欠费ID（地产垫付：保存原欠费ID）
    private String fld_adjust_guid; //优惠欠费ID（地产垫付：保存复制后的）
    private String fld_area_guid; //项目ID
    private String fld_main_guid; //地产结算主表ID
    private Integer fld_status; //状态（0停用 1可用 -1删除）
    private Integer fld_settle_status;//结算状态(0已作废 1已收费 2部分收费 3未收费 4已返销 5退款中 6已退款 7 剩余作废中)
    private String fld_bill_no;  //发票号 AS fld_accounts_detail_bill_no,
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
