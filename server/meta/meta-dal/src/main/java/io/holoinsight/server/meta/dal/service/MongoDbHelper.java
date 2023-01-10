/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.dal.service;

import io.holoinsight.server.meta.common.util.ConstModel;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MongoDbHelper.java, v 0.1 2022年03月03日 3:59 下午 jinsong.yjs Exp $
 */
@Component
public class MongoDbHelper {

  @Autowired
  private MongoTemplate mongoTemplate;

  /**
   * 创建【集合】
   *
   * 创建一个大小没有限制的集合（默认集合创建方式）
   *
   * @return 创建集合的结果
   */
  public Boolean createCollection(String collectionName) {
    // 创建集合并返回集合信息
    mongoTemplate.createCollection(collectionName);
    // 检测新的集合是否存在，返回创建结果
    return mongoTemplate.collectionExists(collectionName);
  }

  public Boolean checkIsExited(String collectionName) {
    // 检测新的集合是否存在
    return mongoTemplate.collectionExists(collectionName);
  }

  /**
   * 创建【固定大小集合】
   *
   * 创建集合并设置 `capped=true` 创建 `固定大小集合`，可以配置参数 `size` 限制文档大小，可以配置参数 `max` 限制集合文档数量。
   *
   * @return 创建集合的结果
   */
  public Boolean createCollectionFixedSize(String collectionName, Long size, Long maxDocument) {
    // 创建固定大小集合
    CollectionOptions collectionOptions = CollectionOptions.empty()
        // 创建固定集合。固定集合是指有着固定大小的集合，当达到最大值时，它会自动覆盖最早的文档。
        .capped()
        // 固定集合指定一个最大值，以千字节计(KB),如果 capped 为 true，也需要指定该字段。
        .size(size)
        // 指定固定集合中包含文档的最大数量。
        .maxDocuments(maxDocument);
    // 执行创建集合
    mongoTemplate.createCollection(collectionName, collectionOptions);
    // 检测新的集合是否存在，返回创建结果
    return mongoTemplate.collectionExists(collectionName);
  }

  /**
   * 获取【集合名称】列表
   *
   * @return 集合名称列表
   */
  public Object getCollectionNames() {
    // 执行获取集合名称列表
    return mongoTemplate.getCollectionNames();
  }

  /**
   * 检测集合【是否存在】
   *
   * @return 集合是否存在
   */
  public boolean collectionExists(String collectionName) {
    // 检测新的集合是否存在，返回检测结果
    return mongoTemplate.collectionExists(collectionName);
  }

  /**
   * 删除【集合】
   *
   * @return 创建集合结果
   */
  public Boolean dropCollection(String collectionName) {
    // 设置集合名称
    // 执行删除集合
    mongoTemplate.getCollection(collectionName).drop();
    // 检测新的集合是否存在，返回删除结果
    return !mongoTemplate.collectionExists(collectionName);
  }

  public void createIndex(Index index, String collectionName) {
    mongoTemplate.indexOps(collectionName).ensureIndex(index);
  }


  public List<IndexInfo> getIndexInfo(String collectionName) {
    return mongoTemplate.indexOps(collectionName).getIndexInfo();
  }

  public void dropIndex(String indexName, String collectionName) {
    mongoTemplate.indexOps(collectionName).dropIndex(indexName);
  }

  /**
   * 保存对象到指定的collection
   *
   * @param list
   * @param collectionName
   * @return
   */
  public <T> Collection<T> save(Collection<T> list, String collectionName) {
    return mongoTemplate.insert(list, collectionName);
  }

  /**
   * 查询数据指定的collection
   *
   * @param query
   * @param tClass
   * @param <T>
   * @return
   */
  public <T> List<T> find(Query query, Class<T> tClass, String collectionName) {
    return mongoTemplate.find(query, tClass, collectionName);
  }

  /**
   * 查询所有指定的collection
   *
   * @param tClass
   * @param collectionName
   * @param <T>
   * @return
   */
  public <T> List<T> findAll(Class<T> tClass, String collectionName) {
    return mongoTemplate.findAll(tClass, collectionName);
  }

  /**
   * 指定条件查询
   *
   * @param tClass
   * @param collectionName
   * @param <T>
   * @return
   */
  public <T> List<T> findAll(Query query, Class<T> tClass, String collectionName) {
    return mongoTemplate.find(query, tClass, collectionName);
  }

  /**
   * 删除集合中【符合条件】的【一个]或[多个】文档
   *
   * @return 删除用户信息的结果
   */
  public long removeByCondition(Query query, String collectionName) {
    // 执行删除查找到的匹配的全部文档信息
    DeleteResult result = mongoTemplate.remove(query, collectionName);
    return result.getDeletedCount();
  }

  /**
   * 删除【符合条件】的【单个文档】，并返回删除的文档。
   *
   * @return 删除的用户信息
   */
  public <T> T findAndRemove(Query query, Class<T> tClass, String collectionName) {

    // 执行删除查找到的匹配的第一条文档,并返回删除的文档信息
    T result = mongoTemplate.findAndRemove(query, tClass, collectionName);
    // 输出结果信息
    String resultInfo = "成功删除文档信息，文档内容为：" + result;
    return result;
  }

  /**
   * 删除【符合条件】的【全部文档】，并返回删除的文档。
   *
   * @return 删除的全部用户信息
   */
  public <T> List<T> findAllAndRemove(Query query, Class<T> tClass, String collectionName) {

    // 执行删除查找到的匹配的全部文档,并返回删除的全部文档信息
    List<T> resultList = mongoTemplate.findAllAndRemove(query, tClass, collectionName);
    // 输出结果信息
    String resultInfo = "成功删除文档信息，文档内容为：" + resultList;
    return resultList;
  }

  public <T> T modifyById(String id, Map<String, Object> data, Class<T> tClass,
      String collectionName) {
    Query query = new Query().addCriteria(Criteria.where(ConstModel.default_pk).is(id));
    Update update = new Update();
    data.forEach(update::set);
    return findAndModifyInstance(query, update, tClass, collectionName);
  }

  public <T> T findAndModifyInstance(Query query, Update update, Class<T> tClass,
      String collectionName) {
    return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true),
        tClass, collectionName);
  }
}
