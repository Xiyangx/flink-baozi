package com.sunac.entity;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 11:59 上午
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsChargeProject extends CommonDomain {
    private Integer fld_object_type;  //int	计费类型(-1分类 0预付款 1三表类费用 2周期性费用 3一次性费用用,99 关联后null值替换为99)es_charge_project

}
