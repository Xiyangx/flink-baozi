package com.sunac.utils;

import com.sunac.ow.owdomain.AllData;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.utils
 * @date:2022/9/14
 */
public class Test {
    public static void main(String[] args) throws Exception {
        AllData value = new AllData();
        value.setEcrr_fld_general_tax(new BigDecimal("-1.00"));
        value.setFld_amount(new BigDecimal("0"));
        BigDecimal tmp = value.getEcrr_fld_general_tax();

        if (tmp.compareTo(new BigDecimal("-1.00"))==0){
            tmp = new BigDecimal("-1");
        }
        if (value.getEcrr_fld_general_tax() == null) {
            tmp = BigDecimal.valueOf(-1);
        }
        if (tmp.equals(BigDecimal.valueOf(-1)) || tmp.equals(BigDecimal.valueOf(-2))) {
            tmp = BigDecimal.valueOf(0);
        }




        BigDecimal bigDecimal = new BigDecimal("-1.00");
        BigDecimal subtract = bigDecimal.subtract(new BigDecimal("-1"));
        System.out.println("multiply = " + subtract);
    }
}
