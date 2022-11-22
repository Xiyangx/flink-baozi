package com.sunac.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author lzc
 * @Date 2022/8/27 14:05
 */
public class ThreadPoolUtil {
public static synchronized ThreadPoolExecutor getThreadPool() {
    return new ThreadPoolExecutor(
            10, // 运行的核心线程数量
            25,  // 最大线程数据流
            120,  // 空闲线程的存活时间
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000) // 超过最大时, 线程会存入到队列中
    );
}
}
