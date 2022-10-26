package com.sunac;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac
 * @date:2022/9/15
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private String operation_type = "INSERT";
    private String fld_guid;
    private int fld_status;//发票状态
    private String fld_bill_code;//发票代码
    private String fld_type_guid;//票据类型GUID
    private String fld_operate_date;
}
