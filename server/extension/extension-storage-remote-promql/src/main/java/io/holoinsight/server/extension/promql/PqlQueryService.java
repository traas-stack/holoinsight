/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql;

import java.util.List;

import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryResult.Result;

/**
 * @author jinyan.ljw
 * @Description TODO
 * @date 2023/3/8
 */
public interface PqlQueryService {

  List<Result> query(PqlParam pqlParam);

  List<Result> queryRange(PqlParam pqlParam);
}
