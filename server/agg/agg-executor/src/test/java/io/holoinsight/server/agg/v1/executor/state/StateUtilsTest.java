/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;

import io.holoinsight.server.agg.v1.core.conf.AggFunc;
import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.LogSamples;
import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;
import io.holoinsight.server.agg.v1.executor.executor.Group;
import io.holoinsight.server.agg.v1.executor.executor.GroupField;
import io.holoinsight.server.agg.v1.executor.executor.HllState;
import io.holoinsight.server.agg.v1.executor.executor.TopnState;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import io.holoinsight.server.agg.v1.executor.executor.XParserUtils;

/**
 * <p>
 * created at 2023/10/26
 *
 * @author xzchaoo
 */
public class StateUtilsTest {
  @Test
  public void test_kryo() throws IOException {
    PartitionState ps0 = new PartitionState();
    ps0.setVersion(1);
    ps0.setOffset(2);
    ps0.setMaxEventTimestamp(3);
    AggTaskKey aggTaskKey = new AggTaskKey("default", "aggId", "ppp=1");
    AggTaskState ats = new AggTaskState(aggTaskKey);
    ps0.getAggTaskStates().put(aggTaskKey, ats);

    ats.setWatermark(4);
    ats.setMaxEventTimestamp(5);
    AggWindowState aws = new AggWindowState();
    aws.setTimestamp(7L);
    ats.getAggWindowStateMap().put(7L, aws);
    aws.getStat().setInput(1);
    aws.getStat().setError(2);
    aws.getStat().setDiscardKeyLimit(3);
    aws.getStat().setFilterWhere(4);
    XAggTask aggTask;
    {
      InputStream is =
          getClass().getClassLoader().getResourceAsStream("agg/k8s_pod_system_5s.json");
      AggTask at = JSON.parseObject(is, AggTask.class);
      aggTask = XParserUtils.parse(at);
      Preconditions.checkNotNull(aggTask);
    }
    aws.setAggTask(aggTask);

    FixedSizeTags tags =
        new FixedSizeTags(new String[] {"k1", "k2", "k3"}, new String[] {"v1", "uv", "topn"});
    Group g = new Group();
    g.setTags(tags);

    // private LogSamplesState logSamples;
    // private TopnState topn;
    // private HllState hll;

    GroupField gf0 = new GroupField();
    gf0.setAgg(new AggFunc("SUM"));
    gf0.setInput(11);
    gf0.setCount(22);
    gf0.setValue(33);

    GroupField gf1 = new GroupField();
    gf1.setAgg(new AggFunc("HLL"));
    gf1.setInput(111);
    gf1.setCount(222);
    gf1.setValue(333);
    gf1.setHll(new HllState());
    gf1.getHll().getHll().addRaw(2023);

    GroupField gf2 = new GroupField();
    gf2.setAgg(new AggFunc("TOPN"));
    gf2.setInput(111);
    {
      AggFunc.TopnParams topnParams = new AggFunc.TopnParams();
      topnParams.setOrderBy("value");
      topnParams.setLimit(3);
      gf2.setTopn(new TopnState(topnParams));
      gf2.getTopn().getQ().add(new TopnState.CostItem(1, "xxx"));
    }

    GroupField gf3 = new GroupField();
    gf3.setAgg(new AggFunc("LOGSAMPLES_MERGE"));
    gf3.setInput(111);
    {
      LogSamplesState lss = new LogSamplesState();
      lss.setMaxCount(3);
      List<LogSamples.HostLogSample> samples = new ArrayList<>();
      LogSamples.HostLogSample hls = new LogSamples.HostLogSample();
      hls.setHostname("foo1");
      LinkedList<String> logs = new LinkedList<>();
      logs.add("log1");
      hls.setLogs(logs);
      samples.add(hls);
      lss.add(samples);
      gf3.setLogSamples(lss);
    }

    g.setFields(new GroupField[] { //
        gf0, //
        gf1, //
        gf2, //
        gf3, //
    });
    aws.getGroupMap().put(tags, g);

    byte[] b0 = StateUtils.serialize(ps0);
    PartitionState ps1 = StateUtils.deserialize(b0);
    byte[] b1 = StateUtils.serialize(ps1);
    Preconditions.checkArgument(Arrays.equals(b0, b1));
    StateUtils.checkEqual(ps0, ps1);

    System.out.println(Base64.getEncoder().encodeToString(b0));
    System.out.println(Base64.getEncoder().encodeToString(b1));
  }

  @Test
  public void test2() throws IOException {
    String b64 =
        "H4sIAAAAAAAAAJ1UzW7TQBDeWTu/LUXiwBNwRJZ4AEShorRqKpBi1ENPSzxxlmy81u46TbmAeAGepXCERwDEE3Bpox4KVYQtRG7sOjRFqgRNfbDWntnv++bbmQWfxXHIdL9tmMF8wEYPh5iYkA9QGzaYyG5XYzFEpbk8BfKcDVmQGS6CDaZ722wCFDyLsHmUMmW4sUkGE5YUHpR/PUjT9O6BBxF2WSYKAC6DnhSSJ5rHPRNoVBY8sMnB8E6AI+xkRqpAOzXB/XNpY3BCd3gSyb1SquXu4/SC3j0bUgOmToCeaw0VYqmVwAp4Cyo45xxDZWbWSaxkllpEm1SYOXWZuCYHqUBrAuqcALH+0D7u50MmMszB322VsgRL4qBtFE/i9+BD/wD6b6H/zi59GB5A9hOMTE+hcjmt88Ujq8uW2eUootywOIfqbusKGOm6Q3DKalfdfQQ1Fh93ZJYUPfGNJ2lWCBm3mXMnd8U5R8ZQ/yd+Ryq0r6TrDmI9Sw7Bm4PobVTx2CGZ/XRMiAftp5/obUJvEtIk7nm1ugo1gDq44EbrI/3gQWPBejaEmB390o03L0tUUjumX+GM44v5iyN8/OQzJcSFobkgUSjTZMZkB0ofCj7ghVQRqgfTCqFVD2Z+vYalKwLjrTWpzabB77YpO1IX3C5nRZDJPQ9Go19/vgiB5QVJ1vkIozZ/gaFtumW4drntMjNpZoL5hBXgR1x3mIq2cL/lHECl5I8uF3aod3qoxmUf0Sr1aZ3SxgqsLKhz58KMWreFCJmK0eQKM41Hhj2zLWqPENz8Alz/f4tGzLCz22oLp36j2ag3qg2f0t9fcDurYAUAAA==";
    byte[] bytes = Base64.getDecoder().decode(b64);
    PartitionState ps = StateUtils.deserialize(bytes);
    System.out.println(ps.getVersion());
    System.out.println(ps.getAggTaskStates().values().iterator().next().getAggWindowStateMap()
        .values().iterator().next().getGroupMap().values().iterator().next().getFields()[2]
            .getTopn().getParams().getOrderBy());
  }
}
