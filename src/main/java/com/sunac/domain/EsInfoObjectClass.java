package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi.domain
 * @date:2022/8/30
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsInfoObjectClass extends CommonDomain {
    private String fld_name;
}
