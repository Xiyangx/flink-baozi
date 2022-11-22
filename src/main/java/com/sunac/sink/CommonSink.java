package com.sunac.sink;

import com.sunac.Config;
import com.sunac.domain.CommonDomain;
import com.sunac.ow.owdomain.AllData;
import com.sunac.utils.HikariUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/11/19 1:00 下午
 * @Version 1.0
 */
public class CommonSink extends RichSinkFunction<CommonDomain> {
    private final Logger log = LoggerFactory.getLogger(CommonSink.class);
    private PreparedStatement ps;
    private Connection conn;
    private final String sql;
    public CommonSink(String sql){
        this.sql = sql;
    }
    @Override
    public void open(Configuration parameters) throws Exception {
        conn = HikariUtil.getInstance().getConnection();
    }
    @Override
    public void close() throws Exception {
        ps.close();
        conn.close();
    }

    @Override
    public void invoke(CommonDomain value, Context context) throws Exception {
        ps = conn.prepareStatement(sql);
        ps.execute();
    }
}
