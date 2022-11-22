package com.sunac.sink;

import com.sunac.utils.MyRunnable;
import com.sunac.utils.ThreadPoolUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class AllDataRichSinkFunctionPlusPlus extends RichSinkFunction<ArrayList<String>> {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AllDataRichSinkFunctionPlusPlus.class);

    private String uid;
    transient ThreadPoolExecutor threadPool;

    @Override
    public void open(Configuration parameters) throws Exception {
        threadPool = ThreadPoolUtil.getThreadPool();
    }
    public AllDataRichSinkFunctionPlusPlus(String uid) {
        this.uid = uid;
    }

    @Override
    public void invoke(ArrayList<String> sqlList, Context context) throws SQLException {
        log.info("===========================执行invoke方法!!!===========================");
        threadPool.submit(new MyRunnable(sqlList, uid));
    }
}
