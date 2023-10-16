/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/10/3
 *
 * @author xzchaoo
 */
@Slf4j
public class BatchSerdes {
  public static class S implements Serializer<XOutput.Batch> {
    @Override
    public byte[] serialize(String topic, XOutput.Batch data) {
      if (data == null) {
        return null;
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
        JSON.writeJSONString(gos, data);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return baos.toByteArray();
    }
  }

  public static class D implements Deserializer<XOutput.Batch> {
    @Override
    public XOutput.Batch deserialize(String topic, byte[] data) {
      if (data == null) {
        return null;
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      try (GZIPInputStream gis = new GZIPInputStream(bais)) {
        return JSON.parseObject(gis, XOutput.Batch.class);
      } catch (ZipException e) {
        return JSON.parseObject(data, XOutput.Batch.class);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
