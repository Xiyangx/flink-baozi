package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
//es_charge_hand_in_record
public class EsChargeHandInRecordDomain extends CommonDomain {
    private int fld_hand_in; //审批状态  1对账中 2提交失败 3待提交 4未审批 5打回 6审批通过 -1作废
    private String fld_start_date; // as fld_hand_in_start_date,交款起始时间
    private String fld_end_date; // as fld_hand_in_end_date,交款结束时间
}
