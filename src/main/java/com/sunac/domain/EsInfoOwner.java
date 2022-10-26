package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.domain
 * @date:2022/9/6
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

public class EsInfoOwner extends CommonDomain {
    //        fld_guid,
    //        fld_name AS fld_owner_name,
    //        fld_desc AS fld_owner_desc,
    private String fld_name;
    private String fld_desc;
}
