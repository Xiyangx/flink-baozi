package com.sunac.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi.domain
 * @date:2022/8/30
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String sex;
}
