/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.core.installer;

import io.holoinsight.server.storage.common.model.storage.Model;
import io.holoinsight.server.storage.common.model.storage.ModelColumn;
import io.holoinsight.server.storage.common.model.storage.annotation.Column;
import io.holoinsight.server.storage.common.model.storage.annotation.ModelAnnotation;
import io.holoinsight.server.storage.common.utils.ClassUtil;
import io.holoinsight.server.storage.core.ModelCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jiwliu
 * @version : ModelInstaller.java, v 0.1 2022年10月11日 21:24 xiangwanpeng Exp $
 */
@Slf4j
public class ModelInstallManager {

  private static final String MODEL_PATH = "io.holoinsight.server.storage";

  @Autowired
  private List<ModelInstaller> installers;
  @Autowired
  @Lazy
  private ModelCenter modelManager;

  /**
   * TODO 这个需要重复执行吗?
   * 
   * @throws IOException
   */
  @Scheduled(initialDelay = 60000, fixedRate = 60000)
  public void start() throws IOException {
    List<Model> models = scanModels();
    for (Model model : models) {
      installModel(model);
    }
  }

  private List<Model> scanModels() throws IOException {
    List<Model> models = new ArrayList<>();
    Collection<Class<?>> modelPathClasses = ClassUtil.getClasses(MODEL_PATH);
    log.info("[storage] load {} models, path={}",
        modelPathClasses == null ? 0 : modelPathClasses.size(), MODEL_PATH);
    for (Class<?> cls : modelPathClasses) {
      if (cls.isAnnotationPresent(ModelAnnotation.class)) {
        ModelAnnotation modelAnnotation = cls.getAnnotation(ModelAnnotation.class);
        List<ModelColumn> modelColumns = new ArrayList<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
          if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            ModelColumn modelColumn =
                new ModelColumn(column.name(), field.getType(), field.getGenericType());
            modelColumns.add(modelColumn);
          }
        }
        Model model = new Model(modelAnnotation.name(), modelColumns, true, modelAnnotation.ttl());
        models.add(model);
      }
    }
    return models;
  }

  private void installModel(Model model) {
    for (ModelInstaller installer : installers) {
      installer.install(model);
      modelManager.register(model);
    }
  }

}
