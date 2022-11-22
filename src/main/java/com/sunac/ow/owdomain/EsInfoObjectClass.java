package com.sunac.ow.owdomain;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


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
public class EsInfoObjectClass extends CommonDomain {
    private String fld_name;
}
