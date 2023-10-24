/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.LogSamples;
import io.holoinsight.server.agg.v1.core.dict.Dict;
import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;
import io.holoinsight.server.agg.v1.executor.executor.Group;
import io.holoinsight.server.agg.v1.executor.executor.GroupField;
import io.holoinsight.server.agg.v1.executor.executor.TopnState;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import io.holoinsight.server.agg.v1.executor.executor.XParserUtils;
import io.holoinsight.server.agg.v1.executor.executor.XSelect;
import io.holoinsight.server.agg.v1.executor.executor.XSelectItem;
import io.holoinsight.server.agg.v1.executor.utils.StringDict;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import io.holoinsight.server.agg.v1.pb.StateProtos;

/**
 * 将内存状态转为 pb, 方便序列化之后存储. TODO 整个过程很容易出错 TODO 以后 增删字段 后怎么确保完整的修改 不遗漏?
 * <p>
 * created at 2023/9/21
 *
 * @author xzchaoo
 */
public class StatePbConverter {
  private static final ThreadLocal<Context> CTX_TL = new ThreadLocal<>();

  public static PartitionState fromPb(StateProtos.PartitionState pb) {
    Context ctx = new Context();
    CTX_TL.set(ctx);

    pb.getStringDictList().forEach(Dict::get);
    pb.getStringDictList().forEach(ctx.dict::add);

    PartitionState s = new PartitionState();
    s.setVersion(pb.getVersion());
    s.setOffset(pb.getOffset());
    s.setMaxEventTimestamp(pb.getMaxEventTimestamp());

    pb.getAggTaskStatesList().stream().map(StatePbConverter::fromPb).forEach(s::put);

    CTX_TL.remove();
    return s;
  }

  public static StateProtos.PartitionState toPb(PartitionState s) {
    Context ctx = new Context();
    CTX_TL.set(ctx);

    StateProtos.PartitionState.Builder b = StateProtos.PartitionState.newBuilder() //
        .setVersion(s.getVersion()) //
        .setOffset(s.getOffset()) //
        .setMaxEventTimestamp(s.getMaxEventTimestamp()); //

    s.getAggTaskStates().values().stream().map(StatePbConverter::toPb).forEach(b::addAggTaskStates);

    b.addAllStringDict(ctx.dict.getList());
    CTX_TL.remove();

    return b.build();
  }

  public static AggProtos.AggTaskKey toPb(AggTaskKey k) {
    return AggProtos.AggTaskKey.newBuilder().setTenant(k.getTenant()).setAggId(k.getAggId())
        .setPartition(k.getPartition()).build();
  }

  public static AggTaskKey fromPb(AggProtos.AggTaskKey pb) {
    return new AggTaskKey(pb.getTenant(), pb.getAggId(), pb.getPartition());
  }

  private static AggTaskState fromPb(StateProtos.AggTaskState pb) {
    AggTaskState s = new AggTaskState();
    s.setKey(fromPb(pb.getKey()));
    s.setMaxEventTimestamp(pb.getMaxEventTimestamp());
    s.setWatermark(pb.getWatermark());

    pb.getAggWindowStatesList().stream().map(StatePbConverter::fromPb).forEach(s::put);
    return s;
  }

  private static AggWindowState fromPb(StateProtos.AggWindowState pb) {
    AggWindowState s = new AggWindowState();
    s.setTimestamp(pb.getTimestamp());
    s.getStat().setInput(pb.getProcessed());

    String aggTaskJson = pb.getExtensionOrThrow("aggTask");
    XAggTask aggTask = XParserUtils.parse(JSON.parseObject(aggTaskJson, AggTask.class));
    s.setAggTask(aggTask);

    for (StateProtos.Group x : pb.getGroupsList()) {
      s.add(fromPb(aggTask, x));
    }

    return s;
  }

  private static Group fromPb(XAggTask aggTask, StateProtos.Group pb) {
    XSelect select = aggTask.getSelect();

    Group g = new Group();
    g.setTags(fromPb(aggTask, pb.getKey()));

    List<StateProtos.GroupField> pblist = pb.getFieldsList();
    GroupField[] gfs = new GroupField[pblist.size()];
    g.setFields(gfs);
    for (int i = 0; i < pblist.size(); i++) {
      gfs[i] = fromPb(pblist.get(i), select.getItem(i));
    }
    return g;
  }

  private static Context ctx() {
    return CTX_TL.get();
  }

  private static FixedSizeTags fromPb(XAggTask aggTask, StateProtos.GroupKey pb) {
    Context ctx = ctx();
    String[] values = new String[pb.getKeyCount()];
    for (int i = 0; i < pb.getKeyCount(); i++) {
      values[i] = ctx.dict.get(pb.getKey(i));
    }
    return new FixedSizeTags(aggTask.getInner().getGroupBy().getGroupTagKeys(), values);
  }

  private static GroupField fromPb(StateProtos.GroupField pb, XSelectItem item) {
    GroupField x = new GroupField();
    x.setAgg(item.getInner().getAgg());
    x.setCount(pb.getCount());
    x.setValue(pb.getValue());
    x.setInput(pb.getAccepted());

    if (pb.hasLogSamples()) {
      StateProtos.LogSamplesState pblss = pb.getLogSamples();

      LogSamplesState lss = new LogSamplesState();
      lss.setStrategy(pblss.getStrategy());
      lss.setMaxCount(pblss.getMaxCount());

      pblss.getHostLogSamplesList() //
          .stream() //
          .map(StatePbConverter::fromPb) //
          .collect(Collectors.groupingBy(LogSamples.HostLogSample::getHostname, HashMap::new,
              Collectors.toCollection(LinkedList::new))) //
          .values() //
          .forEach(lss::add); //

      x.setLogSamples(lss);
    }

    // TODO topn state store
    ByteString topnStateBytes = pb.getBytesExtensionOrDefault("topnState", null);
    if (topnStateBytes != null) {
      TopnState.DTO dto = JSON.parseObject(topnStateBytes.toStringUtf8(), TopnState.DTO.class);
      TopnState topnState = TopnState.fromDTO(item.getInner().getAgg(), dto);
      x.setTopn(topnState);
    }

    return x;
  }

  private static LogSamples.HostLogSample fromPb(StateProtos.HostLogSample pb) {
    LogSamples.HostLogSample s = new LogSamples.HostLogSample();
    s.setHostname(pb.getHostname());
    s.setLogs(new LinkedList<>(pb.getLogsList()));
    return s;
  }

  private static StateProtos.AggTaskState.Builder toPb(AggTaskState s) {
    StateProtos.AggTaskState.Builder b = StateProtos.AggTaskState.newBuilder() //
        .setKey(toPb(s.getKey())) //
        .setMaxEventTimestamp(s.getMaxEventTimestamp()) //
        .setWatermark(s.getWatermark()); //

    s.getAggWindowStateMap() //
        .values() //
        .stream() //
        .map(StatePbConverter::toPb) //
        .forEach(b::addAggWindowStates); //

    return b;
  }

  private static StateProtos.AggWindowState.Builder toPb(AggWindowState s) {
    StateProtos.AggWindowState.Builder b = StateProtos.AggWindowState.newBuilder() //
        .setTimestamp(s.getTimestamp()) //
        .setProcessed(s.getStat().getInput());

    b.putExtension("aggTask", JSON.toJSONString(s.getAggTask().getInner()));

    if (s.getGroupMap() != null) {
      s.getGroupMap().values().stream().map(StatePbConverter::toPb).forEach(b::addGroups);
    }

    return b;
  }

  private static StateProtos.Group.Builder toPb(Group group) {
    StateProtos.Group.Builder b = StateProtos.Group.newBuilder() //
        .setKey(toPb(group.getTags())); //

    for (GroupField gf : group.getFields()) { //
      b.addFields(toPb(gf)); //
    }
    return b;
  }

  private static StateProtos.GroupKey.Builder toPb(FixedSizeTags tags) {
    Context ctx = CTX_TL.get();

    StateProtos.GroupKey.Builder b = StateProtos.GroupKey.newBuilder();
    for (String str : tags.getValues()) {
      b.addKey(ctx.dict.add(str));
    }
    return b;
  }

  private static StateProtos.GroupField.Builder toPb(GroupField gf) {
    StateProtos.GroupField.Builder b = StateProtos.GroupField.newBuilder() //
        .setCount(gf.getCount()) //
        .setValue(gf.getValue()) //
        .setAccepted(gf.getInput());

    {
      LogSamplesState lss = gf.getLogSamples();
      if (lss != null && !lss.isEmpty()) {
        StateProtos.LogSamplesState.Builder b1 = StateProtos.LogSamplesState.newBuilder() //
            .setMaxCount(lss.getMaxCount()) //
            .setStrategy(lss.getStrategy()); //

        if (lss.getAccumulated() != null) {
          lss.getAccumulated() //
              .stream() //
              .map(StatePbConverter::toPb) //
              .forEach(b1::addHostLogSamples);
        }

        if (lss.getPending() != null) {
          lss.getPending() //
              .stream() //
              .map(StatePbConverter::toPb) //
              .forEach(b1::addHostLogSamples);
        }

        b.setLogSamples(b1);
      }
    }

    {
      TopnState ts = gf.getTopn();
      if (ts != null) {
        TopnState.DTO dto = ts.toDTO();
        byte[] bytes = JSON.toJSONBytes(dto);
        b.putBytesExtension("topnState", ByteString.copyFrom(bytes));
      }
    }
    return b;
  }

  private static StateProtos.HostLogSample.Builder toPb(LogSamples.HostLogSample s) {
    return StateProtos.HostLogSample.newBuilder() //
        .setHostname(s.getHostname()) //
        .addAllLogs(s.getLogs()); //
  }

  private static class Context {
    final StringDict dict = new StringDict();
  }
}
