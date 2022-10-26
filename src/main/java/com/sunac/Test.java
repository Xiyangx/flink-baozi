package com.sunac;


import java.util.HashMap;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac
 * @date:2022/9/13
 */
public class Test {
    public static void main(String[] args) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("123","456");

        System.out.println(hm.toString());

        hm.clear();

        System.out.println(hm.toString());


    }
}
