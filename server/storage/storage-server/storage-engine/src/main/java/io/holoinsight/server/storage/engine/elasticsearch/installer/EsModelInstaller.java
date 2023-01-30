/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.installer;

import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.common.model.storage.Model;
import io.holoinsight.server.storage.common.model.storage.ModelColumn;
import io.holoinsight.server.storage.common.utils.DownSampling;
import io.holoinsight.server.storage.common.utils.GsonUtils;
import io.holoinsight.server.storage.common.utils.TimeBucket;
import io.holoinsight.server.storage.core.installer.DataTypeMapping;
import io.holoinsight.server.storage.core.installer.ModelInstaller;
import io.holoinsight.server.storage.engine.elasticsearch.utils.EsGsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.elasticsearch.cluster.metadata.ComposableIndexTemplate;
import org.elasticsearch.cluster.metadata.Template;
import org.elasticsearch.common.compress.CompressedXContent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * @author jiwliu
 * @version : EsModelInstaller.java, v 0.1 2022年10月11日 21:26 xiangwanpeng Exp $
 */
@Slf4j
public class EsModelInstaller implements ModelInstaller {

  @Autowired
  private RestHighLevelClient esClient;

  @Autowired
  DataTypeMapping dataTypeMapping;

  private IndexStructures structures = new IndexStructures();

  @Override
  public boolean isExists(Model model) {
    String tableName = model.getName();
    IndicesClient indicesClient = esClient.indices();
    try {
      boolean isExists = indicesClient.existsIndexTemplate(
          new ComposableIndexTemplateExistRequest(tableName), RequestOptions.DEFAULT);
      GetComposableIndexTemplatesResponse indexTemplatesResponse = indicesClient.getIndexTemplate(
          new GetComposableIndexTemplateRequest(tableName), RequestOptions.DEFAULT);
      if (isExists && indexTemplatesResponse != null
          && indexTemplatesResponse.getIndexTemplates().containsKey(tableName)) {
        ComposableIndexTemplate composableIndexTemplate =
            indexTemplatesResponse.getIndexTemplates().get(tableName);
        Template template = composableIndexTemplate.template();
        Mappings mappings = GsonUtils.get().fromJson(template.mappings().string(), Mappings.class);
        structures.putStructure(tableName, mappings);
        isExists = structures.containsStructure(tableName, createMapping(model));
      }
      return isExists;
    } catch (ElasticsearchStatusException e) {
      if (e.status() == RestStatus.NOT_FOUND) {
        return false;
      } else {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void install(Model model) {
    if (model.isTimeSeries()) {
      createTimeSeriesTable(model);
    } else {
      createNormalTable(model);
    }
  }

  private void createTimeSeriesTable(Model model) {
    String tableName = model.getName();
    IndicesClient indicesClient = esClient.indices();
    try {
      Mappings mappings = createMapping(model);
      boolean shouldUpdateTemplate = !isExists(model);
      shouldUpdateTemplate =
          shouldUpdateTemplate || !structures.containsStructure(tableName, mappings);
      if (shouldUpdateTemplate) {
        structures.putStructure(tableName, mappings);
        Settings settings = createSettings(model);
        CompressedXContent mappingCompressedXContent =
            new CompressedXContent(EsGsonUtils.esGson().toJson(mappings));
        PutComposableIndexTemplateRequest putComposableIndexTemplateRequest =
            new PutComposableIndexTemplateRequest().name(tableName);
        Map<String, AliasMetadata> aliases = new HashMap<>();
        aliases.put(tableName, AliasMetadata.newAliasMetadataBuilder(tableName).build());
        Template template = new Template(settings, mappingCompressedXContent, aliases);
        ComposableIndexTemplate composableIndexTemplate = new ComposableIndexTemplate(
            Arrays.asList(tableName + "-*"), template, null, null, null, null);
        putComposableIndexTemplateRequest.indexTemplate(composableIndexTemplate);
        AcknowledgedResponse acknowledgedResponse = indicesClient
            .putIndexTemplate(putComposableIndexTemplateRequest, RequestOptions.DEFAULT);
        boolean isAcknowledged = acknowledgedResponse.isAcknowledged();
        log.info("create {} index template finished, isAcknowledged: {}", tableName,
            isAcknowledged);
        if (!isAcknowledged) {
          throw new IOException("create " + tableName + " index template failure");
        }
      }
      String indexName = tableName + Const.LINE
          + TimeBucket.getTimeBucket(System.currentTimeMillis(), DownSampling.Day);
      GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
      if (!indicesClient.exists(getIndexRequest, RequestOptions.DEFAULT)) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.mapping(EsGsonUtils.esGson().toJson(mappings), XContentType.JSON);
        CreateIndexResponse createIndexResponse =
            indicesClient.create(createIndexRequest, RequestOptions.DEFAULT);
        boolean isAcknowledged = createIndexResponse.isAcknowledged();
        log.info("create {} index finished, isAcknowledged: {}", indexName, isAcknowledged);
        if (!isAcknowledged) {
          throw new RuntimeException("create " + indexName + " time series index failure");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("cannot create " + tableName + " index template", e);
    }
  }

  private void createNormalTable(Model model) {
    throw new UnsupportedOperationException();
  }

  protected Settings createSettings(Model model) {
    Settings settings = Settings.builder().put("index.number_of_replicas", 1)
        .put("index.number_of_shards", 5).put("index.refresh_interval", "10s").build();
    return settings;
  }

  protected Mappings createMapping(Model model) throws IOException {
    Map<String, Object> properties = createProperties(model);
    List<Map<String, DynamicTemplate>> dynamicTemplates = new ArrayList<>();
    Mappings mappings = new Mappings(dynamicTemplates, properties);
    dynamicTemplates
        .add(Collections.singletonMap("strings_as_keyword", new DynamicTemplate("string", null,
            null, null, null, null, Collections.singletonMap("type", "keyword"))));
    return mappings;
  }

  private Map<String, Object> createProperties(Model model) {
    Map<String, Object> properties = new HashMap<>();
    for (ModelColumn columnDefine : model.getColumns()) {
      final String type =
          dataTypeMapping.transform(columnDefine.getType(), columnDefine.getGenericType());
      String columnName = columnDefine.getName();
      Map<String, Object> column = new HashMap<>();
      column.put("type", type);
      properties.put(columnName, column);
    }
    return properties;
  }
}
