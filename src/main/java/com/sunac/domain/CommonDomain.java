package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.domain
 * @date:2022/9/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommonDomain {
    public String operation_type = "PASS";
    public String fld_guid;
    // 变化的字段
    public HashMap<String, String> updateInfoMap;
}
