package com.sunac.ow.owdomain;

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
public class EsInfoOwner extends CommonDomain {
    private String fld_phone_number;	//string 手机号码
    private String fld_desc;  //as fld_owner_desc	string	客户描述
}
