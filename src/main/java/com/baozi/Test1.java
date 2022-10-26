package com.baozi;

import com.baozi.domain.ChargeOwnerFee;
import com.baozi.utils.JdbcUtil;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.tuple.Tuple10;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.BootstrapTransformation;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.state.api.OperatorTransformation;
import org.apache.flink.state.api.Savepoint;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: create by Lantian
 * @version: v1.0
 * @description: com.baozi
 * @date:2022/8/29
 */
public class Test1 {
    public static void main(String[] args) throws Exception {
        writeData();
    }

    private static void readData() throws Exception {
        ExecutionEnvironment bEnv = ExecutionEnvironment.getExecutionEnvironment();

        ExistingSavepoint savepoint = Savepoint.load(bEnv, "file:///H://flink-baozi//src//main//java//baozi2", new EmbeddedRocksDBStateBackend(true));
        DataSource<ChargeOwnerFee> mapState = savepoint.readKeyedState("uid-test", new KeyedStateReaderFunction<String, ChargeOwnerFee>() {
            MapState<String, ChargeOwnerFee> mapState;

            @Override
            public void open(Configuration configuration) throws Exception {
                RuntimeContext runtimeContext = getRuntimeContext();
                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, ChargeOwnerFee>("map_stat", String.class, ChargeOwnerFee.class));
                Iterator<Map.Entry<String, ChargeOwnerFee>> entryIterator = mapState.iterator(); // 拿到mapstate的entry迭代器
                for (Iterator<Map.Entry<String, ChargeOwnerFee>> it = entryIterator; it.hasNext(); ) {
                    Map.Entry<String, ChargeOwnerFee> entry = it.next();
                    System.out.println(String.format("key--: %s, value--: %s", entry.getKey(), entry.getValue()));
                }
            }

            @Override
            public void readKey(String key, KeyedStateReaderFunction.Context context, Collector<ChargeOwnerFee> out) throws Exception {
                out.collect(mapState.get(key));
            }
        });
        mapState.print();
    }

    private static void writeData() throws Exception {
        // 混合环境创建
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//        Connection conn = JdbcUtil.getConnection();
//        String s = "select fld_allot_date, count(1) mn from maindb.es_charge_owner_fee   group by fld_allot_date order by count(1)  desc";
//        ResultSet res = conn.createStatement().executeQuery(s);
//        ArrayList<String> set = new ArrayList<>();
//        while (res.next()) {
//           set.add(res.getString("fld_allot_date"));
//        }

//         https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/dev/dataset/overview/
        DataSet<Row> dbData = env.createInput(JdbcInputFormat.buildJdbcInputFormat()
                .setDrivername("com.mysql.cj.jdbc.Driver")
                .setDBUrl("jdbc:mysql://172.17.44.84:4000/maindb")
                .setUsername("report_readonly_user").setPassword("TIDB_report_user")
                .setQuery("SELECT " + "md5(CONCAT(fld_guid,fld_area_guid)) pk," + "fld_reason_remark," + "fld_price," + "fld_rebate," + "fld_resource," + "fld_desc," + "fld_adjust_guid," + "fld_guid,fld_area_guid FROM es_charge_owner_fee").setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.BIG_DEC_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO)).setFetchSize(1000).finish());
        MapOperator<Row, ChargeOwnerFee> mapData = dbData.map(new MapFunction<Row, ChargeOwnerFee>() {
            @Override
            public ChargeOwnerFee map(Row row) throws Exception {
                System.out.println(row.toString());
                return new ChargeOwnerFee(row.getFieldAs(0),
                        row.getFieldAs(1),
                        row.getFieldAs(2),
                        row.getFieldAs(3),
                        row.getFieldAs(4),
                        row.getFieldAs(5),
                        row.getFieldAs(6),
                        row.getFieldAs(7),
                        row.getFieldAs(8));
            }
        });
        // .returns(new TypeHint<Tuple2<String, Integer>>() {});   // 通过 TypeHint 传达返回数据类型
        // .returns(TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {}));  // 更通用的，是传入TypeInformation,上面的TypeHint也是封装了TypeInformation
        // .returns(Types.TUPLE(Types.STRING, Types.INT));  // 利用工具类Types的各种静态方法，来生成TypeInformation

        BootstrapTransformation<ChargeOwnerFee> transformation = OperatorTransformation
                .bootstrapWith(mapData)
                .keyBy(k -> k.getPk())
                .transform(new KeyedStateBootstrapFunction<String, ChargeOwnerFee>() {
            MapState<String, ChargeOwnerFee> mapState;
            @Override
            public void open(Configuration parameters) throws Exception {
                RuntimeContext runtimeContext = getRuntimeContext();
                mapState = runtimeContext.getMapState(new MapStateDescriptor<String, ChargeOwnerFee>("map_stat", String.class, ChargeOwnerFee.class));
            }
            @Override
            public void processElement(ChargeOwnerFee chargeOwnerFee, Context context) throws Exception {
                mapState.put(chargeOwnerFee.getPk(), chargeOwnerFee);
            }
        });
        EmbeddedRocksDBStateBackend embeddedRocksDBStateBackend = new EmbeddedRocksDBStateBackend(true);
        embeddedRocksDBStateBackend.setDbStoragePath("file:///D://ckpt");
        Savepoint.create(embeddedRocksDBStateBackend, 128)
                .withOperator("es_charge_owner_fee", transformation)
                .write("file:///D://charge");
        env.execute();
    }
}
