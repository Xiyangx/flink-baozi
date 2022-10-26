package com.sunac.entity;

import com.sunac.domain.CommonDomain;
import lombok.*;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 12:00 下午
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsInfoObjectAndOwner extends CommonDomain implements Comparable<EsInfoObjectAndOwner>{
    private String pk;
    private String fld_object_guid;
    private String fld_owner_guid;
    private String fld_area_guid;
    private Integer fld_is_owner; //int	居住关系(0业主 1租户 2地产公司 -1其它,99 关联后null值替换为99)

    //        fld_is_current desc,是否当前(0否 1是)
    //        fld_is_charge desc, 是否计费(0否 1是)
    //        fld_status desc    数据状态(1可用 2不可用 6迁出7作废)
    //        fld_is_current desc,.fld_is_charge desc,fld_status desc
    // 下面三个字段用于排序

    private Integer fld_is_current;
    private Integer fld_is_charge;
    private Integer fld_status;

    @SneakyThrows
    @Override
    public int compareTo(EsInfoObjectAndOwner other) {
        if (null == other) {
            return -1;
        }
        //第一排序字段
        if (this.getFld_is_current() != other.getFld_is_current()) {
            return other.getFld_is_current() - this.getFld_is_current();
        }
        if (this.getFld_is_charge() != other.getFld_is_charge()) {
            return other.getFld_is_charge() - this.getFld_is_charge();
        }
        if (this.getFld_status() != other.getFld_status()) {
            return other.getFld_status() - this.getFld_status();
        }
        return 0;
    }

}
