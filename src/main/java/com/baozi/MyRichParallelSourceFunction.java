package com.baozi;

import com.baozi.domain.ChargeOwnerFee;
import com.baozi.utils.JdbcUtil;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/9/1
 */
public class MyRichParallelSourceFunction extends RichParallelSourceFunction<ChargeOwnerFee> {
    transient Connection conn = null;
    transient Statement statement = null;
    transient ArrayList<String> set = null;
    ChargeOwnerFee chargeOwnerFee = null;
    ResultSet res = null;
    List<String> selfList = null;
    volatile boolean flag = true;

    @Override
    public void open(Configuration parameters) throws Exception {
        set = new ArrayList<>();
        conn = JdbcUtil.getConnection();
        statement = conn.createStatement();
        chargeOwnerFee = new ChargeOwnerFee();

        String s = "select fld_allot_date from maindb.es_charge_owner_fee group by fld_allot_date";
        ResultSet res = conn.createStatement().executeQuery(s);
        while (res.next()) {
            this.set.add(res.getString("fld_allot_date"));
        }
        int indexOfThisSubtask = getRuntimeContext().getIndexOfThisSubtask();
        List<List<String>> newList = Lists.partition(set, (int) Math.ceil(new Double(set.size()) / new Double(2)));
        for (int index = 0; index < newList.size(); index++) {
            if (indexOfThisSubtask == index) {
                selfList = newList.get(index);
                System.out.println("========================indexOfThisSubtask=" + indexOfThisSubtask + "============" + selfList.toString());
                return;
            }
            continue;
        }

    }

    @Override
    public void close() throws Exception {
        JdbcUtil.close(conn, statement);
    }

    @Override
    public void run(SourceContext<ChargeOwnerFee> sourceContext) throws Exception {
//        LIMIT (pageNo - 1) * pageSize, pageSize;
        String sql = "SELECT fld_reason_remark,fld_price,fld_rebate,fld_resource,fld_desc,fld_adjust_guid,fld_guid,fld_area_guid "
                + "FROM es_charge_owner_fee where fld_allot_date='%s' limit 100000";
        for (String fld_allot_date : selfList) {
            res = statement.executeQuery(String.format(sql, fld_allot_date));
//            System.out.println("=====================================执行sql=" + String.format(sql, fld_allot_date));
//            System.out.println("==============现在处理fld_allot_date=" + fld_allot_date + "==============条数:" + res.getFetchSize());

            while (res.next()) {
                String fld_reason_remark = res.getString("fld_reason_remark");
                String fld_price = res.getString("fld_price");
                String fld_rebate = res.getString("fld_rebate");
                String fld_resource = res.getString("fld_resource");
                String fld_desc = res.getString("fld_desc");
                String fld_adjust_guid = res.getString("fld_adjust_guid");
                String fld_guid = res.getString("fld_guid");
                String fld_area_guid = res.getString("fld_area_guid");
                String pk = DigestUtils.md5Hex(fld_guid + fld_area_guid);
                chargeOwnerFee.setPk(pk);
                chargeOwnerFee.setFld_reason_remark(fld_reason_remark);
                chargeOwnerFee.setFld_price(fld_price);
                chargeOwnerFee.setFld_rebate(new BigDecimal(fld_rebate));
                chargeOwnerFee.setFld_resource(fld_resource);
                chargeOwnerFee.setFld_desc(fld_desc);
                chargeOwnerFee.setFld_adjust_guid(fld_adjust_guid);
                sourceContext.collect(chargeOwnerFee);
            }
            res = null;
        }
        while (flag) {
            Thread.sleep(100000);
            continue;
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }
}
