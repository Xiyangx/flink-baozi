package com.sunac.domain;

import lombok.*;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class EsChargeProject extends CommonDomain {
    private String fld_name;//票据类型GUID AS fld_project_name
    private String fld_object_type;//关联t表使用
}
