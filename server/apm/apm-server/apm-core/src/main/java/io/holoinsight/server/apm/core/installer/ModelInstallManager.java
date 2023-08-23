/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.core.installer;

import io.holoinsight.server.apm.common.model.storage.Model;
import io.holoinsight.server.apm.common.model.storage.ModelColumn;
import io.holoinsight.server.apm.common.model.storage.annotation.Column;
import io.holoinsight.server.apm.common.model.storage.annotation.ModelAnnotation;
import io.holoinsight.server.apm.common.utils.ClassUtil;
import io.holoinsight.server.apm.core.ModelCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author jiwliu
 * @version : ModelInstaller.java, v 0.1 2022年10月11日 21:24 xiangwanpeng Exp $
 */
@Slf4j
public class ModelInstallManager implements IModelInstallManager {

  protected static final String MODEL_PATH = "io.holoinsight.server.apm";

  public String[] getModelPath() {
    return new String[] {MODEL_PATH};
  }


  @Autowired
  private List<ModelInstaller> installers;
  @Autowired
  @Lazy
  private ModelCenter modelManager;

  /**
   * @throws IOException
   */
  @Override
  public void start() throws IOException {
    List<Model> models = scanModels();
    for (Model model : models) {
      installModel(model);
    }
  }

  protected List<Model> scanModels() throws IOException {
    List<Model> models = new ArrayList<>();
    Collection<Class<?>> modelPathClasses = new HashSet<>();
    for (String modelPath : getModelPath()) {
      modelPathClasses.addAll(ClassUtil.getClasses(modelPath));
    }
    log.info("[apm] load {} models, path={}", modelPathClasses.size(), getModelPath());
    for (Class<?> cls : modelPathClasses) {
      if (cls.isAnnotationPresent(ModelAnnotation.class)) {
        models.add(scanModel(cls));
      }
    }
    return models;
  }

  protected Model scanModel(Class<?> cls) {
    ModelAnnotation modelAnnotation = cls.getAnnotation(ModelAnnotation.class);
    List<Field> fields = new ArrayList<>();
    while (cls != null) {
      fields.addAll(Arrays.asList(cls.getDeclaredFields()));
      cls = cls.getSuperclass();
    }
    List<ModelColumn> modelColumns = new ArrayList<>();
    for (Field field : fields) {
      if (field.isAnnotationPresent(Column.class)) {
        Column column = field.getAnnotation(Column.class);
        ModelColumn modelColumn =
            new ModelColumn(column.name(), field.getType(), field.getGenericType());
        modelColumns.add(modelColumn);
      }
    }
    return new Model(modelAnnotation.name(), modelColumns, true, modelAnnotation.ttl());
  }

  private void installModel(Model model) {
    for (ModelInstaller installer : installers) {
      installer.install(model);
      log.info("[apm] model installed, model={}, installer={}", model.getName(),
          installer.getClass().getSimpleName());
    }
    modelManager.register(model);
  }

}
