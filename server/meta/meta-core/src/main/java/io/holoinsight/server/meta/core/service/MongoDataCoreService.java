/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.core.common.DocumentUtil;
import io.holoinsight.server.meta.dal.service.MongoDbHelper;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import com.mongodb.bulk.BulkWriteResult;
import org.apache.commons.lang3.time.StopWatch;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static io.holoinsight.server.meta.common.util.ConstModel.*;

/**
 *
 * @author jsy1001de
 * @version 1.0: MongoDataService.java, v 0.1 2022年03月07日 9:13 下午 jinsong.yjs Exp $
 */
@Service
public class MongoDataCoreService extends AbstractDataCoreService {

  @Autowired
  private MongoDbHelper mongoDbHelper;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public io.holoinsight.server.common.Pair<Integer, Integer> insertOrUpdate(String tableName,
      List<Map<String, Object>> rows) {
    logger.info("[insertOrUpdate] start, table={}, records={}.", tableName, rows.size());
    if (CollectionUtils.isEmpty(rows))
      return new io.holoinsight.server.common.Pair<>(0, 0);

    List<Map<String, Object>> filterRows = addUkValues(tableName, rows);

    StopWatch stopWatch = StopWatch.createStarted();
    List<Pair<Query, Update>> updateList = new ArrayList<>(filterRows.size());
    BulkOperations operations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, tableName);
    filterRows.forEach(data -> {
      Query query = new Query(Criteria.where(default_pk).is(data.get(default_pk)));

      Update update = new Update();
      for (Map.Entry<String, Object> entry : data.entrySet()) {
        if (default_id.equals(entry.getKey())) {
          continue;
        }
        update.set(entry.getKey(), entry.getValue());
      }
      Pair<Query, Update> updatePair = Pair.of(query, update);
      updateList.add(updatePair);
    });
    operations.upsert(updateList);
    BulkWriteResult execute = operations.execute();

    int matchedCount = execute.getMatchedCount();
    int modifiedCount = execute.getModifiedCount();
    int upsertSize = execute.getUpserts().size();
    logger.info(
        "[insertOrUpdate] finish, table={}, upsertSize={}, matchedCount={}, modifiedCount={}, cost={}.",
        tableName, upsertSize, matchedCount, modifiedCount, stopWatch.getTime());
    return new io.holoinsight.server.common.Pair<>(upsertSize, modifiedCount);
  }

  @Override
  public List<Map<String, Object>> insert(String tableName, List<Map<String, Object>> rows) {

    logger.info("[insert] start, table={}, records={}.", tableName, rows.size());
    if (CollectionUtils.isEmpty(rows))
      return new ArrayList<>();

    List<Map<String, Object>> filterRows = addUkValues(tableName, rows);

    StopWatch stopWatch = StopWatch.createStarted();
    List<Document> documents = DocumentUtil.parseList(filterRows);
    Collection<Document> save = mongoDbHelper.save(documents, tableName);
    logger.info("[insert] finish, table={}, records={}, cost={}.", tableName, save.size(),
        stopWatch.getTime());

    return DocumentUtil.toMapList(new ArrayList<>(documents));
  }

  @Override
  public List<Map<String, Object>> update(String tableName, List<Map<String, Object>> rows) {

    logger.info("[update] start, table={}, records={}.", tableName, rows.size());
    if (CollectionUtils.isEmpty(rows))
      return new ArrayList<>();

    StopWatch stopWatch = StopWatch.createStarted();
    List<Document> documents = new ArrayList<>();
    rows.forEach(row -> {
      if (row.containsKey(default_pk)) {
        return;
      }
      Document document =
          mongoDbHelper.modifyById(row.get(default_pk).toString(), row, Document.class, tableName);
      documents.add(document);
      logger.info("[update] finish, table={}, record={}, cost={}.", tableName,
          document.get(default_pk), stopWatch.getTime());
    });

    logger.info("[update] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return DocumentUtil.toMapList(new ArrayList<>(documents));
  }

  @Override
  public List<Map<String, Object>> updateByExample(String tableName, QueryExample queryExample,
      Map<String, Object> row) {
    logger.info("[updateByExample] finish, table={}, queryExample={}, row={}.", tableName,
        J.toJson(queryExample), J.toJson(row));
    StopWatch stopWatch = StopWatch.createStarted();
    List<Map<String, Object>> list = queryByExample(tableName, queryExample);

    if (CollectionUtils.isEmpty(list)) {
      return new ArrayList<>();
    }
    List<Document> documents = new ArrayList<>();
    list.forEach(l -> {
      l.putAll(row);
      Document document =
          mongoDbHelper.modifyById(l.get(default_pk).toString(), l, Document.class, tableName);
      documents.add(document);
      logger.info("[update] finish, table={}, record={}, cost={}.", tableName,
          document.get(default_pk), stopWatch.getTime());

    });
    logger.info("[updateByExample] finish, table={}, cost={}.", tableName, stopWatch.getTime());
    return DocumentUtil.toMapList(new ArrayList<>(documents));
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName) {
    logger.info("[queryByTable] finish, table={}.", tableName);
    StopWatch stopWatch = StopWatch.createStarted();
    List<Document> rows = mongoDbHelper.findAll(Document.class, tableName);
    logger.info("[queryByTable] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return DocumentUtil.toMapList(rows);
  }

  // @Override
  // public Map<String, Object> queryByPk(String tableName, String pkVal) {
  // logger.info("[queryByPk] finish, table={}, pkVal={}.", tableName, pkVal);
  // StopWatch stopWatch = StopWatch.createStarted();
  // Query query = new Query();
  // query.addCriteria(Criteria.where(default_pk).is(pkVal));
  //
  // List<Document> rows = mongoDbHelper.find(query, Document.class, tableName);
  // logger.info("[queryByPk] finish, table={}, records={}, cost={}.", tableName, rows.size(),
  // stopWatch.getTime());
  //
  // if (CollectionUtils.isEmpty(rows)) { return new HashMap<>(); }
  //
  // return DocumentUtil.toMapList(rows).get(0);
  // }

  @Override
  public List<Map<String, Object>> queryByPks(String tableName, List<String> pkValList) {
    logger.info("[queryByPks] finish, table={}, pkValList={}.", tableName, pkValList.size());
    if (CollectionUtils.isEmpty(pkValList))
      return new ArrayList<>();

    StopWatch stopWatch = StopWatch.createStarted();
    Query query = new Query();
    query.addCriteria(Criteria.where(default_pk).in(pkValList));

    query.with(Sort.by(Direction.DESC, default_modified));
    List<Document> rows = mongoDbHelper.find(query, Document.class, tableName);
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
    Query query = new Query();
    query.with(Sort.by(Direction.DESC, default_modified));
    if (!CollectionUtils.isEmpty(queryExample.getParams())) {
      for (Map.Entry<String, Object> entry : queryExample.getParams().entrySet()) {

        if (entry.getValue() instanceof String) {
          query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
        } else if (entry.getValue() instanceof List) {
          List<String> o =
              J.fromJson(J.toJson(entry.getValue()), new TypeToken<List<String>>() {}.getType());
          query.addCriteria(Criteria.where(entry.getKey()).in(o));
        }
      }
    }

    List<Document> rows = mongoDbHelper.find(query, Document.class, tableName);
    logger.info("[queryByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());

    return DocumentUtil.toMapList(rows);
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample queryExample) {
    logger.info("[fuzzyByExample] finish, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    Query query = new Query();
    query.with(Sort.by(Direction.DESC, default_modified));
    List<Criteria> orCriteria = new ArrayList<>();
    Criteria criteria = new Criteria();

    if (!CollectionUtils.isEmpty(queryExample.getParams())) {
      for (Map.Entry<String, Object> entry : queryExample.getParams().entrySet()) {
        if (entry.getKey().equalsIgnoreCase(default_type)) {
          query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
          continue;
        }
        if (entry.getKey().equalsIgnoreCase(default_workspace)) {
          query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
          continue;
        }
        orCriteria.add(Criteria.where(entry.getKey())
            .regex(J.json2Bean(J.toJson(entry.getValue()), Pattern.class)));
      }
    }
    criteria.orOperator(orCriteria.toArray(new Criteria[0]));
    query.addCriteria(criteria);

    List<Document> rows = mongoDbHelper.find(query, Document.class, tableName);
    logger.info("[fuzzyByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());

    return DocumentUtil.toMapList(rows);
  }

  // @Override
  // public List<String> queryPksByExample(String tableName, QueryExample queryExample) {
  // logger.info("[queryPksByExample] finish, table={}, queryExample={}.", tableName,
  // J.toJson(queryExample));
  // StopWatch stopWatch = StopWatch.createStarted();
  // List<Map<String, Object>> list = queryByExample(tableName, queryExample);
  // if (CollectionUtils.isEmpty(list)) { return new ArrayList<>(); }
  //
  // Collection<String> rows = Collections2.transform(list, new Function<Map<String, Object>,
  // String>() {
  // @Override
  // public String apply(Map<String, Object> input) {
  // return input.get(default_pk).toString();
  // }
  // });
  // logger.info("[queryPksByExample] finish, table={}, records={}, cost={}.", tableName,
  // rows.size(),
  // stopWatch.getTime());
  //
  // return new ArrayList<>(rows);
  // }

  @Override
  public long deleteByExample(String tableName, QueryExample queryExample) {
    logger.info("[deleteByExample] finish, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));

    StopWatch stopWatch = StopWatch.createStarted();
    Query query = new Query();
    if (!CollectionUtils.isEmpty(queryExample.getParams())) {
      for (Map.Entry<String, Object> entry : queryExample.getParams().entrySet()) {
        if (entry.getValue() instanceof String) {
          query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
        } else if (entry.getValue() instanceof List) {
          query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
        }
      }
    }

    long deleteCount = mongoDbHelper.removeByCondition(query, tableName);
    // 输出结果信息
    logger.info("[deleteByExample] finish, table={}, deleteCount={}, cost={}.", tableName,
        deleteCount, stopWatch.getTime());
    return deleteCount;
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
    Query query = new Query();
    if (!CollectionUtils.isEmpty(default_pks)) {
      query.addCriteria(Criteria.where(default_pk).in(default_pks));
    }

    long deleteCount = mongoDbHelper.removeByCondition(query, tableName);
    logger.info("[batchDeleteByPk] finish, table={}, deleteCount={}, cost={}.", tableName,
        deleteCount, stopWatch.getTime());
    return deleteCount;
  }
}
