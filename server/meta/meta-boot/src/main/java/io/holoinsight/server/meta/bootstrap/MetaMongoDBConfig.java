/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.bootstrap;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jsy1001de
 * @version 1.0: MongoDBConfig.java, Date: 2023-04-18 Time: 20:17
 */
@Configuration
public class MetaMongoDBConfig {

  @Value("${spring.data.mongodb.uri}")
  private String mongoUri;

  @Value("${holoinsight.meta.database}")
  private String database;

  @Bean
  public MongoClient mongoClient() {
    MongoClient mongoClient = MongoClients.create(mongoUri);
    return mongoClient;
  }

  @Bean
  public MongoDatabase mongoDatabase() throws Exception {
    MongoDatabase mongoDatabase = mongoClient().getDatabase(database);
    return mongoDatabase;
  }
}