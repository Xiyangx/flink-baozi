package com.sunac.map;

import com.sunac.domain.EsChargeBill;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/9/16
 */
public class Test {
    public static void main(String[] args) {
        EsChargeBill esChargeBill = new EsChargeBill();
        esChargeBill.setFld_guid("111");
        System.out.println(esChargeBill.getFld_guid());
    }
}
