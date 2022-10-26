package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

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
public class EsChargeIncomingDataBill extends CommonDomain {
    private String fld_data_src_guid;
    private String fld_bill_guid;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        EsChargeIncomingDataBill that = (EsChargeIncomingDataBill) o;
        return Objects.equals(fld_data_src_guid, that.fld_data_src_guid) && Objects.equals(fld_bill_guid, that.fld_bill_guid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fld_data_src_guid, fld_bill_guid);
    }
}
