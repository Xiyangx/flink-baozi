package com.sunac.map;

import com.sunac.domain.EsChargeOwnerFee;
import com.sunac.utils.Util;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.sunac.map
 * @date:2022/9/8
 */
public class AllDataMapFunction implements MapFunction<String, EsChargeOwnerFee> {
    @Override
    public EsChargeOwnerFee map(String s) throws Exception {
        return Util.parseEsChargeOwnerFeeJson(s);
    }
}
