/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.model.dto;

import io.holoinsight.server.common.dao.entity.dto.IntegrationPluginDTO;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author masaimu
 * @version 2023-03-16 18:08:00
 */
public class IntegrationPluginDTOTest {

  @Test
  public void testGet() {
    IntegrationPluginDTO.DataRange dataRange = new IntegrationPluginDTO.DataRange();
    dataRange.extraMap.put("testExtra", "extraValue");
    dataRange.valuesMap.put("valueKey", "value");
    Assert.assertEquals(dataRange.getExtra("testExtra"), "extraValue");
    Assert.assertEquals(dataRange.get("valueKey"), "value");
  }
}
