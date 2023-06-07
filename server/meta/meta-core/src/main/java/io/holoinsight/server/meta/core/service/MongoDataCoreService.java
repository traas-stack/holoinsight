/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.reflect.TypeToken;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.Pair;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.core.common.DocumentUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static io.holoinsight.server.meta.common.util.ConstModel.default_hostname;
import static io.holoinsight.server.meta.common.util.ConstModel.default_ip;
import static io.holoinsight.server.meta.common.util.ConstModel.default_modified;
import static io.holoinsight.server.meta.common.util.ConstModel.default_pk;

/**
 *
 * @author jsy1001de
 * @version 1.0: MongoDataService.java, v 0.1 2022年03月07日 9:13 下午 jinsong.yjs Exp $
 */
public class MongoDataCoreService extends AbstractDataCoreService {

  public static final Logger logger = LoggerFactory.getLogger(MongoDataCoreService.class);

  private MongoDatabase mongoDatabase;

  public MongoDataCoreService(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

  @Override
  public Pair<Integer, Integer> insertOrUpdate(String tableName, List<Map<String, Object>> rows) {
    logger.info("[insertOrUpdate] start, table={}, records={}.", tableName, rows.size());
    if (CollectionUtils.isEmpty(rows))
      return new Pair<>(0, 0);

    List<Map<String, Object>> filterRows = addUkValues(tableName, rows);

    StopWatch stopWatch = StopWatch.createStarted();
    int matchedCount = 0;
    int modifiedCount = 0;
    int upsertSize = 0;
    for (Map<String, Object> data : filterRows) {

      Document doc = new Document(default_pk, data.get(default_pk));

      for (Map.Entry<String, Object> entry : data.entrySet()) {
        if (default_pk.equals(entry.getKey())) {
          continue;
        }
        doc.append(entry.getKey(), entry.getValue());
      }
      UpdateResult updateResult = mongoDatabase.getCollection(tableName).updateOne(
          new Document(default_pk, data.get(default_pk)), new Document("$set", doc),
          new UpdateOptions().upsert(true));
      matchedCount += updateResult.getMatchedCount();
      modifiedCount += updateResult.getModifiedCount();
      upsertSize += updateResult.getMatchedCount() - updateResult.getModifiedCount();
    }

    logger.info(
        "[insertOrUpdate] finish, table={}, upsertSize={}, matchedCount={}, modifiedCount={}, cost={}.",
        tableName, upsertSize, matchedCount, modifiedCount, stopWatch.getTime());
    return new io.holoinsight.server.common.Pair<>(upsertSize, modifiedCount);
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName) {
    logger.info("[queryByTable] finish, table={}.", tableName);
    StopWatch stopWatch = StopWatch.createStarted();
    FindIterable<Document> documents = mongoDatabase.getCollection(tableName).find()
        .projection(Projections.exclude("annotations"));
    List<Document> rows = new ArrayList<>();
    documents.into(rows);
    logger.info("[queryByTable] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return DocumentUtil.toMapList(rows);
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName, List<String> rowKeys) {
    logger.info("[queryByTable] finish, table={}, rowsKeys={}.", tableName, rowKeys);
    StopWatch stopWatch = StopWatch.createStarted();
    Document projection = new Document();
    if (!CollectionUtils.isEmpty(rowKeys)) {
      rowKeys.forEach(row -> {
        projection.append(row, 1);
      });
    }

    FindIterable<Document> documents =
        mongoDatabase.getCollection(tableName).find().projection(projection);
    List<Document> rows = new ArrayList<>();
    documents.into(rows);
    logger.info("[queryByTable] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return DocumentUtil.toMapList(rows);
  }

  @Override
  public List<Map<String, Object>> queryByPks(String tableName, List<String> pkValList) {
    logger.info("[queryByPks] finish, table={}, pkValList={}.", tableName, pkValList.size());
    if (CollectionUtils.isEmpty(pkValList))
      return new ArrayList<>();

    StopWatch stopWatch = StopWatch.createStarted();
    Bson filter = Filters.eq(default_pk, pkValList);

    FindIterable<Document> documents = mongoDatabase.getCollection(tableName).find(filter)
        .projection(Projections.exclude("annotations"));
    List<Document> rows = new ArrayList<>();
    documents.sort(Sorts.descending(default_modified)).into(rows);

    logger.info("[queryByPks] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());

    if (CollectionUtils.isEmpty(rows)) {
      return new ArrayList<>();
    }
    return DocumentUtil.toMapList(rows);
  }

  @Override
  public List<Map<String, Object>> queryByExample(String tableName, QueryExample queryExample) {
    logger.info("[queryByExample] finish, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    List<Bson> filters = new ArrayList<>();

    if (!CollectionUtils.isEmpty(queryExample.getParams())) {
      for (Map.Entry<String, Object> entry : queryExample.getParams().entrySet()) {

        if (entry.getValue() instanceof String
            && StringUtils.isNotBlank(entry.getValue().toString())) {
          filters.add(new Document(entry.getKey(), entry.getValue()));
        } else if (entry.getValue() instanceof List) {
          List<String> o =
              J.fromJson(J.toJson(entry.getValue()), new TypeToken<List<String>>() {}.getType());
          if (CollectionUtils.isEmpty(o))
            continue;
          filters.add(new Document(entry.getKey(), new Document("$in", o)));
        }
      }
    }

    Bson projection =
        Projections.fields(Projections.excludeId(), Projections.exclude("annotations"));
    if (!CollectionUtils.isEmpty(queryExample.getRowKeys())) {
      projection = Projections.fields(Projections.include(queryExample.getRowKeys()));
    }

    FindIterable<Document> documents;
    if (!CollectionUtils.isEmpty(filters)) {
      documents =
          mongoDatabase.getCollection(tableName).find(Filters.and(filters)).projection(projection);
    } else {
      documents = mongoDatabase.getCollection(tableName).find().projection(projection);
    }

    List<Document> rows = new ArrayList<>();
    documents.sort(Sorts.descending(default_modified)).into(rows);

    logger.info("[queryByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());

    return DocumentUtil.toMapList(rows);
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample queryExample) {
    logger.info("[fuzzyByExample] finish, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    List<Bson> filters = new ArrayList<>();
    if (!CollectionUtils.isEmpty(queryExample.getParams())) {
      for (Map.Entry<String, Object> entry : queryExample.getParams().entrySet()) {
        if (entry.getKey().equals(default_ip) || entry.getKey().equals(default_hostname)) {
          Pattern pattern = J.json2Bean(J.toJson(entry.getValue()), Pattern.class);
          filters.add(new Document(entry.getKey(), new Document("$regex", pattern.pattern())));
          continue;
        }
        if (entry.getValue() instanceof String
            && StringUtils.isNotBlank(entry.getValue().toString())) {
          filters.add(new Document(entry.getKey(), entry.getValue()));
        } else if (entry.getValue() instanceof List) {
          if (CollectionUtils.isEmpty((List) entry.getValue()))
            continue;
          filters.add(new Document(entry.getKey(), new Document("$in", entry.getValue())));
        }
      }
    }
    FindIterable<Document> documents;
    if (!CollectionUtils.isEmpty(filters)) {
      documents = mongoDatabase.getCollection(tableName).find(Filters.and(filters))
          .projection(Projections.exclude("annotations"));
    } else {
      documents = mongoDatabase.getCollection(tableName).find()
          .projection(Projections.exclude("annotations"));
    }
    List<Document> rows = new ArrayList<>();
    documents.sort(Sorts.descending(default_modified)).into(rows);
    logger.info("[fuzzyByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());

    return DocumentUtil.toMapList(rows);
  }


  @Override
  public long deleteByExample(String tableName, QueryExample queryExample) {
    logger.info("[deleteByExample] finish, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));

    StopWatch stopWatch = StopWatch.createStarted();
    List<Bson> filters = new ArrayList<>();

    if (!CollectionUtils.isEmpty(queryExample.getParams())) {
      for (Map.Entry<String, Object> entry : queryExample.getParams().entrySet()) {
        if (entry.getValue() instanceof String
            && StringUtils.isNotBlank(entry.getValue().toString())) {
          filters.add(new Document(entry.getKey(), entry.getValue()));
        } else if (entry.getValue() instanceof List) {
          if (CollectionUtils.isEmpty((List) entry.getValue()))
            continue;
          filters.add(new Document(entry.getKey(), new Document("$in", entry.getValue())));
        }
      }
    }

    DeleteResult deleteResult =
        mongoDatabase.getCollection(tableName).deleteMany(Filters.and(filters));

    // 输出结果信息
    logger.info("[deleteByExample] finish, table={}, deleteCount={}, cost={}.", tableName,
        deleteResult.getDeletedCount(), stopWatch.getTime());
    return deleteResult.getDeletedCount();
  }

  @Override
  public long deleteByRowMap(String tableName, List<Map<String, Object>> rows) {
    logger.info("[deleteByRowMap] finish, table={}, default_pks={}.", tableName, rows.size());
    if (CollectionUtils.isEmpty(rows))
      return 0;

    List<String> uks = getUks(tableName, rows);
    if (CollectionUtils.isEmpty(uks)) {
      logger.info("[deleteByRowMap] finish, table={}, uks is null.", tableName);
      return 0;
    }
    return batchDeleteByPk(tableName, uks);
  }

  @Override
  public long batchDeleteByPk(String tableName, List<String> default_pks) {
    logger.info("[batchDeleteByPk] finish, table={}, default_pks={}.", tableName,
        default_pks.size());

    if (CollectionUtils.isEmpty(default_pks))
      return 0;
    StopWatch stopWatch = StopWatch.createStarted();
    List<Bson> filters = new ArrayList<>();
    if (!CollectionUtils.isEmpty(default_pks)) {
      filters.add(new Document(default_pk, new Document("$in", default_pks)));
    }

    DeleteResult deleteResult =
        mongoDatabase.getCollection(tableName).deleteMany(Filters.and(filters));

    logger.info("[batchDeleteByPk] finish, table={}, deleteCount={}, cost={}.", tableName,
        deleteResult.getDeletedCount(), stopWatch.getTime());
    return deleteResult.getDeletedCount();
  }
}
