/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.registry.core.agent.Agent;
import io.holoinsight.server.registry.core.agent.AgentStorage;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.TemplateService;

/**
 * <p>
 * created at 2022/4/19
 *
 * @author zzhb101
 */
@Service
public class TargetDebugService {
  @Autowired
  private TemplateService templateService;
  @Autowired
  private CollectTargetStorage collectTargetStorage;
  @Autowired
  private AgentStorage agentStorage;
  @Autowired
  private DataClientService dataClientService;

  public CollectTargetDebugInfo get(String t, String d) {
    CollectTemplate ct = templateService.fuzzyGet(t);
    if (ct == null) {
      return null;
    }
    CollectTargetKey ctk = new CollectTargetKey(ct.getId(), d);
    CollectTarget collectTarget = collectTargetStorage.get(ctk);
    if (collectTarget == null) {
      return null;
    }
    CollectTargetDebugInfo i = new CollectTargetDebugInfo();
    i.setTenant(ct.getTenant());

    CollectTargetDebugInfo.Template it = i.getTemplate();
    it.setId(ct.getId());
    it.setTableName(ct.getTableName());
    it.setCollectRange(ct.getCollectRange());
    it.setTargets(collectTargetStorage.countByTemplate(ct.getId()));

    CollectTargetDebugInfo.Dim dim = i.getDim();
    dim.setId(ctk.getDimId());
    if (dim.getId().startsWith("dim2:")) {
      QueryExample qe = new QueryExample();
      qe.getParams().put("_uk", ctk.getDimId().substring("dim2:".length()));
      List<Map<String, Object>> raw =
          dataClientService.queryByExample(ct.getCollectRange().getCloudmonitor().getTable(), qe);
      if (raw.size() == 1) {
        dim.setRaw(raw.get(0));
      }
    }

    Agent agent = agentStorage.get(collectTarget.getRefAgent());
    i.setAgent(agent);

    i.addCmd("# 请先将 kubectl 切换到对应的上下文后再执行如下命令");

    if (agent != null) {
      i.addCmd("# Login to agent");
      if (agent.getJson().getK8s() != null) {
        i.addCmd("kubectl -n %s exec -it %s -- bash", //
            agent.getJson().getK8s().getNamespace(), //
            agent.getJson().getK8s().getPod()); //
        i.addCmd("# 'g' is aliased to cd /usr/local/holoinsight/agent/logs");
      } else {
        i.addCmd("agent=%s", agent.getJson().getIp());
      }
    }

    i.addCmd("# Query config records");
    i.addCmd("tail -99999 config.log | grep %s | grep %s", ct.getTableName(), ctk.getDimId());

    i.addCmd("# Query executing records");
    i.addCmd("tail -99999 info.log | grep '%s/%s'", ct.getTableName(), ctk.getDimId());

    if (dim.getRaw() != null) {
      Map<String, Object> m = (Map<String, Object>) dim.getRaw();
      if ("POD".equals(m.get("_type"))) {

        String namespace = (String) m.get("namespace");
        String pod = (String) m.get("name");
        i.addCmd("# 查询 target 元数据");
        i.addCmd("tail -99999 meta.log | grep %s | grep %s", namespace, pod);

        i.addCmd("# 登录 target POD");
        i.addCmd("kubectl -n %s exec -it %s -- bash", namespace, pod);
      }
    }

    return i;
  }
}
