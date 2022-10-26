package com.sunac.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/9 12:06 下午
 * @Version 1.0
 */
public class MyJsonStringProcessFunction extends ProcessFunction<String, JSONObject> {
    @Override
    public void processElement(String s, Context context, Collector<JSONObject> collector) throws Exception {
        JSONObject jsonObject=null;
        try {
            jsonObject = JSON.parseObject(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != jsonObject){
            collector.collect(jsonObject);
        }
    }
}
