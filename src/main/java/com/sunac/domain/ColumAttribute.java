package com.sunac.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.domain
 * @date:2022/9/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumAttribute {
    private String newName;
    private boolean isTable_column;
    private boolean isUpdate_column;
    private boolean isDelete_column;
    private boolean isInsert_column;
    private Object defaultValue;
}
